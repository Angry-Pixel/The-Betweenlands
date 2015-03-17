package thebetweenlands.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelSwampHag extends ModelBase
{
	ModelRenderer Hips;
	ModelRenderer Ribs;
	ModelRenderer Shoulders;
	ModelRenderer Head;
	ModelRenderer Head2;
	ModelRenderer RightArm;
	ModelRenderer LeftArm;
	ModelRenderer HeadOver;
	ModelRenderer Spine;
	ModelRenderer LeftLeg;
	ModelRenderer RightLeg;

	public ModelSwampHag()
	{
		textureWidth = 100;
		textureHeight = 70;

		Hips = new ModelRenderer(this, 24, 27);
		Hips.addBox(0F, -14F, 0F, 9, 4, 4);
		Hips.setRotationPoint(-4.5F, 5F, -4F);
		Hips.setTextureSize(100, 70);
		Hips.mirror = true;
		setRotation(Hips, 0F, 0F, 0F);

		Ribs = new ModelRenderer(this, 24, 8);
		Ribs.addBox(0F, 0F, 0F, 8, 8, 3);
		Ribs.setRotationPoint(-4F, -5F, -4F);
		Ribs.setTextureSize(100, 70);
		Ribs.mirror = true;
		setRotation(Ribs, 0F, 0F, 0F);

		Shoulders = new ModelRenderer(this, 24, 0);
		Shoulders.addBox(0F, 0F, 0F, 9, 4, 4);
		Shoulders.setRotationPoint(-4.5F, 3F, -4F);
		Shoulders.setTextureSize(100, 70);
		Shoulders.mirror = true;
		setRotation(Shoulders, 0F, 0F, 0F);

		Head = new ModelRenderer(this, 0, 26);
		Head.addBox(-3F, -6F, -3F, 6, 6, 6);
		Head.setRotationPoint(0F, -7F, -5F);
		Head.setTextureSize(100, 70);
		Head.mirror = true;
		setRotation(Head, 0F, 0F, 0F);

		Head2 = new ModelRenderer(this, 0, 54);
		Head2.addBox(-3.5F, 0F, 0F, 7, 13, 3);
		Head2.setRotationPoint(0F, -6F, -8F);
		Head2.setTextureSize(100, 70);
		Head2.mirror = true;
		setRotation(Head2, 0F, 0F, 0F);

		RightArm = new ModelRenderer(this, 62, 0);
		RightArm.addBox(0F, 0F, 0F, 3, 20, 3);
		RightArm.setRotationPoint(-7.5F, -8.5F, -3.5F);
		RightArm.setTextureSize(100, 70);
		RightArm.mirror = true;
		setRotation(RightArm, 0F, 0F, 0F);

		LeftArm = new ModelRenderer(this, 50, 0);
		LeftArm.addBox(0F, 0F, 0F, 3, 20, 3);
		LeftArm.setRotationPoint(4.5F, -8.5F, -3.5F);
		LeftArm.setTextureSize(100, 70);
		LeftArm.mirror = true;
		setRotation(LeftArm, 0F, 0F, 0F);

		HeadOver = new ModelRenderer(this, 0, 38);
		HeadOver.addBox(-4F, -8F, -4F, 8, 8, 8);
		HeadOver.setRotationPoint(0F, -6F, -5F);
		HeadOver.setTextureSize(100, 70);
		HeadOver.mirror = true;
		setRotation(HeadOver, 0F, 0F, 0F);

		Spine = new ModelRenderer(this, 32, 45);
		Spine.addBox(0F, 0F, 0F, 2, 8, 1);
		Spine.setRotationPoint(-1F, -5F, -1.5F);
		Spine.setTextureSize(100, 70);
		Spine.mirror = true;
		setRotation(Spine, 0F, 0F, 0F);

		LeftLeg = new ModelRenderer(this, 0, 0);
		LeftLeg.addBox(0F, 0F, 0F, 2, 17, 2);
		LeftLeg.setRotationPoint(1.5F, 7F, -3F);
		LeftLeg.setTextureSize(100, 70);
		LeftLeg.mirror = true;
		setRotation(LeftLeg, 0F, 0F, 0F);

		RightLeg = new ModelRenderer(this, 0, 0);
		RightLeg.addBox(0F, 0F, 0F, 2, 17, 2);
		RightLeg.setRotationPoint(-3.5F, 7F, -3F);
		RightLeg.setTextureSize(100, 70);
		RightLeg.mirror = true;
		setRotation(RightLeg, 0F, 0F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		Hips.render(f5);
		Ribs.render(f5);
		Shoulders.render(f5);
		Head.render(f5);
		Head2.render(f5);
		RightArm.render(f5);
		LeftArm.render(f5);
		HeadOver.render(f5);
		Spine.render(f5);
		LeftLeg.render(f5);
		RightLeg.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
	{
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		Head.rotateAngleY = f3 / (180F / (float)Math.PI);
		Head.rotateAngleX = f4 / (180F / (float)Math.PI);
		HeadOver.rotateAngleY = f3 / (180F / (float)Math.PI);
		HeadOver.rotateAngleX = f4 / (180F / (float)Math.PI);
		RightArm.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 2.0F * f1 * 0.5F;
		LeftArm.rotateAngleX = MathHelper.cos(f * 0.6662F) * 2.0F * f1 * 0.5F;
		RightArm.rotateAngleZ = 0.0F;
		LeftArm.rotateAngleZ = 0.0F;
		RightLeg.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
		LeftLeg.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 1.4F * f1;
		RightLeg.rotateAngleY = 0.0F;
		LeftLeg.rotateAngleY = 0.0F;
	}
}
