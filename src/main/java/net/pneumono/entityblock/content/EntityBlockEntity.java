package net.pneumono.entityblock.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.pneumono.entityblock.EntityBlockMain;

public class EntityBlockEntity extends BlockEntity implements Spawner {
    public static final ResourceLocation EMPTY_TYPE = ResourceLocation.withDefaultNamespace("empty");
    public static final ResourceLocation EMPTY_BLOCK = BuiltInRegistries.BLOCK.getKey(Blocks.AIR);

    private ResourceLocation entityType = EMPTY_TYPE;
    private CompoundTag tag = new CompoundTag();
    private Vec3i offset = Vec3i.ZERO;
    private ResourceLocation finalState = EMPTY_BLOCK;

    public EntityBlockEntity(BlockPos pos, BlockState state) {
        super(EntityBlockMain.ENTITY_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);
        this.entityType = view.read("entity_id", ResourceLocation.CODEC).orElse(EMPTY_TYPE);
        this.tag = view.read("entity_tag", CompoundTag.CODEC).orElse(new CompoundTag());
        this.offset = view.read("entity_offset", Vec3i.CODEC).orElse(Vec3i.ZERO);
        this.finalState = view.read("final_state", ResourceLocation.CODEC).orElse(EMPTY_BLOCK);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        view.store("entity_id", ResourceLocation.CODEC, this.entityType);
        view.store("entity_tag", CompoundTag.CODEC, this.tag);
        view.store("entity_offset", Vec3i.CODEC, this.offset);
        view.store("final_state", ResourceLocation.CODEC, this.finalState);
    }

    public boolean hasInfo() {
        return !this.entityType.equals(EMPTY_TYPE);
    }

    public StructureTemplate.StructureEntityInfo getStructureEntityInfo(BlockPos relativePos) {
        CompoundTag nbt = getTag().copy();
        nbt.store("id", ResourceLocation.CODEC, getEntityType());

        return new StructureTemplate.StructureEntityInfo(
                Vec3.atBottomCenterOf(relativePos.offset(getOffset())),
                relativePos,
                nbt
        );
    }

    @Override
    public void setEntityId(EntityType<?> entityType, RandomSource random) {
        setEntityType(entityType == null ? EMPTY_TYPE : EntityType.getKey(entityType));
    }

    public ResourceLocation getEntityType() {
        return this.entityType;
    }

    public CompoundTag getTag() {
        return this.tag;
    }

    public Vec3i getOffset() {
        return offset;
    }

    public ResourceLocation getFinalState() {
        return finalState;
    }

    public void setEntityType(ResourceLocation entityType) {
        this.entityType = entityType;
        setChanged();
    }

    public void setTag(CompoundTag tag) {
        this.tag = tag;
        setChanged();
    }

    public void setOffset(Vec3i offset) {
        this.offset = offset;
    }

    public void setFinalState(ResourceLocation finalState) {
        this.finalState = finalState;
    }
}
