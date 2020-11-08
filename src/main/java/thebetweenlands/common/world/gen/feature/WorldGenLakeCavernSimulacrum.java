package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;

public class WorldGenLakeCavernSimulacrum extends WorldGenSimulacrum {
	public WorldGenLakeCavernSimulacrum() {
		super(ImmutableList.of(BlockRegistry.SIMULACRUM_LAKE_CAVERN), LootTableRegistry.LAKE_CAVERN_SIMULACRUM_OFFERINGS);
	}

	@Override
	protected boolean canGenerateHere(World world, Random rand, BlockPos pos) {
		if(world.getBlockState(pos.down()).getBlock() != BlockRegistry.PITSTONE) {
			return false;
		}

		for(EnumFacing offset : EnumFacing.HORIZONTALS) {
			BlockPos offsetPos = pos.offset(offset, 3);

			if(world.getBlockState(offsetPos.down()).getMaterial() == Material.WATER || world.getBlockState(offsetPos.down(2)).getMaterial() == Material.WATER) {
				return true;
			}
		}

		return false;
	}
}
