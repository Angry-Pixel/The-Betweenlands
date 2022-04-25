package thebetweenlands.common.item.misc;

import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.entity.EntityBLItemFrame;

import javax.annotation.Nonnull;


public class ItemBLItemFrame extends Item {
    @Nonnull
    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        BlockPos blockpos = pos.offset(facing);

        if (facing != EnumFacing.DOWN && facing != EnumFacing.UP && playerIn.canPlayerEdit(blockpos, facing, stack)) {
            EntityHanging entityhanging = this.createEntity(worldIn, blockpos, facing, stack.getItemDamage());

            if (entityhanging.onValidSurface())
            {
                if (!worldIn.isRemote)
                {
                    System.out.println("SPAWN ENTITY");
                    entityhanging.playPlaceSound();
                    worldIn.spawnEntity(entityhanging);
                }

                stack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL;
    }

    private EntityHanging createEntity(World worldIn, BlockPos pos, EnumFacing clickedSide, int color) {
        System.out.println(pos);
        return new EntityBLItemFrame(worldIn, pos, clickedSide);
    }
}
