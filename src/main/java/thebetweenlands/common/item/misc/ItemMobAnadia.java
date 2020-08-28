package thebetweenlands.common.item.misc;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.mobs.EntityAnadia;

public class ItemMobAnadia extends ItemMob {
	/**
	 * @param maxStackSize Max stack size of the item. If this is > 1 then only the entity's ID and no additional NBT is stored.
	 * @param defaultMob Default mob type of this item
	 * @param defaultMobSetter Sets the properties of the default mob
	 */
	@SuppressWarnings("unchecked")
	public <T extends Entity> ItemMobAnadia(int maxStackSize, @Nullable Class<T> defaultMob, @Nullable Consumer<T> defaultMobSetter) {
		super(1, defaultMob, defaultMobSetter);
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if(!world.isRemote) {
			Entity entity = this.createCapturedEntity(world, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, stack);
			if(entity != null) {
				if(entity instanceof EntityAnadia && ((EntityAnadia) entity).getFishColour() == 2)
					return EnumActionResult.PASS;
				
				if(facing.getXOffset() != 0) {
					entity.setPosition(entity.posX + facing.getXOffset() * entity.width * 0.5f, entity.posY, entity.posZ);
				}
				if(facing.getYOffset() < 0) {
					entity.setPosition(entity.posX, entity.posY - entity.height, entity.posZ);
				}
				if(facing.getZOffset() != 0) {
					entity.setPosition(entity.posX, entity.posY, entity.posZ + facing.getZOffset() * entity.width * 0.5f);
				}

				if(world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty()) {
					this.spawnCapturedEntity(player, world, entity);
					stack.shrink(1);
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return EnumActionResult.SUCCESS;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if(worldIn != null) {
			Entity entity = this.createCapturedEntity(worldIn, 0, 0, 0, stack);
			if(entity instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase) entity;
				if (living instanceof EntityAnadia) {
					if(((EntityAnadia) living).isBeingRidden())
						((EntityAnadia) living).removePassengers();
					tooltip.add(I18n.format(living.getName()));
					tooltip.add(I18n.format("Colour" + ((EntityAnadia) living).getFishColour(), ((EntityAnadia) living).getFishColour()));
					tooltip.add(I18n.format("tooltip.bl.item_mob.health", MathHelper.ceil(living.getHealth()), MathHelper.ceil((living.getMaxHealth()))));
					tooltip.add(I18n.format("tooltip.bl.item_mob.size", ((EntityAnadia) living).getFishSize()));
					tooltip.add(I18n.format("tooltip.bl.item_mob.speed", (living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue())));
					tooltip.add(I18n.format("tooltip.bl.item_mob.strength", (living.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue())));
					tooltip.add(I18n.format("tooltip.bl.item_mob.stamina", ((EntityAnadia) living).getStaminaMods()));
				}
				else
					tooltip.add(I18n.format("tooltip.bl.item_mob.health", MathHelper.ceil(living.getHealth() / 2), MathHelper.ceil(living.getMaxHealth() / 2)));
			}
		}
	}
}
