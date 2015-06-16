package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import thebetweenlands.creativetabs.ModCreativeTabs;


/**
 * Created by Bart on 16-6-2015.
 */
public class BlockBLPane extends BlockPane {
    public IIcon iconSide;

    public BlockBLPane(String name, Material material, boolean dropItem) {
        super(name, name, material, dropItem);
        setBlockName("thebetweenlands." + name + "Pane");
        textureName = "thebetweenlands:" + name;
        this.setCreativeTab(ModCreativeTabs.blocks);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon(textureName + "1");
        this.iconSide = register.registerIcon(textureName + "2");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon func_150097_e()
    {
        return this.iconSide;
    }
}
