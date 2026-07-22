package thebetweenlands.common.handler;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.world.spawning.CustomWorldSpawner;

public class WorldSpawnerEventHandler {

	@SubscribeEvent
	public static void onLevelTick(LevelTickEvent.Post event) {

		if (event.getLevel().isClientSide())
			return;

		if (!(event.getLevel() instanceof ServerLevel level))
			return;

		if (!level.dimension().equals(DimensionRegistries.DIMENSION_KEY))
			return;

		if (level.getGameTime() % 20 == 0 && !level.players().isEmpty()) {
			List<ServerPlayer> activePlayers = level.getPlayers(player -> !player.isSpectator());
			int playerCount = activePlayers.size();

			if (playerCount > 0) {
				ServerPlayer randomPlayer = activePlayers.get(level.getRandom().nextInt(playerCount));
				BlockPos playerPos = randomPlayer.blockPosition();
				CustomWorldSpawner.spawnNearPlayer(level, playerPos);
			}
		}
	}
}
