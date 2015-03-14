package thebetweenlands.manager;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.property.EntityPropertiesDecay;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.network.MessageSyncPlayerDecay;

public class DecayManager
{
    public static int getDecayLevel(EntityPlayer player)
    {
        return ((EntityPropertiesDecay) player.getExtendedProperties(EntityPropertiesDecay.getId())).decayLevel;
    }

    public static int setDecayLevel(int decayLevel, EntityPlayer player)
    {
        ((EntityPropertiesDecay) player.getExtendedProperties(EntityPropertiesDecay.getId())).decayLevel = decayLevel > 20 ? 20 : decayLevel;
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) TheBetweenlands.networkWrapper.sendToServer(new MessageSyncPlayerDecay(decayLevel));
        return ((EntityPropertiesDecay) player.getExtendedProperties(EntityPropertiesDecay.getId())).decayLevel;
    }

    public static int resetDecay(EntityPlayer player)
    {
        return setDecayLevel(0, player);
    }

    public static float getPlayerHearts(EntityPlayer player)
    {
        return 20f - (14 - getDecayLevel(player) * 14 / 20);
    }

    public static boolean enableDecay(EntityPlayer player)
    {
        return player.dimension == ModInfo.DIMENSION_ID;
    }

    public static int getCorruptionLevel(EntityPlayer player)
    {
        int decayLevel = getDecayLevel(player);

        if (!enableDecay(player)) return 0;

        if (decayLevel <= 4) return 4;
        else if (decayLevel <= 6) return 3;
        else if (decayLevel <= 8) return 2;
        else if (decayLevel <= 12) return 1;
        else return 0;
    }
}
