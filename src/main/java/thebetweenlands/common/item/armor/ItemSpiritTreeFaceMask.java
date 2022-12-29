package thebetweenlands.common.item.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemSpiritTreeFaceMask extends ItemBLArmor {
	public static interface EntityFactory {
		public EntityHanging create(World world, BlockPos pos, EnumFacing clickedSide);
	}

	private final EntityFactory factory;

	@SideOnly(Side.CLIENT)
	private ModelBiped model;

	public ItemSpiritTreeFaceMask(String armorName, EntityFactory factory) {
		super(BLMaterialRegistry.ARMOR_DECORATIVE, 2, EntityEquipmentSlot.HEAD, armorName);
		this.setMaxDamage(0);
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
		this.factory = factory;
	}

	@Override
	public int getItemEnchantability() {
		return 0;
	}

	@Override
	public boolean isBookEnchantable(ItemStack is, ItemStack book) {
		return false;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack itemstack = player.getHeldItem(hand);
		BlockPos offsetPos = pos.offset(facing);

		if (facing != EnumFacing.DOWN && facing != EnumFacing.UP && player.canPlayerEdit(offsetPos, facing, itemstack)) {
			EntityHanging entity = this.factory.create(world, offsetPos, facing);

			if (entity != null && entity.onValidSurface()) {
				if (!world.isRemote) {
					entity.playPlaceSound();
					world.spawnEntity(entity);
				}

				itemstack.shrink(1);
			}

			return EnumActionResult.SUCCESS;
		} else {
			return EnumActionResult.FAIL;
		}
	}
}
