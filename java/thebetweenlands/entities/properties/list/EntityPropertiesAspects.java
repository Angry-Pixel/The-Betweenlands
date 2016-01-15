package thebetweenlands.entities.properties.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import thebetweenlands.entities.properties.EntityProperties;
import thebetweenlands.entities.properties.list.EntityPropertiesAspects.AspectDiscovery.EnumDiscoveryResult;
import thebetweenlands.herblore.aspects.Aspect;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.herblore.aspects.AspectManager.AspectItem;
import thebetweenlands.herblore.aspects.IAspectType;
import thebetweenlands.utils.EnumNBTTypes;

public class EntityPropertiesAspects extends EntityProperties<EntityPlayer> {
	public static class AspectDiscovery {
		public static enum EnumDiscoveryResult {
			NONE, NEW, LAST, END;
		}

		public final EnumDiscoveryResult result;
		public final boolean successful;
		public final Aspect discovered;

		private AspectDiscovery(EnumDiscoveryResult result, Aspect discovered, boolean successful) {
			this.result = result;
			this.discovered = discovered;
			this.successful = successful;
		}
	}

	private final Map<AspectItem, List<IAspectType>> discoveredStaticAspects = new HashMap<AspectItem, List<IAspectType>>();

	/**
	 * Returns how many aspects of the specified item are already discovered
	 * @param item
	 * @return
	 */
	private int getDiscoveryCount(AspectItem item) {
		return !this.discoveredStaticAspects.containsKey(item) ? 0 : this.discoveredStaticAspects.get(item).size();
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
			return new AspectDiscovery(EnumDiscoveryResult.NONE, null, false);
		}
		int discoveryCount = this.getDiscoveryCount(item) + 1;
		if(discoveryCount > staticAspects.size()) {
			return new AspectDiscovery(EnumDiscoveryResult.END, null, false);
		}
		Aspect undiscovered = this.getUndiscoveredAspect(staticAspects, this.discoveredStaticAspects.get(item));
		this.addDiscovery(item, undiscovered.type);
		this.sync();
		if(discoveryCount == staticAspects.size()) {
			return new AspectDiscovery(EnumDiscoveryResult.LAST, undiscovered, true);
		} else {
			return new AspectDiscovery(EnumDiscoveryResult.NEW, undiscovered, true);
		}
	}

	/**
	 * Discovers all aspects of all aspect items
	 * @param manager
	 */
	public void discoverAll(AspectManager manager) {
		for(Entry<AspectItem, List<Aspect>> e : manager.getMatchedAspects().entrySet()) {
			for(Aspect a : e.getValue())
				this.addDiscovery(e.getKey(), a.type);
		}
		this.sync();
	}

	/**
	 * Removes all discoveries of the specified item
	 * @param item
	 */
	public void resetDiscovery(AspectItem item) {
		if(this.discoveredStaticAspects.containsKey(item) && this.discoveredStaticAspects.get(item).size() != 0) {
			this.sync();
		}
		this.discoveredStaticAspects.remove(item);
	}

	/**
	 * Resets all aspect discoveries of all aspect items
	 */
	public void resetAllDiscovery() {
		this.discoveredStaticAspects.clear();
		this.sync();
	}

	private void addDiscovery(AspectItem item, IAspectType discovered) {
		List<IAspectType> discoveredAspects = this.discoveredStaticAspects.get(item);
		if(discoveredAspects == null) {
			this.discoveredStaticAspects.put(item, discoveredAspects = new ArrayList<IAspectType>());
		}
		discoveredAspects.add(discovered);
	}

	private Aspect getUndiscoveredAspect(List<Aspect> all, List<IAspectType> discovered) {
		if(discovered == null) {
			return all.size() == 0 ? null : all.get(0);
		}
		for(Aspect a : all) {
			if(!discovered.contains(a.type))
				return a;
		}
		return null;
	}

	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		NBTTagList discoveryList = new NBTTagList();
		Iterator<Entry<AspectItem, List<IAspectType>>> discoveryIT = this.discoveredStaticAspects.entrySet().iterator();
		while(discoveryIT.hasNext()) {
			Entry<AspectItem, List<IAspectType>> e = discoveryIT.next();
			if(e.getValue() == null || e.getValue().size() == 0) {
				discoveryIT.remove();
				continue;
			}
			NBTTagCompound discoveryEntry = new NBTTagCompound();
			e.getKey().writeToNBT(discoveryEntry);
			NBTTagList aspectListCompound = new NBTTagList();
			for(IAspectType type : e.getValue()) {
				aspectListCompound.appendTag(AspectManager.writeAspectTypeNBT(type, new NBTTagCompound()));
			}
			discoveryEntry.setTag("aspects", aspectListCompound);
			discoveryList.appendTag(discoveryEntry);
		}
		nbt.setTag("discoveries", discoveryList);
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt) {
		this.discoveredStaticAspects.clear();
		NBTTagList discoveryList = nbt.getTagList("discoveries", EnumNBTTypes.NBT_COMPOUND.ordinal());
		int discoveryEntries = discoveryList.tagCount();
		for(int i = 0; i < discoveryEntries; i++) {
			NBTTagCompound discoveryEntry = discoveryList.getCompoundTagAt(i);
			AspectItem item = AspectItem.readFromNBT(discoveryEntry);
			List<IAspectType> aspectTypeList = new ArrayList<IAspectType>();
			NBTTagList aspectListCompound = discoveryEntry.getTagList("aspects", EnumNBTTypes.NBT_COMPOUND.ordinal());
			for(int c = 0; c < aspectListCompound.tagCount(); c++) {
				NBTTagCompound aspectTypeCompound = aspectListCompound.getCompoundTagAt(c);
				aspectTypeList.add(AspectManager.readAspectTypeFromNBT(aspectTypeCompound));
			}
			this.discoveredStaticAspects.put(item, aspectTypeList);
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
	public List<Aspect> getDiscoveredStaticAspects(AspectManager manager, AspectItem item) {
		List<Aspect> discoveredStaticAspects = new ArrayList<Aspect>();
		if(this.discoveredStaticAspects.containsKey(item)) {
			List<IAspectType> discoveredAspects = this.discoveredStaticAspects.get(item);
			List<Aspect> staticAspects = manager.getStaticAspects(item);
			for(Aspect a : staticAspects) {
				if(discoveredAspects.contains(a.type))
					discoveredStaticAspects.add(a);
			}
		}
		return discoveredStaticAspects;
	}

	@Override
	public boolean saveTrackingSensitiveData(NBTTagCompound nbt) {
		this.saveNBTData(nbt);
		return false;
	}

	@Override
	public void loadTrackingSensitiveData(NBTTagCompound nbt) {
		this.loadNBTData(nbt);
	}
}
