package com.mojang.blaze3d.audio;

import com.mojang.math.Vector3f;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.openal.AL10;

@OnlyIn(Dist.CLIENT)
public class Listener {
   private float gain = 1.0F;
   private Vec3 position = Vec3.ZERO;

   public void setListenerPosition(Vec3 p_83740_) {
      this.position = p_83740_;
      AL10.alListener3f(4100, (float)p_83740_.x, (float)p_83740_.y, (float)p_83740_.z);
   }

   public Vec3 getListenerPosition() {
      return this.position;
   }

   public void setListenerOrientation(Vector3f p_83742_, Vector3f p_83743_) {
      AL10.alListenerfv(4111, new float[]{p_83742_.x(), p_83742_.y(), p_83742_.z(), p_83743_.x(), p_83743_.y(), p_83743_.z()});
   }

   public void setGain(float p_83738_) {
      AL10.alListenerf(4106, p_83738_);
      this.gain = p_83738_;
   }

   public float getGain() {
      return this.gain;
   }

   public void reset() {
      this.setListenerPosition(Vec3.ZERO);
      this.setListenerOrientation(Vector3f.ZN, Vector3f.YP);
   }
}