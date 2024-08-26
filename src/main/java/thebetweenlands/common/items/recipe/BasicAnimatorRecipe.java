package thebetweenlands.common.items.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import thebetweenlands.api.recipes.AnimatorRecipe;
import thebetweenlands.common.block.entity.AnimatorBlockEntity;
import thebetweenlands.common.registries.RecipeRegistry;

import javax.annotation.Nullable;
import java.util.Optional;

public record BasicAnimatorRecipe(Ingredient input, ItemStack resultStack, Optional<EntityType<?>> resultEntity, int requiredFuel, int requiredLife, Optional<EntityType<?>> renderEntity, Optional<ResourceLocation> lootTable, boolean closeOnFinish) implements AnimatorRecipe {

	public BasicAnimatorRecipe(Ingredient input, int requiredFuel, int requiredLife) {
		this(input, ItemStack.EMPTY, Optional.empty(), requiredFuel, requiredLife, Optional.empty(), Optional.empty(), false);
	}

	public BasicAnimatorRecipe(Ingredient input, int requiredFuel, int requiredLife, ResourceLocation lootTable) {
		this(input, ItemStack.EMPTY, Optional.empty(), requiredFuel, requiredLife, Optional.empty(), Optional.of(lootTable), false);
	}

	public BasicAnimatorRecipe(Ingredient input, ItemStack result, int requiredFuel, int requiredLife) {
		this(input, result, Optional.empty(), requiredFuel, requiredLife, Optional.empty(), Optional.empty(), false);
	}

	public BasicAnimatorRecipe(Ingredient input, EntityType<?> result, int requiredFuel, int requiredLife) {
		this(input, ItemStack.EMPTY, Optional.of(result), requiredFuel, requiredLife, Optional.empty(), Optional.empty(), true);
	}

	public BasicAnimatorRecipe(Ingredient input, ItemStack result, EntityType<?> resultEntity, int requiredFuel, int requiredLife) {
		this(input, result, Optional.of(resultEntity), requiredFuel, requiredLife, Optional.empty(), Optional.empty(), true);
	}

	//TODO create entity cache system
	@Override
	public Entity getRenderEntity(SingleRecipeInput input) {
//		if (this.renderEntity != null) {
//			if (this.renderEntityInstance == null) {
//				this.renderEntityInstance = EntityList.createEntityByIDFromName(this.renderEntity, Minecraft.getMinecraft().world);
//			}
//			return this.renderEntityInstance;
//		}
		return null;
	}

	@Nullable
	@Override
	public EntityType<?> getSpawnEntity(SingleRecipeInput input) {
		return this.renderEntity().orElse(null);
	}

	@Override
	public boolean matches(SingleRecipeInput input, Level level) {
		return this.input().test(input.item());
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return this.resultStack();
	}

	@Override
	public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
		return this.resultStack().copy();
	}

	@Override
	public ItemStack onAnimated(Level level, BlockPos pos, SingleRecipeInput input) {
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
			ItemStack.STRICT_CODEC.optionalFieldOf("result", ItemStack.EMPTY).forGetter(BasicAnimatorRecipe::resultStack),
			BuiltInRegistries.ENTITY_TYPE.byNameCodec().optionalFieldOf("result_entity").forGetter(BasicAnimatorRecipe::resultEntity),
			Codec.INT.fieldOf("required_fuel").forGetter(BasicAnimatorRecipe::requiredFuel),
			Codec.INT.fieldOf("required_life").forGetter(BasicAnimatorRecipe::requiredLife),
			BuiltInRegistries.ENTITY_TYPE.byNameCodec().optionalFieldOf("render_entity").forGetter(BasicAnimatorRecipe::renderEntity),
			ResourceLocation.CODEC.optionalFieldOf("loot_table").forGetter(BasicAnimatorRecipe::lootTable),
			Codec.BOOL.optionalFieldOf("close_on_finish", false).forGetter(BasicAnimatorRecipe::closeOnFinish)
		).apply(instance, BasicAnimatorRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, BasicAnimatorRecipe> STREAM_CODEC = new StreamCodec<>() {
			@Override
			public BasicAnimatorRecipe decode(RegistryFriendlyByteBuf buf) {
				Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
				ItemStack outputStack = ItemStack.STREAM_CODEC.decode(buf);
//				Optional<EntityType<?>> resultEntity = ByteBufCodecs.registry(Registries.ENTITY_TYPE).apply(ByteBufCodecs::optional).decode(buf);
				int fuel = ByteBufCodecs.INT.decode(buf);
				int life = ByteBufCodecs.INT.decode(buf);
//				Optional<EntityType<?>> renderEntity = ByteBufCodecs.registry(Registries.ENTITY_TYPE).apply(ByteBufCodecs::optional).decode(buf);
				Optional<ResourceLocation> lootTable = ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs::optional).decode(buf);
				boolean close = ByteBufCodecs.BOOL.decode(buf);
				return new BasicAnimatorRecipe(input, outputStack, null, fuel, life, null, lootTable, close);
			}

			@Override
			public void encode(RegistryFriendlyByteBuf buf, BasicAnimatorRecipe recipe) {
				Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input());
				ItemStack.STREAM_CODEC.encode(buf, recipe.resultStack());
//				ByteBufCodecs.registry(Registries.ENTITY_TYPE).apply(ByteBufCodecs::optional).encode(buf, recipe.resultEntity());
				ByteBufCodecs.INT.encode(buf, recipe.requiredFuel());
				ByteBufCodecs.INT.encode(buf, recipe.requiredLife());
//				ByteBufCodecs.registry(Registries.ENTITY_TYPE).apply(ByteBufCodecs::optional).encode(buf, recipe.renderEntity());
				ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs::optional).encode(buf, recipe.lootTable());
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
