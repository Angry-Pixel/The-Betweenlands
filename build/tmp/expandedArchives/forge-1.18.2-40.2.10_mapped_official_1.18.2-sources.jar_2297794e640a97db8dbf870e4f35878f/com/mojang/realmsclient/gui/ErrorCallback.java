package com.mojang.realmsclient.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ErrorCallback {
   void error(Component p_87793_);

   default void error(String p_87792_) {
      this.error(new TextComponent(p_87792_));
   }
}