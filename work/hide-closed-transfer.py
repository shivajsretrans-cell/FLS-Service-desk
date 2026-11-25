from pathlib import Path
p=Path('outputs/index.html')
s=p.read_text(encoding='utf-8')
old='''<button class="btn" onclick="submitHourlyUpdate(\\''+esc(r.id)+'\\')">Update progress</button><button class="btn" onclick="openTransferDialog(\\''+esc(r.id)+'\\')">Transfer vehicle</button><button class="btn primary job-open"'''
new='''<button class="btn" onclick="submitHourlyUpdate(\\''+esc(r.id)+'\\')">Update progress</button>'+(r.assistantOk||r.status==='Service report submitted'||r.status==='Completed'?'':'<button class="btn" onclick="openTransferDialog(\\''+esc(r.id)+'\\')">Transfer vehicle</button>')+'<button class="btn primary job-open"'''
if old not in s: raise SystemExit('transfer button template missing')
s=s.replace(old,new,1)
p.write_text(s,encoding='utf-8')
