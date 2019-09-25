package me.lkp111138.dealbot.translation;

import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.Card;

public class TraditionalChinese extends Translation {
    @Override
    public String BOT_NAME() {
        return "jokedealbot";
    }
    @Override
    public String ERROR() {
        return "發生錯誤：";
    }
    @Override
    public String JOIN_SUCCESS() {
        return "你已成功加入 *%s* 的遊戲！遊戲編號：%d";
    }
    @Override
    public String BACK_TO() {
        return "返回 ";
    }
    @Override
    public String JOINED_ANNOUNCEMENT() {
        return "[ <a href=\"tg://user?id=%d\">%s</a> ] 已加入遊戲！ 已有 <b>%d</b> 名玩家，最多可有 <b>5</b> 名。";
    }
    @Override
    public String START_ME_FIRST() {
        return "請先啟用我！";
    }
    @Override
    public String START_ME() {
        return "啟用";
    }
    @Override
    public String EXTENDED_ANNOUNCEMENT() {
        return "加入時間已延長30秒，尚餘 %d 秒。 /join";
    }
    public String GAME_STARTING_ANNOUNCEMENT() {
        return "遊戲正在啟動，請稍等...";
    }
    public String PROPERTY_NAME(int index) {
        switch (index) {
            case 0:
                return "赤臘角";
            case 1:
                return "梅窩";
            case 2:
                return "坪洲";
            case 3:
                return "長洲";
            case 4:
                return "南丫島";
            case 5:
                return "羅湖";
            case 6:
                return "元朗";
            case 7:
                return "深井";
            case 8:
                return "葵涌";
            case 9:
                return "沙田";
            case 10:
                return "西貢";
            case 11:
                return "鯉魚門";
            case 12:
                return "黃大仙";
            case 13:
                return "九龍塘";
            case 14:
                return "深水埗";
            case 15:
                return "旺角";
            case 16:
                return "尖沙嘴";
            case 17:
                return "銅鑼灣";
            case 18:
                return "跑馬地";
            case 19:
                return "中環";
            case 20:
                return "淺水灣";
            case 21:
                return "山頂";
            case 22:
                return "機場站";
            case 23:
                return "青衣站";
            case 24:
                return "九龍站";
            case 25:
                return "香港站";
            case 26:
                return "香港電燈";
            case 27:
                return "水務處";
        }
        return "";
    }
    public String NO_OF_FULL_SETS(int sets) {
        return String.format("（%d 套完整物業）", sets);
    }
    public String WILD_CARD() {
        return "多功能物業牌";
    }
    public String WILD_CARD(int g1, int g2) {
        return "多功能物業牌";
    }
    public String ITS_MY_BDAY() {
        return "我的生日";
    }
    public String DEBT_COLLECTOR() {
        return "收取債務";
    }
    public String GO_PASS() {
        return "通行證";
    }
    public String DBL_RENT() {
        return "雙倍租金";
    }
    public String JUST_SAY_NO() {
        return "作出反對";
    }
    public String DEAL_BREAKER() {
        return "物業接管";
    }
    public String SLY_DEAL() {
        return "盜取";
    }
    public String HOUSE() {
        return "房子";
    }
    public String HOTEL() {
        return "酒店";
    }
    public String FORCED_DEAL() {
        return "強制交易";
    }
    public String WILDCARD_RENT(int g1, int g2) {
        return String.format("租金 [%s / %s]", PROPERTY_GROUP(g1), PROPERTY_GROUP(g2));
    }
    public String RAINBOW_RENT() {
        return "多功能租金牌";
    }
    public String CURRENT_STATE() {
        return "目前情況：";
    }
    public String CARDS_IN_HAND() {
        return "手牌數: ";
    }
    public String CARDS_IN_CURRENCY_DECK() {
        return "貨幣牌數：";
    }
    public String PROPERTIES() {
        return "物業";
    }
    public String PROPERTY_GROUP(int group) {
        switch (group) {
            case 0:
                return "啡色";
            case 1:
                return "淺藍色";
            case 2:
                return "粉紅色";
            case 3:
                return "橙色";
            case 4:
                return "紅色";
            case 5:
                return "黃色";
            case 6:
                return "綠色";
            case 7:
                return "藍色";
            case 8:
                return "鐵路事業";
            case 9:
                return "公共事業";
        }
        return "";
    }
    public String JUST_SAY_NO_DESC() {
        return "此卡可以抵銷一個針對你的行動，包括其他作出反對。";
    }
    @Override
    public String PASS() {
        return "結束回合";
    }
    public String MANAGE_CARD_MENU() {
        return "管理多功能物業牌和建築物";
    }
    public String CHOOSE_AN_ACTION(int remaining, int secs) {
        return String.format("選擇一個行動（尚餘 %d 個），你有 %d 秒的時間！", remaining, secs);
    }
    public String SELF_CURRENCY_DECK(int count, int total) {
        return String.format("貨幣堆 （%d / $ %dM）：", count, total);
    }
    public String JUST_SAY_NO_BTN(long count) {
        return "作出反對（你有 " + count + " 張）";
    }
    public String NO() {
        return "不了";
    }
    public String PAYMENT_COLLECTION_MESSAGE_SAY_NO(int group, String collector, int amount) {
        if (group < 10) {
            return String.format("%s 正向你收集 $ %dM 作為 %s 的租金！你要使用作出反對嗎？",
                    collector, amount, PROPERTY_GROUP(group));
        }
        if (group == 10) {
            return String.format("%s 正向你收集 $ %dM 作為生日禮物！你要使用作出反對嗎？",
                    collector, amount);
        }
        if (group == 11) {
            return String.format("%s 正向你收集 $ %dM 的債務！你要使用作出反對嗎？",
                    collector, amount);
        }
        return "";
    }
    public String PAYMENT_COLLECTION_MESSAGE(int group, String collector, int amount, int secs) {
        if (group < 10) {
            return String.format("%s 正向你收集 $ %dM 作為 %s 的租金！你有 %d 秒選擇如何繳付。",
                    collector, amount, PROPERTY_GROUP(group), secs);
        }
        if (group == 10) {
            return String.format("%s 正向你收集 $ %dM 作為生日禮物！你有 %d 秒選擇如何繳付。",
                    collector, amount, secs);
        }
        if (group == 11) {
            return String.format("%s 正向你收集 $ %dM 的債務！你有 %d 秒選擇如何繳付。",
                    collector, amount, secs);
        }
        return "";
    }
    public String PAY(int value) {
        return String.format("繳付（$ %dM）", value);
    }
    public String CHOOSE_CARD_TO_MANAGE() {
        return "選擇要管理的卡牌：";
    }
    public String CHOOSE_RELOCATE() {
        return "選擇擺放此卡的位置：";
    }
    public String GROUP_FULL() {
        return "這組物業已滿";
    }
    public String PAYMENT_TOO_LOW() {
        return "你繳付的金額太低";
    }
    public String SAID_NO() {
        return "你使用了作出反對";
    }
    public String SB_SAID_NO(String name) {
        return name + " 使用了作出反對";
    }
    public String SAID_YES() {
        return "你沒有使用作出反對";
    }
    public String PAYMENT_THX() {
        return "感謝繳付 - ";
    }
    public String DISPOSE_CARD(int remaining) {
        return String.format("請棄置一些卡牌 (尚餘 %d 張)", remaining);
    }
    public String BUILD_THIS_ON(String name) {
        return String.format("將此 %s 放置於哪一組物業？", name);
    }
    public String DEAL_BREAKER_DESC() {
        return "從一名玩家取去一套完整物業，包括任何建築物";
    }
    public String WHOSE_DEAL_TO_BREAK() {
        return "要接管誰的物業？";
    }
    public String VICTIM_SAID_NO(String name) {
        return name + " 使用了作出反對！";
    }
    public String DEAL_BREAKER_SAY_NO_PROMPT(String name, int group) {
        return String.format("%s 對你使用物業接管，打算接管你的 %s 物業。你要使用作出反對嗎？", name, PROPERTY_GROUP(group));
    }
    public String YOU_HAVE_USED_AGAINST(String card, String against) {
        return "你對 " + against + " 使用了 " + card;
    }
    public String YOU_HAVE_USED(String card) {
        return "你使用了 " + card;
    }
    public String SOMEONE_HAVE_USED_AGAINST(String user, String card, String against) {
        return user + " 對 " + against + " 使用了 " + card;
    }
    public String SOMEONE_HAVE_USED(String user, String card) {
        return user + " 使用了 " + card;
    }
    public String DEBT_COLLECTOR_PROMPT(String victim) {
        return String.format("正在對 %s 收取 $ 5M 的債務", victim);
    }
    public String DEBT_COLLECTOR_CHOOSE_PROMPT() {
        return "選擇一名玩家來支付此債務。";
    }
    public String DEBT_COLLECTOR_DESC() {
        return "從一名玩家收取 $ 5M。";
    }
    public String DBL_RENT_DESC() {
        return "下次收取租金時，金額將作雙倍計算。";
    }
    public String DBL_RENT_MSG(int multiplier) {
        return String.format("本輪下一次收取租金時，金額將作 %d 倍計算。", multiplier);
    }
    public String FORCED_DEAL_DESC() {
        return "以自己一項物業強制換取另一名玩家的一項物業。不得為完整一套物業之一。";
    }
    public String FORCED_DEAL_TARGET() {
        return "你要和誰強制交易？";
    }
    public String FORCED_DEAL_CHOOSE_TARGET() {
        return "你想要哪張卡？";
    }
    public String FORCED_DEAL_CHOOSE_GIVE() {
        return "你要用哪張卡來交易？";
    }
    public String FORCED_DEAL_SAY_NO_PROMPT(GamePlayer player, Card card, int group, Card selfCard) {
        return String.format("%s 對你使用了強制交易，打算用你 %s 物業內的 %s 交易 %s。你要作出反對嗎？", player.getName(), card.getCardTitle(), PROPERTY_GROUP(group), selfCard.getCardTitle());
    }
    public String GO_PASS_DESC() {
        return "從牌堆取兩張卡";
    }
    public String YOU_HAVE_DRAWN() {
        return "你從牌堆抽取以下卡牌:\n";
    }
    public String BUILDING_DESC(String name, int amt) {
        return String.format("在你的一套完整物業上興建 %s 使其租金增加 $ %dM", name, amt);
    }
    public String COLLECTING_BDAY() {
        return "正在對所有人收集生日禮物。";
    }
    public String BDAY_DESC() {
        return "這是你的生日！所有人都將向你繳付 $ 2M 作為生日禮物。";
    }
    public String RENT_CARD_DESC(int g1, int g2) {
        return String.format("向所有玩家收取 %s 或 %s 的租金。", PROPERTY_GROUP(g1), PROPERTY_GROUP(g2));
    }
    public String RAINBOW_RENT_CARD_DESC() {
        return "向一名玩家收取任意一組物業的租金";
    }
    public String YOU_HAVE_USED_RENT_FOR(String card, int group, int amount) {
        return String.format("你使用了 %s 收取 %s 的租金，合共 $ %dM。", card, PROPERTY_GROUP(group), amount);
    }
    public String SOMEONE_HAVE_USED_RENT_FOR(String name, String card, int group, int amount) {
        return String.format("%s 使用了 %s 收取 %s 的租金，合共 $ %dM。", name, card, PROPERTY_GROUP(group), amount);
    }
    public String YOU_HAVE_USED_RENT_FOR_AGAINST(String card, String victim, int group, int amount) {
        return String.format("你使用了 %1$s 向 %3$s 收取 %2$s 的租金，合共 $ %4$dM。", card, PROPERTY_GROUP(group), victim, amount);
    }
    public String SOMEONE_HAVE_USED_RENT_FOR_AGAINST(String name, String card, String victim, int group, int amount) {
        return String.format("%1$s 使用了 %2$s 向 %4$s 收取 %3$s 的租金，合共 $ %5$dM。", name, card, PROPERTY_GROUP(group), victim, amount);
    }
    public String COLLECTING_RENT(String from, int group, int amount) {
        return String.format("向 %s 收取 %s 的租金，金額為 $ %dM。", from == null ? "所有人" : from, PROPERTY_GROUP(group), amount);
    }
    public String RENT_CHOOSE_GROUP() {
        return "選擇一組物業來收取租金：";
    }
    public String RENT_CHOOSE_PLAYER() {
        return "選擇一名玩家來收取租金：";
    }
    public String SLY_DEAL_DESC() {
        return "盜取一名玩家的一項物業。不得為完整物業之一。";
    }
    public String SLY_DEAL_CHOOSE_PLAYER() {
        return "你要盜取誰的物業？";
    }
    public String SLY_DEAL_SAY_NO_PROMPT(String name, int group, String target) {
        return String.format("%s 對你使用盜取，想要你 %s 物業中的 %s. 你要作出反對嗎？", name, PROPERTY_GROUP(group), target);
    }
    public String ACTION_CARD_DESC(String title, String desc) {
        return String.format("將 %s 存入銀行還是作為行動？\n卡牌描述：%s", title, desc);
    }
    public String AS_CURRENCY() {
        return "\uD83D\uDCB5 存入銀行";
    }
    public String AS_ACTION() {
        return "\uD83D\uDE4B\uD83C\uDFFB\u200D♂️ 作為行動";
    }
    public String YOU_DEPOSITED(String title) {
        return "你將 " + title + " 存入銀行。";
    }
    public String SOMEONE_DEPOSITED(String name, String title) {
        return name + " 將 " + title + " 存入銀行。";
    }
    public String YOU_PLACED_PROP(String title) {
        return "你將 " + title + " 放置於你的物業區。";
    }
    public String SOMEONE_PLACED_PROP(String name, String title) {
        return name + " 將 " + title + " 放置於物業區。";
    }
    public String YOU_PLACED_PROP_AS(String title, int group) {
        return "你將 " + title + " 放置於你的 " + PROPERTY_GROUP(group) + " 物業區。";
    }
    public String SOMEONE_PLACED_PROP_AS(String name, String title, int group) {
        return name + " 將 " + title + " 放置於 " + PROPERTY_GROUP(group) + " 物業區。";
    }
    public String WILDCARD_CHOOSE_GROUP() {
        return "將此卡視為哪種顏色的物業？";
    }
    public String SB_PAID_YOU(String name, String payment) {
        return name + " 向你支付了 " + payment;
    }
    public String SB_PAID_SB(String name, String payee, String payment) {
        return name + " 向 " + payee + " 支付了 " + payment;
    }
    public String SAID_NO_PROMPT_SAY_NO(String name) {
        return name + " 使用了作出反對！你想用另外一張作出反對來抵銷嗎？";
    }
    public String SB_DISPOSED(String name, String card) {
        return name + " 棄置了 " + card;
    }
    public String YOU_DISPOSED(String card) {
        return "你棄置了 " + card;
    }
    @Override
    public String PASS_ANNOUNCEMENT() {
        return "%s 選擇結束回合";
    }
    public String WON_ANNOUNCEMENT(int tgid, String name) {
        return String.format("<a href=\"tg://user?id=%1$s\">%2$s</a> 成功收集三套完整物業，並勝出遊戲！", tgid, name);
    }
    public String PASS_TIMEOUT() {
        return "時間到！";
    }
    public String PASS_CLICK() {
        return "你結束了你的回合。";
    }
    @Override
    public String NEW_GAME_PROMPT() {
        return "\n按 /play 來開始新遊戲";
    }
    @Override
    public String GAME_ENDED() {
        return "遊戲已結束";
    }
    @Override
    public String GAME_ENDED_ANNOUNCEMENT() {
        return "遊戲結束。按 /play 來開始新遊戲";
    }
    @Override
    public String AFK_KILL() {
        return "看來所有人都已離開，停止遊戲！";
    }
    @Override
    public String YOUR_TURN_ANNOUNCEMENT() {
        return "輪到你了， <a href=\"tg://user?id=%d\">%s</a>，你有 %d 秒出牌！";
    }
    public String ME_CMD_PMED() {
        return "我已經私下告訴你。";
    }
    @Override
    public String PICK_CARDS() {
        return "按此選牌";
    }
    @Override
    public String JOIN_PROMPT() {
        return "尚餘 %d 秒加入遊戲 /join";
    }
    @Override
    public String NO_GAME_TO_JOIN() {
        return "目前沒有遊戲進行中，按 /play 開新遊戲";
    }
    @Override
    public String GAME_STARTING() {
        return "遊戲即將開始！ 按 /join 加入。";
    }
    @Override
    public String GAME_STARTED() {
        return "遊戲已開始！請等遊戲結束後再開新遊戲。";
    }
    @Override
    public String GAME_START_ANNOUNCEMENT() {
        return "[ <a href=\"tg://user?id=%d\">%s</a> ] 已開始新遊戲！你有 %d 秒加入 /join\n\n遊戲編號：%d";
    }
    @Override
    public String NOTHING_ON_DESK() {
        return "\n桌面上什麼也沒有。\n";
    }
    @Override
    public String SORT_SUIT() {
        return "按花色排序";
    }
    @Override
    public String SORT_FACE() {
        return "按點數排序";
    }
    @Override
    public String FLEE_ANNOUNCEMENT() {
        return "<a href=\"tg://user?id=%d\">%s</a> 已退出遊戲！ 尚餘 %d 名玩家";
    }
    @Override
    public String MAINT_MODE_NOTICE() {
        return "正在維修，請稍後再試。";
    }
    @Override
    public String CLOSE() {
        return "關閉";
    }
    @Override
    public String ACHIEVEMENT_UNLOCKED() {
        return "解鎖成就！\n";
    }
    public String ACHV_TITLE(DealBot.Achievement ach) {
        switch (ach) {
            case MASTER:
                return "大師";
            case WINNER:
                return "贏家";
            case ADEPTED:
                return "老手";
            case MANSION:
                return "大宅";
            case ADDICTED:
                return "上癮";
            case FAMILIAR:
                return "新手";
            case THANK_YOU:
                return "感謝繳付！";
            case SHOCK_BILL:
                return "震撼帳單";
            case WELCOME_HOME:
                return "歡迎回家！";
            case HOTEL_MANAGER:
                return "酒店經理";
            case RENT_COLLECTOR:
                return "收租者";
            case GETTING_STARTED:
                return "第一場";
            case WHERE_DID_IT_GO:
                return "去哪了？";
            case NICE_DEAL_WITH_U:
                return "十分值得！";
            case WHAT_WAS_THIS_DEBT:
                return "什麼債務？";
            case YOUR_PROPERTY_ISNT_YOURS:
                return "你的物業不是你的";
            case PLAY_WITH_MINT:
                return "來點薄荷嗎？";
        }
        return "";
    }
    public String ACHV_DESC(DealBot.Achievement ach) {
        switch (ach) {
            case MASTER:
                return "贏 50 場遊戲。";
            case WINNER:
                return "贏 1 場遊戲。";
            case ADEPTED:
                return "贏 10 場遊戲。";
            case MANSION:
                return "在你的完整物業上蓋房子。";
            case ADDICTED:
                return "玩 50 場遊戲。";
            case FAMILIAR:
                return "玩 10 場遊戲。";
            case THANK_YOU:
                return "收取一次 $ 20M 的租金。我是大富豪了！";
            case SHOCK_BILL:
                return "被收取一次 $ 20M 的租金。一夜破產的感覺如何？";
            case WELCOME_HOME:
                return "被收取一次租金。";
            case HOTEL_MANAGER:
                return "在你的完整物業上蓋酒店。";
            case RENT_COLLECTOR:
                return "收取一次租金。";
            case GETTING_STARTED:
                return "玩 1 場遊戲。";
            case WHERE_DID_IT_GO:
                return "被其他玩家使用 \"盜取\" 偷去物業。";
            case NICE_DEAL_WITH_U:
                return "被其他玩家使用 \"強制交易\"。";
            case WHAT_WAS_THIS_DEBT:
                return "被其他玩家使用 \"收取債務\"。";
            case YOUR_PROPERTY_ISNT_YOURS:
                return "被其他玩家使用 \"物業接管\" 取去一整套物業。";
            case PLAY_WITH_MINT:
                return "和開發者玩一局。";
        }
        return "";
    }
    @Override
    public String ACHV_UNLOCKED() {
        return "已解鎖成就：\n";
    }
    @Override
    public String A_TOTAL_OF() {
        return "合共 %d 個";
    }

    @Override
    public String HELP() {
        return "<b>指令列表</b>\n" +
                "\n" +
                "/stats - 顯示統計數據。\n" +
                "/achv - 顯示你已解鎖的成就。\n" +
                "/help - 顯示此列表。\n" +
                "\n" +
                "<b>群組指令</b>\n" +
                "\n" +
                "/play - 開始新遊戲。\n" +
                "/startgame - 與 /play 相同\n" +
                "/join - 加入遊戲。\n" +
                "/flee - 在遊戲開始前離開遊戲。\n" +
                "/extend - 延長加入遊戲階段\n" +
                "\n" +
                "<b>僅限群組管理員</b>\n" +
                "\n" +
                "/config - 在私訊中更改群組遊戲設定。\n" +
                "/setlang - 更改群組語言\n" +
                "\n" +
                "<b>技術指令</b>\n" +
                "\n" +
                "/runinfo - 顯示某些資料。\n" +
                "/ping - 未知的指令。";
    }

    @Override
    public String ACHIEVEMENT_DESC(String achv_key) {
        switch (achv_key) {
            case "FIRST_GAME":
                return "玩一場遊戲。";
            case "FIRST_WIN":
                return "贏一場遊戲。";
            case "PLAY_WITH_MINT":
                return "個遊戲開發者玩一場。";
            case "FIRST_BLOOD":
                return "輸一場遊戲，同時失去籌碼。";
            case "ROOKIE":
                return "玩 50 場遊戲。";
            case "FAMILIARIZED":
                return "玩 200 場遊戲。";
            case "ADDICTED":
                return "玩 1000 場遊戲。";
            case "AMATEUR":
                return "贏 20 場遊戲。";
            case "ADEPT":
                return "贏 100 場遊戲。";
            case "EXPERT":
                return "贏 500 場遊戲。";
            case "LOSE_IT_ALL":
                return "在一場遊戲中輸 13 張牌。";
            case "DEEP_FRIED":
                return "贏一場遊戲，當中你的對手並未打出任何一張牌。";
            default:
                return achv_key;
        }
    }

    @Override
    public String JOIN_69_PROTEST() {
        return "香港嘅情況日益嚴峻 - 警察肆意攻擊同拘捕市民，" +
                "而政府一直拒絕回應五大訴求。\n" +
                "請幫手去各大社交平台分享 #StandWithHongKong 嚟喚起國際關注。\n" +
                "唔該曬你。" +
                "打 /toggle69 唔再顯示呢個訊息";
    }

    @Override
    public String GAME_ENDED_ERROR() {
        return "發生錯誤！遊戲提前結束！";
    }

    @Override
    public String NEXT_GAME_QUEUED(String name) {
        return String.format("當遊戲喺 %s 度開始嗰陣，我會通知你", name);
    }

    @Override
    public String GAME_STARTING_IN(String name) {
        return String.format("遊戲即將於 %s 開始", name);
    }

    @Override
    public String CANCEL() {
        return "取消";
    }

    @Override
    public String SB_IS_ELIMINATED(String name) {
        return String.format("%s 已經連續三回合沒有參與遊戲！出局！", name);
    }

    @Override
    public String LONE_WIN(int tgid, String name) {
        return String.format("其他人均已出局，<a href=\"tg://user?id=%1$s\">%2$s</a> 尚未出局，並自動勝出遊戲！", tgid, name);
    }
}
