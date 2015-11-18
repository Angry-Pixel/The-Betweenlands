package thebetweenlands.world;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

public class BLGamerules {
	public static final BLGamerules INSTANCE = new BLGamerules();

	public static final String BL_DECAY = "blDecay";
	public static final String BL_CORROSION = "blCorrosion";

	public void onServerStarting(FMLServerStartingEvent event) {
		this.createGamerule(BL_DECAY, "true");
		this.createGamerule(BL_CORROSION, "true");
	}

	private void createGamerule(String name, String value) {
		GameRules gameRules = MinecraftServer.getServer().worldServerForDimension(0).getGameRules();
		if(!gameRules.hasRule(name)) {
			gameRules.addGameRule(name, value);
		}
	}

	public static GameRules getGameRules() {
		if(MinecraftServer.getServer() != null && MinecraftServer.getServer().worldServerForDimension(0) != null) {
			return MinecraftServer.getServer().worldServerForDimension(0).getGameRules();
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
