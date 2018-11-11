package thebetweenlands.common.loot;

import java.util.Map;
import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import net.minecraft.entity.Entity;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import thebetweenlands.api.entity.IEntityWithLootModifier;
import thebetweenlands.common.lib.ModInfo;

public class EntityPropertyLootModifier implements EntityProperty {
	private final float min, max;
	private final String key;
	private final boolean invert;

	public EntityPropertyLootModifier(String key, float min, float max, boolean invert) {
		this.key = key;
		this.min = min;
		this.max = max;
		this.invert = invert;
	}

	@Override
	public boolean testProperty(Random random, Entity entity) {
		if(entity instanceof IEntityWithLootModifier) {
			Map<String, Float> map = ((IEntityWithLootModifier)entity).getLootModifiers(null, true);
			if(map != null && map.containsKey(this.key)) {
				float value = map.get(this.key);
				return (value >= this.min && value <= this.max) == !this.invert;
			}
		}
		return false;
	}

	public static class Serializer extends EntityProperty.Serializer<EntityPropertyLootModifier> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "loot_modifier"), EntityPropertyLootModifier.class);
		}

		@Override
		public JsonElement serialize(EntityPropertyLootModifier property, JsonSerializationContext serializationContext) {
			JsonObject obj = new JsonObject();
			obj.add("min", new JsonPrimitive(property.min));
			obj.add("max", new JsonPrimitive(property.max));
			obj.add("key", new JsonPrimitive(property.key));
			obj.add("invert", new JsonPrimitive(property.invert));
			return obj;
		}

		@Override
		public EntityPropertyLootModifier deserialize(JsonElement element, JsonDeserializationContext deserializationContext) {
			JsonObject obj = JsonUtils.getJsonObject(element, this.getName().getResourcePath());
			float min = obj.has("min") ? JsonUtils.getFloat(obj.get("min"), "min") : -Float.MAX_VALUE;
			float max = obj.has("max") ? JsonUtils.getFloat(obj.get("max"), "max") : Float.MAX_VALUE;
			String key = JsonUtils.getString(obj.get("key"), "key");
			boolean invert = obj.has("invert") ? JsonUtils.getBoolean(obj.get("invert"), "invert") : false;
			return new EntityPropertyLootModifier(key, min, max, invert);
		}
	}
}