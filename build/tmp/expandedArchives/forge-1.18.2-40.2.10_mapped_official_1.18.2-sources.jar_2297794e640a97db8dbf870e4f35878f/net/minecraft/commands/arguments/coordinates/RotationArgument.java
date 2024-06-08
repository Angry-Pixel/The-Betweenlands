package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;

public class RotationArgument implements ArgumentType<Coordinates> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~-5 ~5");
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(new TranslatableComponent("argument.rotation.incomplete"));

   public static RotationArgument rotation() {
      return new RotationArgument();
   }

   public static Coordinates getRotation(CommandContext<CommandSourceStack> p_120483_, String p_120484_) {
      return p_120483_.getArgument(p_120484_, Coordinates.class);
   }

   public Coordinates parse(StringReader p_120481_) throws CommandSyntaxException {
      int i = p_120481_.getCursor();
      if (!p_120481_.canRead()) {
         throw ERROR_NOT_COMPLETE.createWithContext(p_120481_);
      } else {
         WorldCoordinate worldcoordinate = WorldCoordinate.parseDouble(p_120481_, false);
         if (p_120481_.canRead() && p_120481_.peek() == ' ') {
            p_120481_.skip();
            WorldCoordinate worldcoordinate1 = WorldCoordinate.parseDouble(p_120481_, false);
            return new WorldCoordinates(worldcoordinate1, worldcoordinate, new WorldCoordinate(true, 0.0D));
         } else {
            p_120481_.setCursor(i);
            throw ERROR_NOT_COMPLETE.createWithContext(p_120481_);
         }
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}