package thebetweenlands.world.feature.gen;

import thebetweenlands.blocks.BLBlockRegistry;

public class OreGens {
	public static final WorldGenMinableBetweenlands SULFUR = 
			new WorldGenMinableBetweenlands().prepare(BLBlockRegistry.sulfurOre, 0, 16, BLBlockRegistry.betweenstone);
	
	public static final WorldGenMinableBetweenlands BLURITE = 
			new WorldGenMinableBetweenlands().prepare(BLBlockRegistry.bluriteOre, 0, 8, BLBlockRegistry.betweenstone);
	
	public static final WorldGenMinableBetweenlands OCTINE = 
			new WorldGenMinableBetweenlands().prepare(BLBlockRegistry.octineOre, 0, 8, BLBlockRegistry.betweenstone);
	
	public static final WorldGenMinableBetweenlands VALONITE = 
			new WorldGenMinableBetweenlands().prepare(BLBlockRegistry.valoniteOre, 0, 8, BLBlockRegistry.betweenstone);
	
	public static final WorldGenMinableBetweenlands LIFE_GEM = 
			new WorldGenMinableBetweenlands().prepare(BLBlockRegistry.lifeCrystalOre, 0, 7, BLBlockRegistry.betweenstone);
}
