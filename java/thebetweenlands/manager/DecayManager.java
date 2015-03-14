package thebetweenlands.manager;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.property.EntityPropertiesDecay;
import thebetweenlands.network.MessageSyncPlayerDecay;

public class DecayManager
{
    public static int getDecayLevel(EntityPlayer player)
    {
        //System.out.println(FMLCommonHandler.instance().getEffectiveSide() + ": " + ((EntityPropertiesDecay) player.getExtendedProperties(EntityPropertiesDecay.getId())).decayLevel);
        return ((EntityPropertiesDecay) player.getExtendedProperties(EntityPropertiesDecay.getId())).decayLevel;
    }

    public static int setDecayLevel(int decayLevel, EntityPlayer player)
    {
        ((EntityPropertiesDecay) player.getExtendedProperties(EntityPropertiesDecay.getId())).decayLevel = decayLevel > 20 ? 20 : decayLevel;
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) TheBetweenlands.networkWrapper.sendToServer(new MessageSyncPlayerDecay(decayLevel));

        System.out.println(FMLCommonHandler.instance().getEffectiveSide() + ": " + ((EntityPropertiesDecay) player.getExtendedProperties(EntityPropertiesDecay.getId())).decayLevel);
        return ((EntityPropertiesDecay) player.getExtendedProperties(EntityPropertiesDecay.getId())).decayLevel;
    }

    public static int resetDecay(EntityPlayer player)
    {
        return setDecayLevel(0, player);
    }
}
