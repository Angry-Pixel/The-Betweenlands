package net.minecraft.world.level.saveddata;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.slf4j.Logger;

public abstract class SavedData {
   private static final Logger LOGGER = LogUtils.getLogger();
   private boolean dirty;

   public abstract CompoundTag save(CompoundTag p_77763_);

   public void setDirty() {
      this.setDirty(true);
   }

   public void setDirty(boolean p_77761_) {
      this.dirty = p_77761_;
   }

   public boolean isDirty() {
      return this.dirty;
   }

   public void save(File p_77758_) {
      if (this.isDirty()) {
         CompoundTag compoundtag = new CompoundTag();
         compoundtag.put("data", this.save(new CompoundTag()));
         compoundtag.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());

         try {
            NbtIo.writeCompressed(compoundtag, p_77758_);
         } catch (IOException ioexception) {
            LOGGER.error("Could not save data {}", this, ioexception);
         }

         this.setDirty(false);
      }
   }
}