package ru.mdm.signatures.service.event;

import ru.mdm.kafka.model.Event;
import ru.mdm.signatures.model.SignEventModel;

public class DocumentVerifySuccess extends Event<SignEventModel> {

    public static final String EVENT_TYPE = "mdm-signatures-service.DocumentVerify.Success";


    public DocumentVerifySuccess(SignEventModel data) {
        super(EVENT_TYPE, data);
    }

    public static DocumentVerifySuccess of(SignEventModel data) {
        return new DocumentVerifySuccess(data);
    }
}
