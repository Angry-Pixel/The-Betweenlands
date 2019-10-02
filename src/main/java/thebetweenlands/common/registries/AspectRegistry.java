package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.herblore.Amounts;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.aspect.AspectManager.AspectGroup;
import thebetweenlands.common.herblore.aspect.AspectManager.AspectTier;
import thebetweenlands.common.herblore.aspect.DefaultItemStackMatchers;
import thebetweenlands.common.herblore.aspect.type.AspectArmaniis;
import thebetweenlands.common.herblore.aspect.type.AspectAzuwynn;
import thebetweenlands.common.herblore.aspect.type.AspectByariis;
import thebetweenlands.common.herblore.aspect.type.AspectByrginaz;
import thebetweenlands.common.herblore.aspect.type.AspectCelawynn;
import thebetweenlands.common.herblore.aspect.type.AspectDayuniis;
import thebetweenlands.common.herblore.aspect.type.AspectFergalaz;
import thebetweenlands.common.herblore.aspect.type.AspectFirnalaz;
import thebetweenlands.common.herblore.aspect.type.AspectFreiwynn;
import thebetweenlands.common.herblore.aspect.type.AspectGeoliirgaz;
import thebetweenlands.common.herblore.aspect.type.AspectOrdaniis;
import thebetweenlands.common.herblore.aspect.type.AspectUduriis;
import thebetweenlands.common.herblore.aspect.type.AspectWodren;
import thebetweenlands.common.herblore.aspect.type.AspectYeowynn;
import thebetweenlands.common.herblore.aspect.type.AspectYihinren;
import thebetweenlands.common.herblore.aspect.type.AspectYunugaz;
import thebetweenlands.common.item.herblore.ItemCrushed.EnumItemCrushed;

public class AspectRegistry {
	private AspectRegistry() { }

	public static final List<IAspectType> ASPECT_TYPES = new ArrayList<IAspectType>();

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
	public static final IAspectType WODREN = new AspectWodren();
	public static final IAspectType UDURIIS = new AspectUduriis();
	
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
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_ALGAE.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 					AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_ARROW_ARUM.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_BLACKHAT_MUSHROOM.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 2);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_BOG_BEAN.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_BONESET.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_BOTTLE_BRUSH_GRASS.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 	AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_BROOM_SEDGE.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_BUTTON_BUSH.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_CATTAIL.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_CAVE_GRASS.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_CAVE_MOSS.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_COPPER_IRIS.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_DRIED_SWAMP_REED.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_FLATHEAD_MUSHROOM.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 2);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_HANGER.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_LICHEN.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_MARSH_HIBISCUS.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_MARSH_MALLOW.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 2);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_MILKWEED.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 2);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_MOSS.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 					AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_NETTLE.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_PHRAGMITES.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_PICKEREL_WEED.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_SHOOTS.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_SLUDGECREEP.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_SOFT_RUSH.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_SWAMP_KELP.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_SWAMP_GRASS_TALL.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_ROOTS.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_WEEDWOOD_BARK.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_WATER_WEEDS.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_VOLARPAD.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 2);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_THORNS.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_POISON_IVY.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_GENERIC_LEAF.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.15F, 0.2F, 4);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_BLADDERWORT_FLOWER.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 	AspectTier.COMMON, AspectGroup.HERB, 0.25F, 0.5F, 2);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_BLADDERWORT_STALK.create(1), DefaultItemStackMatchers.ITEM_DAMAGE,		AspectTier.COMMON, AspectGroup.HERB, 0.15F, 0.5F, 4);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_BLOOD_SNAIL_SHELL.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.5F, 2);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_BLUE_IRIS.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.5F);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_BLUE_EYED_GRASS.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.45F);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_CARDINAL_FLOWER.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.45F);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_MIRE_CORAL.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.45F);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_MARSH_MARIGOLD.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.45F);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_GOLDEN_CLUB.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.45F);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_DEEP_WATER_CORAL.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.45F);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_MIRE_SNAIL_SHELL.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.45F, 2);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_BULB_CAPPED_MUSHROOM.create(1), DefaultItemStackMatchers.ITEM_DAMAGE,	AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.45F);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_ANGLER_TOOTH.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.45F, 2);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_SUNDEW.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.RARE, AspectGroup.HERB, 1.25F, 0.5F);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_PITCHER_PLANT.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.RARE, AspectGroup.HERB, 1.25F, 0.5F);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_VENUS_FLY_TRAP.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.RARE, AspectGroup.HERB, 1.25F, 0.5F);

		//Gems
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_GREEN_MIDDLE_GEM.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.UNCOMMON, AspectGroup.GEM_FERGALAZ, 1.75F, 0.25F);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_CRIMSON_MIDDLE_GEM.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 	AspectTier.UNCOMMON, AspectGroup.GEM_FIRNALAZ, 1.75F, 0.25F);
		AspectManager.addStaticAspectsToItem(EnumItemCrushed.GROUND_AQUA_MIDDLE_GEM.create(1), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.UNCOMMON, AspectGroup.GEM_BYRGINAZ, 1.75F, 0.25F);
	
		//Sap spit
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.SAP_SPIT), DefaultItemStackMatchers.ITEM_DAMAGE, 					AspectTier.UNCOMMON, AspectGroup.SAP_SPIT, 1.0F, 0.1F, 2);
	}

	private static void registerAspects() {
		AspectManager.registerAspect(AspectRegistry.BYARIIS, 	AspectTier.COMMON, 		AspectGroup.HERB, Amounts.HIGH);
		AspectManager.registerAspect(AspectRegistry.AZUWYNN, 	AspectTier.COMMON, 		AspectGroup.HERB, Amounts.LOW_MEDIUM);
		AspectManager.registerAspect(AspectRegistry.CELAWYNN, 	AspectTier.COMMON, 		AspectGroup.HERB, Amounts.LOW_MEDIUM);
		AspectManager.registerAspect(AspectRegistry.ORDANIIS, 	AspectTier.COMMON, 		AspectGroup.HERB, Amounts.LOW_MEDIUM);
		AspectManager.registerAspect(AspectRegistry.YEOWYNN, 	AspectTier.COMMON, 		AspectGroup.HERB, Amounts.LOW_MEDIUM);
		AspectManager.registerAspect(AspectRegistry.ARMANIIS, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM);
		AspectManager.registerAspect(AspectRegistry.BYRGINAZ, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM);
		AspectManager.registerAspect(AspectRegistry.DAYUNIIS, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM);
		AspectManager.registerAspect(AspectRegistry.FERGALAZ, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM);
		AspectManager.registerAspect(AspectRegistry.FIRNALAZ, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM);
		AspectManager.registerAspect(AspectRegistry.FREIWYNN, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM);
		AspectManager.registerAspect(AspectRegistry.YUNUGAZ, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM);
		AspectManager.registerAspect(AspectRegistry.GEOLIIRGAZ,	AspectTier.RARE, 		AspectGroup.HERB, Amounts.MEDIUM_HIGH);
		AspectManager.registerAspect(AspectRegistry.YIHINREN, 	AspectTier.RARE, 		AspectGroup.HERB, Amounts.MEDIUM_HIGH);

		//For middle gems
		AspectManager.registerAspect(AspectRegistry.BYRGINAZ, 	AspectTier.UNCOMMON, 	AspectGroup.GEM_BYRGINAZ, Amounts.MEDIUM);
		AspectManager.registerAspect(AspectRegistry.FERGALAZ, 	AspectTier.UNCOMMON, 	AspectGroup.GEM_FERGALAZ, Amounts.MEDIUM);
		AspectManager.registerAspect(AspectRegistry.FIRNALAZ, 	AspectTier.UNCOMMON, 	AspectGroup.GEM_FIRNALAZ, Amounts.MEDIUM);
		
		//Sap spit
		AspectManager.registerAspect(AspectRegistry.YEOWYNN, 	AspectTier.UNCOMMON, 	AspectGroup.SAP_SPIT, Amounts.HIGH);
		AspectManager.registerAspect(AspectRegistry.ORDANIIS, 	AspectTier.UNCOMMON, 	AspectGroup.SAP_SPIT, Amounts.HIGH);
	}
}
