package thebetweenlands.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import thebetweenlands.herblore.aspects.DiscoveryContainer;
import thebetweenlands.herblore.aspects.IDiscoveryProvider;
import thebetweenlands.items.BLItemRegistry;

public class BookMergeRecipe implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		int bookCount = 0;
		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if(stack != null && stack.getItem() != BLItemRegistry.manualHL) {
				return false;
			} else if(stack != null && stack.getItem() == BLItemRegistry.manualHL) {
				++bookCount;
			}
		}
		return bookCount >= 2;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		List<DiscoveryContainer> discoveryContainers = new ArrayList<DiscoveryContainer>();
		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if(stack != null && stack.getItem() == BLItemRegistry.manualHL) {
				IDiscoveryProvider<ItemStack> provider = (IDiscoveryProvider<ItemStack>) BLItemRegistry.manualHL;
				DiscoveryContainer container = provider.getContainer(stack);
				if(container != null)
					discoveryContainers.add(container);
			}
		}
		ItemStack result = new ItemStack(BLItemRegistry.manualHL, discoveryContainers.size());
		DiscoveryContainer mergedContainer = new DiscoveryContainer((IDiscoveryProvider<ItemStack>) BLItemRegistry.manualHL, result);
		for(DiscoveryContainer container : discoveryContainers)
			mergedContainer.mergeDiscoveries(container);
		return result;
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

}
