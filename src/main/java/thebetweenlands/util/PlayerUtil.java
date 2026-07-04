package thebetweenlands.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;

public class PlayerUtil {

	public static void resetFloating(Entity entity) {
		if (entity instanceof ServerPlayer player) {
			ServerGamePacketListenerImpl handler = player.connection;
			handler.clientIsFloating = false;
			handler.aboveGroundTickCount = 0;
		}
	}

	public static void resetVehicleFloating(Entity entity) {
		if (entity instanceof ServerPlayer player) {
			ServerGamePacketListenerImpl handler = player.connection;
			handler.clientVehicleIsFloating = false;
			handler.aboveGroundVehicleTickCount = 0;
		}
	}
}
