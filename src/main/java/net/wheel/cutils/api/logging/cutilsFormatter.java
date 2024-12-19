package net.wheel.cutils.api.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class cutilsFormatter extends Formatter {

    public String format(LogRecord record) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[" + new SimpleDateFormat("HH.mm.ss").format(new Date()) + "] ");
        sb.append("[CU]: ");
        sb.append(formatMessage(record));
        sb.append("\n");
        return sb.toString();
    }

}
