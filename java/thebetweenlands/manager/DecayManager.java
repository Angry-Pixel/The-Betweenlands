package thebetweenlands.manager;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.BLGamerules;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.property.EntityPropertiesDecay;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.network.message.MessageSyncPlayerDecay;

public class DecayManager {
	public static int getDecayLevel(EntityPlayer player) {
		EntityPropertiesDecay property = ((EntityPropertiesDecay) player.getExtendedProperties(EntityPropertiesDecay.getId()));
		if (property != null) {
			return property.decayLevel;
		}
		return 0;
	}

	public static int setDecayLevel(int decayLevel, EntityPlayer player) {
		if (decayLevel < 0) return 0;
		((EntityPropertiesDecay) player.getExtendedProperties(EntityPropertiesDecay.getId())).decayLevel = decayLevel > 20 ? 20 : decayLevel;
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			TheBetweenlands.networkWrapper.sendToServer(new MessageSyncPlayerDecay(decayLevel));
		return ((EntityPropertiesDecay) player.getExtendedProperties(EntityPropertiesDecay.getId())).decayLevel;
	}

	public static int resetDecay(EntityPlayer player) {
		return setDecayLevel(0, player);
	}

	public static float getPlayerHearts(EntityPlayer player) {
		return Math.min(26f - ((20 - getDecayLevel(player))), 20f);
	}

	public static boolean enableDecay(EntityPlayer player) {
		return player.dimension == ModInfo.DIMENSION_ID && !player.capabilities.isCreativeMode && BLGamerules.getGameRuleBooleanValue(BLGamerules.BL_DECAY);
	}

	public static int getCorruptionLevel(EntityPlayer player) {
		if (!enableDecay(player)) return 0;
		return 10 - getDecayLevel(player) / 2;
	}
}
