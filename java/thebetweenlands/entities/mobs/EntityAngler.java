package thebetweenlands.entities.mobs;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.utils.AnimationMathHelper;

public class EntityAngler extends EntityWaterMob implements IEntityBL, IMob {

	private ChunkCoordinates currentSwimTarget;
	AnimationMathHelper animation = new AnimationMathHelper();
	public float moveProgress;

	public EntityAngler(World world) {
		super(world);
		setSize(0.9F, 0.5F);
		setAir(80);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(20, (byte) 0);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.6D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(34.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
	}

	@Override
	protected String getHurtSound() {
		if (rand.nextBoolean())
			return "thebetweenlands:anglerAttack1";
		else
			return "thebetweenlands:anglerAttack2";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:anglerDeath";
	}

	@Override
	protected void func_145780_a(int x, int y, int z, Block block) {
		if (rand.nextInt(10) == 0)
			playSound("game.hostile.swim", 0.1F, 2.0F);
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		int amount = rand.nextInt(3) + rand.nextInt(1 + looting);
		int count;
		if (recentlyHit) {
			for (count = 0; count < amount; ++count) {
				if (rand.nextBoolean())
					entityDropItem(ItemGeneric.createStack(EnumItemGeneric.ANGLER_TOOTH, 3), 0.0F);
				else
					entityDropItem(ItemGeneric.createStack(BLItemRegistry.anglerMeatRaw, 1, 0), 0.0F);
			}
		}
	}

	@Override
	public boolean getCanSpawnHere() {
		return worldObj.difficultySetting != EnumDifficulty.PEACEFUL && worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) == BLBlockRegistry.swampWater;
	}

	@Override
	public boolean isInWater() {
		return worldObj.handleMaterialAcceleration(boundingBox, Material.water, this);
	}

	public boolean isGrounded() {
		return !isInWater() && worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY + 1), MathHelper.floor_double(posZ)) == Blocks.air && worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY - 1), MathHelper.floor_double(posZ)).isCollidable();
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (!this.worldObj.isRemote) {
			EntityPlayer target = worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);
			setTarget(target);

			if (isInWater()) {
				if (!worldObj.isRemote) {
					if (getEntityToAttack() != null) {
						currentSwimTarget = new ChunkCoordinates((int) getEntityToAttack().posX, (int) ((int) getEntityToAttack().posY + getEntityToAttack().getEyeHeight()), (int) getEntityToAttack().posZ);
						swimToTarget();
					} else
						swimAbout();
				}
			} else {
				if (!worldObj.isRemote) {
					if (!onGround) {
						motionX = 0.0D;
						motionY -= 0.08D;
						motionY *= 0.9800000190734863D;
						motionZ = 0.0D;
					} else if (onGround) {
						setIsLeaping(false);
						motionY += 0.4F;
						motionX += (rand.nextFloat() - rand.nextFloat()) * 0.3F;
						motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.3F;
					}
				}
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
	public void onUpdate() {
		super.onUpdate();
		if (worldObj.difficultySetting == EnumDifficulty.PEACEFUL)
			setDead();
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
		if (currentSwimTarget != null && (worldObj.getBlock(currentSwimTarget.posX, currentSwimTarget.posY, currentSwimTarget.posZ) != BLBlockRegistry.swampWater && worldObj.getBlock(currentSwimTarget.posX, currentSwimTarget.posY, currentSwimTarget.posZ) != Blocks.water || currentSwimTarget.posY < 1))
			currentSwimTarget = null;

		if (currentSwimTarget == null || rand.nextInt(30) == 0 || currentSwimTarget.getDistanceSquared((int) posX, (int) posY, (int) posZ) < 10.0F)
			currentSwimTarget = new ChunkCoordinates((int) posX + rand.nextInt(10) - rand.nextInt(10), (int) posY - rand.nextInt(6) + 2, (int) posZ + rand.nextInt(10) - rand.nextInt(10));

		swimToTarget();
	}

	protected void swimToTarget() {
		double targetX = currentSwimTarget.posX + 0.5D - posX;
		double targetY = currentSwimTarget.posY + 0.5D - posY;
		double targetZ = currentSwimTarget.posZ + 0.5D - posZ;
		motionX += (Math.signum(targetX) * 0.3D - motionX) * 0.1D;
		motionY += (Math.signum(targetY) * 0.4D - motionY) * 0.08D;
		motionY -= 0.01D;
		motionZ += (Math.signum(targetZ) * 0.3D - motionZ) * 0.1D;
		moveForward = 0.5F;
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		super.onCollideWithPlayer(player);
		if (!player.capabilities.isCreativeMode && !worldObj.isRemote && getEntitySenses().canSee(player)) {
			if (getDistanceToEntity(player) <= 1.5F)
				if (player.boundingBox.maxY >= boundingBox.minY && player.boundingBox.minY <= boundingBox.maxY) {
					player.attackEntityFrom(DamageSource.causeMobDamage(this), 2F);
				}
		}
	}

	@Override
	protected void attackEntity(Entity entity, float distance) {
		if (distance > 2.0F && distance < 6.0F && entity.boundingBox.maxY >= boundingBox.minY && entity.boundingBox.minY <= boundingBox.maxY && rand.nextInt(3) == 0)
			if (isInWater() && worldObj.getBlock((int) posX, (int) posY + 1, (int) posZ) == Blocks.air) {
				setIsLeaping(true);
				double distanceX = entity.posX - posX;
				double distanceZ = entity.posZ - posZ;
				float distanceSqrRoot = MathHelper.sqrt_double(distanceX * distanceX + distanceZ * distanceZ);
				motionX = distanceX / distanceSqrRoot * 1.5D * 0.900000011920929D + motionX * 2.70000000298023224D;
				motionZ = distanceZ / distanceSqrRoot * 1.5D * 0.900000011920929D + motionZ * 2.70000000298023224D;
				motionY = 0.8D;
			}
	}

	public boolean isLeaping() {
		return dataWatcher.getWatchableObjectByte(20) == 1;
	}

	public void setIsLeaping(boolean leaping) {
		dataWatcher.updateObject(20, (byte) (leaping ? 1 : 0));
	}

	@Override
	public String pageName() {
		return "angler";
	}
}
