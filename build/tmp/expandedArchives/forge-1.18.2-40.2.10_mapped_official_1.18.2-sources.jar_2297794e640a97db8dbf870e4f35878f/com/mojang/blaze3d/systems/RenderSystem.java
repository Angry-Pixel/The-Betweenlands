package com.mojang.blaze3d.systems;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
@DontObfuscate
public class RenderSystem {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final ConcurrentLinkedQueue<RenderCall> recordingQueue = Queues.newConcurrentLinkedQueue();
   private static final Tesselator RENDER_THREAD_TESSELATOR = new Tesselator();
   private static final int MINIMUM_ATLAS_TEXTURE_SIZE = 1024;
   private static boolean isReplayingQueue;
   @Nullable
   private static Thread gameThread;
   @Nullable
   private static Thread renderThread;
   private static int MAX_SUPPORTED_TEXTURE_SIZE = -1;
   private static boolean isInInit;
   private static double lastDrawTime = Double.MIN_VALUE;
   private static final RenderSystem.AutoStorageIndexBuffer sharedSequential = new RenderSystem.AutoStorageIndexBuffer(1, 1, IntConsumer::accept);
   private static final RenderSystem.AutoStorageIndexBuffer sharedSequentialQuad = new RenderSystem.AutoStorageIndexBuffer(4, 6, (p_157398_, p_157399_) -> {
      p_157398_.accept(p_157399_ + 0);
      p_157398_.accept(p_157399_ + 1);
      p_157398_.accept(p_157399_ + 2);
      p_157398_.accept(p_157399_ + 2);
      p_157398_.accept(p_157399_ + 3);
      p_157398_.accept(p_157399_ + 0);
   });
   private static final RenderSystem.AutoStorageIndexBuffer sharedSequentialLines = new RenderSystem.AutoStorageIndexBuffer(4, 6, (p_157401_, p_157402_) -> {
      p_157401_.accept(p_157402_ + 0);
      p_157401_.accept(p_157402_ + 1);
      p_157401_.accept(p_157402_ + 2);
      p_157401_.accept(p_157402_ + 3);
      p_157401_.accept(p_157402_ + 2);
      p_157401_.accept(p_157402_ + 1);
   });
   private static Matrix3f inverseViewRotationMatrix = new Matrix3f();
   private static Matrix4f projectionMatrix = new Matrix4f();
   private static Matrix4f savedProjectionMatrix = new Matrix4f();
   private static PoseStack modelViewStack = new PoseStack();
   private static Matrix4f modelViewMatrix = new Matrix4f();
   private static Matrix4f textureMatrix = new Matrix4f();
   private static final int[] shaderTextures = new int[12];
   private static final float[] shaderColor = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
   private static float shaderFogStart;
   private static float shaderFogEnd = 1.0F;
   private static final float[] shaderFogColor = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
   private static FogShape shaderFogShape = FogShape.SPHERE;
   private static final Vector3f[] shaderLightDirections = new Vector3f[2];
   private static float shaderGameTime;
   private static float shaderLineWidth = 1.0F;
   private static String apiDescription = "Unknown";
   @Nullable
   private static ShaderInstance shader;

   public static void initRenderThread() {
      if (renderThread == null && gameThread != Thread.currentThread()) {
         renderThread = Thread.currentThread();
      } else {
         throw new IllegalStateException("Could not initialize render thread");
      }
   }

   public static boolean isOnRenderThread() {
      return Thread.currentThread() == renderThread;
   }

   public static boolean isOnRenderThreadOrInit() {
      return isInInit || isOnRenderThread();
   }

   public static void initGameThread(boolean p_69578_) {
      boolean flag = renderThread == Thread.currentThread();
      if (gameThread == null && renderThread != null && flag != p_69578_) {
         gameThread = Thread.currentThread();
      } else {
         throw new IllegalStateException("Could not initialize tick thread");
      }
   }

   public static boolean isOnGameThread() {
      return true;
   }

   public static void assertInInitPhase() {
      if (!isInInitPhase()) {
         throw constructThreadException();
      }
   }

   public static void assertOnGameThreadOrInit() {
      if (!isInInit && !isOnGameThread()) {
         throw constructThreadException();
      }
   }

   public static void assertOnRenderThreadOrInit() {
      if (!isInInit && !isOnRenderThread()) {
         throw constructThreadException();
      }
   }

   public static void assertOnRenderThread() {
      if (!isOnRenderThread()) {
         throw constructThreadException();
      }
   }

   public static void assertOnGameThread() {
      if (!isOnGameThread()) {
         throw constructThreadException();
      }
   }

   private static IllegalStateException constructThreadException() {
      return new IllegalStateException("Rendersystem called from wrong thread");
   }

   public static boolean isInInitPhase() {
      return true;
   }

   public static void recordRenderCall(RenderCall p_69880_) {
      recordingQueue.add(p_69880_);
   }

   public static void flipFrame(long p_69496_) {
      GLFW.glfwPollEvents();
      replayQueue();
      Tesselator.getInstance().getBuilder().clear();
      GLFW.glfwSwapBuffers(p_69496_);
      GLFW.glfwPollEvents();
   }

   public static void replayQueue() {
      isReplayingQueue = true;

      while(!recordingQueue.isEmpty()) {
         RenderCall rendercall = recordingQueue.poll();
         rendercall.execute();
      }

      isReplayingQueue = false;
   }

   public static void limitDisplayFPS(int p_69831_) {
      double d0 = lastDrawTime + 1.0D / (double)p_69831_;

      double d1;
      for(d1 = GLFW.glfwGetTime(); d1 < d0; d1 = GLFW.glfwGetTime()) {
         GLFW.glfwWaitEventsTimeout(d0 - d1);
      }

      lastDrawTime = d1;
   }

   public static void disableDepthTest() {
      assertOnRenderThread();
      GlStateManager._disableDepthTest();
   }

   public static void enableDepthTest() {
      assertOnGameThreadOrInit();
      GlStateManager._enableDepthTest();
   }

   public static void enableScissor(int p_69489_, int p_69490_, int p_69491_, int p_69492_) {
      assertOnGameThreadOrInit();
      GlStateManager._enableScissorTest();
      GlStateManager._scissorBox(p_69489_, p_69490_, p_69491_, p_69492_);
   }

   public static void disableScissor() {
      assertOnGameThreadOrInit();
      GlStateManager._disableScissorTest();
   }

   public static void depthFunc(int p_69457_) {
      assertOnRenderThread();
      GlStateManager._depthFunc(p_69457_);
   }

   public static void depthMask(boolean p_69459_) {
      assertOnRenderThread();
      GlStateManager._depthMask(p_69459_);
   }

   public static void enableBlend() {
      assertOnRenderThread();
      GlStateManager._enableBlend();
   }

   public static void disableBlend() {
      assertOnRenderThread();
      GlStateManager._disableBlend();
   }

   public static void blendFunc(GlStateManager.SourceFactor p_69409_, GlStateManager.DestFactor p_69410_) {
      assertOnRenderThread();
      GlStateManager._blendFunc(p_69409_.value, p_69410_.value);
   }

   public static void blendFunc(int p_69406_, int p_69407_) {
      assertOnRenderThread();
      GlStateManager._blendFunc(p_69406_, p_69407_);
   }

   public static void blendFuncSeparate(GlStateManager.SourceFactor p_69417_, GlStateManager.DestFactor p_69418_, GlStateManager.SourceFactor p_69419_, GlStateManager.DestFactor p_69420_) {
      assertOnRenderThread();
      GlStateManager._blendFuncSeparate(p_69417_.value, p_69418_.value, p_69419_.value, p_69420_.value);
   }

   public static void blendFuncSeparate(int p_69412_, int p_69413_, int p_69414_, int p_69415_) {
      assertOnRenderThread();
      GlStateManager._blendFuncSeparate(p_69412_, p_69413_, p_69414_, p_69415_);
   }

   public static void blendEquation(int p_69404_) {
      assertOnRenderThread();
      GlStateManager._blendEquation(p_69404_);
   }

   public static void enableCull() {
      assertOnRenderThread();
      GlStateManager._enableCull();
   }

   public static void disableCull() {
      assertOnRenderThread();
      GlStateManager._disableCull();
   }

   public static void polygonMode(int p_69861_, int p_69862_) {
      assertOnRenderThread();
      GlStateManager._polygonMode(p_69861_, p_69862_);
   }

   public static void enablePolygonOffset() {
      assertOnRenderThread();
      GlStateManager._enablePolygonOffset();
   }

   public static void disablePolygonOffset() {
      assertOnRenderThread();
      GlStateManager._disablePolygonOffset();
   }

   public static void polygonOffset(float p_69864_, float p_69865_) {
      assertOnRenderThread();
      GlStateManager._polygonOffset(p_69864_, p_69865_);
   }

   public static void enableColorLogicOp() {
      assertOnRenderThread();
      GlStateManager._enableColorLogicOp();
   }

   public static void disableColorLogicOp() {
      assertOnRenderThread();
      GlStateManager._disableColorLogicOp();
   }

   public static void logicOp(GlStateManager.LogicOp p_69836_) {
      assertOnRenderThread();
      GlStateManager._logicOp(p_69836_.value);
   }

   public static void activeTexture(int p_69389_) {
      assertOnRenderThread();
      GlStateManager._activeTexture(p_69389_);
   }

   public static void enableTexture() {
      assertOnRenderThread();
      GlStateManager._enableTexture();
   }

   public static void disableTexture() {
      assertOnRenderThread();
      GlStateManager._disableTexture();
   }

   public static void texParameter(int p_69938_, int p_69939_, int p_69940_) {
      GlStateManager._texParameter(p_69938_, p_69939_, p_69940_);
   }

   public static void deleteTexture(int p_69455_) {
      assertOnGameThreadOrInit();
      GlStateManager._deleteTexture(p_69455_);
   }

   public static void bindTextureForSetup(int p_157185_) {
      bindTexture(p_157185_);
   }

   public static void bindTexture(int p_69397_) {
      GlStateManager._bindTexture(p_69397_);
   }

   public static void viewport(int p_69950_, int p_69951_, int p_69952_, int p_69953_) {
      assertOnGameThreadOrInit();
      GlStateManager._viewport(p_69950_, p_69951_, p_69952_, p_69953_);
   }

   public static void colorMask(boolean p_69445_, boolean p_69446_, boolean p_69447_, boolean p_69448_) {
      assertOnRenderThread();
      GlStateManager._colorMask(p_69445_, p_69446_, p_69447_, p_69448_);
   }

   public static void stencilFunc(int p_69926_, int p_69927_, int p_69928_) {
      assertOnRenderThread();
      GlStateManager._stencilFunc(p_69926_, p_69927_, p_69928_);
   }

   public static void stencilMask(int p_69930_) {
      assertOnRenderThread();
      GlStateManager._stencilMask(p_69930_);
   }

   public static void stencilOp(int p_69932_, int p_69933_, int p_69934_) {
      assertOnRenderThread();
      GlStateManager._stencilOp(p_69932_, p_69933_, p_69934_);
   }

   public static void clearDepth(double p_69431_) {
      assertOnGameThreadOrInit();
      GlStateManager._clearDepth(p_69431_);
   }

   public static void clearColor(float p_69425_, float p_69426_, float p_69427_, float p_69428_) {
      assertOnGameThreadOrInit();
      GlStateManager._clearColor(p_69425_, p_69426_, p_69427_, p_69428_);
   }

   public static void clearStencil(int p_69433_) {
      assertOnRenderThread();
      GlStateManager._clearStencil(p_69433_);
   }

   public static void clear(int p_69422_, boolean p_69423_) {
      assertOnGameThreadOrInit();
      GlStateManager._clear(p_69422_, p_69423_);
   }

   public static void setShaderFogStart(float p_157446_) {
      assertOnRenderThread();
      _setShaderFogStart(p_157446_);
   }

   private static void _setShaderFogStart(float p_157172_) {
      shaderFogStart = p_157172_;
   }

   public static float getShaderFogStart() {
      assertOnRenderThread();
      return shaderFogStart;
   }

   public static void setShaderFogEnd(float p_157444_) {
      assertOnRenderThread();
      _setShaderFogEnd(p_157444_);
   }

   private static void _setShaderFogEnd(float p_157170_) {
      shaderFogEnd = p_157170_;
   }

   public static float getShaderFogEnd() {
      assertOnRenderThread();
      return shaderFogEnd;
   }

   public static void setShaderFogColor(float p_157439_, float p_157440_, float p_157441_, float p_157442_) {
      assertOnRenderThread();
      _setShaderFogColor(p_157439_, p_157440_, p_157441_, p_157442_);
   }

   public static void setShaderFogColor(float p_157435_, float p_157436_, float p_157437_) {
      setShaderFogColor(p_157435_, p_157436_, p_157437_, 1.0F);
   }

   private static void _setShaderFogColor(float p_157165_, float p_157166_, float p_157167_, float p_157168_) {
      shaderFogColor[0] = p_157165_;
      shaderFogColor[1] = p_157166_;
      shaderFogColor[2] = p_157167_;
      shaderFogColor[3] = p_157168_;
   }

   public static float[] getShaderFogColor() {
      assertOnRenderThread();
      return shaderFogColor;
   }

   public static void setShaderFogShape(FogShape p_202161_) {
      assertOnRenderThread();
      _setShaderFogShape(p_202161_);
   }

   private static void _setShaderFogShape(FogShape p_202040_) {
      shaderFogShape = p_202040_;
   }

   public static FogShape getShaderFogShape() {
      assertOnRenderThread();
      return shaderFogShape;
   }

   public static void setShaderLights(Vector3f p_157451_, Vector3f p_157452_) {
      assertOnRenderThread();
      _setShaderLights(p_157451_, p_157452_);
   }

   public static void _setShaderLights(Vector3f p_157174_, Vector3f p_157175_) {
      shaderLightDirections[0] = p_157174_;
      shaderLightDirections[1] = p_157175_;
   }

   public static void setupShaderLights(ShaderInstance p_157462_) {
      assertOnRenderThread();
      if (p_157462_.LIGHT0_DIRECTION != null) {
         p_157462_.LIGHT0_DIRECTION.set(shaderLightDirections[0]);
      }

      if (p_157462_.LIGHT1_DIRECTION != null) {
         p_157462_.LIGHT1_DIRECTION.set(shaderLightDirections[1]);
      }

   }

   public static void setShaderColor(float p_157430_, float p_157431_, float p_157432_, float p_157433_) {
      if (!isOnRenderThread()) {
         recordRenderCall(() -> {
            _setShaderColor(p_157430_, p_157431_, p_157432_, p_157433_);
         });
      } else {
         _setShaderColor(p_157430_, p_157431_, p_157432_, p_157433_);
      }

   }

   private static void _setShaderColor(float p_157160_, float p_157161_, float p_157162_, float p_157163_) {
      shaderColor[0] = p_157160_;
      shaderColor[1] = p_157161_;
      shaderColor[2] = p_157162_;
      shaderColor[3] = p_157163_;
   }

   public static float[] getShaderColor() {
      assertOnRenderThread();
      return shaderColor;
   }

   public static void drawElements(int p_157187_, int p_157188_, int p_157189_) {
      assertOnRenderThread();
      GlStateManager._drawElements(p_157187_, p_157188_, p_157189_, 0L);
   }

   public static void lineWidth(float p_69833_) {
      if (!isOnRenderThread()) {
         recordRenderCall(() -> {
            shaderLineWidth = p_69833_;
         });
      } else {
         shaderLineWidth = p_69833_;
      }

   }

   public static float getShaderLineWidth() {
      assertOnRenderThread();
      return shaderLineWidth;
   }

   public static void pixelStore(int p_69855_, int p_69856_) {
      assertOnGameThreadOrInit();
      GlStateManager._pixelStore(p_69855_, p_69856_);
   }

   public static void readPixels(int p_69872_, int p_69873_, int p_69874_, int p_69875_, int p_69876_, int p_69877_, ByteBuffer p_69878_) {
      assertOnRenderThread();
      GlStateManager._readPixels(p_69872_, p_69873_, p_69874_, p_69875_, p_69876_, p_69877_, p_69878_);
   }

   public static void getString(int p_69520_, Consumer<String> p_69521_) {
      assertOnRenderThread();
      p_69521_.accept(GlStateManager._getString(p_69520_));
   }

   public static String getBackendDescription() {
      assertInInitPhase();
      return String.format("LWJGL version %s", GLX._getLWJGLVersion());
   }

   public static String getApiDescription() {
      return apiDescription;
   }

   public static LongSupplier initBackendSystem() {
      assertInInitPhase();
      return GLX._initGlfw();
   }

   public static void initRenderer(int p_69581_, boolean p_69582_) {
      assertInInitPhase();
      GLX._init(p_69581_, p_69582_);
      apiDescription = GLX.getOpenGLVersionString();
   }

   public static void setErrorCallback(GLFWErrorCallbackI p_69901_) {
      assertInInitPhase();
      GLX._setGlfwErrorCallback(p_69901_);
   }

   public static void renderCrosshair(int p_69882_) {
      assertOnRenderThread();
      GLX._renderCrosshair(p_69882_, true, true, true);
   }

   public static String getCapsString() {
      assertOnRenderThread();
      return "Using framebuffer using OpenGL 3.2";
   }

   public static void setupDefaultState(int p_69903_, int p_69904_, int p_69905_, int p_69906_) {
      assertInInitPhase();
      GlStateManager._enableTexture();
      GlStateManager._clearDepth(1.0D);
      GlStateManager._enableDepthTest();
      GlStateManager._depthFunc(515);
      projectionMatrix.setIdentity();
      savedProjectionMatrix.setIdentity();
      modelViewMatrix.setIdentity();
      textureMatrix.setIdentity();
      GlStateManager._viewport(p_69903_, p_69904_, p_69905_, p_69906_);
   }

   public static int maxSupportedTextureSize() {
      if (MAX_SUPPORTED_TEXTURE_SIZE == -1) {
         assertOnRenderThreadOrInit();
         int i = GlStateManager._getInteger(3379);

         for(int j = Math.max(32768, i); j >= 1024; j >>= 1) {
            GlStateManager._texImage2D(32868, 0, 6408, j, j, 0, 6408, 5121, (IntBuffer)null);
            int k = GlStateManager._getTexLevelParameter(32868, 0, 4096);
            if (k != 0) {
               MAX_SUPPORTED_TEXTURE_SIZE = j;
               return j;
            }
         }

         MAX_SUPPORTED_TEXTURE_SIZE = Math.max(i, 1024);
         LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", (int)MAX_SUPPORTED_TEXTURE_SIZE);
      }

      return MAX_SUPPORTED_TEXTURE_SIZE;
   }

   public static void glBindBuffer(int p_157209_, IntSupplier p_157210_) {
      GlStateManager._glBindBuffer(p_157209_, p_157210_.getAsInt());
   }

   public static void glBindVertexArray(Supplier<Integer> p_157212_) {
      GlStateManager._glBindVertexArray(p_157212_.get());
   }

   public static void glBufferData(int p_69526_, ByteBuffer p_69527_, int p_69528_) {
      assertOnRenderThreadOrInit();
      GlStateManager._glBufferData(p_69526_, p_69527_, p_69528_);
   }

   public static void glDeleteBuffers(int p_69530_) {
      assertOnRenderThread();
      GlStateManager._glDeleteBuffers(p_69530_);
   }

   public static void glDeleteVertexArrays(int p_157214_) {
      assertOnRenderThread();
      GlStateManager._glDeleteVertexArrays(p_157214_);
   }

   public static void glUniform1i(int p_69544_, int p_69545_) {
      assertOnRenderThread();
      GlStateManager._glUniform1i(p_69544_, p_69545_);
   }

   public static void glUniform1(int p_69541_, IntBuffer p_69542_) {
      assertOnRenderThread();
      GlStateManager._glUniform1(p_69541_, p_69542_);
   }

   public static void glUniform2(int p_69550_, IntBuffer p_69551_) {
      assertOnRenderThread();
      GlStateManager._glUniform2(p_69550_, p_69551_);
   }

   public static void glUniform3(int p_69556_, IntBuffer p_69557_) {
      assertOnRenderThread();
      GlStateManager._glUniform3(p_69556_, p_69557_);
   }

   public static void glUniform4(int p_69562_, IntBuffer p_69563_) {
      assertOnRenderThread();
      GlStateManager._glUniform4(p_69562_, p_69563_);
   }

   public static void glUniform1(int p_69538_, FloatBuffer p_69539_) {
      assertOnRenderThread();
      GlStateManager._glUniform1(p_69538_, p_69539_);
   }

   public static void glUniform2(int p_69547_, FloatBuffer p_69548_) {
      assertOnRenderThread();
      GlStateManager._glUniform2(p_69547_, p_69548_);
   }

   public static void glUniform3(int p_69553_, FloatBuffer p_69554_) {
      assertOnRenderThread();
      GlStateManager._glUniform3(p_69553_, p_69554_);
   }

   public static void glUniform4(int p_69559_, FloatBuffer p_69560_) {
      assertOnRenderThread();
      GlStateManager._glUniform4(p_69559_, p_69560_);
   }

   public static void glUniformMatrix2(int p_69565_, boolean p_69566_, FloatBuffer p_69567_) {
      assertOnRenderThread();
      GlStateManager._glUniformMatrix2(p_69565_, p_69566_, p_69567_);
   }

   public static void glUniformMatrix3(int p_69569_, boolean p_69570_, FloatBuffer p_69571_) {
      assertOnRenderThread();
      GlStateManager._glUniformMatrix3(p_69569_, p_69570_, p_69571_);
   }

   public static void glUniformMatrix4(int p_69573_, boolean p_69574_, FloatBuffer p_69575_) {
      assertOnRenderThread();
      GlStateManager._glUniformMatrix4(p_69573_, p_69574_, p_69575_);
   }

   public static void setupOverlayColor(IntSupplier p_69921_, int p_69922_) {
      assertOnRenderThread();
      int i = p_69921_.getAsInt();
      setShaderTexture(1, i);
   }

   public static void teardownOverlayColor() {
      assertOnRenderThread();
      setShaderTexture(1, 0);
   }

   public static void setupLevelDiffuseLighting(Vector3f p_69915_, Vector3f p_69916_, Matrix4f p_69917_) {
      assertOnRenderThread();
      GlStateManager.setupLevelDiffuseLighting(p_69915_, p_69916_, p_69917_);
   }

   public static void setupGuiFlatDiffuseLighting(Vector3f p_69912_, Vector3f p_69913_) {
      assertOnRenderThread();
      GlStateManager.setupGuiFlatDiffuseLighting(p_69912_, p_69913_);
   }

   public static void setupGui3DDiffuseLighting(Vector3f p_69909_, Vector3f p_69910_) {
      assertOnRenderThread();
      GlStateManager.setupGui3DDiffuseLighting(p_69909_, p_69910_);
   }

   public static void beginInitialization() {
      isInInit = true;
   }

   public static void finishInitialization() {
      isInInit = false;
      if (!recordingQueue.isEmpty()) {
         replayQueue();
      }

      if (!recordingQueue.isEmpty()) {
         throw new IllegalStateException("Recorded to render queue during initialization");
      }
   }

   public static void glGenBuffers(Consumer<Integer> p_69532_) {
      if (!isOnRenderThread()) {
         recordRenderCall(() -> {
            p_69532_.accept(GlStateManager._glGenBuffers());
         });
      } else {
         p_69532_.accept(GlStateManager._glGenBuffers());
      }

   }

   public static void glGenVertexArrays(Consumer<Integer> p_157216_) {
      if (!isOnRenderThread()) {
         recordRenderCall(() -> {
            p_157216_.accept(GlStateManager._glGenVertexArrays());
         });
      } else {
         p_157216_.accept(GlStateManager._glGenVertexArrays());
      }

   }

   public static Tesselator renderThreadTesselator() {
      assertOnRenderThread();
      return RENDER_THREAD_TESSELATOR;
   }

   public static void defaultBlendFunc() {
      blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
   }

   /** @deprecated */
   @Deprecated
   public static void runAsFancy(Runnable p_69891_) {
      boolean flag = Minecraft.useShaderTransparency();
      if (!flag) {
         p_69891_.run();
      } else {
         Options options = Minecraft.getInstance().options;
         GraphicsStatus graphicsstatus = options.graphicsMode;
         options.graphicsMode = GraphicsStatus.FANCY;
         p_69891_.run();
         options.graphicsMode = graphicsstatus;
      }
   }

   public static void setShader(Supplier<ShaderInstance> p_157428_) {
      if (!isOnRenderThread()) {
         recordRenderCall(() -> {
            shader = p_157428_.get();
         });
      } else {
         shader = p_157428_.get();
      }

   }

   @Nullable
   public static ShaderInstance getShader() {
      assertOnRenderThread();
      return shader;
   }

   public static int getTextureId(int p_157206_) {
      return GlStateManager._getTextureId(p_157206_);
   }

   public static void setShaderTexture(int p_157457_, ResourceLocation p_157458_) {
      if (!isOnRenderThread()) {
         recordRenderCall(() -> {
            _setShaderTexture(p_157457_, p_157458_);
         });
      } else {
         _setShaderTexture(p_157457_, p_157458_);
      }

   }

   public static void _setShaderTexture(int p_157180_, ResourceLocation p_157181_) {
      if (p_157180_ >= 0 && p_157180_ < shaderTextures.length) {
         TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
         AbstractTexture abstracttexture = texturemanager.getTexture(p_157181_);
         shaderTextures[p_157180_] = abstracttexture.getId();
      }

   }

   public static void setShaderTexture(int p_157454_, int p_157455_) {
      if (!isOnRenderThread()) {
         recordRenderCall(() -> {
            _setShaderTexture(p_157454_, p_157455_);
         });
      } else {
         _setShaderTexture(p_157454_, p_157455_);
      }

   }

   public static void _setShaderTexture(int p_157177_, int p_157178_) {
      if (p_157177_ >= 0 && p_157177_ < shaderTextures.length) {
         shaderTextures[p_157177_] = p_157178_;
      }

   }

   public static int getShaderTexture(int p_157204_) {
      assertOnRenderThread();
      return p_157204_ >= 0 && p_157204_ < shaderTextures.length ? shaderTextures[p_157204_] : 0;
   }

   public static void setProjectionMatrix(Matrix4f p_157426_) {
      Matrix4f matrix4f = p_157426_.copy();
      if (!isOnRenderThread()) {
         recordRenderCall(() -> {
            projectionMatrix = matrix4f;
         });
      } else {
         projectionMatrix = matrix4f;
      }

   }

   public static void setInverseViewRotationMatrix(Matrix3f p_200919_) {
      Matrix3f matrix3f = p_200919_.copy();
      if (!isOnRenderThread()) {
         recordRenderCall(() -> {
            inverseViewRotationMatrix = matrix3f;
         });
      } else {
         inverseViewRotationMatrix = matrix3f;
      }

   }

   public static void setTextureMatrix(Matrix4f p_157460_) {
      Matrix4f matrix4f = p_157460_.copy();
      if (!isOnRenderThread()) {
         recordRenderCall(() -> {
            textureMatrix = matrix4f;
         });
      } else {
         textureMatrix = matrix4f;
      }

   }

   public static void resetTextureMatrix() {
      if (!isOnRenderThread()) {
         recordRenderCall(() -> {
            textureMatrix.setIdentity();
         });
      } else {
         textureMatrix.setIdentity();
      }

   }

   public static void applyModelViewMatrix() {
      Matrix4f matrix4f = modelViewStack.last().pose().copy();
      if (!isOnRenderThread()) {
         recordRenderCall(() -> {
            modelViewMatrix = matrix4f;
         });
      } else {
         modelViewMatrix = matrix4f;
      }

   }

   public static void backupProjectionMatrix() {
      if (!isOnRenderThread()) {
         recordRenderCall(() -> {
            _backupProjectionMatrix();
         });
      } else {
         _backupProjectionMatrix();
      }

   }

   private static void _backupProjectionMatrix() {
      savedProjectionMatrix = projectionMatrix;
   }

   public static void restoreProjectionMatrix() {
      if (!isOnRenderThread()) {
         recordRenderCall(() -> {
            _restoreProjectionMatrix();
         });
      } else {
         _restoreProjectionMatrix();
      }

   }

   private static void _restoreProjectionMatrix() {
      projectionMatrix = savedProjectionMatrix;
   }

   public static Matrix4f getProjectionMatrix() {
      assertOnRenderThread();
      return projectionMatrix;
   }

   public static Matrix3f getInverseViewRotationMatrix() {
      assertOnRenderThread();
      return inverseViewRotationMatrix;
   }

   public static Matrix4f getModelViewMatrix() {
      assertOnRenderThread();
      return modelViewMatrix;
   }

   public static PoseStack getModelViewStack() {
      return modelViewStack;
   }

   public static Matrix4f getTextureMatrix() {
      assertOnRenderThread();
      return textureMatrix;
   }

   public static RenderSystem.AutoStorageIndexBuffer getSequentialBuffer(VertexFormat.Mode p_157194_, int p_157195_) {
      assertOnRenderThread();
      RenderSystem.AutoStorageIndexBuffer rendersystem$autostorageindexbuffer;
      if (p_157194_ == VertexFormat.Mode.QUADS) {
         rendersystem$autostorageindexbuffer = sharedSequentialQuad;
      } else if (p_157194_ == VertexFormat.Mode.LINES) {
         rendersystem$autostorageindexbuffer = sharedSequentialLines;
      } else {
         rendersystem$autostorageindexbuffer = sharedSequential;
      }

      rendersystem$autostorageindexbuffer.ensureStorage(p_157195_);
      return rendersystem$autostorageindexbuffer;
   }

   public static void setShaderGameTime(long p_157448_, float p_157449_) {
      float f = ((float)(p_157448_ % 24000L) + p_157449_) / 24000.0F;
      if (!isOnRenderThread()) {
         recordRenderCall(() -> {
            shaderGameTime = f;
         });
      } else {
         shaderGameTime = f;
      }

   }

   public static float getShaderGameTime() {
      assertOnRenderThread();
      return shaderGameTime;
   }

   static {
      projectionMatrix.setIdentity();
      savedProjectionMatrix.setIdentity();
      modelViewMatrix.setIdentity();
      textureMatrix.setIdentity();
   }

   @OnlyIn(Dist.CLIENT)
   public static final class AutoStorageIndexBuffer {
      private final int vertexStride;
      private final int indexStride;
      private final RenderSystem.AutoStorageIndexBuffer.IndexGenerator generator;
      private int name;
      private VertexFormat.IndexType type = VertexFormat.IndexType.BYTE;
      private int indexCount;

      AutoStorageIndexBuffer(int p_157472_, int p_157473_, RenderSystem.AutoStorageIndexBuffer.IndexGenerator p_157474_) {
         this.vertexStride = p_157472_;
         this.indexStride = p_157473_;
         this.generator = p_157474_;
      }

      void ensureStorage(int p_157477_) {
         if (p_157477_ > this.indexCount) {
            p_157477_ = Mth.roundToward(p_157477_ * 2, this.indexStride);
            RenderSystem.LOGGER.debug("Growing IndexBuffer: Old limit {}, new limit {}.", this.indexCount, p_157477_);
            if (this.name == 0) {
               this.name = GlStateManager._glGenBuffers();
            }

            VertexFormat.IndexType vertexformat$indextype = VertexFormat.IndexType.least(p_157477_);
            int i = Mth.roundToward(p_157477_ * vertexformat$indextype.bytes, 4);
            GlStateManager._glBindBuffer(34963, this.name);
            GlStateManager._glBufferData(34963, (long)i, 35048);
            ByteBuffer bytebuffer = GlStateManager._glMapBuffer(34963, 35001);
            if (bytebuffer == null) {
               throw new RuntimeException("Failed to map GL buffer");
            } else {
               this.type = vertexformat$indextype;
               it.unimi.dsi.fastutil.ints.IntConsumer intconsumer = this.intConsumer(bytebuffer);

               for(int j = 0; j < p_157477_; j += this.indexStride) {
                  this.generator.accept(intconsumer, j * this.vertexStride / this.indexStride);
               }

               GlStateManager._glUnmapBuffer(34963);
               GlStateManager._glBindBuffer(34963, 0);
               this.indexCount = p_157477_;
               BufferUploader.invalidateElementArrayBufferBinding();
            }
         }
      }

      private it.unimi.dsi.fastutil.ints.IntConsumer intConsumer(ByteBuffer p_157479_) {
         switch(this.type) {
         case BYTE:
            return (p_157486_) -> {
               p_157479_.put((byte)p_157486_);
            };
         case SHORT:
            return (p_157482_) -> {
               p_157479_.putShort((short)p_157482_);
            };
         case INT:
         default:
            return p_157479_::putInt;
         }
      }

      public int name() {
         return this.name;
      }

      public VertexFormat.IndexType type() {
         return this.type;
      }

      @OnlyIn(Dist.CLIENT)
      interface IndexGenerator {
         void accept(it.unimi.dsi.fastutil.ints.IntConsumer p_157488_, int p_157489_);
      }
   }
}