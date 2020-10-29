package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;

/**
 * BLGecko - TripleHeadedSheep Created using Tabula 4.1.1
 */
public class ModelGeckoNormal extends MowzieModelBase {
	public MowzieModelRenderer body_base;
	public MowzieModelRenderer head;
	public MowzieModelRenderer legleft_f1;
	public MowzieModelRenderer legright_f1;
	public MowzieModelRenderer legleft_b1;
	public MowzieModelRenderer legright_b1;
	public MowzieModelRenderer tail1;
	public MowzieModelRenderer tail2;
	public MowzieModelRenderer tail3;
	public MowzieModelRenderer crane;
	public MowzieModelRenderer tongue;
	public MowzieModelRenderer legleft_f2;
	public MowzieModelRenderer legright_f2;
	public MowzieModelRenderer legleft_b2;
	public MowzieModelRenderer legright_b2;
	private MowzieModelRenderer[] tail;

	public ModelGeckoNormal() {
		textureWidth = 64;
		textureHeight = 32;
		crane = new MowzieModelRenderer(this, 19, 7);
		crane.setRotationPoint(0.0F, -2.0F, 0.0F);
		crane.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 4, 0.0F);
		setRotateAngle(crane, -0.136659280431156F, 0.0F, 0.0F);
		legright_b1 = new MowzieModelRenderer(this, 34, 13);
		legright_b1.setRotationPoint(-2.0F, 22.0F, 0.5F);
		legright_b1.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1, 0.0F);
		setRotateAngle(legright_b1, 0.6373942428283291F, 0.31869712141416456F, 0.6829473363053812F);
		legright_f1 = new MowzieModelRenderer(this, 34, 4);
		legright_f1.setRotationPoint(-2.0F, 21.5F, -3.0F);
		legright_f1.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1, 0.0F);
		setRotateAngle(legright_f1, 0.36425021489121656F, -0.18203784098300857F, 0.40980330836826856F);
		legleft_f2 = new MowzieModelRenderer(this, 39, 0);
		legleft_f2.setRotationPoint(0.0F, 1.5F, -0.5F);
		legleft_f2.addBox(-0.5F, -0.2F, -1.5F, 1, 1, 2, 0.0F);
		setRotateAngle(legleft_f2, 0.8196066167365371F, 0.0F, 0.0F);
		legleft_b2 = new MowzieModelRenderer(this, 39, 8);
		legleft_b2.setRotationPoint(0.0F, 1.5F, 0.0F);
		legleft_b2.addBox(-0.5F, -0.2F, -0.4F, 1, 3, 1, 0.0F);
		setRotateAngle(legleft_b2, 0.6373942428283291F, 0.0F, 0.0F);
		tongue = new MowzieModelRenderer(this, 19, 12);
		tongue.setRotationPoint(0.0F, -1.0F, -4.0F);
		tongue.addBox(-0.5F, 0.0F, -1.0F, 1, 0, 2, 0.0F);
		setRotateAngle(tongue, 0.36425021489121656F, 0.0F, 0.0F);
		legright_f2 = new MowzieModelRenderer(this, 39, 4);
		legright_f2.setRotationPoint(0.0F, 1.5F, -0.5F);
		legright_f2.addBox(-0.5F, -0.2F, -1.5F, 1, 1, 2, 0.0F);
		setRotateAngle(legright_f2, 0.8196066167365371F, 0.0F, 0.0F);
		tail1 = new MowzieModelRenderer(this, 0, 8);
		tail1.setRotationPoint(0.0F, 0.0F, 5.0F);
		tail1.addBox(-1.5F, -2.0F, 0.0F, 3, 2, 3, 0.0F);
		setRotateAngle(tail1, 0.27314402793711257F, 0.0F, 0.0F);
		legleft_f1 = new MowzieModelRenderer(this, 34, 0);
		legleft_f1.setRotationPoint(2.0F, 21.5F, -3.0F);
		legleft_f1.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1, 0.0F);
		setRotateAngle(legleft_f1, 0.36425021489121656F, 0.18203784098300857F, -0.40980330836826856F);
		head = new MowzieModelRenderer(this, 19, 0);
		head.setRotationPoint(0.0F, 21.5F, -3.0F);
		head.addBox(-1.5F, -2.0F, -4.0F, 3, 2, 4, 0.0F);
		setRotateAngle(head, 0.22759093446006054F, 0.0F, 0.0F);
		legright_b2 = new MowzieModelRenderer(this, 39, 13);
		legright_b2.setRotationPoint(0.0F, 1.5F, 0.0F);
		legright_b2.addBox(-0.5F, -0.2F, -0.4F, 1, 3, 1, 0.0F);
		setRotateAngle(legright_b2, 0.6373942428283291F, 0.0F, 0.0F);
		body_base = new MowzieModelRenderer(this, 0, 0);
		body_base.setRotationPoint(0.0F, 22.0F, -4.0F);
		body_base.addBox(-2.0F, -2.0F, 0.0F, 4, 2, 5, 0.0F);
		setRotateAngle(body_base, -0.091106186954104F, 0.0F, 0.0F);
		tail3 = new MowzieModelRenderer(this, 0, 20);
		tail3.setRotationPoint(0.0F, 0.0F, 3.0F);
		tail3.addBox(-0.5F, -2.0F, 0.0F, 1, 3, 3, 0.0F);
		setRotateAngle(tail3, 0.27314402793711257F, 0.0F, 0.0F);
		legleft_b1 = new MowzieModelRenderer(this, 34, 8);
		legleft_b1.setRotationPoint(2.0F, 22.0F, 0.5F);
		legleft_b1.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1, 0.0F);
		setRotateAngle(legleft_b1, 0.6373942428283291F, -0.31869712141416456F, -0.6829473363053812F);
		tail2 = new MowzieModelRenderer(this, 0, 14);
		tail2.setRotationPoint(0.0F, 0.0F, 3.0F);
		tail2.addBox(-1.0F, -2.0F, 0.0F, 2, 2, 3, 0.0F);
		setRotateAngle(tail2, 0.27314402793711257F, 0.0F, 0.0F);
		head.addChild(crane);
		legleft_f1.addChild(legleft_f2);
		legleft_b1.addChild(legleft_b2);
		head.addChild(tongue);
		legright_f1.addChild(legright_f2);
		body_base.addChild(tail1);
		legright_b1.addChild(legright_b2);
		tail2.addChild(tail3);
		tail1.addChild(tail2);

		tail = new MowzieModelRenderer[] { tail3, tail2, tail1 };
		setInitPose();
	}

	/*@Override
	public void render(Entity entity, float swing, float speed, float age, float yaw, float pitch, float scale) {
		setRotationAngles(swing, speed, age, yaw, pitch, scale, entity);
		legright_b1.render(scale);
		legright_f1.render(scale);
		legleft_f1.render(scale);
		head.render(scale);
		body_base.render(scale);
		legleft_b1.render(scale);
	}*/

	public void render(int ticks, float delta, float swing, float speed) {
		this.setLivingAnimations(ticks, delta);

		this.faceTarget(head, 1, 0, 0);

		if (speed > 0.4) {
			speed = 0.4F;
		}

		float globalSpeed = 1, globalDegree = 1, frontOffset = 0.85F;

		walk(legleft_f1, globalSpeed, globalDegree, false, frontOffset, 0, swing, speed);
		walk(legleft_f2, globalSpeed, 1.5F * globalDegree, false, 2 + frontOffset, -0.4F * globalDegree, swing, speed);
		walk(legright_f1, globalSpeed, globalDegree, true, frontOffset, 0, swing, speed);
		walk(legright_f2, globalSpeed, 1.5F * globalDegree, true, 2 + frontOffset, -0.4F * globalDegree, swing, speed);

		walk(legleft_b1, globalSpeed, globalDegree, false, 0, -2.5F * globalDegree, swing, speed);
		walk(legleft_b2, globalSpeed, globalDegree, false, -1, 2.5F * globalDegree, swing, speed);
		walk(legright_b1, globalSpeed, globalDegree, true, 0, -2.5F * globalDegree, swing, speed);
		walk(legright_b2, globalSpeed, globalDegree, true, -1, 2.5F * globalDegree, swing, speed);

		legright_b1.render(0.0625F);
		legright_f1.render(0.0625F);
		legleft_f1.render(0.0625F);
		head.render(0.0625F);
		body_base.render(0.0625F);
		legleft_b1.render(0.0625F);
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	public void setLivingAnimations(int ticks, float delta) {
		setToInitPose();
		float frame = ticks + delta;
		float tongueControl = (int) ((MathHelper.sin(0.15F * frame - MathHelper.cos(0.15F * frame)) + 1) / 2 + 0.5F);
		tongue.isHidden = tongueControl == 0;
		walk(tongue, 2, 1, false, 0, 0, frame, 1);
		chainWave(tail, 0.2F, 0.1f, 0, frame, 1);
		chainSwing(tail, 0.4F, 0.15f, 3, frame, 1);
	}

	/*@Override
	public void setRotationAngles(float swing, float speed, float age, float yaw, float pitch, float scale, Entity entity) {
		faceTarget(head, 1, yaw, pitch);

		if (speed > 0.4) {
			speed = 0.4F;
		}

		float globalSpeed = 1, globalDegree = 1, frontOffset = 0.85F;

		walk(legleft_f1, globalSpeed, globalDegree, false, frontOffset, 0, swing, speed);
		walk(legleft_f2, globalSpeed, 1.5F * globalDegree, false, 2 + frontOffset, -0.4F * globalDegree, swing, speed);
		walk(legright_f1, globalSpeed, globalDegree, true, frontOffset, 0, swing, speed);
		walk(legright_f2, globalSpeed, 1.5F * globalDegree, true, 2 + frontOffset, -0.4F * globalDegree, swing, speed);

		walk(legleft_b1, globalSpeed, globalDegree, false, 0, -2.5F * globalDegree, swing, speed);
		walk(legleft_b2, globalSpeed, globalDegree, false, -1, 2.5F * globalDegree, swing, speed);
		walk(legright_b1, globalSpeed, globalDegree, true, 0, -2.5F * globalDegree, swing, speed);
		walk(legright_b2, globalSpeed, globalDegree, true, -1, 2.5F * globalDegree, swing, speed);
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float yaw, float pitch, float delta) {
		setToInitPose();
		float frame = entity.ticksExisted + delta;
		float tongueControl = (int) ((MathHelper.sin(0.15F * frame - MathHelper.cos(0.15F * frame)) + 1) / 2 + 0.5F);
		tongue.isHidden = tongueControl == 0;
		walk(tongue, 2, 1, false, 0, 0, frame, 1);
		chainWave(tail, 0.2F, 0.1f, 0, frame, 1);
		chainSwing(tail, 0.4F, 0.15f, 3, frame, 1);
	}*/
}
