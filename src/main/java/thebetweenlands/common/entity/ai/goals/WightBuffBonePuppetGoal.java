package thebetweenlands.common.entity.ai.goals;

import java.util.EnumSet;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.entity.monster.BonePuppetRanged;
import thebetweenlands.common.entity.monster.Wight;
import thebetweenlands.common.network.clientbound.WightVolatileParticlesPacket;
import thebetweenlands.common.registries.SoundRegistry;
// Dupe of swamphag boosting for the moment NYI
public class WightBuffBonePuppetGoal extends Goal {

	protected final Wight wight;
	protected final Level level;
	protected final PathNavigation navigation;

	protected int cooldown;

	@Nullable
	protected BonePuppetRanged puppet = null;

	public WightBuffBonePuppetGoal(Wight wight) {
		this.wight = wight;
		this.level = wight.level();
		this.navigation = wight.getNavigation();
		this.cooldown = 0;//10 + this.level.getRandom().nextInt(20);
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
	}

	@Override
	public boolean canUse() {
		boolean canBuff = this.wight.getTarget() != null && !this.wight.isPassenger() && this.getTargetBonePuppet() != null;
		if(canBuff) {
			if(this.cooldown <= 0 && this.level.getRandom().nextInt(1) == 0) { //20
				return true;
			}
			this.cooldown--;
		}
		return false;
	}

	@Override
	public void start() {
		this.puppet = this.getTargetBonePuppet();
	}

	@Override
	public boolean canContinueToUse() {
		return this.puppet != null && this.puppet.isAlive();
	}

	@Override
	public void stop() {
		this.navigation.stop();
		this.cooldown = 80 + this.level.getRandom().nextInt(60);
		if(!this.wight.isPassenger() && (this.puppet == null || !this.puppet.isAlive())) {
			this.wight.setVolatile(false);
		}
	}

	@Override
	public void tick() {
		if(this.puppet != null) {
			if(!this.wight.isVolatile()) {
				this.wight.setVolatile(true);
				PacketDistributor.sendToPlayersTrackingEntity(this.wight, new WightVolatileParticlesPacket(this.wight.getId()));
				this.level.playSound(null, this.wight.blockPosition(), SoundRegistry.WIGHT_ATTACK.get(), SoundSource.HOSTILE, 1.6F, 1.0F);
			}

			if(!this.wight.isPassenger()) {
				if (this.wight.distanceTo(this.puppet) < 1.75D) {
					this.wight.startRiding(this.puppet);
				}

				this.wight.lookAt(this.puppet, 10.0F, (float)this.wight.getMaxHeadYRot());
				this.wight.getMoveControl().setWantedPosition(this.puppet.getX(), this.puppet.getY(), this.puppet.getZ(), 1);
			}
		}
	}

	@Nullable
	protected BonePuppetRanged getTargetBonePuppet() {
		LivingEntity target = this.wight.getTarget();
		if(target != null) {
			double range = 16.0D;
			BonePuppetRanged closestSuitableToTarget = null;
			List<BonePuppetRanged> nearby = this.level.getEntitiesOfClass(BonePuppetRanged.class, this.wight.getBoundingBox().inflate(range));
			for(BonePuppetRanged puppet : nearby) {
				if(puppet.getTarget() == target && puppet.getPassengers().isEmpty() && puppet.distanceTo(this.wight) <= range && (closestSuitableToTarget == null || puppet.distanceTo(target) <= closestSuitableToTarget.distanceTo(target))) {
					closestSuitableToTarget = puppet;
				}
			}
			return closestSuitableToTarget;
		}
		return null;
	}
}
