package thebetweenlands.client.model.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class WindChimeModel {
	public ModelPart base;
	public ModelPart string1;
	public ModelPart string2;
	public ModelPart string3;
	public ModelPart string4;
	public ModelPart string5;
	public ModelPart string6;
	public ModelPart midstring;
	public ModelPart rod1;
	public ModelPart rod2;
	public ModelPart rod3;
	public ModelPart rod4;
	public ModelPart rod5;
	public ModelPart rod6;
	public ModelPart clapper;

	public WindChimeModel(ModelPart root) {
		this.base = root.getChild("base");
		this.string1 = this.base.getChild("string_1");
		this.rod1 = this.string1.getChild("rod_1");
		this.string2 = this.base.getChild("string_2");
		this.rod2 = this.string2.getChild("rod_2");
		this.string3 = this.base.getChild("string_3");
		this.rod3 = this.string3.getChild("rod_3");
		this.string4 = this.base.getChild("string_4");
		this.rod4 = this.string4.getChild("rod_4");
		this.string5 = this.base.getChild("string_5");
		this.rod5 = this.string5.getChild("rod_5");
		this.string6 = this.base.getChild("string_6");
		this.rod6 = this.string6.getChild("rod_6");
		this.midstring = this.base.getChild("middle_string");
		this.clapper = this.midstring.getChild("clapper");
	}

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.texOffs(0, 12)
				.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1)
				.texOffs(0, 0)
				.addBox(-1.5F, 2.0F, -1.5F, 3, 1, 3)
				.texOffs(0, 5)
				.addBox(-2.0F, 3.0F, -2.0F, 4, 2, 4),
			PartPose.offset(0.0F, -16.0F, 0.0F));

		base.addOrReplaceChild("left_edge", CubeListBuilder.create()
			.texOffs(13, 8)
			.addBox(0.0F, -1.0F, -2.0F, 1, 1, 4, new CubeDeformation(-0.001F)),
			PartPose.offsetAndRotation(2.0F, 4.0F, 0.0F, 0.0F, 0.0F, -0.136659280431156F));

		base.addOrReplaceChild("right_edge", CubeListBuilder.create()
				.texOffs(20, 6)
				.addBox(-1.0F, -1.0F, -2.0F, 1, 1, 4, new CubeDeformation(-0.001F)),
			PartPose.offsetAndRotation(-2.0F, 4.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F));

		var string1 = base.addOrReplaceChild("string_1", CubeListBuilder.create()
			.texOffs(13, 0)
			.addBox(0.0F, 0.0F, -0.5F, 0, 4, 1),
			PartPose.offset(-2.0F, 5.0F, 0.0F));

		string1.addOrReplaceChild("rod_1", CubeListBuilder.create()
			.texOffs(0, 16)
			.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1),
			PartPose.offset(0.0F, 4.0F, 0.0F));

		var string2 = base.addOrReplaceChild("string_2", CubeListBuilder.create()
				.texOffs(16, 0)
				.addBox(0.0F, 0.0F, -0.5F, 0, 5, 1),
			PartPose.offsetAndRotation(-1.5F, 5.0F, -1.5F, 0.0F, -0.7853981633974483F, 0.0F));

		string2.addOrReplaceChild("rod_2", CubeListBuilder.create()
				.texOffs(5, 16)
				.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1),
			PartPose.offset(0.0F, 5.0F, 0.0F));

		var string3 = base.addOrReplaceChild("string_3", CubeListBuilder.create()
				.texOffs(19, 0)
				.addBox(0.0F, 0.0F, -0.5F, 0, 5, 1),
			PartPose.offsetAndRotation(1.5F, 5.0F, -1.5F, 0.0F, 0.7853981633974483F, 0.0F));

		string3.addOrReplaceChild("rod_3", CubeListBuilder.create()
				.texOffs(10, 16)
				.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1),
			PartPose.offset(0.0F, 5.0F, 0.0F));

		var string4 = base.addOrReplaceChild("string_4", CubeListBuilder.create()
				.texOffs(22, 0)
				.addBox(0.0F, 0.0F, -0.5F, 0, 4, 1),
			PartPose.offset(2.0F, 5.0F, 0.0F));

		string4.addOrReplaceChild("rod_4", CubeListBuilder.create()
				.texOffs(15, 16)
				.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1),
			PartPose.offset(0.0F, 4.0F, 0.0F));

		var string5 = base.addOrReplaceChild("string_5", CubeListBuilder.create()
				.texOffs(25, 0)
				.addBox(0.0F, 0.0F, -0.5F, 0, 3, 1),
			PartPose.offsetAndRotation(1.5F, 5.0F, 1.5F, 0.0F, -0.7853981633974483F, 0.0F));

		string5.addOrReplaceChild("rod_5", CubeListBuilder.create()
				.texOffs(20, 16)
				.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1),
			PartPose.offset(0.0F, 3.0F, 0.0F));

		var string6 = base.addOrReplaceChild("string_6", CubeListBuilder.create()
				.texOffs(28, 0)
				.addBox(0.0F, 0.0F, -0.5F, 0, 3, 1),
			PartPose.offsetAndRotation(-1.5F, 5.0F, 1.5F, 0.0F, 0.7853981633974483F, 0.0F));

		string6.addOrReplaceChild("rod_6", CubeListBuilder.create()
				.texOffs(25, 16)
				.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1),
			PartPose.offset(0.0F, 3.0F, 0.0F));

		var middleString = base.addOrReplaceChild("middle_string", CubeListBuilder.create()
				.texOffs(0, 23)
				.addBox(-0.5F, 0.0F, 0.0F, 1, 5, 0),
			PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 0.0F, 0.27314402793711257F, 0.0F));

		middleString.addOrReplaceChild("clapper", CubeListBuilder.create()
			.texOffs(3, 23)
			.addBox(-1.0F, 0.0F, -1.0F, 2, 1, 2),
			PartPose.offset(0.0F, 5.0F, 0.0F));

		return LayerDefinition.create(definition, 32, 32);
	}

	public void renderWithAnimation(PoseStack stack, VertexConsumer consumer, int light, int overlay, float ticks, float strength) {
		this.string1.xRot = 0.0F;
		this.string1.zRot = 0.0F;
		this.rod1.zRot = 0.0F;

		this.string2.xRot = 0.0F;
		this.string2.zRot = 0.0F;
		this.rod2.zRot = 0.0F;

		this.string3.xRot = 0.0F;
		this.string3.zRot = 0.0F;
		this.rod3.zRot = 0.0F;

		this.string4.xRot = 0.0F;
		this.string4.zRot = 0.0F;
		this.rod4.zRot = 0.0F;

		this.string5.xRot = 0.0F;
		this.string5.zRot = 0.0F;
		this.rod5.zRot = 0.0F;

		this.string6.xRot = 0.0F;
		this.string6.zRot = 0.0F;
		this.rod6.zRot = 0.0F;

		this.midstring.xRot = 0.0F;
		this.midstring.zRot = 0.0F;
		this.clapper.zRot = 0.0F;

		float frame2 = ticks * 0.1F;

		float b1 = Mth.cos(frame2) * 2.0F * 0.15F;
		float b2 = Mth.cos(frame2 - 0.1F) * 2.0F * b1 * 0.15F;
		float b3 = Mth.sin(frame2 * 0.5F + 0.1F) * 1.5F * 0.15F;

		float b4 = Mth.cos(frame2 * 0.94F + 0.5F) * 2.0F * 0.15F;
		float b5 = Mth.cos(frame2 * 0.94F + 0.5F - 0.1F) * 2.0F * b4 * 0.15F;
		float b6 = Mth.sin(frame2 * 0.94F * 0.5F + 0.5F + 0.1F) * 2.0F * 0.15F;

		this.string1.xRot += -b3 * 0.1F;
		this.string1.zRot += b1 * 0.1F;
		this.rod1.zRot += b2 * 0.1F;

		this.string2.xRot += -b6 * 0.1F;
		this.string2.zRot += -b4 * 0.1F;
		this.rod2.zRot += b5 * 0.1F;

		this.string3.xRot += b3 * 0.1F;
		this.string3.zRot += b1 * 0.1F;
		this.rod3.zRot += b2 * 0.1F;

		this.string4.xRot += -b6 * 0.1F;
		this.string4.zRot += b4 * 0.1F;
		this.rod4.zRot += b5 * 0.1F;

		this.string5.xRot += -b6 * 0.1F;
		this.string5.zRot += -b4 * 0.1F;
		this.rod5.zRot += -b5 * 0.1F;

		this.string6.xRot += b3 * 0.1F;
		this.string6.zRot += b1 * 0.1F;
		this.rod6.zRot += -b2 * 0.1F;

		this.midstring.xRot += -b3 * 0.1F;
		this.midstring.zRot += -b4 * 0.1F;
		this.clapper.zRot += -b2 * 0.1F;

		if (strength > 0.01F) {
			float frame = ticks * 0.8F;

			float a1 = Mth.cos(frame) * 2.0F * strength;
			float a2 = Mth.cos(frame - 0.1F) * 2.0F * a1 * strength;
			float a3 = Mth.sin(frame * 0.5F + 0.1F) * 1.5F * strength;

			float a4 = Mth.cos(frame * 0.94F + 0.5F) * 2.0F * strength;
			float a5 = Mth.cos(frame * 0.94F + 0.5F - 0.1F) * 2.0F * a4 * strength;
			float a6 = Mth.sin(frame * 0.94F * 0.5F + 0.5F + 0.1F) * 2.0F * strength;

			this.string1.xRot += -a3 * 0.1F;
			this.string1.zRot += a1 * 0.1F;
			this.rod1.zRot += a2 * 0.1F;

			this.string2.xRot += -a6 * 0.1F;
			this.string2.zRot += -a4 * 0.1F;
			this.rod2.zRot += a5 * 0.1F;

			this.string3.xRot += a3 * 0.1F;
			this.string3.zRot += a1 * 0.1F;
			this.rod3.zRot += a2 * 0.1F;

			this.string4.xRot += -a6 * 0.1F;
			this.string4.zRot += a4 * 0.1F;
			this.rod4.zRot += a5 * 0.1F;

			this.string5.xRot += -a6 * 0.1F;
			this.string5.zRot += -a4 * 0.1F;
			this.rod5.zRot += -a5 * 0.1F;

			this.string6.xRot += a3 * 0.1F;
			this.string6.zRot += a1 * 0.1F;
			this.rod6.zRot += -a2 * 0.1F;

			this.midstring.xRot += -a3 * 0.1F;
			this.midstring.zRot += -a4 * 0.1F;
			this.clapper.zRot += -a2 * 0.1F;
		}

		this.base.render(stack, consumer, light, overlay);
	}
}
