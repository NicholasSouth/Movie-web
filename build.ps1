param(
    [string]$TomcatHome = (Join-Path $PSScriptRoot 'runtime\tomcat')
)

$ErrorActionPreference = 'Stop'
$projectRoot = [IO.Path]::GetFullPath($PSScriptRoot)

function Find-JdkHome {
    $candidates = New-Object 'System.Collections.Generic.List[string]'
    if ($env:JAVA_HOME) { $candidates.Add($env:JAVA_HOME) }
    $javacCommand = Get-Command javac.exe -ErrorAction SilentlyContinue
    if ($javacCommand) {
        $candidates.Add((Split-Path (Split-Path $javacCommand.Source -Parent) -Parent))
        $javacFile = Get-Item -LiteralPath $javacCommand.Source
        if ($javacFile.Target) {
            $candidates.Add((Split-Path (Split-Path @($javacFile.Target)[0] -Parent) -Parent))
        }
    }
    foreach ($vendor in @('Java', 'Eclipse Adoptium', 'Microsoft', 'Amazon Corretto')) {
        $vendorPath = Join-Path $env:ProgramFiles $vendor
        if (Test-Path -LiteralPath $vendorPath) {
            Get-ChildItem -LiteralPath $vendorPath -Directory | Sort-Object Name -Descending | ForEach-Object {
                $candidates.Add($_.FullName)
            }
        }
    }
    foreach ($candidate in $candidates) {
        if ((Test-Path -LiteralPath (Join-Path $candidate 'bin\javac.exe')) -and
            (Test-Path -LiteralPath (Join-Path $candidate 'bin\jar.exe'))) {
            return $candidate
        }
    }
    throw 'JDK 21 was not found. Install JDK 21 and set JAVA_HOME to its installation directory.'
}

if (-not (Test-Path -LiteralPath (Join-Path $TomcatHome 'lib\servlet-api.jar'))) {
    if (-not $PSBoundParameters.ContainsKey('TomcatHome')) {
        if ($env:CATALINA_HOME -and (Test-Path -LiteralPath (Join-Path $env:CATALINA_HOME 'lib\servlet-api.jar'))) {
            $TomcatHome = $env:CATALINA_HOME
        } else {
            $runtimeDirectory = Join-Path $projectRoot 'runtime'
            if (Test-Path -LiteralPath $runtimeDirectory) {
                $tomcatDirectory = Get-ChildItem -LiteralPath $runtimeDirectory -Directory |
                    Where-Object { $_.Name -like 'apache-tomcat-9*' } |
                    Sort-Object Name -Descending | Select-Object -First 1
                if ($tomcatDirectory) { $TomcatHome = $tomcatDirectory.FullName }
            }
        }
    }
}
if (-not (Test-Path -LiteralPath (Join-Path $TomcatHome 'lib\servlet-api.jar'))) {
    throw 'Tomcat 9 was not found. Place it in runtime\tomcat or run build.ps1 -TomcatHome "C:\path\to\apache-tomcat-9".'
}

$jdkHome = Find-JdkHome
$javac = Join-Path $jdkHome 'bin\javac.exe'
$jar = Join-Path $jdkHome 'bin\jar.exe'
$compilerVersion = (& $javac -version 2>&1 | Out-String).Trim()
if ($compilerVersion -notmatch 'javac\s+(\d+)' -or [int]$Matches[1] -lt 21) {
    throw "JDK 21 or later is required. Found: $compilerVersion"
}

$buildRoot = Join-Path $projectRoot 'build'
$stagingDirectory = [IO.Path]::GetFullPath((Join-Path $buildRoot 'war'))
$expectedStaging = [IO.Path]::GetFullPath((Join-Path $projectRoot 'build\war'))
if ($stagingDirectory -ne $expectedStaging -or
    -not $stagingDirectory.StartsWith($projectRoot + [IO.Path]::DirectorySeparatorChar, [StringComparison]::OrdinalIgnoreCase)) {
    throw 'Refusing to clean a staging directory outside this project.'
}
if (Test-Path -LiteralPath $stagingDirectory) { Remove-Item -LiteralPath $stagingDirectory -Recurse -Force }
New-Item -ItemType Directory -Path $stagingDirectory -Force | Out-Null
Get-ChildItem -LiteralPath (Join-Path $projectRoot 'src\main\webapp') -Force |
    Copy-Item -Destination $stagingDirectory -Recurse -Force
$classesDirectory = Join-Path $stagingDirectory 'WEB-INF\classes'
New-Item -ItemType Directory -Path $classesDirectory -Force | Out-Null

$classpath = (Join-Path ([IO.Path]::GetFullPath($TomcatHome)) 'lib\servlet-api.jar') + ';' +
    (Join-Path $projectRoot 'src\main\webapp\WEB-INF\lib\*')
$sourceFiles = @(Get-ChildItem -LiteralPath (Join-Path $projectRoot 'src\main\java') -Filter '*.java' -File -Recurse)
if ($sourceFiles.Count -eq 0) { throw 'No Java source files were found.' }
$sourceList = Join-Path $buildRoot 'sources.txt'
$sourceArguments = $sourceFiles | ForEach-Object { '"' + $_.FullName.Replace('\', '/') + '"' }
[IO.File]::WriteAllLines($sourceList, [string[]]$sourceArguments, (New-Object Text.UTF8Encoding($false)))
Write-Host "Compiling $($sourceFiles.Count) Java files with $compilerVersion..."
& $javac '-J-Dfile.encoding=UTF-8' '-encoding' 'UTF-8' '--release' '21' '-classpath' $classpath '-d' $classesDirectory "@$sourceList"
if ($LASTEXITCODE -ne 0) { throw "Java compilation failed (exit code $LASTEXITCODE)." }

$resourcesDirectory = Join-Path $projectRoot 'src\main\resources'
if (Test-Path -LiteralPath $resourcesDirectory) {
    Get-ChildItem -LiteralPath $resourcesDirectory -Force | Copy-Item -Destination $classesDirectory -Recurse -Force
}
$distDirectory = Join-Path $projectRoot 'dist'
New-Item -ItemType Directory -Path $distDirectory -Force | Out-Null
$warFile = Join-Path $distDirectory 'MovieWeb.war'
& $jar '--create' '--file' $warFile '-C' $stagingDirectory '.'
if ($LASTEXITCODE -ne 0) { throw "WAR packaging failed (exit code $LASTEXITCODE)." }
Write-Host "Build completed: $warFile"
