package net.minecraft.data.tags;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import org.slf4j.Logger;

public abstract class TagsProvider<T> implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   protected final DataGenerator generator;
   protected final Registry<T> registry;
   protected final Map<ResourceLocation, Tag.Builder> builders = Maps.newLinkedHashMap();
   protected final String modId;
   protected final net.minecraftforge.common.data.ExistingFileHelper existingFileHelper;
   private final net.minecraftforge.common.data.ExistingFileHelper.IResourceType resourceType;

   /**
    * @see #TagsProvider(DataGenerator, Registry, String, net.minecraftforge.common.data.ExistingFileHelper)
    * @deprecated Forge: Use the mod id variant
    */
   @Deprecated
   protected TagsProvider(DataGenerator p_126546_, Registry<T> p_126547_) {
      this(p_126546_, p_126547_, "vanilla", null);
   }
   protected TagsProvider(DataGenerator p_126546_, Registry<T> p_126547_, String modId, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
      this.generator = p_126546_;
      this.registry = p_126547_;
      this.modId = modId;
      this.existingFileHelper = existingFileHelper;
      this.resourceType = new net.minecraftforge.common.data.ExistingFileHelper.ResourceType(net.minecraft.server.packs.PackType.SERVER_DATA, ".json", TagManager.getTagDir(p_126547_.key()));
   }

   protected abstract void addTags();

   public void run(HashCache p_126554_) {
      this.builders.clear();
      this.addTags();
      this.builders.forEach((p_176835_, p_176836_) -> {
         List<Tag.BuilderEntry> list = p_176836_.getEntries().filter((p_176832_) -> {
            return !p_176832_.entry().verifyIfPresent(this.registry::containsKey, this.builders::containsKey);
         }).filter(this::missing).collect(Collectors.toList()); // Forge: Add validation via existing resources
         if (!list.isEmpty()) {
            throw new IllegalArgumentException(String.format("Couldn't define tag %s as it is missing following references: %s", p_176835_, list.stream().map(Objects::toString).collect(Collectors.joining(","))));
         } else {
            JsonObject jsonobject = p_176836_.serializeToJson();
            Path path = this.getPath(p_176835_);
            if (path == null) return; // Forge: Allow running this data provider without writing it. Recipe provider needs valid tags.

            try {
               String s = GSON.toJson((JsonElement)jsonobject);
               String s1 = SHA1.hashUnencodedChars(s).toString();
               if (!Objects.equals(p_126554_.getHash(path), s1) || !Files.exists(path)) {
                  Files.createDirectories(path.getParent());
                  BufferedWriter bufferedwriter = Files.newBufferedWriter(path);

                  try {
                     bufferedwriter.write(s);
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

               p_126554_.putNew(path, s1);
            } catch (IOException ioexception) {
               LOGGER.error("Couldn't save tags to {}", path, ioexception);
            }

         }
      });
   }

   private boolean missing(Tag.BuilderEntry reference) {
      Tag.Entry entry = reference.entry();
      // We only care about non-optional tag entries, this is the only type that can reference a resource and needs validation
      // Optional tags should not be validated

      if (entry instanceof Tag.TagEntry nonOptionalEntry) {
         return existingFileHelper == null || !existingFileHelper.exists(nonOptionalEntry.getId(), resourceType);
      }
      return false;
   }

   protected Path getPath(ResourceLocation p_126561_) {
      ResourceKey<? extends Registry<T>> resourcekey = this.registry.key();
      return this.generator.getOutputFolder().resolve("data/" + p_126561_.getNamespace() + "/" + TagManager.getTagDir(resourcekey) + "/" + p_126561_.getPath() + ".json");
   }

   protected TagsProvider.TagAppender<T> tag(TagKey<T> p_206425_) {
      Tag.Builder tag$builder = this.getOrCreateRawBuilder(p_206425_);
      return new TagsProvider.TagAppender<>(tag$builder, this.registry, modId);
   }

   protected Tag.Builder getOrCreateRawBuilder(TagKey<T> p_206427_) {
      return this.builders.computeIfAbsent(p_206427_.location(), (p_176838_) -> {
         existingFileHelper.trackGenerated(p_176838_, resourceType);
         return new Tag.Builder();
      });
   }

   public static class TagAppender<T> implements net.minecraftforge.common.extensions.IForgeTagAppender<T> {
      private final Tag.Builder builder;
      public final Registry<T> registry;
      private final String source;

      TagAppender(Tag.Builder p_126572_, Registry<T> p_126573_, String p_126574_) {
         this.builder = p_126572_;
         this.registry = p_126573_;
         this.source = p_126574_;
      }

      public TagsProvider.TagAppender<T> add(T p_126583_) {
         this.builder.addElement(this.registry.getKey(p_126583_), this.source);
         return this;
      }

      @SafeVarargs
      public final TagsProvider.TagAppender<T> add(ResourceKey<T>... p_211102_) {
         for(ResourceKey<T> resourcekey : p_211102_) {
            this.builder.addElement(resourcekey.location(), this.source);
         }

         return this;
      }

      public TagsProvider.TagAppender<T> addOptional(ResourceLocation p_176840_) {
         this.builder.addOptionalElement(p_176840_, this.source);
         return this;
      }

      public TagsProvider.TagAppender<T> addTag(TagKey<T> p_206429_) {
         this.builder.addTag(p_206429_.location(), this.source);
         return this;
      }

      public TagsProvider.TagAppender<T> addOptionalTag(ResourceLocation p_176842_) {
         this.builder.addOptionalTag(p_176842_, this.source);
         return this;
      }

      @SafeVarargs
      public final TagsProvider.TagAppender<T> add(T... p_126585_) {
         Stream.<T>of(p_126585_).map(this.registry::getKey).forEach((p_126587_) -> {
            this.builder.addElement(p_126587_, this.source);
         });
         return this;
      }

      public TagsProvider.TagAppender<T> add(Tag.Entry tag) {
          builder.add(tag, source);
          return this;
      }

      public Tag.Builder getInternalBuilder() {
          return builder;
      }

      public String getModID() {
          return source;
      }
   }
}
