package thebetweenlands.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import thebetweenlands.common.world.DimensionBetweenlands;

public class BrightnessHandler {
	private BrightnessHandler() { }

	@SubscribeEvent
	public static void onTick(TickEvent.ClientTickEvent event) {
		if(event.phase == Phase.END) {
			EntityPlayer player = Minecraft.getInstance().player;
			World world = Minecraft.getInstance().world;
			if(player != null && world != null && world.isRemote() && world.dimension instanceof DimensionBetweenlands) {
				DimensionBetweenlands provider = (DimensionBetweenlands)world.dimension;
				provider.updateClientLightTable(Minecraft.getInstance().player);
			}
		}
	}
}
