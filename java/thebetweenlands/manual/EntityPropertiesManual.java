package thebetweenlands.manual;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import thebetweenlands.entities.properties.EntityProperties;

import java.util.ArrayList;

/**
 * Created by Bart on 10-8-2015.
 */
public class EntityPropertiesManual extends EntityProperties {

    public ArrayList<String> foundPages = new ArrayList<>();


    @Override
    public void saveNBTData(NBTTagCompound nbt) {

        NBTTagList pages = new NBTTagList();
        for (String string:foundPages) {
            NBTTagCompound data = new NBTTagCompound();
            data.setString("page", string);
            pages.appendTag(data);
        }
        nbt.setTag("pages", pages);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        NBTTagList tag = nbt.getTagList("pages", 10);

        for (int i = 0; i < tag.tagCount(); i++) {
            NBTTagCompound data = tag.getCompoundTagAt(i);
            this.foundPages.add(data.getString("page"));
        }
    }


    @Override
    public void init(Entity entity, World world) {

    }

    @Override
    public String getID() {
        return "thebetweenlands_manual_data";
    }

    @Override
    public Class<? extends Entity> getEntityClass() {
        return EntityPlayer.class;
    }
}
