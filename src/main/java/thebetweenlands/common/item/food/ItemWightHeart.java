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
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemStackIn = playerIn.getHeldItem(handIn);
		if (playerIn.getHealth() < playerIn.getMaxHealth()) {
			playerIn.setActiveHand(handIn);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
		} else {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
		}
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
		super.onFoodEaten(stack, world, player);
		player.heal(8.0F);
	}

	@Override
	public boolean canGetSickOf(EntityPlayer player, ItemStack stack) {
		return false;
	}
}
