package cn.zzy.vehicles.mechanics;
import cn.zzy.vehicles.ZzyVehicles;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import java.util.*;

/** 弹体飞出实体更新范围后也按时清理；只保存 UUID，不让临时演出占住区块。 */
@EventBusSubscriber(modid=ZzyVehicles.ID)
public final class LaunchEffectCleanup {
    private static final Map<ServerLevel,Map<UUID,Long>> pending=new WeakHashMap<>();
    public static void register(ServerLevel level,UUID id){pending.computeIfAbsent(level,k->new HashMap<>()).put(id,level.getGameTime()+60);}
    @SubscribeEvent public static void tick(LevelTickEvent.Post event){
        if(!(event.getLevel() instanceof ServerLevel level))return;var jobs=pending.get(level);if(jobs==null)return;
        var i=jobs.entrySet().iterator();while(i.hasNext()){var e=i.next();if(level.getGameTime()>=e.getValue()){var entity=level.getEntity(e.getKey());if(entity!=null)entity.discard();i.remove();}}
        if(jobs.isEmpty())pending.remove(level);
    }
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel() instanceof ServerLevel l)pending.remove(l);}
    private LaunchEffectCleanup(){}
}
