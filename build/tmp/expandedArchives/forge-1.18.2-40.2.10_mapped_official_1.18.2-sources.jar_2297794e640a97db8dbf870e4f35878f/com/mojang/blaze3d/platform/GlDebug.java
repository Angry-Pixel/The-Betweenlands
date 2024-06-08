package com.mojang.blaze3d.platform;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Queue;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.KHRDebug;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GlDebug {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int CIRCULAR_LOG_SIZE = 10;
   private static final Queue<GlDebug.LogEntry> MESSAGE_BUFFER = EvictingQueue.create(10);
   @Nullable
   private static volatile GlDebug.LogEntry lastEntry;
   private static final List<Integer> DEBUG_LEVELS = ImmutableList.of(37190, 37191, 37192, 33387);
   private static final List<Integer> DEBUG_LEVELS_ARB = ImmutableList.of(37190, 37191, 37192);
   private static boolean debugEnabled;

   private static String printUnknownToken(int p_84037_) {
      return "Unknown (0x" + Integer.toHexString(p_84037_).toUpperCase() + ")";
   }

   public static String sourceToString(int p_84056_) {
      switch(p_84056_) {
      case 33350:
         return "API";
      case 33351:
         return "WINDOW SYSTEM";
      case 33352:
         return "SHADER COMPILER";
      case 33353:
         return "THIRD PARTY";
      case 33354:
         return "APPLICATION";
      case 33355:
         return "OTHER";
      default:
         return printUnknownToken(p_84056_);
      }
   }

   public static String typeToString(int p_84058_) {
      switch(p_84058_) {
      case 33356:
         return "ERROR";
      case 33357:
         return "DEPRECATED BEHAVIOR";
      case 33358:
         return "UNDEFINED BEHAVIOR";
      case 33359:
         return "PORTABILITY";
      case 33360:
         return "PERFORMANCE";
      case 33361:
         return "OTHER";
      case 33384:
         return "MARKER";
      default:
         return printUnknownToken(p_84058_);
      }
   }

   public static String severityToString(int p_84060_) {
      switch(p_84060_) {
      case 33387:
         return "NOTIFICATION";
      case 37190:
         return "HIGH";
      case 37191:
         return "MEDIUM";
      case 37192:
         return "LOW";
      default:
         return printUnknownToken(p_84060_);
      }
   }

   private static void printDebugLog(int p_84039_, int p_84040_, int p_84041_, int p_84042_, int p_84043_, long p_84044_, long p_84045_) {
      String s = GLDebugMessageCallback.getMessage(p_84043_, p_84044_);
      GlDebug.LogEntry gldebug$logentry;
      synchronized(MESSAGE_BUFFER) {
         gldebug$logentry = lastEntry;
         if (gldebug$logentry != null && gldebug$logentry.isSame(p_84039_, p_84040_, p_84041_, p_84042_, s)) {
            ++gldebug$logentry.count;
         } else {
            gldebug$logentry = new GlDebug.LogEntry(p_84039_, p_84040_, p_84041_, p_84042_, s);
            MESSAGE_BUFFER.add(gldebug$logentry);
            lastEntry = gldebug$logentry;
         }
      }

      LOGGER.info("OpenGL debug message: {}", (Object)gldebug$logentry);
   }

   public static List<String> getLastOpenGlDebugMessages() {
      synchronized(MESSAGE_BUFFER) {
         List<String> list = Lists.newArrayListWithCapacity(MESSAGE_BUFFER.size());

         for(GlDebug.LogEntry gldebug$logentry : MESSAGE_BUFFER) {
            list.add(gldebug$logentry + " x " + gldebug$logentry.count);
         }

         return list;
      }
   }

   public static boolean isDebugEnabled() {
      return debugEnabled;
   }

   public static void enableDebugCallback(int p_84050_, boolean p_84051_) {
      RenderSystem.assertInInitPhase();
      if (p_84050_ > 0) {
         GLCapabilities glcapabilities = GL.getCapabilities();
         if (glcapabilities.GL_KHR_debug) {
            debugEnabled = true;
            GL11.glEnable(37600);
            if (p_84051_) {
               GL11.glEnable(33346);
            }

            for(int i = 0; i < DEBUG_LEVELS.size(); ++i) {
               boolean flag = i < p_84050_;
               KHRDebug.glDebugMessageControl(4352, 4352, DEBUG_LEVELS.get(i), (int[])null, flag);
            }

            KHRDebug.glDebugMessageCallback(GLX.make(GLDebugMessageCallback.create(GlDebug::printDebugLog), DebugMemoryUntracker::untrack), 0L);
         } else if (glcapabilities.GL_ARB_debug_output) {
            debugEnabled = true;
            if (p_84051_) {
               GL11.glEnable(33346);
            }

            for(int j = 0; j < DEBUG_LEVELS_ARB.size(); ++j) {
               boolean flag1 = j < p_84050_;
               ARBDebugOutput.glDebugMessageControlARB(4352, 4352, DEBUG_LEVELS_ARB.get(j), (int[])null, flag1);
            }

            ARBDebugOutput.glDebugMessageCallbackARB(GLX.make(GLDebugMessageARBCallback.create(GlDebug::printDebugLog), DebugMemoryUntracker::untrack), 0L);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   static class LogEntry {
      private final int id;
      private final int source;
      private final int type;
      private final int severity;
      private final String message;
      int count = 1;

      LogEntry(int p_166234_, int p_166235_, int p_166236_, int p_166237_, String p_166238_) {
         this.id = p_166236_;
         this.source = p_166234_;
         this.type = p_166235_;
         this.severity = p_166237_;
         this.message = p_166238_;
      }

      boolean isSame(int p_166240_, int p_166241_, int p_166242_, int p_166243_, String p_166244_) {
         return p_166241_ == this.type && p_166240_ == this.source && p_166242_ == this.id && p_166243_ == this.severity && p_166244_.equals(this.message);
      }

      public String toString() {
         return "id=" + this.id + ", source=" + GlDebug.sourceToString(this.source) + ", type=" + GlDebug.typeToString(this.type) + ", severity=" + GlDebug.severityToString(this.severity) + ", message='" + this.message + "'";
      }
   }
}