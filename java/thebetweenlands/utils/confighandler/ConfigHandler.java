package thebetweenlands.utils.confighandler;

import net.minecraftforge.common.config.Configuration;
import thebetweenlands.lib.ModInfo;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ConfigHandler
{
	public static final ConfigHandler INSTANCE = new ConfigHandler();
	public Configuration config;

	public static final String[] usedCategories = { "World and Dimension"};

	//////// Values ///////
	public static int DIMENSION_ID;
	public static int DRUID_CIRCLE_FREQUENCY;
	public static int BIOME_ID_SWAMPLANDS;
	public static int BIOME_ID_COARSE_ISLANDS;
	public static int BIOME_ID_DEEP_WATER;
	public static int BIOME_ID_PATCHY_ISLANDS;
	public static int BIOME_ID_MARSH1;
	public static int BIOME_ID_MARSH2;
	public static int DIMENSION_BRIGHTNESS;
	public static int GIANT_TREE_DENSITY; //temp

	public void loadConfig(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		syncConfigs();
	}

	private void syncConfigs() {
		DIMENSION_ID = config.get(usedCategories[0], "The Betweenlands Dimension ID", 20).getInt(20);
		DRUID_CIRCLE_FREQUENCY = config.get(usedCategories[0], "Frequency of Druid Circles. Higher numbers de-crease rate.", 10).getInt(10);
		BIOME_ID_SWAMPLANDS = config.get(usedCategories[0], "Swamplands Biome ID", 50).getInt(50);
		BIOME_ID_COARSE_ISLANDS = config.get(usedCategories[0], "Coarse Islands Biome ID", 51).getInt(51);
		BIOME_ID_DEEP_WATER = config.get(usedCategories[0], "Deep Water Biome ID", 52).getInt(52);
		BIOME_ID_PATCHY_ISLANDS = config.get(usedCategories[0], "Patchy Islands Biome ID", 53).getInt(53);
		BIOME_ID_MARSH1 = config.get(usedCategories[0], "Marsh 1 Biome ID", 54).getInt(54);
		BIOME_ID_MARSH2 = config.get(usedCategories[0], "Marsh 2 Biome ID", 55).getInt(55);
		DIMENSION_BRIGHTNESS = config.get(usedCategories[0], "Dimension brightness (0-100)", 10).setMinValue(0).setMaxValue(100).getInt(10);
		GIANT_TREE_DENSITY = config.get(usedCategories[0], "Frequency of Giant Trees. Higher numbers de-crease rate.", 10).getInt(10);
		
		if( config.hasChanged() ) {
            config.save();
        }
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if( event.modID.equals(ModInfo.ID) ) {
            syncConfigs();
        }
	}
}
