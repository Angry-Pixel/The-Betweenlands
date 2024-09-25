package thebetweenlands.common.item.armor.amphibious;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import javax.annotation.Nullable;
import thebetweenlands.api.item.amphibious.AmphibiousArmorUpgrade;
import thebetweenlands.api.item.amphibious.TickingAmphibiousArmorUpgrade;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.armor.AmphibiousArmorModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.item.AmphibiousUpgrades;
import thebetweenlands.common.component.item.UpgradeDamage;
import thebetweenlands.common.inventory.AmphibiousArmorMenu;
import thebetweenlands.common.inventory.container.AmphibiousArmorContainer;
import thebetweenlands.common.registries.AmphibiousArmorUpgradeRegistry;
import thebetweenlands.common.registries.ArmorMaterialRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.List;

public class AmphibiousArmorItem extends ArmorItem {

	public static final String NBT_ASCENT_BOOST_TICKS = "thebetweenlands.ascent_boost_ticks";
	public static final String NBT_ASCENT_BOOST = "thebetweenlands.ascent_boost";
	public static final String NBT_URCHIN_AOE_COOLDOWN = "thebetweenlands.urchin_aoe_cooldown";
	public static final String NBT_ELECTRIC_COOLDOWN = "thebetweenlands.electric_cooldown";
	public static final String AUTO_TOGGLES_KEY = "thebetweenlands.auto_toggles";

	private ServerPlayer serverPlayer;

	public AmphibiousArmorItem(Type type, Properties properties) {
		super(ArmorMaterialRegistry.AMPHIBIOUS, type, properties);
	}

	public static int getUpgradeSlotCount(ItemStack stack) {
		return stack.is(ItemRegistry.AMPHIBIOUS_CHESTPLATE) || stack.is(ItemRegistry.AMPHIBIOUS_LEGGINGS) ? 5 : 3;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (entity instanceof Player player && slotId > 35 && slotId < 40) {
			if (!level.isClientSide() && this.serverPlayer != player) {
				this.serverPlayer = (ServerPlayer) player;
			}

			if (!player.getPersistentData().contains(AmphibiousArmorItem.AUTO_TOGGLES_KEY)) {
				player.getPersistentData().put(AmphibiousArmorItem.AUTO_TOGGLES_KEY, new CompoundTag());
			}

			if (!player.isSpectator()) {
				NonNullList<ItemStack> armor = player.getInventory().armor;
				int armorPieces = 0;

				for (ItemStack anArmor : armor) {
					if (anArmor != null && anArmor.getItem() instanceof AmphibiousArmorItem) {
						armorPieces += 1;
					}
				}

				if (stack.has(DataComponentRegistry.AMPHIBIOUS_UPGRADES)) {
					var upgrades = stack.get(DataComponentRegistry.AMPHIBIOUS_UPGRADES);
					for (var entry : upgrades.getAllUniqueUpgradesWithCounts().object2IntEntrySet()) {
						if (entry.getKey().value() instanceof TickingAmphibiousArmorUpgrade ticking) {
							ticking.onArmorTick(level, player, stack, entry.getIntValue(), armorPieces);
						}
					}
				}

				CompoundTag nbt = player.getPersistentData();

				int ascentBoostTicks = nbt.getInt(NBT_ASCENT_BOOST_TICKS);

				if (ascentBoostTicks <= 0 || !player.isInWater()) {
					nbt.putInt(NBT_ASCENT_BOOST_TICKS, ascentBoostTicks = 0);
					nbt.putBoolean(NBT_ASCENT_BOOST, false);
				}

				if (nbt.getBoolean(NBT_ASCENT_BOOST) && player.isInWater() && player.getDeltaMovement().y() < 2.0D) {
					player.setDeltaMovement(player.getDeltaMovement().add(0.0D, 0.05D, 0.0D));
				}

				if (stack.is(ItemRegistry.AMPHIBIOUS_BOOTS) && player.isEyeInFluid(FluidTags.WATER)) {
					if (ascentBoostTicks == 0 && !player.isShiftKeyDown() && player.zza == 0 && !player.getAbilities().flying && !player.jumping) {
						player.setDeltaMovement(player.getDeltaMovement().x(), Math.sin(player.tickCount / 5.0F) * 0.016D, player.getDeltaMovement().z());
					}

					if (player.zza != 0) {
						if (player.zza > 0) {
							Vec3 lookVec = player.getLookAngle().normalize();
							double speed = 0.01D + 0.05D / 4.0D * armorPieces;
							player.setDeltaMovement(player.getDeltaMovement().add(lookVec.x * player.zza * speed, 0.0D, lookVec.z * player.zza * speed));
							if (!player.isShiftKeyDown() || lookVec.y < 0) {
								player.setDeltaMovement(player.getDeltaMovement().add(0.0D, lookVec.y * player.zza * speed, 0.0D));
							}
							player.getFoodData().addExhaustion(0.0024F);
						}

						if (!player.isShiftKeyDown()) {
							player.setDeltaMovement(player.getDeltaMovement().add(0.0D, 0.0067D, 0.0D));
						}
					}
				}

				//have to keep breathing hardcoded since its meshed with the armor set air logic... sign
				var upgradeCount = Math.min(stack.getOrDefault(DataComponentRegistry.AMPHIBIOUS_UPGRADES, AmphibiousUpgrades.EMPTY).getAllUniqueUpgradesWithCounts().getOrDefault(AmphibiousArmorUpgradeRegistry.BREATHING, 0), 2);
				if (player.isInWater()) {
					boolean hasBreathingUpgrade = false;

					if (armorPieces >= 4) {
						player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 10));

						if (upgradeCount == 2) {
							player.setAirSupply(300);
						} else {
							if (player.tickCount % (10 + (upgradeCount * 10)) == 0) {
								player.setAirSupply(player.getAirSupply() - 1);
							}
						}

						hasBreathingUpgrade = true;
					} else if (upgradeCount > 0) {
						player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 10));

						if (player.tickCount % (4 + upgradeCount) == 0) {
							player.setAirSupply(player.getAirSupply() - 1);
						}

						hasBreathingUpgrade = true;
					}

					if (hasBreathingUpgrade) {
						if (player.getAirSupply() <= -20) {
							player.setAirSupply(0);
							Vec3 vec3 = player.getDeltaMovement();

							for (int i = 0; i < 8; i++) {
								double d1 = player.getRandom().nextDouble() - player.getRandom().nextDouble();
								double d2 = player.getRandom().nextDouble() - player.getRandom().nextDouble();
								double d3 = player.getRandom().nextDouble() - player.getRandom().nextDouble();
								level.addParticle(ParticleTypes.BUBBLE, player.getX() + d1, player.getY() + d2, player.getZ() + d3, vec3.x, vec3.y, vec3.z);
							}

							player.hurt(level.damageSources().drown(), 2.0F);
						}
					}
				}
			}
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (player.isShiftKeyDown()) {
			if (level.isClientSide()) {
				return InteractionResultHolder.success(stack);
			} else {
				player.openMenu(new MenuProvider() {
					@Override
					public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
						return new AmphibiousArmorMenu(containerId, playerInventory, new AmphibiousArmorContainer(stack, AmphibiousArmorItem.getUpgradeSlotCount(stack)));
					}

					@Override
					public Component getDisplayName() {
						return AmphibiousArmorItem.this.getName(stack);
					}
				}, buf -> {
					ItemStack.STREAM_CODEC.encode(buf, stack);
					buf.writeInt(AmphibiousArmorItem.getUpgradeSlotCount(stack));
				});
				return InteractionResultHolder.consume(stack);
			}
		}
		return super.use(level, player, hand);
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		if (stack.has(DataComponentRegistry.AMPHIBIOUS_UPGRADES)) {
			var upgrades = stack.get(DataComponentRegistry.AMPHIBIOUS_UPGRADES);
			for (int i = 0; i < upgrades.getSlots(); i++) {
				damageUpgrade(stack, this.serverPlayer, upgrades.getUpgradeInSlot(i).value(), 1, AmphibiousArmorUpgrade.DamageEvent.ON_DAMAGE, true);
			}

			if (this.getDamage(stack) >= stack.getMaxDamage()) {
				Container inv = new AmphibiousArmorContainer(stack, AmphibiousArmorItem.getUpgradeSlotCount(stack));

				for (int i = 0; i < inv.getContainerSize(); i++) {
					ItemStack upgradeItem = inv.getItem(i).copy();

					if (!upgradeItem.isEmpty()) {
						if (!this.serverPlayer.getInventory().add(upgradeItem))
							this.serverPlayer.drop(upgradeItem, false);
					}
				}
			}
		}

		super.setDamage(stack, damage);
	}

	public static boolean damageUpgrade(Player player, AmphibiousArmorUpgrade upgrade, int amount, AmphibiousArmorUpgrade.DamageEvent damageEvent, boolean damageAll) {
		boolean damaged = false;
		for (ItemStack stack : player.getArmorAndBodyArmorSlots()) {
			if (!stack.isEmpty() && stack.getItem() instanceof AmphibiousArmorItem) {
				damaged |= damageUpgrade(stack, player, upgrade, amount, damageEvent, damageAll);
				if (damaged && !damageAll) {
					break;
				}
			}
		}
		return damaged;
	}

	public static boolean damageUpgrade(ItemStack stack, Player player, AmphibiousArmorUpgrade upgrade, int amount, AmphibiousArmorUpgrade.DamageEvent damageEvent, boolean damageAll) {
		if (damageEvent == AmphibiousArmorUpgrade.DamageEvent.NONE) {
			return false;
		}

		boolean damaged = false;

		Container inv = new AmphibiousArmorContainer(stack, AmphibiousArmorItem.getUpgradeSlotCount(stack));

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack upgradeItem = inv.getItem(i);

			if (!upgradeItem.isEmpty()) {
				Holder<AmphibiousArmorUpgrade> itemUpgrade = ArmorEffectHelper.getUpgrade(((AmphibiousArmorItem)stack.getItem()).getType().getSlot(), upgradeItem);

				if (itemUpgrade.value() == upgrade && (damageEvent == AmphibiousArmorUpgrade.DamageEvent.ALL || itemUpgrade.value().isApplicableDamageEvent(damageEvent))) {
					int damage = upgradeItem.getOrDefault(DataComponentRegistry.UPGRADE_DAMAGE, UpgradeDamage.EMPTY).damage();
					int maxDamage = upgrade.getMaxDamage();

					if (damage + amount > maxDamage) {
						if (itemUpgrade.value().canBreak()) {
							if (player != null && !player.level().isClientSide()) {
								player.playSound(SoundEvents.ITEM_BREAK, 1F, 1F);
								player.sendSystemMessage(Component.translatable("item.thebetweenlands.amphibious_upgrade.broke", upgradeItem.getDisplayName().getString()));
							}

							upgradeItem.shrink(1);
							inv.setItem(i, upgradeItem);
						}
					} else {
						upgradeItem.set(DataComponentRegistry.UPGRADE_DAMAGE, new UpgradeDamage(damage + amount, maxDamage));
					}

					damaged = true;

					if (!damageAll) {
						return true;
					}
				}
			}
		}

		return damaged;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		if (stack.has(DataComponentRegistry.AMPHIBIOUS_UPGRADES)) {
			for (Object2IntMap.Entry<Item> upgrade : stack.get(DataComponentRegistry.AMPHIBIOUS_UPGRADES).getAllUniqueStacksWithSlotCounts().object2IntEntrySet()) {
				tooltip.add(Component.translatable("item.thebetweenlands.amphibious_armor.upgrade", upgrade.getKey().getDescription().getString(), String.valueOf(upgrade.getIntValue())).withStyle(ChatFormatting.GRAY));
			}
		}
	}

	@Override
	public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
		return TheBetweenlands.prefix("textures/models/armor/amphibious_layer.png");
	}

	public static final class ArmorRender implements IClientItemExtensions {
		public static final ArmorRender INSTANCE = new ArmorRender();

		@Override
		public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
			return new AmphibiousArmorModel(slot, Minecraft.getInstance().getEntityModels().bakeLayer(BLModelLayers.AMPHIBIOUS_ARMOR));
		}
	}
}
