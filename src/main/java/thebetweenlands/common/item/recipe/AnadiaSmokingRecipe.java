package thebetweenlands.common.item.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import thebetweenlands.api.recipes.SmokingRackRecipe;
import thebetweenlands.common.entity.fishing.anadia.Anadia;
import thebetweenlands.common.entity.fishing.anadia.AnadiaParts;
import thebetweenlands.common.item.misc.MobItem;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public class AnadiaSmokingRecipe implements SmokingRackRecipe {

	@Override
	public boolean matches(SingleRecipeInput input, Level level) {
		return input.item().is(ItemRegistry.ANADIA);
	}

	@Override
	public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
		ItemStack output = input.item().copy();

		CompoundTag entityNbt = ((MobItem) output.getItem()).getEntityData(output);

		// never called in legit caught anadia, but helps display correct item in JEI smoking recipe ;)
		if (entityNbt == null) {
			entityNbt = new CompoundTag();
			ResourceLocation id = EntityRegistry.ANADIA.getId();
			if (id != null) {
				entityNbt.putString("id", id.toString());
			}
		}

		if (entityNbt.getByte("fish_color") != 0) {
			entityNbt.putByte("fish_color", (byte) AnadiaParts.AnadiaColor.SMOKED.ordinal());

			CompoundTag headItem = entityNbt.getCompound(Anadia.HEAD_KEY);
			CompoundTag bodyItem = entityNbt.getCompound(Anadia.BODY_KEY);
			CompoundTag tailItem = entityNbt.getCompound(Anadia.TAIL_KEY);

			if (!headItem.isEmpty()) {
				this.checkAndConvertNBTStack(headItem, registries).save(registries, headItem);
				entityNbt.put(Anadia.HEAD_KEY, headItem);
			}

			if (!bodyItem.isEmpty()) {
				this.checkAndConvertNBTStack(bodyItem, registries).save(registries, bodyItem);
				entityNbt.put(Anadia.BODY_KEY, bodyItem);
			}

			if (!tailItem.isEmpty()) {
				this.checkAndConvertNBTStack(tailItem, registries).save(registries, tailItem);
				entityNbt.put(Anadia.TAIL_KEY, tailItem);
			}
		}

		((MobItem) output.getItem()).setEntityData(output, entityNbt);

		return output;
	}

	public boolean isRawMeatStack(ItemStack stack) {
		return stack.is(ItemRegistry.RAW_ANADIA_MEAT);
	}

	public ItemStack checkAndConvertNBTStack(CompoundTag nbt, HolderLookup.Provider registries) {
		ItemStack stackOld = ItemStack.parseOptional(registries, nbt);
		int count = stackOld.getCount();
		if (this.isRawMeatStack(stackOld))
			return new ItemStack(ItemRegistry.SMOKED_ANADIA_MEAT.get(), count);
		return stackOld;
	}

	@Override
	public int smokingTime() {
		return 600;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return new ItemStack(ItemRegistry.ANADIA.get());
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.ANADIA_SMOKING_SERIALIZER.get();
	}

	public static class Serializer implements RecipeSerializer<AnadiaSmokingRecipe> {

		private static final AnadiaSmokingRecipe INSTANCE = new AnadiaSmokingRecipe();
		private static final MapCodec<AnadiaSmokingRecipe> CODEC = MapCodec.unit(INSTANCE);
		private static final StreamCodec<RegistryFriendlyByteBuf, AnadiaSmokingRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);

		@Override
		public MapCodec<AnadiaSmokingRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, AnadiaSmokingRecipe> streamCodec() {
			return STREAM_CODEC;
		}

	}
}
