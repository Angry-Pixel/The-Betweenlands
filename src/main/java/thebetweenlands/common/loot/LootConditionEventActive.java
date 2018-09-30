package thebetweenlands.common.loot;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class LootConditionEventActive implements LootCondition {
	private final ResourceLocation event;
	private final boolean active;

	public LootConditionEventActive(ResourceLocation event, boolean active) {
		this.event = event;
		this.active = active;
	}

	@Override
	public boolean testCondition(Random rand, LootContext context) {
		boolean isEventOn = false;
		if(context.getWorld().provider instanceof WorldProviderBetweenlands) {
			IEnvironmentEvent event = ((WorldProviderBetweenlands)context.getWorld().provider).getEnvironmentEventRegistry().forName(this.event);
			if(event != null && event.isActive()) {
				isEventOn = true;
			}
		}
		return isEventOn == this.active;
	}

	public static class Serializer extends LootCondition.Serializer<LootConditionEventActive> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "is_event_active"), LootConditionEventActive.class);
		}

		@Override
		public void serialize(JsonObject json, LootConditionEventActive value, JsonSerializationContext context) {
			json.addProperty("event", value.event.toString());
			json.addProperty("active", value.active);
		}

		@Override
		public LootConditionEventActive deserialize(JsonObject json, JsonDeserializationContext context) {
			return new LootConditionEventActive(new ResourceLocation(JsonUtils.getString(json, "event")), JsonUtils.getBoolean(json, "active"));
		}
	}
}