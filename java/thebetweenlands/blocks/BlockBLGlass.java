package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.creativetabs.BLCreativeTabs;

/**
 * Created by Bart on 16-6-2015.
 */
public class BlockBLGlass extends BlockGlass {
    public BlockBLGlass() {
        super(Material.glass, false);
        this.setBlockName("thebetweenlands.siltGlass");
        this.setCreativeTab(BLCreativeTabs.blocks);
        textureName = "thebetweenlands:siltGlass1";
        setHardness(0.3F);
        setStepSound(soundTypeGlass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon(textureName);
    }

    @Override
    public boolean shouldSideBeRendered (IBlockAccess iblockaccess, int x, int y, int z, int side) {
        Block block = iblockaccess.getBlock(x, y, z);
        return block != BLBlockRegistry.siltGlas;
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
}
