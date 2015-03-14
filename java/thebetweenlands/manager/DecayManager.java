package thebetweenlands.manager;

import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.entities.property.EntityPropertiesDecay;

public class DecayManager
{
    public static int getDecayLevel(EntityPlayer player)
    {
        return ((EntityPropertiesDecay) player.getExtendedProperties(EntityPropertiesDecay.getId())).decayLevel;
    }

    public static int setDecayLevel(int decayLevel, EntityPlayer player)
    {
        return ((EntityPropertiesDecay) player.getExtendedProperties(EntityPropertiesDecay.getId())).decayLevel = decayLevel;
    }

    public static int resetDecay(EntityPlayer player)
    {
        return setDecayLevel(0, player);
    }
}
