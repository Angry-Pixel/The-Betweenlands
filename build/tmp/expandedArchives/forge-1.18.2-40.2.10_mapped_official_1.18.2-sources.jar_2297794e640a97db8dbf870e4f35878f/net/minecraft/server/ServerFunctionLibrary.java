package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class ServerFunctionLibrary implements PreparableReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String FILE_EXTENSION = ".mcfunction";
   private static final int PATH_PREFIX_LENGTH = "functions/".length();
   private static final int PATH_SUFFIX_LENGTH = ".mcfunction".length();
   private volatile Map<ResourceLocation, CommandFunction> functions = ImmutableMap.of();
   private final TagLoader<CommandFunction> tagsLoader = new TagLoader<>(this::getFunction, "tags/functions");
   private volatile Map<ResourceLocation, Tag<CommandFunction>> tags = Map.of();
   private final int functionCompilationLevel;
   private final CommandDispatcher<CommandSourceStack> dispatcher;

   public Optional<CommandFunction> getFunction(ResourceLocation p_136090_) {
      return Optional.ofNullable(this.functions.get(p_136090_));
   }

   public Map<ResourceLocation, CommandFunction> getFunctions() {
      return this.functions;
   }

   public Tag<CommandFunction> getTag(ResourceLocation p_136098_) {
      return this.tags.getOrDefault(p_136098_, Tag.empty());
   }

   public Iterable<ResourceLocation> getAvailableTags() {
      return this.tags.keySet();
   }

   public ServerFunctionLibrary(int p_136053_, CommandDispatcher<CommandSourceStack> p_136054_) {
      this.functionCompilationLevel = p_136053_;
      this.dispatcher = p_136054_;
   }

   public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier p_136057_, ResourceManager p_136058_, ProfilerFiller p_136059_, ProfilerFiller p_136060_, Executor p_136061_, Executor p_136062_) {
      CompletableFuture<Map<ResourceLocation, Tag.Builder>> completablefuture = CompletableFuture.supplyAsync(() -> {
         return this.tagsLoader.load(p_136058_);
      }, p_136061_);
      CompletableFuture<Map<ResourceLocation, CompletableFuture<CommandFunction>>> completablefuture1 = CompletableFuture.supplyAsync(() -> {
         return p_136058_.listResources("functions", (p_179946_) -> {
            return p_179946_.endsWith(".mcfunction");
         });
      }, p_136061_).thenCompose((p_179933_) -> {
         Map<ResourceLocation, CompletableFuture<CommandFunction>> map = Maps.newHashMap();
         CommandSourceStack commandsourcestack = new CommandSourceStack(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, (ServerLevel)null, this.functionCompilationLevel, "", TextComponent.EMPTY, (MinecraftServer)null, (Entity)null);

         for(ResourceLocation resourcelocation : p_179933_) {
            String s = resourcelocation.getPath();
            ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(PATH_PREFIX_LENGTH, s.length() - PATH_SUFFIX_LENGTH));
            map.put(resourcelocation1, CompletableFuture.supplyAsync(() -> {
               List<String> list = readLines(p_136058_, resourcelocation);
               return CommandFunction.fromLines(resourcelocation1, this.dispatcher, commandsourcestack, list);
            }, p_136061_));
         }

         CompletableFuture<?>[] completablefuture2 = map.values().toArray(new CompletableFuture[0]);
         return CompletableFuture.allOf(completablefuture2).handle((p_179949_, p_179950_) -> {
            return map;
         });
      });
      return completablefuture.thenCombine(completablefuture1, Pair::of).thenCompose(p_136057_::wait).thenAcceptAsync((p_179944_) -> {
         Map<ResourceLocation, CompletableFuture<CommandFunction>> map = (Map)p_179944_.getSecond();
         Builder<ResourceLocation, CommandFunction> builder = ImmutableMap.builder();
         map.forEach((p_179941_, p_179942_) -> {
            p_179942_.handle((p_179954_, p_179955_) -> {
               if (p_179955_ != null) {
                  LOGGER.error("Failed to load function {}", p_179941_, p_179955_);
               } else {
                  builder.put(p_179941_, p_179954_);
               }

               return null;
            }).join();
         });
         this.functions = builder.build();
         this.tags = this.tagsLoader.build((Map)p_179944_.getFirst());
      }, p_136062_);
   }

   private static List<String> readLines(ResourceManager p_136070_, ResourceLocation p_136071_) {
      try {
         Resource resource = p_136070_.getResource(p_136071_);

         List list;
         try {
            list = IOUtils.readLines(resource.getInputStream(), StandardCharsets.UTF_8);
         } catch (Throwable throwable1) {
            if (resource != null) {
               try {
                  resource.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }
            }

            throw throwable1;
         }

         if (resource != null) {
            resource.close();
         }

         return list;
      } catch (IOException ioexception) {
         throw new CompletionException(ioexception);
      }
   }
}