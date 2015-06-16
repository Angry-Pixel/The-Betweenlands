package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import thebetweenlands.creativetabs.ModCreativeTabs;

/**
 * Created by Bart on 16-6-2015.
 */
public class BlockBLFenceGate extends BlockFenceGate {

    public BlockBLFenceGate(String name) {
        super();
        this.setBlockName("thebetweenlands." + name + "FenceGate");
        setCreativeTab(ModCreativeTabs.blocks);
        textureName = "thebetweenlands:" + name;
        setHardness(2.0F);
        setResistance(5.0F);
        this.setStepSound(soundTypeWood);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister icon) {
        blockIcon = icon.registerIcon(textureName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return blockIcon;
    }
}
