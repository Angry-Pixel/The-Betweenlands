package thebetweenlands.api.entity.bossbar;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import thebetweenlands.common.network.clientbound.AddBetweenlandsBossBarPacket;
import thebetweenlands.common.network.clientbound.RemoveBetweenlandsBossBarPacket;
import thebetweenlands.common.network.clientbound.UpdateBetweenlandsBossBarPacket;

import java.util.Set;
import java.util.UUID;

public class BetweenlandsServerBossBar extends ServerBossEvent {

	private final Set<ServerPlayer> players = Sets.newHashSet();
	private final BetweenlandsBoss.BossType type;

	public BetweenlandsServerBossBar(Component name, BetweenlandsBoss.BossType type) {
		super(name, BossBarColor.RED, BossBarOverlay.PROGRESS);
		this.type = type;
	}

	public BetweenlandsBoss.BossType getType() {
		return this.type;
	}

	public void addPlayer(ServerPlayer player) {
		if (this.players.add(player)) {
			player.connection.send(new AddBetweenlandsBossBarPacket(this.getId(), this.getName(), this.getProgress(), this.type));
		}
	}

	public void removePlayer(ServerPlayer player) {
		if (this.players.remove(player)) {
			player.connection.send(new RemoveBetweenlandsBossBarPacket(this.getId()));
		}
	}

	@Override
	public void setProgress(float progress) {
		if (progress != this.progress) {
			super.setProgress(progress);
			for (ServerPlayer player : this.players) {
				player.connection.send(new UpdateBetweenlandsBossBarPacket(this.getId(), this.getProgress(), this.getName()));
			}
		}
	}

	@Override
	public void setName(Component name) {
		if (!Objects.equal(name, this.name)) {
			super.setName(name);
			for (ServerPlayer player : this.players) {
				player.connection.send(new UpdateBetweenlandsBossBarPacket(this.getId(), this.getProgress(), this.getName()));
			}
		}
	}
}
