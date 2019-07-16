package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.tile.TileEntityAbstractBLFurnace;

import java.util.stream.IntStream;

import static thebetweenlands.common.tile.TileEntityAbstractBLFurnace.FurnaceData;

public abstract class ContainerAbstractBLFurnace extends Container {
    private TileEntityAbstractBLFurnace tileFurnace;
    private FurnaceData[] lastFurnaceData;

    public ContainerAbstractBLFurnace(TileEntityAbstractBLFurnace tileFurnace) {
        this.tileFurnace = tileFurnace;
        this.lastFurnaceData = IntStream.range(0, tileFurnace.getFurnaceAmount()).mapToObj(FurnaceData::new).toArray(FurnaceData[]::new);
    }
	
	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		for (int i = 0; i < tileFurnace.getFurnaceAmount(); i++) {
            FurnaceData data1 = tileFurnace.getFurnaceData(i);

            listener.sendWindowProperty(this, i * 3, data1.getFurnaceCookTime());
            listener.sendWindowProperty(this, i * 3 + 1, data1.getFurnaceBurnTime());
            listener.sendWindowProperty(this, i * 3 + 1, data1.getCurrentItemBurnTime());
        }
	}

	@Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (int i = 0; i < tileFurnace.getFurnaceAmount(); i++) {
            FurnaceData data = tileFurnace.getFurnaceData(i);
            FurnaceData lastData = lastFurnaceData[i];

            for (IContainerListener listener : listeners) {
                if (lastData.getFurnaceCookTime() != data.getFurnaceCookTime())
                    listener.sendWindowProperty(this, i * 3, data.getFurnaceCookTime());

                if (lastData.getFurnaceBurnTime() != data.getFurnaceBurnTime())
                    listener.sendWindowProperty(this, i * 3 + 1, data.getFurnaceBurnTime());

                if (lastData.getCurrentItemBurnTime() != data.getCurrentItemBurnTime())
                    listener.sendWindowProperty(this, i * 3 + 2, data.getCurrentItemBurnTime());
            }
        }

        for (int i = 0; i < tileFurnace.getFurnaceAmount(); i++) {
            lastFurnaceData[i] = tileFurnace.getFurnaceData(i).clone();
        }
    }

	@Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        int index = id / 3;
        int newId = id % 3;

        FurnaceData data = tileFurnace.getFurnaceData(index);
        if (newId == 0)
            data.setFurnaceCookTime(value);
        else if (newId == 1)
            data.setFurnaceBurnTime(value);
        else if (newId == 2)
            data.setCurrentItemBurnTime(value);
    }

	@Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileFurnace.isUsableByPlayer(player);
    }
}
