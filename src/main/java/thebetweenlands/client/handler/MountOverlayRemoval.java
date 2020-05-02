package thebetweenlands.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityChiromawMatriarch;

@SideOnly(Side.CLIENT)
public class MountOverlayRemoval {

	@SubscribeEvent
	public static void onRenderHUD(Pre event) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player != null && player.isRiding())
			if (player.getRidingEntity() instanceof EntityChiromawMatriarch) {
				if (event.getType().equals(RenderGameOverlayEvent.ElementType.HEALTHMOUNT))
					event.setCanceled(true);
				if (event.getType().equals(RenderGameOverlayEvent.ElementType.ALL)) {
					Minecraft.getMinecraft().ingameGUI.setOverlayMessage("", false);
				}
			}
	}
}