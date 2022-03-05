package com.telegram.reporting.service.impl;

import com.telegram.reporting.bot.ReportingTelegramBot;
import com.telegram.reporting.command.CommandContainer;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Implementation of {@link SendBotMessageService} interface.
 */
@Service
public class SendBotMessageServiceImpl implements SendBotMessageService {

    private final ReportingTelegramBot reportingTelegramBot;
    private final CommandContainer commandContainer;

    public SendBotMessageServiceImpl(TelegramUserService telegramUserService, ReportingTelegramBot reportingTelegramBot) {
        this.reportingTelegramBot = reportingTelegramBot;
        this.commandContainer = new CommandContainer(this, telegramUserService);
    }

    @SneakyThrows
    public void sendMessageWithKeys(SendMessage message, ReplyKeyboardMarkup keyboardMarkup) {
        Objects.requireNonNull(keyboardMarkup, "Keyboard markup is require!");
        message.setReplyMarkup(keyboardMarkup);
        reportingTelegramBot.execute(message);
    }

    @Override
    @SneakyThrows
    public void sendMessage(Long chatId, String message) {
        sendMessage(String.valueOf(chatId), message);
    }

    @Override
    @SneakyThrows
    public void sendMessage(String chatId, String message) {
        if (isBlank(message)) return;

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableHtml(true);
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));

        reportingTelegramBot.execute(sendMessage);
    }

    @Override
    public void sendMessage(String chatId, List<String> messages) {
        if (isEmpty(messages)) return;
        messages.forEach(m -> sendMessage(chatId, m));
    }

    @Override
    public void sendMessage(Long chatId, List<String> messages) {
        if (isEmpty(messages)) return;
        sendMessage(String.valueOf(chatId), messages);
    }

    @Override
    public void sendCommand(String commandIdentifier, String username, Update update) {
        commandContainer.findCommand(commandIdentifier, username).execute(update);
    }
}
