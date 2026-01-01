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
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.creature.CaveJellyfish;

public class CaveJellyfishModel extends MowzieModelBase<CaveJellyfish> {

		public CaveJellyfishModel(ModelPart root) {
			super(root, RenderType::entityTranslucent);
		}

		public static LayerDefinition createBodyLayer() {
			MeshDefinition meshdefinition = new MeshDefinition();
			PartDefinition partdefinition = meshdefinition.getRoot();

			PartDefinition mesoglea_base = partdefinition.addOrReplaceChild("mesoglea_base", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

			PartDefinition tentacles_front1a = mesoglea_base.addOrReplaceChild("tentacles_front1a", CubeListBuilder.create().texOffs(0, 11).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.5F, 0.0911F, 0.0F, 0.0F));

			PartDefinition tentacles_front1b = tentacles_front1a.addOrReplaceChild("tentacles_front1b", CubeListBuilder.create().texOffs(0, 14).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, -0.0911F, 0.0F, 0.0F));

			PartDefinition tentacles_front1c = tentacles_front1b.addOrReplaceChild("tentacles_front1c", CubeListBuilder.create().texOffs(0, 17).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, -0.0911F, 0.0F, 0.0F));

			PartDefinition tentacles_left1a = mesoglea_base.addOrReplaceChild("tentacles_left1a", CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, 0.0F, -1.5F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0911F));

			PartDefinition tentacles_left1b = tentacles_left1a.addOrReplaceChild("tentacles_left1b", CubeListBuilder.create().texOffs(0, 21).addBox(0.0F, 0.0F, -1.5F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, -0.0911F));

			PartDefinition tentacles_left1c = tentacles_left1b.addOrReplaceChild("tentacles_left1c", CubeListBuilder.create().texOffs(0, 24).addBox(0.0F, 0.0F, -1.5F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, -0.0911F));

			PartDefinition mouth = mesoglea_base.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(0, 7).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

			PartDefinition oral_arm1a = mouth.addOrReplaceChild("oral_arm1a", CubeListBuilder.create().texOffs(17, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.2276F, -0.7854F, 0.0F));

			PartDefinition oral_arm1b = oral_arm1a.addOrReplaceChild("oral_arm1b", CubeListBuilder.create().texOffs(17, 4).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, -0.5009F, 0.0F, 0.0F));

			PartDefinition oral_arm1c = oral_arm1b.addOrReplaceChild("oral_arm1c", CubeListBuilder.create().texOffs(17, 8).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.5009F, 0.0F, 0.0F));

			PartDefinition oral_arm1d = oral_arm1c.addOrReplaceChild("oral_arm1d", CubeListBuilder.create().texOffs(17, 12).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, -0.3187F, 0.0F, 0.0F));

			PartDefinition oral_arm1e = oral_arm1d.addOrReplaceChild("oral_arm1e", CubeListBuilder.create().texOffs(17, 16).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0911F, 0.0F, 0.0F));

			PartDefinition tentacles_back1a = mesoglea_base.addOrReplaceChild("tentacles_back1a", CubeListBuilder.create().texOffs(7, 11).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.5F, -0.0911F, 0.0F, 0.0F));

			PartDefinition tentacles_back1b = tentacles_back1a.addOrReplaceChild("tentacles_back1b", CubeListBuilder.create().texOffs(7, 14).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0911F, 0.0F, 0.0F));

			PartDefinition tentacles_back1c = tentacles_back1b.addOrReplaceChild("tentacles_back1c", CubeListBuilder.create().texOffs(7, 17).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0911F, 0.0F, 0.0F));

			PartDefinition tentacles_right1a = mesoglea_base.addOrReplaceChild("tentacles_right1a", CubeListBuilder.create().texOffs(7, 18).addBox(0.0F, 0.0F, -1.5F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0911F));

			PartDefinition tentacles_right1b = tentacles_right1a.addOrReplaceChild("tentacles_right1b", CubeListBuilder.create().texOffs(7, 21).addBox(0.0F, 0.0F, -1.5F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.0911F));

			PartDefinition tentacles_right1c = tentacles_right1b.addOrReplaceChild("tentacles_right1c", CubeListBuilder.create().texOffs(7, 24).addBox(0.0F, 0.0F, -1.5F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.0911F));

			return LayerDefinition.create(meshdefinition, 32, 32);
		}

		@Override
		public void setupAnim(CaveJellyfish entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {}


		@Override
		public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
			super.renderToBuffer(stack, consumer, LightTexture.FULL_BRIGHT, overlay, color);
		}
}
