package thebetweenlands.event.entity;

import java.util.Iterator;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.forgeevent.entity.LivingSetRevengeTargetEvent;
import thebetweenlands.network.base.IPacket;
import thebetweenlands.network.packet.server.PacketAttackTarget;
import thebetweenlands.network.packet.server.PacketRevengeTarget;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class MiscEntitySyncHandler {
	@SubscribeEvent
	public void onLivingSetAttackTargetEvent(LivingSetAttackTargetEvent event) {
		if (!event.entity.worldObj.isRemote) {
			sendToWatchingEntity(event.entity, new PacketAttackTarget(event.entity, event.target));
		}
	}

	@SubscribeEvent
	public void onLivingSetRevengeTargetEvent(LivingSetRevengeTargetEvent event) {
		if (!event.entity.worldObj.isRemote) {
			sendToWatchingEntity(event.entity, new PacketRevengeTarget(event.entity, event.target));
		}
	}

	private void sendToWatchingEntity(Entity entity, IPacket packet) {
		WorldServer world = MinecraftServer.getServer().worldServerForDimension(entity.dimension);
		Iterator<EntityPlayer> trackingPlayers = world.getEntityTracker().getTrackingPlayers(entity).iterator();
		IMessage message = TheBetweenlands.sidedPacketHandler.wrapPacket(packet);
		while (trackingPlayers.hasNext()) {
			TheBetweenlands.networkWrapper.sendTo(message, (EntityPlayerMP) trackingPlayers.next());
		}
		if (entity instanceof EntityPlayer) {
			TheBetweenlands.networkWrapper.sendTo(message, (EntityPlayerMP) entity);
		}
	}
}
