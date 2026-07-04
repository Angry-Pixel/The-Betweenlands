package thebetweenlands.common.handler;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import thebetweenlands.common.entity.VolarkiteEntity;

public class VolarkiteMountHandler {
	private static boolean isMountingEvent = false;
	public static void onMountEvent(EntityMountEvent event) {
		if(!isMountingEvent) {
			isMountingEvent = true;

			try {
				if(event.isDismounting()) {
					Entity mount = event.getEntityBeingMounted();
					Entity rider = event.getEntityMounting();

					if(mount instanceof VolarkiteEntity && rider instanceof LivingEntity) {
						event.setCanceled(true);
						((VolarkiteEntity) mount).handleRiderDismount((LivingEntity) rider);
					}
				}
			} finally {
				isMountingEvent = false;
			}
		}
	}

}
