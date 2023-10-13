package com.mattsmeets.macrokey.handler.hook;

import com.mattsmeets.macrokey.config.ModConfig;
import com.mattsmeets.macrokey.config.ModState;
import com.mattsmeets.macrokey.event.ExecuteOnTickEvent;
import com.mattsmeets.macrokey.model.LayerInterface;
import com.mattsmeets.macrokey.model.lambda.ExecuteOnTickInterface;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Add the switch layer button to the main menu.
 */
public class GuiEventHandler {
    private Button switchButton = null;
    private ModState modState;

    public GuiEventHandler(final ModState modState) {
        this.modState = modState;
    }

    //----------
    // Event handlers
    //----------

    /**
     * Create the switch layer button.
     *
     * @param event The init GUI event.
     */
    @SubscribeEvent
    public void init(final ScreenEvent.Init event) {
        final Screen gui = event.getScreen();
        if (isNotMainMenu(event.getScreen())) return;
        if (isSwitchButtonDisabled()) return;

        switchButton = new Button(Button.builder(
                getLayerButtonLabel(modState.getActiveLayer()),
                Button::onPress).pos(
                gui.width / 2 + ModConfig.buttonLayerSwitchSetting1.get(),
                gui.height / 4 + ModConfig.buttonLayerSwitchSetting2.get()).size(
                ModConfig.buttonLayerSwitchSetting3.get(),
                ModConfig.buttonLayerSwitchSetting4.get())
        ) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                this.setMessage(getLayerButtonLabel(modState.nextLayer()));
            }
        };

        event.addListener(switchButton);
    }

    /**
     * Handle right click.
     * Open the GUI.
     *
     * @param event The mouse click event.
     */
    @SubscribeEvent(receiveCanceled = true)
    public void mouseClickedEvent(final ScreenEvent.MouseButtonPressed.Post event) {
        if (isNotMainMenu(event.getScreen())
                || isSwitchButtonDisabled()
                || switchButton == null
                || !switchButton.isMouseOver(event.getMouseX(), event.getMouseY())) {
            return;
        }

        MinecraftForge.EVENT_BUS.post(new ExecuteOnTickEvent(ExecuteOnTickInterface.openMacroKeyGUI));
    }

    /**
     * Render the tooltip.
     *
     * @param event The draw screen event.
     */
    @SubscribeEvent(receiveCanceled = true)
    public void render(final ScreenEvent.Render.Post event) {
        if (isNotMainMenu(event.getScreen())
                || isSwitchButtonDisabled()
                || switchButton == null
                || !switchButton.isHoveredOrFocused()) {
            return;
        }

        final MouseHandler mouseHelper = Minecraft.getInstance().mouseHandler;
        event.getGuiGraphics().renderTooltip(
                Minecraft.getInstance().font,
                Component.translatable("text.layer.hover.right_click"),
                (int) (mouseHelper.xpos() / 2),
                (int) (mouseHelper.ypos() / 2));
    }

    //----------
    // Helpers
    //----------

    private static boolean isNotMainMenu(final Screen gui) {
        return !(gui instanceof PauseScreen);
    }

    private static boolean isSwitchButtonDisabled() {
        return ModConfig.buttonLayerSwitcherId.get() == -1;
    }

    private static MutableComponent getLayerButtonLabel(final LayerInterface layer) {
        return Component.translatable("text.layer.display",
                layer == null ? I18n.get("text.layer.master") : layer.getDisplayName()
        );
    }
}
