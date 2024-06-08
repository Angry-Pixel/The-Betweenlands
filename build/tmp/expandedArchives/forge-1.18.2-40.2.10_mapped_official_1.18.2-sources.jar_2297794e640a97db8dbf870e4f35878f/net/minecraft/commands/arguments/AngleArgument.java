package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;

public class AngleArgument implements ArgumentType<AngleArgument.SingleAngle> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0", "~", "~-5");
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(new TranslatableComponent("argument.angle.incomplete"));
   public static final SimpleCommandExceptionType ERROR_INVALID_ANGLE = new SimpleCommandExceptionType(new TranslatableComponent("argument.angle.invalid"));

   public static AngleArgument angle() {
      return new AngleArgument();
   }

   public static float getAngle(CommandContext<CommandSourceStack> p_83811_, String p_83812_) {
      return p_83811_.getArgument(p_83812_, AngleArgument.SingleAngle.class).getAngle(p_83811_.getSource());
   }

   public AngleArgument.SingleAngle parse(StringReader p_83809_) throws CommandSyntaxException {
      if (!p_83809_.canRead()) {
         throw ERROR_NOT_COMPLETE.createWithContext(p_83809_);
      } else {
         boolean flag = WorldCoordinate.isRelative(p_83809_);
         float f = p_83809_.canRead() && p_83809_.peek() != ' ' ? p_83809_.readFloat() : 0.0F;
         if (!Float.isNaN(f) && !Float.isInfinite(f)) {
            return new AngleArgument.SingleAngle(f, flag);
         } else {
            throw ERROR_INVALID_ANGLE.createWithContext(p_83809_);
         }
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static final class SingleAngle {
      private final float angle;
      private final boolean isRelative;

      SingleAngle(float p_83819_, boolean p_83820_) {
         this.angle = p_83819_;
         this.isRelative = p_83820_;
      }

      public float getAngle(CommandSourceStack p_83826_) {
         return Mth.wrapDegrees(this.isRelative ? this.angle + p_83826_.getRotation().y : this.angle);
      }
   }
}