package com.jworks.qup.service.utils;

import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.qup.service.models.NotificationTemplateDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.WordUtils;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.handlebars.Helper;
import org.trimou.handlebars.SimpleHelpers;
import org.trimou.handlebars.i18n.DateTimeFormatHelper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TemplateProcessor {
    private static final DecimalFormat df = new DecimalFormat("#,###.00");

    public static String prepareTemplateContent(String template, NotificationTemplateDto.MetaData metadata) throws SystemServiceException {

        try {
            MustacheEngine engine = MustacheEngineBuilder
                    .newBuilder()
                    .registerHelpers(preparedHelpers())
                    .build();
            Mustache mustache = engine.compileMustache("MSG", template);

            return mustache.render(metadata);
        } catch (Exception ex) {
            log.debug("Unable to process template. Reason: {}", ex.getMessage());
            log.error(ex.toString());

            throw new SystemServiceException("Problem formatting message. Check template");
        }
    }

    private static Map<String, Helper> preparedHelpers() {
        Map<String, Helper> helpers = new ConcurrentHashMap<>();
        helpers.put("formatTime", new DateTimeFormatHelper());
        helpers.put("capitalize", SimpleHelpers.execute(
                (o, c) -> {
                    Object target = o.getParameters().get(0);
                    if (target == null) {
                        o.append("");
                    } else {
                        o.append(WordUtils.capitalizeFully(target.toString()));
                    }
                })
        );
        helpers.put("toLowerCase", SimpleHelpers.execute(
                (o, c) -> {
                    Object target = o.getParameters().get(0);
                    if (target == null) {
                        o.append("");
                    } else {
                        o.append(target.toString().toLowerCase());
                    }
                })
        );
        helpers.put("formatAmount", SimpleHelpers.execute(
                (o, c) -> {
                    Object target = o.getParameters().get(0);
                    if (target == null) {
                        o.append("");
                    } else {
                        if (target instanceof BigDecimal) {
                            o.append(df.format(target));
                        } else {
                            // Not a BigDecimal, append as is
                            o.append(target.toString());
                        }
                    }
                })
        );
        helpers.put("trim", SimpleHelpers.execute(
                (o, c) -> {
                    Object targetObj = o.getParameters().get(0);
                    if (targetObj == null) {
                        o.append("");
                    } else {
                        int length = Integer.parseInt(o.getParameters().get(1).toString());
                        String target = targetObj.toString();
                        if (target.length() > length) {
                            o.append(target.substring(0, length));
                        } else {
                            o.append(target);
                        }
                    }
                })
        );

        return helpers;
    }
}
