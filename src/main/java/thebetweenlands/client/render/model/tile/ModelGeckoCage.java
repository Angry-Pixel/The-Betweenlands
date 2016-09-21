package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

/**
 * BLGeckoCage - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelGeckoCage extends ModelBase {
	public ModelRenderer base;
	public ModelRenderer corner1;
	public ModelRenderer corner2;
	public ModelRenderer corner3;
	public ModelRenderer corner4;
	public ModelRenderer edge1;
	public ModelRenderer edge2;
	public ModelRenderer edge3;
	public ModelRenderer edge4;
	public ModelRenderer topbase;
	public ModelRenderer raster1;
	public ModelRenderer raster1b;
	public ModelRenderer raster2;
	public ModelRenderer raster2b;
	public ModelRenderer raster3;
	public ModelRenderer raster3b;
	public ModelRenderer raster4;
	public ModelRenderer raster4b;
	public ModelRenderer edge1b;
	public ModelRenderer edge2b;
	public ModelRenderer edge3b;
	public ModelRenderer edge4b;

	public ModelGeckoCage() {
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.raster4b = new ModelRenderer(this, 81, 42);
		this.raster4b.setRotationPoint(0.0F, -5.0F, 0.0F);
		this.raster4b.addBox(-1.0F, -5.0F, -5.99F, 1, 5, 12, 0.0F);
		this.setRotateAngle(raster4b, 0.0F, 0.0F, -0.27314402793711257F);
		this.raster1b = new ModelRenderer(this, 54, 35);
		this.raster1b.setRotationPoint(0.0F, -5.0F, 0.0F);
		this.raster1b.addBox(-5.99F, -5.0F, 0.0F, 12, 5, 1, 0.0F);
		this.setRotateAngle(raster1b, -0.27314402793711257F, 0.0F, 0.0F);
		this.raster2 = new ModelRenderer(this, 0, 42);
		this.raster2.setRotationPoint(-1.0F, 0.0F, 0.0F);
		this.raster2.addBox(0.0F, -5.0F, -6.01F, 1, 5, 12, 0.0F);
		this.setRotateAngle(raster2, 0.0F, 0.0F, 0.136659280431156F);
		this.edge3b = new ModelRenderer(this, 29, 15);
		this.edge3b.setRotationPoint(0.0F, 0.0F, 6.0F);
		this.edge3b.addBox(-6.0F, -2.0F, 0.0F, 12, 2, 2, 0.0F);
		this.setRotateAngle(edge3b, 0.27314402793711257F, 0.0F, 0.0F);
		this.raster2b = new ModelRenderer(this, 54, 42);
		this.raster2b.setRotationPoint(0.0F, -5.0F, 0.0F);
		this.raster2b.addBox(0.0F, -5.0F, -5.99F, 1, 5, 12, 0.0F);
		this.setRotateAngle(raster2b, 0.0F, 0.0F, 0.27314402793711257F);
		this.corner4 = new ModelRenderer(this, 87, 15);
		this.corner4.setRotationPoint(6.0F, 24.0F, -6.0F);
		this.corner4.addBox(0.0F, -16.0F, -2.0F, 2, 16, 2, 0.0F);
		this.edge4b = new ModelRenderer(this, 29, 20);
		this.edge4b.setRotationPoint(6.0F, 0.0F, 0.0F);
		this.edge4b.addBox(0.0F, -2.0F, -6.0F, 2, 2, 12, 0.0F);
		this.setRotateAngle(edge4b, 0.0F, 0.0F, -0.27314402793711257F);
		this.base = new ModelRenderer(this, 0, 0);
		this.base.setRotationPoint(0.0F, 21.8F, 0.0F);
		this.base.addBox(-6.0F, -2.0F, -6.0F, 12, 2, 12, 0.0F);
		this.corner2 = new ModelRenderer(this, 69, 15);
		this.corner2.setRotationPoint(-6.0F, 24.0F, 6.0F);
		this.corner2.addBox(-2.0F, -16.0F, 0.0F, 2, 16, 2, 0.0F);
		this.edge4 = new ModelRenderer(this, 29, 20);
		this.edge4.setRotationPoint(6.0F, -2.0F, 0.0F);
		this.edge4.addBox(0.0F, 0.0F, -6.0F, 2, 2, 12, 0.0F);
		this.setRotateAngle(edge4, 0.0F, 0.0F, 0.27314402793711257F);
		this.corner1 = new ModelRenderer(this, 60, 15);
		this.corner1.setRotationPoint(-6.0F, 24.0F, -6.0F);
		this.corner1.addBox(-2.0F, -16.0F, -2.0F, 2, 16, 2, 0.0F);
		this.raster1 = new ModelRenderer(this, 0, 35);
		this.raster1.setRotationPoint(0.0F, 0.0F, -1.0F);
		this.raster1.addBox(-6.01F, -5.0F, 0.0F, 12, 5, 1, 0.0F);
		this.setRotateAngle(raster1, -0.136659280431156F, 0.0F, 0.0F);
		this.raster3 = new ModelRenderer(this, 27, 35);
		this.raster3.setRotationPoint(0.0F, 0.0F, 1.0F);
		this.raster3.addBox(-6.01F, -5.0F, -1.0F, 12, 5, 1, 0.0F);
		this.setRotateAngle(raster3, 0.136659280431156F, 0.0F, 0.0F);
		this.edge1 = new ModelRenderer(this, 0, 15);
		this.edge1.setRotationPoint(0.0F, -2.0F, -6.0F);
		this.edge1.addBox(-6.0F, 0.0F, -2.0F, 12, 2, 2, 0.0F);
		this.setRotateAngle(edge1, 0.27314402793711257F, 0.0F, 0.0F);
		this.raster4 = new ModelRenderer(this, 27, 42);
		this.raster4.setRotationPoint(1.0F, 0.0F, 0.0F);
		this.raster4.addBox(-1.0F, -5.0F, -6.01F, 1, 5, 12, 0.0F);
		this.setRotateAngle(raster4, 0.0F, 0.0F, -0.136659280431156F);
		this.raster3b = new ModelRenderer(this, 81, 35);
		this.raster3b.setRotationPoint(0.0F, -5.0F, 0.0F);
		this.raster3b.addBox(-5.99F, -5.0F, -1.0F, 12, 5, 1, 0.0F);
		this.setRotateAngle(raster3b, 0.27314402793711257F, 0.0F, 0.0F);
		this.corner3 = new ModelRenderer(this, 78, 15);
		this.corner3.setRotationPoint(6.0F, 24.0F, 6.0F);
		this.corner3.addBox(0.0F, -16.0F, 0.0F, 2, 16, 2, 0.0F);
		this.edge2b = new ModelRenderer(this, 0, 20);
		this.edge2b.setRotationPoint(-6.0F, 0.0F, 0.0F);
		this.edge2b.addBox(-2.0F, -2.0F, -6.0F, 2, 2, 12, 0.0F);
		this.setRotateAngle(edge2b, 0.0F, 0.0F, 0.27314402793711257F);
		this.edge1b = new ModelRenderer(this, 0, 15);
		this.edge1b.setRotationPoint(0.0F, 0.0F, -6.0F);
		this.edge1b.addBox(-6.0F, -2.0F, -2.0F, 12, 2, 2, 0.0F);
		this.setRotateAngle(edge1b, -0.27314402793711257F, 0.0F, 0.0F);
		this.edge2 = new ModelRenderer(this, 0, 20);
		this.edge2.setRotationPoint(-6.0F, -2.0F, 0.0F);
		this.edge2.addBox(-2.0F, 0.0F, -6.0F, 2, 2, 12, 0.0F);
		this.setRotateAngle(edge2, 0.0F, 0.0F, -0.27314402793711257F);
		this.edge3 = new ModelRenderer(this, 29, 15);
		this.edge3.setRotationPoint(0.0F, -2.0F, 6.0F);
		this.edge3.addBox(-6.0F, 0.0F, 0.0F, 12, 2, 2, 0.0F);
		this.setRotateAngle(edge3, -0.27314402793711257F, 0.0F, 0.0F);
		this.topbase = new ModelRenderer(this, 49, 0);
		this.topbase.setRotationPoint(0.0F, -11.35F, 0.0F);
		this.topbase.addBox(-6.0F, -2.0F, -6.0F, 12, 2, 12, 0.0F);
		this.raster4.addChild(this.raster4b);
		this.raster1.addChild(this.raster1b);
		this.edge2.addChild(this.raster2);
		this.topbase.addChild(this.edge3b);
		this.raster2.addChild(this.raster2b);
		this.topbase.addChild(this.edge4b);
		this.base.addChild(this.edge4);
		this.edge1.addChild(this.raster1);
		this.edge3.addChild(this.raster3);
		this.base.addChild(this.edge1);
		this.edge4.addChild(this.raster4);
		this.raster3.addChild(this.raster3b);
		this.topbase.addChild(this.edge2b);
		this.topbase.addChild(this.edge1b);
		this.base.addChild(this.edge2);
		this.base.addChild(this.edge3);
		this.base.addChild(this.topbase);
	}

	public void render() { 
		this.corner4.render(0.0625F);
		this.base.render(0.0625F);
		this.corner2.render(0.0625F);
		this.corner1.render(0.0625F);
		this.corner3.render(0.0625F);
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
