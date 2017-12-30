package thebetweenlands.common.block.misc;

import java.util.List;

import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockPressurePlateBetweenlands extends BlockPressurePlate {

	public static enum PressurePlateSensitivity {
		EVERYTHING(Sensitivity.EVERYTHING),
		MOBS(Sensitivity.MOBS),
		PLAYERS(EnumHelper.addSensitivity("PLAYERS"));

		private final Sensitivity sensitivity;

		private PressurePlateSensitivity(Sensitivity sensitivity) {
			this.sensitivity = sensitivity;
		}
	}

	public final Sensitivity sensitivity;

	public BlockPressurePlateBetweenlands(Material material, PressurePlateSensitivity sensitivity) {
		super(material, sensitivity.sensitivity);
		this.sensitivity = sensitivity.sensitivity;
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public BlockPressurePlateBetweenlands setSoundType(SoundType type) {
		super.setSoundType(type);
		return this;
	}
	
	@Override
	protected int computeRedstoneStrength(World worldIn, BlockPos pos) {
		AxisAlignedBB axisalignedbb = PRESSURE_AABB.offset(pos);
		List <? extends Entity > list;

		if(this.sensitivity == PressurePlateSensitivity.EVERYTHING.sensitivity) {
			list = worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, axisalignedbb);
		} else if(this.sensitivity == PressurePlateSensitivity.MOBS.sensitivity) {
			list = worldIn.<Entity>getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
		} else if(this.sensitivity == PressurePlateSensitivity.PLAYERS.sensitivity) {
			list = worldIn.<Entity>getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
		} else {
			return 0;
		}

		if (!list.isEmpty()) {
			for (Entity entity : list) {
				if (!entity.doesEntityNotTriggerPressurePlate()) {
					return 15;
				}
			}
		}

		return 0;
	}
}
