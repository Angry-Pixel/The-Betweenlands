package thebetweenlands.items.loot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import thebetweenlands.items.BLItemRegistry;

import java.util.List;

/**
 * Created by Bart on 8-7-2015.
 */
public class ItemRingOfPower extends Item {
    public ItemRingOfPower() {
        this.maxStackSize = 1;
        this.setUnlocalizedName("thebetweenlands.ringOfPower");
        setTextureName("thebetweenlands:ringOfPower");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        list.add(StatCollector.translateToLocal("power.ring.bonus"));
    }
}
