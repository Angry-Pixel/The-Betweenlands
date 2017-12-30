package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLBucket - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelRubberTap extends ModelBase {
	public ModelRenderer bucketbase;
	public ModelRenderer tap1;
	public ModelRenderer side_r;
	public ModelRenderer side_l;
	public ModelRenderer side_f;
	public ModelRenderer side_b;
	public ModelRenderer handlepiece_r;
	public ModelRenderer rope2_a;
	public ModelRenderer rope1_a;
	public ModelRenderer handlepiece_l;
	public ModelRenderer rope2_b;
	public ModelRenderer rope1_b;
	public ModelRenderer rope1_c;
	public ModelRenderer rope2_c;
	public ModelRenderer tap1b;
	public ModelRenderer tap1c;

	public ModelRubberTap() {
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.rope1_c = new ModelRenderer(this, 53, 0);
		this.rope1_c.setRotationPoint(0.0F, 0.0F, 14.0F);
		this.rope1_c.addBox(-9.2F, -0.99F, -0.8F, 9, 1, 1, 0.0F);
		this.setRotateAngle(rope1_c, 0.0F, 0.091106186954104F, 0.0F);
		this.tap1b = new ModelRenderer(this, 17, 55);
		this.tap1b.setRotationPoint(0.5F, 0.0F, -2.5F);
		this.tap1b.addBox(0.0F, -1.0F, -2.5F, 1, 1, 6, 0.0F);
		this.setRotateAngle(tap1b, 0.0F, 0.0F, 0.136659280431156F);
		this.rope1_b = new ModelRenderer(this, 60, 0);
		this.rope1_b.setRotationPoint(1.0F, 0.0F, 2.0F);
		this.rope1_b.addBox(-1.0F, -1.0F, 0.0F, 1, 1, 14, 0.0F);
		this.setRotateAngle(rope1_b, 0.0F, -0.091106186954104F, 0.0F);
		this.handlepiece_r = new ModelRenderer(this, 16, 15);
		this.handlepiece_r.setRotationPoint(-1.0F, -12.0F, 0.0F);
		this.handlepiece_r.addBox(-1.0F, -2.0F, -2.0F, 2, 2, 4, 0.0F);
		this.side_r = new ModelRenderer(this, 0, 15);
		this.side_r.setRotationPoint(-4.0F, -2.0F, 0.0F);
		this.side_r.addBox(-2.0F, -12.0F, -6.0F, 2, 12, 12, 0.0F);
		this.handlepiece_l = new ModelRenderer(this, 29, 15);
		this.handlepiece_l.setRotationPoint(1.0F, -12.0F, 0.0F);
		this.handlepiece_l.addBox(-1.0F, -2.0F, -2.0F, 2, 2, 4, 0.0F);
		this.side_l = new ModelRenderer(this, 29, 15);
		this.side_l.setRotationPoint(4.0F, -2.0F, 0.0F);
		this.side_l.addBox(0.0F, -12.0F, -6.0F, 2, 12, 12, 0.0F);
		this.rope2_c = new ModelRenderer(this, 53, 3);
		this.rope2_c.setRotationPoint(0.0F, 0.0F, 9.0F);
		this.rope2_c.addBox(-9.3F, -1.0F, -0.8F, 9, 1, 1, 0.0F);
		this.setRotateAngle(rope2_c, 0.0F, 0.136659280431156F, 0.0F);
		this.side_b = new ModelRenderer(this, 21, 40);
		this.side_b.setRotationPoint(0.0F, -2.0F, 4.0F);
		this.side_b.addBox(-4.0F, -12.0F, 0.0F, 8, 12, 2, 0.0F);
		this.tap1 = new ModelRenderer(this, 0, 55);
		this.tap1.setRotationPoint(0.0F, 8.0F, 12.0F);
		this.tap1.addBox(-1.5F, 0.0F, -5.0F, 3, 1, 5, 0.0F);
		this.setRotateAngle(tap1, 0.091106186954104F, 0.0F, 0.0F);
		this.rope2_a = new ModelRenderer(this, 60, 43);
		this.rope2_a.setRotationPoint(-2.0F, -1.0F, 6.0F);
		this.rope2_a.addBox(0.0F, -1.0F, 0.0F, 1, 1, 9, 0.0F);
		this.setRotateAngle(rope2_a, 0.0F, 0.136659280431156F, 0.0F);
		this.bucketbase = new ModelRenderer(this, 0, 0);
		this.bucketbase.setRotationPoint(0.0F, 25.0F, 6.0F);
		this.bucketbase.addBox(-6.0F, -2.0F, -6.0F, 12, 2, 12, 0.0F);
		this.setRotateAngle(bucketbase, 0.091106186954104F, 0.0F, 0.0F);
		this.side_f = new ModelRenderer(this, 0, 40);
		this.side_f.setRotationPoint(0.0F, -2.0F, -2.0F);
		this.side_f.addBox(-4.0F, -12.0F, -4.0F, 8, 12, 2, 0.0F);
		this.tap1c = new ModelRenderer(this, 32, 55);
		this.tap1c.setRotationPoint(-0.5F, 0.0F, -2.5F);
		this.tap1c.addBox(-1.0F, -1.0F, -2.5F, 1, 1, 6, 0.0F);
		this.setRotateAngle(tap1c, 0.0F, 0.0F, -0.091106186954104F);
		this.rope2_b = new ModelRenderer(this, 60, 32);
		this.rope2_b.setRotationPoint(2.0F, -1.0F, 6.0F);
		this.rope2_b.addBox(-1.0F, -1.0F, 0.0F, 1, 1, 9, 0.0F);
		this.setRotateAngle(rope2_b, 0.0F, -0.136659280431156F, 0.0F);
		this.rope1_a = new ModelRenderer(this, 60, 16);
		this.rope1_a.setRotationPoint(-1.0F, 0.0F, 2.0F);
		this.rope1_a.addBox(0.0F, -1.0F, 0.0F, 1, 1, 14, 0.0F);
		this.setRotateAngle(rope1_a, 0.0F, 0.091106186954104F, 0.0F);
		this.rope1_b.addChild(this.rope1_c);
		this.tap1.addChild(this.tap1b);
		this.handlepiece_l.addChild(this.rope1_b);
		this.side_r.addChild(this.handlepiece_r);
		this.bucketbase.addChild(this.side_r);
		this.side_l.addChild(this.handlepiece_l);
		this.bucketbase.addChild(this.side_l);
		this.rope2_b.addChild(this.rope2_c);
		this.bucketbase.addChild(this.side_b);
		this.side_r.addChild(this.rope2_a);
		this.bucketbase.addChild(this.side_f);
		this.tap1.addChild(this.tap1c);
		this.side_l.addChild(this.rope2_b);
		this.handlepiece_r.addChild(this.rope1_a);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		this.tap1.render(f5);
		this.bucketbase.render(f5);
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
