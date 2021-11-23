package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class EntityOlm extends EntityCreature implements IEntityBL {
	public long egg_cooldown;

    public EntityOlm(World world) {
        super(world);
        setSize(0.95F, 0.25F);
        moveHelper = new EntityOlm.OlmMoveHelper(this);
        setPathPriority(PathNodeType.WALKABLE, -8.0F);
        setPathPriority(PathNodeType.BLOCKED, -8.0F);
        setPathPriority(PathNodeType.WATER, 16.0F);
    }

	@Override
	protected void entityInit() {
		super.entityInit();
	}

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAIWander(this, 0.4D, 20));
        tasks.addTask(1, new EntityAIMoveTowardsRestriction(this, 0.4D));
        tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(3, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.OLM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.OLM_DEATH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_HOSTILE_SWIM;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected ResourceLocation getLootTable() {
        return LootTableRegistry.OLM;
    }

    @Override
    public boolean getCanSpawnHere() {
        return posY <= WorldProviderBetweenlands.CAVE_WATER_HEIGHT && world.getBlockState(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ))).getBlock() == BlockRegistry.SWAMP_WATER;
    }

    @Override
    public PathNavigate createNavigator(World world) {
        return new PathNavigateSwimmer(this, world);
    }

    @Override
    public float getBlockPathWeight(BlockPos pos) {
        return world.getBlockState(pos).getMaterial() == Material.WATER ? 10.0F + world.getLightBrightness(pos) - 0.5F : super.getBlockPathWeight(pos);
    }

	@Override
	public void onLivingUpdate() {	
		if (inWater) {
			setAir(300);
		} else if (onGround) {
			if(getEntityWorld().getTotalWorldTime()%20==0)
				damageEntity(DamageSource.DROWN, 0.5F);
		}

		super.onLivingUpdate();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		player.swingArm(hand);
		if (!getEntityWorld().isRemote && !stack.isEmpty() && EnumItemMisc.SNOT.isItemOf(stack) && getCanLayEgg()) {
			if (!player.capabilities.isCreativeMode) {
				stack.shrink(1);
				if (stack.getCount() <= 0)
					player.setHeldItem(hand, ItemStack.EMPTY);
			}
			setEggCooldown(getEntityWorld().getTotalWorldTime() + 24000);
			entityDropItem(new ItemStack(ItemRegistry.OLM_EGG_RAW), 0F);
			return true;
		}
		return super.processInteract(player, hand);
	}

    private boolean getCanLayEgg() {
		return getEntityWorld().getTotalWorldTime() >= getEggCooldown();
	}

	public void setEggCooldown(long cooldownTime) {
		egg_cooldown = cooldownTime;
	}

	public long getEggCooldown() {
		return egg_cooldown;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setLong("egg_cooldown", getEggCooldown());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setEggCooldown(nbt.getLong("egg_cooldown"));
	}

	@Override
    public void travel(float strafe, float up, float forward) {
        if (isServerWorld()) {
            if (isInWater()) {
                moveRelative(strafe, up, forward, 0.1F);
                move(MoverType.SELF, motionX, motionY, motionZ);
				motionX *= 0.75D;
				motionY *= 0.75D;
				motionZ *= 0.75D;

                if (getAttackTarget() == null) {
                    motionY -= 0.003D;
                }
            } else {
                super.travel(strafe, up, forward);
            }
        } else {
            super.travel(strafe, up, forward);
        }
    }

	@Override
    public boolean isNotColliding() {
		 return getEntityWorld().checkNoEntityCollision(getEntityBoundingBox(), this) && getEntityWorld().getCollisionBoxes(this, getEntityBoundingBox()).isEmpty();
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    static class OlmMoveHelper extends EntityMoveHelper {
        private final EntityOlm olm;

        public OlmMoveHelper(EntityOlm olm) {
            super(olm);
            this.olm = olm;
        }

        @Override
		public void onUpdateMoveHelper() {
            if (action == EntityMoveHelper.Action.MOVE_TO && !olm.getNavigator().noPath()) {
                double targetX = posX - olm.posX;
                double targetY = posY - olm.posY;
                double targetZ = posZ - olm.posZ;
                double targetDistance = targetX * targetX + targetY * targetY + targetZ * targetZ;
                targetDistance = (double) MathHelper.sqrt(targetDistance);
                targetY = targetY / targetDistance;
                float targetAngle = (float) (MathHelper.atan2(targetZ, targetX) * (180D / Math.PI)) - 90.0F;
                olm.rotationYaw = limitAngle(olm.rotationYaw, targetAngle, 90.0F);
                olm.renderYawOffset = olm.rotationYaw;
                float travelSpeed = (float) (speed * olm.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
                olm.setAIMoveSpeed(olm.getAIMoveSpeed() + (travelSpeed - olm.getAIMoveSpeed()) * 0.125F);
                double wiggleSpeed = Math.sin((double) (olm.ticksExisted + olm.getEntityId()) * 0.5D) * olm.height * 0.05D;
                double wiggleOffsetX = Math.cos((double) (olm.rotationYaw * 0.017453292F));
                double wiggleOffsetZ = Math.sin((double) (olm.rotationYaw * 0.017453292F));
                olm.motionX += wiggleSpeed * wiggleOffsetX;
                olm.motionZ += wiggleSpeed * wiggleOffsetZ;
                wiggleSpeed = Math.sin((double) (olm.ticksExisted + olm.getEntityId()) * 0.75D) * 0.05D;
                olm.motionY += wiggleSpeed * (wiggleOffsetZ + wiggleOffsetX) * 0.25D;
                olm.motionY += (double) olm.getAIMoveSpeed() * targetY * 0.1D;
                EntityLookHelper entitylookhelper = olm.getLookHelper();
                double targetDirectionX = olm.posX + targetX / targetDistance * 2.0D;
                double targetDirectionY = (double) olm.getEyeHeight() + olm.posY + targetY / targetDistance;
                double targetDirectionZ = olm.posZ + targetZ / targetDistance * 2.0D;
                double lookX = entitylookhelper.getLookPosX();
                double lookY = entitylookhelper.getLookPosY();
                double lookZ = entitylookhelper.getLookPosZ();

                if (!entitylookhelper.getIsLooking()) {
                	lookX = targetDirectionX;
                	lookY = targetDirectionY;
                	lookZ = targetDirectionZ;
                }

                olm.getLookHelper().setLookPosition(lookX + (targetDirectionX - lookX) * 0.125D, lookY + (targetDirectionY - lookY) * 0.125D, lookZ + (targetDirectionZ - lookZ) * 0.125D, 10.0F, 40.0F);
            } else {
                olm.setAIMoveSpeed(0.0F);
            }
        }
    }
}
