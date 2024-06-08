package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Lighting {
   private static final Vector3f DIFFUSE_LIGHT_0 = Util.make(new Vector3f(0.2F, 1.0F, -0.7F), Vector3f::normalize);
   private static final Vector3f DIFFUSE_LIGHT_1 = Util.make(new Vector3f(-0.2F, 1.0F, 0.7F), Vector3f::normalize);
   private static final Vector3f NETHER_DIFFUSE_LIGHT_0 = Util.make(new Vector3f(0.2F, 1.0F, -0.7F), Vector3f::normalize);
   private static final Vector3f NETHER_DIFFUSE_LIGHT_1 = Util.make(new Vector3f(-0.2F, -1.0F, 0.7F), Vector3f::normalize);
   private static final Vector3f INVENTORY_DIFFUSE_LIGHT_0 = Util.make(new Vector3f(0.2F, -1.0F, -1.0F), Vector3f::normalize);
   private static final Vector3f INVENTORY_DIFFUSE_LIGHT_1 = Util.make(new Vector3f(-0.2F, -1.0F, 0.0F), Vector3f::normalize);

   public static void setupNetherLevel(Matrix4f p_84926_) {
      RenderSystem.setupLevelDiffuseLighting(NETHER_DIFFUSE_LIGHT_0, NETHER_DIFFUSE_LIGHT_1, p_84926_);
   }

   public static void setupLevel(Matrix4f p_84929_) {
      RenderSystem.setupLevelDiffuseLighting(DIFFUSE_LIGHT_0, DIFFUSE_LIGHT_1, p_84929_);
   }

   public static void setupForFlatItems() {
      RenderSystem.setupGuiFlatDiffuseLighting(DIFFUSE_LIGHT_0, DIFFUSE_LIGHT_1);
   }

   public static void setupFor3DItems() {
      RenderSystem.setupGui3DDiffuseLighting(DIFFUSE_LIGHT_0, DIFFUSE_LIGHT_1);
   }

   public static void setupForEntityInInventory() {
      RenderSystem.setShaderLights(INVENTORY_DIFFUSE_LIGHT_0, INVENTORY_DIFFUSE_LIGHT_1);
   }
}