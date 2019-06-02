package thebetweenlands.common.entity;


import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityTonySludgeWorm;

public class EntityTonyWormEggSac extends EntityProximitySpawner {

	public EntityTonyWormEggSac(World world) {
		super(world);
		setSize(1F, 0.5F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!getEntityWorld().isRemote && getEntityWorld().getTotalWorldTime()%5 == 0)
			checkArea();
	}

	@SuppressWarnings("unchecked")
	protected Entity checkArea() {
		List<EntityLivingBase> list = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, proximityBox());
		for (int entityCount = 0; entityCount < list.size(); entityCount++) {
			Entity entity = list.get(entityCount);
			if (entity != null)
				if (entity instanceof EntityPlayer) {
					if (canSneakPast() && entity.isSneaking())
						return null;
					else if (checkSight() && !canEntityBeSeen(entity))
						return null;
					else {
						for (int count = 0; count < getEntitySpawnCount(); count++) {
							Entity spawn = getEntitySpawned();
							spawn.setPosition(getPosition().getX() + 0.5F, getPosition().getY(), getPosition().getZ() + 0.5F);
							getEntityWorld().spawnEntity(spawn);
						}
						if (isSingleUse())
							setDead();
					}
				}
		}
		return null;
	}

    public boolean canEntityBeSeen(Entity entity) {
        return this.world.rayTraceBlocks(new Vec3d(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ), new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ), false, true, false) == null;
    }

	@Override
	public boolean getIsInvulnerable() {
		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		return false;
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {

	}

	@Override
	protected void entityInit() {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
	}

	@Override
	protected float getProximityRadius() {
		return 3;
	}

	@Override
	protected boolean canSneakPast() {
		return true;
	}

	@Override
	protected boolean checkSight() {
		return true;
	}

	@Override
	protected Entity getEntitySpawned() {
		return new EntityTonySludgeWorm(getEntityWorld());
	}

	@Override
	protected int getEntitySpawnCount() {
		return 4;
	}

	@Override
	protected boolean isSingleUse() {
		return true;
	}

	@Override
	protected int maxUseCount() {
		return 0;
	}

	@Override
	protected AxisAlignedBB proximityBox() {
		return new AxisAlignedBB (getPosition()).grow(getProximityRadius(), 0D, getProximityRadius());
	}

}