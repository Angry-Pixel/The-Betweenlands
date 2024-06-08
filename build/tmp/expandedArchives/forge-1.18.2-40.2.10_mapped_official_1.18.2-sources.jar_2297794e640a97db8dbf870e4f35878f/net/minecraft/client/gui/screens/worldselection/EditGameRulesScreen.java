package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditGameRulesScreen extends Screen {
   private final Consumer<Optional<GameRules>> exitCallback;
   private EditGameRulesScreen.RuleList rules;
   private final Set<EditGameRulesScreen.RuleEntry> invalidEntries = Sets.newHashSet();
   private Button doneButton;
   @Nullable
   private List<FormattedCharSequence> tooltip;
   private final GameRules gameRules;

   public EditGameRulesScreen(GameRules p_101051_, Consumer<Optional<GameRules>> p_101052_) {
      super(new TranslatableComponent("editGamerule.title"));
      this.gameRules = p_101051_;
      this.exitCallback = p_101052_;
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      super.init();
      this.rules = new EditGameRulesScreen.RuleList(this.gameRules);
      this.addWidget(this.rules);
      this.addRenderableWidget(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, CommonComponents.GUI_CANCEL, (p_101073_) -> {
         this.exitCallback.accept(Optional.empty());
      }));
      this.doneButton = this.addRenderableWidget(new Button(this.width / 2 - 155, this.height - 29, 150, 20, CommonComponents.GUI_DONE, (p_101059_) -> {
         this.exitCallback.accept(Optional.of(this.gameRules));
      }));
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public void onClose() {
      this.exitCallback.accept(Optional.empty());
   }

   public void render(PoseStack p_101054_, int p_101055_, int p_101056_, float p_101057_) {
      this.tooltip = null;
      this.rules.render(p_101054_, p_101055_, p_101056_, p_101057_);
      drawCenteredString(p_101054_, this.font, this.title, this.width / 2, 20, 16777215);
      super.render(p_101054_, p_101055_, p_101056_, p_101057_);
      if (this.tooltip != null) {
         this.renderTooltip(p_101054_, this.tooltip, p_101055_, p_101056_);
      }

   }

   void setTooltip(@Nullable List<FormattedCharSequence> p_101082_) {
      this.tooltip = p_101082_;
   }

   private void updateDoneButton() {
      this.doneButton.active = this.invalidEntries.isEmpty();
   }

   void markInvalid(EditGameRulesScreen.RuleEntry p_101061_) {
      this.invalidEntries.add(p_101061_);
      this.updateDoneButton();
   }

   void clearInvalid(EditGameRulesScreen.RuleEntry p_101075_) {
      this.invalidEntries.remove(p_101075_);
      this.updateDoneButton();
   }

   @OnlyIn(Dist.CLIENT)
   public class BooleanRuleEntry extends EditGameRulesScreen.GameRuleEntry {
      private final CycleButton<Boolean> checkbox;

      public BooleanRuleEntry(Component p_101101_, List<FormattedCharSequence> p_101102_, String p_101103_, GameRules.BooleanValue p_101104_) {
         super(p_101102_, p_101101_);
         this.checkbox = CycleButton.onOffBuilder(p_101104_.get()).displayOnlyValue().withCustomNarration((p_170219_) -> {
            return p_170219_.createDefaultNarrationMessage().append("\n").append(p_101103_);
         }).create(10, 5, 44, 20, p_101101_, (p_170215_, p_170216_) -> {
            p_101104_.set(p_170216_, (MinecraftServer)null);
         });
         this.children.add(this.checkbox);
      }

      public void render(PoseStack p_101109_, int p_101110_, int p_101111_, int p_101112_, int p_101113_, int p_101114_, int p_101115_, int p_101116_, boolean p_101117_, float p_101118_) {
         this.renderLabel(p_101109_, p_101111_, p_101112_);
         this.checkbox.x = p_101112_ + p_101113_ - 45;
         this.checkbox.y = p_101111_;
         this.checkbox.render(p_101109_, p_101115_, p_101116_, p_101118_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class CategoryRuleEntry extends EditGameRulesScreen.RuleEntry {
      final Component label;

      public CategoryRuleEntry(Component p_101141_) {
         super((List<FormattedCharSequence>)null);
         this.label = p_101141_;
      }

      public void render(PoseStack p_101143_, int p_101144_, int p_101145_, int p_101146_, int p_101147_, int p_101148_, int p_101149_, int p_101150_, boolean p_101151_, float p_101152_) {
         GuiComponent.drawCenteredString(p_101143_, EditGameRulesScreen.this.minecraft.font, this.label, p_101146_ + p_101147_ / 2, p_101145_ + 5, 16777215);
      }

      public List<? extends GuiEventListener> children() {
         return ImmutableList.of();
      }

      public List<? extends NarratableEntry> narratables() {
         return ImmutableList.of(new NarratableEntry() {
            public NarratableEntry.NarrationPriority narrationPriority() {
               return NarratableEntry.NarrationPriority.HOVERED;
            }

            public void updateNarration(NarrationElementOutput p_170225_) {
               p_170225_.add(NarratedElementType.TITLE, CategoryRuleEntry.this.label);
            }
         });
      }
   }

   @FunctionalInterface
   @OnlyIn(Dist.CLIENT)
   interface EntryFactory<T extends GameRules.Value<T>> {
      EditGameRulesScreen.RuleEntry create(Component p_101155_, List<FormattedCharSequence> p_101156_, String p_101157_, T p_101158_);
   }

   @OnlyIn(Dist.CLIENT)
   public abstract class GameRuleEntry extends EditGameRulesScreen.RuleEntry {
      private final List<FormattedCharSequence> label;
      protected final List<AbstractWidget> children = Lists.newArrayList();

      public GameRuleEntry(List<FormattedCharSequence> p_101164_, Component p_101165_) {
         super(p_101164_);
         this.label = EditGameRulesScreen.this.minecraft.font.split(p_101165_, 175);
      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      public List<? extends NarratableEntry> narratables() {
         return this.children;
      }

      protected void renderLabel(PoseStack p_101167_, int p_101168_, int p_101169_) {
         if (this.label.size() == 1) {
            EditGameRulesScreen.this.minecraft.font.draw(p_101167_, this.label.get(0), (float)p_101169_, (float)(p_101168_ + 5), 16777215);
         } else if (this.label.size() >= 2) {
            EditGameRulesScreen.this.minecraft.font.draw(p_101167_, this.label.get(0), (float)p_101169_, (float)p_101168_, 16777215);
            EditGameRulesScreen.this.minecraft.font.draw(p_101167_, this.label.get(1), (float)p_101169_, (float)(p_101168_ + 10), 16777215);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public class IntegerRuleEntry extends EditGameRulesScreen.GameRuleEntry {
      private final EditBox input;

      public IntegerRuleEntry(Component p_101175_, List<FormattedCharSequence> p_101176_, String p_101177_, GameRules.IntegerValue p_101178_) {
         super(p_101176_, p_101175_);
         this.input = new EditBox(EditGameRulesScreen.this.minecraft.font, 10, 5, 42, 20, p_101175_.copy().append("\n").append(p_101177_).append("\n"));
         this.input.setValue(Integer.toString(p_101178_.get()));
         this.input.setResponder((p_101181_) -> {
            if (p_101178_.tryDeserialize(p_101181_)) {
               this.input.setTextColor(14737632);
               EditGameRulesScreen.this.clearInvalid(this);
            } else {
               this.input.setTextColor(16711680);
               EditGameRulesScreen.this.markInvalid(this);
            }

         });
         this.children.add(this.input);
      }

      public void render(PoseStack p_101183_, int p_101184_, int p_101185_, int p_101186_, int p_101187_, int p_101188_, int p_101189_, int p_101190_, boolean p_101191_, float p_101192_) {
         this.renderLabel(p_101183_, p_101185_, p_101186_);
         this.input.x = p_101186_ + p_101187_ - 44;
         this.input.y = p_101185_;
         this.input.render(p_101183_, p_101189_, p_101190_, p_101192_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class RuleEntry extends ContainerObjectSelectionList.Entry<EditGameRulesScreen.RuleEntry> {
      @Nullable
      final List<FormattedCharSequence> tooltip;

      public RuleEntry(@Nullable List<FormattedCharSequence> p_194062_) {
         this.tooltip = p_194062_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class RuleList extends ContainerObjectSelectionList<EditGameRulesScreen.RuleEntry> {
      public RuleList(final GameRules p_101203_) {
         super(EditGameRulesScreen.this.minecraft, EditGameRulesScreen.this.width, EditGameRulesScreen.this.height, 43, EditGameRulesScreen.this.height - 32, 24);
         final Map<GameRules.Category, Map<GameRules.Key<?>, EditGameRulesScreen.RuleEntry>> map = Maps.newHashMap();
         GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
            public void visitBoolean(GameRules.Key<GameRules.BooleanValue> p_101238_, GameRules.Type<GameRules.BooleanValue> p_101239_) {
               this.addEntry(p_101238_, (p_101228_, p_101229_, p_101230_, p_101231_) -> {
                  return EditGameRulesScreen.this.new BooleanRuleEntry(p_101228_, p_101229_, p_101230_, p_101231_);
               });
            }

            public void visitInteger(GameRules.Key<GameRules.IntegerValue> p_101241_, GameRules.Type<GameRules.IntegerValue> p_101242_) {
               this.addEntry(p_101241_, (p_101233_, p_101234_, p_101235_, p_101236_) -> {
                  return EditGameRulesScreen.this.new IntegerRuleEntry(p_101233_, p_101234_, p_101235_, p_101236_);
               });
            }

            private <T extends GameRules.Value<T>> void addEntry(GameRules.Key<T> p_101225_, EditGameRulesScreen.EntryFactory<T> p_101226_) {
               Component component = new TranslatableComponent(p_101225_.getDescriptionId());
               Component component1 = (new TextComponent(p_101225_.getId())).withStyle(ChatFormatting.YELLOW);
               T t = p_101203_.getRule(p_101225_);
               String s = t.serialize();
               Component component2 = (new TranslatableComponent("editGamerule.default", new TextComponent(s))).withStyle(ChatFormatting.GRAY);
               String s1 = p_101225_.getDescriptionId() + ".description";
               List<FormattedCharSequence> list;
               String s2;
               if (I18n.exists(s1)) {
                  Builder<FormattedCharSequence> builder = ImmutableList.<FormattedCharSequence>builder().add(component1.getVisualOrderText());
                  Component component3 = new TranslatableComponent(s1);
                  EditGameRulesScreen.this.font.split(component3, 150).forEach(builder::add);
                  list = builder.add(component2.getVisualOrderText()).build();
                  s2 = component3.getString() + "\n" + component2.getString();
               } else {
                  list = ImmutableList.of(component1.getVisualOrderText(), component2.getVisualOrderText());
                  s2 = component2.getString();
               }

               map.computeIfAbsent(p_101225_.getCategory(), (p_101223_) -> {
                  return Maps.newHashMap();
               }).put(p_101225_, p_101226_.create(component, list, s2, t));
            }
         });
         map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach((p_101210_) -> {
            this.addEntry(EditGameRulesScreen.this.new CategoryRuleEntry((new TranslatableComponent(p_101210_.getKey().getDescriptionId())).withStyle(new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.YELLOW})));
            p_101210_.getValue().entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.comparing(GameRules.Key::getId))).forEach((p_170229_) -> {
               this.addEntry(p_170229_.getValue());
            });
         });
      }

      public void render(PoseStack p_101205_, int p_101206_, int p_101207_, float p_101208_) {
         super.render(p_101205_, p_101206_, p_101207_, p_101208_);
         EditGameRulesScreen.RuleEntry editgamerulesscreen$ruleentry = this.getHovered();
         if (editgamerulesscreen$ruleentry != null) {
            EditGameRulesScreen.this.setTooltip(editgamerulesscreen$ruleentry.tooltip);
         }

      }
   }
}