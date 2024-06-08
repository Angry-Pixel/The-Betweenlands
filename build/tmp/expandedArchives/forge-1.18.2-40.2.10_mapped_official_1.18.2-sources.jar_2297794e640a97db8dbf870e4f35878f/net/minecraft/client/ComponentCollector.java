package net.minecraft.client;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.FormattedText;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ComponentCollector {
   private final List<FormattedText> parts = Lists.newArrayList();

   public void append(FormattedText p_90676_) {
      this.parts.add(p_90676_);
   }

   @Nullable
   public FormattedText getResult() {
      if (this.parts.isEmpty()) {
         return null;
      } else {
         return this.parts.size() == 1 ? this.parts.get(0) : FormattedText.composite(this.parts);
      }
   }

   public FormattedText getResultOrEmpty() {
      FormattedText formattedtext = this.getResult();
      return formattedtext != null ? formattedtext : FormattedText.EMPTY;
   }

   public void reset() {
      this.parts.clear();
   }
}