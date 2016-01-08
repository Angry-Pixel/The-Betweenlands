package thebetweenlands.decay;

import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesDecay;
import thebetweenlands.lib.ModInfo;

public class DecayManager {
	public static DecayStats getDecayStats(EntityPlayer player) {
		EntityPropertiesDecay property = BLEntityPropertiesRegistry.HANDLER.getProperties(player, EntityPropertiesDecay.class);
		if (property != null) {
			return property.decayStats;
		}
		return null;
	}

	public static int getDecayLevel(EntityPlayer player) {
		DecayStats stats = getDecayStats(player);
		if (stats != null) {
			return stats.getDecayLevel();
		}
		return 0;
	}

	public static void setDecayLevel(int decayLevel, EntityPlayer player) {
		if (decayLevel < 0) return;
		if(!player.worldObj.isRemote) {
			DecayStats stats = getDecayStats(player);
			if(stats != null) {
				stats.setDecayLevel(decayLevel > 20 ? 20 : (decayLevel < 0 ? 0 : decayLevel));
			}
		}
	}

	public static void resetDecay(EntityPlayer player) {
		setDecayLevel(0, player);
	}

	public static float getPlayerHearts(EntityPlayer player) {
		return Math.min(26f - ((20 - getDecayLevel(player))), 20f);
	}

	public static boolean isDecayEnabled(EntityPlayer player) {
		return player.dimension == ModInfo.DIMENSION_ID && !player.capabilities.isCreativeMode && !player.capabilities.disableDamage;
	}

	public static int getCorruptionLevel(EntityPlayer player) {
		if (!isDecayEnabled(player)) return 0;
		return 10 - getDecayLevel(player) / 2;
	}
}
