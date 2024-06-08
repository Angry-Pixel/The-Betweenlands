package net.minecraft.commands.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityAnchorArgument implements ArgumentType<EntityAnchorArgument.Anchor> {
   private static final Collection<String> EXAMPLES = Arrays.asList("eyes", "feet");
   private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType((p_90357_) -> {
      return new TranslatableComponent("argument.anchor.invalid", p_90357_);
   });

   public static EntityAnchorArgument.Anchor getAnchor(CommandContext<CommandSourceStack> p_90354_, String p_90355_) {
      return p_90354_.getArgument(p_90355_, EntityAnchorArgument.Anchor.class);
   }

   public static EntityAnchorArgument anchor() {
      return new EntityAnchorArgument();
   }

   public EntityAnchorArgument.Anchor parse(StringReader p_90352_) throws CommandSyntaxException {
      int i = p_90352_.getCursor();
      String s = p_90352_.readUnquotedString();
      EntityAnchorArgument.Anchor entityanchorargument$anchor = EntityAnchorArgument.Anchor.getByName(s);
      if (entityanchorargument$anchor == null) {
         p_90352_.setCursor(i);
         throw ERROR_INVALID.createWithContext(p_90352_, s);
      } else {
         return entityanchorargument$anchor;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_90360_, SuggestionsBuilder p_90361_) {
      return SharedSuggestionProvider.suggest(EntityAnchorArgument.Anchor.BY_NAME.keySet(), p_90361_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static enum Anchor {
      FEET("feet", (p_90389_, p_90390_) -> {
         return p_90389_;
      }),
      EYES("eyes", (p_90382_, p_90383_) -> {
         return new Vec3(p_90382_.x, p_90382_.y + (double)p_90383_.getEyeHeight(), p_90382_.z);
      });

      static final Map<String, EntityAnchorArgument.Anchor> BY_NAME = Util.make(Maps.newHashMap(), (p_90387_) -> {
         for(EntityAnchorArgument.Anchor entityanchorargument$anchor : values()) {
            p_90387_.put(entityanchorargument$anchor.name, entityanchorargument$anchor);
         }

      });
      private final String name;
      private final BiFunction<Vec3, Entity, Vec3> transform;

      private Anchor(String p_90374_, BiFunction<Vec3, Entity, Vec3> p_90375_) {
         this.name = p_90374_;
         this.transform = p_90375_;
      }

      @Nullable
      public static EntityAnchorArgument.Anchor getByName(String p_90385_) {
         return BY_NAME.get(p_90385_);
      }

      public Vec3 apply(Entity p_90378_) {
         return this.transform.apply(p_90378_.position(), p_90378_);
      }

      public Vec3 apply(CommandSourceStack p_90380_) {
         Entity entity = p_90380_.getEntity();
         return entity == null ? p_90380_.getPosition() : this.transform.apply(p_90380_.getPosition(), entity);
      }
   }
}