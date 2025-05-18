package ru.mdm.signatures.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    DOCUMENT_NOT_FOUND("Запрашиваемый документ не найден"),
    FILE_IS_ABSENT("У документа отсутствует файл"),
    USER_IS_NOT_SIGNER("Пользователь с таким логином не подписывал указанный документ")
    ;

    private final String text;

    public String buildErrorText(Object... params) {
        return String.format(this.getText(), params);
    }
}
