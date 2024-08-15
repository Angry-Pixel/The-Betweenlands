package thebetweenlands.common.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.DruidStoneBlock;
import thebetweenlands.common.registries.BlockRegistry;

public class BetweenlandsBlockStateProvider extends BlockStateProvider {

	public BetweenlandsBlockStateProvider(PackOutput output, ExistingFileHelper helper) {
		super(output, TheBetweenlands.ID, helper);
	}

	@Override
	protected void registerStatesAndModels() {
		this.druidStone(BlockRegistry.DRUID_STONE_1);
		this.druidStone(BlockRegistry.DRUID_STONE_2);
		this.druidStone(BlockRegistry.DRUID_STONE_3);
		this.druidStone(BlockRegistry.DRUID_STONE_4);
		this.druidStone(BlockRegistry.DRUID_STONE_5);
		this.simpleBlockWithItem(BlockRegistry.DRUID_STONE_6.get(), this.models().getExistingFile(this.blockTexture(Blocks.STONE)));
	}

	private void druidStone(DeferredBlock<Block> stone) {
		ModelFile inactive = this.models().orientable(stone.getId().toString(), this.blockTexture(Blocks.STONE), this.blockTexture(stone.get()), this.blockTexture(Blocks.STONE));
		ModelFile active = this.models().orientable(stone.getId() + "_active", this.blockTexture(Blocks.STONE), this.blockTexture(stone.get()), this.blockTexture(Blocks.STONE));

		this.getVariantBuilder(stone.get()).forAllStates(state -> ConfiguredModel.builder().rotationY((int) state.getValue(DruidStoneBlock.FACING).toYRot()).modelFile(state.getValue(DruidStoneBlock.ACTIVE) ? active : inactive).build());
		this.simpleBlockItem(stone);
	}

	public void simpleBlockItem(DeferredBlock<Block> block) {
		this.itemModels().withExistingParent(block.getId().toString(), this.modLoc("block/" + block.getId().getPath()));
	}
}
