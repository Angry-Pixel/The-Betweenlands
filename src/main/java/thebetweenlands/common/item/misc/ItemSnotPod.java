package thebetweenlands.common.item.misc;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.mobs.EntityRockSnot;

public class ItemSnotPod extends Item {
	public ItemSnotPod() {
		this.setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if (!worldIn.isRemote) {
			EntityRockSnot snot = new EntityRockSnot(worldIn);
			snot.setPlacedByPlayer(true);
			snot.setPosition(pos.getX() + 0.5F, pos.getY() + 1F, pos.getZ() + 0.5F);
			worldIn.spawnEntity(snot);
			if (!player.capabilities.isCreativeMode)
				stack.shrink(1);
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return BLCreativeTabs.SPECIALS;
	}
}
