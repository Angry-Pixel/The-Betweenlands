package net.minecraft.client.resources.sounds;

import javax.annotation.Nullable;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Sound implements Weighted<Sound> {
   private final ResourceLocation location;
   private final float volume;
   private final float pitch;
   private final int weight;
   private final Sound.Type type;
   private final boolean stream;
   private final boolean preload;
   private final int attenuationDistance;

   public Sound(String p_119779_, float p_119780_, float p_119781_, int p_119782_, Sound.Type p_119783_, boolean p_119784_, boolean p_119785_, int p_119786_) {
      this.location = new ResourceLocation(p_119779_);
      this.volume = p_119780_;
      this.pitch = p_119781_;
      this.weight = p_119782_;
      this.type = p_119783_;
      this.stream = p_119784_;
      this.preload = p_119785_;
      this.attenuationDistance = p_119786_;
   }

   public ResourceLocation getLocation() {
      return this.location;
   }

   public ResourceLocation getPath() {
      return new ResourceLocation(this.location.getNamespace(), "sounds/" + this.location.getPath() + ".ogg");
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public int getWeight() {
      return this.weight;
   }

   public Sound getSound() {
      return this;
   }

   public void preloadIfRequired(SoundEngine p_119789_) {
      if (this.preload) {
         p_119789_.requestPreload(this);
      }

   }

   public Sound.Type getType() {
      return this.type;
   }

   public boolean shouldStream() {
      return this.stream;
   }

   public boolean shouldPreload() {
      return this.preload;
   }

   public int getAttenuationDistance() {
      return this.attenuationDistance;
   }

   public String toString() {
      return "Sound[" + this.location + "]";
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      FILE("file"),
      SOUND_EVENT("event");

      private final String name;

      private Type(String p_119809_) {
         this.name = p_119809_;
      }

      @Nullable
      public static Sound.Type getByName(String p_119811_) {
         for(Sound.Type sound$type : values()) {
            if (sound$type.name.equals(p_119811_)) {
               return sound$type;
            }
         }

         return null;
      }
   }
}