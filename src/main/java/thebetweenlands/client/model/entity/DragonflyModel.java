package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.creature.Dragonfly;

public class DragonflyModel extends MowzieModelBase<Dragonfly> {

	private final ModelPart root;
	private final ModelPart leftback2;
	private final ModelPart rightback2;
	private final ModelPart leftfront2;
	private final ModelPart rightfront2;
	private final ModelPart leftmid2;
	private final ModelPart rightmid2;
	private final ModelPart wingleft1;
	private final ModelPart wingleft2;
	private final ModelPart wingright1;
	private final ModelPart wingright2;
	private final ModelPart tail1;
	private final ModelPart tail2;
	private final ModelPart tail3;
	private final ModelPart tail4;
	private final ModelPart stinger;

	public DragonflyModel(ModelPart root) {
		this.root = root;
		this.leftback2 = root.getChild("left_back_leg_2");
		this.rightback2 = root.getChild("right_back_leg_2");
		this.leftmid2 = root.getChild("left_mid_leg_2");
		this.rightmid2 = root.getChild("right_mid_leg_2");
		this.leftfront2 = root.getChild("left_front_leg_2");
		this.rightfront2 = root.getChild("right_front_leg_2");
		this.wingleft1 = root.getChild("left_wing_1");
		this.wingleft2 = root.getChild("left_wing_2");
		this.wingright1 = root.getChild("right_wing_1");
		this.wingright2 = root.getChild("right_wing_2");
		this.tail1 = root.getChild("tail_1");
		this.tail2 = this.tail1.getChild("tail_2");
		this.tail3 = this.tail2.getChild("tail_3");
		this.tail4 = this.tail3.getChild("tail_4");
		this.stinger = this.tail4.getChild("stinger");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("left_jaw", CubeListBuilder.create()
				.texOffs(30, 19).addBox(0.0F, 0.0F, -4.5F, 1, 2, 1),
			PartPose.offsetAndRotation(0.0F, 18.0F, -9.0F, 0.22307169437408447F, 0.0F, -0.22307169437408447F));
		partDefinition.addOrReplaceChild("right_jaw", CubeListBuilder.create()
				.texOffs(35, 19).addBox(-1.0F, 0.0F, -4.5F, 1, 2, 1),
			PartPose.offsetAndRotation(0.0F, 18.0F, -9.0F, 0.22307050228118896F, 0.0F, 0.22307050228118896F));

		partDefinition.addOrReplaceChild("left_eye", CubeListBuilder.create()
				.texOffs(30, 14).addBox(2.5F, -0.5F, -2.5F, 1, 2, 2),
			PartPose.offsetAndRotation(0.0F, 18.0F, -9.0F, 0.07436110079288481F, 0.3892394006252289F, -0.18589310348033902F));
		partDefinition.addOrReplaceChild("right_eye", CubeListBuilder.create()
				.texOffs(37, 14).addBox(2.5F, -0.5F, 0.5F, 1, 2, 2),
			PartPose.offsetAndRotation(0.0F, 18.0F, -9.0F, -0.07435102613495843F, 2.7698375229150005F, 0.18587756533739608F));

		partDefinition.addOrReplaceChild("head_1", CubeListBuilder.create()
				.texOffs(30, 0).addBox(-2.0F, -2.0F, -3.0F, 4, 4, 3),
			PartPose.offsetAndRotation(0.0F, 18.0F, -9.0F, 0.3717860877513886F, 0.0F, 0.0F));
		partDefinition.addOrReplaceChild("head_2", CubeListBuilder.create()
				.texOffs(30, 8).addBox(-1.5F, -2.9000000953674316F, -4.0F, 3, 3, 2),
			PartPose.offsetAndRotation(0.0F, 18.0F, -9.0F, 0.7435721755027772F, 0.0F, 0.0F));

		partDefinition.addOrReplaceChild("left_antenna", CubeListBuilder.create()
				.texOffs(30, 23).addBox(0.0F, -4.0F, -2.0F, 1, 2, 0),
			PartPose.offsetAndRotation(0.0F, 18.0F, -9.0F, 0.40896469354629517F, 0.0F, 0.5205006003379822F));
		partDefinition.addOrReplaceChild("right_antenna", CubeListBuilder.create()
				.texOffs(33, 23).addBox(-1.0F, -4.0F, -2.0F, 1, 2, 0),
			PartPose.offsetAndRotation(0.0F, 18.0F, -9.0F, 0.40896469354629517F, 0.0F, -0.5204920768737793F));

		partDefinition.addOrReplaceChild("torso", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.0F, -2.0F, 0.0F, 4, 4, 6),
			PartPose.offsetAndRotation(0.0F, 18.0F, -9.0F, -0.07435719668865202F, 0.0F, 0.0F));
		partDefinition.addOrReplaceChild("back", CubeListBuilder.create()
				.texOffs(0, 11).addBox(-1.5F, -1.5F, 0.0F, 3, 3, 2),
			PartPose.offsetAndRotation(0.0F, 18.200000762939453F, -3.0F, -0.03717859834432601F, 0.0F, 0.0F));


		partDefinition.addOrReplaceChild("left_wing_1", CubeListBuilder.create()
				.texOffs(63, 4).addBox(0, 0, 1F / 32, 4, 16, 0),
			PartPose.offsetAndRotation(-0.5F, 16.0F, -8.0F, 0, -90 * Mth.DEG_TO_RAD, 0));

		partDefinition.addOrReplaceChild("left_wing_2", CubeListBuilder.create()
				.texOffs(63, 25).addBox(0, 0, 1F / 32, 4, 12, 0),
			PartPose.offsetAndRotation(-0.5F, 16.5F, -4.0F, 0, -90 * Mth.DEG_TO_RAD, 0));

		partDefinition.addOrReplaceChild("right_wing_1", CubeListBuilder.create()
				.texOffs(50, 4).addBox(0, 0, 1F / 32, 4, 16, 0),
			PartPose.offsetAndRotation(0.5F, 16.0F, -4.0F, 0, 90 * Mth.DEG_TO_RAD, 0));

		partDefinition.addOrReplaceChild("right_wing_2", CubeListBuilder.create()
				.texOffs(50, 25).addBox(0, 0, 1F / 32, 4, 12, 0),
			PartPose.offsetAndRotation(0.5F, 16.5F, 0.0F, 0, 90 * Mth.DEG_TO_RAD, 0));

		partDefinition.addOrReplaceChild("right_back_leg_1", CubeListBuilder.create()
				.texOffs(65, 44).addBox(0.0F, -0.5F, -0.5F, 6, 1, 1),
			PartPose.offsetAndRotation(-1.0F, 20.0F, -5.0F, 0.17453292519943295F, -2.249380339970292F, -0.17453292519943295F));
		partDefinition.addOrReplaceChild("right_back_leg_2", CubeListBuilder.create()
				.texOffs(55, 59).addBox(4.8F, 1.8F, 0.2F, 1, 4, 1),
			PartPose.offsetAndRotation(-1.0F, 20.0F, -5.0F, -0.38083084278516266F, -2.1769491760125272F, 0.48939032225921F));

		partDefinition.addOrReplaceChild("left_back_leg_1", CubeListBuilder.create()
				.texOffs(50, 44).addBox(0.0F, -0.5F, -0.5F, 6, 1, 1),
			PartPose.offsetAndRotation(1.0F, 20.0F, -5.0F, -0.17453292519943295F, -0.8922123136195012F, 0.17453292519943295F));
		partDefinition.addOrReplaceChild("left_back_leg_2", CubeListBuilder.create()
				.texOffs(50, 59).addBox(4.8F, 1.8F, -1.1F, 1, 4, 1),
			PartPose.offsetAndRotation(1.0F, 20.0F, -5.0F, 0.38083084278516266F, -0.964643477577266F, -0.48939032225921F));

		partDefinition.addOrReplaceChild("right_mid_leg_1", CubeListBuilder.create()
				.texOffs(63, 41).addBox(0.0F, -0.5F, -0.5F, 5, 1, 1),
			PartPose.offsetAndRotation(-1.0F, 20.0F, -6.0F, 0.08726646259971647F, -2.9169687788581227F, -0.08726646259971647F));
		partDefinition.addOrReplaceChild("right_mid_leg_2", CubeListBuilder.create()
				.texOffs(55, 53).addBox(3.9F, 1.6F, -0.6F, 1, 4, 1),
			PartPose.offsetAndRotation(-1.0F, 20.0F, -6.0F, 0.0F, -2.918539575184918F, 0.33457961760731303F));

		partDefinition.addOrReplaceChild("left_mid_leg_1", CubeListBuilder.create()
				.texOffs(50, 41).addBox(0.0F, -0.5F, -0.5F, 5, 1, 1),
			PartPose.offsetAndRotation(1.0F, 20.0F, -6.0F, -0.08726646259971647F, -0.2230530784048753F, 0.08726646259971647F));
		partDefinition.addOrReplaceChild("left_mid_leg_2", CubeListBuilder.create()
				.texOffs(50, 53).addBox(3.9F, 1.6F, -0.4F, 1, 4, 1),
			PartPose.offsetAndRotation(1.0F, 20.0F, -6.0F, 0.0F, -0.2230530784048753F, -0.33457961760731303F));

		partDefinition.addOrReplaceChild("right_front_leg_1", CubeListBuilder.create()
				.texOffs(63, 38).addBox(0.0F, -0.5F, -0.5F, 5, 1, 1),
			PartPose.offsetAndRotation(-1.0F, 20.0F, -7.0F, 0.0F, 2.6954864967800423F, -0.14870205226991687F));
		partDefinition.addOrReplaceChild("right_front_leg_2", CubeListBuilder.create()
				.texOffs(55, 47).addBox(4.0F, 1.2F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(-1.0F, 20.0F, -7.0F, 0.0F, 2.6954864967800423F, 0.18587756533739608F));

		partDefinition.addOrReplaceChild("left_front_leg_1", CubeListBuilder.create()
				.texOffs(50, 38).addBox(0.0F, -0.5F, -0.5F, 5, 1, 1),
			PartPose.offsetAndRotation(1.0F, 20.0F, -7.0F, 0.0F, 0.44614329934120184F, 0.14871439337730405F));
		partDefinition.addOrReplaceChild("left_front_leg_2", CubeListBuilder.create()
				.texOffs(50, 47).addBox(4.0F, 1.2F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(1.0F, 20.0F, -7.0F, 0.0F, 0.4461061568097506F, -0.18587756533739608F));

		var tail1 = partDefinition.addOrReplaceChild("tail_1", CubeListBuilder.create()
				.texOffs(0, 17).addBox(-1.0F, -1.0F, 0.0F, 2, 2, 5),
			PartPose.offset(0.0F, 18.0F, -1.0F));
		var tail2 = tail1.addOrReplaceChild("tail_2", CubeListBuilder.create()
				.texOffs(0, 25).addBox(-1.0F, -1.0F, 0.0F, 2, 2, 4),
			PartPose.offset(0.0F, 0.0F, 5.0F));
		var tail3 = tail2.addOrReplaceChild("tail_3", CubeListBuilder.create()
				.texOffs(0, 32).addBox(-1.0F, -1.0F, 0.0F, 2, 2, 4),
			PartPose.offset(0.0F, 0.0F, 4.0F));
		var tail4 = tail3.addOrReplaceChild("tail_4", CubeListBuilder.create()
				.texOffs(0, 39).addBox(-1.0F, -1.0F, 0.0F, 2, 2, 4),
			PartPose.offset(0.0F, 0.0F, 4.0F));
		tail4.addOrReplaceChild("stinger", CubeListBuilder.create()
				.texOffs(0, 46).addBox(0.0F, 0.0F, 0.0F, 0, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, -0.4363322854042054F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(Dragonfly entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void prepareMobModel(Dragonfly entity, float limbSwing, float limbSwingAmount, float partialTick) {
		float frame = entity.tickCount + partialTick;

		this.setInitPose();

		this.wingright1.xRot = 0.1745329F;
		this.wingleft1.xRot = 0.1745329F;
		this.wingright2.xRot = -0.1745329F;
		this.wingleft2.xRot = -0.1745329F;

		if (entity.onGround()) {
			float flap = Mth.sin(frame * 0.08F) * 0.05F;
			this.wingright1.zRot = -1.570796F - flap;
			this.wingleft1.zRot = 1.570796F + flap;
			this.wingright2.zRot = -1.570796F - flap;
			this.wingleft2.zRot = 1.570796F + flap;
		}
		if (entity.isFlying()) {
			float flap = Mth.sin(frame * 1.6F) * 0.3F;
			this.wingright1.zRot = -1.570796F - flap;
			this.wingleft1.zRot = 1.570796F + flap;
			this.wingright2.zRot = -1.570796F - flap;
			this.wingleft2.zRot = 1.570796F + flap;
		}

		this.walk(this.tail1, 0.1f, -0.05f, false, 1.8f, 0, frame, 1);
		this.walk(this.tail2, 0.1f, -0.05f, false, 1.8f, 0, frame, 1);
		this.walk(this.tail3, 0.1f, -0.05f, false, 1.8f, 0, frame, 1);
		this.walk(this.tail4, 0.1f, -0.05f, false, 1.8f, 0, frame, 1);
		this.walk(this.stinger, 0.1f, -0.05f, false, 1.8f, 0, frame, 1);

		this.swing(this.tail1, 0.03f, -0.08f, false, 1.8f, 0, frame, 1);
		this.swing(this.tail2, 0.04f, -0.08f, false, 1.8f, 0, frame, 1);
		this.swing(this.tail3, 0.05f, -0.08f, false, 1.8f, 0, frame, 1);
		this.swing(this.tail4, 0.06f, -0.08f, false, 1.8f, 0, frame, 1);

		float legFlapSpeed = entity.onGround() ? 0.08F : 0.4F;
		float legFlatDegree = entity.onGround() ? -0.05F : -0.1F;
		this.flap(this.rightback2, legFlapSpeed, legFlatDegree, true, 0.0f, 0, frame, 1);
		this.flap(this.leftback2, legFlapSpeed, legFlatDegree, false, 0.4f, 0, frame, 1);
		this.flap(this.rightmid2, legFlapSpeed, legFlatDegree, true, 0.8f, 0, frame, 1);
		this.flap(this.leftmid2, legFlapSpeed, legFlatDegree, false, 1.2f, 0, frame, 1);
		this.flap(this.rightfront2, legFlapSpeed, legFlatDegree, true, 1.6f, 0, frame, 1);
		this.flap(this.leftfront2, legFlapSpeed, legFlatDegree, false, 2.0f, 0, frame, 1);
	}
}
