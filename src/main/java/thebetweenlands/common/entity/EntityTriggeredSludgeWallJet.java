package thebetweenlands.common.entity;


import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.common.entity.projectiles.EntitySludgeWallJet;

public class EntityTriggeredSludgeWallJet extends EntityProximitySpawner {
	public float animationTicks = 0;
	public float animationTicksPrev = 0;
	public EntityTriggeredSludgeWallJet(World world) {
		super(world);
		setSize(0.5F, 0.5F);
		setNoGravity(true);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!getEntityWorld().isRemote && getEntityWorld().getTotalWorldTime()%40 == 0)
			checkArea();

		animationTicksPrev = animationTicks;
		animationTicks += 1F;
		rotationYaw = MathHelper.wrapDegrees(animationTicks * 0.5F);
		if (animationTicks >= 360F)
			animationTicks = animationTicksPrev = 0;
	}

	@Override
	protected void performPreSpawnaction(Entity targetEntity, Entity entitySpawned) {
		if(targetEntity instanceof EntityPlayer) {
			float rotation = MathHelper.wrapDegrees(rotationYaw) * 2F;
			float maxRot = 270;
			float minRot = 90;
			float rotationTarget = MathHelper.wrapDegrees(targetEntity.rotationYaw) + 180F;
			if(rotation - MathHelper.wrapDegrees(rotationTarget) >= minRot && rotation - MathHelper.wrapDegrees(rotationTarget) <= maxRot) {
				((EntitySludgeWallJet) entitySpawned).setPosition(posX, posY + height * 0.5D , posZ);
				((EntitySludgeWallJet) entitySpawned).shoot(targetEntity.posX - posX, targetEntity.posY + targetEntity.height - posY, targetEntity.posZ - posZ, 0.2F, 0F);
			
			}
			else
				((EntitySludgeWallJet) entitySpawned).setDead();
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
		//motionX = 0;
		//motionY = 0;
		//motionZ = 0;
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
				
				float rotation = MathHelper.wrapDegrees(rotationYaw) * 2F;
				float maxRot = 270;
				float minRot = 90;
				float rotationHitter = MathHelper.wrapDegrees(sourceEntity.rotationYaw) + 180F;
				System.out.println("This rotation: " + (rotation));
				if(rotation - MathHelper.wrapDegrees(rotationHitter) >= minRot && rotation - MathHelper.wrapDegrees(rotationHitter) <= maxRot) {
					System.out.println("Shooting For real at: " + (rotation - MathHelper.wrapDegrees(rotationHitter)));
					//damageEntity(source, 5F);
					return true;
				}
				else {
					
					return false;
				}	
				
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
		EntitySludgeWallJet entity = new EntitySludgeWallJet(getEntityWorld(), this);
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