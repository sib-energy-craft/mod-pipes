package com.github.sib_energy_craft.pipes.filters.item_filter_extractor.screen;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @since 0.0.6
 * @author sibmaks
 */
public class ItemFilterExtractorScreen extends HandledScreen<ItemFilterExtractorScreenHandler> {
    private static final int MODE_BUTTON_X = 105;
    private static final int MODE_BUTTON_Y = 145;
    private static final int MODE_BUTTON_WIDTH = 64;
    private static final int MODE_BUTTON_HEIGHT = 16;

    private static final Identifier TEXTURE = Identifiers.of("textures/gui/container/item_filter_extractor.png");

    public ItemFilterExtractorScreen(@NotNull ItemFilterExtractorScreenHandler handler,
                                     @NotNull PlayerInventory inventory,
                                     @NotNull Text title) {
        super(handler, inventory, title);
        this.titleY = 6;
        this.backgroundWidth = 176;
        this.backgroundHeight = 246;
    }

    @Override
    protected void drawBackground(@NotNull DrawContext drawContext, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = this.x;
        int y = this.y;
        drawContext.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        drawContext.drawTexture(TEXTURE, x + MODE_BUTTON_X, y  + MODE_BUTTON_Y, 176, 16, MODE_BUTTON_WIDTH, MODE_BUTTON_HEIGHT);
        if (isOnModeModeButton(mouseX, mouseY)) {
            drawContext.drawTexture(TEXTURE, x + MODE_BUTTON_X, y  + MODE_BUTTON_Y, 176, 32, MODE_BUTTON_WIDTH, MODE_BUTTON_HEIGHT);
        }

        var mode = handler.getMode().name().toLowerCase();
        var key = "screen.sib_energy_craft.item_filter.button.mode.%s".formatted(mode);
        var modeText = Text.translatable(key);
        int modeTextLeftOffset = MODE_BUTTON_X + (MODE_BUTTON_WIDTH - textRenderer.getWidth(modeText)) / 2;
        drawContext.drawTextWithShadow(textRenderer, modeText, x + modeTextLeftOffset, y + 149, Color.WHITE.getRGB());
    }

    private boolean isOnModeModeButton(int mouseX, int mouseY) {
        return mouseX >= x + MODE_BUTTON_X && mouseX <= x + MODE_BUTTON_X + MODE_BUTTON_WIDTH &&
                mouseY >= y + MODE_BUTTON_Y && mouseY <= y + MODE_BUTTON_Y + MODE_BUTTON_HEIGHT;
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
    public void render(@NotNull DrawContext drawContext, int mouseX, int mouseY, float delta) {
        renderBackground(drawContext);
        super.render(drawContext, mouseX, mouseY, delta);
        drawMouseoverTooltip(drawContext, mouseX, mouseY);
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
        this.playerInventoryTitleY = 149;
    }

}