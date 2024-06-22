package thebetweenlands.common.world.gen.biome.decorator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.feature.legacy.*;

public class DecorationHelper {

	// DEBUG
	public static final WorldGenHelper GEN_PLACEMENT_DEBUG = new WorldGenDebug();

	public static final WorldGenHelper GEN_NIBBLETWIG_TREE = new WorldGenNibbletwigTree();
	public static final WorldGenHelper GEN_WEEDWOOD_TREE = new WorldGenWeedwoodTree();
	public static final WorldGenHelper GEN_HEARTHGROVE_TREE = new WorldGenHearthgroveTree();
	public static final WorldGenRubberTree GEN_RUBBER_TREE = new WorldGenRubberTree();
	public static final WorldGenSapTree GEN_SAP_TREE = new WorldGenSapTree();
	public static final WorldGenBigBulbCappedMushroom GEN_BIG_BULB_CAPPED_MUSHROOM = new WorldGenBigBulbCappedMushroom();
	//public static final WorldGenHelper GEN_BIG_BULB_CAPPED_MUSHROOM = new WorldGenBigBulbCappedMushroom();

	// DEBUG
	public static boolean generateBiomeDebug(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		WorldGenLevel world = decorator.getWorld();
		return GEN_PLACEMENT_DEBUG.generate(world, decorator.getRand(), pos);
	}

	private static boolean canShortThingsGenerateHere(DecoratorPositionProvider decorator) {
		return ((ChunkGeneratorBetweenlands) decorator.getChunkGenerator()).evalTreeNoise(decorator.getX() * 0.01, decorator.getZ() * 0.01) > -0.25;
	}

	public static boolean generateWeedwoodTree(DecoratorPositionProvider decorator) {
		if (decorator.generator != null) {
			if (canShortThingsGenerateHere(decorator)) {
				BlockPos pos = decorator.getRandomPos(14);
				WorldGenLevel world = decorator.getWorld();
				if (world.getBlockState(pos).getBlock() == Blocks.AIR && SurfaceType.GRASS.matches(world, pos.below())) {
					return GEN_WEEDWOOD_TREE.generate(world, decorator.getRand(), pos);
				}
			}
		}
		return false;
	}

	public static boolean generateNibbletwigTree(DecoratorPositionProvider decorator) {
		if (decorator.generator != null) {
			if (canShortThingsGenerateHere(decorator)) {
				BlockPos pos = decorator.getRandomPos(10);
				WorldGenLevel world = decorator.getWorld();
				if (world.getBlockState(pos).getBlock() == Blocks.AIR && SurfaceType.GRASS.matches(world, pos.below())) {
					return GEN_NIBBLETWIG_TREE.generate(world, decorator.getRand(), pos);
				}
			}
		}
		return false;
	}

	public static boolean generateHearthgroveTree(DecoratorPositionProvider decorator) {
		if (decorator.generator != null) {
			if (canShortThingsGenerateHere(decorator)) {
				BlockPos pos = decorator.getRandomPos(12);
				WorldGenLevel world = decorator.getWorld();
				// TODO: find replacment for instanceof EmptyFluid check
				if ((world.getBlockState(pos).getBlock() == Blocks.AIR && SurfaceType.GRASS.matches(world, pos.below())) ||
					(world.getBlockState(pos).getBlock() == BlockRegistry.SWAMP_WATER_BLOCK.get() && SurfaceType.DIRT.matches(world, pos.below()))) {
					return GEN_HEARTHGROVE_TREE.generate(world, decorator.getRand(), pos);
				}
			}
		}
		return false;
	}

	public static boolean generateRubberTree(DecoratorPositionProvider decorator) {
		if (decorator.generator != null) {
			BlockPos pos = decorator.getRandomPos();
			WorldGenLevel world = decorator.getWorld();
			if ((world.getBlockState(pos).getBlock() == Blocks.AIR && SurfaceType.GRASS_AND_DIRT.matches(world, pos.below()))) {
				return GEN_RUBBER_TREE.generate(world, decorator.getRand(), pos);
			}
		}
		return false;
	}

	public static boolean generateSapTree(DecoratorPositionProvider decorator) {
		if (decorator.generator != null) {
			BlockPos pos = decorator.getRandomPos();
			WorldGenLevel world = decorator.getWorld();
			if ((world.getBlockState(pos).getBlock() == Blocks.AIR && SurfaceType.GRASS_AND_DIRT.matches(world, pos.below()))) {
				return GEN_SAP_TREE.generate(world, decorator.getRand(), pos);
			}
		}
		return false;
	}

	public static boolean generateBigBulbCappedMushroomTree(DecoratorPositionProvider decorator) {
		if (decorator.generator != null) {
			BlockPos pos = decorator.getRandomPos();
			WorldGenLevel world = decorator.getWorld();
			if ((world.getBlockState(pos).getBlock() == Blocks.AIR && SurfaceType.GRASS_AND_DIRT.matches(world, pos.below()))) {
				return GEN_BIG_BULB_CAPPED_MUSHROOM.generate(world, decorator.getRand(), pos);
			}
		}
		return false;
	}
}
