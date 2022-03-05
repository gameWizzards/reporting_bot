package com.telegram.reporting.dialogs.create_report.action;

import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.messages.Message;
import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Map;

@Slf4j
@Component
public class RequestConfirmationReport implements Action<CreateReportState, MessageEvent> {
    private final SendBotMessageService sendBotMessageService;

    public RequestConfirmationReport(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(StateContext<CreateReportState, MessageEvent> context) {
        String message = """
                Вы хотите отправить отчет за - %s.
                 Категория рабочего времени - "%s".
                 Затраченное время - %s ч.
                 Примечание - "%s."
                  %s""";

        Map<Object, Object> variables = context.getExtendedState().getVariables();

        String date = (String) variables.get(ContextVariable.REPORT_DATE.name());
        String time = (String) variables.get(ContextVariable.REPORT_TIME.name());
        String note = (String) variables.get(ContextVariable.REPORT_NOTE.name());
        String category = (String) variables.get(ContextVariable.REPORT_CATEGORY_TYPE.name());

        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), String.format(message, date, category, time, note, Message.REQUEST_CONFIRMATION_REPORT.text()));

        KeyboardRow firstRow = KeyboardUtils.createRowButtons(Message.CONFIRM_CREATION_FINAL_REPORT.text(), Message.DECLINE_CREATION_FINAL_REPORT.text());

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(firstRow));
    }
}
