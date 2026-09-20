$ErrorActionPreference = 'Stop'
$config = Join-Path $PSScriptRoot 'config\database.properties'
if (-not (Test-Path -LiteralPath $config)) { throw 'Run setup-database.cmd or create config\database.properties first.' }
$war = Join-Path $PSScriptRoot 'dist\MovieWeb.war'
if (-not (Test-Path -LiteralPath $war)) { & (Join-Path $PSScriptRoot 'build.ps1') }
$classes = Join-Path $PSScriptRoot 'runtime\check'
New-Item -ItemType Directory -Force -Path $classes | Out-Null
Add-Type -AssemblyName System.IO.Compression.FileSystem
$archive = [IO.Compression.ZipFile]::OpenRead($war)
try {
    foreach ($entry in $archive.Entries) {
        if ($entry.FullName.StartsWith('WEB-INF/classes/') -and $entry.Name) {
            $relative = $entry.FullName.Substring('WEB-INF/classes/'.Length)
            $target = [IO.Path]::GetFullPath((Join-Path $classes $relative))
            if (-not $target.StartsWith([IO.Path]::GetFullPath($classes) + '\', [StringComparison]::OrdinalIgnoreCase)) {
                throw 'Unexpected archive path.'
            }
            New-Item -ItemType Directory -Force -Path (Split-Path $target -Parent) | Out-Null
            [IO.Compression.ZipFileExtensions]::ExtractToFile($entry, $target, $true)
        }
    }
} finally { $archive.Dispose() }
$java = if ($env:JAVA_HOME -and (Test-Path -LiteralPath (Join-Path $env:JAVA_HOME 'bin\java.exe'))) {
    Join-Path $env:JAVA_HOME 'bin\java.exe'
} else { (Get-Command java.exe -ErrorAction Stop).Source }
$native = Join-Path $PSScriptRoot 'runtime\native'
$env:MOVIEWEB_CONFIG = $config
$env:PATH = $native + ';' + $env:PATH
$classpath = $classes + ';' + (Join-Path $PSScriptRoot 'src\main\webapp\WEB-INF\lib\*')
& $java '-Dfile.encoding=UTF-8' "-Djava.library.path=$native" '-classpath' $classpath 'com.movieweb.util.DatabaseCheck'
if ($LASTEXITCODE -ne 0) { throw 'Database check failed. Check config\database.properties, native auth DLL, server TCP port and Windows permissions.' }
