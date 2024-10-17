package thebetweenlands.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class TinySludgeWorm extends SludgeWorm {
	public static final byte EVENT_SQUASHED = 80;
	public static final byte EVENT_LEAP = 81;

	protected boolean isSquashed = false;

	public TinySludgeWorm(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.parts = new SludgeWormMultipart[] {
				new SludgeWormMultipart(this, 0.1875F, 0.1875F),
				new SludgeWormMultipart(this, 0.1875F, 0.1875F),
				new SludgeWormMultipart(this, 0.1875F, 0.1875F),
				new SludgeWormMultipart(this, 0.1875F, 0.1875F),
				new SludgeWormMultipart(this, 0.1875F, 0.1875F)
		};
		this.xpReward = 1;
		setId(ENTITY_COUNTER.getAndAdd(this.parts.length + 1) + 1);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new LeapAtTargetGoal(this, 0.3F) {
			@Override
			public void start() {
				super.start();
				TinySludgeWorm.this.level().broadcastEntityEvent(TinySludgeWorm.this, EVENT_LEAP);
			}
		});
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8D, 1));
		this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, entity -> !(entity instanceof Enemy)));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Animal.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 4.0D)
				.add(Attributes.FOLLOW_RANGE, 20.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.21D)
				.add(Attributes.ATTACK_DAMAGE, 0.5D);
	}

	@Override
	protected double getMaxPieceDistance() {
		return 0.2D;
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		if (this.hasLineOfSight(entity) && entity.onGround())
			return super.doHurtTarget(entity);
		return false;
	}

	@Override
	public void playerTouch(Player player) {
		if (!this.level().isClientSide()) {
			// head
			if (this.isPartJumpedOn(player, this.getBoundingBox()))
				this.squashWorm(player);
			// rest of worm
			else
				for (SludgeWormMultipart part : this.parts) {
					if (this.isPartJumpedOn(player, part.getBoundingBox())) {
						this.squashWorm(player);
					}
				}
		}
	}

	public boolean isPartJumpedOn(Player player, AABB hitBox) {
		return player.getBoundingBox().maxY >= hitBox.minY && player.getBoundingBox().minY <= hitBox.maxY
				&& player.getBoundingBox().maxX >= hitBox.minX && player.getBoundingBox().minX <= hitBox.maxX
				&& player.getBoundingBox().maxZ >= hitBox.minZ && player.getBoundingBox().minZ <= hitBox.maxZ
				&& !player.onGround() && player.getDeltaMovement().y() < 0;
	}

	public void squashWorm(Player player) {
		player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 0));

		if (this.level().getDifficulty() == Difficulty.NORMAL)
			player.addEffect(ElixirEffectRegistry.EFFECT_DECAY.get().createEffect(80, 1));
		else if (this.level().getDifficulty() == Difficulty.HARD)
			player.addEffect(ElixirEffectRegistry.EFFECT_DECAY.get().createEffect(160, 1));

		this.isSquashed = true;
		this.level().broadcastEntityEvent(this, EVENT_SQUASHED);
		this.level().playSound(null, this.xo, this.yo, this.zo, this.getJumpedOnSound(), SoundSource.NEUTRAL, 1.0F, 0.5F);
		this.level().playSound(null, this.xo, this.yo, this.zo, this.getDeathSound(), SoundSource.NEUTRAL, 1.0F, 0.5F);
		this.hurt(this.damageSources().playerAttack(player), this.getHealth());

	}

	public boolean isSquashed() {
		return this.isSquashed;
	}

    @Override
    protected void tickDeath() {
		if (this.isSquashed())
			this.deathTime = 19;
		super.tickDeath();
	}

    @Override
    public void handleEntityEvent(byte id) {
		if(id == EVENT_SQUASHED) {
			for(int i = 0; i < 100; i++) {
				RandomSource rnd = this.level().getRandom();
				float rx = rnd.nextFloat() - 0.5F;
				float ry = rnd.nextFloat() - 0.5F;
				float rz = rnd.nextFloat() - 0.5F;
				Vec3 vec = new Vec3(rx, ry, rz);
				vec = vec.normalize();
			//	BLParticles.SPLASH_TAR.spawn(level(), this.xo + rx + 0.1F, this.yo + ry + 0.1F, this.zo + rz + 0.1F, ParticleArgs.get().withMotion(vec.x * 0.4F, vec.y * 0.4F, vec.z * 0.4F)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
			}
		} else if(id == EVENT_LEAP) {
			double motionY;
			for(Entity part : this.getParts()) {
				motionY = part.getDeltaMovement().y();
				motionY += 0.3F;
				part.setDeltaMovement(part.getDeltaMovement().add(0D, motionY, 0D));
			}
		}
	}

	protected SoundEvent getJumpedOnSound() {
		return SoundRegistry.WORM_SPLAT.get();
	}

	@Override
    public float getVoicePitch() {
		return super.getVoicePitch() * 1.5F;
	}
}
