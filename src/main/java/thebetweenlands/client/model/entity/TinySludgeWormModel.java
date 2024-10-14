package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import thebetweenlands.common.entity.monster.TinySludgeWorm;

public class TinySludgeWormModel extends HierarchicalModel<TinySludgeWorm> {
	public ModelPart root;
	public ModelPart head;
	public ModelPart beak_right;
	public ModelPart beak_left;
	public ModelPart dat_detailed_hot_bod;
	public ModelPart cute_lil_butt;
	public ModelPart spoopy_stinger;

	public TinySludgeWormModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("head");
		this.beak_right = head.getChild("beak_right");
		this.beak_left = head.getChild("beak_left");
		this.dat_detailed_hot_bod = root.getChild("body");
		this.cute_lil_butt = root.getChild("butt");
		this.spoopy_stinger = cute_lil_butt.getChild("stinger");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(13, 0)
				.addBox(-1.5F, -1.5F, 0.F, 3, 3, 3),
			PartPose.offset(0.0F, 22.5F, 0.0F));

		var head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3),
			PartPose.offset(0.0F, 22.5F, 0.0F));

		head.addOrReplaceChild("beak_left", CubeListBuilder.create()
				.texOffs(0, 14).addBox(-2F, -2F, -2F, 2, 3, 3, new CubeDeformation(-0.01F)),
			PartPose.offsetAndRotation(1.5F, 0.5F, -1.5F, 0F, -0.31869712141416456F, 0F));

		head.addOrReplaceChild("beak_right", CubeListBuilder.create()
				.texOffs(0, 7).addBox(0F, -1.5F, -2F, 2, 3, 3, new CubeDeformation(-0.01F)),
			PartPose.offsetAndRotation(-1.5F, 0F, -1.5F, 0F, 0.31869712141416456F, 0F));

		var cute_lil_butt = partDefinition.addOrReplaceChild("butt", CubeListBuilder.create().
				texOffs(13, 7)
				.addBox(-1F, -1F, 0F, 2, 2, 2),
			PartPose.offset(0.0F, 23.0F, 0.0F));

		cute_lil_butt.addOrReplaceChild("stinger", CubeListBuilder.create()
				.texOffs(13, 11)
				.addBox(-0.5F, 0F, 0F, 1, 2, 2),
			PartPose.offsetAndRotation(0F, -1.3F, 1F, -0.18203784098300857F, 0F, 0F));

		return LayerDefinition.create(definition, 32, 32);
	}

	public void renderHead(PoseStack stack, VertexConsumer consumer, int light, int overlay, int colour, TinySludgeWorm worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.tickCount + frame + (worm.tickCount + frame - (worm.tickCount + frame - 1)) * partialTicks;
		float wibble = (float) (Math.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength);
		float jaw_wibble = (float) (Math.sin(1F + (smoothedTicks) * 0.5F) * 0.5F);
		stack.translate(0F, -0.0625F - wibble * 0.5F, 0F + wibble * 2F);
		this.head.xRot = worm.getXRot() / Mth.RAD_TO_DEG;
		this.beak_left.yRot = 0F - jaw_wibble;
		this.beak_right.yRot = 0F + jaw_wibble;
		this.head.render(stack, consumer, light, overlay, colour);
	}

	public void renderBody(PoseStack stack, VertexConsumer consumer, int light, int overlay, int colour, TinySludgeWorm worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.tickCount + frame + (worm.tickCount + frame - (worm.tickCount + frame - 1)) * partialTicks;
		float wibble = (float) (Math.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength);
		stack.translate(0F, -0.125F - wibble, -0.125F - wibble * 2F);
		stack.scale(1F + wibble * 2F, 1F + wibble, 1.25F - wibble * 1.5F);
		this.dat_detailed_hot_bod.render(stack, consumer, light, overlay, colour);
	}

	public void renderTail(PoseStack stack, VertexConsumer consumer, int light, int overlay, int colour, TinySludgeWorm worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.tickCount + frame + (worm.tickCount + frame - (worm.tickCount + frame - 1)) * partialTicks;
		float wibble = (float) (Math.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength);
		stack.translate(0F, -0.0625F - wibble * 0.5F, -0.0625F + wibble * 2F);
		this.cute_lil_butt.render(stack, consumer, light, overlay, colour);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(TinySludgeWorm entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
}