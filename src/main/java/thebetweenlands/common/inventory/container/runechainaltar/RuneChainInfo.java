package thebetweenlands.common.inventory.container.runechainaltar;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.rune.gui.IRuneLink;

public class RuneChainInfo {
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
	private final Map<Integer, NBTTagCompound> containerData = new HashMap<>();

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

	public void unlinkAll(int runeIndex) {
		this.links.remove(runeIndex);
	}

	public void moveAllLinks(int formRune, int toRune) {
		//First adjust links that point towards the old position
		for(Entry<Integer, Map<Integer, Link>> entry : this.links.entrySet()) {
			Map<Integer, Link> links = entry.getValue();
			for(Link link : links.values()) {
				if(link.outputRune == formRune) {
					link.outputRune = toRune;
				}
			}
		}

		Map<Integer, Link> links = this.links.get(formRune);

		if(links != null) {
			Map<Integer, Link> newPosLinks = this.links.get(toRune);
			if(newPosLinks == null) {
				this.links.put(toRune, newPosLinks = new HashMap<>());
			} else {
				newPosLinks.clear();
			}

			newPosLinks.putAll(links);

			this.links.remove(formRune);
		} else {
			if(this.links.containsKey(toRune)) {
				this.links.remove(toRune);
			}
		}
	}

	public NBTTagCompound getContainerData(int runeIndex) {
		return this.containerData.get(runeIndex);
	}

	public void setContainerData(int runeIndex, NBTTagCompound nbt) {
		this.containerData.put(runeIndex, nbt);
	}

	public void removeContainerData(int runeIndex) {
		this.containerData.remove(runeIndex);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		NBTTagList linksNbt = new NBTTagList();

		for(Entry<Integer, Map<Integer, Link>> linkEntry : this.links.entrySet()) {
			NBTTagCompound linkEntryNbt = new NBTTagCompound();

			linkEntryNbt.setInteger("inputRune", linkEntry.getKey());

			NBTTagList runeLinksNbt = new NBTTagList();

			for(Entry<Integer, Link> runeLinkEntry : linkEntry.getValue().entrySet()) {
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

		for(Entry<Integer, NBTTagCompound> dataEntry : this.containerData.entrySet()) {
			NBTTagCompound dataEntryNbt = new NBTTagCompound();

			dataEntryNbt.setInteger("rune", dataEntry.getKey());
			dataEntryNbt.setTag("data", dataEntry.getValue());

			dataNbt.appendTag(dataEntryNbt);
		}

		nbt.setTag("data", dataNbt);
		
		return nbt;
	}

	public static RuneChainInfo readFromNBT(NBTTagCompound nbt) {
		RuneChainInfo info = new RuneChainInfo();

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

				info.link(inputRune, input, outputRune, output);
			}
		}

		NBTTagList dataNbt = nbt.getTagList("data", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < dataNbt.tagCount(); i++) {
			NBTTagCompound dataEntryNbt = dataNbt.getCompoundTagAt(i);

			int rune = dataEntryNbt.getInteger("rune");
			NBTTagCompound data = dataEntryNbt.getCompoundTag("data");

			info.setContainerData(rune, data);
		}

		return info;
	}
}
