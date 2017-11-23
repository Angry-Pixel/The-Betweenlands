package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.common.item.herblore.ItemAspectVial;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.AdvancedRecipeHelper;

public class RecipeSummonMummy extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting crafter, World world) {
        ItemStack vial = ItemStack.EMPTY;
        ItemStack shimmerstone = ItemStack.EMPTY;
        ItemStack heart = ItemStack.EMPTY;
        ItemStack sludge = ItemStack.EMPTY;
        ItemAspectContainer container = null;
        for (int i = 0; i < crafter.getSizeInventory(); ++i) {
            ItemStack stack = crafter.getStackInSlot(i);
            if(!stack.isEmpty()) {
                if(stack.getItem() instanceof ItemAspectVial && (container = ItemAspectContainer.fromItem(stack)).getAspects().size() == 1) {
                    if(!vial.isEmpty())
                        return false;
                    if(stack.getCount() > 1)
                        return false;
                    if(!container.getAspects().get(0).type.equals(AspectRegistry.ARMANIIS) || container.getAspects().get(0).amount < 100) {
                        return false;
                    }
                    vial = stack;
                } else if(stack.getItem() == ItemRegistry.SHIMMER_STONE) {
                    if(!shimmerstone.isEmpty())
                        return false;
                    shimmerstone = stack;
                } else if(stack.getItem() == ItemRegistry.SLUDGE_BALL) {
                    if(!sludge.isEmpty())
                        return false;
                    sludge = stack;
                } else if(stack.getItem() instanceof ItemMisc && stack.getItemDamage() == ItemMisc.EnumItemMisc.TAR_BEAST_HEART_ANIMATED.getID()) {
                    if(!heart.isEmpty())
                        return false;
                    heart = stack;
                } else {
                    return false;
                }
            }
        }
        return !vial.isEmpty() && !shimmerstone.isEmpty() && !heart.isEmpty() && !sludge.isEmpty();
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting crafter) {
        ItemStack vial = ItemStack.EMPTY;
        ItemAspectContainer container = null;
        for (int i = 0; i < crafter.getSizeInventory(); ++i) {
            ItemStack stack = crafter.getStackInSlot(i);
            if(!stack.isEmpty()) {
                if(stack.getItem() instanceof ItemAspectVial && (container = ItemAspectContainer.fromItem(stack)).getAspects().size() == 1) {
                    vial = stack;
                }
            }
        }

        Aspect vialAspect = container.getAspects().get(0);
        ItemStack newVial = new ItemStack(ItemRegistry.ASPECT_VIAL, vial.getCount(), vial.getItemDamage());
        ItemAspectContainer newContainer = ItemAspectContainer.fromItem(newVial);
        newContainer.add(vialAspect.type, vialAspect.amount - 100);

        AdvancedRecipeHelper.setContainerItem(vial, newVial, "bait");

        return new ItemStack(ItemRegistry.MUMMY_BAIT);
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 4;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }
}