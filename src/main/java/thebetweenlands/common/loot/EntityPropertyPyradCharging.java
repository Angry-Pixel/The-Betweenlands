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
import thebetweenlands.common.entity.mobs.EntityPyrad;
import thebetweenlands.common.lib.ModInfo;

public class EntityPropertyPyradCharging implements EntityProperty {
	private final boolean isCharging;

	public EntityPropertyPyradCharging(boolean isCharging) {
		this.isCharging = isCharging;
	}

	@Override
	public boolean testProperty(Random random, Entity entity) {
		return entity instanceof EntityPyrad && ((EntityPyrad)entity).isCharging() == this.isCharging;
	}

	public static class Serializer extends EntityProperty.Serializer<EntityPropertyPyradCharging> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "pyrad_charging"), EntityPropertyPyradCharging.class);
		}

		@Override
		public JsonElement serialize(EntityPropertyPyradCharging property, JsonSerializationContext serializationContext) {
			return new JsonPrimitive(property.isCharging);
		}

		@Override
		public EntityPropertyPyradCharging deserialize(JsonElement element, JsonDeserializationContext deserializationContext) {
			return new EntityPropertyPyradCharging(JsonUtils.getBoolean(element, this.getName().getResourcePath()));
		}
	}
}