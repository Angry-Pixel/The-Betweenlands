package thebetweenlands.common.item.tools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;

import java.util.ArrayList;
import java.util.List;

public class ItemShadowStaff extends Item {
    private static float THROW_SPEED = 5f;
    private static float THROW_DISTANCE = 10f;
    private static int RANGE = 10;
    private static int COOLDOWN = 200;
    private static int MAX_TIME_IN_AIR = 1000;
    private static String DISTANCE_NBT = "distance";
    private static String TARGET_ID_NBT = "target_id";
    private static String TIME_IN_AIR_NBT = "time_in_air";

    public ItemShadowStaff() {
        this.setMaxStackSize(1);
        this.setMaxDamage(COOLDOWN);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound != null) {
            if (tagCompound.hasKey(DISTANCE_NBT) && tagCompound.hasKey(TARGET_ID_NBT)) {
                Entity target = worldIn.getEntityByID(tagCompound.getInteger(TARGET_ID_NBT));
                if (target != null) {
                    Vec3d location = entityIn.getPositionVector().add(new Vec3d(0, 1, 0)).add(entityIn.getLookVec().scale(tagCompound.getInteger(DISTANCE_NBT))).add(new Vec3d(0, .5f, 0));
                    BLParticles.FLAME.spawn(worldIn, location.xCoord, location.yCoord, location.zCoord, ParticleFactory.ParticleArgs.get().withMotion(0, 0.1, 0));
                    setEntityMotionFromVector(target, location, .25f);
                } else {
                    tagCompound.removeTag(DISTANCE_NBT);
                    tagCompound.removeTag(TARGET_ID_NBT);
                    tagCompound.removeTag(TIME_IN_AIR_NBT);
                    stack.setItemDamage(COOLDOWN);
                    stack.setTagCompound(tagCompound);
                }
            }

            int cooldown = stack.getItemDamage();
            if (cooldown > 0) {
                cooldown--;
                stack.setItemDamage(cooldown);
            }


            if (tagCompound.hasKey(TIME_IN_AIR_NBT)) {
                int timeInAir = tagCompound.getInteger(TIME_IN_AIR_NBT);
                if (timeInAir > MAX_TIME_IN_AIR) {
                    tagCompound.removeTag(DISTANCE_NBT);
                    tagCompound.removeTag(TARGET_ID_NBT);
                    tagCompound.removeTag(TIME_IN_AIR_NBT);
                    stack.setTagCompound(tagCompound);
                    stack.setItemDamage(COOLDOWN);
                } else {
                    timeInAir++;
                    tagCompound.setInteger(TIME_IN_AIR_NBT, timeInAir);
                    stack.setTagCompound(tagCompound);
                }
            }
        }
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        NBTTagCompound tagCompound = itemStackIn.getTagCompound();
        if (tagCompound != null && itemStackIn.getMetadata() > 0) {
            return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);
        } else if (tagCompound != null && tagCompound.hasKey(DISTANCE_NBT) && tagCompound.hasKey(TARGET_ID_NBT)) {
            tagCompound.removeTag(DISTANCE_NBT);
            tagCompound.removeTag(TARGET_ID_NBT);
            tagCompound.removeTag(TIME_IN_AIR_NBT);
            itemStackIn.setTagCompound(tagCompound);
            itemStackIn.setItemDamage(COOLDOWN);
            return new ActionResult<>(EnumActionResult.PASS, itemStackIn);
        } else {
            int d = 1;
            Vec3d initialLocation = playerIn.getPositionVector().add(new Vec3d(0, 1, 0));
            Vec3d location = null;
            AxisAlignedBB bounds;
            List<Entity> entities = new ArrayList<>();
            while (d <= RANGE && entities.size() == 0) {
                location = initialLocation.add(playerIn.getLookVec().scale(d)).add(new Vec3d(0, .5f, 0));
                bounds = new AxisAlignedBB(location.add(new Vec3d(-1, -1, -1)), location.add(new Vec3d(1, 1, 1)));
                entities = worldIn.getEntitiesWithinAABBExcludingEntity(playerIn, bounds);
                d++;
            }
            if (entities.size() > 0 && location != null) {
                Entity locked = entities.get(0);
                if (tagCompound == null)
                    tagCompound = new NBTTagCompound();
                tagCompound.setInteger(DISTANCE_NBT, d);
                tagCompound.setInteger(TARGET_ID_NBT, locked.getEntityId());
                tagCompound.setInteger(TIME_IN_AIR_NBT, 0);
                itemStackIn.setTagCompound(tagCompound);
                return new ActionResult<>(EnumActionResult.PASS, itemStackIn);
            }
        }
        return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey(DISTANCE_NBT) && tagCompound.hasKey(TARGET_ID_NBT)) {
            Entity target = entityLiving.worldObj.getEntityByID(tagCompound.getInteger(TARGET_ID_NBT));
            Vec3d location = entityLiving.getPositionVector().add(new Vec3d(0, 1, 0)).add(entityLiving.getLookVec().scale(THROW_DISTANCE)).add(new Vec3d(0, .5f, 0));
            System.out.println(location);
            setEntityMotionFromVector(target, location, THROW_SPEED);
            tagCompound.removeTag(DISTANCE_NBT);
            tagCompound.removeTag(TARGET_ID_NBT);
            tagCompound.removeTag(TIME_IN_AIR_NBT);
            stack.setTagCompound(tagCompound);
            stack.setItemDamage(COOLDOWN);
            return true;
        }
        return super.onEntitySwing(entityLiving, stack);
    }

    public static void setEntityMotionFromVector(Entity entity, Vec3d originalPosVector, float modifier) {
        Vec3d entityVector = new Vec3d(entity.posX, entity.posY + entity.height / 2, entity.posZ);
        Vec3d finalVector = originalPosVector.subtract(entityVector);

        if (finalVector.lengthVector() > 1)
            finalVector = finalVector.normalize();

        entity.motionX = finalVector.xCoord * modifier;
        entity.motionY = finalVector.yCoord * modifier;
        entity.motionZ = finalVector.zCoord * modifier;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }


}
