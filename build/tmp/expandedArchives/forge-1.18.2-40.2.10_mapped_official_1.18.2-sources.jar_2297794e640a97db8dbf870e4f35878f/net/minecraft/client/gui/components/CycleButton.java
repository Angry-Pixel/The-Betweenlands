package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CycleButton<T> extends AbstractButton implements TooltipAccessor {
   static final BooleanSupplier DEFAULT_ALT_LIST_SELECTOR = Screen::hasAltDown;
   private static final List<Boolean> BOOLEAN_OPTIONS = ImmutableList.of(Boolean.TRUE, Boolean.FALSE);
   private final Component name;
   private int index;
   private T value;
   private final CycleButton.ValueListSupplier<T> values;
   private final Function<T, Component> valueStringifier;
   private final Function<CycleButton<T>, MutableComponent> narrationProvider;
   private final CycleButton.OnValueChange<T> onValueChange;
   private final CycleButton.TooltipSupplier<T> tooltipSupplier;
   private final boolean displayOnlyValue;

   CycleButton(int p_168869_, int p_168870_, int p_168871_, int p_168872_, Component p_168873_, Component p_168874_, int p_168875_, T p_168876_, CycleButton.ValueListSupplier<T> p_168877_, Function<T, Component> p_168878_, Function<CycleButton<T>, MutableComponent> p_168879_, CycleButton.OnValueChange<T> p_168880_, CycleButton.TooltipSupplier<T> p_168881_, boolean p_168882_) {
      super(p_168869_, p_168870_, p_168871_, p_168872_, p_168873_);
      this.name = p_168874_;
      this.index = p_168875_;
      this.value = p_168876_;
      this.values = p_168877_;
      this.valueStringifier = p_168878_;
      this.narrationProvider = p_168879_;
      this.onValueChange = p_168880_;
      this.tooltipSupplier = p_168881_;
      this.displayOnlyValue = p_168882_;
   }

   public void onPress() {
      if (Screen.hasShiftDown()) {
         this.cycleValue(-1);
      } else {
         this.cycleValue(1);
      }

   }

   private void cycleValue(int p_168909_) {
      List<T> list = this.values.getSelectedList();
      this.index = Mth.positiveModulo(this.index + p_168909_, list.size());
      T t = list.get(this.index);
      this.updateValue(t);
      this.onValueChange.onValueChange(this, t);
   }

   private T getCycledValue(int p_168915_) {
      List<T> list = this.values.getSelectedList();
      return list.get(Mth.positiveModulo(this.index + p_168915_, list.size()));
   }

   public boolean mouseScrolled(double p_168885_, double p_168886_, double p_168887_) {
      if (p_168887_ > 0.0D) {
         this.cycleValue(-1);
      } else if (p_168887_ < 0.0D) {
         this.cycleValue(1);
      }

      return true;
   }

   public void setValue(T p_168893_) {
      List<T> list = this.values.getSelectedList();
      int i = list.indexOf(p_168893_);
      if (i != -1) {
         this.index = i;
      }

      this.updateValue(p_168893_);
   }

   private void updateValue(T p_168906_) {
      Component component = this.createLabelForValue(p_168906_);
      this.setMessage(component);
      this.value = p_168906_;
   }

   private Component createLabelForValue(T p_168911_) {
      return (Component)(this.displayOnlyValue ? this.valueStringifier.apply(p_168911_) : this.createFullName(p_168911_));
   }

   private MutableComponent createFullName(T p_168913_) {
      return CommonComponents.optionNameValue(this.name, this.valueStringifier.apply(p_168913_));
   }

   public T getValue() {
      return this.value;
   }

   protected MutableComponent createNarrationMessage() {
      return this.narrationProvider.apply(this);
   }

   public void updateNarration(NarrationElementOutput p_168889_) {
      p_168889_.add(NarratedElementType.TITLE, this.createNarrationMessage());
      if (this.active) {
         T t = this.getCycledValue(1);
         Component component = this.createLabelForValue(t);
         if (this.isFocused()) {
            p_168889_.add(NarratedElementType.USAGE, new TranslatableComponent("narration.cycle_button.usage.focused", component));
         } else {
            p_168889_.add(NarratedElementType.USAGE, new TranslatableComponent("narration.cycle_button.usage.hovered", component));
         }
      }

   }

   public MutableComponent createDefaultNarrationMessage() {
      return wrapDefaultNarrationMessage((Component)(this.displayOnlyValue ? this.createFullName(this.value) : this.getMessage()));
   }

   public List<FormattedCharSequence> getTooltip() {
      return this.tooltipSupplier.apply(this.value);
   }

   public static <T> CycleButton.Builder<T> builder(Function<T, Component> p_168895_) {
      return new CycleButton.Builder<>(p_168895_);
   }

   public static CycleButton.Builder<Boolean> booleanBuilder(Component p_168897_, Component p_168898_) {
      return (new CycleButton.Builder<Boolean>((p_168902_) -> {
         return p_168902_ ? p_168897_ : p_168898_;
      })).withValues(BOOLEAN_OPTIONS);
   }

   public static CycleButton.Builder<Boolean> onOffBuilder() {
      return (new CycleButton.Builder<Boolean>((p_168891_) -> {
         return p_168891_ ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
      })).withValues(BOOLEAN_OPTIONS);
   }

   public static CycleButton.Builder<Boolean> onOffBuilder(boolean p_168917_) {
      return onOffBuilder().withInitialValue(p_168917_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder<T> {
      private int initialIndex;
      @Nullable
      private T initialValue;
      private final Function<T, Component> valueStringifier;
      private CycleButton.TooltipSupplier<T> tooltipSupplier = (p_168964_) -> {
         return ImmutableList.of();
      };
      private Function<CycleButton<T>, MutableComponent> narrationProvider = CycleButton::createDefaultNarrationMessage;
      private CycleButton.ValueListSupplier<T> values = CycleButton.ValueListSupplier.create(ImmutableList.of());
      private boolean displayOnlyValue;

      public Builder(Function<T, Component> p_168928_) {
         this.valueStringifier = p_168928_;
      }

      public CycleButton.Builder<T> withValues(List<T> p_168951_) {
         this.values = CycleButton.ValueListSupplier.create(p_168951_);
         return this;
      }

      @SafeVarargs
      public final CycleButton.Builder<T> withValues(T... p_168962_) {
         return this.withValues(ImmutableList.copyOf(p_168962_));
      }

      public CycleButton.Builder<T> withValues(List<T> p_168953_, List<T> p_168954_) {
         this.values = CycleButton.ValueListSupplier.create(CycleButton.DEFAULT_ALT_LIST_SELECTOR, p_168953_, p_168954_);
         return this;
      }

      public CycleButton.Builder<T> withValues(BooleanSupplier p_168956_, List<T> p_168957_, List<T> p_168958_) {
         this.values = CycleButton.ValueListSupplier.create(p_168956_, p_168957_, p_168958_);
         return this;
      }

      public CycleButton.Builder<T> withTooltip(CycleButton.TooltipSupplier<T> p_168944_) {
         this.tooltipSupplier = p_168944_;
         return this;
      }

      public CycleButton.Builder<T> withInitialValue(T p_168949_) {
         this.initialValue = p_168949_;
         int i = this.values.getDefaultList().indexOf(p_168949_);
         if (i != -1) {
            this.initialIndex = i;
         }

         return this;
      }

      public CycleButton.Builder<T> withCustomNarration(Function<CycleButton<T>, MutableComponent> p_168960_) {
         this.narrationProvider = p_168960_;
         return this;
      }

      public CycleButton.Builder<T> displayOnlyValue() {
         this.displayOnlyValue = true;
         return this;
      }

      public CycleButton<T> create(int p_168931_, int p_168932_, int p_168933_, int p_168934_, Component p_168935_) {
         return this.create(p_168931_, p_168932_, p_168933_, p_168934_, p_168935_, (p_168946_, p_168947_) -> {
         });
      }

      public CycleButton<T> create(int p_168937_, int p_168938_, int p_168939_, int p_168940_, Component p_168941_, CycleButton.OnValueChange<T> p_168942_) {
         List<T> list = this.values.getDefaultList();
         if (list.isEmpty()) {
            throw new IllegalStateException("No values for cycle button");
         } else {
            T t = (T)(this.initialValue != null ? this.initialValue : list.get(this.initialIndex));
            Component component = this.valueStringifier.apply(t);
            Component component1 = (Component)(this.displayOnlyValue ? component : CommonComponents.optionNameValue(p_168941_, component));
            return new CycleButton<>(p_168937_, p_168938_, p_168939_, p_168940_, component1, p_168941_, this.initialIndex, t, this.values, this.valueStringifier, this.narrationProvider, p_168942_, this.tooltipSupplier, this.displayOnlyValue);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface OnValueChange<T> {
      void onValueChange(CycleButton p_168966_, T p_168967_);
   }

   @FunctionalInterface
   @OnlyIn(Dist.CLIENT)
   public interface TooltipSupplier<T> extends Function<T, List<FormattedCharSequence>> {
   }

   @OnlyIn(Dist.CLIENT)
   interface ValueListSupplier<T> {
      List<T> getSelectedList();

      List<T> getDefaultList();

      static <T> CycleButton.ValueListSupplier<T> create(List<T> p_168969_) {
         final List<T> list = ImmutableList.copyOf(p_168969_);
         return new CycleButton.ValueListSupplier<T>() {
            public List<T> getSelectedList() {
               return list;
            }

            public List<T> getDefaultList() {
               return list;
            }
         };
      }

      static <T> CycleButton.ValueListSupplier<T> create(final BooleanSupplier p_168971_, List<T> p_168972_, List<T> p_168973_) {
         final List<T> list = ImmutableList.copyOf(p_168972_);
         final List<T> list1 = ImmutableList.copyOf(p_168973_);
         return new CycleButton.ValueListSupplier<T>() {
            public List<T> getSelectedList() {
               return p_168971_.getAsBoolean() ? list1 : list;
            }

            public List<T> getDefaultList() {
               return list;
            }
         };
      }
   }
}