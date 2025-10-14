package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;

public class InfestationIgnoreData {

	public static final Codec<InfestationIgnoreData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.LONG.fieldOf("immunity_timestamp").forGetter(o -> o.immunityTime)
	).apply(instance, InfestationIgnoreData::new));

	private long immunityTime;

	public InfestationIgnoreData() {
		this(-1);
	}

	private InfestationIgnoreData(long immunityTime) {
		this.immunityTime = immunityTime;
	}

	public boolean isImmune(Player player) {
		return this.getRemainingImmunityTicks(player) > 0;
	}

	public int getRemainingImmunityTicks(Player player) {
		return this.immunityTime >= 0 ? Math.max(0, (int)(this.immunityTime - player.level().getGameTime())) : 0;
	}

	public void setImmune(Player player, int duration) {
		if(duration <= 0) {
			this.setNotImmune(player);
		} else {
			this.immunityTime = player.level().getGameTime() + duration;
		}
	}

	public void setNotImmune(Player player) {
		if(this.immunityTime != -1) {
			this.immunityTime = -1;
		}
	}
}
