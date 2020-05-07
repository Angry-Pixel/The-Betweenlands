package thebetweenlands.client.render.model.entity;

import java.util.Random;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntityDreadfulMummy;

@SideOnly(Side.CLIENT)
public class ModelDreadfulMummy extends MowzieModelBase {
	public MowzieModelRenderer shoulders;
	public MowzieModelRenderer shoulderright;
	public MowzieModelRenderer shoulderleft;
	public MowzieModelRenderer neck;
	public MowzieModelRenderer body_base;
	public MowzieModelRenderer aestheticleg1;
	public MowzieModelRenderer aestheticleg2;
	public MowzieModelRenderer frontLeftArmJoint;
	public MowzieModelRenderer frontRightArmJoint;
	public MowzieModelRenderer backLeftLegJoint;
	public MowzieModelRenderer armright1;
	public MowzieModelRenderer armleft1;
	public MowzieModelRenderer armleft2;
	public MowzieModelRenderer head1;
	public MowzieModelRenderer head2;
	public MowzieModelRenderer tongue1;
	public MowzieModelRenderer teeth_upper;
	public MowzieModelRenderer jaw;
	public MowzieModelRenderer teeth_lower;
	public MowzieModelRenderer tongue2;
	public MowzieModelRenderer tongue3;
	public MowzieModelRenderer tongue4;
	public MowzieModelRenderer tongue5;
	public MowzieModelRenderer sexybutt;
	public MowzieModelRenderer legright1;
	public MowzieModelRenderer legleft1;
	public MowzieModelRenderer legright2;
	public MowzieModelRenderer aestheticleg1a;
	public MowzieModelRenderer aestheticleg1b;
	public MowzieModelRenderer aestheticleg2a;
	public MowzieModelRenderer spiderleg1;
	public MowzieModelRenderer spiderleg1a;
	public MowzieModelRenderer spiderleg1b;
	public MowzieModelRenderer spiderleg2;
	public MowzieModelRenderer spiderleg2a;
	public MowzieModelRenderer spiderleg2b;
	public MowzieModelRenderer spiderleg3;
	public MowzieModelRenderer spiderleg3a;
	public MowzieModelRenderer spiderleg3b;
	MowzieModelRenderer[] tongue = new MowzieModelRenderer[] {};
	MowzieModelRenderer[] tentacle1 = new MowzieModelRenderer[] {};
	MowzieModelRenderer[] tentacle2 = new MowzieModelRenderer[] {};

	public ModelDreadfulMummy() {
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.aestheticleg1b = new MowzieModelRenderer(this, 91, 20);
		this.aestheticleg1b.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.aestheticleg1b.addBox(-1.0F, 0.0F, 0.0F, 2, 8, 2, 0.0F);
		this.setRotateAngle(aestheticleg1b, 1.0927506446736497F, 0.0F, 0.0F);
		this.spiderleg3 = new MowzieModelRenderer(this, 60, 42);
		this.spiderleg3.setRotationPoint(-0.5F, 0.0F, 0.0F);
		this.spiderleg3.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.setRotateAngle(spiderleg3, 0.8651597102135892F, -1.2747884856566583F, 3.141592653589793F);
		this.teeth_lower = new MowzieModelRenderer(this, 100, 25);
		this.teeth_lower.setRotationPoint(0.0F, 0.0F, -0.7F);
		this.teeth_lower.addBox(-2.5F, -1.0F, -5.0F, 5, 1, 5, 0.0F);
		this.legleft1 = new MowzieModelRenderer(this, 18, 43);
		this.legleft1.setRotationPoint(3.1819604078071397F, 4.8204065100680396F, -1.072154330214967F);
		this.legleft1.addBox(-1.0F, -1.0F, -1.0F, 2, 6, 2, 0.0F);
		this.setRotateAngle(legleft1, -0.6373942428283291F, -0.091106186954104F, -0.045553093477052F);
		this.spiderleg1 = new MowzieModelRenderer(this, 60, 0);
		this.spiderleg1.setRotationPoint(-0.5F, 1.0F, 0.5F);
		this.spiderleg1.addBox(-1.5F, 0.0F, -1.0F, 3, 10, 3, 0.0F);
		this.setRotateAngle(spiderleg1, -2.41309222380736F, -1.2747884856566583F, 0.0F);
		this.aestheticleg1 = new MowzieModelRenderer(this, 91, 0);
		this.aestheticleg1.setRotationPoint(-1.0F, -0.5F, 1.5F);
		this.aestheticleg1.addBox(-1.0F, 0.0F, -1.0F, 2, 6, 2, 0.0F);
		this.setRotateAngle(aestheticleg1, 0.5009094953223726F, 1.2747884856566583F, 3.141592653589793F);
		this.head1 = new MowzieModelRenderer(this, 90, 49);
		this.head1.setRotationPoint(-0.09F, -2.89F, 0.88F);
		this.head1.addBox(-4.07F, -4.1F, -8.09F, 8, 5, 9, 0.0F);
		this.setRotateAngle(head1, -1.593485607070823F, -0.18203784098300857F, 0.0F);
		this.frontLeftArmJoint = new MowzieModelRenderer(this, 0, 0);
		this.frontLeftArmJoint.setRotationPoint(2.0F, -2.0F, -2.0F);
		this.frontLeftArmJoint.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		this.sexybutt = new MowzieModelRenderer(this, 0, 31);
		this.sexybutt.setRotationPoint(-0.0035067239080670137F, 8.998236163378392F, -0.005657502465559849F);
		this.sexybutt.addBox(-4.32098020390357F, -0.5830789771757708F, -4.077009625879022F, 9, 5, 6, 0.0F);
		this.setRotateAngle(sexybutt, -0.5462880558742249F, 0.0F, 0.0F);
		this.tongue3 = new MowzieModelRenderer(this, 100, 44);
		this.tongue3.setRotationPoint(0.0F, 0.0F, -3.0F);
		this.tongue3.addBox(-1.5F, 0.0F, -3.0F, 3, 1, 3, 0.0F);
		this.setRotateAngle(tongue3, 0.4553564018453205F, 0.0F, 0.0F);
		this.neck = new MowzieModelRenderer(this, 100, 0);
		this.neck.setRotationPoint(0.37784292855481877F, -1.635799711236193F, -3.8335259815816247F);
		this.neck.addBox(-1.5F, -3.5F, -2.0F, 3, 4, 3, 0.0F);
		this.setRotateAngle(neck, 1.684591794024927F, 0.0F, 0.091106186954104F);
		this.legright1 = new MowzieModelRenderer(this, 0, 43);
		this.legright1.setRotationPoint(-2.8180395921928603F, 4.8204065100680396F, -1.072154330214967F);
		this.legright1.addBox(-1.0F, -1.0F, -1.0F, 2, 9, 2, 0.0F);
		this.setRotateAngle(legright1, -1.0016444577195458F, 0.18203784098300857F, 0.0F);
		this.shoulderright = new MowzieModelRenderer(this, 0, 17);
		this.shoulderright.setRotationPoint(0.0F, 2.5000000000000004F, -0.49999999999999867F);
		this.shoulderright.addBox(-4.8F, -5.0F, -3.0F, 5, 6, 7, 0.0F);
		this.setRotateAngle(shoulderright, 1.1848398752279146F, 0.026542406431980956F, 0.17827053243801907F);
		this.armright1 = new MowzieModelRenderer(this, 45, 43);
		this.armright1.setRotationPoint(-4.1825438415682195F, -2.157008565237252F, 1.6478898909504247F);
		this.armright1.addBox(-2.0F, -1.0F, -1.5F, 2, 8, 2, 0.0F);
		this.setRotateAngle(armright1, -1.1828318858978228F, 0.13591518556824927F, 0.01249444302571553F);
		this.spiderleg2 = new MowzieModelRenderer(this, 60, 21);
		this.spiderleg2.setRotationPoint(0.5F, 1.0F, 0.5F);
		this.spiderleg2.addBox(-1.5F, 0.0F, -1.0F, 3, 8, 3, 0.0F);
		this.setRotateAngle(spiderleg2, -2.367539130330308F, 1.0471975511965976F, 0.0F);
		this.shoulders = new MowzieModelRenderer(this, 0, 0);
		this.shoulders.setRotationPoint(0.0F, 0.0F, -3.5F);
		this.shoulders.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		this.frontRightArmJoint = new MowzieModelRenderer(this, 0, 0);
		this.frontRightArmJoint.setRotationPoint(-1.5F, -2.0F, -1.5F);
		this.frontRightArmJoint.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		this.spiderleg1a = new MowzieModelRenderer(this, 73, 0);
		this.spiderleg1a.setRotationPoint(0.0F, 10.0F, 0.0F);
		this.spiderleg1a.addBox(-1.01F, 0.0F, -1.0F, 2, 18, 2, 0.0F);
		this.setRotateAngle(spiderleg1a, 1.8212510744560826F, 0.0F, 0.0F);
		this.spiderleg3a = new MowzieModelRenderer(this, 69, 42);
		this.spiderleg3a.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.spiderleg3a.addBox(-1.01F, 0.0F, -1.0F, 2, 14, 2, 0.0F);
		this.setRotateAngle(spiderleg3a, 1.730144887501979F, 0.0F, 0.0F);
		this.head2 = new MowzieModelRenderer(this, 100, 8);
		this.head2.setRotationPoint(-0.07F, 0.9F, -1.09F);
		this.head2.addBox(-3.5F, 0.0F, -2.0F, 7, 3, 3, 0.0F);
		this.setRotateAngle(head2, -0.091106186954104F, 0.0F, 0.0F);
		this.aestheticleg2 = new MowzieModelRenderer(this, 91, 31);
		this.aestheticleg2.setRotationPoint(1.0F, -1.0F, 0.0F);
		this.aestheticleg2.addBox(-1.0F, 0.0F, -1.0F, 2, 6, 2, 0.0F);
		this.setRotateAngle(aestheticleg2, 0.36425021489121717F, -1.3203415791337105F, 3.141592653589793F);
		this.tongue4 = new MowzieModelRenderer(this, 113, 44);
		this.tongue4.setRotationPoint(0.0F, 1.0F, -3.0F);
		this.tongue4.addBox(-1.51F, -1.0F, -2.0F, 3, 1, 2, 0.0F);
		this.setRotateAngle(tongue4, -0.31869712141416456F, 0.0F, 0.0F);
		this.teeth_upper = new MowzieModelRenderer(this, 100, 32);
		this.teeth_upper.setRotationPoint(-0.06908394214624095F, 0.9027403801674749F, -2.7864102424740955F);
		this.teeth_upper.addBox(-3.0F, 0.0F, -5.0F, 6, 1, 5, 0.0F);
		this.aestheticleg2a = new MowzieModelRenderer(this, 91, 40);
		this.aestheticleg2a.setRotationPoint(0.0F, 6.0F, -1.0F);
		this.aestheticleg2a.addBox(-1.01F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		this.setRotateAngle(aestheticleg2a, 0.6373942428283291F, 0.0F, 0.0F);
		this.spiderleg1b = new MowzieModelRenderer(this, 82, 0);
		this.spiderleg1b.setRotationPoint(0.0F, 18.0F, -1.0F);
		this.spiderleg1b.addBox(-1.0F, 0.0F, 0.0F, 2, 18, 2, 0.0F);
		this.setRotateAngle(spiderleg1b, 0.6373942428283291F, 0.0F, 0.0F);
		this.jaw = new MowzieModelRenderer(this, 100, 15);
		this.jaw.setRotationPoint(0.0F, 2.0000000000000004F, -2.0F);
		this.jaw.addBox(-3.0F, 0.0F, -6.0F, 6, 2, 7, 0.0F);
		this.setRotateAngle(jaw, 1.138303738150702F, 0.0F, 0.0F);
		this.tongue5 = new MowzieModelRenderer(this, 116, 48);
		this.tongue5.setRotationPoint(0.0F, 0.0F, -2.0F);
		this.tongue5.addBox(-1.0F, -1.0F, -2.0F, 2, 1, 2, 0.0F);
		this.setRotateAngle(tongue5, -0.36425021489121656F, 0.0F, 0.0F);
		this.spiderleg2a = new MowzieModelRenderer(this, 73, 21);
		this.spiderleg2a.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.spiderleg2a.addBox(-1.01F, 0.0F, -1.0F, 2, 16, 2, 0.0F);
		this.setRotateAngle(spiderleg2a, 1.8212510744560826F, 0.0F, 0.0F);
		this.body_base = new MowzieModelRenderer(this, 0, 0);
		this.body_base.setRotationPoint(0.002791314540518036F, 0.4909944110609308F, 0.5047597093941052F);
		this.body_base.addBox(-3.82F, -0.3765086368562779F, -4.4017040068619995F, 8, 10, 6, 0.0F);
		this.setRotateAngle(body_base, 1.2747884856566583F, 0.0F, 0.091106186954104F);
		this.tongue2 = new MowzieModelRenderer(this, 113, 39);
		this.tongue2.setRotationPoint(0.0F, -1.0F, -2.0F);
		this.tongue2.addBox(-1.51F, 0.0F, -3.0F, 3, 1, 3, 0.0F);
		this.setRotateAngle(tongue2, 0.5462880558742251F, 0.0F, 0.0F);
		this.tongue1 = new MowzieModelRenderer(this, 100, 39);
		this.tongue1.setRotationPoint(-0.06908394214624095F, 3.402740380167474F, -3.086410242474096F);
		this.tongue1.addBox(-1.5F, -1.0F, -2.0F, 3, 1, 3, 0.0F);
		this.setRotateAngle(tongue1, 0.27314402793711257F, 0.091106186954104F, 0.091106186954104F);
		this.shoulderleft = new MowzieModelRenderer(this, 25, 17);
		this.shoulderleft.setRotationPoint(0.0F, 2.5000000000000004F, -0.49999999999999867F);
		this.shoulderleft.addBox(-0.2F, -5.0F, -3.0F, 5, 6, 7, 0.0F);
		this.setRotateAngle(shoulderleft, 1.1848398752279146F, -0.026542406431980966F, 0.00394184147018885F);
		this.backLeftLegJoint = new MowzieModelRenderer(this, 0, 0);
		this.backLeftLegJoint.setRotationPoint(2.0F, 0.0F, 1.5F);
		this.backLeftLegJoint.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		this.armleft1 = new MowzieModelRenderer(this, 27, 43);
		this.armleft1.setRotationPoint(4.190142388244066F, -2.1605578702972807F, 1.6460954039271694F);
		this.armleft1.addBox(0.0F, -1.0F, -1.5F, 2, 10, 2, 0.0F);
		this.setRotateAngle(armleft1, -1.120634374244034F, 0.021097676907686946F, -0.2712049444367715F);
		this.aestheticleg1a = new MowzieModelRenderer(this, 91, 9);
		this.aestheticleg1a.setRotationPoint(0.0F, 6.0F, -1.0F);
		this.aestheticleg1a.addBox(-1.01F, 0.0F, 0.0F, 2, 8, 2, 0.0F);
		this.setRotateAngle(aestheticleg1a, 1.0016444577195458F, 0.0F, 0.0F);
		this.legright2 = new MowzieModelRenderer(this, 9, 43);
		this.legright2.setRotationPoint(-1.0F, 8.0F, -1.0F);
		this.legright2.addBox(-0.01F, 0.0F, 0.0F, 2, 10, 2, 0.0F);
		this.setRotateAngle(legright2, 0.36425021489121656F, 0.0F, 0.0F);
		this.armleft2 = new MowzieModelRenderer(this, 36, 43);
		this.armleft2.setRotationPoint(0.0F, 9.0F, 0.0F);
		this.armleft2.addBox(0.0F, 0.0F, -1.51F, 2, 8, 2, 0.0F);
		this.setRotateAngle(armleft2, -0.36425021489121656F, 0.0F, 0.0F);
		this.spiderleg2b = new MowzieModelRenderer(this, 82, 21);
		this.spiderleg2b.setRotationPoint(0.0F, 16.0F, -1.0F);
		this.spiderleg2b.addBox(-1.0F, 0.0F, 0.0F, 2, 18, 2, 0.0F);
		this.setRotateAngle(spiderleg2b, 0.6373942428283291F, 0.0F, 0.0F);
		this.spiderleg3b = new MowzieModelRenderer(this, 78, 42);
		this.spiderleg3b.setRotationPoint(0.0F, 14.0F, -1.0F);
		this.spiderleg3b.addBox(-0.9F, 0.0F, 0.0F, 2, 18, 2, 0.0F);
		this.setRotateAngle(spiderleg3b, 0.5462880558742251F, 0.0F, 0.0F);
		this.aestheticleg1a.addChild(this.aestheticleg1b);
		this.backLeftLegJoint.addChild(this.spiderleg3);
		this.jaw.addChild(this.teeth_lower);
		this.sexybutt.addChild(this.legleft1);
		this.frontLeftArmJoint.addChild(this.spiderleg1);
		this.shoulders.addChild(this.aestheticleg1);
		this.neck.addChild(this.head1);
		this.shoulders.addChild(this.frontLeftArmJoint);
		this.body_base.addChild(this.sexybutt);
		this.tongue2.addChild(this.tongue3);
		this.shoulders.addChild(this.neck);
		this.sexybutt.addChild(this.legright1);
		this.shoulders.addChild(this.shoulderright);
		this.shoulderright.addChild(this.armright1);
		this.frontRightArmJoint.addChild(this.spiderleg2);
		this.shoulders.addChild(this.frontRightArmJoint);
		this.spiderleg1.addChild(this.spiderleg1a);
		this.spiderleg3.addChild(this.spiderleg3a);
		this.head1.addChild(this.head2);
		this.shoulders.addChild(this.aestheticleg2);
		this.tongue3.addChild(this.tongue4);
		this.head1.addChild(this.teeth_upper);
		this.aestheticleg2.addChild(this.aestheticleg2a);
		this.spiderleg1a.addChild(this.spiderleg1b);
		this.head2.addChild(this.jaw);
		this.tongue4.addChild(this.tongue5);
		this.spiderleg2.addChild(this.spiderleg2a);
		this.shoulders.addChild(this.body_base);
		this.tongue1.addChild(this.tongue2);
		this.head1.addChild(this.tongue1);
		this.shoulders.addChild(this.shoulderleft);
		this.shoulders.addChild(this.backLeftLegJoint);
		this.shoulderleft.addChild(this.armleft1);
		this.aestheticleg1.addChild(this.aestheticleg1a);
		this.legright1.addChild(this.legright2);
		this.armleft1.addChild(this.armleft2);
		this.spiderleg2a.addChild(this.spiderleg2b);
		this.spiderleg3a.addChild(this.spiderleg3b);

		parts = new MowzieModelRenderer[] {shoulders, shoulderright, shoulderleft, neck, body_base, aestheticleg1, aestheticleg2, frontLeftArmJoint, frontRightArmJoint, backLeftLegJoint, armright1, armleft1, armleft2, head1, head2, tongue1, teeth_upper, jaw, teeth_lower, tongue2, tongue3, tongue4, tongue5, sexybutt, legright1, legleft1, legright2, aestheticleg1a, aestheticleg1b, aestheticleg2a, spiderleg1, spiderleg1a, spiderleg1b, spiderleg2, spiderleg2a, spiderleg2b, spiderleg3, spiderleg3a, spiderleg3b};
		tongue = new MowzieModelRenderer[] {tongue1, tongue2, tongue3, tongue4, tongue5};
		tentacle1 = new MowzieModelRenderer[] {aestheticleg1, aestheticleg1a, aestheticleg1b};
		tentacle2 = new MowzieModelRenderer[] {aestheticleg2, aestheticleg2a};
		setInitPose();
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		this.shoulders.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		EntityDreadfulMummy mummy = (EntityDreadfulMummy)entity;

		float partialTicks = f2 - entity.ticksExisted;
		
		float spawningProgress = mummy.getSpawningProgress(partialTicks);
		body_base.rotateAngleX -= Math.min((1 - spawningProgress) * 1.5f, 0.7f);
		head1.rotateAngleX -= Math.min((1 - spawningProgress) * 2.0f, 1.5f);
		
		legleft1.rotateAngleX += (1 - spawningProgress) * 0.85f;
		legright1.rotateAngleX += (1 - spawningProgress) * 0.85f;
		

		faceTarget(neck, 1, MathHelper.clamp(MathHelper.wrapDegrees(f3), -25, 25), MathHelper.clamp(MathHelper.wrapDegrees(f4), -35, 35));
		//        f = mummy.ticksExisted;
		//        f1 = 1f;

		if(mummy.deathTicks > 0) {
			double contractAngle = this.body_base.rotateAngleX - this.body_base.rotateAngleX / (mummy.deathTicks / 120.0D + 1.0D);
			this.body_base.rotateAngleX -= contractAngle;
			this.legright1.rotateAngleX += contractAngle;
			f1 += Math.cos(mummy.deathTicks) / 2.0F * Math.sin(mummy.deathTicks / 2.0D);

			if(mummy.deathTicks > 60) {
				float progress = (mummy.deathTicks - 60) / 40.0F;
				float rotateAngle = progress * (float)Math.PI * 2.0F / 8F;
				this.shoulders.rotateAngleX = rotateAngle;
				this.body_base.rotateAngleX = rotateAngle;
			}
		}

		float speed = 0.3f;
		float degree = 0.65f;
		float height = 0.7f;
		swing(frontLeftArmJoint, speed, degree * 0.5f, false, 0f, 0.3f, f, f1);
		swing(frontRightArmJoint, speed, degree * 0.5f, false, 0f, 0f, f, f1);
		walk(spiderleg1, speed, degree * 0.4f, true, (float) (Math.PI / 2), -0.1f, f, f1);
		walk(spiderleg1a, speed, degree * 0.4f, false, (float) (Math.PI/2), 0, f, f1);
		walk(spiderleg2, speed, degree * 0.4f, false, (float) (Math.PI/2), -0.1f, f, f1);
		walk(spiderleg2a, speed, degree * 0.4f, true, (float) (Math.PI/2), 0, f, f1);
		swing(backLeftLegJoint, speed, degree * 0.5f, false, (float) (Math.PI), 0f, f, f1);
		walk(spiderleg3, speed, degree * 0.4f, true, (float) (Math.PI / 2) + (float) (Math.PI), -0.1f, f, f1);
		walk(spiderleg3a, speed, degree * 0.4f, false, (float) (Math.PI/2) + (float) (Math.PI), 0, f, f1);
		walk(legright1, speed, degree * 0.6f, false, 2, 0.1f, f, f1);
		walk(legright2, speed, degree * 0.6f, false, 0, 0.3f, f, f1);

		bob(shoulders, 2 * speed, height, false, f, f1);
		walk(shoulders, 2 * speed, height * 0.05f, true, (float) (Math.PI), 0, f, f1);
		walk(neck, 2 * speed, height * 0.05f, true, (float) (Math.PI) + 0.5f, 0, f, f1);
		walk(head1, 2 * speed, height * 0.05f, true, (float) (Math.PI) + 1, 0, f, f1);
		walk(jaw, 2 * speed, height * 0.3f, false, (float) (Math.PI) + 1.5f, 0, f, f1);
		chainWave(tongue, 2 * speed, height * 0.2f, -3, f, f1);
		tongue1.rotateAngleX -= 0.2 * f1;
		swing(neck, speed, height * 0.2f, true, (float) (Math.PI) + 0.5f, 0, f, f1);
		swing(head1, speed, height * 0.2f, true, (float) (Math.PI) + 1, 0, f, f1);
		chainSwing(tongue, speed, height * 0.2f, -2, f, f1);

		walk(legleft1, 2 * speed, height * 0.3f, false, 0, 0, f, f1);
		walk(armright1, 2 * speed, height * 0.2f, false, (float) (Math.PI) - 1, 0, f, f1);
		walk(armleft1, 2 * speed, height * 0.2f, false, (float) (Math.PI) - 1, 0, f, f1);
		walk(armleft2, 2 * speed, height * 0.2f, false, (float) (Math.PI) - 3f, 0, f, f1);

		chainWave(tentacle1, 2 * speed, height * 0.2f, -3, f, f1);
		chainWave(tentacle2, 2 * speed, height * 0.2f, -2, f, f1);
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float f3, float f4, float delta) {
		super.setLivingAnimations(entity, f3, f4, delta);
		setToInitPose();
		float frame = entity.ticksExisted + delta;
		chainWave(tentacle1, 0.2f, 0.3f, -2, frame, 1);
		chainWave(tentacle2, 0.3f, 0.3f, -2, frame, 1);

		EntityDreadfulMummy mummy = (EntityDreadfulMummy) entity;

		if (mummy.currentEatPrey != null) {
			walk(neck, 0.8f, 0.7f, false, 0, -0.2f, frame, 1);
			walk(head1, 0.8f, 0.7f, true, 0, 0.2f, frame, 1);
			walk(jaw, 0.8f, 0.8f, true, -0.7f, -0.6f, frame, 1);
			walk(tongue1, 0.8f, 0.8f, true, -0.7f, -0.5f, frame, 1);
			swing(neck, 0.4f, 0.3f, false, 0, 0, frame, 1);
			swing(head1, 0.4f, 0.3f, false, 0, 0, frame, 1);
			tongue2.rotateAngleX += Math.PI;
			tongue3.rotateAngleX += Math.PI;
			tongue4.rotateAngleX += Math.PI;
			tongue5.rotateAngleX += Math.PI;
			armleft1.rotateAngleX -= 1.2;
		} else {
			chainWave(tongue, 0.2f, -0.3f, -3, frame, 1);
			walk(neck, 0.2f, 0.05f, true, 2, 0, frame, 1);
			walk(head1, 0.2f, 0.05f, true, 1, 0, frame, 1);
			if(mummy.deathTicks > 0) {
				Random rnd = new Random();
				rnd.setSeed(mummy.deathTicks);
				double rot = Math.min((mummy.deathTicks) / 80.0F, (Math.PI * 2.0F) / 7.0F);
				head1.rotateAngleX += rot;
				head1.rotateAngleY += rot;
				tongue1.rotateAngleZ -= rot;
			}
			walk(jaw, 0.2f, 0.2f, true, 0, 0.2f, frame, 1);
			walk(armleft1, 0.15f, 0.2f, true, 2, 0, frame, 1);
			walk(armleft2, 0.15f, 0.2f, true, 1, 0, frame, 1);
		}
	}
}