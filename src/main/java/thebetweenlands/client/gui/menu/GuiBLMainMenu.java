package thebetweenlands.client.gui.menu;

import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Runnables;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.lib.ModInfo;

public class GuiBLMainMenu extends GuiMainMenu {
	public static final ResourceLocation LOGO_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/main/logo.png");

	private GuiButton realmsButton;
	private boolean hasCheckedForRealmsNotification;
	private GuiScreen realmsNotification;

	private int widthCopyright;
	private int widthCopyrightRest;

	private GuiButton modButton;
	private NotificationModUpdateScreen modUpdateNotification;

	private final Object threadLock = new Object();
	private int openGLWarning2Width;
	private int openGLWarning1Width;
	private int openGLWarningX1;
	private int openGLWarningY1;
	private int openGLWarningX2;
	private int openGLWarningY2;
	private String openGLWarning1 = "";
	private String openGLWarning2 = "";

	private int ticks;

	private GuiMainMenuBackground background = new GuiMainMenuBackground(new ResourceLocation(ModInfo.ID, "textures/gui/main/layer"), 4);

	public GuiBLMainMenu() {
		if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
			this.openGLWarning1 = I18n.format("title.oldgl1");
			this.openGLWarning2 = I18n.format("title.oldgl2");
		}
	}

	@SubscribeEvent
	public static void onGuiOpened(GuiOpenEvent event) {
		if (BetweenlandsConfig.GENERAL.blMainMenu && event.getGui() instanceof GuiMainMenu
				&& !(event.getGui() instanceof GuiBLMainMenu)) {
			event.setGui(new GuiBLMainMenu());
		}
	}

	@Override
	public void initGui() {
		this.widthCopyright = this.fontRenderer.getStringWidth("Copyright Mojang AB. Do not distribute!");
		this.widthCopyrightRest = this.width - this.widthCopyright - 2;

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

		synchronized (this.threadLock) {
			this.openGLWarning1Width = this.fontRenderer.getStringWidth(this.openGLWarning1);
			this.openGLWarning2Width = this.fontRenderer.getStringWidth(this.openGLWarning2);
			int k = Math.max(this.openGLWarning1Width, this.openGLWarning2Width);
			this.openGLWarningX1 = (this.width - k) / 2;
			this.openGLWarningY1 = (this.buttonList.get(0)).y - 24;
			this.openGLWarningX2 = this.openGLWarningX1 + k;
			this.openGLWarningY2 = this.openGLWarningY1 + 24;
		}

		this.mc.setConnectedToRealms(false);

		if (Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.REALMS_NOTIFICATIONS) && !this.hasCheckedForRealmsNotification) {
			RealmsBridge realmsbridge = new RealmsBridge();
			this.realmsNotification = realmsbridge.getNotificationScreen(this);
			this.hasCheckedForRealmsNotification = true;
		}

		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotification.setGuiSize(this.width, this.height);
			this.realmsNotification.initGui();
		}

		this.modUpdateNotification = NotificationModUpdateScreen.init(this, this.modButton);
	}

	private boolean areRealmsNotificationsEnabled() {
		return Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.REALMS_NOTIFICATIONS) && this.realmsNotification != null;
	}

	@Override
	public void onGuiClosed() {
		this.background.onGuiClosed();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
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
			this.mc.displayGuiScreen(new GuiModList(this));
		}

		if (button.id == 11) {
			this.mc.launchIntegratedServer("Demo_World", "Demo_World", WorldServerDemo.DEMO_WORLD_SETTINGS);
		}

		if (button.id == 12) {
			ISaveFormat isaveformat = this.mc.getSaveLoader();
			WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

			if (worldinfo != null) {
				this.mc.displayGuiScreen(new GuiYesNo(this, I18n.format("selectWorld.deleteQuestion"),
						"\'" + worldinfo.getWorldName() + "\' "
								+ I18n.format("selectWorld.deleteWarning"),
								I18n.format("selectWorld.deleteButton"),
								I18n.format("gui.cancel"), 12));
			}
		}
	}

	@Override
	public void updateScreen() {
		this.ticks++;
		this.background.updateScreen();
		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotification.updateScreen();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.background.drawScreen(mouseX, mouseY, partialTicks);

		GlStateManager.enableCull();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

		this.mc.getTextureManager().bindTexture(GuiBLMainMenu.LOGO_TEXTURE);

		this.drawTexturedModalRect(this.width / 2 - 161 / 2, 20 + MathHelper.sin(((float) this.ticks + partialTicks) / 16.0F) * 6.0F, 0, 0, 161, 79, 256, 256);
		this.drawTexturedModalRect(0, 0, 239, 0, 17, 16);

		ForgeHooksClient.renderMainMenu(this, this.fontRenderer, this.width, this.height, "");

		List<String> brandings = Lists.reverse(FMLCommonHandler.instance().getBrandings(true));
		for (int brdline = 0; brdline < brandings.size(); brdline++) {
			String brd = brandings.get(brdline);
			if (!Strings.isNullOrEmpty(brd)) {
				this.drawString(this.fontRenderer, brd, 2, this.height - (10 + brdline * (this.fontRenderer.FONT_HEIGHT + 1)), 16777215);
			}
		}

		this.drawString(this.fontRenderer, "Copyright Mojang AB. Do not distribute!", this.widthCopyrightRest, this.height - 10, -1);

		if (mouseX > this.widthCopyrightRest && mouseX < this.widthCopyrightRest + this.widthCopyright && mouseY > this.height - 10 && mouseY < this.height && Mouse.isInsideWindow()) {
			drawRect(this.widthCopyrightRest, this.height - 1, this.widthCopyrightRest + this.widthCopyright, this.height, -1);
		}

		if (this.openGLWarning1 != null && !this.openGLWarning1.isEmpty()) {
			drawRect(this.openGLWarningX1 - 2, this.openGLWarningY1 - 2, this.openGLWarningX2 + 2, this.openGLWarningY2 - 1, 1428160512);
			this.drawString(this.fontRenderer, this.openGLWarning1, this.openGLWarningX1, this.openGLWarningY1, -1);
			this.drawString(this.fontRenderer, this.openGLWarning2, (this.width - this.openGLWarning2Width) / 2, (this.buttonList.get(0)).y - 12, -1);
		}

		for (GuiButton button : this.buttonList) {
			button.drawButton(this.mc, mouseX, mouseY, partialTicks);
		}

		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotification.drawScreen(mouseX, mouseY, partialTicks);
		}

		this.modUpdateNotification.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		synchronized (this.threadLock) {
			String openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
			if (!this.openGLWarning1.isEmpty() && !StringUtils.isNullOrEmpty(openGLWarningLink) && mouseX >= this.openGLWarningX1 && mouseX <= this.openGLWarningX2 && mouseY >= this.openGLWarningY1 && mouseY <= this.openGLWarningY2) {
				GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, openGLWarningLink, 13, true);
				guiconfirmopenlink.disableSecurityWarning();
				this.mc.displayGuiScreen(guiconfirmopenlink);
			}
		}

		if (mouseX > this.widthCopyrightRest && mouseX < this.widthCopyrightRest + this.widthCopyright && mouseY > this.height - 10 && mouseY < this.height) {
			this.mc.displayGuiScreen(new GuiWinGame(false, Runnables.doNothing()));
		}
	}

	protected void drawTexturedModalRect(double x, double y, double u, double v, double width, double height, double textureWidth, double textureHeight) {
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
