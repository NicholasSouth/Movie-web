param(
    [string]$H2Jar = $env:H2_JAR,
    [string]$TomcatHome = (Join-Path $PSScriptRoot 'runtime\tomcat'),
    [string]$JavaHome = $env:JAVA_HOME
)

$ErrorActionPreference = 'Stop'
$projectRoot = [IO.Path]::GetFullPath($PSScriptRoot)
if (-not $H2Jar) { $H2Jar = Join-Path $projectRoot 'tests\lib\h2.jar' }
if (-not (Test-Path -LiteralPath $H2Jar)) {
    throw 'H2 2.3.232 is required only for tests. Run test.ps1 -H2Jar "C:\path\to\h2-2.3.232.jar". See tests\README.md.'
}
if (-not (Test-Path -LiteralPath (Join-Path $TomcatHome 'lib\servlet-api.jar'))) {
    throw 'Tomcat 9 was not found. Pass -TomcatHome "C:\path\to\apache-tomcat-9".'
}

if ($JavaHome -and (Test-Path -LiteralPath (Join-Path $JavaHome 'bin\javac.exe')) -and
    (Test-Path -LiteralPath (Join-Path $JavaHome 'bin\java.exe'))) {
    $javac = Join-Path $JavaHome 'bin\javac.exe'
    $java = Join-Path $JavaHome 'bin\java.exe'
} else {
    if ($PSBoundParameters.ContainsKey('JavaHome')) { throw "JDK was not found at $JavaHome." }
    $compilerCommand = Get-Command javac.exe -ErrorAction SilentlyContinue
    $javaCommand = Get-Command java.exe -ErrorAction SilentlyContinue
    if (-not $compilerCommand -or -not $javaCommand) {
        throw 'JDK 21 is required. Add its bin folder to PATH, set JAVA_HOME, or pass -JavaHome.'
    }
    $javac = $compilerCommand.Source
    $java = $javaCommand.Source
}

$classesDirectory = [IO.Path]::GetFullPath((Join-Path $projectRoot 'build\test-classes'))
if ($classesDirectory -ne [IO.Path]::GetFullPath((Join-Path $projectRoot 'build\test-classes')) -or
    -not $classesDirectory.StartsWith($projectRoot + [IO.Path]::DirectorySeparatorChar, [StringComparison]::OrdinalIgnoreCase)) {
    throw 'Refusing to clean test classes outside this project.'
}
if (Test-Path -LiteralPath $classesDirectory) { Remove-Item -LiteralPath $classesDirectory -Recurse -Force }
New-Item -ItemType Directory -Path $classesDirectory -Force | Out-Null
$classpath = (Join-Path ([IO.Path]::GetFullPath($TomcatHome)) 'lib\servlet-api.jar') + ';' +
    (Join-Path $projectRoot 'src\main\webapp\WEB-INF\lib\*') + ';' + [IO.Path]::GetFullPath($H2Jar)
$sourceArguments = Get-ChildItem -LiteralPath (Join-Path $projectRoot 'src\main\java'), (Join-Path $projectRoot 'tests\java') -Filter '*.java' -File -Recurse |
    ForEach-Object { '"' + $_.FullName.Replace('\', '/') + '"' }
$sourceList = Join-Path $projectRoot 'build\test-sources.txt'
[IO.File]::WriteAllLines($sourceList, [string[]]$sourceArguments, (New-Object Text.UTF8Encoding($false)))
Write-Host 'Compiling application and tests (JDK 21, UTF-8)...'
& $javac '-J-Dfile.encoding=UTF-8' '-encoding' 'UTF-8' '--release' '21' '-classpath' $classpath '-d' $classesDirectory "@$sourceList"
if ($LASTEXITCODE -ne 0) { throw "Test compilation failed (exit code $LASTEXITCODE)." }
Write-Host 'Running isolated tests. Your SQL Server database will not be read or changed.'
& $java '-Dfile.encoding=UTF-8' '-classpath' ($classesDirectory + ';' + $classpath) 'com.movieweb.tests.MovieFeatureTest'
if ($LASTEXITCODE -ne 0) { throw "Tests failed (exit code $LASTEXITCODE)." }
