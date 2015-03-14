package thebetweenlands.entities.property;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class EntityPropertiesDecay implements IExtendedEntityProperties
{
    public int decayLevel = 20;

    public void saveNBTData(NBTTagCompound nbt)
    {
        nbt.setInteger("decayLevel", decayLevel);
    }

    public void loadNBTData(NBTTagCompound nbt)
    {
        decayLevel = nbt.getInteger("decayLevel");
    }

    public void init(Entity entity, World world)
    {

    }

    public static String getId()
    {
        return "betweenlands_decay_data";
    }
}
