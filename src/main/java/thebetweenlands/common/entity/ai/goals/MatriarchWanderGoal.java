package thebetweenlands.common.entity.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import thebetweenlands.common.entity.monster.chiromaw.ChiromawMatriarch;

import java.util.EnumSet;

public class MatriarchWanderGoal extends Goal {

	private final ChiromawMatriarch matriarch;

	public MatriarchWanderGoal(ChiromawMatriarch matriarch) {
		this.matriarch = matriarch;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		return !this.matriarch.isReturningToNest() && !this.matriarch.getMoveControl().hasWanted() && this.matriarch.getRandom().nextInt(10) == 0;
	}

	@Override
	public boolean canContinueToUse() {
		return false;
	}

	@Override
	public void tick() {
		BlockPos blockpos;

		if (this.matriarch.getBoundOrigin() == null) {
			blockpos = this.matriarch.blockPosition();
		} else {
			blockpos = this.matriarch.getBoundOrigin().pos();
		}

		for (int i = 0; i < 3; ++i) {
			BlockPos blockpos1 = blockpos.offset(this.matriarch.getRandom().nextInt(33) - 16, this.matriarch.getRandom().nextInt(17) - 8, this.matriarch.getRandom().nextInt(33) - 16);

			if (this.matriarch.level().isEmptyBlock(blockpos1)) {
				this.matriarch.getMoveControl().setWantedPosition(blockpos1.getX() + 0.5D, blockpos1.getY() + 0.5D, blockpos1.getZ() + 0.5D, 3D);

				if (this.matriarch.getTarget() == null) {
					this.matriarch.getLookControl().setLookAt(blockpos1.getX() + 0.5D, blockpos1.getY() + 0.5D, blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
				}
				break;
			}
		}
	}
}
