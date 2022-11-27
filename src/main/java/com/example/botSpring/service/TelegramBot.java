package com.example.botSpring.service;

import com.example.botSpring.config.BotConfig;
import com.example.botSpring.model.User;
import com.example.botSpring.model.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.http.util.TimeStamp;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.vdurmont.emoji.EmojiParser.parseToUnicode;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    final BotConfig config;

    static final String INPUT_TEXT = "Введите топ в таком формате:\n\n" +
            "Топ:\n" +
            "1. первое\n" +
            "2. второе\n" +
            "3. третье\n" +
            "4. четвертое\n" +
            "5. пятое" +
            "\n\n(Не забывайте про пробелы, запятые и конечно надпись Топ:| \n" +
            "Тогда все будет нормально работать:)";
    static final String HELP_TEXT = "Этот бот показывает возможности Spring.\n\n " +
            "Ты можешь выполнить команды через главное меню слева или введя команду.\n\n " +
            "Напиши /start чтобы увидеть приветственное сообщение.\n\n " +
            "Напиши /register чтобы зарегистрироваться в боте\n\n " +
            "Напиши /mydata чтобы увидеть сохраненную историю о тебе.\n\n " +
            "Напиши /settings чтобы выбрать настройки предпочтения\n\n" +
            "Напиши /help чтобы увидеть это сообщение опять";
    String TOP_TEXT =
            "Это ваш топ аниме:\n" +
            "1. пусто \n"   +
            "2. пусто \n"   +
            "3. пусто \n"   +
            "4. пусто \n"   +
            "5. пусто \n"   +

            "\nЧтобы изменить топ введите /settings\n" +
            "\nПрошу простить если сохраненные данные были забыты,\n" +
            "Ведь я неоднократно перезапускаю бота.\n" +
            "(денег на сервер - у меня нет, да и не нужен он мне)" +
            "\nХочу выразить благодарность за то, что тестируете моего бота!";

    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String ERROR_TEXT = "Произошла ошибка";
    static final String DATA_TEXT = "Все что я знаю о тебе:\n";
    static final String BUTTON_ONE = "Топ 1 онгоинг на сегодня";
    static final  String BUTTON_TWO = "Мой топ аниме";
    static final  String BUTTON_THREE = "Сколько всего вышло глав и серий ван пис";
    static final  String BUTTON_FOUR = "Популярные онгоинги сегодня";
    static final  String BUTTON_FIVE = "Топ фильм";

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "Ввести в функционал"));
        listofCommands.add(new BotCommand("/register", "Регистрация"));
        listofCommands.add(new BotCommand("/mydata", "Выдать твои сохраненные данные"));
        listofCommands.add(new BotCommand("/deletedata", "Удалить твои данные"));
        listofCommands.add(new BotCommand("/help", "Выдать информацию как использовать этого бота"));
        listofCommands.add(new BotCommand("/settings", "Установить свои предпочтения"));
        try{
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException e){
            log.error("Ошибка настройки списка команд бота" + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            Message message = update.getMessage();
            if (messageText.contains("/send") && config.getOwnerId() == chatId ) {
                var textToSend =  parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = userRepository.findAll();
                for(User user: users) {
                    prepareAndSendMessage(user.getChatId(), textToSend);
                }
            } else if (messageText.contains("Топ:\n") && config.getOwnerId()== chatId) {
                var textToSend =  parseToUnicode(messageText.substring(messageText.indexOf("1.")));
                var text2 =  parseToUnicode(messageText.substring(messageText.indexOf("\n2.")));
                var text3 =  parseToUnicode(messageText.substring(messageText.indexOf("\n3.")));
                var text4 =  parseToUnicode(messageText.substring(messageText.indexOf("\n4.")));
                var text5 =  parseToUnicode(messageText.substring(messageText.indexOf("\n5.")));
                    TOP_TEXT = "Это ваш топ аниме:\n" +
                            textToSend + " \n" +
                            "\nЧтобы изменить топ введите /settings\n";
                        prepareAndSendMessage(chatId, TOP_TEXT);
            } else {
                switch (messageText) {
                    case "/start":
                        registerUser(update.getMessage());
                        startCommandReceive(chatId, update.getMessage().getChat().getFirstName());
                        break;
                    case  "/mydata":
                        prepareAndSendMessage(chatId, DATA_TEXT);
                        Profile(chatId, update.getMessage().getChat().getFirstName(),
                                update.getMessage().getChat().getLastName(), update.getMessage().getChat().getUserName());
                        break;
                    case "/help":
                        prepareAndSendMessage(chatId, HELP_TEXT);
                        break;
                    case "/register":
                        /// TODO: 11/24/2022 короче сделать так чтобы был аккаунт
                        /// TODO: с именем и возрастом а еще чтобы он назывался по другому.
                        /// TODO: Выдавать свой username и возраст при кнопке да
                        register(chatId);
                        break;
                    case "/deletedata":
                        prepareAndSendMessage(chatId, "Данный функционал пока не работает");
                        break;
                    case "/settings":
                        // prepareAndSendMessage(chatId, INPUT_TEXT);
                        try {
                            sendMsg(message, INPUT_TEXT);
                        } catch (TelegramApiException e) {

                        }

                        break;
                    case BUTTON_ONE:
                        try {
                            parserBestOngoing(chatId, update.getMessage().getChat().getFirstName());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case BUTTON_TWO:
                        prepareAndSendMessage(chatId, TOP_TEXT);
                        break;
                    case BUTTON_THREE:

                        try {
                            parserOP(chatId, update.getMessage().getChat().getFirstName());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        break;
                    case BUTTON_FOUR:

                        try {
                            parserAmountOngoing(chatId, update.getMessage().getChat().getFirstName());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        break;
                    case BUTTON_FIVE:

                        try {
                            parserTopMovieAnime(chatId, update.getMessage().getChat().getFirstName());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    default:

                        prepareAndSendMessage(chatId, "Понять бы еще о чем ты пишешь");

                }

            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals(YES_BUTTON)) {
                String text = "Ты успешно зарегистрирован";

                executeEditMessageText(text, chatId, messageId);
        } else if (callbackData.equals(NO_BUTTON)) {
                String text = "А зачем тогда команду прописал? Типо тестер дохрена? типо пошел ты!";
                executeEditMessageText(text, chatId, messageId);
            }
        }
    }

    private void register(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Ты действительное хочешь зарегистрироваться?");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        var yesButton = new InlineKeyboardButton();
        yesButton.setText("Да");
        yesButton.setCallbackData(YES_BUTTON);

        var noButton = new InlineKeyboardButton();
        noButton.setText("Нет");
        noButton.setCallbackData(NO_BUTTON);

        rowInLine.add(yesButton);
        rowInLine.add(noButton);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        executeMessage(message);
    }

    private void registerUser(Message msg) {

        if(userRepository.findById(msg.getChatId()).isEmpty()){
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();

            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new TimeStamp());

            userRepository.save(user);
            sendMessage(chatId, String.valueOf(user));
            log.info("Пользователь сохранен: " + user);

        }
    }

    private void startCommandReceive(long chatId, String name)   {

        String answer = parseToUnicode("Привет " + name + ", приятно тебя видеть" + " :grinning:");
        log.info("Ответил пользователю " + name);

        sendMessage(chatId, answer);
    }
    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        row.add(BUTTON_ONE);
        row.add(BUTTON_TWO);

        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add(BUTTON_THREE);
        row.add(BUTTON_FOUR);
        row.add(BUTTON_FIVE);

        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);
        executeMessage(message);
    }
    private void executeEditMessageText(String text, long chatId, long messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int)messageId);

        try {
            execute(message);
        } catch (TelegramApiException e){
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e){
            log.error(ERROR_TEXT + e.getMessage());
        }
    }
    private  void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }
    private static Document getPageOnePiece() throws IOException {
        String url = "https://mangalib.me/one-piece?section=chapters";
        Document page = Jsoup.parse(new URL(url), 3000);
        return page;
    }
    private static Document getPageOnePieceSeries() throws IOException {
        String link = "https://jut.su/oneepiece/";
        Document page2 = Jsoup.parse(new URL(link), 3000);
        return page2;
    }
    private void parserOP(long chatId, String parserOP) throws IOException {
        Document page = getPageOnePiece();
        String Ongoing = page.select("div[class=media-info-list__value text-capitalize]").last().text();
        Document page2 = getPageOnePieceSeries();
        String SeriesMovie = page2.select("a[class=short-btn green video the_hildi]").last().text();
        String onePiece = "На мангалибе: Ван пис " + String.valueOf(Ongoing) + " Глава";
        String onePieceSeries = "На jutsu: "+String.valueOf(SeriesMovie);
        sendMessage(chatId, onePiece);
        sendMessage(chatId, onePieceSeries);
    }

    private static Document getPage() throws IOException {
        String url = "https://yummyanime.tv/ongoing/";
        Document page = Jsoup.parse(new URL(url), 3000);
        return page;
    }
    private void parserBestOngoing(long chatId, String firstName) throws IOException {
        Document page = getPage();
        String Ongoing = page.select("div[class=movie-item__title]").first().text()+".";
        String few = String.valueOf(Ongoing);
        sendMessage(chatId, few);

    }
    private void parserAmountOngoing(long chatId, String parserAmount) throws IOException {
        Document page = getPage();
        String What = page.select("div[class=movie-item__title]").text()+".";
        String now = String.valueOf(What);
        sendMessage(chatId,now);
    }
    private static Document Kino() throws IOException {
        String url = "https://www.forbes.ru/forbeslife/458909-klassika-miadzaki-kiberpank-i-istoriceskie-dramy-7-lucsih-polnometraznyh-anime";
        Document page = Jsoup.parse(new URL(url), 3000);
        return page;
    }
    private void parserTopMovieAnime(long chatId, String movieTop) throws IOException {
        Document page = Kino();
        String topMovieElement = page.select("h2[class=_2gzFa]").first().text()+".";
        String allTime = String.valueOf(topMovieElement);
        sendMessage(chatId, allTime);
    }
    private void Profile(long chatId, String firstname, String lastName, String userName) {
        if (firstname != null) {
            String fName = "Твое имя:" + firstname;
            sendMessage(chatId, fName);
        } else {
            String nonFName = "Имя ты не указал";
            sendMessage(chatId, nonFName);
        }
        if (lastName != null) {
            String lName = "Твоя фамилия:" + lastName;
            sendMessage(chatId, lName);
        } else {
            String nonLName = "Фамилию ты не указал";
            sendMessage(chatId, nonLName);
        }
        if (userName != null) {
            String uName = "Твой никнейм: "+ userName;
            sendMessage(chatId, uName);
        }else {
            String nonUName = "Никнейм ты не указал";
            sendMessage(chatId, nonUName);
        }
    }
    public void sendMsg(Message message, String s) throws TelegramApiException
    {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(s);
        execute(sendMessage);
    }
    // TOP_TEXT =
    //                        "1. "+ command + " \n" +
    //                                "2. нет \n" +
    //                                "3. нет \n" +
    //                                "4. нет \n" +
    //                                "5. нет \n" +
    //
    //                                "\nЧтобы изменить топ введите /settings\n";
    //                prepareAndSendMessage(chatId, TOP_TEXT);
}


