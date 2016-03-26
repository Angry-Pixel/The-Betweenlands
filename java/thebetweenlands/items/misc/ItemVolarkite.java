package thebetweenlands.items.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.manual.IManualEntryItem;

public class ItemVolarkite extends Item implements IManualEntryItem {
    public ItemVolarkite(){
        maxStackSize = 1;
        setUnlocalizedName("thebetweenlands.volarkite");
    }

    @Override
    public String manualName(int meta) {
        return "volarKite";
    }

    @Override
    public Item getItem() {
        return this;
    }

    @Override
    public int[] recipeType(int meta) {
        return new int[0];
    }

    @Override
    public int metas() {
        return 0;
    }
}
