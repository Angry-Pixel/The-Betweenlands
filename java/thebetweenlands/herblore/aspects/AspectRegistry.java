package thebetweenlands.herblore.aspects;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import thebetweenlands.herblore.Amounts;
import thebetweenlands.herblore.aspects.AspectManager.AspectEntry;
import thebetweenlands.herblore.aspects.AspectManager.AspectGroup;
import thebetweenlands.herblore.aspects.AspectManager.AspectItem;
import thebetweenlands.herblore.aspects.AspectManager.AspectItemEntry;
import thebetweenlands.herblore.aspects.AspectManager.AspectTier;
import thebetweenlands.herblore.aspects.list.AspectArmaniis;
import thebetweenlands.herblore.aspects.list.AspectAzuwynn;
import thebetweenlands.herblore.aspects.list.AspectByariis;
import thebetweenlands.herblore.aspects.list.AspectByrginaz;
import thebetweenlands.herblore.aspects.list.AspectCelawynn;
import thebetweenlands.herblore.aspects.list.AspectDayuniis;
import thebetweenlands.herblore.aspects.list.AspectFergalaz;
import thebetweenlands.herblore.aspects.list.AspectFirnalaz;
import thebetweenlands.herblore.aspects.list.AspectFreiwynn;
import thebetweenlands.herblore.aspects.list.AspectGeoliirgaz;
import thebetweenlands.herblore.aspects.list.AspectOrdaniis;
import thebetweenlands.herblore.aspects.list.AspectYeowynn;
import thebetweenlands.herblore.aspects.list.AspectYihinren;
import thebetweenlands.herblore.aspects.list.AspectYunugaz;
import thebetweenlands.items.herblore.ItemGenericCrushed;
import thebetweenlands.items.herblore.ItemGenericCrushed.EnumItemGenericCrushed;

public class AspectRegistry {
	public static final List<IAspectType> ASPECT_TYPES = new ArrayList<IAspectType>();

	public static final IAspectType UNKNOWN = new IAspectType() {
		@Override
		public String getName() {
			return "Unknown";
		}

		@Override
		public String getType() {
			return "Unknown";
		}

		@Override
		public String getDescription() {
			return "Unknown";
		}

		@Override
		public int getIconIndex() {
			return 0;
		}
	};

	public static final IAspectType AZUWYNN = new AspectAzuwynn();
	public static final IAspectType ARMANIIS = new AspectArmaniis();
	public static final IAspectType BYARIIS = new AspectByariis();
	public static final IAspectType BYRGINAZ = new AspectByrginaz();
	public static final IAspectType CELAWYNN = new AspectCelawynn();
	public static final IAspectType DAYUNIIS = new AspectDayuniis();
	public static final IAspectType FERGALAZ = new AspectFergalaz();
	public static final IAspectType FIRNALAZ = new AspectFirnalaz();
	public static final IAspectType FREIWYNN = new AspectFreiwynn();
	public static final IAspectType GEOLIIRGAZ = new AspectGeoliirgaz();
	public static final IAspectType ORDANIIS = new AspectOrdaniis();
	public static final IAspectType YEOWYNN = new AspectYeowynn();
	public static final IAspectType YUNUGAZ = new AspectYunugaz();
	public static final IAspectType YIHINREN = new AspectYihinren();

	static {
		try {
			for(Field f : AspectRegistry.class.getDeclaredFields()) {
				if(f.getType() == IAspectType.class) {
					Object obj = f.get(null);
					if(obj instanceof IAspectType) {
						ASPECT_TYPES.add((IAspectType)obj);
					}
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public static IAspectType getAspectTypeFromName(String name) {
		for(IAspectType type : ASPECT_TYPES) {
			if(type.getName().equals(name)) return type;
		}
		return null;
	}

	public static void init() {
		registerItems();
		registerAspects();
	}

	private static void registerItems() {
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_ALGAE)), 				AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_ARROW_ARUM)), 			AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BLACKHAT_MUSHROOM)), 	AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 2);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BOG_BEAN)), 			AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BONESET)), 			AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BOTTLE_BRUSH_GRASS)), 	AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BROOM_SEDGE)), 		AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BUTTON_BUSH)), 		AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CATTAIL)), 			AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CAVE_GRASS)), 			AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CAVE_MOSS)), 			AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_COPPER_IRIS)), 		AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_DRIED_SWAMP_REED)), 	AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_FLATHEAD_MUSHROOM)), 	AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 2);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_HANGER)), 				AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_LICHEN)), 				AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MARSH_HIBISCUS)), 		AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MARSH_MALLOW)), 		AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 2);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MILKWEED)), 			AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 2);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MOSS)), 				AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_NETTLE)), 				AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_PHRAGMITES)), 			AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_PICKEREL_WEED)), 		AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SHOOTS)), 				AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SLUDGECREEP)), 		AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SOFT_RUSH)), 			AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SWAMP_KELP)), 			AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SWAMP_GRASS_TALL)), 	AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_TANGLED_ROOTS)), 		AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_WEEDWOOD_BARK)), 		AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_WATER_WEEDS)), 		AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_VOLARPAD)), 			AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 2);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_THORNS)), 				AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_POISON_IVY)), 			AspectTier.COMMON, AspectGroup.HERB, 0.45F, 1.0F), 3);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_GENERIC_LEAF)), 		AspectTier.COMMON, AspectGroup.HERB, 0.15F, 0.2F), 4);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BLOOD_SNAIL_SHELL)), 	AspectTier.UNCOMMON, AspectGroup.HERB, 1.6F, 1.0F), 2);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BLUE_IRIS)), 			AspectTier.UNCOMMON, AspectGroup.HERB, 1.6F, 1.0F));
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BLUE_EYED_GRASS)), 	AspectTier.UNCOMMON, AspectGroup.HERB, 1.6F, 0.65F));
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CARDINAL_FLOWER)), 	AspectTier.UNCOMMON, AspectGroup.HERB, 1.6F, 0.65F));
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MIRE_CORAL)), 			AspectTier.UNCOMMON, AspectGroup.HERB, 1.6F, 0.65F));
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MARSH_MARIGOLD)), 		AspectTier.UNCOMMON, AspectGroup.HERB, 1.6F, 0.65F));
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_GOLDEN_CLUB)), 		AspectTier.UNCOMMON, AspectGroup.HERB, 1.6F, 0.65F));
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_DEEP_WATER_CORAL)), 	AspectTier.UNCOMMON, AspectGroup.HERB, 1.6F, 0.65F));
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_MIRE_SNAIL_SHELL)), 	AspectTier.UNCOMMON, AspectGroup.HERB, 1.6F, 0.65F), 2);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_BULB_CAPPED_MUSHROOM)),AspectTier.UNCOMMON, AspectGroup.HERB, 1.6F, 0.65F));
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_ANGLER_TOOTH)), 		AspectTier.UNCOMMON, AspectGroup.HERB, 1.6F, 0.65F), 2);
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_SUNDEW)), 				AspectTier.RARE, AspectGroup.HERB, 2.0F, 0.5F));
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_PITCHER_PLANT)), 		AspectTier.RARE, AspectGroup.HERB, 2.0F, 0.5F));
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_VENUS_FLY_TRAP)), 		AspectTier.RARE, AspectGroup.HERB, 2.0F, 0.5F));

		//Gems
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_GREEN_MIDDLE_GEM)), 	AspectTier.UNCOMMON, AspectGroup.GEM_FERGALAZ, 2.0F, 0.25F));
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_CRIMSON_MIDDLE_GEM)), 	AspectTier.UNCOMMON, AspectGroup.GEM_FIRNALAZ, 2.0F, 0.25F));
		AspectManager.addStaticAspectsToItem(new AspectItemEntry(new AspectItem(ItemGenericCrushed.createStack(EnumItemGenericCrushed.GROUND_AQUA_MIDDLE_GEM)), 	AspectTier.UNCOMMON, AspectGroup.GEM_BYRGINAZ, 2.0F, 0.25F));
	}

	private static void registerAspects() {
		AspectManager.registerAspect(new AspectEntry(AspectRegistry.BYARIIS, 	AspectTier.COMMON, 		AspectGroup.HERB, Amounts.HIGH));
		AspectManager.registerAspect(new AspectEntry(AspectRegistry.AZUWYNN, 	AspectTier.COMMON, 		AspectGroup.HERB, Amounts.LOW_MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectRegistry.CELAWYNN, 	AspectTier.COMMON, 		AspectGroup.HERB, Amounts.LOW_MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectRegistry.ORDANIIS, 	AspectTier.COMMON, 		AspectGroup.HERB, Amounts.LOW_MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectRegistry.YEOWYNN, 	AspectTier.COMMON, 		AspectGroup.HERB, Amounts.LOW_MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectRegistry.ARMANIIS, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectRegistry.BYRGINAZ, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectRegistry.DAYUNIIS, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectRegistry.FERGALAZ, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectRegistry.FIRNALAZ, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectRegistry.FREIWYNN, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectRegistry.YUNUGAZ, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectRegistry.GEOLIIRGAZ,	AspectTier.RARE, 		AspectGroup.HERB, Amounts.MEDIUM_HIGH));
		AspectManager.registerAspect(new AspectEntry(AspectRegistry.YIHINREN, 	AspectTier.RARE, 		AspectGroup.HERB, Amounts.MEDIUM_HIGH));

		//For middle gems
		AspectManager.registerAspect(new AspectEntry(AspectRegistry.BYRGINAZ, 	AspectTier.UNCOMMON, 	AspectGroup.GEM_BYRGINAZ, Amounts.MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectRegistry.FERGALAZ, 	AspectTier.UNCOMMON, 	AspectGroup.GEM_FERGALAZ, Amounts.MEDIUM));
		AspectManager.registerAspect(new AspectEntry(AspectRegistry.FIRNALAZ, 	AspectTier.UNCOMMON, 	AspectGroup.GEM_FIRNALAZ, Amounts.MEDIUM));
	}
}
