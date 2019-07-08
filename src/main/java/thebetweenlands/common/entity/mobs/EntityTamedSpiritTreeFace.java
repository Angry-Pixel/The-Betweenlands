package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityTamedSpiritTreeFace extends EntitySpiritTreeFaceSmall {
	public EntityTamedSpiritTreeFace(World world) {
		super(world);
	}

	@Override
	protected void initEntityAI() {
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityLivingBase>(this, EntityLivingBase.class, 10, false, false, e -> IMob.VISIBLE_MOB_SELECTOR.apply(e) && e instanceof EntityTamedSpiritTreeFace == false));

		this.tasks.addTask(0, new AITrackTargetSpiritTreeFace(this, true, 16.0D));
		this.tasks.addTask(1, new AIAttackMelee(this, 1, true));
		this.tasks.addTask(2, new AISpit(this, 5.0F, 30, 70) {
			@Override
			protected float getSpitDamage() {
				return (float) EntityTamedSpiritTreeFace.this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
			}
		});
		this.tasks.addTask(3, new AIWanterTamedSpiritTreeFace(this, 8, 0.33D, 200));
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
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	protected void fixUnsuitablePosition(int violatedChecks) {
		if(this.isAnchored() && (violatedChecks & AnchorChecks.BLOCKS) != 0) {
			this.setAnchored(false);
		}
		super.fixUnsuitablePosition(violatedChecks);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(source.getImmediateSource() instanceof EntityPlayer) {
			return super.attackEntityFrom(source, amount);
		}
		return false;
	}

	public static class AIWanterTamedSpiritTreeFace extends AIWander<EntityTamedSpiritTreeFace> {
		public AIWanterTamedSpiritTreeFace(EntityTamedSpiritTreeFace entity, double range, double speed) {
			super(entity, range, speed);
		}

		public AIWanterTamedSpiritTreeFace(EntityTamedSpiritTreeFace entity, double range, double speed, int chance) {
			super(entity, range, speed, chance);
		}

		@Override
		protected boolean canMove() {
			return this.entity.isActive() && !this.entity.isAttacking();
		}
	}
}
