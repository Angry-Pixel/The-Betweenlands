package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLSundew - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelSundew extends ModelBase {
	public ModelRenderer stalk1;
	public ModelRenderer stalk2;
	public ModelRenderer stalk3;
	public ModelRenderer stalk4;
	public ModelRenderer flowerthing1;
	public ModelRenderer flowerthing2;
	public ModelRenderer hairs1;
	public ModelRenderer flowerthing3;
	public ModelRenderer hairs2;
	public ModelRenderer flowerthing4;
	public ModelRenderer hairs3;
	public ModelRenderer hairs4;

	public ModelSundew() {
		this.textureWidth = 80;
		this.textureHeight = 32;
		this.hairs3 = new ModelRenderer(this, 32, 16);
		this.hairs3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.hairs3.addBox(-4.0F, -3.5F, -4.5F, 8, 4, 5, 0.0F);
		this.hairs4 = new ModelRenderer(this, 30, 7);
		this.hairs4.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.hairs4.addBox(-3.5F, -3.5F, -3.5F, 7, 4, 4, 0.0F);
		this.stalk1 = new ModelRenderer(this, 0, 0);
		this.stalk1.setRotationPoint(0.0F, 24.0F, 0.0F);
		this.stalk1.addBox(-1.0F, -6.0F, -1.0F, 2, 7, 2, 0.0F);
		this.setRotateAngle(stalk1, 0.045553093477052F, 0.0F, 0.0F);
		this.stalk4 = new ModelRenderer(this, 9, 0);
		this.stalk4.setRotationPoint(0.0F, -6.0F, 2.0F);
		this.stalk4.addBox(-1.5F, -6.0F, -2.5F, 3, 6, 3, 0.0F);
		this.setRotateAngle(stalk4, 0.091106186954104F, 0.0F, 0.0F);
		this.flowerthing4 = new ModelRenderer(this, 30, 0);
		this.flowerthing4.setRotationPoint(0.01F, -3.0F, 0.0F);
		this.flowerthing4.addBox(-3.0F, -3.0F, -3.0F, 6, 3, 3, 0.0F);
		this.setRotateAngle(flowerthing4, 0.7285004297824331F, 0.0F, 0.0F);
		this.hairs1 = new ModelRenderer(this, 54, 10);
		this.hairs1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.hairs1.addBox(-3.5F, -3.5F, -2.0F, 7, 4, 4, 0.0F);
		this.hairs2 = new ModelRenderer(this, 49, 0);
		this.hairs2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.hairs2.addBox(-4.0F, -3.5F, -4.5F, 8, 4, 5, 0.0F);
		this.flowerthing3 = new ModelRenderer(this, 9, 25);
		this.flowerthing3.setRotationPoint(0.01F, -3.0F, 0.0F);
		this.flowerthing3.addBox(-3.5F, -3.0F, -4.0F, 7, 3, 4, 0.0F);
		this.setRotateAngle(flowerthing3, 0.36425021489121656F, 0.0F, 0.0F);
		this.stalk3 = new ModelRenderer(this, 0, 19);
		this.stalk3.setRotationPoint(0.0F, -6.0F, 0.0F);
		this.stalk3.addBox(-1.0F, -6.0F, 0.0F, 2, 6, 2, 0.0F);
		this.setRotateAngle(stalk3, -0.091106186954104F, 0.0F, 0.0F);
		this.flowerthing1 = new ModelRenderer(this, 9, 10);
		this.flowerthing1.setRotationPoint(0.0F, -5.7F, -1.0F);
		this.flowerthing1.addBox(-3.0F, -3.0F, -1.5F, 6, 3, 3, 0.0F);
		this.setRotateAngle(flowerthing1, 0.18203784098300857F, 0.0F, 0.0F);
		this.flowerthing2 = new ModelRenderer(this, 9, 17);
		this.flowerthing2.setRotationPoint(0.0F, -3.0F, 1.5F);
		this.flowerthing2.addBox(-3.5F, -3.0F, -4.0F, 7, 3, 4, 0.0F);
		this.setRotateAngle(flowerthing2, 0.5009094953223726F, 0.0F, 0.0F);
		this.stalk2 = new ModelRenderer(this, 0, 10);
		this.stalk2.setRotationPoint(0.0F, -6.0F, -1.0F);
		this.stalk2.addBox(-1.0F, -6.0F, 0.0F, 2, 6, 2, 0.0F);
		this.setRotateAngle(stalk2, -0.091106186954104F, 0.0F, 0.0F);
		this.flowerthing3.addChild(this.hairs3);
		this.flowerthing4.addChild(this.hairs4);
		this.stalk3.addChild(this.stalk4);
		this.flowerthing3.addChild(this.flowerthing4);
		this.flowerthing1.addChild(this.hairs1);
		this.flowerthing2.addChild(this.hairs2);
		this.flowerthing2.addChild(this.flowerthing3);
		this.stalk2.addChild(this.stalk3);
		this.stalk4.addChild(this.flowerthing1);
		this.flowerthing1.addChild(this.flowerthing2);
		this.stalk1.addChild(this.stalk2);
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
