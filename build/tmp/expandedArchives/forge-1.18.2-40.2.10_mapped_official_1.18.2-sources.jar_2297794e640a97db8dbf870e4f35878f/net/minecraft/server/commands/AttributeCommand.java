package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttributeCommand {
   private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType((p_212443_) -> {
      return new TranslatableComponent("commands.attribute.failed.entity", p_212443_);
   });
   private static final Dynamic2CommandExceptionType ERROR_NO_SUCH_ATTRIBUTE = new Dynamic2CommandExceptionType((p_212445_, p_212446_) -> {
      return new TranslatableComponent("commands.attribute.failed.no_attribute", p_212445_, p_212446_);
   });
   private static final Dynamic3CommandExceptionType ERROR_NO_SUCH_MODIFIER = new Dynamic3CommandExceptionType((p_212448_, p_212449_, p_212450_) -> {
      return new TranslatableComponent("commands.attribute.failed.no_modifier", p_212449_, p_212448_, p_212450_);
   });
   private static final Dynamic3CommandExceptionType ERROR_MODIFIER_ALREADY_PRESENT = new Dynamic3CommandExceptionType((p_136497_, p_136498_, p_136499_) -> {
      return new TranslatableComponent("commands.attribute.failed.modifier_already_present", p_136499_, p_136498_, p_136497_);
   });

   public static void register(CommandDispatcher<CommandSourceStack> p_136445_) {
      p_136445_.register(Commands.literal("attribute").requires((p_212441_) -> {
         return p_212441_.hasPermission(2);
      }).then(Commands.argument("target", EntityArgument.entity()).then(Commands.argument("attribute", ResourceKeyArgument.key(Registry.ATTRIBUTE_REGISTRY)).then(Commands.literal("get").executes((p_212452_) -> {
         return getAttributeValue(p_212452_.getSource(), EntityArgument.getEntity(p_212452_, "target"), ResourceKeyArgument.getAttribute(p_212452_, "attribute"), 1.0D);
      }).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((p_136522_) -> {
         return getAttributeValue(p_136522_.getSource(), EntityArgument.getEntity(p_136522_, "target"), ResourceKeyArgument.getAttribute(p_136522_, "attribute"), DoubleArgumentType.getDouble(p_136522_, "scale"));
      }))).then(Commands.literal("base").then(Commands.literal("set").then(Commands.argument("value", DoubleArgumentType.doubleArg()).executes((p_136520_) -> {
         return setAttributeBase(p_136520_.getSource(), EntityArgument.getEntity(p_136520_, "target"), ResourceKeyArgument.getAttribute(p_136520_, "attribute"), DoubleArgumentType.getDouble(p_136520_, "value"));
      }))).then(Commands.literal("get").executes((p_136518_) -> {
         return getAttributeBase(p_136518_.getSource(), EntityArgument.getEntity(p_136518_, "target"), ResourceKeyArgument.getAttribute(p_136518_, "attribute"), 1.0D);
      }).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((p_136516_) -> {
         return getAttributeBase(p_136516_.getSource(), EntityArgument.getEntity(p_136516_, "target"), ResourceKeyArgument.getAttribute(p_136516_, "attribute"), DoubleArgumentType.getDouble(p_136516_, "scale"));
      })))).then(Commands.literal("modifier").then(Commands.literal("add").then(Commands.argument("uuid", UuidArgument.uuid()).then(Commands.argument("name", StringArgumentType.string()).then(Commands.argument("value", DoubleArgumentType.doubleArg()).then(Commands.literal("add").executes((p_136514_) -> {
         return addModifier(p_136514_.getSource(), EntityArgument.getEntity(p_136514_, "target"), ResourceKeyArgument.getAttribute(p_136514_, "attribute"), UuidArgument.getUuid(p_136514_, "uuid"), StringArgumentType.getString(p_136514_, "name"), DoubleArgumentType.getDouble(p_136514_, "value"), AttributeModifier.Operation.ADDITION);
      })).then(Commands.literal("multiply").executes((p_136512_) -> {
         return addModifier(p_136512_.getSource(), EntityArgument.getEntity(p_136512_, "target"), ResourceKeyArgument.getAttribute(p_136512_, "attribute"), UuidArgument.getUuid(p_136512_, "uuid"), StringArgumentType.getString(p_136512_, "name"), DoubleArgumentType.getDouble(p_136512_, "value"), AttributeModifier.Operation.MULTIPLY_TOTAL);
      })).then(Commands.literal("multiply_base").executes((p_136510_) -> {
         return addModifier(p_136510_.getSource(), EntityArgument.getEntity(p_136510_, "target"), ResourceKeyArgument.getAttribute(p_136510_, "attribute"), UuidArgument.getUuid(p_136510_, "uuid"), StringArgumentType.getString(p_136510_, "name"), DoubleArgumentType.getDouble(p_136510_, "value"), AttributeModifier.Operation.MULTIPLY_BASE);
      })))))).then(Commands.literal("remove").then(Commands.argument("uuid", UuidArgument.uuid()).executes((p_136508_) -> {
         return removeModifier(p_136508_.getSource(), EntityArgument.getEntity(p_136508_, "target"), ResourceKeyArgument.getAttribute(p_136508_, "attribute"), UuidArgument.getUuid(p_136508_, "uuid"));
      }))).then(Commands.literal("value").then(Commands.literal("get").then(Commands.argument("uuid", UuidArgument.uuid()).executes((p_136501_) -> {
         return getAttributeModifier(p_136501_.getSource(), EntityArgument.getEntity(p_136501_, "target"), ResourceKeyArgument.getAttribute(p_136501_, "attribute"), UuidArgument.getUuid(p_136501_, "uuid"), 1.0D);
      }).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((p_136490_) -> {
         return getAttributeModifier(p_136490_.getSource(), EntityArgument.getEntity(p_136490_, "target"), ResourceKeyArgument.getAttribute(p_136490_, "attribute"), UuidArgument.getUuid(p_136490_, "uuid"), DoubleArgumentType.getDouble(p_136490_, "scale"));
      })))))))));
   }

   private static AttributeInstance getAttributeInstance(Entity p_136442_, Attribute p_136443_) throws CommandSyntaxException {
      AttributeInstance attributeinstance = getLivingEntity(p_136442_).getAttributes().getInstance(p_136443_);
      if (attributeinstance == null) {
         throw ERROR_NO_SUCH_ATTRIBUTE.create(p_136442_.getName(), new TranslatableComponent(p_136443_.getDescriptionId()));
      } else {
         return attributeinstance;
      }
   }

   private static LivingEntity getLivingEntity(Entity p_136440_) throws CommandSyntaxException {
      if (!(p_136440_ instanceof LivingEntity)) {
         throw ERROR_NOT_LIVING_ENTITY.create(p_136440_.getName());
      } else {
         return (LivingEntity)p_136440_;
      }
   }

   private static LivingEntity getEntityWithAttribute(Entity p_136487_, Attribute p_136488_) throws CommandSyntaxException {
      LivingEntity livingentity = getLivingEntity(p_136487_);
      if (!livingentity.getAttributes().hasAttribute(p_136488_)) {
         throw ERROR_NO_SUCH_ATTRIBUTE.create(p_136487_.getName(), new TranslatableComponent(p_136488_.getDescriptionId()));
      } else {
         return livingentity;
      }
   }

   private static int getAttributeValue(CommandSourceStack p_136454_, Entity p_136455_, Attribute p_136456_, double p_136457_) throws CommandSyntaxException {
      LivingEntity livingentity = getEntityWithAttribute(p_136455_, p_136456_);
      double d0 = livingentity.getAttributeValue(p_136456_);
      p_136454_.sendSuccess(new TranslatableComponent("commands.attribute.value.get.success", new TranslatableComponent(p_136456_.getDescriptionId()), p_136455_.getName(), d0), false);
      return (int)(d0 * p_136457_);
   }

   private static int getAttributeBase(CommandSourceStack p_136492_, Entity p_136493_, Attribute p_136494_, double p_136495_) throws CommandSyntaxException {
      LivingEntity livingentity = getEntityWithAttribute(p_136493_, p_136494_);
      double d0 = livingentity.getAttributeBaseValue(p_136494_);
      p_136492_.sendSuccess(new TranslatableComponent("commands.attribute.base_value.get.success", new TranslatableComponent(p_136494_.getDescriptionId()), p_136493_.getName(), d0), false);
      return (int)(d0 * p_136495_);
   }

   private static int getAttributeModifier(CommandSourceStack p_136464_, Entity p_136465_, Attribute p_136466_, UUID p_136467_, double p_136468_) throws CommandSyntaxException {
      LivingEntity livingentity = getEntityWithAttribute(p_136465_, p_136466_);
      AttributeMap attributemap = livingentity.getAttributes();
      if (!attributemap.hasModifier(p_136466_, p_136467_)) {
         throw ERROR_NO_SUCH_MODIFIER.create(p_136465_.getName(), new TranslatableComponent(p_136466_.getDescriptionId()), p_136467_);
      } else {
         double d0 = attributemap.getModifierValue(p_136466_, p_136467_);
         p_136464_.sendSuccess(new TranslatableComponent("commands.attribute.modifier.value.get.success", p_136467_, new TranslatableComponent(p_136466_.getDescriptionId()), p_136465_.getName(), d0), false);
         return (int)(d0 * p_136468_);
      }
   }

   private static int setAttributeBase(CommandSourceStack p_136503_, Entity p_136504_, Attribute p_136505_, double p_136506_) throws CommandSyntaxException {
      getAttributeInstance(p_136504_, p_136505_).setBaseValue(p_136506_);
      p_136503_.sendSuccess(new TranslatableComponent("commands.attribute.base_value.set.success", new TranslatableComponent(p_136505_.getDescriptionId()), p_136504_.getName(), p_136506_), false);
      return 1;
   }

   private static int addModifier(CommandSourceStack p_136470_, Entity p_136471_, Attribute p_136472_, UUID p_136473_, String p_136474_, double p_136475_, AttributeModifier.Operation p_136476_) throws CommandSyntaxException {
      AttributeInstance attributeinstance = getAttributeInstance(p_136471_, p_136472_);
      AttributeModifier attributemodifier = new AttributeModifier(p_136473_, p_136474_, p_136475_, p_136476_);
      if (attributeinstance.hasModifier(attributemodifier)) {
         throw ERROR_MODIFIER_ALREADY_PRESENT.create(p_136471_.getName(), new TranslatableComponent(p_136472_.getDescriptionId()), p_136473_);
      } else {
         attributeinstance.addPermanentModifier(attributemodifier);
         p_136470_.sendSuccess(new TranslatableComponent("commands.attribute.modifier.add.success", p_136473_, new TranslatableComponent(p_136472_.getDescriptionId()), p_136471_.getName()), false);
         return 1;
      }
   }

   private static int removeModifier(CommandSourceStack p_136459_, Entity p_136460_, Attribute p_136461_, UUID p_136462_) throws CommandSyntaxException {
      AttributeInstance attributeinstance = getAttributeInstance(p_136460_, p_136461_);
      if (attributeinstance.removePermanentModifier(p_136462_)) {
         p_136459_.sendSuccess(new TranslatableComponent("commands.attribute.modifier.remove.success", p_136462_, new TranslatableComponent(p_136461_.getDescriptionId()), p_136460_.getName()), false);
         return 1;
      } else {
         throw ERROR_NO_SUCH_MODIFIER.create(p_136460_.getName(), new TranslatableComponent(p_136461_.getDescriptionId()), p_136462_);
      }
   }
}