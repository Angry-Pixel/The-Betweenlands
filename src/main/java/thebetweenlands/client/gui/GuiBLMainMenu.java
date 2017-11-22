package thebetweenlands.client.gui;

import java.io.IOException;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.config.ConfigHandler;

public class GuiBLMainMenu extends GuiMainMenu {
	public static final ResourceLocation LOGO_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/main/logo.png");

	private GuiButton realmsButton;
	private GuiButton modButton;
	private net.minecraftforge.client.gui.NotificationModUpdateScreen modUpdateNotification;

	private int ticks;

	private GuiMainMenuBackground background = new GuiMainMenuBackground(new ResourceLocation(ModInfo.ID, "textures/gui/main/layer"), 4);

	@SubscribeEvent
	public static void onGuiOpened(GuiOpenEvent event) {
		if (ConfigHandler.blMainMenu && event.getGui() instanceof GuiMainMenu
				&& !(event.getGui() instanceof GuiBLMainMenu)) {
			event.setGui(new GuiBLMainMenu());
		}
	}

	@Override
	public void initGui() {
		this.background.mc = this.mc;
		this.background.width = this.width;
		this.background.height = this.height;
		this.background.initGui();

		this.buttonList.clear();

		int j = this.height / 4 + 48;

		this.buttonList.add(new GuiButtonMainMenu(1, this.width / 2 - 100, j, I18n.format("menu.singleplayer")));
		this.buttonList.add(new GuiButtonMainMenu(2, this.width / 2 - 100, j + 24, I18n.format("menu.multiplayer")));
		this.buttonList.add(this.realmsButton = new GuiButtonMainMenu(14, this.width / 2 + 2, j + 24 * 2, 98, 20, I18n.format("menu.online", new Object[0]).replace("Minecraft", "").trim()));

		this.buttonList.add(this.modButton = new GuiButtonMainMenu(6, this.width / 2 - 100, j + 24 * 2, 98, 20, I18n.format("fml.menu.mods")));
		this.buttonList.add(new GuiButtonMainMenu(5, this.width / 2 - 124, j + 72 + 12, 20, 20, "L"));
		this.buttonList.add(new GuiButtonMainMenu(0, this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.format("menu.options")));
		this.buttonList.add(new GuiButtonMainMenu(4, this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.format("menu.quit")));

		this.modUpdateNotification = net.minecraftforge.client.gui.NotificationModUpdateScreen.init(this, this.modButton);
	}

	@Override
	public void onGuiClosed() {
		this.background.onGuiClosed();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		}

		if (button.id == 5) {
			this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
		}

		if (button.id == 1) {
			this.mc.displayGuiScreen(new GuiWorldSelection(this));
		}

		if (button.id == 2) {
			this.mc.displayGuiScreen(new GuiMultiplayer(this));
		}

		if (button.id == 14 && this.realmsButton.visible) {
			RealmsBridge realmsbridge = new RealmsBridge();
			realmsbridge.switchToRealms(this);
		}

		if (button.id == 4) {
			this.mc.shutdown();
		}

		if (button.id == 6) {
			this.mc.displayGuiScreen(new net.minecraftforge.fml.client.GuiModList(this));
		}

		if (button.id == 11) {
			this.mc.launchIntegratedServer("Demo_World", "Demo_World", WorldServerDemo.DEMO_WORLD_SETTINGS);
		}

		if (button.id == 12) {
			ISaveFormat isaveformat = this.mc.getSaveLoader();
			WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

			if (worldinfo != null) {
				this.mc.displayGuiScreen(new GuiYesNo(this, I18n.format("selectWorld.deleteQuestion", new Object[0]),
						"\'" + worldinfo.getWorldName() + "\' "
								+ I18n.format("selectWorld.deleteWarning", new Object[0]),
								I18n.format("selectWorld.deleteButton", new Object[0]),
								I18n.format("gui.cancel", new Object[0]), 12));
			}
		}
	}

	@Override
	public void updateScreen() {
		this.ticks++;
		this.background.updateScreen();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.background.drawScreen(mouseX, mouseY, partialTicks);

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

		this.mc.getTextureManager().bindTexture(GuiBLMainMenu.LOGO_TEXTURE);

		this.drawTexturedModalRect(this.width / 2 - 161 / 2, 20 + MathHelper.sin(((float) this.ticks + partialTicks) / 16.0F) * 6.0F, 0, 0, 161, 79, 256, 256);
		this.drawTexturedModalRect(0, 0, 239, 0, 17, 16);

		for (GuiButton button : (List<GuiButton>) this.buttonList) {
			button.drawButton(this.mc, mouseX, mouseY, partialTicks);
		}

		net.minecraftforge.client.ForgeHooksClient.renderMainMenu(this, this.fontRenderer, this.width, this.height, "");

		java.util.List<String> brandings = com.google.common.collect.Lists
				.reverse(net.minecraftforge.fml.common.FMLCommonHandler.instance().getBrandings(true));
		for (int brdline = 0; brdline < brandings.size(); brdline++) {
			String brd = brandings.get(brdline);
			if (!com.google.common.base.Strings.isNullOrEmpty(brd)) {
				this.drawString(this.fontRenderer, brd, 2,
						this.height - (10 + brdline * (this.fontRenderer.FONT_HEIGHT + 1)), 16777215);
			}
		}

		this.modUpdateNotification.drawScreen(mouseX, mouseY, partialTicks);

		this.drawString(this.fontRenderer, "Copyright Mojang AB. Do not distribute!",
				this.width - this.fontRenderer.getStringWidth("Copyright Mojang AB. Do not distribute!") - 2,
				this.height - 10, -1);
	}

	protected void drawTexturedModalRect(double x, double y, double u, double v, double width, double height,
			double textureWidth, double textureHeight) {
		double uscale = 1.0F / textureWidth;
		double vscale = 1.0F / textureHeight;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x + 0, y + height, this.zLevel).tex(u * uscale, (v + height) * vscale).endVertex();
		buffer.pos(x + width, y + height, this.zLevel).tex((u + width) * uscale, (v + height) * vscale).endVertex();
		buffer.pos(x + width, y + 0, this.zLevel).tex((u + width) * uscale, v * vscale).endVertex();
		buffer.pos(x + 0, y + 0, this.zLevel).tex(u * uscale, v * vscale).endVertex();
		tessellator.draw();
	}
}
