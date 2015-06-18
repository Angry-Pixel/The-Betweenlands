package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockJukebox;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;

/**
 * Created by Bart on 16-6-2015.
 */
public class BlockBLJukebox extends BlockJukebox {
    private IIcon topIcon;

    public BlockBLJukebox(String name) {
        this.setBlockName("thebetweenlands." + name + "Jukebox");
        setCreativeTab(ModCreativeTabs.blocks);
        textureName = "thebetweenlands:" + name + "Jukebox";
        setHardness(2.0F);
        setResistance(10.0F);
        this.setStepSound(soundTypeWood);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int meta, float hitX, float hitY, float hitZ) {
        if (world.getBlockMetadata(x, y, z) == 0 ) {
            if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemRecord) {
                ((BlockJukebox) Blocks.jukebox).func_149926_b(world, x, y, z, player.getHeldItem());
                world.playAuxSFXAtEntity((EntityPlayer) null, 1005, x, y, z, Item.getIdFromItem(player.getHeldItem().getItem()));
                --player.getHeldItem().stackSize;
                return true;
            }
            else
                return false;
        } else {
            this.func_149925_e(world, x, y, z);
            return true;
        }
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
