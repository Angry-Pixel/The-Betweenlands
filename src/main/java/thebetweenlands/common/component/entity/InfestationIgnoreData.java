package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.network.clientbound.attachment.UpdateInfestationPacket;

public class InfestationIgnoreData {

	public static final Codec<InfestationIgnoreData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.LONG.fieldOf("immunity_timestamp").forGetter(o -> o.immunityTime)
	).apply(instance, InfestationIgnoreData::new));

	private long immunityTime;

	public InfestationIgnoreData() {
		this(-1);
	}

	public InfestationIgnoreData(long immunityTime) {
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
			this.setChanged(player);
		}
	}

	public void setNotImmune(Player player) {
		if(this.immunityTime != -1) {
			this.immunityTime = -1;
			this.setChanged(player);
		}
	}

	private void setChanged(Player player) {
		if (player instanceof ServerPlayer) {
			PacketDistributor.sendToPlayer((ServerPlayer) player, new UpdateInfestationPacket(this.immunityTime));
		}
	}
}
