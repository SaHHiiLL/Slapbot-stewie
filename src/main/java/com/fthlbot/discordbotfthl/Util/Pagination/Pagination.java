package com.fthlbot.discordbotfthl.Util.Pagination;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.LowLevelComponent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Pagination {
    public void buttonPagination(List<EmbedBuilder> em, CompletableFuture<InteractionOriginalResponseUpdater> message, DiscordApi api) {

        LowLevelComponent[] lowLevelComponents = {
                Button.secondary("first", "⏪"),
                Button.secondary("previous", "◀️"),
                Button.secondary("next", "▶️"),
                Button.secondary("last", "⏩")
        };

        InteractionOriginalResponseUpdater m = null;
        try {
            m = message.join();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        m.addEmbed(em.get(0));
        if (em.size() == 1) {
            m.update();
            return;
        }
        m.addComponents(ActionRow.of(lowLevelComponents));
        Message response = m.update().join();

        AtomicInteger i = new AtomicInteger(0);
        try {
            new ButtonRemoveJobScheduler().execute(m);
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        InteractionOriginalResponseUpdater finalM = m;
        response.addButtonClickListener(button -> {
            String customId = button.getButtonInteraction().getCustomId();
            try {
                switch (customId) {
                    case "first" -> {
                        button.getButtonInteraction().acknowledge().thenAccept(a -> {
                            finalM.removeAllEmbeds().addEmbed(em.get(0)).update();
                            i.set(0);
                        });
                    }
                    case "last" -> {
                        button.getButtonInteraction().acknowledge().thenAccept(a -> {
                            finalM.removeAllEmbeds().addEmbed(em.get(em.size() - 1)).update();
                            //  message.update().thenAccept(m -> m.edit(em.get(em.size() - 1)));
                            i.set(em.size() - 1);
                        });
                    }
                    case "next" -> {
                        button.getButtonInteraction().acknowledge().thenAccept(a -> {
                            finalM.removeAllEmbeds().addEmbed(em.get(i.get() + 1)).update();
                            //message.update().thenAccept(m -> m.edit(em.get(i.get() + 1)));
                            i.incrementAndGet();
                        });
                    }
                    case "previous" -> {
                        button.getButtonInteraction().acknowledge().thenAccept(a -> {
                            finalM.removeAllEmbeds().addEmbed(em.get(i.get() - 1)).update();
                            // message.update().thenAccept(m -> m.edit(em.get(i.get() - 1)));
                            i.decrementAndGet();
                        });
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                //
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).removeAfter(10, TimeUnit.MINUTES);
    }

}
