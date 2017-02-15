package thebetweenlands.client.render.entity;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;

import thebetweenlands.client.render.entity.RenderWeedwoodRowboat.ArmArticulation;
import thebetweenlands.client.render.model.entity.ModelPlayerRower;
import thebetweenlands.client.render.render.entity.layer.LayerRowerArmor;

public class RenderPlayerRower extends RenderPlayer {
    private ModelPlayerRower[] models;

    public RenderPlayerRower(RenderManager mgr, boolean useSmallArms) {
        super(mgr, useSmallArms);
        mainModel = new ModelPlayerRower(0, useSmallArms);
        layerRenderers.clear();
        LayerRowerArmor armor = new LayerRowerArmor(this);
        addLayer(armor);
        models = new ModelPlayerRower[] { (ModelPlayerRower) mainModel, armor.getChest(), armor.getLeggings() };
    }

    public void renderPilot(AbstractClientPlayer pilot, ArmArticulation leftArm, ArmArticulation rightArm, float bodyRotateAngleX, float bodyRotateAngleY, double x, double y, double z, float delta) {
        for (ModelPlayerRower model : models) {
            model.bipedLeftArm.rotateAngleX = leftArm.shoulderAngleX;
            model.bipedLeftArm.rotateAngleY = leftArm.shoulderAngleY;
            model.setLeftArmFlexionAngle(leftArm.flexionAngle);
            model.bipedRightArm.rotateAngleX = rightArm.shoulderAngleX;
            model.bipedRightArm.rotateAngleY = rightArm.shoulderAngleY;
            model.setRightArmFlexionAngle(rightArm.flexionAngle);
            model.bipedBody.rotateAngleX = bodyRotateAngleX;
            model.bipedBody.rotateAngleY = bodyRotateAngleY;
            model.bipedHead.rotateAngleX = -bodyRotateAngleX * 0.75F;
            model.bipedHead.rotateAngleY = -bodyRotateAngleY * 0.75F;
            model.bipedLeftArm.rotationPointZ = leftArm.shoulderZ * 16;
            model.bipedRightArm.rotationPointZ = rightArm.shoulderZ * 16;
        }
        float yaw = pilot.prevRotationYaw + (pilot.rotationYaw - pilot.prevRotationYaw) * delta;
        doRender(pilot, x, y, z, yaw, delta);
    }
}