package thebetweenlands.common.items.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import thebetweenlands.api.recipes.SteepingPotRecipe;
import thebetweenlands.common.registries.RecipeRegistry;

public record FluidSteepingPotRecipe(FluidIngredient inputFluid, NonNullList<Ingredient> items, FluidStack result) implements SteepingPotRecipe {

	@Override
	public boolean matches(FluidRecipeInput input, Level level) {
		if (!this.inputFluid().test(input.getFluid())) return false;
		if (input.size() != this.items().size()) {
			return false;
		}
		return input.size() == 1 && this.items().size() == 1
			? this.items().getFirst().test(input.getItem(0))
			: input.stackedContents().canCraft(this, null);
	}

	@Override
	public ItemStack assemble(FluidRecipeInput input, HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= this.items.size() + 1;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public FluidStack getResultFluid(HolderLookup.Provider registries) {
		return this.result();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.FLUID_STEEPING_POT_SERIALIZER.get();
	}

	public static class Serializer implements RecipeSerializer<FluidSteepingPotRecipe> {

		public static final MapCodec<FluidSteepingPotRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			FluidIngredient.CODEC.fieldOf("input_fluid").forGetter(FluidSteepingPotRecipe::inputFluid),
			Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(ingredients -> {
				Ingredient[] aingredient = ingredients.toArray(Ingredient[]::new);
				if (aingredient.length == 0) {
					return DataResult.error(() -> "No ingredients for steeping pot recipe");
				} else {
					return aingredient.length > 4
						? DataResult.error(() -> "Too many ingredients for steeping pot recipe. The maximum is 4")
						: DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
				}
			}, DataResult::success).forGetter(FluidSteepingPotRecipe::items),
			FluidStack.CODEC.fieldOf("result").forGetter(FluidSteepingPotRecipe::result)
		).apply(instance, FluidSteepingPotRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, FluidSteepingPotRecipe> STREAM_CODEC = StreamCodec.of(FluidSteepingPotRecipe.Serializer::toNetwork, FluidSteepingPotRecipe.Serializer::fromNetwork);

		@Override
		public MapCodec<FluidSteepingPotRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, FluidSteepingPotRecipe> streamCodec() {
			return STREAM_CODEC;
		}

		private static FluidSteepingPotRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
			FluidIngredient inputFluid = FluidIngredient.STREAM_CODEC.decode(buffer);
			int i = buffer.readVarInt();
			NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
			nonnulllist.replaceAll(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
			FluidStack result = FluidStack.STREAM_CODEC.decode(buffer);
			return new FluidSteepingPotRecipe(inputFluid, nonnulllist, result);
		}

		private static void toNetwork(RegistryFriendlyByteBuf buffer, FluidSteepingPotRecipe recipe) {
			FluidIngredient.STREAM_CODEC.encode(buffer, recipe.inputFluid());
			buffer.writeVarInt(recipe.items().size());

			for (Ingredient ingredient : recipe.items()) {
				Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
			}

			FluidStack.STREAM_CODEC.encode(buffer, recipe.result());
		}
	}
}
