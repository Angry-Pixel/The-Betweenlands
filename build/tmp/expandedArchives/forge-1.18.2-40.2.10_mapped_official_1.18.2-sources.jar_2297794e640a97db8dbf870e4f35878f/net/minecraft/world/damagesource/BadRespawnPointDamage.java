package net.minecraft.world.damagesource;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;

public class BadRespawnPointDamage extends DamageSource {
   protected BadRespawnPointDamage() {
      super("badRespawnPoint");
      this.setScalesWithDifficulty();
      this.setExplosion();
   }

   public Component getLocalizedDeathMessage(LivingEntity p_19247_) {
      Component component = ComponentUtils.wrapInSquareBrackets(new TranslatableComponent("death.attack.badRespawnPoint.link")).withStyle((p_19249_) -> {
         return p_19249_.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("MCPE-28723")));
      });
      return new TranslatableComponent("death.attack.badRespawnPoint.message", p_19247_.getDisplayName(), component);
   }
}