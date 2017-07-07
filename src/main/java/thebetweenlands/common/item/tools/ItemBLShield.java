package thebetweenlands.common.item.tools;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

@SuppressWarnings("deprecation")
public class ItemBLShield extends ItemShield {
	private ToolMaterial material;

	public ItemBLShield(ToolMaterial material) {
		super();
		this.material = material;
		this.setMaxDamage(material.getMaxUses() * 2);
		this.setCreativeTab(BLCreativeTabs.GEARS);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return ("" + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		//no op
	}

	@Override
	@SideOnly(Side.CLIENT)
	public CreativeTabs getCreativeTab() {
		return BLCreativeTabs.GEARS; //Minecraft seems to override the creative tab for some reason...
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		if (material == BLMaterialRegistry.TOOL_WEEDWOOD) {
			return repair.getItem() == Item.getItemFromBlock(BlockRegistry.WEEDWOOD);
		} else if (material == BLMaterialRegistry.TOOL_BONE) {
			return repair.getItem() == Item.getItemFromBlock(BlockRegistry.BETWEENSTONE);
		} else if (material == BLMaterialRegistry.TOOL_OCTINE) {
			return repair.getItem() == ItemRegistry.OCTINE_INGOT;
		} else if (material == BLMaterialRegistry.TOOL_VALONITE) {
			return EnumItemMisc.VALONITE_SHARD.isItemOf(repair);
		}
		return false;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        ItemStack itemStack = playerIn.getHeldItem(hand);
		playerIn.setActiveHand(hand);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BLOCK;
	}

	/**
	 * Returns the blocking cooldown
	 * @param stack
	 * @param attacked
	 * @param source
	 * @return
	 */
	public int getShieldBlockingCooldown(ItemStack stack, EntityLivingBase attacked, float damage, DamageSource source) {
		return 0;
	}

	/**
	 * Called when an attack was successfully blocked
	 * @param stack
	 * @param attacked
	 * @param source
	 */
	public void onAttackBlocked(ItemStack stack, EntityLivingBase attacked, float damage, DamageSource source) {
		if(!attacked.world.isRemote && source.getEntity() instanceof EntityLivingBase) {
			EntityLivingBase attacker = (EntityLivingBase) source.getEntity();
			ItemStack activeItem = attacker.getActiveItemStack();
			if(activeItem != null && activeItem.getItem() instanceof ItemAxe) {
				float attackStrength = attacker instanceof EntityPlayer ? ((EntityPlayer)attacker).getCooledAttackStrength(0.5F) : 1.0F;
				float criticalChance = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(attacker) * 0.05F;
				if(attacker.isSprinting() && attackStrength > 0.9F) {
					criticalChance += 0.75F;
				}
				if (attacked.world.rand.nextFloat() < criticalChance) {
					if(attacked instanceof EntityPlayer) {
						((EntityPlayer)attacked).getCooldownTracker().setCooldown(this, 100);
						attacked.stopActiveHand();
					}
					//Shield break sound effect
					attacked.world.setEntityState(attacked, (byte)30);
				}
			}
		}
	}

	/**
	 * Returns the damage for the blocked attack
	 * @param stack
	 * @param attacked
	 * @param source
	 * @return
	 */
	public float getBlockedDamage(ItemStack stack, EntityLivingBase attacked, float damage, DamageSource source) {
		float multiplier = 0.4F - Math.min(this.material.getDamageVsEntity() / 3.0F, 1.0F) * 0.4F;
		return Math.min(damage * multiplier, 8.0F);
	}

	/**
	 * Returns the knockback multiplier for defender
	 * @param stack
	 * @param attacked
	 * @param source
	 * @return
	 */
	public float getDefenderKnockbackMultiplier(ItemStack stack, EntityLivingBase attacked, float damage, DamageSource source) {
		//Uses durability as "weight"
		return 0.6F - Math.min(this.material.getMaxUses() / 2500.0F, 1.0F) * 0.6F;
	}

	/**
	 * Returns the knockback multiplier for the attacker
	 * @param stack
	 * @param attacked
	 * @param source
	 * @return
	 */
	public float getAttackerKnockbackMultiplier(ItemStack stack, EntityLivingBase attacked, float damage, DamageSource source) {
		return 0.6F;
	}

	public static enum EventHandler {
		INSTANCE;

		@SubscribeEvent
		public void onLivingAttacked(LivingAttackEvent event) {
			EntityLivingBase attacked = event.getEntityLiving();
			if(attacked.getActiveItemStack() != null && 
					attacked.getActiveItemStack().getItem() instanceof ItemBLShield &&
					this.canBlockDamageSource(attacked, event.getSource())) {

				//Cancel event
				if(!attacked.world.isRemote)
					event.setCanceled(true);

				EnumHand activeHand = attacked.getActiveHand();
				ItemStack stack = attacked.getActiveItemStack();
				ItemBLShield shield = (ItemBLShield) stack.getItem();

				if(!attacked.world.isRemote) {
					//Stop blocking
					attacked.stopActiveHand();
					//Apply damage with multiplier
					float defenderKbMultiplier = shield.getDefenderKnockbackMultiplier(stack, attacked, event.getAmount(), event.getSource());
					float newDamage = shield.getBlockedDamage(stack, attacked, event.getAmount(), event.getSource());
					if(newDamage > 0.0F) {
						double prevMotionX = attacked.motionX;
						double prevMotionY = attacked.motionY;
						double prevMotionZ = attacked.motionZ;
						attacked.attackEntityFrom(event.getSource(), newDamage);
						attacked.motionX = prevMotionX;
						attacked.motionY = prevMotionY;
						attacked.motionZ = prevMotionZ;
					}
					//Knock back defender
					double prevMotionY = attacked.motionY;
					attacked.knockBack(event.getSource().getEntity(), defenderKbMultiplier, event.getSource().getSourceOfDamage().posX - attacked.posX, event.getSource().getSourceOfDamage().posZ - attacked.posZ);
					attacked.motionY = prevMotionY;
					attacked.velocityChanged = true;
					//Shield block sound effect
					attacked.world.setEntityState(attacked, (byte)29);
					//Set shield active again
					attacked.setActiveHand(activeHand);
				}

				//Knock back attacker
				if(!attacked.world.isRemote)
					if (event.getSource().getSourceOfDamage() instanceof EntityLivingBase) {
						float attackerKbMultiplier = shield.getAttackerKnockbackMultiplier(stack, attacked, event.getAmount(), event.getSource());
						if(attackerKbMultiplier > 0.0F)
							((EntityLivingBase)event.getSource().getSourceOfDamage()).knockBack(attacked, attackerKbMultiplier, attacked.posX - event.getSource().getSourceOfDamage().posX, attacked.posZ - event.getSource().getSourceOfDamage().posZ);
					}

				if(attacked instanceof EntityPlayer) {
					int cooldown = shield.getShieldBlockingCooldown(stack, (EntityPlayer)attacked, event.getAmount(), event.getSource());
					if(cooldown > 0) {
						((EntityPlayer)attacked).getCooldownTracker().setCooldown(shield, cooldown);
						attacked.stopActiveHand();
					}
				}

				shield.onAttackBlocked(stack, attacked, event.getAmount(), event.getSource());

				if(!attacked.world.isRemote) {
					//Damage item
					int itemDamage = 1 + MathHelper.floor(event.getAmount());
					stack.damageItem(itemDamage, attacked);
					//Shield broke
					if (stack.getCount() <= 0) {
						EnumHand enumhand = attacked.getActiveHand();
						if(attacked instanceof EntityPlayer)
							net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem((EntityPlayer)attacked, stack, enumhand);
						if (enumhand == EnumHand.MAIN_HAND)
							attacked.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
						else
							attacked.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, null);
						//Shield break sound effect
						attacked.world.setEntityState(attacked, (byte)30);
					}
				}
			}
		}

		/**
		 * Returns whether the shield is facing the damage source
		 * @param attacked
		 * @param damageSourceIn
		 * @return
		 */
		protected boolean canBlockDamageSource(EntityLivingBase attacked, DamageSource damageSourceIn) {
			if (!damageSourceIn.isUnblockable() && attacked.isActiveItemStackBlocking()) {
				Vec3d vec3d = damageSourceIn.getDamageLocation();
				if (vec3d != null) {
					Vec3d vec3d1 = attacked.getLook(1.0F);
					Vec3d vec3d2 = vec3d.subtractReverse(new Vec3d(attacked.posX, attacked.posY, attacked.posZ)).normalize();
					vec3d2 = new Vec3d(vec3d2.xCoord, 0.0D, vec3d2.zCoord);
					if (vec3d2.dotProduct(vec3d1) < 0.0D) {
						return true;
					}
				}
			}
			return false;
		}
	}
}
