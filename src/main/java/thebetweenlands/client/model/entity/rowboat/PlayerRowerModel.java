package thebetweenlands.client.model.entity.rowboat;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.PlayerModelPart;
import thebetweenlands.client.model.definition.ExtendedMeshDefinition;
import thebetweenlands.client.model.definition.ExtendedModelPart;
import thebetweenlands.client.model.definition.ExtendedPartDefinition;
import thebetweenlands.util.BipedTextureUVs;

public class PlayerRowerModel extends HumanoidRowerModel<AbstractClientPlayer> {

	public final ModelPart jacket;
	public final ExtendedModelPart rightSleeve;
	public final ExtendedModelPart leftSleeve;
	public final ModelPart rightPants;
	public final ModelPart leftPants;

	public PlayerRowerModel(ModelPart root) {
		super(root);
		this.jacket = this.body.getChild("jacket");
		this.rightSleeve = (ExtendedModelPart) this.rightArm.getChild("right_sleeve");
		this.leftSleeve = (ExtendedModelPart) this.leftArm.getChild("left_sleeve");
		this.rightPants = this.rightLeg.getChild("right_pants");
		this.leftPants = this.leftLeg.getChild("left_pants");
	}

	public static LayerDefinition create(CubeDeformation expand, boolean slimArms, BipedTextureUVs uvs) {
		ExtendedMeshDefinition definition = createBaseMesh(expand, slimArms, uvs);
		ExtendedPartDefinition partDefinition = definition.getRoot();

		partDefinition.getChild("body").addOrReplaceChild("jacket", CubeListBuilder.create()
				.texOffs(uvs.jacket().u(), uvs.jacket().v()).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, expand.extend(0.25F)),
			PartPose.offset(0, -12, 0));

		partDefinition.getChild("body").getChild("left_arm").addOrReplaceChild("left_sleeve", addMoreCtxBox(CubeListBuilder.create()
				.texOffs(uvs.leftSleeve().u(), uvs.leftSleeve().v()), -1, -2, -2, slimArms ? 3 : 4, 12, 4, expand.extend(0.25F)),
			PartPose.ZERO, ExtendedPartDefinition.Type.ARM);

		partDefinition.getChild("body").getChild("right_arm").addOrReplaceChild("right_sleeve", addMoreCtxBox(CubeListBuilder.create()
				.texOffs(uvs.rightSleeve().u(), uvs.rightSleeve().v()), slimArms ? -2 : -3, -1.75F, -2, slimArms ? 3 : 4, 12, 4, expand.extend(0.25F)),
			PartPose.ZERO, ExtendedPartDefinition.Type.ARM);

		partDefinition.getChild("left_leg").addOrReplaceChild("left_pants", CubeListBuilder.create()
				.texOffs(uvs.leftPant().u(), uvs.leftPant().v()).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand.extend(0.25F)),
			PartPose.ZERO);

		partDefinition.getChild("right_leg").addOrReplaceChild("right_pants", CubeListBuilder.create()
				.texOffs(uvs.rightPant().u(), uvs.rightPant().v()).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand.extend(0.25F)),
			PartPose.ZERO);

		return LayerDefinition.create(definition, uvs.textureWidth(), uvs.textureHeight());
	}

	public void setModelProperties(AbstractClientPlayer player) {
		if (player.isSpectator()) {
			this.setAllVisible(false);
			this.head.visible = true;
			this.hat.visible = true;
		} else {
			this.setAllVisible(true);
			this.hat.visible = player.isModelPartShown(PlayerModelPart.HAT);
			this.jacket.visible = player.isModelPartShown(PlayerModelPart.JACKET);
			this.leftPants.visible = player.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
			this.rightPants.visible = player.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
			this.leftSleeve.visible = player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
			this.rightSleeve.visible = player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
		}
	}

	@Override
	public void setAllVisible(boolean visible) {
		super.setAllVisible(visible);
		this.leftSleeve.visible = visible;
		this.rightSleeve.visible = visible;
		this.leftPants.visible = visible;
		this.rightPants.visible = visible;
		this.jacket.visible = visible;
	}

	@Override
	public void animate(ArmArticulation leftArm, ArmArticulation rightArm, float bodyRotateAngleX, float bodyRotateAngleY) {
		super.animate(leftArm, rightArm, bodyRotateAngleX, bodyRotateAngleY);
		this.leftSleeve.setFlexionAngle(leftArm.flexionAngle);
		this.rightSleeve.setFlexionAngle(rightArm.flexionAngle);
	}
}
