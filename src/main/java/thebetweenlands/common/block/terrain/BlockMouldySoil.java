package thebetweenlands.common.block.terrain;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.world.storage.location.LocationSporeHive;

public class BlockMouldySoil extends BasicBlock {

	public BlockMouldySoil() {
		super(Material.GROUND);
		this.setDefaultCreativeTab()
		.setHarvestLevel2("shovel", 0)
		.setSoundType2(SoundType.GROUND)
		.setHardness(0.5F);
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
		super.onEntityWalk(worldIn, pos, entityIn);

		if(worldIn.isRemote) {
			this.updateParticle(worldIn, pos, entityIn);
		}
	}

	@SideOnly(Side.CLIENT)
	private void updateParticle(World worldIn, BlockPos pos, Entity entityIn) {
		Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();

		if(entityIn instanceof EntityPlayer && renderView != null && renderView.getDistanceSq(pos) < 64 && LocationSporeHive.getAtBlock(worldIn, pos) != null) {
			NBTTagCompound nbt = entityIn.getEntityData();

			float nextStep = nbt.getFloat("thebetweenlands.mouldySoil.nextStep");

			if(entityIn.distanceWalkedOnStepModified > nextStep) {
				BLParticles.MOULD_THROBBING.spawn(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

				nbt.setFloat("thebetweenlands.mouldySoil.nextStep", entityIn.distanceWalkedOnStepModified + 3);
			}
		}
	}

}
