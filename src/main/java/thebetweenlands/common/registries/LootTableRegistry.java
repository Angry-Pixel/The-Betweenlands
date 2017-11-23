package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import net.minecraft.world.storage.loot.properties.EntityPropertyManager;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import thebetweenlands.common.entity.loot.LootPropertyFrogType;
import thebetweenlands.common.entity.loot.LootPropertyHasItem;
import thebetweenlands.common.entity.loot.LootPropertyIsBossPeatMummy;
import thebetweenlands.common.entity.loot.LootPropertyPeatMummyShimmerstone;
import thebetweenlands.common.entity.loot.LootPropertyPyradCharging;
import thebetweenlands.common.lib.ModInfo;

public class LootTableRegistry {

    //LOOT
    public static final ResourceLocation COMMON_POT_LOOT = register("loot/common_pot_loot");
    public static final ResourceLocation DUNGEON_CHEST_LOOT = register("loot/dungeon_chest_loot");
    public static final ResourceLocation COMMON_CHEST_LOOT = register("loot/common_chest_loot");
    public static final ResourceLocation DUNGEON_POT_LOOT = register("loot/dungeon_pot_loot");
    public static final ResourceLocation MUSIC_DISC = register("loot/music_disc");
    public static final ResourceLocation ANIMATOR_SCROLL = register("animator/scroll");

    //MOBS
    public static final ResourceLocation ANGLER = register("entities/angler");
    public static final ResourceLocation BLIND_CAVE_FISH = register("entities/blind_cave_fish");
    public static final ResourceLocation BLOOD_SNAIL = register("entities/blood_snail");
    public static final ResourceLocation CHIROMAW = register("entities/chiromaw");
    public static final ResourceLocation DARK_DRUID = register("entities/dark_druid");
    public static final ResourceLocation DRAGONFLY = register("entities/dragonfly");
    public static final ResourceLocation FIREFLY = register("entities/firefly");
    public static final ResourceLocation FROG = register("entities/frog");
    public static final ResourceLocation GAS_CLOUD = register("entities/gas_cloud");
    public static final ResourceLocation GECKO = register("entities/gecko");
    public static final ResourceLocation LEECH = register("entities/leech");
    public static final ResourceLocation LURKER = register("entities/lurker");
    public static final ResourceLocation MIRE_SNAIL_EGG = register("entities/mire_snail_egg");
    public static final ResourceLocation MIRE_SNAIL = register("entities/mire_snail");
    public static final ResourceLocation PEAT_MUMMY = register("entities/peat_mummy");
    public static final ResourceLocation PYRAD = register("entities/pyrad");
    public static final ResourceLocation SILT_CRAB = register("entities/silt_crab");
    public static final ResourceLocation SLUDGE = register("entities/sludge");
    public static final ResourceLocation SPORELING = register("entities/sporeling");
    public static final ResourceLocation SWAMP_HAG = register("entities/swamp_hag");
    public static final ResourceLocation TAR_BEAST = register("entities/tar_beast");
    public static final ResourceLocation TARMINION = register("entities/tarminion");
    public static final ResourceLocation TERMITE = register("entities/termite");
    public static final ResourceLocation TOAD = register("entities/toad");
    public static final ResourceLocation WIGHT = register("entities/wight");

    //BOSSES
    public static final ResourceLocation FORTRESS_BOSS = register("entities/fortress_boss");

    //LOOT PROPERTIES
    public static final ResourceLocation PROPERTY_FROG_TYPE = register(new LootPropertyFrogType.Serializer());
    public static final ResourceLocation PROPERTY_PEAT_MUMMY_SHIMMERSTONE = register(new LootPropertyPeatMummyShimmerstone.Serializer());
    public static final ResourceLocation PROPERTY_PYRAD_CHARGING = register(new LootPropertyPyradCharging.Serializer());
    public static final ResourceLocation PROPERTY_HAS_ITEM = register(new LootPropertyHasItem.Serializer());
    public static final ResourceLocation PROPERTY_IS_BOSS_MUMMY = register(new LootPropertyIsBossPeatMummy.Serializer());
    

    private static ResourceLocation register(String id) {
        return LootTableList.register(new ResourceLocation(ModInfo.ID, id));
    }

    private static ResourceLocation register(EntityProperty.Serializer<?> serializer) {
        EntityPropertyManager.registerProperty(serializer);
        return serializer.getName();
    }

    public static ArrayList<ItemStack> getItemsFromTable(ResourceLocation lootTable, World world) {
        ArrayList<ItemStack> items = new ArrayList<>();

        LootTable table = world.getLootTableManager().getLootTableFromLocation(lootTable);
        LootContext.Builder lootBuilder = (new LootContext.Builder((WorldServer) world));
        Field f = ReflectionHelper.findField(LootTable.class, "pools", "field_186466_c", "c");
        List<LootPool> pools = null;
        try {
            pools = (List<LootPool>) f.get(table);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (pools != null && pools.size() > 0) {
            for (LootPool pool : pools) {
                Field f2 = ReflectionHelper.findField(LootPool.class, "lootEntries", "field_186453_a", "a");
                List<LootEntry> entries = null;
                try {
                    entries = (List<LootEntry>) f2.get(pool);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (entries != null && entries.size() > 0) {
                    for (LootEntry entry:entries) {
                        if (entry instanceof LootEntryItem) {
                            entry.addLoot(items, new Random(), lootBuilder.build());
                        }
                    }
                }
            }
        }
        return items;
    }
}
