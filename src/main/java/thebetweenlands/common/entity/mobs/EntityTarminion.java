package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.api.entity.IRingOfGatheringMinion;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityTarminion extends EntityTameable implements IEntityBL, IRingOfGatheringMinion {
	public static final IAttribute MAX_TICKS_ATTRIB = (new RangedAttribute(null, "bl.maxAliveTicks", 7200.0D, 0, Integer.MAX_VALUE)).setDescription("Maximum ticks until the Tar Minion despawns");

	private int despawnTicks = 0;

	protected boolean dropContentsWhenDead = true;

	protected final Predicate<Entity> targetPredicate = entity -> {
		return entity instanceof IMob && (entity instanceof IEntityOwnable == false || ((IEntityOwnable) entity).getOwner() != EntityTarminion.this.getOwner());
	};

	public EntityTarminion(World world) {
		super(world);
		this.setSize(0.3F, 0.5F);
		this.experienceValue = 0;
		this.isImmuneToFire = true;
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, true));
		this.tasks.addTask(2, new EntityAIFollowOwner(this, 1.0D, 3.0F, 40.0F));
		this.tasks.addTask(3, new EntityAIWander(this, 0.5D));

		this.targetTasks.addTask(0, new EntityAIOwnerHurtByTarget(this));
		this.targetTasks.addTask(1, new EntityAIOwnerHurtTarget(this));
		this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false) {
			@Override
			protected void setEntityAttackTarget(EntityCreature ally, EntityLivingBase target) {
				if(target instanceof EntityTarminion == false) {
					super.setEntityAttackTarget(ally, target);
				}
			}
		});
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<EntityLiving>(this, EntityLiving.class, 10, false, false, this.targetPredicate));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.9D);

		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
		this.getAttributeMap().registerAttribute(MAX_TICKS_ATTRIB);
	}

	@Override
	public boolean canDespawn() {
		return false;
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return false;
	}
	
	@Override
	public boolean isTamed() {
		return true;
	}
	
	@Override
	protected void playStepSound(BlockPos pos, Block state) {
		if(this.rand.nextInt(10) == 0) {
			this.playSound(SoundRegistry.TAR_BEAST_STEP, 0.8F, 1.5F);
		}
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SQUISH;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(!this.world.isRemote) {
			this.despawnTicks++;
			if(this.despawnTicks > this.getEntityAttribute(MAX_TICKS_ATTRIB).getAttributeValue()) {
				this.attackEntityFrom(DamageSource.GENERIC, this.getMaxHealth());
			}
		}

		if(this.world.isRemote && this.ticksExisted % 20 == 0) {
			this.spawnParticles(this.world, this.posX, this.posY, this.posZ, this.rand);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("despawnTicks", this.despawnTicks);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.despawnTicks = nbt.getInteger("despawnTicks");
	}

	@Override
	public void setDead() {
		if(!this.isDead && this.dropContentsWhenDead) {
			if(this.getAttackTarget() != null) {
				if(this.world.isRemote) {
					for(int i = 0; i < 200; i++) {
						Random rnd = this.world.rand;
						float rx = rnd.nextFloat() * 1.0F - 0.5F;
						float ry = rnd.nextFloat() * 1.0F - 0.5F;
						float rz = rnd.nextFloat() * 1.0F - 0.5F;
						Vec3d vec = new Vec3d(rx, ry, rz);
						vec = vec.normalize();
						BLParticles.SPLASH_TAR.spawn(this.world, this.posX + rx + 0.1F, this.posY + ry, this.posZ + rz + 0.1F, ParticleArgs.get().withMotion(vec.x * 0.4F, vec.y * 0.4F, vec.z * 0.4F));
					}
				} else {
					for(int i = 0; i < 8; i++) {
						this.playSound(SoundRegistry.TAR_BEAST_STEP, 1F, (this.rand.nextFloat() * 0.4F + 0.8F) * 0.8F);
					}
					List<EntityCreature> affectedEntities = (List<EntityCreature>)this.world.getEntitiesWithinAABB(EntityCreature.class, this.getEntityBoundingBox().grow(5.25F, 5.25F, 5.25F));
					for(EntityCreature e : affectedEntities) {
						if(e == this || e.getDistance(this) > 5.25F || !e.canEntityBeSeen(this) || e instanceof EntityTarminion) continue;
						double dst = e.getDistance(this);
						e.attackEntityFrom(DamageSource.causeMobDamage(this), (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * 4);
						e.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, (int)(20 + (1.0F - dst / 5.25F) * 150), 1));
					}
				}
			}

			if(!this.world.isRemote) {
				this.dropLoot(false, 0, DamageSource.GENERIC);
			}

			this.playSound(SoundRegistry.TAR_BEAST_STEP, 2.5F, 0.5F);

			if(this.world.isRemote) {
				for(int i = 0; i < 100; i++) {
					Random rnd = world.rand;
					float rx = rnd.nextFloat() * 1.0F - 0.5F;
					float ry = rnd.nextFloat() * 1.0F - 0.5F;
					float rz = rnd.nextFloat() * 1.0F - 0.5F;
					Vec3d vec = new Vec3d(rx, ry, rz);
					vec = vec.normalize();
					BLParticles.SPLASH_TAR.spawn(this.world, this.posX + rx + 0.1F, this.posY + ry, this.posZ + rz + 0.1F, ParticleArgs.get().withMotion(vec.x * 0.2F, vec.y * 0.2F, vec.z * 0.2F));
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
		if(source == DamageSource.DROWN && this.world.getBlockState(new BlockPos(this.posX, this.posY + this.height, this.posZ)).getBlock() == BlockRegistry.TAR) {
			return false;
		}
		if(source.getTrueSource() instanceof EntityCreature) {
			this.attack(source.getTrueSource());
		}
		return super.attackEntityFrom(source, amount);
	}

	protected boolean attack(Entity entity) {
		if (!this.world.isRemote) {
			if (this.onGround) {
				double dx = entity.posX - this.posX;
				double dz = entity.posZ - this.posZ;
				float dist = MathHelper.sqrt(dx * dx + dz * dz);
				this.motionX = dx / dist * 0.2D + this.motionX * 0.2D;
				this.motionZ = dz / dist * 0.2D + this.motionZ * 0.2D;
				this.motionY = 0.3D;
			}
			
			DamageSource damageSource;
			
			EntityLivingBase owner = this.getOwner();
			if(owner != null) {
				damageSource = new EntityDamageSourceIndirect("mob", this, owner);
			} else {
				damageSource = DamageSource.causeMobDamage(this);
			}
			
			entity.attackEntityFrom(damageSource, (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
			
			if(entity instanceof EntityLivingBase && this.world.rand.nextInt(4) == 0) {
				//Set revenge target to tarminion so it can be attacked by the mob
				((EntityLivingBase) entity).setRevengeTarget(this);
			}
			
			this.playSound(SoundRegistry.TAR_BEAST_STEP, 1.0F, 2.0F);
			
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, world.getDifficulty().getId() * 50, 0));
			
			return true;
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void spawnParticles(World world, double x, double y, double z, Random rand) {
		for (int count = 0; count < 3; ++count) {
			double a = Math.toRadians(renderYawOffset);
			double offSetX = -Math.sin(a) * 0D + rand.nextDouble() * 0.1D - rand.nextDouble() * 0.1D;
			double offSetZ = Math.cos(a) * 0D + rand.nextDouble() * 0.1D - rand.nextDouble() * 0.1D;
			BLParticles.TAR_BEAST_DRIP.spawn(world , x + offSetX, y + 0.1D, z + offSetZ);
		}
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entity) {
		return null;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.TARMINION;
	}

	@Override
	protected boolean canDropLoot() {
		return false; //Loot dropping is handled in death update
	}

	@Override
	public void setDropItemsWhenDead(boolean dropWhenDead) {
		this.dropContentsWhenDead = dropWhenDead;
	}

	@Override
	public Entity changeDimension(int dimensionIn) {
		this.dropContentsWhenDead = false;
		return super.changeDimension(dimensionIn);
	}

	@Override
	public boolean returnFromRing(Entity user, NBTTagCompound nbt) {
		if(this.world instanceof WorldServer) {
			WorldServer worldServer = (WorldServer) this.world;

			LootTable lootTable = worldServer.getLootTableManager().getLootTableFromLocation(LootTableRegistry.TARMINION);
			LootContext.Builder contextBuilder = (new LootContext.Builder(worldServer)).withLootedEntity(this);

			for(ItemStack loot : lootTable.generateLootForPools(this.world.rand, contextBuilder.build())) {
				if(user instanceof EntityPlayer) {
					if(!((EntityPlayer) user).inventory.addItemStackToInventory(loot)) {
						((EntityPlayer) user).dropItem(loot, false);
					}
				} else {
					user.entityDropItem(loot, 0);
				}
			}
		}

		return true;
	}

	@Override
	public boolean shouldReturnOnDeath(boolean isOwnerLoggedIn) {
		return true;
	}

	@Override
	public UUID getRingOwnerId() {
		return this.getOwnerId();
	}
}
