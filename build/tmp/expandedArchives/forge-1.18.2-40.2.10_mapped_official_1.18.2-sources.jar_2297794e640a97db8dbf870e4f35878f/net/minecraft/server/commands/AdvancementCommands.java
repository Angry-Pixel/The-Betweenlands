package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementCommands {
   private static final SuggestionProvider<CommandSourceStack> SUGGEST_ADVANCEMENTS = (p_136344_, p_136345_) -> {
      Collection<Advancement> collection = p_136344_.getSource().getServer().getAdvancements().getAllAdvancements();
      return SharedSuggestionProvider.suggestResource(collection.stream().map(Advancement::getId), p_136345_);
   };

   public static void register(CommandDispatcher<CommandSourceStack> p_136311_) {
      p_136311_.register(Commands.literal("advancement").requires((p_136318_) -> {
         return p_136318_.hasPermission(2);
      }).then(Commands.literal("grant").then(Commands.argument("targets", EntityArgument.players()).then(Commands.literal("only").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_136363_) -> {
         return perform(p_136363_.getSource(), EntityArgument.getPlayers(p_136363_, "targets"), AdvancementCommands.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(p_136363_, "advancement"), AdvancementCommands.Mode.ONLY));
      }).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((p_136339_, p_136340_) -> {
         return SharedSuggestionProvider.suggest(ResourceLocationArgument.getAdvancement(p_136339_, "advancement").getCriteria().keySet(), p_136340_);
      }).executes((p_136361_) -> {
         return performCriterion(p_136361_.getSource(), EntityArgument.getPlayers(p_136361_, "targets"), AdvancementCommands.Action.GRANT, ResourceLocationArgument.getAdvancement(p_136361_, "advancement"), StringArgumentType.getString(p_136361_, "criterion"));
      })))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_136359_) -> {
         return perform(p_136359_.getSource(), EntityArgument.getPlayers(p_136359_, "targets"), AdvancementCommands.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(p_136359_, "advancement"), AdvancementCommands.Mode.FROM));
      }))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_136357_) -> {
         return perform(p_136357_.getSource(), EntityArgument.getPlayers(p_136357_, "targets"), AdvancementCommands.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(p_136357_, "advancement"), AdvancementCommands.Mode.UNTIL));
      }))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_136355_) -> {
         return perform(p_136355_.getSource(), EntityArgument.getPlayers(p_136355_, "targets"), AdvancementCommands.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(p_136355_, "advancement"), AdvancementCommands.Mode.THROUGH));
      }))).then(Commands.literal("everything").executes((p_136353_) -> {
         return perform(p_136353_.getSource(), EntityArgument.getPlayers(p_136353_, "targets"), AdvancementCommands.Action.GRANT, p_136353_.getSource().getServer().getAdvancements().getAllAdvancements());
      })))).then(Commands.literal("revoke").then(Commands.argument("targets", EntityArgument.players()).then(Commands.literal("only").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_136351_) -> {
         return perform(p_136351_.getSource(), EntityArgument.getPlayers(p_136351_, "targets"), AdvancementCommands.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(p_136351_, "advancement"), AdvancementCommands.Mode.ONLY));
      }).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((p_136315_, p_136316_) -> {
         return SharedSuggestionProvider.suggest(ResourceLocationArgument.getAdvancement(p_136315_, "advancement").getCriteria().keySet(), p_136316_);
      }).executes((p_136349_) -> {
         return performCriterion(p_136349_.getSource(), EntityArgument.getPlayers(p_136349_, "targets"), AdvancementCommands.Action.REVOKE, ResourceLocationArgument.getAdvancement(p_136349_, "advancement"), StringArgumentType.getString(p_136349_, "criterion"));
      })))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_136347_) -> {
         return perform(p_136347_.getSource(), EntityArgument.getPlayers(p_136347_, "targets"), AdvancementCommands.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(p_136347_, "advancement"), AdvancementCommands.Mode.FROM));
      }))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_136342_) -> {
         return perform(p_136342_.getSource(), EntityArgument.getPlayers(p_136342_, "targets"), AdvancementCommands.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(p_136342_, "advancement"), AdvancementCommands.Mode.UNTIL));
      }))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_136337_) -> {
         return perform(p_136337_.getSource(), EntityArgument.getPlayers(p_136337_, "targets"), AdvancementCommands.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(p_136337_, "advancement"), AdvancementCommands.Mode.THROUGH));
      }))).then(Commands.literal("everything").executes((p_136313_) -> {
         return perform(p_136313_.getSource(), EntityArgument.getPlayers(p_136313_, "targets"), AdvancementCommands.Action.REVOKE, p_136313_.getSource().getServer().getAdvancements().getAllAdvancements());
      })))));
   }

   private static int perform(CommandSourceStack p_136320_, Collection<ServerPlayer> p_136321_, AdvancementCommands.Action p_136322_, Collection<Advancement> p_136323_) {
      int i = 0;

      for(ServerPlayer serverplayer : p_136321_) {
         i += p_136322_.perform(serverplayer, p_136323_);
      }

      if (i == 0) {
         if (p_136323_.size() == 1) {
            if (p_136321_.size() == 1) {
               throw new CommandRuntimeException(new TranslatableComponent(p_136322_.getKey() + ".one.to.one.failure", p_136323_.iterator().next().getChatComponent(), p_136321_.iterator().next().getDisplayName()));
            } else {
               throw new CommandRuntimeException(new TranslatableComponent(p_136322_.getKey() + ".one.to.many.failure", p_136323_.iterator().next().getChatComponent(), p_136321_.size()));
            }
         } else if (p_136321_.size() == 1) {
            throw new CommandRuntimeException(new TranslatableComponent(p_136322_.getKey() + ".many.to.one.failure", p_136323_.size(), p_136321_.iterator().next().getDisplayName()));
         } else {
            throw new CommandRuntimeException(new TranslatableComponent(p_136322_.getKey() + ".many.to.many.failure", p_136323_.size(), p_136321_.size()));
         }
      } else {
         if (p_136323_.size() == 1) {
            if (p_136321_.size() == 1) {
               p_136320_.sendSuccess(new TranslatableComponent(p_136322_.getKey() + ".one.to.one.success", p_136323_.iterator().next().getChatComponent(), p_136321_.iterator().next().getDisplayName()), true);
            } else {
               p_136320_.sendSuccess(new TranslatableComponent(p_136322_.getKey() + ".one.to.many.success", p_136323_.iterator().next().getChatComponent(), p_136321_.size()), true);
            }
         } else if (p_136321_.size() == 1) {
            p_136320_.sendSuccess(new TranslatableComponent(p_136322_.getKey() + ".many.to.one.success", p_136323_.size(), p_136321_.iterator().next().getDisplayName()), true);
         } else {
            p_136320_.sendSuccess(new TranslatableComponent(p_136322_.getKey() + ".many.to.many.success", p_136323_.size(), p_136321_.size()), true);
         }

         return i;
      }
   }

   private static int performCriterion(CommandSourceStack p_136325_, Collection<ServerPlayer> p_136326_, AdvancementCommands.Action p_136327_, Advancement p_136328_, String p_136329_) {
      int i = 0;
      if (!p_136328_.getCriteria().containsKey(p_136329_)) {
         throw new CommandRuntimeException(new TranslatableComponent("commands.advancement.criterionNotFound", p_136328_.getChatComponent(), p_136329_));
      } else {
         for(ServerPlayer serverplayer : p_136326_) {
            if (p_136327_.performCriterion(serverplayer, p_136328_, p_136329_)) {
               ++i;
            }
         }

         if (i == 0) {
            if (p_136326_.size() == 1) {
               throw new CommandRuntimeException(new TranslatableComponent(p_136327_.getKey() + ".criterion.to.one.failure", p_136329_, p_136328_.getChatComponent(), p_136326_.iterator().next().getDisplayName()));
            } else {
               throw new CommandRuntimeException(new TranslatableComponent(p_136327_.getKey() + ".criterion.to.many.failure", p_136329_, p_136328_.getChatComponent(), p_136326_.size()));
            }
         } else {
            if (p_136326_.size() == 1) {
               p_136325_.sendSuccess(new TranslatableComponent(p_136327_.getKey() + ".criterion.to.one.success", p_136329_, p_136328_.getChatComponent(), p_136326_.iterator().next().getDisplayName()), true);
            } else {
               p_136325_.sendSuccess(new TranslatableComponent(p_136327_.getKey() + ".criterion.to.many.success", p_136329_, p_136328_.getChatComponent(), p_136326_.size()), true);
            }

            return i;
         }
      }
   }

   private static List<Advancement> getAdvancements(Advancement p_136334_, AdvancementCommands.Mode p_136335_) {
      List<Advancement> list = Lists.newArrayList();
      if (p_136335_.parents) {
         for(Advancement advancement = p_136334_.getParent(); advancement != null; advancement = advancement.getParent()) {
            list.add(advancement);
         }
      }

      list.add(p_136334_);
      if (p_136335_.children) {
         addChildren(p_136334_, list);
      }

      return list;
   }

   private static void addChildren(Advancement p_136331_, List<Advancement> p_136332_) {
      for(Advancement advancement : p_136331_.getChildren()) {
         p_136332_.add(advancement);
         addChildren(advancement, p_136332_);
      }

   }

   static enum Action {
      GRANT("grant") {
         protected boolean perform(ServerPlayer p_136395_, Advancement p_136396_) {
            AdvancementProgress advancementprogress = p_136395_.getAdvancements().getOrStartProgress(p_136396_);
            if (advancementprogress.isDone()) {
               return false;
            } else {
               for(String s : advancementprogress.getRemainingCriteria()) {
                  p_136395_.getAdvancements().award(p_136396_, s);
               }

               return true;
            }
         }

         protected boolean performCriterion(ServerPlayer p_136398_, Advancement p_136399_, String p_136400_) {
            return p_136398_.getAdvancements().award(p_136399_, p_136400_);
         }
      },
      REVOKE("revoke") {
         protected boolean perform(ServerPlayer p_136406_, Advancement p_136407_) {
            AdvancementProgress advancementprogress = p_136406_.getAdvancements().getOrStartProgress(p_136407_);
            if (!advancementprogress.hasProgress()) {
               return false;
            } else {
               for(String s : advancementprogress.getCompletedCriteria()) {
                  p_136406_.getAdvancements().revoke(p_136407_, s);
               }

               return true;
            }
         }

         protected boolean performCriterion(ServerPlayer p_136409_, Advancement p_136410_, String p_136411_) {
            return p_136409_.getAdvancements().revoke(p_136410_, p_136411_);
         }
      };

      private final String key;

      Action(String p_136372_) {
         this.key = "commands.advancement." + p_136372_;
      }

      public int perform(ServerPlayer p_136380_, Iterable<Advancement> p_136381_) {
         int i = 0;

         for(Advancement advancement : p_136381_) {
            if (this.perform(p_136380_, advancement)) {
               ++i;
            }
         }

         return i;
      }

      protected abstract boolean perform(ServerPlayer p_136382_, Advancement p_136383_);

      protected abstract boolean performCriterion(ServerPlayer p_136384_, Advancement p_136385_, String p_136386_);

      protected String getKey() {
         return this.key;
      }
   }

   static enum Mode {
      ONLY(false, false),
      THROUGH(true, true),
      FROM(false, true),
      UNTIL(true, false),
      EVERYTHING(true, true);

      final boolean parents;
      final boolean children;

      private Mode(boolean p_136424_, boolean p_136425_) {
         this.parents = p_136424_;
         this.children = p_136425_;
      }
   }
}