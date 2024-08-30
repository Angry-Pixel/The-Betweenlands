package thebetweenlands.common.items.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.AspectContainerItem;
import thebetweenlands.api.recipes.MortarRecipe;
import thebetweenlands.common.herblore.Amounts;
import thebetweenlands.common.items.AspectVialItem;
import thebetweenlands.common.items.DentrothystVialItem;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

import java.util.List;

public class MortarAspectrusRecipe implements MortarRecipe {

	@Override
	public boolean matchesInput(SingleRecipeInput input, ItemStack output, Level level, boolean useInputOnly) {
		if(input.item().is(ItemRegistry.ASPECTRUS_FRUIT)) {
			AspectContainerItem inputContainer = AspectContainerItem.fromItem(input.item());

			List<Aspect> inputAspects = inputContainer.getAspects();

			if(inputAspects.size() == 1) {
				Aspect aspect = inputAspects.getFirst();

				if(aspect.amount() <= Amounts.VIAL) {
					if(useInputOnly) {
						return true;
					} else if(output.getCount() == 1 && output.getItem() instanceof DentrothystVialItem) {
						return true;
					} else if(!output.isEmpty() && output.getItem() instanceof AspectVialItem) {
						AspectContainerItem outputContainer = AspectContainerItem.fromItem(output);

						if(outputContainer.isEmpty()) {
							return true;
						} else {
							int storedAmount = outputContainer.get(aspect.type());
							return storedAmount > 0 /*to check whether aspect matches*/ && storedAmount + aspect.amount() <= Amounts.VIAL;
						}
					}
				}
			}
		}

		return false;
	}

	@Override
	public ItemStack getOutput(SingleRecipeInput input, ItemStack outputStack, HolderLookup.Provider registries) {
		AspectContainerItem inputContainer = AspectContainerItem.fromItem(input.item());

		List<Aspect> inputAspects = inputContainer.getAspects();

		if(inputAspects.size() == 1) {
			Aspect aspect = inputAspects.getFirst();

			if(outputStack.getItem() instanceof DentrothystVialItem vial) {
				ItemStack apsectVial = new ItemStack(vial.getFullAspectBottle());
				AspectContainerItem vialContainer = AspectContainerItem.fromItem(apsectVial);
				vialContainer.add(aspect.type(), aspect.amount());
				return apsectVial;
			} else if(outputStack.getItem() instanceof AspectVialItem) {
				AspectContainerItem outputContainer = AspectContainerItem.fromItem(outputStack);
				outputContainer.add(aspect.type(), aspect.amount());
				return outputStack;
			}
		}

		return outputStack;
	}

	@Override
	public boolean matches(SingleRecipeInput input, Level level) {
		return input.item().is(ItemRegistry.ASPECTRUS_FRUIT);
	}

	@Override
	public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public boolean showNotification() {
		return false;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.MORTAR_ASPECTRUS_SERIALIZER.get();
	}

	@Override
	public boolean isOutputUsed(ItemStack output) {
		return output.getItem() instanceof DentrothystVialItem || output.getItem() instanceof AspectVialItem;
	}

	@Override
	public boolean replacesOutput() {
		return true;
	}

	public static class Serializer implements RecipeSerializer<MortarAspectrusRecipe> {

		private static final MortarAspectrusRecipe INSTANCE = new MortarAspectrusRecipe();
		private static final MapCodec<MortarAspectrusRecipe> CODEC = MapCodec.unit(INSTANCE);
		private static final StreamCodec<RegistryFriendlyByteBuf, MortarAspectrusRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);

		@Override
		public MapCodec<MortarAspectrusRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, MortarAspectrusRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
