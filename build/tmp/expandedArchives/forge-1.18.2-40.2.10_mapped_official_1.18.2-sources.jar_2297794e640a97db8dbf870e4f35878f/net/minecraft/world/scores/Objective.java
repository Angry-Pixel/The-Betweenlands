package net.minecraft.world.scores;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class Objective {
   private final Scoreboard scoreboard;
   private final String name;
   private final ObjectiveCriteria criteria;
   private Component displayName;
   private Component formattedDisplayName;
   private ObjectiveCriteria.RenderType renderType;

   public Objective(Scoreboard p_83308_, String p_83309_, ObjectiveCriteria p_83310_, Component p_83311_, ObjectiveCriteria.RenderType p_83312_) {
      this.scoreboard = p_83308_;
      this.name = p_83309_;
      this.criteria = p_83310_;
      this.displayName = p_83311_;
      this.formattedDisplayName = this.createFormattedDisplayName();
      this.renderType = p_83312_;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public String getName() {
      return this.name;
   }

   public ObjectiveCriteria getCriteria() {
      return this.criteria;
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   private Component createFormattedDisplayName() {
      return ComponentUtils.wrapInSquareBrackets(this.displayName.copy().withStyle((p_83319_) -> {
         return p_83319_.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(this.name)));
      }));
   }

   public Component getFormattedDisplayName() {
      return this.formattedDisplayName;
   }

   public void setDisplayName(Component p_83317_) {
      this.displayName = p_83317_;
      this.formattedDisplayName = this.createFormattedDisplayName();
      this.scoreboard.onObjectiveChanged(this);
   }

   public ObjectiveCriteria.RenderType getRenderType() {
      return this.renderType;
   }

   public void setRenderType(ObjectiveCriteria.RenderType p_83315_) {
      this.renderType = p_83315_;
      this.scoreboard.onObjectiveChanged(this);
   }
}