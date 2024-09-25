package thebetweenlands.common.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import thebetweenlands.common.TheBetweenlands;

//TODO cached config values are stored here, make actual config file to allow changing them
// TODO do you think we could get at least a little structure here?
public class BetweenlandsConfig {

	public static final boolean debug = false;
	public static final ResourceKey<Level> returnDimension = Level.OVERWORLD;
	public static final boolean seasonalEvents = true;
	public static final int portalMaxLinkDist = 1500;

	public static boolean activateRiftOnFirstJoin = true;
	public static int minRiftOnFirstJoinDuration = 1800;
	public static boolean startInBetweenlands = false;

	public static boolean startInPortal = false;
	public static int portalBiomeSearchRange = 256;

	public static int wispQuality = 50;
	public static final boolean fullbrightBlocks = true;
	public static boolean useShader = true;
	public static int shaderPriority = 0;
	public static final boolean dimensionShaderOnly = false;
	public static final int skyResolution = 1024;
	public static final boolean skyRiftClouds = true;

	public static final boolean blMainMenu = true;
	public static boolean rowboatView = true;
	public static final boolean useFoodSicknessInBetweenlands = true;
	public static final boolean useFoodSicknessOutsideBetweenlands = false;
	public static boolean reverseRottenFood = true;
	public static boolean useRottenFood = true;
	public static boolean usePotionBlacklist = true;
	public static boolean useFireToolBlacklist = true;
	public static boolean useFertilizerBlacklist = true;
	public static boolean useToolWeakness = true;
	public static boolean useTorchBlacklist = true;
	public static final boolean useDecay = true;
	public static final List<ResourceKey<Level>> decayDimensionList = new ArrayList<>();
	public static final boolean useCorrosion = true;
	public static boolean cavingRopeIndicator = true;

	public static int cavingRopeDespawnTime = 1200;

	public static int equipmentHotbarSide = 0;
	public static boolean equipmentVisible = true;
	public static int equipmentHorizontalSpacing = 8;
	public static int equipmentVerticalSpacing = -13;
	public static int equipmentZone = 0;
	public static int equipmentOffsetX = 0;
	public static int equipmentOffsetY = 0;
	public static int decayBarZone = 0;
	public static int decayBarOffsetX = 0;
	public static int decayBarOffsetY = 0;
	public static final boolean decayPercentual = false;
	public static final float decayMinHealth = 6.0f;
	public static final float decayMinHealthPercentage = 0.15f;
	public static final boolean itemUsageTooltip = true;
	public static boolean onlineGallery = true;

	public static int maxEntitiesPerLoadedArea = 250;
	public static final int hardEntityLimit = 600;
	public static boolean showNonBLFluids = true;
	public static boolean showNonBLGemRecipes = true;
	public static boolean onlineEnvironmentEventOverrides = false;
	public static int checkInterval = 1800;
	public static int failedRecheckInterval = 60;
	public static int failedRecheckCount = 3;
	public static int defaultRemoteResetTime = 180;

	public static boolean debugModelLoader = false;
	public static boolean dumpPackedTextures = false;

//	public static final OverworldItemLists OVERWORLD = new OverworldItemLists();
//	
//	public static final class OverworldItemLists {
	public static final class Overworld {
		public static List<String> rottenFoodWhitelistUnparsed = new ArrayList<>();
		public static List<String> rottenFoodBlacklistUnparsed = new ArrayList<>();

		public static List<String> taintingWhitelistUnparsed = new ArrayList<>();
		public static List<String> taintingBlacklistUnparsed = new ArrayList<>();

		public static List<String> corrosionWhitelistUnparsed = new ArrayList<>();
		public static List<String> corrosionBlacklistUnparsed = new ArrayList<>();

		public static List<String> fireToolWhitelistUnparsed = new ArrayList<>();
		public static List<String> fireToolBlacklistUnparsed = new ArrayList<>();

		public static List<String> fertilizerWhitelistUnparsed = new ArrayList<>();
		public static List<String> fertilizerBlacklistUnparsed = new ArrayList<>();

		public static List<String> toolWeaknessWhitelistUnparsed = new ArrayList<>();
		public static List<String> toolWeaknessBlacklistUnparsed = new ArrayList<>();

		public static List<String> torchWhitelistUnparsed = new ArrayList<>();
		public static List<String> torchBlacklistUnparsed = new ArrayList<>();
		

		public static ItemListProperty rottenFoodWhitelist = new ItemListProperty(() -> rottenFoodWhitelistUnparsed.toArray(new String[0]));
		public static ItemListProperty rottenFoodBlacklist = new ItemListProperty(() -> rottenFoodBlacklistUnparsed.toArray(new String[0]));

		public static ItemListProperty taintingWhitelist = new ItemListProperty(() -> taintingWhitelistUnparsed.toArray(new String[0]));
		public static ItemListProperty taintingBlacklist = new ItemListProperty(() -> taintingBlacklistUnparsed.toArray(new String[0]));

		public static ItemListProperty corrosionWhitelist = new ItemListProperty(() -> corrosionWhitelistUnparsed.toArray(new String[0]));
		public static ItemListProperty corrosionBlacklist = new ItemListProperty(() -> corrosionBlacklistUnparsed.toArray(new String[0]));

		public static ItemListProperty fireToolWhitelist = new ItemListProperty(() -> fireToolWhitelistUnparsed.toArray(new String[0]));
		public static ItemListProperty fireToolBlacklist = new ItemListProperty(() -> fireToolBlacklistUnparsed.toArray(new String[0]));

		public static ItemListProperty fertilizerWhitelist = new ItemListProperty(() -> fertilizerWhitelistUnparsed.toArray(new String[0]));
		public static ItemListProperty fertilizerBlacklist = new ItemListProperty(() -> fertilizerBlacklistUnparsed.toArray(new String[0]));

		public static ItemListProperty toolWeaknessWhitelist = new ItemListProperty(() -> toolWeaknessWhitelistUnparsed.toArray(new String[0]));
		public static ItemListProperty toolWeaknessBlacklist = new ItemListProperty(() -> toolWeaknessBlacklistUnparsed.toArray(new String[0]));

		public static ItemListProperty torchWhitelist = new ItemListProperty(() -> torchWhitelistUnparsed.toArray(new String[0]));
		public static ItemListProperty torchBlacklist = new ItemListProperty(() -> torchBlacklistUnparsed.toArray(new String[0]));
	}

	@SuppressWarnings("unchecked")
	public static void rebuildCommonConfig(BetweenlandsCommonConfig commonConfig) {
		// Load data from edited config
		Overworld.rottenFoodWhitelistUnparsed = (List<String>) commonConfig.OVERWORLD.rottenFoodWhitelist.get();
		Overworld.rottenFoodBlacklistUnparsed = (List<String>) commonConfig.OVERWORLD.rottenFoodBlacklist.get();
		Overworld.taintingWhitelistUnparsed = (List<String>) commonConfig.OVERWORLD.taintingWhitelist.get();
		Overworld.taintingBlacklistUnparsed = (List<String>) commonConfig.OVERWORLD.taintingBlacklist.get();
		
		Overworld.fireToolWhitelistUnparsed = (List<String>) commonConfig.OVERWORLD.fireToolWhitelist.get();
		Overworld.fireToolBlacklistUnparsed = (List<String>) commonConfig.OVERWORLD.fireToolBlacklist.get();
		Overworld.fertilizerWhitelistUnparsed = (List<String>) commonConfig.OVERWORLD.fertilizerWhitelist.get();
		Overworld.fertilizerBlacklistUnparsed = (List<String>) commonConfig.OVERWORLD.fertilizerBlacklist.get();
		
		Overworld.toolWeaknessWhitelistUnparsed = (List<String>) commonConfig.OVERWORLD.toolWeaknessWhitelist.get();
		Overworld.toolWeaknessBlacklistUnparsed = (List<String>) commonConfig.OVERWORLD.toolWeaknessBlacklist.get();
		Overworld.torchWhitelistUnparsed = (List<String>) commonConfig.OVERWORLD.torchWhitelist.get();
		Overworld.torchBlacklistUnparsed = (List<String>) commonConfig.OVERWORLD.torchBlacklist.get();

		// Parse data into a usable form
		Overworld.rottenFoodWhitelist.parseData();
		Overworld.rottenFoodBlacklist.parseData();
		Overworld.taintingWhitelist.parseData();
		Overworld.taintingBlacklist.parseData();
		Overworld.fireToolWhitelist.parseData();
		Overworld.fireToolBlacklist.parseData();
		Overworld.fertilizerWhitelist.parseData();
		Overworld.fertilizerBlacklist.parseData();
		Overworld.toolWeaknessWhitelist.parseData();
		Overworld.toolWeaknessBlacklist.parseData();
		Overworld.torchWhitelist.parseData();
		Overworld.torchBlacklist.parseData();
		
		// Try to update the cache
		HolderLookup.Provider registryAccess = TheBetweenlands.tryGetRegistryAccess();
		if(registryAccess != null) {
			Overworld.rottenFoodWhitelist.buildCache(registryAccess);
			Overworld.rottenFoodBlacklist.buildCache(registryAccess);
			Overworld.taintingWhitelist.buildCache(registryAccess);
			Overworld.taintingBlacklist.buildCache(registryAccess);
			Overworld.fireToolWhitelist.buildCache(registryAccess);
			Overworld.fireToolBlacklist.buildCache(registryAccess);
			Overworld.fertilizerWhitelist.buildCache(registryAccess);
			Overworld.fertilizerBlacklist.buildCache(registryAccess);
			Overworld.toolWeaknessWhitelist.buildCache(registryAccess);
			Overworld.toolWeaknessBlacklist.buildCache(registryAccess);
			Overworld.torchWhitelist.buildCache(registryAccess);
			Overworld.torchBlacklist.buildCache(registryAccess);
		}
	}
	
}
