package thebetweenlands.client.model.entity.rowboat;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import thebetweenlands.client.model.definition.ExtendedCubeDefinition;
import thebetweenlands.client.model.definition.ExtendedMeshDefinition;
import thebetweenlands.client.model.definition.ExtendedModelPart;
import thebetweenlands.client.model.definition.ExtendedPartDefinition;
import thebetweenlands.util.BipedTextureUVs;
import thebetweenlands.util.Mesh;

import java.util.EnumSet;

public class HumanoidRowerModel<T extends LivingEntity> extends HumanoidModel<T> {

	public final ModelPart head;
	public final ModelPart hat;
	public final ExtendedModelPart rightArm;
	public final ExtendedModelPart leftArm;

	public HumanoidRowerModel(ModelPart root) {
		super(root);
		this.head = this.body.getChild("head");
		this.hat = this.head.getChild("hat");
		this.rightArm = (ExtendedModelPart) this.body.getChild("right_arm");
		this.leftArm = (ExtendedModelPart) this.body.getChild("left_arm");
	}

	protected static ExtendedMeshDefinition createBaseMesh(CubeDeformation expand, boolean slimArms, BipedTextureUVs uvs) {
		ExtendedMeshDefinition definition = new ExtendedMeshDefinition();
		ExtendedPartDefinition partDefinition = definition.getRoot();

		var body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
			.texOffs(uvs.body().u(), uvs.body().v()).addBox(-4, -12, -2, 8, 12, 4, expand),
			PartPose.offset(0, 12, 0));

		var head = body.addOrReplaceChild("head", CubeListBuilder.create()
			.addBox(-4, -8, -4, 8, 8, 8, expand.extend(0.025F)),
			PartPose.offset(0, -12, 0));
		head.addOrReplaceChild("hat", CubeListBuilder.create()
				.texOffs(32, 0).addBox(-4, -8, -4, 8, 8, 8, expand.extend(0.5F)),
			PartPose.ZERO);

		body.addOrReplaceChild("left_arm", addMoreCtxBox(CubeListBuilder.create()
				.texOffs(uvs.leftArm().u(), uvs.leftArm().v()), -1, -2, -2, slimArms ? 3 : 4, 12, 4, expand),
			PartPose.offset(5.0F, -10.0F, 0.0F), ExtendedPartDefinition.Type.ARM);

		body.addOrReplaceChild("right_arm", addMoreCtxBox(CubeListBuilder.create()
				.texOffs(uvs.rightArm().u(), uvs.rightArm().v()), slimArms ? -2 : -3, -2, -2, slimArms ? 3 : 4, 12, 4, expand),
			PartPose.offset(-5.0F, -10.0F, 0.0F), ExtendedPartDefinition.Type.ARM);

		partDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
				.texOffs(uvs.leftLeg().u(), uvs.leftLeg().v()).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand),
			PartPose.offsetAndRotation(1.9F, 12.0F, 0.0F, -1.25F, -0.314F, 0.0F));

		partDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
				.texOffs(uvs.rightLeg().u(), uvs.rightLeg().v()).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand),
			PartPose.offsetAndRotation(-1.9F, 12.0F, 0.0F, -1.25F, 0.314F, 0.0F));

		partDefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
		partDefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
		partDefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
		partDefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);

		return definition;
	}

	public static LayerDefinition create(CubeDeformation expand, boolean slimArms, BipedTextureUVs uvs) {
		return LayerDefinition.create(createBaseMesh(expand, slimArms, uvs), uvs.textureWidth(), uvs.textureHeight());
	}

	protected static CubeListBuilder addMoreCtxBox(CubeListBuilder builder, float originX, float originY, float originZ, float dimensionX, float dimensionY, float dimensionZ, CubeDeformation deformation) {
		builder.cubes.add(new ExtendedCubeDefinition(null, builder.xTexOffs, builder.yTexOffs, originX, originY, originZ, dimensionX, dimensionY, dimensionZ, deformation, builder.mirror, 1.0F, 1.0F, EnumSet.allOf(Direction.class)));
		return builder;
	}

	public void animate(ArmArticulation leftArm, ArmArticulation rightArm, float bodyRotateAngleX, float bodyRotateAngleY) {
		this.leftArm.xRot = leftArm.shoulderAngleX;
		this.leftArm.yRot = leftArm.shoulderAngleY;
		this.leftArm.setFlexionAngle(leftArm.flexionAngle);
		this.rightArm.xRot = rightArm.shoulderAngleX;
		this.rightArm.yRot = rightArm.shoulderAngleY;
		this.rightArm.setFlexionAngle(rightArm.flexionAngle);
		this.body.xRot = bodyRotateAngleX;
		this.body.yRot = bodyRotateAngleY;
		this.head.xRot = -bodyRotateAngleX * 0.75F;
		this.head.yRot = -bodyRotateAngleY * 0.75F;
		this.leftArm.z = leftArm.shoulderZ * 16;
		this.rightArm.z = rightArm.shoulderZ * 16;
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		//NO-OP
	}

	@Override
	public void setAllVisible(boolean visible) {
		this.head.visible = visible;
		this.hat.visible = visible;
		this.body.visible = visible;
		this.rightArm.visible = visible;
		this.leftArm.visible = visible;
		this.rightLeg.visible = visible;
		this.leftLeg.visible = visible;
	}

	@Override
	public void copyPropertiesTo(HumanoidModel<T> model) {
		model.attackTime = this.attackTime;
		model.riding = this.riding;
		model.young = this.young;
		model.head.copyFrom(this.head);
		model.hat.copyFrom(this.hat);
		model.body.copyFrom(this.body);
		model.rightArm.copyFrom(this.rightArm);
		model.leftArm.copyFrom(this.leftArm);
		model.rightLeg.copyFrom(this.rightLeg);
		model.leftLeg.copyFrom(this.leftLeg);
	}

	@Override
	protected Iterable<ModelPart> headParts() {
		return ImmutableList.of();
	}

	@Override
	protected Iterable<ModelPart> bodyParts() {
		return ImmutableList.of(this.body, this.leftLeg, this.rightLeg);
	}

	@Override
	public ModelPart getHead() {
		return this.head;
	}

	@Override
	protected ModelPart getArm(HumanoidArm side) {
		return side == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
	}
}
