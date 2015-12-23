package thebetweenlands.client.render.entity;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.block.ModelPlayerRower;
import thebetweenlands.client.render.PlayerLimbPreMirrorer;
import thebetweenlands.client.render.entity.RenderWeedwoodRowboat.ArmArticulation;

public class RenderPlayerRower extends RenderPlayer {
	private ModelPlayerRower[] models;

	public RenderPlayerRower() {
		mainModel = new ModelPlayerRower(0);
		modelBipedMain = (ModelBiped) mainModel;
		modelArmorChestplate = new ModelPlayerRower(1);
		modelArmor = new ModelPlayerRower(0.5F);
		models = new ModelPlayerRower[] { (ModelPlayerRower) mainModel, (ModelPlayerRower) modelArmorChestplate, (ModelPlayerRower) modelArmor };
	}

	@Override
	protected void renderEquippedItems(AbstractClientPlayer player, float delta) {}

	@Override
	protected ResourceLocation getEntityTexture(AbstractClientPlayer player) {
		return PlayerLimbPreMirrorer.getPlayerSkin(player);
	}

	public void renderPilot(Entity pilot, ArmArticulation leftArm, ArmArticulation rightArm, float bodyRotateAngleX, float bodyRotateAngleY, float delta) {
		for (ModelPlayerRower model : models) {
			model.bipedLeftArm.rotateAngleX = leftArm.getShoulderAngleX();
			model.bipedLeftArm.rotateAngleY = leftArm.getShoulderAngleY();
			model.setLeftArmFlexionAngle(leftArm.getFlexionAngle());
			model.bipedRightArm.rotateAngleX = rightArm.getShoulderAngleX();
			model.bipedRightArm.rotateAngleY = rightArm.getShoulderAngleY();
			model.setRightArmFlexionAngle(rightArm.getFlexionAngle());
			model.bipedBody.rotateAngleX = bodyRotateAngleX;
			model.bipedBody.rotateAngleY = bodyRotateAngleY;
			model.bipedHead.rotateAngleX = -bodyRotateAngleX * 0.75F;
			model.bipedHead.rotateAngleY = -bodyRotateAngleY * 0.75F;
		}
		doRender(pilot, 0, 0, 0, 0, delta);
	}
}
