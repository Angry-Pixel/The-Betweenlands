package thebetweenlands.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import thebetweenlands.entities.mobs.EntityDarkDruid;
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

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
		super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
		setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
	    Leg1.render(unitPixel);
	    Leg2.render(unitPixel);
	    Arm1.render(unitPixel);
	    Arm2.render(unitPixel);
	    TopBody.render(unitPixel);
	    LowBody.render(unitPixel);
	    Head.render(unitPixel);
	    Hood.render(unitPixel);
	}

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);

		 EntityWight wight = (EntityWight) entity;

	       	Hood.rotateAngleX = Head.rotateAngleX = wight.getAnimation(); //1F;

       // Hood.rotateAngleX = Head.rotateAngleX = rotationPitch;
        
		Arm1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAngle;
        Arm2.rotateAngleX = -MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAngle;

		Leg1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAngle;
		Leg2.rotateAngleX = -MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAngle;
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
