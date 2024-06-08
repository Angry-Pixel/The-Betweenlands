package net.minecraft.client.gui.chat;

import java.util.UUID;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ChatListener {
   void handle(ChatType p_93307_, Component p_93308_, UUID p_93309_);
}