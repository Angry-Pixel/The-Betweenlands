package net.minecraft.client.renderer.debug;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class BrainDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final boolean SHOW_NAME_FOR_ALL = true;
   private static final boolean SHOW_PROFESSION_FOR_ALL = false;
   private static final boolean SHOW_BEHAVIORS_FOR_ALL = false;
   private static final boolean SHOW_ACTIVITIES_FOR_ALL = false;
   private static final boolean SHOW_INVENTORY_FOR_ALL = false;
   private static final boolean SHOW_GOSSIPS_FOR_ALL = false;
   private static final boolean SHOW_PATH_FOR_ALL = false;
   private static final boolean SHOW_HEALTH_FOR_ALL = false;
   private static final boolean SHOW_WANTS_GOLEM_FOR_ALL = true;
   private static final boolean SHOW_NAME_FOR_SELECTED = true;
   private static final boolean SHOW_PROFESSION_FOR_SELECTED = true;
   private static final boolean SHOW_BEHAVIORS_FOR_SELECTED = true;
   private static final boolean SHOW_ACTIVITIES_FOR_SELECTED = true;
   private static final boolean SHOW_MEMORIES_FOR_SELECTED = true;
   private static final boolean SHOW_INVENTORY_FOR_SELECTED = true;
   private static final boolean SHOW_GOSSIPS_FOR_SELECTED = true;
   private static final boolean SHOW_PATH_FOR_SELECTED = true;
   private static final boolean SHOW_HEALTH_FOR_SELECTED = true;
   private static final boolean SHOW_WANTS_GOLEM_FOR_SELECTED = true;
   private static final boolean SHOW_POI_INFO = true;
   private static final int MAX_RENDER_DIST_FOR_BRAIN_INFO = 30;
   private static final int MAX_RENDER_DIST_FOR_POI_INFO = 30;
   private static final int MAX_TARGETING_DIST = 8;
   private static final float TEXT_SCALE = 0.02F;
   private static final int WHITE = -1;
   private static final int YELLOW = -256;
   private static final int CYAN = -16711681;
   private static final int GREEN = -16711936;
   private static final int GRAY = -3355444;
   private static final int PINK = -98404;
   private static final int RED = -65536;
   private static final int ORANGE = -23296;
   private final Minecraft minecraft;
   private final Map<BlockPos, BrainDebugRenderer.PoiInfo> pois = Maps.newHashMap();
   private final Map<UUID, BrainDebugRenderer.BrainDump> brainDumpsPerEntity = Maps.newHashMap();
   @Nullable
   private UUID lastLookedAtUuid;

   public BrainDebugRenderer(Minecraft p_113200_) {
      this.minecraft = p_113200_;
   }

   public void clear() {
      this.pois.clear();
      this.brainDumpsPerEntity.clear();
      this.lastLookedAtUuid = null;
   }

   public void addPoi(BrainDebugRenderer.PoiInfo p_113227_) {
      this.pois.put(p_113227_.pos, p_113227_);
   }

   public void removePoi(BlockPos p_113229_) {
      this.pois.remove(p_113229_);
   }

   public void setFreeTicketCount(BlockPos p_113231_, int p_113232_) {
      BrainDebugRenderer.PoiInfo braindebugrenderer$poiinfo = this.pois.get(p_113231_);
      if (braindebugrenderer$poiinfo == null) {
         LOGGER.warn("Strange, setFreeTicketCount was called for an unknown POI: {}", (Object)p_113231_);
      } else {
         braindebugrenderer$poiinfo.freeTicketCount = p_113232_;
      }
   }

   public void addOrUpdateBrainDump(BrainDebugRenderer.BrainDump p_113220_) {
      this.brainDumpsPerEntity.put(p_113220_.uuid, p_113220_);
   }

   public void removeBrainDump(int p_173811_) {
      this.brainDumpsPerEntity.values().removeIf((p_173814_) -> {
         return p_173814_.id == p_173811_;
      });
   }

   public void render(PoseStack p_113214_, MultiBufferSource p_113215_, double p_113216_, double p_113217_, double p_113218_) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      this.clearRemovedEntities();
      this.doRender(p_113216_, p_113217_, p_113218_);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
      if (!this.minecraft.player.isSpectator()) {
         this.updateLastLookedAtUuid();
      }

   }

   private void clearRemovedEntities() {
      this.brainDumpsPerEntity.entrySet().removeIf((p_113263_) -> {
         Entity entity = this.minecraft.level.getEntity((p_113263_.getValue()).id);
         return entity == null || entity.isRemoved();
      });
   }

   private void doRender(double p_113203_, double p_113204_, double p_113205_) {
      BlockPos blockpos = new BlockPos(p_113203_, p_113204_, p_113205_);
      this.brainDumpsPerEntity.values().forEach((p_113210_) -> {
         if (this.isPlayerCloseEnoughToMob(p_113210_)) {
            this.renderBrainInfo(p_113210_, p_113203_, p_113204_, p_113205_);
         }

      });

      for(BlockPos blockpos1 : this.pois.keySet()) {
         if (blockpos.closerThan(blockpos1, 30.0D)) {
            highlightPoi(blockpos1);
         }
      }

      this.pois.values().forEach((p_113238_) -> {
         if (blockpos.closerThan(p_113238_.pos, 30.0D)) {
            this.renderPoiInfo(p_113238_);
         }

      });
      this.getGhostPois().forEach((p_113241_, p_113242_) -> {
         if (blockpos.closerThan(p_113241_, 30.0D)) {
            this.renderGhostPoi(p_113241_, p_113242_);
         }

      });
   }

   private static void highlightPoi(BlockPos p_113275_) {
      float f = 0.05F;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      DebugRenderer.renderFilledBox(p_113275_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
   }

   private void renderGhostPoi(BlockPos p_113244_, List<String> p_113245_) {
      float f = 0.05F;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      DebugRenderer.renderFilledBox(p_113244_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
      renderTextOverPos("" + p_113245_, p_113244_, 0, -256);
      renderTextOverPos("Ghost POI", p_113244_, 1, -65536);
   }

   private void renderPoiInfo(BrainDebugRenderer.PoiInfo p_113273_) {
      int i = 0;
      Set<String> set = this.getTicketHolderNames(p_113273_);
      if (set.size() < 4) {
         renderTextOverPoi("Owners: " + set, p_113273_, i, -256);
      } else {
         renderTextOverPoi(set.size() + " ticket holders", p_113273_, i, -256);
      }

      ++i;
      Set<String> set1 = this.getPotentialTicketHolderNames(p_113273_);
      if (set1.size() < 4) {
         renderTextOverPoi("Candidates: " + set1, p_113273_, i, -23296);
      } else {
         renderTextOverPoi(set1.size() + " potential owners", p_113273_, i, -23296);
      }

      ++i;
      renderTextOverPoi("Free tickets: " + p_113273_.freeTicketCount, p_113273_, i, -256);
      ++i;
      renderTextOverPoi(p_113273_.type, p_113273_, i, -1);
   }

   private void renderPath(BrainDebugRenderer.BrainDump p_113222_, double p_113223_, double p_113224_, double p_113225_) {
      if (p_113222_.path != null) {
         PathfindingRenderer.renderPath(p_113222_.path, 0.5F, false, false, p_113223_, p_113224_, p_113225_);
      }

   }

   private void renderBrainInfo(BrainDebugRenderer.BrainDump p_113268_, double p_113269_, double p_113270_, double p_113271_) {
      boolean flag = this.isMobSelected(p_113268_);
      int i = 0;
      renderTextOverMob(p_113268_.pos, i, p_113268_.name, -1, 0.03F);
      ++i;
      if (flag) {
         renderTextOverMob(p_113268_.pos, i, p_113268_.profession + " " + p_113268_.xp + " xp", -1, 0.02F);
         ++i;
      }

      if (flag) {
         int j = p_113268_.health < p_113268_.maxHealth ? -23296 : -1;
         renderTextOverMob(p_113268_.pos, i, "health: " + String.format("%.1f", p_113268_.health) + " / " + String.format("%.1f", p_113268_.maxHealth), j, 0.02F);
         ++i;
      }

      if (flag && !p_113268_.inventory.equals("")) {
         renderTextOverMob(p_113268_.pos, i, p_113268_.inventory, -98404, 0.02F);
         ++i;
      }

      if (flag) {
         for(String s : p_113268_.behaviors) {
            renderTextOverMob(p_113268_.pos, i, s, -16711681, 0.02F);
            ++i;
         }
      }

      if (flag) {
         for(String s1 : p_113268_.activities) {
            renderTextOverMob(p_113268_.pos, i, s1, -16711936, 0.02F);
            ++i;
         }
      }

      if (p_113268_.wantsGolem) {
         renderTextOverMob(p_113268_.pos, i, "Wants Golem", -23296, 0.02F);
         ++i;
      }

      if (flag) {
         for(String s2 : p_113268_.gossips) {
            if (s2.startsWith(p_113268_.name)) {
               renderTextOverMob(p_113268_.pos, i, s2, -1, 0.02F);
            } else {
               renderTextOverMob(p_113268_.pos, i, s2, -23296, 0.02F);
            }

            ++i;
         }
      }

      if (flag) {
         for(String s3 : Lists.reverse(p_113268_.memories)) {
            renderTextOverMob(p_113268_.pos, i, s3, -3355444, 0.02F);
            ++i;
         }
      }

      if (flag) {
         this.renderPath(p_113268_, p_113269_, p_113270_, p_113271_);
      }

   }

   private static void renderTextOverPoi(String p_113253_, BrainDebugRenderer.PoiInfo p_113254_, int p_113255_, int p_113256_) {
      BlockPos blockpos = p_113254_.pos;
      renderTextOverPos(p_113253_, blockpos, p_113255_, p_113256_);
   }

   private static void renderTextOverPos(String p_113258_, BlockPos p_113259_, int p_113260_, int p_113261_) {
      double d0 = 1.3D;
      double d1 = 0.2D;
      double d2 = (double)p_113259_.getX() + 0.5D;
      double d3 = (double)p_113259_.getY() + 1.3D + (double)p_113260_ * 0.2D;
      double d4 = (double)p_113259_.getZ() + 0.5D;
      DebugRenderer.renderFloatingText(p_113258_, d2, d3, d4, p_113261_, 0.02F, true, 0.0F, true);
   }

   private static void renderTextOverMob(Position p_113247_, int p_113248_, String p_113249_, int p_113250_, float p_113251_) {
      double d0 = 2.4D;
      double d1 = 0.25D;
      BlockPos blockpos = new BlockPos(p_113247_);
      double d2 = (double)blockpos.getX() + 0.5D;
      double d3 = p_113247_.y() + 2.4D + (double)p_113248_ * 0.25D;
      double d4 = (double)blockpos.getZ() + 0.5D;
      float f = 0.5F;
      DebugRenderer.renderFloatingText(p_113249_, d2, d3, d4, p_113250_, p_113251_, false, 0.5F, true);
   }

   private Set<String> getTicketHolderNames(BrainDebugRenderer.PoiInfo p_113283_) {
      return this.getTicketHolders(p_113283_.pos).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
   }

   private Set<String> getPotentialTicketHolderNames(BrainDebugRenderer.PoiInfo p_113288_) {
      return this.getPotentialTicketHolders(p_113288_.pos).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
   }

   private boolean isMobSelected(BrainDebugRenderer.BrainDump p_113266_) {
      return Objects.equals(this.lastLookedAtUuid, p_113266_.uuid);
   }

   private boolean isPlayerCloseEnoughToMob(BrainDebugRenderer.BrainDump p_113281_) {
      Player player = this.minecraft.player;
      BlockPos blockpos = new BlockPos(player.getX(), p_113281_.pos.y(), player.getZ());
      BlockPos blockpos1 = new BlockPos(p_113281_.pos);
      return blockpos.closerThan(blockpos1, 30.0D);
   }

   private Collection<UUID> getTicketHolders(BlockPos p_113285_) {
      return this.brainDumpsPerEntity.values().stream().filter((p_113278_) -> {
         return p_113278_.hasPoi(p_113285_);
      }).map(BrainDebugRenderer.BrainDump::getUuid).collect(Collectors.toSet());
   }

   private Collection<UUID> getPotentialTicketHolders(BlockPos p_113290_) {
      return this.brainDumpsPerEntity.values().stream().filter((p_113235_) -> {
         return p_113235_.hasPotentialPoi(p_113290_);
      }).map(BrainDebugRenderer.BrainDump::getUuid).collect(Collectors.toSet());
   }

   private Map<BlockPos, List<String>> getGhostPois() {
      Map<BlockPos, List<String>> map = Maps.newHashMap();

      for(BrainDebugRenderer.BrainDump braindebugrenderer$braindump : this.brainDumpsPerEntity.values()) {
         for(BlockPos blockpos : Iterables.concat(braindebugrenderer$braindump.pois, braindebugrenderer$braindump.potentialPois)) {
            if (!this.pois.containsKey(blockpos)) {
               map.computeIfAbsent(blockpos, (p_113292_) -> {
                  return Lists.newArrayList();
               }).add(braindebugrenderer$braindump.name);
            }
         }
      }

      return map;
   }

   private void updateLastLookedAtUuid() {
      DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent((p_113212_) -> {
         this.lastLookedAtUuid = p_113212_.getUUID();
      });
   }

   @OnlyIn(Dist.CLIENT)
   public static class BrainDump {
      public final UUID uuid;
      public final int id;
      public final String name;
      public final String profession;
      public final int xp;
      public final float health;
      public final float maxHealth;
      public final Position pos;
      public final String inventory;
      public final Path path;
      public final boolean wantsGolem;
      public final List<String> activities = Lists.newArrayList();
      public final List<String> behaviors = Lists.newArrayList();
      public final List<String> memories = Lists.newArrayList();
      public final List<String> gossips = Lists.newArrayList();
      public final Set<BlockPos> pois = Sets.newHashSet();
      public final Set<BlockPos> potentialPois = Sets.newHashSet();

      public BrainDump(UUID p_113311_, int p_113312_, String p_113313_, String p_113314_, int p_113315_, float p_113316_, float p_113317_, Position p_113318_, String p_113319_, @Nullable Path p_113320_, boolean p_113321_) {
         this.uuid = p_113311_;
         this.id = p_113312_;
         this.name = p_113313_;
         this.profession = p_113314_;
         this.xp = p_113315_;
         this.health = p_113316_;
         this.maxHealth = p_113317_;
         this.pos = p_113318_;
         this.inventory = p_113319_;
         this.path = p_113320_;
         this.wantsGolem = p_113321_;
      }

      boolean hasPoi(BlockPos p_113327_) {
         return this.pois.stream().anyMatch(p_113327_::equals);
      }

      boolean hasPotentialPoi(BlockPos p_113332_) {
         return this.potentialPois.contains(p_113332_);
      }

      public UUID getUuid() {
         return this.uuid;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class PoiInfo {
      public final BlockPos pos;
      public String type;
      public int freeTicketCount;

      public PoiInfo(BlockPos p_113337_, String p_113338_, int p_113339_) {
         this.pos = p_113337_;
         this.type = p_113338_;
         this.freeTicketCount = p_113339_;
      }
   }
}