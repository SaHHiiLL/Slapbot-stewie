package com.fthlbot.discordbotfthl;

import Core.JClash;
import Core.exception.ClashAPIException;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.HelpImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.PingImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.RegistrationImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.RosterAdd.RosterAdditionImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.RosterRemove;
import com.fthlbot.discordbotfthl.DatabaseModels.CommandLogger.CommandLoggerService;
import com.fthlbot.discordbotfthl.Handlers.Command;
import com.fthlbot.discordbotfthl.Handlers.MessageHandlers;
import com.fthlbot.discordbotfthl.Handlers.MessageHolder;
import com.fthlbot.discordbotfthl.Handlers.CommandListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.fthlbot.discordbotfthl.Util.GeneralService.getFileContent;
import static org.javacord.api.interaction.SlashCommandOptionType.*;

@SpringBootApplication
public class DiscordBotFthlApplication {

    @Autowired
    private Environment env;

    @Autowired
    private PingImpl pingImpl;

    @Autowired
    private RegistrationImpl registration;

    @Autowired
    private RosterAdditionImpl rosterAddition;

    @Autowired
    private CommandLoggerService loggerService;

    @Autowired
    private RosterRemove rosterRemove;

    public static final String prefix = "+";

    private static final Logger log = LoggerFactory.getLogger(DiscordBotFthlApplication.class);
    public static JClash clash;


    public static void main(String[] args) {
        SpringApplication.run(DiscordBotFthlApplication.class, args);
    }

    @Bean
    @ConfigurationProperties(value = "discord-bot")
    public DiscordApi api() throws ClashAPIException, IOException {

        String content = getFileContent("Servers.json");

        JSONObject jsonObject = new JSONObject(content);
        long testID = jsonObject.getLong("test");


        clash = new JClash(env.getProperty("CLASH_EMAIL"), env.getProperty("CLASH_PASS"));

        DiscordApi api = new DiscordApiBuilder()
                .setToken(env.getProperty("TOKEN_TEST_BOT"))
                .setAllIntentsExcept(
                        Intent.GUILD_WEBHOOKS,
                        Intent.GUILD_INTEGRATIONS,
                        Intent.DIRECT_MESSAGE_TYPING,
                        Intent.DIRECT_MESSAGE_REACTIONS,
                        Intent.DIRECT_MESSAGES,
                        Intent.DIRECT_MESSAGE_TYPING,
                        Intent.GUILD_MESSAGE_TYPING
                ).login()
                .join();
        ArrayList<Server> servers = new ArrayList<>(api.getServers());
        log.info("Logged in as {}", api.getYourself().getDiscriminatedName());
        log.info("Watching servers {}", servers.size());

        SlashCommand command = SlashCommand.with(
                "help",
                api.getYourself().getName() + "'s help command!")
                .setOptions(List.of(
                        SlashCommandOption.create(STRING,
                                "command-name",
                                "enter a valid command name, to view more detailed information",
                                false)
                        )
                ).createForServer(api.getServerById(testID).get())
                .join();

        List<Command> commandList = new ArrayList<>(List.of(
                this.pingImpl,
                this.registration,
                this.rosterAddition,
                this.rosterRemove
        ));

        HelpImpl help = new HelpImpl(commandList);
        commandList.add(help);

        MessageHandlers messageHandlers = new MessageHandlers(commandList);

        MessageHolder messageHolder = messageHandlers.setCommands();

        CommandListener commandListener = new CommandListener(messageHolder, loggerService);

        api.addListener(commandListener);

        return api;
    }


}
