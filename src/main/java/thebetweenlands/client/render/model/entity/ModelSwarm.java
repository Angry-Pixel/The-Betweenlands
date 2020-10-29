package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * BLInfestationHiveMaster2000 - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelSwarm extends ModelBase {
	public ModelRenderer base;
	public ModelRenderer butt;
	public ModelRenderer head;
	public ModelRenderer elytra_left;
	public ModelRenderer elytra_right;
	public ModelRenderer wing_left;
	public ModelRenderer wing_right;
	public ModelRenderer leg_left_f1a;
	public ModelRenderer leg_left_m1a;
	public ModelRenderer leg_left_b1a;
	public ModelRenderer leg_right_f1a;
	public ModelRenderer leg_right_m1a;
	public ModelRenderer leg_right_b1a;
	public ModelRenderer stingy_mcsting;
	public ModelRenderer stingerstonger_left;
	public ModelRenderer stingerstonger_right;
	public ModelRenderer mandible_left1a;
	public ModelRenderer mandible_right1a;
	public ModelRenderer antenna_left1a;
	public ModelRenderer antenna_right1a;
	public ModelRenderer mandible_left1b;
	public ModelRenderer mandible_left1c;
	public ModelRenderer mandible_right1b;
	public ModelRenderer mandible_right1c;
	public ModelRenderer antenna_left1b;
	public ModelRenderer antenna_left1c;
	public ModelRenderer antenna_right1b;
	public ModelRenderer antenna_right1c;
	public ModelRenderer leg_left_f1b;
	public ModelRenderer leg_left_m1b;
	public ModelRenderer leg_left_b1b;
	public ModelRenderer leg_right_f1b;
	public ModelRenderer leg_right_m1b;
	public ModelRenderer leg_right_b1b;

	public ModelSwarm() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.head = new ModelRenderer(this, 0, 10);
		this.head.setRotationPoint(0.01F, 0.0F, 0.0F);
		this.head.addBox(-1.0F, 0.0F, -2.0F, 2, 2, 2, 0.0F);
		this.setRotateAngle(head, 0.6373942428283291F, 0.0F, 0.0F);
		this.mandible_left1b = new ModelRenderer(this, 0, 20);
		this.mandible_left1b.setRotationPoint(1.0F, 0.0F, -2.0F);
		this.mandible_left1b.addBox(-1.0F, 0.0F, -2.0F, 1, 1, 2, 0.0F);
		this.setRotateAngle(mandible_left1b, 0.0F, 0.36425021489121656F, 0.0F);
		this.elytra_right = new ModelRenderer(this, 33, 0);
		this.elytra_right.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.elytra_right.addBox(-2.0F, 0.0F, 0.0F, 2, 2, 4, 0.0F);
		this.setRotateAngle(elytra_right, 0.0F, -0.40980330836826856F, 0.9105382707654417F);
		this.stingy_mcsting = new ModelRenderer(this, 10, 0);
		this.stingy_mcsting.setRotationPoint(0.0F, 2.0F, 2.0F);
		this.stingy_mcsting.addBox(-0.5F, -1.0F, 0.0F, 1, 1, 1, 0.0F);
		this.setRotateAngle(stingy_mcsting, 0.27314402793711257F, 0.0F, 0.0F);
		this.mandible_left1c = new ModelRenderer(this, 0, 24);
		this.mandible_left1c.setRotationPoint(0.0F, 1.0F, -2.0F);
		this.mandible_left1c.addBox(-1.0F, -1.0F, -1.0F, 1, 1, 1, 0.0F);
		this.setRotateAngle(mandible_left1c, -0.4553564018453205F, 0.0F, 0.0F);
		this.leg_left_f1b = new ModelRenderer(this, 20, 14);
		this.leg_left_f1b.setRotationPoint(0.0F, 2.0F, 0.0F);
		this.leg_left_f1b.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0, 0.0F);
		this.setRotateAngle(leg_left_f1b, -0.22759093446006054F, 0.0F, 0.0F);
		this.elytra_left = new ModelRenderer(this, 20, 0);
		this.elytra_left.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.elytra_left.addBox(0.0F, 0.0F, 0.0F, 2, 2, 4, 0.0F);
		this.setRotateAngle(elytra_left, 0.0F, 0.40980330836826856F, -0.9105382707654417F);
		this.antenna_left1b = new ModelRenderer(this, 1, 27);
		this.antenna_left1b.setRotationPoint(0.0F, 0.0F, -2.0F);
		this.antenna_left1b.addBox(0.0F, 0.0F, -2.0F, 2, 0, 2, 0.0F);
		this.setRotateAngle(antenna_left1b, 0.9105382707654417F, 0.0F, 0.0F);
		this.stingerstonger_left = new ModelRenderer(this, 10, 0);
		this.stingerstonger_left.setRotationPoint(0.3F, 0.0F, 1.0F);
		this.stingerstonger_left.addBox(0.0F, -1.0F, 0.0F, 0, 1, 3, 0.0F);
		this.setRotateAngle(stingerstonger_left, 0.31869712141416456F, 0.31869712141416456F, -0.18203784098300857F);
		this.leg_right_m1a = new ModelRenderer(this, 23, 16);
		this.leg_right_m1a.setRotationPoint(-0.5F, 2.0F, 1.0F);
		this.leg_right_m1a.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0, 0.0F);
		this.setRotateAngle(leg_right_m1a, 1.1838568316277536F, -1.0471975511965976F, -0.091106186954104F);
		this.wing_right = new ModelRenderer(this, 32, 7);
		this.wing_right.setRotationPoint(-0.5F, 0.0F, 0.0F);
		this.wing_right.addBox(-7.0F, 0.0F, 0.0F, 7, 0, 3, 0.0F);
		this.setRotateAngle(wing_right, 0.136659280431156F, -0.18203784098300857F, -0.091106186954104F);
		this.leg_left_f1a = new ModelRenderer(this, 20, 11);
		this.leg_left_f1a.setRotationPoint(0.5F, 2.0F, 0.5F);
		this.leg_left_f1a.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0, 0.0F);
		this.setRotateAngle(leg_left_f1a, 0.22759093446006054F, -0.7285004297824331F, -1.2292353921796064F);
		this.leg_right_f1b = new ModelRenderer(this, 23, 14);
		this.leg_right_f1b.setRotationPoint(0.0F, 2.0F, 0.0F);
		this.leg_right_f1b.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0, 0.0F);
		this.setRotateAngle(leg_right_f1b, -0.22759093446006054F, 0.0F, 0.0F);
		this.leg_right_m1b = new ModelRenderer(this, 23, 19);
		this.leg_right_m1b.setRotationPoint(0.0F, 2.0F, 0.0F);
		this.leg_right_m1b.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0, 0.0F);
		this.setRotateAngle(leg_right_m1b, 0.22759093446006054F, 0.0F, 0.0F);
		this.wing_left = new ModelRenderer(this, 17, 7);
		this.wing_left.setRotationPoint(0.5F, 0.0F, 0.0F);
		this.wing_left.addBox(0.0F, 0.0F, 0.0F, 7, 0, 3, 0.0F);
		this.setRotateAngle(wing_left, 0.136659280431156F, 0.18203784098300857F, 0.091106186954104F);
		this.butt = new ModelRenderer(this, 0, 5);
		this.butt.setRotationPoint(0.0F, 0.0F, 2.0F);
		this.butt.addBox(-1.01F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
		this.setRotateAngle(butt, -0.5009094953223726F, 0.0F, 0.0F);
		this.leg_right_b1b = new ModelRenderer(this, 23, 24);
		this.leg_right_b1b.setRotationPoint(0.0F, 2.0F, 0.0F);
		this.leg_right_b1b.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0, 0.0F);
		this.setRotateAngle(leg_right_b1b, 0.5918411493512771F, 0.0F, 0.0F);
		this.mandible_right1c = new ModelRenderer(this, 9, 24);
		this.mandible_right1c.setRotationPoint(0.0F, 1.0F, -2.0F);
		this.mandible_right1c.addBox(0.0F, -1.0F, -1.0F, 1, 1, 1, 0.0F);
		this.setRotateAngle(mandible_right1c, -0.4553564018453205F, 0.0F, 0.0F);
		this.leg_left_m1a = new ModelRenderer(this, 20, 16);
		this.leg_left_m1a.setRotationPoint(0.5F, 2.0F, 1.0F);
		this.leg_left_m1a.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0, 0.0F);
		this.setRotateAngle(leg_left_m1a, 1.1838568316277536F, 1.0471975511965976F, 0.091106186954104F);
		this.antenna_right1b = new ModelRenderer(this, 1, 30);
		this.antenna_right1b.setRotationPoint(0.0F, 0.0F, -2.0F);
		this.antenna_right1b.addBox(-2.0F, 0.0F, -2.0F, 2, 0, 2, 0.0F);
		this.setRotateAngle(antenna_right1b, 0.9105382707654417F, 0.0F, 0.0F);
		this.leg_right_b1a = new ModelRenderer(this, 23, 21);
		this.leg_right_b1a.setRotationPoint(-0.5F, 2.0F, 1.5F);
		this.leg_right_b1a.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0, 0.0F);
		this.setRotateAngle(leg_right_b1a, 0.8651597102135892F, -0.22759093446006054F, 0.0F);
		this.leg_right_f1a = new ModelRenderer(this, 23, 11);
		this.leg_right_f1a.setRotationPoint(-0.5F, 2.0F, 0.5F);
		this.leg_right_f1a.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0, 0.0F);
		this.setRotateAngle(leg_right_f1a, 0.22759093446006054F, 0.7285004297824331F, 1.2292353921796064F);
		this.mandible_right1a = new ModelRenderer(this, 9, 15);
		this.mandible_right1a.setRotationPoint(-0.25F, 1.0F, -1.0F);
		this.mandible_right1a.addBox(-1.0F, 0.0F, -2.0F, 1, 1, 3, 0.0F);
		this.setRotateAngle(mandible_right1a, 0.31869712141416456F, 0.36425021489121656F, -0.091106186954104F);
		this.antenna_left1a = new ModelRenderer(this, -2, 27);
		this.antenna_left1a.setRotationPoint(0.0F, 0.01F, -2.0F);
		this.antenna_left1a.addBox(0.0F, 0.0F, -2.0F, 1, 0, 2, 0.0F);
		this.setRotateAngle(antenna_left1a, 0.0F, -0.9105382707654417F, 0.0F);
		this.antenna_left1c = new ModelRenderer(this, 6, 27);
		this.antenna_left1c.setRotationPoint(0.0F, 0.0F, -2.0F);
		this.antenna_left1c.addBox(0.0F, 0.0F, -2.0F, 2, 0, 2, 0.0F);
		this.setRotateAngle(antenna_left1c, 0.9105382707654417F, 0.0F, 0.0F);
		this.leg_left_b1a = new ModelRenderer(this, 20, 21);
		this.leg_left_b1a.setRotationPoint(0.5F, 2.0F, 1.5F);
		this.leg_left_b1a.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0, 0.0F);
		this.setRotateAngle(leg_left_b1a, 0.8651597102135892F, 0.22759093446006054F, 0.0F);
		this.leg_left_b1b = new ModelRenderer(this, 20, 24);
		this.leg_left_b1b.setRotationPoint(0.0F, 2.0F, 0.0F);
		this.leg_left_b1b.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0, 0.0F);
		this.setRotateAngle(leg_left_b1b, 0.5918411493512771F, 0.0F, 0.0F);
		this.antenna_right1c = new ModelRenderer(this, 6, 30);
		this.antenna_right1c.setRotationPoint(0.0F, 0.0F, -2.0F);
		this.antenna_right1c.addBox(-2.0F, 0.0F, -2.0F, 2, 0, 2, 0.0F);
		this.setRotateAngle(antenna_right1c, 0.9105382707654417F, 0.0F, 0.0F);
		this.mandible_right1b = new ModelRenderer(this, 9, 20);
		this.mandible_right1b.setRotationPoint(-1.0F, 0.0F, -2.0F);
		this.mandible_right1b.addBox(0.0F, 0.0F, -2.0F, 1, 1, 2, 0.0F);
		this.setRotateAngle(mandible_right1b, 0.0F, -0.36425021489121656F, 0.0F);
		this.leg_left_m1b = new ModelRenderer(this, 20, 19);
		this.leg_left_m1b.setRotationPoint(0.0F, 2.0F, 0.0F);
		this.leg_left_m1b.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0, 0.0F);
		this.setRotateAngle(leg_left_m1b, 0.22759093446006054F, 0.0F, 0.0F);
		this.stingerstonger_right = new ModelRenderer(this, 10, 2);
		this.stingerstonger_right.setRotationPoint(-0.3F, 0.0F, 1.0F);
		this.stingerstonger_right.addBox(0.0F, -1.0F, 0.0F, 0, 1, 3, 0.0F);
		this.setRotateAngle(stingerstonger_right, 0.31869712141416456F, -0.31869712141416456F, 0.18203784098300857F);
		this.base = new ModelRenderer(this, 0, 0);
		this.base.setRotationPoint(0.0F, 18.0F, 0.0F);
		this.base.addBox(-1.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
		this.setRotateAngle(base, -0.6829473363053812F, 0.0F, 0.0F);
		this.mandible_left1a = new ModelRenderer(this, 0, 15);
		this.mandible_left1a.setRotationPoint(0.25F, 1.0F, -1.0F);
		this.mandible_left1a.addBox(0.0F, 0.0F, -2.0F, 1, 1, 3, 0.0F);
		this.setRotateAngle(mandible_left1a, 0.31869712141416456F, -0.36425021489121656F, 0.091106186954104F);
		this.antenna_right1a = new ModelRenderer(this, -2, 30);
		this.antenna_right1a.setRotationPoint(0.0F, 0.01F, -2.0F);
		this.antenna_right1a.addBox(-1.0F, 0.0F, -2.0F, 1, 0, 2, 0.0F);
		this.setRotateAngle(antenna_right1a, 0.0F, 0.9105382707654417F, 0.0F);
		this.base.addChild(this.head);
		this.mandible_left1a.addChild(this.mandible_left1b);
		this.base.addChild(this.elytra_right);
		this.butt.addChild(this.stingy_mcsting);
		this.mandible_left1b.addChild(this.mandible_left1c);
		this.leg_left_f1a.addChild(this.leg_left_f1b);
		this.base.addChild(this.elytra_left);
		this.antenna_left1a.addChild(this.antenna_left1b);
		this.stingy_mcsting.addChild(this.stingerstonger_left);
		this.base.addChild(this.leg_right_m1a);
		this.base.addChild(this.leg_left_f1a);
		this.leg_right_f1a.addChild(this.leg_right_f1b);
		this.leg_right_m1a.addChild(this.leg_right_m1b);
		this.base.addChild(this.butt);
		this.leg_right_b1a.addChild(this.leg_right_b1b);
		this.mandible_right1b.addChild(this.mandible_right1c);
		this.base.addChild(this.leg_left_m1a);
		this.antenna_right1a.addChild(this.antenna_right1b);
		this.base.addChild(this.leg_right_b1a);
		this.base.addChild(this.leg_right_f1a);
		this.head.addChild(this.mandible_right1a);
		this.head.addChild(this.antenna_left1a);
		this.antenna_left1b.addChild(this.antenna_left1c);
		this.base.addChild(this.leg_left_b1a);
		this.leg_left_b1a.addChild(this.leg_left_b1b);
		this.antenna_right1b.addChild(this.antenna_right1c);
		this.mandible_right1a.addChild(this.mandible_right1b);
		this.leg_left_m1a.addChild(this.leg_left_m1b);
		this.stingy_mcsting.addChild(this.stingerstonger_right);
		this.head.addChild(this.mandible_left1a);
		this.head.addChild(this.antenna_right1a);

		//Add wings last for transparency ordering
		this.base.addChild(this.wing_right);
		this.base.addChild(this.wing_left);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		this.base.render(f5);
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		float speed = 2.5f;

		float flap = MathHelper.sin(ageInTicks * speed);
		float flap2 = MathHelper.sin(ageInTicks * speed - 1.72f);

		this.wing_right.rotateAngleX = 0.136659280431156F + 0.05f + flap2 * 0.4f;
		this.wing_right.rotateAngleZ = -0.091106186954104F + flap * 0.2f;

		this.wing_left.rotateAngleX = 0.136659280431156F + 0.05f + flap2 * 0.4f;
		this.wing_left.rotateAngleZ = 0.091106186954104F - flap * 0.2f;

		float crunch = (float) Math.pow(MathHelper.sin(ageInTicks * speed * 0.19f), 12);

		this.mandible_left1a.rotateAngleY = -0.36425021489121656F + 0.1f + crunch * 0.15f;
		this.mandible_left1a.rotateAngleZ = 0.091106186954104F - 0.2f + crunch * 0.15f;

		this.mandible_right1a.rotateAngleY = 0.36425021489121656F - 0.1f - crunch * 0.15f;
		this.mandible_right1a.rotateAngleZ = 0.091106186954104F + 0.2f - crunch * 0.15f;

		float flop = MathHelper.sin(ageInTicks * speed * 0.12f);

		this.elytra_left.rotateAngleZ = -0.9105382707654417F + flop * 0.05f;
		this.elytra_right.rotateAngleZ = 0.9105382707654417F - flop * 0.05f;

		this.antenna_left1a.rotateAngleZ = flop * 0.05f;
		this.antenna_right1a.rotateAngleZ = -flop * 0.05f;

		this.leg_left_f1a.rotateAngleZ = -1.2292353921796064F - flop * 0.05f;
		this.leg_left_m1a.rotateAngleZ = 0.091106186954104F - flop * 0.05f;
		this.leg_left_b1a.rotateAngleX = 0.8651597102135892F + flop * 0.05f;

		this.leg_right_f1a.rotateAngleZ = 1.2292353921796064F + flop * 0.05f;
		this.leg_right_m1a.rotateAngleZ = 0.091106186954104F + flop * 0.05f;
		this.leg_right_b1a.rotateAngleX = 0.8651597102135892F + flop * 0.05f;
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
