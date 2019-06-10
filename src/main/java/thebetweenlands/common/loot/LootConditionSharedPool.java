package thebetweenlands.common.loot;

import java.util.List;
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
		//Returns false if the conditions are met because then the loot pool should
		//not be used in normal non shared loot tables
		return !LootConditionManager.testAllConditions(this.conditions, rand, context);
	}

	/**
	 * Returns whether the pool with the specified loot conditions belongs to a shared pool
	 * @param rand
	 * @param context
	 * @param conditions
	 * @return
	 */
	public static boolean isSharedPool(Random rand, LootContext context, List<LootCondition> conditions) {
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
			if(value.conditions.length > 0) {
				json.add("conditions", context.serialize(value.conditions));
			}
		}

		@Override
		public LootConditionSharedPool deserialize(JsonObject json, JsonDeserializationContext context) {
			LootCondition[] conditions;

			if(json.has("conditions")) {
				conditions = (LootCondition[])JsonUtils.deserializeClass(json, "conditions", context, LootCondition[].class);
			} else {
				conditions = new LootCondition[0];
			}

			return new LootConditionSharedPool(conditions);
		}
	}
}