package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;

public class ComponentUtils {
   public static final String DEFAULT_SEPARATOR_TEXT = ", ";
   public static final Component DEFAULT_SEPARATOR = (new TextComponent(", ")).withStyle(ChatFormatting.GRAY);
   public static final Component DEFAULT_NO_STYLE_SEPARATOR = new TextComponent(", ");

   public static MutableComponent mergeStyles(MutableComponent p_130751_, Style p_130752_) {
      if (p_130752_.isEmpty()) {
         return p_130751_;
      } else {
         Style style = p_130751_.getStyle();
         if (style.isEmpty()) {
            return p_130751_.setStyle(p_130752_);
         } else {
            return style.equals(p_130752_) ? p_130751_ : p_130751_.setStyle(style.applyTo(p_130752_));
         }
      }
   }

   public static Optional<MutableComponent> updateForEntity(@Nullable CommandSourceStack p_178425_, Optional<Component> p_178426_, @Nullable Entity p_178427_, int p_178428_) throws CommandSyntaxException {
      return p_178426_.isPresent() ? Optional.of(updateForEntity(p_178425_, p_178426_.get(), p_178427_, p_178428_)) : Optional.empty();
   }

   public static MutableComponent updateForEntity(@Nullable CommandSourceStack p_130732_, Component p_130733_, @Nullable Entity p_130734_, int p_130735_) throws CommandSyntaxException {
      if (p_130735_ > 100) {
         return p_130733_.copy();
      } else {
         MutableComponent mutablecomponent = p_130733_ instanceof ContextAwareComponent ? ((ContextAwareComponent)p_130733_).resolve(p_130732_, p_130734_, p_130735_ + 1) : p_130733_.plainCopy();

         for(Component component : p_130733_.getSiblings()) {
            mutablecomponent.append(updateForEntity(p_130732_, component, p_130734_, p_130735_ + 1));
         }

         return mutablecomponent.withStyle(resolveStyle(p_130732_, p_130733_.getStyle(), p_130734_, p_130735_));
      }
   }

   private static Style resolveStyle(@Nullable CommandSourceStack p_130737_, Style p_130738_, @Nullable Entity p_130739_, int p_130740_) throws CommandSyntaxException {
      HoverEvent hoverevent = p_130738_.getHoverEvent();
      if (hoverevent != null) {
         Component component = hoverevent.getValue(HoverEvent.Action.SHOW_TEXT);
         if (component != null) {
            HoverEvent hoverevent1 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, updateForEntity(p_130737_, component, p_130739_, p_130740_ + 1));
            return p_130738_.withHoverEvent(hoverevent1);
         }
      }

      return p_130738_;
   }

   public static Component getDisplayName(GameProfile p_130728_) {
      if (p_130728_.getName() != null) {
         return new TextComponent(p_130728_.getName());
      } else {
         return p_130728_.getId() != null ? new TextComponent(p_130728_.getId().toString()) : new TextComponent("(unknown)");
      }
   }

   public static Component formatList(Collection<String> p_130744_) {
      return formatAndSortList(p_130744_, (p_130742_) -> {
         return (new TextComponent(p_130742_)).withStyle(ChatFormatting.GREEN);
      });
   }

   public static <T extends Comparable<T>> Component formatAndSortList(Collection<T> p_130746_, Function<T, Component> p_130747_) {
      if (p_130746_.isEmpty()) {
         return TextComponent.EMPTY;
      } else if (p_130746_.size() == 1) {
         return p_130747_.apply(p_130746_.iterator().next());
      } else {
         List<T> list = Lists.newArrayList(p_130746_);
         list.sort(Comparable::compareTo);
         return formatList(list, p_130747_);
      }
   }

   public static <T> Component formatList(Collection<? extends T> p_178441_, Function<T, Component> p_178442_) {
      return formatList(p_178441_, DEFAULT_SEPARATOR, p_178442_);
   }

   public static <T> MutableComponent formatList(Collection<? extends T> p_178430_, Optional<? extends Component> p_178431_, Function<T, Component> p_178432_) {
      return formatList(p_178430_, DataFixUtils.orElse(p_178431_, DEFAULT_SEPARATOR), p_178432_);
   }

   public static Component formatList(Collection<? extends Component> p_178434_, Component p_178435_) {
      return formatList(p_178434_, p_178435_, Function.identity());
   }

   public static <T> MutableComponent formatList(Collection<? extends T> p_178437_, Component p_178438_, Function<T, Component> p_178439_) {
      if (p_178437_.isEmpty()) {
         return new TextComponent("");
      } else if (p_178437_.size() == 1) {
         return p_178439_.apply(p_178437_.iterator().next()).copy();
      } else {
         MutableComponent mutablecomponent = new TextComponent("");
         boolean flag = true;

         for(T t : p_178437_) {
            if (!flag) {
               mutablecomponent.append(p_178438_);
            }

            mutablecomponent.append(p_178439_.apply(t));
            flag = false;
         }

         return mutablecomponent;
      }
   }

   public static MutableComponent wrapInSquareBrackets(Component p_130749_) {
      return new TranslatableComponent("chat.square_brackets", p_130749_);
   }

   public static Component fromMessage(Message p_130730_) {
      return (Component)(p_130730_ instanceof Component ? (Component)p_130730_ : new TextComponent(p_130730_.getString()));
   }
}