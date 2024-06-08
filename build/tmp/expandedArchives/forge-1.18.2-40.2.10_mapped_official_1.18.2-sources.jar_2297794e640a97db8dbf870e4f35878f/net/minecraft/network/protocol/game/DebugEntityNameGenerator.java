package net.minecraft.network.protocol.game;

import java.util.Random;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class DebugEntityNameGenerator {
   private static final String[] NAMES_FIRST_PART = new String[]{"Slim", "Far", "River", "Silly", "Fat", "Thin", "Fish", "Bat", "Dark", "Oak", "Sly", "Bush", "Zen", "Bark", "Cry", "Slack", "Soup", "Grim", "Hook", "Dirt", "Mud", "Sad", "Hard", "Crook", "Sneak", "Stink", "Weird", "Fire", "Soot", "Soft", "Rough", "Cling", "Scar"};
   private static final String[] NAMES_SECOND_PART = new String[]{"Fox", "Tail", "Jaw", "Whisper", "Twig", "Root", "Finder", "Nose", "Brow", "Blade", "Fry", "Seek", "Wart", "Tooth", "Foot", "Leaf", "Stone", "Fall", "Face", "Tongue", "Voice", "Lip", "Mouth", "Snail", "Toe", "Ear", "Hair", "Beard", "Shirt", "Fist"};

   public static String getEntityName(Entity p_179487_) {
      if (p_179487_ instanceof Player) {
         return p_179487_.getName().getString();
      } else {
         Component component = p_179487_.getCustomName();
         return component != null ? component.getString() : getEntityName(p_179487_.getUUID());
      }
   }

   public static String getEntityName(UUID p_133669_) {
      Random random = getRandom(p_133669_);
      return getRandomString(random, NAMES_FIRST_PART) + getRandomString(random, NAMES_SECOND_PART);
   }

   private static String getRandomString(Random p_133666_, String[] p_133667_) {
      return Util.getRandom(p_133667_, p_133666_);
   }

   private static Random getRandom(UUID p_133671_) {
      return new Random((long)(p_133671_.hashCode() >> 2));
   }
}