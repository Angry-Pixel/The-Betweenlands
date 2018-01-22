package thebetweenlands.common.block.plant;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.util.AdvancedStateMap;

public class BlockMarshMarigoldStalk extends BlockStackablePlantUnderwater {
	public BlockMarshMarigoldStalk() {
		this.harvestAll = true;
		this.setMaxHeight(1);
		this.setCreativeTab(null);
	}

	@Override
	protected boolean isSamePlant(IBlockState blockState) {
		return super.isSamePlant(blockState) || blockState.getBlock() == BlockRegistry.MARSH_MARIGOLD_FLOWER;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		worldIn.setBlockState(pos, BlockRegistry.MARSH_MARIGOLD_STALK.getDefaultState());
		worldIn.setBlockState(pos.up(), BlockRegistry.MARSH_MARIGOLD_FLOWER.getDefaultState());
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return worldIn.isAirBlock(pos.up()) && super.canPlaceBlockAt(worldIn, pos);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		super.setStateMapper(builder);
		builder.ignore(IS_TOP, IS_BOTTOM);
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(Item.getItemFromBlock(BlockRegistry.MARSH_MARIGOLD_FLOWER)));
	}
}
