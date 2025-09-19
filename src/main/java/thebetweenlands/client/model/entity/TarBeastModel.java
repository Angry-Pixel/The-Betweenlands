package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.TarBeast;

public class TarBeastModel extends MowzieModelBase<TarBeast> {

	private final ModelPart waistJoint;
	private final ModelPart waist;
	public final ModelPart waistTar;
	private final ModelPart body;
	private final ModelPart chest;
	private final ModelPart neckJoint;
	private final ModelPart headJoint;
	private final ModelPart head;
	public final ModelPart teeth;
	private final ModelPart jaw;
	public final ModelPart jawTar;
	private final ModelPart rightShoulder;
	private final ModelPart rightArm;
	private final ModelPart leftShoulder;
	private final ModelPart leftArm;
	private final ModelPart leftLeg1;
	private final ModelPart leftLeg2;
	private final ModelPart rightLeg1;
	private final ModelPart rightLeg2;

	public TarBeastModel(ModelPart root) {
		super(root);
		this.waistJoint = root.getChild("waist_joint");
		this.waist = this.waistJoint.getChild("waist");
		this.waistTar = this.waist.getChild("waist_tar");
		this.body = this.waist.getChild("body");
		this.chest = this.body.getChild("chest");
		this.neckJoint = this.chest.getChild("neck_joint");
		this.headJoint = this.neckJoint.getChild("neck").getChild("head_joint");
		this.head = this.headJoint.getChild("head");
		this.teeth = this.head.getChild("teeth");
		this.jaw = this.head.getChild("head_connector").getChild("jaw");
		this.jawTar = this.jaw.getChild("jaw_tar");
		this.rightShoulder = this.chest.getChild("right_chest").getChild("right_shoulder");
		this.rightArm = this.rightShoulder.getChild("right_arm_1").getChild("right_arm_2");
		this.leftShoulder = this.chest.getChild("left_chest").getChild("left_shoulder");
		this.leftArm = this.leftShoulder.getChild("left_arm_1").getChild("left_arm_2");
		this.leftLeg1 = this.waistJoint.getChild("left_leg_1");
		this.leftLeg2 = this.leftLeg1.getChild("left_leg_2");
		this.rightLeg1 = this.waistJoint.getChild("right_leg_1");
		this.rightLeg2 = this.rightLeg1.getChild("right_leg_2");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var waistJoint = partDefinition.addOrReplaceChild("waist_joint", CubeListBuilder.create(),
			PartPose.offset(0.0F, 8.5F, 2.0F));
		var waist = waistJoint.addOrReplaceChild("waist", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-3.68F, -0.6577650255681116F, -1.948538362944113F, 8, 5, 8),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5009094953223726F, 0.0F, -0.091106186954104F));
		waist.addOrReplaceChild("waist_tar", CubeListBuilder.create()
				.texOffs(33, 0).addBox(-3.5F, 0.0F, -5.0F, 7, 7, 5),
			PartPose.offsetAndRotation(0.31843071366249476F, 3.843838169543026F, 5.5595423534164485F, -0.4991641660703782F, 0.04363323129985824F, 0.07993607974134029F));

		var body_base = waist.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 15).addBox(-5.0F, -10.0F, -8.0F, 10, 10, 8),
			PartPose.offsetAndRotation(0.31843071366249476F, -0.6561618304569734F, 6.0595423534164485F, 0.36425021489121656F, 0.0F, 0.0F));
		var chestpiece_invisible = body_base.addOrReplaceChild("chest", CubeListBuilder.create()
				.texOffs(150, 0).addBox(-6.0F, -6.0F, -4.0F, 12, 7, 5),
			PartPose.offset(0.0F, -8.0F, -0.5F));
		var chestpiece_right = chestpiece_invisible.addOrReplaceChild("right_chest", CubeListBuilder.create()
				.texOffs(40, 34).addBox(-6.0F, -7.01F, 0.0F, 6, 8, 9),
			PartPose.offsetAndRotation(0.07999999999999996F, -2.0999999999999996F, -8.110000000000001F, -0.36425021489121645F, 0.045553093477051984F, 0.0F));
		var chestpiece_left = chestpiece_invisible.addOrReplaceChild("left_chest", CubeListBuilder.create()
				.texOffs(0, 34).addBox(0.0F, -7.0F, 0.0F, 7, 8, 9),
			PartPose.offsetAndRotation(0.07999999999999996F, -2.0999999999999996F, -8.110000000000001F, -0.3651228795172137F, -0.08220500776893293F, 0.04869468613064177F));

		var neckJoint = chestpiece_invisible.addOrReplaceChild("neck_joint", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0, 0, 0),
			PartPose.offsetAndRotation(0.08F, -6.1F, -0.11F, -0.863065315111196F, 0.06928957130417489F, 0.059166661642607775F));
		var neck = neckJoint.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(120, 0).addBox(-3.0F, -5.0F, -4.0F, 6, 6, 5),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1362093431334666F, 0.0F, 0.0F));
		var headJoint = neck.addOrReplaceChild("head_joint", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, -5.0F, -1.0F, -1.1362093430483085F, 0.0F, 0.0F));
		var headbase = headJoint.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(120, 12).addBox(-4.0F, -2.9F, -7.0F, 8, 6, 8),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		headbase.addOrReplaceChild("teeth", CubeListBuilder.create()
				.texOffs(120, 70).addBox(-4.01F, 0.0F, 0.0F, 8, 2, 5),
			PartPose.offset(0.0F, 3.0F, -7.0F));
		var headconnection = headbase.addOrReplaceChild("head_connector", CubeListBuilder.create()
				.texOffs(120, 27).addBox(-4.0F, 0.0F, -2.0F, 8, 3, 3),
			PartPose.offset(0.0F, 3.0F, 0.0F));
		var nose = headbase.addOrReplaceChild("nose", CubeListBuilder.create()
				.texOffs(120, 58).addBox(-1.0F, -6.0F, -1.0F, 2, 6, 4),
			PartPose.offsetAndRotation(0.0F, 2.5F, -7.0F, 0.091106186954104F, 0.0F, 0.0F));
		nose.addOrReplaceChild("nose_crane", CubeListBuilder.create()
				.texOffs(133, 58).addBox(-1.0F, 0.0F, 0.0F, 2, 4, 7),
			PartPose.offsetAndRotation(0.0F, -6.0F, 3.0F, -0.136659280431156F, 0.0F, 0.0F));
		var jaw = headconnection.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(120, 34).addBox(-3.5F, 0.0F, -5.0F, 7, 3, 5),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.7740535232594852F, 0.0F, 0.0F));
		jaw.addOrReplaceChild("jaw_tar", CubeListBuilder.create()
				.texOffs(120, 43).addBox(-3.0F, -2.7F, 0.01F, 6, 7, 7),
			PartPose.offsetAndRotation(0.0F, 3.0F, -5.0F, -0.7740535232594852F, 0.0F, 0.0F));

		var shoulder_right = chestpiece_right.addOrReplaceChild("right_shoulder", CubeListBuilder.create()
				.texOffs(40, 52).addBox(-3.0F, -1.0F, -3.5F, 5, 8, 7),
			PartPose.offsetAndRotation(-6.0F, -5.0F, 5.0F, -0.27314402793711257F, 0.045553093477052F, 0.22759093446006054F));
		var armright_1 = shoulder_right.addOrReplaceChild("right_arm_1", CubeListBuilder.create()
				.texOffs(40, 68).addBox(-1.5F, 0.0F, -2.5F, 4, 8, 5),
			PartPose.offsetAndRotation(-1.0F, 6.0F, 0.0F, -0.091106186954104F, -0.045553093477052F, 0.0F));
		armright_1.addOrReplaceChild("right_arm_2", CubeListBuilder.create()
				.texOffs(40, 82).addBox(-2.0F, 0.0F, -3.5F, 5, 13, 7),
			PartPose.offsetAndRotation(0.0F, 7.0F, 0.0F, -0.136659280431156F, 0.0F, -0.136659280431156F));

		var shoulder_left = chestpiece_left.addOrReplaceChild("left_shoulder", CubeListBuilder.create()
				.texOffs(0, 52).addBox(-2.0F, -1.0F, -3.0F, 5, 6, 6),
			PartPose.offsetAndRotation(7.0F, -5.0F, 5.5F, -0.31869712141416456F, -0.091106186954104F, -0.091106186954104F));
		var armleft_1 = shoulder_left.addOrReplaceChild("left_arm_1", CubeListBuilder.create()
				.texOffs(0, 65).addBox(-1.5F, 0.0F, -2.5F, 4, 7, 5),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, -0.045553093477052F, 0.0F, 0.045553093477052F));
		armleft_1.addOrReplaceChild("left_arm_2", CubeListBuilder.create()
				.texOffs(0, 78).addBox(-2.0F, -1.0F, -3.0F, 5, 10, 6),
			PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, -0.5009094953223726F, 0.0F, 0.0F));

		var legleft1 = waistJoint.addOrReplaceChild("left_leg_1", CubeListBuilder.create()
				.texOffs(70, 0).addBox(-2.9F, -1.5F, -2.0F, 4, 7, 4),
			PartPose.offsetAndRotation(4.521076710712751F, 2.0212720258526318F, 2.535163171244812F, -0.08917911277878962F, -0.07988788478262537F, -0.1349508555944485F));
		legleft1.addOrReplaceChild("left_leg_2", CubeListBuilder.create()
				.texOffs(70, 12).addBox(-3.5F, -0.5F, -2.5F, 5, 9, 5),
			PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 0.091106186954104F, -0.045553093477052F, 0.136659280431156F));

		var legright1 = waistJoint.addOrReplaceChild("right_leg_1", CubeListBuilder.create()
				.texOffs(95, 0).addBox(-1.0F, -1.5F, -2.0F, 4, 7, 4),
			PartPose.offsetAndRotation(-3.4856463673423184F, 2.312359468842203F, 2.2950514225232173F, -0.3732619332147009F, -0.006773398892451963F, 0.11212011236447327F));
		legright1.addOrReplaceChild("right_leg_2", CubeListBuilder.create()
				.texOffs(95, 12).addBox(-1.5F, -0.5F, -2.5F, 5, 9, 5),
			PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 0.36425021489121656F, 0.045553093477052F, -0.091106186954104F));

		return LayerDefinition.create(definition, 256, 128);
	}

	@Override
	public void setupAnim(TarBeast entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		float globalSpeed = 0.6F;
		float globalDegree = 1.8F;
		float globalHeight = 1.8F;

//      this.neck.yRot += (netHeadYaw / Mth.DEG_TO_RAD) / 2;
		this.head.yRot += netHeadYaw / Mth.RAD_TO_DEG;
//      this.neck.xRot += (headPitch / Mth.DEG_TO_RAD) / 2;
		this.head.xRot += headPitch / Mth.RAD_TO_DEG;
		this.root.y += 5.0F * limbSwingAmount;
		this.bob(this.waistJoint, globalSpeed, 1.5F * globalHeight, false, limbSwing, limbSwingAmount);
		this.walk(this.waist, globalSpeed, 0.1F * globalHeight, false, 0.75F, 0.1F, limbSwing, limbSwingAmount);
		this.walk(this.body, globalSpeed, 0.1F * globalHeight, false, 0.75F, 0.05F, limbSwing, limbSwingAmount);
		this.walk(this.chest, globalSpeed, 0.05F * globalHeight, false, 0.75F, 0.05F, limbSwing, limbSwingAmount);
		this.walk(this.neckJoint, globalSpeed, 0.15F * globalHeight, true, 0.5F + 0.75F, -0.1F, limbSwing, limbSwingAmount);
		this.walk(this.headJoint, globalSpeed, 0.15F * globalHeight, true, 0.5F + 0.75F, -0.1F, limbSwing, limbSwingAmount);
		this.walk(this.rightShoulder, globalSpeed, 0.25F * globalHeight, true, 0.75F, -0.25F, limbSwing, limbSwingAmount);

		this.walk(this.leftLeg1, globalSpeed, 1.3F * globalDegree, false, 0.0F + 0.7F, 0.1F, limbSwing, limbSwingAmount);
		this.walk(this.rightLeg1, globalSpeed, 1.3F * globalDegree, false, 0.0F, 0.1F, limbSwing, limbSwingAmount);
		this.walk(this.leftLeg2, globalSpeed, 1.2F * globalDegree, true, 0.5F + 0.7F, 0.5F, limbSwing, limbSwingAmount);
		this.walk(this.rightLeg2, globalSpeed, 1.2F * globalDegree, true, 0.5F, 0.5F, limbSwing, limbSwingAmount);

		this.walk(this.rightShoulder, globalSpeed, 0.4F * globalDegree, true, 0.75F, 0.2F, limbSwing, limbSwingAmount);
		this.walk(this.rightArm, globalSpeed, 0.5F * globalDegree, true, 1.5F + 0.75F, -0.7F, limbSwing, limbSwingAmount);

		this.flap(this.leftShoulder, globalSpeed, 0.2F * globalHeight, true, 1.0F, -0.8F, limbSwing, limbSwingAmount);
		this.flap(this.leftArm, globalSpeed, 0.2F * globalHeight, true, 0.0F, 0.3F, limbSwing, limbSwingAmount);

		this.walk(this.jaw, globalSpeed, 0.3F * globalHeight, true, 1.0F, 0.0F, limbSwing, limbSwingAmount);
		this.walk(this.jawTar, globalSpeed, 0.3f * globalHeight, false, 1.0F, 0.0F, limbSwing, limbSwingAmount);

		this.walk(this.waistTar, globalSpeed, 0.2F * globalHeight, true, 1.75F, 0.2F, limbSwing, limbSwingAmount);
		this.walk(this.teeth, globalSpeed, 0.4F * globalHeight, true, 1.75F, 0.2F, limbSwing, limbSwingAmount);
		this.walk(this.jawTar, globalSpeed, 0.4F * globalHeight, true, 0.75F, 0.2F, limbSwing, limbSwingAmount);
	}
}
