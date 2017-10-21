package thebetweenlands.util.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.lib.ModInfo;

public class ConfigHandler {
	public static final ConfigHandler INSTANCE = new ConfigHandler();
	public static final String[] CATEGORIES = {"World and Dimension", "Rendering", "General", "Mob Spawning"};

	//////// Values ///////
	public static int dimensionId;
	public static int druidCircleFrequency;
	public static int biomeIdSwamplands;
	public static int biomeIdCoarseIslands;
	public static int biomeIdDeepWater;
	public static int biomeIdPatchyIslands;
	public static int biomeIdMarsh1;
	public static int biomeIdMarsh2;
	public static int biomeIdSludgePlains;
	public static boolean biomeIdLimit;
	public static int dimensionBrightness;
	public static int wispQuality;
	public static boolean useShader;
	public static boolean debug;
	public static boolean debugModelLoader;
	public static int skyResolution;
	public static boolean rowboatView;
	public static int maxEntitiesPerLoadedArea;
	public static int hardEntityLimit;
	public static boolean blMainMenu;

	public Configuration config;
	public static String path = "";

	public void loadConfig(FMLPreInitializationEvent event) {
		path = event.getSuggestedConfigurationFile().getPath();
		config = new Configuration(new File(path.replace("thebetweenlands.cfg", "thebetweenlands/config.cfg")));
		config.load();
		syncConfigs();
	}

	private void syncConfigs() {
		dimensionId = config.get(CATEGORIES[0], "The Betweenlands Dimension ID", 20).getInt(20);
		druidCircleFrequency = config.get(CATEGORIES[0], "Frequency of Druid Circles. Higher numbers de-crease rate.", 80).getInt(80);
		biomeIdSwamplands = config.get(CATEGORIES[0], "Swamplands Biome ID", 50).getInt(50);
		biomeIdCoarseIslands = config.get(CATEGORIES[0], "Coarse Islands Biome ID", 51).getInt(51);
		biomeIdDeepWater = config.get(CATEGORIES[0], "Deep Water Biome ID", 52).getInt(52);
		biomeIdPatchyIslands = config.get(CATEGORIES[0], "Patchy Islands Biome ID", 53).getInt(53);
		biomeIdMarsh1 = config.get(CATEGORIES[0], "Marsh 1 Biome ID", 54).getInt(54);
		biomeIdMarsh2 = config.get(CATEGORIES[0], "Marsh 2 Biome ID", 55).getInt(55);
		biomeIdSludgePlains = config.get(CATEGORIES[0], "Sludge Plains Biome ID", 56).getInt(56);
		biomeIdLimit = config.getBoolean("Biome ID Limit", CATEGORIES[0], true, "Prevents any biome IDs higher than 127. Setting this to false \nis NOT recommended unless you know what you're doing!");
		dimensionBrightness = config.get(CATEGORIES[0], "Dimension brightness (0-100)", 75).setMinValue(0).setMaxValue(100).getInt(75);

		wispQuality = config.get(CATEGORIES[1], "Wisp Rendering Quality (0-100)", 50).setMinValue(0).setMaxValue(100).getInt(100);
		useShader = config.getBoolean("Use shaders for rendering (this forces FBOs to be enabled)", CATEGORIES[1], true, "");
		skyResolution = config.get(CATEGORIES[1], "Sky texture resolution (only when shaders are enabled)", 1024).getInt(1024);

		// Replaced with false by gradle for release version
		debug = config.getBoolean("Debug mode", CATEGORIES[2], /*!*/true/*!*/, "");
		debugModelLoader = config.getBoolean("Model loader debug", CATEGORIES[2], false, "");

		blMainMenu = config.getBoolean("Betweenlands Main Menu", CATEGORIES[2], true, "If true, the main menu will be replaced by the Betweenlands main menu");
		
		rowboatView = config.getBoolean("Rowboat view", CATEGORIES[2], true, "If true, the camera perspective will be switch to rowboat when you enter a rowboat, otherwise first-person");

		maxEntitiesPerLoadedArea = config.get(CATEGORIES[3], "Max. entities per loaded area", 250, "The maximum amount of naturally spawned entities per loaded area (in most cases per player)").setMinValue(0).getInt(100);
		hardEntityLimit = config.get(CATEGORIES[3], "Max. entities per world", 600, "The maximum amount of naturally spawned entities per world").setMinValue(0).getInt(600);
		
		save();
	}

	public void save() {
		if (config.hasChanged()) {
			config.save();
		}
	}

	/*public static void userRecipes() {
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
	}*/

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(ModInfo.ID)) {
			syncConfigs();
		}
	}
}
