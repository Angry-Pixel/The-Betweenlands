package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public abstract class StoredUserList<K, V extends StoredUserEntry<K>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final File file;
   private final Map<String, V> map = Maps.newHashMap();

   public StoredUserList(File p_11380_) {
      this.file = p_11380_;
   }

   public File getFile() {
      return this.file;
   }

   public void add(V p_11382_) {
      this.map.put(this.getKeyForUser(p_11382_.getUser()), p_11382_);

      try {
         this.save();
      } catch (IOException ioexception) {
         LOGGER.warn("Could not save the list after adding a user.", (Throwable)ioexception);
      }

   }

   @Nullable
   public V get(K p_11389_) {
      this.removeExpired();
      return this.map.get(this.getKeyForUser(p_11389_));
   }

   public void remove(K p_11394_) {
      this.map.remove(this.getKeyForUser(p_11394_));

      try {
         this.save();
      } catch (IOException ioexception) {
         LOGGER.warn("Could not save the list after removing a user.", (Throwable)ioexception);
      }

   }

   public void remove(StoredUserEntry<K> p_11387_) {
      this.remove(p_11387_.getUser());
   }

   public String[] getUserList() {
      return this.map.keySet().toArray(new String[0]);
   }

   public boolean isEmpty() {
      return this.map.size() < 1;
   }

   protected String getKeyForUser(K p_11384_) {
      return p_11384_.toString();
   }

   protected boolean contains(K p_11397_) {
      return this.map.containsKey(this.getKeyForUser(p_11397_));
   }

   private void removeExpired() {
      List<K> list = Lists.newArrayList();

      for(V v : this.map.values()) {
         if (v.hasExpired()) {
            list.add(v.getUser());
         }
      }

      for(K k : list) {
         this.map.remove(this.getKeyForUser(k));
      }

   }

   protected abstract StoredUserEntry<K> createEntry(JsonObject p_11383_);

   public Collection<V> getEntries() {
      return this.map.values();
   }

   public void save() throws IOException {
      JsonArray jsonarray = new JsonArray();
      this.map.values().stream().map((p_11392_) -> {
         return Util.make(new JsonObject(), p_11392_::serialize);
      }).forEach(jsonarray::add);
      BufferedWriter bufferedwriter = Files.newWriter(this.file, StandardCharsets.UTF_8);

      try {
         GSON.toJson((JsonElement)jsonarray, bufferedwriter);
      } catch (Throwable throwable1) {
         if (bufferedwriter != null) {
            try {
               bufferedwriter.close();
            } catch (Throwable throwable) {
               throwable1.addSuppressed(throwable);
            }
         }

         throw throwable1;
      }

      if (bufferedwriter != null) {
         bufferedwriter.close();
      }

   }

   public void load() throws IOException {
      if (this.file.exists()) {
         BufferedReader bufferedreader = Files.newReader(this.file, StandardCharsets.UTF_8);

         try {
            JsonArray jsonarray = GSON.fromJson(bufferedreader, JsonArray.class);
            this.map.clear();

            for(JsonElement jsonelement : jsonarray) {
               JsonObject jsonobject = GsonHelper.convertToJsonObject(jsonelement, "entry");
               StoredUserEntry<K> storeduserentry = this.createEntry(jsonobject);
               if (storeduserentry.getUser() != null) {
                  this.map.put(this.getKeyForUser(storeduserentry.getUser()), (V)storeduserentry);
               }
            }
         } catch (Throwable throwable1) {
            if (bufferedreader != null) {
               try {
                  bufferedreader.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }
            }

            throw throwable1;
         }

         if (bufferedreader != null) {
            bufferedreader.close();
         }

      }
   }
}