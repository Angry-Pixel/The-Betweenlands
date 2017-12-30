package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelFirefly extends ModelBase {
	public ModelRenderer thorax;
	public ModelRenderer thorax2;
	public ModelRenderer dekschild;
	public ModelRenderer leg_left1;
	public ModelRenderer leg_right1;
	public ModelRenderer leg_left2;
	public ModelRenderer leg_right2;
	public ModelRenderer leg_left3;
	public ModelRenderer leg_right3;
	public ModelRenderer thorax3;
	public ModelRenderer head;
	public ModelRenderer wing_left;
	public ModelRenderer wing_right;
	public ModelRenderer antenna_left;
	public ModelRenderer antenna_right;
	public ModelRenderer leg_left1b;
	public ModelRenderer leg_right1b;

	public ModelFirefly() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.head = new ModelRenderer(this, 0, 17);
		this.head.setRotationPoint(0.0F, 0.0F, -1.0F);
		this.head.addBox(-1.01F, 0.0F, -2.0F, 2, 2, 2, 0.0F);
		this.setRotateAngle(head, 0.6373942428283291F, 0.0F, 0.0F);
		this.leg_left1b = new ModelRenderer(this, 11, 10);
		this.leg_left1b.setRotationPoint(0.0F, 1.0F, 0.0F);
		this.leg_left1b.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0, 0.0F);
		this.setRotateAngle(leg_left1b, 0.7740535232594852F, 0.0F, 0.0F);
		this.thorax = new ModelRenderer(this, 0, 0);
		this.thorax.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.thorax.addBox(-1.5F, 0.0F, 0.0F, 3, 2, 2, 0.0F);
		this.setRotateAngle(thorax, 0.40980330836826856F, 0.0F, 0.0F);
		this.leg_right1b = new ModelRenderer(this, 11, 10);
		this.leg_right1b.setRotationPoint(0.0F, 1.0F, 0.0F);
		this.leg_right1b.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0, 0.0F);
		this.setRotateAngle(leg_right1b, 0.7740535232594852F, 0.0F, 0.0F);
		this.leg_right2 = new ModelRenderer(this, 11, 13);
		this.leg_right2.setRotationPoint(-1.0F, 0.8F, 0.0F);
		this.leg_right2.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 0, 0.0F);
		this.setRotateAngle(leg_right2, -0.5009094953223726F, -0.091106186954104F, 0.0F);
		this.thorax3 = new ModelRenderer(this, 0, 10);
		this.thorax3.setRotationPoint(0.0F, 2.0F, 0.0F);
		this.thorax3.addBox(-1.0F, 0.0F, -2.0F, 2, 1, 2, 0.0F);
		this.setRotateAngle(thorax3, -0.136659280431156F, 0.0F, 0.0F);
		this.leg_left3 = new ModelRenderer(this, 11, 17);
		this.leg_left3.setRotationPoint(1.0F, 1.3F, 0.0F);
		this.leg_left3.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 0, 0.0F);
		this.setRotateAngle(leg_left3, -0.40980330836826856F, -0.091106186954104F, 0.22759093446006054F);
		this.antenna_left = new ModelRenderer(this, 8, 0);
		this.antenna_left.setRotationPoint(0.1F, 0.0F, -2.0F);
		this.antenna_left.addBox(0.0F, 0.0F, -3.0F, 2, 0, 3, 0.0F);
		this.setRotateAngle(antenna_left, -0.31869712141416456F, -0.36425021489121656F, 0.091106186954104F);
		this.antenna_right = new ModelRenderer(this, 8, 4);
		this.antenna_right.setRotationPoint(-0.1F, 0.0F, -2.0F);
		this.antenna_right.addBox(-2.0F, 0.0F, -3.0F, 2, 0, 3, 0.0F);
		this.setRotateAngle(antenna_right, -0.31869712141416456F, 0.36425021489121656F, -0.091106186954104F);
		this.leg_right1 = new ModelRenderer(this, 11, 8);
		this.leg_right1.setRotationPoint(-1.0F, 0.4F, 0.0F);
		this.leg_right1.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0, 0.0F);
		this.setRotateAngle(leg_right1, -1.0471975511965976F, 0.22759093446006054F, 0.045553093477052F);
		this.leg_right3 = new ModelRenderer(this, 11, 17);
		this.leg_right3.setRotationPoint(-1.0F, 1.3F, 0.0F);
		this.leg_right3.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 0, 0.0F);
		this.setRotateAngle(leg_right3, -0.40980330836826856F, 0.091106186954104F, -0.22759093446006054F);
		this.thorax2 = new ModelRenderer(this, 0, 5);
		this.thorax2.setRotationPoint(0.0F, 2.0F, 2.0F);
		this.thorax2.addBox(-1.51F, 0.0F, -2.0F, 3, 2, 2, 0.0F);
		this.setRotateAngle(thorax2, -0.27314402793711257F, 0.0F, 0.0F);
		this.wing_left = new ModelRenderer(this, 16, 0);
		this.wing_left.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.wing_left.addBox(0.0F, 0.0F, 0.0F, 5, 2, 0, 0.0F);
		this.setRotateAngle(wing_left, 1.5025539530419183F, 0.31869712141416456F, -0.136659280431156F);
		this.dekschild = new ModelRenderer(this, 0, 13);
		this.dekschild.setRotationPoint(0.0F, -0.0F, 2.0F);
		this.dekschild.addBox(-1.0F, 0.0F, -1.0F, 2, 2, 1, 0.0F);
		this.setRotateAngle(dekschild, -1.1838568316277536F, 0.0F, 0.0F);
		this.leg_left1 = new ModelRenderer(this, 11, 8);
		this.leg_left1.setRotationPoint(1.0F, 0.4F, 0.0F);
		this.leg_left1.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0, 0.0F);
		this.setRotateAngle(leg_left1, -1.0471975511965976F, -0.22759093446006054F, -0.045553093477052F);
		this.leg_left2 = new ModelRenderer(this, 11, 13);
		this.leg_left2.setRotationPoint(1.0F, 0.8F, 0.0F);
		this.leg_left2.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 0, 0.0F);
		this.setRotateAngle(leg_left2, -0.5009094953223726F, 0.091106186954104F, 0.0F);
		this.wing_right = new ModelRenderer(this, 16, 3);
		this.wing_right.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.wing_right.addBox(-5.0F, 0.0F, 0.0F, 5, 2, 0, 0.0F);
		this.setRotateAngle(wing_right, 1.5025539530419183F, -0.31869712141416456F, 0.136659280431156F);
		this.dekschild.addChild(this.head);
		this.leg_left1.addChild(this.leg_left1b);
		this.leg_right1.addChild(this.leg_right1b);
		this.thorax.addChild(this.leg_right2);
		this.thorax2.addChild(this.thorax3);
		this.thorax.addChild(this.leg_left3);
		this.head.addChild(this.antenna_left);
		this.head.addChild(this.antenna_right);
		this.thorax.addChild(this.leg_right1);
		this.thorax.addChild(this.leg_right3);
		this.thorax.addChild(this.thorax2);
		this.dekschild.addChild(this.wing_left);
		this.thorax.addChild(this.dekschild);
		this.thorax.addChild(this.leg_left1);
		this.thorax.addChild(this.leg_left2);
		this.dekschild.addChild(this.wing_right);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		this.thorax.setRotationPoint(0, 16.75F, -2.25F);

		GlStateManager.enableCull();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

		this.thorax.render(f5);

		GlStateManager.disableCull();
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
		float flap = MathHelper.sin(ageInTicks * 2.0F) * 0.6F;
		this.wing_left.rotateAngleZ = -0.5F - flap;
		this.wing_right.rotateAngleZ = 0.5F + flap;
		
		float swing = MathHelper.cos(ageInTicks * 0.1F) * 0.15F + 0.1F;
		this.thorax.rotateAngleX = 0.40980330836826856F + swing;
		this.head.rotateAngleX = 0.6373942428283291F - swing;
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
