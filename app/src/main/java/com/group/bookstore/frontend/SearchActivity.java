package com.group.bookstore.frontend;

/**
 * @author Yusen Nian
 */
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.group.bookstore.R;
import com.group.bookstore.backend.Book;
import com.group.bookstore.backend.parser.Exp;
import com.group.bookstore.backend.parser.Parser;
import com.group.bookstore.backend.tokenizer.Tokenizer;
import com.group.bookstore.database.DataBase;
import com.group.bookstore.frontend.adapter.BookAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ListView resultsListView;
    private Spinner spinnerPrice, spinnerDate, spinnerLanguage;
    private DataBase dataBase;
    private BookAdapter bookAdapter;

    private HashSet<String> addedFields = new HashSet<>();
    private ArrayList<Book> initialResultList = new ArrayList<>();

    private ArrayList<Book> currentDisplayList = new ArrayList<>();

    private boolean sortAscending = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dataBase = DataBase.getDataBase(this);

        // Initialise components to layout
        Button searchButton = findViewById(R.id.search_button);
        Button addFilterButton = findViewById(R.id.apply_search);
        Button showFilterResultButton = findViewById(R.id.show_filter);
        Button sortButton = findViewById(R.id.sort_button);
        sortButton.setOnClickListener(v -> sortOrder());

        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        spinnerDate = findViewById(R.id.spinnerDate);
        spinnerPrice = findViewById(R.id.spinnerPrice);
        resultsListView = findViewById(R.id.search_results);

        // Populate spinners with data
        populateSpinner(spinnerLanguage, dataBase.getLanguageList());
        populateSpinner(spinnerDate, dataBase.getDateList());
        populateSpinner(spinnerPrice, dataBase.getPriceList());

        // Set search button
        searchButton.setOnClickListener(v -> executeSearch());

        //Initialize the adapter
        bookAdapter = new BookAdapter(this,initialResultList);
        resultsListView.setAdapter(bookAdapter);

        // Advanced search
        initializeAdvancedSearch();

        // Link Add Filter Button
        addFilterButton.setOnClickListener(v -> showFieldSelection());

        // Show Filter Result
        showFilterResultButton.setOnClickListener(v -> applyFilter());
    }

    private void populateSpinner(Spinner spinner, ArrayList<?> originalList) {
        ArrayList<String> listWithPrompt = new ArrayList<>();

        // Convert items from the originalList into Strings
        for (Object item : originalList) {
            listWithPrompt.add(String.valueOf(item));
        }

        // Add the default prompt based on the spinner
        if (spinner == spinnerLanguage) {
            listWithPrompt.add(0, getString(R.string.language_prompt));
        } else if (spinner == spinnerDate) {
            listWithPrompt.add(0, getString(R.string.date_prompt));
        } else if (spinner == spinnerPrice) {
            listWithPrompt.add(0, getString(R.string.price_prompt));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,listWithPrompt);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    private void executeSearch() {
        // Create search criteria based on user input
        HashMap<String, String> searchCriteria = new HashMap<>();
        EditText searchEditText = findViewById(R.id.search_edittext);
        String query = searchEditText.getText().toString();

        if (!query.isEmpty()) {
            Tokenizer tokenizer = new Tokenizer(query);
            Parser parser = new Parser(tokenizer);
            Exp expression = parser.parseExp();
            searchCriteria.putAll(translateExp(expression));
            System.out.println("Parsed Expression: " + expression.show());
            System.out.println("Search Criteria: " + searchCriteria.toString());

        }

        // Check if spinners have a value selected other than the prompt
        if (!spinnerLanguage.getSelectedItem().toString().equals(getString(R.string.language_prompt))) {
            searchCriteria.put("language", spinnerLanguage.getSelectedItem().toString());
        }

        if (!spinnerDate.getSelectedItem().toString().equals(getString(R.string.date_prompt))) {
            searchCriteria.put("date", spinnerDate.getSelectedItem().toString());
        }

        if (!spinnerPrice.getSelectedItem().toString().equals(getString(R.string.price_prompt))) {
            searchCriteria.put("price", spinnerPrice.getSelectedItem().toString());
        }

        // Get search results from the database
        initialResultList = dataBase.search(searchCriteria);
        currentDisplayList = new ArrayList<>(initialResultList);

        if (initialResultList == null || initialResultList.isEmpty()) {
            initialResultList = new ArrayList<>();
            Toast.makeText(this, "No books matched your search.", Toast.LENGTH_SHORT).show();
        }

        // Display results
        bookAdapter.updateData(initialResultList);
        bookAdapter.notifyDataSetChanged();
        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book selectedBook = initialResultList.get(position);
                Intent intent = new Intent(SearchActivity.this, BookActivity.class);
                intent.putExtra("id", selectedBook.getId());
                intent.putExtra("date", selectedBook.getDate());
                intent.putExtra("title", selectedBook.getTitle());
                intent.putExtra("author", selectedBook.getAuthor());
                intent.putExtra("language", selectedBook.getLanguage());
                intent.putExtra("bookshelves", selectedBook.getBookshelves());
                intent.putExtra("price", selectedBook.getPrice());
                startActivity(intent);

            }
        });

    }

    private HashMap<String, String> translateExp(Exp expression) {
        HashMap<String, String> criteria = new HashMap<>();
        String parsedResult = expression.show();
        Pattern titlePattern = Pattern.compile("title: ([^;AND]+)");
        Pattern authorPattern = Pattern.compile("author: ([^;AND]+)");
        Pattern pricePattern = Pattern.compile("price: ([^;]+)");
        Pattern languagePattern = Pattern.compile("language: ([^;]+)");
        Pattern datePattern = Pattern.compile("date: ([^;]+)");

        // Matchers for each pattern
        Matcher titleMatcher = titlePattern.matcher(parsedResult);
        Matcher authorMatcher = authorPattern.matcher(parsedResult);
        Matcher priceMatcher = pricePattern.matcher(parsedResult);
        Matcher languageMatcher = languagePattern.matcher(parsedResult);
        Matcher dateMatcher = datePattern.matcher(parsedResult);

        if (titleMatcher.find()) {
            criteria.put("title", titleMatcher.group(1).trim());
        }
        if (authorMatcher.find()) {
            criteria.put("author", authorMatcher.group(1).trim());
        }
        if (priceMatcher.find()) {
            criteria.put("price", priceMatcher.group(1).trim());
        }
        if (languageMatcher.find()) {
            criteria.put("language", languageMatcher.group(1).trim());
        }
        if (dateMatcher.find()) {
            criteria.put("date", dateMatcher.group(1).trim());
        }

        return criteria;
    }

    private void initializeAdvancedSearch() {
        Button advancedButton = findViewById(R.id.advanceSearch);
        LinearLayout advancedSearchLayout = findViewById(R.id.advanced_search_layout);
        Button applyButton = findViewById(R.id.apply_search);
        Button showFilterResultButton = findViewById(R.id.show_filter);

        Spinner searchOptionSpinner = findViewById(R.id.search_option_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.advanced_search_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchOptionSpinner.setAdapter(adapter);

        advancedButton.setOnClickListener(v -> {
            if (advancedSearchLayout.getVisibility() == View.GONE) {
                advancedSearchLayout.setVisibility(View.VISIBLE);
                applyButton.setVisibility(View.VISIBLE);
                showFilterResultButton.setVisibility(View.VISIBLE);
                advancedButton.setText("Hide Filter");
            } else {
                advancedSearchLayout.setVisibility(View.GONE);
                applyButton.setVisibility(View.GONE);
                showFilterResultButton.setVisibility(View.GONE);
                advancedButton.setText("Filter from Result");
            }
        });

        searchOptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                addField(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showFieldSelection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an attribute for filter");

        String[] fields = {"Author", "Title", "Language", "Year", "Price"};

        builder.setItems(fields, (dialog, which) -> {
            switch (which) {
                case 0:
                    addField("Author");
                    break;
                case 1:
                    addField("Title");
                    break;
                case 2:
                    addField("Language");
                    break;
                case 3:
                    addField("Year");
                    break;
                case 4:
                    addField("Price");
                    break;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void applyFilter(){
        LinearLayout container = findViewById(R.id.dynamic_fields_container);
        HashMap<String, String> filterCriteria = new HashMap<>();

        // Gather filter criteria specified by the user
        for (int i = 0; i < container.getChildCount(); i++) {
            View view = container.getChildAt(i);
            if (view instanceof EditText) {
                EditText editText = (EditText) view;
                String tag = (String) editText.getTag();
                if (tag != null && !editText.getText().toString().isEmpty()) {
                    filterCriteria.put(tag, editText.getText().toString());
                }
            } else if (view instanceof Spinner) {
                Spinner spinner = (Spinner) view;
                String tag = (String) spinner.getTag();
                if (tag != null && spinner.getSelectedItem() != null && !spinner.getSelectedItem().toString().isEmpty()) {
                    filterCriteria.put(tag, spinner.getSelectedItem().toString());
                }
            }
        }

        // If no criteria is entered, reset the list to the initial state
        if (filterCriteria.isEmpty()) {
            bookAdapter.updateData(initialResultList);
            bookAdapter.notifyDataSetChanged();
            return;
        }

        // Use database search for filter
        ArrayList<Book> filteredList = dataBase.search(filterCriteria);

        if (filteredList == null) {
            filteredList = new ArrayList<>();
        }

        // Filter the results based on the initial list
        ArrayList<Book> finalFilteredList = new ArrayList<>(initialResultList);
        finalFilteredList.retainAll(filteredList);

        // Display the filtered list to the user
        bookAdapter.updateData(finalFilteredList);
        bookAdapter.notifyDataSetChanged();
        currentDisplayList = new ArrayList<>(finalFilteredList);
    }

    private void sortOrder() {
        sortAscending = !sortAscending;

        if (currentDisplayList != null && !currentDisplayList.isEmpty()) {

            if (sortAscending) {
                currentDisplayList.sort(Comparator.comparingDouble(Book::getPrice));
            } else {
                currentDisplayList.sort((b1, b2) -> Double.compare(b2.getPrice(), b1.getPrice()));
            }


            // Refresh the list view
            bookAdapter.updateData(currentDisplayList);
            bookAdapter.notifyDataSetChanged();
        }
    }
    private void addField(String fieldType) {
        LinearLayout container = findViewById(R.id.dynamic_fields_container);

        switch (fieldType) {
            case "Author":
                if (!addedFields.contains("author")) {
                    EditText authorEditText = new EditText(this);
                    authorEditText.setHint(R.string.author_prompt);
                    container.addView(authorEditText);
                    addedFields.add("author");
                    authorEditText.setTag("author");
                }
                break;
            case "Title":
                if (!addedFields.contains("title")) {
                    EditText titleEditText = new EditText(this);
                    titleEditText.setHint(R.string.title_prompt);
                    container.addView(titleEditText);
                    addedFields.add("title");
                    titleEditText.setTag("title");
                }
                break;
            case "Language":
                if (!addedFields.contains("language")) {
                    Spinner languageSpinner = new Spinner(this);
                    ArrayList<String> options = new ArrayList<>();
                    options.add(getString(R.string.language_prompt));
                    options.addAll(dataBase.getLanguageList());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
                    languageSpinner.setAdapter(adapter);
                    container.addView(languageSpinner);
                    addedFields.add("language");
                    languageSpinner.setTag("language");
                }
                break;
            case "Year":
                if (!addedFields.contains("date")) {
                    Spinner yearSpinner = new Spinner(this);
                    ArrayList<String> options = new ArrayList<>();
                    options.add(getString(R.string.date_prompt));
                    for (Integer date : dataBase.getDateList()) {
                        options.add(String.valueOf(date));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
                    yearSpinner.setAdapter(adapter);
                    container.addView(yearSpinner);
                    addedFields.add("date");
                    yearSpinner.setTag("date");
                }
                break;
            case "Price":
                if (!addedFields.contains("price")) {
                    Spinner priceSpinner = new Spinner(this);
                    ArrayList<String> options = new ArrayList<>();
                    options.add(getString(R.string.price_prompt));
                    for (Integer price : dataBase.getPriceList()) {
                        options.add(String.valueOf(price));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
                    priceSpinner.setAdapter(adapter);
                    container.addView(priceSpinner);
                    addedFields.add("price");
                    priceSpinner.setTag("price");
                }
                break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Please select a item", Toast.LENGTH_SHORT).show();
    }
}
