package thebetweenlands.core;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.tree.AbstractInsnNode.*;
import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class TheBetweenlandsClassTransformer implements IClassTransformer {
	public static final String SLEEP_PER_TICK = "sleepPerTick";

	@Override
	public byte[] transform(String name, String transformedName, byte[] classBytes) {
		boolean obf = false;
		if ("net.minecraft.server.MinecraftServer".equals(name)) {
			return writeClass(transformMinecraftServer(readClass(classBytes)));
		} else if ((obf = "yz".equals(name)) || "net.minecraft.entity.player.EntityPlayer".equals(name)) {
			return writeClass(transformEntityPlayer(readClass(classBytes), obf));
		} else if ((obf = "sv".equals(name)) || "net.minecraft.entity.EntityLivingBase".equals(name)) {
			return writeClass(transformEntityLivingBase(readClass(classBytes), obf));
		} else if ((obf = "bao".equals(name)) || "net.minecraft.client.Minecraft".equals(name)) {
			return writeClass(transformMinecraft(readClass(classBytes), obf));
		} else if ((obf = "bdw".equals(name)) || "net.minecraft.client.gui.GuiScreen".equals(name)) {
			return writeClass(transformGuiScreen(readClass(classBytes), obf));
		} else if ((obf = "oi".equals(name)) || "net.minecraft.server.management.ServerConfigurationManager".equals(name)) {
			return writeClass(transformServerConfigurationManager(readClass(classBytes), obf));
		}
		return classBytes;
	}

	private ClassNode readClass(byte[] classBytes) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(classBytes);
		classReader.accept(classNode, 0);
		return classNode;
	}

	private byte[] writeClass(ClassNode classNode) {
		ClassWriter classWriter = new ClassWriter(0);
		classNode.accept(classWriter);
		return classWriter.toByteArray();
	}

	private ClassNode transformMinecraftServer(ClassNode classNode) {
		FieldNode sleepPerTickField = new FieldNode(ACC_PUBLIC, SLEEP_PER_TICK, "J", null, null);
		classNode.fields.add(sleepPerTickField);
		boolean needsRun = true, needsInit = true;
		for (MethodNode method : classNode.methods) {
			if (needsInit && "<init>".equals(method.name)) {
				needsInit = false;
				for (int i = 0; i < method.instructions.size(); i++) {
					AbstractInsnNode insnNode = method.instructions.get(i);
					if (insnNode.getOpcode() == RETURN) {
						InsnList initSleepPerTickInsns = new InsnList();
						initSleepPerTickInsns.add(new VarInsnNode(ALOAD, 0));
						initSleepPerTickInsns.add(new LdcInsnNode(50L));
						initSleepPerTickInsns.add(new FieldInsnNode(PUTFIELD, "net/minecraft/server/MinecraftServer", SLEEP_PER_TICK, "J"));
						method.instructions.insertBefore(insnNode, initSleepPerTickInsns);
						break;
					}
				}
			}
			if (needsRun && "run".equals(method.name)) {
				needsRun = false;
				Long fifty = Long.valueOf(50);
				for (int i = 0; i < method.instructions.size(); i++) {
					AbstractInsnNode insnNode = method.instructions.get(i);
					if (insnNode.getType() == LDC_INSN && fifty.equals(((LdcInsnNode) insnNode).cst)) {
						InsnList accessSleepPerTickInsns = new InsnList();
						accessSleepPerTickInsns.add(new VarInsnNode(ALOAD, 0));
						accessSleepPerTickInsns.add(new FieldInsnNode(GETFIELD, "net/minecraft/server/MinecraftServer", SLEEP_PER_TICK, "J"));
						method.instructions.insert(insnNode, accessSleepPerTickInsns);
						method.instructions.remove(insnNode);
						break;
					}
				}
			}
			if (!needsRun && !needsInit) {
				break;
			}
		}
		return classNode;
	}

	private ClassNode transformEntityPlayer(ClassNode classNode, boolean obf) {
		String getHurtSoundName = obf ? "aT" : "getHurtSound";
		String entityPlayerClass = obf ? "yz" : "net/minecraft/entity/player/EntityPlayer";
		for (MethodNode method : classNode.methods) {
			if (getHurtSoundName.equals(method.name) && "()Ljava/lang/String;".equals(method.desc)) {
				InsnList insns = method.instructions;
				insns.clear();
				insns.add(new VarInsnNode(ALOAD, 0));
				insns.add(new MethodInsnNode(INVOKESTATIC, "thebetweenlands/forgeevent/BLForgeHooks", "onPlayerGetHurtSound", String.format("(L%s;)Ljava/lang/String;", entityPlayerClass), false));
				insns.add(new InsnNode(ARETURN));
				break;
			}
		}
		return classNode;
	}

	private ClassNode transformEntityLivingBase(ClassNode classNode, boolean obf) {
		String setAttackTargetName = obf ? "b" : "setRevengeTarget";
		String entityLivingBaseClass = obf ? "sv" : "net/minecraft/entity/EntityLivingBase";
		String setAttackTargetDescription = String.format("(L%s;)V", entityLivingBaseClass);
		for (MethodNode method : classNode.methods) {
			if (setAttackTargetName.equals(method.name) && setAttackTargetDescription.equals(method.desc)) {
				for (int i = 0; i < method.instructions.size(); i++) {
					AbstractInsnNode insnNode = method.instructions.get(i);
					if (insnNode.getType() == METHOD_INSN) {
						MethodInsnNode methodNode = (MethodInsnNode) insnNode;
						methodNode.owner = "thebetweenlands/forgeevent/BLForgeHooks";
						methodNode.name = "onLivingSetRevengeTarget";
						break;
					}
				}
				break;
			}
		}
		return classNode;
	}

	private ClassNode transformMinecraft(ClassNode classNode, boolean obf) {
		String startGameName = obf ? "ag" : "startGame";
		for (MethodNode method : classNode.methods) {
			if (startGameName.equals(method.name) && "()V".equals(method.desc)) {
				for (int i = method.instructions.size() - 1; i >= 0; i--) {
					AbstractInsnNode insnNode = method.instructions.get(i);
					if (insnNode.getOpcode() == RETURN) {
						method.instructions.insertBefore(insnNode, new MethodInsnNode(INVOKESTATIC, "thebetweenlands/event/debugging/DebugHandler", "onMinecraftFinishedStarting", "()V", false));
						break;
					}
				}
				break;
			}
		}
		return classNode;
	}

	private ClassNode transformGuiScreen(ClassNode classNode, boolean obf) {
		String handleInputName = obf ? "p" : "handleInput";
		String handleKeyboardInputName = obf ? "l" : "handleKeyboardInput"; 
		for (MethodNode method : classNode.methods) {
			if (handleInputName.equals(method.name) && "()V".equals(method.desc)) {
				for (int i = 0; i < method.instructions.size(); i++) {
					AbstractInsnNode insnNode = method.instructions.get(i);
					if (insnNode.getOpcode() == INVOKEVIRTUAL) {
						MethodInsnNode methodInsnNode = (MethodInsnNode) insnNode;
						if (handleKeyboardInputName.equals(methodInsnNode.name) && "()V".equals(methodInsnNode.desc)) {
							InsnList insns = new InsnList();
							insns.add(new FieldInsnNode(GETSTATIC, "thebetweenlands/event/debugging/DebugHandler", "INSTANCE", "Lthebetweenlands/event/debugging/DebugHandler;"));
							insns.add(new InsnNode(ACONST_NULL));
							insns.add(new MethodInsnNode(INVOKEVIRTUAL, "thebetweenlands/event/debugging/DebugHandler", "onKeyInput", "(Lcpw/mods/fml/common/gameevent/InputEvent$KeyInputEvent;)V", false));
							method.instructions.insert(insnNode, insns);
							break;
						}
					}
				}
				break;
			}
		}
		return classNode;
	}

	private ClassNode transformServerConfigurationManager(ClassNode classNode, boolean obf) {
		String createPlayerForUserName = obf ? "f" : "createPlayerForUser";
		String entityPlayerMPClass = obf ? "mw" : "net/minecraft/entity/player/EntityPlayerMP";
		String createPlayerForUserDescription = String.format("(Lcom/mojang/authlib/GameProfile;)L%s;", entityPlayerMPClass);
		String serverConfigurationManagerClass = obf ? "oi" : "net/minecraft/server/management/ServerConfigurationManager";
		for (MethodNode method : classNode.methods) {
			if (createPlayerForUserName.equals(method.name) && createPlayerForUserDescription.equals(method.desc)) {
				method.instructions.clear();
				method.localVariables.clear();
				method.visitVarInsn(ALOAD, 0);
				method.visitVarInsn(ALOAD, 1);
				method.visitMethodInsn(INVOKESTATIC, "thebetweenlands/event/debugging/DebugHandler", "createPlayerForUser", String.format("(L%s;Lcom/mojang/authlib/GameProfile;)L%s;", serverConfigurationManagerClass, entityPlayerMPClass), false);
				method.visitInsn(ARETURN);
				break;
			}
		}
		return classNode;
	}
}
