package thebetweenlands.common.item.herblore;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.json.JsonRenderGenerator;
import thebetweenlands.common.item.ICustomJsonGenerationItem;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemGenericPlantDrop extends Item implements ICustomJsonGenerationItem, ItemRegistry.ISubItemsItem {
    public ItemGenericPlantDrop() {
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public static ItemStack createStack(ItemGenericCrushed.EnumItemGenericCrushed enumItemGeneric) {
        return createStack(enumItemGeneric, 1);
    }

    public static ItemStack createStack(ItemGenericCrushed.EnumItemGenericCrushed enumItemGeneric, int size) {
        return new ItemStack(ItemRegistry.itemsGeneric, size, enumItemGeneric.ordinal());
    }

    public static EnumItemPlantDrop getEnumFromID(int id) {
        for (int i = 0; i < EnumItemPlantDrop.VALUES.length; i++) {
            EnumItemPlantDrop enumGeneric = EnumItemPlantDrop.VALUES[i];
            if (enumGeneric.ordinal() == id) return enumGeneric;
        }
        return EnumItemPlantDrop.INVALID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (EnumItemPlantDrop itemGeneric : EnumItemPlantDrop.values()) {
            list.add(new ItemStack(item, 1, itemGeneric.ordinal()));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        try {
            return "item.thebetweenlands." + getEnumFromID(stack.getItemDamage()).name;
        } catch (Exception e) {
            return "item.thebetweenlands.unknownPlantDrop";
        }
    }

    @Override
    public String getJsonText(String itemName) {
        return String.format(JsonRenderGenerator.ITEM_DEFAULT_FORMAT, "strictlyHerblore/plantDrops/" + itemName);
    }

    @Override
    public List<String> getModels() {
        List<String> models = new ArrayList<String>();
        for (EnumItemPlantDrop type : EnumItemPlantDrop.values())
            models.add(type.name);
        return models;
    }

    public enum EnumItemPlantDrop {
        GENERIC_LEAF,
        ALGAE,
        ARROW_ARUM_LEAF,
        BLUE_EYED_GRASS_FLOWERS,
        BLUE_IRIS_PETAL,
        MIRE_CORAL,
        DEEP_WATER_CORAL,
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
        WATER_WEEDS,
        VENUS_FLY_TRAP,
        VOLARPAD,
        THORNS,
        POISON_IVY,

        //KEEP AT BOTTOM
        INVALID;

        public static final EnumItemPlantDrop[] VALUES = values();

        public final String name;


        EnumItemPlantDrop() {
            name = name().toLowerCase(Locale.ENGLISH);
        }
    }
}
