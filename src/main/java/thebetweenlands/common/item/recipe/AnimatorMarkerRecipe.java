package thebetweenlands.common.item.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.recipes.AnimatorRecipe;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public record AnimatorMarkerRecipe(Ingredient input, int requiredFuel, int requiredLife) implements AnimatorRecipe {
	@Override
	public int getRequiredFuel(SingleRecipeInput input) {
		return this.requiredFuel;
	}

	@Override
	public int getRequiredLife(SingleRecipeInput input) {
		return this.requiredLife;
	}

	@Override
	public @Nullable Entity getRenderEntity(SingleRecipeInput input, Level level) {
		return null;
	}

	@Override
	public @Nullable EntityType<?> getSpawnEntity(SingleRecipeInput input) {
		return null;
	}

	@Override
	public @Nullable EntityType<?> getSpawnEntity() {
		return null;
	}

	@Override
	public ItemStack onAnimated(ServerLevel level, BlockPos pos, SingleRecipeInput input) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean onRetrieved(Player player, BlockPos pos, SingleRecipeInput input) {
		return true;
	}

	@Override
	public boolean getCloseOnFinish(SingleRecipeInput input) {
		return false;
	}

	@Override
	public boolean matches(SingleRecipeInput input, Level level) {
		return this.input.test(input.item()) && input.item().getDamageValue() == 0;
	}

	@Override
	public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
		var copy = input.item().copy();
		copy.set(DataComponentRegistry.ANIMATED, Unit.INSTANCE);
		return copy;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		var stack = this.input().getItems()[0].copy();
		stack.set(DataComponentRegistry.ANIMATED, Unit.INSTANCE);
		return stack;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.ANIMATOR_MARKER_SERIALIZER.get();
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(Ingredient.EMPTY, this.input());
	}

	public static class Serializer implements RecipeSerializer<AnimatorMarkerRecipe> {

		public static final MapCodec<AnimatorMarkerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(AnimatorMarkerRecipe::input),
			Codec.INT.fieldOf("required_fuel").forGetter(AnimatorMarkerRecipe::requiredFuel),
			Codec.INT.fieldOf("required_life").forGetter(AnimatorMarkerRecipe::requiredLife)
		).apply(instance, AnimatorMarkerRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, AnimatorMarkerRecipe> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, AnimatorMarkerRecipe::input,
			ByteBufCodecs.INT, AnimatorMarkerRecipe::requiredFuel,
			ByteBufCodecs.INT, AnimatorMarkerRecipe::requiredLife,
			AnimatorMarkerRecipe::new
		);

		@Override
		public MapCodec<AnimatorMarkerRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, AnimatorMarkerRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
