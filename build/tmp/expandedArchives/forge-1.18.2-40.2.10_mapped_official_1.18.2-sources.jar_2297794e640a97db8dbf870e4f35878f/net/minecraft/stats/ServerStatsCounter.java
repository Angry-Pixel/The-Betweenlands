package net.minecraft.stats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class ServerStatsCounter extends StatsCounter {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final MinecraftServer server;
   private final File file;
   private final Set<Stat<?>> dirty = Sets.newHashSet();

   public ServerStatsCounter(MinecraftServer p_12816_, File p_12817_) {
      this.server = p_12816_;
      this.file = p_12817_;
      if (p_12817_.isFile()) {
         try {
            this.parseLocal(p_12816_.getFixerUpper(), FileUtils.readFileToString(p_12817_));
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't read statistics file {}", p_12817_, ioexception);
         } catch (JsonParseException jsonparseexception) {
            LOGGER.error("Couldn't parse statistics file {}", p_12817_, jsonparseexception);
         }
      }

   }

   public void save() {
      try {
         FileUtils.writeStringToFile(this.file, this.toJson());
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't save stats", (Throwable)ioexception);
      }

   }

   public void setValue(Player p_12827_, Stat<?> p_12828_, int p_12829_) {
      super.setValue(p_12827_, p_12828_, p_12829_);
      this.dirty.add(p_12828_);
   }

   private Set<Stat<?>> getDirty() {
      Set<Stat<?>> set = Sets.newHashSet(this.dirty);
      this.dirty.clear();
      return set;
   }

   public void parseLocal(DataFixer p_12833_, String p_12834_) {
      try {
         JsonReader jsonreader = new JsonReader(new StringReader(p_12834_));

         label51: {
            try {
               jsonreader.setLenient(false);
               JsonElement jsonelement = Streams.parse(jsonreader);
               if (!jsonelement.isJsonNull()) {
                  CompoundTag compoundtag = fromJson(jsonelement.getAsJsonObject());
                  if (!compoundtag.contains("DataVersion", 99)) {
                     compoundtag.putInt("DataVersion", 1343);
                  }

                  compoundtag = NbtUtils.update(p_12833_, DataFixTypes.STATS, compoundtag, compoundtag.getInt("DataVersion"));
                  if (!compoundtag.contains("stats", 10)) {
                     break label51;
                  }

                  CompoundTag compoundtag1 = compoundtag.getCompound("stats");
                  Iterator iterator = compoundtag1.getAllKeys().iterator();

                  while(true) {
                     if (!iterator.hasNext()) {
                        break label51;
                     }

                     String s = (String)iterator.next();
                     if (compoundtag1.contains(s, 10)) {
                        Util.ifElse(Registry.STAT_TYPE.getOptional(new ResourceLocation(s)), (p_12844_) -> {
                           CompoundTag compoundtag2 = compoundtag1.getCompound(s);

                           for(String s1 : compoundtag2.getAllKeys()) {
                              if (compoundtag2.contains(s1, 99)) {
                                 Util.ifElse(this.getStat(p_12844_, s1), (p_144252_) -> {
                                    this.stats.put(p_144252_, compoundtag2.getInt(s1));
                                 }, () -> {
                                    LOGGER.warn("Invalid statistic in {}: Don't know what {} is", this.file, s1);
                                 });
                              } else {
                                 LOGGER.warn("Invalid statistic value in {}: Don't know what {} is for key {}", this.file, compoundtag2.get(s1), s1);
                              }
                           }

                        }, () -> {
                           LOGGER.warn("Invalid statistic type in {}: Don't know what {} is", this.file, s);
                        });
                     }
                  }
               }

               LOGGER.error("Unable to parse Stat data from {}", (Object)this.file);
            } catch (Throwable throwable1) {
               try {
                  jsonreader.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }

               throw throwable1;
            }

            jsonreader.close();
            return;
         }

         jsonreader.close();
      } catch (IOException | JsonParseException jsonparseexception) {
         LOGGER.error("Unable to parse Stat data from {}", this.file, jsonparseexception);
      }

   }

   private <T> Optional<Stat<T>> getStat(StatType<T> p_12824_, String p_12825_) {
      return Optional.ofNullable(ResourceLocation.tryParse(p_12825_)).flatMap(p_12824_.getRegistry()::getOptional).map(p_12824_::get);
   }

   private static CompoundTag fromJson(JsonObject p_12831_) {
      CompoundTag compoundtag = new CompoundTag();

      for(Entry<String, JsonElement> entry : p_12831_.entrySet()) {
         JsonElement jsonelement = entry.getValue();
         if (jsonelement.isJsonObject()) {
            compoundtag.put(entry.getKey(), fromJson(jsonelement.getAsJsonObject()));
         } else if (jsonelement.isJsonPrimitive()) {
            JsonPrimitive jsonprimitive = jsonelement.getAsJsonPrimitive();
            if (jsonprimitive.isNumber()) {
               compoundtag.putInt(entry.getKey(), jsonprimitive.getAsInt());
            }
         }
      }

      return compoundtag;
   }

   protected String toJson() {
      Map<StatType<?>, JsonObject> map = Maps.newHashMap();

      for(it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Stat<?>> entry : this.stats.object2IntEntrySet()) {
         Stat<?> stat = entry.getKey();
         map.computeIfAbsent(stat.getType(), (p_12822_) -> {
            return new JsonObject();
         }).addProperty(getKey(stat).toString(), entry.getIntValue());
      }

      JsonObject jsonobject = new JsonObject();

      for(Entry<StatType<?>, JsonObject> entry1 : map.entrySet()) {
         jsonobject.add(Registry.STAT_TYPE.getKey(entry1.getKey()).toString(), entry1.getValue());
      }

      JsonObject jsonobject1 = new JsonObject();
      jsonobject1.add("stats", jsonobject);
      jsonobject1.addProperty("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      return jsonobject1.toString();
   }

   private static <T> ResourceLocation getKey(Stat<T> p_12847_) {
      return p_12847_.getType().getRegistry().getKey(p_12847_.getValue());
   }

   public void markAllDirty() {
      this.dirty.addAll(this.stats.keySet());
   }

   public void sendStats(ServerPlayer p_12820_) {
      Object2IntMap<Stat<?>> object2intmap = new Object2IntOpenHashMap<>();

      for(Stat<?> stat : this.getDirty()) {
         object2intmap.put(stat, this.getValue(stat));
      }

      p_12820_.connection.send(new ClientboundAwardStatsPacket(object2intmap));
   }
}