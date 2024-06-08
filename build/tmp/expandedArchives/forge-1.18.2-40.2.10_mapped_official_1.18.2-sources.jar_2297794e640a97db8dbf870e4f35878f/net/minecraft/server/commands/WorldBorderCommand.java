package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec2;

public class WorldBorderCommand {
   private static final SimpleCommandExceptionType ERROR_SAME_CENTER = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.center.failed"));
   private static final SimpleCommandExceptionType ERROR_SAME_SIZE = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.set.failed.nochange"));
   private static final SimpleCommandExceptionType ERROR_TOO_SMALL = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.set.failed.small"));
   private static final SimpleCommandExceptionType ERROR_TOO_BIG = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.set.failed.big", 5.9999968E7D));
   private static final SimpleCommandExceptionType ERROR_TOO_FAR_OUT = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.set.failed.far", 2.9999984E7D));
   private static final SimpleCommandExceptionType ERROR_SAME_WARNING_TIME = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.warning.time.failed"));
   private static final SimpleCommandExceptionType ERROR_SAME_WARNING_DISTANCE = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.warning.distance.failed"));
   private static final SimpleCommandExceptionType ERROR_SAME_DAMAGE_BUFFER = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.damage.buffer.failed"));
   private static final SimpleCommandExceptionType ERROR_SAME_DAMAGE_AMOUNT = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.damage.amount.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> p_139247_) {
      p_139247_.register(Commands.literal("worldborder").requires((p_139268_) -> {
         return p_139268_.hasPermission(2);
      }).then(Commands.literal("add").then(Commands.argument("distance", DoubleArgumentType.doubleArg(-5.9999968E7D, 5.9999968E7D)).executes((p_139290_) -> {
         return setSize(p_139290_.getSource(), p_139290_.getSource().getLevel().getWorldBorder().getSize() + DoubleArgumentType.getDouble(p_139290_, "distance"), 0L);
      }).then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((p_139288_) -> {
         return setSize(p_139288_.getSource(), p_139288_.getSource().getLevel().getWorldBorder().getSize() + DoubleArgumentType.getDouble(p_139288_, "distance"), p_139288_.getSource().getLevel().getWorldBorder().getLerpRemainingTime() + (long)IntegerArgumentType.getInteger(p_139288_, "time") * 1000L);
      })))).then(Commands.literal("set").then(Commands.argument("distance", DoubleArgumentType.doubleArg(-5.9999968E7D, 5.9999968E7D)).executes((p_139286_) -> {
         return setSize(p_139286_.getSource(), DoubleArgumentType.getDouble(p_139286_, "distance"), 0L);
      }).then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((p_139284_) -> {
         return setSize(p_139284_.getSource(), DoubleArgumentType.getDouble(p_139284_, "distance"), (long)IntegerArgumentType.getInteger(p_139284_, "time") * 1000L);
      })))).then(Commands.literal("center").then(Commands.argument("pos", Vec2Argument.vec2()).executes((p_139282_) -> {
         return setCenter(p_139282_.getSource(), Vec2Argument.getVec2(p_139282_, "pos"));
      }))).then(Commands.literal("damage").then(Commands.literal("amount").then(Commands.argument("damagePerBlock", FloatArgumentType.floatArg(0.0F)).executes((p_139280_) -> {
         return setDamageAmount(p_139280_.getSource(), FloatArgumentType.getFloat(p_139280_, "damagePerBlock"));
      }))).then(Commands.literal("buffer").then(Commands.argument("distance", FloatArgumentType.floatArg(0.0F)).executes((p_139278_) -> {
         return setDamageBuffer(p_139278_.getSource(), FloatArgumentType.getFloat(p_139278_, "distance"));
      })))).then(Commands.literal("get").executes((p_139276_) -> {
         return getSize(p_139276_.getSource());
      })).then(Commands.literal("warning").then(Commands.literal("distance").then(Commands.argument("distance", IntegerArgumentType.integer(0)).executes((p_139266_) -> {
         return setWarningDistance(p_139266_.getSource(), IntegerArgumentType.getInteger(p_139266_, "distance"));
      }))).then(Commands.literal("time").then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((p_139249_) -> {
         return setWarningTime(p_139249_.getSource(), IntegerArgumentType.getInteger(p_139249_, "time"));
      })))));
   }

   private static int setDamageBuffer(CommandSourceStack p_139257_, float p_139258_) throws CommandSyntaxException {
      WorldBorder worldborder = p_139257_.getServer().overworld().getWorldBorder();
      if (worldborder.getDamageSafeZone() == (double)p_139258_) {
         throw ERROR_SAME_DAMAGE_BUFFER.create();
      } else {
         worldborder.setDamageSafeZone((double)p_139258_);
         p_139257_.sendSuccess(new TranslatableComponent("commands.worldborder.damage.buffer.success", String.format(Locale.ROOT, "%.2f", p_139258_)), true);
         return (int)p_139258_;
      }
   }

   private static int setDamageAmount(CommandSourceStack p_139270_, float p_139271_) throws CommandSyntaxException {
      WorldBorder worldborder = p_139270_.getServer().overworld().getWorldBorder();
      if (worldborder.getDamagePerBlock() == (double)p_139271_) {
         throw ERROR_SAME_DAMAGE_AMOUNT.create();
      } else {
         worldborder.setDamagePerBlock((double)p_139271_);
         p_139270_.sendSuccess(new TranslatableComponent("commands.worldborder.damage.amount.success", String.format(Locale.ROOT, "%.2f", p_139271_)), true);
         return (int)p_139271_;
      }
   }

   private static int setWarningTime(CommandSourceStack p_139260_, int p_139261_) throws CommandSyntaxException {
      WorldBorder worldborder = p_139260_.getServer().overworld().getWorldBorder();
      if (worldborder.getWarningTime() == p_139261_) {
         throw ERROR_SAME_WARNING_TIME.create();
      } else {
         worldborder.setWarningTime(p_139261_);
         p_139260_.sendSuccess(new TranslatableComponent("commands.worldborder.warning.time.success", p_139261_), true);
         return p_139261_;
      }
   }

   private static int setWarningDistance(CommandSourceStack p_139273_, int p_139274_) throws CommandSyntaxException {
      WorldBorder worldborder = p_139273_.getServer().overworld().getWorldBorder();
      if (worldborder.getWarningBlocks() == p_139274_) {
         throw ERROR_SAME_WARNING_DISTANCE.create();
      } else {
         worldborder.setWarningBlocks(p_139274_);
         p_139273_.sendSuccess(new TranslatableComponent("commands.worldborder.warning.distance.success", p_139274_), true);
         return p_139274_;
      }
   }

   private static int getSize(CommandSourceStack p_139251_) {
      double d0 = p_139251_.getServer().overworld().getWorldBorder().getSize();
      p_139251_.sendSuccess(new TranslatableComponent("commands.worldborder.get", String.format(Locale.ROOT, "%.0f", d0)), false);
      return Mth.floor(d0 + 0.5D);
   }

   private static int setCenter(CommandSourceStack p_139263_, Vec2 p_139264_) throws CommandSyntaxException {
      WorldBorder worldborder = p_139263_.getServer().overworld().getWorldBorder();
      if (worldborder.getCenterX() == (double)p_139264_.x && worldborder.getCenterZ() == (double)p_139264_.y) {
         throw ERROR_SAME_CENTER.create();
      } else if (!((double)Math.abs(p_139264_.x) > 2.9999984E7D) && !((double)Math.abs(p_139264_.y) > 2.9999984E7D)) {
         worldborder.setCenter((double)p_139264_.x, (double)p_139264_.y);
         p_139263_.sendSuccess(new TranslatableComponent("commands.worldborder.center.success", String.format(Locale.ROOT, "%.2f", p_139264_.x), String.format("%.2f", p_139264_.y)), true);
         return 0;
      } else {
         throw ERROR_TOO_FAR_OUT.create();
      }
   }

   private static int setSize(CommandSourceStack p_139253_, double p_139254_, long p_139255_) throws CommandSyntaxException {
      WorldBorder worldborder = p_139253_.getServer().overworld().getWorldBorder();
      double d0 = worldborder.getSize();
      if (d0 == p_139254_) {
         throw ERROR_SAME_SIZE.create();
      } else if (p_139254_ < 1.0D) {
         throw ERROR_TOO_SMALL.create();
      } else if (p_139254_ > 5.9999968E7D) {
         throw ERROR_TOO_BIG.create();
      } else {
         if (p_139255_ > 0L) {
            worldborder.lerpSizeBetween(d0, p_139254_, p_139255_);
            if (p_139254_ > d0) {
               p_139253_.sendSuccess(new TranslatableComponent("commands.worldborder.set.grow", String.format(Locale.ROOT, "%.1f", p_139254_), Long.toString(p_139255_ / 1000L)), true);
            } else {
               p_139253_.sendSuccess(new TranslatableComponent("commands.worldborder.set.shrink", String.format(Locale.ROOT, "%.1f", p_139254_), Long.toString(p_139255_ / 1000L)), true);
            }
         } else {
            worldborder.setSize(p_139254_);
            p_139253_.sendSuccess(new TranslatableComponent("commands.worldborder.set.immediate", String.format(Locale.ROOT, "%.1f", p_139254_)), true);
         }

         return (int)(p_139254_ - d0);
      }
   }
}