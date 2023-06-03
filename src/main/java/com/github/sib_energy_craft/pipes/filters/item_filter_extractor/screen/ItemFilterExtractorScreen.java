package com.github.sib_energy_craft.pipes.filters.item_filter_extractor.screen;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.sec_utils.screen.ScreenSquareArea;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.6
 * @author sibmaks
 */
public class ItemFilterExtractorScreen extends HandledScreen<ItemFilterExtractorScreenHandler> {
    private static final ScreenSquareArea MODE_BUTTON = new ScreenSquareArea(148, 14, 20, 20);
    private static final ScreenSquareArea MODE_ICON = new ScreenSquareArea(150, 16, 16, 16);

    private static final Identifier TEXTURE = Identifiers.of("textures/gui/container/item_filter_extractor.png");

    public ItemFilterExtractorScreen(@NotNull ItemFilterExtractorScreenHandler handler,
                                     @NotNull PlayerInventory inventory,
                                     @NotNull Text title) {
        super(handler, inventory, title);
        this.titleY = 6;
        this.backgroundWidth = 176;
        this.backgroundHeight = 242;
    }

    @Override
    protected void drawBackground(@NotNull MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = this.x;
        int y = this.y;
        drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        var mode = handler.getMode();
        var modeCode = mode.name().toLowerCase();
        drawTexture(matrices, x + MODE_BUTTON.x(), y  + MODE_BUTTON.y(), 176, MODE_BUTTON.height(),
                MODE_BUTTON.width(), MODE_BUTTON.height());
        drawTexture(matrices, x + MODE_ICON.x(), y  + MODE_ICON.y(), 196, 16 * mode.ordinal(),
                MODE_ICON.width(), MODE_ICON.height());

        if (MODE_BUTTON.in(x, y, mouseX, mouseY)) {
            drawTexture(matrices, x + MODE_BUTTON.x(), y  + MODE_BUTTON.y(), 176, MODE_BUTTON.height() * 2,
                    MODE_BUTTON.width(), MODE_BUTTON.height());
            var key = "screen.sib_energy_craft.item_filter.button.mode.%s".formatted(modeCode);
            var modeText = Text.translatable(key);
            renderTooltip(matrices, modeText, mouseX, mouseY);
        }
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

        if (MODE_BUTTON.in(x, y, mouseX, mouseY)) {
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

    @Override
    protected void init() {
        super.init();
        this.titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

}