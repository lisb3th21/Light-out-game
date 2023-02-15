package com.lisbeth.lightoutgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
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
    private Drawable drawableTouch;
    private int segundos = 0;
    private TextView textTime;
    private GameModel model;
    private Handler handler = new Handler();
    private Button solucionar;
    private List<Pair<Integer, Integer>> lucesAMostrar;


    private Runnable runnable = new Runnable() {
        public void run() {
            segundos++;
            textTime.setText(String.valueOf(segundos));
            handler.postDelayed(this, 1000); // Ejecutar de nuevo el Runnable en un segundo
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        this.setDrawableOff(getResources().getDrawable(R.drawable.light_off));
        this.setDrawableOn(getResources().getDrawable(R.drawable.light_on));
        this.setDrawableTouch(getResources().getDrawable(R.drawable.light_touch));
        this.textTime = findViewById(R.id.time);
        this.solucionar = findViewById(R.id.botonSolucion);
        //* tomamos los valores de la pantalla inicial para crear la matriz de botones
        if (getIntent().hasExtra("width") || getIntent().hasExtra("height")) {
            this.width = getIntent().getIntExtra("width", 5);
            this.height = getIntent().getIntExtra("height", 5);
        } else {
            throw new IllegalArgumentException("Activity cannot find  extras " + "width");
        }
        this.gameLayout();
        this.model = new GameModel(height, width);
        this.inicializarTablero();
        // agregamos los listeners
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                this.changeButtonBackground(buttons[i][j]);
            }
        }

        // * inicualizamos el contador de segundos
        handler.post(runnable);


        solucionar.setOnClickListener(new View.OnClickListener() {
            //Handler timer = new Handler();

            @Override
            public void onClick(View v) {
                    //handler.removeCallbacks(runnable);
                    mostrarLucesATocar();

            }
        });


    }

    private void mostrarLucesATocar() {
        lucesAMostrar = obtenerLucesAMostrar(); // Obtener la lista de luces a mostrar

        Handler timer = new Handler();
        for (int i = 0; i < lucesAMostrar.size(); i++) {
            Log.d("solsssss", "aquii");
            final Pair<Integer, Integer> luz = lucesAMostrar.get(i);
            timer.postDelayed(new Runnable() {
                public void run() {
                    mostrarLuz(luz.first, luz.second);

                }
            }, i * 1000); // Mostrar la luz con una pausa de 1 segundo
            //cambiarRojos();
        }
    }

    private void mostrarLuz(int fila, int columna) {
       // cambiarRojos();
        buttons[fila][columna].setText("TOUCH");
        buttons[fila][columna].setTextColor(Color.RED);
        Button boton = buttons[fila][columna];
        Log.d("sol", "aquii");
        updateAdjacentButtons(fila, columna);


    }


    private List<Pair<Integer, Integer>> obtenerLucesAMostrar() {
        List<Pair<Integer, Integer>> luces = new ArrayList<>();

        for (int fila = 0; fila < model.getSolution().length; fila++) {
            for (int columna = 0; columna < model.getSolution()[fila].length; columna++) {
                if (model.getSolution()[fila][columna]) {
                    luces.add(new Pair<>(fila, columna));
                }
            }
        }

        return luces;
    }


    public void cambiarRojos(){
        for (int i = 0; i < buttons.length; i++) {

            for (int j = 0; j < buttons[i].length; j++) {

                if (buttons[i][j].getBackground() == drawableTouch){
                    buttons[i][j].setBackground(drawableOff);

                }
            }
        }
    }

    public void changeButtonBackground(final Button button) {

        int fila = this.obtenerFilaBoton(button);
        int columna = this.obtenerColumnaBoton(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAdjacentButtons(fila, columna);
                finalizado();
            }
        });

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

    public Drawable getDrawableTouch() {
        return drawableTouch;
    }

    public void setDrawableTouch(Drawable drawableTouch) {
        this.drawableTouch = drawableTouch;
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

    public void inicializarTablero(){
        Random ran = new Random();
        for (int i = 0; i < buttons.length; i++) {

            for (int j = 0; j < buttons[i].length; j++) {
                int probabilidad = ran.nextInt(2);
                if (probabilidad==1){
                    updateAdjacentButtons(i, j);

                }
            }
        }


    }





    public void updateAdjacentButtons( int row, int col) {
        this.model.getSolution()[row][col] = !model.getSolution()[row][col];

        int numRows = buttons.length;
        int numCols = buttons[0].length;

        // Actualiza el botón al oeste
        if (col > 0) {
            Button westButton = buttons[row][col - 1];
            cambiarColorBoton(westButton);
        }

        // Actualiza el botón al este
        if (col < numCols - 1) {
            Button eastButton = buttons[row][col + 1];
            cambiarColorBoton(eastButton);
        }

        // Actualiza el botón al norte
        if (row > 0) {
            Button northButton = buttons[row - 1][col];
            cambiarColorBoton(northButton);
        }

        // Actualiza el botón al sur
        if (row < numRows - 1) {
            Button southButton = buttons[row + 1][col];
            cambiarColorBoton(southButton);
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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] patron = { 500, 500 };
            vibrator.vibrate(patron, -1);
        }

        return  true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(runnable); // Detener el contador
    }















}