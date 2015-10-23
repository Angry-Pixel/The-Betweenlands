package thebetweenlands.world.feature.gen;

import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;

public class OreGens {
	public static final WorldGenMinableBetweenlands SULFUR = 
			new WorldGenMinableBetweenlands().prepare(BLBlockRegistry.sulfurOre, 0, 16, BLBlockRegistry.betweenstone, true);

	public static final WorldGenMinableBetweenlands SYRMORITE = 
			new WorldGenMinableBetweenlands().prepare(BLBlockRegistry.syrmoriteOre, 0, 8, BLBlockRegistry.betweenstone, true);

	public static final WorldGenMinableBetweenlands OCTINE = 
			new WorldGenMinableBetweenlands().prepare(BLBlockRegistry.octineOre, 0, 8, BLBlockRegistry.betweenstone, true);

	public static final WorldGenMinableBetweenlands VALONITE = 
			new WorldGenMinableBetweenlands().prepare(BLBlockRegistry.valoniteOre, 0, 4, BLBlockRegistry.pitstone, false);

	public static final WorldGenMinableBetweenlands LIFE_GEM = 
			new WorldGenMinableBetweenlands() {
		@Override
		protected boolean canGenerate(World world, int x, int y, int z) {
			boolean canGen = world.getBlock(x+1, y, z) == BLBlockRegistry.swampWater
					|| world.getBlock(x-1, y, z) == BLBlockRegistry.swampWater
					|| world.getBlock(x, y-1, z) == BLBlockRegistry.swampWater
					|| world.getBlock(x, y, z+1) == BLBlockRegistry.swampWater
					|| world.getBlock(x, y, z-1) == BLBlockRegistry.swampWater;
			return canGen;
		}
	}.prepare(BLBlockRegistry.lifeCrystalOre, 0, 3, BLBlockRegistry.pitstone, false);
}
