package thebetweenlands.blocks.plants;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class BlockBlubCappedMushroomHead extends Block {
    public BlockBlubCappedMushroomHead() {
        super(Material.wood);
        setBlockName("thebetweenlands.hugeMushroomTop");
        setBlockTextureName("thebetweenlands:bulbCappedShroomCap");
        setCreativeTab(ModCreativeTabs.blocks);
        setLightLevel(1.0F);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    @Override
    public int getRenderBlockPass ()
    {
        return 1;
    }
    @Override
    public boolean shouldSideBeRendered (IBlockAccess iblockaccess, int x, int y, int z, int side)
    {
        Block block = iblockaccess.getBlock(x, y, z);
        if (block == BLBlockRegistry.hugeMushroomTop)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

}
