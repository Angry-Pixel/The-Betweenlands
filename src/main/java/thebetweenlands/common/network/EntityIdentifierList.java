package thebetweenlands.common.network;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import thebetweenlands.common.TheBetweenlands;

public class EntityIdentifierList {
	
	// --- entity identifier class ----
	
	// non-final `entity`
	private static class EntityIdentifier {
		private final int entityID;
		private final ResourceKey<Level> dimensionId;
		private Entity entity;

		private EntityIdentifier(int id, ResourceKey<Level> dim, Entity entity) {
			this.entityID = id;
			this.dimensionId = dim;
			this.entity = entity;
		}
	};

	// --- codec stuff ----
	
	public static final StreamCodec<ByteBuf, EntityIdentifierList> STREAM_CODEC = StreamCodec.<ByteBuf, EntityIdentifierList>of(EntityIdentifierList::encode, EntityIdentifierList::decode);

    public static void encode(ByteBuf buffer, EntityIdentifierList identifierList) {
    	List<EntityIdentifier> entityIdentifiers = identifierList.entityIdentifiers;
    	VarInt.write(buffer, entityIdentifiers.size());
    	
    	StreamCodec<ByteBuf, ResourceKey<Level>> dimensionCodec = ResourceKey.streamCodec(Registries.DIMENSION);
    	
    	for(EntityIdentifier id : entityIdentifiers) {
    		buffer.writeInt(id.entityID);
    		dimensionCodec.encode(buffer, id.dimensionId);
    	}
    }
    
	public static EntityIdentifierList decode(ByteBuf buffer) {
    	int size = VarInt.read(buffer);
    	EntityIdentifierList identifierList = new EntityIdentifierList(size);

    	StreamCodec<ByteBuf, ResourceKey<Level>> dimensionCodec = ResourceKey.streamCodec(Registries.DIMENSION);
    	
    	for(int i = 0; i < size; ++i) {
    		int id = buffer.readInt();
    		ResourceKey<Level> dim = dimensionCodec.decode(buffer);
    		identifierList.entityIdentifiers.add(new EntityIdentifier(id, dim, null));
    	}
    	
    	return identifierList;
    }

	// --- actual class code ----
	
	private final List<EntityIdentifier> entityIdentifiers;
	
	private EntityIdentifierList(int size) {
		entityIdentifiers = new ArrayList<EntityIdentifierList.EntityIdentifier>(size);
	}

	public EntityIdentifierList() {
		entityIdentifiers = new ArrayList<EntityIdentifierList.EntityIdentifier>();
	}

	/**
	 * Returns the number of entities in this list
	 */
    public int size() {
		return entityIdentifiers.size();
	}

	/**
	 * Adds an entity to be written to the message
	 * @param entity
	 */
	public void addEntity(@Nonnull Entity entity) {
		this.entityIdentifiers.add(new EntityIdentifier(entity.getId(), entity.level().dimension(), entity));
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
				return tryGetEntity(id);
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
		Entity entity;
		for(EntityIdentifier id : this.entityIdentifiers) {
			if((entity = tryGetEntity(id)) != null) {
				entities.add(entity);
			}
		}
		return entities;
	}
	
	private Entity tryGetEntity(EntityIdentifier id) {
		if(id.entity == null) {
			Level level = TheBetweenlands.getLevelWorkaround(id.dimensionId);
			if(level != null && id.dimensionId.equals(level.dimension()))
				id.entity = level.getEntity(id.entityID);
		}
		
		return id.entity;
	}
}
