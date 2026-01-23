package thebetweenlands.common.entity.ai.goals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredBlock;
import thebetweenlands.common.entity.monster.BonePuppetRanged;
import thebetweenlands.common.entity.monster.Wight;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EntityRegistry;

public class WightSeekBonePileGoal extends Goal {

	protected final Wight wight;
	protected final Level level;
	protected int cooldown;

	protected DeferredBlock<Block> bonePile = BlockRegistry.SLIMY_BONE_ORE;

	public WightSeekBonePileGoal(Wight wight) {
		this.wight = wight;
		level = wight.level();
		this.cooldown = 10;
		setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
	}

	@Override
	public boolean canUse() {
		cooldown--;
		return wight.getTarget() != null && !wight.isPassenger() && wight.isVolatile() && wight.canTransformInToShaman && cooldown <= 0;
	}

	@Override
	public void start() {
		if(wight.getTargetBlock().isEmpty())
			getBonePileTarget();
	}

	@Override
	public boolean canContinueToUse() {
		return wight.getTargetBlock().isPresent();
	}

	@Override
	public void stop() {
	}

	@Override
	public void tick() {
		if (wight.getTargetBlock().isPresent()) {
			BlockPos target = wight.getTargetBlock().get();
			if (!wight.isPassenger()) {
				wight.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 1D, target.getZ() + 0.5D, wight.getAttributeValue(Attributes.FLYING_SPEED));
				if (level.getBlockState(wight.blockPosition().below()).is(bonePile)) { // jank but pos check isn't working atm
					level.destroyBlock(wight.blockPosition().below(), true);
					BonePuppetRanged puppet = EntityRegistry.BONE_PUPPET_RANGED.get().create(level);
					if (puppet != null) {
						puppet.setPos(wight.blockPosition().below().getBottomCenter());
						puppet.setYRot(wight.getYRot());
						puppet.setParentEntityID(wight.getId());
						level.addFreshEntity(puppet);
						wight.setVolatile(false);
						wight.clearTargetBlock();
						wight.canTransformInToShaman = false; // setting this so it only happens once
					}
				}
			}
		}
	}

	@Nullable
	protected void getBonePileTarget() {
		if (wight.getTarget() != null && wight.getTarget() instanceof Player player) {
			Vec3 playerPos = player.position();
			List<BlockPos> list = new ArrayList<>();
			AABB searchBox = new AABB(wight.blockPosition()).expandTowards(playerPos).inflate(4);
			BlockPos minPos = BlockPos.containing(searchBox.minX, searchBox.minY, searchBox.minZ);
			BlockPos maxPos = BlockPos.containing(searchBox.maxX, searchBox.maxY, searchBox.maxZ);
			// add counter and cache for 3 or more blocks then pick one at random as spawn target
			for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos)) {
				if (level.getBlockState(pos).is(bonePile) && level.isEmptyBlock(pos.above())) {
					list.add(new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
				}
			}
			if (!list.isEmpty() && list.size() >= 3) {
				Collections.shuffle(list);
				wight.setTargetBlock(list.get(0));
			}
			else
				cooldown = 20;
		}
	}
}
