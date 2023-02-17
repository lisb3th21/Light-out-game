package com.lisbeth.lightoutgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class Game extends AppCompatActivity {
    private Resolved resolved;
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
    private int seconds = 0;
    private TextView textTime;
    private GameModel model;
    private Handler handler = new Handler();
    private Handler timeHandler = new Handler();
    private Button solve;
    private List<Pair<Integer, Integer>> lightsToShow;
    private TimerRunnable timerRunnable;
    private GameDatabaseHelper dbHelper;
    private SQLiteDatabase db;


    // Creating a new thread that will run every 2 seconds which checks that the
    // matrix is solved.
    private Runnable finisherThread = new Runnable() {
        public void run() {
            handler.postDelayed(this, 1500);
            finished();
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
        this.solve = findViewById(R.id.botonSolucion);
        this.timerRunnable = new TimerRunnable(timeHandler, textTime, 0);

        if (getIntent().hasExtra("width") || getIntent().hasExtra("height")) {
            this.width = getIntent().getIntExtra("width", 5);
            this.height = getIntent().getIntExtra("height", 5);
        } else {
            throw new IllegalArgumentException("Activity cannot find  extras " + "width");
        }

        // Create the buttons of the game
        this.gameLayout();

        // initialize the model of the game
        this.model = new GameModel(height, width);

        // initialize the buttons of the game
        this.initializeBoard();
        // add the listeners of the buttons
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                this.changeButtonBackground(buttons[i][j]);
            }
        }

        // !initialize the timer
        timeHandler.postDelayed(timerRunnable, 1000);
        // timeHandler.post(runnable);

        // initialize the solve button
        handler.post(finisherThread);

        setResolved(Resolved.USER);

        // add listener for the solve button
        solve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableLightButtons();
                timerRunnable.stop();
                mostrarLucesATocar();
                setResolved(Resolved.PROGRAM);
            }
        });
    }

    public Resolved getResolved() {
        return resolved;
    }

    public void setResolved(Resolved resolved) {
        this.resolved = resolved;
    }

    /// !!! aqui me quede

    private void mostrarLucesATocar() {
        lightsToShow = getLightsToShow(); // Obtener la lista de luces a mostrar

        Handler timer = new Handler();
        for (int i = 0; i < lightsToShow.size(); i++) {

            final Pair<Integer, Integer> light = lightsToShow.get(i);
            timer.postDelayed(new Runnable() {
                public void run() {
                    showLight(light.first, light.second);
                }
            }, i * 1000);
        }

    }

    private void showLight(int row, int column) {
        buttons[row][column].setText("x");
        buttons[row][column].setTextColor(getResources().getColor(R.color.fifth));
        updateAdjacentButtons(row, column);
    }

    private List<Pair<Integer, Integer>> getLightsToShow() {
        List<Pair<Integer, Integer>> lights = new ArrayList<>();

        for (int row = 0; row < model.getSolution().length; row++) {
            for (int column = 0; column < model.getSolution()[row].length; column++) {
                if (model.getSolution()[row][column]) {
                    lights.add(new Pair<>(row, column));
                }
            }
        }

        return lights;
    }

    /**
     * When the button is clicked, update the adjacent buttons.
     * 
     * @param button The button that was clicked
     */
    public void changeButtonBackground(final Button button) {
        int row = this.obtenerFilaBoton(button);
        int column = this.obtenerColumnaBoton(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAdjacentButtons(row, column);
            }
        });
    }

    /**
     * Convert a value in dp to a value in pixels.
     * 
     * @param dp The value in dp that you want to convert to pixels.
     * @return The pixels of the screen.
     */
    public int convertDpToPx(float dp) {
        Context context = Game.this;
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
        return (int) pixels;
    }

    public void gameLayout() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x - 100;
        screenHeight = size.y - convertDpToPx(390);
        if (screenWidth > screenHeight) {
            buttonSize = (screenHeight / 5) - 20;
        } else {
            buttonSize = (screenWidth / 5) - 20;
        }
        // Definir el tamaño del botón y el margen
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
        for (int row = 0; row < buttons.length; row++) {
            for (int column = 0; column < buttons[row].length; column++) {
                if (buttons[row][column] == boton) {
                    return row;
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
        if (boton.getBackground() == drawableOff) {
            boton.setBackground(drawableOn);
        } else {
            boton.setBackground(drawableOff);
        }
    }

    public void initializeBoard() {
        Random ran = new Random();
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                int probability = ran.nextInt(2);
                if (probability == 1) {
                    updateAdjacentButtons(i, j);
                }
            }
        }
    }

    public void updateAdjacentButtons(int row, int col) {
        this.model.getSolution()[row][col] = !model.getSolution()[row][col];

        int numRows = buttons.length;
        int numCols = buttons[0].length;
        cambiarColorBoton(buttons[row][col]);
        // Actualiza el botón al oeste
        if (col > 0) {
            cambiarColorBoton(buttons[row][col - 1]);
        }

        // Actualiza el botón al este
        if (col < numCols - 1) {
            cambiarColorBoton(buttons[row][col + 1]);
        }

        // Actualiza el botón al norte
        if (row > 0) {
            cambiarColorBoton(buttons[row - 1][col]);
        }

        // Actualiza el botón al sur
        if (row < numRows - 1) {
            cambiarColorBoton(buttons[row + 1][col]);
        }
    }

    public boolean finished() {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                if (buttons[i][j].getBackground() == drawableOn) {
                    return false;
                }
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] patron = { 500, 500 };
            vibrator.vibrate(patron, -1);
        }
        if (getResolved().equals(Resolved.USER)) {
            saveTime();
            mostrarDialogFinalizacion(seconds);
        } else {
            showDialogWhenSolved();
        }

        timerRunnable.stop();
        handler.removeCallbacks(finisherThread);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerRunnable.stop();
        handler.removeCallbacks(finisherThread);
    }

    // TODO unir las dos cosas en el mismo método

    private void mostrarDialogFinalizacion(int tiempo) {

        // Crear el builder de dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Configurar el título y el mensaje
        builder.setTitle("¡Felicitaciones!");
        builder.setMessage("Has completado el juego en " + timerRunnable.getSeconds() + " seconds");

        // Agregar el botón para regresar a la actividad principal
        builder.setPositiveButton("Regresar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Implementar la acción del botón
                dialog.dismiss();

                finish();
            }
        });

        // Agregar el botón para ver la tabla de calificaciones
        builder.setNegativeButton("Volver a jugar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Implementar la acción del botón
                dialog.dismiss();
                // Iniciar la actividad para ver la tabla de calificaciones
                Intent intent = new Intent(Game.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Crear y mostrar el dialog
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(R.color.fifth);

        dialog.show();
    }

    public void showDialogWhenSolved() {
        // Crear el builder de dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Configurar el título y el mensaje
        builder.setTitle("¡Finalizado!");
        builder.setMessage("Se ha resuelto el juego");

        // Agregar el botón para regresar a la actividad principal
        builder.setPositiveButton("Regresar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Implementar la acción del botón
               // dialog.dismiss();
                finish();
            }
        });

        // Agregar el botón para ver la tabla de calificaciones
        builder.setNegativeButton("Volver a jugar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Implementar la acción del botón
                dialog.dismiss();
                // Iniciar la actividad para ver la tabla de calificaciones
                Intent intent = new Intent(Game.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Crear y mostrar el dialog
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(R.color.fifth);
        prueba();
        dialog.show();
    }


    public void disableLightButtons(){
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    public void saveTime(){
        Log.d("Sirmaza", "save");
        this.dbHelper.getWritableDatabase();

        // Inserta un registro en la tabla game_time
        ContentValues values = new ContentValues();
        values.put("time", timerRunnable.getSeconds());  // Ejemplo de tiempo del juego
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateTime = dateFormat.format(calendar.getTime());
        values.put("date", dateTime);  // Ejemplo de fecha del juego
        db.insert("game_time", null, values);
        prueba();
    }


    public void prueba(){
        Log.d("Sirmaza", "ejec");
        String[] columns = {"id", "time", "date"};
        Cursor cursor = db.query("game_time", columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int time = cursor.getInt(cursor.getColumnIndexOrThrow("time"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            Log.d("Sirmaza", "id: " + id + ", time: " + time + ", date: " + date);
        }
        cursor.close();
    }

}