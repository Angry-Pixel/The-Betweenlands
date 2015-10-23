package thebetweenlands.entities.mobs;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.utils.Mesh.Triangle.Vertex.Vector3D;

public class EntityTarminion extends EntityTameable implements IEntityBL {

	private boolean playOnce = true;
	private String ownerUUID = null;
	private int despawnTicks = 0;

	public EntityTarminion(World world) {
		super(world);
		isImmuneToFire = true;
		setSize(0.16F, 0.25F);
		renderDistanceWeight = 2.5D;
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityMob.class, 0.5D, false));
		tasks.addTask(2, new EntityAIFollowOwner(this, 0.7D, 3.0F, 40.0F));
		tasks.addTask(3, new EntityAIWander(this, 0.5D));
		targetTasks.addTask(0, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(1, new EntityAIOwnerHurtTarget(this));
		targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityMob.class, 0, true));
		experienceValue = 0;
	}

	public void setOwner(String ownerUUID) {
		this.ownerUUID = ownerUUID;
		this.func_152115_b(this.ownerUUID);
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
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.85D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(60.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(32.0D);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.9D);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
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
		if(rand.nextInt(10) == 0) {
			int randomSound = rand.nextInt(3) + 1;
			playSound("thebetweenlands:tarBeastStep" + randomSound, 0.8F, 1.5F);
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(!worldObj.isRemote) {
			this.despawnTicks++;
			if(despawnTicks > 1200) setDead();
		}
		if (worldObj.isRemote && ticksExisted%10 == 0)
			renderParticles(worldObj, posX, posY, posZ, rand);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("despawnTicks", this.despawnTicks);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.despawnTicks = nbt.getInteger("despawnTicks");
	}

	@Override
	public void setDead() {
		if(this.getAttackTarget() != null || this.getEntityToAttack() != null) {
			if(this.worldObj.isRemote) {
				for(int i = 0; i < 200; i++) {
					Random rnd = worldObj.rand;
					float rx = rnd.nextFloat() * 1.0F - 0.5F;
					float ry = rnd.nextFloat() * 1.0F - 0.5F;
					float rz = rnd.nextFloat() * 1.0F - 0.5F;
					Vector3D vec = new Vector3D(rx, ry, rz);
					vec = vec.normalized();
					BLParticle.SPLASH_TAR_BEAST.spawn(this.worldObj, this.posX + rx + 0.1F, this.posY + ry, this.posZ + rz + 0.1F, vec.x * 0.4F, vec.y * 0.4F, vec.z * 0.4F, 1);
				}
			} else {
				for(int i = 0; i < 8; i++) {
					this.playSound("thebetweenlands:tarBeastStep" + (rand.nextInt(3) + 1), 1F, (this.rand.nextFloat() * 0.4F + 0.8F) * 0.8F);
				}
				List<EntityCreature> affectedEntities = (List<EntityCreature>)this.worldObj.getEntitiesWithinAABB(EntityCreature.class, this.boundingBox.expand(5.25F, 5.25F, 5.25F));
				for(EntityCreature e : affectedEntities) {
					if(e == this || e.getDistanceToEntity(this) > 5.25F || !e.canEntityBeSeen(this) || e instanceof EntityTarminion) continue;
					double dst = e.getDistanceToEntity(this);
					e.attackEntityFrom(DamageSource.causeMobDamage(this), (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue() * 4);
					e.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), (int)(20 + (1.0F - dst / 5.25F) * 150), 1, true));
				}
			}
		}
		if(playOnce) {
			int randomSound = rand.nextInt(3) + 1;
			worldObj.playSoundEffect(posX, posY, posZ, "thebetweenlands:tarBeastStep" + randomSound, 2.5F, 0.5F);
			playOnce = false;
			if(this.worldObj.isRemote) {
				for(int i = 0; i < 100; i++) {
					Random rnd = worldObj.rand;
					float rx = rnd.nextFloat() * 1.0F - 0.5F;
					float ry = rnd.nextFloat() * 1.0F - 0.5F;
					float rz = rnd.nextFloat() * 1.0F - 0.5F;
					Vector3D vec = new Vector3D(rx, ry, rz);
					vec = vec.normalized();
					BLParticle.SPLASH_TAR_BEAST.spawn(this.worldObj, this.posX + rx + 0.1F, this.posY + ry, this.posZ + rz + 0.1F, vec.x * 0.2F, vec.y * 0.2F, vec.z * 0.2F, 1);
				}
			}
		}
		super.setDead();
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		super.attackEntityAsMob(entity);
		return attack(entity);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(source.getEntity() instanceof EntityCreature) {
			this.attack(source.getEntity());
		}
		return super.attackEntityFrom(source, amount);
	}

	protected boolean attack(Entity entity) {
		if (!worldObj.isRemote) {
			if (onGround) {
				double dx = entity.posX - posX;
				double dz = entity.posZ - posZ;
				float dist = MathHelper.sqrt_double(dx * dx + dz * dz);
				motionX = dx / dist * 0.2D + motionX * 0.2D;
				motionZ = dz / dist * 0.2D + motionZ * 0.2D;
				motionY = 0.3D;
			}
			entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
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

	@Override
	public EntityAgeable createChild(EntityAgeable entity) {
		return null;
	}
}
