package thebetweenlands.common.loot;

import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;

import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import thebetweenlands.common.lib.ModInfo;

public class LootConditionOr implements LootCondition {
	//From LootTableManager#GSON_INSTANCE
	public static final Gson GSON_INSTANCE = (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer()).registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new LootEntry.Serializer()).registerTypeHierarchyAdapter(LootFunction.class, new LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(LootCondition.class, new LootConditionManager.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();

	private final LootCondition[] conditions;

	public LootConditionOr(LootCondition[] conditions) {
		this.conditions = conditions;
	}

	@Override
	public boolean testCondition(Random rand, LootContext context) {
		for(LootCondition condition : conditions) {
			if(condition.testCondition(rand, context)) {
				return true;
			}
		}
		return false;
	}

	public static class Serializer extends LootCondition.Serializer<LootConditionOr> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "or"), LootConditionOr.class);
		}

		@Override
		public void serialize(JsonObject json, LootConditionOr value, JsonSerializationContext context) {
			JsonArray arr = new JsonArray();
			for(LootCondition condition : value.conditions) {
				arr.add(new JsonParser().parse(GSON_INSTANCE.toJson(condition)));
			}
			json.add("conditions", arr);
		}

		@Override
		public LootConditionOr deserialize(JsonObject json, JsonDeserializationContext context) {
			JsonArray arr = JsonUtils.getJsonArray(json, "conditions");
			LootCondition[] conditions = new LootCondition[arr.size()];
			int i = 0;
			for(JsonElement element : arr) {
				JsonObject conditionJson = JsonUtils.getJsonObject(element, "condition");
				conditions[i++] = GSON_INSTANCE.fromJson(conditionJson, LootCondition.class);
			}
			return new LootConditionOr(conditions);
		}
	}
}