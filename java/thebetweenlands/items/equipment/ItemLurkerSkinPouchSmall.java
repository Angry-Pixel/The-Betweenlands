package thebetweenlands.items.equipment;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.proxy.CommonProxy;

public class ItemLurkerSkinPouchSmall extends Item {

	public ItemLurkerSkinPouchSmall() {
		super();
		setMaxStackSize(1);
		setUnlocalizedName("thebeteewnlands.lurkerSkinPouchSmall");
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			if (!player.isSneaking()) {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_LURKER_POUCH_SMALL, world, 0, 0, 0);
			}
		}

		return itemstack;
	}

}
