package thebetweenlands.common.items.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import thebetweenlands.common.registries.RecipeRegistry;

public class DruidAltarRecipe extends MultiStackRecipe {

	private final int processTime;

	public DruidAltarRecipe(int processTime, NonNullList<Ingredient> items, ItemStack result) {
		super(items, result);
		this.processTime = processTime;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.DRUID_ALTAR_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeRegistry.DRUID_ALTAR_RECIPE.get();
	}

	public int getProcessTime() {
		return this.processTime;
	}

	public static class Serializer implements RecipeSerializer<DruidAltarRecipe> {

		public static final MapCodec<DruidAltarRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.INT.fieldOf("process_time").forGetter(DruidAltarRecipe::getProcessTime),
			Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(ingredients -> {
				Ingredient[] aingredient = ingredients.toArray(Ingredient[]::new);
				if (aingredient.length == 0) {
					return DataResult.error(() -> "No ingredients for druid altar recipe");
				} else {
					return aingredient.length > 4
						? DataResult.error(() -> "Too many ingredients for druid altar recipe. The maximum is 4")
						: DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
				}
			}, DataResult::success).forGetter(DruidAltarRecipe::getItems),
			ItemStack.STRICT_CODEC.fieldOf("result").forGetter(DruidAltarRecipe::getResult)
		).apply(instance, DruidAltarRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, DruidAltarRecipe> STREAM_CODEC = StreamCodec.of(DruidAltarRecipe.Serializer::toNetwork, DruidAltarRecipe.Serializer::fromNetwork);

		@Override
		public MapCodec<DruidAltarRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, DruidAltarRecipe> streamCodec() {
			return STREAM_CODEC;
		}

		private static DruidAltarRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
			int time = buffer.readInt();
			int i = buffer.readVarInt();
			NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
			nonnulllist.replaceAll(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
			ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
			return new DruidAltarRecipe(time, nonnulllist, result);
		}

		private static void toNetwork(RegistryFriendlyByteBuf buffer, DruidAltarRecipe recipe) {
			buffer.writeInt(recipe.getProcessTime());
			buffer.writeVarInt(recipe.getItems().size());

			for (Ingredient ingredient : recipe.getItems()) {
				Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
			}

			ItemStack.STREAM_CODEC.encode(buffer, recipe.getResult());
		}
	}
}
