package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Vector3f;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemModelGenerator {
   public static final List<String> LAYERS = Lists.newArrayList("layer0", "layer1", "layer2", "layer3", "layer4");
   private static final float MIN_Z = 7.5F;
   private static final float MAX_Z = 8.5F;

   public BlockModel generateBlockModel(Function<Material, TextureAtlasSprite> p_111671_, BlockModel p_111672_) {
      Map<String, Either<Material, String>> map = Maps.newHashMap();
      List<BlockElement> list = Lists.newArrayList();

      for(int i = 0; i < LAYERS.size(); ++i) {
         String s = LAYERS.get(i);
         if (!p_111672_.hasTexture(s)) {
            break;
         }

         Material material = p_111672_.getMaterial(s);
         map.put(s, Either.left(material));
         TextureAtlasSprite textureatlassprite = p_111671_.apply(material);
         list.addAll(this.processFrames(i, s, textureatlassprite));
      }

      map.put("particle", p_111672_.hasTexture("particle") ? Either.left(p_111672_.getMaterial("particle")) : map.get("layer0"));
      BlockModel blockmodel = new BlockModel((ResourceLocation)null, list, map, false, p_111672_.getGuiLight(), p_111672_.getTransforms(), p_111672_.getOverrides());
      blockmodel.name = p_111672_.name;
      blockmodel.customData.copyFrom(p_111672_.customData);
      return blockmodel;
   }

   private List<BlockElement> processFrames(int p_111639_, String p_111640_, TextureAtlasSprite p_111641_) {
      Map<Direction, BlockElementFace> map = Maps.newHashMap();
      map.put(Direction.SOUTH, new BlockElementFace((Direction)null, p_111639_, p_111640_, new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
      map.put(Direction.NORTH, new BlockElementFace((Direction)null, p_111639_, p_111640_, new BlockFaceUV(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
      List<BlockElement> list = Lists.newArrayList();
      list.add(new BlockElement(new Vector3f(0.0F, 0.0F, 7.5F), new Vector3f(16.0F, 16.0F, 8.5F), map, (BlockElementRotation)null, true));
      list.addAll(this.createSideElements(p_111641_, p_111640_, p_111639_));
      return list;
   }

   private List<BlockElement> createSideElements(TextureAtlasSprite p_111662_, String p_111663_, int p_111664_) {
      float f = (float)p_111662_.getWidth();
      float f1 = (float)p_111662_.getHeight();
      List<BlockElement> list = Lists.newArrayList();

      for(ItemModelGenerator.Span itemmodelgenerator$span : this.getSpans(p_111662_)) {
         float f2 = 0.0F;
         float f3 = 0.0F;
         float f4 = 0.0F;
         float f5 = 0.0F;
         float f6 = 0.0F;
         float f7 = 0.0F;
         float f8 = 0.0F;
         float f9 = 0.0F;
         float f10 = 16.0F / f;
         float f11 = 16.0F / f1;
         float f12 = (float)itemmodelgenerator$span.getMin();
         float f13 = (float)itemmodelgenerator$span.getMax();
         float f14 = (float)itemmodelgenerator$span.getAnchor();
         ItemModelGenerator.SpanFacing itemmodelgenerator$spanfacing = itemmodelgenerator$span.getFacing();
         switch(itemmodelgenerator$spanfacing) {
         case UP:
            f6 = f12;
            f2 = f12;
            f4 = f7 = f13 + 1.0F;
            f8 = f14;
            f3 = f14;
            f5 = f14;
            f9 = f14 + 1.0F;
            break;
         case DOWN:
            f8 = f14;
            f9 = f14 + 1.0F;
            f6 = f12;
            f2 = f12;
            f4 = f7 = f13 + 1.0F;
            f3 = f14 + 1.0F;
            f5 = f14 + 1.0F;
            break;
         case LEFT:
            f6 = f14;
            f2 = f14;
            f4 = f14;
            f7 = f14 + 1.0F;
            f9 = f12;
            f3 = f12;
            f5 = f8 = f13 + 1.0F;
            break;
         case RIGHT:
            f6 = f14;
            f7 = f14 + 1.0F;
            f2 = f14 + 1.0F;
            f4 = f14 + 1.0F;
            f9 = f12;
            f3 = f12;
            f5 = f8 = f13 + 1.0F;
         }

         f2 *= f10;
         f4 *= f10;
         f3 *= f11;
         f5 *= f11;
         f3 = 16.0F - f3;
         f5 = 16.0F - f5;
         f6 *= f10;
         f7 *= f10;
         f8 *= f11;
         f9 *= f11;
         Map<Direction, BlockElementFace> map = Maps.newHashMap();
         map.put(itemmodelgenerator$spanfacing.getDirection(), new BlockElementFace((Direction)null, p_111664_, p_111663_, new BlockFaceUV(new float[]{f6, f8, f7, f9}, 0)));
         switch(itemmodelgenerator$spanfacing) {
         case UP:
            list.add(new BlockElement(new Vector3f(f2, f3, 7.5F), new Vector3f(f4, f3, 8.5F), map, (BlockElementRotation)null, true));
            break;
         case DOWN:
            list.add(new BlockElement(new Vector3f(f2, f5, 7.5F), new Vector3f(f4, f5, 8.5F), map, (BlockElementRotation)null, true));
            break;
         case LEFT:
            list.add(new BlockElement(new Vector3f(f2, f3, 7.5F), new Vector3f(f2, f5, 8.5F), map, (BlockElementRotation)null, true));
            break;
         case RIGHT:
            list.add(new BlockElement(new Vector3f(f4, f3, 7.5F), new Vector3f(f4, f5, 8.5F), map, (BlockElementRotation)null, true));
         }
      }

      return list;
   }

   private List<ItemModelGenerator.Span> getSpans(TextureAtlasSprite p_111653_) {
      int i = p_111653_.getWidth();
      int j = p_111653_.getHeight();
      List<ItemModelGenerator.Span> list = Lists.newArrayList();
      p_111653_.getUniqueFrames().forEach((p_173444_) -> {
         for(int k = 0; k < j; ++k) {
            for(int l = 0; l < i; ++l) {
               boolean flag = !this.isTransparent(p_111653_, p_173444_, l, k, i, j);
               this.checkTransition(ItemModelGenerator.SpanFacing.UP, list, p_111653_, p_173444_, l, k, i, j, flag);
               this.checkTransition(ItemModelGenerator.SpanFacing.DOWN, list, p_111653_, p_173444_, l, k, i, j, flag);
               this.checkTransition(ItemModelGenerator.SpanFacing.LEFT, list, p_111653_, p_173444_, l, k, i, j, flag);
               this.checkTransition(ItemModelGenerator.SpanFacing.RIGHT, list, p_111653_, p_173444_, l, k, i, j, flag);
            }
         }

      });
      return list;
   }

   private void checkTransition(ItemModelGenerator.SpanFacing p_111643_, List<ItemModelGenerator.Span> p_111644_, TextureAtlasSprite p_111645_, int p_111646_, int p_111647_, int p_111648_, int p_111649_, int p_111650_, boolean p_111651_) {
      boolean flag = this.isTransparent(p_111645_, p_111646_, p_111647_ + p_111643_.getXOffset(), p_111648_ + p_111643_.getYOffset(), p_111649_, p_111650_) && p_111651_;
      if (flag) {
         this.createOrExpandSpan(p_111644_, p_111643_, p_111647_, p_111648_);
      }

   }

   private void createOrExpandSpan(List<ItemModelGenerator.Span> p_111666_, ItemModelGenerator.SpanFacing p_111667_, int p_111668_, int p_111669_) {
      ItemModelGenerator.Span itemmodelgenerator$span = null;

      for(ItemModelGenerator.Span itemmodelgenerator$span1 : p_111666_) {
         if (itemmodelgenerator$span1.getFacing() == p_111667_) {
            int i = p_111667_.isHorizontal() ? p_111669_ : p_111668_;
            if (itemmodelgenerator$span1.getAnchor() == i) {
               itemmodelgenerator$span = itemmodelgenerator$span1;
               break;
            }
         }
      }

      int j = p_111667_.isHorizontal() ? p_111669_ : p_111668_;
      int k = p_111667_.isHorizontal() ? p_111668_ : p_111669_;
      if (itemmodelgenerator$span == null) {
         p_111666_.add(new ItemModelGenerator.Span(p_111667_, k, j));
      } else {
         itemmodelgenerator$span.expand(k);
      }

   }

   private boolean isTransparent(TextureAtlasSprite p_111655_, int p_111656_, int p_111657_, int p_111658_, int p_111659_, int p_111660_) {
      return p_111657_ >= 0 && p_111658_ >= 0 && p_111657_ < p_111659_ && p_111658_ < p_111660_ ? p_111655_.isTransparent(p_111656_, p_111657_, p_111658_) : true;
   }

   @OnlyIn(Dist.CLIENT)
   static class Span {
      private final ItemModelGenerator.SpanFacing facing;
      private int min;
      private int max;
      private final int anchor;

      public Span(ItemModelGenerator.SpanFacing p_111680_, int p_111681_, int p_111682_) {
         this.facing = p_111680_;
         this.min = p_111681_;
         this.max = p_111681_;
         this.anchor = p_111682_;
      }

      public void expand(int p_111685_) {
         if (p_111685_ < this.min) {
            this.min = p_111685_;
         } else if (p_111685_ > this.max) {
            this.max = p_111685_;
         }

      }

      public ItemModelGenerator.SpanFacing getFacing() {
         return this.facing;
      }

      public int getMin() {
         return this.min;
      }

      public int getMax() {
         return this.max;
      }

      public int getAnchor() {
         return this.anchor;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static enum SpanFacing {
      UP(Direction.UP, 0, -1),
      DOWN(Direction.DOWN, 0, 1),
      LEFT(Direction.EAST, -1, 0),
      RIGHT(Direction.WEST, 1, 0);

      private final Direction direction;
      private final int xOffset;
      private final int yOffset;

      private SpanFacing(Direction p_111701_, int p_111702_, int p_111703_) {
         this.direction = p_111701_;
         this.xOffset = p_111702_;
         this.yOffset = p_111703_;
      }

      public Direction getDirection() {
         return this.direction;
      }

      public int getXOffset() {
         return this.xOffset;
      }

      public int getYOffset() {
         return this.yOffset;
      }

      boolean isHorizontal() {
         return this == DOWN || this == UP;
      }
   }
}
