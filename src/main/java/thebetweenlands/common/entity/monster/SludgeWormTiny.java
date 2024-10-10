package thebetweenlands.common.entity.monster;

import javax.annotation.Nullable;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class SludgeWormTiny extends SludgeWorm {
	public static final byte EVENT_SQUASHED = 80;
	public static final byte EVENT_LEAP = 81;

	protected boolean isSquashed = false;
	
	public SludgeWormTiny(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.parts = new SludgeWormMultipart[] {
				new SludgeWormMultipart(this, "body_part_1", 0.1875F, 0.1875F),
				new SludgeWormMultipart(this, "body_part_2", 0.1875F, 0.1875F),
				new SludgeWormMultipart(this, "body_part_3", 0.1875F, 0.1875F),
				new SludgeWormMultipart(this, "body_part_4", 0.1875F, 0.1875F),
				new SludgeWormMultipart(this, "body_part_5", 0.1875F, 0.1875F)
				
		};
		xpReward = 1;
		setId(ENTITY_COUNTER.getAndAdd(this.parts.length + 1) + 1);
	}
	
	
/*	
	public SludgeWormTiny(EntityType<? extends PathfinderMob> type, Level level, boolean doSpawningAnimation) {
		this(type, level);
	}
*/	

    @Override
    public void setId(int id) {
        super.setId(id);
        for (int i = 0; i < this.parts.length; i++)
            this.parts[i].setId(id + i + 1);
    }

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new LeapAtTargetGoal(this, 0.3F) {
			@Override
			public void start() {
				super.start();
				SludgeWormTiny.this.level().broadcastEntityEvent(SludgeWormTiny.this, EVENT_LEAP);
			}
		});
		goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
		goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8D, 1));
		targetSelector.addGoal(0, new HurtByTargetGoal(this));
		targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, null));
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
		if (hasLineOfSight(entity) && entity.onGround())
			if (super.doHurtTarget(entity))
				return true;
		return false;
	}

	@Override
	public void playerTouch(Player player) {
		if (!this.level().isClientSide()) {
			for (SludgeWormMultipart part : this.parts) {
				if (player.getBoundingBox().maxY >= part.getBoundingBox().minY
						&& player.getBoundingBox().minY <= part.getBoundingBox().maxY
						&& player.getBoundingBox().maxX >= part.getBoundingBox().minX
						&& player.getBoundingBox().minX <= part.getBoundingBox().maxX
						&& player.getBoundingBox().maxZ >= part.getBoundingBox().minZ
						&& player.getBoundingBox().minZ <= part.getBoundingBox().maxZ
						&& player.yOld > player.yo) {
					player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 0));
					
					if (level().getDifficulty() == Difficulty.NORMAL) {
						player.addEffect(ElixirEffectRegistry.EFFECT_DECAY.get().createEffect(80, 1));
					} else if (level().getDifficulty() == Difficulty.HARD) {
						player.addEffect(ElixirEffectRegistry.EFFECT_DECAY.get().createEffect(160, 1));
					}
					
					this.isSquashed = true;
				}
			}
			
			if (this.isSquashed) {
				level().broadcastEntityEvent(this, EVENT_SQUASHED);
				
				level().playSound(null, this.xo, this.yo, this.zo, getJumpedOnSound(), SoundSource.NEUTRAL, 1.0F, 0.5F);
				level().playSound(null, this.xo, this.yo, this.zo, getDeathSound(), SoundSource.NEUTRAL, 1.0F, 0.5F);
				
				this.damageWorm(damageSources().playerAttack(player), this.getHealth());
			}
		}
	}

	public boolean isSquashed() {
		return this.isSquashed;
	}
	
    @Override
    protected void tickDeath() {
		if (this.isSquashed) {
			this.deathTime = 19;
		}
		
		super.tickDeath();
	}
	
    @Override
    public void handleEntityEvent(byte id) {	
		if(id == EVENT_SQUASHED) {
			for(int i = 0; i < 100; i++) {
				RandomSource rnd = level().random;
				float rx = rnd.nextFloat() * 1.0F - 0.5F;
				float ry = rnd.nextFloat() * 1.0F - 0.5F;
				float rz = rnd.nextFloat() * 1.0F - 0.5F;
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

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		for (int i = 0; i < this.parts.length; i++) {
			this.parts[i].setPos(xo, yo, zo);
			this.parts[i].setYRot(getYRot());
		}
		return spawnGroupData;
	}
}
