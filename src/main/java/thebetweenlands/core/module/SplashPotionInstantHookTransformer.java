package thebetweenlands.core.module;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class SplashPotionInstantHookTransformer extends TransformerModule {
	public SplashPotionInstantHookTransformer() {
		this.addAcceptedClass("aez", "net.minecraft.entity.projectile.EntityPotion");
	}

	@Override
	public String getName() {
		return "SplashPotionInstantHook";
	}

	@Override
	public boolean acceptsMethod(MethodNode method) {
		return method.name.equals(this.getMappedName("a", "applySplash")) && method.desc.equals(this.getMappedName("(Lbhc;Ljava/util/List;)V", "(Lnet/minecraft/util/math/RayTraceResult;Ljava/util/List;)V"));
	}

	@Override
	public void transformMethodInstruction(MethodNode method, AbstractInsnNode node, int index) {
		if(node instanceof MethodInsnNode) {
			MethodInsnNode methodCallNode = (MethodInsnNode) node;
			if(methodCallNode.name.equals(this.getMappedName("a", "affectEntity")) && methodCallNode.desc.equals(this.getMappedName("(Lvg;Lvg;Lvp;ID)V", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/EntityLivingBase;ID)V"))
					&& methodCallNode.owner.equals(this.getMappedName("uz", "net/minecraft/potion/Potion"))) {
				List<AbstractInsnNode> insertionsBefore = new ArrayList<AbstractInsnNode>();
				List<AbstractInsnNode> insertionsAfter = new ArrayList<AbstractInsnNode>();

				LabelNode jumpIfFalseTarget = new LabelNode();
				LabelNode jumpToEndTarget = new LabelNode();

				insertionsBefore.add(new VarInsnNode(Opcodes.ALOAD, 0));
				insertionsBefore.add(new VarInsnNode(Opcodes.ALOAD, isBukkitServer ? 9 : 6)); //entitylivingbase from "for (EntityLivingBase entitylivingbase : list)"
				insertionsBefore.add(new VarInsnNode(Opcodes.ALOAD, isBukkitServer ? 13 : 12)); //potioneffect from "for (PotionEffect potioneffect : p_190543_2_)"
				insertionsBefore.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "thebetweenlands/common/CommonHooks", "onSplashAffectEntity", this.getMappedName("(Laez;Lvp;Lva;)Z", "(Lnet/minecraft/entity/projectile/EntityPotion;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/PotionEffect;)Z"), false));
				insertionsBefore.add(new JumpInsnNode(Opcodes.IFNE, jumpIfFalseTarget));

				this.insertBefore(method, node, insertionsBefore);

				insertionsAfter.add(new JumpInsnNode(Opcodes.GOTO, jumpToEndTarget));
				insertionsAfter.add(jumpIfFalseTarget);
				insertionsAfter.add(new InsnNode(Opcodes.POP2));
				insertionsAfter.add(new InsnNode(Opcodes.POP2));
				insertionsAfter.add(new InsnNode(Opcodes.POP2));
				insertionsAfter.add(new InsnNode(Opcodes.POP));
				insertionsAfter.add(jumpToEndTarget);

				this.insertAfter(method, node, insertionsAfter);

				this.setSuccessful();
			}
		}
	}
}
