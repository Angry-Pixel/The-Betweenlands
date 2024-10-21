package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

//TODO add rowing logic once entity is moving along
public class WeedwoodRowboatModel extends Model {

	private final ModelPart root;

	public WeedwoodRowboatModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
	}

	public static LayerDefinition createBoat() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var keel = partDefinition.addOrReplaceChild("keel", CubeListBuilder.create()
				.texOffs(0, 92).addBox(-2.0F, -4.0F, -11.0F, 4, 2, 22),
			PartPose.offset(0.0F, 4.0F, 0.0F));
		var fillupfront1 = keel.addOrReplaceChild("fillupfront1", CubeListBuilder.create()
				.texOffs(0, 103).addBox(-2.0F, -4.0F, -6.0F, 4, 4, 6, new CubeDeformation(-0.1F)),
			PartPose.offsetAndRotation(0.0F, -2.0F, -11.0F, -0.40980330836826856F, 0.0F, 0.0F));
		var fillupback1 = keel.addOrReplaceChild("fillupback1", CubeListBuilder.create()
				.texOffs(0, 92).addBox(-2.0F, -4.0F, 0.0F, 4, 4, 6, new CubeDeformation(-0.1F)),
			PartPose.offsetAndRotation(0.0F, -2.0F, 11.0F, 0.40980330836826856F, 0.0F, 0.0F));

		var frontrim1 = fillupfront1.addOrReplaceChild("frontrim1", CubeListBuilder.create()
				.texOffs(105, 92).addBox(-2.0F, -16.0F, -2.0F, 4, 16, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, 0.36425021489121656F, 0.0F, 0.0F));
		var frontrim2 = frontrim1.addOrReplaceChild("frontrim2", CubeListBuilder.create()
				.texOffs(120, 92).addBox(-2.0F, -4.0F, -7.0F, 4, 4, 8),
			PartPose.offset(0.0F, -16.0F, 0.0F));
		frontrim2.addOrReplaceChild("frontrimdetail", CubeListBuilder.create()
				.texOffs(105, 113).addBox(-2.0F, 0.0F, -2.0F, 4, 4, 2),
			PartPose.offset(0.0F, 2.0F, -2.0F));
		var backrim1 = fillupback1.addOrReplaceChild("backrim1", CubeListBuilder.create()
				.texOffs(55, 92).addBox(-2.0F, -16.0F, -1.0F, 4, 16, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 6.0F, -0.36425021489121656F, 0.0F, 0.0F));
		var backrim2 = backrim1.addOrReplaceChild("backrim2", CubeListBuilder.create()
				.texOffs(70, 92).addBox(-2.0F, -4.0F, -1.0F, 4, 4, 11),
			PartPose.offset(0.0F, -16.0F, 0.0F));
		backrim2.addOrReplaceChild("lanternMount", CubeListBuilder.create()
				.texOffs(218, 0).addBox(-1.0F, 0.0F, 0.0F, 2, 4, 2),
			PartPose.offset(0.0F, -3.0F, 10.0F));
		var backrimdetail1 = backrim2.addOrReplaceChild("backrimdetail1", CubeListBuilder.create()
				.texOffs(55, 113).addBox(-2.0F, 0.0F, 0.0F, 4, 4, 3),
			PartPose.offset(0.0F, 10.0F, 2.0F));
		backrimdetail1.addOrReplaceChild("backrimdetail2", CubeListBuilder.create()
				.texOffs(70, 110).addBox(-2.0F, -10.0F, 0.0F, 4, 12, 2),
			PartPose.offset(0.0F, 0.0F, 3.0F));
		backrimdetail1.addOrReplaceChild("backrimdetail3", CubeListBuilder.create()
				.texOffs(55, 121).addBox(-2.0F, -4.0F, 0.0F, 4, 4, 3),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		var hullBottom = partDefinition.addOrReplaceChild("hullBottom", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-6.0F, -2.0F, -11.0F, 12, 2, 22),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		hullBottom.addOrReplaceChild("seat", CubeListBuilder.create()
				.texOffs(100, 60).addBox(-6, 0, 0, 12, 2, 5),
			PartPose.offset(0, -10, 2));

		partDefinition.addOrReplaceChild("hullBottomLeft", CubeListBuilder.create()
				.texOffs(0, 25).addBox(-2.0F, -8.0F, -11.0F, 2, 10, 22),
			PartPose.offset(-6.0F, -2.0F, 0.0F));
		partDefinition.addOrReplaceChild("hullBottomRight", CubeListBuilder.create()
				.texOffs(0, 59).addBox(0.0F, -8.0F, -11.0F, 2, 10, 22),
			PartPose.offset(6.0F, -2.0F, 0.0F));

		var hullBow = partDefinition.addOrReplaceChild("hullBow", CubeListBuilder.create()
				.texOffs(100, 0).addBox(-4.0F, -2.0F, -2.0F, 8, 2, 6),
			PartPose.offsetAndRotation(0.0F, -2.0F, 11.0F, 0.045553093477052F, 0.0F, 0.0F));
		hullBow.addOrReplaceChild("piece2l", CubeListBuilder.create()
				.texOffs(117, 9).addBox(0.0F, -8.0F, -2.0F, 2, 10, 6),
			PartPose.offset(4.0F, -2.0F, 0.0F));
		hullBow.addOrReplaceChild("piece2lb", CubeListBuilder.create()
				.texOffs(117, 27).addBox(0.0F, -6.0F, -2.0F, 2, 6, 6),
			PartPose.offset(6.0F, -8.0F, 0.0F));
		hullBow.addOrReplaceChild("piece2r", CubeListBuilder.create()
				.texOffs(100, 9).addBox(-2.0F, -8.0F, -2.0F, 2, 10, 6),
			PartPose.offset(-4.0F, -2.0F, 0.0F));
		hullBow.addOrReplaceChild("piece2rb", CubeListBuilder.create()
				.texOffs(100, 27).addBox(-2.0F, -6.0F, -2.0F, 2, 6, 6),
			PartPose.offset(-6.0F, -8.0F, 0.0F));
		hullBow.addOrReplaceChild("piece2back", CubeListBuilder.create()
				.texOffs(100, 40).addBox(-6.0F, -8.0F, 0.0F, 12, 8, 2),
			PartPose.offset(0.0F, 0.0F, 4.0F));
		hullBow.addOrReplaceChild("piece2backb", CubeListBuilder.create()
				.texOffs(100, 51).addBox(-8.0F, -6.0F, 0.0F, 16, 6, 2),
			PartPose.offset(0.0F, -8.0F, 4.0F));

		var hullStern = partDefinition.addOrReplaceChild("hullStern", CubeListBuilder.create()
				.texOffs(140, 0).addBox(-4.0F, -2.0F, -4.0F, 8, 2, 6),
			PartPose.offsetAndRotation(0.0F, -2.0F, -11.0F, -0.045553093477052F, 0.0F, 0.0F));
		hullStern.addOrReplaceChild("piece3lb", CubeListBuilder.create()
				.texOffs(140, 27).addBox(0.0F, -6.0F, -2.0F, 2, 6, 6),
			PartPose.offset(6.0F, -8.0F, -2.0F));
		hullStern.addOrReplaceChild("piece3rb", CubeListBuilder.create()
				.texOffs(157, 27).addBox(-2.0F, -6.0F, -2.0F, 2, 6, 6),
			PartPose.offset(-6.0F, -8.0F, -2.0F));
		hullStern.addOrReplaceChild("piece3l", CubeListBuilder.create()
				.texOffs(140, 9).addBox(0.0F, -8.0F, -2.0F, 2, 10, 6),
			PartPose.offset(4.0F, -2.0F, -2.0F));
		hullStern.addOrReplaceChild("piece3r", CubeListBuilder.create()
				.texOffs(157, 9).addBox(-2.0F, -8.0F, -2.0F, 2, 10, 6),
			PartPose.offset(-4.0F, -2.0F, -2.0F));
		hullStern.addOrReplaceChild("piece3front", CubeListBuilder.create()
				.texOffs(140, 40).addBox(-6.0F, -8.0F, -2.0F, 12, 8, 2),
			PartPose.offset(0.0F, 0.0F, -4.0F));
		hullStern.addOrReplaceChild("piece3frontb", CubeListBuilder.create()
				.texOffs(140, 51).addBox(-8.0F, -6.0F, -2.0F, 16, 6, 2),
			PartPose.offset(0.0F, -8.0F, -4.0F));

		var hullGunwaleLeft = partDefinition.addOrReplaceChild("hullGunwaleLeft", CubeListBuilder.create()
				.texOffs(49, 25).addBox(-2.0F, -6.0F, -11.0F, 2, 6, 22),
			PartPose.offset(-8.0F, -8.0F, 0.0F));
		var oarlockRight = hullGunwaleLeft.addOrReplaceChild("oarlockRight", CubeListBuilder.create()
				.texOffs(180, 0).addBox(-2.0F, -3.0F, 0.0F, 2, 3, 4),
			PartPose.offset(0.0F, -6.0F, -5.0F));
		var oarLoomRight = oarlockRight.addOrReplaceChild("oarLoomRight", CubeListBuilder.create()
				.texOffs(180, 8).addBox(-1.0F, -8.0F, -1.0F, 2, 35, 2),
			PartPose.offsetAndRotation(-1.0F, -1.0F, 2.0F, 0.31869712141416456F, 0.0F, 1.0016444577195458F));
		//oarLoomRight.setRotationOrder(RotationOrder.ZXY);
		oarLoomRight.addOrReplaceChild("oarBladeRight", CubeListBuilder.create()
				.texOffs(180, 46).addBox(-3.0F, 0.0F, -0.5F, 6, 12, 1),
			PartPose.offset(0.0F, 25.0F, 0.0F));

		var hullGunwaleRight = partDefinition.addOrReplaceChild("hullGunwaleRight", CubeListBuilder.create()
				.texOffs(49, 59).addBox(0.0F, -6.0F, -11.0F, 2, 6, 22),
			PartPose.offset(8.0F, -8.0F, 0.0F));
		var oarlockLeft = hullGunwaleRight.addOrReplaceChild("oarlockLeft", CubeListBuilder.create()
				.texOffs(200, 0).addBox(0.0F, -3.0F, 0.0F, 2, 3, 4),
			PartPose.offset(0.0F, -6.0F, -5.0F));
		var oarLoomLeft = oarlockLeft.addOrReplaceChild("oarLoomLeft", CubeListBuilder.create()
				.texOffs(200, 8).addBox(-1.0F, -8.0F, -1.0F, 2, 35, 2),
			PartPose.offsetAndRotation(1.0F, -1.0F, 2.0F, 0.31869712141416456F, 0.0F, -1.0016444577195458F));
		//oarLoomLeft.setRotationOrder(RotationOrder.ZXY);
		oarLoomLeft.addOrReplaceChild("oarBladeLeft", CubeListBuilder.create()
				.texOffs(200, 46).addBox(-3.0F, 0.0F, -0.5F, 6, 12, 1),
			PartPose.offset(0.0F, 25.0F, 0.0F));

		return LayerDefinition.create(definition, 256, 128);
	}

	public static LayerDefinition createLantern() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.texOffs(218, 11).addBox(-2.5F, 0.0F, -2.5F, 5, 7, 5)
				.texOffs(239, 13).addBox(-1.5F, 2.0F, -1.5F, 3, 4, 3),
			PartPose.ZERO);

		base.addOrReplaceChild("top", CubeListBuilder.create()
				.texOffs(218, 24).addBox(-3.0F, -1.0F, -3.0F, 6, 2, 6),
			PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 0.13F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 256, 128);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
