package net.minecraft.world.level.timers;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

public class TimerCallbacks<C> {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final TimerCallbacks<MinecraftServer> SERVER_CALLBACKS = (new TimerCallbacks<MinecraftServer>()).register(new FunctionCallback.Serializer()).register(new FunctionTagCallback.Serializer());
   private final Map<ResourceLocation, TimerCallback.Serializer<C, ?>> idToSerializer = Maps.newHashMap();
   private final Map<Class<?>, TimerCallback.Serializer<C, ?>> classToSerializer = Maps.newHashMap();

   public TimerCallbacks<C> register(TimerCallback.Serializer<C, ?> p_82233_) {
      this.idToSerializer.put(p_82233_.getId(), p_82233_);
      this.classToSerializer.put(p_82233_.getCls(), p_82233_);
      return this;
   }

   private <T extends TimerCallback<C>> TimerCallback.Serializer<C, T> getSerializer(Class<?> p_82237_) {
      return (TimerCallback.Serializer<C, T>)this.classToSerializer.get(p_82237_);
   }

   public <T extends TimerCallback<C>> CompoundTag serialize(T p_82235_) {
      TimerCallback.Serializer<C, T> serializer = this.getSerializer(p_82235_.getClass());
      CompoundTag compoundtag = new CompoundTag();
      serializer.serialize(compoundtag, p_82235_);
      compoundtag.putString("Type", serializer.getId().toString());
      return compoundtag;
   }

   @Nullable
   public TimerCallback<C> deserialize(CompoundTag p_82239_) {
      ResourceLocation resourcelocation = ResourceLocation.tryParse(p_82239_.getString("Type"));
      TimerCallback.Serializer<C, ?> serializer = this.idToSerializer.get(resourcelocation);
      if (serializer == null) {
         LOGGER.error("Failed to deserialize timer callback: {}", (Object)p_82239_);
         return null;
      } else {
         try {
            return serializer.deserialize(p_82239_);
         } catch (Exception exception) {
            LOGGER.error("Failed to deserialize timer callback: {}", p_82239_, exception);
            return null;
         }
      }
   }
}