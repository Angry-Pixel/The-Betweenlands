package thebetweenlands.common.entity.mobs;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.common.entity.projectiles.EntitySapSpit;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.world.gen.feature.tree.WorldGenSpiritTreeStructure;

public abstract class EntitySpiritTreeFace extends EntityMovingWallFace {
	public static final byte EVENT_ATTACKED = 2;
	public static final byte EVENT_DEATH = 3;
	public static final byte EVENT_EMERGE_SOUND = 81;
	public static final byte EVENT_HURT_SOUND = 82;
	public static final byte EVENT_SPIT = 83;

	protected int spitTicks = 0;
	protected float spitDamage;

	private boolean emergeSound = false;

	protected int prevGlowTicks = 0;
	protected int glowTicks = 0;
	protected int glowDuration = 0;

	public EntitySpiritTreeFace(World world) {
		super(world);
		this.experienceValue = 4;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0D);
	}

	public void setGlowTicks(int duration) {
		duration = Math.max(duration, 1);
		this.glowTicks = duration;
		this.glowDuration = duration;
	}

	public float getGlow(float partialTicks) {
		return (this.prevGlowTicks + (this.glowTicks - this.prevGlowTicks) * partialTicks) / (float)this.glowDuration;
	}

	protected void playSpitSound() {

	}

	protected void playEmergeSound() {

	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	public boolean isSilent() {
		return super.isSilent() || !this.isActive();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return null;
	}

	@Override
	protected void playHurtSound(DamageSource source) {
		this.world.setEntityState(this, EVENT_HURT_SOUND);
	}

	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_SPIT) {
			if(this.glowTicks < 10) {
				this.setGlowTicks(10);
			}
		} else if(id == EVENT_ATTACKED) {
			if(this.glowTicks < 10) {
				this.setGlowTicks(10);
			}
		} else if(id == EVENT_HURT_SOUND || id == EVENT_DEATH) {
			SoundType soundType = SoundType.WOOD;
			this.world.playSound(this.posX, this.posY, this.posZ, soundType.getBreakSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 1.3F, soundType.getPitch() * 0.8F, false);
			this.world.playSound(this.posX, this.posY, this.posZ, soundType.getHitSound(), SoundCategory.NEUTRAL, (soundType.getVolume() + 1.0F) / 4.0F, soundType.getPitch() * 0.5F, false);
		}
	}

	public boolean isActive() {
		return true;
	}

	@Override
	protected boolean isMovementBlocked() {
		return !this.isActive() || this.isAttacking() || super.isMovementBlocked();
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public boolean canResideInBlock(BlockPos pos, EnumFacing facing, EnumFacing facingUp) {
		return this.world.getBlockState(pos).getBlock() == BlockRegistry.LOG_SPIRIT_TREE;
	}

	@Override
	public boolean hitByEntity(Entity entity) {
		if(this.getIsInvulnerable()) {
			return true;
		}
		return super.hitByEntity(entity);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(this.getIsInvulnerable()) {
			return false;
		}
		if(source.getImmediateSource() instanceof IProjectile && source.getTrueSource() != null && source.getTrueSource().getDistance(this) >= WorldGenSpiritTreeStructure.RADIUS_OUTER_CIRCLE + 12) {
			return false;
		}
		EntityLivingBase attacker = source.getImmediateSource() instanceof EntityLivingBase ? (EntityLivingBase)source.getImmediateSource() : null;
		if(attacker != null && attacker.getActiveHand() != null) {
			ItemStack item = attacker.getHeldItem(attacker.getActiveHand());
			if(!item.isEmpty() && item.getItem().getToolClasses(item).contains("axe")) {
				amount *= 2.0F;
			}
		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
		return source == DamageSource.DROWN || source == DamageSource.LAVA || super.isEntityInvulnerable(source);
	}

	@Override
	public boolean getIsInvulnerable() {
		return super.getIsInvulnerable() || !this.isActive();
	}

	@Override
	public void onKillCommand() {
		this.setDead();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		this.prevGlowTicks = this.glowTicks;
		if(this.glowTicks > 0) {
			this.glowTicks--;
		}

		if(!this.world.isRemote) {
			float moveProgress = this.getMovementProgress(1);
			if(moveProgress < 0.6F) {
				this.emergeSound = false;
			} else {
				if(!this.emergeSound) {
					this.world.setEntityState(this, EVENT_EMERGE_SOUND);
					this.playEmergeSound();
				}
				this.emergeSound = true;
			}
		}

		if(this.spitTicks > 0) {
			this.updateSpitAttack();
		}
	}

	protected void updateSpitAttack() {
		if(this.spitTicks == 1) {
			this.world.setEntityState(this, EVENT_SPIT);
			this.setGlowTicks(10);
			this.playSpitSound();
		}

		if(this.spitTicks > 6) {
			this.doSpitAttack();
			this.spitTicks = 0;
		} else {
			this.spitTicks++;
		}
	}

	@Override
	protected boolean isValidBlockForMovement(BlockPos pos, IBlockState state) {
		return state.getBlock() == BlockRegistry.LOG_SPIRIT_TREE;
	}

	public boolean isAttacking() {
		return this.spitTicks > 0;
	}

	public void startSpit(float spitDamage) {
		this.spitTicks = 1;
		this.spitDamage = spitDamage;
	}

	public void doSpitAttack() {
		Entity target = this.getAttackTarget();
		if(target != null) {
			EnumFacing facing = this.getFacing();

			EntitySapSpit spit = new EntitySapSpit(this.world, this, this.spitDamage);
			spit.setPosition(this.posX + facing.getXOffset() * (this.width / 2 + 0.1F), this.posY + this.height / 2.0F + facing.getYOffset() * (this.height / 2 + 0.1F), this.posZ + facing.getZOffset() * (this.width / 2 + 0.1F));

			double dx = target.posX - spit.posX;
			double dy = target.getEntityBoundingBox().minY + (double)(target.height / 3.0F) - spit.posY;
			double dz = target.posZ - spit.posZ;
			double dist = (double)MathHelper.sqrt(dx * dx + dz * dz);
			spit.shoot(dx, dy + dist * 0.20000000298023224D, dz, 1, 1);

			this.world.spawnEntity(spit);
		}
	}

	public static class AITrackTargetSpiritTreeFace extends AITrackTarget<EntitySpiritTreeFace> {
		public AITrackTargetSpiritTreeFace(EntitySpiritTreeFace entity) {
			super(entity);
		}

		public AITrackTargetSpiritTreeFace(EntitySpiritTreeFace entity, boolean stayInRange, double maxRange) {
			super(entity, stayInRange, maxRange);
		}

		@Override
		protected boolean canMove() {
			return this.entity.isActive() && !this.entity.isAttacking();
		}
	}

	public static class AISpit extends EntityAIBase {
		protected final EntitySpiritTreeFace entity;
		protected int minCooldown;
		protected int maxCooldown;

		protected int cooldown = 0;

		protected float spitDamage;

		public AISpit(EntitySpiritTreeFace entity, float spitDamage) {
			this(entity, spitDamage, 50, 170);
		}

		public AISpit(EntitySpiritTreeFace entity, float spitDamage, int minCooldown, int maxCooldown) {
			this.entity = entity;
			this.minCooldown = minCooldown;
			this.maxCooldown = maxCooldown;
			this.spitDamage = spitDamage;
			this.setMutexBits(0);
		}
		
	    public boolean isWearingTreeMask(EntityLivingBase entityIn) {
	    	if(entityIn instanceof EntityPlayer && this.entity instanceof EntitySpiritTreeFaceSmall) {
	        	ItemStack helmet = ((EntityPlayer)entityIn).getItemStackFromSlot(EntityEquipmentSlot.HEAD);
	        	if(!helmet.isEmpty() && (helmet.getItem() == ItemRegistry.SPIRIT_TREE_FACE_SMALL_MASK || helmet.getItem() == ItemRegistry.SPIRIT_TREE_FACE_LARGE_MASK)) {
	        		return true;
	        	}
	        }
	    	return false;
	    }

		@Override
		public boolean shouldExecute() {
			return this.entity.isActive() && !this.entity.isAttacking() && !this.entity.isMoving() && this.entity.getAttackTarget() != null && this.entity.getAttackTarget().isEntityAlive() && this.entity.getEntitySenses().canSee(this.entity.getAttackTarget()) && !isWearingTreeMask(this.entity.getAttackTarget());
		}

		@Override
		public void startExecuting() {
			this.cooldown = 20 + this.entity.rand.nextInt(40);
		}

		@Override
		public void updateTask() {
			if(!this.entity.isAttacking()) {
				if(this.cooldown <= 0) {
					this.cooldown = this.minCooldown + this.entity.rand.nextInt(this.maxCooldown - this.minCooldown + 1);
					this.entity.startSpit(this.getSpitDamage());
				}
				this.cooldown--;
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			return this.shouldExecute();
		}

		protected float getSpitDamage() {
			return this.spitDamage;
		}
	}
}
