package thebetweenlands.common.config.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.ConfigProperty;

public class ItemDecayFoodProperty extends ConfigProperty {
	public static class DecayFoodStats {
		public final int decay;
		public final float saturation;

		public DecayFoodStats(int decay, float saturation) {
			this.decay = decay;
			this.saturation = saturation;
		}
	}

	private final Supplier<String[]> unparsed;

	private Map<String, IntOpenHashSet> itemList;
	private Map<String, DecayFoodStats> stats;

	public ItemDecayFoodProperty(Supplier<String[]> unparsed) {
		this.unparsed = unparsed;
	}

	@Override
	protected void init() {
		this.itemList = new HashMap<>();
		this.stats = new HashMap<>();

		String[] unparsed = this.unparsed.get();
		for(String listed : unparsed) {
			try {
				String[] data = listed.split("/");
				if(data.length == 3) {
					String[] itemData = data[0].split(":");
					String item = itemData[0] + ":" + itemData[1];
					if(this.itemList.get(item) == null || !this.itemList.get(item).contains(OreDictionary.WILDCARD_VALUE)) {
						int meta = 0;
						if(itemData.length > 2) {
							if(!"*".equals(itemData[2])) {
								try {
									meta = Integer.parseInt(itemData[2]);
								} catch(NumberFormatException ex) {
									TheBetweenlands.logger.error("Failed to parse item: " + listed + ". Invalid metadata: " + itemData[2]);
									continue;
								}
							} else {
								meta = OreDictionary.WILDCARD_VALUE;
							}
						}

						int decay;
						float saturation;
						try {
							decay = Integer.parseInt(data[1]);
							saturation = Float.parseFloat(data[2]);
						} catch(NumberFormatException ex) {
							TheBetweenlands.logger.error("Failed to parse item: " + listed + ". Invalid decay or saturation value: " + itemData[2]);
							continue;
						}

						IntOpenHashSet metaSet = this.itemList.get(item);
						if(metaSet == null) {
							this.itemList.put(item, metaSet = new IntOpenHashSet());
						}
						if(meta == OreDictionary.WILDCARD_VALUE) {
							metaSet.clear();
						}
						metaSet.add(meta);

						this.stats.put(item + "#" + meta, new DecayFoodStats(decay, saturation));
					}
				} else {
					TheBetweenlands.logger.error("Failed to parse item: " + listed, ". Invalid syntax");
				}
			} catch (Exception e) {
				TheBetweenlands.logger.error("Failed to parse item: " + listed);
			}
		}
		
		this.setAlwaysEdible();
	}

	@Override
	public void postInitGame() {
		this.setAlwaysEdible();
	}
	
	private void setAlwaysEdible() {
		for(String itemRegName : this.itemList.keySet()) {
			Item item = Item.getByNameOrId(itemRegName);
			if(item instanceof ItemFood) {
				((ItemFood)item).setAlwaysEdible();
			}
		}
	}

	/**
	 * Returns the config decay stats for the specified item, or null if none exists
	 * @param stack
	 * @return
	 */
	@Nullable
	public DecayFoodStats getStats(ItemStack stack) {
		if(stack.isEmpty()) {
			return null;
		}

		ResourceLocation name = stack.getItem().getRegistryName();

		IntOpenHashSet metas = this.itemList.get(name.toString());

		if(metas == null) {
			return null;
		}

		if(metas.contains(OreDictionary.WILDCARD_VALUE)) {
			DecayFoodStats stats = this.stats.get(name.toString() + "#" + OreDictionary.WILDCARD_VALUE);
			if(stats != null) {
				return stats;
			}
		}

		if(metas.contains(stack.getMetadata())) {
			return this.stats.get(name.toString() + "#" + stack.getMetadata());
		}

		return null;
	}
}
