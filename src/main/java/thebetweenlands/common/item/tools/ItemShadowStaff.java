package thebetweenlands.common.item.tools;

import net.minecraft.entity.Entity;
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
import thebetweenlands.util.NBTHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemShadowStaff extends Item {
    public ItemShadowStaff() {
        this.setMaxStackSize(1);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound != null) {
            if (tagCompound.hasKey("distance") && tagCompound.hasKey("target_id")) {
                Entity target = worldIn.getEntityByID(tagCompound.getInteger("target_id"));
                if (target != null) {
                    Vec3d location = entityIn.getPositionVector().add(new Vec3d(0, 1, 0)).add(entityIn.getLookVec().scale(tagCompound.getInteger("distance"))).add(new Vec3d(0, .5f, 0));
                    BLParticles.FLAME.spawn(worldIn, location.xCoord, location.yCoord, location.zCoord, ParticleFactory.ParticleArgs.get().withMotion(0, 0.1, 0));
                    setEntityMotionFromVector(target, location, .25f);
                } else {
                    tagCompound.removeTag("distance");
                    tagCompound.removeTag("target_id");
                    tagCompound.removeTag("time_in_air");
                    tagCompound.setInteger("cooldown", 200);
                    stack.setTagCompound(tagCompound);
                }
            }
            if (tagCompound.hasKey("cooldown")) {
                int cooldown = tagCompound.getInteger("cooldown");
                if (cooldown > 0) {
                    cooldown--;
                    tagCompound.setInteger("cooldown", cooldown);
                    stack.setTagCompound(tagCompound);
                }
            }

            if (tagCompound.hasKey("time_in_air")) {
                int timeInAir = tagCompound.getInteger("time_in_air");
                if (timeInAir > 1000) {
                    tagCompound.removeTag("distance");
                    tagCompound.removeTag("target_id");
                    tagCompound.removeTag("time_in_air");
                    tagCompound.setInteger("cooldown", 200);
                    stack.setTagCompound(tagCompound);
                } else {
                    timeInAir++;
                    tagCompound.setInteger("time_in_air", timeInAir);
                    stack.setTagCompound(tagCompound);
                }
            }
        }
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        NBTTagCompound tagCompound = itemStackIn.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("cooldown") && tagCompound.getInteger("cooldown") > 0) {
            return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);
        } else if (tagCompound != null && tagCompound.hasKey("distance") && tagCompound.hasKey("target_id")) {
            tagCompound.removeTag("distance");
            tagCompound.removeTag("target_id");
            tagCompound.removeTag("time_in_air");
            tagCompound.setInteger("cooldown", 200);
            itemStackIn.setTagCompound(tagCompound);
            return new ActionResult<>(EnumActionResult.PASS, itemStackIn);
        } else {
            int d = 1;
            Vec3d initialLocation = playerIn.getPositionVector().add(new Vec3d(0, 1, 0));
            Vec3d location = null;
            AxisAlignedBB bounds;
            List<Entity> entities = new ArrayList<>();
            while (d <= 10 && entities.size() == 0) {
                location = initialLocation.add(playerIn.getLookVec().scale(d)).add(new Vec3d(0, .5f, 0));
                bounds = new AxisAlignedBB(location.add(new Vec3d(-1, -1, -1)), location.add(new Vec3d(1, 1, 1)));
                entities = worldIn.getEntitiesWithinAABBExcludingEntity(playerIn, bounds);
                d++;
            }
            if (entities.size() > 0 && location != null) {
                Entity locked = entities.get(0);
                if (tagCompound == null)
                    tagCompound = new NBTTagCompound();
                tagCompound.setInteger("distance", d);
                tagCompound.setInteger("target_id", locked.getEntityId());
                tagCompound.setInteger("time_in_air", 0);
                itemStackIn.setTagCompound(tagCompound);
                return new ActionResult<>(EnumActionResult.PASS, itemStackIn);
            }
        }
        return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
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
        return oldStack.getItem() != newStack.getItem() || oldStack.getMetadata() != newStack.getMetadata();
    }
}
