package com.mojang.blaze3d.platform;

import com.google.common.base.Charsets;
import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
@DontObfuscate
public class GlStateManager {
   private static final boolean ON_LINUX = Util.getPlatform() == Util.OS.LINUX;
   public static final int TEXTURE_COUNT = 12;
   private static final GlStateManager.BlendState BLEND = new GlStateManager.BlendState();
   private static final GlStateManager.DepthState DEPTH = new GlStateManager.DepthState();
   private static final GlStateManager.CullState CULL = new GlStateManager.CullState();
   private static final GlStateManager.PolygonOffsetState POLY_OFFSET = new GlStateManager.PolygonOffsetState();
   private static final GlStateManager.ColorLogicState COLOR_LOGIC = new GlStateManager.ColorLogicState();
   private static final GlStateManager.StencilState STENCIL = new GlStateManager.StencilState();
   private static final GlStateManager.ScissorState SCISSOR = new GlStateManager.ScissorState();
   private static int activeTexture;
   private static final GlStateManager.TextureState[] TEXTURES = IntStream.range(0, 12).mapToObj((p_157120_) -> {
      return new GlStateManager.TextureState();
   }).toArray((p_157122_) -> {
      return new GlStateManager.TextureState[p_157122_];
   });
   private static final GlStateManager.ColorMask COLOR_MASK = new GlStateManager.ColorMask();

   public static void _disableScissorTest() {
      RenderSystem.assertOnRenderThreadOrInit();
      SCISSOR.mode.disable();
   }

   public static void _enableScissorTest() {
      RenderSystem.assertOnRenderThreadOrInit();
      SCISSOR.mode.enable();
   }

   public static void _scissorBox(int p_84169_, int p_84170_, int p_84171_, int p_84172_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL20.glScissor(p_84169_, p_84170_, p_84171_, p_84172_);
   }

   public static void _disableDepthTest() {
      RenderSystem.assertOnRenderThreadOrInit();
      DEPTH.mode.disable();
   }

   public static void _enableDepthTest() {
      RenderSystem.assertOnRenderThreadOrInit();
      DEPTH.mode.enable();
   }

   public static void _depthFunc(int p_84324_) {
      RenderSystem.assertOnRenderThreadOrInit();
      if (p_84324_ != DEPTH.func) {
         DEPTH.func = p_84324_;
         GL11.glDepthFunc(p_84324_);
      }

   }

   public static void _depthMask(boolean p_84299_) {
      RenderSystem.assertOnRenderThread();
      if (p_84299_ != DEPTH.mask) {
         DEPTH.mask = p_84299_;
         GL11.glDepthMask(p_84299_);
      }

   }

   public static void _disableBlend() {
      RenderSystem.assertOnRenderThread();
      BLEND.mode.disable();
   }

   public static void _enableBlend() {
      RenderSystem.assertOnRenderThread();
      BLEND.mode.enable();
   }

   public static void _blendFunc(int p_84329_, int p_84330_) {
      RenderSystem.assertOnRenderThread();
      if (p_84329_ != BLEND.srcRgb || p_84330_ != BLEND.dstRgb) {
         BLEND.srcRgb = p_84329_;
         BLEND.dstRgb = p_84330_;
         GL11.glBlendFunc(p_84329_, p_84330_);
      }

   }

   public static void _blendFuncSeparate(int p_84336_, int p_84337_, int p_84338_, int p_84339_) {
      RenderSystem.assertOnRenderThread();
      if (p_84336_ != BLEND.srcRgb || p_84337_ != BLEND.dstRgb || p_84338_ != BLEND.srcAlpha || p_84339_ != BLEND.dstAlpha) {
         BLEND.srcRgb = p_84336_;
         BLEND.dstRgb = p_84337_;
         BLEND.srcAlpha = p_84338_;
         BLEND.dstAlpha = p_84339_;
         glBlendFuncSeparate(p_84336_, p_84337_, p_84338_, p_84339_);
      }

   }

   public static void _blendEquation(int p_84380_) {
      RenderSystem.assertOnRenderThread();
      GL14.glBlendEquation(p_84380_);
   }

   public static int glGetProgrami(int p_84382_, int p_84383_) {
      RenderSystem.assertOnRenderThread();
      return GL20.glGetProgrami(p_84382_, p_84383_);
   }

   public static void glAttachShader(int p_84424_, int p_84425_) {
      RenderSystem.assertOnRenderThread();
      GL20.glAttachShader(p_84424_, p_84425_);
   }

   public static void glDeleteShader(int p_84422_) {
      RenderSystem.assertOnRenderThread();
      GL20.glDeleteShader(p_84422_);
   }

   public static int glCreateShader(int p_84448_) {
      RenderSystem.assertOnRenderThread();
      return GL20.glCreateShader(p_84448_);
   }

   public static void glShaderSource(int p_157117_, List<String> p_157118_) {
      RenderSystem.assertOnRenderThread();
      StringBuilder stringbuilder = new StringBuilder();

      for(String s : p_157118_) {
         stringbuilder.append(s);
      }

      byte[] abyte = stringbuilder.toString().getBytes(Charsets.UTF_8);
      ByteBuffer bytebuffer = MemoryUtil.memAlloc(abyte.length + 1);
      bytebuffer.put(abyte);
      bytebuffer.put((byte)0);
      bytebuffer.flip();

      try {
         MemoryStack memorystack = MemoryStack.stackPush();

         try {
            PointerBuffer pointerbuffer = memorystack.mallocPointer(1);
            pointerbuffer.put(bytebuffer);
            GL20C.nglShaderSource(p_157117_, 1, pointerbuffer.address0(), 0L);
         } catch (Throwable throwable1) {
            if (memorystack != null) {
               try {
                  memorystack.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }
            }

            throw throwable1;
         }

         if (memorystack != null) {
            memorystack.close();
         }
      } finally {
         MemoryUtil.memFree(bytebuffer);
      }

   }

   public static void glCompileShader(int p_84466_) {
      RenderSystem.assertOnRenderThread();
      GL20.glCompileShader(p_84466_);
   }

   public static int glGetShaderi(int p_84450_, int p_84451_) {
      RenderSystem.assertOnRenderThread();
      return GL20.glGetShaderi(p_84450_, p_84451_);
   }

   public static void _glUseProgram(int p_84479_) {
      RenderSystem.assertOnRenderThread();
      GL20.glUseProgram(p_84479_);
   }

   public static int glCreateProgram() {
      RenderSystem.assertOnRenderThread();
      return GL20.glCreateProgram();
   }

   public static void glDeleteProgram(int p_84485_) {
      RenderSystem.assertOnRenderThread();
      GL20.glDeleteProgram(p_84485_);
   }

   public static void glLinkProgram(int p_84491_) {
      RenderSystem.assertOnRenderThread();
      GL20.glLinkProgram(p_84491_);
   }

   public static int _glGetUniformLocation(int p_84346_, CharSequence p_84347_) {
      RenderSystem.assertOnRenderThread();
      return GL20.glGetUniformLocation(p_84346_, p_84347_);
   }

   public static void _glUniform1(int p_84264_, IntBuffer p_84265_) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform1iv(p_84264_, p_84265_);
   }

   public static void _glUniform1i(int p_84468_, int p_84469_) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform1i(p_84468_, p_84469_);
   }

   public static void _glUniform1(int p_84349_, FloatBuffer p_84350_) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform1fv(p_84349_, p_84350_);
   }

   public static void _glUniform2(int p_84352_, IntBuffer p_84353_) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform2iv(p_84352_, p_84353_);
   }

   public static void _glUniform2(int p_84402_, FloatBuffer p_84403_) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform2fv(p_84402_, p_84403_);
   }

   public static void _glUniform3(int p_84405_, IntBuffer p_84406_) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform3iv(p_84405_, p_84406_);
   }

   public static void _glUniform3(int p_84436_, FloatBuffer p_84437_) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform3fv(p_84436_, p_84437_);
   }

   public static void _glUniform4(int p_84439_, IntBuffer p_84440_) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform4iv(p_84439_, p_84440_);
   }

   public static void _glUniform4(int p_84462_, FloatBuffer p_84463_) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform4fv(p_84462_, p_84463_);
   }

   public static void _glUniformMatrix2(int p_84270_, boolean p_84271_, FloatBuffer p_84272_) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniformMatrix2fv(p_84270_, p_84271_, p_84272_);
   }

   public static void _glUniformMatrix3(int p_84355_, boolean p_84356_, FloatBuffer p_84357_) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniformMatrix3fv(p_84355_, p_84356_, p_84357_);
   }

   public static void _glUniformMatrix4(int p_84408_, boolean p_84409_, FloatBuffer p_84410_) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniformMatrix4fv(p_84408_, p_84409_, p_84410_);
   }

   public static int _glGetAttribLocation(int p_84399_, CharSequence p_84400_) {
      RenderSystem.assertOnRenderThread();
      return GL20.glGetAttribLocation(p_84399_, p_84400_);
   }

   public static void _glBindAttribLocation(int p_157062_, int p_157063_, CharSequence p_157064_) {
      RenderSystem.assertOnRenderThread();
      GL20.glBindAttribLocation(p_157062_, p_157063_, p_157064_);
   }

   public static int _glGenBuffers() {
      RenderSystem.assertOnRenderThreadOrInit();
      return GL15.glGenBuffers();
   }

   public static int _glGenVertexArrays() {
      RenderSystem.assertOnRenderThreadOrInit();
      return GL30.glGenVertexArrays();
   }

   public static void _glBindBuffer(int p_84481_, int p_84482_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL15.glBindBuffer(p_84481_, p_84482_);
   }

   public static void _glBindVertexArray(int p_157069_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL30.glBindVertexArray(p_157069_);
   }

   public static void _glBufferData(int p_84257_, ByteBuffer p_84258_, int p_84259_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL15.glBufferData(p_84257_, p_84258_, p_84259_);
   }

   public static void _glBufferData(int p_157071_, long p_157072_, int p_157073_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL15.glBufferData(p_157071_, p_157072_, p_157073_);
   }

   @Nullable
   public static ByteBuffer _glMapBuffer(int p_157091_, int p_157092_) {
      RenderSystem.assertOnRenderThreadOrInit();
      return GL15.glMapBuffer(p_157091_, p_157092_);
   }

   public static void _glUnmapBuffer(int p_157099_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL15.glUnmapBuffer(p_157099_);
   }

   public static void _glDeleteBuffers(int p_84497_) {
      RenderSystem.assertOnRenderThread();
      if (ON_LINUX) {
         GL32C.glBindBuffer(34962, p_84497_);
         GL32C.glBufferData(34962, 0L, 35048);
         GL32C.glBindBuffer(34962, 0);
      }

      GL15.glDeleteBuffers(p_84497_);
   }

   public static void _glCopyTexSubImage2D(int p_84180_, int p_84181_, int p_84182_, int p_84183_, int p_84184_, int p_84185_, int p_84186_, int p_84187_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL20.glCopyTexSubImage2D(p_84180_, p_84181_, p_84182_, p_84183_, p_84184_, p_84185_, p_84186_, p_84187_);
   }

   public static void _glDeleteVertexArrays(int p_157077_) {
      RenderSystem.assertOnRenderThread();
      GL30.glDeleteVertexArrays(p_157077_);
   }

   public static void _glBindFramebuffer(int p_84487_, int p_84488_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL30.glBindFramebuffer(p_84487_, p_84488_);
   }

   public static void _glBlitFrameBuffer(int p_84189_, int p_84190_, int p_84191_, int p_84192_, int p_84193_, int p_84194_, int p_84195_, int p_84196_, int p_84197_, int p_84198_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL30.glBlitFramebuffer(p_84189_, p_84190_, p_84191_, p_84192_, p_84193_, p_84194_, p_84195_, p_84196_, p_84197_, p_84198_);
   }

   public static void _glBindRenderbuffer(int p_157066_, int p_157067_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL30.glBindRenderbuffer(p_157066_, p_157067_);
   }

   public static void _glDeleteRenderbuffers(int p_157075_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL30.glDeleteRenderbuffers(p_157075_);
   }

   public static void _glDeleteFramebuffers(int p_84503_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL30.glDeleteFramebuffers(p_84503_);
   }

   public static int glGenFramebuffers() {
      RenderSystem.assertOnRenderThreadOrInit();
      return GL30.glGenFramebuffers();
   }

   public static int glGenRenderbuffers() {
      RenderSystem.assertOnRenderThreadOrInit();
      return GL30.glGenRenderbuffers();
   }

   public static void _glRenderbufferStorage(int p_157094_, int p_157095_, int p_157096_, int p_157097_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL30.glRenderbufferStorage(p_157094_, p_157095_, p_157096_, p_157097_);
   }

   public static void _glFramebufferRenderbuffer(int p_157085_, int p_157086_, int p_157087_, int p_157088_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL30.glFramebufferRenderbuffer(p_157085_, p_157086_, p_157087_, p_157088_);
   }

   public static int glCheckFramebufferStatus(int p_84509_) {
      RenderSystem.assertOnRenderThreadOrInit();
      return GL30.glCheckFramebufferStatus(p_84509_);
   }

   public static void _glFramebufferTexture2D(int p_84174_, int p_84175_, int p_84176_, int p_84177_, int p_84178_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL30.glFramebufferTexture2D(p_84174_, p_84175_, p_84176_, p_84177_, p_84178_);
   }

   public static int getBoundFramebuffer() {
      RenderSystem.assertOnRenderThread();
      return _getInteger(36006);
   }

   public static void glActiveTexture(int p_84515_) {
      RenderSystem.assertOnRenderThread();
      GL13.glActiveTexture(p_84515_);
   }

   public static void glBlendFuncSeparate(int p_84389_, int p_84390_, int p_84391_, int p_84392_) {
      RenderSystem.assertOnRenderThread();
      GL14.glBlendFuncSeparate(p_84389_, p_84390_, p_84391_, p_84392_);
   }

   public static String glGetShaderInfoLog(int p_84493_, int p_84494_) {
      RenderSystem.assertOnRenderThread();
      return GL20.glGetShaderInfoLog(p_84493_, p_84494_);
   }

   public static String glGetProgramInfoLog(int p_84499_, int p_84500_) {
      RenderSystem.assertOnRenderThread();
      return GL20.glGetProgramInfoLog(p_84499_, p_84500_);
   }

   public static void setupLevelDiffuseLighting(Vector3f p_84291_, Vector3f p_84292_, Matrix4f p_84293_) {
      RenderSystem.assertOnRenderThread();
      Vector4f vector4f = new Vector4f(p_84291_);
      vector4f.transform(p_84293_);
      Vector4f vector4f1 = new Vector4f(p_84292_);
      vector4f1.transform(p_84293_);
      RenderSystem.setShaderLights(new Vector3f(vector4f), new Vector3f(vector4f1));
   }

   public static void setupGuiFlatDiffuseLighting(Vector3f p_84288_, Vector3f p_84289_) {
      RenderSystem.assertOnRenderThread();
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.setIdentity();
      matrix4f.multiply(Matrix4f.createScaleMatrix(1.0F, -1.0F, 1.0F));
      matrix4f.multiply(Vector3f.YP.rotationDegrees(-22.5F));
      matrix4f.multiply(Vector3f.XP.rotationDegrees(135.0F));
      setupLevelDiffuseLighting(p_84288_, p_84289_, matrix4f);
   }

   public static void setupGui3DDiffuseLighting(Vector3f p_84361_, Vector3f p_84362_) {
      RenderSystem.assertOnRenderThread();
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.setIdentity();
      matrix4f.multiply(Vector3f.YP.rotationDegrees(62.0F));
      matrix4f.multiply(Vector3f.XP.rotationDegrees(185.5F));
      matrix4f.multiply(Vector3f.YP.rotationDegrees(-22.5F));
      matrix4f.multiply(Vector3f.XP.rotationDegrees(135.0F));
      setupLevelDiffuseLighting(p_84361_, p_84362_, matrix4f);
   }

   public static void _enableCull() {
      RenderSystem.assertOnRenderThread();
      CULL.enable.enable();
   }

   public static void _disableCull() {
      RenderSystem.assertOnRenderThread();
      CULL.enable.disable();
   }

   public static void _polygonMode(int p_84517_, int p_84518_) {
      RenderSystem.assertOnRenderThread();
      GL11.glPolygonMode(p_84517_, p_84518_);
   }

   public static void _enablePolygonOffset() {
      RenderSystem.assertOnRenderThread();
      POLY_OFFSET.fill.enable();
   }

   public static void _disablePolygonOffset() {
      RenderSystem.assertOnRenderThread();
      POLY_OFFSET.fill.disable();
   }

   public static void _polygonOffset(float p_84137_, float p_84138_) {
      RenderSystem.assertOnRenderThread();
      if (p_84137_ != POLY_OFFSET.factor || p_84138_ != POLY_OFFSET.units) {
         POLY_OFFSET.factor = p_84137_;
         POLY_OFFSET.units = p_84138_;
         GL11.glPolygonOffset(p_84137_, p_84138_);
      }

   }

   public static void _enableColorLogicOp() {
      RenderSystem.assertOnRenderThread();
      COLOR_LOGIC.enable.enable();
   }

   public static void _disableColorLogicOp() {
      RenderSystem.assertOnRenderThread();
      COLOR_LOGIC.enable.disable();
   }

   public static void _logicOp(int p_84533_) {
      RenderSystem.assertOnRenderThread();
      if (p_84533_ != COLOR_LOGIC.op) {
         COLOR_LOGIC.op = p_84533_;
         GL11.glLogicOp(p_84533_);
      }

   }

   public static void _activeTexture(int p_84539_) {
      RenderSystem.assertOnRenderThread();
      if (activeTexture != p_84539_ - '\u84c0') {
         activeTexture = p_84539_ - '\u84c0';
         glActiveTexture(p_84539_);
      }

   }

   public static void _enableTexture() {
      RenderSystem.assertOnRenderThreadOrInit();
      TEXTURES[activeTexture].enable = true;
   }

   public static void _disableTexture() {
      RenderSystem.assertOnRenderThread();
      TEXTURES[activeTexture].enable = false;
   }

   /* Stores the last values sent into glMultiTexCoord2f */
   public static float lastBrightnessX = 0.0f;
   public static float lastBrightnessY = 0.0f;
   public static void _texParameter(int p_84161_, int p_84162_, float p_84163_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glTexParameterf(p_84161_, p_84162_, p_84163_);
      if (p_84161_ == GL13.GL_TEXTURE1) {
          lastBrightnessX = p_84162_;
          lastBrightnessY = p_84163_;
       }
   }

   public static void _texParameter(int p_84332_, int p_84333_, int p_84334_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glTexParameteri(p_84332_, p_84333_, p_84334_);
   }

   public static int _getTexLevelParameter(int p_84385_, int p_84386_, int p_84387_) {
      RenderSystem.assertInInitPhase();
      return GL11.glGetTexLevelParameteri(p_84385_, p_84386_, p_84387_);
   }

   public static int _genTexture() {
      RenderSystem.assertOnRenderThreadOrInit();
      return GL11.glGenTextures();
   }

   public static void _genTextures(int[] p_84306_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glGenTextures(p_84306_);
   }

   public static void _deleteTexture(int p_84542_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glDeleteTextures(p_84542_);

      for(GlStateManager.TextureState glstatemanager$texturestate : TEXTURES) {
         if (glstatemanager$texturestate.binding == p_84542_) {
            glstatemanager$texturestate.binding = -1;
         }
      }

   }

   public static void _deleteTextures(int[] p_84366_) {
      RenderSystem.assertOnRenderThreadOrInit();

      for(GlStateManager.TextureState glstatemanager$texturestate : TEXTURES) {
         for(int i : p_84366_) {
            if (glstatemanager$texturestate.binding == i) {
               glstatemanager$texturestate.binding = -1;
            }
         }
      }

      GL11.glDeleteTextures(p_84366_);
   }

   public static void _bindTexture(int p_84545_) {
      RenderSystem.assertOnRenderThreadOrInit();
      if (p_84545_ != TEXTURES[activeTexture].binding) {
         TEXTURES[activeTexture].binding = p_84545_;
         GL11.glBindTexture(3553, p_84545_);
      }

   }

   public static int _getTextureId(int p_157060_) {
      return p_157060_ >= 0 && p_157060_ < 12 && TEXTURES[p_157060_].enable ? TEXTURES[p_157060_].binding : 0;
   }

   public static int _getActiveTexture() {
      return activeTexture + '\u84c0';
   }

   public static void _texImage2D(int p_84210_, int p_84211_, int p_84212_, int p_84213_, int p_84214_, int p_84215_, int p_84216_, int p_84217_, @Nullable IntBuffer p_84218_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glTexImage2D(p_84210_, p_84211_, p_84212_, p_84213_, p_84214_, p_84215_, p_84216_, p_84217_, p_84218_);
   }

   public static void _texSubImage2D(int p_84200_, int p_84201_, int p_84202_, int p_84203_, int p_84204_, int p_84205_, int p_84206_, int p_84207_, long p_84208_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glTexSubImage2D(p_84200_, p_84201_, p_84202_, p_84203_, p_84204_, p_84205_, p_84206_, p_84207_, p_84208_);
   }

   public static void _getTexImage(int p_84228_, int p_84229_, int p_84230_, int p_84231_, long p_84232_) {
      RenderSystem.assertOnRenderThread();
      GL11.glGetTexImage(p_84228_, p_84229_, p_84230_, p_84231_, p_84232_);
   }

   public static void _viewport(int p_84431_, int p_84432_, int p_84433_, int p_84434_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GlStateManager.Viewport.INSTANCE.x = p_84431_;
      GlStateManager.Viewport.INSTANCE.y = p_84432_;
      GlStateManager.Viewport.INSTANCE.width = p_84433_;
      GlStateManager.Viewport.INSTANCE.height = p_84434_;
      GL11.glViewport(p_84431_, p_84432_, p_84433_, p_84434_);
   }

   public static void _colorMask(boolean p_84301_, boolean p_84302_, boolean p_84303_, boolean p_84304_) {
      RenderSystem.assertOnRenderThread();
      if (p_84301_ != COLOR_MASK.red || p_84302_ != COLOR_MASK.green || p_84303_ != COLOR_MASK.blue || p_84304_ != COLOR_MASK.alpha) {
         COLOR_MASK.red = p_84301_;
         COLOR_MASK.green = p_84302_;
         COLOR_MASK.blue = p_84303_;
         COLOR_MASK.alpha = p_84304_;
         GL11.glColorMask(p_84301_, p_84302_, p_84303_, p_84304_);
      }

   }

   public static void _stencilFunc(int p_84427_, int p_84428_, int p_84429_) {
      RenderSystem.assertOnRenderThread();
      if (p_84427_ != STENCIL.func.func || p_84427_ != STENCIL.func.ref || p_84427_ != STENCIL.func.mask) {
         STENCIL.func.func = p_84427_;
         STENCIL.func.ref = p_84428_;
         STENCIL.func.mask = p_84429_;
         GL11.glStencilFunc(p_84427_, p_84428_, p_84429_);
      }

   }

   public static void _stencilMask(int p_84551_) {
      RenderSystem.assertOnRenderThread();
      if (p_84551_ != STENCIL.mask) {
         STENCIL.mask = p_84551_;
         GL11.glStencilMask(p_84551_);
      }

   }

   public static void _stencilOp(int p_84453_, int p_84454_, int p_84455_) {
      RenderSystem.assertOnRenderThread();
      if (p_84453_ != STENCIL.fail || p_84454_ != STENCIL.zfail || p_84455_ != STENCIL.zpass) {
         STENCIL.fail = p_84453_;
         STENCIL.zfail = p_84454_;
         STENCIL.zpass = p_84455_;
         GL11.glStencilOp(p_84453_, p_84454_, p_84455_);
      }

   }

   public static void _clearDepth(double p_84122_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glClearDepth(p_84122_);
   }

   public static void _clearColor(float p_84319_, float p_84320_, float p_84321_, float p_84322_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glClearColor(p_84319_, p_84320_, p_84321_, p_84322_);
   }

   public static void _clearStencil(int p_84554_) {
      RenderSystem.assertOnRenderThread();
      GL11.glClearStencil(p_84554_);
   }

   public static void _clear(int p_84267_, boolean p_84268_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glClear(p_84267_);
      if (p_84268_) {
         _getError();
      }

   }

   public static void _glDrawPixels(int p_157079_, int p_157080_, int p_157081_, int p_157082_, long p_157083_) {
      RenderSystem.assertOnRenderThread();
      GL11.glDrawPixels(p_157079_, p_157080_, p_157081_, p_157082_, p_157083_);
   }

   public static void _vertexAttribPointer(int p_84239_, int p_84240_, int p_84241_, boolean p_84242_, int p_84243_, long p_84244_) {
      RenderSystem.assertOnRenderThread();
      GL20.glVertexAttribPointer(p_84239_, p_84240_, p_84241_, p_84242_, p_84243_, p_84244_);
   }

   public static void _vertexAttribIPointer(int p_157109_, int p_157110_, int p_157111_, int p_157112_, long p_157113_) {
      RenderSystem.assertOnRenderThread();
      GL30.glVertexAttribIPointer(p_157109_, p_157110_, p_157111_, p_157112_, p_157113_);
   }

   public static void _enableVertexAttribArray(int p_84566_) {
      RenderSystem.assertOnRenderThread();
      GL20.glEnableVertexAttribArray(p_84566_);
   }

   public static void _disableVertexAttribArray(int p_84087_) {
      RenderSystem.assertOnRenderThread();
      GL20.glDisableVertexAttribArray(p_84087_);
   }

   public static void _drawElements(int p_157054_, int p_157055_, int p_157056_, long p_157057_) {
      RenderSystem.assertOnRenderThread();
      GL11.glDrawElements(p_157054_, p_157055_, p_157056_, p_157057_);
   }

   public static void _pixelStore(int p_84523_, int p_84524_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glPixelStorei(p_84523_, p_84524_);
   }

   public static void _readPixels(int p_84220_, int p_84221_, int p_84222_, int p_84223_, int p_84224_, int p_84225_, ByteBuffer p_84226_) {
      RenderSystem.assertOnRenderThread();
      GL11.glReadPixels(p_84220_, p_84221_, p_84222_, p_84223_, p_84224_, p_84225_, p_84226_);
   }

   public static void _readPixels(int p_157101_, int p_157102_, int p_157103_, int p_157104_, int p_157105_, int p_157106_, long p_157107_) {
      RenderSystem.assertOnRenderThread();
      GL11.glReadPixels(p_157101_, p_157102_, p_157103_, p_157104_, p_157105_, p_157106_, p_157107_);
   }

   public static int _getError() {
      RenderSystem.assertOnRenderThread();
      return GL11.glGetError();
   }

   public static String _getString(int p_84090_) {
      RenderSystem.assertOnRenderThread();
      return GL11.glGetString(p_84090_);
   }

   public static int _getInteger(int p_84093_) {
      RenderSystem.assertOnRenderThreadOrInit();
      return GL11.glGetInteger(p_84093_);
   }

   @OnlyIn(Dist.CLIENT)
   static class BlendState {
      public final GlStateManager.BooleanState mode = new GlStateManager.BooleanState(3042);
      public int srcRgb = 1;
      public int dstRgb = 0;
      public int srcAlpha = 1;
      public int dstAlpha = 0;
   }

   @OnlyIn(Dist.CLIENT)
   static class BooleanState {
      private final int state;
      private boolean enabled;

      public BooleanState(int p_84588_) {
         this.state = p_84588_;
      }

      public void disable() {
         this.setEnabled(false);
      }

      public void enable() {
         this.setEnabled(true);
      }

      public void setEnabled(boolean p_84591_) {
         RenderSystem.assertOnRenderThreadOrInit();
         if (p_84591_ != this.enabled) {
            this.enabled = p_84591_;
            if (p_84591_) {
               GL11.glEnable(this.state);
            } else {
               GL11.glDisable(this.state);
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ColorLogicState {
      public final GlStateManager.BooleanState enable = new GlStateManager.BooleanState(3058);
      public int op = 5379;
   }

   @OnlyIn(Dist.CLIENT)
   static class ColorMask {
      public boolean red = true;
      public boolean green = true;
      public boolean blue = true;
      public boolean alpha = true;
   }

   @OnlyIn(Dist.CLIENT)
   static class CullState {
      public final GlStateManager.BooleanState enable = new GlStateManager.BooleanState(2884);
      public int mode = 1029;
   }

   @OnlyIn(Dist.CLIENT)
   static class DepthState {
      public final GlStateManager.BooleanState mode = new GlStateManager.BooleanState(2929);
      public boolean mask = true;
      public int func = 513;
   }

   @OnlyIn(Dist.CLIENT)
   @DontObfuscate
   public static enum DestFactor {
      CONSTANT_ALPHA(32771),
      CONSTANT_COLOR(32769),
      DST_ALPHA(772),
      DST_COLOR(774),
      ONE(1),
      ONE_MINUS_CONSTANT_ALPHA(32772),
      ONE_MINUS_CONSTANT_COLOR(32770),
      ONE_MINUS_DST_ALPHA(773),
      ONE_MINUS_DST_COLOR(775),
      ONE_MINUS_SRC_ALPHA(771),
      ONE_MINUS_SRC_COLOR(769),
      SRC_ALPHA(770),
      SRC_COLOR(768),
      ZERO(0);

      public final int value;

      private DestFactor(int p_84652_) {
         this.value = p_84652_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum LogicOp {
      AND(5377),
      AND_INVERTED(5380),
      AND_REVERSE(5378),
      CLEAR(5376),
      COPY(5379),
      COPY_INVERTED(5388),
      EQUIV(5385),
      INVERT(5386),
      NAND(5390),
      NOOP(5381),
      NOR(5384),
      OR(5383),
      OR_INVERTED(5389),
      OR_REVERSE(5387),
      SET(5391),
      XOR(5382);

      public final int value;

      private LogicOp(int p_84721_) {
         this.value = p_84721_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class PolygonOffsetState {
      public final GlStateManager.BooleanState fill = new GlStateManager.BooleanState(32823);
      public final GlStateManager.BooleanState line = new GlStateManager.BooleanState(10754);
      public float factor;
      public float units;
   }

   @OnlyIn(Dist.CLIENT)
   static class ScissorState {
      public final GlStateManager.BooleanState mode = new GlStateManager.BooleanState(3089);
   }

   @OnlyIn(Dist.CLIENT)
   @DontObfuscate
   public static enum SourceFactor {
      CONSTANT_ALPHA(32771),
      CONSTANT_COLOR(32769),
      DST_ALPHA(772),
      DST_COLOR(774),
      ONE(1),
      ONE_MINUS_CONSTANT_ALPHA(32772),
      ONE_MINUS_CONSTANT_COLOR(32770),
      ONE_MINUS_DST_ALPHA(773),
      ONE_MINUS_DST_COLOR(775),
      ONE_MINUS_SRC_ALPHA(771),
      ONE_MINUS_SRC_COLOR(769),
      SRC_ALPHA(770),
      SRC_ALPHA_SATURATE(776),
      SRC_COLOR(768),
      ZERO(0);

      public final int value;

      private SourceFactor(int p_84757_) {
         this.value = p_84757_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class StencilFunc {
      public int func = 519;
      public int ref;
      public int mask = -1;
   }

   @OnlyIn(Dist.CLIENT)
   static class StencilState {
      public final GlStateManager.StencilFunc func = new GlStateManager.StencilFunc();
      public int mask = -1;
      public int fail = 7680;
      public int zfail = 7680;
      public int zpass = 7680;
   }

   @OnlyIn(Dist.CLIENT)
   static class TextureState {
      public boolean enable;
      public int binding;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Viewport {
      INSTANCE;

      protected int x;
      protected int y;
      protected int width;
      protected int height;

      public static int x() {
         return INSTANCE.x;
      }

      public static int y() {
         return INSTANCE.y;
      }

      public static int width() {
         return INSTANCE.width;
      }

      public static int height() {
         return INSTANCE.height;
      }
   }
}
