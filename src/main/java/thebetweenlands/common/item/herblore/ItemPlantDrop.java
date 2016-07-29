package thebetweenlands.common.item.herblore;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.CaseFormat;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.json.JsonRenderGenerator;
import thebetweenlands.common.item.ICustomJsonGenerationItem;
import thebetweenlands.common.item.IGenericItem;
import thebetweenlands.common.lib.ModInfo;
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
    public List<ResourceLocation> getModels() {
        return Stream.of(EnumItemPlantDrop.values()).map(t -> new ResourceLocation(ModInfo.ID, t.getModelName())).collect(Collectors.toList());
    }

	public enum EnumItemPlantDrop implements IGenericItem {
		GENERIC_LEAF,
		ALGAE,
		ARROW_ARUM_LEAF,
		BLUE_EYED_GRASS_FLOWERS,
		BLUE_IRIS_PETAL,
		MIRE_CORAL_ITEM,
		DEEP_WATER_CORAL_ITEM,
		BOG_BEAN_FLOWER,
		BONESET_FLOWERS,
		BOTTLE_BRUSH_GRASS_BLADES,
		BROOM_SEDGE_LEAVES,
		BUTTON_BUSH_FLOWERS,
		CARDINAL_FLOWER_PETALS,
		CATTAIL_HEAD,
		CAVE_GRASS_BLADES,
		COPPER_IRIS_PETALS,
		GOLDEN_CLUB_FLOWERS,
		LICHEN,
		MARSH_HIBISCUS_FLOWER,
		MARSH_MALLOW_FLOWER,
		MARSH_MARIGOLD_FLOWER,
		NETTLE_LEAF,
		PHRAGMITE_STEMS,
		PICKEREL_WEED_FLOWER,
		SHOOT_LEAVES,
		SLUDGECREEP_LEAVES,
		SOFT_RUSH_LEAVES,
		SUNDEW_HEAD,
		SWAMP_TALL_GRASS_BLADES,
		CAVE_MOSS,
		MOSS,
		MILK_WEED,
		HANGER,
		PITCHER_PLANT_TRAP,
		WATER_WEEDS_ITEM,
		VENUS_FLY_TRAP_ITEM,
		VOLARPAD_ITEM,
		THORNS,
		POISON_IVY_ITEM;

		private final String unlocalizedName;
        private final String modelName;

		EnumItemPlantDrop() {
			this.modelName = this.name().toLowerCase(Locale.ENGLISH);
			this.unlocalizedName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.modelName);
		}

		@Override
		public String getUnlocalizedName() {
			return this.unlocalizedName;
		}

        @Override
        public String getModelName() {
            return this.modelName;
        }

		@Override
		public int getID() {
			return this.ordinal();
		}

		@Override
		public Item getItem() {
			return ItemRegistry.ITEMS_PLANT_DROP;
		}
	}
}
