package thebetweenlands.common.loot;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import thebetweenlands.common.lib.ModInfo;

public class LootConditionFromSharedPool implements LootCondition {
	private final LootCondition[] conditions;
	private final float guaranteedAfterPercentage;
	private final float guaranteedAfter;

	public LootConditionFromSharedPool(LootCondition[] conditions, float guaranteedAfterPercentage, int guaranteedAfter) {
		this.conditions = conditions;
		this.guaranteedAfterPercentage = guaranteedAfterPercentage;
		this.guaranteedAfter = guaranteedAfter;
	}

	@Override
	public boolean testCondition(Random rand, LootContext context) {
		//Returns false if the conditions are met because then the loot entry should
		//not be used in normal non shared loot tables
		return !LootConditionManager.testAllConditions(this.conditions, rand, context);
	}

	public boolean isGuaranteed(float guaranteePercentage, int guaranteeCounter) {
		return (this.guaranteedAfterPercentage >= 0 && this.guaranteedAfterPercentage <= guaranteePercentage) || (this.guaranteedAfter >= 0 && this.guaranteedAfter <= guaranteeCounter);
	}

	/**
	 * Returns whether the loot entry with the specified loot conditions gets items from the shared
	 * pool
	 * @param rand
	 * @param context
	 * @param conditions
	 * @return
	 */
	public static boolean isFromSharedPool(Random rand, LootContext context, List<LootCondition> conditions) {
		return getCondition(rand, context, conditions) != null;
	}

	@Nullable
	public static LootConditionFromSharedPool getCondition(Random rand, LootContext context, List<LootCondition> conditions) {
		for(LootCondition condition : conditions) {
			if(condition instanceof LootConditionFromSharedPool && LootConditionManager.testAllConditions(((LootConditionFromSharedPool) condition).conditions, rand, context)) {
				return (LootConditionFromSharedPool) condition;
			}
		}

		return null;
	}

	public static class Serializer extends LootCondition.Serializer<LootConditionFromSharedPool> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "from_shared_pool"), LootConditionFromSharedPool.class);
		}

		@Override
		public void serialize(JsonObject json, LootConditionFromSharedPool value, JsonSerializationContext context) {
			if(value.conditions.length > 0) {
				json.add("conditions", context.serialize(value.conditions));
			}
		}

		@Override
		public LootConditionFromSharedPool deserialize(JsonObject json, JsonDeserializationContext context) {
			LootCondition[] conditions;

			if(json.has("conditions")) {
				conditions = (LootCondition[])JsonUtils.deserializeClass(json, "conditions", context, LootCondition[].class);
			} else {
				conditions = new LootCondition[0];
			}

			float guaranteedAfterPercentage = -1;

			if(json.has("guaranteed_after_percentage")) {
				guaranteedAfterPercentage = JsonUtils.getFloat(json, "guaranteed_after_percentage");
			}

			int guaranteedAfter = -1;

			if(json.has("guaranteed_after")) {
				guaranteedAfter = JsonUtils.getInt(json, "guaranteed_after");
			}

			return new LootConditionFromSharedPool(conditions, guaranteedAfterPercentage, guaranteedAfter);
		}
	}
}