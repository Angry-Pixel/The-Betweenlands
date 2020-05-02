package thebetweenlands.common.entity.projectiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import thebetweenlands.common.entity.mobs.EntityDreadfulMummy;
import thebetweenlands.common.entity.mobs.EntityMultipartDummy;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;

public class EntitySludgeBall extends EntityThrowable {
	private int bounces = 0;
	private String ownerUUID;
	private boolean breakBlocks;

	public EntitySludgeBall(World world) {
		super(world);
		this.setSize(0.75F, 0.75F);
		this.ownerUUID = "";
		this.breakBlocks = false;
	}

	public EntitySludgeBall(World world, EntityLivingBase owner, boolean breakBlocks) {
		this(world);
		this.ownerUUID = owner.getUniqueID().toString();
		this.thrower = owner;
		this.breakBlocks = breakBlocks;
	}

	public Entity getOwner() {
		try {
			UUID uuid = UUID.fromString(this.ownerUUID);
			return uuid == null ? null : this.getEntityByUUID(uuid);
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
		}
	}

	private Entity getEntityByUUID(UUID uuid) {
		for (int i = 0; i < this.getEntityWorld().loadedEntityList.size(); ++i) {
			Entity entity = (Entity)this.getEntityWorld().loadedEntityList.get(i);
			if (uuid.equals(entity.getUniqueID())) {
				return entity;
			}
		}
		return null;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	public void onUpdate() {
		double prevMotionX = this.motionX;
		double prevMotionY = this.motionY;
		double prevMotionZ = this.motionZ;
		double prevX = this.posX;
		double prevY = this.posY;
		double prevZ = this.posZ;
		move(MoverType.SELF,this.motionX, this.motionY, this.motionZ);
		if(!this.getEntityWorld().isRemote && new Vec3d(prevX - this.posX, prevY - this.posY, prevZ - this.posZ).lengthSquared() < 0.001D && this.ticksExisted > 10) {
			this.explode();
		}
		this.motionX = prevMotionX;
		this.motionY = prevMotionY;
		this.motionZ = prevMotionZ;
		this.setPosition(prevX, prevY, prevZ);
		
		super.onUpdate();
	}
	
	@Override
	public void move(MoverType type, double x, double y, double z) {
		if(this.ticksExisted < 2) {
			//Stupid EntityTrackerEntry is broken and desyncs server position.
			//Tracker updates server side position but *does not* send the change to the client
			//when tracker.updateCounter == 0, causing a desync until the next force teleport
			//packet.......
			//By not moving the entity until then it works.
			return;
		}
		
		super.move(type, x, y, z);
	}

	@Override
	protected void onImpact(RayTraceResult collision) {
		if(collision.typeOfHit == RayTraceResult.Type.BLOCK) {
			IBlockState state = getEntityWorld().getBlockState(collision.getBlockPos());
			
			if (state.getBlock().canCollideCheck(state, false)) {
				List<AxisAlignedBB> aabbs = new ArrayList<>();
				state.addCollisionBoxToList(this.world, collision.getBlockPos(), this.getEntityBoundingBox().offset(this.motionX, this.motionY, this.motionZ), aabbs, this, true);
				
				if(!aabbs.isEmpty()) {
					if(!this.world.isRemote && ForgeEventFactory.getMobGriefingEvent(this.world, this) && this.bounces == 0) {
						Entity owner = this.getOwner();
						
						if(owner instanceof EntityLivingBase && this.posY > owner.getEntityBoundingBox().maxY && this.motionY > 0.1D) {
							BlockPos pos = collision.getBlockPos();
							IBlockState hitState = this.world.getBlockState(pos);
							float hardness = hitState.getBlockHardness(this.world, pos);
							
							if(!hitState.getBlock().isAir(hitState, this.world, pos) && hardness >= 0 && hardness <= 2.5F
									&& hitState.getBlock().canEntityDestroy(hitState, this.world, pos, (EntityLivingBase) owner)
									&& ForgeEventFactory.onEntityDestroyBlock((EntityLivingBase) owner, pos, hitState)) {
								this.world.playEvent(2001, pos, Block.getStateId(hitState));
								this.world.setBlockToAir(pos);
								
								explode();
							}
						}
					}
					
					if(Math.abs(this.motionY) <= 0.001) {
						if(this.getEntityWorld().isRemote)
							this.motionX = this.motionY = this.motionZ = 0.0D;
						else 
							explode();
					}
					
					if (collision.sideHit.getAxis() == Axis.Y) {
						this.motionY *= -0.9D;
						this.velocityChanged = true;
						this.bounces++;
						if (this.bounces >= 3) {
							if(this.getEntityWorld().isRemote)
								this.motionX = this.motionY = this.motionZ = 0.0D;
							else 
								explode();
						} else {
							getEntityWorld().playSound(null, getPosition(), SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.HOSTILE, 1, 0.9f);
							spawnBounceParticles(8);
						}
					} else if (collision.sideHit.getAxis() == Axis.Z) {
						this.motionZ *= -0.9D;
						this.velocityChanged = true;
						this.bounces++;
						if(this.bounces >= 3) {
							if(this.getEntityWorld().isRemote)
								this.motionX = this.motionY = this.motionZ = 0.0D;
							else 
								explode();
						}
					} else if (collision.sideHit.getAxis() == Axis.X) {
						this.motionX *= -0.9D;
						this.velocityChanged = true;
						this.bounces++;
						if(this.bounces >= 3) {
							if(this.getEntityWorld().isRemote)
								this.motionX = this.motionY = this.motionZ = 0.0D;
							else 
								explode();
						}
					}
				}
			}
		}
		
		if (collision.typeOfHit == RayTraceResult.Type.ENTITY) {
			if(collision.entityHit != this.thrower && !(collision.entityHit instanceof EntitySludgeBall) && !(collision.entityHit instanceof MultiPartEntityPart) &&  !(collision.entityHit instanceof EntityMultipartDummy) && !(collision.entityHit instanceof EntityPeatMummy) && !(collision.entityHit instanceof EntityDreadfulMummy)) {
				if(this.attackEntity(collision.entityHit)) {
					explode();
				} else {
					this.motionX *= -0.1D;
	                this.motionY *= -0.1D;
	                this.motionZ *= -0.1D;
	                this.bounces++;
				}
			}
		}
	}

	private void explode() {
		if(!this.getEntityWorld().isRemote) {
			float radius = 3;
			AxisAlignedBB region = new AxisAlignedBB(this.posX - radius, this.posY - radius, this.posZ - radius, this.posX + radius, this.posY + radius, this.posZ + radius);
			List<Entity> entities = this.getEntityWorld().getEntitiesWithinAABBExcludingEntity(this, region);
			double radiusSq = radius * radius;
			for (Entity entity : entities) {
				if (entity instanceof EntityLivingBase && !(entity instanceof EntityPeatMummy) && !(entity instanceof EntityDreadfulMummy) && getDistanceSq(entity) < radiusSq) {
					this.attackEntity(entity);
				}
			}
			getEntityWorld().playSound(null, getPosition(), SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.HOSTILE, 1, 0.5f);
			getEntityWorld().playSound(null, getPosition(), SoundEvents.ENTITY_SMALL_SLIME_SQUISH, SoundCategory.HOSTILE, 1, 0.5f);
			setDead();
		} else {
			//TODO Better explosion particle effects
			spawnBounceParticles(20);
		}
	}

	private boolean attackEntity(Entity entity) {
		boolean attacked = false;
		Entity owner = this.getOwner();
		if (owner != null) {
			attacked = entity.attackEntityFrom(new EntityDamageSourceIndirect("mob", this, owner).setProjectile(), 8);
		} else {
			attacked = entity.attackEntityFrom(new EntityDamageSource("entity", this).setProjectile(), 8);
		}
		if(!this.world.isRemote && attacked && entity instanceof EntityLivingBase) {
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 80, 3));
		}
		return attacked;
	}
	
	@Override
	protected float getGravityVelocity() {
		return 0.08F;
	}

	private void spawnBounceParticles(int amount) {
		for (int i = 0; i <= amount; i++) {
			this.getEntityWorld().spawnParticle(EnumParticleTypes.SLIME, this.posX + (amount/8) * (this.rand.nextFloat() - 0.5), this.posY + 0.3, this.posZ + (amount/8) * (this.rand.nextFloat() - 0.5), 0, 0, 0);
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("bounces", this.bounces);
		nbt.setString("ownerUUID", this.ownerUUID);
		nbt.setBoolean("breakBlocks", this.breakBlocks);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.bounces = nbt.getInteger("bounces");
		this.ownerUUID = nbt.getString("ownerUUID");
		this.breakBlocks = nbt.getBoolean("breakBlocks");
	}
}
