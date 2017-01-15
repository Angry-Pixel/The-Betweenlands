package thebetweenlands.common.block.misc;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityRubberTap;

public class BlockWeedwoodRubberTap extends BlockRubberTap {
	public BlockWeedwoodRubberTap() {
		super(BlockRegistry.WEEDWOOD_PLANKS.getDefaultState(), 540);
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
		if(!worldIn.isRemote && te instanceof TileEntityRubberTap) {
			player.addStat(StatList.getBlockStats(this));
			player.addExhaustion(0.025F);

			TileEntityRubberTap tap = (TileEntityRubberTap) te;

			FluidStack drained = tap.drain(Fluid.BUCKET_VOLUME, false);

			if(drained != null && drained.amount == Fluid.BUCKET_VOLUME) {
				spawnAsEntity(worldIn, pos, UniversalBucket.getFilledBucket(ItemRegistry.WEEDWOOD_BUCKET_FILLED, FluidRegistry.RUBBER));
			} else {
				spawnAsEntity(worldIn, pos, new ItemStack(ItemRegistry.WEEDWOOD_BUCKET_EMPTY));
			}
		}
	}
}
