package net.minecraft.server.bossevents;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class CustomBossEvents {
   private final Map<ResourceLocation, CustomBossEvent> events = Maps.newHashMap();

   @Nullable
   public CustomBossEvent get(ResourceLocation p_136298_) {
      return this.events.get(p_136298_);
   }

   public CustomBossEvent create(ResourceLocation p_136300_, Component p_136301_) {
      CustomBossEvent custombossevent = new CustomBossEvent(p_136300_, p_136301_);
      this.events.put(p_136300_, custombossevent);
      return custombossevent;
   }

   public void remove(CustomBossEvent p_136303_) {
      this.events.remove(p_136303_.getTextId());
   }

   public Collection<ResourceLocation> getIds() {
      return this.events.keySet();
   }

   public Collection<CustomBossEvent> getEvents() {
      return this.events.values();
   }

   public CompoundTag save() {
      CompoundTag compoundtag = new CompoundTag();

      for(CustomBossEvent custombossevent : this.events.values()) {
         compoundtag.put(custombossevent.getTextId().toString(), custombossevent.save());
      }

      return compoundtag;
   }

   public void load(CompoundTag p_136296_) {
      for(String s : p_136296_.getAllKeys()) {
         ResourceLocation resourcelocation = new ResourceLocation(s);
         this.events.put(resourcelocation, CustomBossEvent.load(p_136296_.getCompound(s), resourcelocation));
      }

   }

   public void onPlayerConnect(ServerPlayer p_136294_) {
      for(CustomBossEvent custombossevent : this.events.values()) {
         custombossevent.onPlayerConnect(p_136294_);
      }

   }

   public void onPlayerDisconnect(ServerPlayer p_136306_) {
      for(CustomBossEvent custombossevent : this.events.values()) {
         custombossevent.onPlayerDisconnect(p_136306_);
      }

   }
}