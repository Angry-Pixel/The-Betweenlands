package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.Pyrad;

public class PyradModel extends MowzieModelBase<Pyrad> {

	private final ModelPart head;
	private final ModelPart rightLeaf;
	private final ModelPart leftLeaf;
	private final ModelPart backPlate;
	private final ModelPart rightPlate;
	private final ModelPart leftPlate;
	private final ModelPart staff;

	public PyradModel(ModelPart root) {
		super(root);
		var body = root.getChild("body");
		this.head = body.getChild("head");
		this.rightLeaf = this.head.getChild("right_head_leaf1");
		this.leftLeaf = this.head.getChild("left_head_leaf1");
		this.backPlate = body.getChild("back_plate1");
		this.rightPlate = body.getChild("right_chestpiece").getChild("right_plate1");
		this.leftPlate = body.getChild("left_chestpiece").getChild("left_plate1");
		this.staff = body.getChild("staff1");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
			.texOffs(0, 0).addBox(-5.0F, 0.0F, 0.0F, 10, 6, 7), PartPose.
			offsetAndRotation(0.0F, 4.0F, -3.0F, 0.091106186954104F, 0.0F, 0.0F));
		body.addOrReplaceChild("body_piece", CubeListBuilder.create()
			.texOffs(0, 29).addBox(-4.0F, 0.0F, 0.0F, 8, 4, 7), PartPose.
			offsetAndRotation(0.0F, 6.0F, 0.0F, 0.136659280431156F, 0.0F, 0.0F));

		var headbase = body.addOrReplaceChild("head", CubeListBuilder.create()
			.texOffs(60, 0).addBox(-4.0F, -6.0F, -4.0F, 8, 6, 6), PartPose.
			offsetAndRotation(0.0F, -9.0F, 6.0F, -0.091106186954104F, 0.0F, 0.0F));
		var headconnectionpiece = headbase.addOrReplaceChild("head_connection", CubeListBuilder.create()
			.texOffs(60, 13).addBox(-4.01F, 0.0F, -3.0F, 8, 2, 3), PartPose.
			offset(0.0F, 0.0F, 2.0F));
		headbase.addOrReplaceChild("snout", CubeListBuilder.create()
			.texOffs(60, 30).addBox(-3.0F, 0.0F, -6.0F, 6, 5, 6), PartPose.
			offsetAndRotation(0.0F, -6.0F, -4.0F, 0.40980330836826856F, 0.0F, 0.0F));
		headconnectionpiece.addOrReplaceChild("lower_jaw", CubeListBuilder.create()
			.texOffs(60, 19).addBox(-3.015F, -3.0F, -7.0F, 6, 3, 7), PartPose.
			offset(0.0F, 2.0F, -3.0F));
		var leaf_headright1 = headbase.addOrReplaceChild("right_head_leaf1", CubeListBuilder.create()
			.texOffs(35, -3).addBox(0.0F, -2.0F, 0.0F, 0, 4, 3), PartPose.
			offsetAndRotation(-4.0F, -3.0F, -2.0F, 0.22759093446006054F, -0.4553564018453205F, -0.091106186954104F));
		leaf_headright1.addOrReplaceChild("right_head_leaf2", CubeListBuilder.create()
			.texOffs(42, -3).addBox(0.0F, -2.0F, 0.0F, 0, 4, 3), PartPose.
			offsetAndRotation(0.0F, 0.0F, 3.0F, 0.0F, -0.5009094953223726F, 0.0F));
		var leaf_headleft1 = headbase.addOrReplaceChild("left_head_leaf1", CubeListBuilder.create()
			.texOffs(35, 2).addBox(0.0F, -2.0F, 0.0F, 0, 4, 3), PartPose.
			offsetAndRotation(4.0F, -3.0F, -2.0F, 0.22759093446006054F, 0.4553564018453205F, 0.091106186954104F));
		leaf_headleft1.addOrReplaceChild("left_head_leaf2", CubeListBuilder.create()
			.texOffs(42, 2).addBox(0.0F, -2.0F, 0.0F, 0, 4, 3), PartPose.
			offsetAndRotation(0.0F, 0.0F, 3.0F, 0.0F, 0.5462880558742251F, 0.0F));

		var staff1 = body.addOrReplaceChild("staff1", CubeListBuilder.create()
			.texOffs(90, 0).addBox(-1.0F, -12.0F, 0.0F, 2, 24, 2), PartPose.
			offsetAndRotation(0.0F, -2.0F, 8.0F, -0.045553093477052F, 0.0F, -0.27314402793711257F));
		var staff2 = staff1.addOrReplaceChild("staff2", CubeListBuilder.create()
			.texOffs(99, 0).addBox(-2.0F, -6.0F, -1.01F, 2, 6, 2), PartPose.
			offsetAndRotation(1.0F, -12.0F, 1.0F, 0.0F, 0.0F, -0.8651597102135892F));
		var staff3 = staff2.addOrReplaceChild("staff3", CubeListBuilder.create()
			.texOffs(99, 9).addBox(0.0F, -4.0F, -1.02F, 2, 4, 2), PartPose.
			offsetAndRotation(-2.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.8651597102135892F));
		var staff4 = staff3.addOrReplaceChild("staff4", CubeListBuilder.create()
			.texOffs(99, 16).addBox(0.0F, -4.0F, -1.03F, 2, 4, 2), PartPose.
			offsetAndRotation(0.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.8651597102135892F));
		staff4.addOrReplaceChild("staff5", CubeListBuilder.create()
			.texOffs(99, 23).addBox(0.0F, -4.0F, -1.04F, 2, 4, 2), PartPose.
			offsetAndRotation(0.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.8651597102135892F));
		staff1.addOrReplaceChild("staff6", CubeListBuilder.create()
			.texOffs(108, 0).addBox(-2.0F, 0.0F, -0.99F, 2, 4, 2), PartPose.
			offsetAndRotation(1.0F, 12.0F, 1.0F, 0.0F, 0.0F, 0.4553564018453205F));
		var staffleaf1 = staff4.addOrReplaceChild("staff_leaf1", CubeListBuilder.create()
			.texOffs(99, 30).addBox(-3.0F, -2.0F, 0.0F, 3, 4, 0), PartPose.
			offsetAndRotation(0.0F, -3.0F, 1.0F, 0.0F, 0.4553564018453205F, 0.0F));
		staffleaf1.addOrReplaceChild("staff_leaf2", CubeListBuilder.create()
			.texOffs(99, 35).addBox(-3.0F, -2.0F, 0.0F, 3, 4, 0), PartPose.
			offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.9560913642424937F, 0.0F));

		var chestpiece_right = body.addOrReplaceChild("right_chestpiece", CubeListBuilder.create()
			.texOffs(0, 14).addBox(-6.0F, 0.0F, 0.0F, 6, 6, 8), PartPose.
			offsetAndRotation(0.0F, -6.0F, 0.0F, -0.09145525280450287F, 0.09075712110370514F, -0.008377580409572781F));
		var plate_right = chestpiece_right.addOrReplaceChild("right_plate1", CubeListBuilder.create()
			.texOffs(0, 41).addBox(-2.0F, -2.0F, -4.0F, 2, 8, 8), PartPose.
			offsetAndRotation(-7.0F, 0.0F, 3.0F, 0.0F, 0.0F, 0.36425021489121656F));
		plate_right.addOrReplaceChild("right_plate2", CubeListBuilder.create()
			.texOffs(21, 49).addBox(-2.0F, 0.0F, -3.0F, 2, 2, 6), PartPose.
			offset(0.0F, 6.0F, 0.0F));
		plate_right.addOrReplaceChild("right_plate_edge", CubeListBuilder.create()
			.texOffs(38, 47).addBox(-2.0F, -2.0F, -4.01F, 2, 2, 8), PartPose.
			offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.22759093446006054F));

		var chestpiece_left = body.addOrReplaceChild("left_chestpiece", CubeListBuilder.create()
			.texOffs(29, 14).addBox(0.0F, 0.01F, 0.0F, 6, 6, 8), PartPose.
			offsetAndRotation(0.0F, -6.0F, 0.0F, -0.09145525280450287F, -0.09058258817850572F, 0.008377580409572781F));
		var plate_left = chestpiece_left.addOrReplaceChild("left_plate1", CubeListBuilder.create()
			.texOffs(59, 42).addBox(0.0F, -2.0F, -4.0F, 2, 8, 8), PartPose.
			offsetAndRotation(7.0F, 0.0F, 3.0F, 0.0F, 0.0F, -0.36425021489121656F));
		plate_left.addOrReplaceChild("left_plate2", CubeListBuilder.create()
			.texOffs(80, 50).addBox(0.0F, 0.0F, -3.0F, 2, 2, 6), PartPose.
			offset(0.0F, 6.0F, 0.0F));
		plate_left.addOrReplaceChild("left_plate_edge", CubeListBuilder.create()
			.texOffs(97, 48).addBox(0.0F, -2.0F, -4.01F, 2, 2, 8), PartPose.
			offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.22759093446006054F));

		var plate_back = body.addOrReplaceChild("back_plate1", CubeListBuilder.create()
			.texOffs(108, 7).addBox(-4.0F, -2.0F, 0.0F, 8, 8, 2), PartPose.
			offsetAndRotation(0.0F, -5.0F, 10.0F, 0.22759093446006054F, 0.0F, 0.0F));
		plate_back.addOrReplaceChild("back_plate2", CubeListBuilder.create()
			.texOffs(108, 18).addBox(-3.0F, 0.0F, 0.0F, 6, 2, 2), PartPose.
			offset(0.0F, 6.0F, 0.0F));
		plate_back.addOrReplaceChild("back_plate_edge", CubeListBuilder.create()
			.texOffs(108, 23).addBox(-4.01F, -2.0F, 0.0F, 8, 2, 2), PartPose.
			offsetAndRotation(0.0F, 0.0F, 2.0F, 0.22759093446006054F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}

	@Override
	public void setupAnim(Pyrad entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		float flap = Mth.sin(ageInTicks * 0.05F) * 0.5F * entity.getActiveTicks(partialTick) / 60.0F - 0.4F * (1.0F - entity.getActiveTicks(partialTick) / 60.0F);
		this.backPlate.xRot = 0.22759093446006054F + flap;
		this.rightPlate.zRot = 0.36425021489121656F + flap;
		this.leftPlate.zRot = -0.36425021489121656F - flap;

		this.leftPlate.x += -0.1F * (1.0F - entity.getActiveTicks(partialTick) / 60.0F);
		this.rightPlate.x += 0.1F * (1.0F - entity.getActiveTicks(partialTick) / 60.0F);
		this.backPlate.z += -0.05F * (1.0F - entity.getActiveTicks(partialTick) / 60.0F);

		this.head.y += 0.125F * (1.0F - entity.getActiveTicks(partialTick) / 60.0F);

		this.leftLeaf.visible = entity.isActive();
		this.rightLeaf.visible = entity.isActive();
		this.staff.visible = entity.isActive();
	}
}
