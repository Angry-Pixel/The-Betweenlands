package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.DecorationHelper;
import thebetweenlands.common.world.gen.biome.decorator.DecoratorPositionProvider;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class WorldGenRootPodRoots extends WorldGenerator {
	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		if(SurfaceType.PLANT_DECORATION_SOIL.matches(world, position.down())) {
			if(this.generateRootsStack(world, rand, position)) {
				DecoratorPositionProvider provider = new DecoratorPositionProvider();
				provider.init(world, world.getBiome(position), null, rand, position.getX(), position.getY(), position.getZ());
				provider.setOffsetXZ(-3, 3);
				provider.setOffsetY(-1, 1);

				for(int i = 0; i < 10; i++) {
					DecorationHelper.generateSwampDoubleTallgrass(provider);
					DecorationHelper.generateTallCattail(provider);
					DecorationHelper.generateSwampTallgrassCluster(provider);
					if(rand.nextInt(5) == 0) {
						DecorationHelper.generateCattailCluster(provider);
					}
					if(rand.nextInt(3) == 0) {
						DecorationHelper.generateShootsCluster(provider);
					}
				}
				
				world.setBlockState(position.down(), BlockRegistry.GIANT_ROOT.getDefaultState());
				
				for(EnumFacing facing : EnumFacing.HORIZONTALS) {
					BlockPos offset = position.offset(facing).down();
					if(SurfaceType.PLANT_DECORATION_SOIL.apply(world.getBlockState(offset))) {
						world.setBlockState(offset, BlockRegistry.GIANT_ROOT.getDefaultState());
					}
				}
				
				for(int i = 0; i < 32; i++) {
					int rx = rand.nextInt(7) - 3;
					int rz = rand.nextInt(7) - 3;
					if(rx != 0 || rz != 0) {
						BlockPos offset = position.add(rx, rand.nextInt(3) - 2, rz);
						if(SurfaceType.PLANT_DECORATION_SOIL.apply(world.getBlockState(offset))) {
							world.setBlockState(offset, BlockRegistry.GIANT_ROOT.getDefaultState());
						}
					}
				}
				
				return true;
			}
		}

		return false;
	}

	private boolean generateRootsStack(World world, Random rand, BlockPos pos) {
		int height = 6;
		MutableBlockPos checkPos = new MutableBlockPos();
		for(int yo = 0; yo < 6; yo++) {
			checkPos.setPos(pos.getX(), pos.getY() + yo, pos.getZ());
			if(!world.isAirBlock(checkPos)) {
				height = yo;
				break;
			}
		}
		if(height < 2) {
			return false;
		}
		height = rand.nextInt(height) + 1 + rand.nextInt(4);
		for(int yo = 0; yo < height; yo++) {
			BlockPos offsetPos = pos.add(0, yo, 0);
			if(!world.isAirBlock(offsetPos)) {
				break;
			}
			world.setBlockState(offsetPos, BlockRegistry.ROOT.getDefaultState(), 2);
		}
		return true;
	}
}
