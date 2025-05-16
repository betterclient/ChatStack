package io.github.betterclient.chatstack.mixin.v161;

import io.github.betterclient.chatstack.ChatStack;
import io.github.betterclient.chatstack.RepeatingMessage;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
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
    @Shadow @Final private List<ChatHudLine> visibleMessages;
    @Unique private final ChatStack chatStack = ChatStack.getInstance();
    @Unique private MutableText finalOut;
    @Unique private RepeatingMessage currMessage;
    @Unique private final Style GREEN_COLOR_STYLE = Style.EMPTY.withColor(Formatting.GREEN);
    @Unique private final Style RED_COLOR_STYLE = Style.EMPTY.withColor(Formatting.RED);
    @Unique private final Style YELLOW_COLOR_STYLE = Style.EMPTY.withColor(Formatting.YELLOW);

    @Inject(method = "addMessage(Lnet/minecraft/text/StringRenderable;IIZ)V", at = @At("HEAD"))
    @SuppressWarnings("SuspiciousMethodCalls") //not sus. shhhhhh!
    public void chatstack$addMessage(StringRenderable stringRenderable, int messageId, int timestamp, boolean bl, CallbackInfo ci) {
        currMessage = chatStack.messages.get(stringRenderable.toString());
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
                    .append(new LiteralText("[").shallowCopy().fillStyle(color))
                    .append(currMessage.getCount().incrementAndGet() + "x")
                    .append(new LiteralText("]").shallowCopy().fillStyle(color));
        } else {
            if (stringRenderable instanceof Text) {
                currMessage = new RepeatingMessage(((Text) stringRenderable).copy(), new ArrayList<>(), new AtomicInteger(1));
                chatStack.messages.put(stringRenderable.toString(), currMessage);
            }
        }
    }

    @ModifyArg(method = "addMessage(Lnet/minecraft/text/StringRenderable;IIZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/ChatMessages;breakRenderedChatMessageLines(Lnet/minecraft/text/StringRenderable;ILnet/minecraft/client/font/TextRenderer;)Ljava/util/List;"), index = 0)
    public StringRenderable chatstack$addMessage(StringRenderable par1) {
        if (finalOut == null) {
            return par1;
        } else {
            return finalOut;
        }
    }

    @Redirect(method = "addMessage(Lnet/minecraft/text/StringRenderable;IIZ)V", at = @At(value = "NEW", target = "(ILnet/minecraft/text/StringRenderable;I)Lnet/minecraft/client/gui/hud/ChatHudLine;"))
    public ChatHudLine chatstack$createOrderedText(int i, StringRenderable o, int i1) {
        ChatHudLine visible = new ChatHudLine(i, o, i1);
        currMessage.getInstances().add(visible);
        return visible;
    }
}