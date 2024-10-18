package thebetweenlands.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

import java.util.Arrays;
import java.util.stream.Stream;

public record AspectIngredient(Ingredient input, Holder<AspectType> aspect, int minAmount) implements ICustomIngredient {
	public static final MapCodec<AspectIngredient> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
			Ingredient.CODEC.fieldOf("input").forGetter(AspectIngredient::input),
			AspectType.CODEC.fieldOf("aspect").forGetter(AspectIngredient::aspect),
			Codec.INT.fieldOf("min_amount").forGetter(AspectIngredient::minAmount))
		.apply(builder, AspectIngredient::new));

	@Override
	public boolean test(ItemStack stack) {
		var contents = stack.getOrDefault(DataComponentRegistry.ASPECT_CONTENTS, AspectContents.EMPTY);
		return this.input().test(stack) && contents.aspect().isPresent() && contents.aspect().get().is(this.aspect()) && contents.amount() >= this.minAmount();
	}

	@Override
	public Stream<ItemStack> getItems() {
		return Arrays.stream(this.input().getItems()).map(stack -> AspectContents.createItemStack(stack.getItem(), this.aspect(), this.minAmount()));
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IngredientType<?> getType() {
		return RecipeRegistry.ASPECT_INGREDIENT_TYPE.get();
	}
}
