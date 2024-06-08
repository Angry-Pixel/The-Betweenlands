package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

public class CompoundTagArgument implements ArgumentType<CompoundTag> {
   private static final Collection<String> EXAMPLES = Arrays.asList("{}", "{foo=bar}");

   private CompoundTagArgument() {
   }

   public static CompoundTagArgument compoundTag() {
      return new CompoundTagArgument();
   }

   public static <S> CompoundTag getCompoundTag(CommandContext<S> p_87661_, String p_87662_) {
      return p_87661_.getArgument(p_87662_, CompoundTag.class);
   }

   public CompoundTag parse(StringReader p_87659_) throws CommandSyntaxException {
      return (new TagParser(p_87659_)).readStruct();
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}