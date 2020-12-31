package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;

/**
 * BLGeckoHLCorruption - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelGeckoCorrupted extends MowzieModelBase {
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

	public ModelGeckoCorrupted() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.legright_f1 = new MowzieModelRenderer(this, 34, 4);
		this.legright_f1.setRotationPoint(-2.0F, 21.5F, -3.5F);
		this.legright_f1.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1, 0.0F);
		this.setRotateAngle(legright_f1, -0.27314402793711257F, -0.6373942428283291F, 0.5918411493512771F);
		this.tail1 = new MowzieModelRenderer(this, 0, 8);
		this.tail1.setRotationPoint(0.0F, 0.0F, 4.2F);
		this.tail1.addBox(-1.5F, -2.0F, 0.0F, 3, 2, 3, 0.0F);
		this.setRotateAngle(tail1, -0.18203784098300857F, 0.6829473363053812F, 0.0F);
		this.legleft_f1 = new MowzieModelRenderer(this, 34, 0);
		this.legleft_f1.setRotationPoint(2.0F, 21.5F, -2.2F);
		this.legleft_f1.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1, 0.0F);
		this.setRotateAngle(legleft_f1, 0.22759093446006054F, 0.18203784098300857F, -0.40980330836826856F);
		this.crane = new MowzieModelRenderer(this, 19, 7);
		this.crane.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.crane.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 4, 0.0F);
		this.setRotateAngle(crane, -0.136659280431156F, 0.0F, 0.0F);
		this.tongue = new MowzieModelRenderer(this, 19, 12);
		this.tongue.setRotationPoint(0.0F, -1.0F, -4.0F);
		this.tongue.addBox(-0.5F, 0.0F, -1.0F, 1, 0, 2, 0.0F);
		this.setRotateAngle(tongue, 0.36425021489121656F, 0.0F, 0.0F);
		this.legleft_f2 = new MowzieModelRenderer(this, 39, 0);
		this.legleft_f2.setRotationPoint(0.0F, 1.5F, -0.5F);
		this.legleft_f2.addBox(-0.5F, -0.2F, -1.5F, 1, 1, 2, 0.0F);
		this.setRotateAngle(legleft_f2, 0.8196066167365371F, 0.0F, 0.0F);
		this.legleft_b1 = new MowzieModelRenderer(this, 34, 8);
		this.legleft_b1.setRotationPoint(2.0F, 22.0F, 0.5F);
		this.legleft_b1.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1, 0.0F);
		this.setRotateAngle(legleft_b1, 0.6373942428283291F, -0.31869712141416456F, -0.6829473363053812F);
		this.legright_b2 = new MowzieModelRenderer(this, 39, 13);
		this.legright_b2.setRotationPoint(0.0F, 1.5F, 0.0F);
		this.legright_b2.addBox(-0.5F, -0.2F, -0.4F, 1, 3, 1, 0.0F);
		this.setRotateAngle(legright_b2, 0.6373942428283291F, 0.0F, 0.0F);
		this.head = new MowzieModelRenderer(this, 19, 0);
		this.head.setRotationPoint(0.0F, 21.2F, -3.0F);
		this.head.addBox(-1.5F, -2.0F, -4.0F, 3, 2, 4, 0.0F);
		this.setRotateAngle(head, 1.0471975511965976F, -1.6390387005478748F, -0.7740535232594852F);
		this.tail3 = new MowzieModelRenderer(this, 0, 20);
		this.tail3.setRotationPoint(0.4F, 0.0F, 2.8F);
		this.tail3.addBox(-0.5F, -2.0F, 0.0F, 1, 3, 3, 0.0F);
		this.setRotateAngle(tail3, -0.045553093477052F, 1.1838568316277536F, -0.091106186954104F);
		this.legright_b1 = new MowzieModelRenderer(this, 34, 13);
		this.legright_b1.setRotationPoint(-2.0F, 22.0F, 1.3F);
		this.legright_b1.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1, 0.0F);
		this.setRotateAngle(legright_b1, 0.6373942428283291F, 0.31869712141416456F, 1.1838568316277536F);
		this.legleft_b2 = new MowzieModelRenderer(this, 39, 8);
		this.legleft_b2.setRotationPoint(0.0F, 1.5F, 0.0F);
		this.legleft_b2.addBox(-0.5F, -0.2F, -0.4F, 1, 3, 1, 0.0F);
		this.setRotateAngle(legleft_b2, 0.6373942428283291F, 0.0F, 0.0F);
		this.tail2 = new MowzieModelRenderer(this, 0, 14);
		this.tail2.setRotationPoint(0.0F, 0.0F, 2.3F);
		this.tail2.addBox(-1.0F, -2.0F, 0.0F, 2, 2, 3, 0.0F);
		this.setRotateAngle(tail2, -0.136659280431156F, 1.1383037381507017F, 0.136659280431156F);
		this.body_base = new MowzieModelRenderer(this, 0, 0);
		this.body_base.setRotationPoint(0.0F, 22.0F, -4.0F);
		this.body_base.addBox(-2.0F, -2.0F, 0.0F, 4, 2, 5, 0.0F);
		this.setRotateAngle(body_base, -0.045553093477052F, 0.0F, -0.31869712141416456F);
		this.legright_f2 = new MowzieModelRenderer(this, 39, 4);
		this.legright_f2.setRotationPoint(0.0F, 1.5F, -0.5F);
		this.legright_f2.addBox(-0.5F, -0.2F, -1.5F, 1, 1, 2, 0.0F);
		this.setRotateAngle(legright_f2, 0.8196066167365371F, 0.0F, 0.0F);
		this.body_base.addChild(this.tail1);
		this.head.addChild(this.crane);
		this.head.addChild(this.tongue);
		this.legleft_f1.addChild(this.legleft_f2);
		this.legright_b1.addChild(this.legright_b2);
		this.tail2.addChild(this.tail3);
		this.legleft_b1.addChild(this.legleft_b2);
		this.tail1.addChild(this.tail2);
		this.legright_f1.addChild(this.legright_f2);

		tail = new MowzieModelRenderer[] { tail3, tail2, tail1 };
		setInitPose();
	}

	public void render(int ticks, float delta) {
		this.setLivingAnimations(ticks, delta);

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
}
