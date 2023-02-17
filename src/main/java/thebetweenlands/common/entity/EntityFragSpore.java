package thebetweenlands.common.entity;


import java.util.List;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.item.tools.bow.EnumArrowType;

public class EntityFragSpore extends EntityLiving implements IEntityBL {

	public EntityFragSpore (World world) {
		super(world);
		setSize(0.25F, 0.25F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!world.isRemote) {
			if (world.getTotalWorldTime() % 5 == 0)
				checkForPlayerInRange();
			if(ticksExisted > 60)
				explode();
		}
	}

	private void explode() {
		for (int x = 0; x < 12; x++) {
			double angle = Math.toRadians(x * 30D);
			double offSetX = Math.floor(-Math.sin(angle) * 3D);
			double offSetZ = Math.floor(Math.cos(angle) * 3D);
				EntityBLArrow arrow = new EntityBLArrow(getEntityWorld(), this);
				Vec3d vector3d = new Vec3d(this.posX, this.posY, this.posZ);
				double velX = posX + offSetX * 1D - arrow .posX;
				double velY = posY + 0.5D - arrow .posY;
				double velZ = posZ + offSetZ * 1D - arrow .posZ;
				double distanceSqRt = (double) MathHelper.sqrt(velX * velX + velY * velY + velZ * velZ);
				double accelerationX = velX / distanceSqRt * 0.3D + rand.nextDouble() * 0.2D;
				double accelerationY = velY / distanceSqRt * 0.5D + rand.nextDouble() * 0.5D;
				double accelerationZ = velZ / distanceSqRt * 0.3D + rand.nextDouble() * 0.2D;
				vector3d.add(accelerationX, accelerationY, accelerationZ).scale(0.9D);
				arrow.addVelocity(accelerationX, accelerationY, accelerationZ);
				arrow.setType(EnumArrowType.FRAG_SPORE_BARB);
				getEntityWorld().spawnEntity(arrow);
			}
		setDead();
	}

	private void checkForPlayerInRange() {
		List<Entity> list = getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, getEntityBoundingBox().grow(6D, 0D, 6D));
		if(!list.isEmpty())
			explode();
	}

	@Override
	public boolean isNotColliding() {
		return true;
	}

	@Override
    public EnumPushReaction getPushReaction() {
        return EnumPushReaction.IGNORE;
    }

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
    public boolean canBePushed() {
        return false;
    }

	@Override
	public boolean getIsInvulnerable() {
		return true;
	}

    @Override
    public boolean getCanSpawnHere() {
        return true;
    }

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
	}

	@Override
	public void onKillCommand() {
		this.setDead();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source instanceof EntityDamageSource) {
			Entity sourceEntity = ((EntityDamageSource) source).getTrueSource();
			if(sourceEntity instanceof EntityPlayer && ((EntityPlayer) sourceEntity).isCreative()) {
				this.setDead();
			}
		}
		return false;
	}
}