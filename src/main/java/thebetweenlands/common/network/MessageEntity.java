package thebetweenlands.common.network;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.xml.ws.handler.MessageContext;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class MessageEntity extends MessageBase {
	private static class EntityIdentifier {
		private int entityID, dimensionId;
		private Entity entity;

		private EntityIdentifier(int id, int dim, Entity entity) {
			this.entityID = id;
			this.dimensionId = dim;
			this.entity = entity;
		}
	}

	private List<EntityIdentifier> entityIdentifiers = new ArrayList<EntityIdentifier>();

	public MessageEntity() { }

	/**
	 * Adds an entity to be written to the message
	 * @param entity
	 */
	public void addEntity(Entity entity) {
		this.entityIdentifiers.add(new EntityIdentifier(entity.getEntityId(), entity.dimension, entity));
	}

	/**
	 * Reads an entity from the message
	 * @param index
	 * @return
	 */
	@Nullable
	public Entity getEntity(int index) {
		if(index < this.entityIdentifiers.size()) {
			EntityIdentifier id = this.entityIdentifiers.get(index);
			if(id != null) {
				return id.entity;
			}
		}
		return null;
	}

	/**
	 * Returns a list of all entities from the message
	 * @return
	 */
	public List<Entity> getEntities() {
		List<Entity> entities = new ArrayList<Entity>();
		for(EntityIdentifier id : this.entityIdentifiers) {
			if(id.entity != null) {
				entities.add(id.entity);
			}
		}
		return entities;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeByte(this.entityIdentifiers.size());
		for(EntityIdentifier id : this.entityIdentifiers) {
			buf.writeInt(id.entityID);
			buf.writeInt(id.dimensionId);
		}
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.entityIdentifiers.clear();
		int entities = buf.readByte();
		for(int i = 0; i < entities; i++) {
			this.entityIdentifiers.add(new EntityIdentifier(buf.readInt(), buf.readInt(), null));
		}
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Dist.CLIENT) {
			this.processClient();
		} else {
			for(EntityIdentifier id : this.entityIdentifiers) {
				World world = DimensionManager.getWorld(id.dimensionId);
				if(world != null) {
					id.entity = world.getEntityByID(id.entityID);
				}
			}
		}

		return null;
	}

	@OnlyIn(Dist.CLIENT)
	private void processClient() {
		World world = Minecraft.getInstance().world;
		if(world != null) {
			for(EntityIdentifier id : this.entityIdentifiers) {
				if(id.dimensionId == world.dimension.getDimension()) {
					id.entity = world.getEntityByID(id.entityID);
				}
			}
		}
	}
}
