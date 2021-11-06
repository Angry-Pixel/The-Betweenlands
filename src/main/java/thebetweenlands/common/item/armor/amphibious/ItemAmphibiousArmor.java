package thebetweenlands.common.item.armor.amphibious;

import java.util.*;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Multimap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.item.IAmphibiousArmorUpgrade;
import thebetweenlands.api.item.IAmphibiousArmorUpgrade.DamageEvent;
import thebetweenlands.client.render.model.armor.ModelAmphibiousArmor;
import thebetweenlands.client.render.model.armor.ModelBodyAttachment;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.inventory.InventoryAmphibiousArmor;
import thebetweenlands.common.inventory.container.ContainerAmphibiousArmor;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.armor.Item3DArmor;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.sound.BLSoundEvent;
import thebetweenlands.util.NBTHelper;

public class ItemAmphibiousArmor extends Item3DArmor {
	private static final Random UID_RNG = new Random();

	private static final String NBT_WORN_UPGRADE_MAP_KEY = "thebetweenlands.worn_amphibious_armor_upgrades";
	
	private static final String NBT_UPGRADE_MAP_KEY = "thebetweenlands.amphibious_armor_upgrades";
	private static final String NBT_DAMAGE_MAP_KEY = "thebetweenlands.amphibious_armor_damage";
	private static final String NBT_UPGRADE_FILTER_KEY = "thebetweenlands.amphibious_armor_upgrade_filters";

	private static final String NBT_AMPHIBIOUS_UPGRADE_HAD_NO_NBT_KEY = "thebetweenlands.amphibious_armor_upgrade_had_no_nbt";
	private static final String NBT_AMPHIBIOUS_UPGRADE_DAMAGE_KEY = "thebetweenlands.amphibious_armor_upgrade_damage";
	private static final String NBT_AMPHIBIOUS_UPGRADE_MAX_DAMAGE_KEY = "thebetweenlands.amphibious_armor_upgrade_max_damage";
	private static final String NBT_AMPHIBIOUS_UPGRADE_DAMAGE_UID_KEY = "thebetweenlands.amphibious_armor_upgrade_damage_uid";

	private static final String NBT_ASCENT_BOOST_TICKS = "thebetweenlands.ascent_boost_ticks";
	private static final String NBT_ASCENT_BOOST = "thebetweenlands.ascent_boost";
	
	private static final String NBT_URCHIN_AOE_COOLDOWN = "thebetweenlands.urchin_aoe_cooldown";
	private static final String NBT_ELECTRIC_COOLDOWN = "thebetweenlands.electric_cooldown";

	private EntityPlayerMP player;

	AmphibiousArmorEffectsHelper armorEffectsHelper = new AmphibiousArmorEffectsHelper();

	public ItemAmphibiousArmor(EntityEquipmentSlot slot) {
		super(BLMaterialRegistry.ARMOR_AMPHIBIOUS, 3, slot, "amphibious");

		this.setGemArmorTextureOverride(CircleGemType.AQUA, "amphibious_aqua");
		this.setGemArmorTextureOverride(CircleGemType.CRIMSON, "amphibious_crimson");
		this.setGemArmorTextureOverride(CircleGemType.GREEN, "amphibious_green");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ModelBodyAttachment createModel() {
		return new ModelAmphibiousArmor();
	}

	public int getUpgradeSlotCount(ItemStack stack) {
		switch(this.armorType) {
		case HEAD:
		case FEET:
			return 3;
		case CHEST:
		case LEGS:
			return 5;
		default:
			return 0;
		}
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		if(!world.isRemote) {
			this.player = (EntityPlayerMP) player;

			Map<IAmphibiousArmorUpgrade, Map<EntityEquipmentSlot, Integer>> wornUpgrades = getWornUpgradeCounts(player);
			
			NBTTagCompound nbt = itemStack.getTagCompound();
			
			if(!wornUpgrades.isEmpty()) {
				if(nbt == null) {
					nbt = new NBTTagCompound();
				}
				
				NBTTagList wornUpgradesNbt = new NBTTagList();
				
				for(Entry<IAmphibiousArmorUpgrade, Map<EntityEquipmentSlot, Integer>> upgrade : wornUpgrades.entrySet()) {
					NBTTagCompound upgradeNbt = new NBTTagCompound();
					
					upgradeNbt.setString("id", upgrade.getKey().getId().toString());
					
					NBTTagList countsNbt = new NBTTagList();
					
					for(Entry<EntityEquipmentSlot, Integer> count : upgrade.getValue().entrySet()) {
						NBTTagCompound countNbt = new NBTTagCompound();
						
						countNbt.setString("slot", count.getKey().getName());
						countNbt.setInteger("count", count.getValue());
						
						countsNbt.appendTag(countNbt);
					}
					
					upgradeNbt.setTag("counts", countsNbt);
					
					wornUpgradesNbt.appendTag(upgradeNbt);
				}
				
				nbt.setTag(NBT_WORN_UPGRADE_MAP_KEY, wornUpgradesNbt);
				
				itemStack.setTagCompound(nbt);
			} else if(nbt != null) {
				nbt.removeTag(NBT_WORN_UPGRADE_MAP_KEY);
			}
		}
		
		if(!player.isSpectator()) {
			NonNullList<ItemStack> armor = player.inventory.armorInventory;
			int armorPieces = 0;
			boolean fullyInWater = false;

			for (ItemStack anArmor : armor) {
				if (anArmor != null && anArmor.getItem() instanceof ItemAmphibiousArmor) {
					armorPieces += 1;
				}
			}

			if(player.isInWater()) {
				IBlockState blockState = player.world.getBlockState(new BlockPos(player.posX, player.getEntityBoundingBox().maxY + 0.1D, player.posZ));
				fullyInWater = blockState.getMaterial().isLiquid();
			}

			NBTTagCompound nbt = player.getEntityData();

			if (itemStack.getItem() == ItemRegistry.AMPHIBIOUS_CHESTPLATE) {
		        if (!itemStack.hasTagCompound()) {
		        	itemStack.setTagCompound(new NBTTagCompound());
		        	itemStack.getTagCompound().setBoolean("vortexAuto", true);
		        	itemStack.getTagCompound().setBoolean("urchinAuto", true);
		        	itemStack.getTagCompound().setBoolean("electricAuto", true);
		        	itemStack.getTagCompound().setBoolean("glideAuto", true);
					itemStack.getTagCompound().setBoolean("ascentAuto", true);
		            }

				int vortexCount = getUpgradeCount(itemStack, AmphibiousArmorUpgrades.FISH_VORTEX);
				int urchinCount = getUpgradeCount(itemStack, AmphibiousArmorUpgrades.URCHIN);
				int electricCount = getUpgradeCount(itemStack, AmphibiousArmorUpgrades.ELECTRIC);
				int glideCount = getUpgradeCount(itemStack, AmphibiousArmorUpgrades.GLIDE);

				long urchinAOECooldown = nbt.getLong(NBT_URCHIN_AOE_COOLDOWN);
				long electricCooldown = nbt.getLong(NBT_ELECTRIC_COOLDOWN);

				if (vortexCount >= 1 && itemStack.getTagCompound().getBoolean("vortexAuto")) {
					if (world.getTotalWorldTime() % 200 == 0) { //TODO dunno about timings yet
						if (!world.isRemote) {// && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
							armorEffectsHelper.activateFishVortex(world, player, vortexCount);
							damageUpgrade(itemStack, AmphibiousArmorUpgrades.FISH_VORTEX, 1, DamageEvent.ON_USE, false);
						}
					}
				}

				if (urchinCount >= 1 && itemStack.getTagCompound().getBoolean("urchinAuto")) { // more upgrades do more damage at 2F * urchinCount ;)
					if (world.getTotalWorldTime() %10 == 0 && world.getTotalWorldTime() >= urchinAOECooldown) {
						if (!world.isRemote) {// && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
							armorEffectsHelper.activateUrchinSpikes(world, player, urchinCount, nbt);
							damageUpgrade(itemStack, AmphibiousArmorUpgrades.URCHIN, 1, DamageEvent.ON_USE, false);
						}
					}
				}

				if (electricCount >= 1 && itemStack.getTagCompound().getBoolean("electricAuto")) { // count increases damage
					if (player.hurtResistantTime == player.maxHurtResistantTime && world.getTotalWorldTime() >= electricCooldown) {
						if (!world.isRemote) {// && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
							armorEffectsHelper.activateElectricEntity(world, player, electricCount, nbt);
							damageUpgrade(itemStack, AmphibiousArmorUpgrades.ELECTRIC, 1, DamageEvent.ON_USE, false);
						}
					}
				}

				if (glideCount >= 1 && itemStack.getTagCompound().getBoolean("glideAuto")) {
					if (player.isSprinting() && !player.onGround && !player.isJumping) {
						player.fallDistance = 0.0F;
						player.motionX *= 1.05D;
						player.motionZ *= 1.05D;
						player.motionY *= 0.6D - glideCount * 0.1F;

						if(player.ticksExisted % 20 == 0) {
							damageUpgrade(itemStack, AmphibiousArmorUpgrades.GLIDE, 1, DamageEvent.ON_USE, false);
						}
					}
				}
			}

			int ascentBoostTicks = nbt.getInteger(NBT_ASCENT_BOOST_TICKS);

			if(itemStack.getItem() == ItemRegistry.AMPHIBIOUS_LEGGINGS) {
				if(this.getUpgradeCount(itemStack, AmphibiousArmorUpgrades.ASCENT_BOOST) >= 1) {
					if(player.isSneaking() && player.onGround && !nbt.getBoolean(NBT_ASCENT_BOOST)) {
						nbt.setInteger(NBT_ASCENT_BOOST_TICKS, ++ascentBoostTicks);
					} else if(!player.isSneaking() && player.isInWater()) {
						if(ascentBoostTicks > 10 && !nbt.getBoolean(NBT_ASCENT_BOOST)) {
							ascentBoostTicks = 30;
							nbt.setInteger(NBT_ASCENT_BOOST_TICKS, ascentBoostTicks);

							nbt.setBoolean(NBT_ASCENT_BOOST, true);

							player.motionY += 0.75D;

							Vec3d lookVec = player.getLookVec().normalize();
							double speed = 1.2D;
							player.motionX += lookVec.x * speed;
							player.motionZ += lookVec.z * speed;
							player.motionY += lookVec.y * 0.5D;

							damageUpgrade(itemStack, AmphibiousArmorUpgrades.ASCENT_BOOST, 1, DamageEvent.ON_USE, false);
						}

						ascentBoostTicks = Math.max(0, ascentBoostTicks - 1);
						nbt.setInteger(NBT_ASCENT_BOOST_TICKS, ascentBoostTicks);
					}
				} else {
					nbt.setInteger(NBT_ASCENT_BOOST_TICKS, 0);
				}
			}

			/*
			if(nbt.getBoolean(NBT_ASCENT_BOOST)) {
				player.fallDistance = 0;

				if(player.onGround) {
					ascentBoostTicks = 0;
				}
			}
			*/

			if(ascentBoostTicks <= 0 || !player.isInWater()) {
				nbt.setInteger(NBT_ASCENT_BOOST_TICKS, ascentBoostTicks = 0);
				nbt.setBoolean(NBT_ASCENT_BOOST, false);
			}

			if(nbt.getBoolean(NBT_ASCENT_BOOST) && player.isInWater() && player.motionY < 2.0D) {
				player.motionY += 0.05D;
			}

			if(itemStack.getItem() == ItemRegistry.AMPHIBIOUS_HELMET) {
				if(player.isInWater()) {
					int visibilityCount = this.getUpgradeCount(itemStack, AmphibiousArmorUpgrades.VISIBILITY);

					if (visibilityCount >= 1 && fullyInWater) {
						player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 10, 0));
					}
				} else {
					if(this.getUpgradeCount(itemStack, AmphibiousArmorUpgrades.FISH_SIGHT) > 0 && player.ticksExisted % 40 == 0) {
						int radius = this.getUpgradeCount(itemStack, AmphibiousArmorUpgrades.FISH_SIGHT) * 8;

						AxisAlignedBB aabb = new AxisAlignedBB(player.getPosition()).grow(radius);

						for(EntityAnadia anadia : world.getEntitiesWithinAABB(EntityAnadia.class, aabb, a -> a.getDistanceSq(player.getPosition().getX() + 0.5f, player.getPosition().getY() + 0.5f, player.getPosition().getZ() + 0.5f) <= radius * radius)) {
							if(!anadia.isGlowing()) {
								anadia.setGlowTimer(200);
								damageUpgrade(itemStack, AmphibiousArmorUpgrades.FISH_SIGHT, 1, DamageEvent.ON_USE, false);
							}
						}
					}
				}
			}

			if (itemStack.getItem() == ItemRegistry.AMPHIBIOUS_BOOTS && player.isInWater()) {
				if(fullyInWater) {
					if(ascentBoostTicks <= 0 && !player.isSneaking() && player.moveForward == 0) {
						player.motionY = Math.sin(player.ticksExisted / 5.0F) * 0.016D;
					}

					if(player.moveForward != 0) {
						if(player.moveForward > 0) {
							Vec3d lookVec = player.getLookVec().normalize();
							double speed = 0.01D + 0.05D / 4.0D * armorPieces;
							player.motionX += lookVec.x * player.moveForward * speed;
							player.motionZ += lookVec.z * player.moveForward * speed;
							if(!player.isSneaking() || lookVec.y < 0) {
								player.motionY += lookVec.y * player.moveForward * speed;
							}
							player.getFoodStats().addExhaustion(0.0024F);
						}

						if(!player.isSneaking()) {
							player.motionY += 0.02D;
						}
					}

					int bladderCount = getUpgradeCount(player, AmphibiousArmorUpgrades.BUOYANCY);

					if(bladderCount > 0) {
						double speedMod = bladderCount * 0.01D;

						if(player.moveForward != 0) {
							speedMod *= 0.5D;
						}

						if(player.isSneaking()) {
							player.motionY -= speedMod;
						} else if(player.isJumping) {
							player.motionY += speedMod + 0.02D;
						}
					}
				}
			}

			int breathingCount = Math.min(getUpgradeCount(itemStack, AmphibiousArmorUpgrades.BREATHING), 2);

			if(player.isInWater()) {
				boolean  hasBreathingUpgrade = false;

				if (armorPieces >= 4) {
					player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 10));

					if (breathingCount >= 2) {
						player.setAir(300);
					} else {
						if (player.ticksExisted % (10 + (breathingCount * 10)) == 0) {
							player.setAir(player.getAir() - 1);
						}
					}

					hasBreathingUpgrade = true;
				} else if(breathingCount > 0) {
					player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 10));

					if (player.ticksExisted % (4 + breathingCount) == 0) {
						player.setAir(player.getAir() - 1);
					}

					hasBreathingUpgrade = true;
				}

				if(hasBreathingUpgrade) {
					if (player.getAir() <= -20) {
						player.setAir(0);

						for (int i = 0; i < 8; ++i) {
							Random rand = world.rand;
							float rx = rand.nextFloat() - rand.nextFloat();
							float ry = rand.nextFloat() - rand.nextFloat();
							float rz = rand.nextFloat() - rand.nextFloat();

							player.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, player.posX + (double) rx, player.posY + (double) ry, player.posZ + (double) rz, player.motionX, player.motionY, player.motionZ);
						}

						player.attackEntityFrom(DamageSource.DROWN, 2.0F);
					}
				}
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		ItemStack stack = player.getHeldItem(hand);

		//TODO Testing
		for(IAmphibiousArmorUpgrade upgrade : AmphibiousArmorUpgrades.values()) {
			int count = this.getUpgradeCount(stack, upgrade);
			if(count > 0) {
				Map<IAmphibiousArmorUpgrade, Map<EntityEquipmentSlot, Integer>> counts = getWornUpgradeCounts(stack);
				int wornCount = 0;
				if(counts.containsKey(upgrade)) {
					for(Entry<EntityEquipmentSlot, Integer> entry : counts.get(upgrade).entrySet()) {
						wornCount += entry.getValue();
					}
				}
				System.out.println(upgrade.getId() + ": " + count + " / " + wornCount);
			}
		}

		if (player.isSneaking()) {
			player.openGui(TheBetweenlands.instance, CommonProxy.GUI_AMPHIBIOUS_ARMOR, world, 0, 0, 0);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		} else {
			return super.onItemRightClick(world, player, hand);
		}
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);

		if(slot == this.armorType) {
			for(IAmphibiousArmorUpgrade upgrade : AmphibiousArmorUpgrades.getUpgrades(this.armorType)) {
				int count = this.getUpgradeCount(stack, upgrade);

				if(count > 0) {
					upgrade.applyAttributeModifiers(this.armorType, stack, count, getWornUpgradeCounts(stack), modifiers);
				}
			}
		}

		return modifiers;
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		for(IAmphibiousArmorUpgrade upgrade : AmphibiousArmorUpgrades.values()) {
			this.damageUpgrade(stack, upgrade, 1, DamageEvent.ON_DAMAGE, true);
		}

		if(getDamage(stack) >= stack.getMaxDamage()) {
			IInventory inv = new InventoryAmphibiousArmor(stack, "");

			for(int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack upgradeItem = inv.getStackInSlot(i).copy();

				if(!upgradeItem.isEmpty()) {
					int itemDamage = this.getUpgradeDamage(stack, i);
					setUpgradeItemStoredDamage(upgradeItem, itemDamage, upgradeItem.getMaxDamage());

					if (!player.inventory.addItemStackToInventory(upgradeItem))
						player.dropItem(upgradeItem, false);
				}
			}
		}

		super.setDamage(stack, damage);
	}

	public void setUpgradeFilter(ItemStack stack, int slot, ItemStack filter) {
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		if(filter.isEmpty()) {
			nbt.removeTag(NBT_UPGRADE_FILTER_KEY + "." + slot);
		} else {
			NBTTagCompound filterNbt = nbt.getCompoundTag(NBT_UPGRADE_FILTER_KEY + "." + slot);
			filter = filter.copy().splitStack(1);
			setUpgradeItemStoredDamage(filter, 0, 0);
			filter.writeToNBT(filterNbt);
			nbt.setTag(NBT_UPGRADE_FILTER_KEY + "." + slot, filterNbt);
		}
	}

	public ItemStack getUpgradeFilter(ItemStack stack, int slot) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			NBTTagCompound filterNbt = nbt.getCompoundTag(NBT_UPGRADE_FILTER_KEY + "." + slot);
			if(!filterNbt.isEmpty()) {
				return new ItemStack(filterNbt);
			}
		}
		return ItemStack.EMPTY;
	}

	public void setUpgradeCounts(ItemStack stack, IInventory inv) {
		NBTTagCompound upgradesMap = new NBTTagCompound();

		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack upgradeItem = inv.getStackInSlot(i);

			if(!upgradeItem.isEmpty()) {
				IAmphibiousArmorUpgrade upgrade = AmphibiousArmorUpgrades.getUpgrade(this.armorType, upgradeItem);

				if(upgrade != null) {
					String idStr = upgrade.getId().toString();
					upgradesMap.setInteger(idStr, upgradesMap.getInteger(idStr) + 1);
				}
			}
		}

		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setTag(NBT_UPGRADE_MAP_KEY, upgradesMap);
	}

	public static int getUpgradeCount(EntityLivingBase entity, IAmphibiousArmorUpgrade upgrade) {
		int count = 0;
		for(ItemStack stack : entity.getArmorInventoryList()) {
			if(!stack.isEmpty() && stack.getItem() instanceof ItemAmphibiousArmor) {
				count += ((ItemAmphibiousArmor) stack.getItem()).getUpgradeCount(stack, upgrade);
			}
		}
		return count;
	}


	public static Map<IAmphibiousArmorUpgrade, Map<EntityEquipmentSlot, Integer>> getWornUpgradeCounts(EntityLivingBase entity) {
		Map<IAmphibiousArmorUpgrade, Map<EntityEquipmentSlot, Integer>> upgrades = new HashMap<>();
		for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
			if(slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
				ItemStack stack = entity.getItemStackFromSlot(slot);
				if(!stack.isEmpty() && stack.getItem() instanceof ItemAmphibiousArmor) {
					for(IAmphibiousArmorUpgrade upgrade : AmphibiousArmorUpgrades.getUpgrades(slot)) {
						int count = ((ItemAmphibiousArmor) stack.getItem()).getUpgradeCount(stack, upgrade);
						if(count > 0) {
							Map<EntityEquipmentSlot, Integer> counts = upgrades.get(upgrade);
							if(counts == null) {
								upgrades.put(upgrade, counts = new HashMap<>());
							}
							counts.put(slot, count);
						}
					}
				}
			}
		}
		return upgrades;
	}
	
	public static Map<IAmphibiousArmorUpgrade, Map<EntityEquipmentSlot, Integer>> getWornUpgradeCounts(ItemStack stack) {
		Map<IAmphibiousArmorUpgrade, Map<EntityEquipmentSlot, Integer>> wornUpgrades = new HashMap<>();
		
		NBTTagCompound nbt = stack.getTagCompound();
		
		if(nbt != null && nbt.hasKey(NBT_WORN_UPGRADE_MAP_KEY, Constants.NBT.TAG_LIST)) {
			NBTTagList wornUpgradesNbt = nbt.getTagList(NBT_WORN_UPGRADE_MAP_KEY, Constants.NBT.TAG_COMPOUND);
			
			for(int i = 0; i < wornUpgradesNbt.tagCount(); i++) {
				NBTTagCompound upgradeNbt = wornUpgradesNbt.getCompoundTagAt(i);
				
				IAmphibiousArmorUpgrade upgrade = AmphibiousArmorUpgrades.getUpgrade(new ResourceLocation(upgradeNbt.getString("id")));
				
				if(upgrade != null) {
					NBTTagList countsNbt = upgradeNbt.getTagList("counts", Constants.NBT.TAG_COMPOUND);

					Map<EntityEquipmentSlot, Integer> counts = new HashMap<>();
					
					for(int j = 0; j < countsNbt.tagCount(); j++) {
						NBTTagCompound countNbt = countsNbt.getCompoundTagAt(j);
						counts.put(EntityEquipmentSlot.fromString(countNbt.getString("slot")), countNbt.getInteger("count"));
					}
					
					wornUpgrades.put(upgrade, counts);
				}
			}
		}
		
		return wornUpgrades;
	}
	
	public int getUpgradeCount(ItemStack stack, IAmphibiousArmorUpgrade upgrade) {
		NBTTagCompound nbt = stack.getTagCompound();

		if(nbt != null) {
			return nbt.getCompoundTag(NBT_UPGRADE_MAP_KEY).getInteger(upgrade.getId().toString());
		}

		return 0;
	}

	public static boolean damageUpgrade(EntityLivingBase entity, IAmphibiousArmorUpgrade upgrade, int amount, DamageEvent damageEvent, boolean damageAll) {
		boolean damaged = false;
		for(ItemStack stack : entity.getArmorInventoryList()) {
			if(!stack.isEmpty() && stack.getItem() instanceof ItemAmphibiousArmor) {
				damaged |= ((ItemAmphibiousArmor) stack.getItem()).damageUpgrade(stack, upgrade, amount, damageEvent, damageAll);
				if(damaged && !damageAll) {
					break;
				}
			}
		}
		return damaged;
	}

	public boolean damageUpgrade(ItemStack stack, IAmphibiousArmorUpgrade upgrade, int amount, DamageEvent damageEvent, boolean damageAll) {
		if(damageEvent == DamageEvent.NONE) {
			return false;
		}
		
		boolean damaged = false;

		IInventory inv = new InventoryAmphibiousArmor(stack, "");

		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack upgradeItem = inv.getStackInSlot(i);

			if(!upgradeItem.isEmpty()) {
				IAmphibiousArmorUpgrade itemUpgrade = AmphibiousArmorUpgrades.getUpgrade(this.armorType, upgradeItem);

				if(itemUpgrade == upgrade && (damageEvent == DamageEvent.ALL || itemUpgrade.isApplicableDamageEvent(damageEvent))) {
					int damage = this.getUpgradeDamage(stack, i);
					int maxDamage = this.getUpgradeMaxDamage(stack, i);

					if(damage + amount > maxDamage) {
						if(itemUpgrade.canBreak()) {
							if(player != null && !player.world.isRemote) {
								System.out.println(player.getPosition());
								player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.AMBIENT, 1F, 1F);
								player.sendStatusMessage(new TextComponentTranslation("chat.aa_upgrade_broke", upgradeItem.getDisplayName()), false);
							}

							upgradeItem.shrink(1);
							inv.setInventorySlotContents(i, upgradeItem);
							this.setUpgradeDamage(stack, i, 0, itemUpgrade.getMaxDamage());
						}
					} else {
						this.setUpgradeDamage(stack, i, damage + amount);
					}

					damaged = true;

					if(!damageAll) {
						return true;
					}
				}
			}
		}

		return damaged;
	}

	public void setUpgradeDamage(ItemStack stack, int slot, int damage) {
		this.setUpgradeDamage(stack, slot, damage, -1);
	}

	public void setUpgradeDamage(ItemStack stack, int slot, int damage, int maxDamage) {
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		String key = NBT_DAMAGE_MAP_KEY + "." + slot;
		NBTTagCompound damageNbt = nbt.getCompoundTag(key);
		if(maxDamage < 0) {
			maxDamage = damageNbt.getInteger("maxDamage");
		} else {
			damageNbt.setInteger("maxDamage", maxDamage);
		}
		damageNbt.setInteger("damage", MathHelper.clamp(damage, 0, maxDamage));
		nbt.setTag(key, damageNbt);
	}

	public int getUpgradeDamage(ItemStack stack, int slot) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			String key = NBT_DAMAGE_MAP_KEY + "." + slot;
			NBTTagCompound damageNbt = nbt.getCompoundTag(key);
			return damageNbt.getInteger("damage");
		}
		return 0;
	}

	public int getUpgradeMaxDamage(ItemStack stack, int slot) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			String key = NBT_DAMAGE_MAP_KEY + "." + slot;
			NBTTagCompound damageNbt = nbt.getCompoundTag(key);
			return damageNbt.getInteger("maxDamage");
		}
		return 0;
	}

	public static int getUpgradeItemStoredDamage(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			return nbt.getInteger(NBT_AMPHIBIOUS_UPGRADE_DAMAGE_KEY);
		}
		return 0;
	}

	public static int getUpgradeItemMaxStoredDamage(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			return nbt.getInteger(NBT_AMPHIBIOUS_UPGRADE_MAX_DAMAGE_KEY);
		}
		return 0;
	}

	public static void setUpgradeItemStoredDamage(ItemStack stack, int damage, int maxDamage) {
		boolean hadNbt = stack.getTagCompound() != null;

		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);

		if(!nbt.hasKey(NBT_AMPHIBIOUS_UPGRADE_DAMAGE_KEY, Constants.NBT.TAG_INT)) {
			//Store whether item previously already had a tag compound
			//If not it can be removed again when damage is removed
			nbt.setBoolean(NBT_AMPHIBIOUS_UPGRADE_HAD_NO_NBT_KEY, !hadNbt);
		}

		if(damage > 0) {
			nbt.setInteger(NBT_AMPHIBIOUS_UPGRADE_DAMAGE_KEY, damage);
			nbt.setInteger(NBT_AMPHIBIOUS_UPGRADE_MAX_DAMAGE_KEY, maxDamage);
			nbt.setLong(NBT_AMPHIBIOUS_UPGRADE_DAMAGE_UID_KEY, UID_RNG.nextLong()); //unique ID makes sure the item cannot be stacked
		} else {
			boolean hadNoNbt = nbt.getBoolean(NBT_AMPHIBIOUS_UPGRADE_HAD_NO_NBT_KEY);

			nbt.removeTag(NBT_AMPHIBIOUS_UPGRADE_HAD_NO_NBT_KEY);
			nbt.removeTag(NBT_AMPHIBIOUS_UPGRADE_DAMAGE_KEY);
			nbt.removeTag(NBT_AMPHIBIOUS_UPGRADE_MAX_DAMAGE_KEY);
			nbt.removeTag(NBT_AMPHIBIOUS_UPGRADE_DAMAGE_UID_KEY);

			if(hadNoNbt && nbt.isEmpty()) {
				stack.setTagCompound(null);
			}
		}
	}
}
