package io.github.betterclient.chatstack.mixin;

import io.github.betterclient.chatstack.ChatStack;
import io.github.betterclient.chatstack.RepeatingMessage;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(ChatHud.class)
public class MixinChatHud {
    @Shadow @Final private List<ChatHudLine.Visible> visibleMessages;
    @Unique private final ChatStack chatStack = ChatStack.getInstance();
    @Unique private MutableText finalOut;
    @Unique private RepeatingMessage currMessage;
    @Unique private final Style GREEN_COLOR_STYLE = Style.EMPTY.withColor(Formatting.GREEN);
    @Unique private final Style RED_COLOR_STYLE = Style.EMPTY.withColor(Formatting.RED);
    @Unique private final Style YELLOW_COLOR_STYLE = Style.EMPTY.withColor(Formatting.YELLOW);

    @Inject(method = "addVisibleMessage", at = @At("HEAD"))
    public void chatstack$addVisibleMessage(ChatHudLine message, CallbackInfo ci) {
        currMessage = chatStack.messages.get(message.content().toString());
        finalOut = null;
        if (currMessage != null) {
            visibleMessages.removeAll(currMessage.getInstances());
            currMessage.getInstances().clear();
            Style color = getColor();

            finalOut = currMessage
                    .getOriginalMessage()
                    .copy()
                    .append(" ")
                    .append(Text.literal("[").fillStyle(color))
                    .append(currMessage.getCount().incrementAndGet() + "x")
                    .append(Text.literal("]").fillStyle(color));
        } else {
            currMessage = new RepeatingMessage(message.content().copy(), new ArrayList<>(), new AtomicInteger(1));
            chatStack.messages.put(message.content().toString(), currMessage);
        }
    }

    @Unique
    private Style getColor() {
        int i = currMessage.getCount().get();
        Style color;
        if (i >= 1 && i <= 30) {
            color = GREEN_COLOR_STYLE;
        } else if (i > 30 && i <= 70) {
            color = YELLOW_COLOR_STYLE;
        } else {
            color = RED_COLOR_STYLE;
        }
        return color;
    }

    @ModifyArg(method = "addVisibleMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/ChatMessages;breakRenderedChatMessageLines(Lnet/minecraft/text/StringVisitable;ILnet/minecraft/client/font/TextRenderer;)Ljava/util/List;"), index = 0)
    public StringVisitable chatstack$addMessage(StringVisitable message) {
        if (finalOut == null) {
            return message;
        } else {
            return finalOut;
        }
    }

    @Redirect(method = "addVisibleMessage", at = @At(value = "NEW", target = "(ILnet/minecraft/text/OrderedText;Lnet/minecraft/client/gui/hud/MessageIndicator;Z)Lnet/minecraft/client/gui/hud/ChatHudLine$Visible;"))
    public ChatHudLine.Visible chatstack$createOrderedText(int i, OrderedText orderedText, MessageIndicator messageIndicator, boolean bl) {
        ChatHudLine.Visible visible = new ChatHudLine.Visible(i, orderedText, messageIndicator, bl);
        currMessage.getInstances().add(visible);
        return visible;
    }
}