package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class ParticleArgument implements ArgumentType<ParticleOptions> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "particle with options");
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_PARTICLE = new DynamicCommandExceptionType((p_103941_) -> {
      return new TranslatableComponent("particle.notFound", p_103941_);
   });

   public static ParticleArgument particle() {
      return new ParticleArgument();
   }

   public static ParticleOptions getParticle(CommandContext<CommandSourceStack> p_103938_, String p_103939_) {
      return p_103938_.getArgument(p_103939_, ParticleOptions.class);
   }

   public ParticleOptions parse(StringReader p_103933_) throws CommandSyntaxException {
      return readParticle(p_103933_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static ParticleOptions readParticle(StringReader p_103945_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = ResourceLocation.read(p_103945_);
      ParticleType<?> particletype = Registry.PARTICLE_TYPE.getOptional(resourcelocation).orElseThrow(() -> {
         return ERROR_UNKNOWN_PARTICLE.create(resourcelocation);
      });
      return readParticle(p_103945_, particletype);
   }

   private static <T extends ParticleOptions> T readParticle(StringReader p_103935_, ParticleType<T> p_103936_) throws CommandSyntaxException {
      return p_103936_.getDeserializer().fromCommand(p_103936_, p_103935_);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_103948_, SuggestionsBuilder p_103949_) {
      return SharedSuggestionProvider.suggestResource(Registry.PARTICLE_TYPE.keySet(), p_103949_);
   }
}