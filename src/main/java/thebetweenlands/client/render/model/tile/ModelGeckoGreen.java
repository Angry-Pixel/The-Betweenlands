package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelRenderer;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;

/**
 * BLGeckoHLForm - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelGeckoGreen extends MowzieModelBase {
	public MowzieModelRenderer body_base;
	public MowzieModelRenderer head;
	public MowzieModelRenderer legleft_f1;
	public MowzieModelRenderer legright_f1;
	public MowzieModelRenderer legleft_b1;
	public MowzieModelRenderer legright_b1;
	public MowzieModelRenderer tail1;
	public MowzieModelRenderer tail2;
	public MowzieModelRenderer connectionpiece;
	public MowzieModelRenderer crane;
	public MowzieModelRenderer jaw;
	public MowzieModelRenderer legleft_f2;
	public MowzieModelRenderer legright_f2;
	public MowzieModelRenderer legleft_b2;
	public MowzieModelRenderer legright_b2;
	private MowzieModelRenderer[] tail;

	public ModelGeckoGreen() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.body_base = new MowzieModelRenderer(this, 0, 0);
		this.body_base.setRotationPoint(0.0F, 22.0F, -4.0F);
		this.body_base.addBox(-2.0F, -2.0F, 0.0F, 4, 2, 5, 0.0F);
		this.setRotateAngle(body_base, -0.091106186954104F, 0.0F, 0.0F);
		this.head = new MowzieModelRenderer(this, 19, 0);
		this.head.setRotationPoint(0.0F, 21.5F, -4.5F);
		this.head.addBox(-1.5F, -2.0F, -4.0F, 3, 2, 5, 0.0F);
		this.setRotateAngle(head, 0.045553093477052F, 0.0F, 0.0F);
		this.legleft_f2 = new MowzieModelRenderer(this, 43, 0);
		this.legleft_f2.setRotationPoint(0.0F, 1.5F, -0.5F);
		this.legleft_f2.addBox(-0.5F, -0.2F, -1.5F, 1, 1, 2, 0.0F);
		this.setRotateAngle(legleft_f2, 0.8196066167365371F, 0.0F, 0.0F);
		this.legright_b2 = new MowzieModelRenderer(this, 43, 13);
		this.legright_b2.setRotationPoint(0.0F, 1.5F, 0.0F);
		this.legright_b2.addBox(-0.5F, -0.2F, -0.4F, 1, 3, 1, 0.0F);
		this.setRotateAngle(legright_b2, 0.6373942428283291F, 0.0F, 0.0F);
		this.connectionpiece = new MowzieModelRenderer(this, 19, 8);
		this.connectionpiece.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.connectionpiece.addBox(-1.5F, 0.0F, -1.0F, 3, 1, 2, 0.0F);
		this.legleft_b1 = new MowzieModelRenderer(this, 36, 8);
		this.legleft_b1.setRotationPoint(2.0F, 22.0F, 0.5F);
		this.legleft_b1.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 2, 0.0F);
		this.setRotateAngle(legleft_b1, 0.6373942428283291F, -0.31869712141416456F, -0.6829473363053812F);
		this.tail1 = new MowzieModelRenderer(this, 0, 8);
		this.tail1.setRotationPoint(0.0F, 0.0F, 5.0F);
		this.tail1.addBox(-1.5F, -2.0F, 0.0F, 3, 2, 3, 0.0F);
		this.setRotateAngle(tail1, 0.136659280431156F, 0.0F, 0.0F);
		this.legright_b1 = new MowzieModelRenderer(this, 36, 13);
		this.legright_b1.setRotationPoint(-2.0F, 22.0F, 0.5F);
		this.legright_b1.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 2, 0.0F);
		this.setRotateAngle(legright_b1, 0.6373942428283291F, 0.31869712141416456F, 0.6829473363053812F);
		this.jaw = new MowzieModelRenderer(this, 19, 12);
		this.jaw.setRotationPoint(0.0F, 0.0F, -1.0F);
		this.jaw.addBox(-1.51F, 0.0F, -3.0F, 3, 1, 3, 0.0F);
		this.setRotateAngle(jaw, 0.36425021489121656F, 0.0F, 0.0F);
		this.legright_f1 = new MowzieModelRenderer(this, 36, 4);
		this.legright_f1.setRotationPoint(-2.0F, 21.5F, -3.0F);
		this.legright_f1.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 2, 0.0F);
		this.setRotateAngle(legright_f1, 0.36425021489121656F, -0.18203784098300857F, 0.40980330836826856F);
		this.legleft_f1 = new MowzieModelRenderer(this, 36, 0);
		this.legleft_f1.setRotationPoint(2.0F, 21.5F, -3.0F);
		this.legleft_f1.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 2, 0.0F);
		this.setRotateAngle(legleft_f1, 0.36425021489121656F, 0.18203784098300857F, -0.40980330836826856F);
		this.tail2 = new MowzieModelRenderer(this, 0, 14);
		this.tail2.setRotationPoint(0.0F, 0.0F, 3.0F);
		this.tail2.addBox(-1.0F, -2.0F, 0.0F, 2, 2, 2, 0.0F);
		this.setRotateAngle(tail2, 0.091106186954104F, 0.0F, 0.0F);
		this.legleft_b2 = new MowzieModelRenderer(this, 43, 8);
		this.legleft_b2.setRotationPoint(0.0F, 1.5F, 0.0F);
		this.legleft_b2.addBox(-0.5F, -0.2F, -0.4F, 1, 3, 1, 0.0F);
		this.setRotateAngle(legleft_b2, 0.6373942428283291F, 0.0F, 0.0F);
		this.crane = new MowzieModelRenderer(this, 19, 17);
		this.crane.setRotationPoint(0.0F, -2.0F, -1.6F);
		this.crane.addBox(-1.0F, -2.0F, -1.0F, 2, 4, 2, 0.0F);
		this.setRotateAngle(crane, -1.2747884856566583F, 0.0F, 0.0F);
		this.legright_f2 = new MowzieModelRenderer(this, 43, 4);
		this.legright_f2.setRotationPoint(0.0F, 1.5F, -0.5F);
		this.legright_f2.addBox(-0.5F, -0.2F, -1.5F, 1, 1, 2, 0.0F);
		this.setRotateAngle(legright_f2, 0.8196066167365371F, 0.0F, 0.0F);
		this.legleft_f1.addChild(this.legleft_f2);
		this.legright_b1.addChild(this.legright_b2);
		this.head.addChild(this.connectionpiece);
		this.body_base.addChild(this.tail1);
		this.connectionpiece.addChild(this.jaw);
		this.tail1.addChild(this.tail2);
		this.legleft_b1.addChild(this.legleft_b2);
		this.head.addChild(this.crane);
		this.legright_f1.addChild(this.legright_f2);

		tail = new MowzieModelRenderer[] { tail2, tail1 };
		setInitPose();
	}

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
		chainWave(tail, 0.2F, 0.1f, 0, frame, 1);
		chainSwing(tail, 0.4F, 0.15f, 3, frame, 1);
	}
}
