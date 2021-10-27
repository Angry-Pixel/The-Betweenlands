package thebetweenlands.common.loot;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.lib.ModInfo;

public class LootFunctionSetCountFromAnadia extends LootFunction {
	private static final Logger LOGGER = LogManager.getLogger();
	private final float sizeStart, sizeEnd;
	private final int minCount, maxCount;

	public LootFunctionSetCountFromAnadia(LootCondition[] conditionsIn, float sizeStart, float sizeEnd, int minCount, int maxCount) {
		super(conditionsIn);
		this.sizeStart = sizeStart;
		this.sizeEnd = sizeEnd;
		this.minCount = minCount;
		this.maxCount = maxCount;
	}

	@Override
	public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
		Entity entity = context.getLootedEntity();

		if(entity instanceof EntityAnadia) {
			float size = ((EntityAnadia) entity).getFishSize();

			int count = this.minCount + MathHelper.floor((MathHelper.clamp(size, this.sizeStart, this.sizeEnd) - this.sizeStart) / (this.sizeEnd - this.sizeStart) * (this.maxCount - this.minCount));

			if(count > stack.getMaxStackSize()) {
				LOGGER.warn("Stack size {} exceeds max. stack size {} of loot item {}", count, stack.getMaxStackSize(), stack);
			} else {
				stack.setCount(count);
			}
		}

		return stack;
	}

	public static class Serializer extends LootFunction.Serializer<LootFunctionSetCountFromAnadia> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "set_count_from_anadia"), LootFunctionSetCountFromAnadia.class);
		}

		@Override
		public void serialize(JsonObject object, LootFunctionSetCountFromAnadia functionClazz, JsonSerializationContext serializationContext) {
			object.addProperty("size_start", functionClazz.sizeStart);
			object.addProperty("size_end", functionClazz.sizeEnd);
			object.addProperty("min_count", functionClazz.minCount);
			object.addProperty("max_count", functionClazz.maxCount);
		}

		@Override
		public LootFunctionSetCountFromAnadia deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
			return new LootFunctionSetCountFromAnadia(conditionsIn, JsonUtils.getFloat(object, "size_start"), JsonUtils.getFloat(object, "size_end"), JsonUtils.getInt(object, "min_count"), JsonUtils.getInt(object, "max_count"));
		}
	}
}