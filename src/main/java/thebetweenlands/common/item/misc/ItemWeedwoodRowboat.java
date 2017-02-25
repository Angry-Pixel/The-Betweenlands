package thebetweenlands.common.item.misc;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;

public class ItemWeedwoodRowboat extends Item {
    private static final float REACH = 5;

    public ItemWeedwoodRowboat() {
        maxStackSize = 1;
        setCreativeTab(BLCreativeTabs.ITEMS);
    }

    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        Vec3d pos = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d look = player.getLookVec();
        Vec3d lookExtent = pos.addVector(look.xCoord * REACH, look.yCoord * REACH, look.zCoord * REACH);
        RayTraceResult hit = world.rayTraceBlocks(pos, lookExtent, true);
        if (hit == null) {
            return new ActionResult(EnumActionResult.PASS, stack);
        }
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().addCoord(look.xCoord * REACH, look.yCoord * REACH, look.zCoord * REACH).expandXyz(1));
        for (Entity entity : list) {
            if (entity.canBeCollidedWith()) {
                AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expandXyz(entity.getCollisionBorderSize());
                if (axisalignedbb.isVecInside(pos)) {
                    return new ActionResult(EnumActionResult.PASS, stack);
                }
            }
        }
        if (hit.typeOfHit != RayTraceResult.Type.BLOCK) {
            return new ActionResult(EnumActionResult.PASS, stack);
        }
        IBlockState block = world.getBlockState(hit.getBlockPos());
        boolean liquid = block.getMaterial().isLiquid();
        EntityWeedwoodRowboat rowboat = new EntityWeedwoodRowboat(world, hit.hitVec.xCoord, liquid ? hit.hitVec.yCoord - 0.3 : hit.hitVec.yCoord, hit.hitVec.zCoord);
        rowboat.rotationYaw = player.rotationYaw;
        if (!world.getCollisionBoxes(rowboat, rowboat.getEntityBoundingBox().expandXyz(-0.1)).isEmpty()) {
            return new ActionResult(EnumActionResult.FAIL, stack);
        }
        if (!world.isRemote) {
            world.spawnEntityInWorld(rowboat);
        }
        if (!player.capabilities.isCreativeMode) {
            stack.stackSize--;
        }
        player.addStat(StatList.getObjectUseStats(this));
        return new ActionResult(EnumActionResult.SUCCESS, stack);
    }
}
