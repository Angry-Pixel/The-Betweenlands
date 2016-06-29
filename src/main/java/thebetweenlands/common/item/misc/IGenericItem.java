package thebetweenlands.common.item.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IGenericItem {
	/**
	 * Returns the unlocalized name of this generic item
	 * @return
	 */
	String getUnlocalizedName();

	/**
	 * Returns the ID (damage value) of this generic item
	 * @return
	 */
	int getID();

	/**
	 * Returns the item instance of this generic item
	 * @return
	 */
	Item getItem();

	/**
	 * Creates a new stack of this generic item
	 * @param size
	 * @return
	 */
	default ItemStack create(int size) {
		return new ItemStack(this.getItem(), size, this.getID());
	}

	/**
	 * Returns whether the specified stack is a stack of this generic item
	 * @param stack
	 * @return
	 */
	default boolean isItemOf(ItemStack stack) {
		return stack != null && stack.getItem() == this.getItem() && stack.getItemDamage() == this.getID();
	}

	/**
	 * Returns whether the specified item is the same as this item
	 * @param item
	 * @return
	 */
	default boolean isItemEqual(IGenericItem item) {
		return item != null && item.getItem() == this.getItem() && item.getID() == this.getID();
	}

	static Map<Class<? extends Enum>, List<IGenericItem>> TYPE_TO_ITEMS = new HashMap<Class<? extends Enum>, List<IGenericItem>>();

	/**
	 * Returns a list of generic items for the specified type
	 * @param type
	 * @return
	 */
	public static List<IGenericItem> getGenericItemsFromType(Class<? extends Enum> type) {
		List<IGenericItem> genericItems = TYPE_TO_ITEMS.get(type);
		if(genericItems == null) {
			if(!IGenericItem.class.isAssignableFrom(type))
				throw new RuntimeException("Type " + type + " does not implement IGenericItem");
			TYPE_TO_ITEMS.put(type, genericItems = new ArrayList<IGenericItem>());
			for(Object item : type.getEnumConstants())
				genericItems.add((IGenericItem)item);
		}
		return genericItems;
	}

	/**
	 * Returns the generic item for the specified type and ID
	 * @param type
	 * @param id
	 * @return
	 */
	public static IGenericItem getFromID(Class<? extends Enum> type, int id) {
		for(IGenericItem item : getGenericItemsFromType(type)) {
			if(item.getID() == id)
				return item;
		}
		return null;
	}

	/**
	 * Returns the generic item for the specified type and item stack
	 * @param type
	 * @param stack
	 * @return
	 */
	public static IGenericItem getFromStack(Class<? extends Enum> type, ItemStack stack) {
		return stack == null ? null : getFromID(type, stack.getItemDamage());
	}
}
