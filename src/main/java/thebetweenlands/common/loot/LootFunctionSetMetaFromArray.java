package thebetweenlands.common.loot;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import thebetweenlands.common.lib.ModInfo;

public class LootFunctionSetMetaFromArray extends LootFunction {
	private static final Logger LOGGER = LogManager.getLogger();
	private final int[] meta;

	public LootFunctionSetMetaFromArray(LootCondition[] conditionsIn, int[] meta) {
		super(conditionsIn);
		this.meta = meta;
	}

	@Override
	public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
		if (stack.isItemStackDamageable()) {
			LOGGER.warn("Couldn't set data of loot item {}", (Object)stack);
		} else {
			stack.setItemDamage(this.meta[rand.nextInt(this.meta.length)]);
		}

		return stack;
	}

	public static class Serializer extends LootFunction.Serializer<LootFunctionSetMetaFromArray> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "set_meta_from_array"), LootFunctionSetMetaFromArray.class);
		}

		@Override
		public void serialize(JsonObject object, LootFunctionSetMetaFromArray functionClazz, JsonSerializationContext serializationContext) {
			JsonArray array = new JsonArray();
			for(int m : functionClazz.meta) {
				array.add(m);
			}
			object.add("data", array);
		}

		@Override
		public LootFunctionSetMetaFromArray deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
			JsonArray array = JsonUtils.getJsonArray(object, "data");

			if(array.size() < 2) {
				throw new JsonParseException("The data array must have at least 2 integers");
			}

			int[] meta = new int[array.size()];
			for(int i = 0; i < array.size(); i++) {
				meta[i] = array.get(i).getAsInt();
			}

			return new LootFunctionSetMetaFromArray(conditionsIn, meta);
		}
	}
}