package net.pneumono.entityblock.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.pneumono.entityblock.EntityBlockMain;
import net.pneumono.entityblock.content.EntityBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin {
    @Shadow
    @Final
    private List<StructureTemplate.StructureEntityInfo> entityInfoList;

    @Unique
    private final List<StructureTemplate.StructureEntityInfo> entityBlocks = new ArrayList<>();

    @WrapOperation(
            method = "fillFromWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/stream/Stream;anyMatch(Ljava/util/function/Predicate;)Z"
            )
    )
    private boolean findEntityBlocks(
            Stream<Block> instance,
            Predicate<Block> predicate,
            Operation<Boolean> original,
            @Local(argsOnly = true) Level level,
            @Local(ordinal = 4) BlockPos globalPos,
            @Local(ordinal = 5) BlockPos relativePos,
            @Local LocalRef<BlockState> state
    ) {
        BlockState blockState = state.get();
        if (!blockState.is(EntityBlockMain.ENTITY_BLOCK)) return original.call(instance, predicate);

        BlockEntity blockEntity = level.getBlockEntity(globalPos);
        if (blockEntity instanceof EntityBlockEntity entityBlockEntity && entityBlockEntity.hasInfo()) {
            this.entityBlocks.add(entityBlockEntity.getStructureEntityInfo(relativePos));
            Optional<Holder.Reference<Block>> finalState = BuiltInRegistries.BLOCK.get(entityBlockEntity.getFinalState());
            if (finalState.isPresent()) {
                blockState = finalState.get().value().defaultBlockState();
                state.set(blockState);
            }
        }

        return original.call(instance, (Predicate<Block>)blockState::is);
    }

    @WrapOperation(
            method = "fillFromWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"
            )
    )
    private BlockEntity avoidSavingEntityBlockData(Level level, BlockPos pos, Operation<BlockEntity> original) {
        if (level.getBlockEntity(pos) instanceof EntityBlockEntity) {
            return null;
        }
        return original.call(level, pos);
    }

    @Inject(
            method = "fillFromWorld",
            at = @At("RETURN")
    )
    private void saveEntityBlocks(Level level, BlockPos start, Vec3i dimensions, boolean includeEntities, List<Block> ignoredBlocks, CallbackInfo ci) {
        this.entityInfoList.addAll(this.entityBlocks);
        this.entityBlocks.clear();
    }
}
