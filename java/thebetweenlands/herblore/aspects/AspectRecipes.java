package thebetweenlands.herblore.aspects;

import thebetweenlands.herblore.Amounts;
import thebetweenlands.herblore.aspects.AspectRegistry.AspectEntry;
import thebetweenlands.herblore.aspects.AspectRegistry.AspectTier;
import thebetweenlands.herblore.aspects.AspectRegistry.AspectType;
import thebetweenlands.herblore.aspects.AspectRegistry.ItemEntry;
import thebetweenlands.herblore.aspects.AspectRegistry.ItemEntryAspects;
import thebetweenlands.items.herblore.ItemGenericCrushed;
import thebetweenlands.items.herblore.ItemGenericCrushed.EnumItemGenericCrushed;

public class AspectRecipes {
	public static final AspectRegistry REGISTRY = new AspectRegistry();

	public static void init() {
		registerItems();
		registerAspects();
	}

	private static void registerItems() {
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_ALGAE)), 					AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_ANGLER_TOOTH)), 			AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_ARROW_ARUM)), 			AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BLACKHAT_MUSHROOM)), 		AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BLOOD_SNAIL_SHELL)), 		AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BLUE_EYED_GRASS)), 		AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BLUE_IRIS)), 				AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BOG_BEAN)), 				AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BONESET)), 				AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BOTTLE_BRUSH_GRASS)), 	AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BROOM_SEDGE)), 			AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BULB_CAPPED_MUSHROOM)), 	AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BUTTON_BUSH)), 			AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CARDINAL_FLOWER)), 		AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CATTAIL)), 				AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CAVE_GRASS)), 			AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CAVE_MOSS)), 				AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_COPPER_IRIS)), 			AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_DRIED_SWAMP_REED)), 		AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_FLATHEAD_MUSHROOM)), 		AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_HANGER)), 				AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_LICHEN)), 				AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MARSH_HIBISCUS)), 		AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MARSH_MALLOW)), 			AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MILKWEED)), 				AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MIRE_SNAIL_SHELL)), 		AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MOSS)), 					AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_NETTLE)), 				AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_PHRAGMITES)), 			AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_PICKEREL_WEED)), 			AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SHOOTS)), 				AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SLUDGECREEP)), 			AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SOFT_RUSH)), 				AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SWAMP_KELP)), 			AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SWAMP_GRASS_TALL)), 		AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_TANGLED_ROOTS)), 			AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_WEEDWOOD_BARK)), 			AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SUNDEW)), 				AspectTier.UNCOMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MIRE_CORAL)), 			AspectTier.UNCOMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MARSH_MARIGOLD)), 		AspectTier.UNCOMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_GOLDEN_CLUB)), 			AspectTier.UNCOMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_GREEN_MIDDLE_GEM)), 		AspectTier.UNCOMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CRIMSON_MIDDLE_GEM)), 	AspectTier.UNCOMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_DEEP_WATER_CORAL)), 		AspectTier.UNCOMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_AQUA_MIDDLE_GEM)), 		AspectTier.UNCOMMON, AspectType.HERB, 1.0F, 1.0F));
	}

	private static void registerAspects() {
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.BYARIIS, 	AspectTier.COMMON, 		AspectType.HERB, Amounts.HIGH));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.AZUWYNN, 	AspectTier.COMMON, 		AspectType.HERB, Amounts.LOW_MEDIUM));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.CELAWYNN, 	AspectTier.COMMON, 		AspectType.HERB, Amounts.LOW_MEDIUM));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.ORDANIIS, 	AspectTier.COMMON, 		AspectType.HERB, Amounts.LOW_MEDIUM));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.YEOWYNN, 	AspectTier.COMMON, 		AspectType.HERB, Amounts.LOW_MEDIUM));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.BYRGINAZ, 	AspectTier.UNCOMMON, 	AspectType.HERB, Amounts.MEDIUM));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.DAYUNIIS, 	AspectTier.UNCOMMON, 	AspectType.HERB, Amounts.MEDIUM));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.FERGALAZ, 	AspectTier.UNCOMMON, 	AspectType.HERB, Amounts.MEDIUM));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.FIRNALAZ, 	AspectTier.UNCOMMON, 	AspectType.HERB, Amounts.MEDIUM));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.FREIWYNN, 	AspectTier.UNCOMMON, 	AspectType.HERB, Amounts.MEDIUM));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.YUNUGAZ, 	AspectTier.UNCOMMON, 	AspectType.HERB, Amounts.MEDIUM));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.GEOLIIRGAZ, 	AspectTier.RARE, 		AspectType.HERB, Amounts.MEDIUM_HIGH));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.YIHINREN, 	AspectTier.RARE, 		AspectType.HERB, Amounts.MEDIUM_HIGH));
	}
}
