package thebetweenlands.common.item.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import thebetweenlands.api.recipes.TrimmingTableRecipe;
import thebetweenlands.common.entity.fishing.anadia.Anadia;
import thebetweenlands.common.item.misc.AnadiaMobItem;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public class AnadiaTrimmingRecipe implements TrimmingTableRecipe {

	@Override
	public NonNullList<ItemStack> assembleRecipe(SingleRecipeInput input, Level level) {
		NonNullList<ItemStack> results = NonNullList.withSize(3, ItemStack.EMPTY);
		ItemStack stack = input.item();
		if (stack.getItem() instanceof AnadiaMobItem mob && mob.hasEntityData(stack)) {
			results.set(0, mob.getItemFromEntity(Anadia.HEAD_KEY, stack, level));
			results.set(1, mob.getItemFromEntity(Anadia.BODY_KEY, stack, level));
			results.set(2, mob.getItemFromEntity(Anadia.TAIL_KEY, stack, level));
		}
		return results;
	}

	@Override
	public NonNullList<ItemStack> getResultItems(HolderLookup.Provider registries) {
		return NonNullList.withSize(3, ItemStack.EMPTY);
	}

	@Override
	public boolean matches(SingleRecipeInput input, Level level) {
		return input.item().is(ItemRegistry.ANADIA);
	}

	@Override
	public ItemStack getRemains() {
		return new ItemStack(ItemRegistry.ANADIA_REMAINS.get());
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.ANADIA_TRIMMING_SERIALIZER.get();
	}

	public static class Serializer implements RecipeSerializer<AnadiaTrimmingRecipe> {

		private static final AnadiaTrimmingRecipe INSTANCE = new AnadiaTrimmingRecipe();
		private static final MapCodec<AnadiaTrimmingRecipe> CODEC = MapCodec.unit(INSTANCE);
		private static final StreamCodec<RegistryFriendlyByteBuf, AnadiaTrimmingRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);

		@Override
		public MapCodec<AnadiaTrimmingRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, AnadiaTrimmingRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
