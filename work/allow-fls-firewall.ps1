$ErrorActionPreference = 'Stop'
$name = 'FLSServiceDesk-8788-LAN'
$existing = Get-NetFirewallRule -Name $name -ErrorAction SilentlyContinue
if (-not $existing) {
    New-NetFirewallRule -Name $name -DisplayName 'FLS Service Desk local network' -Direction Inbound -Action Allow -Enabled True -Profile Public -Protocol TCP -LocalPort 8788 -RemoteAddress LocalSubnet -Program 'C:\Users\eTrans\.cache\codex-runtimes\codex-primary-runtime\dependencies\node\bin\node.exe' | Out-Null
}
Get-NetFirewallRule -Name $name | Select-Object Name,Enabled,Profile,Direction,Action | Format-List
