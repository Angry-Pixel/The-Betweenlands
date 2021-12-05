package thebetweenlands.common.item.misc;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemPheromoneThorax extends Item {

	public ItemPheromoneThorax() {
		setMaxDamage(2);
		setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack held = player.getHeldItem(hand);
		if (!world.isRemote && !held.isEmpty() && held.getItem() == this) {
			IBlockState iblockstate = world.getBlockState(pos);
			Block block = iblockstate.getBlock();
			if (block == BlockRegistry.WEEDWOOD_BUSH) {
				world.setBlockState(pos, BlockRegistry.WEEDWOOD_BUSH_INFESTED_0.getDefaultState(), 2);
				held.damageItem(1, player);
				return EnumActionResult.SUCCESS;
			} else
				return EnumActionResult.FAIL;
		}
		player.swingArm(hand);;
		return EnumActionResult.PASS;
	}
}
