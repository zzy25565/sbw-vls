package com.zzy.sbwvls.client;

import com.atsuishio.superbwarfare.client.renderer.entity.BasicVehicleRenderer;
import com.zzy.sbwvls.MergedVehicleEntity;
import com.zzy.sbwvls.TrackProfiles;
import com.atsuishio.superbwarfare.client.model.entity.VehicleModelInstance;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MergedVehicleRenderer extends BasicVehicleRenderer {
    private final TrackProfiles.Curve track;
    public MergedVehicleRenderer(EntityRendererProvider.Context manager, String id) {
        super(manager);
        track=TrackProfiles.forId(id);
        // Keep the base visibility state consistent with the recovered model profile.
        setHideForTurretControllerWhileZooming(track!=null && track.hideForTurretControllerWhileZooming());
    }
    @Override public float getBoneMoveY(float t){return track==null?super.getBoneMoveY(t):track.getBoneMoveY(t);}
    @Override public float getBoneMoveZ(float t){return track==null?super.getBoneMoveZ(t):track.getBoneMoveZ(t);}
    @Override public float getBoneRotX(float t){return track==null?super.getBoneRotX(t):track.getBoneRotX(t);}
    @Override public float getTrackDistance(){return track==null?super.getTrackDistance():track.getTrackDistance();}
    @Override public boolean hideForTurretControllerWhileZooming(){return track!=null && track.hideForTurretControllerWhileZooming();}
    @Override public void render(com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity vehicle,
            float yaw,float partial,PoseStack pose,net.minecraft.client.renderer.MultiBufferSource buffers,int light){
        var player=net.minecraft.client.Minecraft.getInstance().player;
        // Some imported models have several root bones; hiding only a bone named "root"
        // leaves their gun or hull inside the optic camera. Hide this local exterior pass.
        if(player!=null && player.getVehicle()==vehicle
                && com.atsuishio.superbwarfare.event.ClientEventHandler.zoomVehicle)return;
        super.render(vehicle,yaw,partial,pose,buffers,light);
    }
    @Override public void transformCustomModelPart(com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity vehicle,
            VehicleModelInstance model,PoseStack pose,float yaw,float partial){
        super.transformCustomModelPart(vehicle,model,pose,yaw,partial);
        // The stock recoil code writes X/Z displacement as though the bind translation were zero.
        // Imported hull roots can have a non-zero bind; preserve it before applying that displacement.
        var base=model.getBone("base");
        if(base!=null){base.x+=base.definition().bindX();base.z+=base.definition().bindZ();}
        if(track==null)return;
        // SBM's folded hierarchy stores the bind translation relative to its runtime parent.
        // A model-space pivot is not that translation when ancestors have non-zero pivots.
        var groups=model.getBoneGroups();
        for(var bones:java.util.List.of(groups.getLeftTrackMove(),groups.getRightTrackMove()))for(var b:bones){
            var d=b.definition();b.y+=d.bindY()-d.pivotY()*16;b.z+=d.bindZ()-d.pivotZ()*16;
        }
    }
}
