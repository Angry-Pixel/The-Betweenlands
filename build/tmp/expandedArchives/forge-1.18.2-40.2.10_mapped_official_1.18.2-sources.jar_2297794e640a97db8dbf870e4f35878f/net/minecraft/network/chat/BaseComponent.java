package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.locale.Language;
import net.minecraft.util.FormattedCharSequence;

public abstract class BaseComponent implements MutableComponent {
   protected final List<Component> siblings = Lists.newArrayList();
   private FormattedCharSequence visualOrderText = FormattedCharSequence.EMPTY;
   @Nullable
   private Language decomposedWith;
   private Style style = Style.EMPTY;

   public MutableComponent append(Component p_130585_) {
      this.siblings.add(p_130585_);
      return this;
   }

   public String getContents() {
      return "";
   }

   public List<Component> getSiblings() {
      return this.siblings;
   }

   public MutableComponent setStyle(Style p_130587_) {
      this.style = p_130587_;
      return this;
   }

   public Style getStyle() {
      return this.style;
   }

   public abstract BaseComponent plainCopy();

   public final MutableComponent copy() {
      BaseComponent basecomponent = this.plainCopy();
      basecomponent.siblings.addAll(this.siblings);
      basecomponent.setStyle(this.style);
      return basecomponent;
   }

   public FormattedCharSequence getVisualOrderText() {
      Language language = Language.getInstance();
      if (this.decomposedWith != language) {
         this.visualOrderText = language.getVisualOrder(this);
         this.decomposedWith = language;
      }

      return this.visualOrderText;
   }

   public boolean equals(Object p_130593_) {
      if (this == p_130593_) {
         return true;
      } else if (!(p_130593_ instanceof BaseComponent)) {
         return false;
      } else {
         BaseComponent basecomponent = (BaseComponent)p_130593_;
         return this.siblings.equals(basecomponent.siblings) && Objects.equals(this.getStyle(), basecomponent.getStyle());
      }
   }

   public int hashCode() {
      return Objects.hash(this.getStyle(), this.siblings);
   }

   public String toString() {
      return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + "}";
   }
}