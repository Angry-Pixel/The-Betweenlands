package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import org.slf4j.Logger;

public class FileZipper implements Closeable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Path outputFile;
   private final Path tempFile;
   private final FileSystem fs;

   public FileZipper(Path p_144697_) {
      this.outputFile = p_144697_;
      this.tempFile = p_144697_.resolveSibling(p_144697_.getFileName().toString() + "_tmp");

      try {
         this.fs = Util.ZIP_FILE_SYSTEM_PROVIDER.newFileSystem(this.tempFile, ImmutableMap.of("create", "true"));
      } catch (IOException ioexception) {
         throw new UncheckedIOException(ioexception);
      }
   }

   public void add(Path p_144704_, String p_144705_) {
      try {
         Path path = this.fs.getPath(File.separator);
         Path path1 = path.resolve(p_144704_.toString());
         Files.createDirectories(path1.getParent());
         Files.write(path1, p_144705_.getBytes(StandardCharsets.UTF_8));
      } catch (IOException ioexception) {
         throw new UncheckedIOException(ioexception);
      }
   }

   public void add(Path p_144701_, File p_144702_) {
      try {
         Path path = this.fs.getPath(File.separator);
         Path path1 = path.resolve(p_144701_.toString());
         Files.createDirectories(path1.getParent());
         Files.copy(p_144702_.toPath(), path1);
      } catch (IOException ioexception) {
         throw new UncheckedIOException(ioexception);
      }
   }

   public void add(Path p_144699_) {
      try {
         Path path = this.fs.getPath(File.separator);
         if (Files.isRegularFile(p_144699_)) {
            Path path3 = path.resolve(p_144699_.getParent().relativize(p_144699_).toString());
            Files.copy(path3, p_144699_);
         } else {
            Stream<Path> stream = Files.find(p_144699_, Integer.MAX_VALUE, (p_144707_, p_144708_) -> {
               return p_144708_.isRegularFile();
            });

            try {
               for(Path path1 : stream.collect(Collectors.toList())) {
                  Path path2 = path.resolve(p_144699_.relativize(path1).toString());
                  Files.createDirectories(path2.getParent());
                  Files.copy(path1, path2);
               }
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

         }
      } catch (IOException ioexception) {
         throw new UncheckedIOException(ioexception);
      }
   }

   public void close() {
      try {
         this.fs.close();
         Files.move(this.tempFile, this.outputFile);
         LOGGER.info("Compressed to {}", (Object)this.outputFile);
      } catch (IOException ioexception) {
         throw new UncheckedIOException(ioexception);
      }
   }
}