package thebetweenlands.common.recipe.misc;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thebetweenlands.api.aspect.DiscoveryContainer;
import thebetweenlands.api.item.IDiscoveryProvider;
import thebetweenlands.common.registries.ItemRegistry;

public class BookMergeRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        int bookCount = 0;
        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if(!stack.isEmpty() && stack.getItem() != ItemRegistry.MANUAL_HL) {
                return false;
            } else if(!stack.isEmpty() && stack.getItem() == ItemRegistry.MANUAL_HL) {
                ++bookCount;
            }
        }
        return bookCount >= 2;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        List<DiscoveryContainer> discoveryContainers = new ArrayList<>();
        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if(!stack.isEmpty() && stack.getItem() == ItemRegistry.MANUAL_HL) {
                IDiscoveryProvider<ItemStack> provider = (IDiscoveryProvider<ItemStack>) ItemRegistry.MANUAL_HL;
                DiscoveryContainer container = provider.getContainer(stack);
                if(container != null)
                    discoveryContainers.add(container);
            }
        }
        ItemStack result = new ItemStack(ItemRegistry.MANUAL_HL, discoveryContainers.size());
        DiscoveryContainer mergedContainer = new DiscoveryContainer((IDiscoveryProvider<ItemStack>) ItemRegistry.MANUAL_HL, result);
        for(DiscoveryContainer container : discoveryContainers)
            mergedContainer.mergeDiscoveries(container);
        return result;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
