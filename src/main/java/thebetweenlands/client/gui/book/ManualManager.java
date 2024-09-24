package thebetweenlands.client.gui.book;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.registry.AspectItem;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.component.item.DiscoveryContainerData;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.network.serverbound.SetLastPageDataPacket;
import thebetweenlands.common.registries.DataComponentRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class ManualManager {

	public static boolean hasFoundPage(@Nullable String page, AspectManager manager, HolderLookup.Provider registries, ItemStack manual) {
		if (!manual.isEmpty()) {
			DiscoveryContainerData container = manual.getOrDefault(DataComponentRegistry.DISCOVERY_DATA, DiscoveryContainerData.EMPTY);
			ItemStack ingredient = ItemStack.EMPTY;
			for (Holder<AspectItem> item : registries.lookupOrThrow(BLRegistries.Keys.ASPECT_ITEMS).listElements().toList()) {
				if (item.getKey().location().toString().equals(page)) {
					ingredient = new ItemStack(item.value().item());
					break;
				}
			}
			if (!ingredient.isEmpty()) {
				List<Aspect> aspects = AspectContents.getAspectsFromContainer(ingredient, registries, manager, container);
				return !aspects.isEmpty();
			}
			return false;
		}

		return page != null;
	}

	public static boolean isFullyDiscovered(String page, AspectManager manager, HolderLookup.Provider registries, ItemStack manual) {
		if (!manual.isEmpty()) {
			DiscoveryContainerData container = manual.getOrDefault(DataComponentRegistry.DISCOVERY_DATA, DiscoveryContainerData.EMPTY);
			ItemStack ingredient = ItemStack.EMPTY;
			for (Holder<AspectItem> item : registries.lookupOrThrow(BLRegistries.Keys.ASPECT_ITEMS).listElements().toList()) {
				ItemStack itemStack = new ItemStack(item.value().item(), 1);
				if (item.getKey().location().toString().equals(page)) {
					ingredient = itemStack;
					break;
				}
			}
			if (!ingredient.isEmpty()) {
				ResourceKey<AspectItem> aspectItem = AspectManager.getAspectItem(ingredient, registries);
				return aspectItem != null && container.getDiscoveredStaticAspects(manager, aspectItem).size() == manager.getStaticAspects(aspectItem).size();
			}
			return false;
		}

		return false;
	}

	public static void setCurrentPage(@Nullable String category, int pageNumber, ItemStack manual) {
		if (!manual.isEmpty() && category != null) {
			var data = manual.getOrDefault(DataComponentRegistry.DISCOVERY_DATA, DiscoveryContainerData.EMPTY);
			manual.set(DataComponentRegistry.DISCOVERY_DATA, data.setCurrentPage(category, pageNumber));
			PacketDistributor.sendToServer(new SetLastPageDataPacket(category, pageNumber));
		}
	}

	public static int getCurrentPageNumber(ItemStack manual) {
		if (!manual.isEmpty()) {
			return manual.getOrDefault(DataComponentRegistry.DISCOVERY_DATA, DiscoveryContainerData.EMPTY).getPageNumber();
		}
		return -1;
	}

	@Nullable
	public static ManualCategory getCurrentCategory(ItemStack manual) {
		if (!manual.isEmpty()) {
			String category = manual.getOrDefault(DataComponentRegistry.DISCOVERY_DATA, DiscoveryContainerData.EMPTY).getCategory();
			if (category != null) {
				return getCategoryFromString(category);
			}
		}
		return null;
	}

	@Nullable
	public static ManualCategory getCategoryFromString(@Nullable String categoryName) {
		if (categoryName != null) {
			for (ManualCategory category : HerbloreEntryCategory.CATEGORIES) {
				if (category.getName().equals(categoryName)) {
					return category;
				}
			}
		}
		return null;
	}
}
