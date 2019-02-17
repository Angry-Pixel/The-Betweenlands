package thebetweenlands.common.entity.mobs;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.common.registries.EntityRegistry;

public class EntityTamedSpiritTreeFace extends EntitySpiritTreeFaceSmall {
	public EntityTamedSpiritTreeFace(World world) {
		super(EntityRegistry.TAMED_SPIRIT_TREE_FACE, world);
	}

	@Override
	protected void initEntityAI() {
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityLivingBase>(this, EntityLivingBase.class, 10, false, false, e -> IMob.VISIBLE_MOB_SELECTOR.apply(e) && e instanceof EntityTamedSpiritTreeFace == false));

		this.tasks.addTask(0, new AITrackTarget(this, true, 16.0D));
		this.tasks.addTask(1, new AIAttackMelee(this, 1, true));
		this.tasks.addTask(2, new AISpit(this, 5.0F, 30, 70) {
			@Override
			protected float getSpitDamage() {
				return (float) EntityTamedSpiritTreeFace.this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
			}
		});
		this.tasks.addTask(3, new AIWander(this, 8, 0.33D, 200));
		this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F) {
			@Override
			public void updateTask() {
				EntityTamedSpiritTreeFace.this.getLookHelper().setSpeed(0.33D);
				super.updateTask();
			}
		});
		this.tasks.addTask(5, new EntityAILookIdle(this) {
			@Override
			public void updateTask() {
				EntityTamedSpiritTreeFace.this.getLookHelper().setSpeed(0.33D);
				super.updateTask();
			}
		});
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50);
		this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	protected void fixUnsuitablePosition(int violatedChecks) {
		if((violatedChecks & AnchorChecks.ANCHOR_BLOCKS) != 0 || (violatedChecks & AnchorChecks.FACE_BLOCKS) != 0) {
			this.onDeath(DamageSource.OUT_OF_WORLD);
			this.remove();
		} else {
			super.fixUnsuitablePosition(violatedChecks);
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(source.getImmediateSource() instanceof EntityPlayer) {
			return super.attackEntityFrom(source, amount);
		}
		return false;
	}

	public static class AIWander extends EntityAIBase {
		protected final EntitySpiritTreeFace entity;

		protected int findWoodCooldown = 0;
		protected List<BlockPos> woodBlocks;

		protected int checkCooldown = 0;

		protected double speed;
		protected int chance;

		protected double range;
		protected double rangeSq;

		protected boolean wandered = false;

		public AIWander(EntitySpiritTreeFace entity, double range, double speed) {
			this(entity, range, speed, 120);
		}

		public AIWander(EntitySpiritTreeFace entity, double range, double speed, int chance) {
			this.entity = entity;
			this.range = range;
			this.rangeSq = range * range;
			this.speed = speed;
			this.chance = chance;
			this.setMutexBits(1);
		}

		@Override
		public boolean shouldExecute() {
			return this.entity.isActive() && !this.entity.isAttacking() && !this.entity.isMoving() && this.entity.getIdleTime() < 100 && this.entity.getRNG().nextInt(this.chance) == 0;
		}

		@Override
		public void startExecuting() {
			this.checkCooldown = 0;
			this.findWoodCooldown = 20 + this.entity.getRNG().nextInt(30);
			this.woodBlocks = null;
			this.wandered = false;
		}

		@Override
		public void updateTask() {
			if(this.findWoodCooldown <= 0 && (this.woodBlocks == null || this.woodBlocks.isEmpty())) {
				this.findWoodCooldown = 40 + this.entity.getRNG().nextInt(60);
				this.woodBlocks = this.entity.findNearbyWoodBlocks();
			}

			if(this.woodBlocks != null && !this.woodBlocks.isEmpty() && this.checkCooldown <= 0) {
				this.checkCooldown = 5 + this.entity.getRNG().nextInt(15);

				for(int i = 0; i < 16; i++) {
					if(this.woodBlocks.isEmpty()) {
						break;
					}

					BlockPos pos = this.woodBlocks.remove(this.entity.getRNG().nextInt(this.woodBlocks.size()));

					if(this.entity.getDistanceSqToCenter(pos) <= this.rangeSq && this.entity.isWithinHomeDistanceFromPosition(pos)) {
						Vec3d center = new Vec3d(pos.getX() + this.entity.getBlockWidth() / 2.0D, pos.getY() + this.entity.getBlockHeight() / 2.0D, pos.getZ() + this.entity.getBlockWidth() / 2.0D);
						double dx = this.entity.getRNG().nextDouble() - 0.5D;
						double dy = this.entity.getRNG().nextDouble() - 0.5D;
						double dz = this.entity.getRNG().nextDouble() - 0.5D;
						if(this.entity.checkAnchorAt(center, new Vec3d(dx, dy, dz), AnchorChecks.ALL) == 0) {
							this.entity.getMoveHelper().setMoveTo(center.x, center.y, center.z, this.speed);
							this.entity.getLookHelper().setLookDirection(dx, dy, dz);
							this.wandered = true;
							return;
						}
					}
				}
			}

			this.checkCooldown--;
			this.findWoodCooldown--;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return !this.wandered && this.entity.isActive() && !this.entity.isMoving() && !this.entity.isAttacking();
		}
	}
}
