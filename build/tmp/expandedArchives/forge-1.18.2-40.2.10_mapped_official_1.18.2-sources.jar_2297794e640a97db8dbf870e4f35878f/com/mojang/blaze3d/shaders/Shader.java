package com.mojang.blaze3d.shaders;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface Shader {
   int getId();

   void markDirty();

   Program getVertexProgram();

   Program getFragmentProgram();

   void attachToProgram();
}