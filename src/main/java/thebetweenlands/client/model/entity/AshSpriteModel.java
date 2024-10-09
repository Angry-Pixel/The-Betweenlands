package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.AshSprite;

public class AshSpriteModel extends MowzieModelBase<AshSprite> {

	private final ModelPart root;
	private final ModelPart cloth_left_front1;
	private final ModelPart cloth_left_front2;
	private final ModelPart cloth_right_front1;
	private final ModelPart cloth_right_front2;
	private final ModelPart jaw_lower;
	private final ModelPart cloth_right_back1;
	private final ModelPart cloth_right_back2;
	private final ModelPart cloth_right_back3;
	private final ModelPart cloth_left_back1;
	private final ModelPart cloth_left_back2;
	private final ModelPart cloth_left_back3;

	public AshSpriteModel(ModelPart root) {
		this.root = root;
		this.jaw_lower = root.getChild("head_base").getChild("head_connection").getChild("jaw_lower");
		this.cloth_left_front1 = root.getChild("head_base").getChild("cloth_left_front1");
		this.cloth_left_front2 = this.cloth_left_front1.getChild("cloth_left_front2");
		this.cloth_right_front1 = root.getChild("head_base").getChild("cloth_right_front1");
		this.cloth_right_front2 = this.cloth_right_front1.getChild("cloth_right_front2");
		this.cloth_left_back1 = root.getChild("head_base").getChild("maskplate_mid1").getChild("maskplate_left1").getChild("maskplate_left2").getChild("cloth_left_back1");
		this.cloth_left_back2 = this.cloth_left_back1.getChild("cloth_left_back2");
		this.cloth_left_back3 = this.cloth_left_back2.getChild("cloth_left_back3");
		this.cloth_right_back1 = root.getChild("head_base").getChild("maskplate_mid1").getChild("maskplate_right1").getChild("maskplate_right2").getChild("cloth_right_back1");
		this.cloth_right_back2 = this.cloth_right_back1.getChild("cloth_right_back2");
		this.cloth_right_back3 = this.cloth_right_back2.getChild("cloth_right_back3");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var head_base = partDefinition.addOrReplaceChild("head_base", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.0F, -3.0F, -4.0F, 4, 3, 5),
			PartPose.offsetAndRotation(0.0F, 4.0F, 1.0F, 0.27314402793711257F, 0.0F, 0.0F));

		var maskplate_mid1 = head_base.addOrReplaceChild("maskplate_mid1", CubeListBuilder.create()
				.texOffs(20, 0).addBox(-3.0F, -1.0F, -0.5F, 6, 2, 3),
			PartPose.offsetAndRotation(0.0F, -2.5F, -4.0F, 0.136659280431156F, 0.0F, 0.0F));
		maskplate_mid1.addOrReplaceChild("maskplate_mid2", CubeListBuilder.create()
				.texOffs(20, 6).addBox(-2.0F, -1.0F, -1.0F, 4, 2, 1),
			PartPose.offset(0.0F, 0.0F, -0.5F));
		var maskplate_left1 = maskplate_mid1.addOrReplaceChild("maskplate_left1", CubeListBuilder.create()
				.texOffs(33, 10).addBox(0.0F, -1.0F, 0.0F, 3, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.5F, 0.0F, 0.091106186954104F, 0.0F));
		var maskplate_left2 = maskplate_left1.addOrReplaceChild("maskplate_left2", CubeListBuilder.create()
				.texOffs(33, 14).addBox(-1.5F, -2.0F, 0.0F, 3, 2, 3),
			PartPose.offsetAndRotation(1.5F, 1.0F, 1.0F, 0.31869712141416456F, 0.0F, 0.0F));
		maskplate_left2.addOrReplaceChild("maskplate_left3", CubeListBuilder.create()
				.texOffs(33, 20).addBox(0.0F, -2.0F, 0.0F, 3, 2, 2),
			PartPose.offsetAndRotation(-1.5F, 0.0F, 3.0F, 0.0F, 0.136659280431156F, 0.0F));
		var maskplate_right1 = maskplate_mid1.addOrReplaceChild("maskplate_right1", CubeListBuilder.create()
				.texOffs(20, 10).addBox(-3.0F, -1.0F, 0.0F, 3, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.5F, 0.0F, -0.091106186954104F, 0.0F));
		var maskplate_right2 = maskplate_right1.addOrReplaceChild("maskplate_right2", CubeListBuilder.create()
				.texOffs(20, 14).addBox(-1.5F, -2.0F, 0.0F, 3, 2, 3),
			PartPose.offsetAndRotation(-1.5F, 1.0F, 1.0F, 0.31869712141416456F, 0.0F, 0.0F));
		maskplate_right2.addOrReplaceChild("maskplate_right3", CubeListBuilder.create()
				.texOffs(20, 20).addBox(-3.0F, -2.0F, 0.0F, 3, 2, 2),
			PartPose.offsetAndRotation(1.5F, 0.0F, 3.0F, 0.0F, -0.136659280431156F, 0.0F));

		var head_connection = head_base.addOrReplaceChild("head_connection", CubeListBuilder.create()
				.texOffs(0, 9).addBox(-2.0F, 0.0F, -1.0F, 4, 1, 2),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		var jaw_lower = head_connection.addOrReplaceChild("jaw_lower", CubeListBuilder.create()
				.texOffs(0, 13).addBox(-1.5F, 0.0F, -3.0F, 3, 1, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.091106186954104F, 0.0F, 0.0F));

		var tooth_left1 = jaw_lower.addOrReplaceChild("tooth_left1", CubeListBuilder.create()
				.texOffs(5, 18).addBox(-1.0F, -1.0F, 0.0F, 1, 1, 1),
			PartPose.offsetAndRotation(1.5F, 1.0F, -2.5F, -0.18203784098300857F, -0.091106186954104F, 1.1838568316277536F));
		tooth_left1.addOrReplaceChild("tooth_left2", CubeListBuilder.create()
				.texOffs(5, 21).addBox(-1.0F, -2.0F, 0.0F, 1, 2, 1),
			PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.31869712141416456F));

		var tooth_right1 = jaw_lower.addOrReplaceChild("tooth_right1", CubeListBuilder.create()
				.texOffs(0, 18).addBox(0.0F, -1.0F, 0.0F, 1, 1, 1),
			PartPose.offsetAndRotation(-1.5F, 1.0F, -2.5F, -0.18203784098300857F, 0.091106186954104F, -1.1838568316277536F));
		tooth_right1.addOrReplaceChild("tooth_right2", CubeListBuilder.create()
				.texOffs(0, 21).addBox(0.0F, -2.0F, 0.0F, 1, 2, 1),
			PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.31869712141416456F));

		var neck = head_base.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(0, 25).addBox(-1.0F, 0.0F, -2.0F, 2, 3, 2),
			PartPose.offsetAndRotation(0.0F, -2.0F, 1.0F, 0.4553564018453205F, 0.0F, 0.0F));
		var torso1 = neck.addOrReplaceChild("torso1", CubeListBuilder.create()
				.texOffs(0, 31).addBox(-2.5F, 0.0F, -3.0F, 5, 3, 3),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, -0.22759093446006054F, 0.0F, 0.0F));
		var torso2 = torso1.addOrReplaceChild("torso2", CubeListBuilder.create()
				.texOffs(0, 38).addBox(-2.0F, 0.0F, -2.0F, 4, 3, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, -0.6829473363053812F, 0.0F, 0.0F));
		torso2.addOrReplaceChild("torso3", CubeListBuilder.create()
				.texOffs(0, 44).addBox(-2.0F, 0.0F, 0.0F, 4, 4, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, -2.0F, 0.18203784098300857F, 0.0F, 0.0F));

		var cloth_left_front1 = head_base.addOrReplaceChild("cloth_left_front1", CubeListBuilder.create()
				.texOffs(33, 43).addBox(0.0F, 0.0F, 0.0F, 0, 3, 2),
			PartPose.offsetAndRotation(2.5F, -2.0F, -2.0F, 0.0F, 0.0F, -0.27314402793711257F));
		cloth_left_front1.addOrReplaceChild("cloth_left_front2", CubeListBuilder.create()
				.texOffs(33, 49).addBox(0.0F, 0.0F, 0.0F, 0, 3, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.27314402793711257F));

		var cloth_right_front1 = head_base.addOrReplaceChild("cloth_right_front1", CubeListBuilder.create()
				.texOffs(20, 43).addBox(0.0F, 0.0F, 0.0F, 0, 3, 2),
			PartPose.offsetAndRotation(-2.5F, -2.0F, -2.0F, 0.0F, 0.0F, 0.27314402793711257F));
		cloth_right_front1.addOrReplaceChild("cloth_right_front2", CubeListBuilder.create()
				.texOffs(20, 49).addBox(0.0F, 0.0F, 0.0F, 0, 3, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, -0.27314402793711257F));

		var cloth_left_back1 = maskplate_left2.addOrReplaceChild("cloth_left_back1", CubeListBuilder.create()
				.texOffs(33, 25).addBox(0.0F, 0.0F, 0.0F, 0, 3, 2),
			PartPose.offset(1.5F, 0.0F, 0.0F));
		var cloth_left_back2 = cloth_left_back1.addOrReplaceChild("cloth_left_back2", CubeListBuilder.create()
				.texOffs(33, 31).addBox(0.0F, 0.0F, 0.0F, 0, 3, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, -0.22759093446006054F));
		cloth_left_back2.addOrReplaceChild("cloth_left_back3", CubeListBuilder.create()
				.texOffs(33, 37).addBox(0.0F, 0.0F, 0.0F, 0, 3, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, -0.22759093446006054F));

		var cloth_right_back1 = maskplate_right2.addOrReplaceChild("cloth_right_back1", CubeListBuilder.create()
				.texOffs(20, 25).addBox(0.0F, 0.0F, 0.0F, 0, 3, 2),
			PartPose.offset(-1.5F, 0.0F, 0.0F));
		var cloth_right_back2 = cloth_right_back1.addOrReplaceChild("cloth_right_back2", CubeListBuilder.create()
				.texOffs(20, 31).addBox(0.0F, 0.0F, 0.0F, 0, 3, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.22759093446006054F));
		cloth_right_back2.addOrReplaceChild("cloth_right_back3", CubeListBuilder.create()
				.texOffs(20, 37).addBox(0.0F, 0.0F, 0.0F, 0, 3, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.22759093446006054F));

		return LayerDefinition.create(definition, 64, 64);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(AshSprite entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float flap = Mth.sin(ageInTicks * 0.6F) * 0.8F;
		float flapJaw = Mth.sin(ageInTicks * 0.4F) * 0.75F;
		jaw_lower.xRot = convertDegtoRad(26F) - flapJaw * 0.5F;
		cloth_left_back1.zRot = convertDegtoRad(0F) - flap * 0.0625F;
		cloth_left_back2.zRot = convertDegtoRad(-13F) + flap * 0.25F;
		cloth_left_back3.zRot = convertDegtoRad(-13F) - flap * 0.5F;

		cloth_right_back1.zRot = convertDegtoRad(0F) + flap * 0.0625F;
		cloth_right_back2.zRot = convertDegtoRad(13F) - flap * 0.25F;
		cloth_right_back3.zRot = convertDegtoRad(13F) + flap * 0.5F;

		cloth_left_front1.zRot = convertDegtoRad(-16F) - flap * 0.0625F;
		cloth_left_front2.zRot = convertDegtoRad(16F) + flap * 0.25F;

		cloth_right_front1.zRot = convertDegtoRad(16F) + flap * 0.0625F;
		cloth_right_front2.zRot = convertDegtoRad(-16F) - flap * 0.25F;

		if(entity.getDeltaMovement().y() < 0) {
			cloth_left_back1.zRot = (float) (convertDegtoRad(0F) - flap * 0.0625F + entity.getDeltaMovement().y() * 4F);
			cloth_right_back1.zRot = (float) (convertDegtoRad(0F) + flap * 0.0625F - entity.getDeltaMovement().y() * 4F);
			cloth_left_front1.zRot = (float) (convertDegtoRad(-16F) - flap * 0.0625F + entity.getDeltaMovement().y() * 4F);
			cloth_right_front1.zRot = (float) (convertDegtoRad(16F) + flap * 0.0625F + entity.getDeltaMovement().y() * 4F);
		}
	}
}
