package thebetweenlands.common.block.plant;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWeedwoodBushInfested extends BlockWeedwoodBush {
	private ItemStack drop;
	private int stage; //dunno yet

	public BlockWeedwoodBushInfested(ItemStack drop, int stage) {
		this.drop = drop;
		this.stage = stage;
	}

	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return this.drop.getItem();
	}

	@Override
	public int damageDropped(IBlockState state) {
		return this.drop.getItemDamage();
	}

	@Override
	public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos, EnumFacing dir) {
		IBlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();
		return block instanceof BlockWeedwoodBush && !(block instanceof BlockNesting);
	}

	@Override
	public int getColorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
		return 0xFFFFFFFF;
	}
	
	@Override
	public boolean isFarmable(World world, BlockPos pos, IBlockState state) {
		return false;
	}

	@Override
	public boolean canSpreadTo(World world, BlockPos pos, IBlockState state, BlockPos targetPos, Random rand) {
		return false;
	}
	
	@Override
	public float getSpreadChance(World world, BlockPos pos, IBlockState state, BlockPos taretPos, Random rand) {
		return 0F;
	}

	@Override
	public void spreadTo(World world, BlockPos pos, IBlockState state, BlockPos targetPos, Random rand) {
	}

	@Override
	public void decayPlant(World world, BlockPos pos, IBlockState state, Random rand) {
		world.setBlockToAir(pos);
	}

	@Override
	public int getCompostCost(World world, BlockPos pos, IBlockState state, Random rand) {
		return 0;
	}
}
