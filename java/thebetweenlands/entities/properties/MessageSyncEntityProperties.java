package thebetweenlands.entities.properties;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import thebetweenlands.network.message.base.AbstractMessage;

public class MessageSyncEntityProperties extends AbstractMessage<MessageSyncEntityProperties> {
	private String propertyID;
	private NBTTagCompound nbt;
	private int entityID;

	public MessageSyncEntityProperties() { }

	public MessageSyncEntityProperties(EntityProperties prop, Entity entity) {
		this.propertyID = prop.getID();
		NBTTagCompound trackingData = new NBTTagCompound();
		prop.saveTrackingSensitiveData(trackingData);
		this.nbt = trackingData;
		this.entityID = entity.getEntityId();
	}

	@Override
	public void onMessageClientSide(MessageSyncEntityProperties message, EntityPlayer player) {
		Entity target = message.getEntity(player.worldObj);
		if(target != null) {
			IExtendedEntityProperties prop = target.getExtendedProperties(message.propertyID);
			if(prop instanceof EntityProperties) {
				EntityProperties blProp = (EntityProperties)prop;
				blProp.loadTrackingSensitiveData(message.nbt);
			}
		}
	}

	@Override
	public void onMessageServerSide(MessageSyncEntityProperties message, EntityPlayer player) { }

	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			PacketBuffer packetBuffer = new PacketBuffer(buf);
			this.propertyID = packetBuffer.readStringFromBuffer(40);
			this.nbt = packetBuffer.readNBTTagCompoundFromBuffer();
			this.entityID = packetBuffer.readInt();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		try {
			PacketBuffer packetBuffer = new PacketBuffer(buf);
			packetBuffer.writeStringToBuffer(this.propertyID);
			packetBuffer.writeNBTTagCompoundToBuffer(this.nbt);
			packetBuffer.writeInt(this.entityID);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public Entity getEntity(World world) {
		return world.getEntityByID(this.entityID);
	}
}
