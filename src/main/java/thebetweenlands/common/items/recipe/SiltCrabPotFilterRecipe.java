package thebetweenlands.common.items.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import thebetweenlands.api.recipes.CrabPotFilterRecipe;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public record SiltCrabPotFilterRecipe(Ingredient input, ItemStack result) implements CrabPotFilterRecipe {
	@Override
	public EntityType<?> getRequiredFilteringMob() {
		return EntityRegistry.SILT_CRAB.get();
	}

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
		return RecipeRegistry.SILT_CRAB_POT_FILTER_SERIALIZER.get();
	}

	public static class Serializer implements RecipeSerializer<SiltCrabPotFilterRecipe> {

		public static final MapCodec<SiltCrabPotFilterRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(SiltCrabPotFilterRecipe::input),
			ItemStack.STRICT_CODEC.fieldOf("result").forGetter(SiltCrabPotFilterRecipe::result)
		).apply(instance, SiltCrabPotFilterRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, SiltCrabPotFilterRecipe> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, SiltCrabPotFilterRecipe::input,
			ItemStack.STREAM_CODEC, SiltCrabPotFilterRecipe::result,
			SiltCrabPotFilterRecipe::new);

		@Override
		public MapCodec<SiltCrabPotFilterRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, SiltCrabPotFilterRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
