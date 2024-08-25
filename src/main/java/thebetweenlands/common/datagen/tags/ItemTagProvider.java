package thebetweenlands.common.datagen.tags;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.api.item.ICustomCorrodible;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemTagProvider extends ItemTagsProvider {

	public static final TagKey<Item> OCTINE_IGNITES = tag("octine_ignites");
	public static final TagKey<Item> GIVES_FOOD_SICKNESS = tag("gives_food_sickness");

	/**
	 * Whether or not an item should be looked at by the corrosion engine
	 */
	public static final TagKey<Item> CORRODIBLE = tag("corrodible");
	/**
	 * Use default corrosion on an item. Apply to items from other mods you want to be corrodible.
	 */
	public static final TagKey<Item> DEFAULT_CORRODIBLE = tag("corrodible/default");
	/**
	 * Whether or not an item has custom corrosion information. The item should implement {@link ICustomCorrodible}; this tag will be ignored if it doesn't.
	 */
	public static final TagKey<Item> CUSTOM_CORRODIBLE = tag("corrodible/custom");

	public ItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper helper) {
		super(output, lookupProvider, blockTags, TheBetweenlands.ID, helper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.copy(BlockTagProvider.OCTINE_IGNITES, OCTINE_IGNITES);

		// Those two "inherit" from this one
		this.tag(CORRODIBLE).addTag(DEFAULT_CORRODIBLE).addTag(CUSTOM_CORRODIBLE);
		this.tag(DEFAULT_CORRODIBLE).add(Items.DIAMOND_SWORD);
		this.tag(CUSTOM_CORRODIBLE);

		this.tag(GIVES_FOOD_SICKNESS).add(ItemRegistry.MIRE_SNAIL_EGG.get(), ItemRegistry.COOKED_MIRE_SNAIL_EGG.get(),
			ItemRegistry.RAW_FROG_LEGS.get(), ItemRegistry.COOKED_FROG_LEGS.get(),
			ItemRegistry.RAW_SNAIL_FLESH.get(), ItemRegistry.COOKED_SNAIL_FLESH.get(),
			ItemRegistry.REED_DONUT.get(), ItemRegistry.JAM_DONUT.get(),
			ItemRegistry.GERTS_DONUT.get(), ItemRegistry.PUFFSHROOM_TENDRIL.get(),
			ItemRegistry.KRAKEN_TENTACLE.get(), ItemRegistry.KRAKEN_CALAMARI.get(),
			ItemRegistry.MIDDLE_FRUIT.get(), ItemRegistry.MINCE_PIE.get(),
			ItemRegistry.CHRISTMAS_PUDDING.get(), ItemRegistry.CANDY_CANE.get(),
			ItemRegistry.WEEPING_BLUE_PETAL.get(), ItemRegistry.YELLOW_DOTTED_FUNGUS.get(),
			ItemRegistry.SILT_CRAB_CLAW.get(), ItemRegistry.CRAB_STICK.get(),
			ItemRegistry.SLUDGE_JELLO.get(), ItemRegistry.MIDDLE_FRUIT_JELLO.get(),
			ItemRegistry.SAP_JELLO.get(), ItemRegistry.GREEN_MARSHMALLOW.get(),
			ItemRegistry.PINK_MARSHMALLOW.get(), ItemRegistry.BULB_CAPPED_MUSHROOM.get(),
			ItemRegistry.FRIED_SWAMP_KELP.get(), ItemRegistry.FORBIDDEN_FIG.get(),
			ItemRegistry.BLUE_CANDY.get(), ItemRegistry.RED_CANDY.get(),
			ItemRegistry.YELLOW_CANDY.get(), ItemRegistry.MIRE_SCRAMBLE.get(),
			ItemRegistry.WEEPING_BLUE_PETAL_SALAD.get(), ItemRegistry.NIBBLESTICK.get(),
			ItemRegistry.SPIRIT_FRUIT.get(), ItemRegistry.SUSHI.get(),
			ItemRegistry.RAW_ANADIA_MEAT.get(), ItemRegistry.COOKED_ANADIA_MEAT.get(),
			ItemRegistry.SMOKED_ANADIA_MEAT.get(), ItemRegistry.BARNACLE.get(),
			ItemRegistry.COOKED_BARNACLE.get(), ItemRegistry.SMOKED_BARNACLE.get(),
			ItemRegistry.SMOKED_CRAB_STICK.get(), ItemRegistry.SMOKED_FROG_LEGS.get(),
			ItemRegistry.SMOKED_PUFFSHROOM_TENDRIL.get(), ItemRegistry.SMOKED_SILT_CRAB_CLAW.get(),
			ItemRegistry.SMOKED_SNAIL_FLESH.get(), ItemRegistry.RAW_OLM_EGG.get(),
			ItemRegistry.COOKED_OLM_EGG.get(), ItemRegistry.OLMLETTE.get(),
			ItemRegistry.SILK_GRUB.get());
	}

	public static TagKey<Item> tag(String tagName) {
		return ItemTags.create(TheBetweenlands.prefix(tagName));
	}

	public static TagKey<Item> commonTag(String tagName) {
		return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", tagName));
	}

}
