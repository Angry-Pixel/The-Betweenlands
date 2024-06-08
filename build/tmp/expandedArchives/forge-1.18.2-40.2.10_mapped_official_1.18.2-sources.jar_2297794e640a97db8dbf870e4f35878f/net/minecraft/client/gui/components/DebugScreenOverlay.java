package net.minecraft.client.gui.components;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebugScreenOverlay extends GuiComponent {
   private static final int COLOR_GREY = 14737632;
   private static final int MARGIN_RIGHT = 2;
   private static final int MARGIN_LEFT = 2;
   private static final int MARGIN_TOP = 2;
   private static final Map<Heightmap.Types, String> HEIGHTMAP_NAMES = Util.make(new EnumMap<>(Heightmap.Types.class), (p_94070_) -> {
      p_94070_.put(Heightmap.Types.WORLD_SURFACE_WG, "SW");
      p_94070_.put(Heightmap.Types.WORLD_SURFACE, "S");
      p_94070_.put(Heightmap.Types.OCEAN_FLOOR_WG, "OW");
      p_94070_.put(Heightmap.Types.OCEAN_FLOOR, "O");
      p_94070_.put(Heightmap.Types.MOTION_BLOCKING, "M");
      p_94070_.put(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, "ML");
   });
   private final Minecraft minecraft;
   private final Font font;
   protected HitResult block;
   protected HitResult liquid;
   @Nullable
   private ChunkPos lastPos;
   @Nullable
   private LevelChunk clientChunk;
   @Nullable
   private CompletableFuture<LevelChunk> serverChunk;
   private static final int RED = -65536;
   private static final int YELLOW = -256;
   private static final int GREEN = -16711936;

   public DebugScreenOverlay(Minecraft p_94039_) {
      this.minecraft = p_94039_;
      this.font = p_94039_.font;
   }

   public void clearChunkCache() {
      this.serverChunk = null;
      this.clientChunk = null;
   }

   public void render(PoseStack p_94057_) {
      this.minecraft.getProfiler().push("debug");
      Entity entity = this.minecraft.getCameraEntity();
      this.block = entity.pick(20.0D, 0.0F, false);
      this.liquid = entity.pick(20.0D, 0.0F, true);
      this.drawGameInformation(p_94057_);
      this.drawSystemInformation(p_94057_);
      if (this.minecraft.options.renderFpsChart) {
         int i = this.minecraft.getWindow().getGuiScaledWidth();
         this.drawChart(p_94057_, this.minecraft.getFrameTimer(), 0, i / 2, true);
         IntegratedServer integratedserver = this.minecraft.getSingleplayerServer();
         if (integratedserver != null) {
            this.drawChart(p_94057_, integratedserver.getFrameTimer(), i - Math.min(i / 2, 240), i / 2, false);
         }
      }

      this.minecraft.getProfiler().pop();
   }

   protected void drawGameInformation(PoseStack p_94077_) {
      List<String> list = this.getGameInformation();
      list.add("");
      boolean flag = this.minecraft.getSingleplayerServer() != null;
      list.add("Debug: Pie [shift]: " + (this.minecraft.options.renderDebugCharts ? "visible" : "hidden") + (flag ? " FPS + TPS" : " FPS") + " [alt]: " + (this.minecraft.options.renderFpsChart ? "visible" : "hidden"));
      list.add("For help: press F3 + Q");

      for(int i = 0; i < list.size(); ++i) {
         String s = list.get(i);
         if (!Strings.isNullOrEmpty(s)) {
            int j = 9;
            int k = this.font.width(s);
            int l = 2;
            int i1 = 2 + j * i;
            fill(p_94077_, 1, i1 - 1, 2 + k + 1, i1 + j - 1, -1873784752);
            this.font.draw(p_94077_, s, 2.0F, (float)i1, 14737632);
         }
      }

   }

   protected void drawSystemInformation(PoseStack p_94080_) {
      List<String> list = this.getSystemInformation();

      for(int i = 0; i < list.size(); ++i) {
         String s = list.get(i);
         if (!Strings.isNullOrEmpty(s)) {
            int j = 9;
            int k = this.font.width(s);
            int l = this.minecraft.getWindow().getGuiScaledWidth() - 2 - k;
            int i1 = 2 + j * i;
            fill(p_94080_, l - 1, i1 - 1, l + k + 1, i1 + j - 1, -1873784752);
            this.font.draw(p_94080_, s, (float)l, (float)i1, 14737632);
         }
      }

   }

   protected List<String> getGameInformation() {
      IntegratedServer integratedserver = this.minecraft.getSingleplayerServer();
      Connection connection = this.minecraft.getConnection().getConnection();
      float f = connection.getAverageSentPackets();
      float f1 = connection.getAverageReceivedPackets();
      String s;
      if (integratedserver != null) {
         s = String.format("Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", integratedserver.getAverageTickTime(), f, f1);
      } else {
         s = String.format("\"%s\" server, %.0f tx, %.0f rx", this.minecraft.player.getServerBrand(), f, f1);
      }

      BlockPos blockpos = this.minecraft.getCameraEntity().blockPosition();
      if (this.minecraft.showOnlyReducedInfo()) {
         return Lists.newArrayList("Minecraft " + SharedConstants.getCurrentVersion().getName() + " (" + this.minecraft.getLaunchedVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.minecraft.fpsString, s, this.minecraft.levelRenderer.getChunkStatistics(), this.minecraft.levelRenderer.getEntityStatistics(), "P: " + this.minecraft.particleEngine.countParticles() + ". T: " + this.minecraft.level.getEntityCount(), this.minecraft.level.gatherChunkSourceStats(), "", String.format("Chunk-relative: %d %d %d", blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15));
      } else {
         Entity entity = this.minecraft.getCameraEntity();
         Direction direction = entity.getDirection();
         String s1;
         switch(direction) {
         case NORTH:
            s1 = "Towards negative Z";
            break;
         case SOUTH:
            s1 = "Towards positive Z";
            break;
         case WEST:
            s1 = "Towards negative X";
            break;
         case EAST:
            s1 = "Towards positive X";
            break;
         default:
            s1 = "Invalid";
         }

         ChunkPos chunkpos = new ChunkPos(blockpos);
         if (!Objects.equals(this.lastPos, chunkpos)) {
            this.lastPos = chunkpos;
            this.clearChunkCache();
         }

         Level level = this.getLevel();
         LongSet longset = (LongSet)(level instanceof ServerLevel ? ((ServerLevel)level).getForcedChunks() : LongSets.EMPTY_SET);
         List<String> list = Lists.newArrayList("Minecraft " + SharedConstants.getCurrentVersion().getName() + " (" + this.minecraft.getLaunchedVersion() + "/" + ClientBrandRetriever.getClientModName() + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType()) + ")", this.minecraft.fpsString, s, this.minecraft.levelRenderer.getChunkStatistics(), this.minecraft.levelRenderer.getEntityStatistics(), "P: " + this.minecraft.particleEngine.countParticles() + ". T: " + this.minecraft.level.getEntityCount(), this.minecraft.level.gatherChunkSourceStats());
         String s2 = this.getServerChunkStats();
         if (s2 != null) {
            list.add(s2);
         }

         list.add(this.minecraft.level.dimension().location() + " FC: " + longset.size());
         list.add("");
         list.add(String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", this.minecraft.getCameraEntity().getX(), this.minecraft.getCameraEntity().getY(), this.minecraft.getCameraEntity().getZ()));
         list.add(String.format("Block: %d %d %d [%d %d %d]", blockpos.getX(), blockpos.getY(), blockpos.getZ(), blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15));
         list.add(String.format("Chunk: %d %d %d [%d %d in r.%d.%d.mca]", chunkpos.x, SectionPos.blockToSectionCoord(blockpos.getY()), chunkpos.z, chunkpos.getRegionLocalX(), chunkpos.getRegionLocalZ(), chunkpos.getRegionX(), chunkpos.getRegionZ()));
         list.add(String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", direction, s1, Mth.wrapDegrees(entity.getYRot()), Mth.wrapDegrees(entity.getXRot())));
         LevelChunk levelchunk = this.getClientChunk();
         if (levelchunk.isEmpty()) {
            list.add("Waiting for chunk...");
         } else {
            int i = this.minecraft.level.getChunkSource().getLightEngine().getRawBrightness(blockpos, 0);
            int j = this.minecraft.level.getBrightness(LightLayer.SKY, blockpos);
            int k = this.minecraft.level.getBrightness(LightLayer.BLOCK, blockpos);
            list.add("Client Light: " + i + " (" + j + " sky, " + k + " block)");
            LevelChunk levelchunk1 = this.getServerChunk();
            StringBuilder stringbuilder = new StringBuilder("CH");

            for(Heightmap.Types heightmap$types : Heightmap.Types.values()) {
               if (heightmap$types.sendToClient()) {
                  stringbuilder.append(" ").append(HEIGHTMAP_NAMES.get(heightmap$types)).append(": ").append(levelchunk.getHeight(heightmap$types, blockpos.getX(), blockpos.getZ()));
               }
            }

            list.add(stringbuilder.toString());
            stringbuilder.setLength(0);
            stringbuilder.append("SH");

            for(Heightmap.Types heightmap$types1 : Heightmap.Types.values()) {
               if (heightmap$types1.keepAfterWorldgen()) {
                  stringbuilder.append(" ").append(HEIGHTMAP_NAMES.get(heightmap$types1)).append(": ");
                  if (levelchunk1 != null) {
                     stringbuilder.append(levelchunk1.getHeight(heightmap$types1, blockpos.getX(), blockpos.getZ()));
                  } else {
                     stringbuilder.append("??");
                  }
               }
            }

            list.add(stringbuilder.toString());
            if (blockpos.getY() >= this.minecraft.level.getMinBuildHeight() && blockpos.getY() < this.minecraft.level.getMaxBuildHeight()) {
               list.add("Biome: " + printBiome(this.minecraft.level.getBiome(blockpos)));
               long l = 0L;
               float f2 = 0.0F;
               if (levelchunk1 != null) {
                  f2 = level.getMoonBrightness();
                  l = levelchunk1.getInhabitedTime();
               }

               DifficultyInstance difficultyinstance = new DifficultyInstance(level.getDifficulty(), level.getDayTime(), l, f2);
               list.add(String.format(Locale.ROOT, "Local Difficulty: %.2f // %.2f (Day %d)", difficultyinstance.getEffectiveDifficulty(), difficultyinstance.getSpecialMultiplier(), this.minecraft.level.getDayTime() / 24000L));
            }
         }

         ServerLevel serverlevel = this.getServerLevel();
         if (serverlevel != null) {
            ServerChunkCache serverchunkcache = serverlevel.getChunkSource();
            ChunkGenerator chunkgenerator = serverchunkcache.getGenerator();
            chunkgenerator.addDebugScreenInfo(list, blockpos);
            Climate.Sampler climate$sampler = chunkgenerator.climateSampler();
            BiomeSource biomesource = chunkgenerator.getBiomeSource();
            biomesource.addDebugInfo(list, blockpos, climate$sampler);
            NaturalSpawner.SpawnState naturalspawner$spawnstate = serverchunkcache.getLastSpawnState();
            if (naturalspawner$spawnstate != null) {
               Object2IntMap<MobCategory> object2intmap = naturalspawner$spawnstate.getMobCategoryCounts();
               int i1 = naturalspawner$spawnstate.getSpawnableChunkCount();
               list.add("SC: " + i1 + ", " + (String)Stream.of(MobCategory.values()).map((p_94068_) -> {
                  return Character.toUpperCase(p_94068_.getName().charAt(0)) + ": " + object2intmap.getInt(p_94068_);
               }).collect(Collectors.joining(", ")));
            } else {
               list.add("SC: N/A");
            }
         }

         PostChain postchain = this.minecraft.gameRenderer.currentEffect();
         if (postchain != null) {
            list.add("Shader: " + postchain.getName());
         }

         list.add(this.minecraft.getSoundManager().getDebugString() + String.format(" (Mood %d%%)", Math.round(this.minecraft.player.getCurrentMood() * 100.0F)));
         return list;
      }
   }

   private static String printBiome(Holder<Biome> p_205375_) {
      return p_205375_.unwrap().map((p_205377_) -> {
         return p_205377_.location().toString();
      }, (p_205367_) -> {
         return "[unregistered " + p_205367_ + "]";
      });
   }

   @Nullable
   private ServerLevel getServerLevel() {
      IntegratedServer integratedserver = this.minecraft.getSingleplayerServer();
      return integratedserver != null ? integratedserver.getLevel(this.minecraft.level.dimension()) : null;
   }

   @Nullable
   private String getServerChunkStats() {
      ServerLevel serverlevel = this.getServerLevel();
      return serverlevel != null ? serverlevel.gatherChunkSourceStats() : null;
   }

   private Level getLevel() {
      return DataFixUtils.orElse(Optional.ofNullable(this.minecraft.getSingleplayerServer()).flatMap((p_205373_) -> {
         return Optional.ofNullable(p_205373_.getLevel(this.minecraft.level.dimension()));
      }), this.minecraft.level);
   }

   @Nullable
   private LevelChunk getServerChunk() {
      if (this.serverChunk == null) {
         ServerLevel serverlevel = this.getServerLevel();
         if (serverlevel != null) {
            this.serverChunk = serverlevel.getChunkSource().getChunkFuture(this.lastPos.x, this.lastPos.z, ChunkStatus.FULL, false).thenApply((p_205369_) -> {
               return p_205369_.map((p_205371_) -> {
                  return (LevelChunk)p_205371_;
               }, (p_205363_) -> {
                  return null;
               });
            });
         }

         if (this.serverChunk == null) {
            this.serverChunk = CompletableFuture.completedFuture(this.getClientChunk());
         }
      }

      return this.serverChunk.getNow((LevelChunk)null);
   }

   private LevelChunk getClientChunk() {
      if (this.clientChunk == null) {
         this.clientChunk = this.minecraft.level.getChunk(this.lastPos.x, this.lastPos.z);
      }

      return this.clientChunk;
   }

   protected List<String> getSystemInformation() {
      long i = Runtime.getRuntime().maxMemory();
      long j = Runtime.getRuntime().totalMemory();
      long k = Runtime.getRuntime().freeMemory();
      long l = j - k;
      List<String> list = Lists.newArrayList(String.format("Java: %s %dbit", System.getProperty("java.version"), this.minecraft.is64Bit() ? 64 : 32), String.format("Mem: % 2d%% %03d/%03dMB", l * 100L / i, bytesToMegabytes(l), bytesToMegabytes(i)), String.format("Allocated: % 2d%% %03dMB", j * 100L / i, bytesToMegabytes(j)), "", String.format("CPU: %s", GlUtil.getCpuInfo()), "", String.format("Display: %dx%d (%s)", Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight(), GlUtil.getVendor()), GlUtil.getRenderer(), GlUtil.getOpenGLVersion());
      if (this.minecraft.showOnlyReducedInfo()) {
         return list;
      } else {
         if (this.block.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult)this.block).getBlockPos();
            BlockState blockstate = this.minecraft.level.getBlockState(blockpos);
            list.add("");
            list.add(ChatFormatting.UNDERLINE + "Targeted Block: " + blockpos.getX() + ", " + blockpos.getY() + ", " + blockpos.getZ());
            list.add(String.valueOf((Object)Registry.BLOCK.getKey(blockstate.getBlock())));

            for(Entry<Property<?>, Comparable<?>> entry : blockstate.getValues().entrySet()) {
               list.add(this.getPropertyValueString(entry));
            }

            blockstate.getTags().map((p_205379_) -> {
               return "#" + p_205379_.location();
            }).forEach(list::add);
         }

         if (this.liquid.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos1 = ((BlockHitResult)this.liquid).getBlockPos();
            FluidState fluidstate = this.minecraft.level.getFluidState(blockpos1);
            list.add("");
            list.add(ChatFormatting.UNDERLINE + "Targeted Fluid: " + blockpos1.getX() + ", " + blockpos1.getY() + ", " + blockpos1.getZ());
            list.add(String.valueOf((Object)Registry.FLUID.getKey(fluidstate.getType())));

            for(Entry<Property<?>, Comparable<?>> entry1 : fluidstate.getValues().entrySet()) {
               list.add(this.getPropertyValueString(entry1));
            }

            fluidstate.getTags().map((p_205365_) -> {
               return "#" + p_205365_.location();
            }).forEach(list::add);
         }

         Entity entity = this.minecraft.crosshairPickEntity;
         if (entity != null) {
            list.add("");
            list.add(ChatFormatting.UNDERLINE + "Targeted Entity");
            list.add(String.valueOf((Object)Registry.ENTITY_TYPE.getKey(entity.getType())));
            entity.getType().builtInRegistryHolder().tags().forEach(t -> list.add("#" + t.location()));
         }

         return list;
      }
   }

   private String getPropertyValueString(Entry<Property<?>, Comparable<?>> p_94072_) {
      Property<?> property = p_94072_.getKey();
      Comparable<?> comparable = p_94072_.getValue();
      String s = Util.getPropertyName(property, comparable);
      if (Boolean.TRUE.equals(comparable)) {
         s = ChatFormatting.GREEN + s;
      } else if (Boolean.FALSE.equals(comparable)) {
         s = ChatFormatting.RED + s;
      }

      return property.getName() + ": " + s;
   }

   private void drawChart(PoseStack p_94059_, FrameTimer p_94060_, int p_94061_, int p_94062_, boolean p_94063_) {
      RenderSystem.disableDepthTest();
      int i = p_94060_.getLogStart();
      int j = p_94060_.getLogEnd();
      long[] along = p_94060_.getLog();
      int l = p_94061_;
      int i1 = Math.max(0, along.length - p_94062_);
      int j1 = along.length - i1;
      int $$8 = p_94060_.wrapIndex(i + i1);
      long k1 = 0L;
      int l1 = Integer.MAX_VALUE;
      int i2 = Integer.MIN_VALUE;

      for(int j2 = 0; j2 < j1; ++j2) {
         int k2 = (int)(along[p_94060_.wrapIndex($$8 + j2)] / 1000000L);
         l1 = Math.min(l1, k2);
         i2 = Math.max(i2, k2);
         k1 += (long)k2;
      }

      int k4 = this.minecraft.getWindow().getGuiScaledHeight();
      fill(p_94059_, p_94061_, k4 - 60, p_94061_ + j1, k4, -1873784752);
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
      RenderSystem.enableBlend();
      RenderSystem.disableTexture();
      RenderSystem.defaultBlendFunc();
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

      for(Matrix4f matrix4f = Transformation.identity().getMatrix(); $$8 != j; $$8 = p_94060_.wrapIndex($$8 + 1)) {
         int l2 = p_94060_.scaleSampleTo(along[$$8], p_94063_ ? 30 : 60, p_94063_ ? 60 : 20);
         int i3 = p_94063_ ? 100 : 60;
         int j3 = this.getSampleColor(Mth.clamp(l2, 0, i3), 0, i3 / 2, i3);
         int k3 = j3 >> 24 & 255;
         int l3 = j3 >> 16 & 255;
         int i4 = j3 >> 8 & 255;
         int j4 = j3 & 255;
         bufferbuilder.vertex(matrix4f, (float)(l + 1), (float)k4, 0.0F).color(l3, i4, j4, k3).endVertex();
         bufferbuilder.vertex(matrix4f, (float)(l + 1), (float)(k4 - l2 + 1), 0.0F).color(l3, i4, j4, k3).endVertex();
         bufferbuilder.vertex(matrix4f, (float)l, (float)(k4 - l2 + 1), 0.0F).color(l3, i4, j4, k3).endVertex();
         bufferbuilder.vertex(matrix4f, (float)l, (float)k4, 0.0F).color(l3, i4, j4, k3).endVertex();
         ++l;
      }

      bufferbuilder.end();
      BufferUploader.end(bufferbuilder);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
      if (p_94063_) {
         fill(p_94059_, p_94061_ + 1, k4 - 30 + 1, p_94061_ + 14, k4 - 30 + 10, -1873784752);
         this.font.draw(p_94059_, "60 FPS", (float)(p_94061_ + 2), (float)(k4 - 30 + 2), 14737632);
         this.hLine(p_94059_, p_94061_, p_94061_ + j1 - 1, k4 - 30, -1);
         fill(p_94059_, p_94061_ + 1, k4 - 60 + 1, p_94061_ + 14, k4 - 60 + 10, -1873784752);
         this.font.draw(p_94059_, "30 FPS", (float)(p_94061_ + 2), (float)(k4 - 60 + 2), 14737632);
         this.hLine(p_94059_, p_94061_, p_94061_ + j1 - 1, k4 - 60, -1);
      } else {
         fill(p_94059_, p_94061_ + 1, k4 - 60 + 1, p_94061_ + 14, k4 - 60 + 10, -1873784752);
         this.font.draw(p_94059_, "20 TPS", (float)(p_94061_ + 2), (float)(k4 - 60 + 2), 14737632);
         this.hLine(p_94059_, p_94061_, p_94061_ + j1 - 1, k4 - 60, -1);
      }

      this.hLine(p_94059_, p_94061_, p_94061_ + j1 - 1, k4 - 1, -1);
      this.vLine(p_94059_, p_94061_, k4 - 60, k4, -1);
      this.vLine(p_94059_, p_94061_ + j1 - 1, k4 - 60, k4, -1);
      if (p_94063_ && this.minecraft.options.framerateLimit > 0 && this.minecraft.options.framerateLimit <= 250) {
         this.hLine(p_94059_, p_94061_, p_94061_ + j1 - 1, k4 - 1 - (int)(1800.0D / (double)this.minecraft.options.framerateLimit), -16711681);
      }

      String s = l1 + " ms min";
      String s1 = k1 / (long)j1 + " ms avg";
      String s2 = i2 + " ms max";
      this.font.drawShadow(p_94059_, s, (float)(p_94061_ + 2), (float)(k4 - 60 - 9), 14737632);
      this.font.drawShadow(p_94059_, s1, (float)(p_94061_ + j1 / 2 - this.font.width(s1) / 2), (float)(k4 - 60 - 9), 14737632);
      this.font.drawShadow(p_94059_, s2, (float)(p_94061_ + j1 - this.font.width(s2)), (float)(k4 - 60 - 9), 14737632);
      RenderSystem.enableDepthTest();
   }

   private int getSampleColor(int p_94046_, int p_94047_, int p_94048_, int p_94049_) {
      return p_94046_ < p_94048_ ? this.colorLerp(-16711936, -256, (float)p_94046_ / (float)p_94048_) : this.colorLerp(-256, -65536, (float)(p_94046_ - p_94048_) / (float)(p_94049_ - p_94048_));
   }

   private int colorLerp(int p_94042_, int p_94043_, float p_94044_) {
      int i = p_94042_ >> 24 & 255;
      int j = p_94042_ >> 16 & 255;
      int k = p_94042_ >> 8 & 255;
      int l = p_94042_ & 255;
      int i1 = p_94043_ >> 24 & 255;
      int j1 = p_94043_ >> 16 & 255;
      int k1 = p_94043_ >> 8 & 255;
      int l1 = p_94043_ & 255;
      int i2 = Mth.clamp((int)Mth.lerp(p_94044_, (float)i, (float)i1), 0, 255);
      int j2 = Mth.clamp((int)Mth.lerp(p_94044_, (float)j, (float)j1), 0, 255);
      int k2 = Mth.clamp((int)Mth.lerp(p_94044_, (float)k, (float)k1), 0, 255);
      int l2 = Mth.clamp((int)Mth.lerp(p_94044_, (float)l, (float)l1), 0, 255);
      return i2 << 24 | j2 << 16 | k2 << 8 | l2;
   }

   private static long bytesToMegabytes(long p_94051_) {
      return p_94051_ / 1024L / 1024L;
   }
}
