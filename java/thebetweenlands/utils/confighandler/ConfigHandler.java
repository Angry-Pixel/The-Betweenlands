package thebetweenlands.utils.confighandler;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;
import thebetweenlands.lib.ModInfo;

public class ConfigHandler {
	public static final ConfigHandler INSTANCE = new ConfigHandler();
	public static final String[] CATEGORIES = { "World and Dimension", "Rendering", "General" };

	//////// Values ///////
	public static int DIMENSION_ID;
	public static int DRUID_CIRCLE_FREQUENCY;
	
	public static int BIOME_ID_SWAMPLANDS;
	public static int BIOME_ID_COARSE_ISLANDS;
	public static int BIOME_ID_DEEP_WATER;
	public static int BIOME_ID_PATCHY_ISLANDS;
	public static int BIOME_ID_MARSH1;
	public static int BIOME_ID_MARSH2;
	public static boolean BIOME_ID_LIMIT;
	public static int DIMENSION_BRIGHTNESS;
	public static int WISP_QUALITY;
	public static boolean USE_SHADER;
	public static boolean FIREFLY_LIGHTING;
	public static boolean DEBUG;
	public static boolean DEBUG_MENU_ON_START;
	public static int SKY_RESOLUTION;

	public Configuration config;

	public void loadConfig(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		syncConfigs();
	}

	private void syncConfigs() {
		DIMENSION_ID = config.get(CATEGORIES[0], "The Betweenlands Dimension ID", 20).getInt(20);
		DRUID_CIRCLE_FREQUENCY = config.get(CATEGORIES[0], "Frequency of Druid Circles. Higher numbers de-crease rate.", 10).getInt(10);
		BIOME_ID_SWAMPLANDS = config.get(CATEGORIES[0], "Swamplands Biome ID", 50).getInt(50);
		BIOME_ID_COARSE_ISLANDS = config.get(CATEGORIES[0], "Coarse Islands Biome ID", 51).getInt(51);
		BIOME_ID_DEEP_WATER = config.get(CATEGORIES[0], "Deep Water Biome ID", 52).getInt(52);
		BIOME_ID_PATCHY_ISLANDS = config.get(CATEGORIES[0], "Patchy Islands Biome ID", 53).getInt(53);
		BIOME_ID_MARSH1 = config.get(CATEGORIES[0], "Marsh 1 Biome ID", 54).getInt(54);
		BIOME_ID_MARSH2 = config.get(CATEGORIES[0], "Marsh 2 Biome ID", 55).getInt(55);
		BIOME_ID_LIMIT = config.getBoolean("Biome ID Limit", CATEGORIES[0], true, "Prevents any biome IDs higher than 127. Setting this to false \nis NOT recommended unless you know what you're doing!");
		DIMENSION_BRIGHTNESS = config.get(CATEGORIES[0], "Dimension brightness (0-100)", 60).setMinValue(0).setMaxValue(100).getInt(60);

		WISP_QUALITY = config.get(CATEGORIES[1], "Wisp Rendering Quality (0-100)", 100).setMinValue(0).setMaxValue(100).getInt(100);
		FIREFLY_LIGHTING = config.getBoolean("Firefly block lighting", CATEGORIES[1], true, "");
		USE_SHADER = config.getBoolean("Use shaders for rendering (this forces FBOs to be enabled)", CATEGORIES[1], true, "");
		SKY_RESOLUTION = config.get(CATEGORIES[1], "Sky texture resolution (only when shaders are enabled)", 1024).getInt(1024);
		
		// Replaced with false by gradle for release version
		DEBUG = config.getBoolean("Debug mode", CATEGORIES[2], /*!*/true/*!*/, "");
		DEBUG_MENU_ON_START = config.getBoolean("Debug menu on start", CATEGORIES[2], /*!*/true/*!*/, "");

		if (config.hasChanged()) {
			config.save();
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.modID.equals(ModInfo.ID)) {
			syncConfigs();
		}
	}
}
