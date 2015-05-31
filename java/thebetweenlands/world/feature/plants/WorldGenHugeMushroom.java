package thebetweenlands.world.feature.plants;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.plants.BlockBlubCappedMushroomHead;
import thebetweenlands.blocks.plants.BlockBlubCappedMushroomStalk;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;

import java.util.Random;

/**
 * Created by Bart on 31-5-2015.
 */
public class WorldGenHugeMushroom extends WorldGenerator {
    private BlockBlubCappedMushroomHead head = (BlockBlubCappedMushroomHead)BLBlockRegistry.hugeMushroomTop;
    private BlockBlubCappedMushroomStalk stalk = (BlockBlubCappedMushroomStalk)BLBlockRegistry.hugeMushroomStalk;

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int height = rand.nextInt(2) + 8;
        int maxRadius = 2;

        for (int xx = x - maxRadius; xx <= x + maxRadius; xx++)
            for (int zz = z - maxRadius; zz <= z + maxRadius; zz++)
                for (int yy = y + 2; yy < y + height; yy++)
                    if (!world.isAirBlock(xx, yy, zz))
                        return false;


        for (int yy = y; yy < y + height; ++yy) {
            world.setBlock(x, yy, z, stalk);

            if(yy == y + height -1)
                generateHead(world, x, yy, z);

        }
        new WorldGenMushrooms(BLBlockRegistry.bulbCappedMushroom, 15).generate(world, rand, x, y, z);
        return true;
    }

    public void generateHead(World world, int x, int y, int z){
        world.setBlock(x, y, z, head);
        world.setBlock(x, y-1, z, head);
        for(int yy = y; yy >= y-4; yy--){
            world.setBlock(x+1, yy, z, head);
            world.setBlock(x-1, yy, z, head);
            world.setBlock(x, yy, z+1, head);
            world.setBlock(x, yy, z-1, head);
            world.setBlock(x+1, yy, z-1, head);
            world.setBlock(x-1, yy, z-1, head);
            world.setBlock(x+1, yy, z+1, head);
            world.setBlock(x-1, yy, z+1, head);
            if(yy >= y-3 && yy <= y-1){
                world.setBlock(x-2, yy, z, head);
                world.setBlock(x+2, yy, z, head);
                world.setBlock(x, yy, z-2, head);
                world.setBlock(x, yy, z+2, head);
                world.setBlock(x-2, yy, z-1, head);
                world.setBlock(x-2, yy, z+1, head);
                world.setBlock(x+2, yy, z-1, head);
                world.setBlock(x+2, yy, z+1, head);
                world.setBlock(x-1, yy, z+2, head);
                world.setBlock(x+1, yy, z+2, head);
                world.setBlock(x-1, yy, z-2, head);
                world.setBlock(x+1, yy, z-2, head);
            }
        }
    }

}
