package thebetweenlands.common.entity.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.entity.monster.chiromaw.ChiromawMatriarch;
import thebetweenlands.common.entity.projectile.ChiromawDroppings;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class MatriarchPoopGoal extends Goal {

	private final ChiromawMatriarch matriarch;

	public MatriarchPoopGoal(ChiromawMatriarch matriarch) {
		this.matriarch = matriarch;
		this.setFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		return !this.matriarch.isNesting() && this.matriarch.getRandom().nextInt(10) == 0; // frequency of random pooping
	}

	@Override
	public boolean canContinueToUse() {
		return false;
	}

	@Override
	public void tick() {
		this.checkForPoopTarget();
	}

	private void checkForPoopTarget() {
		int distanceToSurface = Mth.floor(this.matriarch.getY()) - this.matriarch.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, this.matriarch.blockPosition()).getY();
		List<BlockPos> placeToPoop = new ArrayList<>();
		if (distanceToSurface >= 16) {
			AABB underBox = this.matriarch.getBoundingBox().inflate(0.625D, distanceToSurface, 0.625D).expandTowards(0, -distanceToSurface / 2.0D, 0);
			if (!this.getPoopTarget(this.matriarch.level(), underBox).isEmpty()) {
				Player player = this.getPoopTarget(this.matriarch.level(), underBox).getFirst();
				if (player != null) {
					for (BlockPos posDrop : BlockPos.betweenClosed(player.blockPosition().offset(-1, 0, -1), player.blockPosition().offset(1, 2, 1)))
						if (this.matriarch.level().isEmptyBlock(posDrop) && this.matriarch.level().canSeeSky(posDrop))
							placeToPoop.add(posDrop);
					if (!placeToPoop.isEmpty()) {
						Collections.shuffle(placeToPoop);
						BlockPos posPoop = placeToPoop.getFirst();
						ChiromawDroppings poopEntity = new ChiromawDroppings(this.matriarch.level(), this.matriarch, posPoop.getX() + 0.5D, this.matriarch.blockPosition().getY(), posPoop.getZ() + 0.5D);
						this.matriarch.level().addFreshEntity(poopEntity);
						this.matriarch.playSound(SoundRegistry.CHIROMAW_MATRIARCH_POOP.get(), 0.5F, this.matriarch.getVoicePitch());
					}
				}
			}
		}
	}

	public List<Player> getPoopTarget(Level level, AABB underBox) {
		return level.getEntitiesOfClass(Player.class, underBox, EntitySelector.ENTITY_STILL_ALIVE);
	}
}
