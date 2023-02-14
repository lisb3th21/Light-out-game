package com.lisbeth.lightoutgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Random;

public class Game extends AppCompatActivity {
    int screenWidth;
    int screenHeight;
    int buttonSize;
    int marginSize;
    private int height;
    private int width;
    private Button[][] buttons;
    private Drawable drawableOn;
    private Drawable drawableOff;
    private GameModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        this.setDrawableOff(getResources().getDrawable(R.drawable.light_off));
        this.setDrawableOn(getResources().getDrawable(R.drawable.light_on));

        // tomamos los valores de la pantalla inicial para crear la matriz de botones
        if (getIntent().hasExtra("width") || getIntent().hasExtra("height")) {
            this.width = getIntent().getIntExtra("width", 5);
            this.height = getIntent().getIntExtra("height", 5);
        } else {
            throw new IllegalArgumentException("Activity cannot find  extras " + "width");
        }
        this.gameLayout();
        this.inicializarTablero();
        // agregamos los listeners
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                this.changeButtonBackground(buttons[i][j]);
            }
        }

    }


    public void gameLayout(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x-100;
        screenHeight = size.y;

        // Definir el tamaño del botón y el margen
        buttonSize = (screenWidth / 5 ) - 20;
        marginSize = buttonSize / 10;
        LinearLayout root = findViewById(R.id.rootGame);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        buttons = new Button[height][width];
        for (int row = 0; row < height; row++) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            for (int col = 0; col < width; col++) {
                Button button = new Button(this);
                button.setBackground(drawableOff);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        buttonSize,
                        buttonSize);
                params.setMargins(10, 10, 10, 10);
                button.setLayoutParams(params);

                buttons[row][col] = button;
                rowLayout.addView(button);
            }
            root.addView(rowLayout);
        }
    }

    public void changeButtonBackground(final Button button) {

    int fila = this.obtenerFilaBoton(button);
    int columna = this.obtenerColumnaBoton(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarBotonesAdyacentes(fila, columna);
            }
        });

    }


    private int obtenerFilaBoton(Button boton) {
        for (int fila = 0; fila < buttons.length; fila++) {
            for (int columna = 0; columna < buttons[fila].length; columna++) {
                if (buttons[fila][columna] == boton) {
                    return fila;
                }
            }
        }
        return -1;
    }

    private int obtenerColumnaBoton(Button boton) {
        for (int fila = 0; fila < buttons.length; fila++) {
            for (int columna = 0; columna < buttons[fila].length; columna++) {
                if (buttons[fila][columna] == boton) {
                    return columna;
                }
            }
        }
        return -1;
    }



    public Button[][] getButtons() {
        return buttons;
    }

    public void setButtons(Button[][] buttons) {
        this.buttons = buttons;
    }

    public void setDrawableOn(Drawable drawableOn) {
        this.drawableOn = drawableOn;
    }

    public Drawable getDrawableOff() {
        return drawableOff;
    }

    public void setDrawableOff(Drawable drawableOff) {
        this.drawableOff = drawableOff;
    }


    private void cambiarColorBoton(Button boton) {
        if(boton.getBackground() == drawableOff){
            boton.setBackground(drawableOn);
        }else{
            boton.setBackground(drawableOff);
        }
    }


    private void actualizarBotonesAdyacentes(int fila, int columna) {
        cambiarColorBoton(buttons[fila][columna]);
        // Cambiar el color del botón de arriba
        if (fila > 0) {
            cambiarColorBoton(buttons[fila - 1][columna]);
        }
        // Cambiar el color del botón de abajo
        if (fila < buttons.length - 1) {
            cambiarColorBoton(buttons[fila + 1][columna]);
        }
        // Cambiar el color del botón de la izquierda
        if (columna > 0) {
            cambiarColorBoton(buttons[fila][columna - 1]);
        }
        // Cambiar el color del botón de la derecha
        if (columna < buttons[fila].length - 1) {
            cambiarColorBoton(buttons[fila][columna + 1]);
        }
    }


    public void inicializarTablero(){
        Random ran = new Random();

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                int probabilidad = ran.nextInt(2);
                if (probabilidad==1){
                    buttons[i][j].setBackground(drawableOn);
                }
            }
        }
    }

    public  boolean finalizado(){
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                if(buttons[i][j].getBackground()== drawableOn){
                    return  false;
                }
            }
        }
        return  true;
    }


}