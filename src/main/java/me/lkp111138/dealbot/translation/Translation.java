package me.lkp111138.dealbot.translation;

import java.util.HashMap;

public class Translation {
    public String BOT_NAME() {
        return "jokedealbot";
    }
    public String ERROR() {
        return "An error occurred: ";
    }
    public String JOIN_SUCCESS() {
        return "You have successfully joined the game in *%s*! Game ID: %d";
    }
    public String BACK_TO() {
        return "Back to ";
    }
    public String JOINED_ANNOUNCEMENT() {
        return "[ <a href=\"tg://user?id=%d\">%s</a> ] has joined the game! <b>%d</b> out of <b>5</b> has joined.";
    }
    public String START_ME_FIRST() {
        return "Please start me first!";
    }
    public String START_ME() {
        return "Start me";
    }
    public String EXTENDED_ANNOUNCEMENT() {
        return "Extended for 30 seconds. %d seconds left to /join";
    }
    public String GAME_STARTING_ANNOUNCEMENT() {
        return "The game is starting, please wait...";
    }
    public String PROPERTY_NAME(int index) {
        switch (index) {
            case 0:
                return "Chek Lap Kok";
            case 1:
                return "Mui Wo";
            case 2:
                return "Peng Chau";
            case 3:
                return "Cheung Chau";
            case 4:
                return "Lamma Island";
            case 5:
                return "Lo Wu";
            case 6:
                return "Yuen Long";
            case 7:
                return "Sham Cheng";
            case 8:
                return "Kwai Chung";
            case 9:
                return "Sha Tin";
            case 10:
                return "Sai Kung";
            case 11:
                return "Lei Yue Mun";
            case 12:
                return "Wong Tai Sin";
            case 13:
                return "Kowloon Tong";
            case 14:
                return "Sham Shui Po";
            case 15:
                return "Mong Kok";
            case 16:
                return "Tsim Sha Tsui";
            case 17:
                return "Causeway Bay";
            case 18:
                return "Happy Valley";
            case 19:
                return "Central";
            case 20:
                return "Repulse Bay";
            case 21:
                return "Victoria Peak";
            case 22:
                return "Airport Station";
            case 23:
                return "Tsing Yi Station";
            case 24:
                return "Kowloon Station";
            case 25:
                return "Hong Kong Station";
            case 26:
                return "Hong Kong Electric";
            case 27:
                return "Water Works";
        }
        return "";
    }
    public String WILD_CARD() {
        return "Rainbow Wild Card";
    }
    public String WILD_CARD(int g1, int g2) {
        return "Wild Card [" + g1 + " / " + g2 + "]";
    }
    public String ITS_MY_BDAY() {
        return "It's my Birthday!";
    }
    public String DEBT_COLLECTOR() {
        return "Debt Collector";
    }
    public String GO_PASS() {
        return "GO Pass";
    }
    public String DBL_RENT() {
        return "Double Rent";
    }
    public String JUST_SAY_NO() {
        return "Just Say No!";
    }
    public String DEAL_BREAKER() {
        return "Deal Breaker";
    }
    public String SLY_DEAL() {
        return "Sly Deal";
    }
    public String HOUSE() {
        return "House";
    }
    public String HOTEL() {
        return "Hotel";
    }
    public String FORCED_DEAL() {
        return "Forced Deal";
    }
    public String WILDCARD_RENT(int g1, int g2) {
        return String.format("Rent [%d / %d]", g1, g2);
    }
    public String RAINBOW_RENT() {
        return "Wildcard Rent";
    }
    public String CURRENT_STATE() {
        return "Current State:";
    }
    public String CARDS_IN_HAND() {
        return "Cards in hand: ";
    }
    public String CARDS_IN_CURRENCY_DECK() {
        return "Cards in currency deck: ";
    }
    public String PROPERTIES() {
        return "Properties";
    }
    public String PROPERTY_GROUP(int group) {
        switch (group) {
            case 0:
                return "Brown";
            case 1:
                return "Light Blue";
            case 2:
                return "Pink";
            case 3:
                return "Orange";
            case 4:
                return "Red";
            case 5:
                return "Yellow";
            case 6:
                return "Green";
            case 7:
                return "Blue";
            case 8:
                return "Railway Works";
            case 9:
                return "Public Utilities";
        }
        return "";
    }
    public String JUST_SAY_NO_DESC() {
        return "This card can be used to neutralize an action against you, including another Just Say No.";
    }
    public String PASS() {
        return "Pass";
    }
    public String PLAYED_ANNOUNCEMENT(String card) {
        return "%s played " + card;
    }
    public String PASS_ANNOUNCEMENT() {
        return "%s ended their turn";
    }
    public String INVALID_HAND() {
        return "Invalid combination, please try again";
    }
    public String WON_ANNOUNCEMENT(int tgid, String name) {
        return String.format("<a href=\"tg://user?id=%1$s\">%2$s</a> has successfully gathered 3 full sets of properties and won!", tgid, name);
    }
    public String NEW_GAME_PROMPT() {
        return "\nType /play to start a new game";
    }
    public String GAME_ENDED() {
        return "Game ended";
    }
    public String GAME_ENDED_ANNOUNCEMENT() {
        return "Game ended. /play to start a new one";
    }
    public String AFK_KILL() {
        return "Looks like everybody is away, stopping game!";
    }
    public String YOUR_TURN_ANNOUNCEMENT() {
        return "It's your turn, <a href=\"tg://user?id=%d\">%s</a>, you have %d seconds to play your cards!";
    }
    public String PICK_CARDS() {
        return "Pick your cards";
    }
    public String JOIN_PROMPT() {
        return "You have %d seconds left to /join";
    }
    public String NO_GAME_TO_JOIN() {
        return "There is no game to join here. /play to start one.";
    }
    public String GAME_STARTING() {
        return "A game is about to start! Type /join to join.";
    }
    public String GAME_STARTED() {
        return "The game has already started! Wait for it to finish before starting a new one.";
    }
    public String GAME_START_ANNOUNCEMENT() {
        return "[ <a href=\"tg://user?id=%d\">%s</a> ] has started a new game! You have %d seconds to /join\n\nGame ID: %d";
    }
    public String NOTHING_ON_DESK() {
        return "\nThere is nothing on the desk.\n";
    }
    public String SORT_SUIT() {
        return "Sort by suit";
    }
    public String SORT_FACE() {
        return "Sort by face";
    }
    public String FLEE_ANNOUNCEMENT() {
        return "<a href=\"tg://user?id=%d\">%s</a> has fled from the game! %d player%s remaining.";
    }
    public String MAINT_MODE_NOTICE() {
        return "Bot is under maintenance, please try again later.";
    }
    public String CLOSE() {
        return "Close";
    }

    public String ACHIEVEMENT_UNLOCKED() {
        return "Achievement Unlocked!\n";
    }

    public String ACHV_UNLOCKED() {
        return "Achievements unlocked:\n";
    }

    public String A_TOTAL_OF() {
        return "A total of %d.";
    }

    public String HELP() {
        return "**Commands**\n" +
                "\n" +
                "/stats - View your stats.\n" +
                "/achv - View your achievements.\n" +
                "/help - Shows help.\n" +
                "\n" +
                "**Group only commands**\n" +
                "\n" +
                "/play - Starts a new game.\n" +
                "/startgame - Alias of /play\n" +
                "/join - Joins a game.\n" +
                "/flee - Leaves a game while it is still recruiting.\n" +
                "/extend - Extend the recruiting period.\n" +
                "\n" +
                "**Group admin only commands**\n" +
                "\n" +
                "/config - Changes game configuration, opens in PM.\n" +
                "/setlang - Changes game language.\n" +
                "\n" +
                "**Technical**\n" +
                "\n" +
                "/runinfo - Displays some info.\n" +
                "/ping - Unknown command.";
    }

    public String ACHIEVEMENT_TITLE(String achv_key) {
        switch (achv_key) {
            case "FIRST_GAME":
                return "**Newbie**\n";
            case "FIRST_WIN":
                return "**I won**\n";
            case "PLAY_WITH_MINT":
                return "**You need a mint?**\n";
            case "FIRST_BLOOD":
                return "**First Game**\n";
            case "ROOKIE":
                return "**Rookie**\n";
            case "FAMILIARIZED":
                return "**Familiarized**\n";
            case "ADDICTED":
                return "**Addicted**\n";
            case "AMATEUR":
                return "**Amateur**\n";
            case "ADEPT":
                return "**Adept**\n";
            case "EXPERT":
                return "**Expert**\n";
            case "LOSE_IT_ALL":
                return "**Lose it all**\n";
            case "DEEP_FRIED":
                return "**Deep fried**\n";
            default:
                return achv_key;
        }
    }

    public String ACHIEVEMENT_DESC(String achv_key) {
        switch (achv_key) {
            case "FIRST_GAME":
                return "Play your first game.";
            case "FIRST_WIN":
                return "Win a game.";
            case "PLAY_WITH_MINT":
                return "Play a game with the developer.";
            case "FIRST_BLOOD":
                return "Lose a game along with some chips.";
            case "ROOKIE":
                return "Play 50 games.";
            case "FAMILIARIZED":
                return "Play 200 games.";
            case "ADDICTED":
                return "Play 1000 games. Loved the game don't you?";
            case "AMATEUR":
                return "Win 20 games.";
            case "ADEPT":
                return "Win 100 games. That's impressive.";
            case "EXPERT":
                return "Win 500 games. That's insane!";
            case "LOSE_IT_ALL":
                return "Lose a game without playing a single card. Oof.";
            case "DEEP_FRIED":
                return "Win a game while your opponents do not get a chance to play a single card.";
            default:
                return achv_key;
        }
    }

    private static HashMap<String, Translation> translations;
    private static final Translation DEFAULT = new Translation();

    public static Translation get(String lang) {
        if (translations == null) {
            translations = new HashMap<>();
            translations.put("en", DEFAULT);
            translations.put("zh", new TraditionalChinese());
            translations.put("hk", new HongKongChinese());
        }
        return translations.getOrDefault(lang, DEFAULT);
    }

    public String JOIN_69_PROTEST() {
        return "Hong Kong's situation is becoming worse - the police is out of control, " +
                "and government is refusing to address the issue.\n" +
                "Please tweet #StandWithHongKong to raise international awareness about my home.\n" +
                "Thank you." +
                "Use /toggle69 to turn this message off.";
    }

    public String GAME_ENDED_ERROR() {
        return "An error occurred! The game is killed!";
    }

    public String NEXT_GAME_QUEUED(String name) {
        return String.format("You will be notified when a game is about to start in %s", name);
    }

    public String GAME_STARTING_IN(String name) {
        return String.format("A game is about to start in %s", name);
    }

    public String CANCEL() {
        return "Cancel";
    }
}
