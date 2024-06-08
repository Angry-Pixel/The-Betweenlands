package com.mojang.blaze3d.audio;

import com.mojang.logging.LogUtils;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class OpenAlUtil {
   private static final Logger LOGGER = LogUtils.getLogger();

   private static String alErrorToString(int p_83783_) {
      switch(p_83783_) {
      case 40961:
         return "Invalid name parameter.";
      case 40962:
         return "Invalid enumerated parameter value.";
      case 40963:
         return "Invalid parameter parameter value.";
      case 40964:
         return "Invalid operation.";
      case 40965:
         return "Unable to allocate memory.";
      default:
         return "An unrecognized error occurred.";
      }
   }

   static boolean checkALError(String p_83788_) {
      int i = AL10.alGetError();
      if (i != 0) {
         LOGGER.error("{}: {}", p_83788_, alErrorToString(i));
         return true;
      } else {
         return false;
      }
   }

   private static String alcErrorToString(int p_83792_) {
      switch(p_83792_) {
      case 40961:
         return "Invalid device.";
      case 40962:
         return "Invalid context.";
      case 40963:
         return "Illegal enum.";
      case 40964:
         return "Invalid value.";
      case 40965:
         return "Unable to allocate memory.";
      default:
         return "An unrecognized error occurred.";
      }
   }

   static boolean checkALCError(long p_83785_, String p_83786_) {
      int i = ALC10.alcGetError(p_83785_);
      if (i != 0) {
         LOGGER.error("{}{}: {}", p_83786_, p_83785_, alcErrorToString(i));
         return true;
      } else {
         return false;
      }
   }

   static int audioFormatToOpenAl(AudioFormat p_83790_) {
      Encoding encoding = p_83790_.getEncoding();
      int i = p_83790_.getChannels();
      int j = p_83790_.getSampleSizeInBits();
      if (encoding.equals(Encoding.PCM_UNSIGNED) || encoding.equals(Encoding.PCM_SIGNED)) {
         if (i == 1) {
            if (j == 8) {
               return 4352;
            }

            if (j == 16) {
               return 4353;
            }
         } else if (i == 2) {
            if (j == 8) {
               return 4354;
            }

            if (j == 16) {
               return 4355;
            }
         }
      }

      throw new IllegalArgumentException("Invalid audio format: " + p_83790_);
   }
}