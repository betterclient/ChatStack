package io.github.betterclient.chatstack.mixin.v202;

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
    @SuppressWarnings("SuspiciousMethodCalls") //not sus. shhhhhh!
    public void chatstack$addVisibleMessage(ChatHudLine message, CallbackInfo ci) {
        currMessage = chatStack.messages.get(message.content().toString());
        finalOut = null;
        if (currMessage != null) {
            visibleMessages.removeAll(currMessage.getInstances());
            currMessage.getInstances().clear();

            Style color = chatStack.getColor(
                    currMessage.getCount().get(),
                    RED_COLOR_STYLE,
                    YELLOW_COLOR_STYLE,
                    GREEN_COLOR_STYLE
            );

            finalOut = ((MutableText) (currMessage
                    .getOriginalMessage()))
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