package thebetweenlands.common.item.armor.amphibious;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Multimap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
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
import thebetweenlands.common.entity.EntityFishVortex;
import thebetweenlands.common.entity.EntityUrchinSpikeAOE;
import thebetweenlands.common.inventory.InventoryAmphibiousArmor;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.armor.Item3DArmor;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemAmphibiousArmor extends Item3DArmor {
	private static final Random UID_RNG = new Random();

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

	protected AxisAlignedBB proximityBox(EntityPlayer player, double xSize, double ySize, double zSize) {
		return new AxisAlignedBB(player.getPosition()).grow(xSize, ySize, zSize);
	}

	private void spawnFishVortex(World world, EntityLivingBase entity) {
		EntityFishVortex vortex = new EntityFishVortex(world);
		vortex.setPosition(entity.posX, entity.posY + 0.25D, entity.posZ);
		world.spawnEntity(vortex);
		entity.startRiding(vortex, true);
	}
	
	private void spawnUrchinSpikes(World world, EntityPlayer player, int damage) {
		EntityUrchinSpikeAOE urchinSpikes = new EntityUrchinSpikeAOE(world, player, damage);
		urchinSpikes.setPosition(player.posX, player.posY + player.height * 0.5D, player.posZ);
		world.spawnEntity(urchinSpikes);
		urchinSpikes.shootSpikes();
	}
	
	private List findNearbyEntities(World world, EntityPlayer player, AxisAlignedBB box) {
		return world.getEntitiesWithinAABB(EntityLivingBase.class, box, e -> e instanceof IMob);
	}
	
	private EntityLivingBase pickRandomEntityFromList(List<EntityLivingBase> list) {
		Collections.shuffle(list);
		return list.get(0);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		if(!player.isSpectator()) {
			NonNullList<ItemStack> armor = player.inventory.armorInventory;
			int armorPieces = 0;

			for (ItemStack anArmor : armor) {
				if (anArmor != null && anArmor.getItem() instanceof ItemAmphibiousArmor) {
					armorPieces += 1;
				}
			}

			NBTTagCompound nbt = player.getEntityData();
			
			if (itemStack.getItem() == ItemRegistry.AMPHIBIOUS_CHESTPLATE) {
				int vortexCount = getUpgradeCount(itemStack, AmphibiousArmorUpgrades.FISH_VORTEX);
				int urchinCount = getUpgradeCount(itemStack, AmphibiousArmorUpgrades.URCHIN);
				
				if (vortexCount >= 1) {
					if (world.getTotalWorldTime() % 200 == 0) { //TODO dunno about timings yet
						if (!world.isRemote && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
							List<EntityLivingBase> list = findNearbyEntities(world, player, proximityBox(player, 8D, 4D, 8D));
							for (int entityCount = 0; entityCount < Math.min(vortexCount, list.size()); entityCount++) {
								EntityLivingBase entity = pickRandomEntityFromList(list);
								if (entity != null)
									if (!(entity instanceof EntityPlayer)) {
										spawnFishVortex(world, entity);
										list.remove(0);
									}
							}
						}
					}
				}
				
				long urchinAOEcooldown = nbt.getLong(NBT_URCHIN_AOE_COOLDOWN);

				if (urchinCount >= 1) { // more upgrades do more damage at 2F * urchinCount ;)
					if (world.getTotalWorldTime() %10 == 0 && world.getTotalWorldTime() >= urchinAOEcooldown) { // TODO cooldown balancing
						if (!world.isRemote && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
							List<EntityLivingBase> list = findNearbyEntities(world, player, proximityBox(player, 2D, 2D, 2D));
							if (!list.isEmpty()) {
								EntityLivingBase entity = list.get(0);
								if (entity != null)
									if (!(entity instanceof EntityPlayer)) {
										spawnUrchinSpikes(world, player, urchinCount);
										nbt.setLong(NBT_URCHIN_AOE_COOLDOWN, world.getTotalWorldTime() + 200);
									}
							}
						}
					}
				}
			}

			int ascentBoostTicks = nbt.getInteger(NBT_ASCENT_BOOST_TICKS);

			if(itemStack.getItem() == ItemRegistry.AMPHIBIOUS_LEGGINGS) {
				if(this.getUpgradeCount(itemStack, AmphibiousArmorUpgrades.ASCENT_BOOST) >= 1) {
					if(player.isSneaking() && player.onGround) {
						nbt.setInteger(NBT_ASCENT_BOOST_TICKS, ++ascentBoostTicks);
					} else if(!player.isSneaking()) {
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
						}

						ascentBoostTicks = Math.max(0, ascentBoostTicks - 1);
						nbt.setInteger(NBT_ASCENT_BOOST_TICKS, ascentBoostTicks);
					}
				} else {
					nbt.setInteger(NBT_ASCENT_BOOST_TICKS, 0);
				}
			}

			if(ascentBoostTicks <= 0 || !player.isInWater()) {
				nbt.setInteger(NBT_ASCENT_BOOST_TICKS, ascentBoostTicks = 0);
				nbt.setBoolean(NBT_ASCENT_BOOST, false);
			}

			if(nbt.getBoolean(NBT_ASCENT_BOOST) && player.isInWater() && player.motionY < 2.0D) {
				player.motionY += 0.05D;
			}

			if (itemStack.getItem() == ItemRegistry.AMPHIBIOUS_BOOTS && player.isInWater()) {
				IBlockState blockState = player.world.getBlockState(new BlockPos(player.posX, player.getEntityBoundingBox().maxY + 0.1D, player.posZ));
				boolean fullyInWater = blockState.getMaterial().isLiquid();

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
				}

				if(armorPieces >= 4) {
					player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 10));

					if(player.ticksExisted % 3 == 0) {
						player.setAir(player.getAir() - 1);
					}

					if(player.getAir() <= -20) {
						player.setAir(0);

						for (int i = 0; i < 8; ++i) {
							Random rand = world.rand;
							float rx = rand.nextFloat() - rand.nextFloat();
							float ry = rand.nextFloat() - rand.nextFloat();
							float rz = rand.nextFloat() - rand.nextFloat();

							player.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, player.posX + (double)rx, player.posY + (double)ry, player.posZ + (double)rz, player.motionX, player.motionY, player.motionZ);
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
			System.out.println(upgrade.getId() + ": " + this.getUpgradeCount(stack, upgrade));
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
					upgrade.applyAttributeModifiers(this.armorType, stack, count, modifiers);
				}
			}
		}

		return modifiers;
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		super.setDamage(stack, damage);

		for(IAmphibiousArmorUpgrade upgrade : AmphibiousArmorUpgrades.values()) {
			this.damageUpgrade(stack, upgrade, 1, DamageEvent.ON_DAMAGE, true);
		}
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
