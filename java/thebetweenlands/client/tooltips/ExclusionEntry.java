package thebetweenlands.client.tooltips;

import java.util.List;

import net.minecraft.item.ItemStack;

public class ExclusionEntry {
	protected List<String> exclusionList;

	public ExclusionEntry(List<String> exclusionList) {
		this.exclusionList = exclusionList;
	}

	public List<String> getExclusionList(ItemStack itemStack) {
		return exclusionList;
	}
}