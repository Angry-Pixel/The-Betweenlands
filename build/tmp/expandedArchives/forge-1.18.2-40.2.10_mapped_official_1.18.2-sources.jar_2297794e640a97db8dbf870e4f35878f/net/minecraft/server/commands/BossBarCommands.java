package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class BossBarCommands {
   private static final DynamicCommandExceptionType ERROR_ALREADY_EXISTS = new DynamicCommandExceptionType((p_136636_) -> {
      return new TranslatableComponent("commands.bossbar.create.failed", p_136636_);
   });
   private static final DynamicCommandExceptionType ERROR_DOESNT_EXIST = new DynamicCommandExceptionType((p_136623_) -> {
      return new TranslatableComponent("commands.bossbar.unknown", p_136623_);
   });
   private static final SimpleCommandExceptionType ERROR_NO_PLAYER_CHANGE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.players.unchanged"));
   private static final SimpleCommandExceptionType ERROR_NO_NAME_CHANGE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.name.unchanged"));
   private static final SimpleCommandExceptionType ERROR_NO_COLOR_CHANGE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.color.unchanged"));
   private static final SimpleCommandExceptionType ERROR_NO_STYLE_CHANGE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.style.unchanged"));
   private static final SimpleCommandExceptionType ERROR_NO_VALUE_CHANGE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.value.unchanged"));
   private static final SimpleCommandExceptionType ERROR_NO_MAX_CHANGE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.max.unchanged"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_HIDDEN = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.visibility.unchanged.hidden"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_VISIBLE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.visibility.unchanged.visible"));
   public static final SuggestionProvider<CommandSourceStack> SUGGEST_BOSS_BAR = (p_136587_, p_136588_) -> {
      return SharedSuggestionProvider.suggestResource(p_136587_.getSource().getServer().getCustomBossEvents().getIds(), p_136588_);
   };

   public static void register(CommandDispatcher<CommandSourceStack> p_136583_) {
      p_136583_.register(Commands.literal("bossbar").requires((p_136627_) -> {
         return p_136627_.hasPermission(2);
      }).then(Commands.literal("add").then(Commands.argument("id", ResourceLocationArgument.id()).then(Commands.argument("name", ComponentArgument.textComponent()).executes((p_136693_) -> {
         return createBar(p_136693_.getSource(), ResourceLocationArgument.getId(p_136693_, "id"), ComponentArgument.getComponent(p_136693_, "name"));
      })))).then(Commands.literal("remove").then(Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR).executes((p_136691_) -> {
         return removeBar(p_136691_.getSource(), getBossBar(p_136691_));
      }))).then(Commands.literal("list").executes((p_136689_) -> {
         return listBars(p_136689_.getSource());
      })).then(Commands.literal("set").then(Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR).then(Commands.literal("name").then(Commands.argument("name", ComponentArgument.textComponent()).executes((p_136687_) -> {
         return setName(p_136687_.getSource(), getBossBar(p_136687_), ComponentArgument.getComponent(p_136687_, "name"));
      }))).then(Commands.literal("color").then(Commands.literal("pink").executes((p_136685_) -> {
         return setColor(p_136685_.getSource(), getBossBar(p_136685_), BossEvent.BossBarColor.PINK);
      })).then(Commands.literal("blue").executes((p_136683_) -> {
         return setColor(p_136683_.getSource(), getBossBar(p_136683_), BossEvent.BossBarColor.BLUE);
      })).then(Commands.literal("red").executes((p_136681_) -> {
         return setColor(p_136681_.getSource(), getBossBar(p_136681_), BossEvent.BossBarColor.RED);
      })).then(Commands.literal("green").executes((p_136679_) -> {
         return setColor(p_136679_.getSource(), getBossBar(p_136679_), BossEvent.BossBarColor.GREEN);
      })).then(Commands.literal("yellow").executes((p_136677_) -> {
         return setColor(p_136677_.getSource(), getBossBar(p_136677_), BossEvent.BossBarColor.YELLOW);
      })).then(Commands.literal("purple").executes((p_136675_) -> {
         return setColor(p_136675_.getSource(), getBossBar(p_136675_), BossEvent.BossBarColor.PURPLE);
      })).then(Commands.literal("white").executes((p_136673_) -> {
         return setColor(p_136673_.getSource(), getBossBar(p_136673_), BossEvent.BossBarColor.WHITE);
      }))).then(Commands.literal("style").then(Commands.literal("progress").executes((p_136671_) -> {
         return setStyle(p_136671_.getSource(), getBossBar(p_136671_), BossEvent.BossBarOverlay.PROGRESS);
      })).then(Commands.literal("notched_6").executes((p_136669_) -> {
         return setStyle(p_136669_.getSource(), getBossBar(p_136669_), BossEvent.BossBarOverlay.NOTCHED_6);
      })).then(Commands.literal("notched_10").executes((p_136667_) -> {
         return setStyle(p_136667_.getSource(), getBossBar(p_136667_), BossEvent.BossBarOverlay.NOTCHED_10);
      })).then(Commands.literal("notched_12").executes((p_136665_) -> {
         return setStyle(p_136665_.getSource(), getBossBar(p_136665_), BossEvent.BossBarOverlay.NOTCHED_12);
      })).then(Commands.literal("notched_20").executes((p_136663_) -> {
         return setStyle(p_136663_.getSource(), getBossBar(p_136663_), BossEvent.BossBarOverlay.NOTCHED_20);
      }))).then(Commands.literal("value").then(Commands.argument("value", IntegerArgumentType.integer(0)).executes((p_136661_) -> {
         return setValue(p_136661_.getSource(), getBossBar(p_136661_), IntegerArgumentType.getInteger(p_136661_, "value"));
      }))).then(Commands.literal("max").then(Commands.argument("max", IntegerArgumentType.integer(1)).executes((p_136659_) -> {
         return setMax(p_136659_.getSource(), getBossBar(p_136659_), IntegerArgumentType.getInteger(p_136659_, "max"));
      }))).then(Commands.literal("visible").then(Commands.argument("visible", BoolArgumentType.bool()).executes((p_136657_) -> {
         return setVisible(p_136657_.getSource(), getBossBar(p_136657_), BoolArgumentType.getBool(p_136657_, "visible"));
      }))).then(Commands.literal("players").executes((p_136655_) -> {
         return setPlayers(p_136655_.getSource(), getBossBar(p_136655_), Collections.emptyList());
      }).then(Commands.argument("targets", EntityArgument.players()).executes((p_136653_) -> {
         return setPlayers(p_136653_.getSource(), getBossBar(p_136653_), EntityArgument.getOptionalPlayers(p_136653_, "targets"));
      }))))).then(Commands.literal("get").then(Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR).then(Commands.literal("value").executes((p_136648_) -> {
         return getValue(p_136648_.getSource(), getBossBar(p_136648_));
      })).then(Commands.literal("max").executes((p_136643_) -> {
         return getMax(p_136643_.getSource(), getBossBar(p_136643_));
      })).then(Commands.literal("visible").executes((p_136638_) -> {
         return getVisible(p_136638_.getSource(), getBossBar(p_136638_));
      })).then(Commands.literal("players").executes((p_136625_) -> {
         return getPlayers(p_136625_.getSource(), getBossBar(p_136625_));
      })))));
   }

   private static int getValue(CommandSourceStack p_136596_, CustomBossEvent p_136597_) {
      p_136596_.sendSuccess(new TranslatableComponent("commands.bossbar.get.value", p_136597_.getDisplayName(), p_136597_.getValue()), true);
      return p_136597_.getValue();
   }

   private static int getMax(CommandSourceStack p_136629_, CustomBossEvent p_136630_) {
      p_136629_.sendSuccess(new TranslatableComponent("commands.bossbar.get.max", p_136630_.getDisplayName(), p_136630_.getMax()), true);
      return p_136630_.getMax();
   }

   private static int getVisible(CommandSourceStack p_136640_, CustomBossEvent p_136641_) {
      if (p_136641_.isVisible()) {
         p_136640_.sendSuccess(new TranslatableComponent("commands.bossbar.get.visible.visible", p_136641_.getDisplayName()), true);
         return 1;
      } else {
         p_136640_.sendSuccess(new TranslatableComponent("commands.bossbar.get.visible.hidden", p_136641_.getDisplayName()), true);
         return 0;
      }
   }

   private static int getPlayers(CommandSourceStack p_136645_, CustomBossEvent p_136646_) {
      if (p_136646_.getPlayers().isEmpty()) {
         p_136645_.sendSuccess(new TranslatableComponent("commands.bossbar.get.players.none", p_136646_.getDisplayName()), true);
      } else {
         p_136645_.sendSuccess(new TranslatableComponent("commands.bossbar.get.players.some", p_136646_.getDisplayName(), p_136646_.getPlayers().size(), ComponentUtils.formatList(p_136646_.getPlayers(), Player::getDisplayName)), true);
      }

      return p_136646_.getPlayers().size();
   }

   private static int setVisible(CommandSourceStack p_136619_, CustomBossEvent p_136620_, boolean p_136621_) throws CommandSyntaxException {
      if (p_136620_.isVisible() == p_136621_) {
         if (p_136621_) {
            throw ERROR_ALREADY_VISIBLE.create();
         } else {
            throw ERROR_ALREADY_HIDDEN.create();
         }
      } else {
         p_136620_.setVisible(p_136621_);
         if (p_136621_) {
            p_136619_.sendSuccess(new TranslatableComponent("commands.bossbar.set.visible.success.visible", p_136620_.getDisplayName()), true);
         } else {
            p_136619_.sendSuccess(new TranslatableComponent("commands.bossbar.set.visible.success.hidden", p_136620_.getDisplayName()), true);
         }

         return 0;
      }
   }

   private static int setValue(CommandSourceStack p_136599_, CustomBossEvent p_136600_, int p_136601_) throws CommandSyntaxException {
      if (p_136600_.getValue() == p_136601_) {
         throw ERROR_NO_VALUE_CHANGE.create();
      } else {
         p_136600_.setValue(p_136601_);
         p_136599_.sendSuccess(new TranslatableComponent("commands.bossbar.set.value.success", p_136600_.getDisplayName(), p_136601_), true);
         return p_136601_;
      }
   }

   private static int setMax(CommandSourceStack p_136632_, CustomBossEvent p_136633_, int p_136634_) throws CommandSyntaxException {
      if (p_136633_.getMax() == p_136634_) {
         throw ERROR_NO_MAX_CHANGE.create();
      } else {
         p_136633_.setMax(p_136634_);
         p_136632_.sendSuccess(new TranslatableComponent("commands.bossbar.set.max.success", p_136633_.getDisplayName(), p_136634_), true);
         return p_136634_;
      }
   }

   private static int setColor(CommandSourceStack p_136603_, CustomBossEvent p_136604_, BossEvent.BossBarColor p_136605_) throws CommandSyntaxException {
      if (p_136604_.getColor().equals(p_136605_)) {
         throw ERROR_NO_COLOR_CHANGE.create();
      } else {
         p_136604_.setColor(p_136605_);
         p_136603_.sendSuccess(new TranslatableComponent("commands.bossbar.set.color.success", p_136604_.getDisplayName()), true);
         return 0;
      }
   }

   private static int setStyle(CommandSourceStack p_136607_, CustomBossEvent p_136608_, BossEvent.BossBarOverlay p_136609_) throws CommandSyntaxException {
      if (p_136608_.getOverlay().equals(p_136609_)) {
         throw ERROR_NO_STYLE_CHANGE.create();
      } else {
         p_136608_.setOverlay(p_136609_);
         p_136607_.sendSuccess(new TranslatableComponent("commands.bossbar.set.style.success", p_136608_.getDisplayName()), true);
         return 0;
      }
   }

   private static int setName(CommandSourceStack p_136615_, CustomBossEvent p_136616_, Component p_136617_) throws CommandSyntaxException {
      Component component = ComponentUtils.updateForEntity(p_136615_, p_136617_, (Entity)null, 0);
      if (p_136616_.getName().equals(component)) {
         throw ERROR_NO_NAME_CHANGE.create();
      } else {
         p_136616_.setName(component);
         p_136615_.sendSuccess(new TranslatableComponent("commands.bossbar.set.name.success", p_136616_.getDisplayName()), true);
         return 0;
      }
   }

   private static int setPlayers(CommandSourceStack p_136611_, CustomBossEvent p_136612_, Collection<ServerPlayer> p_136613_) throws CommandSyntaxException {
      boolean flag = p_136612_.setPlayers(p_136613_);
      if (!flag) {
         throw ERROR_NO_PLAYER_CHANGE.create();
      } else {
         if (p_136612_.getPlayers().isEmpty()) {
            p_136611_.sendSuccess(new TranslatableComponent("commands.bossbar.set.players.success.none", p_136612_.getDisplayName()), true);
         } else {
            p_136611_.sendSuccess(new TranslatableComponent("commands.bossbar.set.players.success.some", p_136612_.getDisplayName(), p_136613_.size(), ComponentUtils.formatList(p_136613_, Player::getDisplayName)), true);
         }

         return p_136612_.getPlayers().size();
      }
   }

   private static int listBars(CommandSourceStack p_136590_) {
      Collection<CustomBossEvent> collection = p_136590_.getServer().getCustomBossEvents().getEvents();
      if (collection.isEmpty()) {
         p_136590_.sendSuccess(new TranslatableComponent("commands.bossbar.list.bars.none"), false);
      } else {
         p_136590_.sendSuccess(new TranslatableComponent("commands.bossbar.list.bars.some", collection.size(), ComponentUtils.formatList(collection, CustomBossEvent::getDisplayName)), false);
      }

      return collection.size();
   }

   private static int createBar(CommandSourceStack p_136592_, ResourceLocation p_136593_, Component p_136594_) throws CommandSyntaxException {
      CustomBossEvents custombossevents = p_136592_.getServer().getCustomBossEvents();
      if (custombossevents.get(p_136593_) != null) {
         throw ERROR_ALREADY_EXISTS.create(p_136593_.toString());
      } else {
         CustomBossEvent custombossevent = custombossevents.create(p_136593_, ComponentUtils.updateForEntity(p_136592_, p_136594_, (Entity)null, 0));
         p_136592_.sendSuccess(new TranslatableComponent("commands.bossbar.create.success", custombossevent.getDisplayName()), true);
         return custombossevents.getEvents().size();
      }
   }

   private static int removeBar(CommandSourceStack p_136650_, CustomBossEvent p_136651_) {
      CustomBossEvents custombossevents = p_136650_.getServer().getCustomBossEvents();
      p_136651_.removeAllPlayers();
      custombossevents.remove(p_136651_);
      p_136650_.sendSuccess(new TranslatableComponent("commands.bossbar.remove.success", p_136651_.getDisplayName()), true);
      return custombossevents.getEvents().size();
   }

   public static CustomBossEvent getBossBar(CommandContext<CommandSourceStack> p_136585_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = ResourceLocationArgument.getId(p_136585_, "id");
      CustomBossEvent custombossevent = p_136585_.getSource().getServer().getCustomBossEvents().get(resourcelocation);
      if (custombossevent == null) {
         throw ERROR_DOESNT_EXIST.create(resourcelocation.toString());
      } else {
         return custombossevent;
      }
   }
}