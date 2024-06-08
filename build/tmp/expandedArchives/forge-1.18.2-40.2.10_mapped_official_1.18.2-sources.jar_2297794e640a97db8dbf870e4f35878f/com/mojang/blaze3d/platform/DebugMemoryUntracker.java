package com.mojang.blaze3d.platform;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.Pointer;

@OnlyIn(Dist.CLIENT)
public class DebugMemoryUntracker {
   @Nullable
   private static final MethodHandle UNTRACK = GLX.make(() -> {
      try {
         Lookup lookup = MethodHandles.lookup();
         Class<?> oclass = Class.forName("org.lwjgl.system.MemoryManage$DebugAllocator");
         Method method = oclass.getDeclaredMethod("untrack", Long.TYPE);
         method.setAccessible(true);
         Field field = Class.forName("org.lwjgl.system.MemoryUtil$LazyInit").getDeclaredField("ALLOCATOR");
         field.setAccessible(true);
         Object object = field.get((Object)null);
         return oclass.isInstance(object) ? lookup.unreflect(method) : null;
      } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | ClassNotFoundException classnotfoundexception) {
         throw new RuntimeException(classnotfoundexception);
      }
   });

   public static void untrack(long p_84002_) {
      if (UNTRACK != null) {
         try {
            UNTRACK.invoke(p_84002_);
         } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
         }
      }
   }

   public static void untrack(Pointer p_84004_) {
      untrack(p_84004_.address());
   }
}