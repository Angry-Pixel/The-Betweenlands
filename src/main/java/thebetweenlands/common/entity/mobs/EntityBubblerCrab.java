package thebetweenlands.common.entity.mobs;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.projectiles.EntityBubblerCrabBubble;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityBubblerCrab extends EntityMob implements IEntityBL {

	private EntityAIAttackMelee aiAttack;
	private EntityAIAvoidEntity<EntityPlayer> aiRunAway;
	private EntityAINearestAttackableTarget<EntityPlayer> aiTarget;
	private AIBubbleAttack aiBubbleAttack;
	private int aggroCooldown = 200;
	private boolean canAttack = false;

	public EntityBubblerCrab(World world) {
		super(world);
		this.setSize(0.7F, 0.5F);
		this.stepHeight = 2;
	}

	@Override
	protected void initEntityAI() {
		this.aiAttack = new EntityAIAttackMelee(this, 1.0D, true);
		this.aiRunAway = new EntityAIAvoidEntity<EntityPlayer>(this, EntityPlayer.class, 10.0F, 0.7D, 0.7D);
		this.aiTarget =  new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true);
		
		this.aiBubbleAttack = new AIBubbleAttack(this);

		this.tasks.addTask(0, this.aiAttack);
		this.tasks.addTask(1, this.aiRunAway);
		this.tasks.addTask(2, aiBubbleAttack);
		this.tasks.addTask(3, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(4, new EntityAILookIdle(this));
		this.tasks.addTask(5, new EntityAIAttackOnCollide(this));

		this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(1, this.aiTarget);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 5;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!this.world.isRemote) {
			if (this.aggroCooldown == 200 && !this.canAttack) {
				this.tasks.removeTask(this.aiRunAway);
				this.tasks.addTask(0, this.aiAttack);
				this.targetTasks.addTask(1, this.aiTarget);
				this.canAttack = true;
			}

			if (this.aggroCooldown == 0 && this.canAttack) {
				this.tasks.removeTask(this.aiAttack);
				this.targetTasks.removeTask(this.aiTarget);
				this.tasks.addTask(1, this.aiRunAway);
				this.canAttack = false;
			}

			if (this.aggroCooldown < 201)
				this.aggroCooldown++;
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		return !source.equals(DamageSource.DROWN) && super.attackEntityFrom(source, damage);
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		if (!this.world.isRemote && getDistance(player) <= 1.5F && this.canAttack) {
			this.aggroCooldown = 0;
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		boolean attacked;
		if(attacked = super.attackEntityAsMob(entityIn)) {
			this.playSound(SoundRegistry.CRAB_SNIP, 1, 1);
		}
		return attacked;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.SILT_CRAB;
	}
	
	@Override
    public float getBlockPathWeight(BlockPos pos) {
        return 0.5F;
    }

    @Override
    protected boolean isValidLightLevel() {
    	return true;
    }
    
    static class AIBubbleAttack extends EntityAIBase {
		private final EntityBubblerCrab crab;
		private int attackStep;
		private int attackTime;

		public AIBubbleAttack(EntityBubblerCrab crabIn) {
			crab = crabIn;
		}

		@Override
		public boolean shouldExecute() {
			EntityLivingBase entitylivingbase = crab.getAttackTarget();
			return entitylivingbase != null && entitylivingbase.isEntityAlive();
		}

		@Override
		public void startExecuting() {
			attackStep = 0;
		}

		@Override
		public void updateTask() {
			--attackTime;
			EntityLivingBase entitylivingbase = crab.getAttackTarget();
			double d0 = crab.getDistanceSq(entitylivingbase);
			if (d0 < 25D) {
				double d1 = entitylivingbase.posX - crab.posX;
				double d2 = entitylivingbase.posY - crab.posY;
				double d3 = entitylivingbase.posZ - crab.posZ;
				if (attackTime <= 0) {
					++attackStep;
					if (attackStep == 1)
						attackTime = 40;
					else {
						attackTime = 40;
						attackStep = 0;
					}
					if (attackStep == 1) {
						EntityBubblerCrabBubble entityBubble = new EntityBubblerCrabBubble(crab.getEntityWorld(), crab);
						entityBubble.setPosition(crab.posX, crab.posY + crab.height + 0.5D , crab.posZ);
						entityBubble.shoot(d1, d2, d3, 0.5F, 0F);
						crab.getEntityWorld().spawnEntity(entityBubble);
						crab.aggroCooldown = 0;
					}
				}
			}
			super.updateTask();
		}
	}
}
