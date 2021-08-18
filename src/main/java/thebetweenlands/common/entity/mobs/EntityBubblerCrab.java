package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.projectiles.EntityBubblerCrabBubble;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityBubblerCrab extends EntitySiltCrab {
	private AIBubbleAttack aiBubbleAttack;

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
	public void randomizeSiltCrabProperties() {
		setItem1(getPartFromLootTable(LootTableRegistry.BUBBLER_CRAB_TRIM_1));
		setItem2(getPartFromLootTable(LootTableRegistry.BUBBLER_CRAB_TRIM_2));
		setItem3(getPartFromLootTable(LootTableRegistry.BUBBLER_CRAB_TRIM_3));
	}
	
	@Override
	public ItemMob getCrabPotItem() {
		return ItemRegistry.BUBBLER_CRAB;
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
						crab.getEntityWorld().playSound((EntityPlayer) null, crab.getPosition(), SoundRegistry.BUBBLER_SPIT, SoundCategory.HOSTILE, 1F, 1.0F);
						crab.aggroCooldown = 0;
					}
				}
			}
			super.updateTask();
		}
	}
}
