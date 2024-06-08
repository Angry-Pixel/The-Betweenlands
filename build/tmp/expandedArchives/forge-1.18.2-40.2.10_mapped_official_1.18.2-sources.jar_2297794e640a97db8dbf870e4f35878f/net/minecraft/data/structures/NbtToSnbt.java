package net.minecraft.data.structures;

import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import org.slf4j.Logger;

public class NbtToSnbt implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final DataGenerator generator;

   public NbtToSnbt(DataGenerator p_126425_) {
      this.generator = p_126425_;
   }

   public void run(HashCache p_126428_) throws IOException {
      Path path = this.generator.getOutputFolder();

      for(Path path1 : this.generator.getInputFolders()) {
         Files.walk(path1).filter((p_126430_) -> {
            return p_126430_.toString().endsWith(".nbt");
         }).forEach((p_126441_) -> {
            convertStructure(p_126441_, this.getName(path1, p_126441_), path);
         });
      }

   }

   public String getName() {
      return "NBT to SNBT";
   }

   private String getName(Path p_126436_, Path p_126437_) {
      String s = p_126436_.relativize(p_126437_).toString().replaceAll("\\\\", "/");
      return s.substring(0, s.length() - ".nbt".length());
   }

   @Nullable
   public static Path convertStructure(Path p_126432_, String p_126433_, Path p_126434_) {
      try {
         writeSnbt(p_126434_.resolve(p_126433_ + ".snbt"), NbtUtils.structureToSnbt(NbtIo.readCompressed(Files.newInputStream(p_126432_))));
         LOGGER.info("Converted {} from NBT to SNBT", (Object)p_126433_);
         return p_126434_.resolve(p_126433_ + ".snbt");
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", p_126433_, p_126432_, ioexception);
         return null;
      }
   }

   public static void writeSnbt(Path p_176813_, String p_176814_) throws IOException {
      Files.createDirectories(p_176813_.getParent());
      BufferedWriter bufferedwriter = Files.newBufferedWriter(p_176813_);

      try {
         bufferedwriter.write(p_176814_);
         bufferedwriter.write(10);
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
}