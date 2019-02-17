package thebetweenlands.common.block.plant;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockBulbCappedMushroom extends BlockMushroomBetweenlands {
	public BlockBulbCappedMushroom() {
		this.setLightLevel(1.0F);
	}

	@Override
	public IItemProvider getItemDropped(IBlockState state, World world, BlockPos pos, int fortune) {
		return ItemRegistry.BULB_CAPPED_MUSHROOM_ITEM;
	}

	@Override
	public boolean isShearable(ItemStack item, IWorldReader world, BlockPos pos) {
		return false;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IWorldReader world, BlockPos pos, int fortune) {
		return ImmutableList.of();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.XZ;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}
}
