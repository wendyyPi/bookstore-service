package com.group.bookstore.database;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.group.bookstore.R;
import com.group.bookstore.backend.Book;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * This class will load all book data and create inverted index in B+ tree and also provide searching
 * @author Fanghzou Liu
 */
public class DataBase {
    //private static final BPlusTree<Integer, Book> dataBase = new BPlusTree<>(100);
    private static final BPlusTree<String, HashSet<Integer>> titleIndex = new BPlusTree<>(10000);
    private static final BPlusTree<String, HashSet<Integer>> authorIndex = new BPlusTree<>(10000);
    private static final HashMap<String, HashSet<Integer>> languageIndex = new HashMap<>();
    private static DataBase base;

    private static final HashMap<Integer, Book> bookHashMap = new HashMap<>();
    private static final HashMap<Integer, HashSet<Integer>> dateIndex = new HashMap<>();
    private static final HashMap<Integer, HashSet<Integer>> priceIndex = new HashMap<>();

    private DataBase (Context context) {
        Book[] list = null;
        BufferedReader reader = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.all);
            reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        } catch (Exception e) {
            Log.d("File", "Read in file failed");
        }
        try {
            Gson gson = new GsonBuilder().create();
            assert reader != null;
            list = gson.fromJson(reader, Book[].class);
        } catch (Exception e) {
            Log.d("Json", "Parse failed, no book data");
        }
        if (list != null) {
            for (Book book : list) {
                bookHashMap.put(book.getId(), book);
                //dataBase.insertOrUpdate(book.getId(), book);

                initIndex(book);
            }
        }
    }

    private static HashSet<Integer> searchTitle(String title) {
        String[] words = title.trim().toLowerCase().split(" ");
        HashSet<Integer> set = null;
        for (String word : words) {
            HashSet<Integer> currentSet = titleIndex.fuzzyGet(word);
            if (currentSet != null) {
                if (set == null) {
                    set = new HashSet<>(currentSet);
                } else {
                    set.retainAll(currentSet);
                }
            }
        }
        return set;
    }

    private static HashSet<Integer> searchAuthor(String author) {
        String[] words = author.toLowerCase().split(" ");
        HashSet<Integer> set = null;
        for (String word : words) {
            HashSet<Integer> currentSet = authorIndex.fuzzyGet(word);
            if (currentSet != null) {
                if (set == null) {
                    set = new HashSet<>(currentSet);
                } else {
                    set.retainAll(currentSet);
                }
            }
        }
        return set;
    }

    public static DataBase getDataBase(Context context) {
        if (base == null) {
            base = new DataBase(context);
        }
        return base;
    }

    /**
     * Reads in a hash map to do specific search
     *
     * @param info: a hash map. Accessible keys: "author", "title", "language", "date", "price".
     * @return result: array list of books of the search result
     */
    public ArrayList<Book> search(HashMap<String, String> info) {
        if (info == null) {
            return null;
        }

        Set<Integer> ids = new HashSet<>(bookHashMap.keySet());

        // 定义一个筛选函数，用于根据条件筛选书籍
        Set<Integer> finalIds = new HashSet<>(ids);
        Function<String, Set<Integer>> filterFunction = (key) -> {
            if (info.get(key) != null) {
                Set<Integer> filteredIds = search(key, info.get(key));
                if (filteredIds == null) {
                    return null; // 如果搜索结果为空，则直接返回null
                }
                finalIds.retainAll(filteredIds);
                return finalIds;
            }
            return finalIds;
        };

        // 使用filterFunction筛选书籍
        ids = filterFunction.apply("title");
        ids = filterFunction.apply("author");
        ids = filterFunction.apply("language");
        ids = filterFunction.apply("date");
        ids = filterFunction.apply("price");

        if (ids == null || ids.isEmpty()) {
            return null;
        }

        ArrayList<Book> result = new ArrayList<>();
        for (int i : ids) {
            result.add(bookHashMap.get(i));
        }

        Collections.sort(result, new TitleComparator());
        return result;
    }


    private static Set<Integer> search(String key, String s) {
        if ("title".equals(key)) {
            return searchTitle(s);
        } else if ("author".equals(key)) {
            return searchAuthor(s);
        } else if ("language".equals(key)) {
            return languageIndex.get(s);
        } else if ("date".equals(key)) {
            return dateIndex.get(Integer.parseInt(s));
        } else if ("price".equals(key)) {
            return priceIndex.get(Integer.parseInt(s));
        }
        return null; // 如果未识别的属性名，返回null或者根据需求返回合适的默认值
    }

    private static void initIndex(Book book) {
        String[] titleWords = book.getTitle().toLowerCase().split(" ");
        String[] authorWords = book.getAuthor().toLowerCase().split(" ");
        Integer id = book.getId();
        String language = book.getLanguage();
        for (String word : titleWords) {
            if (titleIndex.get(word) == null)
                titleIndex.insertOrUpdate(word, new HashSet<>());
            titleIndex.get(word).add(id);
        }
        for (String word : authorWords) {
            if (authorIndex.get(word) == null)
                authorIndex.insertOrUpdate(word, new HashSet<>());
            authorIndex.get(word).add(id);
        }
        languageIndex.computeIfAbsent(language, k -> new HashSet<>());
        languageIndex.get(language).add(id);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        try {
            LocalDate date = LocalDate.parse(book.getDate(), formatter);
            int year = date.getYear() / 10 * 10;
            if (dateIndex.get(year) == null)
                dateIndex.put(year, new HashSet<>());
            dateIndex.get(year).add(id);
        } catch (Exception e) {
            System.out.println("Parse failed: " + book.getDate());
        }

        int price = book.getPrice() / 10 * 10;
        if (priceIndex.get(price) == null)
            priceIndex.put(price, new HashSet<>());
        priceIndex.get(price).add(id);

    }

    public ArrayList<String> getLanguageList() {
        ArrayList<String> languagelist = new ArrayList<>(languageIndex.keySet());
        Collections.sort(languagelist);
        return languagelist;
    }

    public ArrayList<Integer> getDateList() {
        ArrayList<Integer> datelist = new ArrayList<>(dateIndex.keySet());
        Collections.sort(datelist);
        return datelist;
    }

    public ArrayList<Integer> getPriceList() {
        ArrayList<Integer> pricelist = new ArrayList<>(priceIndex.keySet());
        Collections.sort(pricelist);
        return pricelist;
    }


    private static class TitleComparator implements Comparator<Book> {
        @Override
        public int compare(Book book1, Book book2) {
            return Integer.compare(book1.getTitle().length(), book2.getTitle().length());
        }
    }

    public int getAllDataNum(){
        return bookHashMap.size();
    }

}
