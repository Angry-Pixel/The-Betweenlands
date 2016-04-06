package thebetweenlands.items.equipment;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.proxy.CommonProxy;

public class ItemLurkerSkinPouchLarge extends Item {

	public ItemLurkerSkinPouchLarge() {
		super();
		setMaxStackSize(1);
		setUnlocalizedName("thebetweenlands.lurkerSkinPouchLarge");
		setTextureName("thebetweenlands:lurkerSkinPouchLarge");
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			if (!player.isSneaking()) {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_LURKER_POUCH_LARGE, world, 0, 0, 0);
			}
		}

		return itemstack;
	}

}
