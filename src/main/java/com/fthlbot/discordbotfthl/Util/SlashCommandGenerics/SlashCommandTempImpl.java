package com.fthlbot.discordbotfthl.Util.SlashCommandGenerics;

import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;

import static java.util.Arrays.asList;
import static org.javacord.api.interaction.SlashCommandOptionType.STRING;

public class SlashCommandTempImpl implements SlashCommandTemp {

    @Override
    public SlashCommandOption getDivisions() {
        return SlashCommandOption.createWithChoices(STRING,
                "division",
                "choose from one of the following division",
                true,
                asList(
                        SlashCommandOptionChoice.create("f8", "f8"),
                        SlashCommandOptionChoice.create("f9", "f9"),
                        SlashCommandOptionChoice.create("f10", "f10"),
                        SlashCommandOptionChoice.create("f11", "f11"),
                        SlashCommandOptionChoice.create("f12", "f12"),
                        SlashCommandOptionChoice.create("Lite", "Lite"),
                        SlashCommandOptionChoice.create("Elite", "Elite")
                )
        );
    }

    @Override
    public SlashCommandOption getTeamName() {
        return SlashCommandOption.createStringOption(
                "team-identifier",
                "Enter the name/alias of the team you want to delete",
                true,
                true
        );
    }

    @Override
    public SlashCommandOption getUser() {
        return SlashCommandOption.create(SlashCommandOptionType.USER,
                "old-rep",
                "Mention the old representative of the team",
                true
        );
    }
}
