package thebetweenlands.common.loot;

import java.util.Iterator;
import java.util.Random;
import java.util.function.BiFunction;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.common.lib.ModInfo;

public class EntityPropertyHasItem implements EntityProperty {
	private final boolean hasItem, combineStacks, held, armor, main;
	private final ItemStack item;
	private final StackSizeMatcher sizeMatcher;

	public static enum StackSizeMatcher {
		GREATER((o, i) -> i > o),
		GREATER_EQ((o, i) -> i >= o),
		LESS((o, i) -> i < o),
		LESS_EQ((o, i) -> i <= o),
		EQUAL((o, i) -> i == o);

		public final BiFunction<Integer, Integer, Boolean> matcher;

		private StackSizeMatcher(BiFunction<Integer, Integer, Boolean> matcher) {
			this.matcher = matcher;
		}
	}

	public EntityPropertyHasItem(boolean hasItem, boolean combineStacks, ItemStack item, StackSizeMatcher sizeMatcher, boolean held, boolean armor, boolean main) {
		this.hasItem = hasItem;
		this.combineStacks = combineStacks;
		this.item = item;
		this.sizeMatcher = sizeMatcher;
		this.held = held;
		this.armor = armor;
		this.main = main;
	}

	@Override
	public boolean testProperty(Random random, Entity entity) {
		int amount = -1;
		if(this.held) {
			Iterator<ItemStack> it = entity.getHeldEquipment().iterator();
			while(it.hasNext()) {
				ItemStack stack = it.next();
				if(this.doesItemMatch(stack)) {
					if(!this.combineStacks) {
						if(this.doesSizeMatch(stack.getCount())) {
							return this.hasItem;
						}
					} else {
						amount++;
					}
				}
			}
		}
		if(this.armor) {
			Iterator<ItemStack> it = entity.getArmorInventoryList().iterator();
			while(it.hasNext()) {
				ItemStack stack = it.next();
				if(this.doesItemMatch(stack)) {
					if(!this.combineStacks) {
						if(this.doesSizeMatch(stack.getCount())) {
							return this.hasItem;
						}
					} else {
						amount++;
					}
				}
			}
		}
		if(this.main && entity instanceof EntityPlayer) {
			NonNullList<ItemStack> inv = ((EntityPlayer) entity).inventory.mainInventory;
			for (ItemStack stack : inv) {
				if (this.doesItemMatch(stack)) {
					if (!this.combineStacks) {
						if (this.doesSizeMatch(stack.getCount())) {
							return this.hasItem;
						}
					} else {
						amount++;
					}
				}
			}
		}
		if(this.combineStacks && amount != -1) {
			if(this.sizeMatcher.matcher.apply(this.item.getCount(), amount + 1)) {
				return this.hasItem;
			}
		}
		return !this.hasItem;
	}

	private boolean doesItemMatch(ItemStack stack) {
		if(this.item.isEmpty() && stack.isEmpty()) {
			return true;
		}
		if(stack.isEmpty()) {
			return false;
		}
		return stack.getItem() == this.item.getItem() && (this.item.getItemDamage() == OreDictionary.WILDCARD_VALUE || this.item.getItemDamage() == stack.getItemDamage());
	}

	private boolean doesSizeMatch(int size) {
		return this.sizeMatcher == null || this.sizeMatcher.matcher.apply(this.item.getCount(), size);
	}

	public static class Serializer extends EntityProperty.Serializer<EntityPropertyHasItem> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "has_item"), EntityPropertyHasItem.class);
		}

		@Override
		public JsonElement serialize(EntityPropertyHasItem property, JsonSerializationContext serializationContext) {
			JsonObject obj = new JsonObject();
			obj.add("has_item", new JsonPrimitive(property.hasItem));
			obj.add("combine_stacks", new JsonPrimitive(property.combineStacks));
			JsonObject itemJson = new JsonObject();
			itemJson.add("id", new JsonPrimitive(property.item.getItem().getRegistryName().toString()));
			if(property.item.getItemDamage() != OreDictionary.WILDCARD_VALUE) {
				itemJson.add("meta", new JsonPrimitive(property.item.getItemDamage()));
			}
			if(property.item.getCount() != 1) {
				itemJson.add("size", new JsonPrimitive(property.item.getCount()));
			}
			obj.add("item", itemJson);
			JsonArray inventories = new JsonArray();
			if(property.held) {
				inventories.add(new JsonPrimitive("held"));
			}
			if(property.armor) {
				inventories.add(new JsonPrimitive("armor"));
			}
			if(property.main) {
				inventories.add(new JsonPrimitive("main"));
			}
			obj.add("inventories", inventories);
			if(property.sizeMatcher != null) {
				switch(property.sizeMatcher) {
				case GREATER:
					obj.add("size_operator", new JsonPrimitive(">"));
					break;
				case GREATER_EQ:
					obj.add("size_operator", new JsonPrimitive(">="));
					break;
				case LESS:
					obj.add("size_operator", new JsonPrimitive("<"));
					break;
				case LESS_EQ:
					obj.add("size_operator", new JsonPrimitive("<="));
					break;
				case EQUAL:
					obj.add("size_operator", new JsonPrimitive("="));
					break;
				}
			}
			return obj;
		}

		@Override
		public EntityPropertyHasItem deserialize(JsonElement element, JsonDeserializationContext deserializationContext) {
			JsonObject obj = JsonUtils.getJsonObject(element, this.getName().getResourcePath());
			boolean hasItem = JsonUtils.getBoolean(obj.get("has_item"), "has_item");
			boolean combineStacks = obj.has("combine_stacks") ? JsonUtils.getBoolean(obj.get("combine_stacks"), "combine_stacks") : false;
			JsonObject itemJson = JsonUtils.getJsonObject(obj.get("item"), "item");
			Item item = JsonUtils.getItem(itemJson.get("id"), "id");
			int meta = itemJson.has("meta") ? JsonUtils.getInt(itemJson.get("meta"), "meta") : OreDictionary.WILDCARD_VALUE;
			int size = itemJson.has("size") ? JsonUtils.getInt(itemJson.get("size"), "size") : 1;
			ItemStack stack = new ItemStack(item, size, meta);
			boolean held = false, armor = false, main = false;
			JsonArray inventories = JsonUtils.getJsonArray(obj.get("inventories"), "inventories");
			for(JsonElement inventoryType : inventories) {
				switch(inventoryType.getAsString()) {
				case "held":
					held = true;
					break;
				case "armor":
					armor = true;
					break;
				case "main":
					main = true;
					break;
				default:
					throw new JsonParseException("Invalid inventory type. Valid inventory types: held, armor, main");
				}
			}
			StackSizeMatcher sizeMatcher = null;
			if(obj.has("size_operator")) {
				String sizeOperator = JsonUtils.getString(obj.get("size_operator"), "size_operator");
				switch(sizeOperator) {
				case ">":
					sizeMatcher = StackSizeMatcher.GREATER;
					break;
				case "<":
					sizeMatcher = StackSizeMatcher.LESS;
					break;
				case ">=":
					sizeMatcher = StackSizeMatcher.GREATER_EQ;
					break;
				case "<=":
					sizeMatcher = StackSizeMatcher.LESS_EQ;
					break;
				case "=":
					sizeMatcher = StackSizeMatcher.EQUAL;
					break;
				default:
					throw new JsonParseException("Invalid size operator. Valid size operators: >, <, <=, >=, =");
				}
			}
			return new EntityPropertyHasItem(hasItem, combineStacks, stack, sizeMatcher, held, armor, main);
		}
	}
}