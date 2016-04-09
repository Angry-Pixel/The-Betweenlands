package thebetweenlands.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelFrog extends ModelBase {
	ModelRenderer torso;
	ModelRenderer head;
	ModelRenderer legfrontleft1;
	ModelRenderer legfrontright1;
	ModelRenderer legfrontleft2;
	ModelRenderer legfrontright2;
	ModelRenderer legbackleft1;
	ModelRenderer legbackright1;
	ModelRenderer legbackleft2;
	ModelRenderer legbackright2;

	public ModelFrog() {
		textureWidth = 64;
		textureHeight = 32;

		torso = new ModelRenderer(this, 0, 0);
		torso.addBox(-2.5F, -1F, 0F, 5, 3, 6);
		torso.setRotationPoint(0F, 19F, 0F);
		setRotation(torso, -0.5948578F, 0F, 0F);
		head = new ModelRenderer(this, 0, 10);
		head.addBox(-1.5F, -1F, -3F, 3, 2, 3);
		head.setRotationPoint(0F, 19F, 0F);
		setRotation(head, -0.0743572F, 0F, 0F);
		legfrontleft1 = new ModelRenderer(this, 25, 0);
		legfrontleft1.addBox(0F, -1F, -0.5F, 1, 3, 2);
		legfrontleft1.setRotationPoint(2F, 20F, 0F);
		setRotation(legfrontleft1, -0.0743572F, 0.1858931F, -0.3717861F);
		legfrontright1 = new ModelRenderer(this, 32, 0);
		legfrontright1.addBox(-1F, -1F, -0.5F, 1, 3, 2);
		legfrontright1.setRotationPoint(-2F, 20F, 0F);
		setRotation(legfrontright1, -0.074351F, -0.185895F, 0.37179F);
		legfrontleft2 = new ModelRenderer(this, 25, 6);
		legfrontleft2.addBox(1F, 0.5F, -0.5F, 1, 3, 1);
		legfrontleft2.setRotationPoint(2F, 20F, 0F);
		setRotation(legfrontleft2, -0.4089647F, 0F, 0.5948578F);
		legfrontright2 = new ModelRenderer(this, 30, 6);
		legfrontright2.addBox(-2F, 0.5F, -0.5F, 1, 3, 1);
		legfrontright2.setRotationPoint(-2F, 20F, 0F);
		setRotation(legfrontright2, -0.4089647F, 0F, -0.5948606F);
		legbackleft1 = new ModelRenderer(this, 25, 11);
		legbackleft1.addBox(-0.5F, -1.5F, -0.5F, 2, 2, 3);
		legbackleft1.setRotationPoint(2F, 22F, 3F);
		setRotation(legbackleft1, -0.2974289F, -0.1487144F, -0.1858931F);
		legbackright1 = new ModelRenderer(this, 36, 11);
		legbackright1.addBox(-1.5F, -1.5F, -0.5F, 2, 2, 3);
		legbackright1.setRotationPoint(-2F, 22F, 3F);
		setRotation(legbackright1, -0.2974216F, 0.1487195F, 0.185895F);
		legbackleft2 = new ModelRenderer(this, 25, 17);
		legbackleft2.addBox(0.5F, 1F, -2F, 1, 1, 4);
		legbackleft2.setRotationPoint(2F, 22F, 3F);
		setRotation(legbackleft2, 0.1487144F, -0.1115358F, 0F);
		legbackright2 = new ModelRenderer(this, 36, 17);
		legbackright2.addBox(-1.5F, 1F, -2F, 1, 1, 4);
		legbackright2.setRotationPoint(-2F, 22F, 3F);
		setRotation(legbackright2, 0.1487144F, 0.111544F, 0F);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
		super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
		setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		torso.render(unitPixel);
		head.render(unitPixel);
		legfrontleft1.render(unitPixel);
		legfrontright1.render(unitPixel);
		legfrontleft2.render(unitPixel);
		legfrontright2.render(unitPixel);
		legbackleft1.render(unitPixel);
		legbackright1.render(unitPixel);
		legbackleft2.render(unitPixel);
		legbackright2.render(unitPixel);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
	}

}
