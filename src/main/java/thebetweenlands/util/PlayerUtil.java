package thebetweenlands.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public final class PlayerUtil {
	private static final Field f_NetHandlerPlayServer_floating;
	private static final Field f_NetHandlerPlayServer_floatingTickCount;
	private static final Field f_NetHandlerPlayServer_vehicleFloating;
	private static final Field f_NetHandlerPlayServer_vehicleFloatingTickCount;
	private static final Method m_NetHandlerPlayServer_captureCurrentPosition;

	static {
		f_NetHandlerPlayServer_floating = ReflectionHelper.findField(NetHandlerPlayServer.class, "floating", "field_184344_B", "B");
		f_NetHandlerPlayServer_floatingTickCount = ReflectionHelper.findField(NetHandlerPlayServer.class, "floatingTickCount", "field_147365_f", "C");
		f_NetHandlerPlayServer_vehicleFloating = ReflectionHelper.findField(NetHandlerPlayServer.class, "vehicleFloating", "field_184345_D", "D");
		f_NetHandlerPlayServer_vehicleFloatingTickCount = ReflectionHelper.findField(NetHandlerPlayServer.class, "vehicleFloatingTickCount", "field_184346_E", "E");
		m_NetHandlerPlayServer_captureCurrentPosition = ReflectionHelper.findMethod(NetHandlerPlayServer.class, "captureCurrentPosition", "func_184342_d");
	}

	private PlayerUtil() {}

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

	public static void teleport(Entity player, double x, double y, double z) {
		if(player.isRiding()) {
			player.dismountRidingEntity();
		}

		player.setPositionAndUpdate(x, y, z);
		player.fallDistance = 0.0F;

		if(player instanceof EntityPlayerMP) {
			NetHandlerPlayServer handler = ((EntityPlayerMP) player).connection;

			try {
				//Server sided captured positions need to be updated.
				//This is necessary if the player is teleported after the player has already ticked,
				//otherwise the server would complain with "player moved wrongly".
				m_NetHandlerPlayServer_captureCurrentPosition.invoke(handler);
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
