package thebetweenlands.client.tooltips;

import java.util.List;

import net.minecraft.item.ItemStack;

public final class ExclusionEntryClass extends ExclusionEntry {
	private Class<?> clazz;

	public ExclusionEntryClass(Class<?> clazz, List<String> exclusionList) {
		super(exclusionList);
		this.clazz = clazz;
	}

	public boolean appliesFor(ItemStack itemStack) {
		return clazz.isAssignableFrom(itemStack.getItem().getClass());
	}
}
