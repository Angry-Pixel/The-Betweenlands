package thebetweenlands.client.renderer.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.state.properties.ChestType;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.WeedwoodChestBlockEntity;

import java.util.EnumMap;

public class WeedwoodChestRenderer extends ChestRenderer<WeedwoodChestBlockEntity> {

	private static final EnumMap<ChestType, Material> MATERIAL_MAP = Maps.newEnumMap(ImmutableMap.of(
		ChestType.SINGLE, new Material(Sheets.CHEST_SHEET, TheBetweenlands.prefix("entity/chest/weedwood_chest")),
		ChestType.LEFT, new Material(Sheets.CHEST_SHEET, TheBetweenlands.prefix("entity/chest/weedwood_chest_left")),
		ChestType.RIGHT, new Material(Sheets.CHEST_SHEET, TheBetweenlands.prefix("entity/chest/weedwood_chest_right"))
	));

	public WeedwoodChestRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected Material getMaterial(WeedwoodChestBlockEntity blockEntity, ChestType chestType) {
		return MATERIAL_MAP.get(chestType);
	}
}
