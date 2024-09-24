package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.creature.Gecko;

public class GeckoModel<T extends Gecko> extends MowzieModelBase<T> {

	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart tongue;
	private final ModelPart legleft_f1;
	private final ModelPart legright_f1;
	private final ModelPart legleft_b1;
	private final ModelPart legright_b1;
	private final ModelPart legleft_f2;
	private final ModelPart legright_f2;
	private final ModelPart legleft_b2;
	private final ModelPart legright_b2;
	private final ModelPart[] tail;

	public GeckoModel(ModelPart root) {
		this.root = root;
		var tail1 = root.getChild("body").getChild("tail_1");
		var tail2 = tail1.getChild("tail_2");
		var tail3 = tail2.getChild("tail_3");
		this.head = root.getChild("head");
		this.tongue = this.head.getChild("tongue");
		this.legleft_f1 = root.getChild("left_front_leg");
		this.legleft_b1 = root.getChild("left_back_leg");
		this.legright_f1 = root.getChild("right_front_leg");
		this.legright_b1 = root.getChild("right_back_leg");
		this.legleft_f2 = this.legleft_f1.getChild("left_front_foot");
		this.legleft_b2 = this.legleft_b1.getChild("left_back_foot");
		this.legright_f2 = this.legright_f1.getChild("right_front_foot");
		this.legright_b2 = this.legright_b1.getChild("right_back_foot");

		this.tail = new ModelPart[]{tail3, tail2, tail1};
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.0F, -2.0F, 0.0F, 4, 2, 5),
			PartPose.offsetAndRotation(0.0F, 22.0F, -4.0F, -0.091106186954104F, 0.0F, 0.0F));

		var tail1 = body.addOrReplaceChild("tail_1", CubeListBuilder.create()
				.texOffs(0, 8)
				.addBox(-1.5F, -2.0F, 0.0F, 3, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 5.0F, 0.27314402793711257F, 0.0F, 0.0F));

		var tail2 = tail1.addOrReplaceChild("tail_2", CubeListBuilder.create()
				.texOffs(0, 14)
				.addBox(-1.0F, -2.0F, 0.0F, 2, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.27314402793711257F, 0.0F, 0.0F));

		tail2.addOrReplaceChild("tail_3", CubeListBuilder.create()
				.texOffs(0, 20)
				.addBox(-0.5F, -2.0F, 0.0F, 1, 3, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.27314402793711257F, 0.0F, 0.0F));

		var head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(19, 0)
				.addBox(-1.5F, -2.0F, -4.0F, 3, 2, 4),
			PartPose.offsetAndRotation(0.0F, 21.5F, -3.0F, 0.22759093446006054F, 0.0F, 0.0F));

		head.addOrReplaceChild("tongue", CubeListBuilder.create()
				.texOffs(19, 12)
				.addBox(-0.5F, 0.0F, -1.0F, 1, 0, 2),
			PartPose.offsetAndRotation(0.0F, -1.0F, -4.0F, 0.36425021489121656F, 0.0F, 0.0F));

		head.addOrReplaceChild("crane", CubeListBuilder.create()
				.texOffs(19, 7)
				.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 4),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.136659280431156F, 0.0F, 0.0F));

		var legright_b1 = partDefinition.addOrReplaceChild("right_back_leg", CubeListBuilder.create()
				.texOffs(34, 13).addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1),
			PartPose.offsetAndRotation(-2.0F, 22.0F, 0.5F, 0.6373942428283291F, 0.31869712141416456F, 0.6829473363053812F));

		legright_b1.addOrReplaceChild("right_back_foot", CubeListBuilder.create()
				.texOffs(39, 13)
				.addBox(-0.5F, -0.2F, -0.4F, 1, 3, 1, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, 1.5F, 0.0F, 0.6373942428283291F, 0.0F, 0.0F));

		var legright_f1 = partDefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
				.texOffs(34, 4)
				.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1),
			PartPose.offsetAndRotation(-2.0F, 21.5F, -3.0F, 0.36425021489121656F, -0.18203784098300857F, 0.40980330836826856F));

		legright_f1.addOrReplaceChild("right_front_foot", CubeListBuilder.create()
				.texOffs(39, 4)
				.addBox(-0.5F, -0.2F, -1.5F, 1, 1, 2, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, 1.5F, -0.5F, 0.8196066167365371F, 0.0F, 0.0F));

		var legleft_f1 = partDefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
				.texOffs(34, 0)
				.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1),
			PartPose.offsetAndRotation(2.0F, 21.5F, -3.0F, 0.36425021489121656F, 0.18203784098300857F, -0.40980330836826856F));

		legleft_f1.addOrReplaceChild("left_front_foot", CubeListBuilder.create()
				.texOffs(39, 0)
				.addBox(-0.5F, -0.2F, -1.5F, 1, 1, 2, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, 1.5F, -0.5F, 0.8196066167365371F, 0.0F, 0.0F));

		var legleft_b1 = partDefinition.addOrReplaceChild("left_back_leg", CubeListBuilder.create()
				.texOffs(34, 8)
				.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1),
			PartPose.offsetAndRotation(2.0F, 22.0F, 0.5F, 0.6373942428283291F, -0.31869712141416456F, -0.6829473363053812F));

		legleft_b1.addOrReplaceChild("left_back_foot", CubeListBuilder.create()
				.texOffs(39, 8)
				.addBox(-0.5F, -0.2F, -0.4F, 1, 3, 1, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, 1.5F, 0.0F, 0.6373942428283291F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 32);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.faceTarget(this.head, 1, netHeadYaw, headPitch);

		if (limbSwingAmount > 0.4) {
			limbSwingAmount = 0.4F;
		}

		float globalSpeed = 1, globalDegree = 1, frontOffset = 0.85F;

		this.walk(this.legleft_f1, globalSpeed, globalDegree, false, frontOffset, 0.0F, limbSwing, limbSwingAmount);
		this.walk(this.legleft_f2, globalSpeed, 1.5F * globalDegree, false, 2.0F + frontOffset, -0.4F * globalDegree, limbSwing, limbSwingAmount);
		this.walk(this.legright_f1, globalSpeed, globalDegree, true, frontOffset, 0.0F, limbSwing, limbSwingAmount);
		this.walk(this.legright_f2, globalSpeed, 1.5F * globalDegree, true, 2.0F + frontOffset, -0.4F * globalDegree, limbSwing, limbSwingAmount);

		this.walk(this.legleft_b1, globalSpeed, globalDegree, false, 0.0F, -2.5F * globalDegree, limbSwing, limbSwingAmount);
		this.walk(this.legleft_b2, globalSpeed, globalDegree, false, -1.0F, 2.5F * globalDegree, limbSwing, limbSwingAmount);
		this.walk(this.legright_b1, globalSpeed, globalDegree, true, 0.0F, -2.5F * globalDegree, limbSwing, limbSwingAmount);
		this.walk(this.legright_b2, globalSpeed, globalDegree, true, -1.0F, 2.5F * globalDegree, limbSwing, limbSwingAmount);
	}

	@Override
	public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
		this.setInitPose();
		float frame = entity.tickCount + partialTick;
		float tongueControl = (int) (Mth.sin(0.15F * frame - Mth.cos(0.15F * frame)) + 1.0F) / 2.0F + 0.5F;
		this.tongue.visible = tongueControl == 1;
		this.walk(this.tongue, 2.0F, 1.0F, false, 0.0F, 0.0F, frame, 1.0F);
		this.chainWave(this.tail, 0.2F, 0.1F, 0.0F, frame, 1.0F);
		this.chainSwing(this.tail, 0.4F, 0.15F, 3.0F, frame, 1.0F);
	}
}
