package com.fthlbot.discordbotfthl.core.CommandCreation;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.SlashCommandUpdater;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CommandBuilder {
    private DiscordApi api;

    public InputStream yaml() {
        return getClass().getResourceAsStream("commands.yml");
    }

    public CommandFromYaml getCommands() {
        Yaml yaml = new Yaml();
        CommandFromYaml commands = yaml.load(yaml());
        return commands;
    }

    public void init() {
        Holder holder = checkCommands();
        deleteCommands(holder);
        editCommands(holder);
        //createCommands();
    }

    private Holder checkCommands() {
        List<SlashCommand> commands = api.getGlobalSlashCommands().join();

        List<SlashCommand> toDelete = new ArrayList<>();

        List<SlashCommand> found = new ArrayList<>();

        for (SlashCommand slashCommand : commands) {
            if (checkSlashCommandWithYaml(slashCommand)) {
                found.add(slashCommand);
            } else {
                toDelete.add(slashCommand);
            }
        }
        return new Holder(toDelete, found);
    }

    private void deleteCommands(Holder holder) {
        for (SlashCommand slashCommand : api.getGlobalSlashCommands().join()) {
            if (holder.toDelete.contains(slashCommand)) {
                slashCommand.deleteGlobal();
            }
        }
    }

    public void editCommands(Holder holder) {
        for (SlashCommand slashCommand : holder.found) {
            if (!holder.found.contains(slashCommand)) {
                throw new IllegalArgumentException("Command not found [UNREACHABLE]");
            }
            for (Command command : getCommands().getCommand()) {
                if (command.getName().equals(slashCommand.getName())) {
                    SlashCommandUpdater slashCommandUpdater = slashCommand.createSlashCommandUpdater();
                }
            }
        }
    }
    private SlashCommandUpdater editSlashCommandToYaml(SlashCommandUpdater slashCommand, Command command) {
        slashCommand.setDescription(command.getDescription());
        slashCommand.setName(command.getName());
        if (!command.getOption().isEmpty()){
            List<SlashCommandOption> options = new ArrayList<>();

            for (Option option : command.getOption()) {
                SlashCommandOption e = SlashCommandOption.create(getSlashCommandOptionType(option.getType()),
                        option.getName(),
                        option.getDescription(),
                        option.getRequired()
                );

                if (!option.getChoices().isEmpty()){
                    for (Choice choice : option.getChoices()) {
                       // e.addChoice(choice.getName(), choice.getDescription());
                    }
                }

                options.add(e);
            }
            slashCommand.setSlashCommandOptions(List.of(
                    
            ));
        }

        return slashCommand;
    }

    private SlashCommandOptionType getSlashCommandOptionType(String type) {
        type = type.toUpperCase();
        return switch (type) {
            case "STRING" -> SlashCommandOptionType.STRING;
            case "INTEGER" -> SlashCommandOptionType.LONG;
            case "USER" -> SlashCommandOptionType.USER;
            case "BOOLEAN" -> SlashCommandOptionType.BOOLEAN;
            case "CHANNEL" -> SlashCommandOptionType.CHANNEL;
            case "DECIMAL" -> SlashCommandOptionType.DECIMAL;
            case "ATTACHMENT" -> SlashCommandOptionType.ATTACHMENT;
            case "MENTIONABLE" -> SlashCommandOptionType.MENTIONABLE;
            case "ROLE" -> SlashCommandOptionType.ROLE;
            case "SUB_COMMAND" -> SlashCommandOptionType.SUB_COMMAND;
            case "SUB_COMMAND_GROUP" -> SlashCommandOptionType.SUB_COMMAND_GROUP;
            case "UNKNOWN" -> SlashCommandOptionType.UNKNOWN;
            default -> throw new IllegalArgumentException("Invalid option type");
        };
    }

    class Holder {
        List<SlashCommand> toDelete;
        List<SlashCommand> found;

        public Holder(List<SlashCommand> toDelete, List<SlashCommand> found) {
            this.toDelete = toDelete;
            this.found = found;
        }
    }

    private boolean checkSlashCommandWithYaml(SlashCommand slashCommand) {
        CommandFromYaml commands = getCommands();
        for (Command command : commands.getCommand()) {
            if (slashCommand.getName().equals(command.getName())) {
                //Check Slash Command Name with YAML
                return true;
            }
        }
        return false;
    }

    private DiscordApi getApi() {
        return api;
    }

    private void setApi(DiscordApi api) {
        this.api = api;
    }
}