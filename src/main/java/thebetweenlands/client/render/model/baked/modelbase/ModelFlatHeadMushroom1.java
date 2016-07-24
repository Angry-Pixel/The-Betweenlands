package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLFlatheadMushroom1 - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelFlatHeadMushroom1 extends ModelBase {
	public ModelRenderer stalk1;
	public ModelRenderer hat1;
	public ModelRenderer hat1a;
	public ModelRenderer hat1b;

	public ModelFlatHeadMushroom1() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.hat1b = new ModelRenderer(this, 19, 22);
		this.hat1b.setRotationPoint(0.0F, 0.0F, 4.0F);
		this.hat1b.addBox(-4.0F, -3.0F, 0.0F, 8, 2, 1, 0.0F);
		this.stalk1 = new ModelRenderer(this, 0, 0);
		this.stalk1.setRotationPoint(0.0F, 24.0F, 0.0F);
		this.stalk1.addBox(-1.5F, -4.0F, -1.5F, 3, 7, 3, 0.0F);
		this.setRotateAngle(stalk1, -0.091106186954104F, 0.0F, 0.136659280431156F);
		this.hat1 = new ModelRenderer(this, 0, 11);
		this.hat1.setRotationPoint(0.0F, -2.8F, 0.0F);
		this.hat1.addBox(-5.0F, -3.0F, -4.0F, 10, 2, 8, 0.0F);
		this.setRotateAngle(hat1, 0.045553093477052F, 0.0F, -0.091106186954104F);
		this.hat1a = new ModelRenderer(this, 0, 22);
		this.hat1a.setRotationPoint(0.0F, 0.0F, -3.0F);
		this.hat1a.addBox(-4.0F, -3.0F, -2.0F, 8, 2, 1, 0.0F);
		this.hat1.addChild(this.hat1b);
		this.stalk1.addChild(this.hat1);
		this.hat1.addChild(this.hat1a);
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
