package net.pneumono.entityblock.content;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.pneumono.entityblock.EntityBlockMain;
import net.pneumono.entityblock.network.EditEntityBlockPayload;

import java.util.Objects;

public class EntityBlockScreen extends Screen {
    private static final Component TYPE_TEXT = Component.translatable("block.entity_block.entity_block.type");
    private static final Component NBT_TEXT = Component.translatable("block.entity_block.entity_block.nbt");
    private static final Component POSITION_TEXT = Component.translatable("block.entity_block.entity_block.offset");
    private static final Component FINAL_STATE_TEXT = Component.translatable("block.entity_block.entity_block.final_state");

    private final BlockPos pos;
    private String entityType;
    private String nbt;
    private Vec3i offset;
    private String finalState;

    private EditBox typeBox;
    private EditBox tagBox;
    private EditBox posXBox;
    private EditBox posYBox;
    private EditBox posZBox;
    private EditBox finalStateBox;

    public EntityBlockScreen(BlockPos pos, String entityType, String nbt, Vec3i offset, String finalState) {
        super(Component.translatable(EntityBlockMain.ENTITY_BLOCK.getDescriptionId()));
        this.pos = pos;
        this.entityType = entityType;
        this.nbt = nbt;
        this.offset = offset;
        this.finalState = finalState;
    }

    @Override
    protected void init() {
        this.typeBox = new EditBox(
                this.font,
                this.width / 2 - 153, 40, 300, 20,
                TYPE_TEXT
        );
        this.typeBox.setMaxLength(128);
        this.typeBox.setValue(this.entityType);
        this.typeBox.setResponder(string -> this.entityType = string);
        this.addWidget(this.typeBox);

        this.tagBox = new EditBox(
                this.font,
                this.width / 2 - 153, 80, 300, 20,
                NBT_TEXT
        );
        this.tagBox.setMaxLength(512);
        this.tagBox.setValue(this.nbt);
        this.tagBox.setResponder(string -> this.nbt = string);
        this.addWidget(this.tagBox);

        this.posXBox = new EditBox(
                this.font,
                this.width / 2 - 153, 120, 100, 20,
                Component.translatable("block.entity_block.entity_block.offset.x")
        );
        this.posXBox.setMaxLength(15);
        this.posXBox.setValue(Integer.toString(this.offset.getX()));
        this.posXBox.setResponder(string -> this.offset = new BlockPos(parseInt(string), this.offset.getY(), this.offset.getZ()));
        this.addWidget(this.posXBox);

        this.posYBox = new EditBox(
                this.font,
                this.width / 2 - 53, 120, 100, 20,
                Component.translatable("block.entity_block.entity_block.offset.y")
        );
        this.posYBox.setMaxLength(15);
        this.posYBox.setValue(Integer.toString(this.offset.getY()));
        this.posYBox.setResponder(string -> this.offset = new BlockPos(this.offset.getX(), parseInt(string), this.offset.getZ()));
        this.addWidget(this.posYBox);

        this.posZBox = new EditBox(
                this.font,
                this.width / 2 + 47, 120, 100, 20,
                Component.translatable("block.entity_block.entity_block.offset.z")
        );
        this.posZBox.setMaxLength(15);
        this.posZBox.setValue(Integer.toString(this.offset.getZ()));
        this.posZBox.setResponder(string -> this.offset = new BlockPos(this.offset.getX(), this.offset.getY(), parseInt(string)));
        this.addWidget(this.posZBox);

        this.finalStateBox = new EditBox(
                this.font,
                this.width / 2 - 153, 160, 300, 20,
                NBT_TEXT
        );
        this.finalStateBox.setMaxLength(512);
        this.finalStateBox.setValue(this.finalState);
        this.finalStateBox.setResponder(string -> this.finalState = string);
        this.addWidget(this.finalStateBox);

        this.addRenderableWidget(
                Button.builder(
                        CommonComponents.GUI_DONE,
                        button -> this.onDone()
                ).bounds(this.width / 2 - 4 - 150, 210, 150, 20).build()
        );
        this.addRenderableWidget(
                Button.builder(
                        CommonComponents.GUI_CANCEL,
                        button -> this.onCancel()
                ).bounds(this.width / 2 + 4, 210, 150, 20).build()
        );
    }

    private void onDone() {
        this.updateServer();
        Objects.requireNonNull(this.minecraft).setScreen(null);
    }

    private void onCancel() {
        Objects.requireNonNull(this.minecraft).setScreen(null);
    }

    public void updateServer() {
        ClientPlayNetworking.send(EditEntityBlockPayload.createFinish(
                this.pos,
                this.entityType,
                this.nbt,
                new Vec3i(
                        parseInt(this.posXBox.getValue()),
                        parseInt(this.posYBox.getValue()),
                        parseInt(this.posZBox.getValue())
                ),
                this.finalState
        ));
    }

    private int parseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        super.render(graphics, mouseX, mouseY, deltaTicks);

        graphics.drawCenteredString(this.font, this.title, this.width / 2, 10, CommonColors.WHITE);

        graphics.drawString(this.font, TYPE_TEXT, this.width / 2 - 153, 30, CommonColors.LIGHT_GRAY);
        this.typeBox.render(graphics, mouseX, mouseY, deltaTicks);

        graphics.drawString(this.font, NBT_TEXT, this.width / 2 - 153, 70, CommonColors.LIGHT_GRAY);
        this.tagBox.render(graphics, mouseX, mouseY, deltaTicks);

        graphics.drawString(this.font, POSITION_TEXT, this.width / 2 - 153, 110, CommonColors.LIGHT_GRAY);
        this.posXBox.render(graphics, mouseX, mouseY, deltaTicks);
        this.posYBox.render(graphics, mouseX, mouseY, deltaTicks);
        this.posZBox.render(graphics, mouseX, mouseY, deltaTicks);

        graphics.drawString(this.font, FINAL_STATE_TEXT, this.width / 2 - 153, 150, CommonColors.LIGHT_GRAY);
        this.finalStateBox.render(graphics, mouseX, mouseY, deltaTicks);
    }
}
