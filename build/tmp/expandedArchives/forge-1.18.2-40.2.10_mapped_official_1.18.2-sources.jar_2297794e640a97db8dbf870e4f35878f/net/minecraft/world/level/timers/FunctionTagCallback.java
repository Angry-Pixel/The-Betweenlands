package net.minecraft.world.level.timers;

import net.minecraft.commands.CommandFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.tags.Tag;

public class FunctionTagCallback implements TimerCallback<MinecraftServer> {
   final ResourceLocation tagId;

   public FunctionTagCallback(ResourceLocation p_82191_) {
      this.tagId = p_82191_;
   }

   public void handle(MinecraftServer p_82199_, TimerQueue<MinecraftServer> p_82200_, long p_82201_) {
      ServerFunctionManager serverfunctionmanager = p_82199_.getFunctions();
      Tag<CommandFunction> tag = serverfunctionmanager.getTag(this.tagId);

      for(CommandFunction commandfunction : tag.getValues()) {
         serverfunctionmanager.execute(commandfunction, serverfunctionmanager.getGameLoopSender());
      }

   }

   public static class Serializer extends TimerCallback.Serializer<MinecraftServer, FunctionTagCallback> {
      public Serializer() {
         super(new ResourceLocation("function_tag"), FunctionTagCallback.class);
      }

      public void serialize(CompoundTag p_82206_, FunctionTagCallback p_82207_) {
         p_82206_.putString("Name", p_82207_.tagId.toString());
      }

      public FunctionTagCallback deserialize(CompoundTag p_82204_) {
         ResourceLocation resourcelocation = new ResourceLocation(p_82204_.getString("Name"));
         return new FunctionTagCallback(resourcelocation);
      }
   }
}