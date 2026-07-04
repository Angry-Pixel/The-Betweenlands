package thebetweenlands.common.entity.monster.spirit_tree;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.entity.monster.wall.AbstractMovingWallCreature;
import thebetweenlands.common.entity.projectile.SapSpit;
import thebetweenlands.common.item.armor.SpiritTreeMaskItem;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.structure.SpiritTreePiece;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class AbstractSpiritTreeFace extends AbstractMovingWallCreature {

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

	public AbstractSpiritTreeFace(EntityType<? extends AbstractSpiritTreeFace> type, Level level) {
		super(type, level);
		this.xpReward = 4;
	}

	public void setGlowTicks(int duration) {
		duration = Math.max(duration, 1);
		this.glowTicks = duration;
		this.glowDuration = duration;
	}

	public float getGlow(float partialTicks) {
		return Mth.lerp(partialTicks, this.prevGlowTicks, this.glowTicks);
	}

	protected void playSpitSound() {

	}

	protected void playEmergeSound() {

	}

	@Override
	public SoundSource getSoundSource() {
		return SoundSource.HOSTILE;
	}

	@Override
	public boolean isSilent() {
		return super.isSilent() || !this.isActive();
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return null;
	}

	@Override
	protected void playHurtSound(DamageSource source) {
		this.level().broadcastEntityEvent(this, EVENT_HURT_SOUND);
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);

		if (id == EVENT_SPIT) {
			if (this.glowTicks < 10) {
				this.setGlowTicks(10);
			}
		} else if (id == EVENT_ATTACKED) {
			if (this.glowTicks < 10) {
				this.setGlowTicks(10);
			}
		} else if (id == EVENT_HURT_SOUND || id == EVENT_DEATH) {
			SoundType soundType = SoundType.WOOD;
			this.level().playSound(null, this.blockPosition(), soundType.getBreakSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 1.3F, soundType.getPitch() * 0.8F);
			this.level().playSound(null, this.blockPosition(), soundType.getHitSound(), SoundSource.NEUTRAL, (soundType.getVolume() + 1.0F) / 4.0F, soundType.getPitch() * 0.5F);
		}
	}

	public boolean isActive() {
		return true;
	}

	@Override
	protected boolean isImmobile() {
		return !this.isActive() || this.isAttacking() || super.isImmobile();
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public boolean canResideInBlock(BlockPos pos, Direction facing, Direction facingUp) {
		return this.level().getBlockState(pos).is(BlockRegistry.SPIRIT_TREE_LOG) || this.level().getBlockState(pos).is(BlockRegistry.SPIRIT_TREE_BARK);
	}

	@Override
	public boolean skipAttackInteraction(Entity entity) {
		if (this.isInvulnerable()) {
			return true;
		}
		return super.skipAttackInteraction(entity);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (this.isInvulnerable()) {
			return false;
		}
		if (source.getDirectEntity() instanceof Projectile && source.getEntity() != null && source.getEntity().distanceTo(this) >= SpiritTreePiece.RADIUS_OUTER_CIRCLE + 12) {
			return false;
		}
		if (source.getWeaponItem() != null && source.getWeaponItem().is(ItemTags.AXES)) {
			amount *= 2.0F;
		}
		return super.hurt(source, amount);
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		return source.is(DamageTypes.DROWN) || source.is(DamageTypes.LAVA) || super.isInvulnerableTo(source);
	}

	@Override
	public boolean isInvulnerable() {
		return super.isInvulnerable() || !this.isActive();
	}

	@Override
	public void kill() {
		this.discard();
	}

	@Override
	public void aiStep() {
		super.aiStep();

		this.prevGlowTicks = this.glowTicks;
		if (this.glowTicks > 0) {
			this.glowTicks--;
		}

		if (!this.level().isClientSide()) {
			float moveProgress = this.getMovementProgress(1);
			if (moveProgress < 0.6F) {
				this.emergeSound = false;
			} else {
				if (!this.emergeSound) {
					this.level().broadcastEntityEvent(this, EVENT_EMERGE_SOUND);
					this.playEmergeSound();
				}
				this.emergeSound = true;
			}
		}

		if (this.spitTicks > 0) {
			this.updateSpitAttack();
		}
	}

	protected void updateSpitAttack() {
		if (this.spitTicks == 1) {
			this.level().broadcastEntityEvent(this, EVENT_SPIT);
			this.setGlowTicks(10);
			this.playSpitSound();
		}

		if (this.spitTicks > 6) {
			this.doSpitAttack();
			this.spitTicks = 0;
		} else {
			this.spitTicks++;
		}
	}

	@Override
	protected boolean isValidBlockForMovement(BlockPos pos, BlockState state) {
		return this.level().getBlockState(pos).is(BlockRegistry.SPIRIT_TREE_LOG) || this.level().getBlockState(pos).is(BlockRegistry.SPIRIT_TREE_BARK);
	}

	public boolean isAttacking() {
		return this.spitTicks > 0;
	}

	public void startSpit(float spitDamage) {
		this.spitTicks = 1;
		this.spitDamage = spitDamage;
	}

	public void doSpitAttack() {
		Entity target = this.getTarget();
		if (target != null) {
			Direction facing = this.getFacing();

			SapSpit spit = new SapSpit(this.level(), this, this.spitDamage);
			spit.moveTo(this.getX() + facing.getStepX() * (this.getBbWidth() / 2 + 0.1F), this.getY() + this.getBbHeight() / 2.0F + facing.getStepY() * (this.getBbHeight() / 2 + 0.1F), this.getZ() + facing.getStepZ() * (this.getBbWidth() / 2 + 0.1F));

			double dx = target.getX() - spit.getX();
			double dy = target.getBoundingBox().minY + (double) (target.getBbHeight() / 3.0F) - spit.getY();
			double dz = target.getZ() - spit.getZ();
			double dist = Mth.sqrt((float) (dx * dx + dz * dz));
			spit.shoot(dx, dy + dist * 0.20000000298023224D, dz, 1, 1);

			this.level().addFreshEntity(spit);
		}
	}

	public static class SpiritTreeTrackTargetGoal extends TrackTargetGoal<AbstractSpiritTreeFace> {
		public SpiritTreeTrackTargetGoal(AbstractSpiritTreeFace entity) {
			super(entity);
		}

		public SpiritTreeTrackTargetGoal(AbstractSpiritTreeFace entity, boolean stayInRange, double maxRange) {
			super(entity, stayInRange, maxRange);
		}

		@Override
		protected boolean canMove() {
			return this.entity.isActive() && !this.entity.isAttacking();
		}
	}

	public static class SpitGoal extends Goal {
		protected final AbstractSpiritTreeFace entity;
		protected int minCooldown;
		protected int maxCooldown;

		protected int cooldown = 0;

		protected float spitDamage;

		public SpitGoal(AbstractSpiritTreeFace entity, float spitDamage) {
			this(entity, spitDamage, 50, 170);
		}

		public SpitGoal(AbstractSpiritTreeFace entity, float spitDamage, int minCooldown, int maxCooldown) {
			this.entity = entity;
			this.minCooldown = minCooldown;
			this.maxCooldown = maxCooldown;
			this.spitDamage = spitDamage;
		}

		public boolean isWearingTreeMask(LivingEntity entity) {
			if (entity instanceof Player && this.entity instanceof SmallSpiritTreeFace) {
				ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
				return helmet.getItem() instanceof SpiritTreeMaskItem;
			}
			return false;
		}

		@Override
		public boolean canUse() {
			return this.entity.isActive() && !this.entity.isAttacking() && !this.entity.isMoving() && this.entity.getTarget() != null && this.entity.getTarget().isAlive() && this.entity.hasLineOfSight(this.entity.getTarget()) && !this.isWearingTreeMask(this.entity.getTarget());
		}

		@Override
		public void start() {
			this.cooldown = 20 + this.entity.getRandom().nextInt(40);
		}

		@Override
		public void tick() {
			if (!this.entity.isAttacking()) {
				if (this.cooldown <= 0) {
					this.cooldown = this.minCooldown + this.entity.getRandom().nextInt(this.maxCooldown - this.minCooldown + 1);
					this.entity.startSpit(this.getSpitDamage());
				}
				this.cooldown--;
			}
		}

		@Override
		public boolean canContinueToUse() {
			return this.canUse();
		}

		protected float getSpitDamage() {
			return this.spitDamage;
		}
	}
}
