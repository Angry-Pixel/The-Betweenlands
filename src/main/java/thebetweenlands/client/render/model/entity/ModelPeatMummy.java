package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;

public class ModelPeatMummy extends MowzieModelBase {
	public MowzieModelRenderer body_base;
	public MowzieModelRenderer buttJoint;
	public MowzieModelRenderer shoulderJoint;
	public MowzieModelRenderer sexybutt;
	public MowzieModelRenderer legleftJoint;
	public MowzieModelRenderer legrightJoint;
	public MowzieModelRenderer legleft;
	public MowzieModelRenderer legleft2;
	public MowzieModelRenderer legright;
	public MowzieModelRenderer legright2;
	public MowzieModelRenderer shoulderBase;
	public MowzieModelRenderer shoulderright;
	public MowzieModelRenderer shoulderleft;
	public MowzieModelRenderer neck;
	public MowzieModelRenderer armrightJoint;
	public MowzieModelRenderer armleftJoint;
	public MowzieModelRenderer head1;
	public MowzieModelRenderer head2;
	public MowzieModelRenderer jaw;
	public MowzieModelRenderer teeth2;
	public MowzieModelRenderer hair;
	public MowzieModelRenderer teeth1;
	public MowzieModelRenderer cheecktissueright;
	public MowzieModelRenderer cheecktissue2;
	public MowzieModelRenderer armright;
	public MowzieModelRenderer armright2;
	public MowzieModelRenderer armleft;
	public MowzieModelRenderer armleft2;
	public MowzieModelRenderer modelBase;

	public ModelPeatMummy() {
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.modelBase = new MowzieModelRenderer(this, 0, 0);
		this.teeth1 = new MowzieModelRenderer(this, 82, 30);
		this.teeth1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.teeth1.addBox(-2.5F, -2.0F, -7.7F, 5, 1, 6, 0.0F);
		this.legright = new MowzieModelRenderer(this, 90, 12);
		this.legright.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.legright.addBox(-2.0F, -1.0F, -1.0F, 2, 9, 2, 0.0F);
		this.setRotateAngle(legright, -1.1838568316277536F, 1.1383037381507017F, -0.091106186954104F);
		this.armleft2 = new MowzieModelRenderer(this, 28, 32);
		this.armleft2.setRotationPoint(0.0F, 9.0F, 0.0F);
		this.armleft2.addBox(0.0F, 0.0F, -1.5F, 2, 10, 2, 0.0F);
		this.setRotateAngle(armleft2, -0.5918411493512771F, 0.0F, 0.0F);
		this.head2 = new MowzieModelRenderer(this, 55, 23);
		this.head2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.head2.addBox(-3.5F, 0.0F, -3.0F, 7, 3, 3, 0.0F);
		this.armleft = new MowzieModelRenderer(this, 19, 32);
		this.armleft.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.armleft.addBox(0.0F, -1.0F, -1.5F, 2, 10, 2, 0.0F);
		this.setRotateAngle(armleft, 0.13735741213195374F, 0.7311184236604247F, -0.4316199240181977F);
		this.neck = new MowzieModelRenderer(this, 55, 0);
		this.neck.setRotationPoint(0.0F, -1.5F, -5.02F);
		this.neck.addBox(-1.5F, -3.8F, -1.5F, 3, 4, 3, 0.0F);
		this.setRotateAngle(neck, 0.9105382707654417F, 0.0F, 0.0F);
		this.legleft = new MowzieModelRenderer(this, 108, 12);
		this.legleft.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.legleft.addBox(0.0F, -1.0F, -1.0F, 2, 9, 2, 0.0F);
		this.setRotateAngle(legleft, -1.1838568316277536F, -1.1383037381507017F, 0.091106186954104F);
		this.jaw = new MowzieModelRenderer(this, 55, 30);
		this.jaw.setRotationPoint(0.0F, 1.1F, 0.0F);
		this.jaw.addBox(-3.0F, -1.0F, -8.0F, 6, 2, 7, 0.0F);
		this.setRotateAngle(jaw, 0.5918411493512771F, 0.0F, 0.18203784098300857F);
		this.teeth2 = new MowzieModelRenderer(this, 82, 44);
		this.teeth2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.teeth2.addBox(-3.0F, 0.0F, -7.8F, 6, 1, 5, 0.0F);
		this.shoulderBase = new MowzieModelRenderer(this, 0, 0);
		this.shoulderBase.setRotationPoint(0.0F, 0.0F, -4.440892098500626E-16F);
		this.shoulderBase.addBox(0.0F, -7.5F, 2.5000000000000004F, 0, 0, 0, 0.0F);
		this.head1 = new MowzieModelRenderer(this, 55, 9);
		this.head1.setRotationPoint(0.0F, -2.6F, 0.0F);
		this.head1.addBox(-4.0F, -5.0F, -8.0F, 8, 5, 8, 0.0F);
		this.setRotateAngle(head1, -0.9105382707654417F, 0.0F, 0.0F);
		this.legrightJoint = new MowzieModelRenderer(this, 0, 0);
		this.legrightJoint.setRotationPoint(-4.0F, 3.5F, 1.5F);
		this.legrightJoint.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		this.body_base = new MowzieModelRenderer(this, 0, 0);
		this.body_base.setRotationPoint(0.0F, 10.0F, 6.0F);
		this.body_base.addBox(-4.0F, -9.68799815159352F, -4.678445443159999F, 8, 10, 6, 0.0F);
		this.setRotateAngle(body_base, 1.3203415791337103F, 0.0F, 0.0F);
		this.shoulderJoint = new MowzieModelRenderer(this, 0, 0);
		this.shoulderJoint.setRotationPoint(0.0F, -9.026879981515934F, 0.2252155455684F);
		this.shoulderJoint.addBox(0.0F, -7.5F, 2.5F, 0, 0, 0, 0.0F);
		this.setRotateAngle(shoulderJoint, -1.3203415791337103F, 0.0F, 0.0F);
		this.armleftJoint = new MowzieModelRenderer(this, 0, 0);
		this.armleftJoint.setRotationPoint(4.0F, -0.13F, -2.89F);
		this.armleftJoint.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		this.shoulderleft = new MowzieModelRenderer(this, 25, 17);
		this.shoulderleft.setRotationPoint(0.0F, 2.5F, -1.5000000000000004F);
		this.shoulderleft.addBox(-0.2F, -5.0F, -3.0F, 5, 6, 7, 0.0F);
		this.setRotateAngle(shoulderleft, 1.230231460770601F, -0.02255085846982904F, -0.08827863759542955F);
		this.shoulderright = new MowzieModelRenderer(this, 0, 17);
		this.shoulderright.setRotationPoint(0.0F, 2.5F, -1.5000000000000004F);
		this.shoulderright.addBox(-4.8F, -5.0F, -3.0F, 5, 6, 7, 0.0F);
		this.setRotateAngle(shoulderright, 1.230231460770601F, 0.02255085846982904F, 0.08827863759542955F);
		this.buttJoint = new MowzieModelRenderer(this, 0, 0);
		this.buttJoint.setRotationPoint(0.0F, 0.5420018484064801F, 1.5215545568400013F);
		this.buttJoint.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		this.setRotateAngle(buttJoint, -1.3203415791337103F, 0.0F, 0.0F);
		this.cheecktissueright = new MowzieModelRenderer(this, 82, 34);
		this.cheecktissueright.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.cheecktissueright.addBox(-2.4F, -4.3F, -7.0F, 0, 5, 4, 0.0F);
		this.setRotateAngle(cheecktissueright, -0.27314402793711257F, 0.0F, -0.27314402793711257F);
		this.cheecktissue2 = new MowzieModelRenderer(this, 92, 34);
		this.cheecktissue2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.cheecktissue2.addBox(2.8F, -4.9F, -6.0F, 0, 5, 4, 0.0F);
		this.setRotateAngle(cheecktissue2, -0.18203784098300857F, 0.0F, 0.091106186954104F);
		this.legright2 = new MowzieModelRenderer(this, 99, 12);
		this.legright2.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.legright2.addBox(-2.0F, 0.0F, -1.0F, 2, 10, 2, 0.0F);
		this.setRotateAngle(legright2, 1.2292353921796064F, 0.0F, 0.0F);
		this.hair = new MowzieModelRenderer(this, 40, 42);
		this.hair.setRotationPoint(0.0F, -0.0F, 0.0F);
		this.hair.addBox(-4.5F, -5.1F, -8.5F, 9, 12, 9, 0.0F);
		this.armright2 = new MowzieModelRenderer(this, 9, 32);
		this.armright2.setRotationPoint(0.0F, 9.0F, 0.0F);
		this.armright2.addBox(-2.0F, 0.0F, -1.5F, 2, 10, 2, 0.0F);
		this.setRotateAngle(armright2, -0.5918411493512771F, 0.0F, 0.0F);
		this.sexybutt = new MowzieModelRenderer(this, 90, 0);
		this.sexybutt.setRotationPoint(0.0F, 3.9F, -2.0F);
		this.sexybutt.addBox(-4.5F, -1.4F, -2.0F, 9, 5, 6, 0.0F);
		this.setRotateAngle(sexybutt, 0.7740535232594852F, 0.0F, 0.0F);
		this.legleftJoint = new MowzieModelRenderer(this, 0, 0);
		this.legleftJoint.setRotationPoint(4.0F, 3.5F, 1.5F);
		this.legleftJoint.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		this.armright = new MowzieModelRenderer(this, 0, 32);
		this.armright.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.armright.addBox(-2.0F, -1.0F, -1.5F, 2, 10, 2, 0.0F);
		this.setRotateAngle(armright, 0.13735741213195374F, -0.7311184236604247F, 0.4316199240181977F);
		this.legleft2 = new MowzieModelRenderer(this, 117, 12);
		this.legleft2.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.legleft2.addBox(0.0F, 0.0F, -1.0F, 2, 10, 2, 0.0F);
		this.setRotateAngle(legleft2, 1.2292353921796064F, 0.0F, 0.0F);
		this.armrightJoint = new MowzieModelRenderer(this, 0, 0);
		this.armrightJoint.setRotationPoint(-4.0F, -0.13F, -2.89F);
		this.armrightJoint.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		this.jaw.addChild(this.teeth1);
		this.legrightJoint.addChild(this.legright);
		this.armleft.addChild(this.armleft2);
		this.head1.addChild(this.head2);
		this.armleftJoint.addChild(this.armleft);
		this.shoulderBase.addChild(this.neck);
		this.legleftJoint.addChild(this.legleft);
		this.head1.addChild(this.jaw);
		this.head1.addChild(this.teeth2);
		this.shoulderJoint.addChild(this.shoulderBase);
		this.neck.addChild(this.head1);
		this.sexybutt.addChild(this.legrightJoint);
		this.body_base.addChild(this.shoulderJoint);
		this.shoulderBase.addChild(this.armleftJoint);
		this.shoulderBase.addChild(this.shoulderleft);
		this.shoulderBase.addChild(this.shoulderright);
		this.body_base.addChild(this.buttJoint);
		this.jaw.addChild(this.cheecktissueright);
		this.jaw.addChild(this.cheecktissue2);
		this.legright.addChild(this.legright2);
		this.head1.addChild(this.hair);
		this.armright.addChild(this.armright2);
		this.buttJoint.addChild(this.sexybutt);
		this.sexybutt.addChild(this.legleftJoint);
		this.armrightJoint.addChild(this.armright);
		this.legleft.addChild(this.legleft2);
		this.shoulderBase.addChild(this.armrightJoint);
		modelBase.addChild(body_base);

		parts = new MowzieModelRenderer[] {body_base, buttJoint, shoulderJoint, sexybutt, legright, legleft, legright2, legleft2, shoulderBase, shoulderright, shoulderleft, neck, armright, armleft, head1, head2, jaw, teeth2, hair, teeth1, cheecktissueright, cheecktissue2, armright2, armleft2, legleftJoint, legrightJoint, armleftJoint, armrightJoint, modelBase};
		setInitPose();
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		setRotationAngles(entity, f, f1, f2, f3, f4, f5);
		this.modelBase.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	 public void setRotateAngle(ModelRenderer ModelRenderer, float x, float y, float z) {
		ModelRenderer.rotateAngleX = x;
		ModelRenderer.rotateAngleY = y;
		ModelRenderer.rotateAngleZ = z;
	}

	public void setRotationAngles(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		//        f = entity.ticksExisted;
		//        f1 = 1f;

		faceTarget(neck, 1, f3, f4);

		float newf1 = f1;
		if (newf1 > 0.4) newf1 = 0.4f;
		float newf12 = f1;
		if (newf12 > 0.7) newf12 = 0.7f;

		float globalDegree = 1.5f;
		float wiggleDegree = 1.5f;
		float globalSpeed = 0.6f;
		float globalHeight = 1.5f;

		body_base.rotationPointX -= wiggleDegree * globalDegree * newf1 * 3f * Math.cos(globalSpeed * f);
		swing(sexybutt, globalSpeed, 0.2f * globalDegree * wiggleDegree, true, -1.6f, 0, f, newf1);
		swing(body_base, globalSpeed, 0.3f * globalDegree * wiggleDegree, true, -0.8f, 0, f, newf1);
		swing(shoulderBase, globalSpeed, 0.4f * globalDegree * wiggleDegree, true, 0, 0, f, newf1);
		swing(neck, globalSpeed, 0.6f * globalDegree * wiggleDegree, false, -0.5f, 0, f, newf1);

		walk(body_base, 2 * globalSpeed, 0.1f * globalHeight, true, -1.5f, 0.1f, f, f1);
		walk(neck, 2 * globalSpeed, 0.1f * globalHeight, false, -1f, -0.1f, f, f1);
		walk(jaw, 2 * globalSpeed, 0.1f * globalHeight, false, -0.7f, -0.1f, f, f1);
		bob(body_base, 2 * globalSpeed, 0.5f * globalHeight, false, f, f1);

		flap(legleftJoint, 1 * globalSpeed, 0.3f * globalDegree, false, 0- 0.8f, -0.3f, f, newf12);
		walk(legleftJoint, 1 * globalSpeed, 0.3f * globalDegree, false, 0- 0.8f, -0.5f, f, newf12);
		walk(legleft2, 1 * globalSpeed, 0.3f * globalDegree, false, -1.5f - 0.8f, 0.3f, f, newf12);
		swing(legleft2, 1 * globalSpeed, 0.3f * globalDegree, false, -1.5f - 0.8f, 0.3f, f, newf12);

		flap(legrightJoint, 1 * globalSpeed, 0.3f * globalDegree, false, 0 - 0.8f, 0.3f, f, newf12);
		walk(legrightJoint, 1 * globalSpeed, 0.3f * globalDegree, true, 0- 0.8f, -0.5f, f, newf12);
		walk(legright2, 1 * globalSpeed, 0.3f * globalDegree, true, -1.5f- 0.8f, 0.3f, f, newf12);
		swing(legright2, 1 * globalSpeed, 0.3f * globalDegree, false, -1.5f- 0.8f, -0.3f, f, newf12);

		walk(armleftJoint, 1 * globalSpeed, 0.5f * globalDegree, true, -1.6f - 0.4f, 0.3f, f, newf12);
		walk(armleft2, 1 * globalSpeed, 0.3f * globalDegree, true, -0.1f - 0.4f, -0.4f, f, newf12);
		swing(armleft2, 1 * globalSpeed, 0.3f * globalDegree, true, -0.1f - 0.4f, -0.4f, f, newf12);

		walk(armrightJoint, 1 * globalSpeed, 0.5f * globalDegree, false, -1.6f - 0.4f, 0.3f, f, newf12);
		walk(armright2, 1 * globalSpeed, 0.3f * globalDegree, false, -0.1f - 0.4f, -0.4f, f, newf12);
		swing(armright2, 1 * globalSpeed, 0.3f * globalDegree, true, -0.1f - 0.4f, 0.4f, f, newf12);
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float yaw, float pitch, float delta) {
		setToInitPose();
		
		EntityPeatMummy mummy = entity instanceof EntityPeatMummy ? (EntityPeatMummy) entity : null;
		
		float spawningProgress = mummy != null ? mummy.getInterpolatedSpawningProgress(delta) : 1;
		body_base.rotateAngleX -= 1 - spawningProgress;
		armleftJoint.rotateAngleX -= 1.5 * (1 - spawningProgress);
		armrightJoint.rotateAngleX -= 1.5 * (1 - spawningProgress);
		modelBase.rotationPointZ += 20 * (1 - spawningProgress);
		modelBase.rotationPointY += 10 * (1 - spawningProgress);

		float globalDegree = 1.5f;
		float wiggleDegree = 1.5f;
		float globalSpeed = 1.3f;
		float globalHeight = 1.5f;

		float f = spawningProgress * 10;
		float f1 = (float) (0.6f * (1/(1+Math.pow(2, 100*(spawningProgress-0.9)))));
		if (spawningProgress != 1) {

			body_base.rotationPointX -= wiggleDegree * globalDegree * f1 * 3f * Math.cos(globalSpeed * f);
			swing(sexybutt, globalSpeed, 0.2f * globalDegree * wiggleDegree, true, -1.6f, 0, f, f1);
			swing(body_base, globalSpeed, 0.3f * globalDegree * wiggleDegree, true, -0.8f, 0, f, f1);
			swing(shoulderBase, globalSpeed, 0.4f * globalDegree * wiggleDegree, true, 0, 0, f, f1);
			swing(neck, globalSpeed, 0.6f * globalDegree * wiggleDegree, false, -0.5f, 0, f, f1);

			walk(body_base, 2 * globalSpeed, 0.1f * globalHeight, true, -1.5f, 0.1f, f, f1);
			walk(neck, 2 * globalSpeed, 0.1f * globalHeight, false, -1f, -0.1f, f, f1);
			walk(jaw, 2 * globalSpeed, 0.1f * globalHeight, false, -0.7f, -0.1f, f, f1);
			bob(body_base, 2 * globalSpeed, 0.5f * globalHeight, false, f, f1);

			flap(legleftJoint, 1 * globalSpeed, 0.3f * globalDegree, false, 0 - 0.8f, -0.3f, f, f1);
			walk(legleftJoint, 1 * globalSpeed, 0.3f * globalDegree, false, 0 - 0.8f, -0.5f, f, f1);
			walk(legleft2, 1 * globalSpeed, 0.3f * globalDegree, false, -1.5f - 0.8f, 0.3f, f, f1);
			swing(legleft2, 1 * globalSpeed, 0.3f * globalDegree, false, -1.5f - 0.8f, 0.3f, f, f1);

			flap(legrightJoint, 1 * globalSpeed, 0.3f * globalDegree, false, 0 - 0.8f, 0.3f, f, f1);
			walk(legrightJoint, 1 * globalSpeed, 0.3f * globalDegree, true, 0 - 0.8f, -0.5f, f, f1);
			walk(legright2, 1 * globalSpeed, 0.3f * globalDegree, true, -1.5f - 0.8f, 0.3f, f, f1);
			swing(legright2, 1 * globalSpeed, 0.3f * globalDegree, false, -1.5f - 0.8f, -0.3f, f, f1);

			walk(armleftJoint, 1 * globalSpeed, 0.5f * globalDegree, true, -1.6f - 0.4f, 0.3f, f, f1);
			walk(armleft2, 1 * globalSpeed, 0.3f * globalDegree, true, -0.1f - 0.4f, -0.4f, f, f1);
			swing(armleft2, 1 * globalSpeed, 0.3f * globalDegree, true, -0.1f - 0.4f, -0.4f, f, f1);

			walk(armrightJoint, 1 * globalSpeed, 0.5f * globalDegree, false, -1.6f - 0.4f, 0.3f, f, f1);
			walk(armright2, 1 * globalSpeed, 0.3f * globalDegree, false, -0.1f - 0.4f, -0.4f, f, f1);
			swing(armright2, 1 * globalSpeed, 0.3f * globalDegree, true, -0.1f - 0.4f, 0.4f, f, f1);
		}

		if (mummy != null) {
			float screamProgress = mummy.getScreamingProgress(delta);
			
			if(screamProgress != 0) {
				if (screamProgress > 1) screamProgress = 1;
				float controller = 40 * (float) (-screamProgress * (screamProgress - 1) * (screamProgress - 0.1));
				if (controller > 0.2f) controller = 0.2f;
				float controller2 = controller;
				if (controller2 < 0) controller2 = 0;
	
				body_base.rotateAngleX -= 1.6f * controller;
				body_base.rotationPointY -= 1.6f * controller;
				body_base.rotationPointZ -= 1.6f * controller;
				legleftJoint.rotateAngleX += 1.6f * controller;
				legrightJoint.rotateAngleX += 1.6f * controller;
				armleftJoint.rotateAngleX += 1.6f * controller;
				armrightJoint.rotateAngleX += 1.6f * controller;
				armleftJoint.rotationPointY += 10 * controller;
				armrightJoint.rotationPointY += 10 * controller;
				armleftJoint.rotationPointZ += 5 * controller;
				armrightJoint.rotationPointZ += 5 * controller;
				armleftJoint.rotateAngleZ += 0.5 * controller;
				armrightJoint.rotateAngleZ -= 0.5 * controller;
				armleft2.rotateAngleX += 1 * controller;
				armright2.rotateAngleX += 1 * controller;
				jaw.rotateAngleX += 2.4 * controller + controller2 * 0.5 * Math.cos(4 * (mummy.ticksExisted + delta));
				cheecktissue2.rotateAngleX -= 2.4 * controller + controller2 * 0.5 * Math.cos(4 * (mummy.ticksExisted + delta));
				cheecktissue2.rotationPointY += 10 * controller;
				cheecktissueright.rotateAngleX -= 2.4 * controller + controller2 * 0.5 * Math.cos(4 * (mummy.ticksExisted + delta));
				cheecktissueright.rotationPointY += 10 * controller;
			}
		}
	}
}
