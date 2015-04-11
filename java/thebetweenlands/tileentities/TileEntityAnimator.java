package thebetweenlands.tileentities;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.packets.PacketAnimatorProgress;
import thebetweenlands.utils.network.SubscribePacket;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class TileEntityAnimator extends TileEntity {
	//Progress (0-100). Used for rendering
	public int progress;
	
	public float crystalVelocity = 0.0F;
	public float crystalRotation = 0.0F;
	
	@Override
	public void updateEntity() {
		if(this.worldObj.isRemote) {
			this.crystalVelocity -= Math.signum(this.crystalVelocity) * 0.05F;
			this.crystalRotation += this.crystalVelocity;
			if(this.crystalRotation >= 360.0F) {
				this.crystalRotation -= 360.0F;
			} else if(this.crystalRotation <= 360.0F) {
				this.crystalRotation += 360.0F;
			}
			if(Math.abs(this.crystalVelocity) <= 1.0F && this.getWorldObj().rand.nextInt(15) == 0) {
				this.crystalVelocity = this.worldObj.rand.nextFloat() * 18.0F - 9.0F;
			}
			if(this.progress != 0) {
				this.progress++;
				if(this.progress >= 100) {
					this.progress = 0;
				}
			}
		} else {
			
			//FIXME: Just testing
			this.progress++;
			if(this.progress >= 100) {
				this.progress = 0;
			}
			
			this.sendProgressPacket();
		}
	}
	
	public void sendProgressPacket() {
		World world = this.getWorldObj();
		int dim = 0;
		if(world instanceof WorldServer) {
			dim = ((WorldServer)world).provider.dimensionId;
		}
		TheBetweenlands.networkWrapper.sendToAllAround(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketAnimatorProgress(this)), new TargetPoint(dim, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, 64D));
	}
	
	@SubscribePacket
	public static void onProgressPacket(PacketAnimatorProgress pkt) {
		TileEntity te = FMLClientHandler.instance().getWorldClient().getTileEntity(pkt.x, pkt.y, pkt.z);
		if(te instanceof TileEntityAnimator) {
			TileEntityAnimator tile = (TileEntityAnimator) te;
			tile.progress = pkt.progress;
		}
	}
}
