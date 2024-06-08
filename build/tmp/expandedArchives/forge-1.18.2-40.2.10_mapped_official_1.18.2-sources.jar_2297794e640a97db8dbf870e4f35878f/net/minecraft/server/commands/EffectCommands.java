package net.minecraft.server.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MobEffectArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EffectCommands {
   private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.effect.give.failed"));
   private static final SimpleCommandExceptionType ERROR_CLEAR_EVERYTHING_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.effect.clear.everything.failed"));
   private static final SimpleCommandExceptionType ERROR_CLEAR_SPECIFIC_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.effect.clear.specific.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> p_136954_) {
      p_136954_.register(Commands.literal("effect").requires((p_136958_) -> {
         return p_136958_.hasPermission(2);
      }).then(Commands.literal("clear").executes((p_136984_) -> {
         return clearEffects(p_136984_.getSource(), ImmutableList.of(p_136984_.getSource().getEntityOrException()));
      }).then(Commands.argument("targets", EntityArgument.entities()).executes((p_136982_) -> {
         return clearEffects(p_136982_.getSource(), EntityArgument.getEntities(p_136982_, "targets"));
      }).then(Commands.argument("effect", MobEffectArgument.effect()).executes((p_136980_) -> {
         return clearEffect(p_136980_.getSource(), EntityArgument.getEntities(p_136980_, "targets"), MobEffectArgument.getEffect(p_136980_, "effect"));
      })))).then(Commands.literal("give").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("effect", MobEffectArgument.effect()).executes((p_136978_) -> {
         return giveEffect(p_136978_.getSource(), EntityArgument.getEntities(p_136978_, "targets"), MobEffectArgument.getEffect(p_136978_, "effect"), (Integer)null, 0, true);
      }).then(Commands.argument("seconds", IntegerArgumentType.integer(1, 1000000)).executes((p_136976_) -> {
         return giveEffect(p_136976_.getSource(), EntityArgument.getEntities(p_136976_, "targets"), MobEffectArgument.getEffect(p_136976_, "effect"), IntegerArgumentType.getInteger(p_136976_, "seconds"), 0, true);
      }).then(Commands.argument("amplifier", IntegerArgumentType.integer(0, 255)).executes((p_136974_) -> {
         return giveEffect(p_136974_.getSource(), EntityArgument.getEntities(p_136974_, "targets"), MobEffectArgument.getEffect(p_136974_, "effect"), IntegerArgumentType.getInteger(p_136974_, "seconds"), IntegerArgumentType.getInteger(p_136974_, "amplifier"), true);
      }).then(Commands.argument("hideParticles", BoolArgumentType.bool()).executes((p_136956_) -> {
         return giveEffect(p_136956_.getSource(), EntityArgument.getEntities(p_136956_, "targets"), MobEffectArgument.getEffect(p_136956_, "effect"), IntegerArgumentType.getInteger(p_136956_, "seconds"), IntegerArgumentType.getInteger(p_136956_, "amplifier"), !BoolArgumentType.getBool(p_136956_, "hideParticles"));
      }))))))));
   }

   private static int giveEffect(CommandSourceStack p_136967_, Collection<? extends Entity> p_136968_, MobEffect p_136969_, @Nullable Integer p_136970_, int p_136971_, boolean p_136972_) throws CommandSyntaxException {
      int i = 0;
      int j;
      if (p_136970_ != null) {
         if (p_136969_.isInstantenous()) {
            j = p_136970_;
         } else {
            j = p_136970_ * 20;
         }
      } else if (p_136969_.isInstantenous()) {
         j = 1;
      } else {
         j = 600;
      }

      for(Entity entity : p_136968_) {
         if (entity instanceof LivingEntity) {
            MobEffectInstance mobeffectinstance = new MobEffectInstance(p_136969_, j, p_136971_, false, p_136972_);
            if (((LivingEntity)entity).addEffect(mobeffectinstance, p_136967_.getEntity())) {
               ++i;
            }
         }
      }

      if (i == 0) {
         throw ERROR_GIVE_FAILED.create();
      } else {
         if (p_136968_.size() == 1) {
            p_136967_.sendSuccess(new TranslatableComponent("commands.effect.give.success.single", p_136969_.getDisplayName(), p_136968_.iterator().next().getDisplayName(), j / 20), true);
         } else {
            p_136967_.sendSuccess(new TranslatableComponent("commands.effect.give.success.multiple", p_136969_.getDisplayName(), p_136968_.size(), j / 20), true);
         }

         return i;
      }
   }

   private static int clearEffects(CommandSourceStack p_136960_, Collection<? extends Entity> p_136961_) throws CommandSyntaxException {
      int i = 0;

      for(Entity entity : p_136961_) {
         if (entity instanceof LivingEntity && ((LivingEntity)entity).removeAllEffects()) {
            ++i;
         }
      }

      if (i == 0) {
         throw ERROR_CLEAR_EVERYTHING_FAILED.create();
      } else {
         if (p_136961_.size() == 1) {
            p_136960_.sendSuccess(new TranslatableComponent("commands.effect.clear.everything.success.single", p_136961_.iterator().next().getDisplayName()), true);
         } else {
            p_136960_.sendSuccess(new TranslatableComponent("commands.effect.clear.everything.success.multiple", p_136961_.size()), true);
         }

         return i;
      }
   }

   private static int clearEffect(CommandSourceStack p_136963_, Collection<? extends Entity> p_136964_, MobEffect p_136965_) throws CommandSyntaxException {
      int i = 0;

      for(Entity entity : p_136964_) {
         if (entity instanceof LivingEntity && ((LivingEntity)entity).removeEffect(p_136965_)) {
            ++i;
         }
      }

      if (i == 0) {
         throw ERROR_CLEAR_SPECIFIC_FAILED.create();
      } else {
         if (p_136964_.size() == 1) {
            p_136963_.sendSuccess(new TranslatableComponent("commands.effect.clear.specific.success.single", p_136965_.getDisplayName(), p_136964_.iterator().next().getDisplayName()), true);
         } else {
            p_136963_.sendSuccess(new TranslatableComponent("commands.effect.clear.specific.success.multiple", p_136965_.getDisplayName(), p_136964_.size()), true);
         }

         return i;
      }
   }
}