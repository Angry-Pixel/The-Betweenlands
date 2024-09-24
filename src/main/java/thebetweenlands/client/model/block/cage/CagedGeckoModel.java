package thebetweenlands.client.model.block.cage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class CagedGeckoModel {

	public final ModelPart root;
	private final ModelPart tongue;
	private final ModelPart[] tail;

	public CagedGeckoModel(ModelPart root) {
		this.root = root;
		var tail1 = root.getChild("body").getChild("tail_1");
		var tail2 = tail1.getChild("tail_2");
		var tail3 = tail2.getChild("tail_3");
		this.tongue = root.getChild("head").getChild("tongue");

		this.tail = new ModelPart[]{tail3, tail2, tail1};
	}

	public static LayerDefinition createCorruptGecko() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-2.0F, -2.0F, 0.0F, 4, 2, 5),
			PartPose.offsetAndRotation(0.0F, 22.0F, -4.0F, -0.045553093477052F, 0.0F, -0.31869712141416456F));

		var tail1 = body.addOrReplaceChild("tail_1", CubeListBuilder.create()
				.texOffs(0, 8)
				.addBox(-1.5F, -2.0F, 0.0F, 3, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 4.2F, -0.18203784098300857F, 0.6829473363053812F, 0.0F));

		var tail2 = tail1.addOrReplaceChild("tail_2", CubeListBuilder.create()
				.texOffs(0, 14)
				.addBox(-1.0F, -2.0F, 0.0F, 2, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.3F, -0.136659280431156F, 1.1383037381507017F, 0.136659280431156F));

		tail2.addOrReplaceChild("tail_3", CubeListBuilder.create()
				.texOffs(0, 20)
				.addBox(-0.5F, -2.0F, 0.0F, 1, 3, 3),
			PartPose.offsetAndRotation(0.4F, 0.0F, 2.8F, -0.045553093477052F, 1.1838568316277536F, -0.091106186954104F));

		var head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(19, 0)
				.addBox(-1.5F, -2.0F, -4.0F, 3, 2, 4),
			PartPose.offsetAndRotation(0.0F, 21.2F, -3.0F, 1.0471975511965976F, -1.6390387005478748F, -0.7740535232594852F));

		head.addOrReplaceChild("tongue", CubeListBuilder.create()
				.texOffs(19, 12)
				.addBox(-0.5F, 0.0F, -1.0F, 1, 0, 2),
			PartPose.offsetAndRotation(0.0F, -1.0F, -4.0F, 0.36425021489121656F, 0.0F, 0.0F));

		head.addOrReplaceChild("crane", CubeListBuilder.create()
				.texOffs(19, 7)
				.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 4),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.136659280431156F, 0.0F, 0.0F));

		var legright_b1 = partDefinition.addOrReplaceChild("right_back_leg", CubeListBuilder.create()
				.texOffs(34, 13)
				.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1),
			PartPose.offsetAndRotation(-2.0F, 22.0F, 1.3F, 0.6373942428283291F, 0.31869712141416456F, 1.1838568316277536F));

		legright_b1.addOrReplaceChild("right_back_foot", CubeListBuilder.create()
				.texOffs(39, 13)
				.addBox(-0.5F, -0.2F, -0.4F, 1, 3, 1, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, 1.5F, 0.0F, 0.6373942428283291F, 0.0F, 0.0F));

		var legright_f1 = partDefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
				.texOffs(34, 4)
				.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1),
			PartPose.offsetAndRotation(-2.0F, 21.5F, -3.5F, -0.27314402793711257F, -0.6373942428283291F, 0.5918411493512771F));

		legright_f1.addOrReplaceChild("right_front_foot", CubeListBuilder.create()
				.texOffs(39, 4)
				.addBox(-0.5F, -0.2F, -1.5F, 1, 1, 2, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, 1.5F, -0.5F, 0.8196066167365371F, 0.0F, 0.0F));

		var legleft_f1 = partDefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
				.texOffs(34, 0)
				.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1),
			PartPose.offsetAndRotation(2.0F, 21.5F, -2.2F, 0.22759093446006054F, 0.18203784098300857F, -0.40980330836826856F));

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

	public static LayerDefinition createMutatedGecko() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-2.0F, -2.0F, 0.0F, 4, 2, 5),
			PartPose.offsetAndRotation(0.0F, 22.0F, -4.0F, -0.091106186954104F, 0.0F, 0.0F));

		var tail1 = body.addOrReplaceChild("tail_1", CubeListBuilder.create()
				.texOffs(0, 8)
				.addBox(-1.5F, -2.0F, 0.0F, 3, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 5.0F, 0.136659280431156F, 0.0F, 0.0F));

		var tail2 = tail1.addOrReplaceChild("tail_2", CubeListBuilder.create()
				.texOffs(0, 14)
				.addBox(-1.0F, -2.0F, 0.0F, 2, 2, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.091106186954104F, 0.0F, 0.0F));

		tail2.addOrReplaceChild("tail_3", CubeListBuilder.create(), PartPose.ZERO);

		var head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(19, 0)
				.addBox(-1.5F, -2.0F, -4.0F, 3, 2, 5),
			PartPose.offsetAndRotation(0.0F, 21.5F, -4.5F, 0.045553093477052F, 0.0F, 0.0F));

		head.addOrReplaceChild("tongue", CubeListBuilder.create(), PartPose.ZERO);

		head.addOrReplaceChild("crane", CubeListBuilder.create()
				.texOffs(19, 17)
				.addBox(-1.0F, -2.0F, -1.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, -2.0F, -1.6F, -1.2747884856566583F, 0.0F, 0.0F));

		var connection = head.addOrReplaceChild("jaw_connector", CubeListBuilder.create()
				.texOffs(19, 8)
				.addBox(-1.5F, 0.0F, -1.0F, 3, 1, 2),
			PartPose.ZERO);

		connection.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(19, 12)
				.addBox(-1.51F, 0.0F, -3.0F, 3, 1, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.36425021489121656F, 0.0F, 0.0F));


		var legleft_f1 = partDefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
				.texOffs(36, 0)
				.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 2),
			PartPose.offsetAndRotation(2.0F, 21.5F, -3.0F, 0.36425021489121656F, 0.18203784098300857F, -0.40980330836826856F));

		legleft_f1.addOrReplaceChild("left_front_foot", CubeListBuilder.create()
				.texOffs(43, 0)
				.addBox(-0.5F, -0.2F, -1.5F, 1, 1, 2, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, 1.5F, -0.5F, 0.8196066167365371F, 0.0F, 0.0F));

		var legright_f1 = partDefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
				.texOffs(36, 4)
				.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 2),
			PartPose.offsetAndRotation(-2.0F, 21.5F, -3.0F, 0.36425021489121656F, -0.18203784098300857F, 0.40980330836826856F));

		legright_f1.addOrReplaceChild("right_front_foot", CubeListBuilder.create()
				.texOffs(43, 4)
				.addBox(-0.5F, -0.2F, -1.5F, 1, 1, 2, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, 1.5F, -0.5F, 0.8196066167365371F, 0.0F, 0.0F));

		var legleft_b1 = partDefinition.addOrReplaceChild("left_back_leg", CubeListBuilder.create()
				.texOffs(36, 8)
				.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 2),
			PartPose.offsetAndRotation(2.0F, 22.0F, 0.5F, 0.6373942428283291F, -0.31869712141416456F, -0.6829473363053812F));

		legleft_b1.addOrReplaceChild("left_back_foot", CubeListBuilder.create()
				.texOffs(43, 8)
				.addBox(-0.5F, -0.2F, -0.4F, 1, 3, 1, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, 1.5F, 0.0F, 0.6373942428283291F, 0.0F, 0.0F));

		var legright_b1 = partDefinition.addOrReplaceChild("right_back_leg", CubeListBuilder.create()
				.texOffs(36, 13)
				.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 2),
			PartPose.offsetAndRotation(-2.0F, 22.0F, 0.5F, 0.6373942428283291F, 0.31869712141416456F, 0.6829473363053812F));

		legright_b1.addOrReplaceChild("right_back_foot", CubeListBuilder.create()
				.texOffs(43, 13)
				.addBox(-0.5F, -0.2F, -0.4F, 1, 3, 1, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, 1.5F, 0.0F, 0.6373942428283291F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 32);
	}

	public void renderWithAnimations(PoseStack stack, VertexConsumer consumer, int light, int overlay, int ticks, float partialTick) {
		this.root.getAllParts().forEach(ModelPart::resetPose);
		float frame = ticks + partialTick;
		float tongueControl = (int) (Mth.sin(0.15F * frame - Mth.cos(0.15F * frame)) + 1.0F) / 2.0F + 0.5F;
		this.tongue.visible = tongueControl == 1;
		this.tongue.xRot += Mth.cos(frame * 2);
		this.chainWave(this.tail, 0.2F, 0.1F, 0.0F, frame, 1.0F);
		this.chainSwing(this.tail, 0.4F, 0.15F, 3.0F, frame, 1.0F);
		this.root.render(stack, consumer, light, overlay);
	}

	public void chainSwing(ModelPart[] boxes, float speed, float degree, double rootOffset, float f, float f1) {
		int numberOfSegments = boxes.length;
		float offset = (float) ((rootOffset * Mth.PI) / (2.0F * numberOfSegments));
		for (int i = 0; i < numberOfSegments; i++)
			boxes[i].yRot += Mth.cos(f * speed + offset * i) * f1 * degree;
	}

	public void chainWave(ModelPart[] boxes, float speed, float degree, double rootOffset, float f, float f1) {
		int numberOfSegments = boxes.length;
		float offset = (float) ((rootOffset * Mth.PI) / (2 * numberOfSegments));
		for (int i = 0; i < numberOfSegments; i++)
			boxes[i].xRot += Mth.cos(f * speed + offset * i) * f1 * degree;
	}
}
