package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class ResourceKeyArgument<T> implements ArgumentType<ResourceKey<T>> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_ATTRIBUTE = new DynamicCommandExceptionType((p_212392_) -> {
      return new TranslatableComponent("attribute.unknown", p_212392_);
   });
   private static final DynamicCommandExceptionType ERROR_INVALID_FEATURE = new DynamicCommandExceptionType((p_212385_) -> {
      return new TranslatableComponent("commands.placefeature.invalid", p_212385_);
   });
   final ResourceKey<? extends Registry<T>> registryKey;

   public ResourceKeyArgument(ResourceKey<? extends Registry<T>> p_212367_) {
      this.registryKey = p_212367_;
   }

   public static <T> ResourceKeyArgument<T> key(ResourceKey<? extends Registry<T>> p_212387_) {
      return new ResourceKeyArgument<>(p_212387_);
   }

   private static <T> ResourceKey<T> getRegistryType(CommandContext<CommandSourceStack> p_212374_, String p_212375_, ResourceKey<Registry<T>> p_212376_, DynamicCommandExceptionType p_212377_) throws CommandSyntaxException {
      ResourceKey<?> resourcekey = p_212374_.getArgument(p_212375_, ResourceKey.class);
      Optional<ResourceKey<T>> optional = resourcekey.cast(p_212376_);
      return optional.orElseThrow(() -> {
         return p_212377_.create(resourcekey);
      });
   }

   private static <T> Registry<T> getRegistry(CommandContext<CommandSourceStack> p_212379_, ResourceKey<? extends Registry<T>> p_212380_) {
      return p_212379_.getSource().getServer().registryAccess().registryOrThrow(p_212380_);
   }

   public static Attribute getAttribute(CommandContext<CommandSourceStack> p_212371_, String p_212372_) throws CommandSyntaxException {
      ResourceKey<Attribute> resourcekey = getRegistryType(p_212371_, p_212372_, Registry.ATTRIBUTE_REGISTRY, ERROR_UNKNOWN_ATTRIBUTE);
      return getRegistry(p_212371_, Registry.ATTRIBUTE_REGISTRY).getOptional(resourcekey).orElseThrow(() -> {
         return ERROR_UNKNOWN_ATTRIBUTE.create(resourcekey.location());
      });
   }

   public static Holder<ConfiguredFeature<?, ?>> getConfiguredFeature(CommandContext<CommandSourceStack> p_212389_, String p_212390_) throws CommandSyntaxException {
      ResourceKey<ConfiguredFeature<?, ?>> resourcekey = getRegistryType(p_212389_, p_212390_, Registry.CONFIGURED_FEATURE_REGISTRY, ERROR_INVALID_FEATURE);
      return getRegistry(p_212389_, Registry.CONFIGURED_FEATURE_REGISTRY).getHolder(resourcekey).orElseThrow(() -> {
         return ERROR_INVALID_FEATURE.create(resourcekey.location());
      });
   }

   public ResourceKey<T> parse(StringReader p_212369_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = ResourceLocation.read(p_212369_);
      return ResourceKey.create(this.registryKey, resourcelocation);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_212399_, SuggestionsBuilder p_212400_) {
      Object object = p_212399_.getSource();
      if (object instanceof SharedSuggestionProvider) {
         SharedSuggestionProvider sharedsuggestionprovider = (SharedSuggestionProvider)object;
         return sharedsuggestionprovider.suggestRegistryElements(this.registryKey, SharedSuggestionProvider.ElementSuggestionType.ELEMENTS, p_212400_, p_212399_);
      } else {
         return p_212400_.buildFuture();
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static class Serializer implements ArgumentSerializer<ResourceKeyArgument<?>> {
      public void serializeToNetwork(ResourceKeyArgument<?> p_212414_, FriendlyByteBuf p_212415_) {
         p_212415_.writeResourceLocation(p_212414_.registryKey.location());
      }

      public ResourceKeyArgument<?> deserializeFromNetwork(FriendlyByteBuf p_212417_) {
         ResourceLocation resourcelocation = p_212417_.readResourceLocation();
         return new ResourceKeyArgument(ResourceKey.createRegistryKey(resourcelocation));
      }

      public void serializeToJson(ResourceKeyArgument<?> p_212411_, JsonObject p_212412_) {
         p_212412_.addProperty("registry", p_212411_.registryKey.location().toString());
      }
   }
}