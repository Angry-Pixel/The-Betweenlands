package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLWeepingBlue - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelWeepingBlue extends ModelBase {
	public ModelRenderer stalk1;
	public ModelRenderer stalk2;
	public ModelRenderer stalkroots;
	public ModelRenderer stalk3;
	public ModelRenderer leaf7;
	public ModelRenderer stalk4;
	public ModelRenderer leaf6;
	public ModelRenderer leaf1;
	public ModelRenderer leaf2;
	public ModelRenderer leaf3;
	public ModelRenderer leaf4;
	public ModelRenderer leaf5;
	public ModelRenderer flower1a;
	public ModelRenderer flower2a;
	public ModelRenderer flower3;
	public ModelRenderer flower4;
	public ModelRenderer flower5;
	public ModelRenderer leaf1b;
	public ModelRenderer leaf2b;
	public ModelRenderer leaf3b;
	public ModelRenderer leaf4b;
	public ModelRenderer leaf5b;
	public ModelRenderer flower1b;
	public ModelRenderer flower1c;
	public ModelRenderer flower2b;
	public ModelRenderer flower2c;
	public ModelRenderer leaf6b;
	public ModelRenderer leaf7b;

	public ModelWeepingBlue() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.leaf3 = new ModelRenderer(this, -4, 20);
		this.leaf3.setRotationPoint(-0.5F, -6.0F, 0.5F);
		this.leaf3.addBox(-2.0F, 0.0F, -4.0F, 4, 0, 4, 0.0F);
		this.setRotateAngle(leaf3, -0.4553564018453205F, 2.41309222380736F, 0.0F);
		this.stalkroots = new ModelRenderer(this, 36, 0);
		this.stalkroots.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.stalkroots.addBox(-1.5F, -8.0F, -1.5F, 3, 8, 3, 0.0F);
		this.leaf5b = new ModelRenderer(this, 51, 22);
		this.leaf5b.setRotationPoint(0.0F, 0.0F, -5.0F);
		this.leaf5b.addBox(-2.5F, 0.0F, -4.0F, 5, 0, 4, 0.0F);
		this.setRotateAngle(leaf5b, 1.2747884856566583F, 0.0F, 0.0F);
		this.flower1b = new ModelRenderer(this, 49, 5);
		this.flower1b.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.flower1b.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
		this.leaf2 = new ModelRenderer(this, 21, 13);
		this.leaf2.setRotationPoint(-0.5F, -6.0F, 0.0F);
		this.leaf2.addBox(-2.5F, 0.0F, -5.0F, 5, 0, 5, 0.0F);
		this.setRotateAngle(leaf2, -0.36425021489121656F, 1.2292353921796064F, 0.0F);
		this.flower4 = new ModelRenderer(this, 47, 28);
		this.flower4.setRotationPoint(-0.3F, -5.5F, 0.8F);
		this.flower4.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 2, 0.0F);
		this.setRotateAngle(flower4, -2.1855012893472994F, -0.31869712141416456F, 0.0F);
		this.flower2c = new ModelRenderer(this, 58, 11);
		this.flower2c.setRotationPoint(0.0F, -0.1F, 0.0F);
		this.flower2c.addBox(0.0F, -3.0F, -0.5F, 1, 3, 1, 0.0F);
		this.leaf2b = new ModelRenderer(this, 33, 13);
		this.leaf2b.setRotationPoint(0.0F, 0.0F, -5.0F);
		this.leaf2b.addBox(-2.5F, 0.0F, -4.0F, 5, 0, 4, 0.0F);
		this.setRotateAngle(leaf2b, 1.3658946726107624F, 0.0F, 0.0F);
		this.flower1c = new ModelRenderer(this, 58, 0);
		this.flower1c.setRotationPoint(0.0F, -0.1F, 0.0F);
		this.flower1c.addBox(-0.5F, -3.0F, -0.5F, 1, 3, 1, 0.0F);
		this.stalk3 = new ModelRenderer(this, 18, 0);
		this.stalk3.setRotationPoint(0.0F, -7.9F, 0.0F);
		this.stalk3.addBox(-1.0F, -6.0F, -1.0F, 2, 6, 2, 0.0F);
		this.setRotateAngle(stalk3, 0.0F, 0.0F, -0.045553093477052F);
		this.leaf7b = new ModelRenderer(this, 24, 27);
		this.leaf7b.setRotationPoint(0.0F, 0.0F, -5.0F);
		this.leaf7b.addBox(-2.5F, 0.0F, -4.0F, 5, 0, 4, 0.0F);
		this.setRotateAngle(leaf7b, 1.2747884856566583F, 0.0F, 0.0F);
		this.leaf7 = new ModelRenderer(this, 13, 27);
		this.leaf7.setRotationPoint(0.5F, 0.0F, 0.0F);
		this.leaf7.addBox(-2.5F, 0.0F, -5.0F, 5, 0, 5, 0.0F);
		this.setRotateAngle(leaf7, -0.4553564018453205F, -1.6390387005478748F, 0.0F);
		this.stalk4 = new ModelRenderer(this, 27, 0);
		this.stalk4.setRotationPoint(0.0F, -5.9F, 0.0F);
		this.stalk4.addBox(-1.0F, -6.0F, -1.0F, 2, 6, 2, 0.0F);
		this.setRotateAngle(stalk4, 0.0F, 0.0F, -0.045553093477052F);
		this.flower5 = new ModelRenderer(this, 56, 28);
		this.flower5.setRotationPoint(-0.4F, -5.5F, -0.5F);
		this.flower5.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 2, 0.0F);
		this.setRotateAngle(flower5, 2.1855012893472994F, 0.9105382707654417F, 0.0F);
		this.flower1a = new ModelRenderer(this, 49, 0);
		this.flower1a.setRotationPoint(0.5F, -6.0F, -0.3F);
		this.flower1a.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 2, 0.0F);
		this.setRotateAngle(flower1a, 0.22759093446006054F, -0.045553093477052F, 0.31869712141416456F);
		this.leaf4b = new ModelRenderer(this, 27, 20);
		this.leaf4b.setRotationPoint(0.0F, 0.0F, -6.0F);
		this.leaf4b.addBox(-3.0F, 0.0F, -4.0F, 6, 0, 4, 0.0F);
		this.setRotateAngle(leaf4b, 1.0927506446736497F, 0.0F, 0.0F);
		this.flower3 = new ModelRenderer(this, 38, 28);
		this.flower3.setRotationPoint(0.8F, -5.5F, -0.3F);
		this.flower3.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 2, 0.0F);
		this.setRotateAngle(flower3, 2.0943951023931953F, -1.1383037381507017F, 0.0F);
		this.flower2b = new ModelRenderer(this, 49, 16);
		this.flower2b.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.flower2b.addBox(-1.0F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
		this.leaf6b = new ModelRenderer(this, 6, 27);
		this.leaf6b.setRotationPoint(0.0F, 0.0F, -4.0F);
		this.leaf6b.addBox(-2.0F, 0.0F, -3.0F, 4, 0, 3, 0.0F);
		this.setRotateAngle(leaf6b, 0.9105382707654417F, 0.0F, 0.0F);
		this.stalk1 = new ModelRenderer(this, 0, 0);
		this.stalk1.setRotationPoint(0.0F, 24.0F, 0.0F);
		this.stalk1.addBox(-1.0F, -8.0F, -1.0F, 2, 10, 2, 0.0F);
		this.leaf5 = new ModelRenderer(this, 39, 22);
		this.leaf5.setRotationPoint(0.5F, -6.0F, 0.0F);
		this.leaf5.addBox(-2.5F, 0.0F, -5.0F, 5, 0, 5, 0.0F);
		this.setRotateAngle(leaf5, -0.36425021489121656F, -1.3203415791337103F, 0.0F);
		this.stalk2 = new ModelRenderer(this, 9, 0);
		this.stalk2.setRotationPoint(0.0F, -7.9F, 0.0F);
		this.stalk2.addBox(-1.0F, -8.0F, -1.0F, 2, 8, 2, 0.0F);
		this.setRotateAngle(stalk2, 0.0F, 0.0F, 0.045553093477052F);
		this.leaf4 = new ModelRenderer(this, 12, 20);
		this.leaf4.setRotationPoint(0.0F, -6.0F, 0.5F);
		this.leaf4.addBox(-3.0F, 0.0F, -6.0F, 6, 0, 6, 0.0F);
		this.setRotateAngle(leaf4, -0.40980330836826856F, -2.777342438698576F, 0.0F);
		this.leaf6 = new ModelRenderer(this, -4, 27);
		this.leaf6.setRotationPoint(-0.5F, 0.0F, -0.5F);
		this.leaf6.addBox(-2.0F, 0.0F, -4.0F, 4, 0, 4, 0.0F);
		this.setRotateAngle(leaf6, -0.6829473363053812F, 0.7285004297824331F, 0.0F);
		this.leaf1b = new ModelRenderer(this, 9, 13);
		this.leaf1b.setRotationPoint(0.0F, 0.0F, -6.0F);
		this.leaf1b.addBox(-3.0F, 0.0F, -4.0F, 6, 0, 4, 0.0F);
		this.setRotateAngle(leaf1b, 0.9105382707654417F, 0.0F, 0.0F);
		this.leaf1 = new ModelRenderer(this, -6, 13);
		this.leaf1.setRotationPoint(0.0F, -6.0F, -0.5F);
		this.leaf1.addBox(-3.0F, 0.0F, -6.0F, 6, 0, 6, 0.0F);
		this.setRotateAngle(leaf1, -0.4553564018453205F, 0.091106186954104F, 0.0F);
		this.leaf3b = new ModelRenderer(this, 6, 20);
		this.leaf3b.setRotationPoint(0.0F, 0.0F, -4.0F);
		this.leaf3b.addBox(-2.0F, 0.0F, -3.0F, 4, 0, 3, 0.0F);
		this.setRotateAngle(leaf3b, 0.5918411493512771F, 0.0F, 0.0F);
		this.flower2a = new ModelRenderer(this, 49, 11);
		this.flower2a.setRotationPoint(-1.0F, -6.0F, 0.0F);
		this.flower2a.addBox(-0.5F, -2.0F, -1.0F, 2, 2, 2, 0.0F);
		this.setRotateAngle(flower2a, -0.5918411493512771F, 0.22759093446006054F, -0.5462880558742251F);
		this.stalk4.addChild(this.leaf3);
		this.stalk1.addChild(this.stalkroots);
		this.leaf5.addChild(this.leaf5b);
		this.flower1a.addChild(this.flower1b);
		this.stalk4.addChild(this.leaf2);
		this.stalk4.addChild(this.flower4);
		this.flower2a.addChild(this.flower2c);
		this.leaf2.addChild(this.leaf2b);
		this.flower1a.addChild(this.flower1c);
		this.stalk2.addChild(this.stalk3);
		this.leaf7.addChild(this.leaf7b);
		this.stalk2.addChild(this.leaf7);
		this.stalk3.addChild(this.stalk4);
		this.stalk4.addChild(this.flower5);
		this.stalk4.addChild(this.flower1a);
		this.leaf4.addChild(this.leaf4b);
		this.stalk4.addChild(this.flower3);
		this.flower2a.addChild(this.flower2b);
		this.leaf6.addChild(this.leaf6b);
		this.stalk4.addChild(this.leaf5);
		this.stalk1.addChild(this.stalk2);
		this.stalk4.addChild(this.leaf4);
		this.stalk3.addChild(this.leaf6);
		this.leaf1.addChild(this.leaf1b);
		this.stalk4.addChild(this.leaf1);
		this.leaf3.addChild(this.leaf3b);
		this.stalk4.addChild(this.flower2a);
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
