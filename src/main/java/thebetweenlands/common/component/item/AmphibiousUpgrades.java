package thebetweenlands.common.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.item.amphibious.AmphibiousArmorUpgrade;
import thebetweenlands.common.registries.AmphibiousArmorUpgradeRegistry;

import java.util.*;

public class AmphibiousUpgrades {

	public static final AmphibiousUpgrades EMPTY = new AmphibiousUpgrades(NonNullList.create());
	public static final Codec<AmphibiousUpgrades> CODEC = AmphibiousUpgrades.UpgradeEntrySlot.CODEC.sizeLimitedListOf(10).xmap(AmphibiousUpgrades::fromSlots, AmphibiousUpgrades::asSlots);
	public static final StreamCodec<RegistryFriendlyByteBuf, AmphibiousUpgrades> STREAM_CODEC = UpgradeEntry.STREAM_CODEC.apply(ByteBufCodecs.list(10)).map(AmphibiousUpgrades::new, o -> o.upgrades);
	private final NonNullList<UpgradeEntry> upgrades;

	private AmphibiousUpgrades(NonNullList<UpgradeEntry> entries) {
		if (entries.size() > 10) {
			throw new IllegalArgumentException("Got " + entries.size() + " upgrade entries, but maximum is 10");
		} else {
			this.upgrades = entries;
		}
	}

	private AmphibiousUpgrades(int size) {
		this(NonNullList.withSize(size, UpgradeEntry.EMPTY));
	}

	private AmphibiousUpgrades(List<UpgradeEntry> entries) {
		this(entries.size());

		for (int i = 0; i < entries.size(); i++) {
			this.upgrades.set(i, entries.get(i));
		}
	}

	private static AmphibiousUpgrades fromSlots(List<UpgradeEntrySlot> slots) {
		OptionalInt optionalint = slots.stream().mapToInt(UpgradeEntrySlot::index).max();
		if (optionalint.isEmpty()) {
			return EMPTY;
		} else {
			AmphibiousUpgrades upgrades = new AmphibiousUpgrades(optionalint.getAsInt() + 1);

			for (UpgradeEntrySlot slot : slots) {
				upgrades.upgrades.set(slot.index(), slot.entry());
			}

			return upgrades;
		}
	}

	public static AmphibiousUpgrades fromEntries(List<UpgradeEntry> items) {
		int i = findLastNonEmptySlot(items);
		if (i == -1) {
			return EMPTY;
		} else {
			AmphibiousUpgrades upgrades = new AmphibiousUpgrades(i + 1);

			for (int j = 0; j <= i; j++) {
				upgrades.upgrades.set(j, items.get(j));
			}

			return upgrades;
		}
	}

	private static int findLastNonEmptySlot(List<UpgradeEntry> items) {
		for (int i = items.size() - 1; i >= 0; i--) {
			if (!items.get(i).stack().isEmpty()) {
				return i;
			}
		}

		return -1;
	}

	private List<UpgradeEntrySlot> asSlots() {
		List<UpgradeEntrySlot> list = new ArrayList<>();

		for (int i = 0; i < this.upgrades.size(); i++) {
			UpgradeEntry entry = this.upgrades.get(i);
			if (!entry.stack().isEmpty()) {
				list.add(new UpgradeEntrySlot(i, entry));
			}
		}

		return list;
	}

	public void copyInto(NonNullList<UpgradeEntry> list) {
		for (int i = 0; i < list.size(); i++) {
			UpgradeEntry itemstack = i < this.upgrades.size() ? this.upgrades.get(i) : UpgradeEntry.EMPTY;
			list.set(i, itemstack);
		}
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else {
			return other instanceof AmphibiousUpgrades otherUpgrades && this.listMatches(this.upgrades, otherUpgrades.upgrades);
		}
	}

	private boolean listMatches(List<UpgradeEntry> list, List<UpgradeEntry> other) {
		if (list.size() != other.size()) {
			return false;
		} else {
			for (int i = 0; i < list.size(); i++) {
				UpgradeEntry entry = list.get(i);
				UpgradeEntry otherEntry = other.get(i);
				if (entry.upgrade() != otherEntry.upgrade() || ItemStack.matches(entry.stack(), otherEntry.stack())) {
					return false;
				}
			}

			return true;
		}
	}

	@Override
	public int hashCode() {
		return this.upgrades.hashCode();
	}

	public int getSlots() {
		return this.upgrades.size();
	}

	public UpgradeEntry getEntryInSlot(int slot) {
		this.validateSlotIndex(slot);
		return this.upgrades.get(slot);
	}

	public ItemStack getStackInSlot(int slot) {
		this.validateSlotIndex(slot);
		return this.upgrades.get(slot).stack().copy();
	}

	public Holder<AmphibiousArmorUpgrade> getUpgradeInSlot(int slot) {
		this.validateSlotIndex(slot);
		return this.upgrades.get(slot).upgrade();
	}

	private void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= this.getSlots()) {
			throw new UnsupportedOperationException("Slot " + slot + " not in valid range - [0," + this.getSlots() + ")");
		}
	}

	public Object2IntMap<Holder<AmphibiousArmorUpgrade>> getAllUniqueUpgradesWithCounts() {
		Object2IntOpenHashMap<Holder<AmphibiousArmorUpgrade>> upgrades = new Object2IntOpenHashMap<>();
		for (UpgradeEntry entry : this.upgrades) {
			if (entry.upgrade() != AmphibiousArmorUpgradeRegistry.NONE.get()) {
				upgrades.merge(entry.upgrade(), 1, Integer::sum);
			}
		}
		return upgrades;
	}

	public Object2IntMap<Item> getAllUniqueStacksWithSlotCounts() {
		Object2IntOpenHashMap<Item> upgrades = new Object2IntOpenHashMap<>();
		for (UpgradeEntry entry : this.upgrades) {
			if (!entry.stack().isEmpty()) {
				upgrades.merge(entry.stack().getItem(), 1, Integer::sum);
			}
		}
		return upgrades;
	}

	public record UpgradeEntry(Holder<AmphibiousArmorUpgrade> upgrade, ItemStack stack) {
		public static UpgradeEntry EMPTY = new UpgradeEntry(AmphibiousArmorUpgradeRegistry.NONE, ItemStack.EMPTY);

		public static final Codec<UpgradeEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				BLRegistries.AMPHIBIOUS_ARMOR_UPGRADES.holderByNameCodec().fieldOf("upgrade").forGetter(UpgradeEntry::upgrade),
				ItemStack.CODEC.fieldOf("item").forGetter(UpgradeEntry::stack))
			.apply(instance, UpgradeEntry::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, UpgradeEntry> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.holderRegistry(BLRegistries.Keys.AMPHIBIOUS_ARMOR_UPGRADES), UpgradeEntry::upgrade,
			ItemStack.OPTIONAL_STREAM_CODEC, UpgradeEntry::stack,
			UpgradeEntry::new
		);
	}

	private record UpgradeEntrySlot(int index, UpgradeEntry entry) {
		public static final Codec<UpgradeEntrySlot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.intRange(0, 255).fieldOf("slot").forGetter(UpgradeEntrySlot::index),
				UpgradeEntry.CODEC.fieldOf("entry").forGetter(UpgradeEntrySlot::entry))
			.apply(instance, UpgradeEntrySlot::new));
	}
}
