package thebetweenlands.common.recipe.mortar;

import java.util.List;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.api.recipes.IPestleAndMortarRecipe;
import thebetweenlands.common.herblore.Amounts;
import thebetweenlands.common.registries.ItemRegistry;

public class PestleAndMortarRecipeAspectrus implements IPestleAndMortarRecipe {

	@Override
	public ItemStack getOutput(ItemStack input) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getOutput(ItemStack inputStack, ItemStack outputStack) {
		ItemAspectContainer inputContainer = ItemAspectContainer.fromItem(inputStack);

		List<Aspect> inputAspects = inputContainer.getAspects();

		if(inputAspects.size() == 1) {
			Aspect aspect = inputAspects.get(0);

			if(outputStack.getItem() == ItemRegistry.DENTROTHYST_VIAL) {
				ItemStack vial = new ItemStack(ItemRegistry.ASPECT_VIAL, 1, outputStack.getItemDamage() == 0 ? 0 : 1);
				ItemAspectContainer vialContainer = ItemAspectContainer.fromItem(vial);
				vialContainer.add(aspect.type, aspect.amount);
				return vial;
			} else if(outputStack.getItem() == ItemRegistry.ASPECT_VIAL) {
				ItemAspectContainer outputContainer = ItemAspectContainer.fromItem(outputStack);
				outputContainer.add(aspect.type, aspect.amount);
				return outputStack;
			}
		}

		return outputStack;
	}

	@Override
	public ItemStack getInputs() {
		return new ItemStack(ItemRegistry.ASPECTRUS_FRUIT);
	}

	@Override
	public boolean matchesOutput(ItemStack stack) {
		return false;
	}

	@Override
	public boolean matchesInput(ItemStack stack) {
		return false;
	}

	@Override
	public boolean matchesInput(ItemStack inputStack, ItemStack outputStack, boolean inputOnly) {
		if(inputStack.getItem() == ItemRegistry.ASPECTRUS_FRUIT) {
			ItemAspectContainer inputContainer = ItemAspectContainer.fromItem(inputStack);

			List<Aspect> inputAspects = inputContainer.getAspects();

			if(inputAspects.size() == 1) {
				Aspect aspect = inputAspects.get(0);
				
				if(aspect.amount <= Amounts.VIAL) {
					if(inputOnly) {
						return true;
					} else if(outputStack.getItem() == ItemRegistry.DENTROTHYST_VIAL) {
						return true;
					} else if(outputStack.getItem() == ItemRegistry.ASPECT_VIAL) {
						ItemAspectContainer outputContainer = ItemAspectContainer.fromItem(outputStack);
	
						if(outputContainer.isEmpty()) { 
							return true;
						} else {
							int storedAmount = outputContainer.get(aspect.type);
							return storedAmount > 0 /*to check whether aspect matches*/ && storedAmount + aspect.amount <= Amounts.VIAL;
						}
					}
				}
			}
		}

		return false;
	}

	@Override
	public boolean replacesOutput() {
		return true;
	}

}
