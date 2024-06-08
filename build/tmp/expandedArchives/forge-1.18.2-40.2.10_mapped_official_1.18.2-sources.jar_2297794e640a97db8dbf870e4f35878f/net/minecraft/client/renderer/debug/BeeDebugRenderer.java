package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeeDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final boolean SHOW_GOAL_FOR_ALL_BEES = true;
   private static final boolean SHOW_NAME_FOR_ALL_BEES = true;
   private static final boolean SHOW_HIVE_FOR_ALL_BEES = true;
   private static final boolean SHOW_FLOWER_POS_FOR_ALL_BEES = true;
   private static final boolean SHOW_TRAVEL_TICKS_FOR_ALL_BEES = true;
   private static final boolean SHOW_PATH_FOR_ALL_BEES = false;
   private static final boolean SHOW_GOAL_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_NAME_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_HIVE_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_FLOWER_POS_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_TRAVEL_TICKS_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_PATH_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_HIVE_MEMBERS = true;
   private static final boolean SHOW_BLACKLISTS = true;
   private static final int MAX_RENDER_DIST_FOR_HIVE_OVERLAY = 30;
   private static final int MAX_RENDER_DIST_FOR_BEE_OVERLAY = 30;
   private static final int MAX_TARGETING_DIST = 8;
   private static final int HIVE_TIMEOUT = 20;
   private static final float TEXT_SCALE = 0.02F;
   private static final int WHITE = -1;
   private static final int YELLOW = -256;
   private static final int ORANGE = -23296;
   private static final int GREEN = -16711936;
   private static final int GRAY = -3355444;
   private static final int PINK = -98404;
   private static final int RED = -65536;
   private final Minecraft minecraft;
   private final Map<BlockPos, BeeDebugRenderer.HiveInfo> hives = Maps.newHashMap();
   private final Map<UUID, BeeDebugRenderer.BeeInfo> beeInfosPerEntity = Maps.newHashMap();
   private UUID lastLookedAtUuid;

   public BeeDebugRenderer(Minecraft p_113053_) {
      this.minecraft = p_113053_;
   }

   public void clear() {
      this.hives.clear();
      this.beeInfosPerEntity.clear();
      this.lastLookedAtUuid = null;
   }

   public void addOrUpdateHiveInfo(BeeDebugRenderer.HiveInfo p_113072_) {
      this.hives.put(p_113072_.pos, p_113072_);
   }

   public void addOrUpdateBeeInfo(BeeDebugRenderer.BeeInfo p_113067_) {
      this.beeInfosPerEntity.put(p_113067_.uuid, p_113067_);
   }

   public void removeBeeInfo(int p_173764_) {
      this.beeInfosPerEntity.values().removeIf((p_173767_) -> {
         return p_173767_.id == p_173764_;
      });
   }

   public void render(PoseStack p_113061_, MultiBufferSource p_113062_, double p_113063_, double p_113064_, double p_113065_) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      this.clearRemovedHives();
      this.clearRemovedBees();
      this.doRender();
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
      if (!this.minecraft.player.isSpectator()) {
         this.updateLastLookedAtUuid();
      }

   }

   private void clearRemovedBees() {
      this.beeInfosPerEntity.entrySet().removeIf((p_113132_) -> {
         return this.minecraft.level.getEntity((p_113132_.getValue()).id) == null;
      });
   }

   private void clearRemovedHives() {
      long i = this.minecraft.level.getGameTime() - 20L;
      this.hives.entrySet().removeIf((p_113057_) -> {
         return (p_113057_.getValue()).lastSeen < i;
      });
   }

   private void doRender() {
      BlockPos blockpos = this.getCamera().getBlockPosition();
      this.beeInfosPerEntity.values().forEach((p_113153_) -> {
         if (this.isPlayerCloseEnoughToMob(p_113153_)) {
            this.renderBeeInfo(p_113153_);
         }

      });
      this.renderFlowerInfos();

      for(BlockPos blockpos1 : this.hives.keySet()) {
         if (blockpos.closerThan(blockpos1, 30.0D)) {
            highlightHive(blockpos1);
         }
      }

      Map<BlockPos, Set<UUID>> map = this.createHiveBlacklistMap();
      this.hives.values().forEach((p_113098_) -> {
         if (blockpos.closerThan(p_113098_.pos, 30.0D)) {
            Set<UUID> set = map.get(p_113098_.pos);
            this.renderHiveInfo(p_113098_, (Collection<UUID>)(set == null ? Sets.newHashSet() : set));
         }

      });
      this.getGhostHives().forEach((p_113090_, p_113091_) -> {
         if (blockpos.closerThan(p_113090_, 30.0D)) {
            this.renderGhostHive(p_113090_, p_113091_);
         }

      });
   }

   private Map<BlockPos, Set<UUID>> createHiveBlacklistMap() {
      Map<BlockPos, Set<UUID>> map = Maps.newHashMap();
      this.beeInfosPerEntity.values().forEach((p_113135_) -> {
         p_113135_.blacklistedHives.forEach((p_173771_) -> {
            map.computeIfAbsent(p_173771_, (p_173777_) -> {
               return Sets.newHashSet();
            }).add(p_113135_.getUuid());
         });
      });
      return map;
   }

   private void renderFlowerInfos() {
      Map<BlockPos, Set<UUID>> map = Maps.newHashMap();
      this.beeInfosPerEntity.values().stream().filter(BeeDebugRenderer.BeeInfo::hasFlower).forEach((p_113121_) -> {
         map.computeIfAbsent(p_113121_.flowerPos, (p_173775_) -> {
            return Sets.newHashSet();
         }).add(p_113121_.getUuid());
      });
      map.entrySet().forEach((p_113118_) -> {
         BlockPos blockpos = p_113118_.getKey();
         Set<UUID> set = p_113118_.getValue();
         Set<String> set1 = set.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
         int i = 1;
         renderTextOverPos(set1.toString(), blockpos, i++, -256);
         renderTextOverPos("Flower", blockpos, i++, -1);
         float f = 0.05F;
         renderTransparentFilledBox(blockpos, 0.05F, 0.8F, 0.8F, 0.0F, 0.3F);
      });
   }

   private static String getBeeUuidsAsString(Collection<UUID> p_113116_) {
      if (p_113116_.isEmpty()) {
         return "-";
      } else {
         return p_113116_.size() > 3 ? p_113116_.size() + " bees" : p_113116_.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet()).toString();
      }
   }

   private static void highlightHive(BlockPos p_113077_) {
      float f = 0.05F;
      renderTransparentFilledBox(p_113077_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
   }

   private void renderGhostHive(BlockPos p_113093_, List<String> p_113094_) {
      float f = 0.05F;
      renderTransparentFilledBox(p_113093_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
      renderTextOverPos("" + p_113094_, p_113093_, 0, -256);
      renderTextOverPos("Ghost Hive", p_113093_, 1, -65536);
   }

   private static void renderTransparentFilledBox(BlockPos p_113079_, float p_113080_, float p_113081_, float p_113082_, float p_113083_, float p_113084_) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      DebugRenderer.renderFilledBox(p_113079_, p_113080_, p_113081_, p_113082_, p_113083_, p_113084_);
   }

   private void renderHiveInfo(BeeDebugRenderer.HiveInfo p_113074_, Collection<UUID> p_113075_) {
      int i = 0;
      if (!p_113075_.isEmpty()) {
         renderTextOverHive("Blacklisted by " + getBeeUuidsAsString(p_113075_), p_113074_, i++, -65536);
      }

      renderTextOverHive("Out: " + getBeeUuidsAsString(this.getHiveMembers(p_113074_.pos)), p_113074_, i++, -3355444);
      if (p_113074_.occupantCount == 0) {
         renderTextOverHive("In: -", p_113074_, i++, -256);
      } else if (p_113074_.occupantCount == 1) {
         renderTextOverHive("In: 1 bee", p_113074_, i++, -256);
      } else {
         renderTextOverHive("In: " + p_113074_.occupantCount + " bees", p_113074_, i++, -256);
      }

      renderTextOverHive("Honey: " + p_113074_.honeyLevel, p_113074_, i++, -23296);
      renderTextOverHive(p_113074_.hiveType + (p_113074_.sedated ? " (sedated)" : ""), p_113074_, i++, -1);
   }

   private void renderPath(BeeDebugRenderer.BeeInfo p_113128_) {
      if (p_113128_.path != null) {
         PathfindingRenderer.renderPath(p_113128_.path, 0.5F, false, false, this.getCamera().getPosition().x(), this.getCamera().getPosition().y(), this.getCamera().getPosition().z());
      }

   }

   private void renderBeeInfo(BeeDebugRenderer.BeeInfo p_113138_) {
      boolean flag = this.isBeeSelected(p_113138_);
      int i = 0;
      renderTextOverMob(p_113138_.pos, i++, p_113138_.toString(), -1, 0.03F);
      if (p_113138_.hivePos == null) {
         renderTextOverMob(p_113138_.pos, i++, "No hive", -98404, 0.02F);
      } else {
         renderTextOverMob(p_113138_.pos, i++, "Hive: " + this.getPosDescription(p_113138_, p_113138_.hivePos), -256, 0.02F);
      }

      if (p_113138_.flowerPos == null) {
         renderTextOverMob(p_113138_.pos, i++, "No flower", -98404, 0.02F);
      } else {
         renderTextOverMob(p_113138_.pos, i++, "Flower: " + this.getPosDescription(p_113138_, p_113138_.flowerPos), -256, 0.02F);
      }

      for(String s : p_113138_.goals) {
         renderTextOverMob(p_113138_.pos, i++, s, -16711936, 0.02F);
      }

      if (flag) {
         this.renderPath(p_113138_);
      }

      if (p_113138_.travelTicks > 0) {
         int j = p_113138_.travelTicks < 600 ? -3355444 : -23296;
         renderTextOverMob(p_113138_.pos, i++, "Travelling: " + p_113138_.travelTicks + " ticks", j, 0.02F);
      }

   }

   private static void renderTextOverHive(String p_113106_, BeeDebugRenderer.HiveInfo p_113107_, int p_113108_, int p_113109_) {
      BlockPos blockpos = p_113107_.pos;
      renderTextOverPos(p_113106_, blockpos, p_113108_, p_113109_);
   }

   private static void renderTextOverPos(String p_113111_, BlockPos p_113112_, int p_113113_, int p_113114_) {
      double d0 = 1.3D;
      double d1 = 0.2D;
      double d2 = (double)p_113112_.getX() + 0.5D;
      double d3 = (double)p_113112_.getY() + 1.3D + (double)p_113113_ * 0.2D;
      double d4 = (double)p_113112_.getZ() + 0.5D;
      DebugRenderer.renderFloatingText(p_113111_, d2, d3, d4, p_113114_, 0.02F, true, 0.0F, true);
   }

   private static void renderTextOverMob(Position p_113100_, int p_113101_, String p_113102_, int p_113103_, float p_113104_) {
      double d0 = 2.4D;
      double d1 = 0.25D;
      BlockPos blockpos = new BlockPos(p_113100_);
      double d2 = (double)blockpos.getX() + 0.5D;
      double d3 = p_113100_.y() + 2.4D + (double)p_113101_ * 0.25D;
      double d4 = (double)blockpos.getZ() + 0.5D;
      float f = 0.5F;
      DebugRenderer.renderFloatingText(p_113102_, d2, d3, d4, p_113103_, p_113104_, false, 0.5F, true);
   }

   private Camera getCamera() {
      return this.minecraft.gameRenderer.getMainCamera();
   }

   private Set<String> getHiveMemberNames(BeeDebugRenderer.HiveInfo p_173773_) {
      return this.getHiveMembers(p_173773_.pos).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
   }

   private String getPosDescription(BeeDebugRenderer.BeeInfo p_113069_, BlockPos p_113070_) {
      double d0 = Math.sqrt(p_113070_.distToCenterSqr(p_113069_.pos));
      double d1 = (double)Math.round(d0 * 10.0D) / 10.0D;
      return p_113070_.toShortString() + " (dist " + d1 + ")";
   }

   private boolean isBeeSelected(BeeDebugRenderer.BeeInfo p_113143_) {
      return Objects.equals(this.lastLookedAtUuid, p_113143_.uuid);
   }

   private boolean isPlayerCloseEnoughToMob(BeeDebugRenderer.BeeInfo p_113148_) {
      Player player = this.minecraft.player;
      BlockPos blockpos = new BlockPos(player.getX(), p_113148_.pos.y(), player.getZ());
      BlockPos blockpos1 = new BlockPos(p_113148_.pos);
      return blockpos.closerThan(blockpos1, 30.0D);
   }

   private Collection<UUID> getHiveMembers(BlockPos p_113130_) {
      return this.beeInfosPerEntity.values().stream().filter((p_113087_) -> {
         return p_113087_.hasHive(p_113130_);
      }).map(BeeDebugRenderer.BeeInfo::getUuid).collect(Collectors.toSet());
   }

   private Map<BlockPos, List<String>> getGhostHives() {
      Map<BlockPos, List<String>> map = Maps.newHashMap();

      for(BeeDebugRenderer.BeeInfo beedebugrenderer$beeinfo : this.beeInfosPerEntity.values()) {
         if (beedebugrenderer$beeinfo.hivePos != null && !this.hives.containsKey(beedebugrenderer$beeinfo.hivePos)) {
            map.computeIfAbsent(beedebugrenderer$beeinfo.hivePos, (p_113140_) -> {
               return Lists.newArrayList();
            }).add(beedebugrenderer$beeinfo.getName());
         }
      }

      return map;
   }

   private void updateLastLookedAtUuid() {
      DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent((p_113059_) -> {
         this.lastLookedAtUuid = p_113059_.getUUID();
      });
   }

   @OnlyIn(Dist.CLIENT)
   public static class BeeInfo {
      public final UUID uuid;
      public final int id;
      public final Position pos;
      @Nullable
      public final Path path;
      @Nullable
      public final BlockPos hivePos;
      @Nullable
      public final BlockPos flowerPos;
      public final int travelTicks;
      public final List<String> goals = Lists.newArrayList();
      public final Set<BlockPos> blacklistedHives = Sets.newHashSet();

      public BeeInfo(UUID p_113167_, int p_113168_, Position p_113169_, Path p_113170_, BlockPos p_113171_, BlockPos p_113172_, int p_113173_) {
         this.uuid = p_113167_;
         this.id = p_113168_;
         this.pos = p_113169_;
         this.path = p_113170_;
         this.hivePos = p_113171_;
         this.flowerPos = p_113172_;
         this.travelTicks = p_113173_;
      }

      public boolean hasHive(BlockPos p_113176_) {
         return this.hivePos != null && this.hivePos.equals(p_113176_);
      }

      public UUID getUuid() {
         return this.uuid;
      }

      public String getName() {
         return DebugEntityNameGenerator.getEntityName(this.uuid);
      }

      public String toString() {
         return this.getName();
      }

      public boolean hasFlower() {
         return this.flowerPos != null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class HiveInfo {
      public final BlockPos pos;
      public final String hiveType;
      public final int occupantCount;
      public final int honeyLevel;
      public final boolean sedated;
      public final long lastSeen;

      public HiveInfo(BlockPos p_113187_, String p_113188_, int p_113189_, int p_113190_, boolean p_113191_, long p_113192_) {
         this.pos = p_113187_;
         this.hiveType = p_113188_;
         this.occupantCount = p_113189_;
         this.honeyLevel = p_113190_;
         this.sedated = p_113191_;
         this.lastSeen = p_113192_;
      }
   }
}