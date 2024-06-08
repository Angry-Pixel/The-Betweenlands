package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CelebrateVillagersSurvivedRaid extends Behavior<Villager> {
   @Nullable
   private Raid currentRaid;

   public CelebrateVillagersSurvivedRaid(int p_22684_, int p_22685_) {
      super(ImmutableMap.of(), p_22684_, p_22685_);
   }

   protected boolean checkExtraStartConditions(ServerLevel p_22690_, Villager p_22691_) {
      BlockPos blockpos = p_22691_.blockPosition();
      this.currentRaid = p_22690_.getRaidAt(blockpos);
      return this.currentRaid != null && this.currentRaid.isVictory() && MoveToSkySeeingSpot.hasNoBlocksAbove(p_22690_, p_22691_, blockpos);
   }

   protected boolean canStillUse(ServerLevel p_22693_, Villager p_22694_, long p_22695_) {
      return this.currentRaid != null && !this.currentRaid.isStopped();
   }

   protected void stop(ServerLevel p_22704_, Villager p_22705_, long p_22706_) {
      this.currentRaid = null;
      p_22705_.getBrain().updateActivityFromSchedule(p_22704_.getDayTime(), p_22704_.getGameTime());
   }

   protected void tick(ServerLevel p_22712_, Villager p_22713_, long p_22714_) {
      Random random = p_22713_.getRandom();
      if (random.nextInt(100) == 0) {
         p_22713_.playCelebrateSound();
      }

      if (random.nextInt(200) == 0 && MoveToSkySeeingSpot.hasNoBlocksAbove(p_22712_, p_22713_, p_22713_.blockPosition())) {
         DyeColor dyecolor = Util.getRandom(DyeColor.values(), random);
         int i = random.nextInt(3);
         ItemStack itemstack = this.getFirework(dyecolor, i);
         FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(p_22713_.level, p_22713_, p_22713_.getX(), p_22713_.getEyeY(), p_22713_.getZ(), itemstack);
         p_22713_.level.addFreshEntity(fireworkrocketentity);
      }

   }

   private ItemStack getFirework(DyeColor p_22697_, int p_22698_) {
      ItemStack itemstack = new ItemStack(Items.FIREWORK_ROCKET, 1);
      ItemStack itemstack1 = new ItemStack(Items.FIREWORK_STAR);
      CompoundTag compoundtag = itemstack1.getOrCreateTagElement("Explosion");
      List<Integer> list = Lists.newArrayList();
      list.add(p_22697_.getFireworkColor());
      compoundtag.putIntArray("Colors", list);
      compoundtag.putByte("Type", (byte)FireworkRocketItem.Shape.BURST.getId());
      CompoundTag compoundtag1 = itemstack.getOrCreateTagElement("Fireworks");
      ListTag listtag = new ListTag();
      CompoundTag compoundtag2 = itemstack1.getTagElement("Explosion");
      if (compoundtag2 != null) {
         listtag.add(compoundtag2);
      }

      compoundtag1.putByte("Flight", (byte)p_22698_);
      if (!listtag.isEmpty()) {
         compoundtag1.put("Explosions", listtag);
      }

      return itemstack;
   }
}