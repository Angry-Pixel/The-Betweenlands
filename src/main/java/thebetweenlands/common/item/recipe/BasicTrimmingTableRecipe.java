package thebetweenlands.common.item.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import thebetweenlands.api.recipes.TrimmingTableRecipe;
import thebetweenlands.common.registries.RecipeRegistry;

import java.util.Optional;

public record BasicTrimmingTableRecipe(Ingredient input, NonNullList<ItemStack> outputs, Optional<ItemStack> remains) implements TrimmingTableRecipe {
	@Override
	public boolean matches(SingleRecipeInput input, Level level) {
		return this.input().test(input.item());
	}

	@Override
	public NonNullList<ItemStack> assembleRecipe(SingleRecipeInput input, Level level) {
		return this.outputs();
	}

	@Override
	public NonNullList<ItemStack> getResultItems(HolderLookup.Provider registries) {
		return this.outputs();
	}

	@Override
	public ItemStack getRemains() {
		return this.remains().orElse(ItemStack.EMPTY);
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(Ingredient.EMPTY, this.input());
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.TRIMMING_SERIALIZER.get();
	}

	public static class Serializer implements RecipeSerializer<BasicTrimmingTableRecipe> {

		public static final MapCodec<BasicTrimmingTableRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Ingredient.CODEC.fieldOf("input").forGetter(BasicTrimmingTableRecipe::input),
			ItemStack.CODEC.listOf().fieldOf("outputs").flatXmap(ingredients -> {
				ItemStack[] aingredient = ingredients.toArray(ItemStack[]::new);
				if (aingredient.length == 0) {
					return DataResult.error(() -> "No ingredients for trimming table recipe");
				} else {
					return aingredient.length > 3
						? DataResult.error(() -> "Too many ingredients for trimming table recipe. The maximum is 3")
						: DataResult.success(NonNullList.of(ItemStack.EMPTY, aingredient));
				}
			}, DataResult::success).forGetter(BasicTrimmingTableRecipe::outputs),
			ItemStack.STRICT_SINGLE_ITEM_CODEC.optionalFieldOf("remains").forGetter(BasicTrimmingTableRecipe::remains)
		).apply(instance, BasicTrimmingTableRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, BasicTrimmingTableRecipe> STREAM_CODEC = StreamCodec.of(BasicTrimmingTableRecipe.Serializer::toNetwork, BasicTrimmingTableRecipe.Serializer::fromNetwork);

		@Override
		public MapCodec<BasicTrimmingTableRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BasicTrimmingTableRecipe> streamCodec() {
			return STREAM_CODEC;
		}

		private static BasicTrimmingTableRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
			Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
			int i = buffer.readVarInt();
			NonNullList<ItemStack> outputs = NonNullList.withSize(i, ItemStack.EMPTY);
			outputs.replaceAll(ingredient -> ItemStack.STREAM_CODEC.decode(buffer));
			Optional<ItemStack> remains = ItemStack.STREAM_CODEC.apply(ByteBufCodecs::optional).decode(buffer);
			return new BasicTrimmingTableRecipe(input, outputs, remains);
		}

		private static void toNetwork(RegistryFriendlyByteBuf buffer, BasicTrimmingTableRecipe recipe) {
			Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input());
			buffer.writeVarInt(recipe.outputs().size());

			for (ItemStack stack : recipe.outputs()) {
				ItemStack.STREAM_CODEC.encode(buffer, stack);
			}

			ItemStack.STREAM_CODEC.apply(ByteBufCodecs::optional).encode(buffer, recipe.remains);
		}
	}
}
