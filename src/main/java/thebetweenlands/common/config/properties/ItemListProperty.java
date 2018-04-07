package thebetweenlands.common.config.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.ConfigProperty;

public class ItemListProperty extends ConfigProperty {
	private final Supplier<String[]> unparsed;

	private Map<String, IntOpenHashSet> itemList;

	public ItemListProperty(Supplier<String[]> unparsed) {
		this.unparsed = unparsed;
	}

	@Override
	protected void init() {
		this.itemList = new HashMap<>();

		String[] unparsed = this.unparsed.get();
		for(String listed : unparsed) {
			try {
				String[] data = listed.split(":");
				String item = data[0] + ":" + data[1];
				if(this.itemList.get(item) == null || !this.itemList.get(item).contains(OreDictionary.WILDCARD_VALUE)) {
					int meta = 0;
					if(data.length > 2) {
						if(!"*".equals(data[2])) {
							try {
								meta = Integer.parseInt(data[2]);
							} catch(NumberFormatException ex) {
								TheBetweenlands.logger.error("Failed to parse item: " + listed + ". Invalid metadata: " + data[2]);
							}
						} else {
							meta = OreDictionary.WILDCARD_VALUE;
						}
					}
					IntOpenHashSet metaSet = this.itemList.get(item);
					if(metaSet == null) {
						this.itemList.put(item, metaSet = new IntOpenHashSet());
					}
					if(meta == OreDictionary.WILDCARD_VALUE) {
						metaSet.clear();
					}
					metaSet.add(meta);
				}
			} catch (Exception e) {
				TheBetweenlands.logger.error("Failed to parse item: " + listed);
			}
		}
	}

	/**
	 * Returns whether the specified item is listed
	 * @param stack
	 * @return
	 */
	public boolean isListed(ItemStack stack) {
		if(stack.isEmpty()) {
			return false;
		}

		ResourceLocation name = stack.getItem().getRegistryName();

		IntOpenHashSet metas = this.itemList.get(name.toString());

		if(metas == null) {
			return false;
		}

		if(metas.contains(OreDictionary.WILDCARD_VALUE)) {
			return true;
		}

		return metas.contains(stack.getMetadata());
	}
}
