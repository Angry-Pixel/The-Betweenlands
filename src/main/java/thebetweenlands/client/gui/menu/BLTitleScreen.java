package thebetweenlands.client.gui.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.gui.ModListScreen;
import net.neoforged.neoforge.internal.BrandingControl;
import thebetweenlands.client.event.MainMenuEvents;
import thebetweenlands.common.TheBetweenlands;

public class BLTitleScreen extends TitleScreen {
	public static final ResourceLocation LOGO_TEXTURE = TheBetweenlands.prefix("textures/gui/menu/logo.png");
	private int ticks;
	private float bgFade = 1.0F;

	public BLTitleScreen() {
		super(true);
	}

	@Override
	protected void init() {
		int i = this.font.width(Component.translatable("title.credits"));
		int j = this.width - i - 2;
		int l = this.height / 4 + 32;
		this.addRenderableWidget(new BLMenuButton(Button.builder(Component.translatable("menu.singleplayer"), button -> this.minecraft.setScreen(new SelectWorldScreen(this))).bounds(this.width / 2 - 100, l, 200, 20)));
		Component component = this.getMultiplayerDisabledReason();
		boolean flag = component == null;
		Tooltip tooltip = component != null ? Tooltip.create(component) : null;
		this.addRenderableWidget(new BLMenuButton(Button.builder(Component.translatable("menu.multiplayer"), button -> this.minecraft.setScreen(this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this))).bounds(this.width / 2 - 100, l + 24, 200, 20).tooltip(tooltip))).active = flag;
		this.addRenderableWidget(new BLMenuButton(Button.builder(Component.translatable("menu.online"), button -> this.minecraft.setScreen(new RealmsMainScreen(this))).bounds(this.width / 2 - 100, l + 48, 200, 20).tooltip(tooltip))).active = flag;
		this.addRenderableWidget(new BLMenuButton(Button.builder(Component.translatable("fml.menu.mods"), button -> this.minecraft.setScreen(new ModListScreen(this))).pos(this.width / 2 - 100, l + 24 * 3).size(200, 20)));

		//TODO consider making the language and accessibility buttons picture buttons like in vanilla
		this.addRenderableWidget(new BLMenuButton(Button.builder(Component.literal("L"), button -> this.minecraft.setScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager()))).bounds(this.width / 2 - 124, l + 94 + 12, 20, 20)));
		this.addRenderableWidget(new BLMenuButton(Button.builder(Component.translatable("menu.options"), button -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))).bounds(this.width / 2 - 100, l + 94 + 12, 98, 20)));
		this.addRenderableWidget(new BLMenuButton(Button.builder(Component.translatable("menu.quit"), button -> this.minecraft.stop()).bounds(this.width / 2 + 2, l + 94 + 12, 98, 20)));
		this.addRenderableWidget(new BLMenuButton(Button.builder(Component.literal("A"), button -> this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options))).bounds(this.width / 2 + 104, l + 94 + 12, 20, 20)));
		this.addRenderableWidget(new PlainTextButton(j, this.height - 10, i, 10, Component.translatable("title.credits"), button -> this.minecraft.setScreen(new CreditsAndAttributionScreen(this)), this.font));

		if (this.realmsNotificationsScreen == null) {
			this.realmsNotificationsScreen = new RealmsNotificationsScreen();
		}

		this.realmsNotificationsScreen.init(this.minecraft, this.width, this.height);
	}

	@Override
	public void tick() {
		super.tick();
		this.ticks++;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		if (this.fadeInStart == 0L && this.fading) {
			this.fadeInStart = Util.getMillis();
		}

		float f = 1.0F;
		if (this.fading) {
			float f1 = (float) (Util.getMillis() - this.fadeInStart) / 2000.0F;
			if (f1 > 1.0F) {
				this.fading = false;
				this.bgFade = 1.0F;
			} else {
				f1 = Mth.clamp(f1, 0.0F, 1.0F);
				f = Mth.clampedMap(f1, 0.5F, 1.0F, 0.0F, 1.0F);
				this.bgFade = Mth.clampedMap(f1, 0.0F, 0.5F, 0.0F, 1.0F);
			}

			this.fadeWidgets(f);
		}

		MainMenuEvents.background.render(graphics, partialTicks, this.bgFade);
		int i = Mth.ceil(f * 255.0F) << 24;
		if ((i & -67108864) != 0) {
			for (Renderable renderable : this.renderables) {
				renderable.render(graphics, mouseX, mouseY, partialTicks);
			}
			this.renderTitle(graphics, partialTicks, f);
			ClientHooks.renderMainMenu(this, graphics, this.font, this.width, this.height, i);

			BrandingControl.forEachLine(true, true, (brdline, brd) -> graphics.drawString(this.font, brd, 2, this.height - (10 + brdline * (this.font.lineHeight + 1)), 16777215 | i));
			BrandingControl.forEachAboveCopyrightLine((brdline, brd) -> graphics.drawString(this.font, brd, this.width - font.width(brd), this.height - (10 + (brdline + 1) * (this.font.lineHeight + 1)), 16777215 | i));
			if (this.realmsNotificationsScreen != null && f >= 1.0F) {
				RenderSystem.enableDepthTest();
				this.realmsNotificationsScreen.render(graphics, mouseX, mouseY, partialTicks);
			}
		}
	}

	private void renderTitle(GuiGraphics graphics, float partialTicks, float alpha) {
		RenderSystem.enableCull();
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
		graphics.pose().pushPose();
		graphics.pose().translate(0.0F, Mth.sin(((float) this.ticks + partialTicks) / 16.0F) * 5.0F, 0.0F);
		graphics.blit(LOGO_TEXTURE, this.width / 2 - 161 / 2, 7, 0, 0, 161, 79);
		graphics.pose().popPose();
		graphics.blit(LOGO_TEXTURE, 0, 0, 239, 0, 17, 16);
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableBlend();
	}
}
