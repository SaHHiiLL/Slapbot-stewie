package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Handlers.Command;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.springframework.stereotype.Component;

@Component
@Invoker(
        alias = "statistics",
        description = "Shows the statistics of the bot",
        usage = "/statistics",
        type = CommandType.STAFF
)
public class Statistic implements Command {
    @Override
    public void execute(SlashCommandCreateEvent event) {
        if (!event.getSlashCommandInteraction().getUser().isBotOwner()){
            event.getSlashCommandInteraction().createFollowupMessageBuilder().setContent("This command can only be used by the bot owner").send();
            return;
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Statistics");
        printMemoryUsage(embedBuilder);

    }

    private void printMemoryUsage(EmbedBuilder embedBuilder) {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = allocatedMemory - freeMemory;
        embedBuilder.addField("Memory usage", String.format("%.2f/%.2f MB", usedMemory / 1024.0 / 1024.0, maxMemory / 1024.0 / 1024.0), false);
    }
}