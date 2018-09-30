package thebetweenlands.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.event.ArmSwingSpeedEvent;

@SideOnly(Side.CLIENT)
public class ArmSwingSpeedHandler {

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent e) {
		if(e.phase == TickEvent.Phase.END) {
			World world = Minecraft.getMinecraft().world;
			if(world != null) {
				for(Entity entity : world.loadedEntityList) {
					if(entity instanceof EntityLivingBase) {
						EntityLivingBase living = (EntityLivingBase) entity;
						if(living.isSwingInProgress && living.swingProgressInt != 0) {
							ArmSwingSpeedEvent event = new ArmSwingSpeedEvent(living);

							MinecraftForge.EVENT_BUS.post(event);

							if(!event.isCanceled() && event.getSpeed() != 1.0F) {
								float speed = event.getSpeed();

								int swingAnimationEnd = living.getArmSwingAnimationEnd();

								if(living.prevSwingProgress < living.swingProgress) {
									living.swingProgress = living.prevSwingProgress;
									living.swingProgress += 1.0F / (float)(swingAnimationEnd / speed);
								}

								if(living.swingProgressInt < 0 || living.swingProgress < 0) {
									living.swingProgressInt = 0;
									living.swingProgress = 0;
								}

								living.swingProgressInt = (int)(living.swingProgress * swingAnimationEnd);
							}
						}
					}
				}
			}
		}
	}

}
