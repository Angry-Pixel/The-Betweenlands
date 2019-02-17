package thebetweenlands.common.block.plant;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.util.AdvancedStateMap;

public class BlockGoldenClubFlower extends BlockStackablePlant {
	public BlockGoldenClubFlower() {
		this.harvestAll = true;
		this.setMaxHeight(1);
	}

	@Override
	protected boolean isSamePlant(IBlockState blockState) {
		return super.isSamePlant(blockState) || blockState.getBlock() == BlockRegistry.GOLDEN_CLUB_STALK;
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return state.getBlock() == BlockRegistry.GOLDEN_CLUB_STALK;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.NONE;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		worldIn.setBlockState(pos, BlockRegistry.GOLDEN_CLUB_STALK.getDefaultState());
		worldIn.setBlockState(pos.up(), BlockRegistry.GOLDEN_CLUB_FLOWER.getDefaultState());
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return BlockRegistry.GOLDEN_CLUB_STALK.canPlaceBlockAt(worldIn, pos);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		super.setStateMapper(builder);
		builder.ignore(IS_TOP, IS_BOTTOM);
	}
}
