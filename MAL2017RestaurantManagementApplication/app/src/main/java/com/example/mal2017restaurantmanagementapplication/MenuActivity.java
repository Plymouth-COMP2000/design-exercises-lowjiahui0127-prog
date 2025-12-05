package com.example.mal2017restaurantmanagementapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    RecyclerView rvMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        rvMenu = findViewById(R.id.rv_menu);
        rvMenu.setLayoutManager(new LinearLayoutManager(this));

        // Load sample data
        List<MenuItemModel> items = exampleMenuList();

        // Set adapter
        rvMenu.setAdapter(new MenuAdapter(items));
    }

    private List<MenuItemModel> exampleMenuList() {

        List<MenuItemModel> l = new ArrayList<>();

        l.add(new MenuItemModel(
                R.drawable.pizza1,
                "Main course",
                "Hawaiian Thin Crust",
                "10 Thin crust pizza topped with mozzarella cheese, red sauce, chicken toast slices, pineapple, yellow onion.",
                "RM18.99"
        ));

        l.add(new MenuItemModel(
                R.drawable.pizza2,
                "Main course",
                "Pepperoni Thin Crust",
                "10 Thin crust pizza topped with mozzarella cheese, red sauce, beef pepperoni.",
                "RM18.99"
        ));

        l.add(new MenuItemModel(
                R.drawable.pizza3,
                "Main course",
                "Mushroom Thin Crust",
                "Thin crust pizza topped with mozzarella cheese, red sauce, sauté mushroom.",
                "RM18.99"
        ));

        l.add(new MenuItemModel(
                R.drawable.pizza4,
                "Main course",
                "Shrimp Thin Crust",
                "Thin crust pizza topped with mozzarella cheese, grilled shrimp & cherry tomatoes.",
                "RM19.99"
        ));

        l.add(new MenuItemModel(
                R.drawable.salad,
                "Appetizer",
                "Caesar Salad",
                "Crispy romaine lettuce with parmesan, croutons and caesar dressing.",
                "RM8.99"
        ));

        l.add(new MenuItemModel(
                R.drawable.lava_cake,
                "Dessert",
                "Chocolate Lava Cake",
                "Warm chocolate cake with molten center.",
                "RM12.99"
        ));

        return l;
    }
}
