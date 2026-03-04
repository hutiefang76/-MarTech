param(
  [switch]$SkipBuild
)

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectDir = Resolve-Path (Join-Path $scriptDir "..")
$pidDir = Join-Path $projectDir "runtime-data\\pids"
$procLogDir = Join-Path $projectDir "runtime-data\\process-logs"

New-Item -ItemType Directory -Force -Path $pidDir | Out-Null
New-Item -ItemType Directory -Force -Path $procLogDir | Out-Null

Set-Location $projectDir

if (-not $SkipBuild) {
  Write-Host "building demo services..."
  & mvn -q -DskipTests -pl cdp-callchain-demo,cdp-connector-control-service,cdp-tag-task-service,cdp-flink-job-service -am package
  if ($LASTEXITCODE -ne 0) {
    throw "maven build failed"
  }
}

function Start-Instance {
  param(
    [string]$Module,
    [int]$Port,
    [int]$Instance,
    [string[]]$ExtraArgs
  )

  $jar = Join-Path $projectDir "$Module\\target\\$Module-1.0.0-SNAPSHOT.jar"
  if (-not (Test-Path $jar)) {
    throw "jar not found: $jar"
  }

  $outFile = Join-Path $procLogDir "$Module-$Port.out.log"
  $errFile = Join-Path $procLogDir "$Module-$Port.err.log"
  $pidFile = Join-Path $pidDir "$Module-$Port.pid"

  $args = @(
    "-jar", $jar,
    "--server.port=$Port",
    "--APP_INSTANCE=$Instance"
  ) + $ExtraArgs

  $proc = Start-Process -FilePath "java" -ArgumentList $args -RedirectStandardOutput $outFile -RedirectStandardError $errFile -PassThru
  Set-Content -Path $pidFile -Value $proc.Id -Encoding UTF8
  Write-Host "started $Module port=$Port pid=$($proc.Id)"
}

Start-Instance -Module "cdp-flink-job-service" -Port 19190 -Instance 1 -ExtraArgs @()
Start-Instance -Module "cdp-flink-job-service" -Port 19191 -Instance 2 -ExtraArgs @()
Start-Instance -Module "cdp-connector-control-service" -Port 19101 -Instance 1 -ExtraArgs @("--cdp.flink-job.base-url=http://localhost:19190")
Start-Instance -Module "cdp-connector-control-service" -Port 19102 -Instance 2 -ExtraArgs @("--cdp.flink-job.base-url=http://localhost:19190")
Start-Instance -Module "cdp-tag-task-service" -Port 19141 -Instance 1 -ExtraArgs @("--cdp.flink-job.base-url=http://localhost:19190")
Start-Instance -Module "cdp-tag-task-service" -Port 19142 -Instance 2 -ExtraArgs @("--cdp.flink-job.base-url=http://localhost:19190")
Start-Instance -Module "cdp-callchain-demo" -Port 19180 -Instance 1 -ExtraArgs @(
  "--cdp.target.connector-control-base-url=http://localhost:19101",
  "--cdp.target.tag-task-base-url=http://localhost:19141",
  "--cdp.target.flink-job-base-url=http://localhost:19190"
)
Start-Instance -Module "cdp-callchain-demo" -Port 19181 -Instance 2 -ExtraArgs @(
  "--cdp.target.connector-control-base-url=http://localhost:19101",
  "--cdp.target.tag-task-base-url=http://localhost:19141",
  "--cdp.target.flink-job-base-url=http://localhost:19190"
)

Write-Host "ha demo services started"
Write-Host "nacos instance list: http://localhost:8848/nacos"
