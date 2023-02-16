package com.lisbeth.lightoutgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
    ImageButton menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        this.menuButton = findViewById(R.id.menu_button);
        this.popupMenu();

    }

    public void toMainGame(View view) {

        Spinner spinner = (Spinner) findViewById(R.id.spinner_width);
        String w = spinner.getSelectedItem().toString();
        spinner = (Spinner) findViewById(R.id.spinner_height);
        String h = spinner.getSelectedItem().toString();

        Intent intent = new Intent(MainActivity.this, Game.class);

        intent.putExtra("width", Integer.parseInt(w));
        intent.putExtra("height", Integer.parseInt(h));
        startActivity(intent);
    }


    public  void popupMenu(){
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crear una instancia de PopupMenu
                Context wrapper = new ContextThemeWrapper(MainActivity.this, R.style.MyPopupMenuStyle);

                PopupMenu popupMenu = new PopupMenu(wrapper, menuButton);

                // Agregar opciones al menú
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                // Configurar la acción del menú
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.estadisticas:
                                // Implementar la acción de la opción 1
                                return true;
                            case R.id.sobre_el_juego:
                                // Implementar la acción de la opción 2
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                // Mostrar el menú desplegable
                popupMenu.show();



            }
        });

    }
}