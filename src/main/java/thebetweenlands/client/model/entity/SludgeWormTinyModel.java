package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import thebetweenlands.common.entity.monster.SludgeWormTiny;

public class SludgeWormTinyModel extends HierarchicalModel<SludgeWormTiny> {
	public ModelPart root;
	public ModelPart head;
	public ModelPart beak_right;
	public ModelPart beak_left;
	public ModelPart dat_detailed_hot_bod;
	public ModelPart cute_lil_butt;
	public ModelPart spoopy_stinger;

	public SludgeWormTinyModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("head");
		this.beak_right = head.getChild("beak_right");
		this.beak_left = head.getChild("beak_left");
		this.dat_detailed_hot_bod = root.getChild("dat_detailed_hot_bod");
		this.cute_lil_butt = root.getChild("cute_lil_butt");
		this.spoopy_stinger = cute_lil_butt.getChild("spoopy_stinger");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var dat_detailed_hot_bod = partDefinition.addOrReplaceChild("dat_detailed_hot_bod", CubeListBuilder.create()
				.texOffs(13, 0)
				.addBox(-1.5F, -1.5F, 0.F, 3, 3, 3),
				PartPose.offset(0.0F, 22.5F, 0.0F));

		var head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3), 
				PartPose.offset(0.0F, 22.5F, 0.0F));

		head.addOrReplaceChild("beak_left", CubeListBuilder.create()
				.texOffs(0, 14).addBox(-2F, -2F, -2F, 2, 3, 3),
				PartPose.offsetAndRotation(1.5F, 0.5F, -1.5F, 0F, -0.31869712141416456F, 0F));

		head.addOrReplaceChild("beak_right", CubeListBuilder.create()
				.texOffs(0, 7).addBox(0F, -1.5F, -2F, 2, 3, 3),
				PartPose.offsetAndRotation(-1.5F, 0F, -1.5F, 0F, 0.31869712141416456F, 0F));

		var cute_lil_butt = partDefinition.addOrReplaceChild("cute_lil_butt", CubeListBuilder.create().
				texOffs(13, 7)
				.addBox(-1F, -1F, 0F, 2, 2, 2),
				PartPose.offset(0.0F, 23.0F, 0.0F));

		cute_lil_butt.addOrReplaceChild("spoopy_stinger", CubeListBuilder.create()
				.texOffs(13, 11)
				.addBox(-0.5F, 0F, 0F, 1, 2, 2),
				PartPose.offsetAndRotation(0F, -1.3F, 1F, -0.18203784098300857F, 0F, 0F));

		return LayerDefinition.create(definition, 32, 32);
	}

	public void renderHead(PoseStack stack, VertexConsumer consumer, int light, int overlay, int colour, SludgeWormTiny worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.tickCount + frame + (worm.tickCount + frame - (worm.tickCount + frame - 1)) * partialTicks;
		float wibble = (float) (Math.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength);
		float jaw_wibble = (float) (Math.sin(1F + (smoothedTicks) * 0.5F) * 0.5F);
		stack.translate(0F, -0.0625F - wibble * 0.5F, 0F + wibble * 2F);
		head.render(stack, consumer, light, overlay, colour);
		head.xRot = worm.getXRot() / (180F / (float) Math.PI);
		beak_left.yRot = 0F - jaw_wibble;
		beak_right.yRot = 0F + jaw_wibble;
	}

	public void renderBody(PoseStack stack, VertexConsumer consumer, int light, int overlay, int colour, SludgeWormTiny worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.tickCount + frame + (worm.tickCount + frame - (worm.tickCount + frame - 1)) * partialTicks;
		float wibble = (float) (Math.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength);
		stack.translate(0F, -0.125F - wibble, -0.125F - wibble * 2F);
		stack.scale(1F + wibble * 2F, 1F + wibble, 1.25F - wibble * 1.5F);
		dat_detailed_hot_bod.render(stack, consumer, light, overlay, colour);
	}

	public void renderTail(PoseStack stack, VertexConsumer consumer, int light, int overlay, int colour, SludgeWormTiny worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.tickCount + frame + (worm.tickCount + frame - (worm.tickCount + frame - 1)) * partialTicks;
		float wibble = (float) (Math.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength);
		stack.translate(0F, -0.0625F - wibble * 0.5F, -0.0625F + wibble * 2F);
		cute_lil_butt.render(stack, consumer, light, overlay, colour);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(SludgeWormTiny entity, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {
		// TODO Auto-generated method stub
	}
}