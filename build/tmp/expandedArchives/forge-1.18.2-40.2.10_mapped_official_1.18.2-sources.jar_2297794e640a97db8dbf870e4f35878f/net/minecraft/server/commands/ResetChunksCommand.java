package net.minecraft.server.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.datafixers.util.Unit;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ResetChunksCommand {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static void register(CommandDispatcher<CommandSourceStack> p_183667_) {
      p_183667_.register(Commands.literal("resetchunks").requires((p_183683_) -> {
         return p_183683_.hasPermission(2);
      }).executes((p_183693_) -> {
         return resetChunks(p_183693_.getSource(), 0, true);
      }).then(Commands.argument("range", IntegerArgumentType.integer(0, 5)).executes((p_183689_) -> {
         return resetChunks(p_183689_.getSource(), IntegerArgumentType.getInteger(p_183689_, "range"), true);
      }).then(Commands.argument("skipOldChunks", BoolArgumentType.bool()).executes((p_183669_) -> {
         return resetChunks(p_183669_.getSource(), IntegerArgumentType.getInteger(p_183669_, "range"), BoolArgumentType.getBool(p_183669_, "skipOldChunks"));
      }))));
   }

   private static int resetChunks(CommandSourceStack p_183685_, int p_183686_, boolean p_183687_) {
      ServerLevel serverlevel = p_183685_.getLevel();
      ServerChunkCache serverchunkcache = serverlevel.getChunkSource();
      serverchunkcache.chunkMap.debugReloadGenerator();
      Vec3 vec3 = p_183685_.getPosition();
      ChunkPos chunkpos = new ChunkPos(new BlockPos(vec3));
      int i = chunkpos.z - p_183686_;
      int j = chunkpos.z + p_183686_;
      int k = chunkpos.x - p_183686_;
      int l = chunkpos.x + p_183686_;

      for(int i1 = i; i1 <= j; ++i1) {
         for(int j1 = k; j1 <= l; ++j1) {
            ChunkPos chunkpos1 = new ChunkPos(j1, i1);
            LevelChunk levelchunk = serverchunkcache.getChunk(j1, i1, false);
            if (levelchunk != null && (!p_183687_ || !levelchunk.isOldNoiseGeneration())) {
               for(BlockPos blockpos : BlockPos.betweenClosed(chunkpos1.getMinBlockX(), serverlevel.getMinBuildHeight(), chunkpos1.getMinBlockZ(), chunkpos1.getMaxBlockX(), serverlevel.getMaxBuildHeight() - 1, chunkpos1.getMaxBlockZ())) {
                  serverlevel.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 16);
               }
            }
         }
      }

      ProcessorMailbox<Runnable> processormailbox = ProcessorMailbox.create(Util.backgroundExecutor(), "worldgen-resetchunks");
      long j3 = System.currentTimeMillis();
      int k3 = (p_183686_ * 2 + 1) * (p_183686_ * 2 + 1);

      for(ChunkStatus chunkstatus : ImmutableList.of(ChunkStatus.BIOMES, ChunkStatus.NOISE, ChunkStatus.SURFACE, ChunkStatus.CARVERS, ChunkStatus.LIQUID_CARVERS, ChunkStatus.FEATURES)) {
         long k1 = System.currentTimeMillis();
         CompletableFuture<Unit> completablefuture = CompletableFuture.supplyAsync(() -> {
            return Unit.INSTANCE;
         }, processormailbox::tell);

         for(int i2 = chunkpos.z - p_183686_; i2 <= chunkpos.z + p_183686_; ++i2) {
            for(int j2 = chunkpos.x - p_183686_; j2 <= chunkpos.x + p_183686_; ++j2) {
               ChunkPos chunkpos2 = new ChunkPos(j2, i2);
               LevelChunk levelchunk1 = serverchunkcache.getChunk(j2, i2, false);
               if (levelchunk1 != null && (!p_183687_ || !levelchunk1.isOldNoiseGeneration())) {
                  List<ChunkAccess> list = Lists.newArrayList();
                  int k2 = Math.max(1, chunkstatus.getRange());

                  for(int l2 = chunkpos2.z - k2; l2 <= chunkpos2.z + k2; ++l2) {
                     for(int i3 = chunkpos2.x - k2; i3 <= chunkpos2.x + k2; ++i3) {
                        ChunkAccess chunkaccess = serverchunkcache.getChunk(i3, l2, chunkstatus.getParent(), true);
                        ChunkAccess chunkaccess1;
                        if (chunkaccess instanceof ImposterProtoChunk) {
                           chunkaccess1 = new ImposterProtoChunk(((ImposterProtoChunk)chunkaccess).getWrapped(), true);
                        } else if (chunkaccess instanceof LevelChunk) {
                           chunkaccess1 = new ImposterProtoChunk((LevelChunk)chunkaccess, true);
                        } else {
                           chunkaccess1 = chunkaccess;
                        }

                        list.add(chunkaccess1);
                     }
                  }

                  completablefuture = completablefuture.thenComposeAsync((p_183678_) -> {
                     return chunkstatus.generate(processormailbox::tell, serverlevel, serverchunkcache.getGenerator(), serverlevel.getStructureManager(), serverchunkcache.getLightEngine(), (p_183691_) -> {
                        throw new UnsupportedOperationException("Not creating full chunks here");
                     }, list, true).thenApply((p_183681_) -> {
                        if (chunkstatus == ChunkStatus.NOISE) {
                           p_183681_.left().ifPresent((p_183671_) -> {
                              Heightmap.primeHeightmaps(p_183671_, ChunkStatus.POST_FEATURES);
                           });
                        }

                        return Unit.INSTANCE;
                     });
                  }, processormailbox::tell);
               }
            }
         }

         p_183685_.getServer().managedBlock(completablefuture::isDone);
         LOGGER.debug(chunkstatus.getName() + " took " + (System.currentTimeMillis() - k1) + " ms");
      }

      long l3 = System.currentTimeMillis();

      for(int i4 = chunkpos.z - p_183686_; i4 <= chunkpos.z + p_183686_; ++i4) {
         for(int l1 = chunkpos.x - p_183686_; l1 <= chunkpos.x + p_183686_; ++l1) {
            ChunkPos chunkpos3 = new ChunkPos(l1, i4);
            LevelChunk levelchunk2 = serverchunkcache.getChunk(l1, i4, false);
            if (levelchunk2 != null && (!p_183687_ || !levelchunk2.isOldNoiseGeneration())) {
               for(BlockPos blockpos1 : BlockPos.betweenClosed(chunkpos3.getMinBlockX(), serverlevel.getMinBuildHeight(), chunkpos3.getMinBlockZ(), chunkpos3.getMaxBlockX(), serverlevel.getMaxBuildHeight() - 1, chunkpos3.getMaxBlockZ())) {
                  serverchunkcache.blockChanged(blockpos1);
               }
            }
         }
      }

      LOGGER.debug("blockChanged took " + (System.currentTimeMillis() - l3) + " ms");
      long j4 = System.currentTimeMillis() - j3;
      p_183685_.sendSuccess(new TextComponent(String.format("%d chunks have been reset. This took %d ms for %d chunks, or %02f ms per chunk", k3, j4, k3, (float)j4 / (float)k3)), true);
      return 1;
   }
}