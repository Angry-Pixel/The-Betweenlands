package thebetweenlands.common.loot;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import thebetweenlands.common.lib.ModInfo;

public class LootConditionSharedPool implements LootCondition {
	private final LootCondition[] conditions;

	public LootConditionSharedPool(LootCondition[] conditions) {
		this.conditions = conditions;
	}

	@Override
	public boolean testCondition(Random rand, LootContext context) {
		//Pools with this condition should just be ignored in normal loot tables
		return false;
	}

	/**
	 * Returns whether the pool with the specified loot conditions is a shared pool
	 * @param rand
	 * @param context
	 * @param conditions
	 * @return
	 */
	public static boolean isSharedPool(Random rand, LootContext context, LootCondition[] conditions) {
		for(LootCondition condition : conditions) {
			if(condition instanceof LootConditionSharedPool && LootConditionManager.testAllConditions(((LootConditionSharedPool) condition).conditions, rand, context)) {
				return true;
			}
		}

		return false;
	}

	public static class Serializer extends LootCondition.Serializer<LootConditionSharedPool> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "shared_pool"), LootConditionSharedPool.class);
		}

		@Override
		public void serialize(JsonObject json, LootConditionSharedPool value, JsonSerializationContext context) {
			/*JsonArray arr = new JsonArray();
			for(LootCondition condition : value.conditions) {
				arr.add(new JsonParser().parse(LootConditionOr.GSON_INSTANCE.toJson(condition)));
			}
			json.add("conditions", arr);*/

			if (value.conditions.length > 0) {
				json.add("conditions", context.serialize(value.conditions));
			}
		}

		@Override
		public LootConditionSharedPool deserialize(JsonObject json, JsonDeserializationContext context) {
			/*JsonArray arr = JsonUtils.getJsonArray(json, "conditions");
			LootCondition[] conditions = new LootCondition[arr.size()];
			int i = 0;
			for(JsonElement element : arr) {
				JsonObject conditionJson = JsonUtils.getJsonObject(element, "condition");
				conditions[i++] = LootConditionOr.GSON_INSTANCE.fromJson(conditionJson, LootCondition.class);
			}*/

			LootCondition[] conditions;

			if (json.has("conditions")) {
				conditions = (LootCondition[])JsonUtils.deserializeClass(json, "conditions", context, LootCondition[].class);
			} else {
				conditions = new LootCondition[0];
			}

			return new LootConditionSharedPool(conditions);
		}
	}
}