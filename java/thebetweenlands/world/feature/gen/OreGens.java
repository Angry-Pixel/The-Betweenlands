package thebetweenlands.world.feature.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;

public class OreGens {
	public static final WorldGenMinableBetweenlands SULFUR = 
			new WorldGenMinableBetweenlands().prepare(BLBlockRegistry.sulfurOre, 0, 14, BLBlockRegistry.betweenstone, true);

	public static final WorldGenMinableBetweenlands SYRMORITE = 
			new WorldGenMinableBetweenlands().prepare(BLBlockRegistry.syrmoriteOre, 0, 6, BLBlockRegistry.betweenstone, true);
	
	public static final WorldGenMinableBetweenlands BONE_ORE = 
			new WorldGenMinableBetweenlands().prepare(BLBlockRegistry.boneOre, 0, 6, BLBlockRegistry.betweenstone, true);

	public static final WorldGenMinableBetweenlands OCTINE = 
			new WorldGenMinableBetweenlands().prepare(BLBlockRegistry.octineOre, 0, 6, BLBlockRegistry.betweenstone, true);

	public static final WorldGenMinableBetweenlands SWAMP_DIRT = 
			new WorldGenMinableBetweenlands().prepare(BLBlockRegistry.swampDirt, 0, 25, BLBlockRegistry.betweenstone, true);

	public static final WorldGenMinableBetweenlands LIMESTONE = 
			new WorldGenMinableBetweenlands().prepare(BLBlockRegistry.limestone, 0, 100, BLBlockRegistry.betweenstone, true);

	public static final WorldGenMinableBetweenlands VALONITE = 
			new WorldGenMinableBetweenlands().prepare(BLBlockRegistry.valoniteOre, 0, 2, BLBlockRegistry.pitstone, false);

	public static final WorldGenMinableBetweenlands SCABYST = 
			new WorldGenMinableBetweenlands().prepare(BLBlockRegistry.scabystOre, 0, 2, BLBlockRegistry.pitstone, false);
	
	public static final WorldGenerator LIFE_GEM = 
			new WorldGenerator() {
		@Override
		public boolean generate(World world, Random rand, int x, int y, int z) {
			if(world.getBlock(x, y, z) == BLBlockRegistry.swampWater && world.getBlock(x, y-1, z) == BLBlockRegistry.pitstone) {
				boolean genOre = rand.nextInt(14) == 0;
				int height = 0;
				while(world.getBlock(x, y + (++height), z) == BLBlockRegistry.swampWater && height < 8);
				height--;
				if(height >= 2) {
					height = rand.nextInt(Math.min(height - 1, 6)) + 2;
					int oreBlock = rand.nextInt(height);
					for(int i = 0; i <= height; i++) {
						world.setBlock(x, y + i, z, BLBlockRegistry.lifeCrystalOre, genOre && (i == oreBlock || rand.nextInt(18) == 0) ? 1 : 0, 2);
					}
				}
			}
			return false;
		}
	};
}
