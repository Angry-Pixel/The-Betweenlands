package thebetweenlands.common.features;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import thebetweenlands.common.blocks.BetweenlandsHangingBlock;
import thebetweenlands.common.registries.BlockRegistry;

// Edit ore feture to look for air to place blobs of vegitation
public class BetweenlandsCaveVegetation extends Feature<BetweenlandsCaveVegetationConfig> {

	// Cashe reguler blocks to sertch first (curently only one)
	public static BlockState cashe = null;

	public BetweenlandsCaveVegetation(Codec<BetweenlandsCaveVegetationConfig> p_66531_) {
		super(p_66531_);
	}

	// Do stuff (or debug stuff like i made it do)
	@Override
	public boolean place(FeaturePlaceContext<BetweenlandsCaveVegetationConfig> p_159749_) {
		// Init dependents
		Random random = p_159749_.random();
		BlockPos blockpos = p_159749_.origin();
		WorldGenLevel worldgenlevel = p_159749_.level();
		BetweenlandsCaveVegetationConfig configuration = p_159749_.config();

		int originx = blockpos.getX();
		int originy = blockpos.getY();
		int originz = blockpos.getZ();

		int sizex = (int) configuration.sizex.sample(random);
		int sizey = (int) configuration.sizex.sample(random);
		int sizez = (int) configuration.sizex.sample(random);

		// Environment conditon if active
		if (configuration.active) {
			// Get Ready for position translation
			int[] translation = configuration.scansample(worldgenlevel, originx, originy, originz);

			if (translation != null) {
				// Apply translation
				originx = translation[0];
				originy = translation[1];
				originz = translation[2];
			}
		}

		if (configuration.type == 0) {
			// Cube
			for (int x = originx - sizex; x <= originx + sizex; x++) {
				for (int y = originy - sizey; y <= originy + sizey; y++) {
					for (int z = originz - sizez; z <= originz + sizez; z++) {
						worldgenlevel.setBlock(new BlockPos(x, y, z), BlockRegistry.OCTINE_ORE.get().defaultBlockState(), 0);
					}
				}
			}
		} else if (configuration.type == 1) {
			// Sphere (simple trick trying)
			for (int x = originx - sizex; x <= originx + sizex; x++) {
				for (int y = originy - sizey; y <= originy + sizey; y++) {
					for (int z = originz - sizez; z <= originz + sizez; z++) {
						// sphere checker
						if (Math.sqrt(Math.pow(x - originx, 2) + Math.pow(y - originy, 2) + Math.pow(z - originz, 2)) <= sizex) {
							// Air check
							if (worldgenlevel.getBlockState(new BlockPos(x, y, z)) == Blocks.AIR.defaultBlockState()) {
								// Made to work with vegetation that hang from wall sides
								// surface check
								boolean x1 = configuration.west && isTarget(configuration, worldgenlevel, new BlockPos(x - 1, y, z)) && random.nextBoolean();
								boolean x2 = configuration.east && isTarget(configuration, worldgenlevel, new BlockPos(x + 1, y, z)) && random.nextBoolean();
								boolean y1 = configuration.down && isTarget(configuration, worldgenlevel, new BlockPos(x, y - 1, z)) && random.nextBoolean();
								boolean y2 = configuration.up && isTarget(configuration, worldgenlevel, new BlockPos(x, y + 1, z)) && random.nextBoolean();
								boolean z1 = configuration.north && isTarget(configuration, worldgenlevel, new BlockPos(x, y, z - 1)) && random.nextBoolean();
								boolean z2 = configuration.south && isTarget(configuration, worldgenlevel, new BlockPos(x, y, z + 1)) && random.nextBoolean();

								if (x1 || x2 || y1 || y2 || z1 || z2) {
									BlockState input = BlockRegistry.CAVE_MOSS.get().getStateDefinition().any()
										.setValue(BetweenlandsHangingBlock.BOTTOM, true);
									worldgenlevel.setBlock(new BlockPos(x, y, z), input, 0);
								}
							}
						}
					}
				}
			}
		} else if (configuration.type == 2) {
			// Ellipse (just set to sphere for the time being cos im too lazy)
			for (int x = originx - sizex; x <= originx + sizex; x++) {
				for (int y = originy - sizey; y <= originy + sizey; y++) {
					for (int z = originz - sizez; z <= originz + sizez; z++) {
						// sphere checker
						if (Math.sqrt(Math.pow(x - originx, 2) + Math.pow(y - originy, 2) + Math.pow(z - originz, 2)) <= sizex) {
							worldgenlevel.setBlock(new BlockPos(x, y, z), BlockRegistry.OCTINE_ORE.get().defaultBlockState(), 0);
						}
					}
				}
			}
		}


		return true;
	}

	public boolean isTarget(BetweenlandsCaveVegetationConfig configuration, WorldGenLevel level, BlockPos blockpos) {

		// cashe for optimisation
		if (cashe != null && level.getBlockState(blockpos) == cashe) {
			return true;
		}

		// basic sertch
		for (int que = 0; que < configuration.targetStates.size(); que++) {
			if (level.getBlockState(blockpos) == configuration.targetStates.get(que).state) {
				cashe = level.getBlockState(blockpos);
				return true;
			}
		}

		return false;
	}
}
