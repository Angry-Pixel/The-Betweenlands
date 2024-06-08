package net.minecraft.world.level.chunk.storage;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;
import net.minecraft.util.FastBufferedInputStream;

public class RegionFileVersion {
   private static final Int2ObjectMap<RegionFileVersion> VERSIONS = new Int2ObjectOpenHashMap<>();
   public static final RegionFileVersion VERSION_GZIP = register(new RegionFileVersion(1, (p_63767_) -> {
      return new FastBufferedInputStream(new GZIPInputStream(p_63767_));
   }, (p_63769_) -> {
      return new BufferedOutputStream(new GZIPOutputStream(p_63769_));
   }));
   public static final RegionFileVersion VERSION_DEFLATE = register(new RegionFileVersion(2, (p_196964_) -> {
      return new FastBufferedInputStream(new InflaterInputStream(p_196964_));
   }, (p_196966_) -> {
      return new BufferedOutputStream(new DeflaterOutputStream(p_196966_));
   }));
   public static final RegionFileVersion VERSION_NONE = register(new RegionFileVersion(3, (p_196960_) -> {
      return p_196960_;
   }, (p_196962_) -> {
      return p_196962_;
   }));
   private final int id;
   private final RegionFileVersion.StreamWrapper<InputStream> inputWrapper;
   private final RegionFileVersion.StreamWrapper<OutputStream> outputWrapper;

   private RegionFileVersion(int p_63752_, RegionFileVersion.StreamWrapper<InputStream> p_63753_, RegionFileVersion.StreamWrapper<OutputStream> p_63754_) {
      this.id = p_63752_;
      this.inputWrapper = p_63753_;
      this.outputWrapper = p_63754_;
   }

   private static RegionFileVersion register(RegionFileVersion p_63759_) {
      VERSIONS.put(p_63759_.id, p_63759_);
      return p_63759_;
   }

   @Nullable
   public static RegionFileVersion fromId(int p_63757_) {
      return VERSIONS.get(p_63757_);
   }

   public static boolean isValidVersion(int p_63765_) {
      return VERSIONS.containsKey(p_63765_);
   }

   public int getId() {
      return this.id;
   }

   public OutputStream wrap(OutputStream p_63763_) throws IOException {
      return this.outputWrapper.wrap(p_63763_);
   }

   public InputStream wrap(InputStream p_63761_) throws IOException {
      return this.inputWrapper.wrap(p_63761_);
   }

   @FunctionalInterface
   interface StreamWrapper<O> {
      O wrap(O p_63771_) throws IOException;
   }
}