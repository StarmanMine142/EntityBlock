package net.pneumono.entityblock.content;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.pneumono.entityblock.network.EditEntityBlockPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityBlock extends BaseEntityBlock implements GameMasterBlock {
    public static final MapCodec<EntityBlock> CODEC = simpleCodec(EntityBlock::new);

    @NotNull
    @Override
    protected MapCodec<EntityBlock> codec() {
        return CODEC;
    }

    public EntityBlock(Properties settings) {
        super(settings);
    }

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.canUseGameMasterBlocks() && level.getBlockEntity(pos) instanceof EntityBlockEntity entityBlockEntity) {
            if (player instanceof ServerPlayer serverPlayer) {
                ServerPlayNetworking.send(serverPlayer, EditEntityBlockPayload.createStart(
                        pos,
                        entityBlockEntity.getEntityType().toString(),
                        entityBlockEntity.getTag().toString(),
                        entityBlockEntity.getOffset(),
                        entityBlockEntity.getFinalState().toString()
                ));
            }
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EntityBlockEntity(pos, state);
    }
}
