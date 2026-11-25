from pathlib import Path
# Browser/PWA assets
p=Path('outputs/index.html'); s=p.read_text(encoding='utf-8')
pairs=[
('''<link rel="icon" href="/app-icon.svg" type="image/svg+xml">''','''<link rel="icon" href="/fls-app-icon.png" type="image/png">'''),
('''<div class="brand"><i>↗</i> FLS SERVICE</div>''','''<div class="brand"><img src="/fls-logo.png" alt="Fuel Sensor Care"><span>FLS SERVICE</span></div>'''),
('''<div class="eyebrow">FLS service operations</div>''','''<img class="auth-logo" src="/fls-logo.png" alt="Fuel Sensor Care">'''),
('''<div class="eyebrow">FLS service assistant</div>''','''<div class="assistant-brand"><img src="/fls-logo.png" alt="">Fuel Sensor Care · Service assistant</div>'''),
('''<div class="eyebrow" style="margin-top:16px">FLS service assistant</div>''','''<div class="assistant-brand" style="margin-top:16px"><img src="/fls-logo.png" alt="">Fuel Sensor Care · Service assistant</div>''')
]
for old,new in pairs:
    if old not in s: raise SystemExit('missing web brand hook '+old[:60])
    s=s.replace(old,new,1)
css='''.brand{display:flex;align-items:center;gap:9px}.brand img{width:38px;height:42px;object-fit:contain}.brand span{font-weight:700}.auth-logo{display:block;width:126px;height:auto;margin:0 auto 12px}.assistant-brand{display:flex;align-items:center;gap:8px;font-size:11px;color:#155c45;font-weight:bold}.assistant-brand img{width:38px;height:38px;object-fit:contain}'''
s=s.replace('</style>',css+'</style>',1)
p.write_text(s,encoding='utf-8')
# Launcher foreground now uses the supplied logo
p=Path('outputs/android-app/app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml')
p.write_text('''<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">\n    <background android:drawable="@color/launcher_background" />\n    <foreground android:drawable="@drawable/fls_launcher_foreground" />\n</adaptive-icon>\n''',encoding='utf-8')
# Native app header branding
p=Path('outputs/android-app/app/src/main/java/com/fls/servicedesk/MainActivity.java'); s=p.read_text(encoding='utf-8')
if 'import android.widget.ImageView;' not in s:s=s.replace('import android.widget.LinearLayout;','import android.widget.LinearLayout;\nimport android.widget.ImageView;')
old='''TextView brand = new TextView(this); brand.setText("FLS SERVICE");'''
new='''ImageView logo = new ImageView(this); logo.setImageResource(R.drawable.fls_logo); logo.setScaleType(ImageView.ScaleType.FIT_CENTER); bar.addView(logo,new LinearLayout.LayoutParams(dp(38),dp(42))); TextView brand = new TextView(this); brand.setText("FLS SERVICE");'''
if old not in s: raise SystemExit('native brand view not found')
s=s.replace(old,new,1);p.write_text(s,encoding='utf-8')
# PWA icon
p=Path('outputs/manifest.webmanifest');s=p.read_text(encoding='utf-8').replace('/app-icon.svg','/fls-app-icon.png').replace('"image/svg+xml"','"image/png"');p.write_text(s,encoding='utf-8')
# Add user image assets to explicit server allow-list
p=Path('outputs/server.js');s=p.read_text(encoding='utf-8').replace("'vehicle-background.png'","'vehicle-background.png','fls-logo.png','fls-app-icon.png'");p.write_text(s,encoding='utf-8')
# Native app version
p=Path('outputs/android-app/app/build.gradle');s=p.read_text(encoding='utf-8').replace('versionCode 10','versionCode 11').replace("versionName '1.0.9'","versionName '1.1.0'");p.write_text(s,encoding='utf-8')
