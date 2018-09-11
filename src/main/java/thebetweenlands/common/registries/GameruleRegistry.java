package thebetweenlands.common.registries;

import net.minecraft.world.GameRules;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class GameruleRegistry {
	public static final GameruleRegistry INSTANCE = new GameruleRegistry();

	public static final String BL_FOOD_SICKNESS = "blFoodSickness";
	public static final String BL_ROTTEN_FOOD = "blRottenFood";
	public static final String BL_DECAY = "blDecay";
	public static final String BL_CORROSION = "blCorrosion";
	public static final String BL_TOOL_WEAKNESS = "blToolWeakness";
	public static final String BL_TORCH_BLACKLIST = "blTorchBlacklist";
	public static final String BL_FIRE_TOOL_BLACKLIST = "blFireToolBlacklist";
	public static final String BL_POTION_BLACKLIST = "blPotionBlacklist";
	public static final String BL_FERTILIZER_BLACKLIST = "blFertilizerBlacklist";

	public void onServerStarting(FMLServerStartingEvent event) {
		this.createGamerule(BL_FOOD_SICKNESS, "true", GameRules.ValueType.BOOLEAN_VALUE);
		this.createGamerule(BL_ROTTEN_FOOD, "true", GameRules.ValueType.BOOLEAN_VALUE);
		this.createGamerule(BL_DECAY, "true", GameRules.ValueType.BOOLEAN_VALUE);
		this.createGamerule(BL_CORROSION, "true", GameRules.ValueType.BOOLEAN_VALUE);
		this.createGamerule(BL_TOOL_WEAKNESS, "true", GameRules.ValueType.BOOLEAN_VALUE);
		this.createGamerule(BL_TORCH_BLACKLIST, "true", GameRules.ValueType.BOOLEAN_VALUE);
		this.createGamerule(BL_FIRE_TOOL_BLACKLIST, "true", GameRules.ValueType.BOOLEAN_VALUE);
		this.createGamerule(BL_POTION_BLACKLIST, "true", GameRules.ValueType.BOOLEAN_VALUE);
		this.createGamerule(BL_FERTILIZER_BLACKLIST, "true", GameRules.ValueType.BOOLEAN_VALUE);
	}

	private void createGamerule(String name, String value, GameRules.ValueType type) {
		GameRules gameRules = DimensionManager.getWorld(0).getGameRules();
		if(!gameRules.hasRule(name)) {
			gameRules.addGameRule(name, value, type);
		}
	}

	public static GameRules getGameRules() {
		WorldServer world = DimensionManager.getWorld(0);
		if(world != null) {
			return world.getGameRules();
		}
		return null;
	}

	public static boolean getGameRuleBooleanValue(String name) {
		return getGameRules() != null && getGameRules().getBoolean(name);
	}

	public static String getGameRuleStringValue(String name) {
		return getGameRules() != null ? getGameRules().getString(name) : "";
	}

}
