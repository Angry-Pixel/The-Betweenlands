package thebetweenlands.client.render.model.baked;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityJellyfish;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.registries.ItemRegistry;

public class ModelItemJellyfish extends ModelItemMultiPart {
	public ModelItemJellyfish() {
		super(new PartsMapper() {
			@Override
			public Map<String, String> getPartsMapping(ItemStack stack, World world, EntityLivingBase entity) {
				if(stack.getItem() == ItemRegistry.JELLYFISH) {
					Entity itemEntity = ((ItemMob) stack.getItem()).createCapturedEntity(Minecraft.getMinecraft().world, 0, 0, 0, stack, false);

					if(itemEntity instanceof EntityJellyfish) {
						EntityJellyfish jellyfish = (EntityJellyfish) itemEntity;

						Map<String, String> mapping = new HashMap<>();
						mapping.put("colour", String.valueOf(jellyfish.getJellyfishColour()));
						return mapping;
					}
				}

				return null;
			}
		}, new ResourceLocation("minecraft:items/fish_cod_raw"));
	}
}
