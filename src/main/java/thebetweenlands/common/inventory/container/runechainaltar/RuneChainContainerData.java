package thebetweenlands.common.inventory.container.runechainaltar;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.rune.IRuneChainContainerData;
import thebetweenlands.api.rune.IRuneLink;

public class RuneChainContainerData implements IRuneChainContainerData {
	public static final class Link implements IRuneLink {
		private int outputRune;
		private int output;

		private Link(int outputRune, int output) {
			this.outputRune = outputRune;
			this.output = output;
		}

		@Override
		public int getOutputRune() {
			return this.outputRune;
		}

		@Override
		public int getOutput() {
			return this.output;
		}
	}

	private final Map<Integer, Map<Integer, Link>> links = new HashMap<>();
	private final Map<Integer, NBTTagCompound> containerNbt = new HashMap<>();
	private final Map<Integer, Integer> configurationIds = new HashMap<>();

	@Override
	public Collection<Integer> getLinkedInputs(int runeIndex) {
		Set<Integer> linkedSlots = new HashSet<>();
		Map<Integer, Link> links = this.links.get(runeIndex);
		if(links != null) {
			for(Entry<Integer, Link> link : links.entrySet()) {
				linkedSlots.add(link.getKey());
			}
		}
		return linkedSlots;
	}

	@Override
	@Nullable
	public Link getLink(int runeIndex, int input) {
		if(input >= 0) {
			Map<Integer, Link> links = this.links.get(runeIndex);
			if(links != null) {
				return links.get(input);
			}
		}
		return null;
	}

	@Override
	public boolean link(int runeIndex, int input, int outputRuneIndex, int output) {
		if(runeIndex <= outputRuneIndex) {
			return false;
		}

		Map<Integer, Link> links = this.links.get(runeIndex);

		if(links == null) {
			this.links.put(runeIndex, links = new HashMap<>());
		}

		links.put(input, new Link(outputRuneIndex, output));

		return true;
	}

	@Override
	@Nullable
	public Link unlink(int runeIndex, int input) {
		Map<Integer, Link> links = this.links.get(runeIndex);
		if(links != null) {
			Link removed = links.remove(input);
			if(links.isEmpty()) {
				this.links.remove(runeIndex);
			}
			return removed;
		}
		return null;
	}

	@Override
	public void unlinkAll(int runeIndex) {
		this.links.remove(runeIndex);
	}

	@Override
	public void unlinkAllIncoming(int runeIndex) {
		Iterator<Entry<Integer, Map<Integer, Link>>> entryIT = this.links.entrySet().iterator();
		while(entryIT.hasNext()) {
			Entry<Integer, Map<Integer, Link>> entry = entryIT.next();

			Iterator<Entry<Integer, Link>> linksIT = entry.getValue().entrySet().iterator();
			while(linksIT.hasNext()) {
				if(linksIT.next().getValue().outputRune == runeIndex) {
					linksIT.remove();
				}
			}

			if(entry.getValue().isEmpty()) {
				entryIT.remove();
			}
		}
	}

	@Override
	public void moveRuneData(int fromRune, int toRune) {
		//First adjust links that point towards the old position
		for(Entry<Integer, Map<Integer, Link>> entry : this.links.entrySet()) {
			Map<Integer, Link> links = entry.getValue();
			for(Link link : links.values()) {
				if(link.outputRune == fromRune) {
					link.outputRune = toRune;
				}
			}
		}

		Map<Integer, Link> links = this.links.get(fromRune);

		if(links != null) {
			Map<Integer, Link> newPosLinks = this.links.get(toRune);
			if(newPosLinks == null) {
				this.links.put(toRune, newPosLinks = new HashMap<>());
			} else {
				newPosLinks.clear();
			}

			newPosLinks.putAll(links);

			this.links.remove(fromRune);
		} else {
			if(this.links.containsKey(toRune)) {
				this.links.remove(toRune);
			}
		}
		
		if(this.containerNbt.containsKey(fromRune)) {
			this.containerNbt.put(toRune, this.containerNbt.get(fromRune));
		}
		
		if(this.configurationIds.containsKey(fromRune)) {
			this.configurationIds.put(toRune, this.configurationIds.get(fromRune));
		}
	}

	@Override
	public NBTTagCompound getContainerNbt(int runeIndex) {
		return this.containerNbt.get(runeIndex);
	}

	@Override
	public void setContainerNbt(int runeIndex, NBTTagCompound nbt) {
		this.containerNbt.put(runeIndex, nbt);
	}

	@Override
	public int getConfigurationId(int runeIndex) {
		Integer id = this.configurationIds.get(runeIndex);
		return id != null ? id : 0;
	}

	@Override
	public boolean hasConfigurationId(int runeIndex) {
		return this.configurationIds.containsKey(runeIndex);
	}

	@Override
	public void setConfigurationId(int runeIndex, int configurationId) {
		this.configurationIds.put(runeIndex, configurationId);
	}

	@Override
	public void removeConfigurationId(int runeIndex) {
		this.configurationIds.remove(runeIndex);
	}

	@Override
	public void removeContainerNbt(int runeIndex) {
		this.containerNbt.remove(runeIndex);
	}

	public static NBTTagCompound writeToNBT(IRuneChainContainerData data, NBTTagCompound nbt) {
		NBTTagList linksNbt = new NBTTagList();

		Map<Integer, Map<Integer, IRuneLink>> links = data.getLinks();

		for(Entry<Integer, Map<Integer, IRuneLink>> linkEntry : links.entrySet()) {
			NBTTagCompound linkEntryNbt = new NBTTagCompound();

			linkEntryNbt.setInteger("inputRune", linkEntry.getKey());

			NBTTagList runeLinksNbt = new NBTTagList();

			for(Entry<Integer, IRuneLink> runeLinkEntry : linkEntry.getValue().entrySet()) {
				NBTTagCompound linkNbt = new NBTTagCompound();

				linkNbt.setInteger("input", runeLinkEntry.getKey());
				linkNbt.setInteger("outputRune", runeLinkEntry.getValue().getOutputRune());
				linkNbt.setInteger("output", runeLinkEntry.getValue().getOutput());

				runeLinksNbt.appendTag(linkNbt);
			}

			linkEntryNbt.setTag("links", runeLinksNbt);

			linksNbt.appendTag(linkEntryNbt);
		}

		nbt.setTag("links", linksNbt);

		NBTTagList dataNbt = new NBTTagList();

		for(Entry<Integer, NBTTagCompound> dataEntry : data.getContainerNbt().entrySet()) {
			NBTTagCompound dataEntryNbt = new NBTTagCompound();

			NBTTagCompound containerNbt = dataEntry.getValue();

			if(containerNbt != null) {
				dataEntryNbt.setInteger("rune", dataEntry.getKey());
				dataEntryNbt.setTag("nbt", containerNbt);

				dataNbt.appendTag(dataEntryNbt);
			}
		}

		nbt.setTag("data", dataNbt);

		NBTTagList configurationsNbt = new NBTTagList();

		for(Entry<Integer, Integer> dataEntry : data.getConfigurationIds().entrySet()) {
			NBTTagCompound configurationEntryNbt = new NBTTagCompound();

			Integer id = dataEntry.getValue();

			if(id != null) {
				configurationEntryNbt.setInteger("rune", dataEntry.getKey());
				configurationEntryNbt.setInteger("configuration", id);

				configurationsNbt.appendTag(configurationEntryNbt);
			}
		}

		nbt.setTag("configurations", configurationsNbt);

		return nbt;
	}

	public static RuneChainContainerData readFromNBT(NBTTagCompound nbt) {
		RuneChainContainerData containerData = new RuneChainContainerData();

		NBTTagList linksNbt = nbt.getTagList("links", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < linksNbt.tagCount(); i++) {
			NBTTagCompound linkEntryNbt = linksNbt.getCompoundTagAt(i);

			int inputRune = linkEntryNbt.getInteger("inputRune");

			NBTTagList runeLinksNbt = linkEntryNbt.getTagList("links", Constants.NBT.TAG_COMPOUND);
			for(int j = 0; j < runeLinksNbt.tagCount(); j++) {
				NBTTagCompound linkNbt = runeLinksNbt.getCompoundTagAt(j);

				int input = linkNbt.getInteger("input");
				int outputRune = linkNbt.getInteger("outputRune");
				int output = linkNbt.getInteger("output");

				containerData.link(inputRune, input, outputRune, output);
			}
		}

		NBTTagList dataNbt = nbt.getTagList("data", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < dataNbt.tagCount(); i++) {
			NBTTagCompound dataEntryNbt = dataNbt.getCompoundTagAt(i);
			containerData.setContainerNbt(dataEntryNbt.getInteger("rune"), dataEntryNbt.getCompoundTag("nbt"));
		}

		NBTTagList configurationsNbt = nbt.getTagList("configurations", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < configurationsNbt.tagCount(); i++) {
			NBTTagCompound configurationEntryNbt = configurationsNbt.getCompoundTagAt(i);
			containerData.setConfigurationId(configurationEntryNbt.getInteger("rune"), configurationEntryNbt.getInteger("configuration"));
		}
		
		return containerData;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer, Map<Integer, Link>> getLinks() {
		return this.links;
	}

	@Override
	public Map<Integer, Integer> getConfigurationIds() {
		return this.configurationIds;
	}

	@Override
	public Map<Integer, NBTTagCompound> getContainerNbt() {
		return this.containerNbt;
	}
}
