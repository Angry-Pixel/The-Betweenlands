package thebetweenlands.common.block.misc;

import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockTrapDoorBetweenlands extends BlockTrapDoor {
	public BlockTrapDoorBetweenlands(Material material) {
		super(material);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public BlockTrapDoorBetweenlands setSoundType(SoundType type) {
		return (BlockTrapDoorBetweenlands) super.setSoundType(type);
	}

	@Override
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		if (state.getValue(OPEN)) {
			IBlockState down = world.getBlockState(pos.down());
			if (down.getBlock() instanceof BlockLadder) {
				return down.getValue(BlockLadder.FACING) == state.getValue(FACING);
			}
		}
		return false;
	}
}
