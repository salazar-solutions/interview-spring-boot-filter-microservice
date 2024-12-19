package com.hackerrank.sample.repository;

import com.hackerrank.sample.repository.entities.BookEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
public class BookRepository {
    private final TreeMap<Integer, List<Integer>> indexByPrice = new TreeMap<>();;
    private final List<BookEntity> bookEntityStore = new ArrayList<>();


    @PostConstruct
    private void init() {
        final String uri = "https://jsonmock.hackerrank.com/api/inventory";
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        JSONObject root = new JSONObject(result);
        JSONArray data = root.getJSONArray("data");

        for (int i = 0; i < data.length(); i++) {
            JSONObject obj = data.getJSONObject(i);

            String barcode = obj.getString("barcode");
            String item = obj.getString("item");
            String category = obj.getString("category");
            Integer price = obj.getInt("price");
            Integer discount = obj.getInt("discount");
            boolean available = obj.getInt("available") == 1;

            BookEntity bookEntity = new BookEntity(barcode, item, category, price, discount, available);
            indexByPrice.computeIfAbsent(price, k -> new ArrayList<>()).add(i);
            bookEntityStore.add(bookEntity);
        }
    }

    public Collection<BookEntity> getRange(Integer min, Integer max) {
        List<Integer> indexList = indexByPrice.subMap(min, max + 1).values().stream().flatMap(List::stream).sorted().collect(Collectors.toList());
        return indexList.stream().map(bookEntityStore::get).collect(Collectors.toList());
    }

    public Collection<BookEntity> getSortByPrice() {
        List<Integer> indexList = indexByPrice.values().stream().flatMap(List::stream).collect(Collectors.toList());
        return indexList.stream().map(bookEntityStore::get).collect(Collectors.toList());
    }

}
