package thebetweenlands.util.config;

import java.io.File;
import java.util.Collection;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.lib.ModInfo;

public class ConfigHandler {
	public static final ConfigHandler INSTANCE = new ConfigHandler();
	public static final String[] CATEGORIES = {"World and Dimension", "Rendering", "General", "Mob Spawning", "Debug", "Compat", "Online Environment Event Overrides"};

	//////// Values ///////
	public static int dimensionId;
	public static int druidCircleFrequency;
	public static int dimensionBrightness;
	public static boolean enableSeasonalEvents;

	public static int wispQuality;
	public static boolean useShader;
	public static int skyResolution;
	public static boolean fullbrightBlocks;

	public static boolean debug;
	public static boolean debugModelLoader;

	public static boolean rowboatView;
	public static boolean blMainMenu;
	public static boolean useFoodSickness;
	private static Multimap<String, String> rottenFoodWhitelist;
	public static boolean cavingRopeIndicator;
	public static boolean showNonBLFuids;

	public static int maxEntitiesPerLoadedArea;
	public static int hardEntityLimit;

	public static boolean jeiGemRecipesNonBL;

	public static boolean onlineEnvironmentEventOverrides;
	public static int checkIntervalTicks;
	public static int failedRecheckIntervalTicks;
	public static int failedRecheckCount;
	public static int defaultRemoteResetTicks;

	public static boolean overrideConflictingRecipes;
	
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
		druidCircleFrequency = config.get(CATEGORIES[0], "Frequency of Druid Circles", 80, "Higher numbers decrease rate").getInt(80);
		dimensionBrightness = config.get(CATEGORIES[0], "Dimension brightness (0-100)", 75).setMinValue(0).setMaxValue(100).getInt(75);
		enableSeasonalEvents = config.getBoolean("Enable Seasonal Events", CATEGORIES[0], true, "If true seasonal events will occur during special periods during a year");

		wispQuality = config.get(CATEGORIES[1], "Wisp Rendering Quality (0-100)", 50).setMinValue(0).setMaxValue(100).getInt(100);
		useShader = config.getBoolean("Use shaders for rendering", CATEGORIES[1], true, "Some features in the Betweenlands use shaders for special effects. If you don't have a dedicated graphics card or want to use other mods with shaders you should set this to false. May have an impact on performance depending on your computer. Forces FBOs to be enabled");
		skyResolution = config.get(CATEGORIES[1], "Sky texture resolution", 1024, "Only works when shaders are enabled. Determines the resolution of the shader sky texture. Bigger resolutions may have a bad impact on performance").getInt(1024);
		fullbrightBlocks = config.getBoolean("Full brightness blocks", CATEGORIES[1], true, "Some blocks glow in the dark (eg Life Crystal Ore) which doesn't work in some cases. If you run into problems like broken textures for such blocks then set this to false");

		// Replaced with false by gradle for release version
		debug = config.getBoolean("Debug mode", CATEGORIES[4], /*!*/true/*!*/, "If ture, enables debug mode with additional features for testing or development");
		debugModelLoader = config.getBoolean("Model loader debug", CATEGORIES[4], false, "If true, enables the model loader debug logger");

		blMainMenu = config.getBoolean("Betweenlands Main Menu", CATEGORIES[2], true, "If true, the main menu will be replaced by the Betweenlands main menu");
		rowboatView = config.getBoolean("Rowboat view", CATEGORIES[2], true, "If true, the camera perspective will be switch to rowboat when you enter a rowboat, otherwise first-person");
		useFoodSickness = config.getBoolean("Food Sickness", CATEGORIES[2], true, "If true the food sickness system will be enabled");
		String[] rottenFoodWhitelistUnparsed = config.getStringList("Rotten Food Whitelist", CATEGORIES[2], new String[0], "A list of items that should be whitelisted from rotting in the dimension. Syntax is \"modid:itemname:meta\", meta can be * for wildcard, if no meta is provided 0 is used");
		parseFoodWhitelist(rottenFoodWhitelistUnparsed);
		cavingRopeIndicator = config.getBoolean("Caving Rope Indicator", CATEGORIES[2], true, "Adds an indicator next to the crosshair that shows whether the player is connected to the caving rope and how much rope is left");
		showNonBLFuids = config.getBoolean("Show Non BL Buckets", CATEGORIES[2], true, "If fluids from other mods should show in BL buckets in the creative tab");
		overrideConflictingRecipes = config.getBoolean("Override Conflicting Recipes", CATEGORIES[2], true, "If true, Betweenlands recipes that conflict with oredict'd vanilla recipes will take priority over the vanilla recipes (should be true unless you intend to fix the recipes yourself with another mod)");

		maxEntitiesPerLoadedArea = config.get(CATEGORIES[3], "Max. entities per loaded area", 250, "The maximum amount of naturally spawned entities per loaded area (in most cases this means per player)").setMinValue(0).getInt(100);
		hardEntityLimit = config.get(CATEGORIES[3], "Max. entities per world", 600, "The maximum amount of naturally spawned entities in the Betweenlands per world").setMinValue(0).getInt(600);

		jeiGemRecipesNonBL = config.getBoolean("JEI - Show Non BL Gem Recipes", CATEGORIES[5], true, "If true, non BL items will show in the JEI recipe for middle gems");

		onlineEnvironmentEventOverrides = config.getBoolean("Enabled", CATEGORIES[6], true, "If true this allows the developers to remotely enable certain environment events (such as the seasonal events for example) over a file hosted on our repository (https://raw.githubusercontent.com/Angry-Pixel/The-Betweenlands/environment_event_overrides/overrides.json). If you do not wish to use this feature it can be fully disabled by setting this to false");
		checkIntervalTicks = config.getInt("Check Interval", CATEGORIES[6], 600, 60, Integer.MAX_VALUE / 20, "Check interval in seconds") * 20;
		failedRecheckIntervalTicks = config.getInt("Failed Recheck Interval", CATEGORIES[6], 60, 10, Integer.MAX_VALUE / 20, "Recheck interval in seconds if previous check has failed") * 20;
		failedRecheckCount = config.getInt("Failed Recheck Count", CATEGORIES[6], 3, 0, Integer.MAX_VALUE, "How many times a connection can fail before the '(Default) Remote Reset Time' starts counting down and how many times 'Failed Recheck Interval' is used before falling back to 'Check Interval'");
		defaultRemoteResetTicks = config.getInt("Default Remote Reset Time", CATEGORIES[6], 180, 0, Integer.MAX_VALUE / 20, "Default time in seconds before an event that no longer has an override resets its state") * 20;

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
