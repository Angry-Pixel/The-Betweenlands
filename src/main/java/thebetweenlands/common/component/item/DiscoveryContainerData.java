package thebetweenlands.common.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.registry.AspectItem;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.registries.DataComponentRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public record DiscoveryContainerData(Map<ResourceKey<AspectItem>, List<ResourceKey<AspectType>>> discoveredAspects) {

	public static final DiscoveryContainerData EMPTY = new DiscoveryContainerData(Map.of());

	public static final Codec<DiscoveryContainerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.unboundedMap(ResourceKey.codec(BLRegistries.Keys.ASPECT_ITEMS), ResourceKey.codec(BLRegistries.Keys.ASPECT_TYPES).listOf()).fieldOf("discovered_aspects").forGetter(DiscoveryContainerData::discoveredAspects)
	).apply(instance, DiscoveryContainerData::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DiscoveryContainerData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.map(HashMap::new, ResourceKey.streamCodec(BLRegistries.Keys.ASPECT_ITEMS), ResourceKey.streamCodec(BLRegistries.Keys.ASPECT_TYPES).apply(ByteBufCodecs.list())),
		DiscoveryContainerData::discoveredAspects,
		DiscoveryContainerData::new
	);

	private int getDiscoveryCount(ResourceKey<AspectItem> item) {
		return !this.discoveredAspects.containsKey(item) ? 0 : this.discoveredAspects.get(item).size();
	}

	public AspectDiscovery discover(Player player, AspectManager manager, ResourceKey<AspectItem> aspectItem) {
		List<Aspect> staticAspects = manager.getStaticAspects(aspectItem);
		if (staticAspects.isEmpty()) {
			return new AspectDiscovery(AspectDiscovery.DiscoveryResult.NONE, null, false);
		}
		int discoveryCount = this.getDiscoveryCount(aspectItem) + 1;
		if (discoveryCount > staticAspects.size()) {
			return new AspectDiscovery(AspectDiscovery.DiscoveryResult.END, null, false);
		}
		Aspect undiscovered = this.getUndiscoveredAspect(staticAspects, this.discoveredAspects.get(aspectItem));
		DiscoveryContainerData.getWritableDiscoveryContainers(player).forEach(stack -> DiscoveryContainerData.executeDiscoveryAction(stack, data -> data.addDiscovery(aspectItem, undiscovered.type())));
		if (discoveryCount == staticAspects.size()) {
			return new AspectDiscovery(AspectDiscovery.DiscoveryResult.LAST, undiscovered, true);
		} else {
			return new AspectDiscovery(AspectDiscovery.DiscoveryResult.NEW, undiscovered, true);
		}
	}

	public boolean haveDiscoveredAll(AspectManager manager) {
		for (Map.Entry<ResourceKey<AspectItem>, List<Aspect>> e : manager.getMatchedAspects().entrySet()) {
			List<Aspect> staticAspects = manager.getStaticAspects(e.getKey());
			if (this.getDiscoveryCount(e.getKey()) < staticAspects.size())
				return false;
		}
		return true;
	}

	public DiscoveryContainerData discoverAll(AspectManager manager) {
		DiscoveryContainerData data = this;
		for (Map.Entry<ResourceKey<AspectItem>, List<Aspect>> e : manager.getMatchedAspects().entrySet()) {
			for (Aspect a : e.getValue())
				data = this.addDiscovery(e.getKey(), a.type());
		}
		return data;
	}

	public DiscoveryContainerData resetDiscovery(@Nullable ResourceKey<AspectItem> item) {
		var mapCopy = new HashMap<>(this.discoveredAspects);
		mapCopy.remove(item);
		return new DiscoveryContainerData(mapCopy);
	}

	public DiscoveryContainerData resetAllDiscoveries() {
		return EMPTY;
	}

	public DiscoveryContainerData addDiscovery(ResourceKey<AspectItem> item, Holder<AspectType> discovered) {
		var mapCopy = new HashMap<>(this.discoveredAspects);
		List<ResourceKey<AspectType>> discoveredAspects = mapCopy.computeIfAbsent(item, k -> new ArrayList<>());
		if (!discoveredAspects.contains(discovered.getKey()))
			discoveredAspects.add(discovered.getKey());
		return new DiscoveryContainerData(mapCopy);
	}

	@Nullable
	private Aspect getUndiscoveredAspect(List<Aspect> all, @Nullable List<ResourceKey<AspectType>> discovered) {
		if (discovered == null) {
			return all.isEmpty() ? null : all.getFirst();
		}
		for (Aspect a : all) {
			if (!discovered.contains(a.type().getKey()))
				return a;
		}
		return null;
	}

	public DiscoveryContainerData mergeDiscoveries(DiscoveryContainerData other) {
		var mapCopy = new HashMap<>(this.discoveredAspects);
		for (var entry : other.discoveredAspects.entrySet()) {
			ResourceKey<AspectItem> otherItem = entry.getKey();
			List<ResourceKey<AspectType>> otherTypes = entry.getValue();
			if (!mapCopy.containsKey(otherItem)) {
				mapCopy.put(otherItem, otherTypes);
			} else {
				List<ResourceKey<AspectType>> aspectTypes = mapCopy.get(otherItem);
				for (ResourceKey<AspectType> otherType : otherTypes) {
					if (!aspectTypes.contains(otherType)) {
						aspectTypes.add(otherType);
					}
				}
			}
		}
		return new DiscoveryContainerData(mapCopy);
	}

	public List<Aspect> getDiscoveredStaticAspects(AspectManager manager, @Nullable ResourceKey<AspectItem> item) {
		List<Aspect> discoveredStaticAspects = new ArrayList<>();
		if (this.discoveredAspects.containsKey(item)) {
			List<ResourceKey<AspectType>> discoveredAspects = this.discoveredAspects.get(item);
			List<Aspect> staticAspects = manager.getStaticAspects(item);
			for (Aspect a : staticAspects) {
				if (discoveredAspects.contains(a.type().getKey()))
					discoveredStaticAspects.add(a);
			}
		}
		return discoveredStaticAspects;
	}

	public static boolean hasDiscoveryProvider(Player player) {
		Inventory inventory = player.getInventory();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (!stack.isEmpty() && stack.has(DataComponentRegistry.DISCOVERY_DATA))
				return true;
		}
		return false;
	}

	public static List<ItemStack> getWritableDiscoveryContainers(Player player) {
		List<ItemStack> containerList = new ArrayList<>();
		Inventory inventory = player.getInventory();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (!stack.isEmpty() && stack.has(DataComponentRegistry.DISCOVERY_DATA)) {
				containerList.add(stack);
			}
		}
		return containerList;
	}

	public static DiscoveryContainerData getMergedDiscoveryContainer(Player player) {
		List<ItemStack> containerList = getWritableDiscoveryContainers(player);
		DiscoveryContainerData merged = DiscoveryContainerData.EMPTY;
		for (ItemStack container : containerList) {
			if (container != null) {
				merged = merged.mergeDiscoveries(container.getOrDefault(DataComponentRegistry.DISCOVERY_DATA, EMPTY));
			}
		}
		return merged;
	}

	public static void addDiscoveryToContainers(Player player, ResourceKey<AspectItem> item, Holder<AspectType> type) {
		List<ItemStack> discoveryContainers = getWritableDiscoveryContainers(player);
		for (ItemStack container : discoveryContainers) {
			DiscoveryContainerData.executeDiscoveryAction(container, data -> data.addDiscovery(item, type));
		}
	}

	public static void executeDiscoveryAction(ItemStack stack, Function<DiscoveryContainerData, DiscoveryContainerData> action) {
		DiscoveryContainerData data = stack.getOrDefault(DataComponentRegistry.DISCOVERY_DATA, EMPTY);
		stack.set(DataComponentRegistry.DISCOVERY_DATA, action.apply(data));
	}

	public static class AspectDiscovery {
		public final AspectDiscovery.DiscoveryResult result;
		public final boolean successful;
		@Nullable
		public final Aspect discovered;

		private AspectDiscovery(AspectDiscovery.DiscoveryResult result, @Nullable Aspect discovered, boolean successful) {
			this.result = result;
			this.discovered = discovered;
			this.successful = successful;
		}

		public enum DiscoveryResult {
			NONE, NEW, LAST, END
		}
	}
}
