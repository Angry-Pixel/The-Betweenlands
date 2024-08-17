package thebetweenlands.client.event;

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

public class MainMenuEvents {

	public static final BLTitleScreenBackground background = new BLTitleScreenBackground(TheBetweenlands.prefix("textures/gui/menu/layer"), 4);
	public static final Music BL_MENU = new Music(SoundRegistry.BL_MUSIC_MENU, 20, 600, true);

	public static void init() {
		NeoForge.EVENT_BUS.addListener(MainMenuEvents::openMainMenu);
		NeoForge.EVENT_BUS.addListener(MainMenuEvents::initBackground);
		NeoForge.EVENT_BUS.addListener(MainMenuEvents::tickBackground);
		NeoForge.EVENT_BUS.addListener(MainMenuEvents::playProperMenuMusic);
	}

	private static void openMainMenu(ScreenEvent.Opening event) {
		if (event.getNewScreen().getClass() == TitleScreen.class && BetweenlandsConfig.blMainMenu) {
			event.setNewScreen(new BLTitleScreen(((TitleScreen)event.getNewScreen()).fading));
		}
	}

	private static void initBackground(ScreenEvent.Init.Pre event) {
		if (!event.isCanceled() && Minecraft.getInstance().screen != null && BetweenlandsConfig.blMainMenu) {
			MainMenuEvents.background.width = event.getScreen().width;
			MainMenuEvents.background.height = event.getScreen().height;
			MainMenuEvents.background.init();
		}
	}

	private static void tickBackground(ClientTickEvent.Pre event) {
		if (Minecraft.getInstance().screen != null && BetweenlandsConfig.blMainMenu) {
			MainMenuEvents.background.tick();
		}
	}

	private static void playProperMenuMusic(SelectMusicEvent event) {
		if (event.getOriginalMusic() == Musics.MENU && BetweenlandsConfig.blMainMenu) {
			event.overrideMusic(BL_MENU);
		}
	}
}
