package net.pneumono.entityblock.network;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.pneumono.entityblock.EntityBlockMain;
import net.pneumono.entityblock.content.EntityBlockEntity;

public class EntityBlockServerNetworking {
    public static void initialize() {
        registerPayloads();
        registerReceivers();
    }

    private static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(EditEntityBlockPayload.START_ID, EditEntityBlockPayload.START_CODEC);
        PayloadTypeRegistry.playC2S().register(EditEntityBlockPayload.FINISH_ID, EditEntityBlockPayload.FINISH_CODEC);
    }

    private static void registerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(EditEntityBlockPayload.FINISH_ID, (payload, context) -> {
            Level level = context.player().level();

            ResourceLocation entityType = ResourceLocation.tryParse(payload.entityType());
            if (entityType == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(entityType)) {
                context.player().sendSystemMessage(Component.translatable(
                        "block.entity_block.entity_block.invalid_id",
                        payload.entityType()
                ));
                return;
            }

            CompoundTag tag;
            try {
                tag = TagParser.parseCompoundFully(payload.nbt());
            } catch (CommandSyntaxException e) {
                context.player().sendSystemMessage(Component.translatable(
                        "block.entity_block.entity_block.invalid_nbt",
                        payload.entityType()
                ));
                return;
            }

            ResourceLocation finalState = ResourceLocation.tryParse(payload.finalState());
            if (finalState == null || !BuiltInRegistries.BLOCK.containsKey(finalState)) {
                context.player().sendSystemMessage(Component.translatable(
                        "block.entity_block.entity_block.invalid_state",
                        payload.entityType()
                ));
                return;
            }

            if (level.getBlockEntity(payload.pos()) instanceof EntityBlockEntity blockEntity) {
                blockEntity.setEntityType(entityType);
                blockEntity.setTag(tag);
                blockEntity.setOffset(payload.offset());
                blockEntity.setFinalState(finalState);
            } else {
                EntityBlockMain.LOGGER.warn("Received invalid Entity Block edit packet");
            }
        });
    }
}
