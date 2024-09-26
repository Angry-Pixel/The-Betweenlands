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
import net.minecraft.util.Mth;
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
import thebetweenlands.common.registries.RecipeRegistry;

public record ToolRepairAnimatorRecipe(Ingredient input, int minRepairLifeCost, int fullRepairLifeCost, int minRepairFuelCost, int fullRepairFuelCost) implements AnimatorRecipe {

	@Override
	public boolean matches(SingleRecipeInput input, Level level) {
		return !input.item().isEmpty() && input.item().getDamageValue() > 0 && this.input().test(input.item());
	}

	@Override
	public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
		ItemStack result = input.item().copy();
		result.setDamageValue(0);
		return result;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return this.input().getItems()[0];
	}

	@Override
	public int getRequiredFuel(SingleRecipeInput input) {
		return this.minRepairFuelCost() + Mth.ceil((this.fullRepairFuelCost() - this.minRepairFuelCost()) / (float)input.item().getMaxDamage() * (float)input.item().getDamageValue());
	}

	@Override
	public int getRequiredLife(SingleRecipeInput input) {
		return this.minRepairLifeCost() + Mth.ceil((this.fullRepairLifeCost() - this.minRepairLifeCost()) / (float)input.item().getMaxDamage() * (float)input.item().getDamageValue());
	}

	@Override
	public Entity getRenderEntity(SingleRecipeInput input, Level level) {
		return null;
	}

	@Override
	public EntityType<?> getSpawnEntity(SingleRecipeInput input) {
		return null;
	}

	@Nullable
	@Override
	public EntityType<?> getSpawnEntity() {
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
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.ANIMATOR_TOOL_SERIALIZER.get();
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(Ingredient.EMPTY, this.input());
	}

	public static class Serializer implements RecipeSerializer<ToolRepairAnimatorRecipe> {

		public static final MapCodec<ToolRepairAnimatorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(ToolRepairAnimatorRecipe::input),
			Codec.INT.fieldOf("min_required_fuel").forGetter(ToolRepairAnimatorRecipe::minRepairFuelCost),
			Codec.INT.fieldOf("full_required_fuel").forGetter(ToolRepairAnimatorRecipe::fullRepairFuelCost),
			Codec.INT.fieldOf("min_required_life").forGetter(ToolRepairAnimatorRecipe::minRepairLifeCost),
			Codec.INT.fieldOf("full_required_life").forGetter(ToolRepairAnimatorRecipe::fullRepairLifeCost)
		).apply(instance, ToolRepairAnimatorRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, ToolRepairAnimatorRecipe> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, ToolRepairAnimatorRecipe::input,
			ByteBufCodecs.INT, ToolRepairAnimatorRecipe::minRepairFuelCost,
			ByteBufCodecs.INT, ToolRepairAnimatorRecipe::fullRepairFuelCost,
			ByteBufCodecs.INT, ToolRepairAnimatorRecipe::minRepairLifeCost,
			ByteBufCodecs.INT, ToolRepairAnimatorRecipe::fullRepairLifeCost,
			ToolRepairAnimatorRecipe::new);

		@Override
		public MapCodec<ToolRepairAnimatorRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, ToolRepairAnimatorRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
