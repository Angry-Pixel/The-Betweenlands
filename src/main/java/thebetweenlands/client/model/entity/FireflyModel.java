package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.creature.Firefly;

public class FireflyModel extends MowzieModelBase<Firefly> {

	private final ModelPart root;
	private final ModelPart thorax;
	private final ModelPart head;
	private final ModelPart leftWing;
	private final ModelPart rightWing;

	public FireflyModel(ModelPart root) {
		super(RenderType::entityTranslucent);
		this.root = root;
		this.thorax = root.getChild("thorax");
		this.head = this.thorax.getChild("dekschild").getChild("head");
		this.leftWing = this.thorax.getChild("dekschild").getChild("left_wing");
		this.rightWing = this.thorax.getChild("dekschild").getChild("right_wing");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var thorax = partDefinition.addOrReplaceChild("thorax", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-1.5F, 0.0F, 0.0F, 3, 2, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.40980330836826856F, 0.0F, 0.0F));
		var thorax2 = thorax.addOrReplaceChild("thorax2", CubeListBuilder.create()
				.texOffs(0, 5).addBox(-1.51F, 0.0F, -2.0F, 3, 2, 2),
			PartPose.offsetAndRotation(0.0F, 2.0F, 2.0F, -0.27314402793711257F, 0.0F, 0.0F));
		thorax2.addOrReplaceChild("thorax3", CubeListBuilder.create()
				.texOffs(0, 10).addBox(-1.0F, 0.0F, -2.0F, 2, 1, 2),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.136659280431156F, 0.0F, 0.0F));
		var dekschild = thorax.addOrReplaceChild("dekschild", CubeListBuilder.create()
				.texOffs(0, 13).addBox(-1.0F, 0.0F, -1.0F, 2, 2, 1),
			PartPose.offsetAndRotation(0.0F, -0.0F, 2.0F, -1.1838568316277536F, 0.0F, 0.0F));

		var head = dekschild.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 17).addBox(-1.01F, 0.0F, -2.0F, 2, 2, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.6373942428283291F, 0.0F, 0.0F));
		head.addOrReplaceChild("left_antenna", CubeListBuilder.create()
				.texOffs(8, 0).addBox(0.0F, 0.0F, -3.0F, 2, 0, 3),
			PartPose.offsetAndRotation(0.1F, 0.0F, -2.0F, -0.31869712141416456F, -0.36425021489121656F, 0.091106186954104F));
		head.addOrReplaceChild("right_antenna", CubeListBuilder.create()
				.texOffs(8, 4).addBox(-2.0F, 0.0F, -3.0F, 2, 0, 3),
			PartPose.offsetAndRotation(-0.1F, 0.0F, -2.0F, -0.31869712141416456F, 0.36425021489121656F, -0.091106186954104F));

		dekschild.addOrReplaceChild("left_wing", CubeListBuilder.create()
				.texOffs(16, 0).addBox(0.0F, 0.0F, 0.0F, 5, 2, 0),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5025539530419183F, 0.31869712141416456F, -0.136659280431156F));
		dekschild.addOrReplaceChild("right_wing", CubeListBuilder.create()
				.texOffs(16, 3).addBox(-5.0F, 0.0F, 0.0F, 5, 2, 0),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5025539530419183F, -0.31869712141416456F, 0.136659280431156F));

		var leg_left1 = thorax.addOrReplaceChild("left_leg_1", CubeListBuilder.create()
				.texOffs(11, 8).addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0),
			PartPose.offsetAndRotation(1.0F, 0.4F, 0.0F, -1.0471975511965976F, -0.22759093446006054F, -0.045553093477052F));
		leg_left1.addOrReplaceChild("left_leg_1b", CubeListBuilder.create()
				.texOffs(11, 10).addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0),
			PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.7740535232594852F, 0.0F, 0.0F));

		var leg_right1 = thorax.addOrReplaceChild("right_leg_1", CubeListBuilder.create()
				.texOffs(11, 8).addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0),
			PartPose.offsetAndRotation(-1.0F, 0.4F, 0.0F, -1.0471975511965976F, 0.22759093446006054F, 0.045553093477052F));
		leg_right1.addOrReplaceChild("right_leg_1b", CubeListBuilder.create()
				.texOffs(11, 10).addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0),
			PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.7740535232594852F, 0.0F, 0.0F));

		thorax.addOrReplaceChild("left_leg_2", CubeListBuilder.create()
				.texOffs(11, 13).addBox(-0.5F, 0.0F, 0.0F, 1, 3, 0),
			PartPose.offsetAndRotation(1.0F, 0.8F, 0.0F, -0.5009094953223726F, 0.091106186954104F, 0.0F));

		thorax.addOrReplaceChild("right_leg_2", CubeListBuilder.create()
				.texOffs(11, 13).addBox(-0.5F, 0.0F, 0.0F, 1, 3, 0),
			PartPose.offsetAndRotation(-1.0F, 0.8F, 0.0F, -0.5009094953223726F, -0.091106186954104F, 0.0F));

		thorax.addOrReplaceChild("left_leg_3", CubeListBuilder.create()
				.texOffs(11, 17).addBox(-0.5F, 0.0F, 0.0F, 1, 3, 0),
			PartPose.offsetAndRotation(1.0F, 1.3F, 0.0F, -0.40980330836826856F, -0.091106186954104F, 0.22759093446006054F));

		thorax.addOrReplaceChild("right_leg_3", CubeListBuilder.create()
				.texOffs(11, 17).addBox(-0.5F, 0.0F, 0.0F, 1, 3, 0),
			PartPose.offsetAndRotation(-1.0F, 1.3F, 0.0F, -0.40980330836826856F, 0.091106186954104F, -0.22759093446006054F));

		return LayerDefinition.create(definition, 32, 32);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(Firefly entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float flap = Mth.sin(ageInTicks * 2.0F) * 0.6F;
		this.leftWing.zRot = -0.5F - flap;
		this.rightWing.zRot = 0.5F + flap;

		float swing = Mth.cos(ageInTicks * 0.1F) * 0.15F + 0.1F;
		this.thorax.xRot = 0.40980330836826856F + swing;
		this.head.xRot = 0.6373942428283291F - swing;
	}
}
