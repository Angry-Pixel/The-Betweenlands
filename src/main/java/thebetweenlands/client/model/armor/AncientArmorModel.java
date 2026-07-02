package thebetweenlands.client.model.armor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.EquipmentSlot;

public class AncientArmorModel extends BLArmorModel {

	private final EquipmentSlot slot;
	private final ModelPart belt;
	private final ModelPart leftBoot;
	private final ModelPart rightBoot;
	
	public AncientArmorModel(EquipmentSlot slot, ModelPart root) {
		super(root);
		this.slot = slot;
		this.belt = root.getChild("belt");
		this.leftBoot = root.getChild("left_boot");
		this.rightBoot = root.getChild("right_boot");
	}

	public static LayerDefinition makeModel() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		// Hat
		PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
		
		// Helmet
		PartDefinition helmet_mainrotation = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);

		PartDefinition topplate1 = helmet_mainrotation.addOrReplaceChild("topplate1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -1.0F, -3.0F, 9.0F, 3.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -7.0F, -1.5F));

		PartDefinition topplate2 = topplate1.addOrReplaceChild("topplate2", CubeListBuilder.create().texOffs(0, 10).addBox(-4.5F, 0.0F, 0.0F, 9.0F, 3.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.0911F, 0.0F, 0.0F));

		PartDefinition sideplate1 = topplate2.addOrReplaceChild("sideplate1", CubeListBuilder.create().texOffs(0, 20).addBox(-4.5F, 0.0F, 0.0F, 9.0F, 2.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0456F, 0.0F, 0.0F));

		PartDefinition sideplate2 = sideplate1.addOrReplaceChild("sideplate2", CubeListBuilder.create().texOffs(0, 29).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 2.0F, 5.0F, 0.0456F, 0.0F, 0.0F));

		PartDefinition antler_right1a = topplate1.addOrReplaceChild("antler_right1a", CubeListBuilder.create().texOffs(33, 0).mirror().addBox(-2.0F, -1.0F, -1.0F, 2.0F, 2.0F, 3.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(-2.5F, -1.5F, -0.5F, 0.1367F, -0.3187F, -0.2731F));

		PartDefinition antlers_right1j = antler_right1a.addOrReplaceChild("antlers_right1j", CubeListBuilder.create().texOffs(33, 45).mirror().addBox(0.0F, -1.0F, -2.0F, 2.0F, 1.0F, 2.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(-2.0F, -1.0F, 1.0F, 0.0F, 0.0F, 0.2731F));

		PartDefinition antler_right1b = antler_right1a.addOrReplaceChild("antler_right1b", CubeListBuilder.create().texOffs(33, 6).mirror().addBox(-1.99F, 0.0F, -3.0F, 2.0F, 2.0F, 3.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(0.0F, -1.0F, -1.0F, 0.3187F, 0.0F, 0.0F));

		PartDefinition antler_right1c = antler_right1a.addOrReplaceChild("antler_right1c", CubeListBuilder.create().texOffs(33, 12).mirror().addBox(-2.01F, 0.0F, 0.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(0.0F, -1.0F, 2.0F, -0.4098F, 0.0F, 0.0F));

		PartDefinition antler_right1d = antler_right1c.addOrReplaceChild("antler_right1d", CubeListBuilder.create().texOffs(33, 16).mirror().addBox(0.0F, -0.01F, 0.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(-2.0F, 0.0F, 1.0F, 0.0F, 0.2276F, 0.0F));

		PartDefinition antler_right1e = antler_right1d.addOrReplaceChild("antler_right1e", CubeListBuilder.create().texOffs(33, 21).mirror().addBox(-0.01F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(0.0F, 2.0F, 2.0F, 0.4098F, 0.0F, 0.0F));

		PartDefinition antlers_right1f = antler_right1e.addOrReplaceChild("antlers_right1f", CubeListBuilder.create().texOffs(33, 25).mirror().addBox(0.0F, -2.01F, 0.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.0F, 0.2276F, 0.0F));

		PartDefinition antlers_right1g = antlers_right1f.addOrReplaceChild("antlers_right1g", CubeListBuilder.create().texOffs(33, 30).mirror().addBox(-0.01F, -2.0F, 0.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.4098F, 0.0F, 0.0F));

		PartDefinition antlers_right1h = antlers_right1g.addOrReplaceChild("antlers_right1h", CubeListBuilder.create().texOffs(33, 35).mirror().addBox(-0.02F, -2.0F, 0.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.7741F, 0.0F, 0.0F));

		PartDefinition antlers_right1i = antlers_right1h.addOrReplaceChild("antlers_right1i", CubeListBuilder.create().texOffs(33, 40).mirror().addBox(-0.03F, -2.0F, 0.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.7741F, 0.0F, 0.0F));

		PartDefinition foreheadpiece = topplate1.addOrReplaceChild("foreheadpiece", CubeListBuilder.create().texOffs(0, 7).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 2.0F, -3.0F, 0.0911F, 0.0F, 0.0F));

		PartDefinition antler_left1a = topplate1.addOrReplaceChild("antler_left1a", CubeListBuilder.create().texOffs(33, 0).addBox(0.0F, -1.0F, -1.0F, 2.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.5F, -1.5F, -0.5F, 0.1367F, 0.3187F, 0.2731F));

		PartDefinition antlers_left1j = antler_left1a.addOrReplaceChild("antlers_left1j", CubeListBuilder.create().texOffs(33, 45).addBox(-2.0F, -1.0F, -2.0F, 2.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, -1.0F, 1.0F, 0.0F, 0.0F, -0.2731F));

		PartDefinition antler_left1c = antler_left1a.addOrReplaceChild("antler_left1c", CubeListBuilder.create().texOffs(33, 12).addBox(0.01F, 0.0F, 0.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -1.0F, 2.0F, -0.4098F, 0.0F, 0.0F));

		PartDefinition antler_left1d = antler_left1c.addOrReplaceChild("antler_left1d", CubeListBuilder.create().texOffs(33, 16).addBox(-2.0F, -0.01F, 0.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 0.0F, 1.0F, 0.0F, -0.2276F, 0.0F));

		PartDefinition antler_left1e = antler_left1d.addOrReplaceChild("antler_left1e", CubeListBuilder.create().texOffs(33, 21).addBox(-1.99F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 2.0F, 2.0F, 0.4098F, 0.0F, 0.0F));

		PartDefinition antlers_left1f = antler_left1e.addOrReplaceChild("antlers_left1f", CubeListBuilder.create().texOffs(33, 25).addBox(-2.0F, -2.01F, 0.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.0F, -0.2276F, 0.0F));

		PartDefinition antlers_left1g = antlers_left1f.addOrReplaceChild("antlers_left1g", CubeListBuilder.create().texOffs(33, 30).addBox(-1.99F, -2.0F, 0.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.4098F, 0.0F, 0.0F));

		PartDefinition antlers_left1h = antlers_left1g.addOrReplaceChild("antlers_left1h", CubeListBuilder.create().texOffs(33, 35).addBox(-1.98F, -2.0F, 0.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.7741F, 0.0F, 0.0F));

		PartDefinition antlers_left1i = antlers_left1h.addOrReplaceChild("antlers_left1i", CubeListBuilder.create().texOffs(33, 40).addBox(-1.97F, -2.0F, 0.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.7741F, 0.0F, 0.0F));

		PartDefinition antler_left1b = antler_left1a.addOrReplaceChild("antler_left1b", CubeListBuilder.create().texOffs(33, 6).addBox(-0.01F, 0.0F, -3.0F, 2.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -1.0F, -1.0F, 0.3187F, 0.0F, 0.0F));

		PartDefinition maincover = helmet_mainrotation.addOrReplaceChild("maincover", CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -6.0F, 0.0F));
		
		// Chestplate
		PartDefinition chestplate_mainrotation = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);

		PartDefinition chestpiece_left1 = chestplate_mainrotation.addOrReplaceChild("chestpiece_left1", CubeListBuilder.create().texOffs(75, 0).addBox(0.0F, 0.0F, 0.0F, 6.0F, 3.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, -3.5F, 0.0F, -0.1367F, 0.0F));

		PartDefinition chestpiece_left2 = chestpiece_left1.addOrReplaceChild("chestpiece_left2", CubeListBuilder.create().texOffs(75, 10).addBox(0.0F, -1.0F, 0.0F, 4.0F, 1.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(2.0F, 0.0F, 0.0F));

		PartDefinition chestpiece_right1 = chestplate_mainrotation.addOrReplaceChild("chestpiece_right1", CubeListBuilder.create().texOffs(100, 0).addBox(-6.0F, 0.0F, 0.0F, 6.0F, 3.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, -3.5F, 0.0F, 0.1367F, 0.0F));

		PartDefinition chestpiece_right2 = chestpiece_right1.addOrReplaceChild("chestpiece_right2", CubeListBuilder.create().texOffs(100, 10).addBox(-4.0F, -1.0F, 0.0F, 4.0F, 1.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(-2.0F, 0.0F, 0.0F));

		PartDefinition lowerplate_main = chestplate_mainrotation.addOrReplaceChild("lowerplate_main", CubeListBuilder.create().texOffs(75, 18).addBox(-4.5F, 0.0F, -2.5F, 9.0F, 1.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition lowerplate_left = lowerplate_main.addOrReplaceChild("lowerplate_left", CubeListBuilder.create().texOffs(75, 25).addBox(0.0F, 0.0F, -2.5F, 3.0F, 1.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.5F, 1.0F, 0.0F, 0.0F, 0.0F, -0.0911F));

		PartDefinition chainmail = lowerplate_main.addOrReplaceChild("chainmail", CubeListBuilder.create().texOffs(75, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.9F, 0.0F));

		PartDefinition lowerplate_right = lowerplate_main.addOrReplaceChild("lowerplate_right", CubeListBuilder.create().texOffs(92, 25).addBox(-3.0F, 0.0F, -2.5F, 3.0F, 1.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.5F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0911F));

		// Belt
		PartDefinition leggings_mainrotation = partdefinition.addOrReplaceChild("belt", CubeListBuilder.create(), PartPose.offset(0.0F, 10.0F, 0.0F));

		PartDefinition belt_main = leggings_mainrotation.addOrReplaceChild("belt_main", CubeListBuilder.create().texOffs(45, 0).addBox(-4.5F, 0.0F, -2.5F, 9.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0456F));

		PartDefinition belt_add1 = belt_main.addOrReplaceChild("belt_add1", CubeListBuilder.create().texOffs(45, 8).addBox(0.0F, 0.0F, -2.5F, 2.0F, 1.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(-2.5F, 2.0F, 0.0F));

		PartDefinition belt_add2 = belt_add1.addOrReplaceChild("belt_add2", CubeListBuilder.create().texOffs(45, 15).addBox(-2.0F, -1.0F, -2.5F, 2.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0456F));

		PartDefinition bone1a = belt_main.addOrReplaceChild("bone1a", CubeListBuilder.create().texOffs(60, 8).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, 0.0F, -2.5F, 0.0F, 0.0F, 0.0456F));

		PartDefinition bone1b = bone1a.addOrReplaceChild("bone1b", CubeListBuilder.create().texOffs(60, 11).addBox(0.0F, 0.0F, -0.99F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.5F, 1.0F, 0.0F, 0.0F, 0.0F, -0.1367F));

		PartDefinition bone2a = belt_main.addOrReplaceChild("bone2a", CubeListBuilder.create().texOffs(60, 14).addBox(-2.0F, 0.0F, -0.5F, 2.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.0F, -0.05F, -2.5F, 0.0F, 0.1367F, 0.0456F));

		PartDefinition bone2b = bone2a.addOrReplaceChild("bone2b", CubeListBuilder.create().texOffs(60, 20).addBox(0.0F, 0.0F, -0.5F, 1.0F, 1.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(-2.0F, 2.0F, 0.0F));

		PartDefinition bone3a = belt_main.addOrReplaceChild("bone3a", CubeListBuilder.create().texOffs(45, 23).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 2.5F, 0.0F, 0.0F, 0.0456F));

		PartDefinition bone3b = bone3a.addOrReplaceChild("bone3b", CubeListBuilder.create().texOffs(45, 27).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.182F, 0.0F, 0.0F));

		// Right arm
		PartDefinition chestplate_armrotation_right = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition shoulderpad_right = chestplate_armrotation_right.addOrReplaceChild("shoulderpad_right", CubeListBuilder.create().texOffs(100, 41).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0456F));

		// Left arm
		PartDefinition chestplate_armrotation_left = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition shoulderpad_left = chestplate_armrotation_left.addOrReplaceChild("shoulderpad_left", CubeListBuilder.create().texOffs(100, 32).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0456F));

		// Right boot
		PartDefinition boots_right_main = partdefinition.addOrReplaceChild("right_boot", CubeListBuilder.create().texOffs(92, 52).addBox(-2.0F, 4.5F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-1.9F, 12.0F, 0.0F));

		PartDefinition boots_right_toes = boots_right_main.addOrReplaceChild("boots_right_toes", CubeListBuilder.create().texOffs(109, 52).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 9.5F, -2.0F, 0.1367F, 0.0F, 0.0F));

		PartDefinition boots_right_bone = boots_right_toes.addOrReplaceChild("boots_right_bone", CubeListBuilder.create().texOffs(109, 56).addBox(-3.0F, 0.0F, -1.0F, 3.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 0.01F, -1.0F, 0.0F, 0.1367F, 0.0F));

		// Left boot
		PartDefinition boots_left_main = partdefinition.addOrReplaceChild("left_boot", CubeListBuilder.create().texOffs(64, 52).addBox(-2.0F, 4.5F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(1.9F, 12.0F, 0.0F));

		PartDefinition boots_left_toes = boots_left_main.addOrReplaceChild("boots_left_toes", CubeListBuilder.create().texOffs(81, 52).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 9.5F, -2.0F, 0.1367F, 0.0F, 0.0F));

		PartDefinition boots_left_bone = boots_left_toes.addOrReplaceChild("boots_left_bone", CubeListBuilder.create().texOffs(81, 56).addBox(0.0F, 0.0F, -1.0F, 3.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.0F, 0.01F, -1.0F, 0.0F, -0.1367F, 0.0F));

		// Right leg
		PartDefinition chainmail_leg_right = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(45, 42).addBox(-2.0F, 0.0F, -1.985F, 4.0F, 8.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-1.9F, 2.0F, 0.0F));

		// Left leg
		PartDefinition chainmail_leg_left = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(45, 30).addBox(-3.0F, -0.3F, -2.0F, 5.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(1.9F, 2.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}
	
	@Override
	protected Iterable<ModelPart> bodyParts() {
		return Iterables.concat(super.bodyParts(), ImmutableList.of(this.belt, this.leftBoot, this.rightBoot));
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		switch (this.slot) {
			case HEAD -> {
				this.head.visible = true;
				this.hat.visible = false;
				this.body.visible = false;
				this.rightArm.visible = false;
				this.leftArm.visible = false;
				this.belt.visible = false;
				this.rightLeg.visible = false;
				this.leftLeg.visible = false;
				this.leftBoot.visible = false;
				this.rightBoot.visible = false;
			}
			case CHEST -> {
				this.head.visible = false;
				this.hat.visible = false;
				this.body.visible = true;
				this.rightArm.visible = true;
				this.leftArm.visible = true;
				this.belt.visible = false;
				this.rightLeg.visible = false;
				this.leftLeg.visible = false;
				this.leftBoot.visible = false;
				this.rightBoot.visible = false;
			}
			case LEGS -> {
				this.head.visible = false;
				this.hat.visible = false;
				this.body.visible = false;
				this.rightArm.visible = false;
				this.leftArm.visible = false;
				this.belt.visible = true;
				this.rightLeg.visible = true;
				this.leftLeg.visible = true;
				this.leftBoot.visible = false;
				this.rightBoot.visible = false;
			}
			case FEET -> {
				this.head.visible = false;
				this.hat.visible = false;
				this.body.visible = false;
				this.rightArm.visible = false;
				this.leftArm.visible = false;
				this.belt.visible = false;
				this.rightLeg.visible = false;
				this.leftLeg.visible = false;
				this.leftBoot.visible = true;
				this.rightBoot.visible = true;
			}
			default -> { }
		}
		this.rightBoot.copyFrom(this.rightLeg);
		this.leftBoot.copyFrom(this.leftLeg);
		super.renderToBuffer(stack, consumer, light, overlay, color);
	}
}
