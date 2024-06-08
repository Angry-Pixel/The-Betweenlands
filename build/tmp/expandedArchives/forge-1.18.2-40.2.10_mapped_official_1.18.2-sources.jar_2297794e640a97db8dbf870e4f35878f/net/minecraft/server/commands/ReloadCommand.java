package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.WorldData;
import org.slf4j.Logger;

public class ReloadCommand {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static void reloadPacks(Collection<String> p_138236_, CommandSourceStack p_138237_) {
      p_138237_.getServer().reloadResources(p_138236_).exceptionally((p_138234_) -> {
         LOGGER.warn("Failed to execute reload", p_138234_);
         p_138237_.sendFailure(new TranslatableComponent("commands.reload.failure"));
         return null;
      });
   }

   private static Collection<String> discoverNewPacks(PackRepository p_138223_, WorldData p_138224_, Collection<String> p_138225_) {
      p_138223_.reload();
      Collection<String> collection = Lists.newArrayList(p_138225_);
      Collection<String> collection1 = p_138224_.getDataPackConfig().getDisabled();

      for(String s : p_138223_.getAvailableIds()) {
         if (!collection1.contains(s) && !collection.contains(s)) {
            collection.add(s);
         }
      }

      return collection;
   }

   public static void register(CommandDispatcher<CommandSourceStack> p_138227_) {
      p_138227_.register(Commands.literal("reload").requires((p_138231_) -> {
         return p_138231_.hasPermission(2);
      }).executes((p_138229_) -> {
         CommandSourceStack commandsourcestack = p_138229_.getSource();
         MinecraftServer minecraftserver = commandsourcestack.getServer();
         PackRepository packrepository = minecraftserver.getPackRepository();
         WorldData worlddata = minecraftserver.getWorldData();
         Collection<String> collection = packrepository.getSelectedIds();
         Collection<String> collection1 = discoverNewPacks(packrepository, worlddata, collection);
         commandsourcestack.sendSuccess(new TranslatableComponent("commands.reload.success"), true);
         reloadPacks(collection1, commandsourcestack);
         return 0;
      }));
   }
}