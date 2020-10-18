package thebetweenlands.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.entity.projectiles.EntityBLFishHook;

@SideOnly(Side.CLIENT)
public class GuiFishStaminaBar extends Gui {
	public static final ResourceLocation GUI_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/fish_stamina_bar.png");

	@SubscribeEvent
	public void onRenderHUD(RenderGameOverlayEvent.Post event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (event.getType().equals(RenderGameOverlayEvent.ElementType.HOTBAR)) {
			EntityPlayerSP player = mc.player;
			if (player != null && player.fishEntity != null && player.fishEntity instanceof EntityBLFishHook) {
				if (player.fishEntity.isRiding() && player.fishEntity.getRidingEntity() instanceof EntityAnadia) {
					int fishpos = ((EntityAnadia) player.fishEntity.getRidingEntity()).getStaminaTicks() * 256 / 100;
					int escapepos = ((EntityAnadia) player.fishEntity.getRidingEntity()).getEscapeTicks() * 256 / 480;
					GlStateManager.color(1F, 1F, 1F, 1F);
					mc.renderEngine.bindTexture(GUI_TEXTURE);
					ScaledResolution res = new ScaledResolution(mc);
					renderStaminaBar(fishpos, escapepos, (float)res.getScaledWidth() * 0.5F - 128F, (float)res.getScaledHeight() * 0.5F - 120F);
				}
			}
		}
	}

	private void renderStaminaBar(int staminaTicks, int escapeTicks, float posX, float posY) {
		drawTexturedModalRect(posX, posY, 0, 18, 256, 30);
		drawTexturedModalRect(posX + staminaTicks, posY + 1, 0, 0, 17, 11);
		drawTexturedModalRect(posX + escapeTicks, posY + 20, 0, 0, 17, 11);
	}
}
