package net.minecraft.client.resources;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class DirectAssetIndex extends AssetIndex {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final File assetsDirectory;

   public DirectAssetIndex(File p_118635_) {
      this.assetsDirectory = p_118635_;
   }

   public File getFile(ResourceLocation p_118653_) {
      return new File(this.assetsDirectory, p_118653_.toString().replace(':', '/'));
   }

   public File getRootFile(String p_118637_) {
      return new File(this.assetsDirectory, p_118637_);
   }

   public Collection<ResourceLocation> getFiles(String p_118639_, String p_118640_, int p_118641_, Predicate<String> p_118642_) {
      Path path = this.assetsDirectory.toPath().resolve(p_118640_);

      try {
         Stream<Path> stream = Files.walk(path.resolve(p_118639_), p_118641_);

         Collection collection;
         try {
            collection = stream.filter((p_118655_) -> {
               return Files.isRegularFile(p_118655_);
            }).filter((p_118648_) -> {
               return !p_118648_.endsWith(".mcmeta");
            }).filter((p_118651_) -> {
               return p_118642_.test(p_118651_.getFileName().toString());
            }).map((p_118646_) -> {
               return new ResourceLocation(p_118640_, path.relativize(p_118646_).toString().replaceAll("\\\\", "/"));
            }).collect(Collectors.toList());
         } catch (Throwable throwable1) {
            if (stream != null) {
               try {
                  stream.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }
            }

            throw throwable1;
         }

         if (stream != null) {
            stream.close();
         }

         return collection;
      } catch (NoSuchFileException nosuchfileexception) {
      } catch (IOException ioexception) {
         LOGGER.warn("Unable to getFiles on {}", p_118639_, ioexception);
      }

      return Collections.emptyList();
   }
}