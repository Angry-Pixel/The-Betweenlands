package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLPrimordialMalevolence2 - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelFortressBoss extends ModelBase {
	public ModelRenderer cape1;
	public ModelRenderer cap1;
	public ModelRenderer eye;
	public ModelRenderer cape2;
	public ModelRenderer chainpiece1;
	public ModelRenderer chainpiece2;
	public ModelRenderer cape3;
	public ModelRenderer cape4;
	public ModelRenderer cape5;
	public ModelRenderer cape6;
	public ModelRenderer chainpiece3;
	public ModelRenderer cap2;

	public ModelFortressBoss() {
		this.textureWidth = 512;
		this.textureHeight = 256;
		this.cap2 = new ModelRenderer(this, 214, 39);
		this.cap2.setRotationPoint(0.0F, 0.0F, 5.5F);
		this.cap2.addBox(-4.5F, 0.0F, -3.0F, 9, 9, 3, 0.0F);
		this.setRotateAngle(cap2, 0.22759093446006054F, 0.0F, 0.0F);
		this.eye = new ModelRenderer(this, 0, 0);
		this.eye.setRotationPoint(0.0F, -7.0F, -4.0F);
		this.eye.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		this.setRotateAngle(eye, 0.045553093477052F, 0.0F, 0.0F);
		this.cape5 = new ModelRenderer(this, 160, 26);
		this.cape5.setRotationPoint(0.0F, -4.0F, 0.0F);
		this.cape5.addBox(-12.0F, -8.0F, 0.0F, 24, 8, 0, 0.0F);
		this.setRotateAngle(cape5, -0.6829473363053812F, 0.0F, 0.0F);
		this.cape6 = new ModelRenderer(this, 160, 35);
		this.cape6.setRotationPoint(0.0F, -8.0F, 0.0F);
		this.cape6.addBox(-13.0F, -16.0F, 0.0F, 26, 16, 0, 0.0F);
		this.setRotateAngle(cape6, -0.18203784098300857F, 0.0F, 0.0F);
		this.cap1 = new ModelRenderer(this, 214, 0);
		this.cap1.setRotationPoint(0.0F, -18.12F, -4.09F);
		this.cap1.addBox(-5.5F, 0.0F, -5.5F, 11, 27, 11, 0.0F);
		this.setRotateAngle(cap1, 0.045553093477052F, 0.0F, 0.0F);
		this.cape1 = new ModelRenderer(this, 160, 0);
		this.cape1.setRotationPoint(0.0F, -0.8F, -5.7F);
		this.cape1.addBox(-10.0F, 0.0F, 0.0F, 20, 8, 0, 0.0F);
		this.setRotateAngle(cape1, 0.091106186954104F, 0.0F, 0.0F);
		this.chainpiece3 = new ModelRenderer(this, 172, 104);
		this.chainpiece3.setRotationPoint(5.0F, 0.0F, 0.0F);
		this.chainpiece3.addBox(-2.0F, -2.0F, 0.0F, 7, 4, 0, 0.0F);
		this.setRotateAngle(chainpiece3, -0.27314402793711257F, 0.0F, -0.5009094953223726F);
		this.cape3 = new ModelRenderer(this, 160, 16);
		this.cape3.setRotationPoint(0.0F, -6.0F, 0.0F);
		this.cape3.addBox(-12.0F, -4.0F, 0.0F, 24, 4, 0, 0.0F);
		this.setRotateAngle(cape3, -0.27314402793711257F, 0.0F, 0.0F);
		this.cape2 = new ModelRenderer(this, 160, 9);
		this.cape2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.cape2.addBox(-11.0F, -6.0F, 0.0F, 22, 6, 0, 0.0F);
		this.setRotateAngle(cape2, -1.3203415791337103F, 0.0F, 0.0F);
		this.cape4 = new ModelRenderer(this, 160, 21);
		this.cape4.setRotationPoint(0.0F, -4.0F, 0.0F);
		this.cape4.addBox(-12.0F, -4.0F, 0.0F, 24, 4, 0, 0.0F);
		this.setRotateAngle(cape4, -0.8196066167365371F, 0.0F, 0.0F);
		this.chainpiece2 = new ModelRenderer(this, 184, 104);
		this.chainpiece2.setRotationPoint(6.0F, 8.0F, 0.0F);
		this.chainpiece2.addBox(-5.0F, -1.5F, 0.0F, 6, 4, 0, 0.0F);
		this.setRotateAngle(chainpiece2, -0.045553093477052F, -0.045553093477052F, -0.5009094953223726F);
		this.chainpiece1 = new ModelRenderer(this, 160, 104);
		this.chainpiece1.setRotationPoint(-6.0F, 8.0F, 0.0F);
		this.chainpiece1.addBox(-1.0F, -1.5F, 0.0F, 6, 4, 0, 0.0F);
		this.setRotateAngle(chainpiece1, -0.045553093477052F, 0.045553093477052F, 0.5009094953223726F);
		this.cap1.addChild(this.cap2);
		this.cape4.addChild(this.cape5);
		this.cape5.addChild(this.cape6);
		this.chainpiece1.addChild(this.chainpiece3);
		this.cape2.addChild(this.cape3);
		this.cape1.addChild(this.cape2);
		this.cape3.addChild(this.cape4);
		this.cape1.addChild(this.chainpiece2);
		this.cape1.addChild(this.chainpiece1);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		this.eye.render(f5);
		this.cap1.render(f5);
		this.cape1.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
