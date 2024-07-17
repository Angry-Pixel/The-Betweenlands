package thebetweenlands.common.items.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import thebetweenlands.api.recipes.DruidAltarRecipe;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.RecipeRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public record DruidAltarReversionRecipe(NonNullList<Ingredient> items, int processTime) implements DruidAltarRecipe {

	@Override
	public boolean matches(MultiStackInput input, Level level) {
		if (input.size() != this.items.size()) {
			return false;
		}
		return input.size() == 1 && this.items.size() == 1
			? this.items.getFirst().test(input.getItem(0))
			: input.stackedContents().canCraft(this, null);
	}

	@Override
	public ItemStack assemble(MultiStackInput input, HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= this.items.size();
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public boolean showNotification() {
		return false;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.DRUID_ALTAR_REVERSION_SERIALIZER.get();
	}

	@Override
	public void onCrafted(Level level, BlockPos pos, MultiStackInput input, ItemStack output) {
		BlockPos spawnerPos = pos.below();
		if (level.getBlockState(spawnerPos).getDestroySpeed(level, spawnerPos) >= 0.0F) {
			level.setBlockAndUpdate(spawnerPos, BlockRegistry.MOB_SPAWNER.get().defaultBlockState());
			if (level.getBlockEntity(pos) instanceof MobSpawnerBlockEntity spawner) {
				MobSpawnerLogic logic = spawner.getSpawnerLogic();
				logic.setNextEntityName("thebetweenlands:dark_druid").setCheckRange(32.0D).setSpawnRange(6).setSpawnInAir(false).setMaxEntities(1 + level.getRandom().nextInt(3));
			}

			level.playSound(null, spawnerPos, SoundRegistry.DRUID_TELEPORT.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

			// Block break effect, see LevelRenderer#levelEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data)
			level.levelEvent(2001, spawnerPos.above(4), Block.getId(Blocks.OAK_SAPLING.defaultBlockState()));
			level.levelEvent(2003, spawnerPos.above(4), 0);
		}
	}

	public static class Serializer implements RecipeSerializer<DruidAltarReversionRecipe> {

		public static final MapCodec<DruidAltarReversionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(ingredients -> {
				Ingredient[] aingredient = ingredients.toArray(Ingredient[]::new);
				if (aingredient.length == 0) {
					return DataResult.error(() -> "No ingredients for druid altar recipe");
				} else {
					return aingredient.length > 4
						? DataResult.error(() -> "Too many ingredients for druid altar recipe. The maximum is 4")
						: DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
				}
			}, DataResult::success).forGetter(DruidAltarReversionRecipe::items),
			Codec.INT.fieldOf("process_time").forGetter(DruidAltarReversionRecipe::processTime)
		).apply(instance, DruidAltarReversionRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, DruidAltarReversionRecipe> STREAM_CODEC = StreamCodec.of(DruidAltarReversionRecipe.Serializer::toNetwork, DruidAltarReversionRecipe.Serializer::fromNetwork);

		@Override
		public MapCodec<DruidAltarReversionRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, DruidAltarReversionRecipe> streamCodec() {
			return STREAM_CODEC;
		}

		private static DruidAltarReversionRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
			int i = buffer.readVarInt();
			NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
			nonnulllist.replaceAll(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
			int time = buffer.readInt();
			return new DruidAltarReversionRecipe(nonnulllist, time);
		}

		private static void toNetwork(RegistryFriendlyByteBuf buffer, DruidAltarReversionRecipe recipe) {
			buffer.writeVarInt(recipe.items().size());

			for (Ingredient ingredient : recipe.items()) {
				Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
			}

			buffer.writeInt(recipe.processTime());
		}
	}
}
