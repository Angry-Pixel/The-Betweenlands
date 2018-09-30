package thebetweenlands.common.loot;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.entity.Entity;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class EntityPropertyEventActive implements EntityProperty {
	private final ResourceLocation event;
	private final boolean active;

	public EntityPropertyEventActive(ResourceLocation event, boolean active) {
		this.event = event;
		this.active = active;
	}

	@Override
	public boolean testProperty(Random random, Entity entity) {
		boolean isEventOn = false;
		if(entity.world.provider instanceof WorldProviderBetweenlands) {
			IEnvironmentEvent event = ((WorldProviderBetweenlands)entity.world.provider).getEnvironmentEventRegistry().forName(this.event);
			if(event != null && event.isActiveAt(entity.posX, entity.posY, entity.posZ)) {
				isEventOn = true;
			}
		}
		return isEventOn == this.active;
	}

	public static class Serializer extends EntityProperty.Serializer<EntityPropertyEventActive> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "is_event_active"), EntityPropertyEventActive.class);
		}

		@Override
		public JsonElement serialize(EntityPropertyEventActive property, JsonSerializationContext serializationContext) {
			JsonObject obj = new JsonObject();
			obj.addProperty("event", property.event.toString());
			obj.addProperty("active", property.active);
			return obj;
		}

		@Override
		public EntityPropertyEventActive deserialize(JsonElement element, JsonDeserializationContext deserializationContext) {
			JsonObject obj = JsonUtils.getJsonObject(element, this.getName().getResourcePath());
			return new EntityPropertyEventActive(new ResourceLocation(JsonUtils.getString(obj.get("event"), "event")), JsonUtils.getBoolean(obj.get("active"), "active"));
		}
	}
}