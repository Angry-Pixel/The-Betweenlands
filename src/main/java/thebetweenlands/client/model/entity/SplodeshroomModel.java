package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.Splodeshroom;

public class SplodeshroomModel extends MowzieModelBase<Splodeshroom> {

	private final ModelPart root;
	private final ModelPart hat;

	public SplodeshroomModel(ModelPart root) {
		super(RenderType::entityTranslucent);
		this.root = root;
		this.hat = root.getChild("hat_main");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var hat_main = partDefinition.addOrReplaceChild("hat_main", CubeListBuilder.create()
				.texOffs(26, 0).addBox(-4.0F, 0.0F, -3.5F, 7, 8, 7),
			PartPose.offset(0.0F, 13.2F, 0.0F));
		hat_main.addOrReplaceChild("hat_mid_front", CubeListBuilder.create()
				.texOffs(15, 22).addBox(-1.0F, -1.0F, -2.5F, 1, 4, 2),
			PartPose.offset(0.0F, -3.0F, 0.0F));
		hat_main.addOrReplaceChild("hat_right", CubeListBuilder.create()
				.texOffs(22, 19).addBox(-3.0F, -1.0F, -2.5F, 2, 4, 5),
			PartPose.offset(0.0F, -3.0F, 0.0F));
		hat_main.addOrReplaceChild("hat_mid_back", CubeListBuilder.create()
				.texOffs(37, 22).addBox(-1.0F, -1.0F, 0.5F, 1, 4, 2),
			PartPose.offset(0.0F, -3.0F, 0.0F));
		hat_main.addOrReplaceChild("hat_left", CubeListBuilder.create()
				.texOffs(0, 19).addBox(0.0F, -1.0F, -2.5F, 2, 4, 5),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		var stem1 = partDefinition.addOrReplaceChild("stem1", CubeListBuilder.create()
				.texOffs(13, 0).addBox(-1.5F, -4.0F, -1.5F, 3, 5, 3),
			PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, -0.13665927946567535F));
		stem1.addOrReplaceChild("stem2", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, -10.0F, -1.5F, 3, 10, 3),
			PartPose.offsetAndRotation(-1.5F, -4.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F));

		return LayerDefinition.create(definition, 64, 32);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(Splodeshroom entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.setInitPose();
		this.root.visible = !entity.getHasExploded();
		float swell = entity.getSwellCount() * 0.02F;
		float shake = (entity.getRandom().nextFloat() - entity.getRandom().nextFloat() * 0.5F) * swell;

		this.hat.setRotation(shake * 10F * Mth.DEG_TO_RAD, shake * 10F * Mth.DEG_TO_RAD, shake * 10F * Mth.DEG_TO_RAD);
		this.hat.y += 0F - swell * 0.25F;
		this.hat.xScale = 1.0F + swell;
		this.hat.yScale = 1.0F + swell * 0.25F;
		this.hat.zScale = 1.0F + swell;
	}
}
