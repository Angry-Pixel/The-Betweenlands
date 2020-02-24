package thebetweenlands.common.registries;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.GameRuleChangeEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.clientbound.MessageSyncGameRules;

public final class GameruleRegistry {
	private GameruleRegistry() { }
	
	public static final GameruleRegistry INSTANCE = new GameruleRegistry();

	private final Set<String> gamerules = new HashSet<>();
	
	public static final String BL_FOOD_SICKNESS = "blFoodSickness";
	public static final String BL_ROTTEN_FOOD = "blRottenFood";
	public static final String BL_DECAY = "blDecay";
	public static final String BL_CORROSION = "blCorrosion";
	public static final String BL_TOOL_WEAKNESS = "blToolWeakness";
	public static final String BL_TORCH_BLACKLIST = "blTorchBlacklist";
	public static final String BL_FIRE_TOOL_BLACKLIST = "blFireToolBlacklist";
	public static final String BL_POTION_BLACKLIST = "blPotionBlacklist";
	public static final String BL_FERTILIZER_BLACKLIST = "blFertilizerBlacklist";
	public static final String BL_TIMED_EVENTS = "blTimedEvents";

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
		this.createGamerule(BL_TIMED_EVENTS, "true", GameRules.ValueType.BOOLEAN_VALUE);
	}

	private void createGamerule(String name, String value, GameRules.ValueType type) {
		GameRules gameRules = DimensionManager.getWorld(0).getGameRules();
		if(!gameRules.hasRule(name)) {
			gameRules.addGameRule(name, value, type);
		}
		this.gamerules.add(name);
	}

	@Nullable
	public static GameRules getGameRules() {
		WorldServer serverWorld = DimensionManager.getWorld(0);
		if(serverWorld != null) {
			return serverWorld.getGameRules();
		} else {
			World clientWorld = TheBetweenlands.proxy.getClientWorld();
			if(clientWorld != null) {
				return clientWorld.getGameRules();
			}
		}
		return null;
	}

	public static boolean getGameRuleBooleanValue(String name) {
		return getGameRules() != null && getGameRules().getBoolean(name);
	}

	public static String getGameRuleStringValue(String name) {
		return getGameRules() != null ? getGameRules().getString(name) : "";
	}

	@SubscribeEvent
	public static void onEntityJoin(EntityJoinWorldEvent event) {
		if(!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayerMP) {
			TheBetweenlands.networkWrapper.sendTo(new MessageSyncGameRules(GameruleRegistry.INSTANCE.gamerules), (EntityPlayerMP) event.getEntity());
		}
	}
	
	@SubscribeEvent
	public static void onGameruleChange(GameRuleChangeEvent event) {
		if(GameruleRegistry.INSTANCE.gamerules.contains(event.getRuleName())) {
			TheBetweenlands.networkWrapper.sendToAll(new MessageSyncGameRules(Collections.singleton(event.getRuleName())));
		}
	}
}
