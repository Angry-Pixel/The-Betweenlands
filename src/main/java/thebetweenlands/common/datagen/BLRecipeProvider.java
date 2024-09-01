package thebetweenlands.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.datagen.builders.AnimatorRecipeBuilder;
import thebetweenlands.common.datagen.builders.DruidAltarRecipeBuilder;
import thebetweenlands.common.datagen.builders.FishTrimmingRecipeBuilder;
import thebetweenlands.common.items.recipe.AnadiaTrimmingRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;

import java.util.concurrent.CompletableFuture;

public class BLRecipeProvider extends RecipeProvider {
	public BLRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void buildRecipes(RecipeOutput output, HolderLookup.Provider holderLookup) {
		DruidAltarRecipeBuilder.assembly(ItemRegistry.SWAMP_TALISMAN)
			.requires(ItemRegistry.SWAMP_TALISMAN_PIECE_1)
			.requires(ItemRegistry.SWAMP_TALISMAN_PIECE_2)
			.requires(ItemRegistry.SWAMP_TALISMAN_PIECE_3)
			.requires(ItemRegistry.SWAMP_TALISMAN_PIECE_4)
			.save(output);

		DruidAltarRecipeBuilder.reversion()
			.requires(ItemTags.SAPLINGS).requires(ItemTags.SAPLINGS)
			.requires(ItemTags.SAPLINGS).requires(ItemTags.SAPLINGS)
			.save(output, TheBetweenlands.prefix("sapling_altar_reversion"));

		FishTrimmingRecipeBuilder.trimming(ItemRegistry.BUBBLER_CRAB)
			.outputs(ItemRegistry.SILT_CRAB_CLAW)
			.outputs(new ItemStack(ItemRegistry.CRAB_STICK.get(), 2))
			.outputs(ItemRegistry.SILT_CRAB_CLAW)
			.save(output);

		FishTrimmingRecipeBuilder.trimming(ItemRegistry.SILT_CRAB)
			.outputs(ItemRegistry.SILT_CRAB_CLAW)
			.outputs(new ItemStack(ItemRegistry.CRAB_STICK.get(), 2))
			.outputs(ItemRegistry.SILT_CRAB_CLAW)
			.save(output);

		output.accept(TheBetweenlands.prefix("anadia_trimming"), new AnadiaTrimmingRecipe(), null);

		AnimatorRecipeBuilder.animator(ItemRegistry.TAR_BEAST_HEART)
			.setResultStack(ItemRegistry.ANIMATED_TAR_BEAST_HEART)
			.requiredFuel(32)
			.requiredLife(32)
			.save(output);

//		AnimatorRecipeBuilder.animator(ItemRegistry.INANIMATE_TARMINION)
//			.setResultStack(ItemRegistry.TARMINION)
//			.requiredFuel(8)
//			.requiredLife(8)
//			.setRenderEntity(EntityRegistry.TARMINION.get())
//			.save(output);
//
//		AnimatorRecipeBuilder.animator(ItemRegistry.SPORES)
//			.requiredFuel(8)
//			.requiredLife(4)
//			.setResultEntity(EntityRegistry.SPORELING.get())
//			.setRenderEntity(EntityRegistry.SPORELING.get())
//			.save(output, TheBetweenlands.prefix("sporeling"));
//
//		AnimatorRecipeBuilder.animator(BlockRegistry.ROOT_POD)
//			.requiredFuel(10)
//			.requiredLife(6)
//			.setResultEntity(EntityRegistry.ROOT_SPRITE.get())
//			.setRenderEntity(EntityRegistry.ROOT_SPRITE.get())
//			.save(output, TheBetweenlands.prefix("root_sprite"));

		AnimatorRecipeBuilder.animator(ItemRegistry.SMALL_SPIRIT_TREE_FACE_MASK)
			.setResultStack(ItemRegistry.ANIMATED_SMALL_SPIRIT_TREE_FACE_MASK)
			.requiredFuel(24)
			.requiredLife(24)
			.save(output);

		AnimatorRecipeBuilder.animator(ItemRegistry.INANIMATE_ANGRY_PEBBLE)
			.setResultStack(ItemRegistry.ANGRY_PEBBLE)
			.requiredFuel(1)
			.requiredLife(1)
			.save(output);

		AnimatorRecipeBuilder.animator(ItemRegistry.SLUDGE_WORM_EGG_SAC)
			.setResultStack(ItemRegistry.SLUDGE_WORM_ARROW)
			.requiredFuel(6)
			.requiredLife(3)
			.save(output);

		AnimatorRecipeBuilder.animator(ItemRegistry.SNOT)
			.setResultStack(ItemRegistry.SNOT_POD)
			.requiredFuel(1)
			.requiredLife(1)
			.save(output);

		AnimatorRecipeBuilder.animator(ItemRegistry.ITEM_SCROLL)
			.requiredFuel(16)
			.requiredLife(16)
			.setResultLoot(LootTableRegistry.SCROLL)
			.save(output, TheBetweenlands.prefix("scroll"));

		AnimatorRecipeBuilder.animator(ItemRegistry.FABRICATED_SCROLL)
			.requiredFuel(16)
			.requiredLife(16)
			.setResultLoot(LootTableRegistry.FABRICATED_SCROLL)
			.save(output, TheBetweenlands.prefix("fabricated_scroll"));
	}
}
