package thebetweenlands.common.item.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;

public class ItemGreataxe extends ItemGreatsword {
	public ItemGreataxe(ToolMaterial material) {
		super(material);
		this.setHarvestLevel("axe", 3);
		this.setCreativeTab(BLCreativeTabs.GEARS);
		this.setMaxDamage(material.getMaxUses() * 2);
	}

	protected double getBlockBreakHalfAngle(EntityLivingBase entity, ItemStack stack) {
		return 45.0D;
	}

	protected double getBlockBreakReach(EntityLivingBase entity, ItemStack stack) {
		return 2.6D;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity holder, int slot, boolean isHeldItem) {
		super.onUpdate(stack, world, holder, slot, isHeldItem);

		if(holder instanceof EntityPlayerMP && !holder.world.isRemote) {
			EntityPlayerMP player = (EntityPlayerMP) holder;

			if(player.getHeldItemMainhand() == stack && this.isLongSwingInProgress(stack) && this.getSwingStartCooledAttackStrength(stack) > 0.85F) {
				int ticksElapsed = player.ticksExisted - this.getSwingStartTicks(stack) - 1;

				float longSwingTickProgress = 1.0F / (this.getLongSwingDuration(player, stack) - 1);
				float longSwingProgressEnd = (ticksElapsed + 1) / (this.getLongSwingDuration(player, stack) - 1);

				List<BlockPos> targetBlocks = new ArrayList<>();

				for(float longSwingProgressStart = Math.max(0, longSwingProgressEnd - Math.max(0.25F, longSwingTickProgress)); longSwingProgressStart < longSwingProgressEnd; longSwingProgressStart += longSwingTickProgress) {
					double breakReach = this.getBlockBreakReach(player, stack);
					int blockReach = MathHelper.ceil(breakReach);

					double breakHalfAngle = this.getBlockBreakHalfAngle(player, stack);

					double minAngle = -breakHalfAngle + breakHalfAngle * 2 * longSwingProgressStart;
					double maxAngle = -breakHalfAngle + breakHalfAngle * 2 * longSwingProgressEnd;

					float yaw = player.rotationYaw;
					float pitch = player.rotationPitch;

					float yc = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
					float ys = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
					float pc = -MathHelper.cos(-pitch * 0.017453292F);
					float ps = MathHelper.sin(-pitch * 0.017453292F);

					Vec3d forward = new Vec3d((double)(ys * pc), (double)ps, (double)(yc * pc)).normalize();

					pc = -MathHelper.cos(-(pitch - 90) * 0.017453292F);
					ps = MathHelper.sin(-(pitch - 90) * 0.017453292F);

					Vec3d up = new Vec3d((double)(ys * pc), (double)ps, (double)(yc * pc)).normalize();

					Vec3d right = forward.crossProduct(up);

					for(int xo = -blockReach; xo <= blockReach; xo++) {
						for(int yo = -blockReach; yo <= blockReach; yo++) {
							for(int zo = -blockReach; zo <= blockReach; zo++) {
								BlockPos pos = new BlockPos(player.posX + xo, player.posY + player.height * 0.5D + yo, player.posZ + zo);
								Vec3d center = new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

								double dist = center.distanceTo(player.getPositionEyes(1));

								if(dist < breakReach) {
									Vec3d dir = center.subtract(player.getPositionEyes(1)).normalize();

									double py = forward.dotProduct(dir);
									double px = right.dotProduct(dir);

									double angle = Math.toDegrees(-Math.atan2(px, py));

									if(angle >= minAngle && angle < maxAngle) {
										double distUp = up.dotProduct(new Vec3d(center.x - player.posX, center.y - player.posY - player.getEyeHeight(), center.z - player.posZ));

										double verticalRange = 1.0D + 1.5D * (3 - MathHelper.clamp(dist, 0, 3)) / 3.0D;

										if(distUp >= -verticalRange - 0.5D && distUp <= verticalRange - 0.5D) {
											IBlockState state = player.world.getBlockState(pos);

											if((state.getBlock().isWood(player.world, pos) || state.getMaterial() == Material.WOOD) && state.getBlockHardness(player.world, pos) <= 2.25F &&
													state.getPlayerRelativeBlockHardness(player, player.world, pos) > 0.01F) {
												targetBlocks.add(pos);
											}
										}
									}
								}
							}
						}
					}
				}

				if(!targetBlocks.isEmpty()) {
					Collections.shuffle(targetBlocks, player.world.rand);

					int playedEffects = 0;
					for(BlockPos pos : targetBlocks) {
						if(!world.isAirBlock(pos)) {
							IBlockState state = player.world.getBlockState(pos);

							if(player.interactionManager.tryHarvestBlock(pos)) {
								if(++playedEffects <= 3) {
									player.world.playEvent(null, 2001, pos, Block.getStateId(state));
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public float getSwingSpeedMultiplier(EntityLivingBase entity, ItemStack stack) {
		return 0.14F;
	}

	@Override
	protected double getAoEReach(EntityLivingBase entityLiving, ItemStack stack) {
		return 0;
	}

	@Override
	public double getReach() {
		return 2.5D;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

		if(equipmentSlot == EntityEquipmentSlot.MAINHAND) {
			multimap.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -3.3D, 0));
		}

		return multimap;
	}

	@Override
	public float getAttackDamage() {
		return super.getAttackDamage() + 2.0f;
	}

	@Override
	public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
		return true;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.COMMON;
	}
}
