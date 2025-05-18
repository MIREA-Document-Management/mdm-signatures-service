package ru.mdm.signatures.service.event;

import ru.mdm.kafka.model.Event;
import ru.mdm.signatures.model.SignEventModel;

public class DocumentVerifyFailed extends Event<SignEventModel> {

    public static final String EVENT_TYPE = "mdm-signatures-service.DocumentVerify.Failed";


    public DocumentVerifyFailed(SignEventModel data) {
        super(EVENT_TYPE, data);
    }

    public static DocumentVerifyFailed of(SignEventModel data) {
        return new DocumentVerifyFailed(data);
    }
}
