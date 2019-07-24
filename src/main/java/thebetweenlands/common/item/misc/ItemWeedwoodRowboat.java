package thebetweenlands.common.item.misc;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.List;

public class ItemWeedwoodRowboat extends Item {
    private static final float REACH = 5;

    public ItemWeedwoodRowboat() {
        maxStackSize = 1;
        setCreativeTab(BLCreativeTabs.ITEMS);
        addPropertyOverride(new ResourceLocation("tarred"), (stack, world, entity) -> EntityWeedwoodRowboat.isTarred(stack) ? 1 : 0);
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack stack) {
        String key = getTranslationKey(stack);
        if (EntityWeedwoodRowboat.isTarred(stack)) {
            return key + ".tarred";
        }
        return key;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (this.isInCreativeTab(tab)) {
            list.add(new ItemStack(this));
            list.add(getTarred());
        }
    }
    public static ItemStack getTarred() {
        ItemStack tarred = new ItemStack(ItemRegistry.WEEDWOOD_ROWBOAT);
        NBTTagCompound attrs = new NBTTagCompound();
        attrs.setBoolean("isTarred", true);
        tarred.setTagInfo("attributes", attrs);
        return tarred;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        Vec3d pos = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d look = player.getLookVec();
        Vec3d lookExtent = pos.add(look.x * REACH, look.y * REACH, look.z * REACH);
        RayTraceResult hit = world.rayTraceBlocks(pos, lookExtent, true);
        if (hit == null) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().grow(look.x * REACH, look.y * REACH, look.z * REACH).grow(1, 1, 1));
        for (Entity entity : list) {
            if (entity.canBeCollidedWith()) {
                AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize(), entity.getCollisionBorderSize(), entity.getCollisionBorderSize());
                if (axisalignedbb.contains(pos)) {
                    return new ActionResult<>(EnumActionResult.PASS, stack);
                }
            }
        }
        if (hit.typeOfHit != RayTraceResult.Type.BLOCK) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        IBlockState block = world.getBlockState(hit.getBlockPos());
        boolean liquid = block.getMaterial().isLiquid();
        EntityWeedwoodRowboat rowboat = new EntityWeedwoodRowboat(world, hit.hitVec.x, liquid ? hit.hitVec.y - 0.3 : hit.hitVec.y, hit.hitVec.z);
        rowboat.rotationYaw = player.rotationYaw;
        if (!world.getCollisionBoxes(rowboat, rowboat.getEntityBoundingBox().grow(-0.1, -0.1, -0.1)).isEmpty()) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        if (!world.isRemote) {
            NBTTagCompound attrs = stack.getSubCompound("attributes");
            if (attrs != null) {
                rowboat.readEntityFromNBT(attrs);
            }
            world.spawnEntity(rowboat);
        }
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        player.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
