package com.github.sib_energy_craft.item_filter.screen;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public class ItemFilterItemScreen extends HandledScreen<ItemFilterItemScreenHandler> {
    private static final Identifier TEXTURE = Identifiers.of("textures/gui/container/item_filter.png");

    public ItemFilterItemScreen(@NotNull ItemFilterItemScreenHandler handler,
                                @NotNull PlayerInventory inventory,
                                @NotNull Text title) {
        super(handler, inventory, title);
        this.titleY = 6;
        this.backgroundWidth = 176;
        this.backgroundHeight = 240;
    }

    @Override
    protected void drawBackground(@NotNull MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = this.x;
        int y = this.y;
        drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        drawTexture(matrices, x + 7, y  + 128, 176, 16, 64, 16);
        if (isOnModeModeButton(mouseX, mouseY)) {
            drawTexture(matrices, x + 7, y  + 128, 176, 32, 64, 16);
        }

        var mode = handler.getMode().name().toLowerCase();
        var key = "screen.sib_energy_craft.item_filter.button.mode.%s".formatted(mode);
        var modeText = Text.translatable(key);
        int modeTextLeftOffset = (64 - textRenderer.getWidth(modeText)) / 2;
        this.textRenderer.drawWithShadow(matrices, modeText, x + modeTextLeftOffset, y + 132, Color.WHITE.getRGB());
    }

    private boolean isOnModeModeButton(int mouseX, int mouseY) {
        return mouseX >= x + 7 && mouseX <= x + 7 + 64 && mouseY >= y + 128 && mouseY <= y + 128 + 16;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        var client = this.client;
        if (client == null) {
            return false;
        }
        var interactionManager = client.interactionManager;
        if (interactionManager == null) {
            return false;
        }

        if (isOnModeModeButton((int) mouseX, (int) mouseY)) {
            if(client.player != null) {
                this.handler.onButtonClick(Buttons.CHANGE_MODE);
            }
            interactionManager.clickButton(this.handler.syncId, Buttons.CHANGE_MODE.ordinal());
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(@NotNull MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    protected boolean isPointWithinBounds(int x,
                                          int y,
                                          int width,
                                          int height,
                                          double pointX,
                                          double pointY) {
        int i = this.x;
        int j = this.y;
        return (pointX -= i) >= (x - 1) && pointX < (x + width + 1) && (pointY -= j) >= (y - 1) && pointY < (y + height + 1);
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        this.playerInventoryTitleY = 146;
    }

}