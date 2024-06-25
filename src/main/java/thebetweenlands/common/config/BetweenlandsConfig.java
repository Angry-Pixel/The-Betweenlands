package thebetweenlands.common.config;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

//TODO cached config values are stored here, make actual config file to allow changing them
public class BetweenlandsConfig {

	public static boolean debug = false;
	public static ResourceKey<Level> returnDimension = Level.OVERWORLD;
	public static boolean seasonalEvents = true;

	public static boolean activateRiftOnFirstJoin = true;
	public static int minRiftOnFirstJoinDuration = 1800;
	public static boolean startInBetweenlands = false;

	public static boolean startInPortal = false;
	public static int portalBiomeSearchRange = 256;

	public static int wispQuality = 50;
	public static boolean useShader = true;
	public static int shaderPriority = 0;
	public static boolean dimensionShaderOnly = false;
	public static int skyResolution = 1024;
	public static boolean skyRiftClouds = true;

	public static boolean blMainMenu = true;
	public static boolean rowboatView = true;
	public static boolean useFoodSicknessInBetweenlands = true;
	public static boolean useFoodSicknessOutsideBetweenlands = false;
	public static boolean reverseRottenFood = true;
	public static boolean useRottenFood = true;
	public static boolean useDecay = true;
	public static List<ResourceKey<Level>> decayDimensionList = new ArrayList<>();
	public static boolean useCorrosion = true;
	public static boolean useToolWeakness = true;
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
	public static boolean decayPercentual = false;
	public static float decayMinHealth = 6.0f;
	public static float decayMinHealthPercentage = 0.15f;
	public static boolean itemUsageTooltip = true;
	public static boolean onlineGallery = true;

	public static int maxEntitiesPerLoadedArea = 250;
	public static int hardEntityLimit = 600;
	public static boolean showNonBLFluids = true;
	public static boolean showNonBLGemRecipes = true;
	public static boolean onlineEnvironmentEventOverrides = false;
	public static int checkInterval = 1800;
	public static int failedRecheckInterval = 60;
	public static int failedRecheckCount = 3;
	public static int defaultRemoteResetTime = 180;

	public static boolean debugModelLoader = false;
	public static boolean dumpPackedTextures = false;
}
