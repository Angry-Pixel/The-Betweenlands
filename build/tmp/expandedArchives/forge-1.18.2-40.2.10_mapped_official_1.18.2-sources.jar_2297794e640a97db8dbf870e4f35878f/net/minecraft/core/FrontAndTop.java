package net.minecraft.core;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.util.StringRepresentable;

public enum FrontAndTop implements StringRepresentable {
   DOWN_EAST("down_east", Direction.DOWN, Direction.EAST),
   DOWN_NORTH("down_north", Direction.DOWN, Direction.NORTH),
   DOWN_SOUTH("down_south", Direction.DOWN, Direction.SOUTH),
   DOWN_WEST("down_west", Direction.DOWN, Direction.WEST),
   UP_EAST("up_east", Direction.UP, Direction.EAST),
   UP_NORTH("up_north", Direction.UP, Direction.NORTH),
   UP_SOUTH("up_south", Direction.UP, Direction.SOUTH),
   UP_WEST("up_west", Direction.UP, Direction.WEST),
   WEST_UP("west_up", Direction.WEST, Direction.UP),
   EAST_UP("east_up", Direction.EAST, Direction.UP),
   NORTH_UP("north_up", Direction.NORTH, Direction.UP),
   SOUTH_UP("south_up", Direction.SOUTH, Direction.UP);

   private static final Int2ObjectMap<FrontAndTop> LOOKUP_TOP_FRONT = Util.make(new Int2ObjectOpenHashMap<>(values().length), (p_175377_) -> {
      for(FrontAndTop frontandtop : values()) {
         p_175377_.put(lookupKey(frontandtop.front, frontandtop.top), frontandtop);
      }

   });
   private final String name;
   private final Direction top;
   private final Direction front;

   private static int lookupKey(Direction p_122627_, Direction p_122628_) {
      return p_122628_.ordinal() << 3 | p_122627_.ordinal();
   }

   private FrontAndTop(String p_122618_, Direction p_122619_, Direction p_122620_) {
      this.name = p_122618_;
      this.front = p_122619_;
      this.top = p_122620_;
   }

   public String getSerializedName() {
      return this.name;
   }

   public static FrontAndTop fromFrontAndTop(Direction p_122623_, Direction p_122624_) {
      int i = lookupKey(p_122623_, p_122624_);
      return LOOKUP_TOP_FRONT.get(i);
   }

   public Direction front() {
      return this.front;
   }

   public Direction top() {
      return this.top;
   }
}