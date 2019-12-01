package thebetweenlands.common.config.properties;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.ConfigProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BlockListProperty extends ConfigProperty {
	private final Supplier<String[]> unparsed;

	private Map<String, IntOpenHashSet> blockList;

	public BlockListProperty(Supplier<String[]> unparsed) {
		this.unparsed = unparsed;
	}

	@Override
	protected void init() {
		this.blockList = new HashMap<>();

		String[] unparsed = this.unparsed.get();
		for(String listed : unparsed) {
			try {
				String[] data = listed.split(":");
				String block = data[0] + ":" + data[1];
				if(this.blockList.get(block) == null || !this.blockList.get(block).contains(OreDictionary.WILDCARD_VALUE)) {
					int meta = 0;
					if(data.length > 2) {
						if(!"*".equals(data[2])) {
							try {
								meta = Integer.parseInt(data[2]);
							} catch(NumberFormatException ex) {
								TheBetweenlands.logger.error("Failed to parse block: " + listed + ". Invalid metadata: " + data[2]);
								continue;
							}
						} else {
							meta = OreDictionary.WILDCARD_VALUE;
						}
					}
					IntOpenHashSet metaSet = this.blockList.get(block);
					if(metaSet == null) {
						this.blockList.put(block, metaSet = new IntOpenHashSet());
					}
					if(meta == OreDictionary.WILDCARD_VALUE) {
						metaSet.clear();
					}
					metaSet.add(meta);
				}
			} catch (Exception e) {
				TheBetweenlands.logger.error("Failed to parse block: " + listed);
			}
		}
	}

	/**
	 * Returns whether the specified item is listed
	 * @param state
	 * @return
	 */
	public boolean isListed(IBlockState state) {
		ResourceLocation name = state.getBlock().getRegistryName();
		IntOpenHashSet metas = this.blockList.get(name.toString());

		if(metas == null) {
			return false;
		}

		if(metas.contains(OreDictionary.WILDCARD_VALUE)) {
			return true;
		}

		return metas.contains(state.getBlock().getMetaFromState(state));
	}
}
