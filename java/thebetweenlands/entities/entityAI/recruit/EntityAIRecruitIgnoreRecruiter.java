package thebetweenlands.entities.entityAI.recruit;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.recruitment.EntityPropertiesRecruit;

public class EntityAIRecruitIgnoreRecruiter extends EntityAIBase implements IRecruitAI {
	protected final EntityCreature taskOwner;
	protected final EntityPropertiesRecruit properties;

	public EntityAIRecruitIgnoreRecruiter(EntityCreature taskOwner) {
		this.taskOwner = taskOwner;
		this.properties = BLEntityPropertiesRegistry.HANDLER.getProperties(taskOwner, EntityPropertiesRecruit.class);
	}

	@Override
	public boolean shouldExecute() {
		return this.taskOwner.getAttackTarget() != null && this.properties != null && this.taskOwner.getAttackTarget() == this.properties.getRecruiter();
	}

	@Override
	public boolean continueExecuting() {
		return false;
	}

	@Override
	public void startExecuting() {
		this.taskOwner.setAttackTarget(null);
		super.startExecuting();
	}
}
