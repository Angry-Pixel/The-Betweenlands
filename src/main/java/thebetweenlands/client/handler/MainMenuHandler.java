package thebetweenlands.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.SelectMusicEvent;
import net.neoforged.neoforge.common.NeoForge;
import thebetweenlands.client.gui.menu.BLTitleScreen;
import thebetweenlands.client.gui.menu.BLTitleScreenBackground;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.SoundRegistry;

public class MainMenuHandler {

	public static final BLTitleScreenBackground background = new BLTitleScreenBackground(TheBetweenlands.prefix("textures/gui/menu/layer"), 4);

	public static void init() {
		NeoForge.EVENT_BUS.addListener(MainMenuHandler::openMainMenu);
		NeoForge.EVENT_BUS.addListener(MainMenuHandler::initBackground);
		NeoForge.EVENT_BUS.addListener(MainMenuHandler::tickBackground);
		NeoForge.EVENT_BUS.addListener(MainMenuHandler::playProperMenuMusic);
	}

	private static void openMainMenu(ScreenEvent.Opening event) {
		if (event.getNewScreen().getClass() == TitleScreen.class && BetweenlandsConfig.blMainMenu) {
			event.setNewScreen(new BLTitleScreen(((TitleScreen)event.getNewScreen()).fading));
		}
	}

	private static void initBackground(ScreenEvent.Init.Pre event) {
		if (!event.isCanceled() && Minecraft.getInstance().screen != null && BetweenlandsConfig.blMainMenu) {
			MainMenuHandler.background.width = event.getScreen().width;
			MainMenuHandler.background.height = event.getScreen().height;
			MainMenuHandler.background.init();
		}
	}

	private static void tickBackground(ClientTickEvent.Pre event) {
		if (Minecraft.getInstance().screen != null && BetweenlandsConfig.blMainMenu) {
			MainMenuHandler.background.tick();
		}
	}

	private static void playProperMenuMusic(SelectMusicEvent event) {
		if (event.getOriginalMusic() == Musics.MENU && BetweenlandsConfig.blMainMenu) {
			event.overrideMusic(MusicHandler.BL_MAIN_MENU);
		}
	}
}
