from pathlib import Path
p=Path('outputs/index.html'); s=p.read_text(encoding='utf-8')
pairs=[
(r'''<td>'+esc(x.customer)+'</td><td><b>'+esc(x.assignedAssistant||'Unassigned')''',r'''<td>'+esc(x.customer)+(x.phone?'<small>Contact: <a href="tel:'+esc(String(x.phone).replace(/[^\d+]/g,''))+'">'+esc(x.phone)+'</a></small>':'')+'</td><td><b>'+esc(x.assignedAssistant||'Unassigned')'''),
(r'''(x.progressUpdate?'<small>Assistant update: '+esc(x.progressUpdate)+'</small>':'')+'</td>''',r'''(x.progressUpdate?'<small>Assistant update: '+esc(x.progressUpdate)+'</small>':'')+(x.assistantOk||x.problemFound||x.actionTaken?'<small><b>Problem found:</b> '+esc(x.problemFound||'—')+'</small><small><b>Action taken:</b> '+esc(x.actionTaken||'—')+'</small>':'')+'</td>'''),
(r''''Issue','Status','Priority','Follow-up','Notes'],ac=''',r''''Issue','Problem found','Action taken','Status','Priority','Follow-up','Notes'],ac=''')
]
for old,new in pairs:
 if old not in s: raise SystemExit('missing replacement: '+old[:75])
 s=s.replace(old,new,1)
old=r'''r=db.r.map(x=>[x.id,x.created,x.assignedAt||'',x.completedAt||'',x.customer,x.phone,x.vehicle,x.reg,x.location,x.assignedAssistant||'Unassigned',x.issue,x.status,x.priority,x.followup,x.notes])'''
new=r'''r=db.r.map(x=>[x.id,x.created,x.assignedAt||'',x.completedAt||'',x.customer,x.phone,x.vehicle,x.reg,x.location,x.assignedAssistant||'Unassigned',x.issue,x.problemFound||'',x.actionTaken||'',x.status,x.priority,x.followup,x.notes])'''
if old not in s: raise SystemExit('missing Excel row mapper')
s=s.replace(old,new,1)
old=r'''<p><b>'+esc(r.customer||'Customer')+'</b> · '+esc(r.id)+'</p><p>'+esc(r.issue||'No issue details')+'</p><p><b>Assigned:</b>'''
new=r'''<p><b>'+esc(r.customer||'Customer')+'</b> · '+esc(r.id)+'</p><p><b>Contact:</b> '+(r.phone?'<a href="tel:'+esc(String(r.phone).replace(/[^\d+]/g,''))+'">'+esc(r.phone)+'</a>':'—')+'</p><p>'+esc(r.issue||'No issue details')+'</p><p><b>Assigned:</b>'''
if old not in s: raise SystemExit('missing assistant contact location')
s=s.replace(old,new,1)
old=r'''(r.completedAt?'<p><b>Work completed:</b> '+dtm(r.completedAt)+'</p>':'')+(r.progressUpdate?'''
new=r'''(r.completedAt?'<p><b>Work completed:</b> '+dtm(r.completedAt)+'</p>':'')+(r.assistantOk||r.problemFound||r.actionTaken?'<div class="completed-report"><p><b>Problem found:</b> '+esc(r.problemFound||'—')+'</p><p><b>Action taken:</b> '+esc(r.actionTaken||'—')+'</p></div>':'')+(r.progressUpdate?'''
if old not in s: raise SystemExit('missing completed report location')
s=s.replace(old,new,1)
p.write_text(s,encoding='utf-8')
