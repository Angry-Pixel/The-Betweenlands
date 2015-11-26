package thebetweenlands.manual;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.entities.property.IBLExtendedEntityProperties;

/**
 * Created by Bart on 10-8-2015.
 */
public class EntityPropertiesManual implements IBLExtendedEntityProperties {

    public String currentPage = "manual.main.title";


    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        nbt.setString("currentPage", this.currentPage);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        this.currentPage = nbt.getString("currentPage");
    }


    @Override
    public void init(Entity entity, World world) {

    }

    @Override
    public String getID() {
        return "betweenlands_manual_data";
    }

    @Override
    public Class<? extends Entity> getEntityClass() {
        return EntityPlayer.class;
    }
}
