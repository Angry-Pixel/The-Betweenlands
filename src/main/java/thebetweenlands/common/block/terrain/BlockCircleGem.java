package thebetweenlands.common.block.terrain;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.world.storage.BetweenlandsChunkStorage;

public class BlockCircleGem extends BlockGenericOre {
	public final CircleGemType gem;

	public BlockCircleGem(CircleGemType gem) {
		super(Material.ROCK);
		this.setHarvestLevel2("pickaxe", 1);
		this.setLightLevel(0.8F);
		this.gem = gem;
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		super.onBlockAdded(worldIn, pos, state);

		if(this.gem.gemSingerTarget != null) {
			BetweenlandsChunkStorage.markGem(worldIn, pos, this.gem.gemSingerTarget);
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);

		if(this.gem.gemSingerTarget != null) {
			BetweenlandsChunkStorage.unmarkGem(worldIn, pos, this.gem.gemSingerTarget);
		}
	}
}
