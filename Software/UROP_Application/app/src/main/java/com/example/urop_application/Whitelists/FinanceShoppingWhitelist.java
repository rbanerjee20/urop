package com.example.urop_application.Whitelists;

import android.content.Context;

import com.example.urop_application.FileUtility;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FinanceShoppingWhitelist implements Whitelist {
    private List<List<String>> financeApps, shoppingApps;
    private static final String SHOPPING_CSV_PATH = "shopping_top_100_review_count.csv";
    private static final String FINANCE_CSV_PATH = "finance_top_100_review_count.csv";
    private Set<String> packageNamesSet = new HashSet<>();

    public FinanceShoppingWhitelist(Context context) {
        this.financeApps = FileUtility.readFromCSV(FINANCE_CSV_PATH, context);
        this.shoppingApps = FileUtility.readFromCSV(SHOPPING_CSV_PATH, context);
    }


    @Override
    public Set<String> getSet() {
        for (int i = 1; i < 101; i++) {
            packageNamesSet.add(financeApps.get(i).get(0));
            packageNamesSet.add(shoppingApps.get(i).get(0));
        }
        return packageNamesSet;
    }
}
