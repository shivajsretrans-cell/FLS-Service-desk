from PIL import Image, ImageDraw
from pathlib import Path
src=Image.open(r'C:\Users\eTrans\AppData\Local\Temp\codex-clipboard-c2632d72-1dfc-46c2-ab19-47169b6f67f1.png').convert('RGB')
crop=src.crop((74,29,286,256))
pix=[]
for r,g,b in crop.getdata():
    v=max(r,g,b)
    alpha=0 if v<=7 else min(255,round((v-7)*255/(75-7)))
    if alpha==0: pix.append((0,0,0,0))
    else: pix.append((min(255,round(r*255/alpha)),min(255,round(g*255/alpha)),min(255,round(b*255/alpha)),alpha))
logo=Image.new('RGBA',crop.size);logo.putdata(pix)
# Tight transparent logo for web and app headers
logo=logo.resize((424,454),Image.Resampling.LANCZOS)
out=Path('outputs');out.mkdir(exist_ok=True)
logo.save(out/'fls-logo.png',optimize=True)
res=Path('outputs/android-app/app/src/main/res/drawable-nodpi');res.mkdir(parents=True,exist_ok=True)
logo.save(res/'fls_logo.png',optimize=True)
# Adaptive launcher foreground with generous safe-zone padding
fg=Image.new('RGBA',(432,432),(0,0,0,0)); fg_logo=logo.copy();fg_logo.thumbnail((350,374),Image.Resampling.LANCZOS);fg.alpha_composite(fg_logo,((432-fg_logo.width)//2,(432-fg_logo.height)//2));fg.save(res/'fls_launcher_foreground.png',optimize=True)
# PWA icon on a dark green tile
icon=Image.new('RGBA',(512,512),(16,37,31,255));ic=logo.copy();ic.thumbnail((408,436),Image.Resampling.LANCZOS);icon.alpha_composite(ic,((512-ic.width)//2,(512-ic.height)//2));icon.save(out/'fls-app-icon.png',optimize=True)
print('logo',logo.size,'alpha-range',logo.getchannel('A').getextrema(),'bytes', (out/'fls-logo.png').stat().st_size)
