package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.item.herblore.ItemPlantDrop.EnumItemPlantDrop;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class BlockCaveMoss extends BlockHangingPlant {
	public BlockCaveMoss() {
		super(Material.PLANTS);
	}

	@Override
	protected boolean isValidBlock(World world, BlockPos pos, IBlockState blockState) {
		return SurfaceType.UNDERGROUND.matches(blockState) || blockState.getBlock() == this;
	}

	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (rand.nextInt(40) == 0) {
			float dripRange = 0.5F;
			float px = rand.nextFloat() - 0.5F;
			float py = rand.nextFloat();
			float pz = rand.nextFloat() - 0.5F;
			float u = Math.max(Math.abs(px), Math.abs(pz));
			px = px / u * dripRange + 0.5F;
			pz = pz / u * dripRange + 0.5F;
			BLParticles.CAVE_WATER_DRIP.spawn(worldIn, pos.getX() + px, pos.getY() + py, pos.getZ() + pz);
		}
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of(EnumItemPlantDrop.CAVE_MOSS_ITEM.create(1));
	}
}
