package thebetweenlands.common.config;

import java.io.File;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import thebetweenlands.common.config.properties.IntSetProperty;
import thebetweenlands.common.config.properties.ItemListProperty;
import thebetweenlands.common.config.properties.PortalDimensionWhitelist;
import thebetweenlands.common.config.properties.PortalTargetList;
import thebetweenlands.common.config.properties.StringSetProperty;
import thebetweenlands.common.lib.ModInfo;

@Config(modid = ModInfo.ID, category = "", name = ModInfo.ID + "/config")
public class BetweenlandsConfig {
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
		@Ignore
		public final IntSetProperty portalDimensionWhitelistSet = new PortalDimensionWhitelist();

		@Name("portal_default_return_dimension")
		@LangKey(LANG_PREFIX + "portal_default_return_dimension")
		@Comment("The return dimension that is used when creating a portal in the Betweenlands dimension and entering it for the first time")
		public int portalDefaultReturnDimension = 0;

		@Name("portal_max_link_dist")
		@LangKey(LANG_PREFIX + "portal_max_link_dist")
		@Comment("The maximum link distance in blocks between two portals")
		public int portalMaxLinkDist = 1500;

		@Name("activate_rift_on_first_join")
		@LangKey(LANG_PREFIX + "activate_rift_on_first_join")
		@Comment("If true, the Rift will appear whenever a player joins the dimension for the first time")
		public boolean activateRiftOnFirstJoin = true;

		@Name("activate_rift_on_first_join_duration")
		@LangKey(LANG_PREFIX + "activate_rift_on_first_join_duration")
		@Comment("Minimum duration in seconds the rift should stay active when a player joins the dimension for the first time. See 'Activate Rift On First Join'")
		@RangeInt(min = 0, max = Integer.MAX_VALUE / 20)
		public int minRiftOnFirstJoinDuration = 1800;

		@Name("start_in_betweenlands")
		@LangKey(LANG_PREFIX + "start_in_betweenlands")
		@Comment("If true, the players will directly start out in the Betweenlands instead of the Overworld")
		public boolean startInBetweenlands = false;

		@Name("start_in_portal")
		@LangKey(LANG_PREFIX + "start_in_portal")
		@Comment("If \"Start in Betweenlands\" is enabled: whether a portal should be generated at the spawn and the players should spawn in it")
		public boolean startInPortal = false;

		@Name("portal_dimension_targets")
		@LangKey(LANG_PREFIX + "portal_dimension_targets")
		@Comment("Custom saplings or blocks can be specified here to work with the Swamp Talisman. Syntax is \"modid:blockname:meta/dim\", meta can be * for wildcard, if no meta is provided 0 is used. The dimension (\"dim\") specifies to which dimension the portal will lead to")
		public String[] portalDimensionTargets = {};
		@Ignore
		public final PortalTargetList portalDimensionTargetsList = new PortalTargetList();

		@Name("portal_unsafe_biomes")
		@LangKey(LANG_PREFIX + "portal_unsafe_biomes")
		@Comment("A list of unsafe biomes for the portal to try avoid generating in")
		public String[] portalUnsafeBiomes = {
				"minecraft:ocean", "minecraft:river", "minecraft:frozen_ocean", "minecraft:frozen_river",
				"minecraft:mushroom_island_shore", "minecraft:beaches", "minecraft:deep_ocean",
				"minecraft:stone_beach", "minecraft:cold_beach"
		};
		@Ignore
		public final StringSetProperty portalUnsafeBiomesSet = new StringSetProperty(() -> this.portalUnsafeBiomes);
		
		@Name("portal_biome_search_range")
		@LangKey(LANG_PREFIX + "portal_biome_search_range")
		@Comment("The biome search range used to find a suitable biome when a portal is generated. If you find that a suitable biome isn't found reliably enough this can be increased at the cost of taking more time to generate portals")
		@RangeInt(min = 16, max = Integer.MAX_VALUE)
		public int portalBiomeSearchRange = 256;
		
		@Name("generate_portal_in_end")
		@LangKey(LANG_PREFIX + "generate_portal_in_end")
		@Comment("Whether custom portals that teleport to the End should create a Portal Tree in the End")
		public boolean generatePortalInEnd = false;
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

		@Name("shader_priority")
		@LangKey(LANG_PREFIX + "shader_priority")
		@Comment("Shader rendering priority. Some other mods are not compatible with Betweenlands shaders (e.g. LadyLib/Dissolution) due to order conflicts. If you run into such an incompatibility try using a different priority. However rendering the shader earlier may introduce other rendering artifacts so this should be left as default if there are no conflicts.\n"
				+ "0: Late (Default)\n"
				+ "1: Normal\n"
				+ "2: Early")
		@RangeInt(min = 0, max = 2)
		public int shaderPriority = 0;
		
		@Name("dimension_shader_only")
		@LangKey(LANG_PREFIX + "dimension_shader_only")
		@Comment("Whether the shaders should only be active in the Betweenlands dimension (if they are enabled)")
		public boolean dimensionShaderOnly = false;

		@Name("fullbright_blocks")
		@LangKey(LANG_PREFIX + "fullbright_blocks")
		@Comment("Some blocks glow in the dark (eg Life Crystal Ore) which doesn't work in some cases. If you run into problems like broken textures for such blocks then set this to false")
		@RequiresMcRestart
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
		@Comment("If true the food sickness system will be enabled in the Betweenlands")
		public boolean useFoodSicknessInBetweenlands = true;

		@Name("use_food_sickness_outside_betweenlands")
		@LangKey(LANG_PREFIX + "use_food_sickness_outside_betweenlands")
		@Comment("If true the food sickness system will be enabled outside the Betweenlands")
		public boolean useFoodSicknessOutsideBetweenlands = false;
		
		@Name("reverse_rotten_food")
		@LangKey(LANG_PREFIX + "reverse_rotten_food")
		@Comment("Whether rotten food should turn back into normal food when leaving the dimension")
		public boolean reverseRottenFood = true;

		@Name("use_rotten_food")
		@LangKey(LANG_PREFIX + "use_rotten_food")
		@Comment("Whether food from the overworld should rot when going into the dimension")
		public boolean useRottenFood = true;

		@Name("use_decay")
		@LangKey(LANG_PREFIX + "use_decay")
		@Comment("Whether the player decay mechanics should be active in the dimension")
		public boolean useDecay = true;
		
		@Name("use_corrosion")
		@LangKey(LANG_PREFIX + "use_corrosion")
		@Comment("Whether the tool corrosion mechanics should be active in the dimension")
		public boolean useCorrosion = true;
		
		@Name("use_tool_weakness")
		@LangKey(LANG_PREFIX + "use_tool_weakness")
		@Comment("Whether overworld tools should be weakened in the dimension")
		public boolean useToolWeakness = true;
		
		@Name("use_torch_blacklist")
		@LangKey(LANG_PREFIX + "use_torch_blacklist")
		@Comment("Whether torches should turn into damp torches in the dimension")
		public boolean useTorchBlacklist = true;
		
		@Name("use_fire_tool_blacklist")
		@LangKey(LANG_PREFIX + "use_fire_tool_blacklist")
		@Comment("Whether fire tools (e.g. Flint and Steel) should be blacklisted from working in the dimension")
		public boolean useFireToolBlacklist = true;
		
		@Name("use_potion_blacklist")
		@LangKey(LANG_PREFIX + "use_potion_blacklist")
		@Comment("Whether potions should turn into tainted potions in the dimension")
		public boolean usePotionBlacklist = true;
		
		@Name("use_fertilizer_blacklist")
		@LangKey(LANG_PREFIX + "use_fertilizer_blacklist")
		@Comment("Whether fertilizers (e.g. Bonemeal) should be blacklisted from working in the dimension")
		public boolean useFertilizerBlacklist = true;
		
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

		@Name("rotten_food_whitelist")
		@LangKey(LANG_PREFIX + "rotten_food_whitelist")
		@Comment("A list of items that should be whitelisted from rotting in the dimension. Syntax is \"modid:itemname:meta\", meta can be * for wildcard, if no meta is provided 0 is used")
		public String[] rottenFoodWhitelistUnparsed = {};
		@Ignore
		public final ItemListProperty rottenFoodWhitelist = new ItemListProperty(() -> GENERAL.rottenFoodWhitelistUnparsed);

		@Name("rotten_food_blacklist")
		@LangKey(LANG_PREFIX + "rotten_food_blacklist")
		@Comment("A list of items that should turn into Rotten Food in the dimension. Syntax is \"modid:itemname:meta\", meta can be * for wildcard, if no meta is provided 0 is used")
		public String[] rottenFoodBlacklistUnparsed = {};
		@Ignore
		public final ItemListProperty rottenFoodBlacklist = new ItemListProperty(() -> GENERAL.rottenFoodBlacklistUnparsed);

		@Name("tainting_whitelist")
		@LangKey(LANG_PREFIX + "tainting_whitelist")
		@Comment("A list of items that should be whitelisted from turning into Tainted Potions in the dimension. Syntax is \"modid:itemname:meta\", meta can be * for wildcard, if no meta is provided 0 is used")
		public String[] taintingWhitelistUnparsed = {};
		@Ignore
		public final ItemListProperty taintingWhitelist = new ItemListProperty(() -> GENERAL.taintingWhitelistUnparsed);

		@Name("tainting_blacklist")
		@LangKey(LANG_PREFIX + "tainting_blacklist")
		@Comment("A list of items that should turn into Tainted Potions in the dimension. Syntax is \"modid:itemname:meta\", meta can be * for wildcard, if no meta is provided 0 is used")
		public String[] taintingBlacklistUnparsed = {};
		@Ignore
		public final ItemListProperty taintingBlacklist = new ItemListProperty(() -> GENERAL.taintingBlacklistUnparsed);

		@Name("fire_tool_whitelist")
		@LangKey(LANG_PREFIX + "fire_tool_whitelist")
		@Comment("A list of items that should be allowed to create fire in the dimension. Syntax is \"modid:itemname:meta\", meta can be * for wildcard, if no meta is provided 0 is used")
		public String[] fireToolWhitelistUnparsed = {};
		@Ignore
		public final ItemListProperty fireToolWhitelist = new ItemListProperty(() -> GENERAL.fireToolWhitelistUnparsed);

		@Name("fire_tool_blacklist")
		@LangKey(LANG_PREFIX + "fire_tool_blacklist")
		@Comment("A list of items that should not be able to create fire in the dimension. Syntax is \"modid:itemname:meta\", meta can be * for wildcard, if no meta is provided 0 is used")
		public String[] fireToolBlacklistUnparsed = {};
		@Ignore
		public final ItemListProperty fireToolBlacklist = new ItemListProperty(() -> GENERAL.fireToolBlacklistUnparsed);

		@Name("fertilizer_whitelist")
		@LangKey(LANG_PREFIX + "fertilizer_whitelist")
		@Comment("A list of items that should be allowed to fertilize plants in the dimension. Syntax is \"modid:itemname:meta\", meta can be * for wildcard, if no meta is provided 0 is used")
		public String[] fertilizerWhitelistUnparsed = {};
		@Ignore
		public final ItemListProperty fertilizerWhitelist = new ItemListProperty(() -> GENERAL.fertilizerWhitelistUnparsed);

		@Name("fertilizer_blacklist")
		@LangKey(LANG_PREFIX + "fertilizer_blacklist")
		@Comment("A list of items that should not be able to fertilize plants in the dimension. Syntax is \"modid:itemname:meta\", meta can be * for wildcard, if no meta is provided 0 is used")
		public String[] fertilizerBlacklistUnparsed = {};
		@Ignore
		public final ItemListProperty fertilizerBlacklist = new ItemListProperty(() -> GENERAL.fertilizerBlacklistUnparsed);

		@Name("tool_weakness_whitelist")
		@LangKey(LANG_PREFIX + "tool_weakness_whitelist")
		@Comment("A list of items that should not be weakened in the dimension. Syntax is \"modid:itemname:meta\", meta can be * for wildcard, if no meta is provided 0 is used")
		public String[] toolWeaknessWhitelistUnparsed = {};
		@Ignore
		public final ItemListProperty toolWeaknessWhitelist = new ItemListProperty(() -> GENERAL.toolWeaknessWhitelistUnparsed);

		@Name("tool_weakness_blacklist")
		@LangKey(LANG_PREFIX + "tool_weakness_blacklist")
		@Comment("A list of items that should be weakened in the dimension. Syntax is \"modid:itemname:meta\", meta can be * for wildcard, if no meta is provided 0 is used")
		public String[] toolWeaknessBlacklistUnparsed = {};
		@Ignore
		public final ItemListProperty toolWeaknessBlacklist = new ItemListProperty(() -> GENERAL.toolWeaknessBlacklistUnparsed);

		@Name("torch_whitelist")
		@LangKey(LANG_PREFIX + "torch_whitelist")
		@Comment("A list of items that should not turn into damp torches when placed in the dimension. Syntax is \"modid:itemname:meta\", meta can be * for wildcard, if no meta is provided 0 is used")
		public String[] torchWhitelistUnparsed = {};
		@Ignore
		public final ItemListProperty torchWhitelist = new ItemListProperty(() -> GENERAL.torchWhitelistUnparsed);

		@Name("torch_blacklist")
		@LangKey(LANG_PREFIX + "torch_blacklist")
		@Comment("A list of items that should turn into damp torches when placed in the dimension. Syntax is \"modid:itemname:meta\", meta can be * for wildcard, if no meta is provided 0 is used")
		public String[] torchBlacklistUnparsed = {};
		@Ignore
		public final ItemListProperty torchBlacklist = new ItemListProperty(() -> GENERAL.torchBlacklistUnparsed);

		@Name("pouch_blacklist")
		@LangKey(LANG_PREFIX + "pouch_blacklist")
		@Comment("A list of items that should not be allowed in the lurker skin pouch. Syntax is \"modid:itemname:meta\", meta can be * for wildcard, if no meta is provided 0 is used")
		public String[] pouchBlackListUnparsed = {};
		@Ignore
		public final ItemListProperty pouchBlacklist = new ItemListProperty(() -> GENERAL.pouchBlackListUnparsed);

		@Name("caving_rope_despawn_time")
		@LangKey(LANG_PREFIX + "caving_rope_despawn_time")
		@Comment("After how many seconds caving rope should despawn after the player is no longer connected")
		@RangeInt(min = 0, max = Integer.MAX_VALUE / 20)
		public int cavingRopeDespawnTime = 1200;
		
		@Name("equipment_hotbar_side")
		@LangKey(LANG_PREFIX + "equipment_hotbar_side")
		@Comment("Change this to 1 or 0 to change on which side of the hotbar the equipment is shown")
		@RangeInt(min = 0, max = 1)
		public int equipmentHotbarSide = 0;
		
		@Name("equipment_visible")
		@LangKey(LANG_PREFIX + "equipment_visible")
		@Comment("Whether equipment should be shown on the HUD")
		public boolean equipmentVisible = true;
		
		@Name("equipment_horziontal_spacing")
		@LangKey(LANG_PREFIX + "equipment_horziontal_spacing")
		@Comment("Horizontal spacing between equipment items")
		@RangeInt(min = -32, max = 32)
		public int equipmentHorizontalSpacing = 8;
		
		@Name("equipment_vertical_spacing")
		@LangKey(LANG_PREFIX + "equipment_vertical_spacing")
		@Comment("Vertical spacing between equipment items")
		@RangeInt(min = -32, max = 32)
		public int equipmentVerticalSpacing = -13;
		
		@Name("equipment_zone")
		@LangKey(LANG_PREFIX + "equipment_zone")
		@Comment("Zone in which the equipment items are shown on the HUD.\n"
				+ "0: Hotbar\n"
				+ "1: Top left\n"
				+ "2: Top right\n"
				+ "3: Bottom right\n"
				+ "4: Bottom left\n"
				+ "5: Center left\n"
				+ "6: Center top\n"
				+ "7: Center right\n"
				+ "8: Center bottom")
		@RangeInt(min = 0, max = 8)
		public int equipmentZone = 0;
		
		@Name("equipment_zone_offset_x")
		@LangKey(LANG_PREFIX + "equipment_zone_offset_x")
		@Comment("X offset of the equipment items relative to the equipment zone")
		public int equipmentOffsetX = 0;
		
		@Name("equipment_zone_offset_y")
		@LangKey(LANG_PREFIX + "equipment_zone_offset_y")
		@Comment("Y offset of the equipment items relative to the equipment zone")
		public int equipmentOffsetY = 0;
		
		@Name("decay_bar_zone")
		@LangKey(LANG_PREFIX + "decay_bar_zone")
		@Comment("Zone in which the decay bar is shown on the HUD.\n"
				+ "0: Hotbar\n"
				+ "1: Top left\n"
				+ "2: Top right\n"
				+ "3: Bottom right\n"
				+ "4: Bottom left\n"
				+ "5: Center left\n"
				+ "6: Center top\n"
				+ "7: Center right\n"
				+ "8: Center bottom")
		@RangeInt(min = 0, max = 8)
		public int decayBarZone = 0;
		
		@Name("decay_bar_zone_offset_x")
		@LangKey(LANG_PREFIX + "decay_bar_zone_offset_x")
		@Comment("X offset of the decay bar relative to the decay zone")
		public int decayBarOffsetX = 0;
		
		@Name("decay_bar_zone_offset_y")
		@LangKey(LANG_PREFIX + "decay_bar_zone_offset_y")
		@Comment("Y offset of the decay bar relative to the decay zone")
		public int decayBarOffsetY = 0;
		
		@Name("decay_percentage")
		@LangKey(LANG_PREFIX + "decay_percentage")
		@Comment("Whether the decay health reduction should be percentual")
		public boolean decayPercentual = false;
		
		@Name("decay_min_health")
		@LangKey(LANG_PREFIX + "decay_min_health")
		@Comment("Minimum player health when absolute decay is applied")
		@RangeDouble(min = 0, max = Float.MAX_VALUE)
		public float decayMinHealth = 6.0f;
		
		@Name("decay_min_health_percent")
		@LangKey(LANG_PREFIX + "decay_min_health_percent")
		@Comment("Minimum player health percentage when percentual decay is applied")
		@RangeDouble(min = 0, max = Float.MAX_VALUE)
		public float decayMinHealthPercentage = 0.15f;
		
		@Name("item_usage_tooltip")
		@LangKey(LANG_PREFIX + "item_usage_tooltip")
		@Comment("Whether the item usage tooltip (\"Used In: Infuser, Compost Bin\" etc.) should be shown")
		public boolean itemUsageTooltip = true;
		
		@Name("online_gallery")
		@LangKey(LANG_PREFIX + "online_gallery")
		@Comment("Whether the online fan art gallery picture frame should be enabled and be allowed to download fan art that has been manually picked by the developers to be shown in the gallery picture frame")
		public boolean onlineGallery = false;
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
		public int checkInterval = 1800;

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
		public boolean debug = ModInfo.IDE;

		@Name("debug_model_loader")
		@LangKey(LANG_PREFIX + "debug_model_loader")
		@Comment("If true, enables the model loader debug logger")
		public boolean debugModelLoader = false;

		@Name("debug_recipe_overrides")
		@LangKey(LANG_PREFIX + "debug_recipe_overrides")
		@Comment("If true, enables the recipe overrides debug logger")
		public boolean debugRecipeOverrides = false;
		
		@Name("dump_packed_textures")
		@LangKey(LANG_PREFIX + "dump_packed_textures")
		@Comment("If true, mod will dump the packed model textures on startup")
		public boolean dumpPackedTextures = false;
	}
}
