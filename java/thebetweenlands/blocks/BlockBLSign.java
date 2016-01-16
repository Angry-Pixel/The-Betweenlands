package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockSign;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.proxy.ClientProxy;
import thebetweenlands.tileentities.TileEntityBLSign;

/**
 * Created by Bart on 16/01/2016.
 */
public class BlockBLSign extends BlockSign {
    public String material;
    protected BlockBLSign(boolean standing, String material) {
        super(TileEntityBLSign.class, standing);
        this.setBlockName("thebetweenlands." + material + "Sign" + (standing ? "Standing" : "Wall"));
        disableStats();
        this.material = material;
    }

    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
        return BLItemRegistry.weedwoodSign;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon("thebetweenlands:" + material + "Planks");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int meta, int side) {
        return blockIcon;
    }
}
