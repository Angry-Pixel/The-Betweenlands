package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelCompostBin extends ModelBase {
	ModelRenderer binpiece_r;
	ModelRenderer binpiece_l;
	public ModelRenderer bintop;
	ModelRenderer binpiece_b;
	ModelRenderer support1;
	ModelRenderer support2;
	ModelRenderer net1;
	ModelRenderer leg3;
	ModelRenderer leg4;
	ModelRenderer leg1;
	ModelRenderer leg2;
	ModelRenderer net2;
	ModelRenderer net3;

	public ModelCompostBin() {
		textureWidth = 128;
		textureHeight = 64;
		binpiece_l = new ModelRenderer(this, 37, 0);
		binpiece_l.setRotationPoint(4.5F, 8.5F, 0.0F);
		binpiece_l.addBox(0.0F, 1.0F, -8.0F, 2, 12, 16, 0.0F);
		setRotateAngle(binpiece_l, 0.0F, 0.0F, -0.091106186954104F);
		bintop = new ModelRenderer(this, 0, 38);
		bintop.setRotationPoint(6.0F, 9.0F, 0.0F);
		bintop.addBox(-14.0F, -1.0F, -8.01F, 16, 2, 16, 0.0F);
		leg1 = new ModelRenderer(this, 30, 29);
		leg1.setRotationPoint(2.0F, 13.0F, -6.0F);
		leg1.addBox(-2.0F, 0.0F, -1.99F, 2, 3, 5, 0.0F);
		setRotateAngle(leg1, 0.0F, 0.0F, 0.091106186954104F);
		leg4 = new ModelRenderer(this, 15, 29);
		leg4.setRotationPoint(-2.0F, 13.0F, 6.0F);
		leg4.addBox(0.0F, 0.0F, -3.01F, 2, 3, 5, 0.0F);
		setRotateAngle(leg4, 0.0F, 0.0F, -0.091106186954104F);
		leg2 = new ModelRenderer(this, 45, 29);
		leg2.setRotationPoint(2.0F, 13.0F, 6.0F);
		leg2.addBox(-2.0F, 0.0F, -3.01F, 2, 3, 5, 0.0F);
		setRotateAngle(leg2, 0.0F, 0.0F, 0.091106186954104F);
		net2 = new ModelRenderer(this, 75, 21);
		net2.setRotationPoint(0.0F, 5.0F, 0.0F);
		net2.addBox(-6.0F, 0.0F, 0.0F, 12, 5, 0, 0.0F);
		setRotateAngle(net2, 0.091106186954104F, 0.0F, 0.0F);
		support1 = new ModelRenderer(this, 22, 0);
		support1.setRotationPoint(8.0F, 10.0F, -5.0F);
		support1.addBox(-2.0F, 0.0F, -2.0F, 2, 6, 4, 0.0F);
		setRotateAngle(support1, 0.0F, 0.0F, 0.136659280431156F);
		binpiece_b = new ModelRenderer(this, 75, 0);
		binpiece_b.setRotationPoint(0.0F, 10.0F, 5.0F);
		binpiece_b.addBox(-6.0F, -0.6F, 0.0F, 12, 12, 2, 0.0F);
		support2 = new ModelRenderer(this, 35, 0);
		support2.setRotationPoint(8.0F, 10.0F, 5.0F);
		support2.addBox(-2.0F, 0.0F, -2.0F, 2, 6, 4, 0.0F);
		setRotateAngle(support2, 0.0F, 0.0F, 0.136659280431156F);
		binpiece_r = new ModelRenderer(this, 0, 0);
		binpiece_r.setRotationPoint(-4.5F, 8.5F, 0.0F);
		binpiece_r.addBox(-2.0F, 1.0F, -8.0F, 2, 12, 16, 0.0F);
		setRotateAngle(binpiece_r, 0.0F, 0.0F, 0.091106186954104F);
		leg3 = new ModelRenderer(this, 0, 29);
		leg3.setRotationPoint(-2.0F, 13.0F, -6.0F);
		leg3.addBox(0.0F, 0.0F, -1.99F, 2, 3, 5, 0.0F);
		setRotateAngle(leg3, 0.0F, 0.0F, -0.091106186954104F);
		net1 = new ModelRenderer(this, 75, 15);
		net1.setRotationPoint(0.0F, 10.0F, -7.0F);
		net1.addBox(-6.0F, 0.0F, 0.0F, 12, 5, 0, 0.0F);
		setRotateAngle(net1, -0.045553093477052F, 0.091106186954104F, 0.0F);
		net3 = new ModelRenderer(this, 75, 27);
		net3.setRotationPoint(0.0F, 5.0F, 0.0F);
		net3.addBox(-6.0F, 0.0F, 0.0F, 12, 4, 0, 0.0F);
		setRotateAngle(net3, -0.31869712141416456F, 0.0F, 0.0F);
		binpiece_l.addChild(leg1);
		binpiece_r.addChild(leg4);
		binpiece_l.addChild(leg2);
		net1.addChild(net2);
		binpiece_r.addChild(leg3);
		net2.addChild(net3);
	}

	public void render(float lidAngle) {
		binpiece_l.render(0.0625F);
		support1.render(0.0625F);
		binpiece_b.render(0.0625F);
		support2.render(0.0625F);
		binpiece_r.render(0.0625F);
		net1.render(0.0625F);
		if(lidAngle > 80) {
			bintop.offsetX = 0.06f;
		} else {
			bintop.offsetX = 0f;
		}
		bintop.rotateAngleZ = (float)Math.toRadians(lidAngle);
		bintop.render(0.0625F);
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
