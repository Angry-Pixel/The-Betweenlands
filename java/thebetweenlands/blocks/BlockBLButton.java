package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockButton;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

/**
 * Created by Bart on 16-6-2015.
 */
public class BlockBLButton extends BlockButton {
    public BlockBLButton( String name, boolean shootByArrow) {
        super(shootByArrow);
        this.setBlockName("thebetweenlands." + name + "Button");
        textureName = "thebetweenlands:" + name;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon(textureName);
    }

}
