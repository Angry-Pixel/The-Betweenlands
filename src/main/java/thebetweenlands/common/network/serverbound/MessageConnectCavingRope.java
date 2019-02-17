package thebetweenlands.common.network.serverbound;

import java.util.List;

import javax.xml.ws.handler.MessageContext;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thebetweenlands.common.entity.EntityRopeNode;
import thebetweenlands.common.network.MessageBase;

public class MessageConnectCavingRope extends MessageBase {
	public MessageConnectCavingRope() { }

	@Override
	public void serialize(PacketBuffer buf) { }

	@Override
	public void deserialize(PacketBuffer buf) { }

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.getServerHandler() != null) {
			EntityPlayer player = ctx.getServerHandler().player;
			List<EntityRopeNode> connectedRopeNodes = player.world.getEntitiesWithinAABB(EntityRopeNode.class, player.getBoundingBox().grow(32, 32, 32), rope -> rope.getNextNodeByUUID() == player);
			if(!connectedRopeNodes.isEmpty()) {
				for(EntityRopeNode rope : connectedRopeNodes) {
					rope.setNextNode(null);
				}
			} else {
				List<EntityRopeNode> unconnectedRopeNodes = player.world.getEntitiesWithinAABB(EntityRopeNode.class, player.getBoundingBox().grow(EntityRopeNode.ROPE_LENGTH, EntityRopeNode.ROPE_LENGTH, EntityRopeNode.ROPE_LENGTH), rope -> rope.getDistance(player) <= EntityRopeNode.ROPE_LENGTH && rope.getNextNodeByUUID() == null);
				if(!unconnectedRopeNodes.isEmpty()) {
					EntityRopeNode closest = null;
					for(EntityRopeNode rope : unconnectedRopeNodes) {
						if(closest == null || rope.getDistance(player) < closest.getDistance(player)) {
							closest = rope;
						}
					}
					closest.setNextNode(player);
				}
			}
		}
		return null;
	}
}
