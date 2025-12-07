package net.pneumono.entityblock.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.pneumono.entityblock.EntityBlockMain;
import org.jetbrains.annotations.NotNull;

public record EditEntityBlockPayload(
        BlockPos pos, String entityType, String nbt, Vec3i offset, String finalState, boolean start
) implements CustomPacketPayload {

    public static final Type<EditEntityBlockPayload> START_ID = new Type<>(EntityBlockMain.id("start_edit_entity_block"));
    public static final Type<EditEntityBlockPayload> FINISH_ID = new Type<>(EntityBlockMain.id("finish_edit_entity_block"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EditEntityBlockPayload> START_CODEC = createCodec(true);
    public static final StreamCodec<RegistryFriendlyByteBuf, EditEntityBlockPayload> FINISH_CODEC = createCodec(false);

    private static StreamCodec<RegistryFriendlyByteBuf, EditEntityBlockPayload> createCodec(boolean start) {
        return StreamCodec.composite(
                BlockPos.STREAM_CODEC, EditEntityBlockPayload::pos,
                ByteBufCodecs.STRING_UTF8, EditEntityBlockPayload::entityType,
                ByteBufCodecs.STRING_UTF8, EditEntityBlockPayload::nbt,
                Vec3i.STREAM_CODEC, EditEntityBlockPayload::offset,
                ByteBufCodecs.STRING_UTF8, EditEntityBlockPayload::finalState,
                (pos, entityType, nbt, offset, finalState) ->
                        new EditEntityBlockPayload(pos, entityType, nbt, offset, finalState, start)
        );
    }

    public static EditEntityBlockPayload createStart(
            BlockPos pos, String entityType, String nbt, Vec3i offset, String finalState
    ) {
        return new EditEntityBlockPayload(pos, entityType, nbt, offset, finalState, true);
    }

    public static EditEntityBlockPayload createFinish(
            BlockPos pos, String entityType, String nbt, Vec3i offset, String finalState
    ) {
        return new EditEntityBlockPayload(pos, entityType, nbt, offset, finalState, false);
    }

    @NotNull
    @Override
    public Type<EditEntityBlockPayload> type() {
        return this.start ? START_ID : FINISH_ID;
    }
}
