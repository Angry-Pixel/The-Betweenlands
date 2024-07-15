package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.network.UpdateRotSmellPacket;
import thebetweenlands.common.registries.AttachmentRegistry;

public class RotSmellData {

	private long smellyTime;
	private long immunityTime;

	public static final Codec<RotSmellData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.LONG.fieldOf("smell_timestamp").forGetter(o -> o.smellyTime),
		Codec.LONG.fieldOf("immunity_timestamp").forGetter(o -> o.immunityTime)
	).apply(instance, RotSmellData::new));

	public RotSmellData() {
		this(-1, -1);
	}

	public RotSmellData(long level, long prevLevel) {
		this.smellyTime = level;
		this.immunityTime = prevLevel;
	}

	public boolean isSmellingBad(Player player) {
		return this.getRemainingSmellyTicks(player) > 0 && this.getRemainingImmunityTicks(player) <= 0;
	}

	public int getRemainingSmellyTicks(Player player) {
		return this.smellyTime >= 0 ? Math.max(0, (int)(this.smellyTime - player.level().getGameTime())) : 0;
	}

	public void setSmellingBad(Player player, int duration) {
		if(duration <= 0) {
			this.setNotSmellingBad(player);
		} else {
			this.smellyTime = player.level().getGameTime() + duration;
			this.setChanged(player);
		}
	}

	public void setNotSmellingBad(Player player) {
		if(this.smellyTime != -1) {
			this.smellyTime = -1;
			this.setChanged(player);
		}
	}

	public int getRemainingImmunityTicks(Player player) {
		return this.immunityTime >= 0 ? Math.max(0, (int)(this.immunityTime - player.level().getGameTime())) : 0;
	}

	public void setImmune(Player player, int duration) {
		if(duration <= 0) {
			if(this.immunityTime != -1) {
				this.immunityTime = -1;
				this.setChanged(player);
			}
		} else {
			this.immunityTime = player.level().getGameTime() + duration;
			this.setChanged(player);
		}
	}

	private void setChanged(Player player) {
		if (player instanceof ServerPlayer) {
			PacketDistributor.sendToPlayer((ServerPlayer) player, new UpdateRotSmellPacket(this.smellyTime, this.immunityTime));
		}
	}

	public static void onPlayerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		if(player.level().isClientSide()) {
			RotSmellData cap = player.getData(AttachmentRegistry.ROT_SMELL);
			if (player.level().getRandom().nextInt(4) == 0 && cap.isSmellingBad(player)) {
//				player.level().addParticle(ParticleRegistry.FLY.get(), player.getX(), player.getY() + 1.0D, player.getZ(), 0.0D, 0.0D, 0.0D);
			}
		}
	}
}
