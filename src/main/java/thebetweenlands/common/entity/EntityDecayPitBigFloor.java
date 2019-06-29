package thebetweenlands.common.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntitySludgeJet;

public class EntityDecayPitBigFloor extends Entity {
	public float animationTicks = 0;
	public float animationTicksPrev = 0;

	public EntityDecayPitBigFloor(World world) {
		super(world);
		setSize(15F, 1F);
		ignoreFrustumCheck = true;
	}

	@Override
	protected void entityInit() {
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		animationTicksPrev = animationTicks;

		animationTicks += 1F;
		if (animationTicks >= 360F)
			animationTicks = animationTicksPrev = 0;

		if (!getEntityWorld().isRemote) {
			if (animationTicks == 60) {
				spawnSludgeJet(posX + 2D, posY + 1D, posZ - 5.5D);
			}
			if (animationTicks == 150) {
				spawnSludgeJet(posX - 5.5D, posY + 1D, posZ - 2D);
			}
			if (animationTicks == 240) {
				spawnSludgeJet(posX - 2D, posY + 1D, posZ + 5.5D);
			}
			if (animationTicks == 330) {
				spawnSludgeJet(posX + 5.5D, posY + 1D, posZ + 2D);
			}
		}

		checkSurfaceCollisions();
	}

	private Entity checkSurfaceCollisions() {
		for (Entity entity : getEntityAbove())
			if (entity != null) {
				if (getDistance(entity) >= 4.25F - entity.width * 0.5F && getDistance(entity) <= 7.5F + entity.width * 0.5F) {
					if (entity.posY <= posY + height - 0.0625D) {
						entity.motionX = 0D;
						entity.motionY = 0.2D;
						entity.motionZ = 0D;
					}
					else if(entity.motionY < 0 )
							entity.motionY = 0;
				}
			}
		return null;
	}

	public List<Entity> getEntityAbove() {
		return getEntityWorld().<Entity>getEntitiesWithinAABB(Entity.class, getEntityBoundingBox(), EntitySelectors.IS_ALIVE);
    }

	private void spawnSludgeJet(double posX, double posY, double posZ) {
		EntitySludgeJet jet = new EntitySludgeJet(getEntityWorld());
		jet.setPosition(posX, posY, posZ);
		getEntityWorld().spawnEntity(jet);
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
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
		return false;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
	}

}