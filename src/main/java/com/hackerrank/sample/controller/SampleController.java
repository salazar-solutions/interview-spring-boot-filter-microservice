package com.hackerrank.sample.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import com.hackerrank.sample.repository.BookRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.hackerrank.sample.dto.FilteredProducts;
import com.hackerrank.sample.dto.SortedProducts;

import javax.annotation.PostConstruct;

@RestController
public class SampleController {

    private final BookRepository bookRepository;

    @Autowired
    public SampleController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @CrossOrigin
    @GetMapping("/filter/price/{initial_price}/{final_price}")
    private ResponseEntity<ArrayList<FilteredProducts>> filtered_books(@PathVariable("initial_price") int init_price, @PathVariable("final_price") int final_price) {

        try {


            ArrayList<FilteredProducts> books = bookRepository
                    .getRange(init_price, final_price)
                    .stream()
                    .map(r -> new FilteredProducts(r.getBarcode()))
                    .collect(Collectors.toCollection(ArrayList::new));

            if (books.isEmpty())
                return new ResponseEntity<ArrayList<FilteredProducts>>(books, HttpStatus.BAD_REQUEST);

            return new ResponseEntity<ArrayList<FilteredProducts>>(books, HttpStatus.OK);


        } catch (Exception E) {
            System.out.println("Error encountered : " + E.getMessage());
            return new ResponseEntity<ArrayList<FilteredProducts>>(HttpStatus.NOT_FOUND);
        }

    }


    @CrossOrigin
    @GetMapping("/sort/price")
    private ResponseEntity<SortedProducts[]> sorted_books() {

        try {
            SortedProducts[] ans = bookRepository
                    .getSortByPrice()
                    .stream()
                    .map(r -> new SortedProducts(r.getBarcode()))
                    .toArray(SortedProducts[]::new);
            return new ResponseEntity<SortedProducts[]>(ans, HttpStatus.OK);

        } catch (Exception E) {
            System.out.println("Error encountered : " + E.getMessage());
            return new ResponseEntity<SortedProducts[]>(HttpStatus.NOT_FOUND);
        }

    }


}
