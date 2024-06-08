package net.minecraft.client.gui.narration;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface NarrationElementOutput {
   default void add(NarratedElementType p_169147_, Component p_169148_) {
      this.add(p_169147_, NarrationThunk.from(p_169148_.getString()));
   }

   default void add(NarratedElementType p_169144_, String p_169145_) {
      this.add(p_169144_, NarrationThunk.from(p_169145_));
   }

   default void add(NarratedElementType p_169150_, Component... p_169151_) {
      this.add(p_169150_, NarrationThunk.from(ImmutableList.copyOf(p_169151_)));
   }

   void add(NarratedElementType p_169141_, NarrationThunk<?> p_169142_);

   NarrationElementOutput nest();
}