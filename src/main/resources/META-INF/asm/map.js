// noinspection ES6ConvertVarToLetConst

var ASM = Java.type('net.neoforged.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');

var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');

// noinspection JSUnusedGlobalSymbols
function initializeCoreMod() {
    ASM.loadFile('META-INF/asm/util/util.js');
    return {
        'decorations': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.gui.MapRenderer$MapInstance',
                'methodName': 'draw',
                'methodDesc': '(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ZI)V'
            },
            'transformer': function (/*org.objectweb.asm.tree.MethodNode*/ methodNode) {
                var /*org.objectweb.asm.tree.InsnList*/ instructions = methodNode.instructions;
                instructions.insertBefore(
                    findFirstVarInstruction(methodNode, Opcodes.ISTORE, 10),
                    ASM.listOf(
                        new VarInsnNode(Opcodes.ALOAD, 0),
                        new FieldInsnNode(Opcodes.GETFIELD, 'net/minecraft/client/gui/MapRenderer$MapInstance', 'data', 'Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;'),
                        new VarInsnNode(Opcodes.ALOAD, 1),
                        new VarInsnNode(Opcodes.ALOAD, 2),
                        new VarInsnNode(Opcodes.ILOAD, 4),
                        new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            'thebetweenlands/common/ASMHooks',
                            'mapRenderDecorations',
                            '(ILnet/minecraft/world/level/saveddata/maps/MapItemSavedData;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)I',
                            false
                        )
                    )
                );
                return methodNode;
            }
        },
        'render': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.renderer.ItemInHandRenderer',
                'methodName': 'renderArmWithItem',
                'methodDesc': '(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V'
            },
            'transformer': function (/*org.objectweb.asm.tree.MethodNode*/ methodNode) {
                var /*org.objectweb.asm.tree.InsnList*/ instructions = methodNode.instructions;
                var insn = findLastFieldInstruction(methodNode, Opcodes.GETSTATIC, 'net/minecraft/world/item/Items', 'FILLED_MAP');
                if (!insn) {
                    // Must be optifine... Optifine checks for instanceof MapItem, so this patch won't be needed anyway.
                    return methodNode;
                }
                instructions.insert(
                    insn.getNext(),
                    ASM.listOf(
                        new VarInsnNode(Opcodes.ALOAD, 6),
                        new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            'thebetweenlands/common/ASMHooks',
                            'shouldMapRender',
                            '(ZLnet/minecraft/world/item/ItemStack;)Z',
                            false
                        )
                    )
                );
                return methodNode;
            }
        }
    }
}