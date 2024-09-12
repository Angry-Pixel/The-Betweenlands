package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.network.clientbound.attachment.UpdateMudWalkerPacket;

public class MudWalkerData {
	private long reductionTime;

	public static final Codec<MudWalkerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.LONG.fieldOf("reduction_timestamp").forGetter(o -> o.reductionTime)
	).apply(instance, MudWalkerData::new));

	public MudWalkerData() {
		this(-1);
	}

	public MudWalkerData(long reductionTime) {
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
			this.setNotActive(player);
		} else {
			this.reductionTime = player.level().getGameTime() + duration;
			this.setChanged(player);
		}
	}

	public void setNotActive(Player player) {
		if(this.reductionTime != -1) {
			this.reductionTime = -1;
			this.setChanged(player);
		}
	}

	private void setChanged(Player player) {
		if (player instanceof ServerPlayer) {
			PacketDistributor.sendToPlayer((ServerPlayer) player, new UpdateMudWalkerPacket(this.reductionTime));
		}
	}
}
