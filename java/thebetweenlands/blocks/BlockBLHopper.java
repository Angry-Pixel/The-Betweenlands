package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.proxy.ClientProxy;

/**
 * Created by Bart on 20/01/2016.
 */
public class BlockBLHopper extends BlockHopper {

    @SideOnly(Side.CLIENT)
    public IIcon outside;
    @SideOnly(Side.CLIENT)
    public IIcon top;
    @SideOnly(Side.CLIENT)
    public IIcon inside;
    public String material;

    public BlockBLHopper(String material) {
        setBlockName("thebetweelands." + material + "Hopper");
        setCreativeTab(BLCreativeTabs.blocks);
        this.material = material;
        setHardness(3.0F);
        setResistance(8.0F);
        setStepSound(soundTypeWood);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.outside = iconRegister.registerIcon("thebetweenlands:" + material + "HopperOutside");
        this.top = iconRegister.registerIcon("thebetweenlands:" + material + "HopperTop");
        this.inside = iconRegister.registerIcon("thebetweenlands:" + material + "HopperInside");
    }


    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.top : this.outside;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getItemIconName() {
        return "thebetweenlands:" + material + "Hopper";
    }

    @Override
    public int getRenderType() {
        return ClientProxy.BlockRenderIDs.HOPPER.id();
    }

    @SideOnly(Side.CLIENT)
    public static IIcon getHopperIcon(String side) {
        return side.equals("hopper_outside") ? BLBlockRegistry.syrmoriteHopper.outside : (side.equals("hopper_inside") ? BLBlockRegistry.syrmoriteHopper.inside : null);
    }
}
