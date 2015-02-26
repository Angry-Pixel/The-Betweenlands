package thebetweenlands.items;

import java.lang.reflect.Field;

import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.recipes.BLMaterials;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class BLItemRegistry {

		// BASIC MATERIALS
		public static final Item testItem = new TestItem().setUnlocalizedName("thebetweenlands.testItem").setTextureName("thebetweenlands:testItem");
		public static final Item swampTalisman = new SwampTalisman();
		public static final Item materialsBL = new ItemMaterialsBL();

		// WEAPONS TOOLS
		public static final Item weedWoodSword = new ItemSword(BLMaterials.toolWeedWood).setUnlocalizedName("thebetweenlands.weedWoodSword").setTextureName("thebetweenlands:weedWoodSword");
		public static final Item weedWoodPickaxe = new PickaxeBL(BLMaterials.toolWeedWood).setUnlocalizedName("thebetweenlands.weedWoodPickaxe").setTextureName("thebetweenlands:weedWoodPickaxe");
		public static final Item weedWoodAxe = new AxeBL(BLMaterials.toolWeedWood).setUnlocalizedName("thebetweenlands.weedWoodAxe").setTextureName("thebetweenlands:weedWoodAxe");
		public static final Item weedWoodShovel = new ItemSpade(BLMaterials.toolWeedWood).setUnlocalizedName("thebetweenlands.weedWoodShovel").setTextureName("thebetweenlands:weedWoodShovel");
		
		public static final Item betweenstoneSword = new ItemSword(BLMaterials.toolBetweenstone).setUnlocalizedName("thebetweenlands.betweenstoneSword").setTextureName("thebetweenlands:betweenstoneSword");
		public static final Item betweenstonePickaxe = new PickaxeBL(BLMaterials.toolBetweenstone).setUnlocalizedName("thebetweenlands.betweenstonePickaxe").setTextureName("thebetweenlands:betweenstonePickaxe");
		public static final Item betweenstoneAxe = new AxeBL(BLMaterials.toolBetweenstone).setUnlocalizedName("thebetweenlands.betweenstoneAxe").setTextureName("thebetweenlands:betweenstoneAxe");
		public static final Item betweenstoneShovel = new ItemSpade(BLMaterials.toolBetweenstone).setUnlocalizedName("thebetweenlands.betweenstoneShovel").setTextureName("thebetweenlands:betweenstoneShovel");

		public static final Item octineSword = new ItemSword(BLMaterials.toolOctine).setUnlocalizedName("thebetweenlands.octineSword").setTextureName("thebetweenlands:octineSword");
		public static final Item octinePickaxe = new PickaxeBL(BLMaterials.toolOctine).setUnlocalizedName("thebetweenlands.octinePickaxe").setTextureName("thebetweenlands:octinePickaxe");
		public static final Item octineAxe = new AxeBL(BLMaterials.toolOctine).setUnlocalizedName("thebetweenlands.octineAxe").setTextureName("thebetweenlands:octineAxe");
		public static final Item octineShovel = new ItemSpade(BLMaterials.toolOctine).setUnlocalizedName("thebetweenlands.octineShovel").setTextureName("thebetweenlands:octineShovel");

		public static final Item valoniteSword = new ItemSword(BLMaterials.toolValonite).setUnlocalizedName("thebetweenlands.valoniteSword").setTextureName("thebetweenlands:valoniteSword");
		public static final Item valonitePickaxe = new PickaxeBL(BLMaterials.toolValonite).setUnlocalizedName("thebetweenlands.valonitePickaxe").setTextureName("thebetweenlands:valonitePickaxe");
		public static final Item valoniteAxe = new AxeBL(BLMaterials.toolValonite).setUnlocalizedName("thebetweenlands.valoniteAxe").setTextureName("thebetweenlands:valoniteAxe");
		public static final Item valoniteShovel = new ItemSpade(BLMaterials.toolValonite).setUnlocalizedName("thebetweenlands.valoniteShovel").setTextureName("thebetweenlands:valoniteShovel");

		// MISC ARMOUR

		// CREATIVE
		public static final Item spawnEggs = new SpawnEggs().setUnlocalizedName("thebetweenlands.monsterPlacer").setTextureName("spawn_egg");

		public static void init() {
			initCreativeTabs();
			registerItems();
			registerProperties();
		}

		private static void initCreativeTabs() {
			ModCreativeTabs.items.setTab(swampTalisman);
			ModCreativeTabs.items.setTab(materialsBL);
			ModCreativeTabs.specials.setTab(spawnEggs);
			ModCreativeTabs.gears.setTab(testItem);
			ModCreativeTabs.gears.setTab(weedWoodSword, weedWoodPickaxe, weedWoodAxe, weedWoodShovel);
			ModCreativeTabs.gears.setTab(betweenstoneSword, betweenstonePickaxe, betweenstoneAxe, betweenstoneShovel);
			ModCreativeTabs.gears.setTab(octineSword, octinePickaxe, octineAxe, octineShovel);
			ModCreativeTabs.gears.setTab(valoniteSword, valonitePickaxe, valoniteAxe,valoniteShovel);
			
		}

		private static void registerItems() {
			try {
				for (Field f : BLItemRegistry.class.getDeclaredFields()) {
					Object obj = f.get(null);
					if (obj instanceof Item)
						registerItem((Item) obj);
					else if (obj instanceof Item[])
						for (Item item : (Item[]) obj)
							registerItem(item);
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		private static void registerItem(Item item) {
			String name = item.getUnlocalizedName();
			String[] strings = name.split("\\.");
			GameRegistry.registerItem(item, strings[strings.length - 1]);
		}

		private static void registerProperties() {
			GameRegistry.registerFuelHandler(new IFuelHandler() {

				@Override
				public int getBurnTime(ItemStack fuel) {
					return 0;
					//add fuels here
				}
			});
		}
	}
