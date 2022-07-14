package com.fthlbot.discordbotfthl.DatabaseModels.Roster;

import com.fthlbot.discordbotfthl.DatabaseModels.Exception.*;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RosterService {

    private final RosterRepo repo;
    private final TeamService teamService;
    private final BotConfig botConfig;
    private final Logger log = LoggerFactory.getLogger(RosterService.class);

    public RosterService(RosterRepo repo, TeamService teamService, BotConfig config, BotConfig botConfig) {
        this.repo = repo;
        this.teamService = teamService;
        this.botConfig = botConfig;
    }

    public List<Roster> getRosterForATeam(Team team) throws EntityNotFoundException {
        return repo.findRosterByTeam(team);
    }
    //TODO fp checks
    //check pos
    public synchronized Roster addToRoster(Roster roster, User user) throws LeagueException {
        Optional<Roster> alreadyAddedAccount = repo.findRosterByPlayerTagAndDivision(roster.getPlayerTag(), roster.getDivision());
        List<Roster> rosterByTeam = repo.findRosterByTeam(roster.getTeam());
        if (alreadyAddedAccount.isPresent()){
            throw new EntityAlreadyExistsException(
                    "`"+alreadyAddedAccount.get().getPlayerTag()+"` is already rostered with the team `"+alreadyAddedAccount.get().getTeam().getName()+
                            "`"
            );
        }

        boolean isRep =
                roster.getTeam().getRep1ID().equals(user.getId())
                ||
                roster.getTeam().getRep2ID().equals(user.getId());
        if (!isRep){
            throw new NotTheRepException(user, roster.getTeam());
        }

        if (rosterByTeam.size() >= roster.getDivision().getRosterSize()){
            throw new NoMoreRosterChangesLeftException(roster.getTeam(),
                    """
                    Your Roster is full!
                    You can't add anymore accounts to your team. Accounts need to be removed before you can add more.
                    """);
        }

        boolean isCorrectTh = Arrays.stream(roster.getDivision().getAllowedTownHall()).anyMatch(x -> x == roster.getTownHallLevel().intValue());

        if (!isCorrectTh){
            throw new IncorrectTownHallException(roster.getTownHallLevel(), roster.getDivision());
        }
        try {
            decrementAllowedRosterChanges(roster.getTeam());
        } catch (ParseException e) {
            throw new UnExpectedLeagueException("Failed to parse date, this should never happen\n Please report this to the developer");
        }
        return repo.save(roster);
    }

    private void decrementAllowedRosterChanges(Team team) throws NoMoreRosterChangesLeftException, ParseException {
        //decrement allowed roster changes
        //check if today is after the leagueStartDate in botConfig
        if (botConfig.getLeagueStartDate().after(new Date())){
            return;
        }
        int allowRosterChangesLeft = team.getAllowRosterChangesLeft() - 1;
        //throw exception if no more roster changes left
        if (allowRosterChangesLeft <= 0){
            throw new NoMoreRosterChangesLeftException(team,
                    "You have no more roster changes left! No more accounts can be added! [TRANSACTION POINTS - 0]");
        }
        team.setAllowRosterChangesLeft(allowRosterChangesLeft);
        teamService.updateTeam(team);
    }
    public Roster removeFromRoster(Team team, String tag) throws EntityNotFoundException {
        Optional<Roster> roster = repo.findRosterByTeamAndPlayerTag(team, tag);
        if (roster.isPresent()){
            repo.delete(roster.get());
            return roster.get();
        }
        throw new EntityNotFoundException(String.format("""
                `%s` is not present on your roster
                """, tag));
    }

    //Find teams for a player tag
    public List<Team> getTeamsForPlayerTag(String tag) {
        return repo.findRosterByPlayerTag(tag)
                .stream()
                .map(Roster::getTeam)
                .collect(Collectors.toList());
    }

    public void removeRoster(Roster roster) {
        repo.delete(roster);
    }

    //Make a method that remove all roster from a team
    @Transactional
    public void removeAllRoster(Team team) {
        repo.deleteRosterByTeam(team);
    }
}
