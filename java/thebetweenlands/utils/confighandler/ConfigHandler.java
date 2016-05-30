package thebetweenlands.utils.confighandler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.stream.JsonReader;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;
import thebetweenlands.herblore.elixirs.ElixirEffectRegistry;
import thebetweenlands.herblore.elixirs.effects.ElixirEffect;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.recipes.ConfigRecipe;

public class ConfigHandler {
	public static final ConfigHandler INSTANCE = new ConfigHandler();
	public static final String[] CATEGORIES = { "World and Dimension", "Rendering", "General", "CustomRecipes", "Potion effects", "Mob Spawning"};

	//////// Values ///////
	public static int DIMENSION_ID;
	public static int DRUID_CIRCLE_FREQUENCY;

	public static int BIOME_ID_SWAMPLANDS;
	public static int BIOME_ID_COARSE_ISLANDS;
	public static int BIOME_ID_DEEP_WATER;
	public static int BIOME_ID_PATCHY_ISLANDS;
	public static int BIOME_ID_MARSH1;
	public static int BIOME_ID_MARSH2;
	public static int BIOME_ID_SLUDGE_PLAINS;
	public static boolean BIOME_ID_LIMIT;
	public static int DIMENSION_BRIGHTNESS;
	public static int WISP_QUALITY;
	public static boolean USE_SHADER;
	public static boolean FIREFLY_BLOCK_LIGHTING;
	public static boolean DEBUG;
	public static boolean DEBUG_MENU_ON_START;
	public static int SKY_RESOLUTION;

	public static boolean rowboatView;

	public static Map<Integer, Integer> potionIDs = new HashMap<Integer, Integer>();

	public static boolean useBLMainMenu;

	public static int maxEntitiesPerLoadedArea;
	public static int hardEntityLimit;

	public Configuration config;
	public static String path = "";

	public void loadConfig(FMLPreInitializationEvent event) {
		path = event.getSuggestedConfigurationFile().getPath();
		config = new Configuration(new File(path.replace("thebetweenlands.cfg", "thebetweenlands/mainConfig.cfg")));
		config.load();
		syncConfigs();
	}

	private void syncConfigs() {
		DIMENSION_ID = config.get(CATEGORIES[0], "The Betweenlands Dimension ID", 20).getInt(20);
		DRUID_CIRCLE_FREQUENCY = config.get(CATEGORIES[0], "Frequency of Druid Circles", 1400, "Higher numbers decrease rate").getInt(1400);
		BIOME_ID_SWAMPLANDS = config.get(CATEGORIES[0], "Swamplands Biome ID", 50).getInt(50);
		BIOME_ID_COARSE_ISLANDS = config.get(CATEGORIES[0], "Coarse Islands Biome ID", 51).getInt(51);
		BIOME_ID_DEEP_WATER = config.get(CATEGORIES[0], "Deep Water Biome ID", 52).getInt(52);
		BIOME_ID_PATCHY_ISLANDS = config.get(CATEGORIES[0], "Patchy Islands Biome ID", 53).getInt(53);
		BIOME_ID_MARSH1 = config.get(CATEGORIES[0], "Marsh 1 Biome ID", 54).getInt(54);
		BIOME_ID_MARSH2 = config.get(CATEGORIES[0], "Marsh 2 Biome ID", 55).getInt(55);
		BIOME_ID_SLUDGE_PLAINS = config.get(CATEGORIES[0], "Sludge Plains Biome ID", 56).getInt(56);
		BIOME_ID_LIMIT = config.getBoolean("Biome ID Limit", CATEGORIES[0], true, "Prevents any biome IDs higher than 127. Setting this to false \nis NOT recommended unless you know what you're doing!");
		DIMENSION_BRIGHTNESS = config.get(CATEGORIES[0], "Dimension brightness (0-100)", 75).setMinValue(0).setMaxValue(100).getInt(75);

		WISP_QUALITY = config.get(CATEGORIES[1], "Wisp Rendering Quality (0-100)", 100).setMinValue(0).setMaxValue(100).getInt(100);
		FIREFLY_BLOCK_LIGHTING = config.getBoolean("Firefly block lighting", CATEGORIES[1], false, "");
		USE_SHADER = config.getBoolean("Use shaders for rendering", CATEGORIES[1], true, "This forces FBOs to be enabled. \nIf you use OptiFine set \"Fast Render\" to OFF and restart the game!");
		SKY_RESOLUTION = config.get(CATEGORIES[1], "Sky texture resolution", 1024, "Only when shaders are enabled").getInt(1024);

		// Replaced with false by gradle for release version
		DEBUG = config.getBoolean("Debug mode", CATEGORIES[2], /*!*/true/*!*/, "");
		DEBUG_MENU_ON_START = config.getBoolean("Debug menu on start", CATEGORIES[2], /*!*/true/*!*/, "");

		rowboatView = config.getBoolean("Rowboat view", CATEGORIES[2], true, "If true, the camera perspective will be switched to rowboat perspective when you enter a rowboat, otherwise first-person");

		Map<Integer, ElixirEffect> usedIDs = new HashMap<Integer, ElixirEffect>();
		int potionStartID = 50;
		for(int i = 0; i < ElixirEffectRegistry.getEffects().size(); i++) {
			ElixirEffect elixir = ElixirEffectRegistry.getEffects().get(i);
			int id = config.get(CATEGORIES[4], elixir.getEffectName(), potionStartID + i).getInt(potionStartID + i);
			if(usedIDs.containsKey(Integer.valueOf(id)))
				throw new RuntimeException("Duplicate potion ID for: " + elixir.getEffectName() + " and " + usedIDs.get(Integer.valueOf(id)).getEffectName() + " ID: " + id);
			usedIDs.put(id, elixir);
			potionIDs.put(elixir.getID(), id);
		}

		useBLMainMenu = config.getBoolean("Betweenlands Main Menu", CATEGORIES[2], true, "If true, the main menu will be replaced by the Betweenlands main menu");

		maxEntitiesPerLoadedArea = config.get(CATEGORIES[5], "Max. entities per loaded area", 100, "The maximum amount of naturally spawned entities per loaded area (in most cases per player)").setMinValue(0).getInt(100);
		hardEntityLimit = config.get(CATEGORIES[5], "Max. entities per world", 600, "The maximum amount of naturally spawned entities per world").setMinValue(0).getInt(600);

		save();
	}

	public void save() {
		if (config.hasChanged()) {
			config.save();
		}
	}

	public static void userRecipes() {
		File file = new File(path.replace("thebetweenlands.cfg", "thebetweenlands/recipes.json"));
		try {
			if(file.createNewFile()) {
				PrintWriter writer = new PrintWriter(file);
				writer.println("{");
				writer.println("}");
				writer.close();
				System.out.println("Created new file");
			}
			JsonReader jsonReader = new JsonReader(new FileReader(file));

			jsonReader.beginObject();
			ConfigRecipe.readJson(jsonReader);
			jsonReader.endObject();

			jsonReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.modID.equals(ModInfo.ID)) {
			syncConfigs();
		}
	}
}
