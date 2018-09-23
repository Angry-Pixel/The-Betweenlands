package thebetweenlands.common.loot;

import java.util.Map;
import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import net.minecraft.entity.Entity;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import thebetweenlands.api.entity.IEntityWithLootModifier;
import thebetweenlands.common.lib.ModInfo;

public class LootConditionKilledLootModifier implements LootCondition {
	private final float min, max;
	private final String key;
	private final boolean invert;

	public LootConditionKilledLootModifier(String key, float min, float max, boolean invert) {
		this.key = key;
		this.min = min;
		this.max = max;
		this.invert = invert;
	}

	@Override
	public boolean testCondition(Random rand, LootContext context) {
		Entity entity = context.getLootedEntity();
		if(entity instanceof IEntityWithLootModifier) {
			Map<String, Float> map = ((IEntityWithLootModifier)entity).getLootModifiers(context, false);
			if(map != null && map.containsKey(this.key)) {
				float value = map.get(this.key);
				return (value >= this.min && value <= this.max) == !this.invert;
			}
		}
		return false;
	}

	public static class Serializer extends LootCondition.Serializer<LootConditionKilledLootModifier> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "killed_loot_modifier"), LootConditionKilledLootModifier.class);
		}

		@Override
		public void serialize(JsonObject json, LootConditionKilledLootModifier value, JsonSerializationContext context) {
			json.add("min", new JsonPrimitive(value.min));
			json.add("max", new JsonPrimitive(value.max));
			json.add("key", new JsonPrimitive(value.key));
			json.add("invert", new JsonPrimitive(value.invert));
		}

		@Override
		public LootConditionKilledLootModifier deserialize(JsonObject json, JsonDeserializationContext context) {
			float min = JsonUtils.getFloat(json, "min");
			float max = JsonUtils.getFloat(json, "max");
			String key = JsonUtils.getString(json, "key");
			boolean invert = JsonUtils.getBoolean(json, "invert");
			return new LootConditionKilledLootModifier(key, min, max, invert);
		}
	}
}