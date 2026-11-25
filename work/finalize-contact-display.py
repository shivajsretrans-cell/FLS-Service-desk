from pathlib import Path
p=Path('outputs/index.html'); s=p.read_text(encoding='utf-8')
s=s.replace('''(x.phone?'<small>Contact: <a href="tel:'+esc(String(x.phone).replace(/[^\\d+]/g,''))+'">'+esc(x.phone)+'</a></small>':'')''','''(x.phone?'<small>Contact: '+esc(x.phone)+'</small>':'')''',1)
s=s.replace('''(r.phone?'<a href="tel:'+esc(String(r.phone).replace(/[^\\d+]/g,''))+'">'+esc(r.phone)+'</a>':'—')''','''esc(r.phone||'—')''',1)
s=s.replace('.assistant-card .job-open{min-height:40px}.assistant-card .job-meta>button{min-height:40px}', '.assistant-card .job-open{min-height:40px}.assistant-card .job-meta>button{min-height:40px}.completed-report{background:#f4f8f4;border-left:3px solid #155c45;padding:4px 10px;margin:10px 0}.completed-report p{margin:6px 0}a{color:#155c45}')
p.write_text(s,encoding='utf-8')
