package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.creativetabs.ModCreativeTabs;

/**
 * Created by Bart on 16-6-2015.
 */
public class BlockTotem extends Block {
    public IIcon iconTop;
    public IIcon iconSide;


    public BlockTotem() {
        super(Material.wood);
        this.setBlockName("thebetweenlands.totem");
        this.setCreativeTab(ModCreativeTabs.blocks);
        textureName = "thebetweenlands:" + "totem";
        setHardness(2.0F);
        setResistance(5.0F);
    }

    @Override
    public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side)  {
        return side.equals(ForgeDirection.UP);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister icon) {
        iconTop = icon.registerIcon(textureName + "_top");
        iconSide = icon.registerIcon(textureName + "_side");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if(side == 0 || side == 1)
            return iconTop;
        else
            return iconSide;
    }
}
