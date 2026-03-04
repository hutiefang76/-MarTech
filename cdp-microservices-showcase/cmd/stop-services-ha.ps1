$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectDir = Resolve-Path (Join-Path $scriptDir "..")
$pidDir = Join-Path $projectDir "runtime-data\\pids"

if (-not (Test-Path $pidDir)) {
  Write-Host "no pid directory found"
  exit 0
}

Get-ChildItem -Path $pidDir -Filter *.pid | ForEach-Object {
  $pidValue = (Get-Content -Path $_.FullName -Raw).Trim()
  if ($pidValue) {
    try {
      Stop-Process -Id ([int]$pidValue) -Force -ErrorAction Stop
      Write-Host "stopped pid=$pidValue"
    } catch {
      Write-Host "skip pid=$pidValue"
    }
  }
  Remove-Item -Path $_.FullName -Force
}

Write-Host "ha demo services stopped"
