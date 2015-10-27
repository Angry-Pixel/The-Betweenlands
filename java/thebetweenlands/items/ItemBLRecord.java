package thebetweenlands.items;

import net.minecraft.block.BlockJukebox;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bart on 25-10-2015.
 */
public class ItemBLRecord extends ItemRecord {
    private static final Map records = new HashMap();

    public final String recordName;

    public ItemBLRecord(String name) {
        super(name);
        this.recordName = name;
        this.maxStackSize = 1;
        records.put(recordName, this);
        this.setUnlocalizedName("thebetweenlands.record" + name);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon("thebetweenlands:record" + recordName);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {

        if (world.getBlock(x, y, z) instanceof BlockJukebox && world.getBlockMetadata(x, y, z) == 0) {
            if (world.isRemote)
                return true;
            else {
                ((BlockJukebox) Blocks.jukebox).func_149926_b(world, x, y, z, itemStack);
                world.playAuxSFXAtEntity((EntityPlayer) null, 1005, x, y, z, Item.getIdFromItem(this));
                --itemStack.stackSize;
                return true;
            }
        } else
            return false;
    }

    @Override
    public String getRecordNameLocal() {
        return StatCollector.translateToLocal(this.getUnlocalizedName() + ".desc");
    }


    public static ItemBLRecord getRecord(String name) {
        return (ItemBLRecord) records.get(name);
    }

    @Override
    public ResourceLocation getRecordResource(String name) {
        return new ResourceLocation("thebetweenlands:" + name);
    }
}
