package thebetweenlands.common.items.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import thebetweenlands.api.recipes.AnimatorRecipe;
import thebetweenlands.common.block.entity.AnimatorBlockEntity;
import thebetweenlands.common.registries.RecipeRegistry;
import thebetweenlands.util.EntityCache;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public record BasicAnimatorRecipe(Ingredient input, Optional<ItemStack> resultStack, Optional<EntityType<?>> resultEntity, int requiredFuel, int requiredLife, Optional<EntityType<?>> renderEntity, Optional<ResourceKey<LootTable>> lootTable, boolean closeOnFinish) implements AnimatorRecipe {

	@Nullable
	@Override
	public Entity getRenderEntity(SingleRecipeInput input, Level level) {
		if (this.renderEntity().isPresent()) {
			return EntityCache.fetchEntity(this.renderEntity().get(), level);
		}
		return null;
	}

	@Nullable
	@Override
	public EntityType<?> getSpawnEntity(SingleRecipeInput input) {
		return this.resultEntity().orElse(null);
	}

	@Override
	public boolean matches(SingleRecipeInput input, Level level) {
		return this.input().test(input.item());
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return this.resultStack().orElse(ItemStack.EMPTY);
	}

	@Override
	public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
		return this.resultStack().orElse(ItemStack.EMPTY).copy();
	}

	@Override
	public ItemStack onAnimated(ServerLevel level, BlockPos pos, SingleRecipeInput input) {
		if (this.lootTable().isPresent()) {
			LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(this.lootTable().get());
			List<ItemStack> loot = lootTable.getRandomItems(new LootParams.Builder(level).create(LootContextParamSets.EMPTY), level.getRandom());
			if (!loot.isEmpty()) {
				return loot.get(level.getRandom().nextInt(loot.size()));
			}
			return ItemStack.EMPTY;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean onRetrieved(Player player, BlockPos pos, SingleRecipeInput input) {
		if (player.level().getBlockEntity(pos) instanceof AnimatorBlockEntity animator) {
			if (this.resultEntity().isPresent()) {
				Entity entity = this.resultEntity().get().create(player.level());
				entity.moveTo(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0, 0);
				player.level().addFreshEntity(entity);
				animator.setItem(0, ItemStack.EMPTY);
				return false;
			}
			return true;
		}
		return true;
	}

	@Override
	public boolean getCloseOnFinish(SingleRecipeInput input) {
		return this.closeOnFinish();
	}

	@Override
	public int getRequiredFuel(SingleRecipeInput input) {
		return this.requiredFuel();
	}

	@Override
	public int getRequiredLife(SingleRecipeInput input) {
		return this.requiredLife();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.ANIMATOR_SERIALIZER.get();
	}

	public static class Serializer implements RecipeSerializer<BasicAnimatorRecipe> {

		public static final MapCodec<BasicAnimatorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(BasicAnimatorRecipe::input),
			ItemStack.STRICT_CODEC.optionalFieldOf("result_stack").forGetter(BasicAnimatorRecipe::resultStack),
			BuiltInRegistries.ENTITY_TYPE.byNameCodec().optionalFieldOf("result_entity").forGetter(BasicAnimatorRecipe::resultEntity),
			Codec.INT.fieldOf("required_fuel").forGetter(BasicAnimatorRecipe::requiredFuel),
			Codec.INT.fieldOf("required_life").forGetter(BasicAnimatorRecipe::requiredLife),
			BuiltInRegistries.ENTITY_TYPE.byNameCodec().optionalFieldOf("render_entity").forGetter(BasicAnimatorRecipe::renderEntity),
			ResourceKey.codec(Registries.LOOT_TABLE).optionalFieldOf("result_loot_table").forGetter(BasicAnimatorRecipe::lootTable),
			Codec.BOOL.optionalFieldOf("close_on_finish", false).forGetter(BasicAnimatorRecipe::closeOnFinish)
		).apply(instance, BasicAnimatorRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, BasicAnimatorRecipe> STREAM_CODEC = new StreamCodec<>() {
			@Override
			public BasicAnimatorRecipe decode(RegistryFriendlyByteBuf buf) {
				Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
				Optional<ItemStack> outputStack = ItemStack.STREAM_CODEC.apply(ByteBufCodecs::optional).decode(buf);
				Optional<EntityType<?>> resultEntity = ByteBufCodecs.registry(Registries.ENTITY_TYPE).apply(ByteBufCodecs::optional).decode(buf);
				int fuel = ByteBufCodecs.INT.decode(buf);
				int life = ByteBufCodecs.INT.decode(buf);
				Optional<EntityType<?>> renderEntity = ByteBufCodecs.registry(Registries.ENTITY_TYPE).apply(ByteBufCodecs::optional).decode(buf);
				Optional<ResourceKey<LootTable>> lootTable = ResourceKey.streamCodec(Registries.LOOT_TABLE).apply(ByteBufCodecs::optional).decode(buf);
				boolean close = ByteBufCodecs.BOOL.decode(buf);
				return new BasicAnimatorRecipe(input, outputStack, resultEntity, fuel, life, renderEntity, lootTable, close);
			}

			@Override
			public void encode(RegistryFriendlyByteBuf buf, BasicAnimatorRecipe recipe) {
				Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input());
				ItemStack.STREAM_CODEC.apply(ByteBufCodecs::optional).encode(buf, recipe.resultStack());
				ByteBufCodecs.registry(Registries.ENTITY_TYPE).apply(ByteBufCodecs::optional).encode(buf, recipe.resultEntity());
				ByteBufCodecs.INT.encode(buf, recipe.requiredFuel());
				ByteBufCodecs.INT.encode(buf, recipe.requiredLife());
				ByteBufCodecs.registry(Registries.ENTITY_TYPE).apply(ByteBufCodecs::optional).encode(buf, recipe.renderEntity());
				ResourceKey.streamCodec(Registries.LOOT_TABLE).apply(ByteBufCodecs::optional).encode(buf, recipe.lootTable());
				ByteBufCodecs.BOOL.encode(buf, recipe.closeOnFinish());
			}
		};

		@Override
		public MapCodec<BasicAnimatorRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BasicAnimatorRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
