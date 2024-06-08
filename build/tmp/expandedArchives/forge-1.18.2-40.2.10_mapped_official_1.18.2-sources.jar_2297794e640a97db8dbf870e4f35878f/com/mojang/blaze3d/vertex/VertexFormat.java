package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VertexFormat {
   private final ImmutableList<VertexFormatElement> elements;
   private final ImmutableMap<String, VertexFormatElement> elementMapping;
   private final IntList offsets = new IntArrayList();
   private final int vertexSize;
   private int vertexArrayObject;
   private int vertexBufferObject;
   private int indexBufferObject;

   public VertexFormat(ImmutableMap<String, VertexFormatElement> p_166910_) {
      this.elementMapping = p_166910_;
      this.elements = p_166910_.values().asList();
      int i = 0;

      for(VertexFormatElement vertexformatelement : p_166910_.values()) {
         this.offsets.add(i);
         i += vertexformatelement.getByteSize();
      }

      this.vertexSize = i;
   }

   public String toString() {
      return "format: " + this.elementMapping.size() + " elements: " + (String)this.elementMapping.entrySet().stream().map(Object::toString).collect(Collectors.joining(" "));
   }

   public int getIntegerSize() {
      return this.getVertexSize() / 4;
   }

   public int getVertexSize() {
      return this.vertexSize;
   }

   public ImmutableList<VertexFormatElement> getElements() {
      return this.elements;
   }

   public ImmutableList<String> getElementAttributeNames() {
      return this.elementMapping.keySet().asList();
   }

   public boolean equals(Object p_86026_) {
      if (this == p_86026_) {
         return true;
      } else if (p_86026_ != null && this.getClass() == p_86026_.getClass()) {
         VertexFormat vertexformat = (VertexFormat)p_86026_;
         return this.vertexSize != vertexformat.vertexSize ? false : this.elementMapping.equals(vertexformat.elementMapping);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.elementMapping.hashCode();
   }

   public void setupBufferState() {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(this::_setupBufferState);
      } else {
         this._setupBufferState();
      }
   }

   private void _setupBufferState() {
      int i = this.getVertexSize();
      List<VertexFormatElement> list = this.getElements();

      for(int j = 0; j < list.size(); ++j) {
         list.get(j).setupBufferState(j, (long)this.offsets.getInt(j), i);
      }

   }

   public void clearBufferState() {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(this::_clearBufferState);
      } else {
         this._clearBufferState();
      }
   }

   private void _clearBufferState() {
      ImmutableList<VertexFormatElement> immutablelist = this.getElements();

      for(int i = 0; i < immutablelist.size(); ++i) {
         VertexFormatElement vertexformatelement = immutablelist.get(i);
         vertexformatelement.clearBufferState(i);
      }

   }

   public int getOrCreateVertexArrayObject() {
      if (this.vertexArrayObject == 0) {
         this.vertexArrayObject = GlStateManager._glGenVertexArrays();
      }

      return this.vertexArrayObject;
   }

   public int getOrCreateVertexBufferObject() {
      if (this.vertexBufferObject == 0) {
         this.vertexBufferObject = GlStateManager._glGenBuffers();
      }

      return this.vertexBufferObject;
   }

   public int getOrCreateIndexBufferObject() {
      if (this.indexBufferObject == 0) {
         this.indexBufferObject = GlStateManager._glGenBuffers();
      }

      return this.indexBufferObject;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum IndexType {
      BYTE(5121, 1),
      SHORT(5123, 2),
      INT(5125, 4);

      public final int asGLType;
      public final int bytes;

      private IndexType(int p_166930_, int p_166931_) {
         this.asGLType = p_166930_;
         this.bytes = p_166931_;
      }

      public static VertexFormat.IndexType least(int p_166934_) {
         if ((p_166934_ & -65536) != 0) {
            return INT;
         } else {
            return (p_166934_ & '\uff00') != 0 ? SHORT : BYTE;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Mode {
      LINES(4, 2, 2),
      LINE_STRIP(5, 2, 1),
      DEBUG_LINES(1, 2, 2),
      DEBUG_LINE_STRIP(3, 2, 1),
      TRIANGLES(4, 3, 3),
      TRIANGLE_STRIP(5, 3, 1),
      TRIANGLE_FAN(6, 3, 1),
      QUADS(4, 4, 4);

      public final int asGLMode;
      public final int primitiveLength;
      public final int primitiveStride;

      private Mode(int p_166954_, int p_166955_, int p_166956_) {
         this.asGLMode = p_166954_;
         this.primitiveLength = p_166955_;
         this.primitiveStride = p_166956_;
      }

      public int indexCount(int p_166959_) {
         int i;
         switch(this) {
         case LINE_STRIP:
         case DEBUG_LINES:
         case DEBUG_LINE_STRIP:
         case TRIANGLES:
         case TRIANGLE_STRIP:
         case TRIANGLE_FAN:
            i = p_166959_;
            break;
         case LINES:
         case QUADS:
            i = p_166959_ / 4 * 6;
            break;
         default:
            i = 0;
         }

         return i;
      }
   }

   public ImmutableMap<String, VertexFormatElement> getElementMapping() { return elementMapping; }
   public int getOffset(int index) { return offsets.getInt(index); }
   public boolean hasPosition() { return elements.stream().anyMatch(e -> e.getUsage() == VertexFormatElement.Usage.POSITION); }
   public boolean hasNormal() { return elements.stream().anyMatch(e -> e.getUsage() == VertexFormatElement.Usage.NORMAL); }
   public boolean hasColor() { return elements.stream().anyMatch(e -> e.getUsage() == VertexFormatElement.Usage.COLOR); }
   public boolean hasUV(int which) { return elements.stream().anyMatch(e -> e.getUsage() == VertexFormatElement.Usage.UV && e.getIndex() == which); }
}
