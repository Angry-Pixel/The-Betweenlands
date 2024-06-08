package thebetweenlands.common.features;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import thebetweenlands.common.generation.trees.WeedwoodTree;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.biome.decorator.DecoratorPositionProvider;

import static thebetweenlands.common.world.gen.biome.decorator.DecorationHelper.generateNibbletwigTree;

public class WeedWoodTreeFeature extends Feature<WeedWoodTreeConfig> {

	public WeedWoodTreeFeature(Codec<WeedWoodTreeConfig> p_65786_) {
		super(p_65786_);
	}
	
	@Override
	public boolean place(FeaturePlaceContext<WeedWoodTreeConfig> p_159749_) {
		WorldGenLevel level = p_159749_.level();
		Random rand = p_159749_.random();
		BlockPos pos = p_159749_.origin().north(rand.nextInt(8)-4).east(rand.nextInt(8)-4);
		ChunkGeneratorBetweenlands chunk = null;

		if (p_159749_.chunkGenerator() instanceof ChunkGeneratorBetweenlands) {
			chunk = (ChunkGeneratorBetweenlands)p_159749_.chunkGenerator();

			if (generateNibbletwigTree(new DecoratorPositionProvider()));
		}

		int depth = 0;
		
		/*List<ChunkAccess> cal = new ArrayList<ChunkAccess>();
		cal.add(level.getChunk(pos));
		// add all relevent chunks to region list
		for (int x = -1; x > 0; x++) {
			for (int z = -1; z > 0; z++) {
				if (x != 0 && z != 0) {
					cal.add(level.getChunk(pos.getX() + x, pos.getZ() + z));
				}
			}
		}
		
		WorldGenRegion region = new WorldGenRegion(level.getLevel(), cal, level.getChunk(pos).getStatus(), 0);
		*/
		
		// Random chance to not gen
		if (rand.nextInt(8) > 1) {
			return false;
		}
		
		// Do find surface
		boolean surface = false;
		for (int y = 0; y <= 40; y++) {
			BlockPos translatedpos = pos.below(y);
			BlockState scanstate = level.getBlockState(translatedpos);
			if (!scanstate.is(Blocks.AIR) && !(scanstate.getFluidState().is(FluidTags.WATER) && scanstate.getDestroySpeed(level, translatedpos) == 100)) {
				surface = true;
				depth = y - 1;
				
				// Check surface
				if (!scanstate.is(BlockRegistry.SWAMP_GRASS.get()) &&
						!scanstate.is(BlockRegistry.DEAD_SWAMP_GRASS.get()) &&
						!scanstate.is(BlockRegistry.SWAMP_DIRT.get()) &&
						!scanstate.is(BlockRegistry.MUD.get())) {
					return false;
				}
				
				break;
			}
		}
		
		// if no surface
		if (!surface) {
			return false;
		}
		
		// Place
		return WeedwoodTree.generate(level, pos.below(depth), rand, true);
	}
}
