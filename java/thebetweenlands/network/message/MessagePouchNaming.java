package thebetweenlands.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessagePouchNaming implements IMessage, IMessageHandler<MessagePouchNaming, IMessage> {

	public int dimension, entityID;
	public String name;

	public MessagePouchNaming() {}

	public MessagePouchNaming(EntityPlayer player, String string) {
		entityID = player.getEntityId();
		dimension = player.dimension;
		name = string;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityID);
		buf.writeInt(dimension);
		ByteBufUtils.writeUTF8String(buf, name);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityID = buf.readInt();
		dimension = buf.readInt();
		name = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public IMessage onMessage(MessagePouchNaming message, MessageContext ctx) {
		World world = DimensionManager.getWorld(message.dimension);

		if (world == null)
			return null;

		else if (!world.isRemote)
			if (ctx.getServerHandler().playerEntity.getEntityId() == message.entityID) {
				EntityPlayerMP player = ctx.getServerHandler().playerEntity;
				ItemStack stack = player.getHeldItem();
				if (stack != null)
					stack.setStackDisplayName(message.name);
			}
		return null;
	}
}