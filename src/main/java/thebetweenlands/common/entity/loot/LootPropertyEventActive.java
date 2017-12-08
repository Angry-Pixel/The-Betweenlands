package thebetweenlands.common.entity.loot;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.entity.Entity;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import thebetweenlands.api.environment.EnvironmentEvent;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class LootPropertyEventActive implements EntityProperty {
	private final ResourceLocation event;
	private final boolean active;

	public LootPropertyEventActive(ResourceLocation event, boolean active) {
		this.event = event;
		this.active = active;
	}

	@Override
	public boolean testProperty(Random random, Entity entity) {
		boolean isEventOn = false;
		if(entity.world.provider instanceof WorldProviderBetweenlands) {
			EnvironmentEvent event = ((WorldProviderBetweenlands)entity.world.provider).getEnvironmentEventRegistry().forName(this.event);
			if(event != null && event.isActive()) {
				isEventOn = true;
			}
		}
		return isEventOn == this.active;
	}

	public static class Serializer extends EntityProperty.Serializer<LootPropertyEventActive> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "is_event_active"), LootPropertyEventActive.class);
		}

		@Override
		public JsonElement serialize(LootPropertyEventActive property, JsonSerializationContext serializationContext) {
			JsonObject obj = new JsonObject();
			obj.addProperty("event", property.event.toString());
			obj.addProperty("active", property.active);
			return obj;
		}

		@Override
		public LootPropertyEventActive deserialize(JsonElement element, JsonDeserializationContext deserializationContext) {
			JsonObject obj = JsonUtils.getJsonObject(element, this.getName().getResourcePath());
			return new LootPropertyEventActive(new ResourceLocation(JsonUtils.getString(obj.get("event"), "event")), JsonUtils.getBoolean(obj.get("active"), "active"));
		}
	}
}