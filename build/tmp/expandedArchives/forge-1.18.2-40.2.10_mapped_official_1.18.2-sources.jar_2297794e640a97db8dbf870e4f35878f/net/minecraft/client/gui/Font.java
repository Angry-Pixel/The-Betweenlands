package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.Mth;
import net.minecraft.util.StringDecomposer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Font {
   private static final float EFFECT_DEPTH = 0.01F;
   private static final Vector3f SHADOW_OFFSET = new Vector3f(0.0F, 0.0F, 0.03F);
   public static final int ALPHA_CUTOFF = 8;
   public final int lineHeight = 9;
   public final Random random = new Random();
   private final Function<ResourceLocation, FontSet> fonts;
   private final StringSplitter splitter;

   public Font(Function<ResourceLocation, FontSet> p_92717_) {
      this.fonts = p_92717_;
      this.splitter = new StringSplitter((p_92722_, p_92723_) -> {
         return this.getFontSet(p_92723_.getFont()).getGlyphInfo(p_92722_).getAdvance(p_92723_.isBold());
      });
   }

   FontSet getFontSet(ResourceLocation p_92864_) {
      return this.fonts.apply(p_92864_);
   }

   public int drawShadow(PoseStack p_92751_, String p_92752_, float p_92753_, float p_92754_, int p_92755_) {
      return this.drawInternal(p_92752_, p_92753_, p_92754_, p_92755_, p_92751_.last().pose(), true, this.isBidirectional());
   }

   public int drawShadow(PoseStack p_92757_, String p_92758_, float p_92759_, float p_92760_, int p_92761_, boolean p_92762_) {
      return this.drawInternal(p_92758_, p_92759_, p_92760_, p_92761_, p_92757_.last().pose(), true, p_92762_);
   }

   public int draw(PoseStack p_92884_, String p_92885_, float p_92886_, float p_92887_, int p_92888_) {
      return this.drawInternal(p_92885_, p_92886_, p_92887_, p_92888_, p_92884_.last().pose(), false, this.isBidirectional());
   }

   public int drawShadow(PoseStack p_92745_, FormattedCharSequence p_92746_, float p_92747_, float p_92748_, int p_92749_) {
      return this.drawInternal(p_92746_, p_92747_, p_92748_, p_92749_, p_92745_.last().pose(), true);
   }

   public int drawShadow(PoseStack p_92764_, Component p_92765_, float p_92766_, float p_92767_, int p_92768_) {
      return this.drawInternal(p_92765_.getVisualOrderText(), p_92766_, p_92767_, p_92768_, p_92764_.last().pose(), true);
   }

   public int draw(PoseStack p_92878_, FormattedCharSequence p_92879_, float p_92880_, float p_92881_, int p_92882_) {
      return this.drawInternal(p_92879_, p_92880_, p_92881_, p_92882_, p_92878_.last().pose(), false);
   }

   public int draw(PoseStack p_92890_, Component p_92891_, float p_92892_, float p_92893_, int p_92894_) {
      return this.drawInternal(p_92891_.getVisualOrderText(), p_92892_, p_92893_, p_92894_, p_92890_.last().pose(), false);
   }

   public String bidirectionalShaping(String p_92802_) {
      try {
         Bidi bidi = new Bidi((new ArabicShaping(8)).shape(p_92802_), 127);
         bidi.setReorderingMode(0);
         return bidi.writeReordered(2);
      } catch (ArabicShapingException arabicshapingexception) {
         return p_92802_;
      }
   }

   private int drawInternal(String p_92804_, float p_92805_, float p_92806_, int p_92807_, Matrix4f p_92808_, boolean p_92809_, boolean p_92810_) {
      if (p_92804_ == null) {
         return 0;
      } else {
         MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         int i = this.drawInBatch(p_92804_, p_92805_, p_92806_, p_92807_, p_92809_, p_92808_, multibuffersource$buffersource, false, 0, 15728880, p_92810_);
         multibuffersource$buffersource.endBatch();
         return i;
      }
   }

   private int drawInternal(FormattedCharSequence p_92727_, float p_92728_, float p_92729_, int p_92730_, Matrix4f p_92731_, boolean p_92732_) {
      MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      int i = this.drawInBatch(p_92727_, p_92728_, p_92729_, p_92730_, p_92732_, p_92731_, multibuffersource$buffersource, false, 0, 15728880);
      multibuffersource$buffersource.endBatch();
      return i;
   }

   public int drawInBatch(String p_92812_, float p_92813_, float p_92814_, int p_92815_, boolean p_92816_, Matrix4f p_92817_, MultiBufferSource p_92818_, boolean p_92819_, int p_92820_, int p_92821_) {
      return this.drawInBatch(p_92812_, p_92813_, p_92814_, p_92815_, p_92816_, p_92817_, p_92818_, p_92819_, p_92820_, p_92821_, this.isBidirectional());
   }

   public int drawInBatch(String p_92823_, float p_92824_, float p_92825_, int p_92826_, boolean p_92827_, Matrix4f p_92828_, MultiBufferSource p_92829_, boolean p_92830_, int p_92831_, int p_92832_, boolean p_92833_) {
      return this.drawInternal(p_92823_, p_92824_, p_92825_, p_92826_, p_92827_, p_92828_, p_92829_, p_92830_, p_92831_, p_92832_, p_92833_);
   }

   public int drawInBatch(Component p_92842_, float p_92843_, float p_92844_, int p_92845_, boolean p_92846_, Matrix4f p_92847_, MultiBufferSource p_92848_, boolean p_92849_, int p_92850_, int p_92851_) {
      return this.drawInBatch(p_92842_.getVisualOrderText(), p_92843_, p_92844_, p_92845_, p_92846_, p_92847_, p_92848_, p_92849_, p_92850_, p_92851_);
   }

   public int drawInBatch(FormattedCharSequence p_92734_, float p_92735_, float p_92736_, int p_92737_, boolean p_92738_, Matrix4f p_92739_, MultiBufferSource p_92740_, boolean p_92741_, int p_92742_, int p_92743_) {
      return this.drawInternal(p_92734_, p_92735_, p_92736_, p_92737_, p_92738_, p_92739_, p_92740_, p_92741_, p_92742_, p_92743_);
   }

   public void drawInBatch8xOutline(FormattedCharSequence p_168646_, float p_168647_, float p_168648_, int p_168649_, int p_168650_, Matrix4f p_168651_, MultiBufferSource p_168652_, int p_168653_) {
      int i = adjustColor(p_168650_);
      Font.StringRenderOutput font$stringrenderoutput = new Font.StringRenderOutput(p_168652_, 0.0F, 0.0F, i, false, p_168651_, Font.DisplayMode.NORMAL, p_168653_);

      for(int j = -1; j <= 1; ++j) {
         for(int k = -1; k <= 1; ++k) {
            if (j != 0 || k != 0) {
               float[] afloat = new float[]{p_168647_};
               int l = j;
               int i1 = k;
               p_168646_.accept((p_168661_, p_168662_, p_168663_) -> {
                  boolean flag = p_168662_.isBold();
                  FontSet fontset = this.getFontSet(p_168662_.getFont());
                  GlyphInfo glyphinfo = fontset.getGlyphInfo(p_168663_);
                  font$stringrenderoutput.x = afloat[0] + (float)l * glyphinfo.getShadowOffset();
                  font$stringrenderoutput.y = p_168648_ + (float)i1 * glyphinfo.getShadowOffset();
                  afloat[0] += glyphinfo.getAdvance(flag);
                  return font$stringrenderoutput.accept(p_168661_, p_168662_.withColor(i), p_168663_);
               });
            }
         }
      }

      Font.StringRenderOutput font$stringrenderoutput1 = new Font.StringRenderOutput(p_168652_, p_168647_, p_168648_, adjustColor(p_168649_), false, p_168651_, Font.DisplayMode.POLYGON_OFFSET, p_168653_);
      p_168646_.accept(font$stringrenderoutput1);
      font$stringrenderoutput1.finish(0, p_168647_);
   }

   private static int adjustColor(int p_92720_) {
      return (p_92720_ & -67108864) == 0 ? p_92720_ | -16777216 : p_92720_;
   }

   private int drawInternal(String p_92909_, float p_92910_, float p_92911_, int p_92912_, boolean p_92913_, Matrix4f p_92914_, MultiBufferSource p_92915_, boolean p_92916_, int p_92917_, int p_92918_, boolean p_92919_) {
      if (p_92919_) {
         p_92909_ = this.bidirectionalShaping(p_92909_);
      }

      p_92912_ = adjustColor(p_92912_);
      Matrix4f matrix4f = p_92914_.copy();
      if (p_92913_) {
         this.renderText(p_92909_, p_92910_, p_92911_, p_92912_, true, p_92914_, p_92915_, p_92916_, p_92917_, p_92918_);
         matrix4f.translate(SHADOW_OFFSET);
      }

      p_92910_ = this.renderText(p_92909_, p_92910_, p_92911_, p_92912_, false, matrix4f, p_92915_, p_92916_, p_92917_, p_92918_);
      return (int)p_92910_ + (p_92913_ ? 1 : 0);
   }

   private int drawInternal(FormattedCharSequence p_92867_, float p_92868_, float p_92869_, int p_92870_, boolean p_92871_, Matrix4f p_92872_, MultiBufferSource p_92873_, boolean p_92874_, int p_92875_, int p_92876_) {
      p_92870_ = adjustColor(p_92870_);
      Matrix4f matrix4f = p_92872_.copy();
      if (p_92871_) {
         this.renderText(p_92867_, p_92868_, p_92869_, p_92870_, true, p_92872_, p_92873_, p_92874_, p_92875_, p_92876_);
         matrix4f.translate(SHADOW_OFFSET);
      }

      p_92868_ = this.renderText(p_92867_, p_92868_, p_92869_, p_92870_, false, matrix4f, p_92873_, p_92874_, p_92875_, p_92876_);
      return (int)p_92868_ + (p_92871_ ? 1 : 0);
   }

   private float renderText(String p_92898_, float p_92899_, float p_92900_, int p_92901_, boolean p_92902_, Matrix4f p_92903_, MultiBufferSource p_92904_, boolean p_92905_, int p_92906_, int p_92907_) {
      Font.StringRenderOutput font$stringrenderoutput = new Font.StringRenderOutput(p_92904_, p_92899_, p_92900_, p_92901_, p_92902_, p_92903_, p_92905_, p_92907_);
      StringDecomposer.iterateFormatted(p_92898_, Style.EMPTY, font$stringrenderoutput);
      return font$stringrenderoutput.finish(p_92906_, p_92899_);
   }

   private float renderText(FormattedCharSequence p_92927_, float p_92928_, float p_92929_, int p_92930_, boolean p_92931_, Matrix4f p_92932_, MultiBufferSource p_92933_, boolean p_92934_, int p_92935_, int p_92936_) {
      Font.StringRenderOutput font$stringrenderoutput = new Font.StringRenderOutput(p_92933_, p_92928_, p_92929_, p_92930_, p_92931_, p_92932_, p_92934_, p_92936_);
      p_92927_.accept(font$stringrenderoutput);
      return font$stringrenderoutput.finish(p_92935_, p_92928_);
   }

   void renderChar(BakedGlyph p_92788_, boolean p_92789_, boolean p_92790_, float p_92791_, float p_92792_, float p_92793_, Matrix4f p_92794_, VertexConsumer p_92795_, float p_92796_, float p_92797_, float p_92798_, float p_92799_, int p_92800_) {
      p_92788_.render(p_92790_, p_92792_, p_92793_, p_92794_, p_92795_, p_92796_, p_92797_, p_92798_, p_92799_, p_92800_);
      if (p_92789_) {
         p_92788_.render(p_92790_, p_92792_ + p_92791_, p_92793_, p_92794_, p_92795_, p_92796_, p_92797_, p_92798_, p_92799_, p_92800_);
      }

   }

   public int width(String p_92896_) {
      return Mth.ceil(this.splitter.stringWidth(p_92896_));
   }

   public int width(FormattedText p_92853_) {
      return Mth.ceil(this.splitter.stringWidth(p_92853_));
   }

   public int width(FormattedCharSequence p_92725_) {
      return Mth.ceil(this.splitter.stringWidth(p_92725_));
   }

   public String plainSubstrByWidth(String p_92838_, int p_92839_, boolean p_92840_) {
      return p_92840_ ? this.splitter.plainTailByWidth(p_92838_, p_92839_, Style.EMPTY) : this.splitter.plainHeadByWidth(p_92838_, p_92839_, Style.EMPTY);
   }

   public String plainSubstrByWidth(String p_92835_, int p_92836_) {
      return this.splitter.plainHeadByWidth(p_92835_, p_92836_, Style.EMPTY);
   }

   public FormattedText substrByWidth(FormattedText p_92855_, int p_92856_) {
      return this.splitter.headByWidth(p_92855_, p_92856_, Style.EMPTY);
   }

   public void drawWordWrap(FormattedText p_92858_, int p_92859_, int p_92860_, int p_92861_, int p_92862_) {
      Matrix4f matrix4f = Transformation.identity().getMatrix();

      for(FormattedCharSequence formattedcharsequence : this.split(p_92858_, p_92861_)) {
         this.drawInternal(formattedcharsequence, (float)p_92859_, (float)p_92860_, p_92862_, matrix4f, false);
         p_92860_ += 9;
      }

   }

   public int wordWrapHeight(String p_92921_, int p_92922_) {
      return 9 * this.splitter.splitLines(p_92921_, p_92922_, Style.EMPTY).size();
   }

   public List<FormattedCharSequence> split(FormattedText p_92924_, int p_92925_) {
      return Language.getInstance().getVisualOrder(this.splitter.splitLines(p_92924_, p_92925_, Style.EMPTY));
   }

   public boolean isBidirectional() {
      return Language.getInstance().isDefaultRightToLeft();
   }

   public StringSplitter getSplitter() {
      return this.splitter;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum DisplayMode {
      NORMAL,
      SEE_THROUGH,
      POLYGON_OFFSET;
   }

   @OnlyIn(Dist.CLIENT)
   class StringRenderOutput implements FormattedCharSink {
      final MultiBufferSource bufferSource;
      private final boolean dropShadow;
      private final float dimFactor;
      private final float r;
      private final float g;
      private final float b;
      private final float a;
      private final Matrix4f pose;
      private final Font.DisplayMode mode;
      private final int packedLightCoords;
      float x;
      float y;
      @Nullable
      private List<BakedGlyph.Effect> effects;

      private void addEffect(BakedGlyph.Effect p_92965_) {
         if (this.effects == null) {
            this.effects = Lists.newArrayList();
         }

         this.effects.add(p_92965_);
      }

      public StringRenderOutput(MultiBufferSource p_92953_, float p_92954_, float p_92955_, int p_92956_, boolean p_92957_, Matrix4f p_92958_, boolean p_92959_, int p_92960_) {
         this(p_92953_, p_92954_, p_92955_, p_92956_, p_92957_, p_92958_, p_92959_ ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, p_92960_);
      }

      public StringRenderOutput(MultiBufferSource p_181365_, float p_181366_, float p_181367_, int p_181368_, boolean p_181369_, Matrix4f p_181370_, Font.DisplayMode p_181371_, int p_181372_) {
         this.bufferSource = p_181365_;
         this.x = p_181366_;
         this.y = p_181367_;
         this.dropShadow = p_181369_;
         this.dimFactor = p_181369_ ? 0.25F : 1.0F;
         this.r = (float)(p_181368_ >> 16 & 255) / 255.0F * this.dimFactor;
         this.g = (float)(p_181368_ >> 8 & 255) / 255.0F * this.dimFactor;
         this.b = (float)(p_181368_ & 255) / 255.0F * this.dimFactor;
         this.a = (float)(p_181368_ >> 24 & 255) / 255.0F;
         this.pose = p_181370_;
         this.mode = p_181371_;
         this.packedLightCoords = p_181372_;
      }

      public boolean accept(int p_92967_, Style p_92968_, int p_92969_) {
         FontSet fontset = Font.this.getFontSet(p_92968_.getFont());
         GlyphInfo glyphinfo = fontset.getGlyphInfo(p_92969_);
         BakedGlyph bakedglyph = p_92968_.isObfuscated() && p_92969_ != 32 ? fontset.getRandomGlyph(glyphinfo) : fontset.getGlyph(p_92969_);
         boolean flag = p_92968_.isBold();
         float f3 = this.a;
         TextColor textcolor = p_92968_.getColor();
         float f;
         float f1;
         float f2;
         if (textcolor != null) {
            int i = textcolor.getValue();
            f = (float)(i >> 16 & 255) / 255.0F * this.dimFactor;
            f1 = (float)(i >> 8 & 255) / 255.0F * this.dimFactor;
            f2 = (float)(i & 255) / 255.0F * this.dimFactor;
         } else {
            f = this.r;
            f1 = this.g;
            f2 = this.b;
         }

         if (!(bakedglyph instanceof EmptyGlyph)) {
            float f5 = flag ? glyphinfo.getBoldOffset() : 0.0F;
            float f4 = this.dropShadow ? glyphinfo.getShadowOffset() : 0.0F;
            VertexConsumer vertexconsumer = this.bufferSource.getBuffer(bakedglyph.renderType(this.mode));
            Font.this.renderChar(bakedglyph, flag, p_92968_.isItalic(), f5, this.x + f4, this.y + f4, this.pose, vertexconsumer, f, f1, f2, f3, this.packedLightCoords);
         }

         float f6 = glyphinfo.getAdvance(flag);
         float f7 = this.dropShadow ? 1.0F : 0.0F;
         if (p_92968_.isStrikethrough()) {
            this.addEffect(new BakedGlyph.Effect(this.x + f7 - 1.0F, this.y + f7 + 4.5F, this.x + f7 + f6, this.y + f7 + 4.5F - 1.0F, 0.01F, f, f1, f2, f3));
         }

         if (p_92968_.isUnderlined()) {
            this.addEffect(new BakedGlyph.Effect(this.x + f7 - 1.0F, this.y + f7 + 9.0F, this.x + f7 + f6, this.y + f7 + 9.0F - 1.0F, 0.01F, f, f1, f2, f3));
         }

         this.x += f6;
         return true;
      }

      public float finish(int p_92962_, float p_92963_) {
         if (p_92962_ != 0) {
            float f = (float)(p_92962_ >> 24 & 255) / 255.0F;
            float f1 = (float)(p_92962_ >> 16 & 255) / 255.0F;
            float f2 = (float)(p_92962_ >> 8 & 255) / 255.0F;
            float f3 = (float)(p_92962_ & 255) / 255.0F;
            this.addEffect(new BakedGlyph.Effect(p_92963_ - 1.0F, this.y + 9.0F, this.x + 1.0F, this.y - 1.0F, 0.01F, f1, f2, f3, f));
         }

         if (this.effects != null) {
            BakedGlyph bakedglyph = Font.this.getFontSet(Style.DEFAULT_FONT).whiteGlyph();
            VertexConsumer vertexconsumer = this.bufferSource.getBuffer(bakedglyph.renderType(this.mode));

            for(BakedGlyph.Effect bakedglyph$effect : this.effects) {
               bakedglyph.renderEffect(bakedglyph$effect, this.pose, vertexconsumer, this.packedLightCoords);
            }
         }

         return this.x;
      }
   }
}