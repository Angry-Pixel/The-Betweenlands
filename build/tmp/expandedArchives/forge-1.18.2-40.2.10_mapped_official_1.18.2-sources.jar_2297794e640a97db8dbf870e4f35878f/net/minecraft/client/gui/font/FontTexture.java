package net.minecraft.client.gui.font;

import com.mojang.blaze3d.font.RawGlyph;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FontTexture extends AbstractTexture {
   private static final int SIZE = 256;
   private final ResourceLocation name;
   private final RenderType normalType;
   private final RenderType seeThroughType;
   private final RenderType polygonOffsetType;
   private final boolean colored;
   private final FontTexture.Node root;

   public FontTexture(ResourceLocation p_95097_, boolean p_95098_) {
      this.name = p_95097_;
      this.colored = p_95098_;
      this.root = new FontTexture.Node(0, 0, 256, 256);
      TextureUtil.prepareImage(p_95098_ ? NativeImage.InternalGlFormat.RGBA : NativeImage.InternalGlFormat.RED, this.getId(), 256, 256);
      this.normalType = p_95098_ ? RenderType.text(p_95097_) : RenderType.textIntensity(p_95097_);
      this.seeThroughType = p_95098_ ? RenderType.textSeeThrough(p_95097_) : RenderType.textIntensitySeeThrough(p_95097_);
      this.polygonOffsetType = p_95098_ ? RenderType.textPolygonOffset(p_95097_) : RenderType.textIntensityPolygonOffset(p_95097_);
   }

   public void load(ResourceManager p_95101_) {
   }

   public void close() {
      this.releaseId();
   }

   @Nullable
   public BakedGlyph add(RawGlyph p_95103_) {
      if (p_95103_.isColored() != this.colored) {
         return null;
      } else {
         FontTexture.Node fonttexture$node = this.root.insert(p_95103_);
         if (fonttexture$node != null) {
            this.bind();
            p_95103_.upload(fonttexture$node.x, fonttexture$node.y);
            float f = 256.0F;
            float f1 = 256.0F;
            float f2 = 0.01F;
            return new BakedGlyph(this.normalType, this.seeThroughType, this.polygonOffsetType, ((float)fonttexture$node.x + 0.01F) / 256.0F, ((float)fonttexture$node.x - 0.01F + (float)p_95103_.getPixelWidth()) / 256.0F, ((float)fonttexture$node.y + 0.01F) / 256.0F, ((float)fonttexture$node.y - 0.01F + (float)p_95103_.getPixelHeight()) / 256.0F, p_95103_.getLeft(), p_95103_.getRight(), p_95103_.getUp(), p_95103_.getDown());
         } else {
            return null;
         }
      }
   }

   public ResourceLocation getName() {
      return this.name;
   }

   @OnlyIn(Dist.CLIENT)
   static class Node {
      final int x;
      final int y;
      private final int width;
      private final int height;
      @Nullable
      private FontTexture.Node left;
      @Nullable
      private FontTexture.Node right;
      private boolean occupied;

      Node(int p_95113_, int p_95114_, int p_95115_, int p_95116_) {
         this.x = p_95113_;
         this.y = p_95114_;
         this.width = p_95115_;
         this.height = p_95116_;
      }

      @Nullable
      FontTexture.Node insert(RawGlyph p_95124_) {
         if (this.left != null && this.right != null) {
            FontTexture.Node fonttexture$node = this.left.insert(p_95124_);
            if (fonttexture$node == null) {
               fonttexture$node = this.right.insert(p_95124_);
            }

            return fonttexture$node;
         } else if (this.occupied) {
            return null;
         } else {
            int i = p_95124_.getPixelWidth();
            int j = p_95124_.getPixelHeight();
            if (i <= this.width && j <= this.height) {
               if (i == this.width && j == this.height) {
                  this.occupied = true;
                  return this;
               } else {
                  int k = this.width - i;
                  int l = this.height - j;
                  if (k > l) {
                     this.left = new FontTexture.Node(this.x, this.y, i, this.height);
                     this.right = new FontTexture.Node(this.x + i + 1, this.y, this.width - i - 1, this.height);
                  } else {
                     this.left = new FontTexture.Node(this.x, this.y, this.width, j);
                     this.right = new FontTexture.Node(this.x, this.y + j + 1, this.width, this.height - j - 1);
                  }

                  return this.left.insert(p_95124_);
               }
            } else {
               return null;
            }
         }
      }
   }
}