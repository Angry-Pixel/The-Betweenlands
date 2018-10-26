package thebetweenlands.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public interface IRenamableItem {
	public default boolean canRename(EntityPlayer player, EnumHand hand, ItemStack stack, String name) {
		return true;
	}

	public default void setRename(EntityPlayer player, EnumHand hand, ItemStack stack, String name) {
		stack.setStackDisplayName(name);
	}

	public default void clearRename(EntityPlayer player, EnumHand hand, ItemStack stack, String name) {
		stack.clearCustomName();
	}
}
