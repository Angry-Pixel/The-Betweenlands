package net.minecraft.data;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class HashCache {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Path path;
   private final Path cachePath;
   private int hits;
   private final Map<Path, String> oldCache = Maps.newHashMap();
   private final Map<Path, String> newCache = Maps.newHashMap();
   private final Set<Path> keep = Sets.newHashSet();

   public HashCache(Path p_123935_, String p_123936_) throws IOException {
      this.path = p_123935_;
      Path path = p_123935_.resolve(".cache");
      Files.createDirectories(path);
      this.cachePath = path.resolve(p_123936_);
      this.walkOutputFiles().forEach((p_123959_) -> {
         this.oldCache.put(p_123959_, "");
      });
      if (Files.isReadable(this.cachePath)) {
         IOUtils.readLines(Files.newInputStream(this.cachePath), Charsets.UTF_8).forEach((p_123950_) -> {
            int i = p_123950_.indexOf(32);
            this.oldCache.put(p_123935_.resolve(p_123950_.substring(i + 1)), p_123950_.substring(0, i));
         });
      }

   }

   public void purgeStaleAndWrite() throws IOException {
      this.removeStale();

      Writer writer;
      try {
         writer = Files.newBufferedWriter(this.cachePath);
      } catch (IOException ioexception) {
         LOGGER.warn("Unable write cachefile {}: {}", this.cachePath, ioexception.toString());
         return;
      }

      IOUtils.writeLines(this.newCache.entrySet().stream().map((p_123944_) -> {
         return (String)p_123944_.getValue() + ' ' + this.path.relativize(p_123944_.getKey()).toString().replace('\\', '/'); //Forge: Standardize file paths.
      }).sorted(java.util.Comparator.comparing(a -> a.split(" ")[1])).collect(Collectors.toList()), System.lineSeparator(), writer);
      writer.close();
      LOGGER.debug("Caching: cache hits: {}, created: {} removed: {}", this.hits, this.newCache.size() - this.hits, this.oldCache.size());
   }

   @Nullable
   public String getHash(Path p_123939_) {
      return this.oldCache.get(p_123939_);
   }

   public void putNew(Path p_123941_, String p_123942_) {
      this.newCache.put(p_123941_, p_123942_);
      if (Objects.equals(this.oldCache.remove(p_123941_), p_123942_)) {
         ++this.hits;
      }

   }

   public boolean had(Path p_123947_) {
      return this.oldCache.containsKey(p_123947_);
   }

   public void keep(Path p_123953_) {
      this.keep.add(p_123953_);
   }

   private void removeStale() throws IOException {
      this.walkOutputFiles().forEach((p_123957_) -> {
         if (this.had(p_123957_) && !this.keep.contains(p_123957_)) {
            try {
               Files.delete(p_123957_);
            } catch (IOException ioexception) {
               LOGGER.debug("Unable to delete: {} ({})", p_123957_, ioexception.toString());
            }
         }

      });
   }

   private Stream<Path> walkOutputFiles() throws IOException {
      return Files.walk(this.path).filter((p_123955_) -> {
         return !Objects.equals(this.cachePath, p_123955_) && !Files.isDirectory(p_123955_);
      });
   }
}
