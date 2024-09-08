package thebetweenlands.common.items.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import thebetweenlands.api.recipes.MortarRecipe;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.herblore.Amounts;
import thebetweenlands.common.items.AspectVialItem;
import thebetweenlands.common.items.DentrothystVialItem;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public class MortarAspectrusRecipe implements MortarRecipe {

	@Override
	public boolean matchesInput(SingleRecipeInput input, ItemStack output, Level level, boolean useInputOnly) {
		if (input.item().is(ItemRegistry.ASPECTRUS_FRUIT) && input.item().has(DataComponentRegistry.ASPECT_CONTENTS)) {
			AspectContents contents = input.item().get(DataComponentRegistry.ASPECT_CONTENTS);

			if (contents.amount() <= Amounts.VIAL && contents.aspect().isPresent()) {
				if (useInputOnly) {
					return true;
				} else if (output.getCount() == 1 && output.getItem() instanceof DentrothystVialItem) {
					return true;
				} else if (!output.isEmpty() && output.getItem() instanceof AspectVialItem) {
					AspectContents outputContents = output.getOrDefault(DataComponentRegistry.ASPECT_CONTENTS, AspectContents.EMPTY);

					if (outputContents.aspect().isEmpty()) {
						return true;
					} else {
						int storedAmount = outputContents.amount();
						return outputContents.aspect().get().is(contents.aspect().get()) && storedAmount + contents.amount() <= Amounts.VIAL;
					}
				}
			}
		}

		return false;
	}

	@Override
	public ItemStack getOutput(SingleRecipeInput input, ItemStack outputStack, HolderLookup.Provider registries) {
		if (input.item().has(DataComponentRegistry.ASPECT_CONTENTS)) {
			AspectContents contents = input.item().get(DataComponentRegistry.ASPECT_CONTENTS);
			if (contents.aspect().isPresent()) {
				if (outputStack.getItem() instanceof DentrothystVialItem vial) {
					return AspectContents.createItemStack(vial.getFullAspectBottle().value(), contents.aspect().get(), contents.amount());
				} else if (outputStack.getItem() instanceof AspectVialItem) {
					AspectContents outputContents = outputStack.getOrDefault(DataComponentRegistry.ASPECT_CONTENTS, AspectContents.EMPTY);
					outputStack.set(DataComponentRegistry.ASPECT_CONTENTS, new AspectContents(contents.aspect().get(), contents.amount() + outputContents.amount()));
					return outputStack;
				}
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
