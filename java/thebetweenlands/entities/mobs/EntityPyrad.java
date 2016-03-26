package thebetweenlands.entities.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.entities.projectiles.EntityPyradFlame;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;

public class EntityPyrad extends EntityMob {
	private int shouldFire;
    private float heightOffset = 0.5F;
    private int heightOffsetUpdateTime;
	public EntityPyrad(World world) {
		super(world);
		isImmuneToFire = true;
        experienceValue = 10;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(worldObj.isRemote) {
			BLParticle.LEAF_SWIRL.spawn(worldObj, posX, posY, posZ, 0, 0, 0, 1, this, 0.0F);
		}
	}

	@Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D);
    }

	@Override
	protected void attackEntity(Entity entity, float distance) {
		if (attackTime <= 0 && distance < 2.0F && entity.boundingBox.maxY > boundingBox.minY && entity.boundingBox.minY < boundingBox.maxY) {
			attackTime = 20;
			attackEntityAsMob(entity);
		} else if (distance < 30.0F) {
			double d0 = entity.posX - posX;
			double d1 = entity.boundingBox.minY + (double) (entity.height / 2.0F) - (posY + (double) (height / 2.0F));
			double d2 = entity.posZ - posZ;

			if (attackTime == 0) {
				++shouldFire;

				if (shouldFire == 1) {
					attackTime = 60;
				} else if (shouldFire <= 4) {
					attackTime = 6;
				} else {
					attackTime = 100;
					shouldFire = 0;
				}

				if (shouldFire > 1) {
					float f1 = MathHelper.sqrt_float(distance) * 0.5F;
					worldObj.playAuxSFXAtEntity((EntityPlayer) null, 1009, (int) posX, (int) posY, (int) posZ, 0);

					for (int i = 0; i < 1; ++i) {
						EntityPyradFlame flammeBall = new EntityPyradFlame(worldObj, this, d0 + rand.nextGaussian() * (double) f1, d1, d2 + rand.nextGaussian() * (double) f1);
						flammeBall.posY = posY + (double) (height / 2.0F) + 0.5D;
						worldObj.spawnEntityInWorld(flammeBall);
					}
				}
			}

			rotationYaw = (float) (Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
			hasAttacked = true;
		}
	}

	@Override
    protected void fall(float distance) {}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		entityDropItem(ItemGeneric.createStack(EnumItemGeneric.SULFUR), 1);
		int count = 1 + rand.nextInt(3) + rand.nextInt(1 + looting);
		for (int a = 0; a < count; ++a)
			entityDropItem(ItemGeneric.createStack(EnumItemGeneric.TANGLED_ROOT), 0.0F);
	}

	@Override
	public void onLivingUpdate() {
		if (!worldObj.isRemote) {
			--heightOffsetUpdateTime;

			if (heightOffsetUpdateTime <= 0) {
				heightOffsetUpdateTime = 100;
				heightOffset = 0.5F + (float) rand.nextGaussian() * 3.0F;
			}

			if (getEntityToAttack() != null && getEntityToAttack().posY + (double) getEntityToAttack().getEyeHeight() > posY + (double) getEyeHeight() + (double) heightOffset)
				motionY += (0.30000001192092896D - motionY) * 0.30000001192092896D;
		}

		if (!onGround && motionY < 0.0D)
			motionY *= 0.6D;

		super.onLivingUpdate();
	}

	@Override
    public boolean isBurning(){
    	return false;
    }

	@Override
    protected boolean isValidLightLevel() {
        return true;
    }

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}

	@Override
	protected String getLivingSound() {
		return "thebetweenlands:pyradLiving";
	}

	@Override
	protected String getHurtSound() {
		return "thebetweenlands:pyradHurt";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:pyradDeath";
	}
}
