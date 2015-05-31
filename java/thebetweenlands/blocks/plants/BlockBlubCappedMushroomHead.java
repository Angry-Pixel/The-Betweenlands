package thebetweenlands.blocks.plants;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;

import java.util.Random;

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
        return block != BLBlockRegistry.hugeMushroomTop;
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(BLBlockRegistry.bulbCappedMushroom);
    }

    @Override
    public void dropBlockAsItemWithChance (World world, int x, int y, int z, int meta, float chance, int fortune) {
        if (!world.isRemote) {
            int dropChance = 6;

            if (fortune > 0){
                dropChance -= 2*fortune;
            }
            if(world.rand.nextInt(dropChance) == 0) {
                this.dropBlockAsItem(world, x, y, z, new ItemStack(this.getItemDropped(meta, world.rand, fortune), 1));
            }
        }
    }

}
