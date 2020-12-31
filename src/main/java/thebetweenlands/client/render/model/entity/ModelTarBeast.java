package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelTarBeast extends MowzieModelBase {
	public MowzieModelRenderer waistJoint;
	public MowzieModelRenderer waist;
	public MowzieModelRenderer legleft1;
	public MowzieModelRenderer legright1;
	public MowzieModelRenderer body_base;
	public MowzieModelRenderer drippingtar2_keepstraight;
	public MowzieModelRenderer chestpiece_invisible;
	public MowzieModelRenderer chestpiece_left;
	public MowzieModelRenderer chestpiece_right;
	public MowzieModelRenderer neckJoint;
	public MowzieModelRenderer shoulder_left;
	public MowzieModelRenderer armleft_1;
	public MowzieModelRenderer armleft_2;
	public MowzieModelRenderer shoulder_right;
	public MowzieModelRenderer armright_1;
	public MowzieModelRenderer armright_2;
	public MowzieModelRenderer neck;
	public MowzieModelRenderer headJoint;
	public MowzieModelRenderer headbase;
	public MowzieModelRenderer headconnection;
	public MowzieModelRenderer nose1;
	public MowzieModelRenderer teeth_keepstraight;
	public MowzieModelRenderer jaw;
	public MowzieModelRenderer drippingtar1_keepstraight;
	public MowzieModelRenderer nosecrane;
	public MowzieModelRenderer legleft2;
	public MowzieModelRenderer legright2;

	public ModelTarBeast() {
		this.textureWidth = 256;
		this.textureHeight = 128;
		this.waist = new MowzieModelRenderer(this, 0, 0);
		this.waist.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.waist.addBox(-3.68F, -0.6577650255681116F, -1.948538362944113F, 8, 5, 8, 0.0F);
		this.setRotation(waist, 0.5009094953223726F, 0.0F, -0.091106186954104F);
		this.headconnection = new MowzieModelRenderer(this, 120, 27);
		this.headconnection.setRotationPoint(0.0F, 3.0F, 0.0F);
		this.headconnection.addBox(-4.0F, 0.0F, -2.0F, 8, 3, 3, 0.0F);
		this.chestpiece_invisible = new MowzieModelRenderer(this, 150, 0);
		this.chestpiece_invisible.setRotationPoint(0.0F, -8.0F, -0.5F);
		this.chestpiece_invisible.addBox(-6.0F, -6.0F, -4.0F, 12, 7, 5, 0.0F);
		this.body_base = new MowzieModelRenderer(this, 0, 15);
		this.body_base.setRotationPoint(0.31843071366249476F, -0.6561618304569734F, 6.0595423534164485F);
		this.body_base.addBox(-5.0F, -10.0F, -8.0F, 10, 10, 8, 0.0F);
		this.setRotation(body_base, 0.36425021489121656F, 0.0F, 0.0F);
		this.chestpiece_right = new MowzieModelRenderer(this, 40, 34);
		this.chestpiece_right.setRotationPoint(0.07999999999999996F, -2.0999999999999996F, -8.110000000000001F);
		this.chestpiece_right.addBox(-6.0F, -7.01F, 0.0F, 6, 8, 9, 0.0F);
		this.setRotation(chestpiece_right, -0.36425021489121645F, 0.045553093477051984F, 0.0F);
		this.nose1 = new MowzieModelRenderer(this, 120, 58);
		this.nose1.setRotationPoint(0.0F, 2.5F, -7.0F);
		this.nose1.addBox(-1.0F, -6.0F, -1.0F, 2, 6, 4, 0.0F);
		this.setRotation(nose1, 0.091106186954104F, 0.0F, 0.0F);
		this.armleft_1 = new MowzieModelRenderer(this, 0, 65);
		this.armleft_1.setRotationPoint(0.0F, 4.0F, 0.0F);
		this.armleft_1.addBox(-1.5F, 0.0F, -2.5F, 4, 7, 5, 0.0F);
		this.setRotation(armleft_1, -0.045553093477052F, 0.0F, 0.045553093477052F);
		this.shoulder_right = new MowzieModelRenderer(this, 40, 52);
		this.shoulder_right.setRotationPoint(-6.0F, -5.0F, 5.0F);
		this.shoulder_right.addBox(-3.0F, -1.0F, -3.5F, 5, 8, 7, 0.0F);
		this.setRotation(shoulder_right, -0.27314402793711257F, 0.045553093477052F, 0.22759093446006054F);
		this.legleft1 = new MowzieModelRenderer(this, 70, 0);
		this.legleft1.setRotationPoint(4.521076710712751F, 2.0212720258526318F, 2.535163171244812F);
		this.legleft1.addBox(-2.9F, -1.5F, -2.0F, 4, 7, 4, 0.0F);
		this.setRotation(legleft1, -0.08917911277878962F, -0.07988788478262537F, -0.1349508555944485F);
		this.chestpiece_left = new MowzieModelRenderer(this, 0, 34);
		this.chestpiece_left.setRotationPoint(0.07999999999999996F, -2.0999999999999996F, -8.110000000000001F);
		this.chestpiece_left.addBox(0.0F, -7.0F, 0.0F, 7, 8, 9, 0.0F);
		this.setRotation(chestpiece_left, -0.3651228795172137F, -0.08220500776893293F, 0.04869468613064177F);
		this.legleft2 = new MowzieModelRenderer(this, 70, 12);
		this.legleft2.setRotationPoint(0.0F, 5.0F, 0.0F);
		this.legleft2.addBox(-3.5F, -0.5F, -2.5F, 5, 9, 5, 0.0F);
		this.setRotation(legleft2, 0.091106186954104F, -0.045553093477052F, 0.136659280431156F);
		this.armright_1 = new MowzieModelRenderer(this, 40, 68);
		this.armright_1.setRotationPoint(-1.0F, 6.0F, 0.0F);
		this.armright_1.addBox(-1.5F, 0.0F, -2.5F, 4, 8, 5, 0.0F);
		this.setRotation(armright_1, -0.091106186954104F, -0.045553093477052F, 0.0F);
		this.legright1 = new MowzieModelRenderer(this, 95, 0);
		this.legright1.setRotationPoint(-3.4856463673423184F, 2.312359468842203F, 2.2950514225232173F);
		this.legright1.addBox(-1.0F, -1.5F, -2.0F, 4, 7, 4, 0.0F);
		this.setRotation(legright1, -0.3732619332147009F, -0.006773398892451963F, 0.11212011236447327F);
		this.nosecrane = new MowzieModelRenderer(this, 133, 58);
		this.nosecrane.setRotationPoint(0.0F, -6.0F, 3.0F);
		this.nosecrane.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 7, 0.0F);
		this.setRotation(nosecrane, -0.136659280431156F, 0.0F, 0.0F);
		this.headbase = new MowzieModelRenderer(this, 120, 12);
		this.headbase.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.headbase.addBox(-4.0F, -2.9F, -7.0F, 8, 6, 8, 0.0F);
		this.waistJoint = new MowzieModelRenderer(this, 0, 0);
		this.waistJoint.setRotationPoint(0.0F, 8.5F, 2.0F);
		this.waistJoint.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		this.headJoint = new MowzieModelRenderer(this, 0, 0);
		this.headJoint.setRotationPoint(0.0F, -5.0F, -1.0F);
		this.headJoint.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		this.setRotation(headJoint, -1.1362093430483085F, 0.0F, 0.0F);
		this.jaw = new MowzieModelRenderer(this, 120, 34);
		this.jaw.setRotationPoint(0.0F, 0.0F, -2.0F);
		this.jaw.addBox(-3.5F, 0.0F, -5.0F, 7, 3, 5, 0.0F);
		this.setRotation(jaw, 0.7740535232594852F, 0.0F, 0.0F);
		this.drippingtar1_keepstraight = new MowzieModelRenderer(this, 120, 43);
		this.drippingtar1_keepstraight.setRotationPoint(0.0F, 3.0F, -5.0F);
		this.drippingtar1_keepstraight.addBox(-3.0F, -2.7F, 0.01F, 6, 7, 7, 0.0F);
		this.setRotation(drippingtar1_keepstraight, -0.7740535232594852F, 0.0F, 0.0F);
		this.neck = new MowzieModelRenderer(this, 120, 0);
		this.neck.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.neck.addBox(-3.0F, -5.0F, -4.0F, 6, 6, 5, 0.0F);
		this.setRotation(neck, 1.1362093431334666F, 0.0F, 0.0F);
		this.armright_2 = new MowzieModelRenderer(this, 40, 82);
		this.armright_2.setRotationPoint(0.0F, 7.0F, 0.0F);
		this.armright_2.addBox(-2.0F, 0.0F, -3.5F, 5, 13, 7, 0.0F);
		this.setRotation(armright_2, -0.136659280431156F, 0.0F, -0.136659280431156F);
		this.legright2 = new MowzieModelRenderer(this, 95, 12);
		this.legright2.setRotationPoint(0.0F, 5.0F, 0.0F);
		this.legright2.addBox(-1.5F, -0.5F, -2.5F, 5, 9, 5, 0.0F);
		this.setRotation(legright2, 0.36425021489121656F, 0.045553093477052F, -0.091106186954104F);
		this.drippingtar2_keepstraight = new MowzieModelRenderer(this, 33, 0);
		this.drippingtar2_keepstraight.setRotationPoint(0.31843071366249476F, 3.843838169543026F, 5.5595423534164485F);
		this.drippingtar2_keepstraight.addBox(-3.5F, 0.0F, -5.0F, 7, 7, 5, 0.0F);
		this.setRotation(drippingtar2_keepstraight, -0.4991641660703782F, 0.04363323129985824F, 0.07993607974134029F);
		this.shoulder_left = new MowzieModelRenderer(this, 0, 52);
		this.shoulder_left.mirror = true;
		this.shoulder_left.setRotationPoint(7.0F, -5.0F, 5.5F);
		this.shoulder_left.addBox(-2.0F, -1.0F, -3.0F, 5, 6, 6, 0.0F);
		this.setRotation(shoulder_left, -0.31869712141416456F, -0.091106186954104F, -0.091106186954104F);
		this.neckJoint = new MowzieModelRenderer(this, 0, 0);
		this.neckJoint.setRotationPoint(0.08F, -6.1F, -0.11F);
		this.neckJoint.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		this.setRotation(neckJoint, -0.863065315111196F, 0.06928957130417489F, 0.059166661642607775F);
		this.armleft_2 = new MowzieModelRenderer(this, 0, 78);
		this.armleft_2.setRotationPoint(0.0F, 6.0F, 0.0F);
		this.armleft_2.addBox(-2.0F, -1.0F, -3.0F, 5, 10, 6, 0.0F);
		this.setRotation(armleft_2, -0.5009094953223726F, 0.0F, 0.0F);
		this.teeth_keepstraight = new MowzieModelRenderer(this, 120, 70);
		this.teeth_keepstraight.setRotationPoint(0.0F, 3.0F, -7.0F);
		this.teeth_keepstraight.addBox(-4.01F, 0.0F, 0.0F, 8, 2, 5, 0.0F);
		this.waistJoint.addChild(this.waist);
		this.headbase.addChild(this.headconnection);
		this.body_base.addChild(this.chestpiece_invisible);
		this.waist.addChild(this.body_base);
		this.chestpiece_invisible.addChild(this.chestpiece_right);
		this.headbase.addChild(this.nose1);
		this.shoulder_left.addChild(this.armleft_1);
		this.chestpiece_right.addChild(this.shoulder_right);
		this.waistJoint.addChild(this.legleft1);
		this.chestpiece_invisible.addChild(this.chestpiece_left);
		this.legleft1.addChild(this.legleft2);
		this.shoulder_right.addChild(this.armright_1);
		this.waistJoint.addChild(this.legright1);
		this.nose1.addChild(this.nosecrane);
		this.headJoint.addChild(this.headbase);
		this.neck.addChild(this.headJoint);
		this.headconnection.addChild(this.jaw);
		this.jaw.addChild(this.drippingtar1_keepstraight);
		this.neckJoint.addChild(this.neck);
		this.armright_1.addChild(this.armright_2);
		this.legright1.addChild(this.legright2);
		this.waist.addChild(this.drippingtar2_keepstraight);
		this.chestpiece_left.addChild(this.shoulder_left);
		this.chestpiece_invisible.addChild(this.neckJoint);
		this.armleft_1.addChild(this.armleft_2);
		this.headbase.addChild(this.teeth_keepstraight);

		setInitPose();
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
		setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, rotationPitch, entity);
		this.waistJoint.render(unitPixel);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		setToInitPose();
		//        f = entity.ticksExisted;
		//        f1 = 1f;

		float globalSpeed = 0.6f;
		float globalDegree = 1.8f;
		float globalHeight = 1.8f;

		//        neck.rotateAngleY += (f3 / (180f / (float) Math.PI)) / 2;
		headbase.rotateAngleY += (f3 / (180f / (float) Math.PI));
		//        neck.rotateAngleX += (f4 / (180f / (float) Math.PI)) / 2;
		headbase.rotateAngleX += (f4 / (180f / (float) Math.PI));
		waistJoint.rotationPointY += 5 * f1;
		bob(waistJoint, 1 * globalSpeed, 1.5f* globalHeight, false, f, f1);
		walk(waist, 1 * globalSpeed, 0.1f * globalHeight, false, 0.75f, 0.1f, f, f1);
		walk(body_base, 1 * globalSpeed, 0.1f * globalHeight, false, 0.75f, 0.05f, f, f1);
		walk(chestpiece_invisible, 1 * globalSpeed, 0.05f * globalHeight, false, 0.75f, 0.05f, f, f1);
		walk(neckJoint, 1 * globalSpeed, 0.15f * globalHeight, true, 0.5f + 0.75f, -0.1f, f, f1);
		walk(headJoint, 1 * globalSpeed, 0.15f * globalHeight, true, 0.5f + 0.75f, -0.1f, f, f1);
		walk(shoulder_right, 1 * globalSpeed, 0.25f * globalHeight, true, 0.75f, -0.25f, f, f1);

		walk(legleft1, 1f * globalSpeed, 1.3f * globalDegree, false, 0 + 0.7f, 0.1f, f, f1);
		walk(legright1, 1f * globalSpeed, 1.3f * globalDegree, false, 0, 0.1f, f, f1);
		walk(legleft2, 1f * globalSpeed, 1.2f * globalDegree, true, 0.5f + 0.7f, 0.5f, f, f1);
		walk(legright2, 1f * globalSpeed, 1.2f * globalDegree, true, 0.5f, 0.5f, f, f1);

		walk(shoulder_right, 1 * globalSpeed, 0.4f * globalDegree, true, 0.75f, 0.2f, f, f1);
		walk(armright_2, 1 * globalSpeed, 0.5f * globalDegree, true, 1.5f + 0.75f, -0.7f, f, f1);

		flap(shoulder_left, 1 * globalSpeed, 0.2f * globalHeight, true, 1, -0.8f, f, f1);
		flap(armleft_2, 1 * globalSpeed, 0.2f * globalHeight, true, 0, 0.3f, f, f1);

		walk(jaw, 1 * globalSpeed, 0.3f * globalHeight, true, 1, 0f, f, f1);
		walk(drippingtar1_keepstraight, 1 * globalSpeed, 0.3f * globalHeight, false, 1, 0f, f, f1);

		walk(drippingtar2_keepstraight, 1 * globalSpeed, 0.2f * globalHeight, true, 0.75f + 1, 0.2f, f, f1);
		walk(teeth_keepstraight, 1 * globalSpeed, 0.4f * globalHeight, true, 0.75f + 1, 0.2f, f, f1);
		walk(drippingtar1_keepstraight, 1 * globalSpeed, 0.4f * globalHeight, true, 0.75f + 0, 0.2f, f, f1);
	}
}
