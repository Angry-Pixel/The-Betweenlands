package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.item.equipment.ItemRingOfSummoning;
import thebetweenlands.common.registries.EntityRegistry;

public class EntityMummyArm extends EntityCreature implements IEntityBL {
	private static final DataParameter<Integer> OWNER_ID = EntityDataManager.<Integer>createKey(EntityMummyArm.class, DataSerializers.VARINT);

	private Entity owner;
	private UUID ownerUUID;

	public int attackSwing = 0;

	private int spawnTicks = 0;

	private int despawnTicks = 0;

	private int deathTicks = 0;

	private double yOffset = 0.0D;

	public EntityMummyArm(World world) {
		super(EntityRegistry.MUMMY_ARM, world);
		this.setSize(0.7F, 0.7F);
	}

	@Override
	protected void registerData() {
		super.registerData();
		this.getDataManager().register(OWNER_ID, -1);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData data) {
		this.rotationYaw = this.world.rand.nextFloat() * 360.0F;
		return data;
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();

		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0F);
		this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
	}

	public void setOwner(Entity owner) {
		this.owner = owner;
		this.ownerUUID = owner == null ? null : owner.getUniqueID();
		this.getDataManager().set(OWNER_ID, owner == null ? -1 : owner.getEntityId());
	}

	public Entity getOwner() {
		if(!this.world.isRemote()) {
			if(this.owner != null && this.owner.getUniqueID().equals(this.ownerUUID)) {
				return this.owner;
			} else {
				this.owner = this.ownerUUID == null ? null : this.getEntityByUUID(this.ownerUUID);
				return this.owner;
			}
		} else {
			if(this.owner != null && this.owner.getEntityId() != this.getDataManager().get(OWNER_ID)) {
				return this.owner;
			} else {
				int id = this.getDataManager().get(OWNER_ID);
				this.owner = id < 0 ? null : this.world.getEntityByID(id);
				return this.owner;
			}
		}
	}

	private Entity getEntityByUUID(UUID uuid) {
		for (int i = 0; i < this.world.loadedEntityList.size(); ++i) {
			Entity entity = (Entity)this.world.loadedEntityList.get(i);
			if (uuid.equals(entity.getUniqueID())) {
				return entity;
			}
		}
		return null;
	}

	@Override
	public void tick() {
		super.tick();

		BlockPos pos = this.getPosition().down(1);
		IBlockState blockState = this.world.getBlockState(pos);

		if(!this.world.isRemote()) {
			if(blockState.getBlock() == Blocks.AIR || !blockState.isSideSolid(this.world, pos, EnumFacing.UP)) {
				this.remove();
			}

			Entity owner = this.getOwner();

			if(owner == null || owner.getDistance(this) > 32.0D) {
				this.setHealth(0);
			} else if(owner instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) owner;
				if(!ItemRingOfSummoning.isRingActive(player)) {
					this.setHealth(0);
				}
			} else {
				this.setHealth(0);
			}

			if(this.despawnTicks >= 150) {
				this.setHealth(0);
			} else {
				if(this.spawnTicks >= 40) {
					this.despawnTicks++;
				}
			}
		}

		if(this.deathTicks > 0) {
			this.yOffset = -this.deathTicks / 40.0F;
		} else if(this.spawnTicks >= 40) {
			this.yOffset = 0.0F;
		}

		if(this.isAlive()) {
			if(this.spawnTicks >= 4) {
				List<EntityLivingBase> targets = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getBoundingBox());
				for(EntityLivingBase target : targets) {
					if(target != this && target != this.getOwner() && target instanceof EntityMob || target instanceof IMob) {
						target.setInWeb();

						if(target.hurtResistantTime < 10) {
							DamageSource damageSource;
							Entity owner = this.getOwner();
							if(owner != null) {
								damageSource = new EntityDamageSourceIndirect("mob", this, owner);
							} else {
								damageSource = DamageSource.causeMobDamage(this);
							}

							target.attackEntityFrom(damageSource, (float) this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue());

							if(this.attackSwing <= 0) {
								this.attackSwing = 20;
							}
						}
					}
				}
			}

			if(this.spawnTicks < 40) {
				this.spawnTicks++;
				this.yOffset = -1 + this.spawnTicks / 40.0F;
			} else {
				this.yOffset = 0.0F;
			}

			if(this.attackSwing > 0) {
				this.attackSwing--;
			}
		}

		if(this.world.isRemote() && this.rand.nextInt(this.yOffset < 0.0F ? 2 : 8) == 0) {
			if(blockState.getBlock() != Blocks.AIR) {
				double px = this.posX;
				double py = this.posY;
				double pz = this.posZ;
				for (int i = 0, amount = 2 + this.rand.nextInt(this.yOffset < 0.0F ? 8 : 3); i < amount; i++) {
					double ox = this.rand.nextDouble() * 0.1F - 0.05F;
					double oz = this.rand.nextDouble() * 0.1F - 0.05F;
					double motionX = this.rand.nextDouble() * 0.2 - 0.1;
					double motionY = this.rand.nextDouble() * 0.1 + 0.1;
					double motionZ = this.rand.nextDouble() * 0.2 - 0.1;
					this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, px + ox, py, pz + oz, motionX, motionY, motionZ, Block.getStateId(blockState));
				}
			}
		}
	}

	@Override
	public void travel(float strafe, float up,  float forward) { }

	@Override
	public void applyEntityCollision(Entity entity) { }

	@Override
	protected void collideWithEntity(Entity entity) { }

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public double getYOffset() {
		return this.yOffset;
	}

	@Override
	protected void onDeathUpdate() {
		this.deathTicks++;

		if(!this.world.isRemote() && this.deathTicks >= 40) {
			this.remove();
		}
	}

	@Override
	public void writeAdditional(NBTTagCompound nbt) {
		super.writeAdditional(nbt);
		if(this.ownerUUID != null) {
			nbt.setUniqueId("ownerUUID", this.ownerUUID);
		}
		nbt.setInt("spawnTicks", this.spawnTicks);
		nbt.setInt("despawnTicks", this.despawnTicks);
		nbt.setInt("deathTicks", this.deathTicks);
	}

	@Override
	public void readAdditional(NBTTagCompound nbt) {
		super.readAdditional(nbt);
		if(nbt.hasUniqueId("ownerUUID")) {
			this.ownerUUID = nbt.getUniqueId("ownerUUID");
		}
		this.spawnTicks = nbt.getInt("spawnTicks");
		this.despawnTicks = nbt.getInt("despawnTicks");
		this.deathTicks = nbt.getInt("deathTicks");
	}
}
