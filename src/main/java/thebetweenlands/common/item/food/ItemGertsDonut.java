package thebetweenlands.common.item.food;

import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import thebetweenlands.common.block.container.BlockWeedwoodJukebox;


public class ItemGertsDonut extends ItemBLFood {
    public ItemGertsDonut() {
        super(6, 0.6F, false);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);
        player.heal(8.0F);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() instanceof BlockWeedwoodJukebox && !iblockstate.getValue(BlockJukebox.HAS_RECORD)) {
            if (!worldIn.isRemote) {
                ((BlockJukebox) iblockstate.getBlock()).insertRecord(worldIn, pos, iblockstate, stack);
                worldIn.playEvent(null, 1010, pos, Item.getIdFromItem(this));
                stack.shrink(stack.getCount());
                playerIn.addStat(StatList.RECORD_PLAYED);
            } else {
                playerIn.sendStatusMessage(new TextComponentString("DOH!"), true);
                worldIn.playSound(playerIn, pos, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.RECORDS, 1.0F, 1.0F);
            }

            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.PASS;
        }
    }
}
