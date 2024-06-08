package net.minecraft.client.gui.font;

import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.RawGlyph;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.MissingGlyph;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AllMissingGlyphProvider implements GlyphProvider {
   @Nullable
   public RawGlyph getGlyph(int p_94995_) {
      return MissingGlyph.INSTANCE;
   }

   public IntSet getSupportedGlyphs() {
      return IntSets.EMPTY_SET;
   }
}