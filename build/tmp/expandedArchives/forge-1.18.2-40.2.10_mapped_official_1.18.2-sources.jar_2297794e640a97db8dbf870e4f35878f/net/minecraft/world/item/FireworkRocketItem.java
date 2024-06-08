package net.minecraft.world.item;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FireworkRocketItem extends Item {
   public static final String TAG_FIREWORKS = "Fireworks";
   public static final String TAG_EXPLOSION = "Explosion";
   public static final String TAG_EXPLOSIONS = "Explosions";
   public static final String TAG_FLIGHT = "Flight";
   public static final String TAG_EXPLOSION_TYPE = "Type";
   public static final String TAG_EXPLOSION_TRAIL = "Trail";
   public static final String TAG_EXPLOSION_FLICKER = "Flicker";
   public static final String TAG_EXPLOSION_COLORS = "Colors";
   public static final String TAG_EXPLOSION_FADECOLORS = "FadeColors";
   public static final double ROCKET_PLACEMENT_OFFSET = 0.15D;

   public FireworkRocketItem(Item.Properties p_41209_) {
      super(p_41209_);
   }

   public InteractionResult useOn(UseOnContext p_41216_) {
      Level level = p_41216_.getLevel();
      if (!level.isClientSide) {
         ItemStack itemstack = p_41216_.getItemInHand();
         Vec3 vec3 = p_41216_.getClickLocation();
         Direction direction = p_41216_.getClickedFace();
         FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(level, p_41216_.getPlayer(), vec3.x + (double)direction.getStepX() * 0.15D, vec3.y + (double)direction.getStepY() * 0.15D, vec3.z + (double)direction.getStepZ() * 0.15D, itemstack);
         level.addFreshEntity(fireworkrocketentity);
         itemstack.shrink(1);
      }

      return InteractionResult.sidedSuccess(level.isClientSide);
   }

   public InteractionResultHolder<ItemStack> use(Level p_41218_, Player p_41219_, InteractionHand p_41220_) {
      if (p_41219_.isFallFlying()) {
         ItemStack itemstack = p_41219_.getItemInHand(p_41220_);
         if (!p_41218_.isClientSide) {
            FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(p_41218_, itemstack, p_41219_);
            p_41218_.addFreshEntity(fireworkrocketentity);
            if (!p_41219_.getAbilities().instabuild) {
               itemstack.shrink(1);
            }

            p_41219_.awardStat(Stats.ITEM_USED.get(this));
         }

         return InteractionResultHolder.sidedSuccess(p_41219_.getItemInHand(p_41220_), p_41218_.isClientSide());
      } else {
         return InteractionResultHolder.pass(p_41219_.getItemInHand(p_41220_));
      }
   }

   public void appendHoverText(ItemStack p_41211_, @Nullable Level p_41212_, List<Component> p_41213_, TooltipFlag p_41214_) {
      CompoundTag compoundtag = p_41211_.getTagElement("Fireworks");
      if (compoundtag != null) {
         if (compoundtag.contains("Flight", 99)) {
            p_41213_.add((new TranslatableComponent("item.minecraft.firework_rocket.flight")).append(" ").append(String.valueOf((int)compoundtag.getByte("Flight"))).withStyle(ChatFormatting.GRAY));
         }

         ListTag listtag = compoundtag.getList("Explosions", 10);
         if (!listtag.isEmpty()) {
            for(int i = 0; i < listtag.size(); ++i) {
               CompoundTag compoundtag1 = listtag.getCompound(i);
               List<Component> list = Lists.newArrayList();
               FireworkStarItem.appendHoverText(compoundtag1, list);
               if (!list.isEmpty()) {
                  for(int j = 1; j < list.size(); ++j) {
                     list.set(j, (new TextComponent("  ")).append(list.get(j)).withStyle(ChatFormatting.GRAY));
                  }

                  p_41213_.addAll(list);
               }
            }
         }

      }
   }

   public ItemStack getDefaultInstance() {
      ItemStack itemstack = new ItemStack(this);
      itemstack.getOrCreateTag().putByte("Flight", (byte)1);
      return itemstack;
   }

   public static enum Shape {
      SMALL_BALL(0, "small_ball"),
      LARGE_BALL(1, "large_ball"),
      STAR(2, "star"),
      CREEPER(3, "creeper"),
      BURST(4, "burst");

      private static final FireworkRocketItem.Shape[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt((p_41240_) -> {
         return p_41240_.id;
      })).toArray((p_41243_) -> {
         return new FireworkRocketItem.Shape[p_41243_];
      });
      private final int id;
      private final String name;

      private Shape(int p_41234_, String p_41235_) {
         this.id = p_41234_;
         this.name = p_41235_;
      }

      public int getId() {
         return this.id;
      }

      public String getName() {
         return this.name;
      }

      public static FireworkRocketItem.Shape byId(int p_41238_) {
         return p_41238_ >= 0 && p_41238_ < BY_ID.length ? BY_ID[p_41238_] : SMALL_BALL;
      }
   }
}