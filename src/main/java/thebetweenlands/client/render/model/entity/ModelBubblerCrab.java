package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBubblerCrab extends ModelBase {
	    ModelRenderer body_base;
	    ModelRenderer body_main;
	    ModelRenderer washy_mouth_left;
	    ModelRenderer washy_mouth_right;
	    ModelRenderer eyestalk_left;
	    ModelRenderer eyestalk_right;
	    ModelRenderer leg_left_front1a;
	    ModelRenderer leg_left_mid1a;
	    ModelRenderer leg_left_back1a;
	    ModelRenderer arm_left1a;
	    ModelRenderer leg_right_front1a;
	    ModelRenderer leg_right_mid1a;
	    ModelRenderer leg_right_back1a;
	    ModelRenderer arm_right1a;
	    ModelRenderer horn_left1a;
	    ModelRenderer horn_left1a_1;
	    ModelRenderer leg_left_front1b;
	    ModelRenderer leg_left_mid1b;
	    ModelRenderer leg_left_back1b;
	    ModelRenderer arm_left1b;
	    ModelRenderer claw_left_top1a;
	    ModelRenderer claw_left_bottom1a;
	    ModelRenderer claw_left_bottom1b;
	    ModelRenderer leg_right_front1b;
	    ModelRenderer leg_right_mid1b;
	    ModelRenderer leg_right_back1b;
	    ModelRenderer arm_right1b;
	    ModelRenderer claw_right_top1a;
	    ModelRenderer claw_right_bottom1a;
	    ModelRenderer claw_right_bottom1b;
	    ModelRenderer horn_left1b;
	    ModelRenderer horn_left1b_1;

	    public ModelBubblerCrab() {
	        textureWidth = 32;
	        textureHeight = 32;
	        eyestalk_right = new ModelRenderer(this, 5, 17);
	        eyestalk_right.setRotationPoint(-0.5F, 1.0F, -3.0F);
	        eyestalk_right.addBox(-1.0F, -2.0F, 0.0F, 1, 2, 1, 0.0F);
	        setRotateAngle(eyestalk_right, 0.31869712141416456F, 0.0F, -0.22759093446006054F);
	        leg_right_mid1a = new ModelRenderer(this, 20, 10);
	        leg_right_mid1a.setRotationPoint(-1.5F, 3.0F, -1.0F);
	        leg_right_mid1a.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
	        setRotateAngle(leg_right_mid1a, 0.0F, 0.0F, 1.4570008595648662F);
	        eyestalk_left = new ModelRenderer(this, 0, 17);
	        eyestalk_left.setRotationPoint(0.5F, 1.0F, -3.0F);
	        eyestalk_left.addBox(0.0F, -2.0F, 0.0F, 1, 2, 1, 0.0F);
	        setRotateAngle(eyestalk_left, 0.31869712141416456F, 0.0F, 0.22759093446006054F);
	        body_main = new ModelRenderer(this, 0, 6);
	        body_main.setRotationPoint(0.0F, -3.0F, 0.0F);
	        body_main.addBox(-2.0F, 0.0F, -3.0F, 4, 3, 3, 0.0F);
	        setRotateAngle(body_main, 0.31869712141416456F, 0.0F, 0.0F);
	        arm_right1a = new ModelRenderer(this, 25, 14);
	        arm_right1a.setRotationPoint(-1.5F, 3.0F, -2.0F);
	        arm_right1a.addBox(-0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F);
	        setRotateAngle(arm_right1a, -0.9105382707654417F, 0.6373942428283291F, 0.0F);
	        claw_right_top1a = new ModelRenderer(this, 24, 29);
	        claw_right_top1a.setRotationPoint(0.5F, 0.5F, 0.0F);
	        claw_right_top1a.addBox(-1.0F, 0.01F, -0.5F, 3, 2, 1, 0.0F);
	        setRotateAngle(claw_right_top1a, 0.0F, 0.0F, 0.36425021489121656F);
	        horn_left1a_1 = new ModelRenderer(this, 7, 21);
	        horn_left1a_1.setRotationPoint(-2.0F, 0.0F, -3.0F);
	        horn_left1a_1.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
	        setRotateAngle(horn_left1a_1, 0.0F, 0.0F, -0.18203784098300857F);
	        claw_left_bottom1b = new ModelRenderer(this, 25, 10);
	        claw_left_bottom1b.setRotationPoint(-1.0F, 0.5F, 0.0F);
	        claw_left_bottom1b.addBox(-2.0F, -1.0F, 0.0F, 2, 2, 1, 0.0F);
	        setRotateAngle(claw_left_bottom1b, 0.0F, 0.6829473363053812F, 0.0F);
	        leg_right_back1b = new ModelRenderer(this, 20, 23);
	        leg_right_back1b.setRotationPoint(-0.5F, 3.0F, 0.0F);
	        leg_right_back1b.addBox(0.0F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
	        setRotateAngle(leg_right_back1b, 0.0F, 0.0F, -1.1383037381507017F);
	        leg_left_back1a = new ModelRenderer(this, 15, 19);
	        leg_left_back1a.setRotationPoint(1.5F, 3.0F, 0.0F);
	        leg_left_back1a.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
	        setRotateAngle(leg_left_back1a, 0.5009094953223726F, 0.5462880558742251F, -1.0016444577195458F);
	        arm_right1b = new ModelRenderer(this, 25, 17);
	        arm_right1b.setRotationPoint(0.0F, 1.0F, -0.5F);
	        arm_right1b.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
	        setRotateAngle(arm_right1b, 0.22759093446006054F, 0.0F, 0.0F);
	        washy_mouth_right = new ModelRenderer(this, 7, 13);
	        washy_mouth_right.setRotationPoint(-2.0F, 1.0F, -3.0F);
	        washy_mouth_right.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
	        setRotateAngle(washy_mouth_right, 0.0F, 0.36425021489121656F, 0.091106186954104F);
	        leg_left_back1b = new ModelRenderer(this, 15, 23);
	        leg_left_back1b.setRotationPoint(0.5F, 3.0F, 0.0F);
	        leg_left_back1b.addBox(-1.0F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
	        setRotateAngle(leg_left_back1b, 0.0F, 0.0F, 1.1383037381507017F);
	        horn_left1b = new ModelRenderer(this, 0, 25);
	        horn_left1b.setRotationPoint(1.0F, 1.0F, 0.0F);
	        horn_left1b.addBox(0.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F);
	        setRotateAngle(horn_left1b, 0.0F, -0.31869712141416456F, 0.0F);
	        arm_left1b = new ModelRenderer(this, 25, 3);
	        arm_left1b.setRotationPoint(0.0F, 1.0F, -0.5F);
	        arm_left1b.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
	        setRotateAngle(arm_left1b, 0.22759093446006054F, 0.0F, 0.0F);
	        claw_right_bottom1a = new ModelRenderer(this, 25, 20);
	        claw_right_bottom1a.setRotationPoint(-1.0F, 0.5F, 0.5F);
	        claw_right_bottom1a.addBox(0.0F, -0.5F, 0.0F, 1, 2, 1, 0.0F);
	        setRotateAngle(claw_right_bottom1a, 0.0F, 0.36425021489121656F, 0.0F);
	        leg_left_mid1b = new ModelRenderer(this, 15, 14);
	        leg_left_mid1b.setRotationPoint(0.5F, 3.0F, 0.0F);
	        leg_left_mid1b.addBox(-1.0F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
	        setRotateAngle(leg_left_mid1b, 0.0F, 0.0F, 1.1383037381507017F);
	        washy_mouth_left = new ModelRenderer(this, 0, 13);
	        washy_mouth_left.setRotationPoint(2.0F, 1.0F, -3.0F);
	        washy_mouth_left.addBox(-2.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
	        setRotateAngle(washy_mouth_left, 0.0F, -0.36425021489121656F, -0.091106186954104F);
	        leg_left_mid1a = new ModelRenderer(this, 15, 10);
	        leg_left_mid1a.setRotationPoint(1.5F, 3.0F, -1.0F);
	        leg_left_mid1a.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
	        setRotateAngle(leg_left_mid1a, 0.0F, 0.0F, -1.4570008595648662F);
	        claw_left_bottom1a = new ModelRenderer(this, 25, 6);
	        claw_left_bottom1a.setRotationPoint(1.0F, 0.5F, 0.5F);
	        claw_left_bottom1a.addBox(-1.0F, -0.5F, 0.0F, 1, 2, 1, 0.0F);
	        setRotateAngle(claw_left_bottom1a, 0.0F, -0.36425021489121656F, 0.0F);
	        body_base = new ModelRenderer(this, 0, 0);
	        body_base.setRotationPoint(0.0F, 20.0F, 0.0F);
	        body_base.addBox(-2.0F, -3.0F, 0.0F, 4, 3, 2, 0.0F);
	        setRotateAngle(body_base, -0.40980330836826856F, 0.0F, 0.0F);
	        horn_left1a = new ModelRenderer(this, 0, 21);
	        horn_left1a.setRotationPoint(2.0F, 0.0F, -3.0F);
	        horn_left1a.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
	        setRotateAngle(horn_left1a, 0.0F, 0.0F, 0.18203784098300857F);
	        claw_right_bottom1b = new ModelRenderer(this, 25, 24);
	        claw_right_bottom1b.setRotationPoint(1.0F, 0.5F, 0.0F);
	        claw_right_bottom1b.addBox(0.0F, -1.0F, 0.0F, 2, 2, 1, 0.0F);
	        setRotateAngle(claw_right_bottom1b, 0.0F, -0.6829473363053812F, 0.0F);
	        horn_left1b_1 = new ModelRenderer(this, 5, 25);
	        horn_left1b_1.setRotationPoint(-1.0F, 1.0F, 0.0F);
	        horn_left1b_1.addBox(-1.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F);
	        setRotateAngle(horn_left1b_1, 0.0F, 0.31869712141416456F, 0.0F);
	        leg_right_mid1b = new ModelRenderer(this, 20, 14);
	        leg_right_mid1b.setRotationPoint(-0.5F, 3.0F, 0.0F);
	        leg_right_mid1b.addBox(0.0F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
	        setRotateAngle(leg_right_mid1b, 0.0F, 0.0F, -1.1383037381507017F);
	        leg_left_front1a = new ModelRenderer(this, 15, 0);
	        leg_left_front1a.setRotationPoint(1.5F, 3.0F, -1.5F);
	        leg_left_front1a.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
	        setRotateAngle(leg_left_front1a, -0.40980330836826856F, -0.045553093477052F, -1.6845917940249266F);
	        claw_left_top1a = new ModelRenderer(this, 15, 29);
	        claw_left_top1a.setRotationPoint(-0.5F, 0.5F, 0.0F);
	        claw_left_top1a.addBox(-2.0F, 0.01F, -0.5F, 3, 2, 1, 0.0F);
	        setRotateAngle(claw_left_top1a, 0.0F, 0.0F, -0.36425021489121656F);
	        leg_right_back1a = new ModelRenderer(this, 20, 19);
	        leg_right_back1a.setRotationPoint(-1.5F, 3.0F, 0.0F);
	        leg_right_back1a.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
	        setRotateAngle(leg_right_back1a, 0.5009094953223726F, -0.5462880558742251F, 1.0016444577195458F);
	        leg_right_front1b = new ModelRenderer(this, 20, 4);
	        leg_right_front1b.setRotationPoint(-0.5F, 3.0F, 0.0F);
	        leg_right_front1b.addBox(0.0F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
	        setRotateAngle(leg_right_front1b, 0.0F, 0.0F, -1.3203415791337103F);
	        arm_left1a = new ModelRenderer(this, 25, 0);
	        arm_left1a.setRotationPoint(1.5F, 3.0F, -2.0F);
	        arm_left1a.addBox(-0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F);
	        setRotateAngle(arm_left1a, -0.9105382707654417F, -0.6373942428283291F, 0.0F);
	        leg_left_front1b = new ModelRenderer(this, 15, 4);
	        leg_left_front1b.setRotationPoint(0.5F, 3.0F, 0.0F);
	        leg_left_front1b.addBox(-1.0F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
	        setRotateAngle(leg_left_front1b, 0.0F, 0.0F, 1.3203415791337103F);
	        leg_right_front1a = new ModelRenderer(this, 20, 0);
	        leg_right_front1a.setRotationPoint(-1.5F, 3.0F, -1.5F);
	        leg_right_front1a.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
	        setRotateAngle(leg_right_front1a, -0.40980330836826856F, 0.045553093477052F, 1.6845917940249266F);
	        body_main.addChild(eyestalk_right);
	        body_main.addChild(leg_right_mid1a);
	        body_main.addChild(eyestalk_left);
	        body_base.addChild(body_main);
	        body_main.addChild(arm_right1a);
	        arm_right1b.addChild(claw_right_top1a);
	        body_main.addChild(horn_left1a_1);
	        claw_left_bottom1a.addChild(claw_left_bottom1b);
	        leg_right_back1a.addChild(leg_right_back1b);
	        body_main.addChild(leg_left_back1a);
	        arm_right1a.addChild(arm_right1b);
	        body_main.addChild(washy_mouth_right);
	        leg_left_back1a.addChild(leg_left_back1b);
	        horn_left1a.addChild(horn_left1b);
	        arm_left1a.addChild(arm_left1b);
	        claw_right_top1a.addChild(claw_right_bottom1a);
	        leg_left_mid1a.addChild(leg_left_mid1b);
	        body_main.addChild(washy_mouth_left);
	        body_main.addChild(leg_left_mid1a);
	        claw_left_top1a.addChild(claw_left_bottom1a);
	        body_main.addChild(horn_left1a);
	        claw_right_bottom1a.addChild(claw_right_bottom1b);
	        horn_left1a_1.addChild(horn_left1b_1);
	        leg_right_mid1a.addChild(leg_right_mid1b);
	        body_main.addChild(leg_left_front1a);
	        arm_left1b.addChild(claw_left_top1a);
	        body_main.addChild(leg_right_back1a);
	        leg_right_front1a.addChild(leg_right_front1b);
	        body_main.addChild(arm_left1a);
	        leg_left_front1a.addChild(leg_left_front1b);
	        body_main.addChild(leg_right_front1a);
	    }

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale) {
		super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, scale);
		setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, scale, entity);
		GlStateManager.pushMatrix();
		GlStateManager.rotate(90.0F, 0.0F, -1.0F, 0.0F);
        body_base.render(scale);
        GlStateManager.popMatrix();
    }

	public void renderEating(float animationTick, float scale) {
		setRotationAnglesEating(animationTick);
		GlStateManager.pushMatrix();
		GlStateManager.rotate(90.0F, 0.0F, -1.0F, 0.0F);
		body_base.render(scale);
		GlStateManager.popMatrix();
	}

	private void setRotationAnglesEating(float animationTick) {
		float flap2 = MathHelper.sin((animationTick) * 0.15F) * 0.6F;
		float flap = MathHelper.cos((animationTick) * 0.15F) * 0.6F;
		arm_right1a.rotateAngleX = flap * 0.5F - 0.9105382707654417F;
		arm_left1a.rotateAngleX = flap2 * 0.5F - 0.9105382707654417F;
		claw_right_bottom1a.rotateAngleY = -flap * 0.5F;
		claw_left_bottom1a.rotateAngleY = - flap2 * 0.5F;
		claw_right_top1a.rotateAngleY = 0.36425021489121656F * 0.5F  + flap * 0.5F;
		claw_left_top1a.rotateAngleY = - 0.36425021489121656F * 0.5F  - flap2 * 0.5F;

	}

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		float movement = MathHelper.cos(limbSwing * 1.5F + (float) Math.PI) * 1.5F * limbSwingAngle *0.5F;
		arm_right1a.rotateAngleX = -movement * 0.2F -0.9105382707654417F;
		arm_left1a.rotateAngleX = movement * 0.2F -0.9105382707654417F;
		leg_right_front1a.rotateAngleZ = movement + 1.6845917940249266F;
		leg_right_mid1a.rotateAngleZ = -movement + 1.4570008595648662F;
		leg_right_back1a.rotateAngleZ = movement + 1.0016444577195458F;
		leg_left_front1a.rotateAngleZ = movement -1.6845917940249266F;
		leg_left_mid1a.rotateAngleZ = -movement -1.4570008595648662F;
		leg_left_back1a.rotateAngleZ = movement -1.0016444577195458F;
		claw_right_bottom1a.rotateAngleY = 0.36425021489121656F;;
		claw_left_bottom1a.rotateAngleY = - 0.36425021489121656F;
		claw_right_top1a.rotateAngleY = 0.36425021489121656F;
		claw_left_top1a.rotateAngleY = - 0.36425021489121656F;
	}
}
