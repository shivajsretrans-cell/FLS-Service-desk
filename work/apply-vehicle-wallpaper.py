from pathlib import Path
p=Path('outputs/index.html'); s=p.read_text(encoding='utf-8')
css='''#authgate{background:#10251f;background-image:linear-gradient(115deg,rgba(9,29,24,.82),rgba(9,29,24,.42)),url('/vehicle-background.png');background-position:center;background-size:cover;background-repeat:no-repeat}.authcard{box-shadow:0 24px 70px rgba(0,0,0,.28)}#dashboard>.head{min-height:190px;padding:22px 24px;border-radius:16px;overflow:hidden;align-items:center;background:linear-gradient(90deg,rgba(11,34,27,.96) 0%,rgba(11,34,27,.78) 44%,rgba(11,34,27,.18) 100%),url('/vehicle-background.png') 62% center/cover no-repeat}#dashboard>.head .eyebrow{color:#d5f36a}#dashboard>.head h1{color:#fff}#dashboard>.head .muted{color:#e8f0eb}#dashboard>.head>div:last-child{display:flex;justify-content:flex-end;gap:8px;flex-wrap:wrap}@media(max-width:600px){#dashboard>.head{min-height:180px;padding:18px;background-position:58% center}#dashboard>.head>div:last-child{justify-content:flex-start;margin-top:14px}}'''
if '</style>' not in s: raise SystemExit('style close missing')
s=s.replace('</style>',css+'</style>',1)
p.write_text(s,encoding='utf-8')
