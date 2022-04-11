package com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl;

import Core.Enitiy.clanwar.Attack;
import Core.Enitiy.clanwar.ClanWarMember;
import Core.Enitiy.clanwar.WarInfo;
import com.fthlbot.discordbotfthl.Util.Utils;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.*;
import java.util.List;

public class DefenseForOpponent {
    private static final Logger log = LoggerFactory.getLogger(DefenseForOpponent.class);
    private final static int NAME_MAX_LEN = 20, ID_MAX_LEN = 11, ALIAS_MAX_LEN = 10;



    public EmbedBuilder getDefEmbed(User user, WarInfo c) {
        Map<ClanWarMember, List<Attack>> defAndAttacks = this.getDefAndAttacks(c);
        StringBuilder stringBuilder = setDefense(defAndAttacks);
        EmbedBuilder em = new EmbedBuilder();
        em = em.setTitle("Defenses for " + c.getEnemy().getName())
                .setDescription(stringBuilder.toString())
                .setColor(Color.cyan)
                .setAuthor(user)
                .setTimestampToNow();
        return em;
    }

    class tempWarMember {
        private final List<Attack> attacks;
        private final ClanWarMember clanWarMember;

        public tempWarMember(List<Attack> attacks, ClanWarMember clanWarMember) {
            this.attacks = attacks;
            this.clanWarMember = clanWarMember;
        }

        public ClanWarMember getClanWarMember() {
            return clanWarMember;
        }

        public List<Attack> getAttacks() {
            return attacks;
        }

        @Override
        public String toString() {
            return "tempWarMember{" +
                    "attacks=" + attacks +
                    ", clanWarMember=" + clanWarMember +
                    '}';
        }
    }

    private static String formatRow(String name, String tag, String alias, String ext) {
        return String.format("%-" + (ID_MAX_LEN + ext.length()) + "s%-" + (ALIAS_MAX_LEN + ext.length()) +
                "s%-" + NAME_MAX_LEN + "s", name + ext, tag + ext, alias);
    }

    //perfect  example for setting fresh hits just add a spark after the attack lenght is 1 and is a 3 star
    private StringBuilder setDefense(Map<ClanWarMember, List<Attack>> defence) {
        List<tempWarMember> tempWarMembers = new ArrayList<>();
        defence.forEach((x, y) -> {
            tempWarMember e = new tempWarMember(y, x);
            tempWarMembers.add(e);
        });

        List<tempWarMember> collect = tempWarMembers.stream()
                .sorted(Comparator.comparingInt(x -> x.getClanWarMember().getMapPosition()))
                .toList();
        StringBuilder s = new StringBuilder();
        for (tempWarMember x : collect) {
            if (x.getAttacks().isEmpty()) {
                continue;
            }
            final int[] defWon = {0};
            for (Attack attack : x.getAttacks()) {
                if (attack.getStars() <= 0)
                    defWon[0]++;
            }
            String defwonstats = "`  " + defWon[0] + "/" + x.getAttacks().size();
            x.attacks.sort(Comparator.comparingInt(Attack::getStars));//.stream().anyMatch(a -> a.getStars().equals(3));
            defwonstats += "⭐".repeat(x.attacks.get(x.attacks.size() - 1).getStars());


            if (x.getAttacks().size() == 1) {
                if (x.getAttacks().get(0).getStars().equals(3)) {
                    defwonstats += "\uD83D\uDCA5";
                }
            }
            String temp = formatRow(Utils.getTownHallEmote(x.getClanWarMember().getTownhallLevel()), defwonstats, x.getClanWarMember().getName() + "`", " ");
            s.append(temp).append("\n");
        }
        return s;
    }

    private Map<ClanWarMember, List<Attack>> getDefAndAttacks(WarInfo war) {
        List<ClanWarMember> homeWarMembers =  war.getEnemy().getWarMembers();
        List<ClanWarMember> enemyWarMembers =war.getClan().getWarMembers();

        Map<ClanWarMember, List<Attack>> defence = new HashMap<>();

        enemyWarMembers.stream()
                .filter(member -> member.getAttacks() != null)
                .forEach(member -> {
                    member.getAttacks().forEach(attack -> {
                        ClanWarMember homeWarMember = null;
                        String defenderTag = attack.getDefenderTag();
                        for (ClanWarMember warMember : homeWarMembers) {
                            if (warMember.getTag().equalsIgnoreCase(defenderTag)) {
                                homeWarMember = warMember;
                                break;
                            }
                        }

                        if (defence.containsKey(homeWarMember)) {
                            List<Attack> attacks = defence.get(homeWarMember);
                            attacks.add(attack);
                            defence.replace(homeWarMember, attacks);
                        } else {
                            List<Attack> newAttacks = new ArrayList<>();
                            newAttacks.add(attack);
                            defence.put(homeWarMember, newAttacks);
                        }
                    });
                });
        return defence;
    }




}
