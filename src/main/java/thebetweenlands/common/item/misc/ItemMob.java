package thebetweenlands.common.item.misc;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

/**
 * Created by Bart on 03/04/2016.
 */
public class ItemMob extends Item {
    String name = "";

    public ItemMob(String name) {
        this.name = name;
        this.maxStackSize = 1;
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return EnumActionResult.FAIL;
        EntityLiving entity = null;

        switch (name) {
            case "fireFly":
                //entity = new EntityFirefly(world);
                break;
            case "gecko":
                //entity = new EntityGecko(world);
                break;
        }
        if (entity != null) {
            entity.setLocationAndAngles(pos.getX() + facing.getFrontOffsetX(), pos.getY() + facing.getFrontOffsetY(), pos.getZ() + facing.getFrontOffsetX(), 0.0f, 0.0f);
            if (!(stack.getDisplayName().equals(I18n.translateToLocal(stack.getUnlocalizedName()))) && !(stack.getDisplayName().equals(stack.getUnlocalizedName())))
                entity.setCustomNameTag(stack.getDisplayName());
            worldIn.spawnEntityInWorld(entity);
        }
        playerIn.setHeldItem(hand, null);
        return EnumActionResult.SUCCESS;
    }
}
