package net.minecraft.world.entity.decoration;

import net.minecraft.core.Registry;

public class Motive extends net.minecraftforge.registries.ForgeRegistryEntry<Motive> {
   public static final Motive KEBAB = register("kebab", 16, 16);
   public static final Motive AZTEC = register("aztec", 16, 16);
   public static final Motive ALBAN = register("alban", 16, 16);
   public static final Motive AZTEC2 = register("aztec2", 16, 16);
   public static final Motive BOMB = register("bomb", 16, 16);
   public static final Motive PLANT = register("plant", 16, 16);
   public static final Motive WASTELAND = register("wasteland", 16, 16);
   public static final Motive POOL = register("pool", 32, 16);
   public static final Motive COURBET = register("courbet", 32, 16);
   public static final Motive SEA = register("sea", 32, 16);
   public static final Motive SUNSET = register("sunset", 32, 16);
   public static final Motive CREEBET = register("creebet", 32, 16);
   public static final Motive WANDERER = register("wanderer", 16, 32);
   public static final Motive GRAHAM = register("graham", 16, 32);
   public static final Motive MATCH = register("match", 32, 32);
   public static final Motive BUST = register("bust", 32, 32);
   public static final Motive STAGE = register("stage", 32, 32);
   public static final Motive VOID = register("void", 32, 32);
   public static final Motive SKULL_AND_ROSES = register("skull_and_roses", 32, 32);
   public static final Motive WITHER = register("wither", 32, 32);
   public static final Motive FIGHTERS = register("fighters", 64, 32);
   public static final Motive POINTER = register("pointer", 64, 64);
   public static final Motive PIGSCENE = register("pigscene", 64, 64);
   public static final Motive BURNING_SKULL = register("burning_skull", 64, 64);
   public static final Motive SKELETON = register("skeleton", 64, 48);
   public static final Motive DONKEY_KONG = register("donkey_kong", 64, 48);
   private final int width;
   private final int height;

   private static Motive register(String p_31898_, int p_31899_, int p_31900_) {
      return Registry.register(Registry.MOTIVE, p_31898_, new Motive(p_31899_, p_31900_));
   }

   public Motive(int p_31894_, int p_31895_) {
      this.width = p_31894_;
      this.height = p_31895_;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }
}
