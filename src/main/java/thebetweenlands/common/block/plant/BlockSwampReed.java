package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockSwampReed extends BlockStackablePlant {
	public BlockSwampReed() {
		super(false);
		this.setMaxHeight(4);
	}

	@Override
	protected boolean isSamePlant(Block block) {
		return super.isSamePlant(block) || block == BlockRegistry.SWAMP_REED_UNDERWATER;
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return super.canSustainBush(state) || state.getBlock() == BlockRegistry.SWAMP_REED_UNDERWATER;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.NONE;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ItemRegistry.SWAMP_REED_ITEM;
	}
	
	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(ItemRegistry.SWAMP_REED_ITEM));
	}
}
