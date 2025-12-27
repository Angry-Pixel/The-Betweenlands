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
import thebetweenlands.common.entity.monster.Angler;

public class AnglerModel extends MowzieModelBase<Angler> {
	public ModelPart lure1;
	public ModelPart lure2;
	public ModelPart lure3;
	public ModelPart head;
	public ModelPart jaw;
	public ModelPart bottomTeeth;
	public ModelPart topTeeth;
	public ModelPart body;
	public ModelPart midSection;
	public ModelPart dorsalFin;
	public ModelPart pectoralFinL;
	public ModelPart pectoralFinR;
	public ModelPart tail;
	public ModelPart tailFin;

	public AnglerModel(ModelPart root) {
		super(root, RenderType::entityTranslucent);
		lure1 = root.getChild("lure1");
		lure2 = root.getChild("lure2");
		lure3 = root.getChild("lure3");
		head = root.getChild("head");
		jaw = root.getChild("jaw");
		bottomTeeth = root.getChild("bottomTeeth");
		topTeeth = root.getChild("topTeeth");
		body = root.getChild("body");
		midSection = root.getChild("midSection");
		dorsalFin = root.getChild("dorsalFin");
		pectoralFinL = root.getChild("pectoralFinL");
		pectoralFinR = root.getChild("pectoralFinR");
		tail = midSection.getChild("tail");
		tailFin = midSection.getChild("tailFin");
	}

	@SuppressWarnings("unused")
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		var lure1 = partdefinition.addOrReplaceChild("lure1", CubeListBuilder.create().texOffs(2, 0).addBox(-0.5F, -3.0F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.0F, 0.0F, 0.8203F, 0.0F, 0.0F));
		var lure2 = partdefinition.addOrReplaceChild("lure2", CubeListBuilder.create().texOffs(0, 4).addBox(-0.5F, -4.0F, -3.5F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.0F, 0.0F, 0.8203F, 0.0F, 0.0F));
		var lure3 = partdefinition.addOrReplaceChild("lure3", CubeListBuilder.create().texOffs(0, 9).addBox(-1.5F, -5.0F, -4.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.0F, 0.0F, 0.8203F, 0.0F, 0.0F));
		var head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 19).addBox(-2.0F, -5.0F, 0.0F, 4.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 0.8203F, 0.0F, 0.0F));
		var jaw = partdefinition.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(14, 13).addBox(-1.5F, -4.0F, -1.0F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 1.3783F, 0.0F, 0.0F));
		var bottomTeeth = partdefinition.addOrReplaceChild("bottomTeeth", CubeListBuilder.create().texOffs(8, 0).addBox(-1.5F, -3.5F, 0.0F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 1.3783F, 0.0F, 0.0F));
		var topTeeth = partdefinition.addOrReplaceChild("topTeeth", CubeListBuilder.create().texOffs(16, 0).addBox(-2.0F, -4.5F, -2.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 0.8203F, 0.0F, 0.0F));
		var body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(22, 17).addBox(-1.5F, -8.0F, 1.0F, 3.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));
		var midSection = partdefinition.addOrReplaceChild("midSection", CubeListBuilder.create().texOffs(42, 21).addBox(-1.0F, -7.0F, 8.0F, 2.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 0.0F, -0.0243F, 0.0F));
		var tail = midSection.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(56, 26).addBox(-0.5F, -17.5F, 0.0F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.0F, 13.0F, 0.0F, 0.0756F, 0.0F));
		var tailFin = midSection.addOrReplaceChild("tailFin", CubeListBuilder.create().texOffs(58, -3).addBox(0.0F, -18.0F, 3.0F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.0F, 13.0F, 0.0F, 0.0756F, 0.0F));
		var dorsalFin = partdefinition.addOrReplaceChild("dorsalFin", CubeListBuilder.create().texOffs(46, -6).addBox(0.0F, -11.0F, 5.0F, 0.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, -0.1396F, -0.0243F, 0.0F));
		var pectoralFinL = partdefinition.addOrReplaceChild("pectoralFinL", CubeListBuilder.create().texOffs(35, 0).addBox(0.0F, 0.0F, -1.5F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 11.0F, 2.0F, -0.5585F, -0.6217F, -0.2618F));
		var pectoralFinR = partdefinition.addOrReplaceChild("pectoralFinR", CubeListBuilder.create().texOffs(35, 0).addBox(-4.0F, 0.0F, -1.5F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 11.0F, 2.0F, -0.5585F, 0.6217F, 0.2618F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void setupAnim(Angler angler, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		float flap = Mth.sin((ageInTicks + partialTick) * 0.5F) * 0.6F;
        if (angler.isGrounded())
        	flap = Mth.sin((ageInTicks + partialTick) * 1.5F) * 0.6F;
        jaw.xRot = 1.5F + flap*0.5F;
        bottomTeeth.xRot = 1.5F + flap*0.5F;
		dorsalFin.yRot = midSection.yRot = -0.05F + flap * 0.2F;
		pectoralFinR.yRot = 0.5F - flap;
		pectoralFinL.yRot = -0.5F + flap;
		tail.yRot = midSection.yRot * 1.2F;
		tail.yRot = midSection.yRot * 1.4F;
		tailFin.yRot = midSection.yRot * 1.6F;
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int colour) {
		lure1.render(stack, consumer, light, overlay, colour);
		lure2.render(stack, consumer, light, overlay, colour);
		lure3.render(stack, consumer, light, overlay, colour);
		head.render(stack, consumer, light, overlay, colour);
		jaw.render(stack, consumer, light, overlay, colour);
		bottomTeeth.render(stack, consumer, light, overlay, colour);
		topTeeth.render(stack, consumer, light, overlay, colour);
		body.render(stack, consumer, light, overlay, colour);
		midSection.render(stack, consumer, light, overlay, colour);
		dorsalFin.render(stack, consumer, light, overlay, colour);
		pectoralFinL.render(stack, consumer, light, overlay, colour);
		pectoralFinR.render(stack, consumer, light, overlay, colour);
	}

}