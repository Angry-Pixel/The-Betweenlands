package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBarrel extends ModelBase {
	public ModelRenderer base;
	public ModelRenderer top;
	public ModelRenderer bside1;
	public ModelRenderer bside2;
	public ModelRenderer bside3;
	public ModelRenderer bside4;
	public ModelRenderer tside1;
	public ModelRenderer tside2;
	public ModelRenderer tside3;
	public ModelRenderer tside4;

	public ModelBarrel() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.bside1 = new ModelRenderer(this, 0, 15);
		this.bside1.setRotationPoint(-5.0F, 0.0F, 3.7F);
		this.bside1.addBox(0.0F, 0.0F, -0.1F, 10, 1, 6, 0.0F);
		this.setRotateAngle(bside1, 1.354550032472799F, 0.0F, 0.0F);
		this.bside4 = new ModelRenderer(this, 0, 15);
		this.bside4.setRotationPoint(4.7F, 0.3F, -5.0F);
		this.bside4.addBox(0.0F, 0.0F, 0.0F, 10, 1, 6, 0.0F);
		this.setRotateAngle(bside4, 1.7872171540421935F, -1.5707963267948966F, -0.0F);
		this.tside2 = new ModelRenderer(this, 0, 24);
		this.tside2.setRotationPoint(-5.0F, 0.3F, -4.65F);
		this.tside2.addBox(-0.01F, 0.0F, 0.0F, 10, 1, 6, 0.0F);
		this.setRotateAngle(tside2, 1.7872171540421935F, 0.0F, 0.0F);
		this.tside1 = new ModelRenderer(this, 0, 24);
		this.tside1.setRotationPoint(-5.0F, 0.0F, 3.7F);
		this.tside1.addBox(0.01F, 0.0F, -0.1F, 10, 1, 6, 0.0F);
		this.setRotateAngle(tside1, 1.3543754995475996F, 0.0F, 0.0F);
		this.bside2 = new ModelRenderer(this, 0, 15);
		this.bside2.setRotationPoint(-5.0F, 0.3F, -4.65F);
		this.bside2.addBox(0.0F, 0.0F, 0.0F, 10, 1, 6, 0.0F);
		this.setRotateAngle(bside2, 1.7872171540421935F, 0.0F, 0.0F);
		this.bside3 = new ModelRenderer(this, 0, 15);
		this.bside3.setRotationPoint(-3.7F, 0.0F, -5.0F);
		this.bside3.addBox(0.0F, 0.0F, -0.1F, 10, 1, 6, 0.0F);
		this.setRotateAngle(bside3, 1.3543754995475996F, -1.5707963267948966F, 0.0F);
		this.tside3 = new ModelRenderer(this, 0, 24);
		this.tside3.setRotationPoint(-3.7F, 0.0F, -5.0F);
		this.tside3.addBox(-0.01F, 0.0F, -0.1F, 10, 1, 6, 0.0F);
		this.setRotateAngle(tside3, 1.3543754995475996F, -1.5707963267948966F, 0.0F);
		this.base = new ModelRenderer(this, 0, 0);
		this.base.setRotationPoint(0.0F, 23.0F, 0.0F);
		this.base.addBox(-4.5F, 0.0F, -4.5F, 9, 1, 9, 0.0F);
		this.tside4 = new ModelRenderer(this, 0, 24);
		this.tside4.setRotationPoint(4.7F, 0.3F, -5.0F);
		this.tside4.addBox(0.01F, 0.0F, 0.0F, 10, 1, 6, 0.0F);
		this.setRotateAngle(tside4, 1.7872171540421935F, -1.5707963267948966F, -0.0F);
		this.top = new ModelRenderer(this, 28, 2);
		this.top.setRotationPoint(0.0F, 11.9F, 0.0F);
		this.top.addBox(-4.5F, 0.0F, -4.5F, 9, 1, 9, 0.0F);
		this.setRotateAngle(top, 3.141592653589793F, 0.0F, 0.0F);
		this.base.addChild(this.bside1);
		this.base.addChild(this.bside4);
		this.top.addChild(this.tside2);
		this.top.addChild(this.tside1);
		this.base.addChild(this.bside2);
		this.base.addChild(this.bside3);
		this.top.addChild(this.tside3);
		this.top.addChild(this.tside4);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		this.base.render(f5);
		this.top.render(f5);
	}

	public void render(float f5) { 
		this.base.render(f5);
		this.top.render(f5);
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
