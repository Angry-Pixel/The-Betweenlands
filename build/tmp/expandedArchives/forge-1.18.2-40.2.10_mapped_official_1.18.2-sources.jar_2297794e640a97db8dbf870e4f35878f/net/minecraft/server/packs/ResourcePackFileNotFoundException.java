package net.minecraft.server.packs;

import java.io.File;
import java.io.FileNotFoundException;

public class ResourcePackFileNotFoundException extends FileNotFoundException {
   public ResourcePackFileNotFoundException(File p_10310_, String p_10311_) {
      super(String.format("'%s' in ResourcePack '%s'", p_10311_, p_10310_));
   }
}