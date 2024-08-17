package thebetweenlands.common.datagen;

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

public class BetweenlandsItemTagProvider extends ItemTagsProvider {

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

	public BetweenlandsItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper helper) {
		super(output, lookupProvider, blockTags, TheBetweenlands.ID, helper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.copy(BetweenlandsBlockTagsProvider.OCTINE_IGNITES, OCTINE_IGNITES);

		// Those two "inherit" from this one
		this.tag(CORRODIBLE).addTag(DEFAULT_CORRODIBLE).addTag(CUSTOM_CORRODIBLE);
		this.tag(DEFAULT_CORRODIBLE).add(Items.DIAMOND_SWORD);
		this.tag(CUSTOM_CORRODIBLE);
	}

	public static TagKey<Item> tag(String tagName) {
		return ItemTags.create(TheBetweenlands.prefix(tagName));
	}

	public static TagKey<Item> commonTag(String tagName) {
		return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", tagName));
	}

}
