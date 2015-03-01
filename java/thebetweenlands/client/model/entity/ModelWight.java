package thebetweenlands.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import thebetweenlands.entities.mobs.EntityWight;

public class ModelWight extends ModelBase
{
	 public ModelWight()
	 {
		    float scale = 0F;
		    TopBody = new ModelRenderer(this, 14, 23);
		    TopBody.addBox(-4.0F, -2.5F, -2F, 9, 5, 4, scale);
		    TopBody.setRotationPoint(-0.5F, 5.5F, -0.5F);
		    TopBody.rotateAngleX = 0.4363323F;

		    LowBody = new ModelRenderer(this, 0, 24);
		    LowBody.addBox(-2.5F, -3.0F, -1F, 5, 6, 2, scale);
		    LowBody.setRotationPoint(0F, 10.5F, 0F);

		    Head = new ModelRenderer(this, 0, 10);
		    Head.addBox(-2, -2, -2, 4, 4, 4, scale);
		    Head.setRotationPoint(0.0F, 2.5F, -3.5F);

		    Hood = new ModelRenderer(this,0, 0);
		    Hood.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, scale);
		    Hood.setRotationPoint(0F, 2.5F, -3.5F);

		    Arm1 = new ModelRenderer(this, 24, 0);
		    Arm1.addBox(-0.5F, -0.5F, -0.5F, 1, 18, 1, scale);
		    Arm1.setRotationPoint(-3.5F, 5F, 0F);

		    Arm2 = new ModelRenderer(this,24, 0);
		    Arm2.addBox(-0.5F, -0.5F, -0.5F, 1, 18, 1, scale);
		    Arm2.setRotationPoint(3.5F, 5F, 0F);

		    Leg1 = new ModelRenderer(this, 20, 0);
		    Leg1.addBox(-0.5F, -0.5F, -0.5F, 1, 12, 1, scale);
		    Leg1.setRotationPoint(1, 13, 0);

		    Leg2 = new ModelRenderer(this, 20, 0);
		    Leg2.addBox(-0.5F, -0.5F, -0.5F, 1, 12, 1, scale);
		    Leg2.setRotationPoint(-1, 13, 0);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		super.render(entity, f, f1, f2, f3, f4, f5);
	    setRotationAngles(f, f1, f2, f3, f4, f5, (EntityWight)entity);
	    Leg1.render(f5);
	    Leg2.render(f5);
	    Arm1.render(f5);
	    Arm2.render(f5);
	    TopBody.render(f5);
	    LowBody.render(f5);
	    Head.render(f5);
	    Hood.render(f5);
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, EntityWight entity)
	{
		/*Head.rotateAngleY = f3 / 12.29578F;
        Head.rotateAngleX = f4 / 12.29578F;
		Hood.rotateAngleY = f3 / 12.29578F;
		Hood.rotateAngleX = f4 / 12.29578F;
        Arm1.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 0.1F * f1 * 0.5F;
        Arm2.rotateAngleX = MathHelper.cos(f * 0.6662F) * 0.1F * f1 * 0.5F;
        Arm1.rotateAngleZ = 0.0F;
        Arm2.rotateAngleZ = 0.0F;
        Leg1.rotateAngleX = MathHelper.cos(f * 0.6662F) * 0.1F * f1;
        Leg2.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 0.1F * f1;
        Leg1.rotateAngleY = 0.0F;
        Leg2.rotateAngleY = 0.0F;*/

		Arm1.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
        Arm2.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 1.4F * f1;
        Arm1.rotateAngleZ = 0.0F;
        Arm2.rotateAngleZ = 0.0F;
		Leg1.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
		Leg2.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 1.4F * f1;
		Leg1.rotateAngleY = 0.0F;
	  	Leg2.rotateAngleY = 0.0F;
	}

	public ModelRenderer Arm1;
	public ModelRenderer Arm2;
	public ModelRenderer Leg1;
	public ModelRenderer Leg2;
	public ModelRenderer TopBody;
	public ModelRenderer LowBody;
	public ModelRenderer Head;
	public ModelRenderer Hood;
}
