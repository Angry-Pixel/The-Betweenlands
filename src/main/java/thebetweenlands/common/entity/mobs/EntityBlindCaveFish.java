package thebetweenlands.common.entity.mobs;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.util.AnimationMathHelper;

import javax.annotation.Nullable;

public class EntityBlindCaveFish extends EntityCreature implements IEntityBL {
    public float moveProgress;
    private AnimationMathHelper animation = new AnimationMathHelper();

    public EntityBlindCaveFish(World world) {
        super(world);
        setSize(0.3F, 0.2F);
        setAir(80);
        this.moveHelper = new EntityBlindCaveFish.BlindFishMoveHelper(this);
        setPathPriority(PathNodeType.WALKABLE, -8.0F);
        setPathPriority(PathNodeType.BLOCKED, -8.0F);
        setPathPriority(PathNodeType.WATER, 16.0F);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAIMoveTowardsRestriction(this, 0.4D));
        tasks.addTask(1, new EntityAIWander(this, 0.4D, 80));
        tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(3, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
    }

    @Override
    public PathNavigate createNavigator(World world) {
        return new PathNavigateSwimmer(this, world);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundRegistry.FISH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.FISH_DEATH;
    }

    @Override
    @MethodsReturnNonnullByDefault
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_HOSTILE_SWIM;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public float getBlockPathWeight(BlockPos pos) {
        return world.getBlockState(pos).getMaterial() == Material.WATER ? 10.0F + world.getLightBrightness(pos) - 0.5F : super.getBlockPathWeight(pos);
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.posY <= WorldProviderBetweenlands.CAVE_WATER_HEIGHT && world.getBlockState(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ))).getBlock() == BlockRegistry.SWAMP_WATER;
    }

    @Override
    public boolean isInWater() {
        return world.getBlockState(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ))).getBlock() == BlockRegistry.SWAMP_WATER;
    }

    @Override
    public void travel(float strafe, float up, float forward) {
        if (isServerWorld()) {
            if (isInWater()) {
                moveRelative(strafe, up, forward, 0.1F);
                move(MoverType.SELF, motionX, motionY, motionZ);
                motionX *= 0.8999999761581421D;
                motionY *= 0.8999999761581421D;
                motionZ *= 0.8999999761581421D;

                if (getAttackTarget() == null) {
                    motionY -= 0.005D;
                }
            } else {
                super.travel(strafe, up, forward);
            }
        } else {
            super.travel(strafe, up, forward);
        }
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (this.world.isRemote) {
            if (isInWater()) {
                moveProgress = animation.swing(1.2F, 0.4F, false);
            } else {
                moveProgress = animation.swing(2F, 0.4F, false);
            }
        } else {
            if (inWater) {
                setAir(300);
            } else if (onGround) {
                motionY += 0.25D;
                motionX += (double) ((rand.nextFloat() * 2.0F - 1.0F) * 0.075F);
                motionZ += (double) ((rand.nextFloat() * 2.0F - 1.0F) * 0.075F);
                rotationYaw = rand.nextFloat() * 360.0F;
                onGround = false;
                isAirBorne = true;
                if (world.getTotalWorldTime() % 5 == 0)
                    world.playSound((EntityPlayer) null, posX, posY, posZ, SoundEvents.ENTITY_GUARDIAN_FLOP, SoundCategory.HOSTILE, 1F, 1F);
                this.damageEntity(DamageSource.DROWN, 0.5F);
            }
        }
    }

    @Override
    public void onEntityUpdate() {
        int air = getAir();
        super.onEntityUpdate();

        if (isEntityAlive() && !isInWater()) {
            --air;
            setAir(air);

            if (getAir() == -20) {
                setAir(0);
                attackEntityFrom(DamageSource.DROWN, 2.0F);
            }
        } else
            setAir(80);
    }

    @Override
    public boolean isNotColliding() {
        return this.getEntityWorld().getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty() && this.getEntityWorld().checkNoEntityCollision(this.getEntityBoundingBox(), this);
    }

    @Override
    protected ResourceLocation getLootTable() {
        return LootTableRegistry.BLIND_CAVE_FISH;
    }

    static class BlindFishMoveHelper extends EntityMoveHelper {
        private final EntityBlindCaveFish fish;

        public BlindFishMoveHelper(EntityBlindCaveFish fish) {
            super(fish);
            this.fish = fish;
        }

        @Override
		public void onUpdateMoveHelper() {
            if (action == EntityMoveHelper.Action.MOVE_TO && !fish.getNavigator().noPath()) {
                double d0 = posX - fish.posX;
                double d1 = posY - fish.posY;
                double d2 = posZ - fish.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                d3 = (double) MathHelper.sqrt(d3);
                d1 = d1 / d3;
                float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
                fish.rotationYaw = limitAngle(fish.rotationYaw, f, 90.0F);
                fish.renderYawOffset = fish.rotationYaw;
                float f1 = (float) (speed * fish.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
                fish.setAIMoveSpeed(fish.getAIMoveSpeed() + (f1 - fish.getAIMoveSpeed()) * 0.125F);
                double d4 = Math.sin((double) (fish.ticksExisted + fish.getEntityId()) * 0.5D) * 0.05D;
                double d5 = Math.cos((double) (fish.rotationYaw * 0.017453292F));
                double d6 = Math.sin((double) (fish.rotationYaw * 0.017453292F));
                fish.motionX += d4 * d5;
                fish.motionZ += d4 * d6;
                d4 = Math.sin((double) (fish.ticksExisted + fish.getEntityId()) * 0.75D) * 0.05D;
                fish.motionY += d4 * (d6 + d5) * 0.25D;
                fish.motionY += (double) fish.getAIMoveSpeed() * d1 * 0.1D;
                EntityLookHelper entitylookhelper = fish.getLookHelper();
                double d7 = fish.posX + d0 / d3 * 2.0D;
                double d8 = (double) fish.getEyeHeight() + fish.posY + d1 / d3;
                double d9 = fish.posZ + d2 / d3 * 2.0D;
                double d10 = entitylookhelper.getLookPosX();
                double d11 = entitylookhelper.getLookPosY();
                double d12 = entitylookhelper.getLookPosZ();

                if (!entitylookhelper.getIsLooking()) {
                    d10 = d7;
                    d11 = d8;
                    d12 = d9;
                }

                fish.getLookHelper().setLookPosition(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D, 10.0F, 40.0F);
            } else {
                fish.setAIMoveSpeed(0.0F);
            }
        }
    }
}
