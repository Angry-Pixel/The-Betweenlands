package thebetweenlands.common.entity.ai.goals;

import java.util.EnumSet;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredBlock;
import thebetweenlands.common.entity.monster.BonePuppetRanged;
import thebetweenlands.common.entity.monster.Wight;
import thebetweenlands.common.network.clientbound.WightVolatileParticlesPacket;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class WightSeekBonePileGoal extends Goal {

	protected final Wight wight;
	protected final Level level;
	protected final PathNavigation navigation;

	protected int cooldown;

	@Nullable
	protected BlockPos bonePilePos = null;
	protected DeferredBlock<Block> bonePile = BlockRegistry.SLIMY_BONE_ORE;

	public WightSeekBonePileGoal(Wight wight) {
		this.wight = wight;
		level = wight.level();
		navigation = wight.getNavigation();
		cooldown = 0;// = 10 + level.getRandom().nextInt(20);
		setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
	}

	@Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

	@Override
	public boolean canUse() {
		boolean canBuff = wight.getTarget() != null && !wight.isPassenger() && wight.isVolatile();
		if(canBuff) {
			if(cooldown <= 0/* && level.getRandom().nextInt(20) == 0*/) {
				return true;
			}
			cooldown--;
		}
		return false;
	}

	@Override
	public void start() {
		bonePilePos = getBonePileTarget();
	}

	@Override
	public boolean canContinueToUse() {
		return bonePilePos != null && !wight.level().getBlockState(bonePilePos).is(bonePile);
	}

	@Override
	public void stop() {
		navigation.stop();
		cooldown = 80 + level.getRandom().nextInt(60);
		if(!wight.isPassenger() && (bonePilePos == null || !wight.level().getBlockState(bonePilePos).is(bonePile))) {
			wight.setVolatile(false);
		}
		//wight.setOverrideMovement(false);
	}

	@Override
	public void tick() {
		if (bonePilePos != null && wight.level().getBlockState(bonePilePos).is(bonePile)) {
			if (!wight.isPassenger()) {
				wight.getMoveControl().setWantedPosition(bonePilePos.getX() + 0.5D, bonePilePos.getY() + 1D, bonePilePos.getZ() + 0.5D, wight.getAttributeValue(Attributes.FLYING_SPEED));
				if (/*wight.blockPosition() == bonePilePos.above() && */wight.level().getBlockState(wight.blockPosition().below()).is(bonePile)) {
					wight.level().destroyBlock(wight.blockPosition().below(), true);
					BonePuppetRanged puppet = EntityRegistry.BONE_PUPPET_RANGED.get().create(wight.level());
					if (puppet != null) {
						puppet.setPos(wight.blockPosition().below().getBottomCenter());
						puppet.setYRot(wight.getYRot());
						wight.level().addFreshEntity(puppet);
						wight.startRiding(puppet, true);
						wight.setOverrideMovement(false);
					}
				}
			}
		}
	}

	@Nullable
	protected BlockPos getBonePileTarget() {
		LivingEntity target = wight.getTarget();
		if (target != null && target instanceof Player player) {
			Vec3 playerPos = player.position();
			AABB searchBox = new AABB(wight.blockPosition()).expandTowards(playerPos).inflate(4);
			BlockPos minPos = BlockPos.containing(searchBox.minX, searchBox.minY, searchBox.minZ);
			BlockPos maxPos = BlockPos.containing(searchBox.maxX, searchBox.maxY, searchBox.maxZ);
			for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos))
				if (wight.level().getBlockState(pos).is(bonePile)) {
					wight.setOverrideMovement(true);
					return pos;
				}
		}
		return null;
	}
}
