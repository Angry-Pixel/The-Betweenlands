package thebetweenlands.blocks.plants.roots;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.blocks.BLBlockRegistry;

public class RootData
{
    public int maxLength = 32;
    public int posX, posY, posZ;
    public boolean noTop, noBottom;
    public int distUp, distDown;

    public void gatherData(IBlockAccess world, int x, int y, int z)
    {
        posX = x;
        posY = y;
        posZ = z;

        Block block;
        for(distUp = 0; distUp < maxLength; distUp++)
        {
            block = world.getBlock(x, y + 1 + distUp, z);
            if(block == BLBlockRegistry.rootUW || block == BLBlockRegistry.root) continue;
            if(block == Blocks.air || !block.isOpaqueCube()) noTop = true;
            break;
        }
        for(distDown = 0; distDown < maxLength; distDown++)
        {
            block = world.getBlock(x, y - 1 - distDown, z);
            if(block == BLBlockRegistry.rootUW || block == BLBlockRegistry.root) continue;
            if(block == Blocks.air || !block.isOpaqueCube()) noBottom = true;
            break;
        }
    }

    public static RootData getData(IBlockAccess blockAccess, int x, int y, int z)
    {
        RootData info = new RootData();
        info.gatherData(blockAccess, x, y, z);
        return info;
    }
}