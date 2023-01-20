package thebetweenlands.common.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IGenericItem {
	/**
	 * Returns the unlocalized name of this generic item
	 * @return
	 */
	String getTranslationKey();

	/**
	 * Returns the model name of this generic item
	 * @return
	 */
	String getModelName();

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
		return !stack.isEmpty() && stack.getItem() == this.getItem() && stack.getItemDamage() == this.getID();
	}

	/**
	 * Returns whether the specified item is the same as this item
	 * @param item
	 * @return
	 */
	default boolean isItemEqual(IGenericItem item) {
		return item != null && item.getItem() == this.getItem() && item.getID() == this.getID();
	}

	static class TypeContainer {
		private final Map<Class<? extends Enum<?>>, List<IGenericItem>> typeToItems = new HashMap<Class<? extends Enum<?>>, List<IGenericItem>>();
	}
	static TypeContainer TYPE_CONTAINER = new TypeContainer();
	
	/**
	 * Returns a list of generic items for the specified type
	 * @param type
	 * @return
	 */
	public static <X extends Enum<?> & IGenericItem> List<IGenericItem> getGenericItems(Class<? extends X> type) {
		List<IGenericItem> genericItems = TYPE_CONTAINER.typeToItems.get(type);
		if(genericItems == null) {
			if(!IGenericItem.class.isAssignableFrom(type))
				throw new RuntimeException("Type " + type + " does not implement IGenericItem");
			TYPE_CONTAINER.typeToItems.put(type, genericItems = new ArrayList<IGenericItem>());
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
	@Nullable
	public static <X extends Enum<?> & IGenericItem> IGenericItem getFromID(Class<? extends X> type, int id) {
		for(IGenericItem item : getGenericItems(type)) {
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
	@Nullable
	public static <X extends Enum<?> & IGenericItem> IGenericItem getFromStack(Class<? extends X> type, ItemStack stack) {
		return stack.isEmpty() ? null : getFromID(type, stack.getItemDamage());
	}
}
