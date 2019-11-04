package thebetweenlands.common.item.misc;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.entity.EntityRopeNode;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.KeyBindRegistry;

public class ItemCavingRope extends Item {
	public ItemCavingRope() {
		this.setCreativeTab(BLCreativeTabs.GEARS);
	}

	@Override
	public EnumActionResult onItemUse( EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if(!world.isRemote) {
			EntityRopeNode connectedRopeNode = null;
			for(Entity e : (List<Entity>) world.loadedEntityList) {
				if(e instanceof EntityRopeNode) {
					EntityRopeNode ropeNode = (EntityRopeNode) e;
					if(ropeNode.getNextNodeByUUID() == player) {
						connectedRopeNode = ropeNode;
						break;
					}
				}
			}
			if(connectedRopeNode == null) {
				EntityRopeNode ropeNode = new EntityRopeNode(world);
				ropeNode.setLocationAndAngles(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, 0, 0);
				ropeNode.setNextNode(player);
				world.spawnEntity(ropeNode);
				if (player instanceof EntityPlayerMP)
					AdvancementCriterionRegistry.CAVINGROPE_PLACED.trigger((EntityPlayerMP) player);
				world.playSound((EntityPlayer)null, ropeNode.posX, ropeNode.posY, ropeNode.posZ, SoundEvents.BLOCK_METAL_STEP, SoundCategory.PLAYERS, 1, 1.5F);
				stack.shrink(1);
			} else {
				if(connectedRopeNode.getDistance(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ) > EntityRopeNode.ROPE_LENGTH) {
					player.sendStatusMessage(new TextComponentTranslation("chat.rope.too_far"), true);
					
					return EnumActionResult.FAIL;
				} else {
					EntityRopeNode ropeNode = connectedRopeNode.extendRope(player, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ);
					world.playSound((EntityPlayer)null, ropeNode.posX, ropeNode.posY, ropeNode.posZ, SoundEvents.BLOCK_METAL_STEP, SoundCategory.PLAYERS, 1, 1.5F);
					stack.shrink(1);
				}
			}
		}
		
		return EnumActionResult.SUCCESS;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.caving_rope", KeyBindRegistry.CONNECT_CAVING_ROPE.getDisplayName(), StringUtils.ticksToElapsedTime(BetweenlandsConfig.GENERAL.cavingRopeDespawnTime * 20)), 0));
	}
}
