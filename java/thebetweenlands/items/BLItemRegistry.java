package thebetweenlands.items;

import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.creativetabs.ModCreativeTabs;

import java.lang.reflect.Field;

public class BLItemRegistry {

		// BASIC MATERIALS
		public static final Item testItem = new TestItem().setUnlocalizedName("thebetweenlands.testItem").setTextureName("thebetweenlands:testItem");
		public static final Item swampTalisman = new SwampTalisman();
		// MISC WEAPONS

		// MISC ARMOR

		// CREATIVE
		public static final Item spawnEggs = new SpawnEggs().setUnlocalizedName("thebetweenlands.monsterPlacer").setTextureName("spawn_egg");

		public static void init() {
			initCreativeTabs();
			registerItems();
			registerProperties();
		}

		private static void initCreativeTabs() {
			ModCreativeTabs.items.setTab(swampTalisman);
			ModCreativeTabs.specials.setTab(spawnEggs);
			ModCreativeTabs.gears.setTab(testItem);

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
