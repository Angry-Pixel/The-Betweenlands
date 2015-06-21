package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.proxy.ClientProxy;

import java.util.Random;

/**
 * Created by Bart on 17-6-2015.
 */
public class BlockBLFlowerPot extends BlockFlowerPot {
    public IIcon icon;
    public BlockBLFlowerPot(){
        setCreativeTab(ModCreativeTabs.blocks);
        this.setBlockName("thebetweenlands.mudFlowerPot");
        setBlockTextureName("thebetweenlands:mudFlowerPot");
    }

    @Override
    public int getRenderType() {
        return ClientProxy.BlockRenderIDs.MUDFLOWERPOT.id();
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return Item.getItemFromBlock(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        icon = register.registerIcon("thebetweenlands:mudFlowerPot");
    }
}
