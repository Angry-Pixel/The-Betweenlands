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

	/**
	 * Creates a puppet AI that takes over the entities AI. A list of additional tasks to run can be specified
	 * @param taskOwner
	 * @param puppeteer
	 * @param tasks
	 */
	public EntityAIPuppet(EntityLivingBase taskOwner, Supplier<Entity> puppeteer, @Nullable List<EntityAIBase> tasks) {
		this.taskOwner = taskOwner;
		this.puppeteer = puppeteer;
		if(tasks != null && !tasks.isEmpty()) {
			this.tasks = new EntityAITasks(taskOwner.world.profiler);
			for(int i = 0; i < tasks.size(); i++) {
				this.tasks.addTask(i, tasks.get(i));
			}
		} else {
			this.tasks = null;
		}
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
	public boolean isInterruptible() {
		//Prevent other target AIs from taking over
		return false;
	}

	@Override
	public void updateTask() {
		if(this.taskOwner instanceof EntityCreature) {
			EntityCreature creature = (EntityCreature) this.taskOwner;
			EntityLivingBase target = creature.getAttackTarget();

			if(target == this.puppeteer.get()) {
				creature.setAttackTarget(null);
			} else if(target != null && target.hasCapability(CapabilityRegistry.CAPABILITY_PUPPET, null)) {
				IPuppetCapability targetCap = target.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
				if(targetCap.getPuppeteer() == this.puppeteer.get()) {
					creature.setAttackTarget(null);
				}
			}
		}

		if(this.tasks != null) {
			this.tasks.onUpdateTasks();
		}
	}

	/**
	 * Adds a puppet AI to an entity
	 * @param entity
	 * @param creature
	 * @param targetTasks
	 */
	public static void addPuppetAI(Supplier<Entity> puppeteer, EntityLivingBase entity, EntityAITasks creatureTasks, @Nullable List<EntityAIBase> targetTasks) {
		interruptAI(creatureTasks);

		if(getPuppetAI(creatureTasks) == null) {
			creatureTasks.addTask(Integer.MAX_VALUE, new EntityAIPuppet(entity, puppeteer, targetTasks));
		}
	}

	/**
	 * Removes the puppet AI from an entity
	 * @param creatureTasks
	 */
	public static void removePuppetAI(EntityAITasks creatureTasks) {
		EntityAIPuppet puppetAI = getPuppetAI(creatureTasks);

		if(puppetAI != null) {
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
