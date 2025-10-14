package thebetweenlands.common.entity.ai.goals;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.entity.monster.SwampHag;
import thebetweenlands.common.entity.monster.Wight;
import thebetweenlands.common.network.clientbound.WightVolatileParticlesPacket;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.EnumSet;
import java.util.List;

public class WightBuffSwampHagGoal extends Goal {

	protected final Wight wight;
	protected final Level level;
	protected final PathNavigation navigation;

	protected int cooldown;

	@Nullable
	protected SwampHag hag = null;

	public WightBuffSwampHagGoal(Wight wight) {
		this.wight = wight;
		this.level = wight.level();
		this.navigation = wight.getNavigation();
		this.cooldown = 10 + this.level.getRandom().nextInt(20);
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
	}

	@Override
	public boolean canUse() {
		boolean canBuff = this.wight.getTarget() != null && !this.wight.isPassenger() && this.getTargetSwampHag() != null;
		if(canBuff) {
			if(this.cooldown <= 0 && this.level.getRandom().nextInt(20) == 0) {
				return true;
			}
			this.cooldown--;
		}
		return false;
	}

	@Override
	public void start() {
		this.hag = this.getTargetSwampHag();
	}

	@Override
	public boolean canContinueToUse() {
		return this.hag != null && this.hag.isAlive();
	}

	@Override
	public void stop() {
		this.navigation.stop();
		this.cooldown = 80 + this.level.getRandom().nextInt(60);
		if(!this.wight.isPassenger() && (this.hag == null || !this.hag.isAlive())) {
			this.wight.setVolatile(false);
		}
	}

	@Override
	public void tick() {
		if(this.hag != null) {
			if(!this.wight.isVolatile()) {
				this.wight.setVolatile(true);
				PacketDistributor.sendToPlayersTrackingEntity(this.wight, new WightVolatileParticlesPacket(this.wight.getId()));
				this.level.playSound(null, this.wight.blockPosition(), SoundRegistry.WIGHT_ATTACK.get(), SoundSource.HOSTILE, 1.6F, 1.0F);
			}

			if(!this.wight.isPassenger()) {
				if (this.wight.distanceTo(this.hag) < 1.75D) {
					this.wight.startRiding(this.hag);
				}

				this.wight.lookAt(this.hag, 10.0F, (float)this.wight.getMaxHeadYRot());
				this.wight.getMoveControl().setWantedPosition(this.hag.getX(), this.hag.getY(), this.hag.getZ(), 1);
			}
		}
	}

	@Nullable
	protected SwampHag getTargetSwampHag() {
		LivingEntity target = this.wight.getTarget();
		if(target != null) {
			double range = 16.0D;
			SwampHag closestSuitableToTarget = null;
			List<SwampHag> nearby = this.level.getEntitiesOfClass(SwampHag.class, this.wight.getBoundingBox().inflate(range));
			for(SwampHag hag : nearby) {
				if(hag.getTarget() == target && hag.getPassengers().isEmpty() && hag.distanceTo(this.wight) <= range && (closestSuitableToTarget == null || hag.distanceTo(target) <= closestSuitableToTarget.distanceTo(target))) {
					closestSuitableToTarget = hag;
				}
			}
			return closestSuitableToTarget;
		}
		return null;
	}
}
