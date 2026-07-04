package thebetweenlands.common.entity.monster.spirit_tree;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.ItemRegistry;

public class SmallTamedSpiritTreeFace extends AbstractSmallSpritTreeFace {

	public SmallTamedSpiritTreeFace(EntityType<? extends AbstractSmallSpritTreeFace> type, Level level) {
		super(type, level);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new SpiritTreeTrackTargetGoal(this, true, 16.0D));
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, true));
		this.goalSelector.addGoal(2, new SpitGoal(this, 5.0F, 30, 70) {
			@Override
			protected float getSpitDamage() {
				return (float) SmallTamedSpiritTreeFace.this.getAttributeValue(Attributes.ATTACK_DAMAGE);
			}
		});
		this.goalSelector.addGoal(3, new SpiritTreeFaceWanderGoal(this, 8, 0.33D, 200));
		this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F) {
			@Override
			public void tick() {
				SmallTamedSpiritTreeFace.this.getLookControl().setSpeed(0.33D);
				super.tick();
			}
		});
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this) {
			@Override
			public void tick() {
				SmallTamedSpiritTreeFace.this.getLookControl().setSpeed(0.33D);
				super.tick();
			}
		});

		this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Mob.class, 10, false, false, e -> e instanceof Enemy && !(e instanceof AbstractSpiritTreeFace)));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 50.0D)
			.add(Attributes.MOVEMENT_SPEED, 2.0D)
			.add(Attributes.ATTACK_DAMAGE, 5.0D);
	}

	@Override
	protected void fixUnsuitablePosition(int violatedChecks) {
		if (this.isAnchored() && (violatedChecks & AnchorChecks.BLOCKS) != 0) {
			this.setAnchored(false);
		}
		super.fixUnsuitablePosition(violatedChecks);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.getEntity() instanceof Player) {
			return super.hurt(source, amount);
		}
		return false;
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (stack.is(ItemRegistry.COMPOST)) {
			if (this.getHealth() < this.getMaxHealth()) {
				if (!this.level().isClientSide()) {
					this.heal(4);
				} else {
					for (int i = 0; i < 7; ++i) {
						double d0 = this.getRandom().nextGaussian() * 0.02D;
						double d1 = this.getRandom().nextGaussian() * 0.02D;
						double d2 = this.getRandom().nextGaussian() * 0.02D;
						this.level().addParticle(ParticleTypes.HEART, this.getX() + (double) (this.getRandom().nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY() + 0.5D + (double) (this.getRandom().nextFloat() * this.getBbHeight()), this.getZ() + (double) (this.getRandom().nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), d0, d1, d2);
					}
				}
			}

			if (!this.level().isClientSide()) {
				if (this.getRandom().nextBoolean()) {
					//TODO loot table
					this.spawnAtLocation(new ItemStack(ItemRegistry.SAP_SPIT.get(), 1 + this.getRandom().nextInt(3)), this.getBbHeight() / 2);

					this.playSpitSound();
				}

				stack.shrink(1);
			} else {
				for (int i = 0; i < 4; ++i) {
					double d0 = this.getRandom().nextGaussian() * 0.02D;
					double d1 = this.getRandom().nextGaussian() * 0.02D;
					double d2 = this.getRandom().nextGaussian() * 0.02D;
					this.level().addParticle(ParticleTypes.SMOKE, this.getX() + (double) (this.getRandom().nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY() + 0.5D + (double) (this.getRandom().nextFloat() * this.getBbHeight()), this.getZ() + (double) (this.getRandom().nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), d0, d1, d2);
				}

			}

			return InteractionResult.sidedSuccess(this.level().isClientSide());
		}

		return InteractionResult.PASS;
	}

	public static class SpiritTreeFaceWanderGoal extends WanderGoal<SmallTamedSpiritTreeFace> {

		public SpiritTreeFaceWanderGoal(SmallTamedSpiritTreeFace entity, double range, double speed, int chance) {
			super(entity, range, speed, chance);
		}

		@Override
		protected boolean canMove() {
			return this.entity.isActive() && !this.entity.isAttacking();
		}
	}
}
