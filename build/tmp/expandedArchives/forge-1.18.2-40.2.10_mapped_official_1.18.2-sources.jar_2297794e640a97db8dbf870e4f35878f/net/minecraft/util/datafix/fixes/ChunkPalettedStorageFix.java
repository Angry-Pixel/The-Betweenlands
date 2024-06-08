package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.util.datafix.PackedBitStorage;
import org.slf4j.Logger;

public class ChunkPalettedStorageFix extends DataFix {
   private static final int NORTH_WEST_MASK = 128;
   private static final int WEST_MASK = 64;
   private static final int SOUTH_WEST_MASK = 32;
   private static final int SOUTH_MASK = 16;
   private static final int SOUTH_EAST_MASK = 8;
   private static final int EAST_MASK = 4;
   private static final int NORTH_EAST_MASK = 2;
   private static final int NORTH_MASK = 1;
   static final Logger LOGGER = LogUtils.getLogger();
   static final BitSet VIRTUAL = new BitSet(256);
   static final BitSet FIX = new BitSet(256);
   static final Dynamic<?> PUMPKIN = BlockStateData.parse("{Name:'minecraft:pumpkin'}");
   static final Dynamic<?> SNOWY_PODZOL = BlockStateData.parse("{Name:'minecraft:podzol',Properties:{snowy:'true'}}");
   static final Dynamic<?> SNOWY_GRASS = BlockStateData.parse("{Name:'minecraft:grass_block',Properties:{snowy:'true'}}");
   static final Dynamic<?> SNOWY_MYCELIUM = BlockStateData.parse("{Name:'minecraft:mycelium',Properties:{snowy:'true'}}");
   static final Dynamic<?> UPPER_SUNFLOWER = BlockStateData.parse("{Name:'minecraft:sunflower',Properties:{half:'upper'}}");
   static final Dynamic<?> UPPER_LILAC = BlockStateData.parse("{Name:'minecraft:lilac',Properties:{half:'upper'}}");
   static final Dynamic<?> UPPER_TALL_GRASS = BlockStateData.parse("{Name:'minecraft:tall_grass',Properties:{half:'upper'}}");
   static final Dynamic<?> UPPER_LARGE_FERN = BlockStateData.parse("{Name:'minecraft:large_fern',Properties:{half:'upper'}}");
   static final Dynamic<?> UPPER_ROSE_BUSH = BlockStateData.parse("{Name:'minecraft:rose_bush',Properties:{half:'upper'}}");
   static final Dynamic<?> UPPER_PEONY = BlockStateData.parse("{Name:'minecraft:peony',Properties:{half:'upper'}}");
   static final Map<String, Dynamic<?>> FLOWER_POT_MAP = DataFixUtils.make(Maps.newHashMap(), (p_15111_) -> {
      p_15111_.put("minecraft:air0", BlockStateData.parse("{Name:'minecraft:flower_pot'}"));
      p_15111_.put("minecraft:red_flower0", BlockStateData.parse("{Name:'minecraft:potted_poppy'}"));
      p_15111_.put("minecraft:red_flower1", BlockStateData.parse("{Name:'minecraft:potted_blue_orchid'}"));
      p_15111_.put("minecraft:red_flower2", BlockStateData.parse("{Name:'minecraft:potted_allium'}"));
      p_15111_.put("minecraft:red_flower3", BlockStateData.parse("{Name:'minecraft:potted_azure_bluet'}"));
      p_15111_.put("minecraft:red_flower4", BlockStateData.parse("{Name:'minecraft:potted_red_tulip'}"));
      p_15111_.put("minecraft:red_flower5", BlockStateData.parse("{Name:'minecraft:potted_orange_tulip'}"));
      p_15111_.put("minecraft:red_flower6", BlockStateData.parse("{Name:'minecraft:potted_white_tulip'}"));
      p_15111_.put("minecraft:red_flower7", BlockStateData.parse("{Name:'minecraft:potted_pink_tulip'}"));
      p_15111_.put("minecraft:red_flower8", BlockStateData.parse("{Name:'minecraft:potted_oxeye_daisy'}"));
      p_15111_.put("minecraft:yellow_flower0", BlockStateData.parse("{Name:'minecraft:potted_dandelion'}"));
      p_15111_.put("minecraft:sapling0", BlockStateData.parse("{Name:'minecraft:potted_oak_sapling'}"));
      p_15111_.put("minecraft:sapling1", BlockStateData.parse("{Name:'minecraft:potted_spruce_sapling'}"));
      p_15111_.put("minecraft:sapling2", BlockStateData.parse("{Name:'minecraft:potted_birch_sapling'}"));
      p_15111_.put("minecraft:sapling3", BlockStateData.parse("{Name:'minecraft:potted_jungle_sapling'}"));
      p_15111_.put("minecraft:sapling4", BlockStateData.parse("{Name:'minecraft:potted_acacia_sapling'}"));
      p_15111_.put("minecraft:sapling5", BlockStateData.parse("{Name:'minecraft:potted_dark_oak_sapling'}"));
      p_15111_.put("minecraft:red_mushroom0", BlockStateData.parse("{Name:'minecraft:potted_red_mushroom'}"));
      p_15111_.put("minecraft:brown_mushroom0", BlockStateData.parse("{Name:'minecraft:potted_brown_mushroom'}"));
      p_15111_.put("minecraft:deadbush0", BlockStateData.parse("{Name:'minecraft:potted_dead_bush'}"));
      p_15111_.put("minecraft:tallgrass2", BlockStateData.parse("{Name:'minecraft:potted_fern'}"));
      p_15111_.put("minecraft:cactus0", BlockStateData.getTag(2240));
   });
   static final Map<String, Dynamic<?>> SKULL_MAP = DataFixUtils.make(Maps.newHashMap(), (p_15108_) -> {
      mapSkull(p_15108_, 0, "skeleton", "skull");
      mapSkull(p_15108_, 1, "wither_skeleton", "skull");
      mapSkull(p_15108_, 2, "zombie", "head");
      mapSkull(p_15108_, 3, "player", "head");
      mapSkull(p_15108_, 4, "creeper", "head");
      mapSkull(p_15108_, 5, "dragon", "head");
   });
   static final Map<String, Dynamic<?>> DOOR_MAP = DataFixUtils.make(Maps.newHashMap(), (p_15105_) -> {
      mapDoor(p_15105_, "oak_door", 1024);
      mapDoor(p_15105_, "iron_door", 1136);
      mapDoor(p_15105_, "spruce_door", 3088);
      mapDoor(p_15105_, "birch_door", 3104);
      mapDoor(p_15105_, "jungle_door", 3120);
      mapDoor(p_15105_, "acacia_door", 3136);
      mapDoor(p_15105_, "dark_oak_door", 3152);
   });
   static final Map<String, Dynamic<?>> NOTE_BLOCK_MAP = DataFixUtils.make(Maps.newHashMap(), (p_15102_) -> {
      for(int i = 0; i < 26; ++i) {
         p_15102_.put("true" + i, BlockStateData.parse("{Name:'minecraft:note_block',Properties:{powered:'true',note:'" + i + "'}}"));
         p_15102_.put("false" + i, BlockStateData.parse("{Name:'minecraft:note_block',Properties:{powered:'false',note:'" + i + "'}}"));
      }

   });
   private static final Int2ObjectMap<String> DYE_COLOR_MAP = DataFixUtils.make(new Int2ObjectOpenHashMap<>(), (p_15070_) -> {
      p_15070_.put(0, "white");
      p_15070_.put(1, "orange");
      p_15070_.put(2, "magenta");
      p_15070_.put(3, "light_blue");
      p_15070_.put(4, "yellow");
      p_15070_.put(5, "lime");
      p_15070_.put(6, "pink");
      p_15070_.put(7, "gray");
      p_15070_.put(8, "light_gray");
      p_15070_.put(9, "cyan");
      p_15070_.put(10, "purple");
      p_15070_.put(11, "blue");
      p_15070_.put(12, "brown");
      p_15070_.put(13, "green");
      p_15070_.put(14, "red");
      p_15070_.put(15, "black");
   });
   static final Map<String, Dynamic<?>> BED_BLOCK_MAP = DataFixUtils.make(Maps.newHashMap(), (p_15095_) -> {
      for(Entry<String> entry : DYE_COLOR_MAP.int2ObjectEntrySet()) {
         if (!Objects.equals(entry.getValue(), "red")) {
            addBeds(p_15095_, entry.getIntKey(), entry.getValue());
         }
      }

   });
   static final Map<String, Dynamic<?>> BANNER_BLOCK_MAP = DataFixUtils.make(Maps.newHashMap(), (p_15072_) -> {
      for(Entry<String> entry : DYE_COLOR_MAP.int2ObjectEntrySet()) {
         if (!Objects.equals(entry.getValue(), "white")) {
            addBanners(p_15072_, 15 - entry.getIntKey(), entry.getValue());
         }
      }

   });
   static final Dynamic<?> AIR;
   private static final int SIZE = 4096;

   public ChunkPalettedStorageFix(Schema p_15058_, boolean p_15059_) {
      super(p_15058_, p_15059_);
   }

   private static void mapSkull(Map<String, Dynamic<?>> p_15078_, int p_15079_, String p_15080_, String p_15081_) {
      p_15078_.put(p_15079_ + "north", BlockStateData.parse("{Name:'minecraft:" + p_15080_ + "_wall_" + p_15081_ + "',Properties:{facing:'north'}}"));
      p_15078_.put(p_15079_ + "east", BlockStateData.parse("{Name:'minecraft:" + p_15080_ + "_wall_" + p_15081_ + "',Properties:{facing:'east'}}"));
      p_15078_.put(p_15079_ + "south", BlockStateData.parse("{Name:'minecraft:" + p_15080_ + "_wall_" + p_15081_ + "',Properties:{facing:'south'}}"));
      p_15078_.put(p_15079_ + "west", BlockStateData.parse("{Name:'minecraft:" + p_15080_ + "_wall_" + p_15081_ + "',Properties:{facing:'west'}}"));

      for(int i = 0; i < 16; ++i) {
         p_15078_.put("" + p_15079_ + i, BlockStateData.parse("{Name:'minecraft:" + p_15080_ + "_" + p_15081_ + "',Properties:{rotation:'" + i + "'}}"));
      }

   }

   private static void mapDoor(Map<String, Dynamic<?>> p_15083_, String p_15084_, int p_15085_) {
      p_15083_.put("minecraft:" + p_15084_ + "eastlowerleftfalsefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "eastlowerleftfalsetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "eastlowerlefttruefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "eastlowerlefttruetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "eastlowerrightfalsefalse", BlockStateData.getTag(p_15085_));
      p_15083_.put("minecraft:" + p_15084_ + "eastlowerrightfalsetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "eastlowerrighttruefalse", BlockStateData.getTag(p_15085_ + 4));
      p_15083_.put("minecraft:" + p_15084_ + "eastlowerrighttruetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "eastupperleftfalsefalse", BlockStateData.getTag(p_15085_ + 8));
      p_15083_.put("minecraft:" + p_15084_ + "eastupperleftfalsetrue", BlockStateData.getTag(p_15085_ + 10));
      p_15083_.put("minecraft:" + p_15084_ + "eastupperlefttruefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "eastupperlefttruetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "eastupperrightfalsefalse", BlockStateData.getTag(p_15085_ + 9));
      p_15083_.put("minecraft:" + p_15084_ + "eastupperrightfalsetrue", BlockStateData.getTag(p_15085_ + 11));
      p_15083_.put("minecraft:" + p_15084_ + "eastupperrighttruefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "eastupperrighttruetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "northlowerleftfalsefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "northlowerleftfalsetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "northlowerlefttruefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "northlowerlefttruetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "northlowerrightfalsefalse", BlockStateData.getTag(p_15085_ + 3));
      p_15083_.put("minecraft:" + p_15084_ + "northlowerrightfalsetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "northlowerrighttruefalse", BlockStateData.getTag(p_15085_ + 7));
      p_15083_.put("minecraft:" + p_15084_ + "northlowerrighttruetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "northupperleftfalsefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "northupperleftfalsetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "northupperlefttruefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "northupperlefttruetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "northupperrightfalsefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "northupperrightfalsetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "northupperrighttruefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "northupperrighttruetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "southlowerleftfalsefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "southlowerleftfalsetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "southlowerlefttruefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "southlowerlefttruetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "southlowerrightfalsefalse", BlockStateData.getTag(p_15085_ + 1));
      p_15083_.put("minecraft:" + p_15084_ + "southlowerrightfalsetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "southlowerrighttruefalse", BlockStateData.getTag(p_15085_ + 5));
      p_15083_.put("minecraft:" + p_15084_ + "southlowerrighttruetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "southupperleftfalsefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "southupperleftfalsetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "southupperlefttruefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "southupperlefttruetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "southupperrightfalsefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "southupperrightfalsetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "southupperrighttruefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "southupperrighttruetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "westlowerleftfalsefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "westlowerleftfalsetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "westlowerlefttruefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "westlowerlefttruetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "westlowerrightfalsefalse", BlockStateData.getTag(p_15085_ + 2));
      p_15083_.put("minecraft:" + p_15084_ + "westlowerrightfalsetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "westlowerrighttruefalse", BlockStateData.getTag(p_15085_ + 6));
      p_15083_.put("minecraft:" + p_15084_ + "westlowerrighttruetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "westupperleftfalsefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "westupperleftfalsetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "westupperlefttruefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "westupperlefttruetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "westupperrightfalsefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "westupperrightfalsetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "westupperrighttruefalse", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
      p_15083_.put("minecraft:" + p_15084_ + "westupperrighttruetrue", BlockStateData.parse("{Name:'minecraft:" + p_15084_ + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
   }

   private static void addBeds(Map<String, Dynamic<?>> p_15074_, int p_15075_, String p_15076_) {
      p_15074_.put("southfalsefoot" + p_15075_, BlockStateData.parse("{Name:'minecraft:" + p_15076_ + "_bed',Properties:{facing:'south',occupied:'false',part:'foot'}}"));
      p_15074_.put("westfalsefoot" + p_15075_, BlockStateData.parse("{Name:'minecraft:" + p_15076_ + "_bed',Properties:{facing:'west',occupied:'false',part:'foot'}}"));
      p_15074_.put("northfalsefoot" + p_15075_, BlockStateData.parse("{Name:'minecraft:" + p_15076_ + "_bed',Properties:{facing:'north',occupied:'false',part:'foot'}}"));
      p_15074_.put("eastfalsefoot" + p_15075_, BlockStateData.parse("{Name:'minecraft:" + p_15076_ + "_bed',Properties:{facing:'east',occupied:'false',part:'foot'}}"));
      p_15074_.put("southfalsehead" + p_15075_, BlockStateData.parse("{Name:'minecraft:" + p_15076_ + "_bed',Properties:{facing:'south',occupied:'false',part:'head'}}"));
      p_15074_.put("westfalsehead" + p_15075_, BlockStateData.parse("{Name:'minecraft:" + p_15076_ + "_bed',Properties:{facing:'west',occupied:'false',part:'head'}}"));
      p_15074_.put("northfalsehead" + p_15075_, BlockStateData.parse("{Name:'minecraft:" + p_15076_ + "_bed',Properties:{facing:'north',occupied:'false',part:'head'}}"));
      p_15074_.put("eastfalsehead" + p_15075_, BlockStateData.parse("{Name:'minecraft:" + p_15076_ + "_bed',Properties:{facing:'east',occupied:'false',part:'head'}}"));
      p_15074_.put("southtruehead" + p_15075_, BlockStateData.parse("{Name:'minecraft:" + p_15076_ + "_bed',Properties:{facing:'south',occupied:'true',part:'head'}}"));
      p_15074_.put("westtruehead" + p_15075_, BlockStateData.parse("{Name:'minecraft:" + p_15076_ + "_bed',Properties:{facing:'west',occupied:'true',part:'head'}}"));
      p_15074_.put("northtruehead" + p_15075_, BlockStateData.parse("{Name:'minecraft:" + p_15076_ + "_bed',Properties:{facing:'north',occupied:'true',part:'head'}}"));
      p_15074_.put("easttruehead" + p_15075_, BlockStateData.parse("{Name:'minecraft:" + p_15076_ + "_bed',Properties:{facing:'east',occupied:'true',part:'head'}}"));
   }

   private static void addBanners(Map<String, Dynamic<?>> p_15097_, int p_15098_, String p_15099_) {
      for(int i = 0; i < 16; ++i) {
         p_15097_.put(i + "_" + p_15098_, BlockStateData.parse("{Name:'minecraft:" + p_15099_ + "_banner',Properties:{rotation:'" + i + "'}}"));
      }

      p_15097_.put("north_" + p_15098_, BlockStateData.parse("{Name:'minecraft:" + p_15099_ + "_wall_banner',Properties:{facing:'north'}}"));
      p_15097_.put("south_" + p_15098_, BlockStateData.parse("{Name:'minecraft:" + p_15099_ + "_wall_banner',Properties:{facing:'south'}}"));
      p_15097_.put("west_" + p_15098_, BlockStateData.parse("{Name:'minecraft:" + p_15099_ + "_wall_banner',Properties:{facing:'west'}}"));
      p_15097_.put("east_" + p_15098_, BlockStateData.parse("{Name:'minecraft:" + p_15099_ + "_wall_banner',Properties:{facing:'east'}}"));
   }

   public static String getName(Dynamic<?> p_15065_) {
      return p_15065_.get("Name").asString("");
   }

   public static String getProperty(Dynamic<?> p_15067_, String p_15068_) {
      return p_15067_.get("Properties").get(p_15068_).asString("");
   }

   public static int idFor(CrudeIncrementalIntIdentityHashBiMap<Dynamic<?>> p_15062_, Dynamic<?> p_15063_) {
      int i = p_15062_.getId(p_15063_);
      if (i == -1) {
         i = p_15062_.add(p_15063_);
      }

      return i;
   }

   private Dynamic<?> fix(Dynamic<?> p_15093_) {
      Optional<? extends Dynamic<?>> optional = p_15093_.get("Level").result();
      return optional.isPresent() && optional.get().get("Sections").asStreamOpt().result().isPresent() ? p_15093_.set("Level", (new ChunkPalettedStorageFix.UpgradeChunk(optional.get())).write()) : p_15093_;
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.CHUNK);
      Type<?> type1 = this.getOutputSchema().getType(References.CHUNK);
      return this.writeFixAndRead("ChunkPalettedStorageFix", type, type1, this::fix);
   }

   public static int getSideMask(boolean p_15087_, boolean p_15088_, boolean p_15089_, boolean p_15090_) {
      int i = 0;
      if (p_15089_) {
         if (p_15088_) {
            i |= 2;
         } else if (p_15087_) {
            i |= 128;
         } else {
            i |= 1;
         }
      } else if (p_15090_) {
         if (p_15087_) {
            i |= 32;
         } else if (p_15088_) {
            i |= 8;
         } else {
            i |= 16;
         }
      } else if (p_15088_) {
         i |= 4;
      } else if (p_15087_) {
         i |= 64;
      }

      return i;
   }

   static {
      FIX.set(2);
      FIX.set(3);
      FIX.set(110);
      FIX.set(140);
      FIX.set(144);
      FIX.set(25);
      FIX.set(86);
      FIX.set(26);
      FIX.set(176);
      FIX.set(177);
      FIX.set(175);
      FIX.set(64);
      FIX.set(71);
      FIX.set(193);
      FIX.set(194);
      FIX.set(195);
      FIX.set(196);
      FIX.set(197);
      VIRTUAL.set(54);
      VIRTUAL.set(146);
      VIRTUAL.set(25);
      VIRTUAL.set(26);
      VIRTUAL.set(51);
      VIRTUAL.set(53);
      VIRTUAL.set(67);
      VIRTUAL.set(108);
      VIRTUAL.set(109);
      VIRTUAL.set(114);
      VIRTUAL.set(128);
      VIRTUAL.set(134);
      VIRTUAL.set(135);
      VIRTUAL.set(136);
      VIRTUAL.set(156);
      VIRTUAL.set(163);
      VIRTUAL.set(164);
      VIRTUAL.set(180);
      VIRTUAL.set(203);
      VIRTUAL.set(55);
      VIRTUAL.set(85);
      VIRTUAL.set(113);
      VIRTUAL.set(188);
      VIRTUAL.set(189);
      VIRTUAL.set(190);
      VIRTUAL.set(191);
      VIRTUAL.set(192);
      VIRTUAL.set(93);
      VIRTUAL.set(94);
      VIRTUAL.set(101);
      VIRTUAL.set(102);
      VIRTUAL.set(160);
      VIRTUAL.set(106);
      VIRTUAL.set(107);
      VIRTUAL.set(183);
      VIRTUAL.set(184);
      VIRTUAL.set(185);
      VIRTUAL.set(186);
      VIRTUAL.set(187);
      VIRTUAL.set(132);
      VIRTUAL.set(139);
      VIRTUAL.set(199);
      AIR = BlockStateData.getTag(0);
   }

   static class DataLayer {
      private static final int SIZE = 2048;
      private static final int NIBBLE_SIZE = 4;
      private final byte[] data;

      public DataLayer() {
         this.data = new byte[2048];
      }

      public DataLayer(byte[] p_15132_) {
         this.data = p_15132_;
         if (p_15132_.length != 2048) {
            throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + p_15132_.length);
         }
      }

      public int get(int p_15136_, int p_15137_, int p_15138_) {
         int i = this.getPosition(p_15137_ << 8 | p_15138_ << 4 | p_15136_);
         return this.isFirst(p_15137_ << 8 | p_15138_ << 4 | p_15136_) ? this.data[i] & 15 : this.data[i] >> 4 & 15;
      }

      private boolean isFirst(int p_15134_) {
         return (p_15134_ & 1) == 0;
      }

      private int getPosition(int p_15140_) {
         return p_15140_ >> 1;
      }
   }

   public static enum Direction {
      DOWN(ChunkPalettedStorageFix.Direction.AxisDirection.NEGATIVE, ChunkPalettedStorageFix.Direction.Axis.Y),
      UP(ChunkPalettedStorageFix.Direction.AxisDirection.POSITIVE, ChunkPalettedStorageFix.Direction.Axis.Y),
      NORTH(ChunkPalettedStorageFix.Direction.AxisDirection.NEGATIVE, ChunkPalettedStorageFix.Direction.Axis.Z),
      SOUTH(ChunkPalettedStorageFix.Direction.AxisDirection.POSITIVE, ChunkPalettedStorageFix.Direction.Axis.Z),
      WEST(ChunkPalettedStorageFix.Direction.AxisDirection.NEGATIVE, ChunkPalettedStorageFix.Direction.Axis.X),
      EAST(ChunkPalettedStorageFix.Direction.AxisDirection.POSITIVE, ChunkPalettedStorageFix.Direction.Axis.X);

      private final ChunkPalettedStorageFix.Direction.Axis axis;
      private final ChunkPalettedStorageFix.Direction.AxisDirection axisDirection;

      private Direction(ChunkPalettedStorageFix.Direction.AxisDirection p_15154_, ChunkPalettedStorageFix.Direction.Axis p_15155_) {
         this.axis = p_15155_;
         this.axisDirection = p_15154_;
      }

      public ChunkPalettedStorageFix.Direction.AxisDirection getAxisDirection() {
         return this.axisDirection;
      }

      public ChunkPalettedStorageFix.Direction.Axis getAxis() {
         return this.axis;
      }

      public static enum Axis {
         X,
         Y,
         Z;
      }

      public static enum AxisDirection {
         POSITIVE(1),
         NEGATIVE(-1);

         private final int step;

         private AxisDirection(int p_15180_) {
            this.step = p_15180_;
         }

         public int getStep() {
            return this.step;
         }
      }
   }

   static class Section {
      private final CrudeIncrementalIntIdentityHashBiMap<Dynamic<?>> palette = CrudeIncrementalIntIdentityHashBiMap.create(32);
      private final List<Dynamic<?>> listTag;
      private final Dynamic<?> section;
      private final boolean hasData;
      final Int2ObjectMap<IntList> toFix = new Int2ObjectLinkedOpenHashMap<>();
      final IntList update = new IntArrayList();
      public final int y;
      private final Set<Dynamic<?>> seen = Sets.newIdentityHashSet();
      private final int[] buffer = new int[4096];

      public Section(Dynamic<?> p_15195_) {
         this.listTag = Lists.newArrayList();
         this.section = p_15195_;
         this.y = p_15195_.get("Y").asInt(0);
         this.hasData = p_15195_.get("Blocks").result().isPresent();
      }

      public Dynamic<?> getBlock(int p_15198_) {
         if (p_15198_ >= 0 && p_15198_ <= 4095) {
            Dynamic<?> dynamic = this.palette.byId(this.buffer[p_15198_]);
            return dynamic == null ? ChunkPalettedStorageFix.AIR : dynamic;
         } else {
            return ChunkPalettedStorageFix.AIR;
         }
      }

      public void setBlock(int p_15203_, Dynamic<?> p_15204_) {
         if (this.seen.add(p_15204_)) {
            this.listTag.add("%%FILTER_ME%%".equals(ChunkPalettedStorageFix.getName(p_15204_)) ? ChunkPalettedStorageFix.AIR : p_15204_);
         }

         this.buffer[p_15203_] = ChunkPalettedStorageFix.idFor(this.palette, p_15204_);
      }

      public int upgrade(int p_15210_) {
         if (!this.hasData) {
            return p_15210_;
         } else {
            ByteBuffer bytebuffer = this.section.get("Blocks").asByteBufferOpt().result().get();
            ChunkPalettedStorageFix.DataLayer chunkpalettedstoragefix$datalayer = this.section.get("Data").asByteBufferOpt().map((p_15214_) -> {
               return new ChunkPalettedStorageFix.DataLayer(DataFixUtils.toArray(p_15214_));
            }).result().orElseGet(ChunkPalettedStorageFix.DataLayer::new);
            ChunkPalettedStorageFix.DataLayer chunkpalettedstoragefix$datalayer1 = this.section.get("Add").asByteBufferOpt().map((p_15208_) -> {
               return new ChunkPalettedStorageFix.DataLayer(DataFixUtils.toArray(p_15208_));
            }).result().orElseGet(ChunkPalettedStorageFix.DataLayer::new);
            this.seen.add(ChunkPalettedStorageFix.AIR);
            ChunkPalettedStorageFix.idFor(this.palette, ChunkPalettedStorageFix.AIR);
            this.listTag.add(ChunkPalettedStorageFix.AIR);

            for(int i = 0; i < 4096; ++i) {
               int j = i & 15;
               int k = i >> 8 & 15;
               int l = i >> 4 & 15;
               int i1 = chunkpalettedstoragefix$datalayer1.get(j, k, l) << 12 | (bytebuffer.get(i) & 255) << 4 | chunkpalettedstoragefix$datalayer.get(j, k, l);
               if (ChunkPalettedStorageFix.FIX.get(i1 >> 4)) {
                  this.addFix(i1 >> 4, i);
               }

               if (ChunkPalettedStorageFix.VIRTUAL.get(i1 >> 4)) {
                  int j1 = ChunkPalettedStorageFix.getSideMask(j == 0, j == 15, l == 0, l == 15);
                  if (j1 == 0) {
                     this.update.add(i);
                  } else {
                     p_15210_ |= j1;
                  }
               }

               this.setBlock(i, BlockStateData.getTag(i1));
            }

            return p_15210_;
         }
      }

      private void addFix(int p_15200_, int p_15201_) {
         IntList intlist = this.toFix.get(p_15200_);
         if (intlist == null) {
            intlist = new IntArrayList();
            this.toFix.put(p_15200_, intlist);
         }

         intlist.add(p_15201_);
      }

      public Dynamic<?> write() {
         Dynamic<?> dynamic = this.section;
         if (!this.hasData) {
            return dynamic;
         } else {
            dynamic = dynamic.set("Palette", dynamic.createList(this.listTag.stream()));
            int i = Math.max(4, DataFixUtils.ceillog2(this.seen.size()));
            PackedBitStorage packedbitstorage = new PackedBitStorage(i, 4096);

            for(int j = 0; j < this.buffer.length; ++j) {
               packedbitstorage.set(j, this.buffer[j]);
            }

            dynamic = dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(packedbitstorage.getRaw())));
            dynamic = dynamic.remove("Blocks");
            dynamic = dynamic.remove("Data");
            return dynamic.remove("Add");
         }
      }
   }

   static final class UpgradeChunk {
      private int sides;
      private final ChunkPalettedStorageFix.Section[] sections = new ChunkPalettedStorageFix.Section[16];
      private final Dynamic<?> level;
      private final int x;
      private final int z;
      private final Int2ObjectMap<Dynamic<?>> blockEntities = new Int2ObjectLinkedOpenHashMap<>(16);

      public UpgradeChunk(Dynamic<?> p_15222_) {
         this.level = p_15222_;
         this.x = p_15222_.get("xPos").asInt(0) << 4;
         this.z = p_15222_.get("zPos").asInt(0) << 4;
         p_15222_.get("TileEntities").asStreamOpt().result().ifPresent((p_15241_) -> {
            p_15241_.forEach((p_145228_) -> {
               int l3 = p_145228_.get("x").asInt(0) - this.x & 15;
               int i4 = p_145228_.get("y").asInt(0);
               int j4 = p_145228_.get("z").asInt(0) - this.z & 15;
               int k4 = i4 << 8 | j4 << 4 | l3;
               if (this.blockEntities.put(k4, p_145228_) != null) {
                  ChunkPalettedStorageFix.LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", this.x, this.z, l3, i4, j4);
               }

            });
         });
         boolean flag = p_15222_.get("convertedFromAlphaFormat").asBoolean(false);
         p_15222_.get("Sections").asStreamOpt().result().ifPresent((p_15235_) -> {
            p_15235_.forEach((p_145226_) -> {
               ChunkPalettedStorageFix.Section chunkpalettedstoragefix$section1 = new ChunkPalettedStorageFix.Section(p_145226_);
               this.sides = chunkpalettedstoragefix$section1.upgrade(this.sides);
               this.sections[chunkpalettedstoragefix$section1.y] = chunkpalettedstoragefix$section1;
            });
         });

         for(ChunkPalettedStorageFix.Section chunkpalettedstoragefix$section : this.sections) {
            if (chunkpalettedstoragefix$section != null) {
               for(java.util.Map.Entry<Integer, IntList> entry : chunkpalettedstoragefix$section.toFix.entrySet()) {
                  int i = chunkpalettedstoragefix$section.y << 12;
                  switch(entry.getKey()) {
                  case 2:
                     for(int i3 : entry.getValue()) {
                        i3 |= i;
                        Dynamic<?> dynamic11 = this.getBlock(i3);
                        if ("minecraft:grass_block".equals(ChunkPalettedStorageFix.getName(dynamic11))) {
                           String s12 = ChunkPalettedStorageFix.getName(this.getBlock(relative(i3, ChunkPalettedStorageFix.Direction.UP)));
                           if ("minecraft:snow".equals(s12) || "minecraft:snow_layer".equals(s12)) {
                              this.setBlock(i3, ChunkPalettedStorageFix.SNOWY_GRASS);
                           }
                        }
                     }
                     break;
                  case 3:
                     for(int l2 : entry.getValue()) {
                        l2 |= i;
                        Dynamic<?> dynamic10 = this.getBlock(l2);
                        if ("minecraft:podzol".equals(ChunkPalettedStorageFix.getName(dynamic10))) {
                           String s11 = ChunkPalettedStorageFix.getName(this.getBlock(relative(l2, ChunkPalettedStorageFix.Direction.UP)));
                           if ("minecraft:snow".equals(s11) || "minecraft:snow_layer".equals(s11)) {
                              this.setBlock(l2, ChunkPalettedStorageFix.SNOWY_PODZOL);
                           }
                        }
                     }
                     break;
                  case 25:
                     for(int k2 : entry.getValue()) {
                        k2 |= i;
                        Dynamic<?> dynamic9 = this.removeBlockEntity(k2);
                        if (dynamic9 != null) {
                           String s10 = Boolean.toString(dynamic9.get("powered").asBoolean(false)) + (byte)Math.min(Math.max(dynamic9.get("note").asInt(0), 0), 24);
                           this.setBlock(k2, ChunkPalettedStorageFix.NOTE_BLOCK_MAP.getOrDefault(s10, ChunkPalettedStorageFix.NOTE_BLOCK_MAP.get("false0")));
                        }
                     }
                     break;
                  case 26:
                     for(int j2 : entry.getValue()) {
                        j2 |= i;
                        Dynamic<?> dynamic8 = this.getBlockEntity(j2);
                        Dynamic<?> dynamic14 = this.getBlock(j2);
                        if (dynamic8 != null) {
                           int k3 = dynamic8.get("color").asInt(0);
                           if (k3 != 14 && k3 >= 0 && k3 < 16) {
                              String s16 = ChunkPalettedStorageFix.getProperty(dynamic14, "facing") + ChunkPalettedStorageFix.getProperty(dynamic14, "occupied") + ChunkPalettedStorageFix.getProperty(dynamic14, "part") + k3;
                              if (ChunkPalettedStorageFix.BED_BLOCK_MAP.containsKey(s16)) {
                                 this.setBlock(j2, ChunkPalettedStorageFix.BED_BLOCK_MAP.get(s16));
                              }
                           }
                        }
                     }
                     break;
                  case 64:
                  case 71:
                  case 193:
                  case 194:
                  case 195:
                  case 196:
                  case 197:
                     for(int i2 : entry.getValue()) {
                        i2 |= i;
                        Dynamic<?> dynamic7 = this.getBlock(i2);
                        if (ChunkPalettedStorageFix.getName(dynamic7).endsWith("_door")) {
                           Dynamic<?> dynamic13 = this.getBlock(i2);
                           if ("lower".equals(ChunkPalettedStorageFix.getProperty(dynamic13, "half"))) {
                              int j3 = relative(i2, ChunkPalettedStorageFix.Direction.UP);
                              Dynamic<?> dynamic15 = this.getBlock(j3);
                              String s1 = ChunkPalettedStorageFix.getName(dynamic13);
                              if (s1.equals(ChunkPalettedStorageFix.getName(dynamic15))) {
                                 String s2 = ChunkPalettedStorageFix.getProperty(dynamic13, "facing");
                                 String s3 = ChunkPalettedStorageFix.getProperty(dynamic13, "open");
                                 String s4 = flag ? "left" : ChunkPalettedStorageFix.getProperty(dynamic15, "hinge");
                                 String s5 = flag ? "false" : ChunkPalettedStorageFix.getProperty(dynamic15, "powered");
                                 this.setBlock(i2, ChunkPalettedStorageFix.DOOR_MAP.get(s1 + s2 + "lower" + s4 + s3 + s5));
                                 this.setBlock(j3, ChunkPalettedStorageFix.DOOR_MAP.get(s1 + s2 + "upper" + s4 + s3 + s5));
                              }
                           }
                        }
                     }
                     break;
                  case 86:
                     for(int l1 : entry.getValue()) {
                        l1 |= i;
                        Dynamic<?> dynamic6 = this.getBlock(l1);
                        if ("minecraft:carved_pumpkin".equals(ChunkPalettedStorageFix.getName(dynamic6))) {
                           String s9 = ChunkPalettedStorageFix.getName(this.getBlock(relative(l1, ChunkPalettedStorageFix.Direction.DOWN)));
                           if ("minecraft:grass_block".equals(s9) || "minecraft:dirt".equals(s9)) {
                              this.setBlock(l1, ChunkPalettedStorageFix.PUMPKIN);
                           }
                        }
                     }
                     break;
                  case 110:
                     for(int k1 : entry.getValue()) {
                        k1 |= i;
                        Dynamic<?> dynamic5 = this.getBlock(k1);
                        if ("minecraft:mycelium".equals(ChunkPalettedStorageFix.getName(dynamic5))) {
                           String s8 = ChunkPalettedStorageFix.getName(this.getBlock(relative(k1, ChunkPalettedStorageFix.Direction.UP)));
                           if ("minecraft:snow".equals(s8) || "minecraft:snow_layer".equals(s8)) {
                              this.setBlock(k1, ChunkPalettedStorageFix.SNOWY_MYCELIUM);
                           }
                        }
                     }
                     break;
                  case 140:
                     for(int j1 : entry.getValue()) {
                        j1 |= i;
                        Dynamic<?> dynamic4 = this.removeBlockEntity(j1);
                        if (dynamic4 != null) {
                           String s7 = dynamic4.get("Item").asString("") + dynamic4.get("Data").asInt(0);
                           this.setBlock(j1, ChunkPalettedStorageFix.FLOWER_POT_MAP.getOrDefault(s7, ChunkPalettedStorageFix.FLOWER_POT_MAP.get("minecraft:air0")));
                        }
                     }
                     break;
                  case 144:
                     for(int i1 : entry.getValue()) {
                        i1 |= i;
                        Dynamic<?> dynamic3 = this.getBlockEntity(i1);
                        if (dynamic3 != null) {
                           String s6 = String.valueOf(dynamic3.get("SkullType").asInt(0));
                           String s14 = ChunkPalettedStorageFix.getProperty(this.getBlock(i1), "facing");
                           String s15;
                           if (!"up".equals(s14) && !"down".equals(s14)) {
                              s15 = s6 + s14;
                           } else {
                              s15 = s6 + String.valueOf(dynamic3.get("Rot").asInt(0));
                           }

                           dynamic3.remove("SkullType");
                           dynamic3.remove("facing");
                           dynamic3.remove("Rot");
                           this.setBlock(i1, ChunkPalettedStorageFix.SKULL_MAP.getOrDefault(s15, ChunkPalettedStorageFix.SKULL_MAP.get("0north")));
                        }
                     }
                     break;
                  case 175:
                     for(int l : entry.getValue()) {
                        l |= i;
                        Dynamic<?> dynamic2 = this.getBlock(l);
                        if ("upper".equals(ChunkPalettedStorageFix.getProperty(dynamic2, "half"))) {
                           Dynamic<?> dynamic12 = this.getBlock(relative(l, ChunkPalettedStorageFix.Direction.DOWN));
                           String s13 = ChunkPalettedStorageFix.getName(dynamic12);
                           if ("minecraft:sunflower".equals(s13)) {
                              this.setBlock(l, ChunkPalettedStorageFix.UPPER_SUNFLOWER);
                           } else if ("minecraft:lilac".equals(s13)) {
                              this.setBlock(l, ChunkPalettedStorageFix.UPPER_LILAC);
                           } else if ("minecraft:tall_grass".equals(s13)) {
                              this.setBlock(l, ChunkPalettedStorageFix.UPPER_TALL_GRASS);
                           } else if ("minecraft:large_fern".equals(s13)) {
                              this.setBlock(l, ChunkPalettedStorageFix.UPPER_LARGE_FERN);
                           } else if ("minecraft:rose_bush".equals(s13)) {
                              this.setBlock(l, ChunkPalettedStorageFix.UPPER_ROSE_BUSH);
                           } else if ("minecraft:peony".equals(s13)) {
                              this.setBlock(l, ChunkPalettedStorageFix.UPPER_PEONY);
                           }
                        }
                     }
                     break;
                  case 176:
                  case 177:
                     for(int j : entry.getValue()) {
                        j |= i;
                        Dynamic<?> dynamic = this.getBlockEntity(j);
                        Dynamic<?> dynamic1 = this.getBlock(j);
                        if (dynamic != null) {
                           int k = dynamic.get("Base").asInt(0);
                           if (k != 15 && k >= 0 && k < 16) {
                              String s = ChunkPalettedStorageFix.getProperty(dynamic1, entry.getKey() == 176 ? "rotation" : "facing") + "_" + k;
                              if (ChunkPalettedStorageFix.BANNER_BLOCK_MAP.containsKey(s)) {
                                 this.setBlock(j, ChunkPalettedStorageFix.BANNER_BLOCK_MAP.get(s));
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

      }

      @Nullable
      private Dynamic<?> getBlockEntity(int p_15237_) {
         return this.blockEntities.get(p_15237_);
      }

      @Nullable
      private Dynamic<?> removeBlockEntity(int p_15243_) {
         return this.blockEntities.remove(p_15243_);
      }

      public static int relative(int p_15227_, ChunkPalettedStorageFix.Direction p_15228_) {
         switch(p_15228_.getAxis()) {
         case X:
            int i = (p_15227_ & 15) + p_15228_.getAxisDirection().getStep();
            return i >= 0 && i <= 15 ? p_15227_ & -16 | i : -1;
         case Y:
            int j = (p_15227_ >> 8) + p_15228_.getAxisDirection().getStep();
            return j >= 0 && j <= 255 ? p_15227_ & 255 | j << 8 : -1;
         case Z:
            int k = (p_15227_ >> 4 & 15) + p_15228_.getAxisDirection().getStep();
            return k >= 0 && k <= 15 ? p_15227_ & -241 | k << 4 : -1;
         default:
            return -1;
         }
      }

      private void setBlock(int p_15230_, Dynamic<?> p_15231_) {
         if (p_15230_ >= 0 && p_15230_ <= 65535) {
            ChunkPalettedStorageFix.Section chunkpalettedstoragefix$section = this.getSection(p_15230_);
            if (chunkpalettedstoragefix$section != null) {
               chunkpalettedstoragefix$section.setBlock(p_15230_ & 4095, p_15231_);
            }
         }
      }

      @Nullable
      private ChunkPalettedStorageFix.Section getSection(int p_15245_) {
         int i = p_15245_ >> 12;
         return i < this.sections.length ? this.sections[i] : null;
      }

      public Dynamic<?> getBlock(int p_15225_) {
         if (p_15225_ >= 0 && p_15225_ <= 65535) {
            ChunkPalettedStorageFix.Section chunkpalettedstoragefix$section = this.getSection(p_15225_);
            return chunkpalettedstoragefix$section == null ? ChunkPalettedStorageFix.AIR : chunkpalettedstoragefix$section.getBlock(p_15225_ & 4095);
         } else {
            return ChunkPalettedStorageFix.AIR;
         }
      }

      public Dynamic<?> write() {
         Dynamic<?> dynamic = this.level;
         if (this.blockEntities.isEmpty()) {
            dynamic = dynamic.remove("TileEntities");
         } else {
            dynamic = dynamic.set("TileEntities", dynamic.createList(this.blockEntities.values().stream()));
         }

         Dynamic<?> dynamic1 = dynamic.emptyMap();
         List<Dynamic<?>> list = Lists.newArrayList();

         for(ChunkPalettedStorageFix.Section chunkpalettedstoragefix$section : this.sections) {
            if (chunkpalettedstoragefix$section != null) {
               list.add(chunkpalettedstoragefix$section.write());
               dynamic1 = dynamic1.set(String.valueOf(chunkpalettedstoragefix$section.y), dynamic1.createIntList(Arrays.stream(chunkpalettedstoragefix$section.update.toIntArray())));
            }
         }

         Dynamic<?> dynamic2 = dynamic.emptyMap();
         dynamic2 = dynamic2.set("Sides", dynamic2.createByte((byte)this.sides));
         dynamic2 = dynamic2.set("Indices", dynamic1);
         return dynamic.set("UpgradeData", dynamic2).set("Sections", dynamic2.createList(list.stream()));
      }
   }
}