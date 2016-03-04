package thebetweenlands.blocks.structure;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.creativetabs.BLCreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSwordStone extends Block {
	public BlockSwordStone() {
		super(Material.rock);
		setHardness(1.5F);
		setResistance(10.0F);
		setStepSound(soundTypeStone);
		setHarvestLevel("pickaxe", 0);
		setLightLevel(0.8F);
		setCreativeTab(BLCreativeTabs.blocks);
		setBlockName("thebetweenlands.swordStone");
		setBlockTextureName("thebetweenlands:polishedDentrothyst21"); //temp spam reduction 
	}

	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon(textureName);
    }

    @Override
    public boolean shouldSideBeRendered (IBlockAccess iblockaccess, int x, int y, int z, int side) {
        Block block = iblockaccess.getBlock(x, y, z);
        return block != this;
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
    public int getRenderBlockPass () {
        return 1;
    }

}
