package thebetweenlands.common.item.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Multimap;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.client.tab.BLCreativeTabs;

public class ItemGreataxe extends ItemAncientGreatsword {
	public ItemGreataxe(ToolMaterial material) {
		super(material);
		this.setHarvestLevel("axe", 0);
		this.setCreativeTab(BLCreativeTabs.GEARS);
	}

	protected double getBlockBreakHalfAngle(EntityLivingBase entity, ItemStack stack) {
		return 45.0D;
	}

	protected double getBlockBreakReach(EntityLivingBase entity, ItemStack stack) {
		return 2.6D;
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		if(entityLiving instanceof EntityPlayerMP && !entityLiving.world.isRemote) {
			EntityPlayerMP player = (EntityPlayerMP) entityLiving;

			if(player.getCooledAttackStrength(0) > 0.85F) {
				double breakReach = this.getBlockBreakReach(entityLiving, stack);
				int blockReach = MathHelper.ceil(breakReach);

				double breakHalfAngle = this.getBlockBreakHalfAngle(entityLiving, stack);

				List<SoundType> blockSoundTypes = new ArrayList<>();

				for(int xo = -blockReach; xo <= blockReach; xo++) {
					for(int yo = -blockReach; yo <= blockReach; yo++) {
						for(int zo = -blockReach; zo <= blockReach; zo++) {
							BlockPos pos = new BlockPos(entityLiving.posX + xo, entityLiving.posY + entityLiving.height * 0.5D + yo, entityLiving.posZ + zo);
							Vec3d center = new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

							double dist = center.distanceTo(entityLiving.getPositionEyes(1));

							if(dist < breakReach) {
								double angle = Math.toDegrees(Math.acos(center.subtract(entityLiving.getPositionEyes(1)).normalize().dotProduct(entityLiving.getLookVec())));

								if(angle < breakHalfAngle) {
									double distXZ = Math.sqrt((center.x - entityLiving.posX)*(center.x - entityLiving.posX) + (center.z - entityLiving.posZ)*(center.z - entityLiving.posZ));

									if(entityLiving.getLookVec().y * distXZ + entityLiving.posY + entityLiving.height / 2 + 0.25D > pos.getY() - 0.25D && entityLiving.getLookVec().y * distXZ + entityLiving.posY + entityLiving.height / 2 + 0.25D < pos.getY() + 1.25D) {
										IBlockState state = entityLiving.world.getBlockState(pos);

										if(state.getBlock().isWood(entityLiving.world, pos) && state.getBlockHardness(entityLiving.world, pos) <= 2.25F &&
												state.getPlayerRelativeBlockHardness(player, entityLiving.world, pos) > 0.01F) {
											blockSoundTypes.add(state.getBlock().getSoundType(state, player.world, pos, player));

											player.interactionManager.tryHarvestBlock(pos);
										}
									}
								}
							}
						}
					}
				}

				if(!blockSoundTypes.isEmpty()) {
					Collections.shuffle(blockSoundTypes, player.world.rand);

					int playedSounds = 0;
					for(SoundType blockSoundType : blockSoundTypes) {
						player.world.playSound(null, player.posX, player.posY, player.posZ, blockSoundType.getBreakSound(), SoundCategory.BLOCKS, (blockSoundType.getVolume() + 1.0F) / 1.3F, blockSoundType.getPitch() * 0.8F + player.world.rand.nextFloat() * 0.2F - 0.1F);

						if(++playedSounds >= 3) {
							break;
						}
					}
				}
			}
		}

		return super.onEntitySwing(entityLiving, stack);
	}

	@Override
	protected float getSwingSpeedMultiplier(EntityLivingBase entity, ItemStack stack) {
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
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -3.45D, 0));
		}

		return multimap;
	}

	@Override
	public float getAttackDamage() {
		return super.getAttackDamage() + 2.0f;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.COMMON;
	}
}
