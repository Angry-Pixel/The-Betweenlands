package net.minecraft.world.scores;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public class PlayerTeam extends Team {
   private static final int BIT_FRIENDLY_FIRE = 0;
   private static final int BIT_SEE_INVISIBLES = 1;
   private final Scoreboard scoreboard;
   private final String name;
   private final Set<String> players = Sets.newHashSet();
   private Component displayName;
   private Component playerPrefix = TextComponent.EMPTY;
   private Component playerSuffix = TextComponent.EMPTY;
   private boolean allowFriendlyFire = true;
   private boolean seeFriendlyInvisibles = true;
   private Team.Visibility nameTagVisibility = Team.Visibility.ALWAYS;
   private Team.Visibility deathMessageVisibility = Team.Visibility.ALWAYS;
   private ChatFormatting color = ChatFormatting.RESET;
   private Team.CollisionRule collisionRule = Team.CollisionRule.ALWAYS;
   private final Style displayNameStyle;

   public PlayerTeam(Scoreboard p_83340_, String p_83341_) {
      this.scoreboard = p_83340_;
      this.name = p_83341_;
      this.displayName = new TextComponent(p_83341_);
      this.displayNameStyle = Style.EMPTY.withInsertion(p_83341_).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(p_83341_)));
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public String getName() {
      return this.name;
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public MutableComponent getFormattedDisplayName() {
      MutableComponent mutablecomponent = ComponentUtils.wrapInSquareBrackets(this.displayName.copy().withStyle(this.displayNameStyle));
      ChatFormatting chatformatting = this.getColor();
      if (chatformatting != ChatFormatting.RESET) {
         mutablecomponent.withStyle(chatformatting);
      }

      return mutablecomponent;
   }

   public void setDisplayName(Component p_83354_) {
      if (p_83354_ == null) {
         throw new IllegalArgumentException("Name cannot be null");
      } else {
         this.displayName = p_83354_;
         this.scoreboard.onTeamChanged(this);
      }
   }

   public void setPlayerPrefix(@Nullable Component p_83361_) {
      this.playerPrefix = p_83361_ == null ? TextComponent.EMPTY : p_83361_;
      this.scoreboard.onTeamChanged(this);
   }

   public Component getPlayerPrefix() {
      return this.playerPrefix;
   }

   public void setPlayerSuffix(@Nullable Component p_83366_) {
      this.playerSuffix = p_83366_ == null ? TextComponent.EMPTY : p_83366_;
      this.scoreboard.onTeamChanged(this);
   }

   public Component getPlayerSuffix() {
      return this.playerSuffix;
   }

   public Collection<String> getPlayers() {
      return this.players;
   }

   public MutableComponent getFormattedName(Component p_83369_) {
      MutableComponent mutablecomponent = (new TextComponent("")).append(this.playerPrefix).append(p_83369_).append(this.playerSuffix);
      ChatFormatting chatformatting = this.getColor();
      if (chatformatting != ChatFormatting.RESET) {
         mutablecomponent.withStyle(chatformatting);
      }

      return mutablecomponent;
   }

   public static MutableComponent formatNameForTeam(@Nullable Team p_83349_, Component p_83350_) {
      return p_83349_ == null ? p_83350_.copy() : p_83349_.getFormattedName(p_83350_);
   }

   public boolean isAllowFriendlyFire() {
      return this.allowFriendlyFire;
   }

   public void setAllowFriendlyFire(boolean p_83356_) {
      this.allowFriendlyFire = p_83356_;
      this.scoreboard.onTeamChanged(this);
   }

   public boolean canSeeFriendlyInvisibles() {
      return this.seeFriendlyInvisibles;
   }

   public void setSeeFriendlyInvisibles(boolean p_83363_) {
      this.seeFriendlyInvisibles = p_83363_;
      this.scoreboard.onTeamChanged(this);
   }

   public Team.Visibility getNameTagVisibility() {
      return this.nameTagVisibility;
   }

   public Team.Visibility getDeathMessageVisibility() {
      return this.deathMessageVisibility;
   }

   public void setNameTagVisibility(Team.Visibility p_83347_) {
      this.nameTagVisibility = p_83347_;
      this.scoreboard.onTeamChanged(this);
   }

   public void setDeathMessageVisibility(Team.Visibility p_83359_) {
      this.deathMessageVisibility = p_83359_;
      this.scoreboard.onTeamChanged(this);
   }

   public Team.CollisionRule getCollisionRule() {
      return this.collisionRule;
   }

   public void setCollisionRule(Team.CollisionRule p_83345_) {
      this.collisionRule = p_83345_;
      this.scoreboard.onTeamChanged(this);
   }

   public int packOptions() {
      int i = 0;
      if (this.isAllowFriendlyFire()) {
         i |= 1;
      }

      if (this.canSeeFriendlyInvisibles()) {
         i |= 2;
      }

      return i;
   }

   public void unpackOptions(int p_83343_) {
      this.setAllowFriendlyFire((p_83343_ & 1) > 0);
      this.setSeeFriendlyInvisibles((p_83343_ & 2) > 0);
   }

   public void setColor(ChatFormatting p_83352_) {
      this.color = p_83352_;
      this.scoreboard.onTeamChanged(this);
   }

   public ChatFormatting getColor() {
      return this.color;
   }
}