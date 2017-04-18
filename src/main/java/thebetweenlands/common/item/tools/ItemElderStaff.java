package thebetweenlands.common.item.tools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class ItemElderStaff extends Item {
    private static int COOLDOWN = 1000;

    public ItemElderStaff(){
        setMaxStackSize(1);
        setMaxDamage(COOLDOWN);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        int cooldown = stack.getItemDamage();
        if (cooldown > 0) {
            cooldown--;
            stack.setItemDamage(cooldown);
        }
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote) {
            playerIn.setSpawnPoint(pos.up(), true);
            playerIn.addChatMessage(new TextComponentTranslation("chat.elderSpawnSet"));
        }
        stack.setItemDamage(COOLDOWN);
        return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }
}
