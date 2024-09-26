package thebetweenlands.common.item.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import thebetweenlands.api.recipes.DruidAltarRecipe;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

import java.util.ArrayList;
import java.util.List;

public record DruidAltarAssemblyRecipe(NonNullList<Ingredient> items, ItemStack result, int processTime) implements DruidAltarRecipe {

	@Override
	public boolean matches(MultiStackInput input, Level level) {
		if (input.ingredientCount() != this.items.size()) {
			return false;
		}
		List<ItemStack> nonEmptyItems = new ArrayList<>(input.ingredientCount());
		for (var item : input.items()) {
			if (!item.isEmpty()) {
				nonEmptyItems.add(item);
			}
		}
		return RecipeMatcher.findMatches(nonEmptyItems, this.items) != null;
	}

	@Override
	public ItemStack assemble(MultiStackInput input, HolderLookup.Provider registries) {
		return this.result.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= this.items.size();
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return this.result;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.DRUID_ALTAR_ASSEMBLY_SERIALIZER.get();
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return this.items();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(BlockRegistry.DRUID_ALTAR.get());
	}

	public static class Serializer implements RecipeSerializer<DruidAltarAssemblyRecipe> {

		public static final MapCodec<DruidAltarAssemblyRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(ingredients -> {
				Ingredient[] aingredient = ingredients.toArray(Ingredient[]::new);
				if (aingredient.length == 0) {
					return DataResult.error(() -> "No ingredients for druid altar recipe");
				} else {
					return aingredient.length > 4
						? DataResult.error(() -> "Too many ingredients for druid altar recipe. The maximum is 4")
						: DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
				}
			}, DataResult::success).forGetter(DruidAltarAssemblyRecipe::items),
			ItemStack.STRICT_CODEC.fieldOf("result").forGetter(DruidAltarAssemblyRecipe::result),
			Codec.INT.fieldOf("process_time").forGetter(DruidAltarAssemblyRecipe::processTime)
		).apply(instance, DruidAltarAssemblyRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, DruidAltarAssemblyRecipe> STREAM_CODEC = StreamCodec.of(DruidAltarAssemblyRecipe.Serializer::toNetwork, DruidAltarAssemblyRecipe.Serializer::fromNetwork);

		@Override
		public MapCodec<DruidAltarAssemblyRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, DruidAltarAssemblyRecipe> streamCodec() {
			return STREAM_CODEC;
		}

		private static DruidAltarAssemblyRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
			int i = buffer.readVarInt();
			NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
			nonnulllist.replaceAll(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
			ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
			int time = buffer.readInt();
			return new DruidAltarAssemblyRecipe(nonnulllist, result, time);
		}

		private static void toNetwork(RegistryFriendlyByteBuf buffer, DruidAltarAssemblyRecipe recipe) {
			buffer.writeVarInt(recipe.items().size());

			for (Ingredient ingredient : recipe.items()) {
				Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
			}

			ItemStack.STREAM_CODEC.encode(buffer, recipe.result());
			buffer.writeInt(recipe.processTime());
		}
	}
}
