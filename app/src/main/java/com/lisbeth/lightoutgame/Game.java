package com.lisbeth.lightoutgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
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
    private TextView textTime;
    private GameModel model;
    private Handler handler = new Handler();
    private Handler timeHandler = new Handler();
    private Button solve;
    private List<Pair<Integer, Integer>> lightsToShow;
    private TimerRunnable timerRunnable;
    private GameDatabaseHelper dbHelper;

    // Creating a new thread that will run every 2 seconds which checks that the
    // matrix is solved.
    private Runnable finisherThread = new Runnable() {
        public void run() {
            handler.postDelayed(this, 300);
            finished();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        dbHelper = new GameDatabaseHelper(this);
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

        timeHandler.postDelayed(timerRunnable, 1000);

        handler.post(finisherThread);

        this.setResolved(Resolved.USER);

        // add listener for the solve button
        solve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableLightButtons();
                timerRunnable.stop();
                showLightsToTouch();
                setResolved(Resolved.PROGRAM);
            }
        });
    }


    @Override
    protected void onDestroy() {
        timerRunnable.stop();
        dbHelper.close();
        handler.removeCallbacks(finisherThread);
        super.onDestroy();
    }

    // ! methods for the lights

    /**
     * It returns a list of all the lights that are on in the solution
     * 
     * @return A list of pairs of integers.
     */
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
        int row = this.getRowButton(button);
        int column = this.getColumnButton(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAdjacentButtons(row, column);
            }
        });
    }

    private int getRowButton(Button boton) {
        for (int row = 0; row < buttons.length; row++) {
            for (int column = 0; column < buttons[row].length; column++) {
                if (buttons[row][column] == boton) {
                    return row;
                }
            }
        }
        return -1;
    }

    private int getColumnButton(Button boton) {
        for (int fila = 0; fila < buttons.length; fila++) {
            for (int columna = 0; columna < buttons[fila].length; columna++) {
                if (buttons[fila][columna] == boton) {
                    return columna;
                }
            }
        }
        return -1;
    }

    private void showLight(int row, int column) {
        buttons[row][column].setText("X");
        buttons[row][column].setTextColor(getResources().getColor(R.color.fifth));
        updateAdjacentButtons(row, column);
    }

    private void showLightsToTouch() {
        lightsToShow = getLightsToShow();
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

    private void changeColorButton(Button boton) {
        if (boton.getBackground() == drawableOff) {
            boton.setBackground(drawableOn);
        } else {
            boton.setBackground(drawableOff);
        }
    }

    public void initializeBoard() {
        Random ran = new Random();
        boolean isInit = false;
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                int probability = ran.nextInt(2);
                if (probability == 1) {
                    isInit = true;
                    updateAdjacentButtons(i, j);
                }
            }
        }
        if(!isInit){
            initializeBoard();
        }
    }

    public void updateAdjacentButtons(int row, int col) {
        this.model.getSolution()[row][col] = !model.getSolution()[row][col];

        int numRows = buttons.length;
        int numCols = buttons[0].length;
        changeColorButton(buttons[row][col]);
        // Actualiza el botón al oeste
        if (col > 0) {
            changeColorButton(buttons[row][col - 1]);
        }

        // Actualiza el botón al este
        if (col < numCols - 1) {
            changeColorButton(buttons[row][col + 1]);
        }

        // Actualiza el botón al norte
        if (row > 0) {
            changeColorButton(buttons[row - 1][col]);
        }

        // Actualiza el botón al sur
        if (row < numRows - 1) {
            changeColorButton(buttons[row + 1][col]);
        }
    }

    // ! methods for the layout
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

    /**
     * It creates a grid of buttons with a margin of 10dp
     */
    public void gameLayout() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x - 100;
        screenHeight = size.y - convertDpToPx(340);
        if (screenWidth > screenHeight) {
            buttonSize = (screenHeight / 5) - 20;
        } else {
            buttonSize = (screenWidth / 5) - 20;
        }
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

    // ! methods when game is end

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
            showDialogWhenUserSolved(timerRunnable.getSeconds());
        } else {
            showDialogWhenSolved();
        }

        timerRunnable.stop();
        handler.removeCallbacks(finisherThread);
        return true;
    }

    public void disableLightButtons() {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    public void showDialogWhenSolved() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Finished!");
        builder.setMessage("Game has been solved");

        builder.setPositiveButton("ScoreBoard", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // TODO implementar una vista para que se vean las tablas de clasificacion
                finish();
            }
        });

        // return to main activity
        builder.setNegativeButton("Play again", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
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

    private void showDialogWhenUserSolved(int tiempo) {
        dbHelper.saveRecord(new Records(this.getCurrentTime(), Integer.toString(timerRunnable.getSeconds())));
        dbHelper.showRecords();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Congratulations!!");
        builder.setMessage("You have completed the game in " + timerRunnable.getSeconds() + " seconds");

        builder.setPositiveButton("ScoreBoard", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // TODO implementar una vista para que se vean las tablas de clasificacion
                finish();
            }
        });

        // Agregar el botón para ver la tabla de calificaciones
        builder.setNegativeButton("Play again", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Implementar la acción del botón
                dialog.dismiss();
                // Iniciar la actividad para ver la tabla de calificaciones
                Intent intent = new Intent(Game.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(R.color.fifth);
        dialog.show();
    }

    // ! getters and setters

    public Resolved getResolved() {
        return resolved;
    }

    public void setResolved(Resolved resolved) {
        this.resolved = resolved;
    }

    public String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormat.format(calendar.getTime());
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getButtonSize() {
        return buttonSize;
    }

    public void setButtonSize(int buttonSize) {
        this.buttonSize = buttonSize;
    }

    public int getMarginSize() {
        return marginSize;
    }

    public void setMarginSize(int marginSize) {
        this.marginSize = marginSize;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Drawable getDrawableOn() {
        return drawableOn;
    }

    public TextView getTextTime() {
        return textTime;
    }

    public void setTextTime(TextView textTime) {
        this.textTime = textTime;
    }

    public GameModel getModel() {
        return model;
    }

    public void setModel(GameModel model) {
        this.model = model;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Handler getTimeHandler() {
        return timeHandler;
    }

    public void setTimeHandler(Handler timeHandler) {
        this.timeHandler = timeHandler;
    }

    public Button getSolve() {
        return solve;
    }

    public void setSolve(Button solve) {
        this.solve = solve;
    }

    public void setLightsToShow(List<Pair<Integer, Integer>> lightsToShow) {
        this.lightsToShow = lightsToShow;
    }

    public TimerRunnable getTimerRunnable() {
        return timerRunnable;
    }

    public void setTimerRunnable(TimerRunnable timerRunnable) {
        this.timerRunnable = timerRunnable;
    }

    public GameDatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public void setDbHelper(GameDatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public Runnable getFinisherThread() {
        return finisherThread;
    }

    public void setFinisherThread(Runnable finisherThread) {
        this.finisherThread = finisherThread;
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

    public Button[][] getButtons() {
        return buttons;
    }

    public void setButtons(Button[][] buttons) {
        this.buttons = buttons;
    }

    public void setDrawableOn(Drawable drawableOn) {
        this.drawableOn = drawableOn;
    }

}