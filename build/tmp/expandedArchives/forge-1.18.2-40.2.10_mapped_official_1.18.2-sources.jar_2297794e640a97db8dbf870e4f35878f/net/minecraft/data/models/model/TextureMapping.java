package net.minecraft.data.models.model;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class TextureMapping {
   private final Map<TextureSlot, ResourceLocation> slots = Maps.newHashMap();
   private final Set<TextureSlot> forcedSlots = Sets.newHashSet();

   public TextureMapping put(TextureSlot p_125759_, ResourceLocation p_125760_) {
      this.slots.put(p_125759_, p_125760_);
      return this;
   }

   public TextureMapping putForced(TextureSlot p_176481_, ResourceLocation p_176482_) {
      this.slots.put(p_176481_, p_176482_);
      this.forcedSlots.add(p_176481_);
      return this;
   }

   public Stream<TextureSlot> getForced() {
      return this.forcedSlots.stream();
   }

   public TextureMapping copySlot(TextureSlot p_176478_, TextureSlot p_176479_) {
      this.slots.put(p_176479_, this.slots.get(p_176478_));
      return this;
   }

   public TextureMapping copyForced(TextureSlot p_125774_, TextureSlot p_125775_) {
      this.slots.put(p_125775_, this.slots.get(p_125774_));
      this.forcedSlots.add(p_125775_);
      return this;
   }

   public ResourceLocation get(TextureSlot p_125757_) {
      for(TextureSlot textureslot = p_125757_; textureslot != null; textureslot = textureslot.getParent()) {
         ResourceLocation resourcelocation = this.slots.get(textureslot);
         if (resourcelocation != null) {
            return resourcelocation;
         }
      }

      throw new IllegalStateException("Can't find texture for slot " + p_125757_);
   }

   public TextureMapping copyAndUpdate(TextureSlot p_125786_, ResourceLocation p_125787_) {
      TextureMapping texturemapping = new TextureMapping();
      texturemapping.slots.putAll(this.slots);
      texturemapping.forcedSlots.addAll(this.forcedSlots);
      texturemapping.put(p_125786_, p_125787_);
      return texturemapping;
   }

   public static TextureMapping cube(Block p_125749_) {
      ResourceLocation resourcelocation = getBlockTexture(p_125749_);
      return cube(resourcelocation);
   }

   public static TextureMapping defaultTexture(Block p_125769_) {
      ResourceLocation resourcelocation = getBlockTexture(p_125769_);
      return defaultTexture(resourcelocation);
   }

   public static TextureMapping defaultTexture(ResourceLocation p_125762_) {
      return (new TextureMapping()).put(TextureSlot.TEXTURE, p_125762_);
   }

   public static TextureMapping cube(ResourceLocation p_125777_) {
      return (new TextureMapping()).put(TextureSlot.ALL, p_125777_);
   }

   public static TextureMapping cross(Block p_125781_) {
      return singleSlot(TextureSlot.CROSS, getBlockTexture(p_125781_));
   }

   public static TextureMapping cross(ResourceLocation p_125789_) {
      return singleSlot(TextureSlot.CROSS, p_125789_);
   }

   public static TextureMapping plant(Block p_125791_) {
      return singleSlot(TextureSlot.PLANT, getBlockTexture(p_125791_));
   }

   public static TextureMapping plant(ResourceLocation p_125799_) {
      return singleSlot(TextureSlot.PLANT, p_125799_);
   }

   public static TextureMapping rail(Block p_125801_) {
      return singleSlot(TextureSlot.RAIL, getBlockTexture(p_125801_));
   }

   public static TextureMapping rail(ResourceLocation p_125803_) {
      return singleSlot(TextureSlot.RAIL, p_125803_);
   }

   public static TextureMapping wool(Block p_125805_) {
      return singleSlot(TextureSlot.WOOL, getBlockTexture(p_125805_));
   }

   public static TextureMapping wool(ResourceLocation p_176487_) {
      return singleSlot(TextureSlot.WOOL, p_176487_);
   }

   public static TextureMapping stem(Block p_125807_) {
      return singleSlot(TextureSlot.STEM, getBlockTexture(p_125807_));
   }

   public static TextureMapping attachedStem(Block p_125751_, Block p_125752_) {
      return (new TextureMapping()).put(TextureSlot.STEM, getBlockTexture(p_125751_)).put(TextureSlot.UPPER_STEM, getBlockTexture(p_125752_));
   }

   public static TextureMapping pattern(Block p_125811_) {
      return singleSlot(TextureSlot.PATTERN, getBlockTexture(p_125811_));
   }

   public static TextureMapping fan(Block p_125815_) {
      return singleSlot(TextureSlot.FAN, getBlockTexture(p_125815_));
   }

   public static TextureMapping crop(ResourceLocation p_125809_) {
      return singleSlot(TextureSlot.CROP, p_125809_);
   }

   public static TextureMapping pane(Block p_125771_, Block p_125772_) {
      return (new TextureMapping()).put(TextureSlot.PANE, getBlockTexture(p_125771_)).put(TextureSlot.EDGE, getBlockTexture(p_125772_, "_top"));
   }

   public static TextureMapping singleSlot(TextureSlot p_125796_, ResourceLocation p_125797_) {
      return (new TextureMapping()).put(p_125796_, p_125797_);
   }

   public static TextureMapping column(Block p_125819_) {
      return (new TextureMapping()).put(TextureSlot.SIDE, getBlockTexture(p_125819_, "_side")).put(TextureSlot.END, getBlockTexture(p_125819_, "_top"));
   }

   public static TextureMapping cubeTop(Block p_125823_) {
      return (new TextureMapping()).put(TextureSlot.SIDE, getBlockTexture(p_125823_, "_side")).put(TextureSlot.TOP, getBlockTexture(p_125823_, "_top"));
   }

   public static TextureMapping logColumn(Block p_125825_) {
      return (new TextureMapping()).put(TextureSlot.SIDE, getBlockTexture(p_125825_)).put(TextureSlot.END, getBlockTexture(p_125825_, "_top"));
   }

   public static TextureMapping column(ResourceLocation p_125764_, ResourceLocation p_125765_) {
      return (new TextureMapping()).put(TextureSlot.SIDE, p_125764_).put(TextureSlot.END, p_125765_);
   }

   public static TextureMapping cubeBottomTop(Block p_125827_) {
      return (new TextureMapping()).put(TextureSlot.SIDE, getBlockTexture(p_125827_, "_side")).put(TextureSlot.TOP, getBlockTexture(p_125827_, "_top")).put(TextureSlot.BOTTOM, getBlockTexture(p_125827_, "_bottom"));
   }

   public static TextureMapping cubeBottomTopWithWall(Block p_125829_) {
      ResourceLocation resourcelocation = getBlockTexture(p_125829_);
      return (new TextureMapping()).put(TextureSlot.WALL, resourcelocation).put(TextureSlot.SIDE, resourcelocation).put(TextureSlot.TOP, getBlockTexture(p_125829_, "_top")).put(TextureSlot.BOTTOM, getBlockTexture(p_125829_, "_bottom"));
   }

   public static TextureMapping columnWithWall(Block p_125831_) {
      ResourceLocation resourcelocation = getBlockTexture(p_125831_);
      return (new TextureMapping()).put(TextureSlot.WALL, resourcelocation).put(TextureSlot.SIDE, resourcelocation).put(TextureSlot.END, getBlockTexture(p_125831_, "_top"));
   }

   public static TextureMapping door(ResourceLocation p_176484_, ResourceLocation p_176485_) {
      return (new TextureMapping()).put(TextureSlot.TOP, p_176484_).put(TextureSlot.BOTTOM, p_176485_);
   }

   public static TextureMapping door(Block p_125833_) {
      return (new TextureMapping()).put(TextureSlot.TOP, getBlockTexture(p_125833_, "_top")).put(TextureSlot.BOTTOM, getBlockTexture(p_125833_, "_bottom"));
   }

   public static TextureMapping particle(Block p_125835_) {
      return (new TextureMapping()).put(TextureSlot.PARTICLE, getBlockTexture(p_125835_));
   }

   public static TextureMapping particle(ResourceLocation p_125813_) {
      return (new TextureMapping()).put(TextureSlot.PARTICLE, p_125813_);
   }

   public static TextureMapping fire0(Block p_125837_) {
      return (new TextureMapping()).put(TextureSlot.FIRE, getBlockTexture(p_125837_, "_0"));
   }

   public static TextureMapping fire1(Block p_125839_) {
      return (new TextureMapping()).put(TextureSlot.FIRE, getBlockTexture(p_125839_, "_1"));
   }

   public static TextureMapping lantern(Block p_125841_) {
      return (new TextureMapping()).put(TextureSlot.LANTERN, getBlockTexture(p_125841_));
   }

   public static TextureMapping torch(Block p_125843_) {
      return (new TextureMapping()).put(TextureSlot.TORCH, getBlockTexture(p_125843_));
   }

   public static TextureMapping torch(ResourceLocation p_125817_) {
      return (new TextureMapping()).put(TextureSlot.TORCH, p_125817_);
   }

   public static TextureMapping particleFromItem(Item p_125744_) {
      return (new TextureMapping()).put(TextureSlot.PARTICLE, getItemTexture(p_125744_));
   }

   public static TextureMapping commandBlock(Block p_125845_) {
      return (new TextureMapping()).put(TextureSlot.SIDE, getBlockTexture(p_125845_, "_side")).put(TextureSlot.FRONT, getBlockTexture(p_125845_, "_front")).put(TextureSlot.BACK, getBlockTexture(p_125845_, "_back"));
   }

   public static TextureMapping orientableCube(Block p_125847_) {
      return (new TextureMapping()).put(TextureSlot.SIDE, getBlockTexture(p_125847_, "_side")).put(TextureSlot.FRONT, getBlockTexture(p_125847_, "_front")).put(TextureSlot.TOP, getBlockTexture(p_125847_, "_top")).put(TextureSlot.BOTTOM, getBlockTexture(p_125847_, "_bottom"));
   }

   public static TextureMapping orientableCubeOnlyTop(Block p_125849_) {
      return (new TextureMapping()).put(TextureSlot.SIDE, getBlockTexture(p_125849_, "_side")).put(TextureSlot.FRONT, getBlockTexture(p_125849_, "_front")).put(TextureSlot.TOP, getBlockTexture(p_125849_, "_top"));
   }

   public static TextureMapping orientableCubeSameEnds(Block p_125851_) {
      return (new TextureMapping()).put(TextureSlot.SIDE, getBlockTexture(p_125851_, "_side")).put(TextureSlot.FRONT, getBlockTexture(p_125851_, "_front")).put(TextureSlot.END, getBlockTexture(p_125851_, "_end"));
   }

   public static TextureMapping top(Block p_125853_) {
      return (new TextureMapping()).put(TextureSlot.TOP, getBlockTexture(p_125853_, "_top"));
   }

   public static TextureMapping craftingTable(Block p_125783_, Block p_125784_) {
      return (new TextureMapping()).put(TextureSlot.PARTICLE, getBlockTexture(p_125783_, "_front")).put(TextureSlot.DOWN, getBlockTexture(p_125784_)).put(TextureSlot.UP, getBlockTexture(p_125783_, "_top")).put(TextureSlot.NORTH, getBlockTexture(p_125783_, "_front")).put(TextureSlot.EAST, getBlockTexture(p_125783_, "_side")).put(TextureSlot.SOUTH, getBlockTexture(p_125783_, "_side")).put(TextureSlot.WEST, getBlockTexture(p_125783_, "_front"));
   }

   public static TextureMapping fletchingTable(Block p_125793_, Block p_125794_) {
      return (new TextureMapping()).put(TextureSlot.PARTICLE, getBlockTexture(p_125793_, "_front")).put(TextureSlot.DOWN, getBlockTexture(p_125794_)).put(TextureSlot.UP, getBlockTexture(p_125793_, "_top")).put(TextureSlot.NORTH, getBlockTexture(p_125793_, "_front")).put(TextureSlot.SOUTH, getBlockTexture(p_125793_, "_front")).put(TextureSlot.EAST, getBlockTexture(p_125793_, "_side")).put(TextureSlot.WEST, getBlockTexture(p_125793_, "_side"));
   }

   public static TextureMapping campfire(Block p_125737_) {
      return (new TextureMapping()).put(TextureSlot.LIT_LOG, getBlockTexture(p_125737_, "_log_lit")).put(TextureSlot.FIRE, getBlockTexture(p_125737_, "_fire"));
   }

   public static TextureMapping candleCake(Block p_181477_, boolean p_181478_) {
      return (new TextureMapping()).put(TextureSlot.PARTICLE, getBlockTexture(Blocks.CAKE, "_side")).put(TextureSlot.BOTTOM, getBlockTexture(Blocks.CAKE, "_bottom")).put(TextureSlot.TOP, getBlockTexture(Blocks.CAKE, "_top")).put(TextureSlot.SIDE, getBlockTexture(Blocks.CAKE, "_side")).put(TextureSlot.CANDLE, getBlockTexture(p_181477_, p_181478_ ? "_lit" : ""));
   }

   public static TextureMapping cauldron(ResourceLocation p_176489_) {
      return (new TextureMapping()).put(TextureSlot.PARTICLE, getBlockTexture(Blocks.CAULDRON, "_side")).put(TextureSlot.SIDE, getBlockTexture(Blocks.CAULDRON, "_side")).put(TextureSlot.TOP, getBlockTexture(Blocks.CAULDRON, "_top")).put(TextureSlot.BOTTOM, getBlockTexture(Blocks.CAULDRON, "_bottom")).put(TextureSlot.INSIDE, getBlockTexture(Blocks.CAULDRON, "_inner")).put(TextureSlot.CONTENT, p_176489_);
   }

   public static TextureMapping layer0(Item p_125767_) {
      return (new TextureMapping()).put(TextureSlot.LAYER0, getItemTexture(p_125767_));
   }

   public static TextureMapping layer0(Block p_125739_) {
      return (new TextureMapping()).put(TextureSlot.LAYER0, getBlockTexture(p_125739_));
   }

   public static TextureMapping layer0(ResourceLocation p_125821_) {
      return (new TextureMapping()).put(TextureSlot.LAYER0, p_125821_);
   }

   public static ResourceLocation getBlockTexture(Block p_125741_) {
      ResourceLocation resourcelocation = Registry.BLOCK.getKey(p_125741_);
      return new ResourceLocation(resourcelocation.getNamespace(), "block/" + resourcelocation.getPath());
   }

   public static ResourceLocation getBlockTexture(Block p_125754_, String p_125755_) {
      ResourceLocation resourcelocation = Registry.BLOCK.getKey(p_125754_);
      return new ResourceLocation(resourcelocation.getNamespace(), "block/" + resourcelocation.getPath() + p_125755_);
   }

   public static ResourceLocation getItemTexture(Item p_125779_) {
      ResourceLocation resourcelocation = Registry.ITEM.getKey(p_125779_);
      return new ResourceLocation(resourcelocation.getNamespace(), "item/" + resourcelocation.getPath());
   }

   public static ResourceLocation getItemTexture(Item p_125746_, String p_125747_) {
      ResourceLocation resourcelocation = Registry.ITEM.getKey(p_125746_);
      return new ResourceLocation(resourcelocation.getNamespace(), "item/" + resourcelocation.getPath() + p_125747_);
   }
}