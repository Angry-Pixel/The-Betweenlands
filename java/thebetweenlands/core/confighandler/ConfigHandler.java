package thebetweenlands.core.confighandler;

import net.minecraftforge.common.config.Configuration;
import thebetweenlands.lib.ModInfo;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ConfigHandler {

	public static final ConfigHandler INSTANCE = new ConfigHandler();
	public Configuration config;
	public static int DIMENSION_ID;
	public static int DRUID_CIRCLE_FREQUENCY;

	public final String[] usedCategories = { "World and Dimension"};

	public void loadConfig(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		syncConfigs();
	}

	private void syncConfigs() {
		DIMENSION_ID = config.get("World and Dimension", "The Betweenlands Dimension ID", 20).getInt(20);
		DRUID_CIRCLE_FREQUENCY = config.get("World and Dimension", "Frequency of Druid Circles. Higher numbers de-crease rate.", 10).getInt(10);
		
		if (config.hasChanged())
			config.save();
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.modID.equals(ModInfo.ID))
			syncConfigs();
	}
}