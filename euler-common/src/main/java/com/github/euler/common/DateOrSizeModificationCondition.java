package com.github.euler.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.github.euler.core.AbstractBarrierCondition;
import com.github.euler.core.ProcessingContext;

public class DateOrSizeModificationCondition extends AbstractBarrierCondition {

    private final String dateFormat;
    private final SimpleDateFormat sdf;

    public DateOrSizeModificationCondition(String dateFormat, Locale dateLocale) {
        super();
        this.dateFormat = dateFormat;
        if (!dateFormat.equals("date")) {
            sdf = new SimpleDateFormat(dateFormat, dateLocale);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        } else {
            sdf = null;
        }
    }

    @Override
    protected boolean block(ProcessingContext ctx) {
        try {
            return isSizeEquals(ctx) && isLastModifiedDateEquals(ctx) && isCreatedDateEquals(ctx);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isSizeEquals(ProcessingContext ctx) {
        Long oldSize = ((Integer) ctx.context("size")).longValue();
        Long currentSize = ctx.metadata(CommonMetadata.SIZE, 0l);
        return oldSize.equals(currentSize);
    }

    private boolean isDateEquals(ProcessingContext ctx, String key) throws ParseException {
        Long oldValue = getDateAsLong(ctx, key);
        Long currentValue = ctx.metadata(key, new Date()).getTime();
        return oldValue.equals(currentValue);
    }

    protected long getDateAsLong(ProcessingContext ctx, String key) throws ParseException {
        if (dateFormat.equals("date")) {
            return ((Date) ctx.context(key)).getTime();
        } else {
            return sdf.parse((String) ctx.context(key)).getTime();
        }
    }

    private boolean isLastModifiedDateEquals(ProcessingContext ctx) throws ParseException {
        return isDateEquals(ctx, CommonMetadata.LAST_MODIFIED_DATETIME);
    }

    private boolean isCreatedDateEquals(ProcessingContext ctx) throws ParseException {
        return isDateEquals(ctx, CommonMetadata.CREATED_DATETIME);
    }

}
