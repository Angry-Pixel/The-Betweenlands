package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.chiromaw.ChiromawHatchling;

public class ChiromawHatchlingModel extends MowzieModelBase<ChiromawHatchling> {

	private final ModelPart egg;
	private final ModelPart chiromaw;
	private final ModelPart neck;
	private final ModelPart leftArm1;
	private final ModelPart rightArm1;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart leftArm2;
	private final ModelPart rightArm2;

	public ChiromawHatchlingModel(ModelPart root) {
		super(root, RenderType::entityTranslucent);
		this.egg = root.getChild("egg_base");
		this.chiromaw = root.getChild("chiromaw_base");
		var body = this.chiromaw.getChild("chiromaw_body");
		this.neck = body.getChild("neck");
		this.head = this.neck.getChild("head1");
		this.jaw = this.head.getChild("head2").getChild("jaw");

		this.leftArm1 = body.getChild("left_arm1");
		this.leftArm2 = this.leftArm1.getChild("left_arm2");
		this.rightArm1 = body.getChild("right_arm1");
		this.rightArm2 = this.rightArm1.getChild("right_arm2");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();


		var eggBase = partDefinition.addOrReplaceChild("egg_base", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-5.0F, -2.0F, -5.0F, 10, 2, 10),
			PartPose.offset(0.0F, 24.0F, 0.0F));
		var egg1 = eggBase.addOrReplaceChild("egg1", CubeListBuilder.create()
				.texOffs(0, 13).addBox(-6.0F, -8.0F, -6.0F, 12, 8, 12),
			PartPose.offset(0.0F, -2.0F, 0.0F));
		egg1.addOrReplaceChild("egg2", CubeListBuilder.create()
				.texOffs(0, 34).addBox(-5.0F, -4.0F, -5.0F, 10, 4, 10),
			PartPose.offset(0.0F, -8.0F, 0.0F));

		var chiromawBase = partDefinition.addOrReplaceChild("chiromaw_base", CubeListBuilder.create()
				.texOffs(65, 0).addBox(-1.5F, 0.0F, 0.0F, 3, 3, 2),
			PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, -1.1383037381507017F, 0.0F, 0.0F));
		var chiromawBody = chiromawBase.addOrReplaceChild("chiromaw_body", CubeListBuilder.create()
				.texOffs(65, 6).addBox(-2.5F, 0.0F, -4.0F, 5, 3, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.31869712141416456F, 0.0F, 0.0F));

		var rightArm1 = chiromawBody.addOrReplaceChild("right_arm1", CubeListBuilder.create()
				.texOffs(97, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1),
			PartPose.offsetAndRotation(-2.5F, 1.0F, -3.0F, 0.40980330836826856F, -1.0471975511965976F, 0.8651597102135892F));
		var rightArm2 = rightArm1.addOrReplaceChild("right_arm2", CubeListBuilder.create()
				.texOffs(97, 4).addBox(-0.51F, 0.0F, -1.0F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, 1.5F, 0.5F, -0.9105382707654417F, 0.0F, 0.0F));
		rightArm1.addOrReplaceChild("right_wing1", CubeListBuilder.create()
				.texOffs(97, 11).addBox(0.0F, 0.0F, 0.0F, 0, 2, 2),
			PartPose.offsetAndRotation(-0.5F, -0.5F, 0.5F, 0.0F, 0.9105382707654417F, 0.0F));
		rightArm2.addOrReplaceChild("right_wing2", CubeListBuilder.create()
				.texOffs(97, 6).addBox(0.0F, 0.0F, 0.0F, 0, 3, 3),
			PartPose.offsetAndRotation(-0.5F, 0.0F, 0.0F, 0.0F, 0.4553564018453205F, 0.0F));

		var leftArm1 = chiromawBody.addOrReplaceChild("left_arm1", CubeListBuilder.create()
				.texOffs(90, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1),
			PartPose.offsetAndRotation(2.5F, 1.0F, -3.0F, 0.40980330836826856F, 1.0471975511965976F, -0.8651597102135892F));
		var leftArm2 = leftArm1.addOrReplaceChild("left_arm2", CubeListBuilder.create()
				.texOffs(90, 4).addBox(-0.49F, 0.0F, -1.0F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, 1.5F, 0.5F, -0.9105382707654417F, 0.0F, 0.0F));
		leftArm1.addOrReplaceChild("left_wing1", CubeListBuilder.create()
				.texOffs(90, 11).addBox(0.0F, 0.0F, 0.0F, 0, 2, 2),
			PartPose.offsetAndRotation(0.5F, -0.5F, 0.5F, 0.0F, -0.9105382707654417F, 0.0F));
		leftArm2.addOrReplaceChild("left_wing2", CubeListBuilder.create()
				.texOffs(90, 6).addBox(0.0F, 0.0F, 0.0F, 0, 3, 3),
			PartPose.offsetAndRotation(0.5F, 0.0F, 0.0F, 0.0F, -0.4553564018453205F, 0.0F));

		var neck = chiromawBody.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(65, 14).addBox(-1.0F, 0.0F, -1.0F, 2, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.22759093446006054F, 0.0F, 0.0F));
		var head1 = neck.addOrReplaceChild("head1", CubeListBuilder.create()
				.texOffs(65, 18).addBox(-2.5F, -2.0F, -5.0F, 5, 4, 5),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.40980330836826856F, 0.0F, 0.0F));
		var head2 = head1.addOrReplaceChild("head2", CubeListBuilder.create()
				.texOffs(65, 28).addBox(-2.5F, 0.0F, -2.0F, 5, 1, 2),
			PartPose.offset(0.0F, 2.0F, 0.0F));
		head2.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(65, 32).addBox(-2.0F, 0.0F, -3.0F, 4, 1, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.5918411493512771F, 0.0F, 0.0F));
		head1.addOrReplaceChild("left_tooth", CubeListBuilder.create()
				.texOffs(80, 32).addBox(0.0F, 0.0F, 0.0F, 1, 1, 1),
			PartPose.offset(1.5F, 2.0F, -5.0F));
		head1.addOrReplaceChild("right_tooth", CubeListBuilder.create()
				.texOffs(80, 35).addBox(-1.0F, 0.0F, 0.0F, 1, 1, 1),
			PartPose.offset(-1.5F, 2.0F, -5.0F));
		head1.addOrReplaceChild("egg3", CubeListBuilder.create()
				.texOffs(0, 49).addBox(-3.0F, -2.0F, -3.0F, 6, 2, 6),
			PartPose.offsetAndRotation(0.0F, -0.5F, -2.5F, 0.0F, 0.091106186954104F, -0.136659280431156F));

		var leftLeg = chiromawBase.addOrReplaceChild("left_leg1", CubeListBuilder.create()
				.texOffs(90, 17).addBox(0.0F, -1.0F, -1.0F, 1, 2, 2),
			PartPose.offsetAndRotation(1.5F, 1.0F, 1.0F, 0.36425021489121656F, -0.27314402793711257F, -0.36425021489121656F));
		leftLeg.addOrReplaceChild("left_leg2", CubeListBuilder.create()
				.texOffs(90, 22).addBox(-0.01F, 0.0F, 0.0F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, 1.0F, -1.0F, 0.40980330836826856F, 0.0F, 0.0F));

		var rightLeg = chiromawBase.addOrReplaceChild("right_leg1", CubeListBuilder.create()
				.texOffs(97, 17).addBox(-1.0F, -1.0F, -1.0F, 1, 2, 2),
			PartPose.offsetAndRotation(-1.5F, 1.0F, 1.0F, 0.36425021489121656F, 0.27314402793711257F, 0.36425021489121656F));
		rightLeg.addOrReplaceChild("right_leg2", CubeListBuilder.create()
				.texOffs(97, 22).addBox(-1.0F, 0.0F, 0.0F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, 1.0F, -1.0F, 0.40980330836826856F, 0.0F, 0.0F));

		var tail1 = chiromawBase.addOrReplaceChild("tail1", CubeListBuilder.create()
				.texOffs(65, 37).addBox(-0.5F, -1.0F, 0.0F, 1, 1, 2),
			PartPose.offsetAndRotation(0.0F, 1.0F, 2.0F, 0.27314402793711257F, 0.0F, 0.0F));
		tail1.addOrReplaceChild("tail2", CubeListBuilder.create()
				.texOffs(72, 37).addBox(-0.5F, -1.0F, 0.0F, 1, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.40980330836826856F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}

	public void renderBaby(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.chiromaw.render(stack, consumer, light, overlay, color);
	}

	public void renderEgg(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.egg.render(stack, consumer, light, overlay, color);
	}

	@Override
	public void setupAnim(ChiromawHatchling entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		float globalSpeed = 1F;
		float globalDegree = 0.5F;
		float flap = Mth.sin((ageInTicks) * 0.5F) * 0.6F;
		float smoother = Mth.lerp(partialTick, entity.prevRise, entity.getRiseCount());
		float smootherFeed = Mth.lerp(partialTick, entity.prevFeederRotation, entity.feederRotation);
		float smootherHead = Mth.lerp(partialTick, entity.prevHeadPitch, entity.headPitch);
		this.chiromaw.yRot = this.convertDegtoRad(smootherFeed);
		if (!entity.getIsTransforming()) {
			this.head.xRot = this.convertDegtoRad(-43.5F + smootherHead * 1.5F);
			this.jaw.xRot = this.convertDegtoRad(4F + smoother * 1.5F);
			this.rightArm1.xRot = this.convertDegtoRad(-23.5F + smoother);
			this.leftArm1.xRot = this.convertDegtoRad(-23.5F + smoother);
			this.rightArm2.xRot = this.convertDegtoRad(-92F + smoother);
			this.leftArm2.xRot = this.convertDegtoRad(-92F + smoother);
			this.rightArm1.yRot = this.convertDegtoRad(-20F - smootherHead * 1.5F);
			this.leftArm1.yRot = this.convertDegtoRad(20F + smootherHead * 1.5F);
			this.rightArm2.yRot = this.convertDegtoRad(60F - smootherHead * 1.5F);
			this.leftArm2.yRot = this.convertDegtoRad(-60F + smootherHead * 1.5F);

			if (entity.flapArms) {
				this.flap(this.rightArm2, globalSpeed, globalDegree * 0.25f, false, 2.0f, 0f, entity.flapArmsCount, 1F);
				this.flap(this.leftArm2, globalSpeed, globalDegree * 0.25f, true, 2.0f, 0f, entity.flapArmsCount, 1F);
				this.flap(this.rightArm1, globalSpeed, globalDegree * 0.25f, false, 2.0f, 0f, entity.flapArmsCount, 1F);
				this.flap(this.leftArm1, globalSpeed, globalDegree * 0.25f, true, 2.0f, 0f, entity.flapArmsCount, 1F);
			}

			if (entity.getRiseCount() >= ChiromawHatchling.MAX_RISE - 20 && entity.getIsHungry()) {
				this.neck.yRot = 0F + flap;
				this.walk(this.rightArm2, globalSpeed * 0.5f, globalDegree * 0.5f, false, 2.0f, 0f, ageInTicks, 1F);
				this.walk(this.leftArm2, globalSpeed * 0.5f, globalDegree * 0.5f, false, 2.0f, 0f, ageInTicks, 1F);
			} else {
				this.neck.yRot = 0F;
			}

			if (entity.getIsChewing()) {
				this.swing(this.jaw, globalSpeed * 0.75f, globalDegree * 0.5f, false, 2.0f, 0f, ageInTicks / ((float) Math.PI), 1F);
				this.walk(this.jaw, globalSpeed * 0.5f, globalDegree * 0.5f, false, 2.0f, -0.75f, ageInTicks, 1F);
			} else if (!entity.getIsHungry() && entity.getRiseCount() >= ChiromawHatchling.MAX_RISE)
				this.walk(this.jaw, globalSpeed * 0.125f, globalDegree * 0.5f, false, 2.0f, -0.75f, ageInTicks, 1F);
		}
		if (entity.getIsTransforming()) {
			this.flap(this.rightArm1, globalSpeed * 0.5f, globalDegree, false, 2.0f, 0f, ageInTicks, 1F);
			this.flap(this.leftArm1, globalSpeed * 0.5f, globalDegree, true, 2.0f, 0f, ageInTicks, 1F);
			this.flap(this.rightArm2, globalSpeed * 0.5f, globalDegree, false, 2.0f, 0f, ageInTicks, 1F);
			this.flap(this.leftArm2, globalSpeed * 0.5f, globalDegree, true, 2.0f, 0f, ageInTicks, 1F);

			this.swing(this.rightArm1, globalSpeed * 0.5f, globalDegree * 1.1f, false, 2.8f, 0.5f, ageInTicks, 1F);
			this.swing(this.leftArm1, globalSpeed * 0.5f, globalDegree * 1.1f, true, 2.8f, -0.5f, ageInTicks, 1F);
			this.walk(this.jaw, globalSpeed * 0.125f, globalDegree * 0.5f, false, 2.0f, 0f, ageInTicks, 1F);
		}
	}
}
