package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.logging.LogUtils;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.Version;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.Tlhelp32.MODULEENTRY32W;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import net.minecraft.CrashReportCategory;
import org.slf4j.Logger;

public class NativeModuleLister {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int LANG_MASK = 65535;
   private static final int DEFAULT_LANG = 1033;
   private static final int CODEPAGE_MASK = -65536;
   private static final int DEFAULT_CODEPAGE = 78643200;

   public static List<NativeModuleLister.NativeModuleInfo> listModules() {
      if (!Platform.isWindows()) {
         return ImmutableList.of();
      } else {
         int i = Kernel32.INSTANCE.GetCurrentProcessId();
         Builder<NativeModuleLister.NativeModuleInfo> builder = ImmutableList.builder();

         for(MODULEENTRY32W moduleentry32w : Kernel32Util.getModules(i)) {
            String s = moduleentry32w.szModule();
            Optional<NativeModuleLister.NativeModuleVersion> optional = tryGetVersion(moduleentry32w.szExePath());
            builder.add(new NativeModuleLister.NativeModuleInfo(s, optional));
         }

         return builder.build();
      }
   }

   private static Optional<NativeModuleLister.NativeModuleVersion> tryGetVersion(String p_184674_) {
      try {
         IntByReference intbyreference = new IntByReference();
         int i = Version.INSTANCE.GetFileVersionInfoSize(p_184674_, intbyreference);
         if (i == 0) {
            int i1 = Native.getLastError();
            if (i1 != 1813 && i1 != 1812) {
               throw new Win32Exception(i1);
            } else {
               return Optional.empty();
            }
         } else {
            Pointer pointer = new Memory((long)i);
            if (!Version.INSTANCE.GetFileVersionInfo(p_184674_, 0, i, pointer)) {
               throw new Win32Exception(Native.getLastError());
            } else {
               IntByReference intbyreference1 = new IntByReference();
               Pointer pointer1 = queryVersionValue(pointer, "\\VarFileInfo\\Translation", intbyreference1);
               int[] aint = pointer1.getIntArray(0L, intbyreference1.getValue() / 4);
               OptionalInt optionalint = findLangAndCodepage(aint);
               if (!optionalint.isPresent()) {
                  return Optional.empty();
               } else {
                  int j = optionalint.getAsInt();
                  int k = j & '\uffff';
                  int l = (j & -65536) >> 16;
                  String s = queryVersionString(pointer, langTableKey("FileDescription", k, l), intbyreference1);
                  String s1 = queryVersionString(pointer, langTableKey("CompanyName", k, l), intbyreference1);
                  String s2 = queryVersionString(pointer, langTableKey("FileVersion", k, l), intbyreference1);
                  return Optional.of(new NativeModuleLister.NativeModuleVersion(s, s2, s1));
               }
            }
         }
      } catch (Exception exception) {
         LOGGER.info("Failed to find module info for {}", p_184674_, exception);
         return Optional.empty();
      }
   }

   private static String langTableKey(String p_184676_, int p_184677_, int p_184678_) {
      return String.format("\\StringFileInfo\\%04x%04x\\%s", p_184677_, p_184678_, p_184676_);
   }

   private static OptionalInt findLangAndCodepage(int[] p_184682_) {
      OptionalInt optionalint = OptionalInt.empty();

      for(int i : p_184682_) {
         if ((i & -65536) == 78643200 && (i & '\uffff') == 1033) {
            return OptionalInt.of(i);
         }

         optionalint = OptionalInt.of(i);
      }

      return optionalint;
   }

   private static Pointer queryVersionValue(Pointer p_184670_, String p_184671_, IntByReference p_184672_) {
      PointerByReference pointerbyreference = new PointerByReference();
      if (!Version.INSTANCE.VerQueryValue(p_184670_, p_184671_, pointerbyreference, p_184672_)) {
         throw new UnsupportedOperationException("Can't get version value " + p_184671_);
      } else {
         return pointerbyreference.getValue();
      }
   }

   private static String queryVersionString(Pointer p_184687_, String p_184688_, IntByReference p_184689_) {
      try {
         Pointer pointer = queryVersionValue(p_184687_, p_184688_, p_184689_);
         byte[] abyte = pointer.getByteArray(0L, (p_184689_.getValue() - 1) * 2);
         return new String(abyte, StandardCharsets.UTF_16LE);
      } catch (Exception exception) {
         return "";
      }
   }

   public static void addCrashSection(CrashReportCategory p_184680_) {
      p_184680_.setDetail("Modules", () -> {
         return listModules().stream().sorted(Comparator.comparing((p_184685_) -> {
            return p_184685_.name;
         })).map((p_184668_) -> {
            return "\n\t\t" + p_184668_;
         }).collect(Collectors.joining());
      });
   }

   public static class NativeModuleInfo {
      public final String name;
      public final Optional<NativeModuleLister.NativeModuleVersion> version;

      public NativeModuleInfo(String p_184693_, Optional<NativeModuleLister.NativeModuleVersion> p_184694_) {
         this.name = p_184693_;
         this.version = p_184694_;
      }

      public String toString() {
         return this.version.map((p_184696_) -> {
            return this.name + ":" + p_184696_;
         }).orElse(this.name);
      }
   }

   public static class NativeModuleVersion {
      public final String description;
      public final String version;
      public final String company;

      public NativeModuleVersion(String p_184702_, String p_184703_, String p_184704_) {
         this.description = p_184702_;
         this.version = p_184703_;
         this.company = p_184704_;
      }

      public String toString() {
         return this.description + ":" + this.version + ":" + this.company;
      }
   }
}