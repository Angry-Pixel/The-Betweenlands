package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.creature.Olm;

public class OlmModel extends MowzieModelBase<Olm> {

	private final ModelPart root;
	private final ModelPart leg_front_left1a;
	private final ModelPart leg_front_right1a;
	private final ModelPart leg_back_left1a;
	private final ModelPart leg_back_right1a;
	private final ModelPart head;
	private final ModelPart gills_left1a;
	private final ModelPart gills_right1a;
	private final ModelPart gills_top;

	public OlmModel(ModelPart root) {
		this.root = root;
		var frontBody =  root.getChild("body_main").getChild("body_2").getChild("body_3").getChild("body_4");
		this.leg_front_left1a = frontBody.getChild("left_front_leg_1");
		this.leg_front_right1a = frontBody.getChild("right_front_leg_1");
		this.leg_back_left1a = root.getChild("body_main").getChild("left_back_leg_1");
		this.leg_back_right1a = root.getChild("body_main").getChild("right_back_leg_1");
		this.head = frontBody.getChild("head_1");
		this.gills_left1a = this.head.getChild("left_gills_1");
		this.gills_right1a = this.head.getChild("right_gills_1");
		this.gills_top = this.head.getChild("head_2").getChild("gills_top");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body_main = partDefinition.addOrReplaceChild("body_main", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-1.0F, 0.0F, -4.0F, 2, 2, 4),
			PartPose.offset(0.0F, 21.0F, 8.0F));
		var body2 = body_main.addOrReplaceChild("body_2", CubeListBuilder.create()
				.texOffs(0, 7).addBox(-1.0F, 0.0F, -4.0F, 2, 2, 4),
			PartPose.offset(0.0F, 0.0F, -4.0F));
		var body3 = body2.addOrReplaceChild("body_3", CubeListBuilder.create()
				.texOffs(0, 14).addBox(-1.0F, 0.0F, -4.0F, 2, 2, 4),
			PartPose.offset(0.0F, 0.0F, -4.0F));
		var body4 = body3.addOrReplaceChild("body_4", CubeListBuilder.create()
				.texOffs(0, 21).addBox(-1.0F, 0.0F, -4.0F, 2, 2, 4),
			PartPose.offset(0.0F, 0.0F, -4.0F));

		var head1 = body4.addOrReplaceChild("head_1", CubeListBuilder.create()
				.texOffs(0, 28).addBox(-1.5F, 0.0F, -2.0F, 3, 2, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.18203784098300857F, 0.0F, 0.0F));
		var head2 = head1.addOrReplaceChild("head_2", CubeListBuilder.create()
				.texOffs(11, 28).addBox(-1.0F, 0.0F, -2.0F, 2, 2, 2),
			PartPose.offset(0.0F, 0.0F, -2.0F));
		head2.addOrReplaceChild("snout", CubeListBuilder.create()
				.texOffs(20, 29).addBox(-1.0F, 0.0F, -1.0F, 2, 1, 1),
			PartPose.offsetAndRotation(0.01F, 0.0F, -2.0F, 0.8196066167365371F, 0.0F, 0.0F));
		var gills_left1a = head1.addOrReplaceChild("left_gills_1", CubeListBuilder.create()
				.texOffs(22, 10).addBox(0.0F, -2.0F, 0.0F, 0, 2, 1),
			PartPose.offsetAndRotation(1.5F, 2.0F, 0.0F, 0.22759093446006054F, 0.18203784098300857F, 0.0F));
		gills_left1a.addOrReplaceChild("left_gills_2", CubeListBuilder.create()
				.texOffs(25, 10).addBox(0.0F, -2.0F, 0.0F, 0, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.0F, 0.136659280431156F, 0.0F));
		var gills_right1a = head1.addOrReplaceChild("right_gills_1", CubeListBuilder.create()
				.texOffs(22, 13).addBox(0.0F, -2.0F, 0.0F, 0, 2, 1),
			PartPose.offsetAndRotation(-1.5F, 2.0F, 0.0F, 0.22759093446006054F, -0.18203784098300857F, 0.0F));
		gills_right1a.addOrReplaceChild("right_gills_2", CubeListBuilder.create()
				.texOffs(25, 13).addBox(0.0F, -2.0F, 0.0F, 0, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.0F, -0.136659280431156F, 0.0F));
		head2.addOrReplaceChild("gills_top", CubeListBuilder.create()
				.texOffs(21, 9).addBox(-1.5F, 0.0F, 0.0F, 3, 0, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.4553564018453205F, 0.0F, 0.0F));

		var leg_front_left1a = body4.addOrReplaceChild("left_front_leg_1", CubeListBuilder.create()
				.texOffs(13, 21).addBox(0.0F, 0.0F, 0.0F, 2, 1, 1),
			PartPose.offsetAndRotation(1.0F, 1.0F, -3.0F, 0.0F, -0.40980330836826856F, 0.5462880558742251F));
		leg_front_left1a.addOrReplaceChild("left_front_leg_2", CubeListBuilder.create()
				.texOffs(13, 24).addBox(0.0F, 0.01F, -1.0F, 2, 1, 1),
			PartPose.offsetAndRotation(2.0F, 0.0F, 1.0F, 0.0F, 0.9105382707654417F, 0.0F));

		var leg_front_right1a = body4.addOrReplaceChild("right_front_leg_1", CubeListBuilder.create()
				.texOffs(13, 21).addBox(-2.0F, 0.0F, 0.0F, 2, 1, 1),
			PartPose.offsetAndRotation(-1.0F, 1.0F, -3.0F, 0.0F, 0.40980330836826856F, -0.5462880558742251F));
		leg_front_right1a.addOrReplaceChild("right_front_leg_2", CubeListBuilder.create()
				.texOffs(13, 24).addBox(-2.0F, 0.01F, -1.0F, 2, 1, 1),
			PartPose.offsetAndRotation(-2.0F, 0.0F, 1.0F, 0.0F, -0.9105382707654417F, 0.0F));

		var leg_back_left1a = body_main.addOrReplaceChild("left_back_leg_1", CubeListBuilder.create()
				.texOffs(20, 21).addBox(0.0F, 0.0F, -1.0F, 2, 1, 1),
			PartPose.offsetAndRotation(1.0F, 1.0F, -0.5F, 0.0F, 0.22759093446006054F, 0.4553564018453205F));
		leg_back_left1a.addOrReplaceChild("left_back_leg_2", CubeListBuilder.create()
				.texOffs(20, 24).addBox(0.0F, 0.01F, -1.0F, 2, 1, 1),
			PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, 0.36425021489121656F, 0.0F));

		var leg_back_right1a = body_main.addOrReplaceChild("right_back_leg_1", CubeListBuilder.create()
				.texOffs(20, 21).addBox(-2.0F, 0.0F, -1.0F, 2, 1, 1),
			PartPose.offsetAndRotation(-1.0F, 1.0F, -0.5F, 0.0F, -0.22759093446006054F, -0.4553564018453205F));
		leg_back_right1a.addOrReplaceChild("right_back_leg_2", CubeListBuilder.create()
				.texOffs(20, 24).addBox(-2.0F, 0.01F, -1.0F, 2, 1, 1),
			PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, -0.36425021489121656F, 0.0F));

		var tail1 = body_main.addOrReplaceChild("tail_1", CubeListBuilder.create()
				.texOffs(13, 0).addBox(-0.5F, 0.0F, 0.0F, 1, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.045553093477052F, 0.0F, 0.0F));
		var tail2 = tail1.addOrReplaceChild("tail_2", CubeListBuilder.create()
				.texOffs(13, 6).addBox(-0.5F, 0.0F, 0.0F, 1, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, -0.045553093477052F, 0.0F, 0.0F));
		tail2.addOrReplaceChild("tail_2_fin", CubeListBuilder.create()
				.texOffs(22, -3).addBox(0.0F, -0.5F, 0.0F, 0, 1, 3),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		var tail3 = tail2.addOrReplaceChild("tail_3", CubeListBuilder.create()
				.texOffs(13, 12).addBox(-0.5F, 0.0F, 0.0F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 1.0F, 3.0F, -0.045553093477052F, 0.0F, 0.0F));
		tail3.addOrReplaceChild("tail_3_fin", CubeListBuilder.create()
				.texOffs(22, -1).addBox(0.0F, -1.5F, 0.0F, 0, 2, 3),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		var tail4 = tail3.addOrReplaceChild("tail_4", CubeListBuilder.create()
				.texOffs(13, 17).addBox(-0.5F, 0.0F, 0.0F, 1, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, -0.136659280431156F, 0.0F, 0.0F));
		tail4.addOrReplaceChild("tail_4_fin", CubeListBuilder.create()
				.texOffs(22, 2).addBox(0.0F, -1.5F, 0.0F, 0, 3, 3),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 32, 32);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(Olm entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.xRot = (float) Mth.clamp(Math.toRadians(headPitch), -5, 5);
	}

	@Override
	public void prepareMobModel(Olm entity, float limbSwing, float limbSwingAmount, float partialTick) {
		float globalDegree = -0.5F;
		float rippleSpeed = -1.0F;
		float frame = entity.tickCount + partialTick;
		this.setInitPose();
		this.swing(this.gills_left1a, rippleSpeed * 0.125F, globalDegree * 0.5F, true, 0.0F, 0.0F, frame, 1.0F);
		this.swing(this.gills_right1a, rippleSpeed * 0.125F, globalDegree * 0.5F, false, 0.0F, 0.0F, frame, 1.0F);
		this.walk(this.gills_top, rippleSpeed * 0.125F, globalDegree * 0.5F, false, 0.0F, 0.0F, frame, 1.0F);
		this.chainSwing(this.root().getAllParts().toArray(ModelPart[]::new), rippleSpeed * 0.5F, globalDegree, 1.5F, limbSwing, limbSwingAmount * 0.75F);
		this.chainFlap(this.root().getAllParts().toArray(ModelPart[]::new), rippleSpeed, -globalDegree * 0.25F, 1.5F,  limbSwing, limbSwingAmount * 0.75F);
		this.swing(this.leg_back_left1a, rippleSpeed, globalDegree * 3.0F, true, 1.0F, 0.0F, limbSwing, limbSwingAmount);
		this.swing(this.leg_back_right1a, rippleSpeed, globalDegree * 3.0F, true, 1.0F, 0.0F, limbSwing, limbSwingAmount);
		this.swing(this.leg_front_left1a, rippleSpeed, globalDegree * 3.0F, false, 1.0F, 0.0F, limbSwing, limbSwingAmount);
		this.swing(this.leg_front_right1a, rippleSpeed, globalDegree * 3.0F, false, 1.0F, 0.0F, limbSwing, limbSwingAmount);
	}
}
