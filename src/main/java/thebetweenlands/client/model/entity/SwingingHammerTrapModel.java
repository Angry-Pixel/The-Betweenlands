package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.SwingingHammerTrap;

public class SwingingHammerTrapModel extends MowzieModelBase<SwingingHammerTrap> {
	private final ModelPart fixing_plate;
	private final ModelPart weapon;

	public SwingingHammerTrapModel(ModelPart root) {
		super(root, RenderType::entityCutout);
		this.fixing_plate = root.getChild("fixing_plate");
		this.weapon = root.getChild("weapon");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition fixing_plate = partdefinition.addOrReplaceChild("fixing_plate", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -3.0F, -6.0F, 8.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(18, 35).addBox(1.0F, 1.0F, -2.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(9, 31).addBox(-3.0F, 1.0F, -2.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(7, 9).addBox(2.0F, -1.0F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(7, 6).addBox(-3.0F, -1.0F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(7, 3).addBox(-3.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(7, 0).addBox(2.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition axle_hole_back_right = fixing_plate.addOrReplaceChild("axle_hole_back_right", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -4.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 6).addBox(-5.0F, -4.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 2.0F, -2.0F, 0.3927F, 0.0F, 0.0F));

		PartDefinition axle_hole_back_left = fixing_plate.addOrReplaceChild("axle_hole_back_left", CubeListBuilder.create().texOffs(31, 35).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 37).addBox(-5.0F, -4.0F, -1.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 2.0F, 2.0F, -0.4451F, 0.0F, 0.0F));

		PartDefinition weapon = partdefinition.addOrReplaceChild("weapon", CubeListBuilder.create().texOffs(25, 15).addBox(-4.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 27).addBox(-1.0F, 1.0F, -1.0F, 2.0F, 22.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(21, 23).addBox(-4.0F, 17.0F, 3.0F, 8.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 15).addBox(-4.0F, 17.0F, -7.0F, 8.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(29, 0).addBox(-2.0F, 19.0F, -3.0F, 4.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	public float convertDegtoRad(float angle) {
		return angle * Mth.DEG_TO_RAD;
	}

	@Override
	public void setupAnim(SwingingHammerTrap trap, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		float swingAngle = convertDegtoRad(trap.getSwingTicks());
		weapon.xRot = swingAngle;
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		fixing_plate.render(stack, consumer, light, overlay, color);
		weapon.render(stack, consumer, light, overlay, color);
	}
}