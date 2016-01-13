package thebetweenlands.entities.properties.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import thebetweenlands.entities.properties.EntityProperties;
import thebetweenlands.entities.properties.list.EntityPropertiesAspects.AspectDiscovery.EnumDiscoveryResult;
import thebetweenlands.herblore.aspects.Aspect;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.herblore.aspects.AspectManager.AspectItem;
import thebetweenlands.herblore.aspects.AspectManager.AspectItemEntry;
import thebetweenlands.utils.EnumNBTTypes;
import thebetweenlands.utils.confighandler.ConfigHandler;

public class EntityPropertiesAspects extends EntityProperties<EntityPlayer> {
	public static class AspectDiscovery {
		public static enum EnumDiscoveryResult {
			NONE, NEW, LAST, END;
		}

		public final EnumDiscoveryResult result;
		public final Aspect discovered;

		private AspectDiscovery(EnumDiscoveryResult result, Aspect discovered) {
			this.result = result;
			this.discovered = discovered;
		}
	}

	//Server side only!
	private final Map<AspectItem, Integer> discoveredStaticAspectCounts = new HashMap<AspectItem, Integer>();

	//Client side only!
	private final Map<AspectItem, List<Aspect>> discoveredStaticAspects = new HashMap<AspectItem, List<Aspect>>();

	/**
	 * Returns how many aspects of the specified item are already discovered
	 * @param item
	 * @return
	 */
	private int getDiscoveryCount(AspectItem item) {
		return !this.discoveredStaticAspectCounts.containsKey(item) ? 0 : this.discoveredStaticAspectCounts.get(item).intValue();
	}

	/**
	 * Discovers a new aspect of an item and returns the discovery result
	 * @param manager
	 * @param stack
	 * @return
	 */
	public AspectDiscovery discover(AspectManager manager, AspectItem item) {
		List<Aspect> staticAspects = manager.getStaticAspects(item);
		if(staticAspects.size() == 0) {
			return new AspectDiscovery(EnumDiscoveryResult.NONE, null);
		}
		int discoveryCount = this.getDiscoveryCount(item) + 1;
		if(discoveryCount > staticAspects.size()) {
			return new AspectDiscovery(EnumDiscoveryResult.END, null);
		}
		this.discoveredStaticAspectCounts.put(item, discoveryCount);
		this.sync();
		if(discoveryCount - 1 == staticAspects.size()) {
			return new AspectDiscovery(EnumDiscoveryResult.LAST, staticAspects.get(discoveryCount - 1));
		} else {
			return new AspectDiscovery(EnumDiscoveryResult.NEW, staticAspects.get(discoveryCount - 1));
		}
	}

	/**
	 * Discovers all aspects of all aspect items
	 * @param manager
	 */
	public void discoverAll() {
		for(Entry<AspectItem, List<AspectItemEntry>> e : AspectManager.getRegisteredItems().entrySet()) {
			this.discoveredStaticAspectCounts.put(e.getKey(), e.getValue().size());
		}
		this.sync();
	}

	/**
	 * Removes all discoveries of the specified item
	 * @param item
	 */
	public void resetDiscovery(AspectItem item) {
		if(this.discoveredStaticAspectCounts.containsKey(item) && this.discoveredStaticAspectCounts.get(item) != 0) {
			this.sync();
		}
		this.discoveredStaticAspectCounts.put(item, 0);
	}

	/**
	 * Resets all aspect discoveries of all aspect items
	 */
	public void resetAllDiscovery() {
		this.discoveredStaticAspectCounts.clear();
		this.sync();
	}

	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		NBTTagList discoveryList = new NBTTagList();
		for(Entry<AspectItem, Integer> discovery : this.discoveredStaticAspectCounts.entrySet()) {
			AspectItem item = discovery.getKey();
			int discoveryCount = discovery.getValue();
			NBTTagCompound discoveryEntry = new NBTTagCompound();
			item.writeToNBT(discoveryEntry);
			discoveryEntry.setInteger("discoveryCount", discoveryCount);
			discoveryList.appendTag(discoveryEntry);
		}
		nbt.setTag("discoveries", discoveryList);
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt) {
		this.discoveredStaticAspectCounts.clear();
		NBTTagList discoveryList = nbt.getTagList("discoveries", EnumNBTTypes.NBT_COMPOUND.ordinal());
		int discoveryEntries = discoveryList.tagCount();
		for(int i = 0; i < discoveryEntries; i++) {
			NBTTagCompound discoveryEntry = discoveryList.getCompoundTagAt(i);
			AspectItem item = AspectItem.readFromNBT(discoveryEntry);
			if(item == null) continue;
			int discoveryCount = discoveryEntry.getInteger("discoveryCount");
			this.discoveredStaticAspectCounts.put(item, discoveryCount);
		}
	}

	@Override
	public String getID() {
		return "blPropertyAspects";
	}

	@Override
	public Class<EntityPlayer> getEntityClass() {
		return EntityPlayer.class;
	}

	@Override
	public int getTrackingTime() {
		return 0;
	}

	@Override
	public int getTrackingUpdateTime() {
		return 40;
	}

	/**
	 * Returns the list of all the discovered aspects of the specified item. Server side only!
	 * @param manager
	 * @param item
	 * @return
	 */
	private List<Aspect> getDiscoveredStaticAspects(AspectManager manager, AspectItem item) {
		List<Aspect> discoveredStaticAspects = new ArrayList<Aspect>();
		if(this.discoveredStaticAspectCounts.containsKey(item)) {
			int discoveredAspectCount = this.discoveredStaticAspectCounts.get(item);
			List<Aspect> staticAspects = manager.getStaticAspects(item);
			for(int i = 0; i < staticAspects.size() && i < discoveredAspectCount; i++) {
				discoveredStaticAspects.add(staticAspects.get(i));
			}
		}
		return discoveredStaticAspects;
	}

	@Override
	public boolean saveTrackingSensitiveData(NBTTagCompound nbt) {
		WorldServer blWorld = DimensionManager.getWorld(ConfigHandler.DIMENSION_ID);
		if(blWorld != null) {
			NBTTagList discoveryList = new NBTTagList();
			for(Entry<AspectItem, Integer> discovery : this.discoveredStaticAspectCounts.entrySet()) {
				AspectItem item = discovery.getKey();
				List<Aspect> discoveredAspects = this.getDiscoveredStaticAspects(AspectManager.get(blWorld), item);
				NBTTagCompound discoveryEntry = new NBTTagCompound();
				item.writeToNBT(discoveryEntry);
				NBTTagList aspectList = new NBTTagList();
				for(Aspect aspect : discoveredAspects) {
					aspectList.appendTag(aspect.writeToNBT(new NBTTagCompound()));
				}
				discoveryEntry.setTag("discoveredAspects", aspectList);
				discoveryList.appendTag(discoveryEntry);
			}
			nbt.setTag("discoveries", discoveryList);
		}
		return false;
	}

	@Override
	public void loadTrackingSensitiveData(NBTTagCompound nbt) {
		this.discoveredStaticAspects.clear();
		NBTTagList discoveryList = nbt.getTagList("discoveries", EnumNBTTypes.NBT_COMPOUND.ordinal());
		int discoveryEntries = discoveryList.tagCount();
		for(int i = 0; i < discoveryEntries; i++) {
			NBTTagCompound discoveryEntry = discoveryList.getCompoundTagAt(i);
			AspectItem item = AspectItem.readFromNBT(discoveryEntry);
			if(item == null) continue;
			List<Aspect> aspectList = new ArrayList<Aspect>();
			NBTTagList aspectCompoundList = discoveryEntry.getTagList("discoveredAspects", EnumNBTTypes.NBT_COMPOUND.ordinal());
			int discoveredAspects = aspectCompoundList.tagCount();
			for(int c = 0; c < discoveredAspects; c++) {
				NBTTagCompound aspectCompound = aspectCompoundList.getCompoundTagAt(c);
				Aspect aspect = Aspect.readFromNBT(aspectCompound);
				aspectList.add(aspect);
			}
			this.discoveredStaticAspects.put(item, aspectList);
		}
	}

	/**
	 * Returns a list of all discovered (static) aspects of the specified item stack
	 * @param stack
	 * @return
	 */
	public List<Aspect> getDiscoveredStaticAspects(ItemStack stack) {
		List<Aspect> discoveredAspects = this.discoveredStaticAspects.get(new AspectItem(stack));
		if(discoveredAspects == null) {
			discoveredAspects = new ArrayList<Aspect>();
		}
		return discoveredAspects;
	}
}
