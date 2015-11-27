package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockBed;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.proxy.ClientProxy;
import thebetweenlands.utils.confighandler.ConfigHandler;

/**
 * Created by Bart on 22/11/2015.
 */
public class BlockMossBed extends BlockBed {


    public IIcon bedIcon;

    public BlockMossBed() {
        setBlockName("thebetweenlands.mossBed");
    }


    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        } else if (player.dimension == ConfigHandler.DIMENSION_ID) {
            player.setSpawnChunk(new ChunkCoordinates(x, y, z), true, ConfigHandler.DIMENSION_ID);
            return true;
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.bedIcon = iconRegister.registerIcon("thebetweenlands:mossBed");
    }

    @Override
    public int getRenderBlockPass() {
        return ClientProxy.BlockRenderIDs.MOSS_BED.id();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return this.blockIcon;
    }

    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
        return Item.getItemFromBlock(this);
    }
}
