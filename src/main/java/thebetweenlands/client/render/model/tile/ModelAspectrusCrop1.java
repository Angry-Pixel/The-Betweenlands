package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

/**
 * BLAspectCropGrowth1 - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelAspectrusCrop1 extends ModelBase {
	public ModelRenderer crop1;
	public ModelRenderer leaf1;
	public ModelRenderer leaf2;
	public ModelRenderer leaf3;
	public ModelRenderer leaf4;
	public ModelRenderer leaf5;
	public ModelRenderer leaf1b;
	public ModelRenderer leafb;
	public ModelRenderer leaf3b;

	public ModelAspectrusCrop1() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.leaf3 = new ModelRenderer(this, 17, 11);
		this.leaf3.setRotationPoint(-2.0F, -3.0F, 2.5F);
		this.leaf3.addBox(-2.0F, 0.0F, -3.5F, 4, 0, 4, 0.0F);
		this.setRotateAngle(leaf3, -0.27314402793711257F, 2.5497515042385164F, 0.0F);
		this.leaf2 = new ModelRenderer(this, 32, 0);
		this.leaf2.setRotationPoint(2.5F, -4.0F, 1.5F);
		this.leaf2.addBox(-2.5F, 0.0F, -3.5F, 5, 0, 4, 0.0F);
		this.setRotateAngle(leaf2, -0.136659280431156F, -2.0488420089161434F, 0.0F);
		this.leaf5 = new ModelRenderer(this, 33, 15);
		this.leaf5.setRotationPoint(-2.5F, -5.0F, 1.0F);
		this.leaf5.addBox(-2.0F, 0.0F, -2.5F, 4, 0, 3, 0.0F);
		this.setRotateAngle(leaf5, -0.31869712141416456F, 1.7756979809790308F, 0.0F);
		this.crop1 = new ModelRenderer(this, 0, 0);
		this.crop1.setRotationPoint(0.0F, 24.0F, 0.0F);
		this.crop1.addBox(-2.5F, -8.0F, -2.5F, 5, 8, 5, 0.0F);
		this.leaf3b = new ModelRenderer(this, 21, 16);
		this.leaf3b.setRotationPoint(0.0F, 0.0F, -3.5F);
		this.leaf3b.addBox(-2.0F, 0.0F, -4.0F, 4, 1, 4, 0.0F);
		this.setRotateAngle(leaf3b, 0.5462880558742251F, 0.0F, 0.0F);
		this.leaf1b = new ModelRenderer(this, 21, 5);
		this.leaf1b.setRotationPoint(0.0F, 0.0F, -2.5F);
		this.leaf1b.addBox(-2.0F, 0.0F, -3.0F, 4, 1, 3, 0.0F);
		this.setRotateAngle(leaf1b, 0.5462880558742251F, 0.0F, 0.0F);
		this.leaf4 = new ModelRenderer(this, 33, 11);
		this.leaf4.setRotationPoint(1.0F, -6.0F, -2.5F);
		this.leaf4.addBox(-2.0F, 0.0F, -2.5F, 4, 0, 3, 0.0F);
		this.setRotateAngle(leaf4, -0.36425021489121656F, -0.27314402793711257F, 0.0F);
		this.leafb = new ModelRenderer(this, 36, 5);
		this.leafb.setRotationPoint(0.0F, 0.0F, -3.5F);
		this.leafb.addBox(-2.5F, 0.0F, -4.0F, 5, 1, 4, 0.0F);
		this.setRotateAngle(leafb, 0.31869712141416456F, 0.0F, 0.0F);
		this.leaf1 = new ModelRenderer(this, 18, 0);
		this.leaf1.setRotationPoint(-1.5F, -2.0F, -2.5F);
		this.leaf1.addBox(-2.0F, 0.0F, -2.5F, 4, 0, 3, 0.0F);
		this.setRotateAngle(leaf1, -0.40980330836826856F, 0.36425021489121656F, 0.0F);
		this.crop1.addChild(this.leaf3);
		this.crop1.addChild(this.leaf2);
		this.crop1.addChild(this.leaf5);
		this.leaf3.addChild(this.leaf3b);
		this.leaf1.addChild(this.leaf1b);
		this.crop1.addChild(this.leaf4);
		this.leaf2.addChild(this.leafb);
		this.crop1.addChild(this.leaf1);
	}

	public void render() { 
		this.crop1.render(0.0625F);
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
