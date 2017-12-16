package thebetweenlands.util.config;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.lib.ModInfo;

import java.io.File;
import java.util.Collection;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class ConfigHandler {
	public static final ConfigHandler INSTANCE = new ConfigHandler();
	public static final String[] CATEGORIES = {"World and Dimension", "Rendering", "General", "Mob Spawning"};

	//////// Values ///////
	public static int dimensionId;
	public static int druidCircleFrequency;
	public static int dimensionBrightness;
	public static boolean enableSeasonalEvents;

	public static int wispQuality;
	public static boolean useShader;
	public static int skyResolution;

	public static boolean debug;
	public static boolean debugModelLoader;

	public static boolean rowboatView;
	public static boolean blMainMenu;
	public static boolean useFoodSickness;
	private static Multimap<String, String> rottenFoodWhitelist;

	public static int maxEntitiesPerLoadedArea;
	public static int hardEntityLimit;
	
	public static boolean cavingRopeIndicator;

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
		dimensionBrightness = config.get(CATEGORIES[0], "Dimension brightness (0-100)", 75).setMinValue(0).setMaxValue(100).getInt(75);
		enableSeasonalEvents = config.getBoolean("Enable Seasonal Events", CATEGORIES[0], true, "If true seasonal events will occur during special periods during a year");

		wispQuality = config.get(CATEGORIES[1], "Wisp Rendering Quality (0-100)", 50).setMinValue(0).setMaxValue(100).getInt(100);
		useShader = config.getBoolean("Use shaders for rendering (this forces FBOs to be enabled)", CATEGORIES[1], true, "");
		skyResolution = config.get(CATEGORIES[1], "Sky texture resolution (only when shaders are enabled)", 1024).getInt(1024);

		// Replaced with false by gradle for release version
		debug = config.getBoolean("Debug mode", CATEGORIES[2], /*!*/true/*!*/, "");
		debugModelLoader = config.getBoolean("Model loader debug", CATEGORIES[2], false, "");

		blMainMenu = config.getBoolean("Betweenlands Main Menu", CATEGORIES[2], true, "If true, the main menu will be replaced by the Betweenlands main menu");
		rowboatView = config.getBoolean("Rowboat view", CATEGORIES[2], true, "If true, the camera perspective will be switch to rowboat when you enter a rowboat, otherwise first-person");
		useFoodSickness = config.getBoolean("Food Sickness", CATEGORIES[2], true, "If true the food sickness system will be enabled");
		String[] rottenFoodWhitelistUnparsed = config.getStringList("Rotten Food Whitelist", CATEGORIES[2], new String[0], "A list of items that should be whitelisted from rotting in the dimension. Syntax is \"modid:itemname:meta\", meta can be * for wildcard, if no meta is provided 0 is used");
		parseFoodWhitelist(rottenFoodWhitelistUnparsed);
		cavingRopeIndicator = config.getBoolean("Caving Rope Indicator", CATEGORIES[2], true, "Adds an indicator next to the crosshair that shows whether the player is connected to the caving rope and how much rope is left");
		
		maxEntitiesPerLoadedArea = config.get(CATEGORIES[3], "Max. entities per loaded area", 250, "The maximum amount of naturally spawned entities per loaded area (in most cases per player)").setMinValue(0).getInt(100);
		hardEntityLimit = config.get(CATEGORIES[3], "Max. entities per world", 600, "The maximum amount of naturally spawned entities per world").setMinValue(0).getInt(600);
		
		save();
	}

	public void save() {
		if (config.hasChanged()) {
			config.save();
		}
	}

	private static void parseFoodWhitelist(String[] rottenFoodWhitelistUnparsed) {
		rottenFoodWhitelist = HashMultimap.<String, String>create();
		for(String whitelisted : rottenFoodWhitelistUnparsed) {
			try {
				String[] data = whitelisted.split(":");
				String item = data[0] + ":" + data[1];
				String meta = null;
				if(data.length <= 2) {
					meta = "0";
				} else {
					try {
						meta = String.valueOf(Integer.parseInt(data[2]));
					} catch(NumberFormatException ex) {}
					if("*".equals(data[2])) {
						meta = data[2];
					}
				}
				if(meta == null) {
					TheBetweenlands.logger.error("Failed to parse food whitelist item: " + whitelisted + ". Invalid metadata: " + data[2]);
				} else {
					rottenFoodWhitelist.put(item, meta);
				}
			} catch (Exception e) {
				TheBetweenlands.logger.error("Failed to parse food whitelist item: " + whitelisted);
			}
		}
	}
	
	public static boolean isFoodConfigWhitelisted(ItemStack stack) {
		if (stack.isEmpty())
			return false;

		String stackMeta = String.valueOf(stack.getMetadata());
		ResourceLocation name = stack.getItem().getRegistryName();
		Collection<String> metas = rottenFoodWhitelist.get(name.toString());
		for(String meta : metas) {
			if("*".equals(meta)) {
				return true;
			} else if(meta.equals(stackMeta)) {
				return true;
			}
		}
		
		return false;
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
