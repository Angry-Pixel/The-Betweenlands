package thebetweenlands.common.items.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import thebetweenlands.api.recipes.CrabPotFilterRecipe;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public record BubblerCrabPotFilterRecipe(Ingredient input, ItemStack result, int filterTime) implements CrabPotFilterRecipe {

	@Override
	public boolean matches(ItemAndEntityInput input, Level level) {
		return this.input().test(input.stack()) && input.type() == EntityRegistry.BUBBLER_CRAB.get();
	}

	@Override
	public ItemStack assemble(ItemAndEntityInput input, HolderLookup.Provider registries) {
		return this.result().copy();
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return this.result();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.BUBBLER_CRAB_POT_FILTER_SERIALIZER.get();
	}

	public static class Serializer implements RecipeSerializer<BubblerCrabPotFilterRecipe> {

		public static final MapCodec<BubblerCrabPotFilterRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(BubblerCrabPotFilterRecipe::input),
			ItemStack.STRICT_CODEC.fieldOf("result").forGetter(BubblerCrabPotFilterRecipe::result),
			Codec.INT.fieldOf("filter_time").forGetter(BubblerCrabPotFilterRecipe::filterTime)
		).apply(instance, BubblerCrabPotFilterRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, BubblerCrabPotFilterRecipe> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, BubblerCrabPotFilterRecipe::input,
			ItemStack.STREAM_CODEC, BubblerCrabPotFilterRecipe::result,
			ByteBufCodecs.INT, BubblerCrabPotFilterRecipe::filterTime,
			BubblerCrabPotFilterRecipe::new);

		@Override
		public MapCodec<BubblerCrabPotFilterRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BubblerCrabPotFilterRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
