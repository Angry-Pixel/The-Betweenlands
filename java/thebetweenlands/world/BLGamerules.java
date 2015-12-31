package thebetweenlands.world;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class BLGamerules {
	public static final BLGamerules INSTANCE = new BLGamerules();

	public static final String BL_DECAY = "blDecay";
	public static final String BL_CORROSION = "blCorrosion";

	public void onServerStarting(FMLServerStartingEvent event) {
		this.createGamerule(BL_DECAY, "true");
		this.createGamerule(BL_CORROSION, "true");
	}

	private void createGamerule(String name, String value) {
		GameRules gameRules = DimensionManager.getWorld(0).getGameRules();
		if(!gameRules.hasRule(name)) {
			gameRules.addGameRule(name, value);
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
		return getGameRules() != null ? getGameRules().getGameRuleBooleanValue(name) : false;
	}

	public static String getGameRuleStringValue(String name) {
		return getGameRules() != null ? getGameRules().getGameRuleStringValue(name) : "";
	}
}
