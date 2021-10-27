package thebetweenlands.common.item.misc;

import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.container.BlockWeedwoodJukebox;
import thebetweenlands.common.sound.BLSoundEvent;

public class ItemBLRecord extends ItemRecord {
    private String name;

    public ItemBLRecord(BLSoundEvent soundIn, boolean showCreative) {
        super(soundIn.name, soundIn);
        name = soundIn.name;
        if (showCreative)
        	this.setCreativeTab(BLCreativeTabs.SPECIALS);
        else
        	this.setCreativeTab(null);
    }

    @Override
    public EnumActionResult onItemUse( EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() instanceof BlockWeedwoodJukebox && !iblockstate.getValue(BlockJukebox.HAS_RECORD)) {
            if (!worldIn.isRemote) {
                ((BlockJukebox) iblockstate.getBlock()).insertRecord(worldIn, pos, iblockstate, stack);
                worldIn.playEvent(null, 1010, pos, Item.getIdFromItem(this));
                stack.shrink(1);
                playerIn.addStat(StatList.RECORD_PLAYED);
            }

            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.PASS;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getRecordNameLocal() {
        return I18n.translateToLocal("item.thebetweenlands.record." + name + ".desc");
    }

}
