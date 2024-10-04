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
import net.minecraft.client.renderer.texture.OverlayTexture;
import thebetweenlands.common.entity.monster.SludgeWorm;

public class SludgeWormModel extends HierarchicalModel<SludgeWorm> {
	public ModelPart root;
	public ModelPart head1;
	public ModelPart mouth_left;
	public ModelPart mouth_bottom;
	public ModelPart jaw_bottom_left;
	public ModelPart jaw_bottom_right;
	public ModelPart butt;
    public ModelPart pincer_thingy_i_guess_a;
    public ModelPart pincer_thingy_i_guess_b;

	public ModelPart body1;

	public SludgeWormModel(ModelPart root) {
		
		this.root = root;
		this.head1 = root.getChild("head1");
		this.mouth_left = head1.getChild("mouth_left");
		this.mouth_bottom = head1.getChild("mouth_bottom");
		this.jaw_bottom_left = head1.getChild("jaw_bottom_left");
		this.jaw_bottom_right = head1.getChild("jaw_bottom_right");
		this.butt = root.getChild("butt");
		this.pincer_thingy_i_guess_a = butt.getChild("pincer_thingy_i_guess_a");
		this.pincer_thingy_i_guess_b = pincer_thingy_i_guess_a.getChild("pincer_thingy_i_guess_b");
		this.body1 = root.getChild("body1");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();
		
		var head1 = partDefinition.addOrReplaceChild("head1", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5),
			PartPose.offset(0.0F, 21.5F, 0.0F));
		
		head1.addOrReplaceChild("mouth_left", CubeListBuilder.create()
				.texOffs(0, 11)
				.addBox(-2.0F, -1.5F, -2.0F, 2, 3, 3),
			PartPose.offsetAndRotation(2.0F, -0.5F, -2.5F, 0.0F, -0.36425021489121656F, -0.22759093446006054F));
		
		head1.addOrReplaceChild("mouth_bottom", CubeListBuilder.create()
				.texOffs(13, 11)
				.addBox(0.0F, -1.5F, -2.0F, 2, 3, 3),
			PartPose.offsetAndRotation(-2.0F, -0.5F, -2.5F, 0.0F, 0.36425021489121656F, 0.22759093446006054F));
		
		head1.addOrReplaceChild("jaw_bottom_left", CubeListBuilder.create()
				.texOffs(0, 18)
				.addBox(-0.5F, 0.0F, -3.5F, 1, 2, 4),
			PartPose.offsetAndRotation(1.5F, 1.0F, -2.5F, 0.136659280431156F, 0.0F, -0.7740535232594852F));
		
		head1.addOrReplaceChild("jaw_bottom_right", CubeListBuilder.create()
				.texOffs(11, 18)
				.addBox(-0.5F, 0.0F, -3.5F, 1, 2, 4),
			PartPose.offsetAndRotation(-1.5F, 1.0F, -2.5F, 0.136659280431156F, 0.0F, 0.7740535232594852F));
		
		var butt = partDefinition.addOrReplaceChild("butt", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-2.0F, -1.5F, -1.5F, 4, 4, 4),
				PartPose.offset(0.0F, 21.5F, 0.0F));
		
		var pincer_thingy_i_guess_a = butt.addOrReplaceChild("pincer_thingy_i_guess_a", CubeListBuilder.create()
				.texOffs(0, 9)
				.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 2),
			PartPose.offsetAndRotation(0.0F, -0.2F, 2.5F, -0.22759093446006054F, 0.0F, 0.0F));
		
		pincer_thingy_i_guess_a.addOrReplaceChild("pincer_thingy_i_guess_b", CubeListBuilder.create()
				.texOffs(7, 9)
				.addBox(-0.5F, -2.0F, 0.0F, 1, 2, 3),
			PartPose.offsetAndRotation(0.0F, 2.0F, 2.0F, 0.18203784098300857F, 0.0F, 0.0F));

		var body1 = partDefinition.addOrReplaceChild("body1", CubeListBuilder.create()
				.texOffs(0, 15)
				.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5),
				PartPose.offset(0.0F, 21.5F, 0.0F));

		return LayerDefinition.create(definition, 32, 32);
	}

	public void renderHead(PoseStack stack, VertexConsumer consumer, int light, SludgeWorm worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.tickCount + frame + (worm.tickCount + frame - (worm.tickCount + frame - 1)) * partialTicks;
		float wibble = (float) (Math.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength);
		float jaw_wibble = (float) (Math.sin(1F + (smoothedTicks) * 0.5F) * 0.5F);
		stack.translate(0F, - 0.0625F - wibble * 0.5F, 0F + wibble * 2F);
		head1.render(stack, consumer, light, OverlayTexture.NO_OVERLAY);
		head1.xRot = worm.getXRot() / (180F / (float) Math.PI);
	    jaw_bottom_left.yRot =  0F - jaw_wibble;
	    jaw_bottom_right.yRot = 0F + jaw_wibble;
	    mouth_bottom.yRot =  0F - jaw_wibble;
	    mouth_left.yRot = 0F + jaw_wibble;
	}

	public void renderBody(PoseStack stack, VertexConsumer consumer, int light, SludgeWorm worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.tickCount + frame + (worm.tickCount + frame - (worm.tickCount + frame - 1)) * partialTicks;
		float wibble = (float) (Math.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength);
		stack.translate(0F, 0F - wibble, 0F - wibble * 2F);
		stack.scale(1F + wibble * 2F, 1F + wibble, 1.25F - wibble * 1.5F);
		body1.render(stack, consumer, light, OverlayTexture.NO_OVERLAY);
	}
	
	public void renderTail(PoseStack stack, VertexConsumer consumer, int light, SludgeWorm worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.tickCount + frame + (worm.tickCount + frame - (worm.tickCount + frame - 1)) * partialTicks;
		float wibble = (float) (Math.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength);
		stack.translate(0F, - 0.0625F - wibble * 0.5F, - 0.0625F + wibble * 2F);
		butt.render(stack, consumer, light, OverlayTexture.NO_OVERLAY);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(SludgeWorm entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {
		// TODO Auto-generated method stub
		
	}

}
