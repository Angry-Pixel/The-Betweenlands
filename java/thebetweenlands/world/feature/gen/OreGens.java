package thebetweenlands.world.feature.gen;

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
			new WorldGenMinableBetweenlands().prepare(BLBlockRegistry.lifeCrystalOre, 0, 1, BLBlockRegistry.pitstone, false);
}
