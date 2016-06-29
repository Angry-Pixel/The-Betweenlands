package thebetweenlands.common.item.herblore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.json.JsonRenderGenerator;
import thebetweenlands.common.item.ICustomJsonGenerationItem;
import thebetweenlands.common.item.misc.IGenericItem;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemPlantDrop extends Item implements ICustomJsonGenerationItem, ItemRegistry.ISubItemsItem {
	public ItemPlantDrop() {
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public String getJsonText(String itemName) {
		return String.format(JsonRenderGenerator.ITEM_DEFAULT_FORMAT, "strictlyHerblore/plantDrops/" + itemName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (EnumItemPlantDrop type : EnumItemPlantDrop.values())
			list.add(type.create(1));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		try {
			return "item.thebetweenlands." + IGenericItem.getFromStack(EnumItemPlantDrop.class, stack).getUnlocalizedName();
		} catch (Exception e) {
			return "item.thebetweenlands.unknownPlantDrop";
		}
	}

	@Override
	public List<String> getModels() {
		List<String> models = new ArrayList<String>();
		for (EnumItemPlantDrop type : EnumItemPlantDrop.values())
			models.add(type.getUnlocalizedName());
		return models;
	}

	public enum EnumItemPlantDrop implements IGenericItem {
		GENERIC_LEAF(0),
		ALGAE(1),
		ARROW_ARUM_LEAF(2),
		BLUE_EYED_GRASS_FLOWERS(3),
		BLUE_IRIS_PETAL(4),
		MIRE_CORAL(5),
		DEEP_WATER_CORAL(6),
		BOG_BEAN_FLOWER(7),
		BONESET_FLOWERS(8),
		BOTTLE_BRUSH_GRASS_BLADES(9),
		BROOM_SEDGE_LEAVES(10),
		BUTTON_BUSH_FLOWERS(11),
		CARDINAL_FLOWER_PETALS(12),
		CATTAIL_HEAD(13),
		CAVE_GRASS_BLADES(14),
		COPPER_IRIS_PETALS(15),
		GOLDEN_CLUB_FLOWERS(16),
		LICHEN(17),
		MARSH_HIBISCUS_FLOWER(18),
		MARSH_MALLOW_FLOWER(19),
		MARSH_MARIGOLD_FLOWER(20),
		NETTLE_LEAF(21),
		PHRAGMITE_STEMS(22),
		PICKEREL_WEED_FLOWER(23),
		SHOOT_LEAVES(24),
		SLUDGECREEP_LEAVES(25),
		SOFT_RUSH_LEAVES(26),
		SUNDEW_HEAD(27),
		SWAMP_TALL_GRASS_BLADES(28),
		CAVE_MOSS(29),
		MOSS(30),
		MILK_WEED(31),
		HANGER(32),
		PITCHER_PLANT_TRAP(33),
		WATER_WEEDS(34),
		VENUS_FLY_TRAP(35),
		VOLARPAD(36),
		THORNS(37),
		POISON_IVY(38);

		private final int id;
		private final String unlocalizedName;

		EnumItemPlantDrop(int id) {
			this.id = id;
			this.unlocalizedName = this.name().toLowerCase(Locale.ENGLISH);
		}

		@Override
		public String getUnlocalizedName() {
			return this.unlocalizedName;
		}

		@Override
		public int getID() {
			return this.id;
		}

		@Override
		public Item getItem() {
			return ItemRegistry.ITEMS_PLANT_DROP;
		}
	}
}
