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
					if(((EntityAnadia) player.fishEntity.getRidingEntity()).getStaminaTicks() <= 0)
						return;
					int fishpos = ((EntityAnadia) player.fishEntity.getRidingEntity()).getStaminaTicks() * 256 / 100;
					int escapepos = ((EntityAnadia) player.fishEntity.getRidingEntity()).getEscapeTicks() * 256 / 1024;
					int obstructpos1 = ((EntityAnadia) player.fishEntity.getRidingEntity()).getObstruction1Ticks();
					int obstructpos2 = ((EntityAnadia) player.fishEntity.getRidingEntity()).getObstruction2Ticks();
					int obstructpos3 = ((EntityAnadia) player.fishEntity.getRidingEntity()).getObstruction3Ticks();
					int obstructpos4 = ((EntityAnadia) player.fishEntity.getRidingEntity()).getObstruction4Ticks();
					GlStateManager.color(1F, 1F, 1F, 1F);
					mc.renderEngine.bindTexture(GUI_TEXTURE);
					ScaledResolution res = new ScaledResolution(mc);
					renderStaminaBar(-256 + fishpos, -256 + Math.min(256, escapepos), 0 - obstructpos1, 0 - obstructpos2, 0 - obstructpos3, 0 - obstructpos4, (float)res.getScaledWidth() * 0.5F - 128F, (float)res.getScaledHeight() * 0.5F - 120F);
				}
			}
		}
	}

	private void renderStaminaBar(int staminaTicks, int escapeTicks, int obstructionTicks1, int obstructionTicks2, int obstructionTicks3, int obstructionTicks4, float posX, float posY) {
		drawTexturedModalRect(posX, posY + 5, 0, 18, 256, 12);
		drawTexturedModalRect(posX - staminaTicks, posY + 5, 0, 0, 16, 11);
		drawTexturedModalRect(posX - escapeTicks, posY + 18, 0 + (getCrabScroll(escapeTicks) * 16), 32, 16, 48);
		drawTexturedModalRect(posX - obstructionTicks1, posY + 1, 32, 0, 13, 16);
		drawTexturedModalRect(posX - obstructionTicks2, posY + 1, 32, 0, 13, 16);
		drawTexturedModalRect(posX - obstructionTicks3, posY + 1, 32, 0, 13, 16);
		drawTexturedModalRect(posX - obstructionTicks4, posY + 1, 32, 0, 13, 16);
	}

	private int getCrabScroll(int escapeTicks) {
		if (escapeTicks%4 == 0)
			return 0;
		if (escapeTicks%4 == -1)
			return 1;
		if (escapeTicks%4 == -2)
			return 2;
		if (escapeTicks%4 == -3)
			return 3;
		return 0;
	}
}
