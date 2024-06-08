package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class EntitySummonArgument implements ArgumentType<ResourceLocation> {
   private static final Collection<String> EXAMPLES = Arrays.asList("minecraft:pig", "cow");
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_ENTITY = new DynamicCommandExceptionType((p_93342_) -> {
      return new TranslatableComponent("entity.notFound", p_93342_);
   });

   public static EntitySummonArgument id() {
      return new EntitySummonArgument();
   }

   public static ResourceLocation getSummonableEntity(CommandContext<CommandSourceStack> p_93339_, String p_93340_) throws CommandSyntaxException {
      return verifyCanSummon(p_93339_.getArgument(p_93340_, ResourceLocation.class));
   }

   private static ResourceLocation verifyCanSummon(ResourceLocation p_93344_) throws CommandSyntaxException {
      Registry.ENTITY_TYPE.getOptional(p_93344_).filter(EntityType::canSummon).orElseThrow(() -> {
         return ERROR_UNKNOWN_ENTITY.create(p_93344_);
      });
      return p_93344_;
   }

   public ResourceLocation parse(StringReader p_93337_) throws CommandSyntaxException {
      return verifyCanSummon(ResourceLocation.read(p_93337_));
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}