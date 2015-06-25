package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import thebetweenlands.blocks.tree.BlockBLSapling;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.proxy.ClientProxy;

import java.util.Random;

/**
 * Created by Bart on 17-6-2015.
 */
public class BlockBLFlowerPot extends BlockFlowerPot {
    public IIcon icon;

    public BlockBLFlowerPot() {
        this.setBlockName("thebetweenlands.mudFlowerPot");
        setBlockTextureName("thebetweenlands:mudFlowerPot");
    }

    @Override
    public int getRenderType() {
        return ClientProxy.BlockRenderIDs.MUDFLOWERPOT.id();
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return BLItemRegistry.mudFlowerPot;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        icon = register.registerIcon("thebetweenlands:mudFlowerPot");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        Object object = null;
        byte b0 = 0;

        switch (meta) {
            case 1:
                object = Blocks.red_flower;
                b0 = 0;
                break;
            case 2:
                object = Blocks.yellow_flower;
                break;
            case 3:
                object = Blocks.sapling;
                b0 = 0;
                break;
            case 4:
                object = Blocks.sapling;
                b0 = 1;
                break;
            case 5:
                object = Blocks.sapling;
                b0 = 2;
                break;
            case 6:
                object = Blocks.sapling;
                b0 = 3;
                break;
            case 7:
                object = Blocks.red_mushroom;
                break;
            case 8:
                object = Blocks.brown_mushroom;
                break;
            case 9:
                object = Blocks.cactus;
                break;
            case 10:
                object = Blocks.deadbush;
                break;
            case 11:
                object = Blocks.tallgrass;
                b0 = 2;
                break;
            case 12:
                object = Blocks.sapling;
                b0 = 4;
                break;
            case 13:
                object = Blocks.sapling;
                b0 = 5;
                break;
            case 14:
                object = BLBlockRegistry.cardinalFlower;
                break;
            case 15:
                object = BLBlockRegistry.waterFlower;
                break;
        }

        return new TileEntityFlowerPot(Item.getItemFromBlock((Block) object), b0);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int meta, float hitX, float hitY, float hitZ) {
        ItemStack itemstack = player.inventory.getCurrentItem();

        if (itemstack != null && itemstack.getItem() instanceof ItemBlock) {
            TileEntityFlowerPot tileentityflowerpot = this.func_149929_e(world, x, y, z);

            if (tileentityflowerpot != null) {
                if (tileentityflowerpot.getFlowerPotItem() != null) {
                    return false;
                } else {
                    Block block = Block.getBlockFromItem(itemstack.getItem());

                    if (!this.func_149928_a(block, itemstack.getItemDamage())) {
                        return false;
                    } else {
                        tileentityflowerpot.func_145964_a(itemstack.getItem(), itemstack.getItemDamage());
                        tileentityflowerpot.markDirty();

                        if (!world.setBlockMetadataWithNotify(x, y, z, itemstack.getItemDamage(), 2)) {
                            world.markBlockForUpdate(x, y, z);
                        }

                        if (!player.capabilities.isCreativeMode && --itemstack.stackSize <= 0) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack) null);
                        }

                        return true;
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    private TileEntityFlowerPot func_149929_e(World world, int x, int y, int z) {
        TileEntity tileentity = world.getTileEntity(x, y, z);
        return tileentity != null && tileentity instanceof TileEntityFlowerPot ? (TileEntityFlowerPot) tileentity : null;
    }

    private boolean func_149928_a(Block block, int meta) {
        return !(block != BLBlockRegistry.boneset && block != BLBlockRegistry.marshMallow && block != BLBlockRegistry.nettle && block != BLBlockRegistry.nettleFlowered && block != BLBlockRegistry.buttonBush && block != BLBlockRegistry.milkweed && block != BLBlockRegistry.copperIris && block != BLBlockRegistry.blueIris && block != BLBlockRegistry.waterFlower && !(block instanceof BlockBLSapling) && block != BLBlockRegistry.marshHibiscus && block != BLBlockRegistry.pickerelWeed && block != Blocks.yellow_flower && block != Blocks.red_flower && block != Blocks.cactus && block != Blocks.brown_mushroom && block != Blocks.red_mushroom && block != Blocks.sapling && block != Blocks.deadbush) || block == Blocks.tallgrass && meta == 2;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        if (world.getTileEntity(x, y, z) instanceof TileEntityFlowerPot) {
            TileEntityFlowerPot te = ((TileEntityFlowerPot) world.getTileEntity(x, y, z));
            if (te.getFlowerPotItem() != null)
                return new ItemStack(te.getFlowerPotItem());

        }
        return new ItemStack(BLItemRegistry.mudFlowerPot);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
        return true;
    }
}
