package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockSwampKelp extends BlockStackablePlantUnderwater {
	public BlockSwampKelp() {
		this.resetAge = true;
		this.setLightLevel(0.2F);
		this.setHardness(0.1F);
		this.setCreativeTab(null);
	}

	@Override
	public IItemProvider getItemDropped(IBlockState state, World world, BlockPos pos, int fortune) {
		return ItemRegistry.SWAMP_KELP_ITEM;
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return 1;
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
	public void animateTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if (world.rand.nextInt(400) == 0) {
			BLParticles.WATER_BUG.spawn(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		}
	}
	
	@Override
	public ItemBlock getItemBlock() {
		return null;
	}
}
