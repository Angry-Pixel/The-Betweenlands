package thebetweenlands.client.model.entity;

import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.creature.Gecko;

public class GeckoModel<T extends Gecko> extends MowzieModelBase<T> implements HeadedModel {

	public final ModelPart root;
	public final ModelPart body;
	public final ModelPart head;
	public final ModelPart legleft_f1;
	public final ModelPart legright_f1;
	public final ModelPart legleft_b1;
	public final ModelPart legright_b1;
	public final ModelPart tail1;
	public final ModelPart tail2;
	public final ModelPart tail3;
	public final ModelPart crane;
	public final ModelPart tongue;
	public final ModelPart legleft_f2;
	public final ModelPart legright_f2;
	public final ModelPart legleft_b2;
	public final ModelPart legright_b2;
	private final ModelPart[] tail;

	public GeckoModel(ModelPart root) {
		this.root = root;
		this.body = root.getChild("body_base");
		this.tail1 = body.getChild("tail1");
		this.tail2 = tail1.getChild("tail2");
		this.tail3 = tail2.getChild("tail3");
		this.head = root.getChild("head");
		this.tongue = this.head.getChild("tongue");
		this.crane = this.head.getChild("crane");
		this.legleft_f1 = root.getChild("legleft_f1");
		this.legleft_b1 = root.getChild("legleft_b1");
		this.legright_f1 = root.getChild("legright_f1");
		this.legright_b1 = root.getChild("legright_b1");
		this.legleft_f2 = this.legleft_f1.getChild("legleft_f2");
		this.legleft_b2 = this.legleft_b1.getChild("legleft_b2");
		this.legright_f2 = this.legright_f1.getChild("legright_f2");
		this.legright_b2 = this.legright_b1.getChild("legright_b2");

		this.tail = new ModelPart[]{this.tail3, this.tail2, this.tail1};
	}

	public static LayerDefinition createModelLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition body = meshdefinition.getRoot();
		PartDefinition body_base = body.addOrReplaceChild("body_base", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, 0.0F, 4, 2, 5), PartPose.offsetAndRotation(0.0F, 22.0F, -4.0F, -0.091106186954104F, 0.0F, 0.0F));

		// Tail
		PartDefinition tail1 = body_base.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 8).addBox(-1.5F, -2.0F, 0.0F, 3, 2, 3), PartPose.offsetAndRotation(0.0F, 0.0F, 5.0F, 0.27314402793711257F, 0.0F, 0.0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, -2.0F, 0.0F, 2, 2, 3), PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.27314402793711257F, 0.0F, 0.0F));
		tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(0, 20).addBox(-0.5F, -2.0F, 0.0F, 1, 3, 3), PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.27314402793711257F, 0.0F, 0.0F));

		// Head
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(19, 0).addBox(-1.5F, -2.0F, -4.0F, 3, 2, 4), PartPose.offsetAndRotation(0.0F, 21.5F, -3.0F, 0.22759093446006054F, 0.0F, 0.0F));
		head.addOrReplaceChild("tongue", CubeListBuilder.create().texOffs(19, 12).addBox(-0.5F, 0.0F, -1.0F, 1, 0, 2), PartPose.offsetAndRotation(0.0F, -1.0F, -4.0F, 0.36425021489121656F, 0.0F, 0.0F));
		head.addOrReplaceChild("crane", CubeListBuilder.create().texOffs(19, 7).addBox(-1.5F, 0.0F, 0.0F, 3, 0, 4), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.136659280431156F, 0.0F, 0.0F));

		// Legs
		PartDefinition legright_b1 = body.addOrReplaceChild("legright_b1", CubeListBuilder.create().texOffs(34, 13).addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1), PartPose.offsetAndRotation(-2.0F, 22.0F, 0.5F, 0.6373942428283291F, 0.31869712141416456F, 0.6829473363053812F));
		PartDefinition legright_f1 = body.addOrReplaceChild("legright_f1", CubeListBuilder.create().texOffs(34, 4).addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1), PartPose.offsetAndRotation(-2.0F, 21.5F, -3.0F, 0.36425021489121656F, -0.18203784098300857F, 0.40980330836826856F));
		PartDefinition legleft_f1 = body.addOrReplaceChild("legleft_f1", CubeListBuilder.create().texOffs(34, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1), PartPose.offsetAndRotation(2.0F, 21.5F, -3.0F, 0.36425021489121656F, 0.18203784098300857F, -0.40980330836826856F));
		PartDefinition legleft_b1 = body.addOrReplaceChild("legleft_b1", CubeListBuilder.create().texOffs(34, 8).addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1), PartPose.offsetAndRotation(2.0F, 22.0F, 0.5F, 0.6373942428283291F, -0.31869712141416456F, -0.6829473363053812F));

		legright_b1.addOrReplaceChild("legright_b2", CubeListBuilder.create().texOffs(39, 13).addBox(-0.5F, -0.2F, -0.4F, 1, 3, 1), PartPose.offsetAndRotation(0.0F, 1.5F, 0.0F, 0.6373942428283291F, 0.0F, 0.0F));
		legright_f1.addOrReplaceChild("legright_f2", CubeListBuilder.create().texOffs(39, 4).addBox(-0.5F, -0.2F, -1.5F, 1, 1, 2), PartPose.offsetAndRotation(0.0F, 1.5F, -0.5F, 0.8196066167365371F, 0.0F, 0.0F));
		legleft_f1.addOrReplaceChild("legleft_f2", CubeListBuilder.create().texOffs(39, 0).addBox(-0.5F, -0.2F, -1.5F, 1, 1, 2), PartPose.offsetAndRotation(0.0F, 1.5F, -0.5F, 0.8196066167365371F, 0.0F, 0.0F));
		legleft_b1.addOrReplaceChild("legleft_b2", CubeListBuilder.create().texOffs(39, 8).addBox(-0.5F, -0.2F, -0.4F, 1, 3, 1), PartPose.offsetAndRotation(0.0F, 1.5F, 0.0F, 0.6373942428283291F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
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

		this.walk(this.legleft_f1, globalSpeed, globalDegree, false, frontOffset, 0, limbSwing, limbSwingAmount);
		this.walk(this.legleft_f2, globalSpeed, 1.5F * globalDegree, false, 2 + frontOffset, -0.4F * globalDegree, limbSwing, limbSwingAmount);
		this.walk(this.legright_f1, globalSpeed, globalDegree, true, frontOffset, 0, limbSwing, limbSwingAmount);
		this.walk(this.legright_f2, globalSpeed, 1.5F * globalDegree, true, 2 + frontOffset, -0.4F * globalDegree, limbSwing, limbSwingAmount);

		this.walk(this.legleft_b1, globalSpeed, globalDegree, false, 0, -2.5F * globalDegree, limbSwing, limbSwingAmount);
		this.walk(this.legleft_b2, globalSpeed, globalDegree, false, -1, 2.5F * globalDegree, limbSwing, limbSwingAmount);
		this.walk(this.legright_b1, globalSpeed, globalDegree, true, 0, -2.5F * globalDegree, limbSwing, limbSwingAmount);
		this.walk(this.legright_b2, globalSpeed, globalDegree, true, -1, 2.5F * globalDegree, limbSwing, limbSwingAmount);

		float frame = entity.tickCount + ageInTicks;
		float tongueControl = (int) ((Math.sin(0.15F * frame - Math.cos(0.15F * frame)) + 1) / 2 + 0.5F);
		this.tongue.visible = tongueControl == 1;
		this.walk(this.tongue, 2, 1, false, 0, 0, frame, 1);
		this.chainWave(this.tail, 0.2F, 0.1f, 0, frame, 1);
		this.chainSwing(this.tail, 0.4F, 0.15f, 3, frame, 1);
	}

	@Override
	public ModelPart getHead() {
		return this.head;
	}
}
