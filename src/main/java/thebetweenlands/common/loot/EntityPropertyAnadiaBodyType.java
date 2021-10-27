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
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.lib.ModInfo;

public class EntityPropertyAnadiaBodyType implements EntityProperty {
	private final int type;

	public EntityPropertyAnadiaBodyType(int type) {
		this.type = type;
	}

	@Override
	public boolean testProperty(Random random, Entity entity) {
		return entity instanceof EntityAnadia && ((EntityAnadia)entity).getBodyType().ordinal() == this.type;
	}

	public static class Serializer extends EntityProperty.Serializer<EntityPropertyAnadiaBodyType> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "anadia_body_type"), EntityPropertyAnadiaBodyType.class);
		}

		@Override
		public JsonElement serialize(EntityPropertyAnadiaBodyType property, JsonSerializationContext serializationContext) {
			return new JsonPrimitive(property.type);
		}

		@Override
		public EntityPropertyAnadiaBodyType deserialize(JsonElement element, JsonDeserializationContext deserializationContext) {
			return new EntityPropertyAnadiaBodyType(JsonUtils.getInt(element, this.getName().getPath()));
		}
	}
}