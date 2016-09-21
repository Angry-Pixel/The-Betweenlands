package thebetweenlands.common.message.clientbound;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.capability.base.EntityCapability;
import thebetweenlands.common.entity.capability.base.EntityCapabilityHandler;
import thebetweenlands.common.message.BLMessage;

public class MessageSyncEntityCapabilities extends BLMessage {
	private ResourceLocation capability;
	private NBTTagCompound nbt;
	private int entityID;

	public MessageSyncEntityCapabilities() { }

	public MessageSyncEntityCapabilities(EntityCapability<?, ?> entityCapability) {
		this.capability = entityCapability.getID();
		this.entityID = entityCapability.getEntity().getEntityId();
		this.nbt = new NBTTagCompound();
		entityCapability.writeTrackingDataToNBT(this.nbt);
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeString(this.capability.toString());
		buf.writeInt(this.entityID);
		buf.writeNBTTagCompoundToBuffer(this.nbt);
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.capability = new ResourceLocation(buf.readStringFromBuffer(128));
		this.entityID = buf.readInt();
		try {
			this.nbt = buf.readNBTTagCompoundFromBuffer();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Entity getEntity(World world) {
		return world.getEntityByID(this.entityID);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Side.CLIENT) {
			this.handleMessage();
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handleMessage() {
		World world = Minecraft.getMinecraft().theWorld;
		Entity entity = this.getEntity(world);
		if(entity != null) {
			EntityCapability<?, ?> capability = EntityCapabilityHandler.getCapability(this.capability);
			if(capability != null) {
				capability = capability.getEntityCapability(entity);
				capability.readTrackingDataFromNBT(this.nbt);
			}
		}
	}
}
