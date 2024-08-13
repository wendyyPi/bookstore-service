package com.group.bookstore;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

import com.group.bookstore.database.DataBase;

import java.util.HashMap;
import java.util.Map;

/**
 * This test is to test database, search and B+tree
 * @author Fangzhou Liu
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    @Parameterized.Parameter
    Context context;
    @Parameterized.Parameter
    DataBase dataBase;

    @Before
    public void init() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dataBase = DataBase.getDataBase(context);
    }

    @Test
    public void NotNullTest() {
        assertNotNull(dataBase);
        assertNotNull(dataBase.getDateList());
        assertNotNull(dataBase.getLanguageList());
        assertNotNull(dataBase.getPriceList());
        assertNotEquals(0, dataBase.getAllDataNum());
    }

    @Test
    public void LoadInDataSizeTest() {
        assertEquals(2706, dataBase.getAllDataNum());
    }

    @Test
    public void SearchNullTest() {
        assertNull(dataBase.search(null));
    }

    @Test
    public void SearchEmptyTest() {
        assertEquals(dataBase.getAllDataNum(), dataBase.search(new HashMap<String, String>()).size());
    }

    @Test
    public void SearchAuthorTest() {
        HashMap<String, String> map = new HashMap<>();
        map.put("author", "Lincoln");
        assertTrue(dataBase.search(map).get(0).getAuthor().contains("Lincoln"));
        map = new HashMap<>();
        map.put("author", "Carroll");
        assertTrue(dataBase.search(map).get(0).getAuthor().contains("Carroll"));
    }

    @Test
    public void FuzzySearchTest() {
        HashMap<String, String> map = new HashMap<>();
        map.put("author", "lincol");
        assertTrue(dataBase.search(map).get(0).getAuthor().contains("Lincoln"));
    }

    @Test
    public void LanguageSearchTest() {
        HashMap<String, String> map = new HashMap<>();
        map.put("language", "es");
        assertEquals("es", dataBase.search(map).get(0).getLanguage());
        map = new HashMap<>();
        map.put("language", "fi");
        assertEquals("fi", dataBase.search(map).get(0).getLanguage());
        ;
    }

    @Test
    public void DateSearchTest() {
        assertTrue(dataBase.search(new HashMap<String, String>() {{
            put("date", "1970");
        }}).get(0).getDate().contains("197"));
        assertTrue(dataBase.search(new HashMap<String, String>() {{
            put("date", "2000");
        }}).get(0).getDate().contains("200"));
        assertTrue(dataBase.search(new HashMap<String, String>() {{
            put("date", "2010");
        }}).get(1).getDate().contains("201"));
        assertTrue(dataBase.search(new HashMap<String, String>() {{
            put("date", "1990");
        }}).get(5).getDate().contains("199"));
    }

    @Test
    public void PriceSearchTest() {
        HashMap<String, String> map = new HashMap<>();
        map.put("price", "30");
        int price = dataBase.search(map).get(0).getPrice();
        assertTrue(price >= 30 && price < 40);
        map = new HashMap<>();
        map.put("price", "80");
        price = dataBase.search(map).get(2).getPrice();
        assertTrue(price >= 80 && price < 90);
    }

    @Test
    public void MultipleSearchTest(){
        HashMap<String, String> map = new HashMap<>();
        map.put("author", "lincoln");
        map.put("title", "first");
        map.put("date","1970");
        map.put("price", "80");
        map.put("language", "en");
        assertNotNull(dataBase.search(map));
    }
}
