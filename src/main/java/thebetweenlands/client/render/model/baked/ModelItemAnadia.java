package thebetweenlands.client.render.model.baked;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.item.misc.ItemMobAnadia;
import thebetweenlands.common.registries.ItemRegistry;

public class ModelItemAnadia extends ModelItemMultiPart {
	public ModelItemAnadia() {
		super(new PartsMapper() {
			@Override
			public Map<String, String> getPartsMapping(ItemStack stack, World world, EntityLivingBase entity) {
				if(stack.getItem() == ItemRegistry.ANADIA) {
					Entity itemEntity = ((ItemMobAnadia) stack.getItem()).createCapturedEntity(Minecraft.getMinecraft().world, 0, 0, 0, stack);

					if(itemEntity instanceof EntityAnadia) {
						EntityAnadia anadia = (EntityAnadia) itemEntity;

						Map<String, String> mapping = new HashMap<>();
						mapping.put("head", String.valueOf(anadia.getHeadType()));
						mapping.put("body", String.valueOf(anadia.getBodyType()));
						mapping.put("tail", String.valueOf(anadia.getTailType()));
						return mapping;
					}
				}

				return null;
			}
		}, new ResourceLocation("minecraft:items/fish_cod_raw"));
	}
}
