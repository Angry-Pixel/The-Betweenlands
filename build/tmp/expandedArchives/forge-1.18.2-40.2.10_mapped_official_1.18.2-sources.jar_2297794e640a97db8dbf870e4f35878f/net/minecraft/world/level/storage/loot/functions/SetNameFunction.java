package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class SetNameFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogUtils.getLogger();
   final Component name;
   @Nullable
   final LootContext.EntityTarget resolutionContext;

   SetNameFunction(LootItemCondition[] p_81127_, @Nullable Component p_81128_, @Nullable LootContext.EntityTarget p_81129_) {
      super(p_81127_);
      this.name = p_81128_;
      this.resolutionContext = p_81129_;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_NAME;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.resolutionContext != null ? ImmutableSet.of(this.resolutionContext.getParam()) : ImmutableSet.of();
   }

   public static UnaryOperator<Component> createResolver(LootContext p_81140_, @Nullable LootContext.EntityTarget p_81141_) {
      if (p_81141_ != null) {
         Entity entity = p_81140_.getParamOrNull(p_81141_.getParam());
         if (entity != null) {
            CommandSourceStack commandsourcestack = entity.createCommandSourceStack().withPermission(2);
            return (p_81147_) -> {
               try {
                  return ComponentUtils.updateForEntity(commandsourcestack, p_81147_, entity, 0);
               } catch (CommandSyntaxException commandsyntaxexception) {
                  LOGGER.warn("Failed to resolve text component", (Throwable)commandsyntaxexception);
                  return p_81147_;
               }
            };
         }
      }

      return (p_81152_) -> {
         return p_81152_;
      };
   }

   public ItemStack run(ItemStack p_81137_, LootContext p_81138_) {
      if (this.name != null) {
         p_81137_.setHoverName(createResolver(p_81138_, this.resolutionContext).apply(this.name));
      }

      return p_81137_;
   }

   public static LootItemConditionalFunction.Builder<?> setName(Component p_165458_) {
      return simpleBuilder((p_165468_) -> {
         return new SetNameFunction(p_165468_, p_165458_, (LootContext.EntityTarget)null);
      });
   }

   public static LootItemConditionalFunction.Builder<?> setName(Component p_165460_, LootContext.EntityTarget p_165461_) {
      return simpleBuilder((p_165465_) -> {
         return new SetNameFunction(p_165465_, p_165460_, p_165461_);
      });
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetNameFunction> {
      public void serialize(JsonObject p_81163_, SetNameFunction p_81164_, JsonSerializationContext p_81165_) {
         super.serialize(p_81163_, p_81164_, p_81165_);
         if (p_81164_.name != null) {
            p_81163_.add("name", Component.Serializer.toJsonTree(p_81164_.name));
         }

         if (p_81164_.resolutionContext != null) {
            p_81163_.add("entity", p_81165_.serialize(p_81164_.resolutionContext));
         }

      }

      public SetNameFunction deserialize(JsonObject p_81155_, JsonDeserializationContext p_81156_, LootItemCondition[] p_81157_) {
         Component component = Component.Serializer.fromJson(p_81155_.get("name"));
         LootContext.EntityTarget lootcontext$entitytarget = GsonHelper.getAsObject(p_81155_, "entity", (LootContext.EntityTarget)null, p_81156_, LootContext.EntityTarget.class);
         return new SetNameFunction(p_81157_, component, lootcontext$entitytarget);
      }
   }
}