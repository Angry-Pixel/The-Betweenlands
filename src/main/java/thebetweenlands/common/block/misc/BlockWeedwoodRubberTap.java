package thebetweenlands.common.block.misc;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;
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
				spawnAsEntity(worldIn, pos, new ItemStack(ItemRegistry.WEEDWOOD_BUCKET_RUBBER));
			} else {
				spawnAsEntity(worldIn, pos, new ItemStack(ItemRegistry.WEEDWOOD_BUCKET));
			}
		}
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		drops.add(new ItemStack(ItemRegistry.WEEDWOOD_BUCKET));
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(ItemRegistry.WEEDWOOD_BUCKET);
	}
}
