package thebetweenlands.common.block.plant;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockBladderwortFlower extends BlockPlant {
	@Override
	protected boolean canSustainBush(IBlockState state) {
		return state.getBlock() == BlockRegistry.BLADDERWORT_STALK;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.NONE;
	}
}