package thebetweenlands.common.entity;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import thebetweenlands.common.entity.projectiles.EntitySludgeWallJet;

public class EntityTriggeredProjectile extends EntityProximitySpawner {

	public EntityTriggeredProjectile(World world) {
		super(world);
		setSize(0.5F, 0.5F);
		setNoGravity(true);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!getEntityWorld().isRemote && getEntityWorld().getTotalWorldTime()%40 == 0)
			checkArea();
	}

	@Override
	protected void performPreSpawnaction(Entity targetEntity, Entity entitySpawned) {
		if(targetEntity instanceof EntityPlayer) {
			double direction = Math.toRadians(targetEntity.rotationYaw + 180F);
			((EntitySludgeWallJet) entitySpawned).setPosition(posX - Math.sin(direction) * 0.825F, posY + height * 0.5D , posZ + Math.cos(direction) * 0.825F);
			((EntitySludgeWallJet) entitySpawned).shoot(targetEntity.posX - posX, targetEntity.posY + targetEntity.height - posY, targetEntity.posZ - posZ, 0.5F, 0F);
		}
	}

	@Override
	protected boolean isMovementBlocked() {
		return true;
	}

	@Override
    public boolean canBePushed() {
        return false;
    }

	@Override
    public boolean canBeCollidedWith() {
        return true;
    }

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;
	}

	@Override
	public boolean getIsInvulnerable() {
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source instanceof EntityDamageSource) {
			Entity sourceEntity = ((EntityDamageSource) source).getTrueSource();
			if(sourceEntity instanceof EntityPlayer) {
				damageEntity(source, damage);
				return true;
			}
		}
		return false;
	}

	@Override
	protected float getProximityHorizontal() {
		return 5F;
	}

	@Override
	protected float getProximityVertical() {
		return 1F;
	}

	protected AxisAlignedBB proximityBox() {
		return new AxisAlignedBB(getPosition()).grow(getProximityHorizontal(), getProximityVertical(), getProximityHorizontal());
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
		EntitySludgeWallJet entity = new EntitySludgeWallJet(getEntityWorld());
		return entity;
	}

	@Override
	protected int getEntitySpawnCount() {
		return 1;
	}

	@Override
	protected boolean isSingleUse() {
		return false;
	}

	@Override
	protected int maxUseCount() {
		return 0;
	}
}