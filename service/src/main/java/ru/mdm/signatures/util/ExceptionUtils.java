package ru.mdm.signatures.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.mdm.signatures.exception.ResponseStatusException;
import ru.mdm.signatures.exception.ServerException;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionUtils {

    public static Function<Throwable, Throwable> extExceptionMapper(String message) {
        return throwable -> throwable instanceof ResponseStatusException ?
                throwable : new ServerException(message, throwable);
    }
}
