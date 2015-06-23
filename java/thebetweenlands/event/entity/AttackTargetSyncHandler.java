package thebetweenlands.event.entity;

import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.network.packets.PacketAttackTarget;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class AttackTargetSyncHandler {
	@SubscribeEvent
	public void onLivingSetAttackTargetEvent(LivingSetAttackTargetEvent event) {
		if (!event.entity.worldObj.isRemote) {
			WorldServer world = MinecraftServer.getServer().worldServerForDimension(event.entity.dimension);
			Iterator<EntityPlayer> trackingPlayers = world.getEntityTracker().getTrackingPlayers(event.entity).iterator();
			IMessage message = TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketAttackTarget(event.entity, event.target));
			while (trackingPlayers.hasNext()) {
				TheBetweenlands.networkWrapper.sendTo(message, (EntityPlayerMP) trackingPlayers.next());
			}
			if (event.entity instanceof EntityPlayer) {
				TheBetweenlands.networkWrapper.sendTo(message, (EntityPlayerMP) event.entity);
			}
		}
	}
}
