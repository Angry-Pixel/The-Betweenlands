package thebetweenlands.common.registries;

import net.minecraft.world.GameRules;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class GameruleRegistry {

    public static GameruleRegistry INSTANCE = new GameruleRegistry();

    public static final String BL_DECAY = "blDecay";
    public static final String BL_CORROSION = "blCorrosion";

    public void onServerStarting(FMLServerStartingEvent event) {
        this.createGamerule(BL_DECAY, "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.createGamerule(BL_CORROSION, "true", GameRules.ValueType.BOOLEAN_VALUE);
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
