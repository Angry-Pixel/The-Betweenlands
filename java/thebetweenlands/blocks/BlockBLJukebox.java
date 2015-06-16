package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockJukebox;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import thebetweenlands.creativetabs.ModCreativeTabs;

/**
 * Created by Bart on 16-6-2015.
 */
public class BlockBLJukebox extends BlockJukebox {
    private IIcon topIcon;

    public BlockBLJukebox(String name){
        this.setBlockName("thebetweenlands." + name + "Jukebox");
        setCreativeTab(ModCreativeTabs.blocks);
        textureName = "thebetweenlands:" + name + "Jukebox";
        setHardness(2.0F);
        setResistance(10.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.topIcon : this.blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon(textureName + "_side");
        this.topIcon = register.registerIcon(textureName + "_top");
    }
}
