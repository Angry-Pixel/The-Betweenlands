package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;

public class UuidArgument implements ArgumentType<UUID> {
   public static final SimpleCommandExceptionType ERROR_INVALID_UUID = new SimpleCommandExceptionType(new TranslatableComponent("argument.uuid.invalid"));
   private static final Collection<String> EXAMPLES = Arrays.asList("dd12be42-52a9-4a91-a8a1-11c01849e498");
   private static final Pattern ALLOWED_CHARACTERS = Pattern.compile("^([-A-Fa-f0-9]+)");

   public static UUID getUuid(CommandContext<CommandSourceStack> p_113854_, String p_113855_) {
      return p_113854_.getArgument(p_113855_, UUID.class);
   }

   public static UuidArgument uuid() {
      return new UuidArgument();
   }

   public UUID parse(StringReader p_113852_) throws CommandSyntaxException {
      String s = p_113852_.getRemaining();
      Matcher matcher = ALLOWED_CHARACTERS.matcher(s);
      if (matcher.find()) {
         String s1 = matcher.group(1);

         try {
            UUID uuid = UUID.fromString(s1);
            p_113852_.setCursor(p_113852_.getCursor() + s1.length());
            return uuid;
         } catch (IllegalArgumentException illegalargumentexception) {
         }
      }

      throw ERROR_INVALID_UUID.create();
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}