package thebetweenlands.client.render.model.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.AnimationBlender;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntityChiromawMatriarch;

@SideOnly(Side.CLIENT)
public class ModelChiromawMatriarch extends MowzieModelBase {
	MowzieModelRenderer body_base;
	MowzieModelRenderer body1;
	MowzieModelRenderer tail1;
	MowzieModelRenderer leg_left1a;
	MowzieModelRenderer leg_right1a;
	MowzieModelRenderer body2;
	MowzieModelRenderer vertebrate3;
	MowzieModelRenderer neck1;
	MowzieModelRenderer arm_left1a;
	MowzieModelRenderer arm_right1a;
	MowzieModelRenderer vertebrate1;
	MowzieModelRenderer vertebrate2;
	MowzieModelRenderer neck2;
	MowzieModelRenderer head1;
	MowzieModelRenderer head2;
	MowzieModelRenderer teeth_upper_left;
	MowzieModelRenderer teeth_upper_right;
	MowzieModelRenderer canine_upper_left1a;
	MowzieModelRenderer canine_upper_right1a;
	MowzieModelRenderer teeth_upper_mid;
	MowzieModelRenderer crest_left1a;
	MowzieModelRenderer crest_right1a;
	MowzieModelRenderer crest_mid1a;
	MowzieModelRenderer crest_left2;
	MowzieModelRenderer crest_right2;
	MowzieModelRenderer jaw;
	MowzieModelRenderer teeth_lower_left;
	MowzieModelRenderer teeth_lower_right;
	MowzieModelRenderer canine_lower_left1a;
	MowzieModelRenderer canine_lower_right1a;
	MowzieModelRenderer teeth_lower_mid;
	MowzieModelRenderer canine_lower_left1b;
	MowzieModelRenderer canine_lower_right1b;
	MowzieModelRenderer canine_upper_left1b;
	MowzieModelRenderer canine_upper_right1b;
	MowzieModelRenderer crest_left1b;
	MowzieModelRenderer crest_right1b;
	MowzieModelRenderer crest_mid1b;
	MowzieModelRenderer arm_left1b;
	MowzieModelRenderer arm_left1c;
	MowzieModelRenderer wingflap_left1;
	MowzieModelRenderer claw_left1a;
	MowzieModelRenderer wingflap_left2;
	MowzieModelRenderer claw_left1b;
	MowzieModelRenderer arm_right1b;
	MowzieModelRenderer arm_right1c;
	MowzieModelRenderer wingflap_right1;
	MowzieModelRenderer claw_right1a;
	MowzieModelRenderer wingflap_right2;
	MowzieModelRenderer claw_right1b;
	MowzieModelRenderer tail2;
	MowzieModelRenderer tail3;
	MowzieModelRenderer tail4;
	MowzieModelRenderer tail5;
	MowzieModelRenderer leg_left1b;
	MowzieModelRenderer leg_left1c;
	MowzieModelRenderer foot_claw_left1;
	MowzieModelRenderer leg_right1b;
	MowzieModelRenderer leg_right1c;
	MowzieModelRenderer foot_claw_right1;

	MowzieModelRenderer[] partsTail;
	MowzieModelRenderer[] partsTailAndBum;

	public ModelChiromawMatriarch() {
		textureWidth = 128;
		textureHeight = 128;
		arm_right1b = new MowzieModelRenderer(this, 88, 8);
		arm_right1b.setRotationPoint(0.01F, 3.0F, 2.0F);
		arm_right1b.addBox(-2.0F, 0.0F, -2.0F, 2, 12, 2, 0.0F);
		setRotateAngle(arm_right1b, -0.091106186954104F, 0.0F, 0.0F);
		foot_claw_left1 = new MowzieModelRenderer(this, 25, 79);
		foot_claw_left1.setRotationPoint(0.0F, 5.0F, -2.0F);
		foot_claw_left1.addBox(-1.0F, 0.0F, -2.0F, 2, 1, 2, 0.0F);
		setRotateAngle(foot_claw_left1, 0.5009094953223726F, 0.0F, 0.0F);
		teeth_lower_mid = new MowzieModelRenderer(this, 35, 39);
		teeth_lower_mid.setRotationPoint(0.0F, 0.0F, -6.0F);
		teeth_lower_mid.addBox(-2.0F, -2.0F, 0.0F, 4, 2, 0, 0.0F);
		setRotateAngle(teeth_lower_mid, 0.136659280431156F, 0.0F, 0.0F);
		vertebrate3 = new MowzieModelRenderer(this, 0, 115);
		vertebrate3.setRotationPoint(0.0F, 0.0F, -6.0F);
		vertebrate3.addBox(-1.0F, -1.0F, 0.0F, 2, 1, 2, 0.0F);
		setRotateAngle(vertebrate3, -0.136659280431156F, 0.0F, 0.0F);
		arm_right1a = new MowzieModelRenderer(this, 88, 0);
		arm_right1a.setRotationPoint(-4.5F, 2.0F, -6.5F);
		arm_right1a.addBox(-2.0F, -1.0F, -1.0F, 3, 4, 3, 0.0F);
		setRotateAngle(arm_right1a, 0.4553564018453205F, 0.0F, 1.0016444577195458F);
		crest_mid1b = new MowzieModelRenderer(this, 62, 24);
		crest_mid1b.setRotationPoint(0.0F, 0.0F, 3.0F);
		crest_mid1b.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 4, 0.0F);
		setRotateAngle(crest_mid1b, 0.18203784098300857F, 0.0F, 0.0F);
		teeth_upper_right = new MowzieModelRenderer(this, 46, 37);
		teeth_upper_right.setRotationPoint(-4.0F, 0.0F, -6.0F);
		teeth_upper_right.addBox(0.0F, 0.0F, -2.0F, 0, 1, 5, 0.0F);
		setRotateAngle(teeth_upper_right, 0.0F, 0.0F, 0.091106186954104F);
		crest_right1b = new MowzieModelRenderer(this, 62, 20);
		crest_right1b.setRotationPoint(0.0F, 0.0F, 4.0F);
		crest_right1b.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3, 0.0F);
		setRotateAngle(crest_right1b, 0.18203784098300857F, 0.0F, 0.0F);
		tail2 = new MowzieModelRenderer(this, 0, 71);
		tail2.setRotationPoint(0.0F, 0.0F, 4.0F);
		tail2.addBox(-2.0F, -4.0F, 0.0F, 4, 4, 4, 0.0F);
		setRotateAngle(tail2, 0.18203784098300857F, 0.0F, 0.0F);
		crest_mid1a = new MowzieModelRenderer(this, 55, 24);
		crest_mid1a.setRotationPoint(0.0F, -5.0F, -3.0F);
		crest_mid1a.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3, 0.0F);
		setRotateAngle(crest_mid1a, 0.36425021489121656F, 0.0F, 0.0F);
		body_base = new MowzieModelRenderer(this, 0, 0);
		body_base.setRotationPoint(0.0F, 11.0F, 7.0F);
		body_base.addBox(-4.0F, 0.0F, 0.0F, 8, 6, 6, 0.0F);
		setRotateAngle(body_base, -0.9560913642424937F, 0.0F, 0.0F);
		claw_right1b = new MowzieModelRenderer(this, 88, 50);
		claw_right1b.setRotationPoint(0.01F, 3.0F, -2.0F);
		claw_right1b.addBox(-2.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
		setRotateAngle(claw_right1b, 0.6373942428283291F, 0.0F, 0.0F);
		canine_lower_right1b = new MowzieModelRenderer(this, 50, 34);
		canine_lower_right1b.setRotationPoint(0.0F, -2.0F, 0.0F);
		canine_lower_right1b.addBox(0.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F);
		setRotateAngle(canine_lower_right1b, -0.27314402793711257F, 0.0F, 0.0F);
		tail5 = new MowzieModelRenderer(this, 0, 98);
		tail5.setRotationPoint(0.0F, 0.0F, 5.0F);
		tail5.addBox(-1.5F, -3.0F, 0.0F, 3, 3, 5, 0.0F);
		setRotateAngle(tail5, 0.136659280431156F, 0.0F, 0.0F);
		neck1 = new MowzieModelRenderer(this, 0, 43);
		neck1.setRotationPoint(0.0F, 0.0F, -7.0F);
		neck1.addBox(-2.5F, 0.0F, -3.0F, 5, 5, 3, 0.0F);
		setRotateAngle(neck1, 0.40980330836826856F, 0.0F, 0.0F);
		claw_left1b = new MowzieModelRenderer(this, 75, 50);
		claw_left1b.setRotationPoint(-0.01F, 3.0F, -2.0F);
		claw_left1b.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
		setRotateAngle(claw_left1b, 0.6373942428283291F, 0.0F, 0.0F);
		jaw = new MowzieModelRenderer(this, 35, 22);
		jaw.setRotationPoint(0.0F, 1.0F, -3.0F);
		jaw.addBox(-3.0F, 0.0F, -6.0F, 6, 2, 6, 0.0F);
		setRotateAngle(jaw, 0.8651597102135892F, 0.0F, 0.0F);
		vertebrate1 = new MowzieModelRenderer(this, 0, 107);
		vertebrate1.setRotationPoint(0.0F, 0.0F, -7.0F);
		vertebrate1.addBox(-1.0F, -1.0F, 0.0F, 2, 1, 2, 0.0F);
		setRotateAngle(vertebrate1, -0.136659280431156F, 0.0F, 0.0F);
		canine_upper_left1b = new MowzieModelRenderer(this, 40, 44);
		canine_upper_left1b.setRotationPoint(0.0F, 1.0F, 0.0F);
		canine_upper_left1b.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		setRotateAngle(canine_upper_left1b, 0.27314402793711257F, 0.0F, 0.0F);
		arm_right1c = new MowzieModelRenderer(this, 88, 23);
		arm_right1c.setRotationPoint(0.01F, 12.0F, 0.0F);
		arm_right1c.addBox(-2.0F, 0.0F, -2.0F, 2, 18, 2, 0.0F);
		setRotateAngle(arm_right1c, -0.5918411493512771F, 0.0F, 0.0F);
		body2 = new MowzieModelRenderer(this, 0, 28);
		body2.setRotationPoint(0.0F, 0.0F, -7.0F);
		body2.addBox(-5.0F, 0.0F, -7.0F, 10, 7, 7, 0.0F);
		setRotateAngle(body2, 0.36425021489121656F, 0.0F, 0.0F);
		vertebrate2 = new MowzieModelRenderer(this, 0, 111);
		vertebrate2.setRotationPoint(0.0F, 0.0F, -3.0F);
		vertebrate2.addBox(-1.0F, -1.0F, 0.0F, 2, 1, 2, 0.0F);
		setRotateAngle(vertebrate2, -0.136659280431156F, 0.0F, 0.0F);
		teeth_lower_right = new MowzieModelRenderer(this, 46, 26);
		teeth_lower_right.setRotationPoint(-3.0F, 0.0F, -3.0F);
		teeth_lower_right.addBox(0.0F, -2.0F, -2.0F, 0, 2, 5, 0.0F);
		setRotateAngle(teeth_lower_right, 0.0F, 0.0F, -0.136659280431156F);
		claw_right1a = new MowzieModelRenderer(this, 88, 44);
		claw_right1a.setRotationPoint(0.01F, 18.0F, 0.0F);
		claw_right1a.addBox(-2.0F, 0.0F, -2.0F, 2, 3, 2, 0.0F);
		setRotateAngle(claw_right1a, -0.8196066167365371F, 0.0F, 0.0F);
		leg_right1c = new MowzieModelRenderer(this, 40, 70);
		leg_right1c.setRotationPoint(-1.5F, 5.0F, 3.0F);
		leg_right1c.addBox(-1.0F, 0.0F, -2.0F, 2, 6, 2, 0.0F);
		setRotateAngle(leg_right1c, -0.9560913642424937F, 0.0F, 0.0F);
		claw_left1a = new MowzieModelRenderer(this, 75, 44);
		claw_left1a.setRotationPoint(-0.01F, 18.0F, 0.0F);
		claw_left1a.addBox(0.0F, 0.0F, -2.0F, 2, 3, 2, 0.0F);
		setRotateAngle(claw_left1a, -0.8196066167365371F, 0.0F, 0.0F);
		crest_left1b = new MowzieModelRenderer(this, 55, 20);
		crest_left1b.setRotationPoint(0.0F, 0.0F, 4.0F);
		crest_left1b.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3, 0.0F);
		setRotateAngle(crest_left1b, 0.18203784098300857F, 0.0F, 0.0F);
		body1 = new MowzieModelRenderer(this, 0, 13);
		body1.setRotationPoint(0.0F, 0.0F, 0.0F);
		body1.addBox(-5.0F, 0.0F, -7.0F, 10, 7, 7, 0.0F);
		setRotateAngle(body1, 0.36425021489121656F, 0.0F, 0.0F);
		crest_left1a = new MowzieModelRenderer(this, 54, 15);
		crest_left1a.setRotationPoint(2.5F, -4.0F, -3.5F);
		crest_left1a.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 4, 0.0F);
		setRotateAngle(crest_left1a, 0.4553564018453205F, 0.091106186954104F, 0.6373942428283291F);
		wingflap_left2 = new MowzieModelRenderer(this, 108, -10);
		wingflap_left2.setRotationPoint(2.0F, 0.0F, 0.0F);
		wingflap_left2.addBox(0.0F, -4.0F, 0.0F, 0, 24, 10, 0.0F);
		setRotateAngle(wingflap_left2, 0.0F, -0.22759093446006054F, 0.0F);
		crest_left2 = new MowzieModelRenderer(this, 60, 27);
		crest_left2.setRotationPoint(4.0F, 0.0F, -2.0F);
		crest_left2.addBox(0.0F, -4.0F, 0.0F, 0, 6, 3, 0.0F);
		setRotateAngle(crest_left2, 0.0F, 0.27314402793711257F, 0.0F);
		leg_left1b = new MowzieModelRenderer(this, 25, 61);
		leg_left1b.setRotationPoint(-0.01F, 5.0F, 2.0F);
		leg_left1b.addBox(0.0F, 0.0F, 0.0F, 3, 5, 3, 0.0F);
		setRotateAngle(leg_left1b, 2.367539130330308F, 0.0F, 0.0F);
		canine_lower_left1b = new MowzieModelRenderer(this, 40, 34);
		canine_lower_left1b.setRotationPoint(0.0F, -2.0F, 0.0F);
		canine_lower_left1b.addBox(-1.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F);
		setRotateAngle(canine_lower_left1b, -0.27314402793711257F, 0.0F, 0.0F);
		tail4 = new MowzieModelRenderer(this, 0, 89);
		tail4.setRotationPoint(0.0F, 0.0F, 5.0F);
		tail4.addBox(-1.5F, -3.0F, 0.0F, 3, 3, 5, 0.0F);
		setRotateAngle(tail4, 0.22759093446006054F, 0.0F, 0.0F);
		arm_left1a = new MowzieModelRenderer(this, 75, 0);
		arm_left1a.setRotationPoint(4.5F, 2.0F, -6.5F);
		arm_left1a.addBox(-1.0F, -1.0F, -1.0F, 3, 4, 3, 0.0F);
		setRotateAngle(arm_left1a, 0.4553564018453205F, 0.0F, -1.0016444577195458F);
		head1 = new MowzieModelRenderer(this, 35, 0);
		head1.setRotationPoint(0.0F, -2.0F, -3.0F);
		head1.addBox(-4.0F, -5.0F, -9.0F, 8, 5, 9, 0.0F);
		setRotateAngle(head1, 0.136659280431156F, 0.0F, 0.0F);
		neck2 = new MowzieModelRenderer(this, 0, 52);
		neck2.setRotationPoint(0.0F, 5.0F, -3.0F);
		neck2.addBox(-2.5F, -5.0F, -4.0F, 5, 5, 4, 0.0F);
		setRotateAngle(neck2, -0.22759093446006054F, 0.0F, 0.0F);
		arm_left1b = new MowzieModelRenderer(this, 75, 8);
		arm_left1b.setRotationPoint(-0.01F, 3.0F, 2.0F);
		arm_left1b.addBox(0.0F, 0.0F, -2.0F, 2, 12, 2, 0.0F);
		setRotateAngle(arm_left1b, -0.091106186954104F, 0.0F, 0.0F);
		crest_right2 = new MowzieModelRenderer(this, 60, 34);
		crest_right2.setRotationPoint(-4.0F, 0.0F, -2.0F);
		crest_right2.addBox(0.0F, -4.0F, 0.0F, 0, 6, 3, 0.0F);
		setRotateAngle(crest_right2, 0.0F, -0.27314402793711257F, 0.0F);
		leg_right1b = new MowzieModelRenderer(this, 40, 61);
		leg_right1b.setRotationPoint(0.01F, 5.0F, 2.0F);
		leg_right1b.addBox(-3.0F, 0.0F, 0.0F, 3, 5, 3, 0.0F);
		setRotateAngle(leg_right1b, 2.367539130330308F, 0.0F, 0.0F);
		tail1 = new MowzieModelRenderer(this, 0, 62);
		tail1.setRotationPoint(0.0F, 4.0F, 6.0F);
		tail1.addBox(-2.0F, -4.0F, 0.0F, 4, 4, 4, 0.0F);
		setRotateAngle(tail1, 0.18203784098300857F, 0.0F, 0.0F);
		canine_upper_left1a = new MowzieModelRenderer(this, 35, 44);
		canine_upper_left1a.setRotationPoint(4.0F, 0.0F, -9.0F);
		canine_upper_left1a.addBox(-1.0F, -1.0F, 0.0F, 1, 2, 1, 0.0F);
		setRotateAngle(canine_upper_left1a, -0.4553564018453205F, 0.0F, -0.18203784098300857F);
		canine_lower_right1a = new MowzieModelRenderer(this, 45, 34);
		canine_lower_right1a.setRotationPoint(-3.0F, 0.0F, -6.0F);
		canine_lower_right1a.addBox(0.0F, -2.0F, 0.0F, 1, 3, 1, 0.0F);
		setRotateAngle(canine_lower_right1a, 0.22759093446006054F, 0.0F, -0.18203784098300857F);
		arm_left1c = new MowzieModelRenderer(this, 75, 23);
		arm_left1c.setRotationPoint(-0.01F, 12.0F, 0.0F);
		arm_left1c.addBox(0.0F, 0.0F, -2.0F, 2, 18, 2, 0.0F);
		setRotateAngle(arm_left1c, -0.5918411493512771F, 0.0F, 0.0F);
		crest_right1a = new MowzieModelRenderer(this, 61, 15);
		crest_right1a.setRotationPoint(-2.5F, -4.0F, -3.5F);
		crest_right1a.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 4, 0.0F);
		setRotateAngle(crest_right1a, 0.4553564018453205F, -0.091106186954104F, -0.6373942428283291F);
		canine_upper_right1b = new MowzieModelRenderer(this, 50, 44);
		canine_upper_right1b.setRotationPoint(0.0F, 1.0F, 0.0F);
		canine_upper_right1b.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		setRotateAngle(canine_upper_right1b, 0.27314402793711257F, 0.0F, 0.0F);
		tail3 = new MowzieModelRenderer(this, 0, 80);
		tail3.setRotationPoint(0.0F, -1.0F, 4.0F);
		tail3.addBox(-1.5F, -3.0F, 0.0F, 3, 3, 5, 0.0F);
		setRotateAngle(tail3, 0.22759093446006054F, 0.0F, 0.0F);
		teeth_upper_mid = new MowzieModelRenderer(this, 35, 48);
		teeth_upper_mid.setRotationPoint(0.0F, 0.0F, -9.0F);
		teeth_upper_mid.addBox(-3.0F, 0.0F, 0.0F, 6, 1, 0, 0.0F);
		setRotateAngle(teeth_upper_mid, -0.091106186954104F, 0.0F, 0.0F);
		teeth_upper_left = new MowzieModelRenderer(this, 35, 37);
		teeth_upper_left.setRotationPoint(4.0F, 0.0F, -6.0F);
		teeth_upper_left.addBox(0.0F, 0.0F, -2.0F, 0, 1, 5, 0.0F);
		setRotateAngle(teeth_upper_left, 0.0F, 0.0F, -0.091106186954104F);
		head2 = new MowzieModelRenderer(this, 35, 15);
		head2.setRotationPoint(0.0F, 0.0F, 0.0F);
		head2.addBox(-4.0F, 0.0F, -3.0F, 8, 3, 3, 0.0F);
		foot_claw_right1 = new MowzieModelRenderer(this, 40, 79);
		foot_claw_right1.setRotationPoint(0.0F, 5.0F, -2.0F);
		foot_claw_right1.addBox(-1.0F, 0.0F, -2.0F, 2, 1, 2, 0.0F);
		setRotateAngle(foot_claw_right1, 0.5009094953223726F, 0.0F, 0.0F);
		wingflap_right1 = new MowzieModelRenderer(this, 112, 42);
		wingflap_right1.setRotationPoint(-2.0F, 0.0F, 0.0F);
		wingflap_right1.addBox(0.0F, -2.0F, 0.0F, 0, 14, 8, 0.0F);
		setRotateAngle(wingflap_right1, 0.0F, 0.22759093446006054F, 0.0F);
		leg_left1a = new MowzieModelRenderer(this, 25, 50);
		leg_left1a.setRotationPoint(3.0F, 1.5F, 4.5F);
		leg_left1a.addBox(0.0F, -1.0F, -2.0F, 3, 6, 4, 0.0F);
		setRotateAngle(leg_left1a, -0.7285004297824331F, -0.136659280431156F, -0.27314402793711257F);
		canine_lower_left1a = new MowzieModelRenderer(this, 35, 34);
		canine_lower_left1a.setRotationPoint(3.0F, 0.0F, -6.0F);
		canine_lower_left1a.addBox(-1.0F, -2.0F, 0.0F, 1, 3, 1, 0.0F);
		setRotateAngle(canine_lower_left1a, 0.22759093446006054F, 0.0F, 0.18203784098300857F);
		leg_left1c = new MowzieModelRenderer(this, 25, 70);
		leg_left1c.setRotationPoint(1.5F, 5.0F, 3.0F);
		leg_left1c.addBox(-1.0F, 0.0F, -2.0F, 2, 6, 2, 0.0F);
		setRotateAngle(leg_left1c, -0.9560913642424937F, 0.0F, 0.0F);
		leg_right1a = new MowzieModelRenderer(this, 40, 50);
		leg_right1a.setRotationPoint(-3.0F, 1.5F, 4.5F);
		leg_right1a.addBox(-3.0F, -1.0F, -2.0F, 3, 6, 4, 0.0F);
		setRotateAngle(leg_right1a, -0.7285004297824331F, 0.136659280431156F, 0.27314402793711257F);
		teeth_lower_left = new MowzieModelRenderer(this, 35, 26);
		teeth_lower_left.setRotationPoint(3.0F, 0.0F, -3.0F);
		teeth_lower_left.addBox(0.0F, -2.0F, -2.0F, 0, 2, 5, 0.0F);
		setRotateAngle(teeth_lower_left, 0.0F, 0.0F, 0.136659280431156F);
		wingflap_right2 = new MowzieModelRenderer(this, 108, 15);
		wingflap_right2.setRotationPoint(-2.0F, 0.0F, 0.0F);
		wingflap_right2.addBox(0.0F, -4.0F, 0.0F, 0, 24, 10, 0.0F);
		setRotateAngle(wingflap_right2, 0.0F, 0.22759093446006054F, 0.0F);
		wingflap_left1 = new MowzieModelRenderer(this, 112, 57);
		wingflap_left1.setRotationPoint(2.0F, 0.0F, 0.0F);
		wingflap_left1.addBox(0.0F, -2.0F, 0.0F, 0, 14, 8, 0.0F);
		setRotateAngle(wingflap_left1, 0.0F, -0.22759093446006054F, 0.0F);
		canine_upper_right1a = new MowzieModelRenderer(this, 45, 44);
		canine_upper_right1a.setRotationPoint(-4.0F, 0.0F, -9.0F);
		canine_upper_right1a.addBox(0.0F, -1.0F, 0.0F, 1, 2, 1, 0.0F);
		setRotateAngle(canine_upper_right1a, -0.4553564018453205F, 0.0F, 0.18203784098300857F);
		arm_right1a.addChild(arm_right1b);
		leg_left1c.addChild(foot_claw_left1);
		jaw.addChild(teeth_lower_mid);
		body1.addChild(vertebrate3);
		body2.addChild(arm_right1a);
		crest_mid1a.addChild(crest_mid1b);
		head1.addChild(teeth_upper_right);
		crest_right1a.addChild(crest_right1b);
		tail1.addChild(tail2);
		head1.addChild(crest_mid1a);
		claw_right1a.addChild(claw_right1b);
		canine_lower_right1a.addChild(canine_lower_right1b);
		tail4.addChild(tail5);
		body2.addChild(neck1);
		claw_left1a.addChild(claw_left1b);
		head2.addChild(jaw);
		body2.addChild(vertebrate1);
		canine_upper_left1a.addChild(canine_upper_left1b);
		arm_right1b.addChild(arm_right1c);
		body1.addChild(body2);
		body2.addChild(vertebrate2);
		jaw.addChild(teeth_lower_right);
		arm_right1c.addChild(claw_right1a);
		leg_right1b.addChild(leg_right1c);
		arm_left1c.addChild(claw_left1a);
		crest_left1a.addChild(crest_left1b);
		body_base.addChild(body1);
		head1.addChild(crest_left1a);
		arm_left1c.addChild(wingflap_left2);
		head1.addChild(crest_left2);
		leg_left1a.addChild(leg_left1b);
		canine_lower_left1a.addChild(canine_lower_left1b);
		tail3.addChild(tail4);
		body2.addChild(arm_left1a);
		neck2.addChild(head1);
		neck1.addChild(neck2);
		arm_left1a.addChild(arm_left1b);
		head1.addChild(crest_right2);
		leg_right1a.addChild(leg_right1b);
		body_base.addChild(tail1);
		head1.addChild(canine_upper_left1a);
		jaw.addChild(canine_lower_right1a);
		arm_left1b.addChild(arm_left1c);
		head1.addChild(crest_right1a);
		canine_upper_right1a.addChild(canine_upper_right1b);
		tail2.addChild(tail3);
		head1.addChild(teeth_upper_mid);
		head1.addChild(teeth_upper_left);
		head1.addChild(head2);
		leg_right1c.addChild(foot_claw_right1);
		arm_right1b.addChild(wingflap_right1);
		body_base.addChild(leg_left1a);
		jaw.addChild(canine_lower_left1a);
		leg_left1b.addChild(leg_left1c);
		body_base.addChild(leg_right1a);
		jaw.addChild(teeth_lower_left);
		arm_right1c.addChild(wingflap_right2);
		arm_left1b.addChild(wingflap_left1);
		head1.addChild(canine_upper_right1a);
		
		parts = new MowzieModelRenderer[] {
			body_base,
			body1,
			tail1,
			leg_left1a,
			leg_right1a,
			body2,
			vertebrate3,
			neck1,
			arm_left1a,
			arm_right1a,
			vertebrate1,
			vertebrate2,
			neck2,
			head1,
			head2,
			teeth_upper_left,
			teeth_upper_right,
			canine_upper_left1a,
			canine_upper_right1a,
			teeth_upper_mid,
			crest_left1a,
			crest_right1a,
			crest_mid1a,
			crest_left2,
			crest_right2,
			jaw,
			teeth_lower_left,
			teeth_lower_right,
			canine_lower_left1a,
			canine_lower_right1a,
			teeth_lower_mid,
			canine_lower_left1b,
			canine_lower_right1b,
			canine_upper_left1b,
			canine_upper_right1b,
			crest_left1b,
			crest_right1b,
			crest_mid1b,
			arm_left1b,
			arm_left1c,
			wingflap_left1,
			claw_left1a,
			wingflap_left2,
			claw_left1b,
			arm_right1b,
			arm_right1c,
			wingflap_right1,
			claw_right1a,
			wingflap_right2,
			claw_right1b,
			tail2,
			tail3,
			tail4,
			tail5,
			leg_left1b,
			leg_left1c,
			foot_claw_left1,
			leg_right1b,
			leg_right1c,
			foot_claw_right1
	        };

		partsTailAndBum = new MowzieModelRenderer[] {
				body1,
				tail1,
				tail2,
				tail3,
				tail4,
				tail5

		};
		partsTail = new MowzieModelRenderer[] {
				tail1,
				tail2,
				tail3,
				tail4,
				tail5

		};
        
		setInitPose();
	}

	@Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
        GlStateManager.enableCull();
        GlStateManager.cullFace(CullFace.FRONT);
        wingflap_left1.showModel = false;
        wingflap_left2.showModel = false;
        wingflap_right1.showModel = false;
        wingflap_right2.showModel = false;
		crest_left1a.showModel = false;
		crest_left1b.showModel = false;
		crest_left2.showModel = false;
		crest_right1a.showModel = false;
		crest_right1b.showModel = false;
		crest_right2.showModel = false;
		crest_mid1a.showModel = false;
		crest_mid1b.showModel = false;
        wingflap_left1.showModel = true;
        wingflap_left2.showModel = true;
        wingflap_right1.showModel = true;
        wingflap_right2.showModel = true;
		crest_left1a.showModel = true;
		crest_left1b.showModel = true;
		crest_left2.showModel = true;
		crest_right1a.showModel = true;
		crest_right1b.showModel = true;
		crest_right2.showModel = true;
		crest_mid1a.showModel = true;
		crest_mid1b.showModel = true;
        GlStateManager.cullFace(CullFace.BACK);
        body_base.render(unitPixel);
        GlStateManager.disableCull();
    }

	public void setRotateAngle(MowzieModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
    public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
		EntityChiromawMatriarch chiromaw = (EntityChiromawMatriarch) entity;

        float globalSpeed = 1F;
        float globalDegree = 0.5F;
        float rippleSpeed = 1F;

        float frame = chiromaw.ticksExisted + partialRenderTicks;
        float flapFrame = chiromaw.flapTicks + partialRenderTicks;

        AnimationBlender<ModelChiromawMatriarch> blender = new AnimationBlender<>(this); //TODO Make this a field once animations are done
        
        float landingPercent = chiromaw.landingTimer.getAnimationProgressSmooth(partialRenderTicks);
        float nestingPercent = chiromaw.nestingTimer.getAnimationProgressSmooth(partialRenderTicks);
        float spinningPercent = chiromaw.spinningTimer.getAnimationProgressSmooth(partialRenderTicks);
        float flyingPercent = Math.max(0, 1.0f - landingPercent - nestingPercent - spinningPercent);
        float featherControl = (float) ((Math.sin(0.05 * frame - Math.cos(0.05 * frame))) * 0.5);

        //Nesting animation state
        blender.addState(model -> {
        	model.setToInitPose();
        	jaw.rotateAngleX = 0.8651597102135892F;
        	head1.rotateAngleX = 0.136659280431156F;
        	crest_left1a.rotateAngleX -= 0.25 * featherControl * Math.cos(0.5 * frame);
        	crest_right1a.rotateAngleX -= 0.25 * featherControl * Math.cos(0.5 * frame);
        	crest_mid1a.rotateAngleX -= 0.25 * featherControl * Math.cos(0.5 * frame);
        	walk(model.jaw, rippleSpeed * 0.125f, globalDegree * 0.5f, false, 2.0f, 0f, frame, 1F);
        	walk(model.body2, rippleSpeed * 0.125f, globalDegree * 0.125f, true, 2.0f, 0f, frame, 1F);
        	walk(model.arm_right1c, rippleSpeed * 0.125f, globalDegree * 0.125f, false, 2.0f, 0f, frame, 1F);
        	walk(model.arm_left1c, rippleSpeed * 0.125f, globalDegree * 0.125f, true, 2.0f, 0f, frame, 1F);
        	swing(model.arm_right1c, rippleSpeed * 0.125f, globalDegree * 0.125f, true, 2.0f, 0f, frame, 1F);
        	swing(model.arm_left1c, rippleSpeed * 0.125f, globalDegree * 0.125f, false, 2.0f, 0f, frame, 1F);
        	chainSwing(model.partsTail, rippleSpeed * 0.125f, globalDegree * 0.25f, 2f, frame, 1F);
        }, () -> nestingPercent);

        class FlyingPose {
        	public void apply(ModelChiromawMatriarch model, float globalSpeed, float globalDegree, float rippleSpeed) {
        		head1.rotateAngleX = -0.398132F;
				if (!chiromaw.isBeingRidden()) {
					leg_right1a.rotateAngleX = 0.7285004297824331F;
					leg_left1a.rotateAngleX = 0.7285004297824331F;
					leg_right1b.rotateAngleX = 1.367539130330308F;
					leg_left1b.rotateAngleX = 1.367539130330308F;
				}
        		flap(model.arm_right1a, chiromaw.flapSpeed, globalDegree * 1.2f, false, 2.0f, 0f, flapFrame, 1F);
    			swing(model.arm_right1b, chiromaw.flapSpeed, globalDegree * 1.5f, false, 2.8f, 0.5f, flapFrame, 1F);
    			flap(model.arm_right1c, chiromaw.flapSpeed, globalDegree * 2.5f, false, 2.0f, 0f, flapFrame, 1F);
    			
    			flap(model.arm_left1a, chiromaw.flapSpeed, globalDegree * 1.2f, true, 2.0f, 0f, flapFrame, 1F);
    			swing(model.arm_left1b, chiromaw.flapSpeed, globalDegree * 1.5f, true, 2.8f, -0.5f, flapFrame, 1F);
    			flap(model.arm_left1c, chiromaw.flapSpeed, globalDegree * 2.5f, true, 2.0f, 0f, flapFrame, 1F);
    			
    			walk(model.arm_right1b, chiromaw.flapSpeed, globalDegree * 1f, true, 1.2f, 0.15f, flapFrame, 1F);
    			walk(model.arm_right1c, chiromaw.flapSpeed, globalDegree * 1.2f, false, 1.2f, -0.9f, flapFrame, 1F);
    			walk(model.arm_left1b, chiromaw.flapSpeed, globalDegree * 1f, true, 1.2f, 0.15f, flapFrame, 1F);
    			walk(model.arm_left1c, chiromaw.flapSpeed, globalDegree * 1.2f, false, 1.2f, -0.9f, flapFrame, 1F);
    			
    			chainWave(model.partsTailAndBum, rippleSpeed * 0.5f, globalDegree * 0.25f, 2f, frame, 1F);
    			swing(model.head1, rippleSpeed * 0.5f, globalDegree * 0.25f, false, 2.0f, 0f, frame, 1F);
    			walk(model.head1, rippleSpeed * 0.5f, globalDegree * 0.25f, true, 2.0f, 0f, frame, 1F);
    			walk(model.jaw, rippleSpeed * 0.5f, globalDegree * 1f, true, 2.0f, 0f, frame, 1F);
        	}
        }

        class SpinningPose {
        	public void apply(ModelChiromawMatriarch model, float globalSpeed, float globalDegree, float rippleSpeed) {
            	model.setToInitPose();
            	jaw.rotateAngleX = 0.8651597102135892F;
            	head1.rotateAngleX = 0.136659280431156F;
        	}
        }

        FlyingPose idlePose = new FlyingPose();
        SpinningPose spinningPose = new SpinningPose();

        //Idle (flying) animation state
        blender.addState(model -> {
        	model.setToInitPose();

        	idlePose.apply(model, globalSpeed, globalDegree, rippleSpeed);
        }, () -> flyingPercent);

        //Landing animation state
        blender.addState(model -> {
        	model.setToInitPose();

	 		model.arm_right1a.rotateAngleX = 0.3553564018453205F;
	 		model.arm_left1a.rotateAngleX = 0.3553564018453205F;

	 		idlePose.apply(model, 1.5f, 0.25f, 0);
        }, () -> landingPercent);

        //Spinning animation state
        blender.addState(model -> {
        	model.setToInitPose();

        	spinningPose.apply(model, 0f, 0f, 0);
        }, () -> spinningPercent);

        blender.setAngles(false);
    }

	//just some helpers for future
	public float convertDegtoRad(float angleIn) {
		return angleIn * ((float) Math.PI / 180F);
	}

	public float convertRadtoDeg(float radIn) {
		return radIn * 180F / ((float) Math.PI);
	}
}
