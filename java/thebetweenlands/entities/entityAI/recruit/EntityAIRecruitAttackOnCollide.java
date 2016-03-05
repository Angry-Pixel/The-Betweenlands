package thebetweenlands.entities.entityAI.recruit;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;

public class EntityAIRecruitAttackOnCollide extends EntityAIAttackOnCollide implements IRecruitAI {
	public EntityAIRecruitAttackOnCollide(EntityCreature attacker, double speed, boolean longMemory) {
		super(attacker, speed, longMemory);
	}
}
