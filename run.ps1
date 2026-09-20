param([int]$Port = 8080, [switch]$Build)
$ErrorActionPreference = 'Stop'
if ($Port -lt 1024 -or $Port -gt 65535) { throw 'Port must be between 1024 and 65535.' }
$root = [IO.Path]::GetFullPath($PSScriptRoot)
$tomcat = Join-Path $root 'runtime\tomcat'
$base = Join-Path $root 'runtime\server'
$config = Join-Path $root 'config\database.properties'
if (-not (Test-Path -LiteralPath (Join-Path $tomcat 'bin\bootstrap.jar'))) {
    throw 'Tomcat 9 is missing. Use the complete package or see README.md.'
}
if (-not (Test-Path -LiteralPath $config)) {
    Copy-Item -LiteralPath (Join-Path $root 'config\database.example.properties') -Destination $config
    Write-Host 'Created config\database.properties. Default server: localhost:1433; Windows Authentication.'
}
if ($Build -or -not (Test-Path -LiteralPath (Join-Path $root 'dist\MovieWeb.war'))) {
    & (Join-Path $root 'build.ps1') -TomcatHome $tomcat
}
$java = if ($env:JAVA_HOME -and (Test-Path -LiteralPath (Join-Path $env:JAVA_HOME 'bin\java.exe'))) {
    Join-Path $env:JAVA_HOME 'bin\java.exe'
} else { (Get-Command java.exe -ErrorAction Stop).Source }
foreach ($dir in @('conf','logs','temp','work','webapps')) {
    New-Item -ItemType Directory -Force -Path (Join-Path $base $dir) | Out-Null
}
Get-ChildItem -LiteralPath (Join-Path $tomcat 'conf') -File |
    Copy-Item -Destination (Join-Path $base 'conf') -Force
$serverXml = @"
<?xml version="1.0" encoding="UTF-8"?>
<Server port="-1">
  <Listener className="org.apache.catalina.core.JreMemoryLeakPreventionListener" />
  <Listener className="org.apache.catalina.core.ThreadLocalLeakPreventionListener" />
  <Service name="Catalina">
    <Connector address="127.0.0.1" port="$Port" protocol="org.apache.coyote.http11.Http11Nio2Protocol" connectionTimeout="20000" URIEncoding="UTF-8" />
    <Engine name="Catalina" defaultHost="localhost">
      <Host name="localhost" appBase="webapps" unpackWARs="true" autoDeploy="false">
        <Valve className="org.apache.catalina.valves.ErrorReportValve" showReport="false" showServerInfo="false" />
      </Host>
    </Engine>
  </Service>
</Server>
"@
[IO.File]::WriteAllText((Join-Path $base 'conf\server.xml'), $serverXml, (New-Object Text.UTF8Encoding($false)))
Copy-Item -LiteralPath (Join-Path $root 'dist\MovieWeb.war') -Destination (Join-Path $base 'webapps\MovieWeb.war') -Force
$native = Join-Path $root 'runtime\native'
$env:PATH = $native + ';' + $env:PATH
$env:MOVIEWEB_CONFIG = $config
$classPath = (Join-Path $tomcat 'bin\bootstrap.jar') + ';' + (Join-Path $tomcat 'bin\tomcat-juli.jar')
Write-Host "Open http://localhost:$Port/MovieWeb/movies"
Write-Host 'Keep this window open. Press Ctrl+C to stop. Database connection uses your current Windows account.'
& $java '-Dfile.encoding=UTF-8' "-Djava.library.path=$native" "-Dcatalina.home=$tomcat" "-Dcatalina.base=$base" `
    "-Djava.io.tmpdir=$(Join-Path $base 'temp')" "-Djava.util.logging.config.file=$(Join-Path $base 'conf\logging.properties')" `
    '-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager' `
    '--add-opens=java.base/java.lang=ALL-UNNAMED' '--add-opens=java.base/java.io=ALL-UNNAMED' `
    '--add-opens=java.base/java.util=ALL-UNNAMED' '--add-opens=java.base/java.util.concurrent=ALL-UNNAMED' `
    '--add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED' '-classpath' $classPath 'org.apache.catalina.startup.Bootstrap' 'start'
if ($LASTEXITCODE -ne 0) { throw "Server stopped with exit code $LASTEXITCODE. See runtime\server\logs." }
