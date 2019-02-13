package in.curioustools.a2048minimal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/*
todo:
undo
2048 win  screen
animations
rework on mathemetical logic

* */

@SuppressLint("DefaultLocale")
public class MainActivity extends AppCompatActivity {
    ConstraintLayout layoutGameOver,layoutGame;
    TextView tvHighScore, tvCurrentScore,tvGameoverScore;


    Button btNewGame,btRestart;
    GridLayout gvBoard;


    int highScore, currentScore;


    boolean isGameOver;
    ArrayList<String> boardStringArr;

    Random rand = new Random();
    SharedPreferences highScoreSharedPref;

    float x1, x2, y1, y2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialiseViews();
        loadGame();
    }

    private void initialiseViews() {
        //initialise some of the views

        layoutGameOver = findViewById(R.id.layout_gameover);
        layoutGame = findViewById(R.id.layout_game);

        layoutGame.setVisibility(View.VISIBLE);
        layoutGameOver.setVisibility(View.GONE);

        tvHighScore = findViewById(R.id.tv_highscore);
        tvCurrentScore = findViewById(R.id.tv_current_score);
        tvGameoverScore = findViewById(R.id.tv_gameover_score);


        gvBoard = findViewById(R.id.gl_board);
        btNewGame =findViewById(R.id.bt_newgame);
        btRestart =findViewById(R.id.bt_restart);

        btNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });
        btRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });
    }

    public void loadGame() {
        boardStringArr = new ArrayList<>(16);
        setPreviousHighScores();//get  last highest score from shared preferences and set on highscore textviews
        initialiseBoard();//initialising the 2048  with number 2 popping in any 1 cell
        drawBoardColors();//sets color of all cells onthe board , using a for loop

    }

    private void setPreviousHighScores() {
        //get  last highest score from shared preferences and set on highscore textviews
        highScoreSharedPref = this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        highScore = highScoreSharedPref.getInt("Score", 0);
        tvHighScore.setText(String.format("%d", highScore));
    }
    private void initialiseBoard() {
        //generate an arraylist of 16 strings and replace any 1 string  with 2
        // i.e initialising the "boardStringArr"  with number 2 popping in at any cell
        boardStringArr = new ArrayList<>(16);
        for (int i = 0; i < 16; i++) {
            boardStringArr.add("");
        }
        int randFirstIndex = rand.nextInt(16);
        int randSecondIndex = (randFirstIndex + 5 )% 16;
        boardStringArr.set(randFirstIndex, "2");
        boardStringArr.set(randSecondIndex, "2");

        TextView te = (TextView) gvBoard.getChildAt(randFirstIndex);// !! not a correct way of initialising a view
        TextView te2 = (TextView) gvBoard.getChildAt(randSecondIndex);// !! not a correct way of initialising a view
        te.setText("2");
        te2.setText("2");
    }

    private int[] getColor(String text){
        switch (text){
            case "":{ return  new int[]{R.color.gray1,R.color.white}; }
            case "2":{ return  new int[]{R.color.for2,R.color.colorPrimary}; }
            case "4":{ return  new int[]{R.color.for4,R.color.white}; }
            case "8":{ return  new int[]{R.color.for8,R.color.white}; }
            case "16":{ return  new int[]{R.color.for16,R.color.white}; }
            case "32":{ return  new int[]{R.color.for32,R.color.white}; }
            case "64":{ return  new int[]{R.color.for64,R.color.white}; }
            case "128":{ return  new int[]{R.color.for128,R.color.white}; }
            case "256":{ return  new int[]{R.color.for256,R.color.white}; }
            case "512":{ return  new int[]{R.color.for512,R.color.white}; }
            case "1024":{ return  new int[]{R.color.for1024,R.color.white}; }
            case "2048":{ return  new int[]{R.color.for2048,R.color.white}; }
            default:{ return  new int[]{R.color.colorPrimary,R.color.white}; }
        }

    }
    private void drawBoardColors() {
        if (!isGameOver) {
            for (int i = 0; i < 16; i++) {
                TextView te = (TextView) gvBoard.getChildAt(i);

                String tvText = te.getText().toString();
                int bgcolor =ContextCompat.getColor(getBaseContext(),getColor(tvText)[0]);
                int textColor =ContextCompat.getColor(getBaseContext(),getColor(tvText)[1]);

                te.setBackgroundColor(bgcolor);
                te.setTextColor(textColor);
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
        if (!isGameOver) {
            switch (touchevent.getAction()) {
                // when user first touches the screen we get x and y coordinate
                case MotionEvent.ACTION_DOWN: {
                    x1 = touchevent.getX();
                    y1 = touchevent.getY();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    x2 = touchevent.getX();
                    y2 = touchevent.getY();

                    ArrayList<String> copy = new ArrayList<>(16);
                    for (int i = 0; i < 16; i++) {
                        copy.add(boardStringArr.get(i));
                    }

                    //if left to right sweep event on screen
                    if (x2 > x1 && x2 - x1 > y1 - y2 && x2 - x1 > y2 - y1) {
                        for (int i = 0; i < 4; i++) {
                            for (int j = 2; j >= 0; j--) {
                                if (!boardStringArr.get(i * 4 + j).equals(""))
                                    goRight(i, j);
                            }
                        }
                    }

                    // if right to left sweep event on screen
                    else if (x1 > x2 && x1 - x2 > y1 - y2 && x1 - x2 > y2 - y1) {
                        for (int i = 0; i < 4; i++) {
                            for (int j = 1; j < 4; j++) {
                                if (!boardStringArr.get(i * 4 + j).equals(""))
                                    goLeft(i, j);
                            }
                        }
                    }

                    // if UP to Down sweep event on screen
                    else if (y2 > y1 && y2 - y1 > x1 - x2 && y2 - y1 > x2 - x1) {
                        for (int i = 0; i < 4; i++) {
                            for (int j = 2; j >= 0; j--) {
                                if (!boardStringArr.get(j * 4 + i).equals(""))
                                    goDown(i, j);
                            }
                        }
                    }

                    //if Down to UP sweep event on screen
                    else if (y1 > y2 && y1 - y2 > x1 - x2 && y1 - y2 > x2 - x1) {
                        for (int i = 0; i < 4; i++) {
                            for (int j = 1; j < 4; j++) {
                                if (!boardStringArr.get(j * 4 + i).equals(""))
                                    goUp(i, j);
                            }
                        }
                    }


                    boolean gener = false;

                    for (int i = 0; i < 16; i++) {
                        if (!((copy.get(i)).equals(boardStringArr.get(i)))) {
                            gener = true;
                            break;
                        }
                    }

                    if (gener) {
                        Generate();
                        drawBoardColors();
                    }

                    break;
                }
            }
        }
        return super.onTouchEvent(touchevent);
    }

    private void goRight(int row, int col) {
        if (!isGameOver) {

            currentScore = Integer.parseInt(tvCurrentScore.getText() + "");
            int min = 4;
            if (isFinish())
                showGameOverScreen();
            else {
                for (int i = col + 1; i < min; i++) {
                    if ((boardStringArr.get(4 * row + i)).equals("")) {
                        // TODO: 13-02-2019  replace order
                        boardStringArr.set(4 * row + i, boardStringArr.get(4 * row + i - 1));
                        TextView te = (TextView) gvBoard.getChildAt(4 * row + i);
                        te.setText(boardStringArr.get(4 * row + i - 1));
                        boardStringArr.set(4 * row + i - 1, "");
                        TextView te2 = (TextView) gvBoard.getChildAt(4 * row + i - 1);
                        te2.setText("");
                    }
                    else if ((boardStringArr.get(4 * row + i)).equals(boardStringArr.get(4 * row + i - 1))) {
                        boardStringArr.set(4 * row + i, "" + (Integer.parseInt(boardStringArr.get(4 * row + i - 1))) * 2);
                        TextView te = (TextView) gvBoard.getChildAt(4 * row + i);
                        te.setText(String.format("%d", (Integer.parseInt(boardStringArr.get(4 * row + i - 1))) * 2));
                        boardStringArr.set(4 * row + i - 1, "");
                        TextView te2 = (TextView) gvBoard.getChildAt(4 * row + i - 1);
                        te2.setText("");
                        int plus = Integer.parseInt(te.getText() + "");
                        tvCurrentScore.setText(String.format("%d", currentScore + plus));
                        min--;
                    }
                }
            }
        }
    }
    private void goLeft(int row, int col) {
        if (!isGameOver) {

            int pul = 0;
            if (isFinish())
                showGameOverScreen();
            else {
                for (int i = col - 1; i >= pul; i--) {
                    if ((boardStringArr.get(4 * row + i)).equals("")) {
                        boardStringArr.set(4 * row + i, boardStringArr.get(4 * row + i + 1));
                        TextView te = (TextView) gvBoard.getChildAt(4 * row + i);
                        te.setText(boardStringArr.get(4 * row + i + 1));
                        boardStringArr.set(4 * row + i + 1, "");
                        TextView te2 = (TextView) gvBoard.getChildAt(4 * row + 1 + i);
                        te2.setText("");
                    } else if ((boardStringArr.get(4 * row + i)).equals(boardStringArr.get(4 * row + i + 1))) {
                        boardStringArr.set(4 * row + i, "" + (Integer.parseInt(boardStringArr.get(4 * row + i + 1))) * 2);
                        TextView te = (TextView) gvBoard.getChildAt(4 * row + i);
                        te.setText(String.format("%d", (Integer.parseInt(boardStringArr.get(4 * row + i + 1))) * 2));
                        boardStringArr.set(4 * row + i + 1, "");
                        TextView te2 = (TextView) gvBoard.getChildAt(4 * row + i + 1);
                        te2.setText("");
                        int plus = Integer.parseInt(te.getText() + "");
                        tvCurrentScore.setText(String.format("%d", currentScore + plus));
                        pul++;
                    }
                }
            }
        }
    }
    private void goDown(int colm, int roww) {
        if (!isGameOver) {
            int min = 4;
            if (isFinish())
                showGameOverScreen();
            else {
                for (int i = roww + 1; i < min; i++) {
                    if ((boardStringArr.get(4 * i + colm)).equals("")) {
                        boardStringArr.set(4 * i + colm, boardStringArr.get(4 * i + colm - 4));
                        TextView te = (TextView) gvBoard.getChildAt(4 * i + colm);
                        te.setText(boardStringArr.get(4 * i + colm - 4));
                        boardStringArr.set(4 * i + colm - 4, "");
                        TextView te2 = (TextView) gvBoard.getChildAt(4 * i + colm - 4);
                        te2.setText("");
                    }
                    else if ((boardStringArr.get(4 * i + colm)).equals(boardStringArr.get(4 * i + colm - 4))) {
                        boardStringArr.set(4 * i + colm, "" + (Integer.parseInt(boardStringArr.get(4 * i + colm - 4))) * 2);
                        TextView te = (TextView) gvBoard.getChildAt(4 * i + colm);
                        te.setText(String.format("%d", (Integer.parseInt(boardStringArr.get(4 * i + colm - 4))) * 2));
                        boardStringArr.set(4 * i + colm - 4, "");
                        TextView te2 = (TextView) gvBoard.getChildAt(4 * i + colm - 4);
                        te2.setText("");
                        int plus = Integer.parseInt(te.getText() + "");
                        tvCurrentScore.setText(String.format("%d", currentScore + plus));
                        min--;
                    }
                }
            }
        }
    }
    private void goUp(int colm, int roww) {
        if (!isGameOver) {

            int pul = 0;
            if (isFinish())
                showGameOverScreen();
            else {
                for (int i = roww - 1; i >= pul; i--) {
                    if ((boardStringArr.get(4 * i + colm)).equals("")) {
                        boardStringArr.set(4 * i + colm, boardStringArr.get(4 * i + colm + 4));
                        TextView te = (TextView) gvBoard.getChildAt(4 * i + colm);
                        te.setText(boardStringArr.get(4 * i + colm + 4));
                        boardStringArr.set(4 * i + colm + 4, "");
                        TextView te2 = (TextView) gvBoard.getChildAt(4 * i + colm + 4);
                        te2.setText("");
                    }
                    else if ((boardStringArr.get(4 * i + colm)).equals(boardStringArr.get(4 * i + colm + 4))) {
                        boardStringArr.set(4 * i + colm, "" + (Integer.parseInt(boardStringArr.get(4 * i + colm + 4))) * 2);
                        TextView te = (TextView) gvBoard.getChildAt(4 * i + colm);
                        te.setText(String.format("%d", (Integer.parseInt(boardStringArr.get(4 * i + colm + 4))) * 2));
                        boardStringArr.set(4 * i + colm + 4, "");
                        TextView te2 = (TextView) gvBoard.getChildAt(4 * i + colm + 4);
                        te2.setText("");
                        int plus = Integer.parseInt(te.getText() + "");
                        tvCurrentScore.setText(String.format("%d", currentScore + plus));
                        pul++;
                    }
                }
            }
        }
    }
    private void Generate() {
        if (!isGameOver) {
            int theChoices[] = {2, 2, 2, 4, 2, 2, 2, 4, 2, 2, 4};
            int Chosen = theChoices[rand.nextInt(theChoices.length)];
            int placeIt = rand.nextInt(16);
            if (boardStringArr.get(placeIt).equals("")) {
                boardStringArr.set(placeIt, Chosen + "");
                TextView te = (TextView) gvBoard.getChildAt(placeIt);
                te.setText(String.format("%d", Chosen));
            } else {
                Generate();
            }
        }
    }
    private boolean isFinish() {
        for (int i = 0; i < 4; i++) {
            for (int j = 1; j < 4; j++) {
                if (((boardStringArr.get(4 * i + j)).equals(boardStringArr.get(4 * i + j - 1))))
                    return false;
            }

        }
        for (int j = 0; j < 4; j++) {
            for (int i = 1; i < 4; i++) {
                if ((boardStringArr.get(4 * i + j)).equals(boardStringArr.get(4 * (i - 1) + j)))
                    return false;
            }

        }
        for (int i = 0; i < 16; i++) {
            if ((boardStringArr.get(i)).equals(""))
                return false;
        }

        return true;
    }

    private void showGameOverScreen() {
        if(currentScore>highScore){
            highScoreSharedPref.edit().putInt("Score",currentScore).apply();
        }

        tvGameoverScore.setText(String.format("%d", currentScore));
        layoutGameOver.setVisibility(View.VISIBLE);
        layoutGame.setVisibility(View.GONE);
        isGameOver = true;

    }

    public void startNewGame() {
        finish();
        startActivity(new Intent(this,MainActivity.class));
    }

}
