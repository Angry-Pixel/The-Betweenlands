package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

public class DimensionDataStorage {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<String, SavedData> cache = Maps.newHashMap();
   private final DataFixer fixerUpper;
   private final File dataFolder;

   public DimensionDataStorage(File p_78149_, DataFixer p_78150_) {
      this.fixerUpper = p_78150_;
      this.dataFolder = p_78149_;
   }

   private File getDataFile(String p_78157_) {
      return new File(this.dataFolder, p_78157_ + ".dat");
   }

   public <T extends SavedData> T computeIfAbsent(Function<CompoundTag, T> p_164862_, Supplier<T> p_164863_, String p_164864_) {
      T t = this.get(p_164862_, p_164864_);
      if (t != null) {
         return t;
      } else {
         T t1 = p_164863_.get();
         this.set(p_164864_, t1);
         return t1;
      }
   }

   @Nullable
   public <T extends SavedData> T get(Function<CompoundTag, T> p_164859_, String p_164860_) {
      SavedData saveddata = this.cache.get(p_164860_);
      if (saveddata == net.minecraftforge.common.util.DummySavedData.DUMMY) return null;
      if (saveddata == null && !this.cache.containsKey(p_164860_)) {
         saveddata = this.readSavedData(p_164859_, p_164860_);
         this.cache.put(p_164860_, saveddata);
      } else if (saveddata == null) {
         this.cache.put(p_164860_, net.minecraftforge.common.util.DummySavedData.DUMMY);
         return null;
      }

      return (T)saveddata;
   }

   @Nullable
   private <T extends SavedData> T readSavedData(Function<CompoundTag, T> p_164869_, String p_164870_) {
      try {
         File file1 = this.getDataFile(p_164870_);
         if (file1.exists()) {
            CompoundTag compoundtag = this.readTagFromDisk(p_164870_, SharedConstants.getCurrentVersion().getWorldVersion());
            return p_164869_.apply(compoundtag.getCompound("data"));
         }
      } catch (Exception exception) {
         LOGGER.error("Error loading saved data: {}", p_164870_, exception);
      }

      return (T)null;
   }

   public void set(String p_164856_, SavedData p_164857_) {
      this.cache.put(p_164856_, p_164857_);
   }

   public CompoundTag readTagFromDisk(String p_78159_, int p_78160_) throws IOException {
      File file1 = this.getDataFile(p_78159_);
      FileInputStream fileinputstream = new FileInputStream(file1);

      CompoundTag compoundtag1;
      try {
         PushbackInputStream pushbackinputstream = new PushbackInputStream(fileinputstream, 2);

         try {
            CompoundTag compoundtag;
            if (this.isGzip(pushbackinputstream)) {
               compoundtag = NbtIo.readCompressed(pushbackinputstream);
            } else {
               DataInputStream datainputstream = new DataInputStream(pushbackinputstream);

               try {
                  compoundtag = NbtIo.read(datainputstream);
               } catch (Throwable throwable3) {
                  try {
                     datainputstream.close();
                  } catch (Throwable throwable2) {
                     throwable3.addSuppressed(throwable2);
                  }

                  throw throwable3;
               }

               datainputstream.close();
            }

            int i = compoundtag.contains("DataVersion", 99) ? compoundtag.getInt("DataVersion") : 1343;
            compoundtag1 = NbtUtils.update(this.fixerUpper, DataFixTypes.SAVED_DATA, compoundtag, i, p_78160_);
         } catch (Throwable throwable4) {
            try {
               pushbackinputstream.close();
            } catch (Throwable throwable1) {
               throwable4.addSuppressed(throwable1);
            }

            throw throwable4;
         }

         pushbackinputstream.close();
      } catch (Throwable throwable5) {
         try {
            fileinputstream.close();
         } catch (Throwable throwable) {
            throwable5.addSuppressed(throwable);
         }

         throw throwable5;
      }

      fileinputstream.close();
      return compoundtag1;
   }

   private boolean isGzip(PushbackInputStream p_78155_) throws IOException {
      byte[] abyte = new byte[2];
      boolean flag = false;
      int i = p_78155_.read(abyte, 0, 2);
      if (i == 2) {
         int j = (abyte[1] & 255) << 8 | abyte[0] & 255;
         if (j == 35615) {
            flag = true;
         }
      }

      if (i != 0) {
         p_78155_.unread(abyte, 0, i);
      }

      return flag;
   }

   public void save() {
      this.cache.forEach((p_164866_, p_164867_) -> {
         if (p_164867_ != null) {
            p_164867_.save(this.getDataFile(p_164866_));
         }

      });
   }
}
