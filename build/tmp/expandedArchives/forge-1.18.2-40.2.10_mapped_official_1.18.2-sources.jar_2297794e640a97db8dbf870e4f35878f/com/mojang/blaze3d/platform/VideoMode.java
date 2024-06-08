package com.mojang.blaze3d.platform;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

@OnlyIn(Dist.CLIENT)
public final class VideoMode {
   private final int width;
   private final int height;
   private final int redBits;
   private final int greenBits;
   private final int blueBits;
   private final int refreshRate;
   private static final Pattern PATTERN = Pattern.compile("(\\d+)x(\\d+)(?:@(\\d+)(?::(\\d+))?)?");

   public VideoMode(int p_85322_, int p_85323_, int p_85324_, int p_85325_, int p_85326_, int p_85327_) {
      this.width = p_85322_;
      this.height = p_85323_;
      this.redBits = p_85324_;
      this.greenBits = p_85325_;
      this.blueBits = p_85326_;
      this.refreshRate = p_85327_;
   }

   public VideoMode(Buffer p_85329_) {
      this.width = p_85329_.width();
      this.height = p_85329_.height();
      this.redBits = p_85329_.redBits();
      this.greenBits = p_85329_.greenBits();
      this.blueBits = p_85329_.blueBits();
      this.refreshRate = p_85329_.refreshRate();
   }

   public VideoMode(GLFWVidMode p_85331_) {
      this.width = p_85331_.width();
      this.height = p_85331_.height();
      this.redBits = p_85331_.redBits();
      this.greenBits = p_85331_.greenBits();
      this.blueBits = p_85331_.blueBits();
      this.refreshRate = p_85331_.refreshRate();
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getRedBits() {
      return this.redBits;
   }

   public int getGreenBits() {
      return this.greenBits;
   }

   public int getBlueBits() {
      return this.blueBits;
   }

   public int getRefreshRate() {
      return this.refreshRate;
   }

   public boolean equals(Object p_85340_) {
      if (this == p_85340_) {
         return true;
      } else if (p_85340_ != null && this.getClass() == p_85340_.getClass()) {
         VideoMode videomode = (VideoMode)p_85340_;
         return this.width == videomode.width && this.height == videomode.height && this.redBits == videomode.redBits && this.greenBits == videomode.greenBits && this.blueBits == videomode.blueBits && this.refreshRate == videomode.refreshRate;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(this.width, this.height, this.redBits, this.greenBits, this.blueBits, this.refreshRate);
   }

   public String toString() {
      return String.format("%sx%s@%s (%sbit)", this.width, this.height, this.refreshRate, this.redBits + this.greenBits + this.blueBits);
   }

   public static Optional<VideoMode> read(@Nullable String p_85334_) {
      if (p_85334_ == null) {
         return Optional.empty();
      } else {
         try {
            Matcher matcher = PATTERN.matcher(p_85334_);
            if (matcher.matches()) {
               int i = Integer.parseInt(matcher.group(1));
               int j = Integer.parseInt(matcher.group(2));
               String s = matcher.group(3);
               int k;
               if (s == null) {
                  k = 60;
               } else {
                  k = Integer.parseInt(s);
               }

               String s1 = matcher.group(4);
               int l;
               if (s1 == null) {
                  l = 24;
               } else {
                  l = Integer.parseInt(s1);
               }

               int i1 = l / 3;
               return Optional.of(new VideoMode(i, j, i1, i1, i1, k));
            }
         } catch (Exception exception) {
         }

         return Optional.empty();
      }
   }

   public String write() {
      return String.format("%sx%s@%s:%s", this.width, this.height, this.refreshRate, this.redBits + this.greenBits + this.blueBits);
   }
}