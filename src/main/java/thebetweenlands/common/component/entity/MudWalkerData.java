package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;

public class MudWalkerData {
	private long reductionTime;

	public static final Codec<MudWalkerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.LONG.fieldOf("reduction_timestamp").forGetter(o -> o.reductionTime)
	).apply(instance, MudWalkerData::new));

	public MudWalkerData() {
		this(-1);
	}

	private MudWalkerData(long reductionTime) {
		this.reductionTime = reductionTime;
	}

	public boolean isActive(Player player) {
		return this.getRemainingActiveTicks(player) > 0;
	}

	public int getRemainingActiveTicks(Player player) {
		return this.reductionTime >= 0 ? Math.max(0, (int)(this.reductionTime - player.level().getGameTime())) : 0;
	}

	public void setActive(Player player, int duration) {
		if(duration <= 0) {
			this.setNotActive();
		} else {
			this.reductionTime = player.level().getGameTime() + duration;
		}
	}

	public void setNotActive() {
		if(this.reductionTime != -1) {
			this.reductionTime = -1;
		}
	}
}
