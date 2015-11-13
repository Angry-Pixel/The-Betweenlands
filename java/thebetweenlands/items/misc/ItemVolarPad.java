package thebetweenlands.items.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.manual.gui.entries.IManualEntryItem;

/**
 * Created by Bart on 25-9-2015.
 */
public class ItemVolarPad extends Item implements IManualEntryItem {
    public ItemVolarPad(){
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
