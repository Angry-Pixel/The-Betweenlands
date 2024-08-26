package thebetweenlands.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.common.component.entity.FoodSicknessData;
import thebetweenlands.common.network.ShowFoodSicknessPacket;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.util.FoodSickness;

import java.util.List;

public class ChiromawWingItem extends Item {
	public ChiromawWingItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
		if (livingEntity instanceof Player player) {
			FoodSicknessData data = player.getData(AttachmentRegistry.FOOD_SICKNESS);
			if (!level.isClientSide()) {
				if (FoodSickness.getSicknessForHatred(data.getFoodHatred(this)) != FoodSickness.SICK) {
					data.increaseFoodHatred(player, this, FoodSickness.SICK.maxHatred, FoodSickness.SICK.maxHatred);
					if (player instanceof ServerPlayer sp) {
						PacketDistributor.sendToPlayer(sp, new ShowFoodSicknessPacket(FoodSickness.SICK));
					}
				} else {
					player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 2));
				}
			}
		}
		return super.finishUsingItem(stack, level, livingEntity);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		Player player = BetweenlandsClient.getClientPlayer();
		if (player != null) {
			FoodSicknessData data = player.getData(AttachmentRegistry.FOOD_SICKNESS);
			if (FoodSickness.getSicknessForHatred(data.getFoodHatred(this)) != FoodSickness.SICK) {
				tooltip.add(Component.translatable(this.getDescriptionId() + ".eat").withStyle(ChatFormatting.GRAY));
			} else {
				tooltip.add(Component.translatable(this.getDescriptionId() + ".dont_eat").withStyle(ChatFormatting.GRAY));
			}
		}
	}
}
