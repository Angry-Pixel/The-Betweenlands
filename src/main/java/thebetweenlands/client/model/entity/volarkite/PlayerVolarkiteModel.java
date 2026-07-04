package thebetweenlands.client.model.entity.volarkite;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;

public class PlayerVolarkiteModel extends PlayerModel<AbstractClientPlayer> {

	public PlayerVolarkiteModel(ModelPart root, boolean slim) {
		super(root, slim);
	}

	@Override
	public void setupAnim(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		this.jacket.yRot = this.body.yRot = 0.0F;
		this.leftSleeve.yRot = this.leftArm.yRot = Mth.PI;
		this.leftSleeve.xRot = this.leftArm.xRot = 0.0F;
		this.leftSleeve.zRot = this.leftArm.zRot = -2.7F;
		this.leftSleeve.z = this.leftArm.z = 0.01F;
		this.leftSleeve.y = this.leftArm.y = 1.0F;
		this.leftSleeve.x = this.leftArm.x = 4.0F;

		this.rightSleeve.yRot = this.rightArm.yRot = Mth.PI;
		this.rightSleeve.xRot = this.rightArm.xRot = 0.0F;
		this.rightSleeve.zRot = this.rightArm.zRot = 2.7F;
		this.rightSleeve.z = this.rightArm.z = 0.01F;
		this.rightSleeve.y = this.rightArm.y = 1.0F;
		this.rightSleeve.x = this.rightArm.x = -4.0F;
	}
}
