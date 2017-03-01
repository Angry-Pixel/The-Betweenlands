package thebetweenlands.common.item.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;

public class ItemNettleSoup extends ItemBLFood {
	public ItemNettleSoup() {
		super(10, 8.4F, false);
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return EnumItemMisc.WEEDWOOD_BOWL.create(1);
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
		player.getFoodStats().addStats(10, 8.4F);
		//worldIn.playSound(player, player.getPosition(), SoundEvents.slu, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);

		if (stack.stackSize != 0)
			player.inventory.addItemStackToInventory(getContainerItem(stack));
	}
}
