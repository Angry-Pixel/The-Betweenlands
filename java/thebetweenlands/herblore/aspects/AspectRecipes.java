package thebetweenlands.herblore.aspects;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;
import thebetweenlands.herblore.aspects.AspectRegistry.AspectEntry;
import thebetweenlands.herblore.aspects.AspectRegistry.AspectTier;
import thebetweenlands.herblore.aspects.AspectRegistry.AspectType;
import thebetweenlands.herblore.aspects.AspectRegistry.ItemEntry;
import thebetweenlands.herblore.aspects.AspectRegistry.ItemEntryAspects;
import thebetweenlands.items.ItemMaterialsCrushed;
import thebetweenlands.items.ItemMaterialsCrushed.EnumMaterialsCrushed;

public class AspectRecipes {
	public static final AspectRegistry REGISTRY = new AspectRegistry();

	public static void init() {
		registerItems();
		registerAspects();
	}

	private static void registerItems() {
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_ARROW_ARUM)), 			AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_BLUE_EYED_GRASS)), 		AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_BLUE_IRIS)), 				AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_BONESET)), 				AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_BOTTLE_BRUSH_GRASS)), 	AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_BUTTON_BUSH)), 			AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_CAT_TAIL)), 				AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_COPPER_IRIS)), 			AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_DRIED_SWAMP_REED)),		AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_MARSH_HIBISCUS)), 		AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_MARSH_MALLOW)), 			AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_MILKWEED)), 				AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_PICKEREL_WEED)), 			AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_SHOOTS)), 				AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_SOFT_RUSH)), 				AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_SWAMP_GRASS_TALL)), 		AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		REGISTRY.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_WEEDWOOD_BARK)), 			AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
	}

	private static void registerAspects() {
		//TODO: Set tiers and types
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.AZUWYNN, 	AspectTier.COMMON, AspectType.HERB, 1.0F));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.BYARIIS, 	AspectTier.COMMON, AspectType.HERB, 1.0F));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.BYRGINAZ, 	AspectTier.COMMON, AspectType.HERB, 1.0F));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.CELAWYNN, 	AspectTier.COMMON, AspectType.HERB, 1.0F));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.DAYUNIIS, 	AspectTier.COMMON, AspectType.HERB, 1.0F));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.FERGALAZ, 	AspectTier.COMMON, AspectType.HERB, 1.0F));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.FIRNALAZ, 	AspectTier.COMMON, AspectType.HERB, 1.0F));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.FREIWYNN, 	AspectTier.COMMON, AspectType.HERB, 1.0F));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.GEOLIIRGAZ, 	AspectTier.COMMON, AspectType.HERB, 1.0F));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.ORDANIIS, 	AspectTier.COMMON, AspectType.HERB, 1.0F));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.YEOWYNN, 	AspectTier.COMMON, AspectType.HERB, 1.0F));
		REGISTRY.registerAspect(new AspectEntry(AspectRegistry.YUNUGAZ, 	AspectTier.COMMON, AspectType.HERB, 1.0F));
	}
}
