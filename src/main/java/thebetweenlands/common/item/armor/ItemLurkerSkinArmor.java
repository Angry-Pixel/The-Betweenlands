package thebetweenlands.common.item.armor;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemLurkerSkinArmor extends ItemBLArmor {
	public ItemLurkerSkinArmor(EntityEquipmentSlot slot) {
		super(BLMaterialRegistry.ARMOR_LURKER_SKIN, 3, slot, "lurker_skin");

		this.setGemArmorTextureOverride(CircleGemType.AQUA, "lurker_skin_aqua");
		this.setGemArmorTextureOverride(CircleGemType.CRIMSON, "lurker_skin_crimson");
		this.setGemArmorTextureOverride(CircleGemType.GREEN, "lurker_skin_green");
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		if(!player.isSpectator()) {
			NonNullList<ItemStack> armor = player.inventory.armorInventory;
			int armorPieces = 0;
	
			for (ItemStack anArmor : armor) {
				if (anArmor != null && anArmor.getItem() instanceof ItemLurkerSkinArmor) {
					armorPieces += 1;
				}
			}
	
			if (itemStack.getItem() == ItemRegistry.LURKER_SKIN_BOOTS && player.isInWater()) {
				IBlockState blockState = player.world.getBlockState(new BlockPos(player.posX, player.getEntityBoundingBox().maxY + 0.1D, player.posZ));
				boolean fullyInWater = blockState.getMaterial().isLiquid();
	
				if(fullyInWater) {
					if(!player.isSneaking() && player.moveForward == 0) {
						player.motionY = Math.sin(player.ticksExisted / 5.0F) * 0.016D;
					}
	
					if(player.moveForward != 0) {
						if(player.moveForward > 0) {
							Vec3d lookVec = player.getLookVec().normalize();
							double speed = 0.01D + 0.05D / 4.0D * armorPieces;
							player.motionX += lookVec.x * player.moveForward * speed;
							player.motionZ += lookVec.z * player.moveForward * speed;
							player.motionY += lookVec.y * player.moveForward * speed;
							player.getFoodStats().addExhaustion(0.0024F);
						}
						player.motionY += 0.02D;
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
}
