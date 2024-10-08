package thebetweenlands.api.entity.bossbar;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import thebetweenlands.common.network.clientbound.AddBetweenlandsBossBarPacket;

public class BetweenlandsServerBossBar extends ServerBossEvent {

	private final BetweenlandsBossBar.BossType type;

	public BetweenlandsServerBossBar(Component name, BetweenlandsBossBar.BossType type) {
		super(name, BossBarColor.RED, BossBarOverlay.PROGRESS);
		this.type = type;
	}

	@Override
	public void addPlayer(ServerPlayer player) {
		if (this.players.add(player) && this.isVisible()) {
			player.connection.send(new AddBetweenlandsBossBarPacket(this.getId(), this.getName(), this.getProgress(), this.type));
		}
	}
}
