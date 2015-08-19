package thebetweenlands.entities.mobs;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityTarminion extends EntityCreature {
	private boolean playOnce = true;
	public EntityTarminion(World world) {
		super(world);
		isImmuneToFire = true;
		setSize(0.3F, 0.4F);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityMob.class, 0.5D, false));
		tasks.addTask(2, new EntityAIWander(this, 0.5D));
		targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityMob.class, 0, true));
		experienceValue = 0;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
	}

	@Override
	public boolean canDespawn() {
		return false;
	}

	@Override
	public boolean allowLeashing() {
		return !canDespawn() && super.allowLeashing();
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:squish";
	}

	@Override
	protected void func_145780_a(int x, int y, int z, Block block) {
		int randomSound = rand.nextInt(3) + 1;
		playSound("thebetweenlands:tarBeastStep" + randomSound, 1F, 1.5F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(!worldObj.isRemote)
			if(ticksExisted > 120)
				setDead();
		if (worldObj.isRemote && ticksExisted%10 == 0)
			renderParticles(worldObj, posX, posY, posZ, rand);
	}

	@Override
    public void setDead() {
		super.setDead();
		if(playOnce) {
		int randomSound = rand.nextInt(3) + 1;
			worldObj.playSoundEffect(posX, posY, posZ, "thebetweenlands:tarBeastStep" + randomSound, 3.0F, 0.5F);
			playOnce = false;
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		return attack(entity);
	}

	protected boolean attack(Entity entity) {
		if (!worldObj.isRemote) {
			if (onGround) {
				double d0 = entity.posX - posX;
				double d1 = entity.posZ - posZ;
				float f2 = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
				motionX = d0 / f2 * 0.5D * 0.800000011920929D + motionX * 0.20000000298023224D;
				motionZ = d1 / f2 * 0.5D * 0.800000011920929D + motionZ * 0.20000000298023224D;
				motionY = 0.3000000059604645D;
			}
			entity.attackEntityFrom(DamageSource.causeMobDamage(this), 1);
			int randomSound = rand.nextInt(3) + 1;
			worldObj.playSoundAtEntity(entity, "thebetweenlands:tarBeastStep" + randomSound, 1.0F, 2.0F);
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, worldObj.difficultySetting.ordinal() * 50, 0));
			return true;
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void renderParticles(World world, double x, double y, double z, Random rand) {
		for (int count = 0; count < 3; ++count) {
			double a = Math.toRadians(renderYawOffset);
			double offSetX = -Math.sin(a) * 0D + rand.nextDouble() * 0.1D - rand.nextDouble() * 0.1D;
			double offSetZ = Math.cos(a) * 0D + rand.nextDouble() * 0.1D - rand.nextDouble() * 0.1D;
			BLParticle.DRIP_TAR_BEAST.spawn(world , x + offSetX, y + 0.1D, z + offSetZ);
		}
	}
}
