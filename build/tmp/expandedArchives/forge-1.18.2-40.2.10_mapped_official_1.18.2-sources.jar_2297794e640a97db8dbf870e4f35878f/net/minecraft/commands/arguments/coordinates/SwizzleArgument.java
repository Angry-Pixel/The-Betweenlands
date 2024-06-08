package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;

public class SwizzleArgument implements ArgumentType<EnumSet<Direction.Axis>> {
   private static final Collection<String> EXAMPLES = Arrays.asList("xyz", "x");
   private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(new TranslatableComponent("arguments.swizzle.invalid"));

   public static SwizzleArgument swizzle() {
      return new SwizzleArgument();
   }

   public static EnumSet<Direction.Axis> getSwizzle(CommandContext<CommandSourceStack> p_120811_, String p_120812_) {
      return p_120811_.getArgument(p_120812_, EnumSet.class);
   }

   public EnumSet<Direction.Axis> parse(StringReader p_120809_) throws CommandSyntaxException {
      EnumSet<Direction.Axis> enumset = EnumSet.noneOf(Direction.Axis.class);

      while(p_120809_.canRead() && p_120809_.peek() != ' ') {
         char c0 = p_120809_.read();
         Direction.Axis direction$axis;
         switch(c0) {
         case 'x':
            direction$axis = Direction.Axis.X;
            break;
         case 'y':
            direction$axis = Direction.Axis.Y;
            break;
         case 'z':
            direction$axis = Direction.Axis.Z;
            break;
         default:
            throw ERROR_INVALID.create();
         }

         if (enumset.contains(direction$axis)) {
            throw ERROR_INVALID.create();
         }

         enumset.add(direction$axis);
      }

      return enumset;
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}