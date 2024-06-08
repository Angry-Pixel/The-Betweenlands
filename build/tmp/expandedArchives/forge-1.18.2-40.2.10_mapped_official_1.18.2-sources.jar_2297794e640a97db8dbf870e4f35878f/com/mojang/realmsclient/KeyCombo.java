package com.mojang.realmsclient;

import java.util.Arrays;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyCombo {
   private final char[] chars;
   private int matchIndex;
   private final Runnable onCompletion;

   public KeyCombo(char[] p_86225_, Runnable p_86226_) {
      this.onCompletion = p_86226_;
      if (p_86225_.length < 1) {
         throw new IllegalArgumentException("Must have at least one char");
      } else {
         this.chars = p_86225_;
      }
   }

   public KeyCombo(char[] p_167171_) {
      this(p_167171_, () -> {
      });
   }

   public boolean keyPressed(char p_86229_) {
      if (p_86229_ == this.chars[this.matchIndex++]) {
         if (this.matchIndex == this.chars.length) {
            this.reset();
            this.onCompletion.run();
            return true;
         }
      } else {
         this.reset();
      }

      return false;
   }

   public void reset() {
      this.matchIndex = 0;
   }

   public String toString() {
      return "KeyCombo{chars=" + Arrays.toString(this.chars) + ", matchIndex=" + this.matchIndex + "}";
   }
}