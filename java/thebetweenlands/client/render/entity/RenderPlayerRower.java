package thebetweenlands.client.render.entity;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.block.ModelPlayerRower;
import thebetweenlands.client.render.PlayerLimbPreMirrorer;
import thebetweenlands.client.render.entity.RenderWeedwoodRowboat.ArmArticulation;

public class RenderPlayerRower extends RenderPlayer {
	public static final RenderPlayerRower INSTANCE = new RenderPlayerRower();

	private ModelPlayerRower jointedMain, jointedArmorChest, jointedArmor;

	public RenderPlayerRower() {
		mainModel = jointedMain = new ModelPlayerRower(0);
		modelBipedMain = (ModelBiped) mainModel;
		modelArmorChestplate = jointedArmorChest = new ModelPlayerRower(1);
		modelArmor = jointedArmor = new ModelPlayerRower(0.5F);
		setRenderManager(RenderManager.instance);
	}

	@Override
	protected ResourceLocation getEntityTexture(AbstractClientPlayer player) {
		return PlayerLimbPreMirrorer.getPlayerSkin(player);
	}

	public void renderPilot(Entity pilot, ArmArticulation leftArm, ArmArticulation rightArm, float delta) {
		jointedMain.bipedLeftArm.rotateAngleX = leftArm.getShoulderAngleX();
		jointedMain.bipedLeftArm.rotateAngleY = leftArm.getShoulderAngleY();
		jointedMain.setLeftArmFlexionAngle(leftArm.getFlexionAngle());
		jointedArmorChest.bipedLeftArm.rotateAngleX = leftArm.getShoulderAngleX();
		jointedArmorChest.bipedLeftArm.rotateAngleY = leftArm.getShoulderAngleY();
		jointedArmorChest.setLeftArmFlexionAngle(leftArm.getFlexionAngle());
		jointedArmor.bipedLeftArm.rotateAngleX = leftArm.getShoulderAngleX();
		jointedArmor.bipedLeftArm.rotateAngleY = leftArm.getShoulderAngleY();
		jointedArmor.setLeftArmFlexionAngle(leftArm.getFlexionAngle());
		jointedMain.bipedRightArm.rotateAngleX = rightArm.getShoulderAngleX();
		jointedMain.bipedRightArm.rotateAngleY = rightArm.getShoulderAngleY();
		jointedMain.setRightArmFlexionAngle(rightArm.getFlexionAngle());
		jointedArmorChest.bipedRightArm.rotateAngleX = rightArm.getShoulderAngleX();
		jointedArmorChest.bipedRightArm.rotateAngleY = rightArm.getShoulderAngleY();
		jointedArmorChest.setRightArmFlexionAngle(rightArm.getFlexionAngle());
		jointedArmor.bipedRightArm.rotateAngleX = rightArm.getShoulderAngleX();
		jointedArmor.bipedRightArm.rotateAngleY = rightArm.getShoulderAngleY();
		jointedArmor.setRightArmFlexionAngle(rightArm.getFlexionAngle());
		doRender(pilot, 0, 0, 0, 0, delta);
	}
}
