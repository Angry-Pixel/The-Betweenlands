package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLGreeblingDead1 - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelGreeblingCorpse extends ModelBase {
	public ModelRenderer body_base;
	public ModelRenderer body1;
	public ModelRenderer ribcage;
	public ModelRenderer head1;
	public ModelRenderer arm_right1;
	public ModelRenderer head2;
	public ModelRenderer nose;
	public ModelRenderer ear_left;
	public ModelRenderer ear_right;
	public ModelRenderer jaw;
	public ModelRenderer arm_right2;

	public ModelGreeblingCorpse() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		this.ear_left = new ModelRenderer(this, 17, 22);
		this.ear_left.setRotationPoint(1.0F, -3.5F, -1.0F);
		this.ear_left.addBox(-1.0F, -0.5F, 0.0F, 5, 3, 0, 0.0F);
		this.setRotateAngle(ear_left, -0.091106186954104F, -0.5462880558742251F, -0.5009094953223726F);
		this.body1 = new ModelRenderer(this, 0, 8);
		this.body1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.body1.addBox(-2.5F, -3.0F, -3.5F, 5, 4, 4, 0.0F);
		this.setRotateAngle(body1, -0.18203784098300857F, 0.0F, -0.18203784098300857F);
		this.jaw = new ModelRenderer(this, 0, 29);
		this.jaw.setRotationPoint(1.5F, -1.0F, -2.0F);
		this.jaw.addBox(-3.0F, 0.0F, -2.0F, 3, 1, 2, 0.0F);
		this.setRotateAngle(jaw, 0.6829473363053812F, 0.136659280431156F, 0.0F);
		this.arm_right1 = new ModelRenderer(this, 19, 10);
		this.arm_right1.setRotationPoint(-2.0F, -2.5F, -2.0F);
		this.arm_right1.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
		this.setRotateAngle(arm_right1, 0.5918411493512771F, 0.091106186954104F, 0.8651597102135892F);
		this.nose = new ModelRenderer(this, 17, 18);
		this.nose.setRotationPoint(0.0F, -1.0F, -4.0F);
		this.nose.addBox(-0.5F, -2.0F, -1.0F, 1, 2, 1, 0.0F);
		this.setRotateAngle(nose, -0.22759093446006054F, 0.0F, 0.0F);
		this.head1 = new ModelRenderer(this, 0, 17);
		this.head1.setRotationPoint(0.0F, -2.5F, -1.0F);
		this.head1.addBox(-2.0F, -4.0F, -4.0F, 4, 3, 4, 0.0F);
		this.setRotateAngle(head1, -0.36425021489121656F, 0.6373942428283291F, 0.22759093446006054F);
		this.ribcage = new ModelRenderer(this, 17, 0);
		this.ribcage.setRotationPoint(1.0F, 0.0F, 0.0F);
		this.ribcage.addBox(0.0F, 0.0F, -3.0F, 1, 4, 3, 0.0F);
		this.head2 = new ModelRenderer(this, 0, 25);
		this.head2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.head2.addBox(-2.0F, -1.0F, -2.0F, 4, 1, 2, 0.0F);
		this.ear_right = new ModelRenderer(this, 17, 26);
		this.ear_right.setRotationPoint(-1.0F, -3.5F, -1.0F);
		this.ear_right.addBox(-4.0F, -0.5F, 0.0F, 5, 3, 0, 0.0F);
		this.setRotateAngle(ear_right, -0.27314402793711257F, -0.6829473363053812F, 0.7740535232594852F);
		this.body_base = new ModelRenderer(this, 0, 0);
		this.body_base.setRotationPoint(0.0F, 23.5F, -2.0F);
		this.body_base.addBox(-2.0F, 0.0F, -3.0F, 3, 4, 3, 0.0F);
		this.setRotateAngle(body_base, -1.4114477660878142F, 0.5462880558742251F, -0.045553093477052F);
		this.arm_right2 = new ModelRenderer(this, 24, 10);
		this.arm_right2.setRotationPoint(0.51F, 3.5F, 0.5F);
		this.arm_right2.addBox(-1.0F, 0.0F, -1.0F, 1, 4, 1, 0.0F);
		this.setRotateAngle(arm_right2, -0.6829473363053812F, 0.0F, 0.0F);
		this.head1.addChild(this.ear_left);
		this.body_base.addChild(this.body1);
		this.head2.addChild(this.jaw);
		this.body1.addChild(this.arm_right1);
		this.head1.addChild(this.nose);
		this.body1.addChild(this.head1);
		this.body_base.addChild(this.ribcage);
		this.head1.addChild(this.head2);
		this.head1.addChild(this.ear_right);
		this.arm_right1.addChild(this.arm_right2);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		this.body_base.render(f5);
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
