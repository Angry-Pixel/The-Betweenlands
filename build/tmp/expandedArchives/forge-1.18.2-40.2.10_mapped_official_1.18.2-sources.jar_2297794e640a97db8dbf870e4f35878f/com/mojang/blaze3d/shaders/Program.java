package com.mojang.blaze3d.shaders;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Program {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_LOG_LENGTH = 32768;
   private final Program.Type type;
   private final String name;
   private int id;

   protected Program(Program.Type p_85540_, int p_85541_, String p_85542_) {
      this.type = p_85540_;
      this.id = p_85541_;
      this.name = p_85542_;
   }

   public void attachToShader(Shader p_166611_) {
      RenderSystem.assertOnRenderThread();
      GlStateManager.glAttachShader(p_166611_.getId(), this.getId());
   }

   public void close() {
      if (this.id != -1) {
         RenderSystem.assertOnRenderThread();
         GlStateManager.glDeleteShader(this.id);
         this.id = -1;
         this.type.getPrograms().remove(this.name);
      }
   }

   public String getName() {
      return this.name;
   }

   public static Program compileShader(Program.Type p_166605_, String p_166606_, InputStream p_166607_, String p_166608_, GlslPreprocessor p_166609_) throws IOException {
      RenderSystem.assertOnRenderThread();
      int i = compileShaderInternal(p_166605_, p_166606_, p_166607_, p_166608_, p_166609_);
      Program program = new Program(p_166605_, i, p_166606_);
      p_166605_.getPrograms().put(p_166606_, program);
      return program;
   }

   protected static int compileShaderInternal(Program.Type p_166613_, String p_166614_, InputStream p_166615_, String p_166616_, GlslPreprocessor p_166617_) throws IOException {
      String s = TextureUtil.readResourceAsString(p_166615_);
      if (s == null) {
         throw new IOException("Could not load program " + p_166613_.getName());
      } else {
         int i = GlStateManager.glCreateShader(p_166613_.getGlType());
         GlStateManager.glShaderSource(i, p_166617_.process(s));
         GlStateManager.glCompileShader(i);
         if (GlStateManager.glGetShaderi(i, 35713) == 0) {
            String s1 = StringUtils.trim(GlStateManager.glGetShaderInfoLog(i, 32768));
            throw new IOException("Couldn't compile " + p_166613_.getName() + " program (" + p_166616_ + ", " + p_166614_ + ") : " + s1);
         } else {
            return i;
         }
      }
   }

   private static Program createProgram(Program.Type p_166601_, String p_166602_, int p_166603_) {
      return new Program(p_166601_, p_166603_, p_166602_);
   }

   protected int getId() {
      return this.id;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      VERTEX("vertex", ".vsh", 35633),
      FRAGMENT("fragment", ".fsh", 35632);

      private final String name;
      private final String extension;
      private final int glType;
      private final Map<String, Program> programs = Maps.newHashMap();

      private Type(String p_85563_, String p_85564_, int p_85565_) {
         this.name = p_85563_;
         this.extension = p_85564_;
         this.glType = p_85565_;
      }

      public String getName() {
         return this.name;
      }

      public String getExtension() {
         return this.extension;
      }

      int getGlType() {
         return this.glType;
      }

      public Map<String, Program> getPrograms() {
         return this.programs;
      }
   }
}