package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntityMummyArm;

public class ModelMummyArm extends MowzieModelBase {
	public MowzieModelRenderer armleft;
	public MowzieModelRenderer armleft2;

	public ModelMummyArm() {
		this.textureWidth = 128;
		this.textureHeight = 64;

		this.armleft2 = new MowzieModelRenderer(this, 28, 32);
		this.armleft2.setRotationPoint(0.0F, 9.0F, 0.0F);
		this.armleft2.addBox(0.0F, 0.0F, -1.5F, 2, 10, 2, 0.0F);
		this.setRotateAngle(armleft2, -0.5918411493512771F, 0.0F, 0.0F);
		this.armleft = new MowzieModelRenderer(this, 19, 32);
		this.armleft.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.armleft.addBox(0.0F, -1.0F, -1.5F, 2, 10, 2, 0.0F);
		this.setRotateAngle(armleft, 0.13735741213195374F, 0.7311184236604247F, -0.4316199240181977F);
		this.armleft.addChild(this.armleft2);

		setInitPose();
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		GlStateManager.scale(0.7D, 0.7D, 0.7D);
		this.armleft.render(f5);
	}

	private void setRotateAngle(ModelRenderer ModelRenderer, float x, float y, float z) {
		ModelRenderer.rotateAngleX = x;
		ModelRenderer.rotateAngleY = y;
		ModelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float yaw, float pitch, float partialTicks) {
		setToInitPose();

		EntityMummyArm arm = (EntityMummyArm) entity;

		armleft.rotateAngleY = (float) Math.toRadians(yaw);
		armleft.rotateAngleX = 3.4416F;
		armleft.rotateAngleZ = -0.1F;
		float offset = 0.0F;
		if(arm.attackSwing > 0) {
			walk(armleft, 1, -0.4F, false, offset, 0, ((float)Math.abs(arm.attackSwing-10)/2.0F+Math.signum(arm.attackSwing-10)*-1*partialTicks/2.0F), 1);
			walk(armleft2, 1, -0.6F, false, offset, 0, ((float)Math.abs(arm.attackSwing-10)/2.0F+Math.signum(arm.attackSwing-10)*-1*partialTicks/2.0F), 1);
		}
		if(arm.hurtTime > 0) {
			bob(armleft, 0.5F, -10, true, (arm.hurtTime-partialTicks) / 5.0F, 1);
		}
		swing(armleft, 1, 0.4f, false, offset, 0, (arm.ticksExisted + partialTicks) / 10.0F, 1);
		walk(armleft2, 1, 0.4f, false, offset, 0, (arm.ticksExisted + partialTicks) / 8.0F, 1);
		swing(armleft2, 1, 0.1f, false, offset, 0, (arm.ticksExisted + partialTicks) / 4.0F, 1);
		walk(armleft, 1, 0.1F, false, offset, 0, (arm.ticksExisted + partialTicks) / 10.0F, 1);
		walk(armleft, 2, 0.3F, false, offset, 0, (arm.ticksExisted + partialTicks) / 15.0F, 1);
		bob(armleft, 2, 1.8F, false, (arm.ticksExisted + partialTicks) / 15.0F, 1);
	}
}
