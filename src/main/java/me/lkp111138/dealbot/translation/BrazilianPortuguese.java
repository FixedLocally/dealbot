package me.lkp111138.dealbot.translation;

import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.Card;

public class BrazilianPortuguese extends Translation {
    public String BOT_NAME() {
        return "jokedealbot";
    }
    public String ERROR() {
        return "Ocorreu um erro: ";
    }
    public String JOIN_SUCCESS() {
return "Você entrou em um jogo em <b>%s</b>! ID da Partida: %d";
    }
    public String BACK_TO() {
        return "Voltar para ";
    }
    public String JOINED_ANNOUNCEMENT() {
return "[<a href=\"tg://user?id=%d\">%s</a>] entrou na partida! <b>%d</b> de <b>5</b> entraram.";
    }
    public String START_ME_FIRST() {
        return "Por favor, abra uma conversa privada comigo!";
    }
    public String START_ME() {
        return "Iniciar bot";
    }
    public String EXTENDED_ANNOUNCEMENT() {
return "Estender por 30 segundos. %d segundos restantes para enviar /join";
    }
    public String GAME_STARTING_ANNOUNCEMENT() {
        return "A partida está começando, por favor, aguarde...";
    }
    public String PROPERTY_NAME(int index) {
        switch (index) {
            case 0:
                return "Rua Augusta";
            case 1:
                return "Av. Europa";
            case 2:
                return "Av. Pacaembú";
            case 3:
                return "Copacabana";
            case 4:
                return "Av. Atlântica";
            case 5:
                return "Ipanema";
            case 6:
                return "Botafogo";
            case 7:
                return "Brooklin";
            case 8:
                return "Flamengo";
            case 9:
                return "Av. 9 de Julho";
            case 10:
                return "Av. Brigadeiro Faria Lima";
            case 11:
                return "Av. Rebouças";
            case 12:
                return "Leblon";
            case 13:
                return "Av. Presidente Vargas";
            case 14:
                return "Av. Maracanã";
            case 15:
                return "Av. Paulista";
            case 16:
                return "Av. Brasil";
            case 17:
                return "Jardim Europa";
            case 18:
                return "Interlagos";
            case 19:
                return "Morumbi";
            case 20:
                return "Barra da Tijuca";
            case 21:
                return "Rua Oscar Freire";
            case 22:
                return "Aeroporto de Guarulhos";
            case 23:
                return "Aeroporto de Congonhas";
            case 24:
                return "Aeroporto de Brasília";
            case 25:
                return "Aeroporto do Galeão";
            case 26:
                return "Companhia Elétrica";
            case 27:
                return "Companhia de Água";
        }
        return "";
    }
    public String NO_OF_FULL_SETS(int sets) {
        return String.format(" (%d conjuntos completos)", sets);
    }
    public String WILD_CARD() {
        return "🃏 Curinga 🔴🟠🟡🟢🔵🟣⚫️🟤✈️💡";
    }
    public String WILD_CARD(int g1, int g2) {
        return "🃏 Curinga";
    }
    public String ITS_MY_BDAY() {
        return "🎂 É meu aniversário!";
    }
    public String DEBT_COLLECTOR() {
        return "🚪 Cobrador de dívidas";
    }
    public String GO_PASS() {
        return "🎟 Passe Livre";
    }
    public String DBL_RENT() {
        return "2️⃣ Aluguel em dobro";
    }
    public String JUST_SAY_NO() {
        return "🙅 Diga Não!";
    }
    public String DEAL_BREAKER() {
        return "😎 Golpe Baixo";
    }
    public String SLY_DEAL() {
        return "⬅️ Negociação Ligeira";
    }
    public String HOUSE() {
        return "🏠 Casa";
    }
    public String HOTEL() {
        return "🏨 Hotel";
    }
    public String FORCED_DEAL() {
        return "🔄 Negociação Forçada";
    }
    public String WILDCARD_RENT(int g1, int g2) {
        return String.format("✍️ Aluguel [%s / %s]", PROPERTY_GROUP(g1), PROPERTY_GROUP(g2));
    }
    public String RAINBOW_RENT() {
        return "✍️ Aluguel 🔴🟠🟡🟢🔵🟣⚫️🟤✈️💡";
    }
    public String CURRENT_STATE() {
        return "ℹ️ Estado atual:";
    }
    public String CARDS_IN_HAND() {
        return "Cartas na mão: ";
    }
    public String CARDS_IN_CURRENCY_DECK() {
        return "Cartas na pilha de dinheiro: ";
    }
    public String PROPERTIES() {
        return "Propriedades";
    }
    public String PROPERTY_GROUP(int group) {
        switch (group) {
            case 0:
                return "🟤";
            case 1:
                return "🟣";
            case 2:
                return "⚫️";
            case 3:
                return "🟠";
            case 4:
                return "🔴";
            case 5:
                return "🟡";
            case 6:
                return "🟢";
            case 7:
                return "🔵";
            case 8:
                return "✈️";
            case 9:
                return "💡";
        }
        return "";
    }
    public String JUST_SAY_NO_DESC() {
        return "Esta carta pode ser usada para neutralizar uma ação contra você, incluindo outro Diga Não!";
    }
    public String PASS() {
        return "Finalizar minha vez";
    }
    public String MANAGE_CARD_MENU() {
        return "Ajustar curingas e construções";
    }
    public String CHOOSE_AN_ACTION(int remaining, int secs) {
        return String.format("Escolha uma ação (%d restantes). Você tem %d segundos!", remaining, secs);
    }
    public String SELF_CURRENCY_DECK(int count, int total) {
        return String.format("Pilha de dinheiro (%d / $ %dM): ", count, total);
    }
    public String JUST_SAY_NO_BTN(long count) {
        return "Diga Não! (Você tem "+ count +") ";
    }
    public String NO() {
        return "Não";
    }
    public String PAYMENT_COLLECTION_MESSAGE_SAY_NO(int group, String collector, int amount) {
        if (group < 10) {
        return String.format("%s está coletando $ %dM de você como aluguel para o grupo %d! Deseja Dizer Não? ",
                    collector, amount, group);
        }
        if (group == 10) {
        return String.format ("%s está coletando $ %dM como presente de aniversário de você! Deseja Dizer Não? ",
                    collector, amount);
        }
        if (group == 11) {
        return String.format ("%s está cobrando uma dívida de $ %dM de você! Deseja Dizer Não? ",
                    collector, amount);
        }
        return "";
    }
    public String PAYMENT_COLLECTION_MESSAGE(int group, String collector, int amount, int secs) {
        if (group < 10) {
        return String.format ("%s está coletando $ %dM de você como aluguel para o grupo%d ! Você tem %d segundos para escolher como pagar. ",
                    collector, amount, group, secs);
        }
        if (group == 10) {
return String.format ("%s está coletando $ %dM como presente de aniversário de você! Você tem %d segundos para escolher como pagar. ",
                    collector, amount, secs);
        }
        if (group == 11) {
return String.format ("%s está cobrando uma dívida de $ %dM de você! Você tem %d segundos para escolher como pagar. ",
                    collector, amount, secs);
        }
        return "";
    }
    public String PAY(int value) {
        return String.format ("Pagar ($ %dM)", value);
    }
    public String CHOOSE_CARD_TO_MANAGE() {
        return "Escolha uma carta para gerenciar:";
    }
    public String CHOOSE_RELOCATE() {
        return "Escolha um grupo para mover esta carta:";
    }
    public String GROUP_FULL() {
        return "Este grupo está cheio";
    }
    public String PAYMENT_TOO_LOW() {
        return "O valor que você pagou é muito baixo";
    }
    public String SAID_NO() {
        return "Você usou Diga Não!";
    }
    public String SB_SAID_NO(String name) {
        return name + " usou Diga Não!";
    }
    public String SAID_YES() {
        return "Você não usou Diga Não!";
    }
    public String PAYMENT_THX() {
        return "Obrigado pelo seu pagamento - ";
    }
    public String DISPOSE_CARD(int remaining) {
return String.format ("Descarte algumas cartas para ficar com 7 cartas (%d restantes)", remaining);
    }
    public String BUILD_THIS_ON(String name) {
return String.format ("Construir %s em qual grupo?", name);
    }
    public String DEAL_BREAKER_DESC() {
return "Retira um conjunto completo de propriedades de um jogador, incluindo qualquer construção.";
    }
    public String WHOSE_DEAL_TO_BREAK() {
        return "Em quem você aplicar o Golpe Baixo?";
    }
    public String VICTIM_SAID_NO(String name) {
        return name + " usou Diga Não!";
    }
    public String DEAL_BREAKER_SAY_NO_PROMPT(String name, int group) {
return String.format ("%s usou o Golpe Baixo contra sua propriedade %s. Deseja dizer não?", name, PROPERTY_GROUP(group));
    }
    public String YOU_HAVE_USED_AGAINST(String card, String against) {
return "Você usou " + card + " contra " + against;
    }
    public String YOU_HAVE_USED(String card) {
return "Você usou " + card;
    }
    public String SOMEONE_HAVE_USED_AGAINST(String user, String card, String against) {
        return user + " usou " + card + " contra " + against;
    }
    public String SOMEONE_HAVE_USED(String user, String card) {
        return user + " usou " + card;
    }
    public String DEBT_COLLECTOR_PROMPT(String victim) {
return "Cobrando dívida de $ 5M de " + victim;
    }
    public String DEBT_COLLECTOR_CHOOSE_PROMPT() {
return "Escolha um jogador para cobrar sua dívida";
    }
    public String DEBT_COLLECTOR_DESC() {
return "Colete $ 5M de um jogador".;
    }
    public String DBL_RENT_DESC() {
        return "Dobra o próximo aluguel que você coletar".;
    }
    public String DBL_RENT_MSG(int multiplier) {
        return String.format ("O próximo aluguel que você coletar neste turno será multiplicado por %d.", multiplier);
    }
    public String FORCED_DEAL_DESC() {
        return "Obtém uma propriedade de um jogador que não faz parte de um conjunto completo em troca de uma propriedade sua.";
    }
    public String FORCED_DEAL_TARGET() {
        return "Quem vai participar desta negociação forçada?";
    }
    public String FORCED_DEAL_CHOOSE_TARGET() {
        return "Qual carta você quer?";
    }
    public String FORCED_DEAL_CHOOSE_GIVE() {
        return "Qual carta você oferece?";
    }
    public String FORCED_DEAL_SAY_NO_PROMPT(GamePlayer player, Card card, int group, Card selfCard) {
        return String.format ( "%s usou Negociação Forçada contra %s em %s por %s. Deseja dizer não?", player.getName (), card.getCardTitle (), PROPERTY_GROUP (group), selfCard.getCardTitle ());
    }
    public String GO_PASS_DESC() {
        return "Compre duas cartas do baralho";
    }
    public String YOU_HAVE_DRAWN() {
        return "Você comprou as seguintes cartas do baralho:\n";
    }
    public String BUILDING_DESC(String name, int amt) {
        return String.format ("Construir %s no topo de seu conjunto completo de propriedades para que seu aluguel seja aumentado em $ %dM", name, amt);
    }
    public String COLLECTING_BDAY() {
        return "Coletando presente de aniversário de todos";
    }
    public String BDAY_DESC() {
        return "É seu aniversário! Todo mundo paga $ 2M como presente.";
    }
    public String RENT_CARD_DESC(int g1, int g2) {
        return String.format ("Coleta aluguel para as propriedades %s ou %s de todos os jogadores", PROPERTY_GROUP (g1), PROPERTY_GROUP (g2));
    }
    public String RAINBOW_RENT_CARD_DESC() {
        return "Coletar aluguel para qualquer conjunto de propriedades de um jogador";
    }
    public String YOU_HAVE_USED_RENT_FOR(String card, int group, int amount) {
return String.format ("Você usou %s para o grupo %s, coletando $ %dM.", card, PROPERTY_GROUP(group), amount);
    }
    public String SOMEONE_HAVE_USED_RENT_FOR(String name, String card, int group, int amount) {
return String.format ("%s usou %s para o grupo %s, coletando $ %dM.", name, card, PROPERTY_GROUP(group), amount);
    }
    public String YOU_HAVE_USED_RENT_FOR_AGAINST(String card, String victim, int group, int amount) {
return String.format ("Você usou %s para o grupo %s contra %s, coletando $ %dM.", card, PROPERTY_GROUP(group), victim, amount);
    }
    public String SOMEONE_HAVE_USED_RENT_FOR_AGAINST(String name, String card, String victim, int group, int amount) {
return String.format ("%s usou %s para o grupo %s contra %s, coletando $ %dM.", name, card, PROPERTY_GROUP(group), victim, amount);
    }
    public String COLLECTING_RENT(String from, int group, int amount) {
return String.format ("Coletando aluguel de $ %dM de %s para %s", amount, from == null ? "todo mundo": from, PROPERTY_GROUP(group));
    }
    public String RENT_CHOOSE_GROUP() {
return "Escolha um grupo para cobrar aluguel:";
    }
    public String RENT_CHOOSE_PLAYER() {
return "Escolha de quem vai cobrar esse aluguel:";
    }
    public String SLY_DEAL_DESC() {
return "Pega uma propriedade de um jogador que não faz parte de um conjunto completo.";
    }
    public String SLY_DEAL_CHOOSE_PLAYER() {
return "Quem vai participar desta negociação ligeira?";
    }
    public String SLY_DEAL_SAY_NO_PROMPT(String name, int group, String target) {
return String.format ("%s usou o Negociação Ligeira contra %s do grupo %s. Deseja dizer não?", name, target, PROPERTY_GROUP(group));
    }
    public String ACTION_CARD_DESC(String title, String desc) {
return String.format ("Usar %s como moeda ou uma ação?\nDescrição da carta: %s", title, desc);
    }
    public String AS_CURRENCY() {
return "💵 Como moeda";
    }
    public String AS_ACTION() {
return "🙋🏻‍♂️ Como ação";
    }
    public String YOU_DEPOSITED(String title) {
return "Você depositou " + título + " em seu banco.";
    }
    public String SOMEONE_DEPOSITED(String name, String title) {
        return name + " depositou " + title + " em seu banco.";
    }
    public String YOU_PLACED_PROP(String title) {
        return "Você colocou " + title + " em suas propriedades.";
    }
    public String SOMEONE_PLACED_PROP(String name, String title) {
        return name + " colocou " + title + " em suas propriedades.";
    }
    public String YOU_PLACED_PROP_AS(String title, int group) {
        return "Você colocou " + title + " em suas propriedades como grupo " + PROPERTY_GROUP(group);
    }
    public String SOMEONE_PLACED_PROP_AS(String name, String title, int group) {
        return name + " colocou " + title + " em suas propriedades como " + PROPERTY_GROUP(group);
    }
    public String WILDCARD_CHOOSE_GROUP() {
return "Usar essa carta em qual grupo?";
    }
    public String SB_PAID_YOU(String name, String payment) {
        return name + " pagou a você " + payment;
    }
    public String SB_PAID_SB(String name, String payee, String payment) {
        return name + " pegou " + payee + " " + payment;
    }
    public String SAID_NO_PROMPT_SAY_NO(String name) {
return name + " usou Diga Não! Você gostaria de usar outro Diga Não contra isso?";
    }
    public String SB_DISPOSED(String name, String card) {
        return name + " descartou " + card;
    }
    public String YOU_DISPOSED(String card) {
        return "Você descartou " + card;
    }
    public String PASS_ANNOUNCEMENT() {
return "%s finalizou a sua vez";
    }
    public String WON_ANNOUNCEMENT(int tgid, String name) {
return String.format ("<a href=\"tg://user?id=%1$s\">%2$s</a> juntou com sucesso 3 conjuntos completos de propriedades e VENCEU!", tgid, name);
    }
    public String PASS_TIMEOUT() {
return "O tempo acabou!";
    }
    public String PASS_CLICK() {
return "Você finalizou a sua vez.";
    }
    public String NEW_GAME_PROMPT() {
return "\nEnvie /play para iniciar um novo jogo";
    }
    public String GAME_ENDED() {
return "Jogo encerrado";
    }
    public String GAME_ENDED_ANNOUNCEMENT() {
return "Jogo encerrado. Envie /play para iniciar um novo";
    }
    public String AFK_KILL() {
return "Parece que todo mundo está fora, parando o jogo!";
    }
    public String YOUR_TURN_ANNOUNCEMENT() {
return "É a sua vez, <a href=\"tg://user?id=%d\">%s</a>, você tem %d segundos para jogar suas cartas!";
    }
    public String ME_CMD_PMED() {
return "Enviei seu status no privado.";
    }
    public String PICK_CARDS() {
return "Escolha suas cartas";
    }
    public String JOIN_PROMPT() {
return "Você tem %d segundos enviar /join";
    }
    public String NO_GAME_TO_JOIN() {
return "Não há nenhum jogo para entrar aqui. Envie /play para iniciar um.";
    }
    public String GAME_STARTING() {
return "Um jogo está prestes a começar! Envie /join para participar.";
    }
    public String GAME_STARTED() {
return "O jogo já começou! Aguarde o término antes de iniciar um novo.";
    }
    public String GAME_START_ANNOUNCEMENT() {
return "[ <a href=\"tg://user?id=%d\">%s</a>] iniciou um novo jogo! Você tem %d segundos para enviar /join e participar\n\n ID do jogo: %d";
    }
    public String NOTHING_ON_DESK() {
return "\nNão há nada sobre a mesa.\n";
    }
    public String SORT_SUIT() {
return "Classificar por naipe";
    }
    public String SORT_FACE() {
return "Classificar por face";
    }
    public String FLEE_ANNOUNCEMENT() {
return "<a href=\"tg://user?id=%d\">%s</a> fugiu do jogo! %d jogador %s restante.";
    }
    public String MAINT_MODE_NOTICE() {
return "Bot está em manutenção, tente novamente mais tarde.";
    }
    public String CLOSE() {
return "Fechar";
    }

    public String ACHIEVEMENT_UNLOCKED() {
return "Conquista desbloqueada!\n";
    }
    public String ACHV_TITLE(DealBot.Achievement ach) {
switch (ach) {
            case MASTER:
                return "Mestre";
            case WINNER:
                return "Vencedor";
            case ADEPTED:
return "Adepto";
            case MANSION:
                return "Mansão";
            case ADDICTED:
                return "Viciado";
            case FAMILIAR:
                return "Familiar";
            case THANK_YOU:
                return "Obrigado!";
            case SHOCK_BILL:
return "Conta assustadora";
            case WELCOME_HOME:
                return "Bem-vindo ao lar!";
            case HOTEL_MANAGER:
return "Gerente de Hotel";
            case RENT_COLLECTOR:
return "Coletor de Aluguel";
            case GETTING_STARTED:
return "Começando";
            case WHERE_DID_IT_GO:
return "Para onde foi?";
            case NICE_DEAL_WITH_U:
return "Bom negociar com você!";
            case WHAT_WAS_THIS_DEBT:
return "O que era essa dívida?";
            case YOUR_PROPERTY_ISNT_YOURS:
return "Sua propriedade não é sua";
            case PLAY_WITH_MINT:
return "Precisa de hortelã?";
        }
        return "";
    }
    public String ACHV_DESC(DealBot.Achievement ach) {
switch (ach) {
            case MASTER:
return "Ganhe 50 jogos.";
            case WINNER:
return "Ganhe um jogo.";
            case ADEPTED:
return "Ganhe 10 jogos.";
            case MANSION:
return "Construa uma casa em todo o seu conjunto de propriedades.";
            case ADDICTED:
return "Jogue 50 jogos.";
            case FAMILIAR:
return "Jogue 10 jogos";
            case THANK_YOU:
return "Colete um aluguel de mais de $ 20M. Deve ser uma delícia.";
            case SHOCK_BILL:
return "Pague um aluguel de mais de $ 20M. Isso deve doer...";
            case WELCOME_HOME:
return "Seja cobrado de um aluguel uma vez.";
            case HOTEL_MANAGER:
return "Construa um hotel em todo o seu conjunto de propriedades.";
            case RENT_COLLECTOR:
        return "Cobre aluguel uma vez.";
            case GETTING_STARTED:
return "Jogue um jogo.";
            case WHERE_DID_IT_GO:
return "Consiga algo roubado por ser o alvo de uma Negociação Ligeira.";
            case NICE_DEAL_WITH_U:
return "Seja forçado a uma negociação pelo Negociação Forçada.";
            case WHAT_WAS_THIS_DEBT:
return "Seja forçado a pagar uma dívida.";
            case YOUR_PROPERTY_ISNT_YOURS:
return "Tenha todo o seu conjunto completo e valioso tirado de alguém.";
            case PLAY_WITH_MINT:
return "Jogue um jogo com o desenvolvedor.";
        }
        return "";
    }

    public String ACHV_UNLOCKED() {
return "Conquista desbloqueada:\n";
    }

    public String A_TOTAL_OF() {
return "Um total de %d.";
    }

    public String HELP() {
return "<b>Comandos</b>\n" +
                "\n" +
"/stats - Veja suas estatísticas.\n" +
"/achv - Veja suas conquistas.\n" +
"/help - Mostra ajuda.\n" +
"/howto - Mostra as regras.\n" +
                "\n" +
"<b>Somente comandos em grupo</b>\n" +
                "\n" +
"/play - Inicia um novo jogo.\n" +
"/startgame - Mesma coisa de /play\n" +
"/join - Entra em um jogo.\n" +
"/flee - Sai de um jogo enquanto ainda estão entrando jogadores.\n" +
"/extend - Estende o período de entrada de jogadores.\n" +
                "\n" +
"<b>Comandos só para admins do grupo</b>\n" +
                "\n" +
"/config - Altera a configuração do jogo, abre no PV.\n" +
"/setlang - Altera o idioma do jogo.\n" +
                "\n" +
"<b>Técnico</b>\n" +
                "\n" +
"/runinfo - Exibe algumas informações.\n" +
"/ping - Comando desconhecido.";
    }

    public String ACHIEVEMENT_TITLE(String achv_key) {
        return ACHV_TITLE(DealBot.Achievement.valueOf(achv_key));
    }

    public String ACHIEVEMENT_DESC(String achv_key) {
        switch (achv_key) {
            case "FIRST_GAME":
return "Jogue seu primeiro jogo.";
            case "FIRST_WIN":
return "Ganhe um jogo.";
            case "PLAY_WITH_MINT":
return "Jogue um jogo com o desenvolvedor.";
            case "FIRST_BLOOD":
return "Perca um jogo junto com algumas fichas.";
            case "ROOKIE":
return "Jogue 50 jogos.";
            case "FAMILIARIZED":
return "Jogue 200 jogos.";
            case "ADDICTED":
return "Jogue 1000 jogos. Adora o jogo, não é?";
            case "AMATEUR":
return "Ganhe 20 jogos.";
            case "ADEPT":
return "Ganhe 100 jogos. Isso é impressionante.";
            case "EXPERT":
return "Ganhe 500 jogos. Isso é loucura!";
            case "LOSE_IT_ALL":
return "Perca um jogo sem jogar uma única carta. Ops.";
            case "DEEP_FRIED":
return "Ganhe um jogo enquanto seus oponentes não têm chance de jogar uma única carta.";
            default:
                return achv_key;
        }
    }

    public String JOIN_69_PROTEST() {
return "Ele morreu porque caiu de um estacionamento de alguns metros de altura sobre um muro de mais de um metro e meio, supostamente correndo, evitando a polícia e gás lacrimogêneo.\n" +
                "https://www.scmp.com/news/hong-kong/politics/article/3036833/hong-kong-student-who-suffered-severe-brain-injury-after\n" +
"Use /toggle69 para desativar esta mensagem.";
    }

    @Override
    public String OCT_5_STRIKE() {
return "\n\nEste bot suspendeu seu serviço hoje.";
    }

    public String GAME_ENDED_ERROR() {
return "Ocorreu um erro! O jogo não responde! ";
    }

    public String NEXT_GAME_QUEUED(String name) {
return String.format ("Você será notificado quando um jogo estiver prestes a começar em %s", name);
    }

    public String GAME_STARTING_IN(String name) {
return String.format ("Um jogo está prestes a começar em %s", name);
    }

    public String CANCEL() {
return "Cancelar";
    }

    public String SB_IS_ELIMINATED(String name) {
        return String.format ("%s esteve ausente por 3 turnos. Está fora!", name);
    }

    public String LONE_WIN(int tgid, String name) {
return String.format ("Todo mundo foi eliminado, <a href=\"tg://user?id=%1$s\">%2$s</a> permaneceu e VENCEU.", tgid, name);
    }

    @Override
    public String HOW_TO_PLAY() {
        return "Como jogar:\n" +
"Monopoly Deal é um jogo baseado em turnos. A ordem de jogo é determinada aleatoriamente no início.\n" +
"Você compra 2 cartas do baralho e pode jogar no máximo 3 cartas. Se você iniciar o turno sem cartas na mão, irá comprar 5 cartas.\n" +
"Todas as cartas de moeda e catas de ação podem ser depositadas em seu banco para uso posterior.\n" +
"Ao pagar aluguéis e taxas, você pode usar toda a moeda e propriedades baixadas na mesa, nada mais. Não é permitido troco.\n" +
"Quando terminar o turno, toque \""+ PASS() +"\".\n" +
"Você pode ter no máximo 7 cartões em sua mão. Se você tiver mais de 7 cartas quando terminar o seu turno, deverá descartar algumas delas para permanecer dentro do limite.\n" +
"Para vencer você deve coletar 3 conjuntos completos de propriedades.\n" +
                "\n" +
"Cartas de ação:\n" +
                "1. " + SLY_DEAL() + "：" + SLY_DEAL_DESC() + "\n" +
                "2. " + FORCED_DEAL() + "：" + FORCED_DEAL_DESC() + "\n" +
                "3. " + DEAL_BREAKER() + "：" + DEAL_BREAKER_DESC() + "\n" +
                "4. " + JUST_SAY_NO() + "：" + JUST_SAY_NO_DESC() + "\n" +
                "5. " + DEBT_COLLECTOR() + "：" + DEBT_COLLECTOR() + "\n" +
                "6. " + ITS_MY_BDAY() + "：" + BDAY_DESC() + "\n" +
                "7. " + DBL_RENT() + "：" + DBL_RENT() + "\n" +
                "8. " + GO_PASS() + "：" + GO_PASS_DESC() + "\n" +
                "9. " + HOUSE() + "：" + BUILDING_DESC(HOUSE(), 3) + "\n" +
                "10. " + HOTEL() + "：" + BUILDING_DESC(HOTEL(), 4);
    }

    @Override
    public String STATS(int id, String name, int win, int total, float timePlayed, int cardsPlayed, int currency, int properties, int rent) {
return String.format("Estatísticas do Monopoly Deal para <a href=\"tg://user?id=%d\">%s</a>:\n", id, name) +
String.format ("Vitórias/Total de jogos: %d / %d (%.2f%%)\n", win, total, 100.0 * win / total) +
String.format ("Total em minutos do jogo: %.1f\n", timePlayed) +
String.format ("Número de cartas jogadas: %d\n", cardsPlayed) +
String.format ("Dinheiro coletado: $ %dM \n", currency) +
String.format ("Propriedades coletadas: %d\n", properties)
                String.format("Aluguel coletado: $ %dM\n", rent);
    }
}
