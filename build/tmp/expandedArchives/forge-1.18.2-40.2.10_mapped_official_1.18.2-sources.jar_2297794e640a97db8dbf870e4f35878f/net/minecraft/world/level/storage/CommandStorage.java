package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.SavedData;

public class CommandStorage {
   private static final String ID_PREFIX = "command_storage_";
   private final Map<String, CommandStorage.Container> namespaces = Maps.newHashMap();
   private final DimensionDataStorage storage;

   public CommandStorage(DimensionDataStorage p_78035_) {
      this.storage = p_78035_;
   }

   private CommandStorage.Container newStorage(String p_164836_) {
      CommandStorage.Container commandstorage$container = new CommandStorage.Container();
      this.namespaces.put(p_164836_, commandstorage$container);
      return commandstorage$container;
   }

   public CompoundTag get(ResourceLocation p_78045_) {
      String s = p_78045_.getNamespace();
      CommandStorage.Container commandstorage$container = this.storage.get((p_164844_) -> {
         return this.newStorage(s).load(p_164844_);
      }, createId(s));
      return commandstorage$container != null ? commandstorage$container.get(p_78045_.getPath()) : new CompoundTag();
   }

   public void set(ResourceLocation p_78047_, CompoundTag p_78048_) {
      String s = p_78047_.getNamespace();
      this.storage.computeIfAbsent((p_164839_) -> {
         return this.newStorage(s).load(p_164839_);
      }, () -> {
         return this.newStorage(s);
      }, createId(s)).put(p_78047_.getPath(), p_78048_);
   }

   public Stream<ResourceLocation> keys() {
      return this.namespaces.entrySet().stream().flatMap((p_164841_) -> {
         return p_164841_.getValue().getKeys(p_164841_.getKey());
      });
   }

   private static String createId(String p_78038_) {
      return "command_storage_" + p_78038_;
   }

   static class Container extends SavedData {
      private static final String TAG_CONTENTS = "contents";
      private final Map<String, CompoundTag> storage = Maps.newHashMap();

      CommandStorage.Container load(CompoundTag p_164850_) {
         CompoundTag compoundtag = p_164850_.getCompound("contents");

         for(String s : compoundtag.getAllKeys()) {
            this.storage.put(s, compoundtag.getCompound(s));
         }

         return this;
      }

      public CompoundTag save(CompoundTag p_78075_) {
         CompoundTag compoundtag = new CompoundTag();
         this.storage.forEach((p_78070_, p_78071_) -> {
            compoundtag.put(p_78070_, p_78071_.copy());
         });
         p_78075_.put("contents", compoundtag);
         return p_78075_;
      }

      public CompoundTag get(String p_78059_) {
         CompoundTag compoundtag = this.storage.get(p_78059_);
         return compoundtag != null ? compoundtag : new CompoundTag();
      }

      public void put(String p_78064_, CompoundTag p_78065_) {
         if (p_78065_.isEmpty()) {
            this.storage.remove(p_78064_);
         } else {
            this.storage.put(p_78064_, p_78065_);
         }

         this.setDirty();
      }

      public Stream<ResourceLocation> getKeys(String p_78073_) {
         return this.storage.keySet().stream().map((p_78062_) -> {
            return new ResourceLocation(p_78073_, p_78062_);
         });
      }
   }
}