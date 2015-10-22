package thebetweenlands.aspect;

import java.util.List;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import thebetweenlands.aspect.AspectRegistry.AspectEntry;
import thebetweenlands.aspect.AspectRegistry.AspectTier;
import thebetweenlands.aspect.AspectRegistry.AspectType;
import thebetweenlands.aspect.AspectRegistry.ItemEntry;
import thebetweenlands.aspect.AspectRegistry.ItemEntryAspects;
import thebetweenlands.aspect.aspects.AspectAzuwynn;
import thebetweenlands.aspect.aspects.AspectByariis;
import thebetweenlands.aspect.aspects.AspectByrginaz;
import thebetweenlands.aspect.aspects.AspectCelawynn;
import thebetweenlands.aspect.aspects.AspectDayuniis;
import thebetweenlands.aspect.aspects.AspectFergalaz;
import thebetweenlands.aspect.aspects.AspectFirnalaz;
import thebetweenlands.aspect.aspects.AspectFreiwynn;
import thebetweenlands.aspect.aspects.AspectGeoliirgaz;
import thebetweenlands.aspect.aspects.AspectOrdaniis;
import thebetweenlands.aspect.aspects.AspectYeowynn;
import thebetweenlands.aspect.aspects.AspectYunugaz;
import thebetweenlands.items.ItemMaterialsCrushed;
import thebetweenlands.items.ItemMaterialsCrushed.EnumMaterialsCrushed;

public class AspectTest {
	public final AspectRegistry registry = new AspectRegistry();

	public void init() {
		this.registerItems();
		this.registerAspects();
		this.registry.loadAspects(0x584FF4AC);

		System.out.println("ASPECTS");
		for(Entry<ItemEntry, List<ItemAspect>> entry : this.registry.getMatchedAspects().entrySet()) {
			Item item = entry.getKey().item;
			List<ItemAspect> aspects = entry.getValue();
			System.out.print(item.getUnlocalizedName() + ": ");
			for(ItemAspect aspect : aspects) {
				System.out.print(aspect.aspect.getName() + " (" + aspect.amount + "), ");
			}
			System.out.println();
		}
	}

	private void registerItems() {
		this.registry.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_ARROW_ARUM)), AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		this.registry.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_BLUE_EYED_GRASS)), AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		this.registry.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_BLUE_IRIS)), AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		this.registry.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_BONESET)), AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		this.registry.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_BOTTLE_BRUSH_GRASS)), AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		this.registry.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_BUTTON_BUSH)), AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		this.registry.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_CAT_TAIL)), AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		this.registry.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_COPPER_IRIS)), AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		this.registry.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_DRIED_SWAMP_REED)), AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		this.registry.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_MARSH_HIBISCUS)), AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		this.registry.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_MARSH_MALLOW)), AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		this.registry.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_MILKWEED)), AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		this.registry.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_PICKEREL_WEED)), AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		this.registry.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_SHOOTS)), AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		this.registry.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_SOFT_RUSH)), AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		this.registry.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_SWAMP_GRASS_TALL)), AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
		this.registry.addAspectsToItem(new ItemEntryAspects(new ItemEntry(ItemMaterialsCrushed.createStack(EnumMaterialsCrushed.GROUND_WEEDWOOD_BARK)), AspectTier.COMMON, AspectType.HERB, 1.0F, 1.0F));
	}

	private void registerAspects() {
		//TODO: Set tiers and types
		this.registry.registerAspect(new AspectEntry(new AspectAzuwynn(), AspectTier.COMMON, AspectType.HERB, 1.0F));
		this.registry.registerAspect(new AspectEntry(new AspectByariis(), AspectTier.COMMON, AspectType.HERB, 1.0F));
		this.registry.registerAspect(new AspectEntry(new AspectByrginaz(), AspectTier.COMMON, AspectType.HERB, 1.0F));
		this.registry.registerAspect(new AspectEntry(new AspectCelawynn(), AspectTier.COMMON, AspectType.HERB, 1.0F));
		this.registry.registerAspect(new AspectEntry(new AspectDayuniis(), AspectTier.COMMON, AspectType.HERB, 1.0F));
		this.registry.registerAspect(new AspectEntry(new AspectFergalaz(), AspectTier.COMMON, AspectType.HERB, 1.0F));
		this.registry.registerAspect(new AspectEntry(new AspectFirnalaz(), AspectTier.COMMON, AspectType.HERB, 1.0F));
		this.registry.registerAspect(new AspectEntry(new AspectFreiwynn(), AspectTier.COMMON, AspectType.HERB, 1.0F));
		this.registry.registerAspect(new AspectEntry(new AspectGeoliirgaz(), AspectTier.COMMON, AspectType.HERB, 1.0F));
		this.registry.registerAspect(new AspectEntry(new AspectOrdaniis(), AspectTier.COMMON, AspectType.HERB, 1.0F));
		this.registry.registerAspect(new AspectEntry(new AspectYeowynn(), AspectTier.COMMON, AspectType.HERB, 1.0F));
		this.registry.registerAspect(new AspectEntry(new AspectYunugaz(), AspectTier.COMMON, AspectType.HERB, 1.0F));
	}
}
