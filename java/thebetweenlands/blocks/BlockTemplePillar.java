package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.creativetabs.BLCreativeTabs;

public class BlockTemplePillar extends BlockRotatedPillar {
    String textureNameBottom;

    public BlockTemplePillar(String name, String textureNameBottom) {
        super(Material.rock);
        setHardness(1.5F);
        setResistance(10.0F);
        setStepSound(soundTypeStone);
        setCreativeTab(BLCreativeTabs.blocks);
        setBlockName("thebetweenlands." + name);
        setBlockTextureName("thebetweenlands:" + name);
        this.textureNameBottom = textureNameBottom;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        switch (meta) {
            case 0:
            case 1:
            case 2:
            case 3:
                this.setBlockBounds(0.0625f, 0f, 0.0625f, .9375f, 1f, .9375f);
                break;
            case 4:
            case 5:
            case 6:
            case 7:
                this.setBlockBounds(0f, 0.0625f, 0.0625f, 1f, .9375f, .9375f);
                break;
            case 8:
            case 9:
            case 10:
            case 11:
                this.setBlockBounds(0.0625f, 0.0625f, 0f, .9375f, .9375f, 1f);
                break;
        }
    }

    @Override
    public boolean isBlockSolid(IBlockAccess p_149747_1_, int p_149747_2_, int p_149747_3_, int p_149747_4_, int p_149747_5_) {
        return false;
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
    @SideOnly(Side.CLIENT)
    protected IIcon getSideIcon(int side) {
        return blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        super.registerBlockIcons(reg);
        field_150164_N = reg.registerIcon("thebetweenlands:" + textureNameBottom);
    }
}