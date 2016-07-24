package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLTubeplant - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelPitcherPlant extends ModelBase {
	public ModelRenderer plantbase1;
	public ModelRenderer plantbase2;
	public ModelRenderer plantbase3;
	public ModelRenderer sprout1;
	public ModelRenderer sprout2;
	public ModelRenderer leaf1;
	public ModelRenderer leaf2;
	public ModelRenderer leaf3;
	public ModelRenderer leaf4;
	public ModelRenderer leaf5;
	public ModelRenderer tube1;
	public ModelRenderer top1;
	public ModelRenderer lure1;
	public ModelRenderer trapleaf1;
	public ModelRenderer tube2;
	public ModelRenderer top2;
	public ModelRenderer lure2;
	public ModelRenderer trapleaf2;
	public ModelRenderer tube3;
	public ModelRenderer top3;
	public ModelRenderer lure3;
	public ModelRenderer trapleaf3;
	public ModelRenderer sproutleaf1;
	public ModelRenderer sproutleaf2;
	public ModelRenderer leaf1b;
	public ModelRenderer leaf2b;
	public ModelRenderer leaf3b;
	public ModelRenderer leaf4b;
	public ModelRenderer leaf5b;

	public ModelPitcherPlant() {
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.top2 = new ModelRenderer(this, 15, 32);
		this.top2.setRotationPoint(0.0F, -16.9F, 0.0F);
		this.top2.addBox(-1.5F, -4.0F, -1.5F, 3, 4, 3, 0.0F);
		this.setRotateAngle(top2, 0.045553093477052F, 0.0F, 0.0F);
		this.tube3 = new ModelRenderer(this, 30, 9);
		this.tube3.setRotationPoint(0.0F, -4.0F, 0.0F);
		this.tube3.addBox(-1.5F, -11.0F, -1.5F, 3, 12, 3, 0.0F);
		this.setRotateAngle(tube3, -0.18203784098300857F, 0.0F, 0.0F);
		this.trapleaf3 = new ModelRenderer(this, 30, 33);
		this.trapleaf3.setRotationPoint(0.0F, -4.0F, 1.5F);
		this.trapleaf3.addBox(-2.0F, 0.0F, -5.0F, 4, 0, 5, 0.0F);
		this.setRotateAngle(trapleaf3, -0.091106186954104F, 0.0F, 0.0F);
		this.leaf5b = new ModelRenderer(this, 60, 48);
		this.leaf5b.setRotationPoint(0.0F, 0.0F, -4.0F);
		this.leaf5b.addBox(-2.0F, 0.0F, -3.0F, 4, 0, 3, 0.0F);
		this.setRotateAngle(leaf5b, 0.6373942428283291F, 0.0F, 0.0F);
		this.plantbase1 = new ModelRenderer(this, 0, 0);
		this.plantbase1.setRotationPoint(-3.0F, 24.0F, -3.0F);
		this.plantbase1.addBox(-1.0F, -4.0F, -1.0F, 2, 6, 2, 0.0F);
		this.setRotateAngle(plantbase1, 0.27314402793711257F, 0.5918411493512771F, 0.0F);
		this.tube2 = new ModelRenderer(this, 15, 10);
		this.tube2.setRotationPoint(0.0F, -5.0F, 0.0F);
		this.tube2.addBox(-1.5F, -17.0F, -1.5F, 3, 18, 3, 0.0F);
		this.setRotateAngle(tube2, 0.045553093477052F, 0.0F, 0.0F);
		this.lure1 = new ModelRenderer(this, 0, 39);
		this.lure1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.lure1.addBox(-0.5F, -5.0F, -0.5F, 1, 5, 1, 0.0F);
		this.setRotateAngle(lure1, 0.045553093477052F, 0.0F, 0.0F);
		this.leaf4 = new ModelRenderer(this, 60, 32);
		this.leaf4.setRotationPoint(-1.5F, 24.0F, 2.0F);
		this.leaf4.addBox(-2.5F, 0.0F, -5.0F, 5, 0, 5, 0.0F);
		this.setRotateAngle(leaf4, -0.36425021489121656F, 2.0943951023931953F, 0.0F);
		this.lure2 = new ModelRenderer(this, 15, 40);
		this.lure2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.lure2.addBox(-0.5F, -5.0F, -0.5F, 1, 5, 1, 0.0F);
		this.setRotateAngle(lure2, -0.045553093477052F, 0.0F, 0.0F);
		this.leaf4b = new ModelRenderer(this, 60, 38);
		this.leaf4b.setRotationPoint(0.0F, 0.0F, -5.0F);
		this.leaf4b.addBox(-2.5F, 0.0F, -4.0F, 5, 0, 4, 0.0F);
		this.setRotateAngle(leaf4b, 0.6829473363053812F, 0.0F, 0.0F);
		this.top3 = new ModelRenderer(this, 30, 25);
		this.top3.setRotationPoint(0.0F, -10.8F, 0.0F);
		this.top3.addBox(-1.5F, -4.0F, -1.5F, 3, 4, 3, 0.0F);
		this.setRotateAngle(top3, -0.091106186954104F, 0.0F, 0.0F);
		this.leaf5 = new ModelRenderer(this, 60, 43);
		this.leaf5.setRotationPoint(-1.5F, 23.0F, 2.0F);
		this.leaf5.addBox(-2.0F, 0.0F, -4.0F, 4, 0, 4, 0.0F);
		this.setRotateAngle(leaf5, -0.5009094953223726F, -3.0049333731586367F, 0.0F);
		this.leaf1 = new ModelRenderer(this, 60, 0);
		this.leaf1.setRotationPoint(-3.0F, 24.0F, -3.0F);
		this.leaf1.addBox(-3.0F, 0.0F, -6.0F, 6, 0, 6, 0.0F);
		this.setRotateAngle(leaf1, -0.4553564018453205F, 0.22759093446006054F, 0.0F);
		this.plantbase3 = new ModelRenderer(this, 30, 0);
		this.plantbase3.setRotationPoint(3.0F, 24.0F, -1.0F);
		this.plantbase3.addBox(-1.0F, -4.0F, -1.0F, 2, 6, 2, 0.0F);
		this.setRotateAngle(plantbase3, 0.27314402793711257F, -1.3658946726107624F, 0.0F);
		this.sprout1 = new ModelRenderer(this, 45, 0);
		this.sprout1.setRotationPoint(2.0F, 24.0F, 3.0F);
		this.sprout1.addBox(-1.0F, -3.0F, -1.0F, 2, 5, 2, 0.0F);
		this.setRotateAngle(sprout1, -0.091106186954104F, 0.5009094953223726F, 0.0F);
		this.top1 = new ModelRenderer(this, 0, 31);
		this.top1.setRotationPoint(0.0F, -14.8F, 0.0F);
		this.top1.addBox(-1.5F, -4.0F, -1.5F, 3, 4, 3, 0.0F);
		this.setRotateAngle(top1, -0.091106186954104F, 0.0F, 0.0F);
		this.lure3 = new ModelRenderer(this, 30, 33);
		this.lure3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.lure3.addBox(-0.5F, -4.0F, -0.5F, 1, 4, 1, 0.0F);
		this.setRotateAngle(lure3, 0.045553093477052F, 0.0F, 0.0F);
		this.leaf2b = new ModelRenderer(this, 60, 17);
		this.leaf2b.setRotationPoint(0.0F, 0.0F, -4.0F);
		this.leaf2b.addBox(-2.0F, 0.0F, -3.0F, 4, 0, 3, 0.0F);
		this.setRotateAngle(leaf2b, 0.5462880558742251F, 0.0F, 0.0F);
		this.plantbase2 = new ModelRenderer(this, 15, 0);
		this.plantbase2.setRotationPoint(-1.5F, 24.0F, 2.0F);
		this.plantbase2.addBox(-1.0F, -5.0F, -1.0F, 2, 7, 2, 0.0F);
		this.setRotateAngle(plantbase2, -0.091106186954104F, -0.36425021489121656F, 0.0F);
		this.sprout2 = new ModelRenderer(this, 45, 13);
		this.sprout2.setRotationPoint(1.0F, 24.0F, -4.0F);
		this.sprout2.addBox(-1.0F, -2.0F, -1.0F, 2, 4, 2, 0.0F);
		this.setRotateAngle(sprout2, 0.18203784098300857F, -0.36425021489121656F, 0.0F);
		this.leaf1b = new ModelRenderer(this, 60, 7);
		this.leaf1b.setRotationPoint(0.0F, 0.0F, -6.0F);
		this.leaf1b.addBox(-3.0F, 0.0F, -4.0F, 6, 0, 4, 0.0F);
		this.setRotateAngle(leaf1b, 1.0471975511965976F, 0.0F, 0.0F);
		this.leaf2 = new ModelRenderer(this, 60, 12);
		this.leaf2.setRotationPoint(-3.0F, 22.5F, -3.0F);
		this.leaf2.addBox(-2.0F, 0.0F, -4.0F, 4, 0, 4, 0.0F);
		this.setRotateAngle(leaf2, -0.31869712141416456F, 1.2292353921796064F, 0.0F);
		this.leaf3 = new ModelRenderer(this, 60, 21);
		this.leaf3.setRotationPoint(3.0F, 23.0F, -1.0F);
		this.leaf3.addBox(-2.5F, 0.0F, -5.0F, 5, 0, 5, 0.0F);
		this.setRotateAngle(leaf3, -0.5009094953223726F, -0.9105382707654417F, 0.0F);
		this.sproutleaf2 = new ModelRenderer(this, 45, 20);
		this.sproutleaf2.setRotationPoint(0.0F, -2.0F, 1.0F);
		this.sproutleaf2.addBox(-1.5F, 0.0F, -4.0F, 3, 0, 4, 0.0F);
		this.setRotateAngle(sproutleaf2, -0.8651597102135892F, 0.0F, 0.0F);
		this.trapleaf2 = new ModelRenderer(this, 15, 40);
		this.trapleaf2.setRotationPoint(0.0F, -4.0F, -1.5F);
		this.trapleaf2.addBox(-2.0F, 0.0F, 0.0F, 4, 0, 5, 0.0F);
		this.setRotateAngle(trapleaf2, 1.0927506446736497F, 0.0F, 0.0F);
		this.sproutleaf1 = new ModelRenderer(this, 45, 8);
		this.sproutleaf1.setRotationPoint(0.0F, -3.0F, -1.0F);
		this.sproutleaf1.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 4, 0.0F);
		this.setRotateAngle(sproutleaf1, 0.6373942428283291F, 0.0F, 0.0F);
		this.trapleaf1 = new ModelRenderer(this, 0, 39);
		this.trapleaf1.setRotationPoint(0.0F, -4.0F, 1.5F);
		this.trapleaf1.addBox(-2.0F, 0.0F, -5.0F, 4, 0, 5, 0.0F);
		this.setRotateAngle(trapleaf1, -1.0471975511965976F, 0.0F, 0.0F);
		this.leaf3b = new ModelRenderer(this, 60, 27);
		this.leaf3b.setRotationPoint(0.0F, 0.0F, -5.0F);
		this.leaf3b.addBox(-2.5F, 0.0F, -4.0F, 5, 0, 4, 0.0F);
		this.setRotateAngle(leaf3b, 0.5009094953223726F, 0.0F, 0.0F);
		this.tube1 = new ModelRenderer(this, 0, 11);
		this.tube1.setRotationPoint(0.0F, -4.0F, 0.0F);
		this.tube1.addBox(-1.5F, -15.0F, -1.5F, 3, 16, 3, 0.0F);
		this.setRotateAngle(tube1, -0.18203784098300857F, 0.0F, 0.0F);
		this.tube2.addChild(this.top2);
		this.plantbase3.addChild(this.tube3);
		this.top3.addChild(this.trapleaf3);
		this.leaf5.addChild(this.leaf5b);
		this.plantbase2.addChild(this.tube2);
		this.top1.addChild(this.lure1);
		this.top2.addChild(this.lure2);
		this.leaf4.addChild(this.leaf4b);
		this.tube3.addChild(this.top3);
		this.tube1.addChild(this.top1);
		this.top3.addChild(this.lure3);
		this.leaf2.addChild(this.leaf2b);
		this.leaf1.addChild(this.leaf1b);
		this.sprout2.addChild(this.sproutleaf2);
		this.top2.addChild(this.trapleaf2);
		this.sprout1.addChild(this.sproutleaf1);
		this.top1.addChild(this.trapleaf1);
		this.leaf3.addChild(this.leaf3b);
		this.plantbase1.addChild(this.tube1);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		this.plantbase1.render(f5);
		this.leaf4.render(f5);
		this.leaf5.render(f5);
		this.leaf1.render(f5);
		this.plantbase3.render(f5);
		this.sprout1.render(f5);
		this.plantbase2.render(f5);
		this.sprout2.render(f5);
		this.leaf2.render(f5);
		this.leaf3.render(f5);
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
