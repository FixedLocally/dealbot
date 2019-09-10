package me.lkp111138.dealbot.translation;

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
    }public String GAME_STARTING_ANNOUNCEMENT() {
        return "The game is starting, please wait...";
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
    public String WILD_CARD() {
        return "多功能物業牌";
    }
    public String WILD_CARD(int g1, int g2) {
        return "多功能物業牌 [" + g1 + " / " + g2 + "]";
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
        return "做出反對";
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
        return String.format("租金 [%d / %d]", g1, g2);
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
    public String CHOOSE_AN_ACTION(int remaining) {
        return String.format("選擇一個行動（尚餘 %d 個）", remaining);
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
            return String.format("%s 正向你收集 $ %dM 作為生日禮物！你有 %d 秒選擇如何繳付。",
                    collector, amount, secs);
        }
        return "";
    }
    public String PAY(int value) {
        return String.format("繳付（ %dM）", value);
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
    public String SAID_YES() {
        return "你沒有使用作出反對";
    }
    public String PAYMENT_THX() {
        return "感謝繳付 - ";
    }
    public String DISPOSE_CARD(int remaining) {
        return String.format("請棄置一些卡牌 (尚餘 %d 張)", remaining);
    }
    @Override
    public String PASS_ANNOUNCEMENT() {
        return "%s 選擇結束回合";
    }
    @Override
    public String INVALID_HAND() {
        return "組合無效，請再試";
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
        return "**指令列表**\n" +
                "\n" +
                "/stats - 顯示統計數據。\n" +
                "/achv - 顯示你已解鎖的成就。\n" +
                "/help - 顯示此列表。\n" +
                "\n" +
                "**群組指令**\n" +
                "\n" +
                "/play - 開始新遊戲。\n" +
                "/startgame - 與 /play 相同\n" +
                "/join - 加入遊戲。\n" +
                "/flee - 在遊戲開始前離開遊戲。\n" +
                "/extend - 延長加入遊戲階段\n" +
                "\n" +
                "**僅限群組管理員**\n" +
                "\n" +
                "/config - 在私訊中更改群組遊戲設定。\n" +
                "/setlang - 更改群組語言\n" +
                "\n" +
                "**技術指令**\n" +
                "\n" +
                "/runinfo - 顯示某些資料。\n" +
                "/ping - 未知的指令。";
    }

    @Override
    public String ACHIEVEMENT_TITLE(String achv_key) {
        switch (achv_key) {
            case "FIRST_GAME":
                return "**新手**\n";
            case "FIRST_WIN":
                return "**我贏了！**\n";
            case "PLAY_WITH_MINT":
                return "**You need a mint?**\n";
            case "FIRST_BLOOD":
                return "**第一滴血**\n";
            case "ROOKIE":
                return "**初出茅廬**\n";
            case "FAMILIARIZED":
                return "**熟手**\n";
            case "ADDICTED":
                return "**上癮**\n";
            case "AMATEUR":
                return "**業餘選手**\n";
            case "ADEPT":
                return "**老手**\n";
            case "EXPERT":
                return "**專家**\n";
            case "LOSE_IT_ALL":
                return "**一敗塗地**\n";
            case "DEEP_FRIED":
                return "**油炸**\n";
            default:
                return achv_key;
        }
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
}
