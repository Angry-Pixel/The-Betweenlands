package thebetweenlands.network.message;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thebetweenlands.entities.property.EntityPropertiesDecay;
import thebetweenlands.network.message.base.AbstractMessage;

public class MessageSyncPlayerDecay extends AbstractMessage<MessageSyncPlayerDecay>
{
	private int playerDecay;
	private UUID playerUUID;

	public MessageSyncPlayerDecay() { }

	public MessageSyncPlayerDecay(int decay, UUID playerUUID) {
		this.playerDecay = decay;
		this.playerUUID = playerUUID;
	}

	@Override
	public void onMessageClientSide(MessageSyncPlayerDecay message, EntityPlayer player) {
		EntityPlayer target = message.getPlayer(player.worldObj);
		if(target != null) {
			EntityPropertiesDecay prop = ((EntityPropertiesDecay) target.getExtendedProperties(EntityPropertiesDecay.getId()));
			int prev = prop.decayLevel;
			prop.decayLevel = message.playerDecay;
		}
	}

	@Override
	public void onMessageServerSide(MessageSyncPlayerDecay message, EntityPlayer player) {
		//((EntityPropertiesDecay) player.getExtendedProperties(EntityPropertiesDecay.getId())).decayLevel = message.playerDecay;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.playerDecay = buf.readInt();
		int strLen = buf.readInt();
		byte[] strBytes = new byte[strLen];
		buf.readBytes(strBytes);
		try {
			String uuid = new String(strBytes, "UTF-8");
			this.playerUUID = UUID.fromString(uuid);
		} catch(Exception ex) { }
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.playerDecay);
		String uuid = this.playerUUID.toString();
		byte[] strBytes = uuid.getBytes();
		buf.writeInt(strBytes.length);
		buf.writeBytes(strBytes);
	}

	public EntityPlayer getPlayer(World world) {
		return world.func_152378_a(this.playerUUID);
	}
}
