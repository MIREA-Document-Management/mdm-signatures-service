package ru.mdm.signatures.service.event;

import ru.mdm.kafka.model.Event;
import ru.mdm.signatures.model.SignEventModel;

/**
 * Событие успешного подписания документа.
 */
public class SignDocumentSuccess extends Event<SignEventModel> {

    public static final String EVENT_TYPE = "mdm-signatures-service.SignDocument.Success";


    public SignDocumentSuccess(SignEventModel data) {
        super(EVENT_TYPE, data);
    }

    public static SignDocumentSuccess of(SignEventModel data) {
        return new SignDocumentSuccess(data);
    }
}
