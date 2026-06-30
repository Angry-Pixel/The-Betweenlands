package thebetweenlands.client.handler;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import thebetweenlands.client.model.entity.PlayerVolarkiteArmPose;
import thebetweenlands.common.entity.VolarkiteEntity;

public class BipedVolarkiteArmsRenderHandler {

	public static void bipedVolarkiteArmsRenderPre(RenderLivingEvent.Pre<?, ?> event) {
		if (event.getEntity() instanceof Player player) {
			Entity mount = player.getVehicle();
			VolarkiteEntity kite = null;
			if (mount instanceof VolarkiteEntity)
				kite = (VolarkiteEntity) mount;
			else
				kite = (VolarkiteEntity) player.getPassengers().stream().filter(e -> e instanceof VolarkiteEntity).findAny().orElse(null);

			if (kite != null) {
				if (event.getRenderer().getModel() instanceof HumanoidModel<?> baseModel) {
					@SuppressWarnings("unchecked")
					HumanoidModel<AbstractClientPlayer> model = (HumanoidModel<AbstractClientPlayer>) baseModel;
					HumanoidModel.ArmPose volarkiteYPose = PlayerVolarkiteArmPose.THEBETWEENLANDS_VOLARKITE_Y_POSE.getValue();
					model.leftArmPose = volarkiteYPose;
					model.rightArmPose = volarkiteYPose;
				}
			}
		}
	}
}
