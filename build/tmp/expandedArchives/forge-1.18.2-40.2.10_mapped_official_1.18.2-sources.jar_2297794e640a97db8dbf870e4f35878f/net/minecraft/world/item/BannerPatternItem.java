package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;

public class BannerPatternItem extends Item {
   private final BannerPattern bannerPattern;

   public BannerPatternItem(BannerPattern p_40548_, Item.Properties p_40549_) {
      super(p_40549_);
      this.bannerPattern = p_40548_;
   }

   public BannerPattern getBannerPattern() {
      return this.bannerPattern;
   }

   public void appendHoverText(ItemStack p_40551_, @Nullable Level p_40552_, List<Component> p_40553_, TooltipFlag p_40554_) {
      p_40553_.add(this.getDisplayName().withStyle(ChatFormatting.GRAY));
   }

   public MutableComponent getDisplayName() {
      return new TranslatableComponent(this.getDescriptionId() + ".desc");
   }
}