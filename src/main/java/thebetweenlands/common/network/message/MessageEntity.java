package thebetweenlands.common.network.message;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class MessageEntity extends BLMessage {
	private int entityID;
	private int dimensionId;
	private Entity entity;

	public MessageEntity() { }

	public MessageEntity(Entity entity) {
		this.entityID = entity.getEntityId();
		this.dimensionId = entity.dimension;
		this.entity = entity;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeInt(this.entityID);
		buf.writeInt(this.dimensionId);
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.entityID = buf.readInt();
		this.dimensionId = buf.readInt();
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Side.CLIENT) {
			this.processClient();
		} else {
			World world = DimensionManager.getWorld(this.dimensionId);
			if(world != null) {
				this.entity = world.getEntityByID(this.entityID);
			}
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	private void processClient() {
		this.entity = Minecraft.getMinecraft().theWorld.getEntityByID(this.entityID);
	}

	@Nullable
	public Entity getEntity() {
		return this.entity;
	}
}
