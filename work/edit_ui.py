from pathlib import Path
p=Path('outputs/index.html')
s=p.read_text(encoding='utf-8')
pairs=[
('''<button class="btn primary" id="alertsButton" onclick="enableAlerts()">Enable alerts</button></div><div class="notice-status"''','''<div><button class="btn primary" id="alertsButton" onclick="enableAlerts()">Enable alerts</button> <button class="btn" onclick="logout()">Sign out</button></div></div><div class="notice-status"'''),
('''<button class="btn" onclick="backToAssistantJobs()">← My assigned vehicles</button><div class="eyebrow"''','''<div style="display:flex;justify-content:space-between;gap:8px"><button class="btn" onclick="backToAssistantJobs()">← My assigned vehicles</button><button class="btn" onclick="logout()">Sign out</button></div><div class="eyebrow"'''),
('''<button class="btn" onclick="submitHourlyUpdate(\\''+esc(r.id)+'\\')">Update progress</button><button class="btn primary job-open"''','''<button class="btn" onclick="submitHourlyUpdate(\\''+esc(r.id)+'\\')">Update progress</button><button class="btn" onclick="openTransferDialog(\\''+esc(r.id)+'\\')">Transfer vehicle</button><button class="btn primary job-open"'''),
('''<div class="modalbg" id="bg"><form class="modal" id="form">''','''<div class="modalbg" id="transferBg"><form class="modal" id="transferForm"><h2>Transfer assigned vehicle</h2><p class="muted" id="transferVehicleLabel"></p><div class="field"><label>Transfer to service assistant *</label><select name="toAssistant" required><option value="">Loading assistants…</option></select></div><p id="transferMsg" class="authmsg"></p><p style="text-align:right"><button type="button" class="btn" onclick="closeTransferDialog()">Cancel</button> <button class="btn primary">Transfer vehicle</button></p></form></div><div class="modalbg" id="bg"><form class="modal" id="form">''')
]
for old,new in pairs:
    if old not in s: raise SystemExit('missing replacement: '+old[:90])
    s=s.replace(old,new,1)
p.write_text(s,encoding='utf-8')
