package net.minecraft.sounds;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public class SoundEvent extends net.minecraftforge.registries.ForgeRegistryEntry<SoundEvent> {
   public static final Codec<SoundEvent> CODEC = ResourceLocation.CODEC.xmap(SoundEvent::new, (p_11662_) -> {
      return p_11662_.location;
   });
   private final ResourceLocation location;

   public SoundEvent(ResourceLocation p_11659_) {
      this.location = p_11659_;
   }

   public ResourceLocation getLocation() {
      return this.location;
   }
}
