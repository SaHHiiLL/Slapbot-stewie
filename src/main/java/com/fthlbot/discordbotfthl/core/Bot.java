package com.fthlbot.discordbotfthl.core;

import com.fthlbot.discordbotfthl.AutoCompleteSlashcommandOptions.AutoCompleteHandler.AutoCompleter;
import com.fthlbot.discordbotfthl.AutoCompleteSlashcommandOptions.AutoCompleteListener;
import com.fthlbot.discordbotfthl.AutoCompleteSlashcommandOptions.autoCompleteSuggestions.AutoCompleteDivisionWeek;
import com.fthlbot.discordbotfthl.AutoCompleteSlashcommandOptions.autoCompleteSuggestions.AutoCompleteSlashComandTeamIdentifierImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl.*;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.FunAndRandomCommands.FilterWords.FilterWordsEdit;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.FunAndRandomCommands.FilterWords.ModerationFilterWords;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.FunAndRandomCommands.ImageGenCommandImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.FunAndRandomCommands.PingImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.FunAndRandomCommands.SuggestionImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.*;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.RosterAdd.RosterAdditionImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.TeamRoster.TeamRoster;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.UtilCommands.ShowDivisionWeekImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.UtilCommands.TeamInfoImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.*;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.RepChnage.ChangeRepImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.RepChnage.ChangeRepWithIDImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.SchedulingCommands.*;
import com.fthlbot.discordbotfthl.DatabaseModels.CommandLogger.CommandLoggerService;
import com.fthlbot.discordbotfthl.Events.ServerJoinImpl;
import com.fthlbot.discordbotfthl.Events.ServerLeaveImpl;
import com.fthlbot.discordbotfthl.Events.ServerMemberJoin.ApplicantServerJoinImpl;
import com.fthlbot.discordbotfthl.Events.ServerMemberJoin.NegoServerMemberjoinImpl;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import com.fthlbot.discordbotfthl.Util.SlapbotEmojis;
import com.fthlbot.discordbotfthl.Util.SlashGODClass;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import com.fthlbot.discordbotfthl.core.Handlers.CommandListener;
import com.fthlbot.discordbotfthl.core.Handlers.MessageHandlers;
import com.fthlbot.discordbotfthl.core.Handlers.MessageHolder;
import org.javacord.api.DiscordApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Bot {

    private final Environment env;

    private final PingImpl pingImpl;

    private final RegistrationImpl registration;

    private final RosterAdditionImpl rosterAddition;

    private final CommandLoggerService loggerService;

    private final RosterRemove rosterRemove;

    private final TeamRoster teamRoster;

    private final DefenseImpl attack;

    private final AllTeamsImpl allTeams;

    private final ChangeClanImpl changeClan;

    private static final Logger log = LoggerFactory.getLogger(Bot.class);

    private final BotConfig config;

    private final ChangeRepImpl changeRep;

    private final ChangeAliasImpl changeAlias;

    private final AddDivisionWeeksImpl addDivisionWeeks;

    private final CreateMatchUps createMatchUps;

    private final NegoChannelCreationImpl negoChannelCreation;

    private final ShowDivisionWeekImpl showDivisionWeek;

    private final PlayerImpl player;

    private final RemoveAllChannelFromACategoryImpl removeAllChannelFromACategory;

    private final TeamInfoImpl teamInfo;

    private final SlashGODClass builder;

    private final CreateAllDivisionsImpl createAllDivisions;

    private final DeleteATeamImpl deleteATeam;

    private final StatsImpl stats;

    private final AttackImpl attackImpl;

    private final ImageGenCommandImpl imageGenCommand;

    private final FairPlayCheckOnAllTeamImpl fairPlayCheckOnAllTeam;
    private final CheckLineUpImpl checkLineUp;

    private final ClanInfoImpl clanInfo;

    private final SuggestionImpl suggestionImpl;

    private final NegoServerMemberjoinImpl serverMemberJoin;

    private final ApplicantServerJoinImpl applicantServerJoin;

    private final ServerJoinImpl serverJoin;
    private final ServerLeaveImpl serverLeave;

    private final DivisionEditorImpl divisionEditor;

    private final InfoCommandImpl infoCommandImpl;

    private final ClanLineup clanLineup;

    private final ModerationFilterWords moderationFilterWords;

    private final FilterWordsEdit filterWordsEdit;

    private final CheckEveryRepHasJoinThisServerImpl checkEveryRepHasJoinThisServer;

    private DiscordApi api;

    private CommandListener commandListener;

    private final GiveRolesImpl giveRoles;

    private final SchedulingParser schedulingParser;

    private final ShowScheduleWars showScheduleWars;

    private final MinionBotStatsStringImpl minionBotStatsString;

    private final BanRepImpl banRep;

    private final ForceAddImpl forceAdd;

    private final AddRegistrationChannel addRegistrationChannel;

    private final ChangeRepWithIDImpl changeRepWithID;

    private final EditTransaction editTransaction;

    private final ResetDivisionImpl resetDivision;

    private final RemoveChannelsFromCategoryExceptOne removeChannelsFromCategoryExceptOne;

    private final AutoCompleteSlashComandTeamIdentifierImpl autoCompleteTeamIdentifier;

    private final LeaveServerImpl leaveServer;
    private final AutoCompleteDivisionWeek autoCompleteDivisionWeek;
    public Bot(Environment env,
               PingImpl pingImpl,
               RegistrationImpl registration,
               RosterAdditionImpl rosterAddition,
               CommandLoggerService loggerService,
               RosterRemove rosterRemove,
               TeamRoster teamRoster,
               DefenseImpl attack,
               AllTeamsImpl allTeams,
               ChangeClanImpl changeClan,
               BotConfig config,
               ChangeRepImpl changeRep,
               ChangeAliasImpl changeAlias,
               AddDivisionWeeksImpl addDivisionWeeks,
               CreateMatchUps createMatchUps,
               NegoChannelCreationImpl negoChannelCreation,
               ShowDivisionWeekImpl showDivisionWeek,
               PlayerImpl player,
               RemoveAllChannelFromACategoryImpl removeAllChannelFromACategory,
               TeamInfoImpl teamInfo,
               SlashGODClass builder,
               CreateAllDivisionsImpl createAllDivisions,
               DeleteATeamImpl deleteATeam,
               StatsImpl stats,
               AttackImpl attackImpl,
               ImageGenCommandImpl imageGenCommand,
               FairPlayCheckOnAllTeamImpl fairPlayCheckOnAllTeam,
               CheckLineUpImpl checkLineUp,
               ClanInfoImpl clanInfo,
               SuggestionImpl suggestionImpl,
               NegoServerMemberjoinImpl serverMemberJoin,
               ApplicantServerJoinImpl applicantServerJoin,
               ServerJoinImpl serverJoin,
               ServerLeaveImpl serverLeave,
               DivisionEditorImpl divisionEditor,
               InfoCommandImpl infoCommandImpl,
               ClanLineup clanLineup,
               ModerationFilterWords moderationFilterWords,
               FilterWordsEdit filterWordsEdit,
               CheckEveryRepHasJoinThisServerImpl checkEveryRepHasJoinThisServer,
               GiveRolesImpl giveRoles,
               SchedulingParser schedulingParser,
               ShowScheduleWars showScheduleWars,
               MinionBotStatsStringImpl minionBotStatsString,
               BanRepImpl banRep,
               ForceAddImpl forceAdd,
               AddRegistrationChannel addRegistrationChannel,
               ChangeRepWithIDImpl changeRepWithID,
               EditTransaction editTransaction,
               ResetDivisionImpl resetDivision, RemoveChannelsFromCategoryExceptOne removeChannelsFromCategoryExceptOne,
               AutoCompleteSlashComandTeamIdentifierImpl autoCompleteTeamIdentifier,
               LeaveServerImpl leaveServer, AutoCompleteDivisionWeek autoCompleteDivisionWeek) {
        this.env = env;
        this.pingImpl = pingImpl;
        this.registration = registration;
        this.rosterAddition = rosterAddition;
        this.loggerService = loggerService;
        this.rosterRemove = rosterRemove;
        this.teamRoster = teamRoster;
        this.attack = attack;
        this.allTeams = allTeams;
        this.changeClan = changeClan;
        this.config = config;
        this.changeRep = changeRep;
        this.changeAlias = changeAlias;
        this.addDivisionWeeks = addDivisionWeeks;
        this.createMatchUps = createMatchUps;
        this.negoChannelCreation = negoChannelCreation;
        this.showDivisionWeek = showDivisionWeek;
        this.player = player;
        this.removeAllChannelFromACategory = removeAllChannelFromACategory;
        this.teamInfo = teamInfo;
        this.builder = builder;
        this.createAllDivisions = createAllDivisions;
        this.deleteATeam = deleteATeam;
        this.stats = stats;
        this.attackImpl = attackImpl;
        this.imageGenCommand = imageGenCommand;
        this.fairPlayCheckOnAllTeam = fairPlayCheckOnAllTeam;
        this.checkLineUp = checkLineUp;
        this.clanInfo = clanInfo;
        this.suggestionImpl = suggestionImpl;
        this.serverMemberJoin = serverMemberJoin;
        this.applicantServerJoin = applicantServerJoin;
        this.serverJoin = serverJoin;
        this.serverLeave = serverLeave;
        this.divisionEditor = divisionEditor;
        this.infoCommandImpl = infoCommandImpl;
        this.clanLineup = clanLineup;
        this.moderationFilterWords = moderationFilterWords;
        this.filterWordsEdit = filterWordsEdit;
        this.checkEveryRepHasJoinThisServer = checkEveryRepHasJoinThisServer;
        this.giveRoles = giveRoles;
        this.schedulingParser = schedulingParser;
        this.showScheduleWars = showScheduleWars;
        this.minionBotStatsString = minionBotStatsString;
        this.banRep = banRep;
        this.forceAdd = forceAdd;
        this.addRegistrationChannel = addRegistrationChannel;
        this.changeRepWithID = changeRepWithID;
        this.editTransaction = editTransaction;
        this.resetDivision = resetDivision;
        this.removeChannelsFromCategoryExceptOne = removeChannelsFromCategoryExceptOne;
        this.autoCompleteTeamIdentifier = autoCompleteTeamIdentifier;
        this.leaveServer = leaveServer;
        this.autoCompleteDivisionWeek = autoCompleteDivisionWeek;
        log.info("Bot object created");
    }

    private Bot setApi(DiscordApi api) {
        this.api = api;
        return this;
    }

    private void registerCommands() {
        List<Command> commandList = new ArrayList<>(List.of(
                this.pingImpl,
                this.registration,
                this.rosterAddition,
                this.rosterRemove,
                this.teamRoster,
                this.attack,
                this.allTeams,
                this.changeRep,
                this.changeClan,
                this.changeAlias,
                this.addDivisionWeeks,
                this.createMatchUps,
                this.negoChannelCreation,
                this.showDivisionWeek,
                this.leaveServer,
                this.player,
                this.removeAllChannelFromACategory,
                this.teamInfo,
                this.createAllDivisions,
                this.deleteATeam,
                this.stats,
                this.attackImpl,
                this.imageGenCommand,
                this.checkLineUp,
                this.fairPlayCheckOnAllTeam,
                this.clanInfo,
                this.suggestionImpl,
                this.divisionEditor,
                this.infoCommandImpl,
                this.clanLineup,
                this.checkEveryRepHasJoinThisServer,
                this.giveRoles,
                this.schedulingParser,
                this.showScheduleWars,
                this.minionBotStatsString,
                this.banRep,
                this.forceAdd,
                this.addRegistrationChannel,
                this.changeRepWithID,
                this.editTransaction,
                this.removeChannelsFromCategoryExceptOne,
                this.resetDivision
        ));
        //Making help command
        HelpImpl help = new HelpImpl(commandList);
        //Add help command
        commandList.add(help);

        MessageHandlers messageHandlers = new MessageHandlers(commandList);

        MessageHolder messageHolder = messageHandlers.setCommands();

        log.info("Added {} Commands!", commandList.size());

        this.commandListener =  new CommandListener(messageHolder, loggerService, config);
    }

    private void registerAutoComplete() {
        List<AutoCompleter> autoCompleters = List.of(
                this.autoCompleteTeamIdentifier,
                this.autoCompleteDivisionWeek
        );
        AutoCompleteListener autoCompleteListener = new AutoCompleteListener(autoCompleters);
        this.api.addAutocompleteCreateListener(autoCompleteListener);
    }

    private Bot setListeners(){
        this.registerCommands(); // loading slash commands into memory
        this.registerAutoComplete(); // loading auto completed into memory
        this.api.addListener(this.commandListener);
        this.api.addListener(serverMemberJoin);
        this.api.addListener(applicantServerJoin);
        //To log the servers the bot is in
        this.api.addListener(serverJoin);
        this.api.addListener(serverLeave);
//        this.api.addMessageCreateListener(moderationFilterWords);
//        this.api.addMessageEditListener(filterWordsEdit);
        return this;
    }

    private Bot setEmoji(){
        log.info("Setting emoji");
        SlapbotEmojis.setEmojis( this.api.getServerById(config.getEmojiServerID()).get().getCustomEmojis().stream().toList());
        return this;
    }

    public void Start(DiscordApi api){
        this.setApi(api)
                .setListeners()
                .setEmoji();
    }
}
