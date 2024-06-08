package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface BlockEntityRenderer<T extends BlockEntity> {
   void render(T p_112307_, float p_112308_, PoseStack p_112309_, MultiBufferSource p_112310_, int p_112311_, int p_112312_);

   default boolean shouldRenderOffScreen(T p_112306_) {
      return false;
   }

   default int getViewDistance() {
      return 64;
   }

   default boolean shouldRender(T p_173568_, Vec3 p_173569_) {
      return Vec3.atCenterOf(p_173568_.getBlockPos()).closerThan(p_173569_, (double)this.getViewDistance());
   }
}