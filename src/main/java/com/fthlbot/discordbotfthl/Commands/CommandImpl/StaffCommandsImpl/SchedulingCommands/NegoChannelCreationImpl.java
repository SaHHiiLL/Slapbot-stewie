package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.SchedulingCommands;

import com.fthlbot.discordbotfthl.core.Annotation.AllowedChannel;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.StaffCommandListener.NegoChannelCreationListener;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeekService;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeeks;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule.ScheduleWarService;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule.ScheduledWar;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ChannelCategoryBuilder;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(alias = "create-negotiation-channels",
    description = "Creates a new negotiation channel",
    usage = "create-negotiation-channel <Division WeekID>",
    type = CommandType.STAFF,
    where = AllowedChannel.NEGO_SERVER
)
public class NegoChannelCreationImpl implements NegoChannelCreationListener {
    private final DivisionWeekService divisionWeekService;
    private final ScheduleWarService scheduleWarService;
    private final BotConfig botConfig;


    public NegoChannelCreationImpl(DivisionWeekService divisionWeekService, ScheduleWarService scheduleWarService, BotConfig botConfig) {
        this.divisionWeekService = divisionWeekService;
        this.scheduleWarService = scheduleWarService;
        this.botConfig = botConfig;
    }

    @Override
    public void execute(SlashCommandCreateEvent event)  {
        //check if the user is botOnwer
        if (!event.getSlashCommandInteraction().getUser().isBotOwner()) {
            event.getSlashCommandInteraction()
                    .createImmediateResponder()
                    .setContent("Sorry sahil is mean and will not let you use this command :(")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
        }
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = event.getSlashCommandInteraction().respondLater();
        Long aLong = event.getSlashCommandInteraction().getArguments().get(0).getLongValue().get();
        DivisionWeeks divisionWeeks;
        List<ScheduledWar> scheduledWar;
        try {
            divisionWeeks = divisionWeekService.getDivWeekByID(longToInt(aLong));
            scheduledWar = scheduleWarService.getScheduleByDivisionWeek(divisionWeeks);
        } catch (EntityNotFoundException e) {
            //send error message from GeneralService
            GeneralService.leagueSlashErrorMessage(respondLater,e);
            return;
        }
        //TODO create a channels. check if the division week is already created and check if the dates are correct relative to the current date

        Date weekStartDate = divisionWeeks.getWeekStartDate();
        if (isPast(weekStartDate)) {
            //send error message from GeneralService
            GeneralService.leagueSlashErrorMessage(respondLater,"The week has already started");
            return;
        }
        //get a server from the api object inside event and create a new channel

        Optional<Server> serverById = event.getApi().getServerById(botConfig.getNegoServerID());
        if (serverById.isEmpty()) {
            //send error message from GeneralService
            GeneralService.leagueSlashErrorMessage(respondLater,"Negotiation server not found");
            return;
        }
        ChannelCategoryBuilder cat = new ChannelCategoryBuilder(serverById.get());

        //make a string that has division week end and start date
        StringBuilder sb = new StringBuilder();
        sb.append("Week ")
                .append(divisionWeeks.getWeekNumber())
                .append(" - ")
                .append(GeneralService.dateToStringInDiscordFormat(divisionWeeks.getWeekStartDate()))
                .append(" - ")
                .append(GeneralService.dateToStringInDiscordFormat(divisionWeeks.getWeekEndDate()));
        StringBuilder catgoryName = new StringBuilder();
        catgoryName.append("Week ")
                .append(divisionWeeks.getWeekNumber())
                .append(" - ")
                .append(divisionWeeks.getWeekStartDate())
                .append(" - ")
                .append(divisionWeeks.getWeekEndDate());
        cat.setName(catgoryName.toString());
        cat.setAuditLogReason("Negotiation category created");
        ChannelCategory join = cat.create().join();

        for (ScheduledWar war : scheduledWar) {
            ServerTextChannelBuilder channel = new ServerTextChannelBuilder(serverById.get());

            PermissionsBuilder everyoneElse = new PermissionsBuilder().setDenied(PermissionType.VIEW_CHANNEL);
            PermissionsBuilder applicant = new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL, PermissionType.SEND_MESSAGES);
            //make a string that has division name and both teams aliases
            StringBuilder sb2 = new StringBuilder();
            sb2.append(war.getDivisionWeeks().getDivision().getAlias());
            sb2.append(" - ");
            Team teamA = war.getTeamA();
            sb2.append(teamA.getAlias());
            sb2.append(" vs ");
            Team teamB = war.getTeamB();
            sb2.append(teamB.getAlias());

            channel.setName(sb2.toString());
            channel.setCategory(join);
            channel.setAuditLogReason("Negotiation channel created");
            //set permission for the channel

            User rep1 = event.getApi().getUserById(teamA.getRep1ID()).join();
            User rep2 = event.getApi().getUserById(teamA.getRep2ID()).join();
            User rep3 = event.getApi().getUserById(teamB.getRep1ID()).join();
            User rep4 = event.getApi().getUserById(teamB.getRep2ID()).join();
            channel.addPermissionOverwrite(serverById.get().getEveryoneRole(), everyoneElse.build())
                    .addPermissionOverwrite(rep1, applicant.build())
                    .addPermissionOverwrite(rep2, applicant.build())
                    .addPermissionOverwrite(rep3, applicant.build())
                    .addPermissionOverwrite(rep4, applicant.build());

            channel.setTopic("Schedule ID: "+war.getID());
            channel.create().thenAccept(x ->{
                //send a message to the channel
                String content = "Welcome to the negotiation channel for " + sb2.toString() + "!";
                //Create an embed message
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Negotiation Channel");
                embed.addInlineField("Dates", sb.toString());
                embed.setDescription(content);
                embed.setColor(Color.GREEN);
                embed.setTimestampToNow();

                x.sendMessage(embed).join();
                List.of(rep1,rep2,rep3,rep4).forEach(user -> {
                    x.sendMessage("<@%s>".formatted(user.getId()));
                });
            });
        }
        respondLater.thenAccept(x -> {
            //send a message to the channel
            x.setContent("Negotiation channels have been created!").update();
        });
    }

    //converts long to int
    private int longToInt(Long aLong) {
        return aLong.intValue();
    }

    //is date in the past?
    private boolean isPast(Date date) {
        //return date.before(new Date());
        return false;
    }

    //A method to convert date to unix timestamp

}
