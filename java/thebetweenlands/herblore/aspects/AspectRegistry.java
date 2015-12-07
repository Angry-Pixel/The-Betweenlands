package thebetweenlands.herblore.aspects;

import thebetweenlands.herblore.Amounts;
import thebetweenlands.herblore.aspects.AspectManager.AspectEntry;
import thebetweenlands.herblore.aspects.AspectManager.AspectTier;
import thebetweenlands.herblore.aspects.AspectManager.AspectType;
import thebetweenlands.herblore.aspects.AspectManager.ItemEntry;
import thebetweenlands.herblore.aspects.AspectManager.ItemEntryAspects;
import thebetweenlands.items.herblore.ItemGenericCrushed;
import thebetweenlands.items.herblore.ItemGenericCrushed.EnumItemGenericCrushed;

public class AspectRegistry {
	public static void init() {
		registerItems();
		registerAspects();
	}

	private static void registerItems() {
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_ALGAE)), 				AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_ARROW_ARUM)), 			AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BLACKHAT_MUSHROOM)), 	AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 2);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BOG_BEAN)), 			AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BONESET)), 			AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BOTTLE_BRUSH_GRASS)), 	AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BROOM_SEDGE)), 		AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BUTTON_BUSH)), 		AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CATTAIL)), 			AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CAVE_GRASS)), 			AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CAVE_MOSS)), 			AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_COPPER_IRIS)), 		AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_DRIED_SWAMP_REED)), 	AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_FLATHEAD_MUSHROOM)), 	AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 2);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_HANGER)), 				AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_LICHEN)), 				AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MARSH_HIBISCUS)), 		AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MARSH_MALLOW)), 		AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 2);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MILKWEED)), 			AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 2);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MOSS)), 				AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_NETTLE)), 				AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_PHRAGMITES)), 			AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_PICKEREL_WEED)), 		AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SHOOTS)), 				AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SLUDGECREEP)), 		AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SOFT_RUSH)), 			AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SWAMP_KELP)), 			AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SWAMP_GRASS_TALL)), 	AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_TANGLED_ROOTS)), 		AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_WEEDWOOD_BARK)), 		AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_WATER_WEEDS)), 		AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_VOLARPAD)), 			AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 2);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_THORNS)), 				AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_POISON_IVY)), 			AspectTier.COMMON, AspectType.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_GENERIC_LEAF)), 		AspectTier.COMMON, AspectType.HERB, 0.15F, 0.2F), 4);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BLOOD_SNAIL_SHELL)), 	AspectTier.UNCOMMON, AspectType.HERB, 1.6F, 1.0F), 2);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BLUE_IRIS)), 			AspectTier.UNCOMMON, AspectType.HERB, 1.6F, 1.0F));
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BLUE_EYED_GRASS)), 	AspectTier.UNCOMMON, AspectType.HERB, 1.6F, 0.65F));
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CARDINAL_FLOWER)), 	AspectTier.UNCOMMON, AspectType.HERB, 1.6F, 0.65F));
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MIRE_CORAL)), 			AspectTier.UNCOMMON, AspectType.HERB, 1.6F, 0.65F));
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MARSH_MARIGOLD)), 		AspectTier.UNCOMMON, AspectType.HERB, 1.6F, 0.65F));
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_GOLDEN_CLUB)), 		AspectTier.UNCOMMON, AspectType.HERB, 1.6F, 0.65F));
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_DEEP_WATER_CORAL)), 	AspectTier.UNCOMMON, AspectType.HERB, 1.6F, 0.65F));
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MIRE_SNAIL_SHELL)), 	AspectTier.UNCOMMON, AspectType.HERB, 1.6F, 0.65F), 2);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BULB_CAPPED_MUSHROOM)),AspectTier.UNCOMMON, AspectType.HERB, 1.6F, 0.65F));
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_ANGLER_TOOTH)), 		AspectTier.UNCOMMON, AspectType.HERB, 1.6F, 0.65F), 2);
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SUNDEW)), 				AspectTier.RARE, AspectType.HERB, 2.0F, 0.5F));
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_PITCHER_PLANT)), 		AspectTier.RARE, AspectType.HERB, 2.0F, 0.5F));
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_VENUS_FLY_TRAP)), 		AspectTier.RARE, AspectType.HERB, 2.0F, 0.5F));

		//Gems
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_GREEN_MIDDLE_GEM)), 	AspectTier.UNCOMMON, AspectType.GEM_FERGALAZ, 2.0F, 0.25F));
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CRIMSON_MIDDLE_GEM)), 	AspectTier.UNCOMMON, AspectType.GEM_FIRNALAZ, 2.0F, 0.25F));
		AspectManager.addStaticAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_AQUA_MIDDLE_GEM)), 	AspectTier.UNCOMMON, AspectType.GEM_BYRGINAZ, 2.0F, 0.25F));
	}

	private static void registerAspects() {
		AspectManager.registerAspect(new AspectEntry(AspectManager.BYARIIS, 	AspectTier.COMMON, 		AspectType.HERB, Amounts.HIGH));
		AspectManager.registerAspect(new AspectEntry(AspectManager.AZUWYNN, 	AspectTier.COMMON, 		AspectType.HERB, Amounts.LOW_MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectManager.CELAWYNN, 	AspectTier.COMMON, 		AspectType.HERB, Amounts.LOW_MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectManager.ORDANIIS, 	AspectTier.COMMON, 		AspectType.HERB, Amounts.LOW_MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectManager.YEOWYNN, 	AspectTier.COMMON, 		AspectType.HERB, Amounts.LOW_MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectManager.ARMANIIS, 	AspectTier.UNCOMMON, 	AspectType.HERB, Amounts.MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectManager.BYRGINAZ, 	AspectTier.UNCOMMON, 	AspectType.HERB, Amounts.MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectManager.DAYUNIIS, 	AspectTier.UNCOMMON, 	AspectType.HERB, Amounts.MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectManager.FERGALAZ, 	AspectTier.UNCOMMON, 	AspectType.HERB, Amounts.MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectManager.FIRNALAZ, 	AspectTier.UNCOMMON, 	AspectType.HERB, Amounts.MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectManager.FREIWYNN, 	AspectTier.UNCOMMON, 	AspectType.HERB, Amounts.MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectManager.YUNUGAZ, 	AspectTier.UNCOMMON, 	AspectType.HERB, Amounts.MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectManager.GEOLIIRGAZ,	AspectTier.RARE, 		AspectType.HERB, Amounts.MEDIUM_HIGH));
		AspectManager.registerAspect(new AspectEntry(AspectManager.YIHINREN, 	AspectTier.RARE, 		AspectType.HERB, Amounts.MEDIUM_HIGH));

		//For middle gems
		AspectManager.registerAspect(new AspectEntry(AspectManager.BYRGINAZ, 	AspectTier.UNCOMMON, 	AspectType.GEM_BYRGINAZ, Amounts.MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectManager.FERGALAZ, 	AspectTier.UNCOMMON, 	AspectType.GEM_FERGALAZ, Amounts.MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectManager.FIRNALAZ, 	AspectTier.UNCOMMON, 	AspectType.GEM_FIRNALAZ, Amounts.MEDIUM));
	}
}
