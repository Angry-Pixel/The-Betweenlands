package thebetweenlands.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelAngler extends ModelBase
{
	ModelRenderer body;
	ModelRenderer teeth;
	ModelRenderer jaw;
	ModelRenderer fin1;
	ModelRenderer fin2;
	ModelRenderer fin3;
	ModelRenderer fin4;
	ModelRenderer antenna1;
	ModelRenderer antenna2;

	public ModelAngler()
	{
		textureWidth = 64;
		textureHeight = 32;

		body = new ModelRenderer(this, 0, 0);
		body.addBox(-2F, 0F, -10F, 4, 5, 10);
		body.setRotationPoint(0F, 12F, 6F);
		body.setTextureSize(64, 32);
		body.mirror = true;
		setRotation(body, 0F, 0F, 0F);

		teeth = new ModelRenderer(this, 0, 16);
		teeth.addBox(-3F, 0F, -10F, 6, 2, 10);
		teeth.setRotationPoint(0F, 13.5F, 4F);
		teeth.setTextureSize(64, 32);
		teeth.mirror = true;
		setRotation(teeth, -50.3490659F, 0F, 0F);

		jaw = new ModelRenderer(this, 28, 0);
		jaw.addBox(0F, 0F, -9.4F, 6, 2, 10);
		jaw.setRotationPoint(-3F, 15.5F, 4F);
		jaw.setTextureSize(64, 32);
		jaw.mirror = true;
		setRotation(jaw, -50.3490659F, 0F, 0F);

		fin1 = new ModelRenderer(this, 0, 0);
		fin1.addBox(0F, 0F, 0F, 1, 2, 3);
		fin1.setRotationPoint(-0.5F, 12.5F, 5F);
		fin1.setTextureSize(64, 32);
		fin1.mirror = true;
		setRotation(fin1, 0.4363323F, 0F, 0F);

		fin2 = new ModelRenderer(this, 0, 0);
		fin2.addBox(0F, 0F, 0F, 1, 2, 3);
		fin2.setRotationPoint(-0.5F, 13.8F, 5.8F);
		fin2.setTextureSize(64, 32);
		fin2.mirror = true;
		setRotation(fin2, -0.4363323F, 0F, 0F);

		fin3 = new ModelRenderer(this, 0, 0);
		fin3.addBox(0F, 0F, 0F, 1, 2, 4);
		fin3.setRotationPoint(-2F, 13.8F, 5F);
		fin3.setTextureSize(64, 32);
		fin3.mirror = true;
		setRotation(fin3, -0.4363323F, -1.396263F, 0F);

		fin4 = new ModelRenderer(this, 0, 0);
		fin4.addBox(0F, 0F, 0F, 1, 2, 4);
		fin4.setRotationPoint(2F, 13.8F, 5F);
		fin4.setTextureSize(64, 32);
		fin4.mirror = true;
		setRotation(fin4, -0.4363323F, 1.396263F, 0F);

		antenna1 = new ModelRenderer(this, 50, 0);
		antenna1.addBox(0F, -4F, 0F, 1, 4, 1);
		antenna1.setRotationPoint(-0.5F, 12F, -4F);
		antenna1.setTextureSize(64, 32);
		antenna1.mirror = true;
		setRotation(antenna1, 0F, 0F, 0F);

		antenna2 = new ModelRenderer(this, 50, 5);
		antenna2.addBox(0F, 0F, -4F, 1, 1, 4);
		antenna2.setRotationPoint(-0.5F, 8F, -4F);
		antenna2.setTextureSize(64, 32);
		antenna2.mirror = true;
		setRotation(antenna2, 0F, 0F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5);
		body.render(f5);
		teeth.render(f5);
		jaw.render(f5);
		fin1.render(f5);
		fin2.render(f5);
		fin3.render(f5);
		fin4.render(f5);
		antenna1.render(f5);
		antenna2.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.jaw.rotateAngleX = (float) (MathHelper.cos(f * 0.3000000F * 5) *  1.2F * f1);
		this.teeth.rotateAngleX = (float) (MathHelper.cos(f * 0.3000000F * 5) * 1.2F * f1);
		/*
    this.fin1.rotateAngleY = (f4 / (180F / (float)Math.PI)) + 0.443F;
    this.fin2.rotateAngleY = (f4 / (180F / (float)Math.PI)) + 0.443F;
    this.fin3.rotateAngleY = (f4 / (180F / (float)Math.PI)) + 0.443F;
    this.fin4.rotateAngleY = (f4 / (180F / (float)Math.PI)) + 0.443F;
		 */
	}
}
