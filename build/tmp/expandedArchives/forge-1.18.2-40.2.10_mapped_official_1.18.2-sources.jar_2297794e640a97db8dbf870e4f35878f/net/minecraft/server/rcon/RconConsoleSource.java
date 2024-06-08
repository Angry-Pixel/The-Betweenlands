package net.minecraft.server.rcon;

import java.util.UUID;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class RconConsoleSource implements CommandSource {
   private static final String RCON = "Rcon";
   private static final Component RCON_COMPONENT = new TextComponent("Rcon");
   private final StringBuffer buffer = new StringBuffer();
   private final MinecraftServer server;

   public RconConsoleSource(MinecraftServer p_11505_) {
      this.server = p_11505_;
   }

   public void prepareForCommand() {
      this.buffer.setLength(0);
   }

   public String getCommandResponse() {
      return this.buffer.toString();
   }

   public CommandSourceStack createCommandSourceStack() {
      ServerLevel serverlevel = this.server.overworld();
      return new CommandSourceStack(this, Vec3.atLowerCornerOf(serverlevel.getSharedSpawnPos()), Vec2.ZERO, serverlevel, 4, "Rcon", RCON_COMPONENT, this.server, (Entity)null);
   }

   public void sendMessage(Component p_11509_, UUID p_11510_) {
      this.buffer.append(p_11509_.getString()).append("\n"); // FIX MC-7569 - RCON has no newlines in multiline output
   }

   public boolean acceptsSuccess() {
      return true;
   }

   public boolean acceptsFailure() {
      return true;
   }

   public boolean shouldInformAdmins() {
      return this.server.shouldRconBroadcast();
   }
}
