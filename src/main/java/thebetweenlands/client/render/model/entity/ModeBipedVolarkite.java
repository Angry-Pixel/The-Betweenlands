package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

public class ModeBipedVolarkite extends ModelBiped {
	public ModeBipedVolarkite(float modelSize) {
		super(modelSize);
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

		this.bipedLeftArm.rotateAngleY = (float)Math.PI;
		this.bipedLeftArm.rotateAngleX = -0.01F;
		this.bipedLeftArm.rotateAngleZ = -2.7F;
		this.bipedLeftArm.rotationPointY = 1;
		this.bipedLeftArm.rotationPointX = 4;

		this.bipedRightArm.rotateAngleY = (float)Math.PI;
		this.bipedRightArm.rotateAngleX = -0.01F;
		this.bipedRightArm.rotateAngleZ = 2.7F;
		this.bipedRightArm.rotationPointY = 1;
		this.bipedRightArm.rotationPointX = -4;
	}
}
