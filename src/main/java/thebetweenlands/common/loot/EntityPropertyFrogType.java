package thebetweenlands.common.loot;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import net.minecraft.entity.Entity;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import thebetweenlands.common.entity.mobs.EntityFrog;
import thebetweenlands.common.lib.ModInfo;

public class EntityPropertyFrogType implements EntityProperty {
	private final int type;

	public EntityPropertyFrogType(int type) {
		this.type = type;
	}

	@Override
	public boolean testProperty(Random random, Entity entity) {
		return entity instanceof EntityFrog && ((EntityFrog)entity).getSkin() == this.type;
	}

	public static class Serializer extends EntityProperty.Serializer<EntityPropertyFrogType> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "frog_type"), EntityPropertyFrogType.class);
		}

		@Override
		public JsonElement serialize(EntityPropertyFrogType property, JsonSerializationContext serializationContext) {
			return new JsonPrimitive(property.type);
		}

		@Override
		public EntityPropertyFrogType deserialize(JsonElement element, JsonDeserializationContext deserializationContext) {
			return new EntityPropertyFrogType(JsonUtils.getInt(element, this.getName().getResourcePath()));
		}
	}
}