package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLWalkway - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelWalkwayBlock extends ModelBase {
	public ModelRenderer plank1;
	public ModelRenderer plank2;
	public ModelRenderer plank3;
	public ModelRenderer beamleft;
	public ModelRenderer beamright;
	public ModelRenderer standleft;
	public ModelRenderer standright;

	public ModelWalkwayBlock() {
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.plank3 = new ModelRenderer(this, 0, 16);
		this.plank3.setRotationPoint(0.0F, 14.0F, 0.0F);
		this.plank3.addBox(-8.0F, 0.0F, 2.85F, 16, 2, 5, 0.0F);
		this.beamright = new ModelRenderer(this, 43, 20);
		this.beamright.setRotationPoint(-5.0F, 16.0F, 0.0F);
		this.beamright.addBox(-1.0F, 0.0F, -8.0F, 2, 3, 16, 0.0F);
		this.plank1 = new ModelRenderer(this, 0, 0);
		this.plank1.setRotationPoint(0.0F, 14.0F, 0.0F);
		this.plank1.addBox(-8.0F, 0.0F, -7.8F, 16, 2, 5, 0.0F);
		this.standright = new ModelRenderer(this, 13, 24);
		this.standright.setRotationPoint(-4.0F, 16.0F, 0.0F);
		this.standright.addBox(-3.0F, 0.0F, -1.5F, 3, 9, 3, 0.0F);
		this.setRotateAngle(standright, 0.0F, 0.0F, 0.091106186954104F);
		this.standleft = new ModelRenderer(this, 0, 24);
		this.standleft.setRotationPoint(4.0F, 16.0F, 0.0F);
		this.standleft.addBox(0.0F, 0.0F, -1.5F, 3, 9, 3, 0.0F);
		this.setRotateAngle(standleft, 0.0F, 0.0F, -0.091106186954104F);
		this.beamleft = new ModelRenderer(this, 43, 0);
		this.beamleft.setRotationPoint(5.0F, 16.0F, 0.0F);
		this.beamleft.addBox(-1.0F, 0.0F, -8.0F, 2, 3, 16, 0.0F);
		this.plank2 = new ModelRenderer(this, 0, 8);
		this.plank2.setRotationPoint(0.0F, 14.0F, 0.0F);
		this.plank2.addBox(-8.0F, 0.0F, -2.5F, 16, 2, 5, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		this.plank3.render(f5);
		this.beamright.render(f5);
		this.plank1.render(f5);
		this.standright.render(f5);
		this.standleft.render(f5);
		this.beamleft.render(f5);
		this.plank2.render(f5);
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
