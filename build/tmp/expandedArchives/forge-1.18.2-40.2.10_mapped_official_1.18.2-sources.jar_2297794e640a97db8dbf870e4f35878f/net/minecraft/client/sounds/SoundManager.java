package net.minecraft.client.sounds;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Camera;
import net.minecraft.client.Options;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.client.resources.sounds.SoundEventRegistrationSerializer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SoundManager extends SimplePreparableReloadListener<SoundManager.Preparations> {
   public static final Sound EMPTY_SOUND = new Sound("meta:missing_sound", 1.0F, 1.0F, 1, Sound.Type.FILE, false, false, 16);
   static final Logger LOGGER = LogUtils.getLogger();
   private static final String SOUNDS_PATH = "sounds.json";
   private static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(Component.class, new Component.Serializer()).registerTypeAdapter(SoundEventRegistration.class, new SoundEventRegistrationSerializer()).create();
   private static final TypeToken<Map<String, SoundEventRegistration>> SOUND_EVENT_REGISTRATION_TYPE = new TypeToken<Map<String, SoundEventRegistration>>() {
   };
   private final Map<ResourceLocation, WeighedSoundEvents> registry = Maps.newHashMap();
   private final SoundEngine soundEngine;

   public SoundManager(ResourceManager p_120352_, Options p_120353_) {
      this.soundEngine = new SoundEngine(this, p_120353_, p_120352_);
   }

   protected SoundManager.Preparations prepare(ResourceManager p_120356_, ProfilerFiller p_120357_) {
      SoundManager.Preparations soundmanager$preparations = new SoundManager.Preparations();
      p_120357_.startTick();

      for(String s : p_120356_.getNamespaces()) {
         p_120357_.push(s);

         try {
            for(Resource resource : p_120356_.getResources(new ResourceLocation(s, "sounds.json"))) {
               p_120357_.push(resource.getSourceName());

               try {
                  InputStream inputstream = resource.getInputStream();

                  try {
                     Reader reader = new InputStreamReader(inputstream, StandardCharsets.UTF_8);

                     try {
                        p_120357_.push("parse");
                        Map<String, SoundEventRegistration> map = GsonHelper.fromJson(GSON, reader, SOUND_EVENT_REGISTRATION_TYPE);
                        p_120357_.popPush("register");

                        for(Entry<String, SoundEventRegistration> entry : map.entrySet()) {
                           soundmanager$preparations.handleRegistration(new ResourceLocation(s, entry.getKey()), entry.getValue(), p_120356_);
                        }

                        p_120357_.pop();
                     } catch (Throwable throwable2) {
                        try {
                           reader.close();
                        } catch (Throwable throwable1) {
                           throwable2.addSuppressed(throwable1);
                        }

                        throw throwable2;
                     }

                     reader.close();
                  } catch (Throwable throwable3) {
                     if (inputstream != null) {
                        try {
                           inputstream.close();
                        } catch (Throwable throwable) {
                           throwable3.addSuppressed(throwable);
                        }
                     }

                     throw throwable3;
                  }

                  if (inputstream != null) {
                     inputstream.close();
                  }
               } catch (RuntimeException runtimeexception) {
                  LOGGER.warn("Invalid {} in resourcepack: '{}'", "sounds.json", resource.getSourceName(), runtimeexception);
               }

               p_120357_.pop();
            }
         } catch (IOException ioexception) {
         }

         p_120357_.pop();
      }

      p_120357_.endTick();
      return soundmanager$preparations;
   }

   protected void apply(SoundManager.Preparations p_120377_, ResourceManager p_120378_, ProfilerFiller p_120379_) {
      p_120377_.apply(this.registry, this.soundEngine);
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         for(ResourceLocation resourcelocation : this.registry.keySet()) {
            WeighedSoundEvents weighedsoundevents = this.registry.get(resourcelocation);
            if (weighedsoundevents.getSubtitle() instanceof TranslatableComponent) {
               String s = ((TranslatableComponent)weighedsoundevents.getSubtitle()).getKey();
               if (!I18n.exists(s) && Registry.SOUND_EVENT.containsKey(resourcelocation)) {
                  LOGGER.error("Missing subtitle {} for sound event: {}", s, resourcelocation);
               }
            }
         }
      }

      if (LOGGER.isDebugEnabled()) {
         for(ResourceLocation resourcelocation1 : this.registry.keySet()) {
            if (!Registry.SOUND_EVENT.containsKey(resourcelocation1)) {
               LOGGER.debug("Not having sound event for: {}", (Object)resourcelocation1);
            }
         }
      }

      this.soundEngine.reload();
   }

   public List<String> getAvailableSoundDevices() {
      return this.soundEngine.getAvailableSoundDevices();
   }

   static boolean validateSoundResource(Sound p_120396_, ResourceLocation p_120397_, ResourceManager p_120398_) {
      ResourceLocation resourcelocation = p_120396_.getPath();
      if (!p_120398_.hasResource(resourcelocation)) {
         LOGGER.warn("File {} does not exist, cannot add it to event {}", resourcelocation, p_120397_);
         return false;
      } else {
         return true;
      }
   }

   @Nullable
   public WeighedSoundEvents getSoundEvent(ResourceLocation p_120385_) {
      return this.registry.get(p_120385_);
   }

   public Collection<ResourceLocation> getAvailableSounds() {
      return this.registry.keySet();
   }

   public void queueTickingSound(TickableSoundInstance p_120373_) {
      this.soundEngine.queueTickingSound(p_120373_);
   }

   public void play(SoundInstance p_120368_) {
      this.soundEngine.play(p_120368_);
   }

   public void playDelayed(SoundInstance p_120370_, int p_120371_) {
      this.soundEngine.playDelayed(p_120370_, p_120371_);
   }

   public void updateSource(Camera p_120362_) {
      this.soundEngine.updateSource(p_120362_);
   }

   public void pause() {
      this.soundEngine.pause();
   }

   public void stop() {
      this.soundEngine.stopAll();
   }

   public void destroy() {
      this.soundEngine.destroy();
   }

   public void tick(boolean p_120390_) {
      this.soundEngine.tick(p_120390_);
   }

   public void resume() {
      this.soundEngine.resume();
   }

   public void updateSourceVolume(SoundSource p_120359_, float p_120360_) {
      if (p_120359_ == SoundSource.MASTER && p_120360_ <= 0.0F) {
         this.stop();
      }

      this.soundEngine.updateCategoryVolume(p_120359_, p_120360_);
   }

   public void stop(SoundInstance p_120400_) {
      this.soundEngine.stop(p_120400_);
   }

   public boolean isActive(SoundInstance p_120404_) {
      return this.soundEngine.isActive(p_120404_);
   }

   public void addListener(SoundEventListener p_120375_) {
      this.soundEngine.addEventListener(p_120375_);
   }

   public void removeListener(SoundEventListener p_120402_) {
      this.soundEngine.removeEventListener(p_120402_);
   }

   public void stop(@Nullable ResourceLocation p_120387_, @Nullable SoundSource p_120388_) {
      this.soundEngine.stop(p_120387_, p_120388_);
   }

   public String getDebugString() {
      return this.soundEngine.getDebugString();
   }

   public void reload() {
      this.soundEngine.reload();
   }

   @OnlyIn(Dist.CLIENT)
   protected static class Preparations {
      final Map<ResourceLocation, WeighedSoundEvents> registry = Maps.newHashMap();

      void handleRegistration(ResourceLocation p_120426_, SoundEventRegistration p_120427_, ResourceManager p_120428_) {
         WeighedSoundEvents weighedsoundevents = this.registry.get(p_120426_);
         boolean flag = weighedsoundevents == null;
         if (flag || p_120427_.isReplace()) {
            if (!flag) {
               SoundManager.LOGGER.debug("Replaced sound event location {}", (Object)p_120426_);
            }

            weighedsoundevents = new WeighedSoundEvents(p_120426_, p_120427_.getSubtitle());
            this.registry.put(p_120426_, weighedsoundevents);
         }

         for(final Sound sound : p_120427_.getSounds()) {
            final ResourceLocation resourcelocation = sound.getLocation();
            Weighted<Sound> weighted;
            switch(sound.getType()) {
            case FILE:
               if (!SoundManager.validateSoundResource(sound, p_120426_, p_120428_)) {
                  continue;
               }

               weighted = sound;
               break;
            case SOUND_EVENT:
               weighted = new Weighted<Sound>() {
                  public int getWeight() {
                     WeighedSoundEvents weighedsoundevents1 = Preparations.this.registry.get(resourcelocation);
                     return weighedsoundevents1 == null ? 0 : weighedsoundevents1.getWeight();
                  }

                  public Sound getSound() {
                     WeighedSoundEvents weighedsoundevents1 = Preparations.this.registry.get(resourcelocation);
                     if (weighedsoundevents1 == null) {
                        return SoundManager.EMPTY_SOUND;
                     } else {
                        Sound sound1 = weighedsoundevents1.getSound();
                        return new Sound(sound1.getLocation().toString(), sound1.getVolume() * sound.getVolume(), sound1.getPitch() * sound.getPitch(), sound.getWeight(), Sound.Type.FILE, sound1.shouldStream() || sound.shouldStream(), sound1.shouldPreload(), sound1.getAttenuationDistance());
                     }
                  }

                  public void preloadIfRequired(SoundEngine p_120438_) {
                     WeighedSoundEvents weighedsoundevents1 = Preparations.this.registry.get(resourcelocation);
                     if (weighedsoundevents1 != null) {
                        weighedsoundevents1.preloadIfRequired(p_120438_);
                     }
                  }
               };
               break;
            default:
               throw new IllegalStateException("Unknown SoundEventRegistration type: " + sound.getType());
            }

            weighedsoundevents.addSound(weighted);
         }

      }

      public void apply(Map<ResourceLocation, WeighedSoundEvents> p_120423_, SoundEngine p_120424_) {
         p_120423_.clear();

         for(Entry<ResourceLocation, WeighedSoundEvents> entry : this.registry.entrySet()) {
            p_120423_.put(entry.getKey(), entry.getValue());
            entry.getValue().preloadIfRequired(p_120424_);
         }

      }
   }
}