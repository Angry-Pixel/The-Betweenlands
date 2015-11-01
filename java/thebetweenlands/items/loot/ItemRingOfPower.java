package thebetweenlands.items.loot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.manual.gui.entries.IManualEntryItem;

import java.util.List;

/**
 * Created by Bart on 8-7-2015.
 */
public class ItemRingOfPower extends Item implements IManualEntryItem {
    public ItemRingOfPower() {
        this.maxStackSize = 1;
        this.setUnlocalizedName("thebetweenlands.ringOfPower");
        setTextureName("thebetweenlands:ringOfPower");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        list.add(StatCollector.translateToLocal("power.ring.bonus"));
    }

    @Override
    public String manualName(int meta) {
        return "ringOfPower";
    }

    @Override
    public Item getItem() {
        return this;
    }

    @Override
    public int[] recipeType(int meta) {
        return new int[]{6};
    }
}
