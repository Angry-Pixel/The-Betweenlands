package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoalSelectorDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final int MAX_RENDER_DIST = 160;
   private final Minecraft minecraft;
   private final Map<Integer, List<GoalSelectorDebugRenderer.DebugGoal>> goalSelectors = Maps.newHashMap();

   public void clear() {
      this.goalSelectors.clear();
   }

   public void addGoalSelector(int p_113549_, List<GoalSelectorDebugRenderer.DebugGoal> p_113550_) {
      this.goalSelectors.put(p_113549_, p_113550_);
   }

   public void removeGoalSelector(int p_173889_) {
      this.goalSelectors.remove(p_173889_);
   }

   public GoalSelectorDebugRenderer(Minecraft p_113546_) {
      this.minecraft = p_113546_;
   }

   public void render(PoseStack p_113552_, MultiBufferSource p_113553_, double p_113554_, double p_113555_, double p_113556_) {
      Camera camera = this.minecraft.gameRenderer.getMainCamera();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      BlockPos blockpos = new BlockPos(camera.getPosition().x, 0.0D, camera.getPosition().z);
      this.goalSelectors.forEach((p_113559_, p_113560_) -> {
         for(int i = 0; i < p_113560_.size(); ++i) {
            GoalSelectorDebugRenderer.DebugGoal goalselectordebugrenderer$debuggoal = p_113560_.get(i);
            if (blockpos.closerThan(goalselectordebugrenderer$debuggoal.pos, 160.0D)) {
               double d0 = (double)goalselectordebugrenderer$debuggoal.pos.getX() + 0.5D;
               double d1 = (double)goalselectordebugrenderer$debuggoal.pos.getY() + 2.0D + (double)i * 0.25D;
               double d2 = (double)goalselectordebugrenderer$debuggoal.pos.getZ() + 0.5D;
               int j = goalselectordebugrenderer$debuggoal.isRunning ? -16711936 : -3355444;
               DebugRenderer.renderFloatingText(goalselectordebugrenderer$debuggoal.name, d0, d1, d2, j);
            }
         }

      });
      RenderSystem.enableDepthTest();
      RenderSystem.enableTexture();
   }

   @OnlyIn(Dist.CLIENT)
   public static class DebugGoal {
      public final BlockPos pos;
      public final int priority;
      public final String name;
      public final boolean isRunning;

      public DebugGoal(BlockPos p_113566_, int p_113567_, String p_113568_, boolean p_113569_) {
         this.pos = p_113566_;
         this.priority = p_113567_;
         this.name = p_113568_;
         this.isRunning = p_113569_;
      }
   }
}