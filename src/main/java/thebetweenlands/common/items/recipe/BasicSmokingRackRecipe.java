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
import thebetweenlands.api.recipes.SmokingRackRecipe;
import thebetweenlands.common.registries.RecipeRegistry;

public record BasicSmokingRackRecipe(Ingredient input, ItemStack result, int smokingTime) implements SmokingRackRecipe {

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
		return RecipeRegistry.SMOKING_SERIALIZER.get();
	}

	public static class Serializer implements RecipeSerializer<BasicSmokingRackRecipe> {

		public static final MapCodec<BasicSmokingRackRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(BasicSmokingRackRecipe::input),
			ItemStack.STRICT_CODEC.fieldOf("result").forGetter(BasicSmokingRackRecipe::result),
			Codec.INT.fieldOf("smoking_time").forGetter(BasicSmokingRackRecipe::smokingTime)
		).apply(instance, BasicSmokingRackRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, BasicSmokingRackRecipe> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, BasicSmokingRackRecipe::input,
			ItemStack.STREAM_CODEC, BasicSmokingRackRecipe::result,
			ByteBufCodecs.INT, BasicSmokingRackRecipe::smokingTime,
			BasicSmokingRackRecipe::new);

		@Override
		public MapCodec<BasicSmokingRackRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BasicSmokingRackRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
