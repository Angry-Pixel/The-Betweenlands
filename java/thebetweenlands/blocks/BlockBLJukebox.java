package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockJukebox;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
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
        if (world.getBlockMetadata(x, y, z) == 0) {
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

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int meta) {
        ItemStack itemstack = ((BlockBLJukebox.TileEntityBLJukebox) world.getTileEntity(x, y, z)).func_145856_a();
        return itemstack == null ? 0 : Item.getIdFromItem(itemstack.getItem()) + 1 - Item.getIdFromItem(Items.record_13);
    }

    @Override
    public void func_149926_b(World world, int x, int y, int z, ItemStack itemStack) {
        if (!world.isRemote) {
            BlockBLJukebox.TileEntityBLJukebox tileentityjukebox = (BlockBLJukebox.TileEntityBLJukebox) world.getTileEntity(x, y, x);

            if (tileentityjukebox != null) {
                tileentityjukebox.func_145857_a(itemStack.copy());
                world.setBlockMetadataWithNotify(x, y, x, 1, 2);
            }
        }
    }

    @Override
    public void func_149925_e(World world, int x, int y, int z) {
        if (!world.isRemote) {
            BlockBLJukebox.TileEntityBLJukebox tileentityjukebox = (BlockBLJukebox.TileEntityBLJukebox) world.getTileEntity(x, y, z);

            if (tileentityjukebox != null) {
                ItemStack itemstack = tileentityjukebox.func_145856_a();

                if (itemstack != null) {
                    world.playAuxSFX(1005, x, y, z, 0);
                    world.playRecord((String) null, x, y, z);
                    tileentityjukebox.func_145857_a((ItemStack) null);
                    world.setBlockMetadataWithNotify(x, y, z, 0, 2);
                    float f = 0.7F;
                    double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                    double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.2D + 0.6D;
                    double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                    ItemStack itemstack1 = itemstack.copy();
                    EntityItem entityitem = new EntityItem(world, (double) x + d0, (double) y + d1, (double) z + d2, itemstack1);
                    entityitem.delayBeforeCanPickup = 10;
                    world.spawnEntityInWorld(entityitem);
                }
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new BlockBLJukebox.TileEntityBLJukebox();
    }

    public static class TileEntityBLJukebox extends TileEntity {
        private ItemStack itemStack;

        public void readFromNBT(NBTTagCompound nbtTagCompound) {
            super.readFromNBT(nbtTagCompound);

            if (nbtTagCompound.hasKey("RecordItem", 10)) {
                this.func_145857_a(ItemStack.loadItemStackFromNBT(nbtTagCompound.getCompoundTag("RecordItem")));
            } else if (nbtTagCompound.getInteger("Record") > 0) {
                this.func_145857_a(new ItemStack(Item.getItemById(nbtTagCompound.getInteger("Record")), 1, 0));
            }
        }

        public void writeToNBT(NBTTagCompound nbtTagCompound) {
            super.writeToNBT(nbtTagCompound);

            if (this.func_145856_a() != null) {
                nbtTagCompound.setTag("RecordItem", this.func_145856_a().writeToNBT(new NBTTagCompound()));
                nbtTagCompound.setInteger("Record", Item.getIdFromItem(this.func_145856_a().getItem()));
            }
        }

        public ItemStack func_145856_a() {
            return this.itemStack;
        }

        public void func_145857_a(ItemStack itemStack) {
            this.itemStack = itemStack;
            this.markDirty();
        }
    }
}
