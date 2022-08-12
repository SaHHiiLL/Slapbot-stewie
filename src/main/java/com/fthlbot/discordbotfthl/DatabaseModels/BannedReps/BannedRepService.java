package com.fthlbot.discordbotfthl.DatabaseModels.BannedReps;

import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityAlreadyExistsException;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class BannedRepService {
    private final BannedRepRepository bannedRepRepository;


    public BannedRepService(BannedRepRepository bannedRepRepository) {
        this.bannedRepRepository = bannedRepRepository;
    }

    public Optional<BannedRep> getBannedRep(long discordID) {
        return bannedRepRepository.findBannedRepByDiscordUserID(discordID);
    }

    public BannedRep banRep (long discordID,
                             String staffName,
                             String teamName,
                             String divName,
                             Optional<String> reason,
                             Optional<String> notes,
                             Date date) throws EntityAlreadyExistsException {
        BannedRep b = new BannedRep(
                discordID,
                reason.orElse("Null"),
                staffName,
                date,
                teamName,
                divName,
                notes.orElse("Null")
        );
        if (getBannedRep(discordID).isPresent()) {
            throw new EntityAlreadyExistsException("This user is already banned from being a rep!");
        }
        return bannedRepRepository.save(b);
    }

    public void removeBan(long discordID) throws EntityNotFoundException {
        Optional<BannedRep> bannedRep = getBannedRep(discordID);

        if (bannedRep.isEmpty()){
            throw new EntityNotFoundException("This user is not banned, Cannot unban!");
        }

        bannedRepRepository.delete(bannedRep.get());
    }
}