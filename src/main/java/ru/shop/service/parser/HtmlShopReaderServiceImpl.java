package ru.shop.service.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
import ru.shop.model.Material;
import ru.shop.model.MaterialOrder;
import ru.shop.model.Shop;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@RequiredArgsConstructor
public class HtmlShopReaderServiceImpl implements HtmlShopReaderService {
    private static final Logger log = LoggerFactory.getLogger(HtmlShopReaderServiceImpl.class);

    private final String charsetName;
    private final String tableTag;
    private final String tableAttributeName;
    private final String tableName;
    private final String lineTag;
    private final String columnTag;
    private final int columnSize;

    @Override
    public Optional<MaterialOrder> parseFile(String fileName, Shop shop) {
        log.info(String.format("Starting parse file %s", fileName));

        String result = parseDocument(fileName, shop);
        Optional<MaterialOrder> order = parse(result, StringUtils.stripFilenameExtension(fileName), shop);

        log.info(String.format("File %s was successfully parsed", fileName));
        return order;
    }

    @Override
    public Optional<MaterialOrder> parseText(String text, Shop shop) {
        String materialOrderName = shop.getId() + "_" + UUID.randomUUID();
        log.info(String.format("Starting parse text %s", materialOrderName));

        Optional<MaterialOrder> order = parse(text, materialOrderName, shop);

        log.info(String.format("Text %s was successfully parsed", materialOrderName));
        return order;
    }

    private Optional<MaterialOrder> parse(String text, String orderMaterialName, Shop shop) {
        Document doc = Jsoup.parse(text);
        Elements tableElements = doc.select(tableTag);
        Iterator<Element> tables = tableElements.iterator();

        List<Material> items = new ArrayList<>();
        MaterialOrder order = new MaterialOrder();
        order.setName(orderMaterialName);
        order.setMaterials(items);
        order.setDeliveryPrice(DeliveryCostParser.parseDelivery(shop).apply(doc));
        order.setShop(shop);
        order.setPurchaseDate(getDate(orderMaterialName));

        while (tables.hasNext()) {
            Element table = tables.next();
            String tableAttribute = table.attributes().get(tableAttributeName);
            if (tableAttribute != null) {
                if (tableAttribute.equals(tableName)) {
                    Elements trTags = table.select(lineTag);
                    for (Element trTag : trTags) {
                        Elements tdTags = new Elements();
                        for (String tag : columnTag.split(",")) {
                            tdTags.addAll(trTag.select(tag));
                        }
                        if (tdTags.size() == columnSize && !"list_title".equals(trTag.attributes().get("class"))/*special for pandahall*/) {
                            Material item = LineParser.parseLine(shop).apply(tdTags);
                            if (item != null) {
                                item.setOrder(order);
                                items.add(item);
                            }
                        }
                    }
                }
            }
        }

        return Optional.of(order);
    }

    private String parseDocument(String fileName, Shop shop) {
        StringBuilder result = new StringBuilder();
        InputStream inputStream = getClass().getResourceAsStream("/orders/" + shop.getId() + "/" + fileName);
        if (inputStream != null) {
            try (InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(streamReader)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } catch (IOException e) {
                log.error(String.format("Error while parsing file %s", fileName), e);
            }
        }
        return result.toString();
    }

    private Date getDate(String fileName) {
        Pattern pattern = Pattern.compile("^[a-zA-Z]+_([\\d.]+).html$");
        Matcher m = pattern.matcher(fileName);
        if (m.matches()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            try {
                return dateFormat.parse(m.group(1));
            } catch (ParseException e) {
                log.error(String.format("Error while parsing file date %s", fileName), e);
            }
        }
        return null;
    }
}
