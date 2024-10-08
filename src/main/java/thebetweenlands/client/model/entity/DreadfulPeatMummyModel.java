package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.boss.DreadfulPeatMummy;

public class DreadfulPeatMummyModel extends MowzieModelBase<DreadfulPeatMummy> {

	private final ModelPart root;
	private final ModelPart shoulders;
	private final ModelPart neck;
	private final ModelPart bodyBase;
	private final ModelPart armright1;
	private final ModelPart armleft1;
	private final ModelPart armleft2;
	private final ModelPart head1;
	private final ModelPart jaw;
	private final ModelPart tongue1;
	private final ModelPart tongue2;
	private final ModelPart tongue3;
	private final ModelPart tongue4;
	private final ModelPart tongue5;
	private final ModelPart legleft1;
	private final ModelPart legright1;
	private final ModelPart legright2;
	private final ModelPart spiderleg1;
	private final ModelPart spiderleg1a;
	private final ModelPart spiderleg2;
	private final ModelPart spiderleg2a;
	private final ModelPart spiderleg3;
	private final ModelPart spiderleg3a;
	private final ModelPart[] tongue;
	private final ModelPart[] tentacle1;
	private final ModelPart[] tentacle2;


	public DreadfulPeatMummyModel(ModelPart root) {
		this.root = root;
		this.shoulders = root.getChild("shoulders");
		this.armright1 = this.shoulders.getChild("right_shoulder").getChild("right_arm");
		this.armleft1 = this.shoulders.getChild("left_shoulder").getChild("left_arm_1");
		this.armleft2 = this.armleft1.getChild("left_arm_2");
		this.neck = this.shoulders.getChild("neck");
		this.head1 = this.neck.getChild("head_1");
		this.jaw = this.head1.getChild("head_2").getChild("jaw");
		this.tongue1 = this.head1.getChild("tongue_1");
		this.tongue2 = this.tongue1.getChild("tongue_2");
		this.tongue3 = this.tongue2.getChild("tongue_3");
		this.tongue4 = this.tongue3.getChild("tongue_4");
		this.tongue5 = this.tongue4.getChild("tongue_5");
		this.tongue = new ModelPart[]{this.tongue1, this.tongue2, this.tongue3, this.tongue4, this.tongue5};
		this.bodyBase = this.shoulders.getChild("body_base");
		this.legleft1 = this.bodyBase.getChild("butt").getChild("left_leg");
		this.legright1 = this.bodyBase.getChild("butt").getChild("right_leg_1");
		this.legright2 = this.legright1.getChild("right_leg_2");
		this.spiderleg1 = this.shoulders.getChild("spider_leg_1");
		this.spiderleg1a = this.spiderleg1.getChild("spider_leg_1a");
		this.spiderleg2 = this.shoulders.getChild("spider_leg_2");
		this.spiderleg2a = this.spiderleg2.getChild("spider_leg_2a");
		this.spiderleg3 = this.shoulders.getChild("spider_leg_3");
		this.spiderleg3a = this.spiderleg3.getChild("spider_leg_3a");

		var aestheticleg1 = this.shoulders.getChild("aesthetic_leg_1");
		var aestheticleg1a = aestheticleg1.getChild("aesthetic_leg_1a");
		var aestheticleg1b = aestheticleg1a.getChild("aesthetic_leg_1b");

		this.tentacle1 = new ModelPart[]{aestheticleg1, aestheticleg1a, aestheticleg1b};

		var aestheticleg2 = this.shoulders.getChild("aesthetic_leg_2");
		var aestheticleg2a = aestheticleg2.getChild("aesthetic_leg_2a");

		this.tentacle2 = new ModelPart[]{aestheticleg2, aestheticleg2a};
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var shoulders = partDefinition.addOrReplaceChild("shoulders", CubeListBuilder.create(),
			PartPose.offset(0.0F, 0.0F, -3.5F));

		var shoulderleft = shoulders.addOrReplaceChild("left_shoulder", CubeListBuilder.create()
				.texOffs(25, 17).addBox(-0.2F, -5.0F, -3.0F, 5, 6, 7),
			PartPose.offsetAndRotation(0.0F, 2.5F, -0.5F, 1.1848398752279146F, -0.026542406431980966F, 0.00394184147018885F));

		var armleft = shoulderleft.addOrReplaceChild("left_arm_1", CubeListBuilder.create()
				.texOffs(27, 43).addBox(0.0F, -1.0F, -1.5F, 2, 10, 2),
			PartPose.offsetAndRotation(4.190142388244066F, -2.1605578702972807F, 1.6460954039271694F, -1.120634374244034F, 0.021097676907686946F, -0.2712049444367715F));

		armleft.addOrReplaceChild("left_arm_2", CubeListBuilder.create()
				.texOffs(36, 43).addBox(0.0F, 0.0F, -1.51F, 2, 8, 2),
			PartPose.offsetAndRotation(0.0F, 9.0F, 0.0F, -0.36425021489121656F, 0.0F, 0.0F));

		var shoulderright = shoulders.addOrReplaceChild("right_shoulder", CubeListBuilder.create()
				.texOffs(0, 17).addBox(-4.8F, -5.0F, -3.0F, 5, 6, 7),
			PartPose.offsetAndRotation(0.0F, 2.5F, -0.5F, 1.1848398752279146F, 0.026542406431980956F, 0.17827053243801907F));

		shoulderright.addOrReplaceChild("right_arm", CubeListBuilder.create()
				.texOffs(45, 43).addBox(-2.0F, -1.0F, -1.5F, 2, 8, 2),
			PartPose.offsetAndRotation(-4.1825438415682195F, -2.157008565237252F, 1.6478898909504247F, -1.1828318858978228F, 0.13591518556824927F, 0.01249444302571553F));


		var neck = shoulders.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(100, 0).addBox(-1.5F, -3.5F, -2.0F, 3, 4, 3),
			PartPose.offsetAndRotation(0.37784292855481877F, -1.635799711236193F, -3.8335259815816247F, 1.684591794024927F, 0.0F, 0.091106186954104F));

		var head1 = neck.addOrReplaceChild("head_1", CubeListBuilder.create()
				.texOffs(90, 49).addBox(-4.07F, -4.1F, -8.09F, 8, 5, 9),
			PartPose.offsetAndRotation(-0.09F, -2.89F, 0.88F, -1.593485607070823F, -0.18203784098300857F, 0.0F));

		var head2 = head1.addOrReplaceChild("head_2", CubeListBuilder.create()
				.texOffs(100, 8).addBox(-3.5F, 0.0F, -2.0F, 7, 3, 3),
			PartPose.offsetAndRotation(-0.07F, 0.9F, -1.09F, -0.091106186954104F, 0.0F, 0.0F));

		var jaw = head2.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(100, 15).addBox(-3.0F, 0.0F, -6.0F, 6, 2, 7),
			PartPose.offsetAndRotation(0.0F, 2.0F, -2.0F, 1.138303738150702F, 0.0F, 0.0F));

		jaw.addOrReplaceChild("lower_teeth", CubeListBuilder.create()
				.texOffs(100, 25).addBox(-2.5F, -1.0F, -5.0F, 5, 1, 5),
			PartPose.offset(0.0F, 0.0F, -0.7F));

		head1.addOrReplaceChild("upper_teeth", CubeListBuilder.create()
				.texOffs(100, 32).addBox(-3.0F, 0.0F, -5.0F, 6, 1, 5),
			PartPose.offset(-0.06908394214624095F, 0.9027403801674749F, -2.7864102424740955F));

		var tongue1 = head1.addOrReplaceChild("tongue_1", CubeListBuilder.create()
				.texOffs(100, 39).addBox(-1.5F, -1.0F, -2.0F, 3, 1, 3),
			PartPose.offsetAndRotation(-0.06908394214624095F, 3.402740380167474F, -3.086410242474096F, 0.27314402793711257F, 0.091106186954104F, 0.091106186954104F));

		var tongue2 = tongue1.addOrReplaceChild("tongue_2", CubeListBuilder.create()
				.texOffs(113, 39).addBox(-1.51F, 0.0F, -3.0F, 3, 1, 3),
			PartPose.offsetAndRotation(0.0F, -1.0F, -2.0F, 0.5462880558742251F, 0.0F, 0.0F));

		var tongue3 = tongue2.addOrReplaceChild("tongue_3", CubeListBuilder.create()
				.texOffs(100, 44).addBox(-1.5F, 0.0F, -3.0F, 3, 1, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.4553564018453205F, 0.0F, 0.0F));

		var tongue4 = tongue3.addOrReplaceChild("tongue_4", CubeListBuilder.create()
				.texOffs(113, 44).addBox(-1.51F, -1.0F, -2.0F, 3, 1, 2),
			PartPose.offsetAndRotation(0.0F, 1.0F, -3.0F, -0.31869712141416456F, 0.0F, 0.0F));

		tongue4.addOrReplaceChild("tongue_5", CubeListBuilder.create()
				.texOffs(116, 48).addBox(-1.0F, -1.0F, -2.0F, 2, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, -0.36425021489121656F, 0.0F, 0.0F));

		var bodyBase = shoulders.addOrReplaceChild("body_base", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-3.82F, -0.3765086368562779F, -4.4017040068619995F, 8, 10, 6),
			PartPose.offsetAndRotation(0.002791314540518036F, 0.4909944110609308F, 0.5047597093941052F, 1.2747884856566583F, 0.0F, 0.091106186954104F));

		var butt = bodyBase.addOrReplaceChild("butt", CubeListBuilder.create()
				.texOffs(0, 31).addBox(-4.32098020390357F, -0.5830789771757708F, -4.077009625879022F, 9, 5, 6),
			PartPose.offsetAndRotation(-0.0035067239080670137F, 8.998236163378392F, -0.005657502465559849F, -0.5462880558742249F, 0.0F, 0.0F));

		butt.addOrReplaceChild("left_leg", CubeListBuilder.create()
				.texOffs(18, 43).addBox(-1.0F, -1.0F, -1.0F, 2, 6, 2),
			PartPose.offsetAndRotation(3.1819604078071397F, 4.8204065100680396F, -1.072154330214967F, -0.6373942428283291F, -0.091106186954104F, -0.045553093477052F));

		var rightLeg = butt.addOrReplaceChild("right_leg_1", CubeListBuilder.create()
				.texOffs(0, 43).addBox(-1.0F, -1.0F, -1.0F, 2, 9, 2),
			PartPose.offsetAndRotation(-2.8180395921928603F, 4.8204065100680396F, -1.072154330214967F, -1.0016444577195458F, 0.18203784098300857F, 0.0F));

		rightLeg.addOrReplaceChild("right_leg_2", CubeListBuilder.create()
				.texOffs(9, 43).addBox(-0.01F, 0.0F, 0.0F, 2, 10, 2),
			PartPose.offsetAndRotation(-1.0F, 8.0F, -1.0F, 0.36425021489121656F, 0.0F, 0.0F));

		var aestheticleg1 = shoulders.addOrReplaceChild("aesthetic_leg_1", CubeListBuilder.create()
				.texOffs(91, 0).addBox(-1.0F, 0.0F, -1.0F, 2, 6, 2),
			PartPose.offsetAndRotation(-1.0F, -0.5F, 1.5F, 0.5009094953223726F, 1.2747884856566583F, 3.141592653589793F));
		var aestheticleg1a = aestheticleg1.addOrReplaceChild("aesthetic_leg_1a", CubeListBuilder.create()
				.texOffs(91, 9).addBox(-1.01F, 0.0F, 0.0F, 2, 8, 2),
			PartPose.offsetAndRotation(0.0F, 6.0F, -1.0F, 1.0016444577195458F, 0.0F, 0.0F));
		aestheticleg1a.addOrReplaceChild("aesthetic_leg_1b", CubeListBuilder.create()
				.texOffs(91, 20).addBox(-1.0F, 0.0F, 0.0F, 2, 8, 2),
			PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 1.0927506446736497F, 0.0F, 0.0F));

		var aestheticleg2 = shoulders.addOrReplaceChild("aesthetic_leg_2", CubeListBuilder.create()
				.texOffs(91, 31).addBox(-1.0F, 0.0F, -1.0F, 2, 6, 2),
			PartPose.offsetAndRotation(1.0F, -1.0F, 0.0F, 0.36425021489121717F, -1.3203415791337105F, 3.141592653589793F));
		aestheticleg2.addOrReplaceChild("aesthetic_leg_2a", CubeListBuilder.create()
				.texOffs(91, 40).addBox(-1.01F, 0.0F, 0.0F, 2, 6, 2),
			PartPose.offsetAndRotation(0.0F, 6.0F, -1.0F, 0.6373942428283291F, 0.0F, 0.0F));

		var spiderleg1 = shoulders.addOrReplaceChild("spider_leg_1", CubeListBuilder.create()
				.texOffs(60, 0).addBox(-1.5F, 0.0F, -1.0F, 3, 10, 3),
			PartPose.offsetAndRotation(1.5F, -1.0F, -1.5F, -2.41309222380736F, -1.2747884856566583F, 0.0F));

		var spiderleg1a = spiderleg1.addOrReplaceChild("spider_leg_1a", CubeListBuilder.create()
				.texOffs(73, 0).addBox(-1.01F, 0.0F, -1.0F, 2, 18, 2),
			PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, 1.8212510744560826F, 0.0F, 0.0F));

		spiderleg1a.addOrReplaceChild("spider_leg_1b", CubeListBuilder.create()
				.texOffs(82, 0).addBox(-1.0F, 0.0F, 0.0F, 2, 18, 2),
			PartPose.offsetAndRotation(0.0F, 18.0F, -1.0F, 0.6373942428283291F, 0.0F, 0.0F));


		var spiderleg2 = shoulders.addOrReplaceChild("spider_leg_2", CubeListBuilder.create()
				.texOffs(60, 21).addBox(-1.5F, 0.0F, -1.0F, 3, 8, 3),
			PartPose.offsetAndRotation(-1.0F, -1.0F, -1.0F, -2.367539130330308F, 1.0471975511965976F, 0.0F));

		var spiderleg2a = spiderleg2.addOrReplaceChild("spider_leg_2a", CubeListBuilder.create()
				.texOffs(73, 21).addBox(-1.01F, 0.0F, -1.0F, 2, 16, 2),
			PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 1.8212510744560826F, 0.0F, 0.0F));

		spiderleg2a.addOrReplaceChild("spider_leg_2b", CubeListBuilder.create()
				.texOffs(82, 21).addBox(-1.0F, 0.0F, 0.0F, 2, 18, 2),
			PartPose.offsetAndRotation(0.0F, 16.0F, -1.0F, 0.6373942428283291F, 0.0F, 0.0F));


		var spiderleg3 = shoulders.addOrReplaceChild("spider_leg_3", CubeListBuilder.create()
				.texOffs(60, 42).addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2),
			PartPose.offsetAndRotation(1.5F, 0.0F, 1.5F, 0.8651597102135892F, -1.2747884856566583F, 3.141592653589793F));

		var spiderleg3a = spiderleg3.addOrReplaceChild("spider_leg_3a", CubeListBuilder.create()
				.texOffs(69, 42).addBox(-1.01F, 0.0F, -1.0F, 2, 14, 2),
			PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 1.730144887501979F, 0.0F, 0.0F));

		spiderleg3a.addOrReplaceChild("spider_leg_3b", CubeListBuilder.create()
				.texOffs(78, 42).addBox(-0.9F, 0.0F, 0.0F, 2, 18, 2),
			PartPose.offsetAndRotation(0.0F, 14.0F, -1.0F, 0.5462880558742251F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(DreadfulPeatMummy entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float partialTicks = ageInTicks - entity.tickCount;

		float spawningProgress = entity.getSpawningProgress(partialTicks);
		this.bodyBase.xRot -= Math.min((1 - spawningProgress) * 1.5f, 0.7f);
		this.head1.xRot -= Math.min((1 - spawningProgress) * 2.0f, 1.5f);

		this.legleft1.xRot += (1 - spawningProgress) * 0.85f;
		this.legright1.xRot += (1 - spawningProgress) * 0.85f;


		this.faceTarget(this.neck, 1, Mth.clamp(Mth.wrapDegrees(netHeadYaw), -25, 25), Mth.clamp(Mth.wrapDegrees(headPitch), -35, 35));

		if (entity.deathTicks > 0) {
			float contractAngle = this.bodyBase.xRot - this.bodyBase.xRot / (entity.deathTicks / 120.0F + 1.0F);
			this.bodyBase.xRot -= contractAngle;
			this.legright1.xRot += contractAngle;
			limbSwingAmount += Mth.cos(entity.deathTicks) / 2.0F * Mth.sin(entity.deathTicks / 2.0F);

			if (entity.deathTicks > 60) {
				float progress = (entity.deathTicks - 60) / 40.0F;
				float rotateAngle = progress * Mth.PI * 2.0F / 8F;
				this.shoulders.xRot = rotateAngle;
				this.bodyBase.xRot = rotateAngle;
			}
		}

		float speed = 0.3f;
		float degree = 0.65f;
		float height = 0.7f;
		this.swing(this.spiderleg1a, speed, degree * 0.5f, false, 0f, 0.3f, limbSwing, limbSwingAmount);
		this.swing(this.spiderleg2, speed, degree * 0.5f, false, 0f, 0f, limbSwing, limbSwingAmount);
		this.walk(this.spiderleg1, speed, degree * 0.4f, true, Mth.HALF_PI, -0.1f, limbSwing, limbSwingAmount);
		this.walk(this.spiderleg1a, speed, degree * 0.4f, false, Mth.HALF_PI, 0, limbSwing, limbSwingAmount);
		this.walk(this.spiderleg2, speed, degree * 0.4f, false, Mth.HALF_PI, -0.1f, limbSwing, limbSwingAmount);
		this.walk(this.spiderleg2a, speed, degree * 0.4f, true, Mth.HALF_PI, 0, limbSwing, limbSwingAmount);
		this.swing(this.spiderleg3, speed, degree * 0.5f, false, Mth.PI, 0f, limbSwing, limbSwingAmount);
		this.walk(this.spiderleg3, speed, degree * 0.4f, true, Mth.HALF_PI + Mth.PI, -0.1f, limbSwing, limbSwingAmount);
		this.walk(this.spiderleg3a, speed, degree * 0.4f, false, Mth.HALF_PI + Mth.PI, 0, limbSwing, limbSwingAmount);
		this.walk(this.legright1, speed, degree * 0.6f, false, 2, 0.1f, limbSwing, limbSwingAmount);
		this.walk(this.legright2, speed, degree * 0.6f, false, 0, 0.3f, limbSwing, limbSwingAmount);

		this.bob(this.shoulders, 2 * speed, height, false, limbSwing, limbSwingAmount);
		this.walk(this.shoulders, 2 * speed, height * 0.05f, true, Mth.PI, 0, limbSwing, limbSwingAmount);
		this.walk(this.neck, 2 * speed, height * 0.05f, true, Mth.PI + 0.5f, 0, limbSwing, limbSwingAmount);
		this.walk(this.head1, 2 * speed, height * 0.05f, true, Mth.PI + 1, 0, limbSwing, limbSwingAmount);
		this.walk(this.jaw, 2 * speed, height * 0.3f, false, Mth.PI + 1.5f, 0, limbSwing, limbSwingAmount);
		this.chainWave(this.tongue, 2 * speed, height * 0.2f, -3, limbSwing, limbSwingAmount);
		this.tongue1.xRot -= 0.2F * limbSwingAmount;
		this.swing(this.neck, speed, height * 0.2f, true, Mth.PI + 0.5f, 0, limbSwing, limbSwingAmount);
		this.swing(this.head1, speed, height * 0.2f, true, Mth.PI + 1, 0, limbSwing, limbSwingAmount);
		this.chainSwing(this.tongue, speed, height * 0.2f, -2, limbSwing, limbSwingAmount);

		this.walk(this.legleft1, 2 * speed, height * 0.3f, false, 0, 0, limbSwing, limbSwingAmount);
		this.walk(this.armright1, 2 * speed, height * 0.2f, false, Mth.PI - 1, 0, limbSwing, limbSwingAmount);
		this.walk(this.armleft1, 2 * speed, height * 0.2f, false, Mth.PI - 1, 0, limbSwing, limbSwingAmount);
		this.walk(this.armleft2, 2 * speed, height * 0.2f, false, Mth.PI - 3f, 0, limbSwing, limbSwingAmount);

		this.chainWave(this.tentacle1, 2 * speed, height * 0.2f, -3, limbSwing, limbSwingAmount);
		this.chainWave(this.tentacle2, 2 * speed, height * 0.2f, -2, limbSwing, limbSwingAmount);
	}

	@Override
	public void prepareMobModel(DreadfulPeatMummy entity, float limbSwing, float limbSwingAmount, float partialTick) {
		this.setInitPose();
		float frame = entity.tickCount + partialTick;
		this.chainWave(this.tentacle1, 0.2f, 0.3f, -2, frame, 1);
		this.chainWave(this.tentacle2, 0.3f, 0.3f, -2, frame, 1);

		if (entity.currentEatPrey != null) {
			this.walk(this.neck, 0.8f, 0.7f, false, 0, -0.2f, frame, 1);
			this.walk(this.head1, 0.8f, 0.7f, true, 0, 0.2f, frame, 1);
			this.walk(this.jaw, 0.8f, 0.8f, true, -0.7f, -0.6f, frame, 1);
			this.walk(this.tongue1, 0.8f, 0.8f, true, -0.7f, -0.5f, frame, 1);
			this.swing(this.neck, 0.4f, 0.3f, false, 0, 0, frame, 1);
			this.swing(this.head1, 0.4f, 0.3f, false, 0, 0, frame, 1);
			this.tongue2.xRot += Mth.PI;
			this.tongue3.xRot += Mth.PI;
			this.tongue4.xRot += Mth.PI;
			this.tongue5.xRot += Mth.PI;
			this.armleft1.xRot -= 1.2F;
		} else {
			this.chainWave(this.tongue, 0.2f, -0.3f, -3, frame, 1);
			this.walk(this.neck, 0.2f, 0.05f, true, 2, 0, frame, 1);
			this.walk(this.head1, 0.2f, 0.05f, true, 1, 0, frame, 1);
			if (entity.deathTicks > 0) {
				float rot = Math.min((entity.deathTicks) / 80.0F, (Mth.PI * 2.0F) / 7.0F);
				this.head1.xRot += rot;
				this.head1.yRot += rot;
				this.tongue1.zRot -= rot;
			}
			this.walk(this.jaw, 0.2f, 0.2f, true, 0, 0.2f, frame, 1);
			this.walk(this.armleft1, 0.15f, 0.2f, true, 2, 0, frame, 1);
			this.walk(this.armleft2, 0.15f, 0.2f, true, 1, 0, frame, 1);
		}
	}
}
