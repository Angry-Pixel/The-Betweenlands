package thebetweenlands.common.item.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.projectiles.EntityPyradFlame;

public class ItemPyradFlame extends Item {
	public ItemPyradFlame() {
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick( World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(!world.isRemote) {
			world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (itemRand.nextFloat() - itemRand.nextFloat()) * 0.2F + 1.0F);

			Vec3d look = player.getLookVec();

			float f = 0.05F;

			for (int i = 0; i < player.getRNG().nextInt(6) + 1; ++i) {
				EntityPyradFlame flame = new EntityPyradFlame(world, player, look.x + player.getRNG().nextGaussian() * (double)f, look.y, look.z + player.getRNG().nextGaussian() * (double)f);
				flame.posY = player.posY + (double)(player.height / 2.0F) + 0.5D;
				world.spawnEntity(flame);
			}

			if (!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}
}