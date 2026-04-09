package ru.university.textbot.quiz;

import ru.university.textbot.model.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElementDataLoader {

    private static final String DELIMITER = ";";

    public static class ElementData {
        public final Map<String, Element> bySymbol;  // HashMap<символ, элемент>
        public final List<Element>        elements;  // список для random

        ElementData(Map<String, Element> bySymbol, List<Element> elements) {
            this.bySymbol = bySymbol;
            this.elements = elements;
        }
    }
    public static ElementData loadFromResources(String resourceName) {
        InputStream is = ElementDataLoader.class
                .getClassLoader()
                .getResourceAsStream(resourceName);

        if (is == null) {
            System.err.println("⚠ Файл ресурса не найден: " + resourceName + " — используются встроенные данные");
            return buildData(getFallbackElements());
        }

        List<Element> parsed = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            int lineNum = 0;

            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(DELIMITER, -1);

                if (parts.length < 2) {
                    System.err.println("⚠ Строка " + lineNum + " пропущена (мало полей): " + line);
                    continue;
                }

                String symbol = parts[0].trim();
                String name   = parts[1].trim();

                if (symbol.isEmpty() || name.isEmpty()) {
                    System.err.println("⚠ Строка " + lineNum + " пропущена (пустые поля)");
                    continue;
                }

                parsed.add(new Element(symbol, name));
            }
        } catch (IOException e) {
            System.err.println("❌ Ошибка чтения файла: " + e.getMessage());
            return buildData(getFallbackElements());
        }

        if (parsed.isEmpty()) {
            System.err.println("⚠ Файл пустой — используются встроенные данные");
            return buildData(getFallbackElements());
        }

        System.out.println("✅ Загружено элементов: " + parsed.size());
        return buildData(parsed);
    }
    private static ElementData buildData(List<Element> elements) {
        Map<String, Element> bySymbol = new HashMap<>();
        for (Element e : elements) {
            bySymbol.put(e.getSymbol(), e);
        }
        return new ElementData(bySymbol, new ArrayList<>(elements));
    }

    private static List<Element> getFallbackElements() {

        List<Element> list = new ArrayList<>();
        list.add(new Element("H",  "Водород"));
        list.add(new Element("He", "Гелий"));
        list.add(new Element("Li", "Литий"));
        list.add(new Element("C",  "Углерод"));
        list.add(new Element("N",  "Азот"));
        list.add(new Element("O",  "Кислород"));
        list.add(new Element("Na", "Натрий"));
        list.add(new Element("Mg", "Магний"));
        list.add(new Element("Al", "Алюминий"));
        list.add(new Element("Si", "Кремний"));
        list.add(new Element("P",  "Фосфор"));
        list.add(new Element("S",  "Сера"));
        list.add(new Element("Cl", "Хлор"));
        list.add(new Element("K",  "Калий"));
        list.add(new Element("Ca", "Кальций"));
        list.add(new Element("Fe", "Железо"));
        list.add(new Element("Cu", "Медь"));
        list.add(new Element("Zn", "Цинк"));
        list.add(new Element("Ag", "Серебро"));
        list.add(new Element("Au", "Золото"));
        return list;
    }
}