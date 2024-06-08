package net.minecraft.world.level.chunk.storage;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;

public class RegionFile implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int SECTOR_BYTES = 4096;
   @VisibleForTesting
   protected static final int SECTOR_INTS = 1024;
   private static final int CHUNK_HEADER_SIZE = 5;
   private static final int HEADER_OFFSET = 0;
   private static final ByteBuffer PADDING_BUFFER = ByteBuffer.allocateDirect(1);
   private static final String EXTERNAL_FILE_EXTENSION = ".mcc";
   private static final int EXTERNAL_STREAM_FLAG = 128;
   private static final int EXTERNAL_CHUNK_THRESHOLD = 256;
   private static final int CHUNK_NOT_PRESENT = 0;
   private final FileChannel file;
   private final Path externalFileDir;
   final RegionFileVersion version;
   private final ByteBuffer header = ByteBuffer.allocateDirect(8192);
   private final IntBuffer offsets;
   private final IntBuffer timestamps;
   @VisibleForTesting
   protected final RegionBitmap usedSectors = new RegionBitmap();

   public RegionFile(Path p_196950_, Path p_196951_, boolean p_196952_) throws IOException {
      this(p_196950_, p_196951_, RegionFileVersion.VERSION_DEFLATE, p_196952_);
   }

   public RegionFile(Path p_63633_, Path p_63634_, RegionFileVersion p_63635_, boolean p_63636_) throws IOException {
      this.version = p_63635_;
      if (!Files.isDirectory(p_63634_)) {
         throw new IllegalArgumentException("Expected directory, got " + p_63634_.toAbsolutePath());
      } else {
         this.externalFileDir = p_63634_;
         this.offsets = this.header.asIntBuffer();
         this.offsets.limit(1024);
         this.header.position(4096);
         this.timestamps = this.header.asIntBuffer();
         if (p_63636_) {
            this.file = FileChannel.open(p_63633_, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.DSYNC);
         } else {
            this.file = FileChannel.open(p_63633_, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
         }

         this.usedSectors.force(0, 2);
         this.header.position(0);
         int i = this.file.read(this.header, 0L);
         if (i != -1) {
            if (i != 8192) {
               LOGGER.warn("Region file {} has truncated header: {}", p_63633_, i);
            }

            long j = Files.size(p_63633_);

            for(int k = 0; k < 1024; ++k) {
               int l = this.offsets.get(k);
               if (l != 0) {
                  int i1 = getSectorNumber(l);
                  int j1 = getNumSectors(l);
                  if (i1 < 2) {
                     LOGGER.warn("Region file {} has invalid sector at index: {}; sector {} overlaps with header", p_63633_, k, i1);
                     this.offsets.put(k, 0);
                  } else if (j1 == 0) {
                     LOGGER.warn("Region file {} has an invalid sector at index: {}; size has to be > 0", p_63633_, k);
                     this.offsets.put(k, 0);
                  } else if ((long)i1 * 4096L > j) {
                     LOGGER.warn("Region file {} has an invalid sector at index: {}; sector {} is out of bounds", p_63633_, k, i1);
                     this.offsets.put(k, 0);
                  } else {
                     this.usedSectors.force(i1, j1);
                  }
               }
            }
         }

      }
   }

   private Path getExternalChunkPath(ChunkPos p_63685_) {
      String s = "c." + p_63685_.x + "." + p_63685_.z + ".mcc";
      return this.externalFileDir.resolve(s);
   }

   @Nullable
   public synchronized DataInputStream getChunkDataInputStream(ChunkPos p_63646_) throws IOException {
      int i = this.getOffset(p_63646_);
      if (i == 0) {
         return null;
      } else {
         int j = getSectorNumber(i);
         int k = getNumSectors(i);
         int l = k * 4096;
         ByteBuffer bytebuffer = ByteBuffer.allocate(l);
         this.file.read(bytebuffer, (long)(j * 4096));
         bytebuffer.flip();
         if (bytebuffer.remaining() < 5) {
            LOGGER.error("Chunk {} header is truncated: expected {} but read {}", p_63646_, l, bytebuffer.remaining());
            return null;
         } else {
            int i1 = bytebuffer.getInt();
            byte b0 = bytebuffer.get();
            if (i1 == 0) {
               LOGGER.warn("Chunk {} is allocated, but stream is missing", (Object)p_63646_);
               return null;
            } else {
               int j1 = i1 - 1;
               if (isExternalStreamChunk(b0)) {
                  if (j1 != 0) {
                     LOGGER.warn("Chunk has both internal and external streams");
                  }

                  return this.createExternalChunkInputStream(p_63646_, getExternalChunkVersion(b0));
               } else if (j1 > bytebuffer.remaining()) {
                  LOGGER.error("Chunk {} stream is truncated: expected {} but read {}", p_63646_, j1, bytebuffer.remaining());
                  return null;
               } else if (j1 < 0) {
                  LOGGER.error("Declared size {} of chunk {} is negative", i1, p_63646_);
                  return null;
               } else {
                  return this.createChunkInputStream(p_63646_, b0, createStream(bytebuffer, j1));
               }
            }
         }
      }
   }

   private static int getTimestamp() {
      return (int)(Util.getEpochMillis() / 1000L);
   }

   private static boolean isExternalStreamChunk(byte p_63639_) {
      return (p_63639_ & 128) != 0;
   }

   private static byte getExternalChunkVersion(byte p_63670_) {
      return (byte)(p_63670_ & -129);
   }

   @Nullable
   private DataInputStream createChunkInputStream(ChunkPos p_63651_, byte p_63652_, InputStream p_63653_) throws IOException {
      RegionFileVersion regionfileversion = RegionFileVersion.fromId(p_63652_);
      if (regionfileversion == null) {
         LOGGER.error("Chunk {} has invalid chunk stream version {}", p_63651_, p_63652_);
         return null;
      } else {
         return new DataInputStream(regionfileversion.wrap(p_63653_));
      }
   }

   @Nullable
   private DataInputStream createExternalChunkInputStream(ChunkPos p_63648_, byte p_63649_) throws IOException {
      Path path = this.getExternalChunkPath(p_63648_);
      if (!Files.isRegularFile(path)) {
         LOGGER.error("External chunk path {} is not file", (Object)path);
         return null;
      } else {
         return this.createChunkInputStream(p_63648_, p_63649_, Files.newInputStream(path));
      }
   }

   private static ByteArrayInputStream createStream(ByteBuffer p_63660_, int p_63661_) {
      return new ByteArrayInputStream(p_63660_.array(), p_63660_.position(), p_63661_);
   }

   private int packSectorOffset(int p_63643_, int p_63644_) {
      return p_63643_ << 8 | p_63644_;
   }

   private static int getNumSectors(int p_63641_) {
      return p_63641_ & 255;
   }

   private static int getSectorNumber(int p_63672_) {
      return p_63672_ >> 8 & 16777215;
   }

   private static int sizeToSectors(int p_63677_) {
      return (p_63677_ + 4096 - 1) / 4096;
   }

   public boolean doesChunkExist(ChunkPos p_63674_) {
      int i = this.getOffset(p_63674_);
      if (i == 0) {
         return false;
      } else {
         int j = getSectorNumber(i);
         int k = getNumSectors(i);
         ByteBuffer bytebuffer = ByteBuffer.allocate(5);

         try {
            this.file.read(bytebuffer, (long)(j * 4096));
            bytebuffer.flip();
            if (bytebuffer.remaining() != 5) {
               return false;
            } else {
               int l = bytebuffer.getInt();
               byte b0 = bytebuffer.get();
               if (isExternalStreamChunk(b0)) {
                  if (!RegionFileVersion.isValidVersion(getExternalChunkVersion(b0))) {
                     return false;
                  }

                  if (!Files.isRegularFile(this.getExternalChunkPath(p_63674_))) {
                     return false;
                  }
               } else {
                  if (!RegionFileVersion.isValidVersion(b0)) {
                     return false;
                  }

                  if (l == 0) {
                     return false;
                  }

                  int i1 = l - 1;
                  if (i1 < 0 || i1 > 4096 * k) {
                     return false;
                  }
               }

               return true;
            }
         } catch (IOException ioexception) {
            return false;
         }
      }
   }

   public DataOutputStream getChunkDataOutputStream(ChunkPos p_63679_) throws IOException {
      return new DataOutputStream(this.version.wrap(new RegionFile.ChunkBuffer(p_63679_)));
   }

   public void flush() throws IOException {
      this.file.force(true);
   }

   public void clear(ChunkPos p_156614_) throws IOException {
      int i = getOffsetIndex(p_156614_);
      int j = this.offsets.get(i);
      if (j != 0) {
         this.offsets.put(i, 0);
         this.timestamps.put(i, getTimestamp());
         this.writeHeader();
         Files.deleteIfExists(this.getExternalChunkPath(p_156614_));
         this.usedSectors.free(getSectorNumber(j), getNumSectors(j));
      }
   }

   protected synchronized void write(ChunkPos p_63655_, ByteBuffer p_63656_) throws IOException {
      int i = getOffsetIndex(p_63655_);
      int j = this.offsets.get(i);
      int k = getSectorNumber(j);
      int l = getNumSectors(j);
      int i1 = p_63656_.remaining();
      int j1 = sizeToSectors(i1);
      int k1;
      RegionFile.CommitOp regionfile$commitop;
      if (j1 >= 256) {
         Path path = this.getExternalChunkPath(p_63655_);
         LOGGER.warn("Saving oversized chunk {} ({} bytes} to external file {}", p_63655_, i1, path);
         j1 = 1;
         k1 = this.usedSectors.allocate(j1);
         regionfile$commitop = this.writeToExternalFile(path, p_63656_);
         ByteBuffer bytebuffer = this.createExternalStub();
         this.file.write(bytebuffer, (long)(k1 * 4096));
      } else {
         k1 = this.usedSectors.allocate(j1);
         regionfile$commitop = () -> {
            Files.deleteIfExists(this.getExternalChunkPath(p_63655_));
         };
         this.file.write(p_63656_, (long)(k1 * 4096));
      }

      this.offsets.put(i, this.packSectorOffset(k1, j1));
      this.timestamps.put(i, getTimestamp());
      this.writeHeader();
      regionfile$commitop.run();
      if (k != 0) {
         this.usedSectors.free(k, l);
      }

   }

   private ByteBuffer createExternalStub() {
      ByteBuffer bytebuffer = ByteBuffer.allocate(5);
      bytebuffer.putInt(1);
      bytebuffer.put((byte)(this.version.getId() | 128));
      bytebuffer.flip();
      return bytebuffer;
   }

   private RegionFile.CommitOp writeToExternalFile(Path p_63663_, ByteBuffer p_63664_) throws IOException {
      Path path = Files.createTempFile(this.externalFileDir, "tmp", (String)null);
      FileChannel filechannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

      try {
         p_63664_.position(5);
         filechannel.write(p_63664_);
      } catch (Throwable throwable1) {
         if (filechannel != null) {
            try {
               filechannel.close();
            } catch (Throwable throwable) {
               throwable1.addSuppressed(throwable);
            }
         }

         throw throwable1;
      }

      if (filechannel != null) {
         filechannel.close();
      }

      return () -> {
         Files.move(path, p_63663_, StandardCopyOption.REPLACE_EXISTING);
      };
   }

   private void writeHeader() throws IOException {
      this.header.position(0);
      this.file.write(this.header, 0L);
   }

   private int getOffset(ChunkPos p_63687_) {
      return this.offsets.get(getOffsetIndex(p_63687_));
   }

   public boolean hasChunk(ChunkPos p_63683_) {
      return this.getOffset(p_63683_) != 0;
   }

   private static int getOffsetIndex(ChunkPos p_63689_) {
      return p_63689_.getRegionLocalX() + p_63689_.getRegionLocalZ() * 32;
   }

   public void close() throws IOException {
      try {
         this.padToFullSector();
      } finally {
         try {
            this.file.force(true);
         } finally {
            this.file.close();
         }
      }

   }

   private void padToFullSector() throws IOException {
      int i = (int)this.file.size();
      int j = sizeToSectors(i) * 4096;
      if (i != j) {
         ByteBuffer bytebuffer = PADDING_BUFFER.duplicate();
         bytebuffer.position(0);
         this.file.write(bytebuffer, (long)(j - 1));
      }

   }

   class ChunkBuffer extends ByteArrayOutputStream {
      private final ChunkPos pos;

      public ChunkBuffer(ChunkPos p_63696_) {
         super(8096);
         super.write(0);
         super.write(0);
         super.write(0);
         super.write(0);
         super.write(RegionFile.this.version.getId());
         this.pos = p_63696_;
      }

      public void close() throws IOException {
         ByteBuffer bytebuffer = ByteBuffer.wrap(this.buf, 0, this.count);
         bytebuffer.putInt(0, this.count - 5 + 1);
         RegionFile.this.write(this.pos, bytebuffer);
      }
   }

   interface CommitOp {
      void run() throws IOException;
   }
}