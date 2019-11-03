package thebetweenlands.util;

import java.lang.reflect.Field;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public final class FlightHelper {
	private static final Field f_NetHandlerPlayServer_floating;
	private static final Field f_NetHandlerPlayServer_floatingTickCount;
	private static final Field f_NetHandlerPlayServer_vehicleFloating;
	private static final Field f_NetHandlerPlayServer_vehicleFloatingTickCount;

	static {
		f_NetHandlerPlayServer_floating = ReflectionHelper.findField(NetHandlerPlayServer.class, "floating", "field_184344_b", "b");
		f_NetHandlerPlayServer_floatingTickCount = ReflectionHelper.findField(NetHandlerPlayServer.class, "floatingTickCount", "field_147365_f", "c");
		f_NetHandlerPlayServer_vehicleFloating = ReflectionHelper.findField(NetHandlerPlayServer.class, "vehicleFloating", "field_184345_d", "d");
		f_NetHandlerPlayServer_vehicleFloatingTickCount = ReflectionHelper.findField(NetHandlerPlayServer.class, "vehicleFloatingTickCount", "field_184346_e", "e");
	}

	private FlightHelper() {}

	public static void resetFloating(Entity player) {
		if(player instanceof EntityPlayerMP) {
			NetHandlerPlayServer handler = ((EntityPlayerMP) player).connection;
			try {
				f_NetHandlerPlayServer_floating.set(handler, false);
				f_NetHandlerPlayServer_floatingTickCount.set(handler, 0);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void resetVehicleFloating(Entity player) {
		if(player instanceof EntityPlayerMP) {
			NetHandlerPlayServer handler = ((EntityPlayerMP) player).connection;
			try {
				f_NetHandlerPlayServer_vehicleFloating.set(handler, false);
				f_NetHandlerPlayServer_vehicleFloatingTickCount.set(handler, 0);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
