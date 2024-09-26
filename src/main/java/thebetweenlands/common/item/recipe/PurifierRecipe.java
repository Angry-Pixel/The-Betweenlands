package thebetweenlands.common.item.recipe;

import com.mojang.serialization.Codec;
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
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public record PurifierRecipe(Ingredient input, ItemStack result, int purifyingTime, int requiredWater) implements Recipe<SingleRecipeInput> {
	@Override
	public boolean matches(SingleRecipeInput input, Level level) {
		return this.input().test(input.item());
	}

	@Override
	public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
		return this.result().copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return this.result();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.PURIFIER_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeRegistry.PURIFIER_RECIPE.get();
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(Ingredient.EMPTY, this.input());
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(BlockRegistry.PURIFIER.get());
	}

	public static class Serializer implements RecipeSerializer<PurifierRecipe> {

		public static final MapCodec<PurifierRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(PurifierRecipe::input),
			ItemStack.STRICT_CODEC.fieldOf("result").forGetter(PurifierRecipe::result),
			Codec.INT.fieldOf("purifying_time").forGetter(PurifierRecipe::purifyingTime),
			Codec.INT.fieldOf("water_needed").forGetter(PurifierRecipe::requiredWater)
		).apply(instance, PurifierRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, PurifierRecipe> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, PurifierRecipe::input,
			ItemStack.STREAM_CODEC, PurifierRecipe::result,
			ByteBufCodecs.INT, PurifierRecipe::purifyingTime,
			ByteBufCodecs.INT, PurifierRecipe::requiredWater,
			PurifierRecipe::new);

		@Override
		public MapCodec<PurifierRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, PurifierRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
