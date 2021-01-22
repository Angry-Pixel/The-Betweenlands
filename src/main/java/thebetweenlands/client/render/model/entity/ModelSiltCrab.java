package thebetweenlands.client.render.model.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSiltCrab extends ModelBase {
	ModelRenderer body_base;
	ModelRenderer legleft_f1;
	ModelRenderer legleft_m1;
	ModelRenderer legleft_b1;
	ModelRenderer legright_f1;
	ModelRenderer legright_m1;
	ModelRenderer legright_b1;
	ModelRenderer armleft1;
	ModelRenderer armright1;
	ModelRenderer panser1;
	ModelRenderer bodyback;
	ModelRenderer panser2;
	ModelRenderer panser3;
	ModelRenderer legleft_f2;
	ModelRenderer legleft_m2;
	ModelRenderer legleft_b2;
	ModelRenderer legright_f2;
	ModelRenderer legright_m2;
	ModelRenderer legright_b2;
	ModelRenderer clawbase1;
	ModelRenderer clawtop1;
	ModelRenderer snapper1;
	ModelRenderer clawpoint1;
	ModelRenderer clawbase2;
	ModelRenderer clawtop2;
	ModelRenderer snapper2;
	ModelRenderer clawpoint2;

	public ModelSiltCrab() {
		textureWidth = 64;
		textureHeight = 32;
		legleft_b1 = new ModelRenderer(this, 45, 0);
		legleft_b1.setRotationPoint(2.0F, 21.0F, 2.0F);
		legleft_b1.addBox(-0.5F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
		setRotation(legleft_b1, 0.36425021489121656F, -0.40980330836826856F, -0.136659280431156F);
		panser2 = new ModelRenderer(this, 0, 13);
		panser2.setRotationPoint(0.0F, 0.0F, 0.0F);
		panser2.addBox(-3.5F, -2.0F, -2.0F, 7, 2, 2, 0.0F);
		setRotation(panser2, -0.18203784098300857F, 0.0F, 0.0F);
		clawbase2 = new ModelRenderer(this, 41, 22);
		clawbase2.setRotationPoint(0.0F, 3.0F, 0.0F);
		clawbase2.addBox(-1.0F, -0.5F, -1.0F, 2, 1, 2, 0.0F);
		setRotation(clawbase2, -0.31869712141416456F, 0.0F, -0.40980330836826856F);
		clawpoint1 = new ModelRenderer(this, 29, 22);
		clawpoint1.setRotationPoint(0.0F, 3.0F, 0.0F);
		clawpoint1.addBox(-1.0F, 0.0F, -1.0F, 1, 1, 2, 0.0F);
		setRotation(clawpoint1, 0.0F, 0.0F, 0.27314402793711257F);
		snapper1 = new ModelRenderer(this, 29, 26);
		snapper1.setRotationPoint(-1.0F, 0.5F, 0.0F);
		snapper1.addBox(0.0F, 0.0F, -1.0F, 1, 3, 2, 0.0F);
		setRotation(snapper1, 0.0F, 0.0F, -0.136659280431156F);
		clawpoint2 = new ModelRenderer(this, 50, 22);
		clawpoint2.setRotationPoint(0.0F, 3.0F, 0.0F);
		clawpoint2.addBox(0.0F, 0.0F, -1.0F, 1, 1, 2, 0.0F);
		setRotation(clawpoint2, 0.0F, 0.0F, -0.27314402793711257F);
		legright_f2 = new ModelRenderer(this, 23, 14);
		legright_f2.setRotationPoint(-3.5F, 0.0F, 0.0F);
		legright_f2.addBox(-1.0F, -0.5F, -1.0F, 2, 5, 2, 0.0F);
		setRotation(legright_f2, 0.0F, 0.0F, 0.091106186954104F);
		legright_m1 = new ModelRenderer(this, 34, 11);
		legright_m1.setRotationPoint(-2.0F, 21.0F, 1.0F);
		legright_m1.addBox(-3.5F, -0.5F, -0.5F, 4, 1, 1, 0.0F);
		setRotation(legright_m1, 0.0F, 0.136659280431156F, 0.18203784098300857F);
		bodyback = new ModelRenderer(this, 0, 23);
		bodyback.setRotationPoint(0.0F, 2.0F, 3.0F);
		bodyback.addBox(-2.5F, -2.0F, 0.0F, 5, 2, 2, 0.0F);
		legright_b1 = new ModelRenderer(this, 45, 11);
		legright_b1.setRotationPoint(-2.0F, 21.0F, 2.0F);
		legright_b1.addBox(-2.5F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
		setRotation(legright_b1, 0.36425021489121656F, 0.40980330836826856F, 0.136659280431156F);
		legleft_b2 = new ModelRenderer(this, 45, 3);
		legleft_b2.setRotationPoint(2.5F, 0.0F, 0.0F);
		legleft_b2.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
		setRotation(legleft_b2, 0.0F, 0.0F, -0.31869712141416456F);
		clawtop1 = new ModelRenderer(this, 20, 26);
		clawtop1.setRotationPoint(1.0F, 0.5F, 0.0F);
		clawtop1.addBox(-1.0F, 0.0F, -1.5F, 1, 3, 3, 0.0F);
		setRotation(clawtop1, 0.0F, 0.0F, 0.045553093477052F);
		legleft_f2 = new ModelRenderer(this, 23, 3);
		legleft_f2.setRotationPoint(3.5F, 0.0F, 0.0F);
		legleft_f2.addBox(-1.0F, -0.5F, -1.0F, 2, 5, 2, 0.0F);
		setRotation(legleft_f2, 0.0F, 0.0F, -0.091106186954104F);
		legright_m2 = new ModelRenderer(this, 34, 14);
		legright_m2.setRotationPoint(-3.5F, 0.0F, 0.0F);
		legright_m2.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
		setRotation(legright_m2, 0.0F, 0.0F, 0.31869712141416456F);
		legright_b2 = new ModelRenderer(this, 45, 14);
		legright_b2.setRotationPoint(-2.5F, 0.0F, 0.0F);
		legright_b2.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
		setRotation(legright_b2, 0.0F, 0.0F, 0.31869712141416456F);
		clawtop2 = new ModelRenderer(this, 41, 26);
		clawtop2.setRotationPoint(-1.0F, 0.5F, 0.0F);
		clawtop2.addBox(0.0F, 0.0F, -1.5F, 1, 3, 3, 0.0F);
		setRotation(clawtop2, 0.0F, 0.0F, -0.045553093477052F);
		armleft1 = new ModelRenderer(this, 15, 22);
		armleft1.setRotationPoint(2.0F, 20.7F, -0.5F);
		armleft1.addBox(-0.5F, -0.2F, -0.5F, 1, 3, 1, 0.0F);
		setRotation(armleft1, -1.5025539530419183F, -0.4553564018453205F, -0.4553564018453205F);
		legleft_f1 = new ModelRenderer(this, 23, 0);
		legleft_f1.setRotationPoint(2.0F, 21.0F, 0.0F);
		legleft_f1.addBox(-0.5F, -0.5F, -0.5F, 4, 1, 1, 0.0F);
		setRotation(legleft_f1, -0.36425021489121656F, 0.18203784098300857F, -0.31869712141416456F);
		armright1 = new ModelRenderer(this, 36, 22);
		armright1.setRotationPoint(-2.0F, 20.7F, -0.5F);
		armright1.addBox(-0.5F, -0.2F, -0.5F, 1, 3, 1, 0.0F);
		setRotation(armright1, -1.5025539530419183F, 0.4553564018453205F, 0.4553564018453205F);
		panser1 = new ModelRenderer(this, 0, 6);
		panser1.setRotationPoint(0.0F, 0.5F, 1.0F);
		panser1.addBox(-3.5F, -2.0F, 0.0F, 7, 2, 4, 0.0F);
		setRotation(panser1, -0.136659280431156F, 0.0F, 0.0F);
		legleft_m2 = new ModelRenderer(this, 34, 3);
		legleft_m2.setRotationPoint(3.5F, 0.0F, 0.0F);
		legleft_m2.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
		setRotation(legleft_m2, 0.0F, 0.0F, -0.31869712141416456F);
		clawbase1 = new ModelRenderer(this, 20, 22);
		clawbase1.setRotationPoint(0.0F, 3.0F, 0.0F);
		clawbase1.addBox(-1.0F, -0.5F, -1.0F, 2, 1, 2, 0.0F);
		setRotation(clawbase1, -0.31869712141416456F, 0.0F, 0.40980330836826856F);
		panser3 = new ModelRenderer(this, 0, 18);
		panser3.setRotationPoint(0.0F, -2.0F, 4.0F);
		panser3.addBox(-2.5F, 0.0F, 0.0F, 5, 3, 1, 0.0F);
		setRotation(panser3, -0.27314402793711257F, 0.0F, 0.0F);
		legleft_m1 = new ModelRenderer(this, 34, 0);
		legleft_m1.setRotationPoint(2.0F, 21.0F, 1.0F);
		legleft_m1.addBox(-0.5F, -0.5F, -0.5F, 4, 1, 1, 0.0F);
		setRotation(legleft_m1, 0.0F, -0.136659280431156F, -0.18203784098300857F);
		snapper2 = new ModelRenderer(this, 50, 26);
		snapper2.setRotationPoint(1.0F, 0.5F, 0.0F);
		snapper2.addBox(-1.0F, 0.0F, -1.0F, 1, 3, 2, 0.0F);
		setRotation(snapper2, 0.0F, 0.0F, 0.136659280431156F);
		legright_f1 = new ModelRenderer(this, 23, 11);
		legright_f1.setRotationPoint(-2.0F, 21.0F, 0.0F);
		legright_f1.addBox(-3.5F, -0.5F, -0.5F, 4, 1, 1, 0.0F);
		setRotation(legright_f1, -0.36425021489121656F, -0.18203784098300857F, 0.31869712141416456F);
		body_base = new ModelRenderer(this, 0, 0);
		body_base.setRotationPoint(0.0F, 18.5F, -1.0F);
		body_base.addBox(-3.0F, 0.0F, 0.0F, 6, 2, 3, 0.0F);
		setRotation(body_base, -0.22759093446006054F, 0.0F, 0.0F);
		panser1.addChild(panser2);
		armright1.addChild(clawbase2);
		clawtop1.addChild(clawpoint1);
		clawbase1.addChild(snapper1);
		clawtop2.addChild(clawpoint2);
		legright_f1.addChild(legright_f2);
		body_base.addChild(bodyback);
		legleft_b1.addChild(legleft_b2);
		clawbase1.addChild(clawtop1);
		legleft_f1.addChild(legleft_f2);
		legright_m1.addChild(legright_m2);
		legright_b1.addChild(legright_b2);
		clawbase2.addChild(clawtop2);
		body_base.addChild(panser1);
		legleft_m1.addChild(legleft_m2);
		armleft1.addChild(clawbase1);
		panser1.addChild(panser3);
		clawbase2.addChild(snapper2);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
		super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
		setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		GlStateManager.pushMatrix();
		GlStateManager.rotate(90.0F, 0.0F, -1.0F, 0.0F);
		legleft_b1.render(unitPixel);
		legright_m1.render(unitPixel);
		legright_b1.render(unitPixel);
		armleft1.render(unitPixel);
		legleft_f1.render(unitPixel);
		armright1.render(unitPixel);
		legleft_m1.render(unitPixel);
		legright_f1.render(unitPixel);
		body_base.render(unitPixel);
		GlStateManager.popMatrix();
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);

		float movement = MathHelper.cos(limbSwing * 1.5F + (float) Math.PI) * 1.5F * limbSwingAngle *0.5F;
		armright1.rotateAngleX = -movement * 0.2F -1.5025539530419183F;
		armleft1.rotateAngleX = movement * 0.2F -1.5025539530419183F;
		legright_f1.rotateAngleZ = movement;
		legright_m1.rotateAngleZ = -movement;
		legright_b1.rotateAngleZ = movement;
		legleft_f1.rotateAngleZ = movement;
		legleft_m1.rotateAngleZ = -movement;
		legleft_b1.rotateAngleZ = movement;


	}
}
