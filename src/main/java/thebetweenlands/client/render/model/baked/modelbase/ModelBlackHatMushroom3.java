package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLBlackhatMushroom3 - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelBlackHatMushroom3 extends ModelBase {
	public ModelRenderer stalk1;
	public ModelRenderer hat1;
	public ModelRenderer hat2;
	public ModelRenderer moss1;

	public ModelBlackHatMushroom3() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.moss1 = new ModelRenderer(this, 17, 0);
		this.moss1.setRotationPoint(2.5F, 0.0F, 0.0F);
		this.moss1.addBox(0.0F, 0.0F, -2.5F, 0, 7, 5, 0.0F);
		this.setRotateAngle(moss1, 0.0F, 0.0F, -0.091106186954104F);
		this.hat2 = new ModelRenderer(this, 0, 23);
		this.hat2.setRotationPoint(0.0F, -6.0F, 0.0F);
		this.hat2.addBox(-2.0F, -3.0F, -2.0F, 4, 3, 4, 0.0F);
		this.hat1 = new ModelRenderer(this, 0, 11);
		this.hat1.setRotationPoint(0.0F, -6.8F, 0.0F);
		this.hat1.addBox(-2.5F, -6.0F, -2.5F, 5, 6, 5, 0.0F);
		this.setRotateAngle(hat1, -0.091106186954104F, 0.0F, -0.045553093477052F);
		this.stalk1 = new ModelRenderer(this, 0, 0);
		this.stalk1.setRotationPoint(0.0F, 24.0F, 0.0F);
		this.stalk1.addBox(-1.0F, -7.0F, -1.0F, 2, 8, 2, 0.0F);
		this.setRotateAngle(stalk1, 0.091106186954104F, 0.0F, 0.136659280431156F);
		this.hat2.addChild(this.moss1);
		this.hat1.addChild(this.hat2);
		this.stalk1.addChild(this.hat1);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
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
