package thebetweenlands.common.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import thebetweenlands.api.entity.NonDismountable;

public class EntityUnmountHandler {

	public static void onEntityMountEvent(EntityMountEvent event) {
		if (event.isDismounting()) {
			Entity rider = event.getEntityMounting();
			Entity mount = event.getEntityBeingMounted();

			if (mount instanceof NonDismountable nonDismountable && rider instanceof Player player && mount.isAlive() && rider.isAlive() && rider.isShiftKeyDown() && nonDismountable.isUnmountBlocked(player)) {
				CompoundTag nbt = rider.getPersistentData();

				//Allow blocking unmount just once per tick. If it tries unmounting the player multiple times per tick then
				//that means the player is (also) being unmounted by something else other than the player's controls.
				if (nbt.getLong("thebetweenlands.unmount.lastBlockedTime") != rider.level().getGameTime()) {
					nbt.putLong("thebetweenlands.unmount.lastBlockedTime", rider.level().getGameTime());
					nonDismountable.onUnmountBlocked(player);
					event.setCanceled(true);
				}
			}
		}
	}

	public static void onRenderHUD(RenderGuiLayerEvent.Pre event) {
		if (event.getName() == VanillaGuiLayers.VEHICLE_HEALTH) {
			LocalPlayer player = Minecraft.getInstance().player;

			if (player != null && player.isPassenger()) {
				Entity mount = player.getVehicle();

				if (mount instanceof NonDismountable nonDismountable) {
					event.setCanceled(true);
					if (nonDismountable.shouldPreventStatusBarText(player)) {
						Minecraft.getInstance().gui.setOverlayMessage(Component.empty(), false);
					}
				}
			}
		}
	}
}
