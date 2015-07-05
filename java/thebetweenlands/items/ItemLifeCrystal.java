package thebetweenlands.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemLifeCrystal extends Item { //Place Holder Code
	public ItemLifeCrystal() {
        setMaxDamage(4);
		maxStackSize = 1;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		list.add(100 - 25 * getDamage(stack) + "% Remaining");
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return false;
	}
}
