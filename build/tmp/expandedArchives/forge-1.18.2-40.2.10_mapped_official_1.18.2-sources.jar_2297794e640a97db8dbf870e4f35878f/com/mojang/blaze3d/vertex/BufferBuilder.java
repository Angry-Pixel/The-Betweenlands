package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class BufferBuilder extends DefaultedVertexConsumer implements BufferVertexConsumer {
   private static final int GROWTH_SIZE = 2097152;
   private static final Logger LOGGER = LogUtils.getLogger();
   private ByteBuffer buffer;
   private final List<BufferBuilder.DrawState> drawStates = Lists.newArrayList();
   private int lastPoppedStateIndex;
   private int totalRenderedBytes;
   private int nextElementByte;
   private int totalUploadedBytes;
   private int vertices;
   @Nullable
   private VertexFormatElement currentElement;
   private int elementIndex;
   private VertexFormat format;
   private VertexFormat.Mode mode;
   private boolean fastFormat;
   private boolean fullFormat;
   private boolean building;
   @Nullable
   private Vector3f[] sortingPoints;
   private float sortX = Float.NaN;
   private float sortY = Float.NaN;
   private float sortZ = Float.NaN;
   private boolean indexOnly;

   public BufferBuilder(int p_85664_) {
      this.buffer = MemoryTracker.create(p_85664_ * 6);
   }

   private void ensureVertexCapacity() {
      this.ensureCapacity(this.format.getVertexSize());
   }

   private void ensureCapacity(int p_85723_) {
      if (this.nextElementByte + p_85723_ > this.buffer.capacity()) {
         int i = this.buffer.capacity();
         int j = i + roundUp(p_85723_);
         LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", i, j);
         ByteBuffer bytebuffer = MemoryTracker.resize(this.buffer, j);
         bytebuffer.rewind();
         this.buffer = bytebuffer;
      }
   }

   private static int roundUp(int p_85726_) {
      int i = 2097152;
      if (p_85726_ == 0) {
         return i;
      } else {
         if (p_85726_ < 0) {
            i *= -1;
         }

         int j = p_85726_ % i;
         return j == 0 ? p_85726_ : p_85726_ + i - j;
      }
   }

   public void setQuadSortOrigin(float p_166772_, float p_166773_, float p_166774_) {
      if (this.mode == VertexFormat.Mode.QUADS) {
         if (this.sortX != p_166772_ || this.sortY != p_166773_ || this.sortZ != p_166774_) {
            this.sortX = p_166772_;
            this.sortY = p_166773_;
            this.sortZ = p_166774_;
            if (this.sortingPoints == null) {
               this.sortingPoints = this.makeQuadSortingPoints();
            }
         }

      }
   }

   public BufferBuilder.SortState getSortState() {
      return new BufferBuilder.SortState(this.mode, this.vertices, this.sortingPoints, this.sortX, this.sortY, this.sortZ);
   }

   public void restoreSortState(BufferBuilder.SortState p_166776_) {
      this.buffer.clear();
      this.mode = p_166776_.mode;
      this.vertices = p_166776_.vertices;
      this.nextElementByte = this.totalRenderedBytes;
      this.sortingPoints = p_166776_.sortingPoints;
      this.sortX = p_166776_.sortX;
      this.sortY = p_166776_.sortY;
      this.sortZ = p_166776_.sortZ;
      this.indexOnly = true;
   }

   public void begin(VertexFormat.Mode p_166780_, VertexFormat p_166781_) {
      if (this.building) {
         throw new IllegalStateException("Already building!");
      } else {
         this.building = true;
         this.mode = p_166780_;
         this.switchFormat(p_166781_);
         this.currentElement = p_166781_.getElements().get(0);
         this.elementIndex = 0;
         this.buffer.clear();
      }
   }

   private void switchFormat(VertexFormat p_85705_) {
      if (this.format != p_85705_) {
         this.format = p_85705_;
         boolean flag = p_85705_ == DefaultVertexFormat.NEW_ENTITY;
         boolean flag1 = p_85705_ == DefaultVertexFormat.BLOCK;
         this.fastFormat = flag || flag1;
         this.fullFormat = flag;
      }
   }

   private IntConsumer intConsumer(VertexFormat.IndexType p_166778_) {
      switch(p_166778_) {
      case BYTE:
         return (p_166793_) -> {
            this.buffer.put((byte)p_166793_);
         };
      case SHORT:
         return (p_166791_) -> {
            this.buffer.putShort((short)p_166791_);
         };
      case INT:
      default:
         return (p_166789_) -> {
            this.buffer.putInt(p_166789_);
         };
      }
   }

   private Vector3f[] makeQuadSortingPoints() {
      FloatBuffer floatbuffer = this.buffer.asFloatBuffer();
      int i = this.totalRenderedBytes / 4;
      int j = this.format.getIntegerSize();
      int k = j * this.mode.primitiveStride;
      int l = this.vertices / this.mode.primitiveStride;
      Vector3f[] avector3f = new Vector3f[l];

      for(int i1 = 0; i1 < l; ++i1) {
         float f = floatbuffer.get(i + i1 * k + 0);
         float f1 = floatbuffer.get(i + i1 * k + 1);
         float f2 = floatbuffer.get(i + i1 * k + 2);
         float f3 = floatbuffer.get(i + i1 * k + j * 2 + 0);
         float f4 = floatbuffer.get(i + i1 * k + j * 2 + 1);
         float f5 = floatbuffer.get(i + i1 * k + j * 2 + 2);
         float f6 = (f + f3) / 2.0F;
         float f7 = (f1 + f4) / 2.0F;
         float f8 = (f2 + f5) / 2.0F;
         avector3f[i1] = new Vector3f(f6, f7, f8);
      }

      return avector3f;
   }

   private void putSortedQuadIndices(VertexFormat.IndexType p_166787_) {
      float[] afloat = new float[this.sortingPoints.length];
      int[] aint = new int[this.sortingPoints.length];

      for(int i = 0; i < this.sortingPoints.length; aint[i] = i++) {
         float f = this.sortingPoints[i].x() - this.sortX;
         float f1 = this.sortingPoints[i].y() - this.sortY;
         float f2 = this.sortingPoints[i].z() - this.sortZ;
         afloat[i] = f * f + f1 * f1 + f2 * f2;
      }

      IntArrays.mergeSort(aint, (p_166784_, p_166785_) -> {
         return Floats.compare(afloat[p_166785_], afloat[p_166784_]);
      });
      IntConsumer intconsumer = this.intConsumer(p_166787_);
      this.buffer.position(this.nextElementByte);

      for(int j : aint) {
         intconsumer.accept(j * this.mode.primitiveStride + 0);
         intconsumer.accept(j * this.mode.primitiveStride + 1);
         intconsumer.accept(j * this.mode.primitiveStride + 2);
         intconsumer.accept(j * this.mode.primitiveStride + 2);
         intconsumer.accept(j * this.mode.primitiveStride + 3);
         intconsumer.accept(j * this.mode.primitiveStride + 0);
      }

   }

   public void end() {
      if (!this.building) {
         throw new IllegalStateException("Not building!");
      } else {
         int i = this.mode.indexCount(this.vertices);
         VertexFormat.IndexType vertexformat$indextype = VertexFormat.IndexType.least(i);
         boolean flag;
         if (this.sortingPoints != null) {
            int j = Mth.roundToward(i * vertexformat$indextype.bytes, 4);
            this.ensureCapacity(j);
            this.putSortedQuadIndices(vertexformat$indextype);
            flag = false;
            this.nextElementByte += j;
            this.totalRenderedBytes += this.vertices * this.format.getVertexSize() + j;
         } else {
            flag = true;
            this.totalRenderedBytes += this.vertices * this.format.getVertexSize();
         }

         this.building = false;
         this.drawStates.add(new BufferBuilder.DrawState(this.format, this.vertices, i, this.mode, vertexformat$indextype, this.indexOnly, flag));
         this.vertices = 0;
         this.currentElement = null;
         this.elementIndex = 0;
         this.sortingPoints = null;
         this.sortX = Float.NaN;
         this.sortY = Float.NaN;
         this.sortZ = Float.NaN;
         this.indexOnly = false;
      }
   }

   public void putByte(int p_85686_, byte p_85687_) {
      this.buffer.put(this.nextElementByte + p_85686_, p_85687_);
   }

   public void putShort(int p_85700_, short p_85701_) {
      this.buffer.putShort(this.nextElementByte + p_85700_, p_85701_);
   }

   public void putFloat(int p_85689_, float p_85690_) {
      this.buffer.putFloat(this.nextElementByte + p_85689_, p_85690_);
   }

   public void endVertex() {
      if (this.elementIndex != 0) {
         throw new IllegalStateException("Not filled all elements of the vertex");
      } else {
         ++this.vertices;
         this.ensureVertexCapacity();
         if (this.mode == VertexFormat.Mode.LINES || this.mode == VertexFormat.Mode.LINE_STRIP) {
            int i = this.format.getVertexSize();
            this.buffer.position(this.nextElementByte);
            ByteBuffer bytebuffer = this.buffer.duplicate();
            bytebuffer.position(this.nextElementByte - i).limit(this.nextElementByte);
            this.buffer.put(bytebuffer);
            this.nextElementByte += i;
            ++this.vertices;
            this.ensureVertexCapacity();
         }

      }
   }

   public void nextElement() {
      ImmutableList<VertexFormatElement> immutablelist = this.format.getElements();
      this.elementIndex = (this.elementIndex + 1) % immutablelist.size();
      this.nextElementByte += this.currentElement.getByteSize();
      VertexFormatElement vertexformatelement = immutablelist.get(this.elementIndex);
      this.currentElement = vertexformatelement;
      if (vertexformatelement.getUsage() == VertexFormatElement.Usage.PADDING) {
         this.nextElement();
      }

      if (this.defaultColorSet && this.currentElement.getUsage() == VertexFormatElement.Usage.COLOR) {
         BufferVertexConsumer.super.color(this.defaultR, this.defaultG, this.defaultB, this.defaultA);
      }

   }

   public VertexConsumer color(int p_85692_, int p_85693_, int p_85694_, int p_85695_) {
      if (this.defaultColorSet) {
         throw new IllegalStateException();
      } else {
         return BufferVertexConsumer.super.color(p_85692_, p_85693_, p_85694_, p_85695_);
      }
   }

   public void vertex(float p_85671_, float p_85672_, float p_85673_, float p_85674_, float p_85675_, float p_85676_, float p_85677_, float p_85678_, float p_85679_, int p_85680_, int p_85681_, float p_85682_, float p_85683_, float p_85684_) {
      if (this.defaultColorSet) {
         throw new IllegalStateException();
      } else if (this.fastFormat) {
         this.putFloat(0, p_85671_);
         this.putFloat(4, p_85672_);
         this.putFloat(8, p_85673_);
         this.putByte(12, (byte)((int)(p_85674_ * 255.0F)));
         this.putByte(13, (byte)((int)(p_85675_ * 255.0F)));
         this.putByte(14, (byte)((int)(p_85676_ * 255.0F)));
         this.putByte(15, (byte)((int)(p_85677_ * 255.0F)));
         this.putFloat(16, p_85678_);
         this.putFloat(20, p_85679_);
         int i;
         if (this.fullFormat) {
            this.putShort(24, (short)(p_85680_ & '\uffff'));
            this.putShort(26, (short)(p_85680_ >> 16 & '\uffff'));
            i = 28;
         } else {
            i = 24;
         }

         this.putShort(i + 0, (short)(p_85681_ & '\uffff'));
         this.putShort(i + 2, (short)(p_85681_ >> 16 & '\uffff'));
         this.putByte(i + 4, BufferVertexConsumer.normalIntValue(p_85682_));
         this.putByte(i + 5, BufferVertexConsumer.normalIntValue(p_85683_));
         this.putByte(i + 6, BufferVertexConsumer.normalIntValue(p_85684_));
         this.nextElementByte += i + 8;
         this.endVertex();
      } else {
         super.vertex(p_85671_, p_85672_, p_85673_, p_85674_, p_85675_, p_85676_, p_85677_, p_85678_, p_85679_, p_85680_, p_85681_, p_85682_, p_85683_, p_85684_);
      }
   }

   public Pair<BufferBuilder.DrawState, ByteBuffer> popNextBuffer() {
      BufferBuilder.DrawState bufferbuilder$drawstate = this.drawStates.get(this.lastPoppedStateIndex++);
      this.buffer.position(this.totalUploadedBytes);
      this.totalUploadedBytes += Mth.roundToward(bufferbuilder$drawstate.bufferSize(), 4);
      this.buffer.limit(this.totalUploadedBytes);
      if (this.lastPoppedStateIndex == this.drawStates.size() && this.vertices == 0) {
         this.clear();
      }

      ByteBuffer bytebuffer = this.buffer.slice();
      bytebuffer.order(this.buffer.order()); // FORGE: Fix incorrect byte order
      this.buffer.clear();
      return Pair.of(bufferbuilder$drawstate, bytebuffer);
   }

   public void clear() {
      if (this.totalRenderedBytes != this.totalUploadedBytes) {
         LOGGER.warn("Bytes mismatch {} {}", this.totalRenderedBytes, this.totalUploadedBytes);
      }

      this.discard();
   }

   public void discard() {
      this.totalRenderedBytes = 0;
      this.totalUploadedBytes = 0;
      this.nextElementByte = 0;
      this.drawStates.clear();
      this.lastPoppedStateIndex = 0;
   }

   public VertexFormatElement currentElement() {
      if (this.currentElement == null) {
         throw new IllegalStateException("BufferBuilder not started");
      } else {
         return this.currentElement;
      }
   }

   public boolean building() {
      return this.building;
   }

   @OnlyIn(Dist.CLIENT)
   public static final class DrawState {
      private final VertexFormat format;
      private final int vertexCount;
      private final int indexCount;
      private final VertexFormat.Mode mode;
      private final VertexFormat.IndexType indexType;
      private final boolean indexOnly;
      private final boolean sequentialIndex;

      DrawState(VertexFormat p_166802_, int p_166803_, int p_166804_, VertexFormat.Mode p_166805_, VertexFormat.IndexType p_166806_, boolean p_166807_, boolean p_166808_) {
         this.format = p_166802_;
         this.vertexCount = p_166803_;
         this.indexCount = p_166804_;
         this.mode = p_166805_;
         this.indexType = p_166806_;
         this.indexOnly = p_166807_;
         this.sequentialIndex = p_166808_;
      }

      public VertexFormat format() {
         return this.format;
      }

      public int vertexCount() {
         return this.vertexCount;
      }

      public int indexCount() {
         return this.indexCount;
      }

      public VertexFormat.Mode mode() {
         return this.mode;
      }

      public VertexFormat.IndexType indexType() {
         return this.indexType;
      }

      public int vertexBufferSize() {
         return this.vertexCount * this.format.getVertexSize();
      }

      private int indexBufferSize() {
         return this.sequentialIndex ? 0 : this.indexCount * this.indexType.bytes;
      }

      public int bufferSize() {
         return this.vertexBufferSize() + this.indexBufferSize();
      }

      public boolean indexOnly() {
         return this.indexOnly;
      }

      public boolean sequentialIndex() {
         return this.sequentialIndex;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class SortState {
      final VertexFormat.Mode mode;
      final int vertices;
      @Nullable
      final Vector3f[] sortingPoints;
      final float sortX;
      final float sortY;
      final float sortZ;

      SortState(VertexFormat.Mode p_166824_, int p_166825_, @Nullable Vector3f[] p_166826_, float p_166827_, float p_166828_, float p_166829_) {
         this.mode = p_166824_;
         this.vertices = p_166825_;
         this.sortingPoints = p_166826_;
         this.sortX = p_166827_;
         this.sortY = p_166828_;
         this.sortZ = p_166829_;
      }
   }

   // Forge start
   public void putBulkData(ByteBuffer buffer) {
      ensureCapacity(buffer.limit() + this.format.getVertexSize());
      this.buffer.position(this.vertices * this.format.getVertexSize());
      this.buffer.put(buffer);
      this.vertices += buffer.limit() / this.format.getVertexSize();
      this.nextElementByte += buffer.limit();
   }

   @Override
   public VertexFormat getVertexFormat() { return this.format; }
}
