package thebetweenlands.entities.mobs;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;

public class EntityWight extends EntityMob implements IEntityBL {
	private EntityAIAttackOnCollide meleeAttack = new EntityAIAttackOnCollide(this, EntityPlayer.class, 0.5D, false);
	private EntityPlayer previousTarget;
	private boolean updateHasBeenSeen = false;

	public EntityWight(World world) {
		super(world);
		setSize(0.7F, 2F);
		getNavigator().setCanSwim(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, meleeAttack);
		tasks.addTask(2, new EntityAIWander(this, 0.3D));
		targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(1, new EntityAILeapAtTarget(this, 0.5F));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(20, (byte) 0);
		dataWatcher.addObject(21, (float) 1);
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.7D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(50.0D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(80.0D);
	}

	@Override
	public boolean getCanSpawnHere() {
		return worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox);
	}

	@Override
	protected String getLivingSound() {
		int randomSound = worldObj.rand.nextInt(4) + 1;
		return "thebetweenlands:wightMoan" + randomSound;
	}

	@Override
	protected String getHurtSound() {
		if (rand.nextBoolean())
			return "thebetweenlands:wightHurt1";
		else
			return "thebetweenlands:wightHurt2";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:wightDeath";
	}

	@Override
	public void onUpdate() {
		EntityPlayer target = worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);

		if(target != null && !target.isSneaking() && !(target.getCurrentArmor(3) != null && target.getCurrentArmor(3).getItem() == BLItemRegistry.skullMask))
			setTargetSpotted(target, true);

		if(target != null && target != previousTarget && target.isSneaking() && !(target.getCurrentArmor(3) != null && target.getCurrentArmor(3).getItem() == BLItemRegistry.skullMask))
			setTargetSpotted(target, false);

		if((target == null && previousTarget != null) || (target != null && target.getCurrentArmor(3) != null && target.getCurrentArmor(3).getItem() == BLItemRegistry.skullMask))
			setTargetSpotted(target, false);

		if (!worldObj.isRemote && getAttackTarget() != null)
			dataWatcher.updateObject(20, Byte.valueOf((byte) 1));

		if (!worldObj.isRemote && getAttackTarget() == null)
			dataWatcher.updateObject(20, Byte.valueOf((byte) 0));

		super.onUpdate();
	}

	private void setTargetSpotted(EntityPlayer target, boolean hasBeenSeen) {
		if (hasBeenSeen  ){
			if (!updateHasBeenSeen) {
				updateHasBeenSeen = true;
				tasks.addTask(1, meleeAttack);
				setAttackTarget(target);
				previousTarget = target;
			}
			if (getAnimation() > 0)
				setAnimation(getAnimation() - 0.1F);

		} else {
			if (updateHasBeenSeen) {
				updateHasBeenSeen = false;
				setAttackTarget(null);
				tasks.removeTask(meleeAttack);
			}
			if (getAnimation() < 1)
				setAnimation(getAnimation() + 0.1F);
			if (getAnimation() == 0 && previousTarget != null) {
				previousTarget = null;
			}
		}
	}

	private void setAnimation(float progress) {
		dataWatcher.updateObject(21, progress);
	}

	public float getAnimation() {
		return dataWatcher.getWatchableObjectFloat(21);
	}

	@Override
	public boolean isEntityInvulnerable() {
		return dataWatcher.getWatchableObjectByte(20) == 0;
	}

	@Override
	public boolean canBePushed() {
		return dataWatcher.getWatchableObjectByte(20) == 1;
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		entityDropItem(new ItemStack(BLItemRegistry.wightsHeart), 0F);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.getSourceOfDamage() instanceof EntityPlayer) {
			EntityPlayer entityPlayer = (EntityPlayer) source.getSourceOfDamage();
			ItemStack heldItem = entityPlayer.getCurrentEquippedItem();
			if (heldItem != null)
				if (heldItem.getItem() == BLItemRegistry.wightsBane) {
					return super.attackEntityFrom(source, this.getHealth());
				}
		}
		return super.attackEntityFrom(source, damage);
	}
}
