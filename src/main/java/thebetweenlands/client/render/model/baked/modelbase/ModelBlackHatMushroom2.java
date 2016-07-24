package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLBlackhatMushroom2 - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelBlackHatMushroom2 extends ModelBase {
	public ModelRenderer stalk1;
	public ModelRenderer stalk2;
	public ModelRenderer hat1;
	public ModelRenderer hat2;
	public ModelRenderer hat3;
	public ModelRenderer hat4;

	public ModelBlackHatMushroom2() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.hat2 = new ModelRenderer(this, 0, 19);
		this.hat2.setRotationPoint(0.0F, -5.0F, 0.0F);
		this.hat2.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
		this.hat4 = new ModelRenderer(this, 17, 14);
		this.hat4.setRotationPoint(0.0F, -4.0F, 0.0F);
		this.hat4.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2, 0.0F);
		this.hat1 = new ModelRenderer(this, 0, 9);
		this.hat1.setRotationPoint(0.0F, -4.5F, 0.0F);
		this.hat1.addBox(-2.0F, -5.0F, -2.0F, 4, 5, 4, 0.0F);
		this.setRotateAngle(hat1, -0.136659280431156F, 0.0F, -0.136659280431156F);
		this.stalk2 = new ModelRenderer(this, 17, 0);
		this.stalk2.setRotationPoint(-1.0F, 24.0F, 1.5F);
		this.stalk2.addBox(-1.0F, -2.0F, -1.0F, 2, 3, 2, 0.0F);
		this.setRotateAngle(stalk2, -0.091106186954104F, -0.136659280431156F, -0.045553093477052F);
		this.stalk1 = new ModelRenderer(this, 0, 0);
		this.stalk1.setRotationPoint(2.0F, 24.0F, -2.0F);
		this.stalk1.addBox(-1.0F, -5.0F, -1.0F, 2, 6, 2, 0.0F);
		this.setRotateAngle(stalk1, 0.18203784098300857F, 0.0F, 0.22759093446006054F);
		this.hat3 = new ModelRenderer(this, 17, 6);
		this.hat3.setRotationPoint(0.0F, -1.9F, 0.0F);
		this.hat3.addBox(-1.5F, -4.0F, -1.5F, 3, 4, 3, 0.0F);
		this.setRotateAngle(hat3, 0.045553093477052F, 0.0F, 0.045553093477052F);
		this.hat1.addChild(this.hat2);
		this.hat3.addChild(this.hat4);
		this.stalk1.addChild(this.hat1);
		this.stalk2.addChild(this.hat3);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		this.stalk2.render(f5);
		this.stalk1.render(f5);
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
