package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.creature.Greebling;

import javax.annotation.Nullable;

public class GreeblingModel extends MowzieModelBase<Greebling> {

	private final ModelPart actualRoot;
	private final ModelPart root;
	private final ModelPart bodyBase;
	private final ModelPart chest;
	private final ModelPart legleft1;
	private final ModelPart legright1;
	private final ModelPart head1;
	private final ModelPart armright1;
	private final ModelPart instrument;

	public GreeblingModel(ModelPart root) {
		this.actualRoot = root;
		this.root = root.getChild("root");
		this.bodyBase = this.root.getChild("body_base");
		this.chest = this.bodyBase.getChild("chest");
		this.legleft1 = this.bodyBase.getChild("left_leg_1");
		this.legright1 = this.bodyBase.getChild("right_leg_1");
		this.head1 = this.chest.getChild("head_1");
		this.armright1 = this.chest.getChild("right_arm_1");
		this.instrument = this.armright1.getChild("right_arm_2").getChild("instrument");
	}

	public static LayerDefinition createVariant1() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, -24.0F, 5.5F, 0, 0, 0),
			PartPose.offset(0.0F, 24.0F, -5.5F));
		var bodyBase = root.addOrReplaceChild("body_base", CubeListBuilder.create()
				.texOffs(0, 9).addBox(-2.0F, -3.182596199392689F, -2.0625201609095485F, 4, 4, 3),
			PartPose.offsetAndRotation(0.0F, -1.1384599901460248F, 0.5872213733837306F, -0.22776546738526F, 0.0F, 0.0F));
		var chest = bodyBase.addOrReplaceChild("chest", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.5F, -3.3237656918367984F, -2.4506766441575647F, 5, 4, 4),
			PartPose.offsetAndRotation(0.0F, -2.965801266869104F, -0.27582661303015055F, 0.091106186954104F, 0.0F, 0.0F));

		var head1 = chest.addOrReplaceChild("head_1", CubeListBuilder.create()
				.texOffs(0, 17).addBox(-2.0F, -3.0F, -3.0F, 4, 3, 4),
			PartPose.offsetAndRotation(0.0F, -3.8311339463882743F, -0.9674967227287707F, 0.045553093477052F, 0.0F, 0.0F));
		head1.addOrReplaceChild("nose", CubeListBuilder.create()
				.texOffs(11, 25).addBox(-0.5F, -2.0F, -1.0F, 1, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.22759093446006054F, 0.0F, 0.0F));
		head1.addOrReplaceChild("head_2", CubeListBuilder.create()
				.texOffs(17, 21).addBox(-2.0F, 0.0F, -1.0F, 4, 1, 2),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		head1.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(0, 25).addBox(-1.5F, -0.02276867034659224F, -1.5005186813797255F, 3, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.04549010195178495F, -1.4979263506025928F, 0.045553093477052F, 0.0F, 0.0F));
		head1.addOrReplaceChild("left_ear", CubeListBuilder.create()
				.texOffs(16, 25).addBox(-0.6220433050811432F, -0.6372468752590972F, -0.023630725825473986F, 5, 3, 0),
			PartPose.offsetAndRotation(1.0F, -2.5F, -0.59F, -0.091106186954104F, -0.5918411493512771F, -0.5009094953223726F));
		head1.addOrReplaceChild("right_ear", CubeListBuilder.create()
				.texOffs(16, 29).addBox(-4.377956694918857F, -0.6372468752590972F, -0.023630725825473986F, 5, 3, 0),
			PartPose.offsetAndRotation(-1.0F, -2.5F, -0.59F, -0.091106186954104F, 0.5918411493512771F, 0.5009094953223726F));

		var armleft1 = chest.addOrReplaceChild("left_arm_1", CubeListBuilder.create()
				.texOffs(25, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(2.5F, -2.122938627842288F, -0.784733536128615F, -0.3881606553461388F, -0.04707782911457041F, -1.413927420973236F));
		var armleft2 = armleft1.addOrReplaceChild("left_arm_2", CubeListBuilder.create()
				.texOffs(30, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, new CubeDeformation(0.01F)),
			PartPose.offsetAndRotation(0.0F, 3.5F, 0.0F, -1.5025539530419183F, 0.0F, 0.0F));

		var armright1 = chest.addOrReplaceChild("right_arm_1", CubeListBuilder.create()
				.texOffs(35, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(-2.5F, -2.122938627842288F, -0.784733536128615F, -0.7451176564534014F, -0.06546967612617839F, 0.4969738117393972F));
		var armright2 = armright1.addOrReplaceChild("right_arm_2", CubeListBuilder.create()
				.texOffs(40, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, new CubeDeformation(0.01F)),
			PartPose.offsetAndRotation(0.0F, 3.5F, 0.0F, -0.6829473363053812F, 0.0F, -0.7285004297824331F));

		var legleft1 = bodyBase.addOrReplaceChild("left_leg_1", CubeListBuilder.create()
				.texOffs(45, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(1.3F, 0.867466694522502F, -0.9149770530750317F, -1.3087095159565685F, -0.3101876267227021F, -0.07436440490714699F));
		legleft1.addOrReplaceChild("left_leg_2", CubeListBuilder.create()
				.texOffs(50, 0).addBox(-0.5F, -0.5F, -1.0F, 1, 4, 1),
			PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 1.5707963267948966F, 0.0F, 0.0F));

		var legright1 = bodyBase.addOrReplaceChild("right_leg_1", CubeListBuilder.create()
				.texOffs(45, 5).addBox(-0.5F, -0.5F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(-1.3F, 0.867466694522502F, -0.9149770530750317F, -1.3087095159565685F, 0.3101876267227021F, 0.07436440490714699F));
		legright1.addOrReplaceChild("right_leg_2", CubeListBuilder.create()
				.texOffs(50, 5).addBox(-0.5F, -0.5F, -1.0F, 1, 4, 1),
			PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 1.4994643202709739F, 0.0F, 0.0F));

		var lutebase = armleft2.addOrReplaceChild("lute_base", CubeListBuilder.create()
				.texOffs(30, 8).addBox(-1.9902175722236533F, 0.2917097165214597F, -0.8443796388811212F, 4, 5, 3),
			PartPose.offsetAndRotation(-0.7622866342321437F, 3.1200327646462362F, -1.21631341944674F, -0.9139542082504476F, -1.3997957633427642F, 2.535364913532174F));
		var luteneck = lutebase.addOrReplaceChild("lute_neck", CubeListBuilder.create()
				.texOffs(30, 17).addBox(-1.0F, -7.0F, 0.0F, 2, 7, 1),
			PartPose.offsetAndRotation(0.017376167048784863F, 0.29556824894909506F, -0.330517345185515F, -0.091106186954104F, 0.0F, 0.0F));
		luteneck.addOrReplaceChild("lute_end", CubeListBuilder.create()
				.texOffs(37, 17).addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2),
			PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, -0.27314402793711257F, 0.0F, 0.0F));

		armright2.addOrReplaceChild("instrument", CubeListBuilder.create()
				.texOffs(37, 20).addBox(0.0F, -1.5F, -1.0F, 0, 7, 1),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.091106186954104F, 0.0F, -1.0471975511965976F));

		return LayerDefinition.create(definition, 64, 32);
	}

	public static LayerDefinition createVariant2() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var cup = partDefinition.addOrReplaceChild("cup", CubeListBuilder.create()
				.texOffs(25, 15).addBox(-1.0F, -3.0F, -1.0F, 2, 3, 2),
			PartPose.offsetAndRotation(5.5F, 24.0F, -2.0F, 0.0F, -0.5009094953223726F, 0.0F));
		cup.addOrReplaceChild("handle", CubeListBuilder.create()
				.texOffs(34, 15).addBox(0.0F, -3.0F, 0.0F, 0, 3, 2),
			PartPose.offset(0.0F, 0.0F, 1.0F));

		var root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, -24.0F, 0.0F, 0, 0, 0),
			PartPose.offset(0.0F, 24.0F, 0.0F));
		var bodyBase = root.addOrReplaceChild("body_base", CubeListBuilder.create()
				.texOffs(0, 9).addBox(-2.0F, -3.5F, -0.76F, 4, 4, 3),
			PartPose.offsetAndRotation(0.0F, -1.1F, -1.5F, -0.27314402793711257F, 0.0F, 0.0F));
		var chest = bodyBase.addOrReplaceChild("chest", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.5F, -3.0F, -2.0F, 5, 4, 4),
			PartPose.offsetAndRotation(0.0F, -3.7748866854558005F, 0.5002304588572539F, 0.27314402793711257F, 0.0F, 0.0F));

		var head1 = chest.addOrReplaceChild("head_1", CubeListBuilder.create()
				.texOffs(0, 17).addBox(-2.0F, -3.0F, -3.0F, 4, 3, 4),
			PartPose.offsetAndRotation(0.0F, -3.8F, -1.0F, 0.18203784098300857F, 0.0F, 0.0F));
		var jaw = head1.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(0, 25).addBox(-1.5F, 0.0F, -2.0F, 3, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.22759093446006054F, 0.0F, 0.0F));
		head1.addOrReplaceChild("head_2", CubeListBuilder.create()
				.texOffs(17, 21).addBox(-2.0F, 0.0F, -1.0F, 4, 1, 2),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		head1.addOrReplaceChild("nose", CubeListBuilder.create()
				.texOffs(11, 25).addBox(-0.5F, -2.0F, -1.0F, 1, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.22759093446006054F, 0.0F, 0.0F));
		head1.addOrReplaceChild("left_ear", CubeListBuilder.create()
				.texOffs(16, 25).addBox(-0.7620433050811432F, -0.6272468752590973F, -0.2536307258254739F, 5, 3, 0),
			PartPose.offsetAndRotation(1.0F, -2.48F, -0.32F, -0.091106186954104F, -0.5918411493512771F, -0.5009094953223726F));
		head1.addOrReplaceChild("right_ear", CubeListBuilder.create()
				.texOffs(16, 29).addBox(-4.237956694918856F, -0.6272468752590973F, -0.2536307258254739F, 5, 3, 0),
			PartPose.offsetAndRotation(-1.0F, -2.48F, -0.32F, -0.091106186954104F, 0.5918411493512771F, 0.5009094953223726F));
		jaw.addOrReplaceChild("beard", CubeListBuilder.create()
				.texOffs(0, 29).addBox(-1.5F, 0.0F, 0.0F, 3, 3, 0),
			PartPose.offsetAndRotation(0.0F, 1.0F, -2.0F, -0.36425021489121656F, 0.0F, 0.0F));

		var armleft1 = chest.addOrReplaceChild("left_arm_1", CubeListBuilder.create()
				.texOffs(25, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(2.5F, -1.6999999999999957F, -0.5000000000000008F, -0.5918411493512772F, 0.09110618695410401F, -0.36425021489121645F));
		armleft1.addOrReplaceChild("left_arm_2", CubeListBuilder.create()
				.texOffs(30, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, new CubeDeformation(0.01F)),
			PartPose.offsetAndRotation(0.0F, 3.5F, 0.0F, -1.9123572614101867F, 0.0F, 0.0F));

		var armright1 = chest.addOrReplaceChild("right_arm_1", CubeListBuilder.create()
				.texOffs(35, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(-2.5F, -1.6999999999999957F, -0.5000000000000008F, -0.5918411493512772F, -0.09110618695410401F, 0.36425021489121645F));
		var armright2 = armright1.addOrReplaceChild("right_arm_2", CubeListBuilder.create()
				.texOffs(40, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, new CubeDeformation(0.01F)),
			PartPose.offsetAndRotation(0.0F, 3.5F, 0.0F, -1.9123572614101867F, 0.0F, 0.0F));

		var legleft1 = bodyBase.addOrReplaceChild("left_leg_1", CubeListBuilder.create()
				.texOffs(45, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(1.3F, 0.5777565139606047F, 0.16185614160758632F, -1.4175939675619955F, -0.8297069961766927F, 0.24760389207524505F));
		legleft1.addOrReplaceChild("left_leg_2", CubeListBuilder.create()
				.texOffs(50, 0).addBox(-0.5F, -0.5F, -1.0F, 1, 4, 1),
			PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 1.5707963267948966F, -1.593485607070823F, 0.7740535232594852F));

		var legright1 = bodyBase.addOrReplaceChild("right_leg_1", CubeListBuilder.create()
				.texOffs(45, 5).addBox(-0.5F, -0.5F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(-1.3F, 0.5777565139606047F, 0.16185614160758632F, -1.2343262671693576F, 0.6958921313115842F, 0.2361931785788314F));
		legright1.addOrReplaceChild("right_leg_2", CubeListBuilder.create()
				.texOffs(50, 5).addBox(-0.5F, -0.5F, -1.0F, 1, 4, 1),
			PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 2.41309222380736F, 1.593485607070823F, 0.0F));

		var panflute1 = armright2.addOrReplaceChild("instrument", CubeListBuilder.create()
				.texOffs(25, 10).addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1),
			PartPose.offsetAndRotation(0.3091571662794066F, 4.451192104343287F, -0.5676794810700461F, 2.3818404332756264F, 0.07033489531877639F, 0.40103946021775155F));
		var panflute2 = panflute1.addOrReplaceChild("pan_flute_2", CubeListBuilder.create()
				.texOffs(30, 10).addBox(0.0F, 0.0F, -1.0F, 1, 3, 1),
			PartPose.offsetAndRotation(0.5F, 0.0F, 0.5F, 0.0F, -0.045553093477052F, 0.0F));
		var panflute3 = panflute2.addOrReplaceChild("pan_flute_3", CubeListBuilder.create()
				.texOffs(35, 10).addBox(0.0F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, -0.045553093477052F, 0.0F));
		panflute3.addOrReplaceChild("pan_flute_4", CubeListBuilder.create()
				.texOffs(40, 10).addBox(0.0F, 0.0F, -1.0F, 1, 5, 1),
			PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, -0.045553093477052F, 0.0F));

		return LayerDefinition.create(definition, 64, 32);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Nullable
	public ModelPart getCup() {
		if (this.actualRoot.hasChild("cup")) {
			return this.actualRoot.getChild("cup");
		}
		return null;
	}

	@Override
	public void setupAnim(Greebling entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void prepareMobModel(Greebling entity, float limbSwing, float limbSwingAmount, float partialTick) {
		this.setInitPose();
		float frame = entity.tickCount + partialTick;
		float swaySpeed = 0.06F;
		float strokeSpeed = swaySpeed * 0.33F;

		this.flap(this.bodyBase, swaySpeed, 0.15F, false, 0.0F, 0.0F, frame, 1.0F);
		this.flap(this.legleft1, swaySpeed, 0.15F, true, 0.0F, 0.0F, frame, 1.0F);
		this.flap(this.legright1, swaySpeed, 0.15F, true, 0.0F, 0.0F, frame, 1.0F);
		this.chest.y += Mth.sin((frame - 3.0F) * swaySpeed * 2.0F) * 0.25F;
		this.flap(this.head1, swaySpeed * 4.0F, 0.075F, false, 0, 0, frame, 1.0F);

		if (entity.getGreeblingType() == 0) {
			this.swing(this.armright1, strokeSpeed * 4.0F, 0.3F, false, 0.0F, 0.0F, frame, 1.0F);
			this.walk(this.instrument, strokeSpeed * 4.0F, 0.2F, false, 0.0F, 0.0F, frame, 1.0F);
			this.flap(this.instrument, strokeSpeed * 4.0F, 0.4F, true, 0.0F, 0.0F, frame, 1.0F);
		}

		float disappearFrame = entity.disappearTimer > 0 ? entity.disappearTimer + partialTick : 0.0F;

		this.bodyBase.y -= (float) (16.0F * Math.pow(disappearFrame / 8.0F, 1.5F));
		this.bodyBase.yRot += (float) Math.pow(5.0F * disappearFrame / 8.0F, 1.4F);
	}
}
