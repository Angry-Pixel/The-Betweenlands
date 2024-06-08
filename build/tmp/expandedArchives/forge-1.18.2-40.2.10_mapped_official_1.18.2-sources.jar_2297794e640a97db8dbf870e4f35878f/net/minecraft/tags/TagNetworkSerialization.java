package net.minecraft.tags;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class TagNetworkSerialization {
   public static Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> serializeTagsToNetwork(RegistryAccess p_203951_) {
      return p_203951_.networkSafeRegistries().map((p_203949_) -> {
         return Pair.of(p_203949_.key(), serializeToNetwork(p_203949_.value()));
      }).filter((p_203941_) -> {
         return !p_203941_.getSecond().isEmpty();
      }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
   }

   private static <T> TagNetworkSerialization.NetworkPayload serializeToNetwork(Registry<T> p_203943_) {
      Map<ResourceLocation, IntList> map = new HashMap<>();
      p_203943_.getTags().forEach((p_203947_) -> {
         HolderSet<T> holderset = p_203947_.getSecond();
         IntList intlist = new IntArrayList(holderset.size());

         for(Holder<T> holder : holderset) {
            if (holder.kind() != Holder.Kind.REFERENCE) {
               throw new IllegalStateException("Can't serialize unregistered value " + holder);
            }

            intlist.add(p_203943_.getId(holder.value()));
         }

         map.put(p_203947_.getFirst().location(), intlist);
      });
      return new TagNetworkSerialization.NetworkPayload(map);
   }

   public static <T> void deserializeTagsFromNetwork(ResourceKey<? extends Registry<T>> p_203953_, Registry<T> p_203954_, TagNetworkSerialization.NetworkPayload p_203955_, TagNetworkSerialization.TagOutput<T> p_203956_) {
      p_203955_.tags.forEach((p_203961_, p_203962_) -> {
         TagKey<T> tagkey = TagKey.create(p_203953_, p_203961_);
         List<Holder<T>> list = p_203962_.intStream().mapToObj(p_203954_::getHolder).flatMap(Optional::stream).toList();
         p_203956_.accept(tagkey, list);
      });
   }

   public static final class NetworkPayload {
      final Map<ResourceLocation, IntList> tags;

      NetworkPayload(Map<ResourceLocation, IntList> p_203965_) {
         this.tags = p_203965_;
      }

      public void write(FriendlyByteBuf p_203968_) {
         p_203968_.writeMap(this.tags, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeIntIdList);
      }

      public static TagNetworkSerialization.NetworkPayload read(FriendlyByteBuf p_203970_) {
         return new TagNetworkSerialization.NetworkPayload(p_203970_.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readIntIdList));
      }

      public boolean isEmpty() {
         return this.tags.isEmpty();
      }
   }

   @FunctionalInterface
   public interface TagOutput<T> {
      void accept(TagKey<T> p_203972_, List<Holder<T>> p_203973_);
   }
}