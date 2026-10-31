"""Recover original per-model track mathematics; never use one generic track shape.

Input: CFR output from the user's original addon JARs in local/review-20261031/track-sources.
Only numeric curve methods and numeric constants are ported. No original entity classes execute.
"""
from pathlib import Path
import re,json
ROOT=Path(__file__).resolve().parents[1]
SOURCE=ROOT/'local/review-20261031/track-sources'
mapping=dict(abrams_x='AbramsXRenderer',hyr0='HYR0Renderer',m10booker='M10BookerRenderer',m1a1hc='M1a1hcRenderer',m1a2sepv1='M1a2sepv1Renderer',m1a2sepv2='M1A2SEPV2Renderer',rail_tank='RailTankRenderer',t72b3='T72b3Renderer',t80b='T80BRenderer',t90_msa='T90MSRenderer',t90_msa_keep='T90MSRenderer',zbd04a='ZBD04ARenderer',zbd05='Zbd05Renderer',ztd05='Ztd05Renderer',ztz59a='ZTZ59ARenderer',ztz96a='Ztz96aRenderer',ztz99a='ZTZ99ASbmRenderer',aavp='AAVPModel',bmp1='BMP1Model',bmp1p='BMP1PModel',bmp1u='BMP1UModel',bmp2='BMP2Model',bmp2_noatgm='BMP2NoAtgmModel',bmp2d='BMP2DModel',bmp2m='BMP2MModel',bmp2md='BMP2MDModel',m109='M109Model',msta='MstaModel',ajax='AjaxModel',bmp_2_bakhcha='Bmp2BakhcaModel',bmpt_3k='BMPT3KModel',bradley='BradleyModel',cv90='CV90Model',leopard_2a7v='Leopard2A7VModel',oplot='OplotModel',puma='PumaModel',t72_b3m='T72B3MModel',terminator='TerminatorModel',m3a3='M3A3Renderer')
def block(s,start):
 at=s.index('{',start);depth=1;end=at+1
 while depth:
  if s[end]=='{':depth+=1
  elif s[end]=='}':depth-=1
  end+=1
 return s[start:end]
classes=[];periods={};audit=[]
for name in sorted(set(mapping.values())):
 matches=[p for p in SOURCE.rglob('*.java') if p.stem==name];assert len(matches)==1,(name,matches)
 p=matches[0];s=p.read_text(encoding='utf-8');actual=re.search(r'public class (\w+)',s)[1]
 fields=re.findall(r'    private static final (?:float|int|double)(?:\[\])*\s+\w+\s*=.*?;',s,re.S)
 methods=[]
 for m in re.finditer(r'    (?:public|private|protected) (?:static )?(?:float|boolean) (getBoneRotX|getBoneMoveY|getBoneMoveZ|getTrackDistance|hideForTurretControllerWhileZooming|getKeyframeValue|sample)\(',s):methods.append(block(s,m.start()))
 assert all(any(n+'(' in x for x in methods) for n in ['getBoneRotX','getBoneMoveY','getBoneMoveZ'])
 period=re.search(r'int wrapRange = (\d+)',s);period=int(period[1]) if period else 100;periods[name]=period
 # Their renderers sample a closed 0..80 table, and both original entity overrides
 # return 80 (confirmed in javap). The inherited default 100 stacks links at t=80.
 if name in {'HYR0Renderer','M1A2SEPV2Renderer'}:period=80;periods[name]=period
 text='\n'.join(fields+methods);text=text.replace(actual+'.',name+'.').replace('Mth.m_14179_','Mth.lerp').replace('Mth.m_14045_','Mth.clamp')
 # Correct demonstrable arithmetic defects in the originals, retaining their keyed paths.
 if name=='AbramsXRenderer':
  text=text.replace('(t - 93.6667f) / 5.8332977f','(t - 93.6667f) / (100.0f - 93.6667f)')
 if name=='RailTankRenderer':
  assert text.count('(t - 42.9167f) / 0.0f')==2
  text=text.replace('(t - 42.9167f) / 0.0f','(t - 42.9167f) / (44.25f - 42.9167f)')
  text=text.replace('if (t <= 92.5833f) {','if (t <= 91.75f) { return -210.0f; }\n        if (t <= 92.5833f) {',1).replace('(t - 92.5833f) / 0.83340454f','(t - 91.75f) / (92.5833f - 91.75f)')
  text=text.replace('(t - 94.25f) / 0.8332977f','(t - 94.25f) / (94.9167f - 94.25f)')
 assert not re.search(r'\b(?:Vehicle|Entity|GeoBone|ResourceLocation|CannonRecoil)',text),(name,'Non-numeric dependency')
 classes.append('    private static final class '+name+' implements Curve {\n'+text+'\n        public int period(){return '+str(period)+';}\n    }')
 audit.append({'profile':name,'source':p.relative_to(SOURCE).as_posix(),'period':period,'ids':[k for k,v in mapping.items() if v==name]})
interface='''    public interface Curve {
        float getBoneRotX(float t); float getBoneMoveY(float t); float getBoneMoveZ(float t);
        default float getTrackDistance(){return 2;} default int period(){return 100;}
        default boolean hideForTurretControllerWhileZooming(){return false;}
    }
'''
entries='\n'.join('        case "'+id+'" -> new '+name+'();' for id,name in mapping.items())
text='''package com.zzy.sbwvls;
import net.minecraft.util.Mth;
/** Recovered numeric curves; arithmetic corrections documented in restore_track_profiles.py. */
public final class TrackProfiles {
'''+interface+'    public static Curve forId(String id){return switch(id){\n'+entries+'\n        default -> null;\n    };}\n'+'\n'.join(classes)+'\n}\n'
(ROOT/'java/com/zzy/sbwvls/TrackProfiles.java').write_text(text,encoding='utf-8')
(ROOT/'local/review-20261031/track-profile-recovery.json').write_text(json.dumps(audit,indent=2),encoding='utf-8')
print('RESTORED_TRACK_PROFILES',len(mapping),'types',len(classes),'lines',len(text.splitlines()))
