package thebetweenlands.common.registries;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;

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

    private static ResourceLocation register(String id) {
        return LootTableList.register(new ResourceLocation("thebetweenlands", id));
    }
}
