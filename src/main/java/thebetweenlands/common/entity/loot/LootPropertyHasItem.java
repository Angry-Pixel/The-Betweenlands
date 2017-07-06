package thebetweenlands.common.entity.loot;

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
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.common.lib.ModInfo;

public class LootPropertyHasItem implements EntityProperty {
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

	public LootPropertyHasItem(boolean hasItem, boolean combineStacks, ItemStack item, StackSizeMatcher sizeMatcher, boolean held, boolean armor, boolean main) {
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
			for(int i = 0; i < inv.size(); i++) {
				ItemStack stack = inv.get(i);
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
		if(this.combineStacks && amount != -1) {
			if(this.sizeMatcher.matcher.apply(this.item.getCount(), amount + 1)) {
				return this.hasItem;
			}
		}
		return !this.hasItem;
	}

	private boolean doesItemMatch(ItemStack stack) {
		if(this.item == null && stack == null) {
			return true;
		}
		if(stack == null) {
			return false;
		}
		return stack.getItem() == this.item.getItem() && (this.item.getItemDamage() == OreDictionary.WILDCARD_VALUE || this.item.getItemDamage() == stack.getItemDamage());
	}

	private boolean doesSizeMatch(int size) {
		return this.sizeMatcher == null || this.sizeMatcher.matcher.apply(this.item.getCount(), size);
	}

	public static class Serializer extends EntityProperty.Serializer<LootPropertyHasItem> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "has_item"), LootPropertyHasItem.class);
		}

		@Override
		public JsonElement serialize(LootPropertyHasItem property, JsonSerializationContext serializationContext) {
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
		public LootPropertyHasItem deserialize(JsonElement element, JsonDeserializationContext deserializationContext) {
			JsonObject obj = element.getAsJsonObject();
			boolean hasItem = obj.get("has_item").getAsBoolean();
			boolean combineStacks = obj.has("combine_stacks") ? obj.get("combine_stacks").getAsBoolean() : false;
			JsonObject itemJson = obj.get("item").getAsJsonObject();
			String id = itemJson.get("id").getAsString();
			int meta = itemJson.has("meta") ? itemJson.get("meta").getAsInt() : OreDictionary.WILDCARD_VALUE;
			int size = itemJson.has("size") ? itemJson.get("size").getAsInt() : 1;
			ItemStack stack = new ItemStack(Item.getByNameOrId(id), size, meta);
			boolean held = false, armor = false, main = false;
			JsonArray inventories = obj.get("inventories").getAsJsonArray();
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
				String sizeOperator = obj.get("size_operator").getAsString();
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
			return new LootPropertyHasItem(hasItem, combineStacks, stack, sizeMatcher, held, armor, main);
		}
	}
}