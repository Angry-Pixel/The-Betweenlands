package thebetweenlands.common.items.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.RecipeRegistry;

public record SmokingRackRecipe(Ingredient input, ItemStack result, int smokingTime) implements Recipe<SingleRecipeInput> {

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
		return RecipeRegistry.SMOKING_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeRegistry.SMOKING_RECIPE.get();
	}

	public static class Serializer implements RecipeSerializer<SmokingRackRecipe> {

		public static final MapCodec<SmokingRackRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(SmokingRackRecipe::input),
			ItemStack.STRICT_CODEC.fieldOf("result").forGetter(SmokingRackRecipe::result),
			Codec.INT.fieldOf("smoking_time").forGetter(SmokingRackRecipe::smokingTime)
		).apply(instance, SmokingRackRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, SmokingRackRecipe> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, SmokingRackRecipe::input,
			ItemStack.STREAM_CODEC, SmokingRackRecipe::result,
			ByteBufCodecs.INT, SmokingRackRecipe::smokingTime,
			SmokingRackRecipe::new);

		@Override
		public MapCodec<SmokingRackRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, SmokingRackRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
