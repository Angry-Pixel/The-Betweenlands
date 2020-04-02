package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thebetweenlands.api.capability.IPuppeteerCapability;
import thebetweenlands.api.capability.ProtectionShield;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.EntityShockwaveBlock;
import thebetweenlands.common.registries.CapabilityRegistry;

public class EntityFortressBossBlockade extends EntityMob implements IEntityBL, IEntityAdditionalSpawnData {
	protected static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.<Optional<UUID>>createKey(EntityFortressBossBlockade.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	protected static final DataParameter<Float> SIZE = EntityDataManager.<Float>createKey(EntityFortressBossBlockade.class, DataSerializers.FLOAT);
	protected static final DataParameter<Float> ROTATION = EntityDataManager.<Float>createKey(EntityFortressBossBlockade.class, DataSerializers.FLOAT);

	private Entity cachedOwner;

	private float prevRotation = 0.0F;
	private float rotation = 0.0F;
	private int despawnTicks = 0;
	private int maxDespawnTicks = 160;

	public EntityFortressBossBlockade(World world) {
		super(world);
		this.setSize(1.0F, 0.2F);
		this.prevRotation = this.rotation = world.rand.nextFloat() * 360.0f;
	}

	public EntityFortressBossBlockade(World world, Entity source) {
		super(world);
		this.setSize(1.0F, 0.2F);
		this.setOwner(source);
		this.prevRotation = this.rotation = world.rand.nextFloat() * 360.0f;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(OWNER, Optional.absent());
		this.getDataManager().register(SIZE, 1.0F);
		this.getDataManager().register(ROTATION, this.rotation);
	}

	public void setOwner(@Nullable Entity entity) {
		this.getDataManager().set(OWNER, entity == null ? Optional.absent() : Optional.of(entity.getUniqueID()));
	}

	@Nullable
	public UUID getOwnerUUID() {
		Optional<UUID> uuid = this.getDataManager().get(OWNER);
		return uuid.isPresent() ? uuid.get() : null;
	}

	@Nullable
	public Entity getOwner() {
		UUID uuid = this.getOwnerUUID();
		if(uuid == null) {
			this.cachedOwner = null;
		} else if(this.cachedOwner == null || !this.cachedOwner.isEntityAlive() || !this.cachedOwner.getUniqueID().equals(uuid)) {
			this.cachedOwner = null;
			for(Entity entity : this.getEntityWorld().getEntitiesWithinAABB(Entity.class, this.getEntityBoundingBox().grow(64.0D, 64.0D, 64.0D))) {
				if(entity.getUniqueID().equals(uuid)) {
					this.cachedOwner = entity;
					break;
				}
			}
		}
		return this.cachedOwner;
	}

	public void setTriangleSize(float size) {
		this.getDataManager().set(SIZE, size);
		if(this.world.isRemote) {
			double prevX = this.posX;
			double prevZ = this.posZ;
			this.setSize(size*2, this.height);
			this.setPosition(prevX, this.posY, prevZ);
		}
	}

	public float getTriangleSize() {
		return this.getDataManager().get(SIZE);
	}

	public void setMaxDespawnTicks(int ticks) {
		this.maxDespawnTicks = ticks;
	}

	public int getMaxDespawnTicks() {
		return this.maxDespawnTicks;
	}

	public int getDespawnTicks() {
		return this.despawnTicks;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("triangleSize", this.getTriangleSize());
		nbt.setFloat("triangleRotation", this.getDataManager().get(ROTATION));
		nbt.setInteger("despawnTicks", this.despawnTicks);
		nbt.setInteger("maxDespawnTicks", this.maxDespawnTicks);
		if(this.getOwnerUUID() != null) {
			nbt.setUniqueId("owner", this.getOwnerUUID());
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.setTriangleSize(nbt.getFloat("triangleSize"));
		this.getDataManager().set(ROTATION, nbt.getFloat("triangleRotation"));
		this.despawnTicks = nbt.getInteger("despawnTicks");
		this.maxDespawnTicks = nbt.getInteger("maxDespawnTicks");
		if(nbt.hasUniqueId("owner")) {
			this.getDataManager().set(OWNER, Optional.of(nbt.getUniqueId("owner")));
		} else {
			this.getDataManager().set(OWNER, Optional.absent());
		}
	}
	
	protected boolean isPlayerControlled() {
		return this.getOwner() instanceof EntityPlayer;
	}

	@Override
	public void onUpdate() {
		if(!this.world.isRemote && (this.world.getDifficulty() == EnumDifficulty.PEACEFUL || (this.getOwner() != null && !this.getOwner().isEntityAlive()))) {
			this.setDead();
			return;
		}

		this.setTriangleSize(this.getTriangleSize());

		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;

		super.onUpdate();

		if(!this.world.isRemote) {
			this.despawnTicks++;
			if(this.despawnTicks >= this.getMaxDespawnTicks()) {
				this.setDead();
			}

			this.rotation += 1.0F;
			this.getDataManager().set(ROTATION, this.rotation);

			List<EntityLivingBase> targets = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(this.getTriangleSize()*2, 0, this.getTriangleSize()*2));
			for(EntityLivingBase target : targets) {
				if(target != this.getOwner()) {
					Vec3d[] vertices = this.getTriangleVertices(1);
					
					if(EntityFortressBoss.rayTraceTriangle(new Vec3d(target.posX - this.posX, 1, target.posZ - this.posZ), new Vec3d(0, -2, 0), vertices[0], vertices[1], vertices[2])) {
						
						if(this.isPlayerControlled()) {
							Entity owner = this.getOwner();
							if(owner instanceof EntityPlayer) {
								EntityPlayer player = (EntityPlayer) owner;
								
								IPuppeteerCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null);
								if(cap != null) {
									ProtectionShield shield = cap.getShield();
									
									if(shield != null) {
										float healthPercent = 0.3f;
										
										if(target.getHealth() * (1 - healthPercent) > 5.0f) {
											float healthCost = target.getHealth() * healthPercent;
											float prevHealth = target.getHealth();
											
											if(target.attackEntityFrom(DamageSource.MAGIC, healthCost) && (prevHealth - target.getHealth()) >= healthCost * 0.5f) {
												List<Integer> indices = new ArrayList<>();
												for(int i = 0; i < 20; i++) {
													indices.add(i);
												}
												Collections.shuffle(indices, this.rand);
												
												for(int index : indices) {
													if(!shield.isActive(index)) {
														shield.setActive(index, true);
														break;
													}
												}
												
												this.maxDespawnTicks = this.despawnTicks + 8;
											}
										}
									}
								}
							}
						} else if(target instanceof EntityPlayer)  {
							float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
							if(target.attackEntityFrom(DamageSource.MAGIC, damage) && this.getOwner() != null && this.getOwner() instanceof EntityLivingBase) {
								EntityLivingBase owner = (EntityLivingBase) this.getOwner();
								if(owner.getHealth() < owner.getMaxHealth() - damage) {
									owner.heal(damage * 3.0F);
								}
							}
						}
						
					}
				}
			}
		} else {
			this.prevRotation = this.rotation;
			this.rotation = this.getDataManager().get(ROTATION);

			for(int c = 0; c < 4; c++) {
				float r1 = this.world.rand.nextFloat();
				float r2 = this.world.rand.nextFloat();
				this.rotation += 15;
				Vec3d[] vertices = this.getTriangleVertices(1);
				this.rotation -= 15;
				double xc = 0, zc = 0;
				for(int i = 0; i < 3; i++) {
					Vec3d vertex = vertices[i];
					switch(i) {
					default:
					case 0:
						xc += vertex.x * (1 - Math.sqrt(r1));
						zc += vertex.z * (1 - Math.sqrt(r1));
						break;
					case 1:
						xc += (Math.sqrt(r1) * (1 - r2)) * vertex.x;
						zc += (Math.sqrt(r1) * (1 - r2)) * vertex.z;
						break;
					case 2:
						xc += (Math.sqrt(r1) * r2) * vertex.x;
						zc += (Math.sqrt(r1) * r2) * vertex.z;
						break;
					}
				}
				Vec3d rp = new Vec3d(xc, vertices[0].y, zc);

				double sx = this.posX + rp.x;
				double sy = this.posY + rp.y + 4;
				double sz = this.posZ + rp.z;
				double ex = this.posX + rp.x;
				double ey = this.posY + rp.y;
				double ez = this.posZ + rp.z;

				if(this.getOwner() != null) {
					sx = this.getOwner().posX;
					sy = this.getOwner().getEntityBoundingBox().minY + (this.getOwner().getEntityBoundingBox().maxY - this.getOwner().getEntityBoundingBox().minY) / 2.0D;
					sz = this.getOwner().posZ;
				}

				if(!this.isPlayerControlled()) {
					this.world.spawnParticle(EnumParticleTypes.PORTAL, sx, sy, sz, ex - sx, ey - sy, ez - sz);
				}
			}
		}
	}

	@Override
	public void travel(float strafe, float up, float forward) {
		if (this.isInWater()) {
			this.moveRelative(strafe, up, forward, 0.02F);
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.800000011920929D;
			this.motionY *= 0.800000011920929D;
			this.motionZ *= 0.800000011920929D;
		} else {
			float friction = 0.91F;

			if (this.onGround) {
				friction = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.91F;
			}

			float groundFriction = 0.16277136F / (friction * friction * friction);
			this.moveRelative(strafe, up, forward, this.onGround ? 0.1F * groundFriction : 0.02F);
			friction = 0.91F;

			if (this.onGround) {
				friction = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.91F;
			}

			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double)friction;
			this.motionY *= (double)friction;
			this.motionZ *= (double)friction;
		}

		this.prevLimbSwingAmount = this.limbSwingAmount;
		double dx = this.posX - this.prevPosX;
		double dz = this.posZ - this.prevPosZ;
		float distanceMoved = MathHelper.sqrt(dx * dx + dz * dz) * 4.0F;

		if (distanceMoved > 1.0F) {
			distanceMoved = 1.0F;
		}

		this.limbSwingAmount += (distanceMoved - this.limbSwingAmount) * 0.4F;
		this.limbSwing += this.limbSwingAmount;
	}

	public Vec3d[] getTriangleVertices(float partialTicks) {
		Vec3d[] vertices = new Vec3d[3];
		double rot = Math.toRadians(this.prevRotation + (this.rotation - this.prevRotation) * partialTicks);
		double angle = Math.PI * 2.0D / 3.0D;
		for(int i = 0; i < 3; i++) {
			double sin = Math.sin(angle * i + rot);
			double cos = Math.cos(angle * i + rot);
			vertices[i] = new Vec3d(sin * this.getTriangleSize(), 0, cos * this.getTriangleSize());
		}
		return vertices;
	}

	@Override
	public boolean attackEntityAsMob(Entity target) {
		return false;
	}

	@Override
	protected void collideWithEntity(Entity entityIn) { }
	
	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
        return !DamageSource.OUT_OF_WORLD.getDamageType().equals(source.getDamageType()) && source.getImmediateSource() instanceof EntityShockwaveBlock == false;
    }

	@Override
    public boolean getIsInvulnerable() {
		return true;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeFloat(this.rotation);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		this.prevRotation = this.rotation = buffer.readFloat();
		this.dataManager.set(ROTATION, this.rotation);
	}
}
