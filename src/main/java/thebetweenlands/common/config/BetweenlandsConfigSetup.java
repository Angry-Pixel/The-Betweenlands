package thebetweenlands.common.config;

import org.apache.commons.lang3.tuple.Pair;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;

public class BetweenlandsConfigSetup {

	public static final BetweenlandsCommonConfig COMMON_CONFIG;
	
	public static final ModConfigSpec COMMON_CONFIG_SPEC;
	
	static {
		final Pair<BetweenlandsCommonConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(BetweenlandsCommonConfig::new);
		COMMON_CONFIG = pair.getLeft();
		COMMON_CONFIG_SPEC = pair.getRight();
	}
	
	public static void init(IEventBus eventbus, Dist dist) {
		
		ModContainer container = ModLoadingContext.get().getActiveContainer();
		container.registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG_SPEC);
		
		container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		
		eventbus.addListener(BetweenlandsConfigSetup::loadConfigs);
		eventbus.addListener(BetweenlandsConfigSetup::reloadConfigs);
	}
	
	public static void loadConfigs(ModConfigEvent.Loading event) {
		if (event.getConfig().getSpec() == COMMON_CONFIG_SPEC) {
			BetweenlandsConfig.rebuildCommonConfig(COMMON_CONFIG);
		}
	}

	public static void reloadConfigs(ModConfigEvent.Reloading event) {
		if (event.getConfig().getSpec() == COMMON_CONFIG_SPEC) {
			BetweenlandsConfig.rebuildCommonConfig(COMMON_CONFIG);
		}
	}
}
