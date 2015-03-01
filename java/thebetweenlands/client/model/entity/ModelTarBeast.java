package thebetweenlands.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelTarBeast extends ModelBase
{
    ModelRenderer Leg1;
    ModelRenderer Leg2;
    ModelRenderer Body;
    ModelRenderer Head;
    ModelRenderer Jaw;
    ModelRenderer Arm1;
    ModelRenderer Arm2;

  public ModelTarBeast()
  {
    textureWidth = 100;
    textureHeight = 100;

      Leg1 = new ModelRenderer(this, 0, 32);
      Leg1.addBox(0F, 0F, 0F, 5, 17, 5);
      Leg1.setRotationPoint(-5F, 7F, -2F);
      Leg1.setTextureSize(100, 100);
      Leg1.mirror = true;
      setRotation(Leg1, 0F, 0F, 0F);

      Leg2 = new ModelRenderer(this, 20, 32);
      Leg2.addBox(0F, 0F, 0F, 5, 17, 5);
      Leg2.setRotationPoint(0F, 7F, -2F);
      Leg2.setTextureSize(100, 100);
      Leg2.mirror = true;
      setRotation(Leg2, 0F, 0F, 0F);

      Body = new ModelRenderer(this, 0, 0);
      Body.addBox(0F, 0F, 0F, 12, 23, 9);
      Body.setRotationPoint(-6F, -16F, -4F);
      Body.setTextureSize(100, 100);
      Body.mirror = true;
      setRotation(Body, 0F, 0F, 0F);

      Head = new ModelRenderer(this, 42, 0);
      Head.addBox(-4F, 0F, -8F, 8, 8, 8);
      Head.setRotationPoint(0F, -13F, -1F);
      Head.setTextureSize(100, 100);
      Head.mirror = true;
      setRotation(Head, 0F, 0F, 0F);

      Jaw = new ModelRenderer(this, 42, 16);
      Jaw.addBox(-3F, 8F, -8F, 6, 3, 2);
      Jaw.setRotationPoint(0F, -13F, -1F);
      Jaw.setTextureSize(100, 100);
      Jaw.mirror = true;
      setRotation(Jaw, 0F, 0F, 0F);

      Arm1 = new ModelRenderer(this, 20, 54);
      Arm1.addBox(0F, 0F, 0F, 5, 25, 5);
      Arm1.setRotationPoint(-11F, -14F, -2F);
      Arm1.setTextureSize(100, 100);
      Arm1.mirror = true;
      setRotation(Arm1, 0F, 0F, 0F);

      Arm2 = new ModelRenderer(this, 0, 54);
      Arm2.addBox(0F, 0F, 0F, 5, 25, 5);
      Arm2.setRotationPoint(6F, -14F, -2F);
      Arm2.setTextureSize(100, 100);
      Arm2.mirror = true;
      setRotation(Arm2, 0F, 0F, 0F);
  }

  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    Leg1.render(f5);
    Leg2.render(f5);
    Body.render(f5);
    Head.render(f5);
    Jaw.render(f5);
    Arm1.render(f5);
    Arm2.render(f5);
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
	Jaw.rotateAngleY = f3 / (180F / (float)Math.PI);
	Jaw.rotateAngleX = f4 / (180F / (float)Math.PI);
    Arm1.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 2.0F * f1 * 0.5F;
    Arm2.rotateAngleX = MathHelper.cos(f * 0.6662F) * 2.0F * f1 * 0.5F;
    Arm1.rotateAngleZ = 0.0F;
    Arm2.rotateAngleZ = 0.0F;
    Leg1.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
    Leg2.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 1.4F * f1;
    Leg1.rotateAngleY = 0.0F;
    Leg2.rotateAngleY = 0.0F;
  }
}
