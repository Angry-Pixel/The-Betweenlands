package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.world.World;

public class EntitySpiritTreeFaceSmall extends EntitySpiritTreeFace {
	public EntitySpiritTreeFaceSmall(World world) {
		super(world);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();

		//this.tasks.addTask(0, new EntityAILookIdle(this));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		this.fixUnsuitablePosition();
	}

	@Override
	public boolean isActive() {
		//return this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
		return true;
	}
}
