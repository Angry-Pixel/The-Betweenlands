package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionsList extends ContainerObjectSelectionList<OptionsList.Entry> {
   public OptionsList(Minecraft p_94465_, int p_94466_, int p_94467_, int p_94468_, int p_94469_, int p_94470_) {
      super(p_94465_, p_94466_, p_94467_, p_94468_, p_94469_, p_94470_);
      this.centerListVertically = false;
   }

   public int addBig(Option p_94472_) {
      return this.addEntry(OptionsList.Entry.big(this.minecraft.options, this.width, p_94472_));
   }

   public void addSmall(Option p_94474_, @Nullable Option p_94475_) {
      this.addEntry(OptionsList.Entry.small(this.minecraft.options, this.width, p_94474_, p_94475_));
   }

   public void addSmall(Option[] p_94477_) {
      for(int i = 0; i < p_94477_.length; i += 2) {
         this.addSmall(p_94477_[i], i < p_94477_.length - 1 ? p_94477_[i + 1] : null);
      }

   }

   public int getRowWidth() {
      return 400;
   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 32;
   }

   @Nullable
   public AbstractWidget findOption(Option p_94479_) {
      for(OptionsList.Entry optionslist$entry : this.children()) {
         AbstractWidget abstractwidget = optionslist$entry.options.get(p_94479_);
         if (abstractwidget != null) {
            return abstractwidget;
         }
      }

      return null;
   }

   public Optional<AbstractWidget> getMouseOver(double p_94481_, double p_94482_) {
      for(OptionsList.Entry optionslist$entry : this.children()) {
         for(AbstractWidget abstractwidget : optionslist$entry.children) {
            if (abstractwidget.isMouseOver(p_94481_, p_94482_)) {
               return Optional.of(abstractwidget);
            }
         }
      }

      return Optional.empty();
   }

   @OnlyIn(Dist.CLIENT)
   protected static class Entry extends ContainerObjectSelectionList.Entry<OptionsList.Entry> {
      final Map<Option, AbstractWidget> options;
      final List<AbstractWidget> children;

      private Entry(Map<Option, AbstractWidget> p_169047_) {
         this.options = p_169047_;
         this.children = ImmutableList.copyOf(p_169047_.values());
      }

      public static OptionsList.Entry big(Options p_94507_, int p_94508_, Option p_94509_) {
         return new OptionsList.Entry(ImmutableMap.of(p_94509_, p_94509_.createButton(p_94507_, p_94508_ / 2 - 155, 0, 310)));
      }

      public static OptionsList.Entry small(Options p_94511_, int p_94512_, Option p_94513_, @Nullable Option p_94514_) {
         AbstractWidget abstractwidget = p_94513_.createButton(p_94511_, p_94512_ / 2 - 155, 0, 150);
         return p_94514_ == null ? new OptionsList.Entry(ImmutableMap.of(p_94513_, abstractwidget)) : new OptionsList.Entry(ImmutableMap.of(p_94513_, abstractwidget, p_94514_, p_94514_.createButton(p_94511_, p_94512_ / 2 - 155 + 160, 0, 150)));
      }

      public void render(PoseStack p_94496_, int p_94497_, int p_94498_, int p_94499_, int p_94500_, int p_94501_, int p_94502_, int p_94503_, boolean p_94504_, float p_94505_) {
         this.children.forEach((p_94494_) -> {
            p_94494_.y = p_94498_;
            p_94494_.render(p_94496_, p_94502_, p_94503_, p_94505_);
         });
      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      public List<? extends NarratableEntry> narratables() {
         return this.children;
      }
   }
}