package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLRuneCrafting - TripleHeadedSheep
 * Created using Tabula 7.0.0
 */
public class ModelRuneAltar extends ModelBase {
	public ModelRenderer base;
	public ModelRenderer top;
	public ModelRenderer thingleft;
	public ModelRenderer thingright;
	public ModelRenderer decorations1;
	public ModelRenderer decorations2;
	public ModelRenderer leftthing;
	public ModelRenderer anotherthingontheleft;
	public ModelRenderer rightthing;
	public ModelRenderer anotherthingontheright;

	public ModelRuneAltar() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.top = new ModelRenderer(this, 0, 19);
		this.top.setRotationPoint(0.0F, 12.0F, 3.0F);
		this.top.addBox(-5.0F, -3.0F, -8.0F, 10, 3, 10, 0.0F);
		this.setRotateAngle(top, 0.091106186954104F, 0.0F, 0.0F);
		this.base = new ModelRenderer(this, 0, 0);
		this.base.setRotationPoint(0.0F, 24.0F, 0.0F);
		this.base.addBox(-3.0F, -12.0F, -3.0F, 6, 12, 6, 0.0F);
		this.thingleft = new ModelRenderer(this, 25, 0);
		this.thingleft.setRotationPoint(0.0F, 0.0F, -8.0F);
		this.thingleft.addBox(-5.0F, 0.0F, 0.0F, 3, 3, 2, 0.0F);
		this.rightthing = new ModelRenderer(this, 36, 6);
		this.rightthing.setRotationPoint(5.0F, 1.0F, 2.0F);
		this.rightthing.addBox(-2.0F, 0.0F, 0.0F, 2, 2, 3, 0.0F);
		this.setRotateAngle(rightthing, -0.18203784098300857F, 0.0F, 0.0F);
		this.thingright = new ModelRenderer(this, 36, 0);
		this.thingright.setRotationPoint(0.0F, 0.0F, -8.0F);
		this.thingright.addBox(2.0F, 0.0F, 0.0F, 3, 3, 2, 0.0F);
		this.decorations1 = new ModelRenderer(this, 31, 19);
		this.decorations1.setRotationPoint(0.0F, 3.0F, -8.0F);
		this.decorations1.addBox(-6.0F, 0.0F, 0.0F, 12, 5, 0, 0.0F);
		this.setRotateAngle(decorations1, -0.091106186954104F, 0.0F, 0.0F);
		this.decorations2 = new ModelRenderer(this, 31, 24);
		this.decorations2.setRotationPoint(0.0F, 0.0F, 2.0F);
		this.decorations2.addBox(-6.0F, 0.0F, 0.0F, 12, 5, 0, 0.0F);
		this.setRotateAngle(decorations2, -0.091106186954104F, 0.0F, 0.0F);
		this.leftthing = new ModelRenderer(this, 25, 6);
		this.leftthing.setRotationPoint(-5.0F, 1.0F, 2.0F);
		this.leftthing.addBox(0.0F, 0.0F, 0.0F, 2, 2, 3, 0.0F);
		this.setRotateAngle(leftthing, -0.18203784098300857F, 0.0F, 0.0F);
		this.anotherthingontheright = new ModelRenderer(this, 42, 12);
		this.anotherthingontheright.setRotationPoint(0.0F, 0.0F, 3.0F);
		this.anotherthingontheright.addBox(-4.01F, 0.0F, 0.0F, 4, 2, 4, 0.0F);
		this.setRotateAngle(anotherthingontheright, -0.18203784098300857F, 0.0F, 0.0F);
		this.anotherthingontheleft = new ModelRenderer(this, 25, 12);
		this.anotherthingontheleft.setRotationPoint(0.0F, 0.0F, 3.0F);
		this.anotherthingontheleft.addBox(0.01F, 0.0F, 0.0F, 4, 2, 4, 0.0F);
		this.setRotateAngle(anotherthingontheleft, -0.18203784098300857F, 0.0F, 0.0F);
		this.top.addChild(this.thingleft);
		this.thingright.addChild(this.rightthing);
		this.top.addChild(this.thingright);
		this.top.addChild(this.decorations1);
		this.top.addChild(this.decorations2);
		this.thingleft.addChild(this.leftthing);
		this.rightthing.addChild(this.anotherthingontheright);
		this.leftthing.addChild(this.anotherthingontheleft);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		this.top.render(f5);
		this.base.render(f5);
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
