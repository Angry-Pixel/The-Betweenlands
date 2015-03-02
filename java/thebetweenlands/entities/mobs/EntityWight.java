package thebetweenlands.entities.mobs;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.items.AxeBL;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.items.PickaxeBL;
import thebetweenlands.items.SpadeBL;
import thebetweenlands.items.SwordBL;

public class EntityWight extends EntityMob {

	private EntityAIAttackOnCollide meleeAttack = new EntityAIAttackOnCollide(this, EntityPlayer.class, 0.5D, false);
	private EntityAINearestAttackableTarget targetPlayer = new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true);

	public EntityWight(World world) {
		super(world);
		setSize(1.5F, 3F);
		getNavigator().setAvoidsWater(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, meleeAttack);
		tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
		tasks.addTask(3, new EntityAIWander(this, 0.5D));
		targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(1, targetPlayer);
		targetTasks.addTask(0, new EntityAILeapAtTarget(this, 0.5F));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(20, new Byte((byte) 0));
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(50.0D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
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
		if(getAttackTarget() != null && getAttackTarget() instanceof EntityPlayer) {
			if(getAttackTarget().isSneaking()) {
				setAttackTarget(null);
				tasks.removeTask(meleeAttack);
				tasks.removeTask(targetPlayer);
				setBoundingBox(false);
				}
			else {
				tasks.addTask(1, meleeAttack);
				targetTasks.addTask(1, targetPlayer);
				setBoundingBox(true);
				}
			}

		if (!worldObj.isRemote && getAttackTarget() != null) {
			dataWatcher.updateObject(20, Byte.valueOf((byte) 1));
		}

		if (!worldObj.isRemote && getAttackTarget() == null)
			dataWatcher.updateObject(20, Byte.valueOf((byte) 0));

		super.onUpdate();
	}

	private void setBoundingBox(boolean state) {
		if(state) {
			boundingBox.maxY = boundingBox.minY;
		}
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
		entityDropItem(ItemMaterialsBL.createStack(EnumMaterialsBL.WIGHT_HEART), 0F);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.getSourceOfDamage() instanceof EntityPlayer) {
			EntityPlayer entityPlayer = (EntityPlayer) source.getSourceOfDamage();
			ItemStack heldItem = entityPlayer.getCurrentEquippedItem();
			if (heldItem != null)
				if (heldItem.getItem() instanceof SwordBL || heldItem.getItem() instanceof AxeBL || heldItem.getItem() instanceof PickaxeBL || heldItem.getItem() instanceof SpadeBL) {
					return super.attackEntityFrom(source, damage);
				} else {
					return super.attackEntityFrom(source, MathHelper.ceiling_float_int((float) damage * 0.5F));
				}
		}
		return super.attackEntityFrom(source, damage);
	}
}
