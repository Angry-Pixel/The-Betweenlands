package thebetweenlands.common.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityPreventUnmount;

public class EntityUnmountHandler {
	@SubscribeEvent
	public static void onEntityMountEvent(EntityMountEvent event) {
		if(event.isDismounting()) {
			Entity rider = event.getEntityMounting();
			Entity mount = event.getEntityBeingMounted();

			if(mount instanceof IEntityPreventUnmount && rider instanceof EntityPlayer && mount.isEntityAlive() && rider.isEntityAlive() && rider.isSneaking() && ((IEntityPreventUnmount) mount).isUnmountBlocked((EntityPlayer) rider)) {
				NBTTagCompound nbt = rider.getEntityData();

				//Allow blocking unmount just once per tick. If it tries unmounting the player multiple times per tick then
				//that means the player is (also) being unmounted by something else other than the player's controls.
				if(nbt.getLong("thebetweenlands.unmount.lastBlockedTime") != rider.world.getTotalWorldTime()) {
					nbt.setLong("thebetweenlands.unmount.lastBlockedTime", rider.world.getTotalWorldTime());
					((IEntityPreventUnmount) mount).onUnmountBlocked((EntityPlayer) rider);
					event.setCanceled(true);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderHUD(Pre event) {
		if(event.getType() == ElementType.HEALTHMOUNT || event.getType() == ElementType.ALL) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;

			if(player != null && player.isRiding()) {
				Entity mount = player.getRidingEntity();

				if(mount instanceof IEntityPreventUnmount) {
					if(event.getType() == ElementType.HEALTHMOUNT) {
						event.setCanceled(true);
					} else if(event.getType() == ElementType.ALL && ((IEntityPreventUnmount) mount).shouldPreventStatusBarText(player)) {
						Minecraft.getMinecraft().ingameGUI.setOverlayMessage("", false);
					}
				}
			}
		}
	}
}
