package thebetweenlands.common.entity.mobs;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.util.AnimationMathHelper;

public class EntityBlindCaveFish extends EntityWaterMob implements IEntityBL, IMob {
    public float moveProgress;
    private BlockPos currentSwimTarget;
    private AnimationMathHelper animation = new AnimationMathHelper();

    public EntityBlindCaveFish(World world) {
        super(world);
        setSize(0.3F, 0.2F);
        setAir(80);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
    }

    //TODO add anglerAttack sound
    @Override
    protected SoundEvent getHurtSound() {
        return super.getHurtSound();
    }

    //TODO add anglerDeath sound
    @Override
    protected SoundEvent getDeathSound() {
        return super.getDeathSound();
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
    public boolean getCanSpawnHere() {
        return this.posY <= WorldProviderBetweenlands.CAVE_WATER_HEIGHT && worldObj.getBlockState(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ))).getBlock() == BlockRegistry.SWAMP_WATER;
    }

    @Override
    public boolean isInWater() {
        return worldObj.getBlockState(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ))).getBlock() == BlockRegistry.SWAMP_WATER;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.worldObj.isRemote) {
            if (isInWater()) {
                if (!worldObj.isRemote) {
                    swimAbout();
                }
                this.velocityChanged = true;
            }
        } else {
            if (isInWater()) {
                moveProgress = animation.swing(1.2F, 0.4F, false);
                renderYawOffset += (-((float) Math.atan2(motionX, motionZ)) * 180.0F / (float) Math.PI - renderYawOffset) * 0.1F;
                rotationYaw = renderYawOffset;
            } else {
                moveProgress = animation.swing(2F, 0.4F, false);
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
                attackEntityFrom(DamageSource.drown, 2.0F);
            }
        } else
            setAir(80);
    }

    public void swimAbout() {
        if (currentSwimTarget != null && (worldObj.getBlockState(currentSwimTarget).getBlock() != BlockRegistry.SWAMP_WATER && worldObj.getBlockState(currentSwimTarget).getBlock() != Blocks.WATER || currentSwimTarget.getY() < 1))
            currentSwimTarget = null;

        if (currentSwimTarget == null || rand.nextInt(30) == 0 || currentSwimTarget.getDistance((int) posX, (int) posY, (int) posZ) < 10.0F)
            currentSwimTarget = new BlockPos((int) posX + rand.nextInt(10) - rand.nextInt(10), (int) posY - rand.nextInt(5) + 2, (int) posZ + rand.nextInt(10) - rand.nextInt(10));

        swimToTarget();
    }

    protected void swimToTarget() {
        double targetX = currentSwimTarget.getX() + 0.5D - posX;
        double targetY = currentSwimTarget.getY() + 0.5D - posY;
        double targetZ = currentSwimTarget.getZ() + 0.5D - posZ;
        double dist = Math.sqrt(targetX * targetX + targetY * targetY + targetZ * targetZ);
        motionX = (targetX / dist) * 0.06D;
        motionY = (targetY / dist) * 0.06D;
        motionY -= 0.03D;
        motionZ = (targetZ / dist) * 0.06D;
        moveForward = 0.5F;
    }
}
