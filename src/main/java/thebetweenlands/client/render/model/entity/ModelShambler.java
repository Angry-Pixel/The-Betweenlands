package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityShambler;

@SideOnly(Side.CLIENT)
public class ModelShambler extends ModelBase {
     ModelRenderer body_base;
     ModelRenderer body2;
     ModelRenderer head1;
     ModelRenderer body3;
     ModelRenderer hindleg_right1;
     ModelRenderer hindleg_left1;
     ModelRenderer weird_butt;
     ModelRenderer surprise_tail;
     ModelRenderer tail2;
     ModelRenderer tail3;
     ModelRenderer tail4;
     ModelRenderer tail5;
     ModelRenderer tail6;
     ModelRenderer hindleg_right2;
     ModelRenderer hindleg_right3;
     ModelRenderer foot_right1;
     ModelRenderer toe_right1;
     ModelRenderer toe_right2;
     ModelRenderer toe_right3;
     ModelRenderer hindleg_left2;
     ModelRenderer hindleg_left3;
     ModelRenderer foot_left1;
     ModelRenderer toe_left1;
     ModelRenderer toe_left2;
     ModelRenderer toe_left3;
     ModelRenderer mouth;
     ModelRenderer mouth_arm1a;
     ModelRenderer mouth_arm2a;
     ModelRenderer mouth_arm3a;
     ModelRenderer mouth_arm4a;
     ModelRenderer cranialthing1;
     ModelRenderer mouth_arm1b;
     ModelRenderer mouth_arm1c;
     ModelRenderer mouth_arm2b;
     ModelRenderer mouth_arm2c;
     ModelRenderer mouth_arm3b;
     ModelRenderer mouth_arm3c;
     ModelRenderer mouth_arm4b;
     ModelRenderer mouth_arm4c;
     ModelRenderer cranialthing2;
    
     ModelRenderer tongue_part;

     ModelRenderer tongue_end;
     ModelRenderer tongue1;
     ModelRenderer tongue2;
     ModelRenderer teeth1;
     ModelRenderer teeth2;

    public ModelShambler() {
        textureWidth = 128;
        textureHeight = 128;
        toe_left3 = new ModelRenderer(this, 61, 55);
        toe_left3.setRotationPoint(-0.5F, 0.0F, 2.0F);
        toe_left3.addBox(-2.0F, 0.0F, -5.0F, 2, 2, 6, 0.0F);
        setRotateAngle(toe_left3, -0.045553093477052F, 0.18203784098300857F, -0.091106186954104F);
        weird_butt = new ModelRenderer(this, 0, 42);
        weird_butt.setRotationPoint(0.0F, 0.0F, 3.0F);
        weird_butt.addBox(-3.0F, 0.0F, 0.0F, 6, 6, 4, 0.0F);
        setRotateAngle(weird_butt, -0.5009094953223726F, 0.0F, 0.0F);
        tail2 = new ModelRenderer(this, 0, 61);
        tail2.setRotationPoint(0.015F, 0.0F, 3.0F);
        tail2.addBox(-2.0F, -4.0F, 0.0F, 4, 4, 5, 0.0F);
        setRotateAngle(tail2, 0.5009094953223726F, 0.0F, 0.0F);
        mouth = new ModelRenderer(this, 85, 16);
        mouth.setRotationPoint(0.0F, -2.5F, -6.0F);
        mouth.addBox(-3.0F, 0.0F, -2.0F, 6, 6, 2, 0.0F);
        mouth_arm3a = new ModelRenderer(this, 85, 46);
        mouth_arm3a.setRotationPoint(-3.9F, 4.3F, -5.0F);
        mouth_arm3a.addBox(0.0F, -3.0F, -4.0F, 3, 3, 5, 0.0F);
        setRotateAngle(mouth_arm3a, -0.08726646259971647F, 0.0F, 0.0F);
        mouth_arm2b = new ModelRenderer(this, 102, 34);
        mouth_arm2b.setRotationPoint(-0.5F, 0.5F, -4.0F);
        mouth_arm2b.addBox(-2.0F, 0.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(mouth_arm2b, 0.36425021489121656F, 0.36425021489121656F, 0.045553093477052F);
        tail5 = new ModelRenderer(this, 0, 88);
        tail5.setRotationPoint(0.015F, 0.0F, 4.0F);
        tail5.addBox(-1.5F, -3.0F, 0.0F, 3, 3, 4, 0.0F);
        setRotateAngle(tail5, 0.8651597102135892F, 0.0F, 0.0F);
        tail6 = new ModelRenderer(this, 0, 96);
        tail6.setRotationPoint(0.015F, 0.0F, 4.0F);
        tail6.addBox(-1.5F, -3.0F, 0.0F, 3, 3, 3, 0.0F);
        setRotateAngle(tail6, 0.8196066167365371F, 0.0F, 0.0F);
        mouth_arm4b = new ModelRenderer(this, 102, 55);
        mouth_arm4b.setRotationPoint(-0.5F, -0.5F, -4.0F);
        mouth_arm4b.addBox(-2.0F, -2.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(mouth_arm4b, -0.36425021489121656F, 0.36425021489121656F, -0.045553093477052F);
        toe_right2 = new ModelRenderer(this, 40, 49);
        toe_right2.setRotationPoint(0.0F, -0.2F, 0.0F);
        toe_right2.addBox(-1.0F, 0.0F, -2.5F, 2, 2, 3, 0.0F);
        setRotateAngle(toe_right2, -0.045553093477052F, 0.0F, 0.0F);
        mouth_arm2a = new ModelRenderer(this, 102, 25);
        mouth_arm2a.setRotationPoint(3.9F, -3.3F, -5.0F);
        mouth_arm2a.addBox(-3.0F, 0.0F, -4.0F, 3, 3, 5, 0.0F);
        setRotateAngle(mouth_arm2a, 0.08726646259971647F, 0.0F, 0.0F);
        cranialthing1 = new ModelRenderer(this, 85, 74);
        cranialthing1.setRotationPoint(0.0F, -1.5F, 1.0F);
        cranialthing1.addBox(-4.0F, -2.0F, 0.0F, 8, 2, 3, 0.0F);
        setRotateAngle(cranialthing1, 0.18203784098300857F, 0.0F, 0.0F);
        toe_right1 = new ModelRenderer(this, 40, 41);
        toe_right1.setRotationPoint(-0.5F, 0.0F, 2.0F);
        toe_right1.addBox(-2.0F, 0.0F, -4.0F, 2, 2, 5, 0.0F);
        setRotateAngle(toe_right1, -0.045553093477052F, 0.18203784098300857F, -0.091106186954104F);
        hindleg_left3 = new ModelRenderer(this, 61, 25);
        hindleg_left3.setRotationPoint(3.0F, 6.0F, 4.0F);
        hindleg_left3.addBox(-3.02F, 0.0F, -4.0F, 3, 4, 4, 0.0F);
        setRotateAngle(hindleg_left3, -1.0471975511965976F, 0.0F, 0.0F);
        toe_left1 = new ModelRenderer(this, 61, 41);
        toe_left1.setRotationPoint(0.5F, 0.0F, 2.0F);
        toe_left1.addBox(0.0F, 0.0F, -4.0F, 2, 2, 5, 0.0F);
        setRotateAngle(toe_left1, -0.045553093477052F, -0.18203784098300857F, 0.091106186954104F);
        hindleg_right3 = new ModelRenderer(this, 40, 25);
        hindleg_right3.setRotationPoint(0.0F, 6.0F, 4.0F);
        hindleg_right3.addBox(0.02F, 0.0F, -4.0F, 3, 4, 4, 0.0F);
        setRotateAngle(hindleg_right3, -1.0471975511965976F, 0.0F, 0.0F);
        hindleg_right1 = new ModelRenderer(this, 40, 0);
        hindleg_right1.setRotationPoint(-4.0F, 5.0F, 3.0F);
        hindleg_right1.addBox(-2.0F, -3.0F, -5.0F, 3, 6, 7, 0.0F);
        setRotateAngle(hindleg_right1, 0.5235987755982988F, 0.0F, 0.0F);
        mouth_arm2c = new ModelRenderer(this, 102, 40);
        mouth_arm2c.setRotationPoint(0.0F, 0.01F, -3.0F);
        mouth_arm2c.addBox(-2.0F, 0.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(mouth_arm2c, 0.0F, 0.5462880558742251F, 0.0F);
        hindleg_left1 = new ModelRenderer(this, 61, 0);
        hindleg_left1.setRotationPoint(4.0F, 5.0F, 3.0F);
        hindleg_left1.addBox(-1.0F, -3.0F, -5.0F, 3, 6, 7, 0.0F);
        setRotateAngle(hindleg_left1, 0.5235987755982988F, 0.0F, 0.0F);
        hindleg_left2 = new ModelRenderer(this, 61, 14);
        hindleg_left2.setRotationPoint(-1.02F, 3.0F, -5.0F);
        hindleg_left2.addBox(0.0F, 0.0F, 0.0F, 3, 6, 4, 0.0F);
        setRotateAngle(hindleg_left2, 0.4363323129985824F, 0.0F, 0.0F);
        surprise_tail = new ModelRenderer(this, 0, 53);
        surprise_tail.setRotationPoint(0.0F, 6.0F, 4.0F);
        surprise_tail.addBox(-2.0F, -4.0F, 0.0F, 4, 4, 3, 0.0F);
        setRotateAngle(surprise_tail, 0.6829473363053812F, 0.0F, 0.0F);
        mouth_arm3c = new ModelRenderer(this, 85, 61);
        mouth_arm3c.setRotationPoint(0.0F, -0.01F, -3.0F);
        mouth_arm3c.addBox(0.0F, -2.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(mouth_arm3c, 0.0F, -0.5462880558742251F, 0.0F);
        toe_left2 = new ModelRenderer(this, 61, 49);
        toe_left2.setRotationPoint(0.0F, -0.2F, 0.0F);
        toe_left2.addBox(-1.0F, 0.0F, -2.5F, 2, 2, 3, 0.0F);
        setRotateAngle(toe_left2, -0.045553093477052F, 0.0F, 0.0F);
        mouth_arm4c = new ModelRenderer(this, 102, 61);
        mouth_arm4c.setRotationPoint(0.0F, -0.01F, -3.0F);
        mouth_arm4c.addBox(-2.0F, -2.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(mouth_arm4c, 0.0F, 0.5462880558742251F, 0.0F);
        tail3 = new ModelRenderer(this, 0, 71);
        tail3.setRotationPoint(0.0F, 0.0F, 5.0F);
        tail3.addBox(-1.5F, -3.0F, 0.0F, 3, 3, 5, 0.0F);
        setRotateAngle(tail3, 0.7740535232594852F, 0.0F, 0.0F);
        body_base = new ModelRenderer(this, 0, 0);
        body_base.setRotationPoint(0.0F, 8.2F, -3.0F);
        body_base.addBox(-5.0F, 0.0F, 0.0F, 10, 8, 7, 0.0F);
        foot_right1 = new ModelRenderer(this, 40, 34);
        foot_right1.setRotationPoint(1.5F, 4.0F, -4.0F);
        foot_right1.addBox(-1.5F, 0.0F, 0.0F, 3, 2, 4, 0.0F);
        setRotateAngle(foot_right1, 0.4886921905584123F, 0.0F, 0.0F);
        body3 = new ModelRenderer(this, 0, 30);
        body3.setRotationPoint(0.0F, 0.0F, 5.0F);
        body3.addBox(-4.01F, 0.0F, 0.0F, 8, 8, 3, 0.0F);
        setRotateAngle(body3, -0.27314402793711257F, 0.0F, 0.0F);
        toe_right3 = new ModelRenderer(this, 40, 55);
        toe_right3.setRotationPoint(0.5F, 0.0F, 2.0F);
        toe_right3.addBox(0.0F, 0.0F, -5.0F, 2, 2, 6, 0.0F);
        setRotateAngle(toe_right3, -0.045553093477052F, -0.18203784098300857F, 0.091106186954104F);
        mouth_arm1b = new ModelRenderer(this, 85, 34);
        mouth_arm1b.setRotationPoint(0.5F, 0.5F, -4.0F);
        mouth_arm1b.addBox(0.0F, 0.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(mouth_arm1b, 0.36425021489121656F, -0.36425021489121656F, -0.045553093477052F);
        cranialthing2 = new ModelRenderer(this, 85, 80);
        cranialthing2.setRotationPoint(0.0F, 0.0F, 3.0F);
        cranialthing2.addBox(-3.0F, -2.0F, 0.0F, 6, 2, 2, 0.0F);
        setRotateAngle(cranialthing2, 0.18203784098300857F, 0.0F, 0.0F);
        head1 = new ModelRenderer(this, 85, 0);
        head1.setRotationPoint(0.0F, 1.0F, 2.0F);
        head1.addBox(-4.0F, -3.5F, -6.0F, 8, 8, 7, 0.0F);
        mouth_arm3b = new ModelRenderer(this, 85, 55);
        mouth_arm3b.setRotationPoint(0.5F, -0.5F, -4.0F);
        mouth_arm3b.addBox(0.0F, -2.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(mouth_arm3b, -0.36425021489121656F, -0.36425021489121656F, 0.045553093477052F);
        mouth_arm1a = new ModelRenderer(this, 85, 25);
        mouth_arm1a.setRotationPoint(-3.9F, -3.3F, -5.0F);
        mouth_arm1a.addBox(0.0F, 0.0F, -4.0F, 3, 3, 5, 0.0F);
        setRotateAngle(mouth_arm1a, 0.08726646259971647F, 0.0F, 0.0F);
        mouth_arm1c = new ModelRenderer(this, 85, 40);
        mouth_arm1c.setRotationPoint(0.0F, 0.01F, -3.0F);
        mouth_arm1c.addBox(0.0F, 0.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(mouth_arm1c, 0.0F, -0.5462880558742251F, 0.0F);
        tail4 = new ModelRenderer(this, 0, 80);
        tail4.setRotationPoint(0.015F, 0.0F, 5.0F);
        tail4.addBox(-1.5F, -3.0F, 0.0F, 3, 3, 4, 0.0F);
        setRotateAngle(tail4, 0.8651597102135892F, 0.0F, 0.0F);
        foot_left1 = new ModelRenderer(this, 61, 34);
        foot_left1.setRotationPoint(-1.5F, 4.0F, -4.0F);
        foot_left1.addBox(-1.5F, 0.0F, 0.0F, 3, 2, 4, 0.0F);
        setRotateAngle(foot_left1, 0.4886921905584123F, 0.0F, 0.0F);
        body2 = new ModelRenderer(this, 0, 16);
        body2.setRotationPoint(0.0F, 0.0F, 7.0F);
        body2.addBox(-4.0F, 0.0F, 0.0F, 8, 8, 5, 0.0F);
        setRotateAngle(body2, -0.36425021489121656F, 0.0F, 0.0F);
        hindleg_right2 = new ModelRenderer(this, 40, 14);
        hindleg_right2.setRotationPoint(-1.99F, 3.0F, -5.0F);
        hindleg_right2.addBox(0.0F, 0.0F, 0.0F, 3, 6, 4, 0.0F);
        setRotateAngle(hindleg_right2, 0.4363323129985824F, 0.0F, 0.0F);
        mouth_arm4a = new ModelRenderer(this, 102, 46);
        mouth_arm4a.setRotationPoint(3.9F, 4.3F, -5.0F);
        mouth_arm4a.addBox(-3.0F, -3.0F, -4.0F, 3, 3, 5, 0.0F);
        setRotateAngle(mouth_arm4a, -0.0890117918517108F, 0.0F, 0.0F);
        foot_left1.addChild(toe_left3);
        body3.addChild(weird_butt);
        surprise_tail.addChild(tail2);
        head1.addChild(mouth);
        head1.addChild(mouth_arm3a);
        mouth_arm2a.addChild(mouth_arm2b);
        tail4.addChild(tail5);
        tail5.addChild(tail6);
        mouth_arm4a.addChild(mouth_arm4b);
        foot_right1.addChild(toe_right2);
        head1.addChild(mouth_arm2a);
        head1.addChild(cranialthing1);
        foot_right1.addChild(toe_right1);
        hindleg_left2.addChild(hindleg_left3);
        foot_left1.addChild(toe_left1);
        hindleg_right2.addChild(hindleg_right3);
        body2.addChild(hindleg_right1);
        mouth_arm2b.addChild(mouth_arm2c);
        body2.addChild(hindleg_left1);
        hindleg_left1.addChild(hindleg_left2);
        weird_butt.addChild(surprise_tail);
        mouth_arm3b.addChild(mouth_arm3c);
        foot_left1.addChild(toe_left2);
        mouth_arm4b.addChild(mouth_arm4c);
        tail2.addChild(tail3);
        hindleg_right3.addChild(foot_right1);
        body2.addChild(body3);
        foot_right1.addChild(toe_right3);
        mouth_arm1a.addChild(mouth_arm1b);
        cranialthing1.addChild(cranialthing2);
        body_base.addChild(head1);
        mouth_arm3a.addChild(mouth_arm3b);
        head1.addChild(mouth_arm1a);
        mouth_arm1b.addChild(mouth_arm1c);
        tail3.addChild(tail4);
        hindleg_left3.addChild(foot_left1);
        body_base.addChild(body2);
        hindleg_right1.addChild(hindleg_right2);
        head1.addChild(mouth_arm4a);

        tongue_part = new ModelRenderer(this, 85, 86);
        tongue_part.setRotationPoint(0.0F, 16.0F, 0.0F);
        tongue_part.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4, 0.0F);

        tongue_end = new ModelRenderer(this, 85, 86);
        tongue_end.setRotationPoint(0.0F, 16.0F, 0.0F);
        tongue_end.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4, 0.0F);
        tongue1 = new ModelRenderer(this, 85, 93);
        tongue1.setRotationPoint(0.0F, 0.0F, -1.0F);
        tongue1.addBox(-1.5F, 0.0F, -2.0F, 3, 2, 2, 0.0F);
        setRotateAngle(tongue1, 0.5009094953223726F, 0.0F, 0.0F);
        teeth2 = new ModelRenderer(this, 96, 98);
        teeth2.setRotationPoint(0.0F, -2.0F, -2.0F);
        teeth2.addBox(-1.5F, 0.0F, -2.0F, 3, 2, 2, 0.0F);
        tongue2 = new ModelRenderer(this, 96, 93);
        tongue2.setRotationPoint(0.0F, 0.0F, -1.0F);
        tongue2.addBox(-1.5F, -2.0F, -2.0F, 3, 2, 2, 0.0F);
        setRotateAngle(tongue2, -0.5009094953223726F, 0.0F, 0.0F);
        teeth1 = new ModelRenderer(this, 85, 98);
        teeth1.setRotationPoint(0.0F, 2.0F, -2.0F);
        teeth1.addBox(-1.5F, -2.0F, -2.0F, 3, 2, 2, 0.0F);
        tongue_end.addChild(tongue1);
        tongue2.addChild(teeth2);
        tongue_end.addChild(tongue2);
        tongue1.addChild(teeth1);

    }

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ticksExisted, float rotationYaw, float rotationPitch, float scale) {
		float flap = MathHelper.sin(ticksExisted * 0.3F) * 0.8F;
		GlStateManager.pushMatrix();
		GlStateManager.translate(0F, 0F - flap * 0.0625F, 0F);
		body_base.render(scale);
		GlStateManager.popMatrix();
	}

	public void renderTonguePart(float scale) {
		GlStateManager.pushMatrix();
		GlStateManager.rotate(180F, 0F, 1F, 0F);
		tongue_part.render(scale);
		GlStateManager.popMatrix();
	}

	public void renderTongueEnd(float scale) {
		GlStateManager.pushMatrix();
		GlStateManager.rotate(180F, 0F, 1F, 0F);
		tongue_end.render(scale);
		GlStateManager.popMatrix();
	}
	
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entityIn) {
		head1.rotateAngleY = (float)MathHelper.clamp(Math.toRadians(netHeadYaw), -60, 60);
	}
	
	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime ) {
		EntityShambler shambler = (EntityShambler) entity;
		float animation = MathHelper.cos((limbSwing * 1.2F) * 0.75F) * 0.3F * limbSwingAmount * 0.5F;
		float animation2 = MathHelper.sin((limbSwing * 1.2F) * 0.75F) * 0.3F * limbSwingAmount * 0.5F;
		float flap = MathHelper.sin((shambler.ticksExisted + partialTickTime) * 0.3F) * 0.8F;
		float smoothedAngle = shambler.smoothedAngle(partialTickTime);
		float headX = 0F + shambler.rotationPitch / (180F / (float) Math.PI);

		hindleg_left1.rotateAngleX = 0.5235987755982988F - (animation2 * 14F) + flap * 0.1F - flap * 0.075F/ (180F / (float) Math.PI);
		hindleg_right1.rotateAngleX = 0.5235987755982988F - (animation * 14F) + flap * 0.1F - flap * 0.075F/ (180F / (float) Math.PI);

		hindleg_left2.rotateAngleX = 0.4363323129985824F + (animation2 * 8F) - flap * 0.05F + flap * 0.075F/ (180F / (float) Math.PI);
		hindleg_right2.rotateAngleX = 0.4363323129985824F + (animation * 8F) - flap * 0.05F + flap * 0.075F/ (180F / (float) Math.PI);

		hindleg_left3.rotateAngleX = -1.0471975511965976F + (animation2 * 4F) + flap * 0.05F - flap * 0.075F/ (180F / (float) Math.PI);
		hindleg_right3.rotateAngleX = -1.0471975511965976F + (animation * 4F) + flap * 0.05F - flap * 0.075F/ (180F / (float) Math.PI);

		foot_left1.rotateAngleX = 0.4886921905584123F - (animation2 * 2F) - flap * 0.05F - flap * 0.075F/ (180F / (float) Math.PI);
		foot_right1.rotateAngleX = 0.4886921905584123F - (animation * 2F) - flap * 0.05F - flap * 0.075F/ (180F / (float) Math.PI);

		body_base.rotateAngleX = 0F - (animation2 * 3F) - flap * 0.05F;
		head1.rotateAngleX = headX + (animation2 * 4F) + flap * 0.1F;

		body_base.rotateAngleZ = 0F - (animation2 * 2F);
		head1.rotateAngleZ = 0F + (animation2 * 4F);

		hindleg_left1.rotateAngleZ = 0F + (animation2 * 2F);
		hindleg_right1.rotateAngleZ = 0F + (animation * 2F);

	    mouth_arm1a.rotateAngleY = 0F + smoothedAngle / (180F / (float) Math.PI) * 4F - (!shambler.jawsAreOpen() ? 0F : flap * 0.3F);
	    mouth_arm1a.rotateAngleX = 0.08726646259971647F - smoothedAngle / (180F / (float) Math.PI) * 3F - (!shambler.jawsAreOpen() ? 0F : flap * 0.1F);

	    mouth_arm2a.rotateAngleY = 0F - smoothedAngle / (180F / (float) Math.PI) * 4F + (!shambler.jawsAreOpen() ? 0F : flap * 0.3F);
	    mouth_arm2a.rotateAngleX = 0.08726646259971647F - smoothedAngle / (180F / (float) Math.PI) * 3F - (!shambler.jawsAreOpen() ? 0F : flap * 0.1F);

	    mouth_arm3a.rotateAngleY = 0F + smoothedAngle / (180F / (float) Math.PI) * 4F - (!shambler.jawsAreOpen() ? 0F : flap * 0.3F);
	    mouth_arm3a.rotateAngleX = -0.08726646259971647F + smoothedAngle / (180F / (float) Math.PI) * 3F - (!shambler.jawsAreOpen() ? 0F : flap * 0.1F);

	    mouth_arm4a.rotateAngleY = 0F - smoothedAngle / (180F / (float) Math.PI) * 4F + (!shambler.jawsAreOpen() ? 0F : flap * 0.3F);
	    mouth_arm4a.rotateAngleX = -0.08726646259971647F + smoothedAngle / (180F / (float) Math.PI) * 3F - (!shambler.jawsAreOpen() ? 0F : flap * 0.1F);

	    mouth_arm1c.rotateAngleY = - 0.5462880558742251F + smoothedAngle / (180F / (float) Math.PI) * 3F - (!shambler.jawsAreOpen() ? 0F : flap * 0.5F);
	    mouth_arm2c.rotateAngleY = 0.5462880558742251F - smoothedAngle / (180F / (float) Math.PI) * 3F + (!shambler.jawsAreOpen() ? 0F : flap * 0.5F);
	    mouth_arm3c.rotateAngleY = -0.5462880558742251F + smoothedAngle / (180F / (float) Math.PI) * 3F - (!shambler.jawsAreOpen() ? 0F : flap * 0.5F);
	    mouth_arm4c.rotateAngleY = 0.5462880558742251F - smoothedAngle / (180F / (float) Math.PI) * 3F + (!shambler.jawsAreOpen() ? 0F : flap * 0.5F);

		tail2.rotateAngleX = 0.5009094953223726F - MathHelper.sin((animation) * 0.5009094953223726F) * 1F - 1F / 9 * shambler.getTongueLength() * 0.5F;
		tail3.rotateAngleX = 0.7740535232594852F - MathHelper.sin((animation) * 0.7740535232594852F) * 1F - 1F / 9 * shambler.getTongueLength()* 0.5F;
		tail4.rotateAngleX = 0.8651597102135892F - MathHelper.sin((animation) * 0.8651597102135892F) * 1F - 1F / 9 * shambler.getTongueLength();
		tail5.rotateAngleX = 0.8651597102135892F - MathHelper.sin((animation) * 0.8651597102135892F) * 1F - 1F / 9 * shambler.getTongueLength();
		tail6.rotateAngleX = 0.8196066167365371F - MathHelper.sin((animation) *  0.8196066167365371F) * 1F - 1F / 9 * shambler.getTongueLength() * 0.75F;
	}

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
