package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.CryptCrawler;

public class CryptCrawlerModel extends MowzieModelBase<CryptCrawler> {

	private final ModelPart root;
	private final ModelPart body_main;
	private final ModelPart body_lower;
	private final ModelPart neck;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart leg_front_left1;
	private final ModelPart leg_front_left2;
	private final ModelPart leg_front_left3;
	private final ModelPart leg_front_right1;
	private final ModelPart leg_front_right2;
	private final ModelPart leg_front_right3;
	private final ModelPart leg_back_left1;
	private final ModelPart leg_back_left2;
	private final ModelPart leg_back_left3;
	private final ModelPart leg_back_right1;
	private final ModelPart leg_back_right2;
	private final ModelPart leg_back_right3;
	private final ModelPart tail1;
	private final ModelPart tail2;
	private final ModelPart tail3;
	private final ModelPart tail4;
	private final ModelPart tinyurn1;
	private final ModelPart tinyurn2;
	private final ModelPart tinyurn3;

	public CryptCrawlerModel(ModelPart root) {
		this.root = root;
		this.body_main = root.getChild("body_main");
		this.body_lower = this.body_main.getChild("body_lower");
		this.neck = this.body_main.getChild("neck");
		this.head = this.neck.getChild("head");
		this.jaw = this.head.getChild("jaw");
		this.leg_front_left1 = this.body_main.getChild("left_front_leg_1");
		this.leg_front_left2 = this.leg_front_left1.getChild("left_front_leg_2");
		this.leg_front_left3 = this.leg_front_left2.getChild("left_front_leg_3");
		this.leg_front_right1 = this.body_main.getChild("right_front_leg_1");
		this.leg_front_right2 = this.leg_front_right1.getChild("right_front_leg_2");
		this.leg_front_right3 = this.leg_front_right2.getChild("right_front_leg_3");
		this.leg_back_left1 = this.body_lower.getChild("left_back_leg_1");
		this.leg_back_left2 = this.leg_back_left1.getChild("left_back_leg_2");
		this.leg_back_left3 = this.leg_back_left2.getChild("left_back_leg_3");
		this.leg_back_right1 = this.body_lower.getChild("right_back_leg_1");
		this.leg_back_right2 = this.leg_back_right1.getChild("right_back_leg_2");
		this.leg_back_right3 = this.leg_back_right2.getChild("right_back_leg_3");
		this.tail1 = this.body_lower.getChild("tail_1");
		this.tail2 = this.tail1.getChild("tail_2");
		this.tail3 = this.tail2.getChild("tail_3");
		this.tail4 = this.tail3.getChild("tail_4");
		this.tinyurn1 = this.body_lower.getChild("tiny_urn_1");
		this.tinyurn2 = this.body_lower.getChild("tiny_urn_2");
		this.tinyurn3 = this.body_lower.getChild("tiny_urn_3");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body_main = partDefinition.addOrReplaceChild("body_main", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0F, 0.0F, -8.0F, 8, 6, 8),
			PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.091106186954104F, 0.0F, 0.0F));
		var body_lower = body_main.addOrReplaceChild("body_lower", CubeListBuilder.create()
				.texOffs(0, 15).addBox(-4.01F, 0.0F, 0.0F, 8, 6, 8),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.22759093446006054F, 0.0F, 0.0F));
		var neck = body_main.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(0, 30).addBox(-3.01F, 0.0F, -3.0F, 6, 5, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.27314402793711257F, 0.0F, 0.0F));

		var leg_front_left1 = body_main.addOrReplaceChild("left_front_leg_1", CubeListBuilder.create()
				.texOffs(33, 27).addBox(-1.0F, -1.0F, -1.0F, 2, 6, 3),
			PartPose.offsetAndRotation(4.0F, 2.0F, -6.0F, 0.27314402793711257F, 0.0F, -0.136659280431156F));
		var leg_front_left2 = leg_front_left1.addOrReplaceChild("left_front_leg_2", CubeListBuilder.create()
				.texOffs(33, 37).addBox(-1.01F, 0.0F, -2.0F, 2, 6, 2),
			PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, -0.5918411493512771F, 0.0F, 0.0F));
		leg_front_left2.addOrReplaceChild("left_front_leg_3", CubeListBuilder.create()
				.texOffs(33, 46).addBox(-1.02F, -2.0F, -3.0F, 2, 2, 3),
			PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.5918411493512771F, 0.0F, 0.0F));

		var leg_front_right1 = body_main.addOrReplaceChild("right_front_leg_1", CubeListBuilder.create()
				.texOffs(48, 27).addBox(-1.0F, -1.0F, -1.0F, 2, 6, 3),
			PartPose.offsetAndRotation(-4.0F, 2.0F, -6.0F, 0.27314402793711257F, 0.0F, 0.136659280431156F));
		var leg_front_right2 = leg_front_right1.addOrReplaceChild("right_front_leg_2", CubeListBuilder.create()
				.texOffs(48, 37).addBox(-0.99F, 0.0F, -2.0F, 2, 6, 2),
			PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, -0.5918411493512771F, 0.0F, 0.0F));
		leg_front_right2.addOrReplaceChild("right_front_leg_3", CubeListBuilder.create()
				.texOffs(48, 46).addBox(-0.98F, -2.0F, -3.0F, 2, 2, 3),
			PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.5918411493512771F, 0.0F, 0.0F));

		var leg_back_left1 = body_lower.addOrReplaceChild("left_back_leg_1", CubeListBuilder.create()
				.texOffs(33, 0).addBox(-2.0F, 0.0F, -3.0F, 3, 7, 4),
			PartPose.offsetAndRotation(4.0F, 0.0F, 7.0F, 0.27314402793711257F, -0.18203784098300857F, 0.0F));
		var leg_back_left2 = leg_back_left1.addOrReplaceChild("left_back_leg_2", CubeListBuilder.create()
				.texOffs(33, 12).addBox(-1.01F, 0.0F, -2.0F, 2, 6, 2),
			PartPose.offsetAndRotation(0.0F, 7.0F, 1.0F, -0.7740535232594852F, 0.0F, 0.0F));
		leg_back_left2.addOrReplaceChild("left_back_leg_3", CubeListBuilder.create()
				.texOffs(33, 21).addBox(-1.02F, -2.0F, -3.0F, 2, 2, 3),
			PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.8651597102135892F, 0.0F, 0.0F));

		var leg_back_right1 = body_lower.addOrReplaceChild("right_back_leg_1", CubeListBuilder.create()
				.texOffs(48, 0).addBox(-1.0F, 0.0F, -3.0F, 3, 7, 4),
			PartPose.offsetAndRotation(-4.0F, 0.0F, 7.0F, 0.27314402793711257F, 0.18203784098300857F, 0.0F));
		var leg_back_right2 = leg_back_right1.addOrReplaceChild("right_back_leg_2", CubeListBuilder.create()
				.texOffs(48, 12).addBox(-0.99F, 0.0F, -2.0F, 2, 6, 2),
			PartPose.offsetAndRotation(0.0F, 7.0F, 1.0F, -0.7740535232594852F, 0.0F, 0.0F));
		leg_back_right2.addOrReplaceChild("right_back_leg_3", CubeListBuilder.create()
				.texOffs(48, 21).addBox(-0.98F, -2.0F, -3.0F, 2, 2, 3),
			PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.8651597102135892F, 0.0F, 0.0F));

		var head = neck.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 39).addBox(-3.0F, 0.0F, -4.0F, 6, 5, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.22759093446006054F, 0.0F, 0.0F));
		var snout = head.addOrReplaceChild("snout", CubeListBuilder.create()
				.texOffs(0, 49).addBox(-2.0F, 0.0F, -3.0F, 4, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.045553093477052F, 0.0F, 0.0F));
		var jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(22, 55).addBox(-2.0F, 0.0F, -3.0F, 4, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, -3.8F, -0.091106186954104F, 0.0F, 0.0F));
		snout.addOrReplaceChild("upper_teeth_1", CubeListBuilder.create()
				.texOffs(27, 60).addBox(-1.0F, 0.0F, 0.0F, 1, 4, 0),
			PartPose.offsetAndRotation(0.0F, 1.0F, -3.0F, -0.136659280431156F, 0.045553093477052F, 0.0F));
		snout.addOrReplaceChild("upper_teeth_2", CubeListBuilder.create()
				.texOffs(30, 60).addBox(0.0F, 0.0F, 0.0F, 1, 4, 0),
			PartPose.offsetAndRotation(0.0F, 1.0F, -3.0F, -0.136659280431156F, -0.045553093477052F, 0.0F));
		jaw.addOrReplaceChild("lower_teeth", CubeListBuilder.create()
				.texOffs(22, 60).addBox(-1.0F, -2.0F, 0.0F, 2, 2, 0),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.36425021489121656F, 0.0F, 0.0F));

		snout.addOrReplaceChild("left_cheek", CubeListBuilder.create()
				.texOffs(0, 55).addBox(-2.0F, 0.0F, 0.01F, 2, 4, 3),
			PartPose.offsetAndRotation(2.0F, 0.0F, -3.0F, 0.0F, 0.0F, -0.091106186954104F));
		snout.addOrReplaceChild("right_cheek", CubeListBuilder.create()
				.texOffs(11, 55).addBox(0.0F, 0.0F, 0.01F, 2, 4, 3),
			PartPose.offsetAndRotation(-2.0F, 0.0F, -3.0F, 0.0F, 0.0F, 0.091106186954104F));
		head.addOrReplaceChild("left_ear", CubeListBuilder.create()
				.texOffs(21, 43).addBox(-1.0F, 0.0F, 0.0F, 2, 2, 0),
			PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, -0.091106186954104F, -0.5462880558742251F, -0.136659280431156F));
		head.addOrReplaceChild("right_ear", CubeListBuilder.create()
				.texOffs(26, 43).addBox(-1.0F, 0.0F, 0.0F, 2, 2, 0),
			PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, -0.091106186954104F, 0.5462880558742251F, 0.136659280431156F));

		var tail1 = body_lower.addOrReplaceChild("tail_1", CubeListBuilder.create()
				.texOffs(64, 0).addBox(-1.0F, 0.0F, 0.0F, 2, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 8.0F, -0.40980330836826856F, 0.0F, 0.0F));
		var tail2 = tail1.addOrReplaceChild("tail_2", CubeListBuilder.create()
				.texOffs(64, 6).addBox(-1.01F, 0.0F, 0.0F, 2, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, -0.22759093446006054F, 0.0F, 0.0F));
		var tail3 = tail2.addOrReplaceChild("tail_3", CubeListBuilder.create()
				.texOffs(64, 12).addBox(-1.01F, 0.0F, 0.0F, 2, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, -0.22759093446006054F, 0.0F, 0.0F));
		tail3.addOrReplaceChild("tail_4", CubeListBuilder.create()
				.texOffs(64, 18).addBox(-1.01F, -2.0F, 0.0F, 2, 2, 3),
			PartPose.offsetAndRotation(0.0F, 2.0F, 3.0F, 0.22759093446006054F, 0.0F, 0.0F));

		body_lower.addOrReplaceChild("tiny_urn_1", CubeListBuilder.create()
				.texOffs(80, 0).addBox(-1.0F, 0.0F, -2.0F, 2, 3, 2),
			PartPose.offsetAndRotation(2.0F, 6.0F, 3.0F, 0.136659280431156F, 0.0F, 0.0F));
		body_lower.addOrReplaceChild("tiny_urn_2", CubeListBuilder.create()
				.texOffs(80, 6).addBox(-1.0F, 0.0F, -2.0F, 2, 4, 2),
			PartPose.offsetAndRotation(-1.0F, 6.0F, 3.0F, 0.136659280431156F, 0.0F, 0.0F));
		body_lower.addOrReplaceChild("tiny_urn_3", CubeListBuilder.create()
				.texOffs(80, 13).addBox(-1.3F, 0.0F, -2.0F, 2, 3, 2),
			PartPose.offsetAndRotation(-4.0F, 4.0F, 3.0F, 0.136659280431156F, 0.0F, 0.045553093477052F));

		return LayerDefinition.create(definition, 128, 64);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(CryptCrawler entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void prepareMobModel(CryptCrawler entity, float limbSwing, float limbSwingAmount, float partialTick) {
		float animation = Mth.sin((limbSwing * 1.1F + 2) * 0.5F) * 0.3F * limbSwingAmount * 0.3F;
		float animation2 = Mth.sin((limbSwing * 1.1F) * 0.5F) * 0.3F * limbSwingAmount * 0.3F;
		float animation3 = Mth.sin((limbSwing * 1.1F + 4) * 0.5F) * 0.3F * limbSwingAmount * 0.3F;

		float flap = Mth.sin((entity.tickCount + partialTick) * 0.3F) * 0.8F;
		float standingAngle = entity.smoothedStandingAngle(partialTick);

		this.tail1.xRot = -0.40980330836826856F + (standingAngle * 0.75F) - animation;
		this.tail2.xRot = -0.22759093446006054F + (standingAngle * 0.75F) - animation * 3F;
		this.tail3.xRot = -0.22759093446006054F + (standingAngle * 0.75F) - animation * 4F;
		this.tail4.xRot = 0.22759093446006054F + (standingAngle * 0.75F) - animation * 5F;

		this.tinyurn1.xRot = 0.136659280431156F + (standingAngle);
		this.tinyurn2.xRot = 0.136659280431156F + (standingAngle);
		this.tinyurn3.xRot = 0.136659280431156F + (standingAngle);

		if (entity.standingAngle < 0.01f) {
			this.leg_front_right1.xRot = 0.27314402793711257F + (animation2 * 8F) + flap * 0.05F;
			this.leg_front_right2.xRot = -0.5918411493512771F + (animation2 * 6F) - flap * 0.025F;
			this.leg_front_right3.xRot = 0.5918411493512771F - (standingAngle * 1.25F) - 0.17453292519943295F - animation2 * 18F + flap * 0.05F;

			this.leg_front_left1.xRot = 0.27314402793711257F + (animation * 8F) + flap * 0.05F;
			this.leg_front_left2.xRot = -0.5918411493512771F + (animation * 6F) - flap * 0.025F;
			this.leg_front_left3.xRot = 0.5918411493512771F - (standingAngle * 1.25F) - 0.17453292519943295F - (animation * 18F) + flap * 0.05F;

			this.leg_back_right1.xRot = 0.27314402793711257F - (standingAngle * 0.75F) - 0.1F - (animation2 * 6F) - flap * 0.05F;
			this.leg_back_left1.xRot = 0.27314402793711257F - (standingAngle * 0.75F) - 0.1F - (animation3 * 6F) - flap * 0.05F;

			this.leg_back_right1.yRot = 0.18203784098300857F - (standingAngle * 0.0625F);
			this.leg_back_left1.yRot = -0.18203784098300857F + (standingAngle * 0.0625F);

			this.leg_back_right2.xRot = standingAngle - 0.7740535232594852F + (this.leg_back_right1.xRot + animation2) + flap * 0.1F;
			this.leg_back_left2.xRot = standingAngle - 0.7740535232594852F + (this.leg_back_left1.xRot + animation3) + flap * 0.1F;

			this.leg_back_right3.xRot = 0.8651597102135892F - (standingAngle * 0.5F) - (leg_back_right1.xRot * 1.25F) - flap * 0.05F;
			this.leg_back_left3.xRot = 0.8651597102135892F - (standingAngle * 0.5F) - (leg_back_left1.xRot * 1.25F) - flap * 0.05F;

			this.body_main.xRot = 0.091106186954104F - standingAngle - (animation2 * 3F) - flap * 0.025F;
			this.body_main.zRot = 0F - animation2 * 1.5F;

			this.body_lower.xRot = -0.22759093446006054F + standingAngle + (animation2 * 2F) + flap * 0.05F;

			this.neck.xRot = 0.27314402793711257F - (standingAngle * 0.5F) - 0.17453292519943295F + (animation2 * 2.9F) + flap * 0.025F;
			this.head.xRot = -(standingAngle * 0.5F) + 0.17453292519943295F;
			this.head.zRot = -(standingAngle * 0.1F * flap * 6F);

			this.body_main.y = 12.0f;
		} else {
			this.leg_front_right1.xRot = 0.27314402793711257F + (standingAngle * 0.5F * flap) + animation2 * 6F;
			this.leg_front_left1.xRot = 0.27314402793711257F + (standingAngle * 0.5F * flap) + animation * 6F;

			this.leg_front_right2.xRot = -0.5918411493512771F + animation2 * 5F;
			this.leg_front_left2.xRot = -0.5918411493512771F + animation * 5F;

			this.leg_front_right3.xRot = 0.5918411493512771F + (standingAngle * 1.25F) - 0.17453292519943295F - animation2 * 12F;
			this.leg_front_left3.xRot = 0.5918411493512771F + (standingAngle * 1.25F) - 0.17453292519943295F - animation * 12F;

			this.leg_back_right1.xRot = 0.27314402793711257F + (standingAngle * 0.5F) - animation2 * 5F;
			this.leg_back_left1.xRot = 0.27314402793711257F + (standingAngle * 0.5F) - animation3 * 5F;

			this.leg_back_right1.yRot = 0.18203784098300857F - (standingAngle * 0.1625F);
			this.leg_back_left1.yRot = -0.18203784098300857F + (standingAngle * 0.1625F);

			this.leg_back_right2.xRot = -0.7740535232594852F + (standingAngle * 1.25F) + (this.leg_back_right1.xRot + animation2);
			this.leg_back_left2.xRot = -0.7740535232594852F + (standingAngle * 1.25F) + (this.leg_back_left1.xRot + animation3);

			this.leg_back_right3.xRot = 0.8651597102135892F + (standingAngle * 0.125F) - this.leg_back_right1.xRot * 1.25F;
			this.leg_back_left3.xRot = 0.8651597102135892F + (standingAngle * 0.125F) - this.leg_back_left1.xRot * 1.25F;

			this.body_main.xRot = 0.091106186954104F - (standingAngle) - animation2 * 2F;
			this.body_lower.xRot = -0.22759093446006054F - (standingAngle * 0.5F) + animation2 * 2F;

			this.neck.xRot = 0.27314402793711257F + (standingAngle * 0.25F) - 0.17453292519943295F + animation2 * 2.9F;
			this.head.xRot = 0.17453292519943295F + (standingAngle * 0.5F);

			this.body_main.zRot = 0F - animation2 * 1.5F * (1 - standingAngle);

			this.body_main.y = 12.0f - standingAngle * 5.8f;
		}
		if (!entity.onGround()) {
			this.jaw.xRot = -0.091106186954104F;
		} else {
			if (standingAngle > 0) {
				this.jaw.xRot = 0.091106186954104F + flap * 0.5F;
			} else {
				this.jaw.xRot = -0.091106186954104F + flap * 0.3F;
			}
		}
	}
}
