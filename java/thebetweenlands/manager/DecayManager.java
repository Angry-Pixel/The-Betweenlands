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
        return Math.max(6f, getDecayLevel(player));
    }

    public static boolean enableDecay(EntityPlayer player)
    {
        return player.dimension == ModInfo.DIMENSION_ID;
    }
}
