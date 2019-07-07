package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.common.entity.projectiles.EntitySapSpit;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.feature.tree.WorldGenSpiritTreeStructure;

public abstract class EntitySpiritTreeFace extends EntityWallFace implements IMob {
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
		return this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
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
	public boolean canMoveFaceInto(BlockPos pos, EnumFacing facing, EnumFacing facingUp) {
		IBlockState state = this.world.getBlockState(pos);
		if(state.getMaterial().isLiquid() || state.getBlock().isAir(state, this.world, pos)) {
			return true;
		}
		if(state.getBlock().isLeaves(state, this.world, pos)) {
			return false;
		}
		List<AxisAlignedBB> collisionBoxes = new ArrayList<>();
		state.addCollisionBoxToList(this.world, pos, new AxisAlignedBB(pos), collisionBoxes, this, false);
		return collisionBoxes.isEmpty();
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
			if(!item.isEmpty() && item.getItem() instanceof ItemAxe) {
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

		if(this.isMoving() && this.world.isRemote) {
			if(this.ticksExisted % 3 == 0) {
				EnumFacing facing = this.getFacing();
				double px = this.posX + facing.getXOffset() * this.width / 2;
				double py = this.posY + this.height / 2 + facing.getYOffset() * this.height / 2;
				double pz = this.posZ + facing.getZOffset() * this.width / 2;
				for(int i = 0; i < 24; i++) {
					double rx = (this.world.rand.nextDouble() - 0.5D) * this.width;
					double ry = (this.world.rand.nextDouble() - 0.5D) * this.height;
					double rz = (this.world.rand.nextDouble() - 0.5D) * this.width;
					BlockPos pos = new BlockPos(px + rx, py + ry, pz + rz);
					IBlockState state = this.world.getBlockState(pos);
					if(!state.getBlock().isAir(state, this.world, pos)) {
						double mx = facing.getXOffset() * 0.15F + (this.world.rand.nextDouble() - 0.5D) * 0.25F;
						double my = facing.getYOffset() * 0.15F + (this.world.rand.nextDouble() - 0.5D) * 0.25F;
						double mz = facing.getZOffset() * 0.15F + (this.world.rand.nextDouble() - 0.5D) * 0.25F;
						this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, px + rx, py + ry, pz + rz, mx, my, mz, Block.getStateId(state));
					}
				}
			}
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
	protected void fixUnsuitablePosition(int violatedChecks) {
		if(this.ticksExisted % 5 == 0) {
			for(int i = 0; i < 50; i++) {
				float rx = this.world.rand.nextFloat() * 2 - 1;
				float ry = this.world.rand.nextFloat() * 2 - 1;
				float rz = this.world.rand.nextFloat() * 2 - 1;
				BlockPos rndPos = new BlockPos(this.posX + this.world.rand.nextInt(8) - 4, this.posY + this.height / 2 + this.world.rand.nextInt(8) - 4, this.posZ + this.world.rand.nextInt(8) - 4);
				Vec3d pos = new Vec3d(rndPos.getX() + 0.5D, rndPos.getY() + 0.5D, rndPos.getZ() + 0.5D);
				if(this.checkAnchorAt(pos, new Vec3d(rx, ry, rz), AnchorChecks.ALL) == 0) {
					this.lookHelper.setLookDirection(rx, ry, rz);
					this.moveHelper.setMoveTo(pos.x, pos.y, pos.z, 1);
					break;
				}
			}
		}
	}

	public List<BlockPos> findNearbyWoodBlocks() {
		final int radius = 8;
		BlockPos center = new BlockPos(this);
		List<BlockPos> blocks = new ArrayList<>();
		MutableBlockPos pos = new MutableBlockPos();
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				for (int dz = -radius; dz <= radius; dz++) {
					pos.setPos(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
					IBlockState state = this.world.getBlockState(pos);
					if (state.getBlock() == BlockRegistry.LOG_SPIRIT_TREE) {
						blocks.add(pos.toImmutable());
					}
				}
			}
		}
		return blocks;
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

	public static class AITrackTarget extends EntityAIBase {
		protected final EntitySpiritTreeFace entity;

		protected int findWoodCooldown = 0;
		protected List<BlockPos> woodBlocks;

		protected int checkCooldown = 0;

		protected boolean stayInRange;
		protected double maxRangeSq;

		public AITrackTarget(EntitySpiritTreeFace entity) {
			this(entity, false, 0);
		}

		public AITrackTarget(EntitySpiritTreeFace entity, boolean stayInRange, double maxRange) {
			this.entity = entity;
			this.stayInRange = stayInRange;
			this.maxRangeSq = maxRange * maxRange;
			this.setMutexBits(3);
		}

		protected boolean isTargetVisibleAndInRange() {
			return this.entity.getEntitySenses().canSee(this.entity.getAttackTarget()) && (!this.stayInRange || this.entity.getAttackTarget().getDistanceSq(this.entity) <= this.maxRangeSq);
		}

		@Override
		public boolean shouldExecute() {
			return this.entity.isActive() && !this.entity.isAttacking() && !this.entity.isMoving() && this.entity.getAttackTarget() != null && this.entity.getAttackTarget().isEntityAlive() && !this.isTargetVisibleAndInRange();
		}

		@Override
		public void startExecuting() {
			this.checkCooldown = 0;
			this.findWoodCooldown = 20 + this.entity.rand.nextInt(30);
			this.woodBlocks = null;
		}

		@Override
		public void updateTask() {
			EntityLivingBase target = this.entity.getAttackTarget();
			
			if(target != null && !this.entity.isAttacking()) {
				if(this.findWoodCooldown <= 0 && (this.woodBlocks == null || this.woodBlocks.isEmpty())) {
					this.findWoodCooldown = 20 + this.entity.rand.nextInt(40);
					this.woodBlocks = this.entity.findNearbyWoodBlocks();
				}

				if(this.woodBlocks != null && !this.woodBlocks.isEmpty() && this.checkCooldown <= 0) {
					this.checkCooldown = 5 + this.entity.rand.nextInt(15);

					for(int i = 0; i < 16; i++) {
						if(this.woodBlocks.isEmpty()) {
							break;
						}

						BlockPos pos = this.woodBlocks.remove(this.entity.rand.nextInt(this.woodBlocks.size()));

						if(!this.stayInRange || target.getDistanceSqToCenter(pos) <= this.maxRangeSq) {
							Vec3d center = new Vec3d(pos.getX() + this.entity.getBlockWidth() / 2.0D, pos.getY() + this.entity.getBlockHeight() / 2.0D, pos.getZ() + this.entity.getBlockWidth() / 2.0D);
							Vec3d lookDir = target.getPositionVector().add(0, target.getEyeHeight(), 0).subtract(center);

							EnumFacing facing = EnumFacing.getFacingFromVector((float)lookDir.x, (float)lookDir.y, (float)lookDir.z);

							if(this.canSeeFrom(pos, facing, target) && this.entity.checkAnchorAt(center, lookDir, AnchorChecks.ALL) == 0) {
								this.entity.moveHelper.setMoveTo(center.x, center.y, center.z, 1);
								this.entity.lookHelper.setLookDirection(facing.getXOffset(), facing.getYOffset(), facing.getZOffset());
								break;
							} else {
								for(EnumFacing otherFacing : EnumFacing.HORIZONTALS) {
									if(otherFacing != facing) {
										lookDir = new Vec3d(otherFacing.getXOffset(), 0, otherFacing.getZOffset());

										if(this.canSeeFrom(pos, otherFacing, target) && this.entity.checkAnchorAt(center, lookDir, AnchorChecks.ALL) == 0) {
											this.entity.moveHelper.setMoveTo(center.x, center.y, center.z, 1);
											this.entity.lookHelper.setLookDirection(otherFacing.getXOffset(), otherFacing.getYOffset(), otherFacing.getZOffset());
											break;
										} 
									}
								}
							}
						}
					}
				}

				this.checkCooldown--;
				this.findWoodCooldown--;
			}
		}

		protected boolean canSeeFrom(BlockPos pos, EnumFacing facing, Entity entity) {
			return this.entity.world.rayTraceBlocks(new Vec3d(pos.getX() + this.entity.getBlockWidth() / 2.0D + facing.getXOffset() * (this.entity.width / 2 + this.entity.getPeek()), pos.getY() + this.entity.getBlockHeight() / 2.0D + facing.getYOffset() * (this.entity.height / 2 + this.entity.getPeek()), pos.getZ() + this.entity.getBlockWidth() / 2.0D + facing.getZOffset() * (this.entity.width / 2 + this.entity.getPeek())), new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ), false, true, false) == null;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return !this.entity.isMoving() && this.entity.getAttackTarget() != null && this.entity.getAttackTarget().isEntityAlive() && !this.isTargetVisibleAndInRange();
		}
	}

	public static class AIAttackMelee extends EntityAIAttackMelee {
		public AIAttackMelee(EntityCreature creature, double speedIn, boolean useLongMemory) {
			super(creature, speedIn, useLongMemory);
		}

		@Override
		protected double getAttackReachSqr(EntityLivingBase attackTarget) {
			return this.attacker.width * this.attacker.width;
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

		@Override
		public boolean shouldExecute() {
			return this.entity.isActive() && !this.entity.isAttacking() && !this.entity.isMoving() && this.entity.getAttackTarget() != null && this.entity.getAttackTarget().isEntityAlive() && this.entity.getEntitySenses().canSee(this.entity.getAttackTarget());
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
