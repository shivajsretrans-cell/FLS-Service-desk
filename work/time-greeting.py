from pathlib import Path
p=Path('outputs/index.html'); s=p.read_text(encoding='utf-8')
old='''<div class="eyebrow">Vehicle service operations · 26 Oct 2026</div><h1>Good morning, team ☀</h1>'''
new='''<div class="eyebrow" id="currentDateLabel">Vehicle service operations</div><h1 id="greetingTitle">Welcome, team</h1>'''
if old not in s: raise SystemExit('dashboard greeting markup not found')
s=s.replace(old,new,1)
marker='''function dt(s){return s?new Date(s.length===10?s+'T12:00:00':s).toLocaleDateString('en-IN',{day:'numeric',month:'short'}):'—'}'''
fn='''function updateGreeting(){let now=new Date(),hour=now.getHours(),greeting=hour<12?'Good morning':hour<17?'Good afternoon':'Good evening',icon=hour<12?'☀':hour<17?'☼':'☾',date=new Intl.DateTimeFormat('en-IN',{day:'numeric',month:'short',year:'numeric'}).format(now),title=document.getElementById('greetingTitle'),label=document.getElementById('currentDateLabel');if(title)title.textContent=greeting+', team '+icon;if(label)label.textContent='Vehicle service operations · '+date}updateGreeting();setInterval(updateGreeting,60000);'''
if marker not in s: raise SystemExit('date function marker not found')
s=s.replace(marker,marker+fn,1)
p.write_text(s,encoding='utf-8')
