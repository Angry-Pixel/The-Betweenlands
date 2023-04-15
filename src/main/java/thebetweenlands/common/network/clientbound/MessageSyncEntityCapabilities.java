package thebetweenlands.common.network.clientbound;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.capability.base.EntityCapabilityHandler;
import thebetweenlands.common.network.MessageEntity;

public class MessageSyncEntityCapabilities extends MessageEntity {
	private ResourceLocation capability;
	private NBTTagCompound nbt;

	public MessageSyncEntityCapabilities() { }

	public MessageSyncEntityCapabilities(EntityCapability<?, ?, ?> entityCapability) {
		this.capability = entityCapability.getID();
		this.addEntity(entityCapability.getEntity());
		this.nbt = new NBTTagCompound();
		entityCapability.writeTrackingDataToNBT(this.nbt);
	}

	@Override
	public void serialize(PacketBuffer buf) throws IOException {
		super.serialize(buf);
		buf.writeString(this.capability.toString());
		buf.writeCompoundTag(this.nbt);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		super.deserialize(buf);
		this.capability = new ResourceLocation(buf.readString(128));
		this.nbt = buf.readCompoundTag();
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		if(ctx.side == Side.CLIENT) {
			this.handleMessage();
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handleMessage() {
		Entity entity = this.getEntity(0);
		if(entity != null) {
			EntityCapability<?, ?, Entity> capability = EntityCapabilityHandler.getCapability(this.capability, entity);
			if(capability != null) {
				capability.readTrackingDataFromNBT(this.nbt);
			}
		}
	}
}
