# SlapBot Stewie

SlapBot stewie is a clash of clans leagues administration bot for [FTHL]().
Slapbot was introduced at the start of FTHL season 4. This project aims to be the best 
clash of clans leagues bot.

## Features
> Create a league<br />
> Register teams <br />
> Create roster <br />
> Make roster changes <br />
> Manipulate every field for a team, (Team representative, Clan tag, Clan abbreviation)
 and much more!


## Where can I find it?
Either by joining [FTHL](https://discord.gg/x5jKUVkMWm) or by inviting the bot to your own server

## What technology does SlapBot uses?
Slapbot always uses the latest public release of [javacord](https://javacord.org/), development may use 
Snapshot version of javacord for testing, but it's not recommenced to use the dev branch to 
public the bot.

SlapBot also used the latest version of spring boot to manage dependency injection.


## Required JSONs
### To add division week.
will be a json array that has three fields, the first is start date, the second is end date and the third is bye-week boolean.
```json
[
  {
    "start": "08-04-2022",
    "end": "14-04-2022",
    "byeWeek": false
  },
  {
    "start": "15-04-2022",
    "end": "21-04-2022",
    "byeWeek": false
  },
  {
    "start": "22-04-2022",
    "end": "28-04-2022",
    "byeWeek": false
  },
  {
    "start": "29-04-2022",
    "end": "05-05-2022",
    "byeWeek": false
  }
]
```
### To schedule wars for a division week. 
will have an int divWeek, and an array of schedule wars, the array will have two element home team and enemy team, should be ints.
```json
{
  "divWeekID": 1,
  "schedule": [
    {
      "home": 9,
      "enemy": 9
    },
    {
      "home": 9,
      "enemy": 9
    },
    {
      "home": 9,
      "enemy": 9
    }
  ]
}
```
