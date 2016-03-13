package thebetweenlands.entities.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Created by jnad325 on 2/13/16.
 */
public class EntityDreadfulMummy extends EntityMob implements IEntityBL {
	public EntityDreadfulMummy(World p_i1738_1_) {
		super(p_i1738_1_);
	}

	static final int SPAWN_MUMMY_COOLDOWN = 350;
	int untilSpawnMummy = 0;
	static final int SPAWN_SLUDGE_COOLDOWN = 150;
	int untilSpawnSludge = 0;

	int eatPreyTimer = 60;
	public EntityLivingBase eatPrey;

	@Override
	public String pageName() {
		return null;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.7);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(110.0D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(8);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(80.0D);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D);
	}

	@Override
	protected String getLivingSound() {
		return "thebetweenlands:dreadfulPeatMummyLiving";
	}

	@Override
	protected String getHurtSound() {
		return "thebetweenlands:dreadfulPeatMummyHurt";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:dreadfulPeatMummyDeath";
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (getEntityToAttack() != null) {
			if (untilSpawnMummy <= 0) spawnMummy();
			if (untilSpawnSludge <= 0) spawnSludge();
		}
		eatPrey = (EntityLivingBase)getPrey();
		if (eatPrey != null) {
			updateEatPrey();
		}

		if (untilSpawnMummy > 0) untilSpawnMummy--;
		if (untilSpawnSludge > 0) untilSpawnSludge--;
		if (eatPreyTimer > 0 && eatPrey != null) eatPreyTimer--;
		if (eatPreyTimer <= 0) {
			if (!worldObj.isRemote) setPrey(null);
			eatPreyTimer = 60;
		}
	}

	private void spawnMummy() {
		EntityPeatMummy mummy = new EntityPeatMummy(worldObj);
		mummy.setPosition(posX + (rand.nextInt(6) - 3), posY, posZ + (rand.nextInt(6) - 3));
		/*if (mummy.getCanSpawnHere())*/ worldObj.spawnEntityInWorld(mummy);
		//else return;
		//TODO Mummy needs to check if appropriate spawn location
		untilSpawnMummy = SPAWN_MUMMY_COOLDOWN;
		mummy.setAttackTarget((EntityLivingBase) getEntityToAttack());
		mummy.setHealth(15);
	}

	private void spawnSludge() {
		untilSpawnSludge = SPAWN_SLUDGE_COOLDOWN;

		Vec3 look = this.getLookVec();
		double direction = Math.toRadians(renderYawOffset);
		EntitySludgeBall sludge = new EntitySludgeBall(worldObj, 0.000029f, this);
		sludge.setPosition(posX - Math.sin(direction) * 3.5, posY + height, posZ + Math.cos(direction) * 3.5);
		sludge.motionX = look.xCoord * 0.5D;
		sludge.motionY = look.yCoord;
		sludge.motionZ = look.zCoord * 0.5D;
		if (!worldObj.isRemote) worldObj.spawnEntityInWorld(sludge);
	}

	@Override
	public boolean attackEntityAsMob(Entity target) {
		if (rand.nextInt(5) == 0 && target != eatPrey && !(target instanceof EntityPlayer && ((EntityPlayer)target).capabilities.isCreativeMode)) {setPrey(target);}
		return super.attackEntityAsMob(target);
	}

	private void updateEatPrey() {
		double direction = Math.toRadians(renderYawOffset);
		eatPrey.setPositionAndRotation(posX - Math.sin(direction) * 1.7, posY + 1.7, posZ + Math.cos(direction) * 1.7, (float) (Math.toDegrees(direction) + 180), 0);
		eatPrey.setRotationYawHead((float) (Math.toDegrees(direction) + 180));
		eatPrey.fallDistance = 0;
		if (ticksExisted % 10 == 0) eatPrey.attackEntityFrom(DamageSource.causeMobDamage(this), 3);
		if (eatPrey.getHealth() <= 0 && !worldObj.isRemote) setPrey(null);
	}

	private void setPrey(Entity prey) {
		if (prey == null) {dataWatcher.updateObject(24, -1); return;}
		dataWatcher.updateObject(24, prey.getEntityId());
	}

	private Entity getPrey() {
		int id = dataWatcher.getWatchableObjectInt(24);
		return id != -1 ? worldObj.getEntityByID(id) : null;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(24, 0);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.getEntity() != null && source.getEntity() == eatPrey) return false;
		return super.attackEntityFrom(source, damage);
	}
}
