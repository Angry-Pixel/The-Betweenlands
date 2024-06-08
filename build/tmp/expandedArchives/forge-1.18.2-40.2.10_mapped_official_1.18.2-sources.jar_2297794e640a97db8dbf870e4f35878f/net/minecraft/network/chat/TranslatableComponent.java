package net.minecraft.network.chat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.locale.Language;
import net.minecraft.world.entity.Entity;

public class TranslatableComponent extends BaseComponent implements ContextAwareComponent {
   private static final Object[] NO_ARGS = new Object[0];
   private static final FormattedText TEXT_PERCENT = FormattedText.of("%");
   private static final FormattedText TEXT_NULL = FormattedText.of("null");
   private final String key;
   private final Object[] args;
   @Nullable
   private Language decomposedWith;
   private List<FormattedText> decomposedParts = ImmutableList.of();
   private static final Pattern FORMAT_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

   public TranslatableComponent(String p_131305_) {
      this.key = p_131305_;
      this.args = NO_ARGS;
   }

   public TranslatableComponent(String p_131307_, Object... p_131308_) {
      this.key = p_131307_;
      this.args = p_131308_;
   }

   private void decompose() {
      Language language = Language.getInstance();
      if (language != this.decomposedWith) {
         this.decomposedWith = language;
         String s = language.getOrDefault(this.key);

         try {
            Builder<FormattedText> builder = ImmutableList.builder();
            this.decomposeTemplate(s, builder::add);
            this.decomposedParts = builder.build();
         } catch (TranslatableFormatException translatableformatexception) {
            this.decomposedParts = ImmutableList.of(FormattedText.of(s));
         }

      }
   }

   private void decomposeTemplate(String p_200006_, Consumer<FormattedText> p_200007_) {
      Matcher matcher = FORMAT_PATTERN.matcher(p_200006_);

      try {
         int i = 0;

         int j;
         int l;
         for(j = 0; matcher.find(j); j = l) {
            int k = matcher.start();
            l = matcher.end();
            if (k > j) {
               String s = p_200006_.substring(j, k);
               if (s.indexOf(37) != -1) {
                  throw new IllegalArgumentException();
               }

               p_200007_.accept(FormattedText.of(s));
            }

            String s4 = matcher.group(2);
            String s1 = p_200006_.substring(k, l);
            if ("%".equals(s4) && "%%".equals(s1)) {
               p_200007_.accept(TEXT_PERCENT);
            } else {
               if (!"s".equals(s4)) {
                  throw new TranslatableFormatException(this, "Unsupported format: '" + s1 + "'");
               }

               String s2 = matcher.group(1);
               int i1 = s2 != null ? Integer.parseInt(s2) - 1 : i++;
               if (i1 < this.args.length) {
                  p_200007_.accept(this.getArgument(i1));
               }
            }
         }

         if (j == 0) {
            // if we failed to match above, lets try the messageformat handler instead.
            j = net.minecraftforge.internal.TextComponentMessageFormatHandler.handle(this, p_200007_, this.args, p_200006_);
         }
         if (j < p_200006_.length()) {
            String s3 = p_200006_.substring(j);
            if (s3.indexOf(37) != -1) {
               throw new IllegalArgumentException();
            }

            p_200007_.accept(FormattedText.of(s3));
         }

      } catch (IllegalArgumentException illegalargumentexception) {
         throw new TranslatableFormatException(this, illegalargumentexception);
      }
   }

   private FormattedText getArgument(int p_131314_) {
      if (p_131314_ >= this.args.length) {
         throw new TranslatableFormatException(this, p_131314_);
      } else {
         Object object = this.args[p_131314_];
         if (object instanceof Component) {
            return (Component)object;
         } else {
            return object == null ? TEXT_NULL : FormattedText.of(object.toString());
         }
      }
   }

   public TranslatableComponent plainCopy() {
      return new TranslatableComponent(this.key, this.args);
   }

   public <T> Optional<T> visitSelf(FormattedText.StyledContentConsumer<T> p_131318_, Style p_131319_) {
      this.decompose();

      for(FormattedText formattedtext : this.decomposedParts) {
         Optional<T> optional = formattedtext.visit(p_131318_, p_131319_);
         if (optional.isPresent()) {
            return optional;
         }
      }

      return Optional.empty();
   }

   public <T> Optional<T> visitSelf(FormattedText.ContentConsumer<T> p_131316_) {
      this.decompose();

      for(FormattedText formattedtext : this.decomposedParts) {
         Optional<T> optional = formattedtext.visit(p_131316_);
         if (optional.isPresent()) {
            return optional;
         }
      }

      return Optional.empty();
   }

   public MutableComponent resolve(@Nullable CommandSourceStack p_131310_, @Nullable Entity p_131311_, int p_131312_) throws CommandSyntaxException {
      Object[] aobject = new Object[this.args.length];

      for(int i = 0; i < aobject.length; ++i) {
         Object object = this.args[i];
         if (object instanceof Component) {
            aobject[i] = ComponentUtils.updateForEntity(p_131310_, (Component)object, p_131311_, p_131312_);
         } else {
            aobject[i] = object;
         }
      }

      return new TranslatableComponent(this.key, aobject);
   }

   public boolean equals(Object p_131324_) {
      if (this == p_131324_) {
         return true;
      } else if (!(p_131324_ instanceof TranslatableComponent)) {
         return false;
      } else {
         TranslatableComponent translatablecomponent = (TranslatableComponent)p_131324_;
         return Arrays.equals(this.args, translatablecomponent.args) && this.key.equals(translatablecomponent.key) && super.equals(p_131324_);
      }
   }

   public int hashCode() {
      int i = super.hashCode();
      i = 31 * i + this.key.hashCode();
      return 31 * i + Arrays.hashCode(this.args);
   }

   public String toString() {
      return "TranslatableComponent{key='" + this.key + "', args=" + Arrays.toString(this.args) + ", siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
   }

   public String getKey() {
      return this.key;
   }

   public Object[] getArgs() {
      return this.args;
   }
}
