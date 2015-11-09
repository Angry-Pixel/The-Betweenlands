package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockJukebox;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.manual.gui.entries.IManualEntryItem;

/**
 * Created by Bart on 16-6-2015.
 */
public class BlockBLJukebox extends BlockJukebox implements IManualEntryItem {
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
            if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemRecord) {
                ((BlockJukebox) Blocks.jukebox).func_149926_b(world, x, y, z, player.getHeldItem());
                world.playAuxSFXAtEntity((EntityPlayer) null, 1005, x, y, z, Item.getIdFromItem(player.getHeldItem().getItem()));
                --player.getHeldItem().stackSize;
                return true;
            } else if (player.getHeldItem() != null && (player.getHeldItem().getItem() == BLItemRegistry.gertsDonut || player.getHeldItem().getItem() == BLItemRegistry.jamDonut || player.getHeldItem().getItem() == BLItemRegistry.reedDonut)) {
                ((BlockJukebox) Blocks.jukebox).func_149926_b(world, x, y, z, new ItemStack(player.getHeldItem().getItem(), 1, player.getHeldItem().getItemDamage()));
                --player.getHeldItem().stackSize;
                if (world.isRemote)
                    player.addChatMessage(new ChatComponentText("DOH!"));
                return true;
            } else
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
    public String manualName(int meta) {
        return "weedwoodJukebox";
    }

    @Override
    public Item getItem() {
        return Item.getItemFromBlock(this);
    }

    @Override
    public int[] recipeType(int meta) {
        return new int[]{2};
    }

    @Override
    public int[] metas( ) {
        return new int[0];
    }
}
