package thebetweenlands.common.item.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemWightHeart extends ItemBLFood {
	public ItemWightHeart() {
		super(0, 0.0F, false);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if (playerIn.getHealth() < playerIn.getMaxHealth()) {
			playerIn.setActiveHand(hand);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		} else {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		}
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
		super.onFoodEaten(stack, world, player);
		player.heal(8.0F);
	}

	@Override
	public boolean canGetSickOf(ItemStack stack) {
		return false;
	}
}
