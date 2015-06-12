package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.proxy.ClientProxy;

/**
 * Created by Bart on 12-6-2015.
 */
public class BlockWalkway extends Block {
    public IIcon icon;

    public BlockWalkway() {
        super(Material.wood);
        this.setBlockName("thebetweenlands.walkway");
        this.setCreativeTab(ModCreativeTabs.blocks);
    }

    @Override
    public int getRenderType() {
        return ClientProxy.BlockRenderIDs.WALKWAY.id();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        icon = register.registerIcon("thebetweenlands:walkway");
    }
}
