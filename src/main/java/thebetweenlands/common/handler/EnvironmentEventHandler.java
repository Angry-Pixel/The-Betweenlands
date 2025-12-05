package thebetweenlands.common.handler;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ServerLevelData;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.api.environment.EnvironmentEvent;
import thebetweenlands.api.network.GenericDataAccessorAccess;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.clientbound.SyncEnvironmentEventDataPacket;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.world.event.BLEnvironmentEvent;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.WorldStorageGetter;

public class EnvironmentEventHandler {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(EnvironmentEventHandler::tickEvents);
		NeoForge.EVENT_BUS.addListener(EnvironmentEventHandler::syncEventsOnJoin);
	}

	private static void tickEvents(LevelTickEvent.Post event) {
		BetweenlandsWorldStorage storage = WorldStorageGetter.getNullable(event.getLevel());
		if (storage != null) {
			BLEnvironmentEventRegistry reg = storage.getEnvironmentEventRegistry();

			for (EnvironmentEvent eevent : reg.getEvents().values()) {
				if (!eevent.isLoaded()) continue;
				if (reg.isDisabled()) {
					if (eevent.isActive()) {
						eevent.setActive(event.getLevel(), false);
						eevent.setDefaults(event.getLevel(), reg);
					}
				} else {
					eevent.tick(event.getLevel());
				}
				if (!event.getLevel().isClientSide()) {
					GenericDataAccessorAccess dataManager = eevent.getDataManager();
					if (dataManager != null) {
						dataManager.tick(event.getLevel());
						if (dataManager.isDirty()) {
							PacketDistributor.sendToPlayersInDimension((ServerLevel) event.getLevel(), new SyncEnvironmentEventDataPacket(eevent, false));
						}
					}
				}
			}

			if (event.getLevel() instanceof ServerLevel level && TheBetweenlands.isBetweenlands(level)) {
				ServerLevelData data = (ServerLevelData)level.getLevelData();
				data.setRainTime(2000);
				data.setThunderTime(2000);
				data.setRaining(reg.isEventActive(EnvironmentEventRegistry.HEAVY_RAIN.getId()));
				data.setThundering(reg.isEventActive(EnvironmentEventRegistry.THUNDERSTORM.getId()));
				level.setThunderLevel(0.0F);
				level.oRainLevel = level.rainLevel;
				float rainingStrength = level.rainLevel;
				if(reg.isEventActive(EnvironmentEventRegistry.HEAVY_RAIN.getId())) {
					if (rainingStrength < 0.5F) {
						rainingStrength += 0.0125F;
					}
					if (rainingStrength > 0.5F) {
						rainingStrength = 0.5F;
					}
				} else {
					if (rainingStrength > 0) {
						rainingStrength -= 0.0125F;
					}
					if (rainingStrength < 0) {
						rainingStrength = 0;
					}
				}
				level.rainLevel = rainingStrength;
			}
		}
	}

	//Send packet to sync events on joining
	private static void syncEventsOnJoin(EntityJoinLevelEvent event) {
		if (!event.getLevel().isClientSide() && event.getEntity() instanceof ServerPlayer sp) {
			BetweenlandsWorldStorage storage = WorldStorageGetter.getNullable(event.getLevel());
			if (storage != null) {
				for (EnvironmentEvent eevent : storage.getEnvironmentEventRegistry().getEvents().values()) {
					if (eevent instanceof BLEnvironmentEvent) {
						PacketDistributor.sendToPlayer(sp, new SyncEnvironmentEventDataPacket(eevent, true));
					}
					if (eevent.isActive())
						AdvancementCriteriaRegistry.EVENT.get().trigger(sp, eevent);
				}
			}
		}
	}
}
