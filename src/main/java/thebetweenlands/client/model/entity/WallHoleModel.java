package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.wall.AbstractWallCreature;

public class WallHoleModel<T extends AbstractWallCreature> extends MowzieModelBase<T> {

	public final ModelPart window;
	private float windowZOffsetPercent = 0.0F;

	public WallHoleModel(ModelPart root) {
		super(root.getChild("frontPiece1"));
		this.window = root.getChild("window");
	}

	public static LayerDefinition create() {

		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var frontPiece1 = partDefinition.addOrReplaceChild("frontPiece1", CubeListBuilder.create()
				.texOffs(24, 22).addBox(1.0F, 1.0F, 0.0F, 14, 1, 9),
			PartPose.offset(-8.0F, 0.0F, -8.0F));
		var frontPiece2 = frontPiece1.addOrReplaceChild("frontPiece2", CubeListBuilder.create()
				.texOffs(-8, 7).addBox(0.0F, 0.0F, 0.0F, 1, 1, 9),
			PartPose.offset(1.0F, 2.0F, 0.0F));
		var frontPiece4 = frontPiece1.addOrReplaceChild("frontPiece4", CubeListBuilder.create()
				.texOffs(-8, 7).addBox(0.0F, 0.0F, 0.0F, 1, 4, 9),
			PartPose.offset(1.0F, 6.0F, 0.0F));
		var frontPiece5 = frontPiece1.addOrReplaceChild("frontPiece5", CubeListBuilder.create()
				.texOffs(-8, 7).addBox(0.0F, 0.0F, 0.0F, 1, 1, 9),
			PartPose.offset(1.0F, 12.0F, 0.0F));
		var frontPiece6 = frontPiece1.addOrReplaceChild("frontPiece6", CubeListBuilder.create()
				.texOffs(-9, 7).addBox(0.0F, 0.0F, 0.0F, 2, 2, 9),
			PartPose.offset(1.0F, 13.0F, 0.0F));
		var frontPiece7 = frontPiece1.addOrReplaceChild("frontPiece7", CubeListBuilder.create()
				.texOffs(12, 34).addBox(0.0F, 0.0F, 0.0F, 8, 1, 9),
			PartPose.offset(4.0F, 2.0F, 0.0F));
		var frontPiece8 = frontPiece1.addOrReplaceChild("frontPiece8", CubeListBuilder.create()
				.texOffs(11, 12).addBox(0.0F, 0.0F, 0.0F, 4, 1, 9),
			PartPose.offset(5.0F, 3.0F, 0.0F));
		var frontPiece9 = frontPiece1.addOrReplaceChild("frontPiece9", CubeListBuilder.create()
				.texOffs(15, 1).addBox(0.0F, 0.0F, 0.0F, 1, 1, 9),
			PartPose.offset(13.0F, 2.0F, 0.0F));
		var frontPiece10 = frontPiece1.addOrReplaceChild("frontPiece10", CubeListBuilder.create()
				.texOffs(14, 1).addBox(0.0F, 0.0F, 0.0F, 1, 2, 9),
			PartPose.offset(14.0F, 2.0F, 0.0F));
		var frontPiece12 = frontPiece1.addOrReplaceChild("frontPiece12", CubeListBuilder.create()
				.texOffs(15, 1).addBox(0.0F, 0.0F, 0.0F, 1, 2, 9),
			PartPose.offset(14.0F, 9.0F, 0.0F));
		var frontPiece13 = frontPiece1.addOrReplaceChild("frontPiece13", CubeListBuilder.create()
				.texOffs(12, 34).addBox(0.0F, 0.0F, 0.0F, 3, 1, 9),
			PartPose.offset(12.0F, 12.0F, 0.0F));
		var frontPiece14 = frontPiece1.addOrReplaceChild("frontPiece14", CubeListBuilder.create()
				.texOffs(11, 1).addBox(0.0F, 0.0F, 0.0F, 5, 2, 9),
			PartPose.offset(10.0F, 13.0F, 0.0F));
		var frontPiece15 = frontPiece1.addOrReplaceChild("frontPiece15", CubeListBuilder.create()
				.texOffs(-8, 7).addBox(0.0F, 0.0F, 0.0F, 1, 1, 9),
			PartPose.offset(9.0F, 14.0F, 0.0F));
		var frontPiece17 = frontPiece1.addOrReplaceChild("frontPiece17", CubeListBuilder.create()
				.texOffs(-8, 7).addBox(0.0F, 0.0F, 0.0F, 1, 1, 9),
			PartPose.offset(3.0F, 14.0F, 0.0F));

		var top = frontPiece1.addOrReplaceChild("top", CubeListBuilder.create()
				.texOffs(28, 36).addBox(0.0F, 0.0F, 0.0F, 14, 1, 10),
			PartPose.offset(1.0F, 0.0F, 0.0F));
		var left = frontPiece1.addOrReplaceChild("left", CubeListBuilder.create()
				.texOffs(0, 38).addBox(0.0F, 0.0F, 0.0F, 1, 16, 10),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		var right = frontPiece1.addOrReplaceChild("right", CubeListBuilder.create()
				.texOffs(32, 38).addBox(0.0F, 0.0F, 0.0F, 1, 16, 10),
			PartPose.offset(15.0F, 0.0F, 0.0F));
		var back = frontPiece1.addOrReplaceChild("back", CubeListBuilder.create()
				.texOffs(34, 0).addBox(0.0F, 0.0F, 0.0F, 14, 14, 1),
			PartPose.offset(1.0F, 1.0F, 9.0F));
		var bottom = frontPiece1.addOrReplaceChild("bottom", CubeListBuilder.create()
				.texOffs(10, 21).addBox(0.0F, 0.0F, 0.0F, 14, 1, 10),
			PartPose.offset(1.0F, 15.0F, 0.0F));

		var window = partDefinition.addOrReplaceChild("window", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 16, 16, 0),
			PartPose.offset(-8.0F, 0.0F, -8.0F));

		return LayerDefinition.create(definition, 64, 64);
	}

	public float getWindowZOffsetPercent() {
		return this.windowZOffsetPercent;
	}

	public void setWindowZOffsetPercent(float windowZOffsetPercent) {
		this.windowZOffsetPercent = windowZOffsetPercent;
	}
}
