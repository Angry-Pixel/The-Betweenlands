package thebetweenlands.common.entity.ai.puppet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import thebetweenlands.api.capability.IPuppetCapability;
import thebetweenlands.common.registries.CapabilityRegistry;

public class EntityAIPuppet extends EntityAIBase {
	protected final EntityLivingBase taskOwner;
	protected final Supplier<Entity> puppeteer;
	protected final EntityAITasks tasks;

	private int tickCount;
    private int tickRate = 3;
	
	/**
	 * Creates a puppet AI that takes over the entities AI. A list of additional tasks to run can be specified
	 * @param taskOwner
	 * @param puppeteer
	 */
	public EntityAIPuppet(EntityLivingBase taskOwner, Supplier<Entity> puppeteer) {
		this.taskOwner = taskOwner;
		this.puppeteer = puppeteer;
		this.tasks = new EntityAITasks(taskOwner.world.profiler);
		this.setMutexBits(0);
	}

	/**
	 * Returns the additionally assigned tasks
	 * @return
	 */
	public EntityAITasks getSubTasks() {
		return this.tasks;
	}

	@Override
	public boolean shouldExecute() {
		return true;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return true;
	}

	@Override
	public boolean isInterruptible() {
		//Prevent other target AIs from taking over
		return this.getMutexBits() == 0;
	}

	@Override
	public void updateTask() {
		if(this.taskOwner instanceof EntityCreature) {
			EntityCreature creature = (EntityCreature) this.taskOwner;
			EntityLivingBase target = creature.getAttackTarget();

			if(target == this.puppeteer.get()) {
				creature.setAttackTarget(null);
			} else if(target != null) {
				IPuppetCapability cap = target.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
				if(cap != null && cap.getPuppeteer() == this.puppeteer.get()) {
					creature.setAttackTarget(null);
				}
			}
		}

		this.setMutexBits(0);

		if(this.tasks != null) {
			int mutexBits = 0;

			this.tasks.onUpdateTasks();

			for(EntityAITasks.EntityAITaskEntry task : this.tasks.taskEntries) {
				//if(task.using) {
				mutexBits |= task.action.getMutexBits();
				//}
			}

			this.setMutexBits(mutexBits);
		}
	}

	/**
	 * Adds a puppet AI to an entity
	 * @param entity
	 * @param tasks
	 */
	public static EntityAITasks addPuppetAI(Supplier<Entity> puppeteer, EntityLivingBase entity, EntityAITasks creatureTasks) {
		interruptAI(creatureTasks);

		EntityAIPuppet ai = getPuppetAI(creatureTasks);
		if(ai == null) {
			List<EntityAITasks.EntityAITaskEntry> tasks = new ArrayList<>(creatureTasks.taskEntries);
			
			//Temporarily remove all AIs to stop them
			for(EntityAITasks.EntityAITaskEntry task : tasks) {
				creatureTasks.removeTask(task.action);
			}
			
			creatureTasks.addTask(Integer.MIN_VALUE, ai = new EntityAIPuppet(entity, puppeteer));
			
			//Re-add AIs
			for(EntityAITasks.EntityAITaskEntry task : tasks) {
				creatureTasks.addTask(task.priority, task.action);
			}
		}
		
		return ai.getSubTasks();
	}

	/**
	 * Removes the puppet AI from an entity
	 * @param creatureTasks
	 */
	public static void removePuppetAI(EntityAITasks creatureTasks) {
		EntityAIPuppet puppetAI;
		while((puppetAI = getPuppetAI(creatureTasks)) != null) {
			creatureTasks.removeTask(puppetAI);
		}
	}

	/**
	 * Returns the puppet AI for the specified entity
	 * @param creatureTasks
	 * @return
	 */
	@Nullable
	public static EntityAIPuppet getPuppetAI(EntityAITasks creatureTasks) {
		for(EntityAITaskEntry entry : creatureTasks.taskEntries) {
			if(entry.action instanceof EntityAIPuppet) {
				return (EntityAIPuppet) entry.action;
			}
		}
		return null;
	}

	/**
	 * Interrupts all target tasks except the puppet AI
	 * @param creatureTasks
	 */
	public static void interruptAI(EntityAITasks creatureTasks) {
		List<EntityAITaskEntry> tasks = new ArrayList<EntityAITaskEntry>();
		for(EntityAITaskEntry entry : creatureTasks.taskEntries) {
			if(entry.action instanceof EntityAIPuppet == false) {
				tasks.add(entry);
			}
		}
		for(EntityAITaskEntry ai : tasks) {
			creatureTasks.removeTask(ai.action);
		}
		for(EntityAITaskEntry ai : tasks) {
			creatureTasks.addTask(ai.priority, ai.action);
		}
	}
}
