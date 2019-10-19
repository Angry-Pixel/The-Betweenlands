package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.container.BlockLootPot;
import thebetweenlands.common.block.container.BlockLootPot.EnumLootPot;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.common.registries.LootTableRegistry;

public class WorldGenCavePots extends WorldGenerator {
	public WorldGenCavePots() {
		super(false);
	}

	private IBlockState getRandomPot(Random rand) {
		switch (rand.nextInt(3)) {
		case 0:
			return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, EnumLootPot.POT_1);
		case 1:
			return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, EnumLootPot.POT_2);
		case 2:
			return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, EnumLootPot.POT_3);
		default:
			return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, EnumLootPot.POT_1);
		}
	}

	@Override
	public boolean generate(World world, Random random, BlockPos pos) {
		if (pos.getY() > 70)
			return false;
		for (int xx = -2; xx <= 2; xx++) {
			for (int zz = -2; zz <= 2; zz++) {
				for(int yy = -1; yy <= 1; yy++) {
					double dst = Math.sqrt(xx*xx+yy*yy+zz*zz);
					if (random.nextInt(MathHelper.ceil(dst / 1.4D) + 1) == 0) {
						BlockPos offsetPos = pos.add(xx, yy, zz);
						if(world.isAirBlock(offsetPos)) {
							IBlockState surfaceBlock = world.getBlockState(offsetPos.down());
							if(surfaceBlock.getBlock() == BlockRegistry.BETWEENSTONE || surfaceBlock == BlockRegistry.PITSTONE) {
								this.setBlockAndNotifyAdequately(world, offsetPos, this.getRandomPot(random));
								TileEntityLootPot lootPot = BlockLootPot.getTileEntity(world, offsetPos);
								if(lootPot != null) {
									lootPot.setLootTable(LootTableRegistry.CAVE_POT, random.nextLong());
								}
							}
						}
					}
				}
			}
		}
		return true;
	}
}
