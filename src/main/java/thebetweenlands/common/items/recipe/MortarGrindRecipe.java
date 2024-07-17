package thebetweenlands.common.items.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import thebetweenlands.api.recipes.MortarRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public record MortarGrindRecipe(Ingredient input, ItemStack result) implements MortarRecipe {
	@Override
	public boolean matches(SingleRecipeInput input, Level level) {
		return this.input().test(input.item());
	}

	@Override
	public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
		return this.result().copy();
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return this.result();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.MORTAR_GRIND_SERIALIZER.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ItemRegistry.PESTLE.get());
	}

	public static class Serializer implements RecipeSerializer<MortarGrindRecipe> {

		public static final MapCodec<MortarGrindRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(MortarGrindRecipe::input),
			ItemStack.STRICT_CODEC.fieldOf("result").forGetter(MortarGrindRecipe::result)
		).apply(instance, MortarGrindRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, MortarGrindRecipe> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, MortarGrindRecipe::input,
			ItemStack.STREAM_CODEC, MortarGrindRecipe::result,
			MortarGrindRecipe::new);

		@Override
		public MapCodec<MortarGrindRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, MortarGrindRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
