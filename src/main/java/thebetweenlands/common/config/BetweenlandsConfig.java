package thebetweenlands.common.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.remapper.ConfigRemapper;
import thebetweenlands.common.config.remapper.Remapper1;
import thebetweenlands.common.lib.ModInfo;

@Config(modid = ModInfo.ID, category = "", name = ModInfo.ID + "/config")
public class BetweenlandsConfig {
	/**
	 * Config loaded before any default values are generated
	 */
	@Nullable
	@Ignore
	public static Configuration loadedConfig;

	@Ignore
	public static File configDir;

	@Ignore
	private static final String LANG_PREFIX = "config." + ModInfo.ID + ".";

	@Name("world_and_dimension")
	@LangKey(LANG_PREFIX + "world_and_dimension")
	public static final WorldAndDimension WORLD_AND_DIMENSION = new WorldAndDimension();

	public static class WorldAndDimension {
		@Name("dimension_id")
		@LangKey(LANG_PREFIX + "dimension_id")
		@RequiresMcRestart
		public int dimensionId = 20;

		@Name("druid_circle_frequency")
		@LangKey(LANG_PREFIX + "druid_circle_frequency")
		@Comment("Higher numbers decrease rate")
		@RangeInt(min = 2)
		public int druidCircleFrequency = 80;

		@Name("dimension_brightness")
		@LangKey(LANG_PREFIX + "dimension_brightness")
		@RangeInt(min = 0, max = 100)
		public int dimensionBrightness = 75;

		@Name("enable_seasonal_events")
		@LangKey(LANG_PREFIX + "enable_seasonal_events")
		@Comment("If true seasonal events will occur during special periods during a year")
		public boolean enableSeasonalEvents = true;

		@Name("portal_dimension_whitelist")
		@LangKey(LANG_PREFIX + "portal_dimension_whitelist")
		@Comment("Betweenlands portals will only work in these dimensions")
		public int[] portalDimensionWhitelist = {0, -1};

		@Name("portal_default_return_dimension")
		@LangKey(LANG_PREFIX + "portal_default_return_dimension")
		@Comment("The return dimension that is used when creating a portal in the Betweenlands dimension and entering it for the first time")
		public int portalDefaultReturnDimension = 0;

		@Name("activate_rift_on_first_join")
		@LangKey(LANG_PREFIX + "activate_rift_on_first_join")
		@Comment("If true, the Rift will appear whenever a player joins the dimension for the first time")
		public boolean activateRiftOnFirstJoin = true;

		@Name("activate_rift_on_first_join_duration")
		@LangKey(LANG_PREFIX + "activate_rift_on_first_join_duration")
		@Comment("Minimum duration in seconds the rift should stay active when a player joins the dimension for the first time. See 'Activate Rift On First Join'")
		@RangeInt(min = 0, max = Integer.MAX_VALUE / 20)
		public int minRiftOnFirstJoinDuration = 1800;
	}

	@Name("rendering")
	@LangKey(LANG_PREFIX + "rendering")
	public static final Rendering RENDERING = new Rendering();

	public static class Rendering {
		@Name("wisp_quality")
		@LangKey(LANG_PREFIX + "wisp_quality")
		@RangeInt(min = 0, max = 100)
		public int wispQuality = 50;

		@Name("use_shader")
		@LangKey(LANG_PREFIX + "use_shader")
		@Comment("Some features in the Betweenlands use shaders for special effects. If you don't have a dedicated graphics card or want to use other mods with shaders you should set this to false. May have an impact on performance depending on your computer. Forces FBOs to be enabled")
		public boolean useShader = true;

		@Name("fullbright_blocks")
		@LangKey(LANG_PREFIX + "fullbright_blocks")
		@Comment("Some blocks glow in the dark (eg Life Crystal Ore) which doesn't work in some cases. If you run into problems like broken textures for such blocks then set this to false")
		@RequiresWorldRestart
		public boolean fullbrightBlocks = true;

		@Name("sky_texture_resolution")
		@LangKey(LANG_PREFIX + "sky_texture_resolution")
		@Comment("Only works when shaders are enabled. Determines the resolution of the shader sky texture. Bigger resolutions may have a bad impact on performance")
		@RangeInt(min = 16)
		@RequiresMcRestart
		public int skyResolution = 1024;

		@Name("sky_rift_clouds")
		@LangKey(LANG_PREFIX + "sky_rift_clouds")
		@Comment("Whether clouds should be rendered in sky rifts")
		public boolean skyRiftClouds = true;
	}

	@Name("general")
	@LangKey(LANG_PREFIX + "general")
	public static final General GENERAL = new General();

	public static class General {
		@Name("bl_main_menu")
		@LangKey(LANG_PREFIX + "bl_main_menu")
		@Comment("If true, the main menu will be replaced by the Betweenlands main menu")
		public boolean blMainMenu = true;

		@Name("rowboat_view")
		@LangKey(LANG_PREFIX + "rowboat_view")
		@Comment("If true, the camera perspective will be switch to rowboat when you enter a rowboat, otherwise first-person")
		public boolean rowboatView = true;

		@Name("use_food_sickness")
		@LangKey(LANG_PREFIX + "use_food_sickness")
		@Comment("If true the food sickness system will be enabled")
		public boolean useFoodSickness = true;

		@Name("rotten_food_whitelist")
		@LangKey(LANG_PREFIX + "rotten_food_whitelist")
		@Comment("A list of items that should be whitelisted from rotting in the dimension. Syntax is \"modid:itemname:meta\", meta can be * for wildcard, if no meta is provided 0 is used")
		public String[] rottenFoodWhitelistUnparsed = {};

		@Name("reverse_rotten_food")
		@LangKey(LANG_PREFIX + "reverse_rotten_food")
		@Comment("Whether rotten food should turn back into normal food when leaving the dimension")
		public boolean reverseRottenFood = true;
		
		@Name("use_rotten_food")
		@LangKey(LANG_PREFIX + "use_rotten_food")
		@Comment("Whether food from the overworld should rot when going into the dimension")
		public boolean useRottenFood = true;
		
		@Name("caving_rope_indicator")
		@LangKey(LANG_PREFIX + "caving_rope_indicator")
		@Comment("Adds an indicator next to the crosshair that shows whether the player is connected to the caving rope and how much rope is left")
		public boolean cavingRopeIndicator = true;

		@Name("override_conflicting_vanilla_recipes")
		@LangKey(LANG_PREFIX + "override_conflicting_vanilla_recipes")
		@Comment("If true, Betweenlands recipes that conflict with oredict'd vanilla recipes will take priority over the vanilla recipes (should be true unless you intend to fix the recipes yourself with another mod)")
		@RequiresMcRestart
		public boolean overrideConflictingVanillaRecipes = true;

		@Name("override_any_conflicting_recipes")
		@LangKey(LANG_PREFIX + "override_any_conflicting_recipes")
		@Comment("If true, Betweenlands recipes that conflict with any oredict'd recipes will take priority over the oredict'd recipes (should be true unless you intend to fix the recipes yourself with another mod)")
		@RequiresMcRestart
		public boolean overrideAnyConflictingRecipes = true;
	}

	@Name("mob_spawning")
	@LangKey(LANG_PREFIX + "mob_spawning")
	public static final MobSpawning MOB_SPAWNING = new MobSpawning();

	public static class MobSpawning {
		@Name("max_entities_per_loaded_area")
		@LangKey(LANG_PREFIX + "max_entities_per_loaded_area")
		@Comment("The maximum amount of naturally spawned entities per loaded area (in most cases this means per player)")
		@RangeInt(min = 0)
		public int maxEntitiesPerLoadedArea = 250;

		@Name("hard_entity_limit")
		@LangKey(LANG_PREFIX + "hard_entity_limit")
		@Comment("The maximum amount of naturally spawned entities in the Betweenlands per world")
		@RangeInt(min = 0)
		public int hardEntityLimit = 600;
	}

	@Name("compatibility")
	@LangKey(LANG_PREFIX + "compatibility")
	public static final Compatibility COMPATIBILITY = new Compatibility();

	public static class Compatibility {
		@Name("show_non_bl_fluids")
		@LangKey(LANG_PREFIX + "show_non_bl_fluids")
		@Comment("If fluids from other mods should show in BL buckets in the creative tab or in JEI")
		public boolean showNonBLFluids = true;

		@Name("show_non_bl_gem_recipes")
		@LangKey(LANG_PREFIX + "show_non_bl_gem_recipes")
		@Comment("If true, non BL items will show in the JEI recipe for middle gems")
		public boolean showNonBLGemRecipes = true;
	}

	@Name("online_event_overrides")
	@LangKey(LANG_PREFIX + "online_event_overrides")
	public static final EventOverrides EVENT_OVERRIDES = new EventOverrides();

	public static class EventOverrides {
		@Name("online_event_overrides_enabled")
		@LangKey(LANG_PREFIX + "online_event_overrides_enabled")
		@Comment("If true this allows the developers to remotely enable certain environment events (such as the seasonal events for example) over a file hosted on our repository (https://raw.githubusercontent.com/Angry-Pixel/The-Betweenlands/environment_event_overrides/overrides.json). If you do not wish to use this feature it can be fully disabled by setting this to false")
		public boolean onlineEnvironmentEventOverrides = true;

		@Name("check_interval")
		@LangKey(LANG_PREFIX + "check_interval")
		@Comment("Check interval in seconds")
		@RangeInt(min = 60, max = Integer.MAX_VALUE / 20)
		public int checkInterval = 600;

		@Name("failed_recheck_interval")
		@LangKey(LANG_PREFIX + "failed_recheck_interval")
		@Comment("Recheck interval in seconds if previous check has failed")
		@RangeInt(min = 10, max = Integer.MAX_VALUE / 20)
		public int failedRecheckInterval = 60;

		@Name("failed_recheck_count")
		@LangKey(LANG_PREFIX + "failed_recheck_count")
		@Comment("How many times a connection can fail before the '(Default) Remote Reset Time' starts counting down and how many times 'Failed Recheck Interval' is used before falling back to 'Check Interval'")
		@RangeInt(min = 0)
		public int failedRecheckCount = 3;

		@Name("default_remote_reset_time")
		@LangKey(LANG_PREFIX + "default_remote_reset_time")
		@Comment("Default time in seconds before an event that no longer has an override resets its state")
		@RangeInt(min = 0, max = Integer.MAX_VALUE / 20)
		public int defaultRemoteResetTime = 180;
	}

	@Name("debug")
	@LangKey(LANG_PREFIX + "debug")
	public static final Debug DEBUG = new Debug();

	public static class Debug {
		@Name("debug_mode")
		@LangKey(LANG_PREFIX + "debug_mode")
		@Comment("If true, enables debug mode with additional features for testing or development")
		public boolean debug = /*!*/true/*!*/;

		@Name("debug_model_loader")
		@LangKey(LANG_PREFIX + "debug_model_loader")
		@Comment("If true, enables the model loader debug logger")
		public boolean debugModelLoader = false;

		@Name("debug_recipe_overrides")
		@LangKey(LANG_PREFIX + "debug_recipe_overrides")
		@Comment("If true, enables the recipe overrides debug logger")
		public boolean debugRecipeOverrides = false;
	}

	@SubscribeEvent
	public static void onConfigChanged(OnConfigChangedEvent event) {
		if (ModInfo.ID.equals(event.getModID())) {
			ConfigManager.sync(ModInfo.ID, Config.Type.INSTANCE);

			parseFoodWhitelist(GENERAL.rottenFoodWhitelistUnparsed);
		}
	}

	public static void init() {
		final File versionFile = new File(configDir, "config_version");

		Configuration newConfig = new Configuration(new File(BetweenlandsConfig.configDir, "config.cfg"));
		newConfig.load();

		Configuration oldConfig;
		String configVersion = null;

		if(loadedConfig == null) {
			configVersion = ModInfo.CONFIG_VERSION;
			oldConfig = new Configuration(new File(BetweenlandsConfig.configDir, "config.cfg"));
			oldConfig.load();
		} else {
			if(versionFile.exists()) {
				try {
					configVersion = FileUtils.readFileToString(versionFile, (Charset) null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			oldConfig = loadedConfig;
		}

		ConfigRemapper.register(new Remapper1());

		Pair<Configuration, String> remap = ConfigRemapper.remap(oldConfig, newConfig, configVersion);
		if(remap != null) {
			//Create backup of old config values
			final File backupFile = new File(oldConfig.getConfigFile().getParentFile(), "config (" + (configVersion == null ? "no version" : configVersion) + ").cfg.backup");
			Configuration backup = ConfigRemapper.clear(new Configuration(backupFile, oldConfig.getDefinedConfigVersion()));
			ConfigRemapper.copy(oldConfig, backup);
			backup.save();

			remap.getKey().save();
			reloadConfig();
		}

		try {
			FileUtils.writeStringToFile(versionFile, ModInfo.CONFIG_VERSION, (Charset) null, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void reloadConfig() {
		try {
			Field configCacheField = ConfigManager.class.getDeclaredField("CONFIGS");
			configCacheField.setAccessible(true);

			@SuppressWarnings("unchecked")
			Map<String, Configuration> configCache = (Map<String, Configuration>) configCacheField.get(null);

			configCache.put(new File(BetweenlandsConfig.configDir, "config.cfg").getAbsolutePath(), null);

			ConfigManager.sync(ModInfo.ID, Config.Type.INSTANCE);

			parseFoodWhitelist(GENERAL.rottenFoodWhitelistUnparsed);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Ignore
	private static Multimap<String, String> rottenFoodWhitelist;

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
		if(rottenFoodWhitelist == null) {
			parseFoodWhitelist(GENERAL.rottenFoodWhitelistUnparsed);
		}

		if (stack.isEmpty()) {
			return false;
		}

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

	public static boolean isDimensionPortalWhitelisted(int dim) {
		if(dim == WORLD_AND_DIMENSION.dimensionId) {
			return true;
		}
		for(int whitelisted : WORLD_AND_DIMENSION.portalDimensionWhitelist) {
			if(whitelisted == dim) {
				return true;
			}
		}
		return false;
	}
}
