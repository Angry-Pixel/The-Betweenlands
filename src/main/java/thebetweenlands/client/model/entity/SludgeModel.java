package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.Sludge;
import thebetweenlands.common.world.event.SpoopyEvent;

public class SludgeModel extends MowzieModelBase<Sludge> {

	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart[] spine;
	private final ModelPart slime;

	public SludgeModel(ModelPart root) {
		this.head = root.getChild("head2");
		this.jaw = this.head.getChild("jaw");
		this.spine = new ModelPart[]{this.head.getChild("spine"), this.head.getChild("spine").getChild("spinepiece")};

		this.slime = root.getChild("slime");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var head2 = partDefinition.addOrReplaceChild("head2", CubeListBuilder.create()
				.texOffs(0, 16).addBox(-3.0F, 0.0F, -3.0F, 6, 2, 3, new CubeDeformation(-0.01F)),
			PartPose.offsetAndRotation(0.0F, 15.0F, 3.0F, -0.07435102760791776F, 0.0F, -0.11154399067163465F));
		var head1 = head2.addOrReplaceChild("head1", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0F, -6.0F, -8.0F, 8, 6, 8),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		head2.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(0, 22).addBox(-4.0F, -1.0F, -8.0F, 8, 2, 7),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5940400797409059F, -0.01100643542294784F, 0.1483095853034341F));
		head1.addOrReplaceChild("teeth", CubeListBuilder.create()
				.texOffs(0, 32).addBox(-4.0F, 0.0F, -8.0F, 8, 1, 5),
			PartPose.offset(0.0F, -0.07428254352663011F, 0.9972372354295702F));
		var spine = head2.addOrReplaceChild("spine", CubeListBuilder.create()
				.texOffs(0, 39).addBox(-1.0F, 0.0F, -2.0F, 2, 3, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.33414092254743283F, -0.008268694238004389F, 0.11123836025835071F));
		spine.addOrReplaceChild("spinepiece", CubeListBuilder.create()
				.texOffs(0, 45).addBox(-1.0F, 0.0F, -1.0F, 2, 1, 2),
			PartPose.offsetAndRotation(0.0F, 3.865302432904631F, -1.0292896104505997F, -0.25407912408104716F, 0.056957253174300816F, 0.21579472540118327F));

		var slime = partDefinition.addOrReplaceChild("slime", CubeListBuilder.create(), PartPose.ZERO);
		slime.addOrReplaceChild("slime1", CubeListBuilder.create()
				.texOffs(40, 0).addBox(-9.0F, -7.0F, -9.0F, 18, 14, 18),
			PartPose.offset(0.0F, 15.0F, 0.0F));
		slime.addOrReplaceChild("slime2", CubeListBuilder.create()
				.texOffs(40, 32).addBox(-7.0F, -9.0F, -7.0F, 14, 2, 14),
			PartPose.offset(0.0F, 15.0F, 0.0F));
		slime.addOrReplaceChild("slime3", CubeListBuilder.create()
				.texOffs(40, 48).addBox(-7.0F, 7.0F, -7.0F, 14, 2, 14),
			PartPose.offset(0.0F, 15.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}

	@Override
	public ModelPart root() {
		return this.head;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
		if (Minecraft.getInstance().level == null || !SpoopyEvent.isSpoooopy(Minecraft.getInstance().level)) {
			super.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, color);
		}
	}

	public void renderSlime(PoseStack stack, VertexConsumer consumer, int light, int overlay) {
		this.slime.render(stack, consumer, light, overlay);
	}

	@Override
	public void setupAnim(Sludge entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void prepareMobModel(Sludge entity, float limbSwing, float limbSwingAmount, float partialTick) {
		this.setInitPose();
		float frame = entity.tickCount + partialTick;
		float controller = (0.5F * Mth.sin(frame * 0.1F) * Mth.sin(frame * 0.1F)) + 0.5F;
		this.head.y += 1.5F;
		this.walk(this.jaw, 1.0F, 0.3F * controller, false, 0.0F, -0.2F * controller, frame, 1.0F);
		this.bob(this.head, 0.5F, controller, false, frame, 1.0F);
		this.chainWave(this.spine, 0.5F, 0.2F * controller, -2.0F, frame, 1.0F);
		this.chainFlap(this.spine, 0.25F, 0.4F * controller, -2.0F, frame, 1.0F);
		this.head.x += 2.0F * Mth.sin(frame * 0.25F) * controller;
		this.flap(this.head, 0.25F, 0.2F * controller, false, 0.0F, 0.0F, frame, 1.0F);
	}
}
