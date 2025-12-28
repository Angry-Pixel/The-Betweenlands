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
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.RockSnotTendril;

public class RockSnotGrabberModel extends MowzieModelBase<RockSnotTendril> {
    public final ModelPart grabbybit_base;
    public final ModelPart grabber1a;
    public final ModelPart grabber2a;
    public final ModelPart grabber3a;
    public final ModelPart grabber4a;
    public final ModelPart grabber1b;
    public final ModelPart grabber2b;
    public final ModelPart grabber3b;
    public final ModelPart grabber4b;

	public RockSnotGrabberModel(ModelPart root) {
		super(root, RenderType::entityCutout);
		grabbybit_base = root.getChild("grabbybit_base");
		grabber1a = grabbybit_base.getChild("grabber1a");
		grabber1b = grabber1a.getChild("grabber1b");
		grabber2a = grabbybit_base.getChild("grabber2a");
		grabber2b = grabber2a.getChild("grabber2b");
		grabber3a = grabbybit_base.getChild("grabber3a");
		grabber3b = grabber3a.getChild("grabber3b");
		grabber4a = grabbybit_base.getChild("grabber4a");
		grabber4b = grabber4a.getChild("grabber4b");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		var grabbybit_base = partdefinition.addOrReplaceChild("grabbybit_base", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		var grabber3a = grabbybit_base.addOrReplaceChild("grabber3a", CubeListBuilder.create().texOffs(0, 17).addBox(0.0F, -2.0F, -1.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.5463F));
		var grabber3b = grabber3a.addOrReplaceChild("grabber3b", CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, -3.0F, -2.0F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.8196F));
		var grabber2a = grabbybit_base.addOrReplaceChild("grabber2a", CubeListBuilder.create().texOffs(0, 10).addBox(0.0F, -2.0F, -1.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.5463F));
		var grabber2b = grabber2a.addOrReplaceChild("grabber2b", CubeListBuilder.create().texOffs(0, 11).addBox(0.0F, -3.0F, -2.0F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.8196F));
		var grabber1a = grabbybit_base.addOrReplaceChild("grabber1a", CubeListBuilder.create().texOffs(0, 5).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -1.0F, 0.5463F, 0.0F, 0.0F));
		var grabber1b = grabber1a.addOrReplaceChild("grabber1b", CubeListBuilder.create().texOffs(0, 8).addBox(-2.0F, -3.0F, 0.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.8196F, 0.0F, 0.0F));
		var grabber4a = grabbybit_base.addOrReplaceChild("grabber4a", CubeListBuilder.create().texOffs(0, 26).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 1.0F, -0.5463F, 0.0F, 0.0F));
		var grabber4b = grabber4a.addOrReplaceChild("grabber4b", CubeListBuilder.create().texOffs(0, 29).addBox(-2.0F, -3.0F, 0.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.8196F, 0.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int colour) {
    	grabbybit_base.render(stack, consumer, light, overlay, colour);
    }

	@Override
	public void setupAnim(RockSnotTendril grabber, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		grabber1a.xRot = 0.5462880558742251F + (grabber.getExtending() ? convertDegtoRad(ageInTicks * 4F) : convertDegtoRad(80F));
		grabber2a.zRot = 0.5462880558742251F + (grabber.getExtending() ? convertDegtoRad(ageInTicks * 4F) : convertDegtoRad(80F));
		grabber3a.zRot = -0.5462880558742251F - (grabber.getExtending() ? convertDegtoRad(ageInTicks * 4F) : convertDegtoRad(80F));
		grabber4a.xRot = -0.5462880558742251F - (grabber.getExtending() ? convertDegtoRad(ageInTicks * 4F) : convertDegtoRad(80F));

		grabber1b.xRot = -0.8196066167365371F + (grabber.getExtending() ? convertDegtoRad(ageInTicks * 2F) : convertDegtoRad(40F));
		grabber2b.zRot = -0.8196066167365371F + (grabber.getExtending() ? convertDegtoRad(ageInTicks * 2F) : convertDegtoRad(40F));
		grabber3b.zRot = 0.8196066167365371F - (grabber.getExtending() ? convertDegtoRad(ageInTicks * 2F) : convertDegtoRad(40F));
		grabber4b.xRot = 0.8196066167365371F - (grabber.getExtending() ? convertDegtoRad(ageInTicks * 2F) : convertDegtoRad(40F));
	}

	public float convertDegtoRad(float angleIn) {
		return angleIn * ((float) Math.PI / 180F);
	}
}
