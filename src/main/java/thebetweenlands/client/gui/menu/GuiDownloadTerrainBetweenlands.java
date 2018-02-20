package thebetweenlands.client.gui.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.BetweenlandsConfig;
import thebetweenlands.common.lib.ModInfo;

public class GuiDownloadTerrainBetweenlands extends GuiDownloadTerrain {
	private static int prevDimension;

	private GuiMainMenuBackground background = new GuiMainMenuBackground(new ResourceLocation(ModInfo.ID, "textures/gui/main/layer"), 4);

	@SubscribeEvent
	public static void onGuiOpened(GuiOpenEvent event) {
		if (event.getGui() instanceof GuiDownloadTerrain && 
				!(event.getGui() instanceof GuiDownloadTerrainBetweenlands) &&
				Minecraft.getMinecraft().player != null && 
				(Minecraft.getMinecraft().player.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId || prevDimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId)) {
			event.setGui(new GuiDownloadTerrainBetweenlands());
			prevDimension = Minecraft.getMinecraft().player.dimension;
		}
	}

	@Override
	public void initGui() {
		this.background.mc = this.mc;
		this.background.width = this.width;
		this.background.height = this.height;
		this.background.initGui();

		super.initGui();
	}

	@Override
	public void onGuiClosed() {
		this.background.onGuiClosed();
	}

	@Override
	public void updateScreen() {
		this.background.updateScreen();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.background.drawScreen(mouseX, mouseY, partialTicks);
		this.drawCenteredString(this.fontRenderer, I18n.format("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
	}
}
