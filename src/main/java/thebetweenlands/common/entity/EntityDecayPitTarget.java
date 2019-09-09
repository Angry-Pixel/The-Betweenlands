package thebetweenlands.common.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityMusic;
import thebetweenlands.client.audio.EntityMusicLayers;
import thebetweenlands.common.entity.mobs.EntityFortressBoss;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.sound.BLSoundEvent;
import thebetweenlands.common.tile.TileEntityDecayPitControl;
import thebetweenlands.common.tile.TileEntityDecayPitGroundChain;
import thebetweenlands.common.tile.TileEntityDecayPitHangingChain;
import thebetweenlands.util.RotationMatrix;

public class EntityDecayPitTarget extends Entity implements IEntityMultiPartPitTarget, IEntityMusic {
	private static final byte EVENT_ATTACK_BLOCKED = 80;
	private static final byte EVENT_ATTACK_DAMAGE = 81;

	private final RotationMatrix rotationMatrix = new RotationMatrix();

	public float animationTicksPrev = 0;
	public int animationTicksChain = 0;
	public int animationTicksChainPrev = 0;
	public final int MAX_PROGRESS = 768; // max distance of travel from origin so; 768 * 0.0078125F = 6 Blocks
	public final int MIN_PROGRESS = 0;
	public final float MOVE_UNIT = 0.0078125F; // unit of movement 
	public EntityDecayPitTargetPart[] parts;
	public EntityDecayPitTargetPart shield_1;
	public EntityDecayPitTargetPart shield_2;
	public EntityDecayPitTargetPart shield_3;
	public EntityDecayPitTargetPart shield_4;
	public EntityDecayPitTargetPart shield_5;
	public EntityDecayPitTargetPart shield_6;
	public EntityDecayPitTargetPart shield_7;
	public EntityDecayPitTargetPart shield_8;
	public EntityDecayPitTargetPart shield_9;
	public EntityDecayPitTargetPart shield_10;
	public EntityDecayPitTargetPart shield_11;
	public EntityDecayPitTargetPart shield_12;
	public EntityDecayPitTargetPart shield_13;
	public EntityDecayPitTargetPart shield_14;
	public EntityDecayPitTargetPart shield_15;
	public EntityDecayPitTargetPart shield_16;
	public EntityDecayPitTargetPart target_north;
	public EntityDecayPitTargetPart target_east;
	public EntityDecayPitTargetPart target_west;
	public EntityDecayPitTargetPart target_south;
	public EntityDecayPitTargetPart bottom;

	private static final DataParameter<Float> ANIMATION_TICKS = EntityDataManager.createKey(EntityDecayPitTarget.class, DataSerializers.FLOAT);
	private static final DataParameter<Boolean> IS_RAISING = EntityDataManager.createKey(EntityDecayPitTarget.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_MOVING = EntityDataManager.createKey(EntityDecayPitTarget.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_SLOW = EntityDataManager.createKey(EntityDecayPitTarget.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> PROGRESS = EntityDataManager.createKey(EntityDecayPitTarget.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> TARGET_N_ACTIVE = EntityDataManager.createKey(EntityDecayPitTarget.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> TARGET_E_ACTIVE = EntityDataManager.createKey(EntityDecayPitTarget.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> TARGET_S_ACTIVE = EntityDataManager.createKey(EntityDecayPitTarget.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> TARGET_W_ACTIVE = EntityDataManager.createKey(EntityDecayPitTarget.class, DataSerializers.BOOLEAN);

	public int attackDamageTicks = 0;
	public int[] beamTransparencyTicks = new int[4];

	public EntityDecayPitTarget(World world) {
		super(world);
		setSize(5F, 5F);
		parts = new EntityDecayPitTargetPart[] {
				shield_1 = new EntityDecayPitTargetPart(this, "part1", 1F, 1F, true),
				shield_2 = new EntityDecayPitTargetPart(this, "part2", 1F, 1F, true),
				shield_3 = new EntityDecayPitTargetPart(this, "part3", 1F, 1F, true),
				shield_4 = new EntityDecayPitTargetPart(this, "part4", 1F, 1F, true),
				shield_5 = new EntityDecayPitTargetPart(this, "part5", 1F, 1F, true),
				shield_6 = new EntityDecayPitTargetPart(this, "part6", 1F, 1F, true),
				shield_7 = new EntityDecayPitTargetPart(this, "part7", 1F, 1F, true),
				shield_8 = new EntityDecayPitTargetPart(this, "part8", 1F, 1F, true),
				shield_9 = new EntityDecayPitTargetPart(this, "part9", 1F, 1F, true),
				shield_10 = new EntityDecayPitTargetPart(this, "part10", 1F, 1F, true),
				shield_11 = new EntityDecayPitTargetPart(this, "part11", 1F, 1F, true),
				shield_12 = new EntityDecayPitTargetPart(this, "part12", 1F, 1F, true),
				shield_13 = new EntityDecayPitTargetPart(this, "part13", 1F, 1F, true),
				shield_14 = new EntityDecayPitTargetPart(this, "part16", 1F, 1F, true),
				shield_15 = new EntityDecayPitTargetPart(this, "part15", 1F, 1F, true),
				shield_16 = new EntityDecayPitTargetPart(this, "part16", 1F, 1F, true),
				target_north = new EntityDecayPitTargetPart(this, "target_north", 2F, 2F, false),
				target_east = new EntityDecayPitTargetPart(this, "target_east", 2F, 2F, false),
				target_south = new EntityDecayPitTargetPart(this, "target_south", 2F, 2F, false),
				target_west = new EntityDecayPitTargetPart(this, "target_west", 2F, 2F, false),
				bottom = new EntityDecayPitTargetPart(this, "bottom", 3F, 1F, false),
				};
	}

	@Override
	protected void entityInit() {
		dataManager.register(IS_RAISING, false);
		dataManager.register(IS_MOVING, false);
		dataManager.register(IS_SLOW, true);
		dataManager.register(PROGRESS, 0);
		dataManager.register(ANIMATION_TICKS, 0.0F);
		dataManager.register(TARGET_N_ACTIVE, false);
		dataManager.register(TARGET_E_ACTIVE, false);
		dataManager.register(TARGET_W_ACTIVE, false);
		dataManager.register(TARGET_S_ACTIVE, false);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		
		if(this.attackDamageTicks > 0) {
			this.attackDamageTicks--;
		}
		
		boolean isMovingDown = this.isMoving() && !this.isSlow();
		
		if(this.getTargetEActive() && !isMovingDown) {
			this.beamTransparencyTicks[0] = Math.min(15, this.beamTransparencyTicks[0] + 1);
		} else {
			this.beamTransparencyTicks[0] = Math.max(0, this.beamTransparencyTicks[0] - 1);
		}
		
		if(this.getTargetWActive() && !isMovingDown) {
			this.beamTransparencyTicks[1] = Math.min(15, this.beamTransparencyTicks[1] + 1);
		} else {
			this.beamTransparencyTicks[1] = Math.max(0, this.beamTransparencyTicks[1] - 1);
		}
		
		if(this.getTargetSActive() && !isMovingDown) {
			this.beamTransparencyTicks[2] = Math.min(15, this.beamTransparencyTicks[2] + 1);
		} else {
			this.beamTransparencyTicks[2] = Math.max(0, this.beamTransparencyTicks[2] - 1);
		}
		
		if(this.getTargetNActive() && !isMovingDown) {
			this.beamTransparencyTicks[3] = Math.min(15, this.beamTransparencyTicks[3] + 1);
		} else {
			this.beamTransparencyTicks[3] = Math.max(0, this.beamTransparencyTicks[3] - 1);
		}
		
		float animationTicks = this.dataManager.get(ANIMATION_TICKS);
		
		animationTicksPrev = animationTicks;
		animationTicksChainPrev = animationTicksChain;
		
		if(!this.world.isRemote) {
			if (animationTicks + 1 >= 360F) {
				this.dataManager.set(ANIMATION_TICKS, 0.0F);
			} else {
				this.dataManager.set(ANIMATION_TICKS, animationTicks + 1);
			}
		}

		while(animationTicks - this.animationTicksPrev < -180.0F) {
			this.animationTicksPrev -= 360.0F;
        }
        while(animationTicks - this.animationTicksPrev >= 180.0F) {
        	this.animationTicksPrev += 360.0F;
        }
		
        //Set prev pos, rotation, etc.
        for(Entity entity : parts) {
        	entity.onUpdate();
        }
        
		setNewShieldHitboxPos(animationTicks, shield_1);
		setNewShieldHitboxPos(-animationTicks + 22.5F, shield_2);
		setNewShieldHitboxPos(animationTicks + 45F, shield_3);
		setNewShieldHitboxPos(-animationTicks + 67.5F, shield_4);
		setNewShieldHitboxPos(animationTicks + 90F, shield_5);
		setNewShieldHitboxPos(-animationTicks + 112.5F, shield_6);
		setNewShieldHitboxPos(animationTicks + 135F, shield_7);
		setNewShieldHitboxPos(-animationTicks + 157F, shield_8);

		setNewShieldHitboxPos(animationTicks + 180F, shield_9);
		setNewShieldHitboxPos(-animationTicks + 202.5F, shield_10);
		setNewShieldHitboxPos(animationTicks + 225F, shield_11);
		setNewShieldHitboxPos(-animationTicks + 247.5F, shield_12);
		setNewShieldHitboxPos(animationTicks + 270F, shield_13);
		setNewShieldHitboxPos(-animationTicks + 292.5F, shield_14);
		setNewShieldHitboxPos(animationTicks + 315F, shield_15);
		setNewShieldHitboxPos(-animationTicks + 337.5F, shield_16);
		
		target_north.setPosition(posX, posY + 3D, posZ - 0.75D);
		target_east.setPosition(posX + 0.75D, posY + 3D, posZ);
		target_south.setPosition(posX, posY + 3D, posZ + 0.75D);
		target_west.setPosition(posX - 0.75D, posY + 3D, posZ);
		bottom.setPosition(posX, posY, posZ);

		if (isMoving()) {
			if (isSlow())
				animationTicksChain++;
			else
				animationTicksChain += 8;
			if(getHangingChains() != null)
				getHangingChains().setProgress(getProgress());

			if (!isRaising() && getProgress() < MAX_PROGRESS) {
				move(MoverType.SELF, 0D, -MOVE_UNIT * 8D, 0D);
				setProgress(getProgress() + 8);

				if (getHangingChains() != null) {
					getHangingChains().setMoving(true);
					getHangingChains().setSlow(false);
				}

				if (getGroundChains() != null) {
					for (TileEntityDecayPitGroundChain chain : getGroundChains()) {
						chain.setRaising(true);
						chain.setMoving(true);
						chain.setSlow(false);
					}
				}
			}

			if (isRaising() && getProgress() > MIN_PROGRESS) {
				move(MoverType.SELF, 0D, + MOVE_UNIT, 0D);
				setProgress(getProgress() - 1);

				if (getHangingChains() != null) {
					getHangingChains().setMoving(true);
					getHangingChains().setSlow(true);
				}

				if (getGroundChains() != null) {
					for (TileEntityDecayPitGroundChain chain : getGroundChains()) {
						chain.setRaising(false);
						chain.setMoving(true);
						chain.setSlow(true);
					}
				}
			}

		}

		if (animationTicksChainPrev >= 128) {
			animationTicksChain = animationTicksChainPrev = 0;
			setMoving(false);
		}

		if (getProgress() > MAX_PROGRESS)
			setProgress(MAX_PROGRESS);
		
		if (getProgress() < MIN_PROGRESS)
			setProgress(MIN_PROGRESS);

		if (!getEntityWorld().isRemote) { // upsy-daisy
			if (getProgress() > MIN_PROGRESS && getEntityWorld().getTotalWorldTime() % 400 == 0 && attackDamageTicks == 0)
				moveUp();

			TileEntityDecayPitControl control = this.getControl();
			
			if (control != null && getEntityWorld().getTotalWorldTime() % 10 == 0) {
				if (getProgress() < 128)
					control.setSpawnType(0);
				if (getProgress() >= 128 && getProgress() < 256)
					control.setSpawnType(1);
				if (getProgress() >= 256 && getProgress() < 384)
					control.setSpawnType(2);
				if (getProgress() >= 384 && getProgress() < 512)
					control.setSpawnType(3);
				if (getProgress() >= 512 && getProgress() < 640)
					control.setSpawnType(4);
				if (getProgress() >= 640)
					control.setSpawnType(5);
	
			}
			
			if (control == null || control.getSpawnType() == 5) {
				TileEntityDecayPitHangingChain hangingChains = this.getHangingChains();
				
				if (hangingChains != null) {
					hangingChains.setBroken(true);
					hangingChains.setMoving(true);
					hangingChains.setSlow(false);
					hangingChains.updateBlock();
				}

				if (getGroundChains() != null) {
					for (TileEntityDecayPitGroundChain chain : getGroundChains()) {
						chain.setBroken(true);
						chain.setRaising(false);
						chain.setMoving(true);
						chain.setSlow(false);
						chain.updateBlock();
					}
				}
				setDead(); // TODO some particles to show the bobbing shields break
			}
		}
	}

	protected void setHangingLength(EntityDecayPitTargetPart chain, float extended) {
		chain.height = extended;
		AxisAlignedBB axisalignedbb = new AxisAlignedBB(chain.posX - chain.width * 0.5D, posY + height, chain.posZ - chain.width * 0.5D, chain.posX + chain.width * 0.5D, posY + height + 2F + getProgress() * MOVE_UNIT, chain.posZ + chain.width * 0.5D);
		chain.setEntityBoundingBox(axisalignedbb);
		chain.onUpdate();
	}

	protected void setNewShieldHitboxPos(float animationTicks, EntityDecayPitTargetPart shield) {
		double a = Math.toRadians(animationTicks);
		double offSetX = -Math.sin(a) * 2.825D;
		double offSetZ = Math.cos(a) * 2.825D;
		float wobble = 0F;
		if (shield == shield_1 || shield == shield_3 || shield == shield_5 || shield == shield_7 || shield == shield_9 || shield == shield_11 || shield == shield_13 || shield == shield_15)
			wobble = MathHelper.sin(animationTicks * 0.14F) * 1.2F;
		else
			wobble = -MathHelper.sin(animationTicks * 0.14F) * 1.2F;
		float squarePoint = Math.signum(wobble);
		if(squarePoint == -1F)
			wobble = 0F;
		shield.setPosition(posX + offSetX, target_north.posY + target_north.height / 2.0D - shield.height + wobble, posZ + offSetZ);
		shield.rotationYaw = animationTicks + 180F;
		
		while(shield.rotationYaw - shield.prevRotationYaw < -180.0F) {
			shield.prevRotationYaw -= 360.0F;
        }
        while(shield.rotationYaw - shield.prevRotationYaw >= 180.0F) {
        	shield.prevRotationYaw += 360.0F;
        }
	}

	@Override
	public void setDead() {
		// TODO Not this, will add something better, this is a placeholder test. ;P
		/*
		  for(EntityDecayPitTargetPart shieldPart : parts)
		  	if(shieldPart.isShield)
		  		getEntityWorld().playEvent(null, 2001, shieldPart.getPosition(), Block.getIdFromBlock(BlockRegistry.SMOOTH_PITSTONE));
		 */
		super.setDead();
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Nullable
	public Entity[] getParts() {
		return parts;
	}

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0D;
		motionY = 0D;
		motionZ = 0D;
	}

	@Override
	public boolean getIsInvulnerable() {
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		return false;
	}

	@Override
	public boolean attackEntityFromPart(EntityDecayPitTargetPart part, DamageSource source, float damage) {
		boolean wasBlocked = false;
		
		if(source instanceof EntityDamageSource) {
			EntityDamageSource entityDamage = (EntityDamageSource) source;
			
			Entity sourceEntity = entityDamage.getTrueSource();
			Entity immediateEntity = entityDamage.getImmediateSource();

			Entity attackingEntity = immediateEntity != null ? immediateEntity : sourceEntity;
			
			if(attackingEntity == null) {
				wasBlocked = true;
			} else {
				Vec3d pos = new Vec3d(attackingEntity.posX, attackingEntity.posY + attackingEntity.getEyeHeight(), attackingEntity.posZ);
				
				Vec3d ray;
				if(attackingEntity instanceof EntityLivingBase) {
					ray = attackingEntity.getLookVec();
				} else {
					ray = new Vec3d(attackingEntity.motionX, attackingEntity.motionY, attackingEntity.motionZ).normalize();
				}
				
				EntityDecayPitTargetPart hitShield = this.rayTraceShields(pos, ray);
				if(hitShield != null) {
					wasBlocked = true;
				}
			}
		}

		if(part == target_north && !wasBlocked) {
			if(!this.world.isRemote) {
				if(getTargetNActive()) {
					setTargetNActive(false);
					playHitSound(getWorld(), target_north.getPosition());
				}
				if(getAllTargetsHit())
					moveDown();
				this.world.setEntityState(this, EVENT_ATTACK_DAMAGE);
			}
			return true;
		}
		else if(part == target_east && !wasBlocked) {
			if(!this.world.isRemote) {
				if(getTargetEActive()) {
					setTargetEActive(false);
					playHitSound(getWorld(), target_east.getPosition());
				}
				if(getAllTargetsHit())
					moveDown();
				this.world.setEntityState(this, EVENT_ATTACK_DAMAGE);
			}
			return true;
		}
		else if(part == target_south && !wasBlocked) {
			if(!this.world.isRemote) {
				if(getTargetSActive()) {
					setTargetSActive(false);
					playHitSound(getWorld(), target_south.getPosition());
				}
				if(getAllTargetsHit())
					moveDown();
				this.world.setEntityState(this, EVENT_ATTACK_DAMAGE);
			}
			return true;
		}
		else if(part == target_west && !wasBlocked) {
			if(!this.world.isRemote) {
				if(getTargetWActive()) {
					setTargetWActive(false);
					playHitSound(getWorld(), target_west.getPosition());
				}
				if(getAllTargetsHit())
					moveDown();
				this.world.setEntityState(this, EVENT_ATTACK_DAMAGE);
			}
			return true;
		}
		else if(wasBlocked) {
			if(!this.world.isRemote) {
				if(source instanceof EntityDamageSourceIndirect) {
					Entity sourceEntity = ((EntityDamageSourceIndirect) source).getTrueSource();
					if(sourceEntity != null && !world.isAirBlock(sourceEntity.getPosition().down())) {
						EntityRootGrabber grabber = new EntityRootGrabber(this.world, true);
						grabber.setPosition(source.getTrueSource().getPosition().down(), 40);
						getEntityWorld().spawnEntity(grabber);
					}
				}
				this.moveUp();
				this.world.setEntityState(this, EVENT_ATTACK_BLOCKED);
			}
			return false;
		}

		return false;
	}
	
	public void playHitSound(World world, BlockPos pos) {
		getWorld().playSound(null, getPosition(), SoundRegistry.BEAM_ACTIVATE, SoundCategory.HOSTILE, 1F, 1F);
	}

	public void playDropSound(World world, BlockPos pos) {
		getWorld().playSound(null, getPosition(), SoundRegistry.PLUG_HIT, SoundCategory.HOSTILE, 0.5F, 1F);
	}

	private boolean getAllTargetsHit() {
		return !getTargetNActive() && !getTargetEActive() && !getTargetSActive() && !getTargetWActive();
	}

	@Nullable
	public EntityDecayPitTargetPart rayTraceShields(Vec3d pos, Vec3d dir) {
		Vec3d ray = dir.normalize().scale(3);
		
		float shieldSize = 0.6F;
		
		Vec3d v0 = new Vec3d(-shieldSize, -shieldSize, 0);
		Vec3d v1 = new Vec3d(shieldSize, -shieldSize, 0);
		Vec3d v2 = new Vec3d(shieldSize, shieldSize, 0);
		Vec3d v3 = new Vec3d(-shieldSize, shieldSize, 0);
		
		for(EntityDecayPitTargetPart shieldPart : parts) {
			if(shieldPart.isShield) {
				Vec3d center = shieldPart.getPositionVector().add(0, shieldPart.height / 2, 0);
				
				this.rotationMatrix.setRotations(0, (float)Math.toRadians(shieldPart.rotationYaw), 0);
				
				Vec3d relPos = this.rotationMatrix.transformVec(pos.subtract(center), Vec3d.ZERO);;
				Vec3d relRay = this.rotationMatrix.transformVec(ray, Vec3d.ZERO);
				
				if(EntityFortressBoss.rayTraceTriangle(relPos, relRay, v0, v1, v2) || EntityFortressBoss.rayTraceTriangle(relPos, relRay, v2, v3, v0)) {
					return shieldPart;
				}
			}
		}
		
		return null;
	}
	
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		
		if(id == EVENT_ATTACK_DAMAGE) {
			this.attackDamageTicks = 40;
		}
	}
	
	private void moveUp() {
		if (getProgress() > MIN_PROGRESS) {
			setRaising(true);
			setMoving(true);
			setSlow(true);
		}
	}

	private void moveDown() {
		if (getProgress() < MAX_PROGRESS) {
			setRaising(false);
			setMoving(true);
			setSlow(false);
			setTargetNActive(true);
			setTargetEActive(true);
			setTargetSActive(true);
			setTargetWActive(true);
		}
		playDropSound(getWorld(), getPosition());
	}

	public TileEntityDecayPitHangingChain getHangingChains() {
		TileEntityDecayPitHangingChain tile = null;
		for (int x = -1; x < 1; x++)
			for (int y = 0; y < 15; y++)
				for (int z = -1; z < 1; z++) {
					if (getWorld().getTileEntity(getPosition().add(x, y, z)) instanceof TileEntityDecayPitHangingChain) {
						tile = (TileEntityDecayPitHangingChain) getWorld().getTileEntity(getPosition().add(x, y, z));
						tile.setProgress(getProgress());
					}
				}
		return tile;
	}

	public TileEntityDecayPitControl getControl() {
		TileEntityDecayPitControl tile = null;
		for (int x = -1; x < 1; x++)
			for (int y = -15; y < 0; y++)
				for (int z = -1; z < 1; z++) {
					if (getWorld().getTileEntity(getPosition().add(x, y, z)) instanceof TileEntityDecayPitControl)
						tile = (TileEntityDecayPitControl) getWorld().getTileEntity(getPosition().add(x, y, z));
				}
		return tile;
	}

	public List<TileEntityDecayPitGroundChain> getGroundChains() {
		TileEntityDecayPitGroundChain tile = null;
		List<TileEntityDecayPitGroundChain> chains = new ArrayList<TileEntityDecayPitGroundChain>();
		BlockPos posEntity = getPosition();
		Iterable<BlockPos> blocks = BlockPos.getAllInBox(posEntity.add(-12.0F, 3F, -12F), posEntity.add(12F, 9F, 12F));
		for (BlockPos pos : blocks)
			if (getWorld().getTileEntity(pos) instanceof TileEntityDecayPitGroundChain) {
				tile = (TileEntityDecayPitGroundChain) getWorld().getTileEntity(pos);
				chains.add(tile);
			}
		return chains;
	}

	public void setProgress(int progress) {
		dataManager.set(PROGRESS, progress);
	}

	public int getProgress() {
		return dataManager.get(PROGRESS);
	}

	public void setRaising(boolean raising) {
		dataManager.set(IS_RAISING, raising);
	}

	public boolean isRaising() {
		return dataManager.get(IS_RAISING);
	}

	public void setMoving(boolean moving) {
		dataManager.set(IS_MOVING, moving);
	}
	
	public boolean isMoving() {
		return dataManager.get(IS_MOVING);
	}

	public void setSlow(boolean slow) {
		dataManager.set(IS_SLOW, slow);
	}

	public boolean isSlow() {
		return dataManager.get(IS_SLOW);
	}

	public void setTargetNActive(boolean active) {
		dataManager.set(TARGET_N_ACTIVE, active);
	}

	public boolean getTargetNActive() {
		return dataManager.get(TARGET_N_ACTIVE);
	}

	public void setTargetEActive(boolean active) {
		dataManager.set(TARGET_E_ACTIVE, active);
	}

	public boolean getTargetEActive() {
		return dataManager.get(TARGET_E_ACTIVE);
	}

	public void setTargetWActive(boolean active) {
		dataManager.set(TARGET_W_ACTIVE, active);
	}

	public boolean getTargetWActive() {
		return dataManager.get(TARGET_W_ACTIVE);
	}

	public void setTargetSActive(boolean active) {
		dataManager.set(TARGET_S_ACTIVE, active);
	}

	public boolean getTargetSActive() {
		return dataManager.get(TARGET_S_ACTIVE);
	}

	@Override
	public World getWorld() {
		return getEntityWorld();
	}

	@Override
	public BLSoundEvent getMusicFile(EntityPlayer listener) {
		return SoundRegistry.PIT_OF_DECAY_LOOP;
	}

	@Override
	public double getMusicRange(EntityPlayer listener) {
		return 16D;
	}

	@Override
	public boolean isMusicActive(EntityPlayer listener) {
		return isEntityAlive() && canEntityBeSeen(listener);
	}

	@Override
	public int getMusicLayer(EntityPlayer listener) {
		return EntityMusicLayers.BOSS;
	}

    public boolean canEntityBeSeen(Entity entity) {
        return getEntityWorld().rayTraceBlocks(new Vec3d(posX, posY + (double)getEyeHeight(), posZ), new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ), false, true, false) == null;
    }

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setProgress(nbt.getInteger("progress"));
		setTargetNActive(nbt.getBoolean("target_north"));
		setTargetEActive(nbt.getBoolean("target_east"));
		setTargetWActive(nbt.getBoolean("target_west"));
		setTargetSActive(nbt.getBoolean("target_south"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("progress", getProgress());
		nbt.setBoolean("target_north", getTargetNActive());
		nbt.setBoolean("target_east", getTargetEActive());
		nbt.setBoolean("target_west", getTargetWActive());
		nbt.setBoolean("target_south", getTargetSActive());
	}
}