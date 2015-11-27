package thebetweenlands.items.misc;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.entities.mobs.EntityGecko;

/**
 * Created by Bart on 27/11/2015.
 */
public class ItemMob extends Item {
    String name = "";

    public ItemMob(String name){
        this.setTextureName("thebetweenlands:" + name);
        this.setUnlocalizedName("thebetweenlands." + name);
        this.name = name;
        this.maxStackSize = 1;
    }


    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        EntityLiving entity = null;
        ForgeDirection direction = ForgeDirection.getOrientation(side);
        switch (name){
            case "fireFly":
                entity = new EntityFirefly(world);
                break;
            case "gecko":
                entity = new EntityGecko(world);
                break;
        }
        if (entity != null){
            entity.setLocationAndAngles(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ, 0.0f, 0.0f);
            entity.setCustomNameTag(itemStack.getDisplayName());
            world.spawnEntityInWorld(entity);
        }
        player.destroyCurrentEquippedItem();
        return true;
    }
}
