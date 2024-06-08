package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VertexFormatElement {
   private final VertexFormatElement.Type type;
   private final VertexFormatElement.Usage usage;
   private final int index;
   private final int count;
   private final int byteSize;

   public VertexFormatElement(int p_86037_, VertexFormatElement.Type p_86038_, VertexFormatElement.Usage p_86039_, int p_86040_) {
      if (this.supportsUsage(p_86037_, p_86039_)) {
         this.usage = p_86039_;
         this.type = p_86038_;
         this.index = p_86037_;
         this.count = p_86040_;
         this.byteSize = p_86038_.getSize() * this.count;
      } else {
         throw new IllegalStateException("Multiple vertex elements of the same type other than UVs are not supported");
      }
   }

   private boolean supportsUsage(int p_86043_, VertexFormatElement.Usage p_86044_) {
      return p_86043_ == 0 || p_86044_ == VertexFormatElement.Usage.UV;
   }

   public final VertexFormatElement.Type getType() {
      return this.type;
   }

   public final VertexFormatElement.Usage getUsage() {
      return this.usage;
   }

   public final int getCount() {
      return this.count;
   }

   public final int getIndex() {
      return this.index;
   }

   public String toString() {
      return this.count + "," + this.usage.getName() + "," + this.type.getName();
   }

   public final int getByteSize() {
      return this.byteSize;
   }

   public final boolean isPosition() {
      return this.usage == VertexFormatElement.Usage.POSITION;
   }

   public boolean equals(Object p_86053_) {
      if (this == p_86053_) {
         return true;
      } else if (p_86053_ != null && this.getClass() == p_86053_.getClass()) {
         VertexFormatElement vertexformatelement = (VertexFormatElement)p_86053_;
         if (this.count != vertexformatelement.count) {
            return false;
         } else if (this.index != vertexformatelement.index) {
            return false;
         } else if (this.type != vertexformatelement.type) {
            return false;
         } else {
            return this.usage == vertexformatelement.usage;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = this.type.hashCode();
      i = 31 * i + this.usage.hashCode();
      i = 31 * i + this.index;
      return 31 * i + this.count;
   }

   public void setupBufferState(int p_166966_, long p_166967_, int p_166968_) {
      this.usage.setupBufferState(this.count, this.type.getGlType(), p_166968_, p_166967_, this.index, p_166966_);
   }

   public void clearBufferState(int p_166964_) {
      this.usage.clearBufferState(this.index, p_166964_);
   }

    public int getElementCount() {
       return count;
    }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      FLOAT(4, "Float", 5126),
      UBYTE(1, "Unsigned Byte", 5121),
      BYTE(1, "Byte", 5120),
      USHORT(2, "Unsigned Short", 5123),
      SHORT(2, "Short", 5122),
      UINT(4, "Unsigned Int", 5125),
      INT(4, "Int", 5124);

      private final int size;
      private final String name;
      private final int glType;

      private Type(int p_86071_, String p_86072_, int p_86073_) {
         this.size = p_86071_;
         this.name = p_86072_;
         this.glType = p_86073_;
      }

      public int getSize() {
         return this.size;
      }

      public String getName() {
         return this.name;
      }

      public int getGlType() {
         return this.glType;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Usage {
      POSITION("Position", (p_167043_, p_167044_, p_167045_, p_167046_, p_167047_, p_167048_) -> {
         GlStateManager._enableVertexAttribArray(p_167048_);
         GlStateManager._vertexAttribPointer(p_167048_, p_167043_, p_167044_, false, p_167045_, p_167046_);
      }, (p_167040_, p_167041_) -> {
         GlStateManager._disableVertexAttribArray(p_167041_);
      }),
      NORMAL("Normal", (p_167033_, p_167034_, p_167035_, p_167036_, p_167037_, p_167038_) -> {
         GlStateManager._enableVertexAttribArray(p_167038_);
         GlStateManager._vertexAttribPointer(p_167038_, p_167033_, p_167034_, true, p_167035_, p_167036_);
      }, (p_167030_, p_167031_) -> {
         GlStateManager._disableVertexAttribArray(p_167031_);
      }),
      COLOR("Vertex Color", (p_167023_, p_167024_, p_167025_, p_167026_, p_167027_, p_167028_) -> {
         GlStateManager._enableVertexAttribArray(p_167028_);
         GlStateManager._vertexAttribPointer(p_167028_, p_167023_, p_167024_, true, p_167025_, p_167026_);
      }, (p_167020_, p_167021_) -> {
         GlStateManager._disableVertexAttribArray(p_167021_);
      }),
      UV("UV", (p_167013_, p_167014_, p_167015_, p_167016_, p_167017_, p_167018_) -> {
         GlStateManager._enableVertexAttribArray(p_167018_);
         if (p_167014_ == 5126) {
            GlStateManager._vertexAttribPointer(p_167018_, p_167013_, p_167014_, false, p_167015_, p_167016_);
         } else {
            GlStateManager._vertexAttribIPointer(p_167018_, p_167013_, p_167014_, p_167015_, p_167016_);
         }

      }, (p_167010_, p_167011_) -> {
         GlStateManager._disableVertexAttribArray(p_167011_);
      }),
      PADDING("Padding", (p_167003_, p_167004_, p_167005_, p_167006_, p_167007_, p_167008_) -> {
      }, (p_167000_, p_167001_) -> {
      }),
      GENERIC("Generic", (p_166993_, p_166994_, p_166995_, p_166996_, p_166997_, p_166998_) -> {
         GlStateManager._enableVertexAttribArray(p_166998_);
         GlStateManager._vertexAttribPointer(p_166998_, p_166993_, p_166994_, false, p_166995_, p_166996_);
      }, (p_166990_, p_166991_) -> {
         GlStateManager._disableVertexAttribArray(p_166991_);
      });

      private final String name;
      private final VertexFormatElement.Usage.SetupState setupState;
      private final VertexFormatElement.Usage.ClearState clearState;

      private Usage(String p_166975_, VertexFormatElement.Usage.SetupState p_166976_, VertexFormatElement.Usage.ClearState p_166977_) {
         this.name = p_166975_;
         this.setupState = p_166976_;
         this.clearState = p_166977_;
      }

      void setupBufferState(int p_166982_, int p_166983_, int p_166984_, long p_166985_, int p_166986_, int p_166987_) {
         this.setupState.setupBufferState(p_166982_, p_166983_, p_166984_, p_166985_, p_166986_, p_166987_);
      }

      public void clearBufferState(int p_166979_, int p_166980_) {
         this.clearState.clearBufferState(p_166979_, p_166980_);
      }

      public String getName() {
         return this.name;
      }

      @FunctionalInterface
      @OnlyIn(Dist.CLIENT)
      interface ClearState {
         void clearBufferState(int p_167050_, int p_167051_);
      }

      @FunctionalInterface
      @OnlyIn(Dist.CLIENT)
      interface SetupState {
         void setupBufferState(int p_167053_, int p_167054_, int p_167055_, long p_167056_, int p_167057_, int p_167058_);
      }
   }
}
