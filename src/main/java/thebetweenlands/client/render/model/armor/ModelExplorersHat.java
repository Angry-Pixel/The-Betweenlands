package thebetweenlands.client.render.model.armor;

import net.minecraft.client.model.ModelRenderer;

public class ModelExplorersHat extends ModelBodyAttachment {
	public ModelRenderer hatrim;

	public ModelExplorersHat() {
		textureWidth = 64;
		textureHeight = 32;
		ModelRenderer rimjobback = new ModelRenderer(this, 37, 16);
		rimjobback.setRotationPoint(0.0F, -8.0F, 6.0F);
		rimjobback.addBox(-5.0F, 0.0F, 0.0F, 10, 2, 1, 0.0F);
		setRotateAngle(rimjobback, -0.18203784098300857F, 0.0F, 0.0F);
		ModelRenderer hatcup = new ModelRenderer(this, 0, 15);
		hatcup.setRotationPoint(0.0F, 0.0F, 1.0F);
		hatcup.addBox(-4.5F, -11.4F, -6.3F, 9, 4, 9, 0.0F);
		setRotateAngle(hatcup, -0.091106186954104F, 0.0F, 0.0F);
		ModelRenderer rimjobfront = new ModelRenderer(this, 37, 16);
		rimjobfront.setRotationPoint(0.0F, -8.0F, -6.0F);
		rimjobfront.addBox(-5.0F, 0.0F, -1.0F, 10, 2, 1, 0.0F);
		setRotateAngle(rimjobfront, 0.136659280431156F, 0.0F, 0.0F);
		ModelRenderer rimjobleft = new ModelRenderer(this, 37, 0);
		rimjobleft.setRotationPoint(6.0F, -6.0F, 0.0F);
		rimjobleft.addBox(0.0F, -2.0F, -5.0F, 1, 2, 10, 0.0F);
		setRotateAngle(rimjobleft, 0.0F, 0.0F, -0.18203784098300857F);
		ModelRenderer rimjobright = new ModelRenderer(this, 37, 0);
		rimjobright.setRotationPoint(-6.0F, -6.0F, 0.0F);
		rimjobright.addBox(-1.0F, -2.0F, -5.0F, 1, 2, 10, 0.0F);
		setRotateAngle(rimjobright, 0.0F, 0.0F, 0.18203784098300857F);
		hatrim = new ModelRenderer(this, 0, 0);
		hatrim.setRotationPoint(0.0F, 1.0F, 0.0F);
		hatrim.addBox(-6.0F, -8.0F, -6.0F, 12, 2, 12, 0.0F);
		setRotateAngle(hatrim, -0.18203784098300857F, 0.0F, 0.0F);
		hatrim.addChild(rimjobback);
		hatrim.addChild(hatcup);
		hatrim.addChild(rimjobfront);
		hatrim.addChild(rimjobleft);
		hatrim.addChild(rimjobright);

		bipedHead.addChild(hatrim);
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
