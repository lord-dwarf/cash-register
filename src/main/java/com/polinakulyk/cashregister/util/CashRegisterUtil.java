package com.polinakulyk.cashregister.util;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

/**
 * Application wide static utility class.
 */
public class CashRegisterUtil {

    private CashRegisterUtil() {
        throw new UnsupportedOperationException("Cannot instantiate");
    }

    /**
     * A shorthand for constructing developer friendly exception messages.
     * <p>
     * Quotes are single quote for JSON-friendliness.
     *
     * @param message
     * @param value
     * @return
     */
    public static String quote(String message, Object value) {
        return String.format("%s: '%s'", message, value);
    }

    /**
     * An overloaded version of {@link CashRegisterUtil#quote(String, Object)} with 2 object params.
     *
     * @param message
     * @param value1
     * @param value2
     * @return
     */
    public static String quote(String message, Object value1, Object value2) {
        return String.format("%s: '%s', '%s'", message, value1, value2);
    }

    /**
     * Current server-side time in UTC.
     *
     * @return
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(Clock.systemUTC());
    }

    /**
     * Obtains {@link Date} from {@link LocalDateTime} in UTC.
     *
     * @param localDateTime
     * @return
     */
    public static Date from(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

    /**
     * Obtains {@link LocalDateTime} from {@link Date} in UTC.
     *
     * @param date
     * @return
     */
    public static LocalDateTime from(Date date) {
        return date.toInstant().atZone(ZoneId.of("Z")).toLocalDateTime();
    }

    /**
     * Generates UUID v4 using {@link java.security.SecureRandom}.
     *
     * @return
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
