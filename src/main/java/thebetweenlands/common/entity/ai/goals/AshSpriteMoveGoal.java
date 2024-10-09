package thebetweenlands.common.entity.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import thebetweenlands.common.entity.monster.AshSprite;

import java.util.EnumSet;

public class AshSpriteMoveGoal extends Goal {
	private final AshSprite ashSprite;

	public AshSpriteMoveGoal(AshSprite sprite) {
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		this.ashSprite = sprite;
	}

	@Override
	public boolean canUse() {
		return !this.ashSprite.getMoveControl().hasWanted() && this.ashSprite.getRandom().nextInt(reducedTickDelay(7)) == 0;
	}

	@Override
	public boolean canContinueToUse() {
		return false;
	}

	@Override
	public void tick() {
		BlockPos blockpos = this.ashSprite.getBoundOrigin();
		if (blockpos == null) {
			blockpos = this.ashSprite.blockPosition();
		}

		for (int i = 0; i < 3; i++) {
			BlockPos blockpos1 = blockpos.offset(this.ashSprite.getRandom().nextInt(15) - 7, this.ashSprite.getRandom().nextInt(11) - 5, this.ashSprite.getRandom().nextInt(15) - 7);
			if (this.ashSprite.level().isEmptyBlock(blockpos1)) {
				this.ashSprite.getMoveControl().setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 0.25);
				if (this.ashSprite.getTarget() == null) {
					this.ashSprite.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0F, 20.0F);
				}
				break;
			}
		}
	}
}
