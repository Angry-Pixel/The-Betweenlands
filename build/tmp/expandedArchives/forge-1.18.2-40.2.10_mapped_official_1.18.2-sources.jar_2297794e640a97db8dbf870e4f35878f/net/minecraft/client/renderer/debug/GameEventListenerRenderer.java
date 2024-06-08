package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Vector3f;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GameEventListenerRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private static final int LISTENER_RENDER_DIST = 32;
   private static final float BOX_HEIGHT = 1.0F;
   private final List<GameEventListenerRenderer.TrackedGameEvent> trackedGameEvents = Lists.newArrayList();
   private final List<GameEventListenerRenderer.TrackedListener> trackedListeners = Lists.newArrayList();

   public GameEventListenerRenderer(Minecraft p_173822_) {
      this.minecraft = p_173822_;
   }

   public void render(PoseStack p_173846_, MultiBufferSource p_173847_, double p_173848_, double p_173849_, double p_173850_) {
      Level level = this.minecraft.level;
      if (level == null) {
         this.trackedGameEvents.clear();
         this.trackedListeners.clear();
      } else {
         BlockPos blockpos = new BlockPos(p_173848_, 0.0D, p_173850_);
         this.trackedGameEvents.removeIf(GameEventListenerRenderer.TrackedGameEvent::isExpired);
         this.trackedListeners.removeIf((p_173826_) -> {
            return p_173826_.isExpired(level, blockpos);
         });
         RenderSystem.disableTexture();
         RenderSystem.enableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         VertexConsumer vertexconsumer = p_173847_.getBuffer(RenderType.lines());

         for(GameEventListenerRenderer.TrackedListener gameeventlistenerrenderer$trackedlistener : this.trackedListeners) {
            gameeventlistenerrenderer$trackedlistener.getPosition(level).ifPresent((p_173858_) -> {
               int i = p_173858_.getX() - gameeventlistenerrenderer$trackedlistener.getListenerRadius();
               int j = p_173858_.getY() - gameeventlistenerrenderer$trackedlistener.getListenerRadius();
               int k = p_173858_.getZ() - gameeventlistenerrenderer$trackedlistener.getListenerRadius();
               int l = p_173858_.getX() + gameeventlistenerrenderer$trackedlistener.getListenerRadius();
               int i1 = p_173858_.getY() + gameeventlistenerrenderer$trackedlistener.getListenerRadius();
               int j1 = p_173858_.getZ() + gameeventlistenerrenderer$trackedlistener.getListenerRadius();
               Vector3f vector3f = new Vector3f(1.0F, 1.0F, 0.0F);
               LevelRenderer.renderVoxelShape(p_173846_, vertexconsumer, Shapes.create(new AABB((double)i, (double)j, (double)k, (double)l, (double)i1, (double)j1)), -p_173848_, -p_173849_, -p_173850_, vector3f.x(), vector3f.y(), vector3f.z(), 0.35F);
            });
         }

         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         Tesselator tesselator = Tesselator.getInstance();
         BufferBuilder bufferbuilder = tesselator.getBuilder();
         bufferbuilder.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

         for(GameEventListenerRenderer.TrackedListener gameeventlistenerrenderer$trackedlistener1 : this.trackedListeners) {
            gameeventlistenerrenderer$trackedlistener1.getPosition(level).ifPresent((p_173844_) -> {
               Vector3f vector3f = new Vector3f(1.0F, 1.0F, 0.0F);
               LevelRenderer.addChainedFilledBoxVertices(bufferbuilder, (double)((float)p_173844_.getX() - 0.25F) - p_173848_, (double)p_173844_.getY() - p_173849_, (double)((float)p_173844_.getZ() - 0.25F) - p_173850_, (double)((float)p_173844_.getX() + 0.25F) - p_173848_, (double)p_173844_.getY() - p_173849_ + 1.0D, (double)((float)p_173844_.getZ() + 0.25F) - p_173850_, vector3f.x(), vector3f.y(), vector3f.z(), 0.35F);
            });
         }

         tesselator.end();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.lineWidth(2.0F);
         RenderSystem.depthMask(false);

         for(GameEventListenerRenderer.TrackedListener gameeventlistenerrenderer$trackedlistener2 : this.trackedListeners) {
            gameeventlistenerrenderer$trackedlistener2.getPosition(level).ifPresent((p_173860_) -> {
               DebugRenderer.renderFloatingText("Listener Origin", (double)p_173860_.getX(), (double)((float)p_173860_.getY() + 1.8F), (double)p_173860_.getZ(), -1, 0.025F);
               DebugRenderer.renderFloatingText((new BlockPos(p_173860_)).toString(), (double)p_173860_.getX(), (double)((float)p_173860_.getY() + 1.5F), (double)p_173860_.getZ(), -6959665, 0.025F);
            });
         }

         for(GameEventListenerRenderer.TrackedGameEvent gameeventlistenerrenderer$trackedgameevent : this.trackedGameEvents) {
            Vec3 vec3 = gameeventlistenerrenderer$trackedgameevent.position;
            double d0 = (double)0.2F;
            double d1 = vec3.x - (double)0.2F;
            double d2 = vec3.y - (double)0.2F;
            double d3 = vec3.z - (double)0.2F;
            double d4 = vec3.x + (double)0.2F;
            double d5 = vec3.y + (double)0.2F + 0.5D;
            double d6 = vec3.z + (double)0.2F;
            renderTransparentFilledBox(new AABB(d1, d2, d3, d4, d5, d6), 1.0F, 1.0F, 1.0F, 0.2F);
            DebugRenderer.renderFloatingText(gameeventlistenerrenderer$trackedgameevent.gameEvent.getName(), vec3.x, vec3.y + (double)0.85F, vec3.z, -7564911, 0.0075F);
         }

         RenderSystem.depthMask(true);
         RenderSystem.enableTexture();
         RenderSystem.disableBlend();
      }
   }

   private static void renderTransparentFilledBox(AABB p_173834_, float p_173835_, float p_173836_, float p_173837_, float p_173838_) {
      Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
      if (camera.isInitialized()) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         Vec3 vec3 = camera.getPosition().reverse();
         DebugRenderer.renderFilledBox(p_173834_.move(vec3), p_173835_, p_173836_, p_173837_, p_173838_);
      }
   }

   public void trackGameEvent(GameEvent p_173828_, BlockPos p_173829_) {
      this.trackedGameEvents.add(new GameEventListenerRenderer.TrackedGameEvent(Util.getMillis(), p_173828_, Vec3.atBottomCenterOf(p_173829_)));
   }

   public void trackListener(PositionSource p_173831_, int p_173832_) {
      this.trackedListeners.add(new GameEventListenerRenderer.TrackedListener(p_173831_, p_173832_));
   }

   @OnlyIn(Dist.CLIENT)
   static class TrackedGameEvent {
      public final long timeStamp;
      public final GameEvent gameEvent;
      public final Vec3 position;

      public TrackedGameEvent(long p_173865_, GameEvent p_173866_, Vec3 p_173867_) {
         this.timeStamp = p_173865_;
         this.gameEvent = p_173866_;
         this.position = p_173867_;
      }

      public boolean isExpired() {
         return Util.getMillis() - this.timeStamp > 3000L;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class TrackedListener implements GameEventListener {
      public final PositionSource listenerSource;
      public final int listenerRange;

      public TrackedListener(PositionSource p_173872_, int p_173873_) {
         this.listenerSource = p_173872_;
         this.listenerRange = p_173873_;
      }

      public boolean isExpired(Level p_173883_, BlockPos p_173884_) {
         Optional<BlockPos> optional = this.listenerSource.getPosition(p_173883_);
         return !optional.isPresent() || optional.get().distSqr(p_173884_) <= 1024.0D;
      }

      public Optional<BlockPos> getPosition(Level p_173876_) {
         return this.listenerSource.getPosition(p_173876_);
      }

      public PositionSource getListenerSource() {
         return this.listenerSource;
      }

      public int getListenerRadius() {
         return this.listenerRange;
      }

      public boolean handleGameEvent(Level p_173878_, GameEvent p_173879_, @Nullable Entity p_173880_, BlockPos p_173881_) {
         return false;
      }
   }
}