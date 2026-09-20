param([string]$ServerName, [int]$TcpPort = 1433, [switch]$Restore)
$ErrorActionPreference = 'Stop'
if (-not $ServerName) {
    $ServerName = Read-Host 'SQL Server HOST name (e.g. localhost or DESKTOP-ABC; not LocalDB) [localhost]'
    if ([string]::IsNullOrWhiteSpace($ServerName)) { $ServerName = 'localhost' }
    $portInput = Read-Host 'SQL Server TCP port [1433]'
    if ($portInput) { $TcpPort = [int]$portInput }
    $Restore = ((Read-Host 'Restore database\MovieWeb.bak if MovieWeb does not exist? [y/N]') -eq 'y')
}
if ($ServerName -notmatch '^[A-Za-z0-9._-]+$' -or $TcpPort -lt 1 -or $TcpPort -gt 65535) {
    throw 'Enter the server HOST and TCP port separately; e.g. localhost and 1433.'
}
Add-Type -AssemblyName System.Data
$builder = New-Object System.Data.SqlClient.SqlConnectionStringBuilder
# Windows PowerShell adapts this builder as a dictionary. Use SQL connection
# keywords; property-style names such as DataSource are not valid keywords.
$builder['Data Source'] = 'tcp:' + $ServerName + ',' + $TcpPort
$builder['Initial Catalog'] = 'master'
$builder['Integrated Security'] = $true
$builder['Encrypt'] = $true
$builder['TrustServerCertificate'] = $true
$builder['Connect Timeout'] = 8
$connection = New-Object System.Data.SqlClient.SqlConnection($builder.ConnectionString)
try {
    $connection.Open()
    $check = $connection.CreateCommand()
    $check.CommandText = "SELECT CAST(SERVERPROPERTY('ProductMajorVersion') AS int)"
    $version = [int]$check.ExecuteScalar()
    $check.CommandText = "SELECT COUNT(*) FROM sys.databases WHERE name=N'MovieWeb'"
    $exists = [int]$check.ExecuteScalar() -gt 0
    if (-not $exists -and $Restore) {
        if ($version -lt 17) { throw 'This backup requires SQL Server 2025 (17.x) or newer.' }
        $backup = Join-Path $PSScriptRoot 'database\MovieWeb.bak'
        if (-not (Test-Path -LiteralPath $backup)) { throw 'database\MovieWeb.bak is missing. Copy your supplied backup there.' }
        $backupSql = $backup.Replace("'", "''")
        $filesCmd = $connection.CreateCommand()
        $filesCmd.CommandText = "RESTORE FILELISTONLY FROM DISK=N'$backupSql'"
        $filesCmd.CommandTimeout = 60
        $files = New-Object System.Data.DataTable
        $reader = $filesCmd.ExecuteReader()
        $files.Load($reader)
        $reader.Close()
        $check.CommandText = "SELECT CAST(SERVERPROPERTY('InstanceDefaultDataPath') AS nvarchar(4000))"
        $dataPath = [string]$check.ExecuteScalar()
        $check.CommandText = "SELECT CAST(SERVERPROPERTY('InstanceDefaultLogPath') AS nvarchar(4000))"
        $logPath = [string]$check.ExecuteScalar()
        if (-not $dataPath -or -not $logPath) { throw 'Server data/log folders unavailable. Restore in SSMS using Relocate all files.' }
        $moves = @(); $i = 0; $suffix = [Guid]::NewGuid().ToString('N').Substring(0,8)
        foreach ($row in $files.Rows) {
            $i++
            if ($row.Type -ne 'D' -and $row.Type -ne 'L') { throw 'Unexpected backup file type. Restore manually in SSMS.' }
            $folder = if ($row.Type -eq 'L') { $logPath } else { $dataPath }
            $extension = if ($row.Type -eq 'L') { '.ldf' } elseif ($i -eq 1) { '.mdf' } else { '.ndf' }
            $target = $folder.TrimEnd('\','/') + '\MovieWeb_' + $suffix + '_' + $i + $extension
            $logicalSql = ([string]$row.LogicalName).Replace("'", "''")
            $targetSql = $target.Replace("'", "''")
            $moves += "MOVE N'$logicalSql' TO N'$targetSql'"
        }
        $restoreCommand = $connection.CreateCommand()
        $restoreCommand.CommandTimeout = 300
        $restoreCommand.CommandText = "RESTORE DATABASE [MovieWeb] FROM DISK=N'$backupSql' WITH " + ($moves -join ', ') + ', RECOVERY'
        [void]$restoreCommand.ExecuteNonQuery()
        Write-Host 'MovieWeb restored. Existing databases were not overwritten.'
        $exists = $true
    }
    if (-not $exists) { throw 'MovieWeb does not exist yet. Restore database\MovieWeb.bak in SSMS or rerun with restore enabled.' }
    $connection.ChangeDatabase('MovieWeb')
    $check = $connection.CreateCommand()
    $check.CommandText = 'SELECT COUNT(*) FROM dbo.Movies WHERE isActive=1 AND deleted_at IS NULL'
    $count = $check.ExecuteScalar()
    $configPath = Join-Path $PSScriptRoot 'config\database.properties'
    New-Item -ItemType Directory -Force -Path (Split-Path $configPath -Parent) | Out-Null
    $value = 'db.url=jdbc:sqlserver://' + $ServerName + ':' + $TcpPort + ';databaseName=MovieWeb;integratedSecurity=true;authenticationScheme=NativeAuthentication;encrypt=true;trustServerCertificate=true;'
    [IO.File]::WriteAllText($configPath, $value + [Environment]::NewLine, (New-Object Text.UTF8Encoding($false)))
    Write-Host "Connected using Windows Authentication. Visible movies: $count. Configuration saved. Run run.cmd."
} catch {
    Write-Host 'Setup failed. Check SQL Server 2025 service, TCP/IP, actual port, Windows account permissions and backup file access.'
    throw
} finally { $connection.Dispose() }
