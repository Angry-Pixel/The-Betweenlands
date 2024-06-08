package net.minecraft.server.dedicated;

import com.google.common.base.MoreObjects;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.core.RegistryAccess;
import org.slf4j.Logger;

public abstract class Settings<T extends Settings<T>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected final Properties properties;

   public Settings(Properties p_139801_) {
      this.properties = p_139801_;
   }

   public static Properties loadFromFile(Path p_139840_) {
      Properties properties = new Properties();

      try {
         InputStream inputstream = Files.newInputStream(p_139840_);

         try {
            properties.load(inputstream);
         } catch (Throwable throwable1) {
            if (inputstream != null) {
               try {
                  inputstream.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }
            }

            throw throwable1;
         }

         if (inputstream != null) {
            inputstream.close();
         }
      } catch (IOException ioexception) {
         LOGGER.error("Failed to load properties from file: {}", (Object)p_139840_);
      }

      return properties;
   }

   public void store(Path p_139877_) {
      try {
         OutputStream outputstream = Files.newOutputStream(p_139877_);

         try {
            net.minecraftforge.common.util.SortedProperties.store(properties, outputstream, "Minecraft server properties");
         } catch (Throwable throwable1) {
            if (outputstream != null) {
               try {
                  outputstream.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }
            }

            throw throwable1;
         }

         if (outputstream != null) {
            outputstream.close();
         }
      } catch (IOException ioexception) {
         LOGGER.error("Failed to store properties to file: {}", (Object)p_139877_);
      }

   }

   private static <V extends Number> Function<String, V> wrapNumberDeserializer(Function<String, V> p_139842_) {
      return (p_139845_) -> {
         try {
            return p_139842_.apply(p_139845_);
         } catch (NumberFormatException numberformatexception) {
            return (V)null;
         }
      };
   }

   protected static <V> Function<String, V> dispatchNumberOrString(IntFunction<V> p_139851_, Function<String, V> p_139852_) {
      return (p_139856_) -> {
         try {
            return p_139851_.apply(Integer.parseInt(p_139856_));
         } catch (NumberFormatException numberformatexception) {
            return p_139852_.apply(p_139856_);
         }
      };
   }

   @Nullable
   private String getStringRaw(String p_139879_) {
      return (String)this.properties.get(p_139879_);
   }

   @Nullable
   protected <V> V getLegacy(String p_139815_, Function<String, V> p_139816_) {
      String s = this.getStringRaw(p_139815_);
      if (s == null) {
         return (V)null;
      } else {
         this.properties.remove(p_139815_);
         return p_139816_.apply(s);
      }
   }

   protected <V> V get(String p_139822_, Function<String, V> p_139823_, Function<V, String> p_139824_, V p_139825_) {
      String s = this.getStringRaw(p_139822_);
      V v = MoreObjects.firstNonNull((V)(s != null ? p_139823_.apply(s) : null), p_139825_);
      this.properties.put(p_139822_, p_139824_.apply(v));
      return v;
   }

   protected <V> Settings<T>.MutableValue<V> getMutable(String p_139869_, Function<String, V> p_139870_, Function<V, String> p_139871_, V p_139872_) {
      String s = this.getStringRaw(p_139869_);
      V v = MoreObjects.firstNonNull((V)(s != null ? p_139870_.apply(s) : null), p_139872_);
      this.properties.put(p_139869_, p_139871_.apply(v));
      return new Settings.MutableValue(p_139869_, v, p_139871_);
   }

   protected <V> V get(String p_139827_, Function<String, V> p_139828_, UnaryOperator<V> p_139829_, Function<V, String> p_139830_, V p_139831_) {
      return this.get(p_139827_, (p_139849_) -> {
         V v = p_139828_.apply(p_139849_);
         return (V)(v != null ? p_139829_.apply(v) : null);
      }, p_139830_, p_139831_);
   }

   protected <V> V get(String p_139818_, Function<String, V> p_139819_, V p_139820_) {
      return this.get(p_139818_, p_139819_, Objects::toString, p_139820_);
   }

   protected <V> Settings<T>.MutableValue<V> getMutable(String p_139865_, Function<String, V> p_139866_, V p_139867_) {
      return this.getMutable(p_139865_, p_139866_, Objects::toString, p_139867_);
   }

   protected String get(String p_139812_, String p_139813_) {
      return this.get(p_139812_, Function.identity(), Function.identity(), p_139813_);
   }

   @Nullable
   protected String getLegacyString(String p_139804_) {
      return this.getLegacy(p_139804_, Function.identity());
   }

   protected int get(String p_139806_, int p_139807_) {
      return this.get(p_139806_, wrapNumberDeserializer(Integer::parseInt), p_139807_);
   }

   protected Settings<T>.MutableValue<Integer> getMutable(String p_139862_, int p_139863_) {
      return this.getMutable(p_139862_, wrapNumberDeserializer(Integer::parseInt), p_139863_);
   }

   protected int get(String p_139833_, UnaryOperator<Integer> p_139834_, int p_139835_) {
      return this.get(p_139833_, wrapNumberDeserializer(Integer::parseInt), p_139834_, Objects::toString, p_139835_);
   }

   protected long get(String p_139809_, long p_139810_) {
      return this.get(p_139809_, wrapNumberDeserializer(Long::parseLong), p_139810_);
   }

   protected boolean get(String p_139837_, boolean p_139838_) {
      return this.get(p_139837_, Boolean::valueOf, p_139838_);
   }

   protected Settings<T>.MutableValue<Boolean> getMutable(String p_139874_, boolean p_139875_) {
      return this.getMutable(p_139874_, Boolean::valueOf, p_139875_);
   }

   @Nullable
   protected Boolean getLegacyBoolean(String p_139860_) {
      return this.getLegacy(p_139860_, Boolean::valueOf);
   }

   protected Properties cloneProperties() {
      Properties properties = new Properties();
      properties.putAll(this.properties);
      return properties;
   }

   protected abstract T reload(RegistryAccess p_139857_, Properties p_139858_);

   public class MutableValue<V> implements Supplier<V> {
      private final String key;
      private final V value;
      private final Function<V, String> serializer;

      MutableValue(String p_139886_, V p_139887_, Function<V, String> p_139888_) {
         this.key = p_139886_;
         this.value = p_139887_;
         this.serializer = p_139888_;
      }

      public V get() {
         return this.value;
      }

      public T update(RegistryAccess p_139896_, V p_139897_) {
         Properties properties = Settings.this.cloneProperties();
         properties.put(this.key, this.serializer.apply(p_139897_));
         return Settings.this.reload(p_139896_, properties);
      }
   }
}
