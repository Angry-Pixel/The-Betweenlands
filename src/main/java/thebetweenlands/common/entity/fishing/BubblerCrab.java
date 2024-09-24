package thebetweenlands.common.entity.fishing;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import thebetweenlands.common.entity.ai.goals.BubbleAttackGoal;
import thebetweenlands.common.item.misc.MobItem;
import thebetweenlands.common.registries.ItemRegistry;

public class BubblerCrab extends SiltCrab {

	public BubblerCrab(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(2, new BubbleAttackGoal(this));
	}

	@Override
	public MobItem<?> getCrabPotItem() {
		return ItemRegistry.BUBBLER_CRAB.get();
	}
}
