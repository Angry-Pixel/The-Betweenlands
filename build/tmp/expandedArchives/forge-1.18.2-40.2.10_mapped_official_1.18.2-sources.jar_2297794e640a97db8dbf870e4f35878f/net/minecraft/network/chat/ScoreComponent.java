package net.minecraft.network.chat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class ScoreComponent extends BaseComponent implements ContextAwareComponent {
   private static final String SCORER_PLACEHOLDER = "*";
   private final String name;
   @Nullable
   private final EntitySelector selector;
   private final String objective;

   @Nullable
   private static EntitySelector parseSelector(String p_131067_) {
      try {
         return (new EntitySelectorParser(new StringReader(p_131067_))).parse();
      } catch (CommandSyntaxException commandsyntaxexception) {
         return null;
      }
   }

   public ScoreComponent(String p_131054_, String p_131055_) {
      this(p_131054_, parseSelector(p_131054_), p_131055_);
   }

   private ScoreComponent(String p_131050_, @Nullable EntitySelector p_131051_, String p_131052_) {
      this.name = p_131050_;
      this.selector = p_131051_;
      this.objective = p_131052_;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public EntitySelector getSelector() {
      return this.selector;
   }

   public String getObjective() {
      return this.objective;
   }

   private String findTargetName(CommandSourceStack p_131057_) throws CommandSyntaxException {
      if (this.selector != null) {
         List<? extends Entity> list = this.selector.findEntities(p_131057_);
         if (!list.isEmpty()) {
            if (list.size() != 1) {
               throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
            }

            return list.get(0).getScoreboardName();
         }
      }

      return this.name;
   }

   private String getScore(String p_131063_, CommandSourceStack p_131064_) {
      MinecraftServer minecraftserver = p_131064_.getServer();
      if (minecraftserver != null) {
         Scoreboard scoreboard = minecraftserver.getScoreboard();
         Objective objective = scoreboard.getObjective(this.objective);
         if (scoreboard.hasPlayerScore(p_131063_, objective)) {
            Score score = scoreboard.getOrCreatePlayerScore(p_131063_, objective);
            return Integer.toString(score.getScore());
         }
      }

      return "";
   }

   public ScoreComponent plainCopy() {
      return new ScoreComponent(this.name, this.selector, this.objective);
   }

   public MutableComponent resolve(@Nullable CommandSourceStack p_131059_, @Nullable Entity p_131060_, int p_131061_) throws CommandSyntaxException {
      if (p_131059_ == null) {
         return new TextComponent("");
      } else {
         String s = this.findTargetName(p_131059_);
         String s1 = p_131060_ != null && s.equals("*") ? p_131060_.getScoreboardName() : s;
         return new TextComponent(this.getScore(s1, p_131059_));
      }
   }

   public boolean equals(Object p_131069_) {
      if (this == p_131069_) {
         return true;
      } else if (!(p_131069_ instanceof ScoreComponent)) {
         return false;
      } else {
         ScoreComponent scorecomponent = (ScoreComponent)p_131069_;
         return this.name.equals(scorecomponent.name) && this.objective.equals(scorecomponent.objective) && super.equals(p_131069_);
      }
   }

   public String toString() {
      return "ScoreComponent{name='" + this.name + "'objective='" + this.objective + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
   }
}