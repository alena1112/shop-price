package ru.shop.service.parser;

import ru.shop.model.Shop;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Iterator;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeliveryCostParser {
    private static final Logger log = LoggerFactory.getLogger(DeliveryCostParser.class);

    public static Function<Document, Double> parseDelivery(Shop shop) {
        log.info(String.format("Starting parse delivery price for %s shop", shop.getId()));
        switch (shop) {
            case GREEN_BIRD:
                log.info("Parsing of delivery price is not developed for Green Bird shop");
                return document -> 0.0;
            case PANDAHALL:
                return DeliveryCostParser::parsePandahallDelivery;
            case STILNAYA:
                return DeliveryCostParser::parseStilnayaDelivery;
            case LUXFURNITURA:
                return DeliveryCostParser::parseLuxfurnituraDelivery;
        }
        return null;
    }

    private static double parsePandahallDelivery(Document doc) {
        Elements tableElements = doc.select("div");
        Iterator<Element> tables = tableElements.iterator();

        while (tables.hasNext()) {
            Element table = tables.next();
            String attributeClass = table.attributes().get("class");
            if (attributeClass != null && attributeClass.equals("content_rit")) {
                Elements pTags = table.select("p");
                double delivery = parsePandahallDouble(pTags.get(1).text());
                double discount = 0;
                if (pTags.size() == 5) {
                    discount = parsePandahallDouble(pTags.get(2).text());
                }
                double result = delivery - discount;
                log.info(String.format("Delivery price for Pandahall shop is %s", result));
                return result;
            }
        }
        log.warn("Delivery price for Pandahall shop is not parsed");
        return 0;
    }

    private static double parsePandahallDouble(String number) {
        Pattern pattern = Pattern.compile("^[a-zA-Zа-яА-Я.]+([\\d.,]+)$");
        Matcher m = pattern.matcher(number);
        if (m.matches()) {
            try {
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setDecimalSeparator('.');
                symbols.setGroupingSeparator(',');
                return new DecimalFormat("###,###.##", symbols).parse(m.group(1)).doubleValue();
            } catch (ParseException e) {
                log.error("Error while parsing delivery price for pandahall shop", e);
            }
        }
        return 0;
    }

    private static double parseStilnayaDelivery(Document doc) {
        Elements tableElements = doc.select("table");
        Iterator<Element> tables = tableElements.iterator();

        while (tables.hasNext()) {
            Element table = tables.next();
            String attributeClass = table.attributes().get("class");
            if (attributeClass != null) {
                if (attributeClass.equals("user-order-items")) {
                    Elements trTags = table.select("tr");
                    for (Element trTag : trTags) {
                        Elements tdTags = trTag.select("td");
                        if (tdTags.size() == 2 && tdTags.get(0).text().equals("Доставка:")) {
                            String deliveryStr = tdTags.get(1).text();
                            Pattern pattern = Pattern.compile("^([\\d.,]+)\\s[a-zA-Zа-яА-Я.]+");
                            Matcher m = pattern.matcher(deliveryStr);
                            if (m.matches()) {
                                double result = Double.parseDouble(m.group(1).replace(",", "."));
                                log.info(String.format("Delivery price for Stilnaya shop is %s", result));
                                return result;
                            }
                        }
                    }
                }
            }
        }
        log.warn("Delivery price for Stilnaya shop is not parsed");
        return 0;
    }

    private static double parseLuxfurnituraDelivery(Document doc) {
        Elements tableElements = doc.select("table");
        Element purchasesTable = tableElements.stream()
                .filter(element -> "purchases".equals(element.attributes().get("id")))
                .findFirst()
                .orElse(null);
        if (purchasesTable != null) {
            Elements trs = purchasesTable.select("tr");
            Element tr = trs.get(trs.size() - 2);
            Elements tds = tr.select("td");
            if (tds.stream().anyMatch(element -> "name".equals(element.className()) && (element.text().contains("СДЭК") || element.text().contains("Почта")))) {
                Element priceStr = tds.stream()
                        .filter(element -> "price".equals(element.className()) && StringUtils.isNotBlank(element.text()))
                        .findFirst()
                        .orElse(null);
                if (priceStr != null) {
                    double result = Double.parseDouble(priceStr.text().replace(" руб", ""));
                    log.info(String.format("Delivery price for Luxfurnitura shop is %s", result));
                    return result;
                }
            } else {
                log.info(String.format("Delivery price for Luxfurnitura shop is %s", 0));
                return 0;
            }
        }
        log.warn("Delivery price for Luxfurnitura shop is not parsed");
        return 0;
    }
}
