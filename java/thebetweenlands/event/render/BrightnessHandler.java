package thebetweenlands.event.render;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldProvider;
import thebetweenlands.event.debugging.DebugHandler;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.WorldProviderBetweenlands;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BrightnessHandler {
	public static final BrightnessHandler INSTANCE = new BrightnessHandler();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTick(PlayerTickEvent event) {
		if(event.side == Side.SERVER || DebugHandler.INSTANCE.fullBright) return;
		EntityPlayer player = event.player;
		if(player.dimension == ConfigHandler.DIMENSION_ID && player.worldObj != null) {
			if(player.worldObj.provider instanceof WorldProviderBetweenlands) {
				WorldProviderBetweenlands provider = (WorldProviderBetweenlands) player.worldObj.provider;
				double diff = Math.max(WorldProviderBetweenlands.CAVE_START - player.posY, 0.0D);
				float multiplier = (float) diff / WorldProviderBetweenlands.CAVE_START;
				multiplier = 1.0F - multiplier;
				multiplier *= Math.pow(multiplier, 6);
				multiplier = multiplier * 0.9F + 0.1F;
				for(int i = 0; i < 16; i++) {
					provider.lightBrightnessTable[i] = provider.originalLightBrightnessTable[i] * (multiplier + (float)Math.pow(i, (1.0F - multiplier) * 2.2F) / 32.0F + multiplier * 0.5F);
				}
			}
		}
	}
}
