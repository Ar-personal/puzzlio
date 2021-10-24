package com.example.puzzlio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.io.Serializable;
import java.util.ArrayList;

public class SudokuCreator extends AppCompatActivity implements Serializable{

    private int[] dims;
    private Button[][] gridButtons;
    private ToggleButton toggleButton;
    private boolean locked;
    private String text, title;
    private int puzzleType;
    private TextView puzzleTitle;
    private Integer[][] arrayLocked, arrayBlack;
    private ArrayList<String> arrayValues;
    private boolean scanned;
    static int a = 0;
    // 0 = topleft, 1= top, 2 = top right, 3 = left, 4 = center, 5 = right, 6 = inner right, 7 = inner left 8 = bottom 9 = bottom right, 10 = right4bottom2, 11 = inner top right, 12 = inner top left, 13 = inner bottom left, 14 = bottomleft, 15 = inner bottom, 16 = innerbottomright, 17 = right bottom
    private int[][] backGroundMap = {{0, 1, 11, 12, 1, 11, 12, 1, 2}, {3, 4, 6, 7, 4, 6, 7, 4, 5}, {23, 15, 16, 13, 15, 16, 13, 15, 10}, {21, 20, 22, 24, 20, 22, 24, 20, 25}, {3, 4, 6, 7, 4, 6, 7, 4, 5}, {23, 15, 16, 13, 15, 16, 13, 15, 10}, {21, 20, 22, 24, 20, 22, 24, 20, 25}, {3, 4, 6, 7, 4, 6, 7, 4, 5}, {14, 8, 17, 19, 8, 17, 19, 8, 9}};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sudokucreator);

        Bundle extras = getIntent().getExtras();
        title = extras.getString("title");
        puzzleType = extras.getInt("type");
        dims = extras.getIntArray("dims");
        scanned = extras.getBoolean("scanned");

        gridButtons = new Button[dims[0]][dims[1]];


        //use nested for loop
        arrayLocked = new Integer[dims[0]][dims[1]];

        //if the puzzle was scanned in then creating a new object here will lose the scanned data
        if(!scanned) {
            arrayValues = new ArrayList();
        }

        puzzleTitle = findViewById(R.id.puzzleTitle);
        puzzleTitle.setText(title);


        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.sodukulayout);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //set puzzle start to half the screen minus half the puzzle size - change variables
        int X, Y = (displayMetrics.heightPixels / 2) - ((120 * 9) / 2 ) -100;

        //action bar
        getSupportActionBar().setTitle("Editor Mode");
        getSupportActionBar().setLogo(R.drawable.ic_menu_white_30dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#307BC0")));

        //lockbutton
        toggleButton = findViewById(R.id.lockgrid);


//        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(toggleButton.isChecked()){
//                    locked = true;
//                }else{
//                    locked = false;
//                }
//            }
//        });


        //puzzle grid

        for(int y = 0; y < gridButtons.length; y++){
                X = 0;
            for(int x = 0; x < gridButtons[y].length; x++){
                final int j = x , k = y;


                gridButtons[x][y] = new Button(this);
                gridButtons[x][y].setX(X);
                gridButtons[x][y].setY(Y);
                gridButtons[x][y].setLayoutParams(new ConstraintLayout.LayoutParams(120, 120));

                gridButtons[x][y].setText("");
                gridButtons[x][y].setTextColor(Color.BLACK);
                gridButtons[x][y].setGravity(Gravity.CENTER);
                gridButtons[x][y].setTextSize(28);
                gridButtons[x][y].setPadding(0, 0, 0, 0);
                gridButtons[x][y].setBackgroundResource(R.drawable.gridborder);
                gridButtons[x][y].setInputType(InputType.TYPE_CLASS_NUMBER);

                //begin terrible code for sudoku borders
                // 0 = topleft, 1= top, 2 = top right, 3 = left, 4 = center, 5 = right, 6 = vertical, 7 = bottom left 8 = bottom 9 = bottom right, 10 = inner bottom right, 11 = inner top right, 12 = inner top left
                switch (backGroundMap[y][x]){
                    case 0:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokutopleft);
                        break;
                    case 1:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokutop);
                        break;
                    case 2:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokutopright);
                        break;
                    case 3:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokuleft);
                        break;
                    case 4:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokucenter);
                        break;
                    case 5:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokuright);
                        break;
                    case 6:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokuinnerright);
                        break;
                    case 7:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokuinnerleft);
                        break;
                    case 8:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokubottom);
                        break;
                    case 9:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokubottomright);
                        break;
                    case 10:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokuright4bottom2);
                        break;
                    case 11:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokuinnertopright);
                        break;
                    case 12:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokuinnertopleft);
                        break;
                    case 13:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokuinnerbottomleft);
                        break;
                    case 14:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokubottomleft);
                        break;
                    case 15:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokuinnerbottom);
                        break;
                    case 16:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokuinnerbottomright);
                        break;
                    case 17:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokurightbottom);
                        break;
                    case 18:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokuleftbottom);
                        break;
                    case 19:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudokuleft2bottom4);
                        break;
                    case 20:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudoku2top);
                        break;
                    case 21:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudoku4left2top);
                        break;
                    case 22:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudoku2top2right);
                        break;
                    case 23:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudoku4left2bottom);
                        break;
                    case 24:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudoku2left2top);
                        break;
                    case 25:
                        gridButtons[x][y].setBackgroundResource(R.drawable.sudoku2top4right);
                        break;

                }


                //set max text length to 1
                int maxLength = 1;
                InputFilter[] fa = new InputFilter[1];
                fa[0] = new InputFilter.LengthFilter(maxLength);
                gridButtons[x][y].setFilters(fa);


                int finalX = X;
                int finalY = Y;
                gridButtons[x][y].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                finalX, finalY);
                        if (!locked) {
//                            gridButtons[j][k].setBackground(ContextCompat.getDrawable(SudokuCreator.this, R.drawable.ic_lock_open_green_30dp));
                            EditText input = new EditText(SudokuCreator.this);
                            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1) {}});
                            input.setLines(1);
                            input.setMaxLines(1);

                            input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                @Override
                                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                                    switch (i){
                                        //if keyboard 'tick' reset the tile value and remove edit text from layout to reset keyboard
                                        case EditorInfo.IME_ACTION_DONE:

                                            String text = input.getText().toString();
                                            gridButtons[j][k].setText(text);
                                            arrayValues.set(a, gridButtons[j][k].getText().toString());
                                            a++;
                                            input.clearFocus();

                                            break;
                                    }
                                    return false;
                                }
                            });

                            constraintLayout.addView(input, params);

                            input.requestFocus();

                            input.setFocusableInTouchMode(true);

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                            input.setTransformationMethod(new SudokuCreator.NumericKeyBoardTransformationMethod());

                            //if user clicks away from a tile, remove the edittext to reset keyboard
                            input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View view, boolean b) {
                                    if(!b){
                                        constraintLayout.removeView(input);
                                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    }
                                }
                            });
                        }
                    }
                });

                System.out.println(gridButtons[x][y].getText());

                constraintLayout.addView(gridButtons[x][y]);

                X += 120;

            }
            Y += 120;

        }

        if(scanned){
            arrayValues = (ArrayList<String>) extras.get("valuesScanned");
            int a = 0;
            for(int i = 0; i < dims[1]; i++){
                for(int j = 0; j < dims[0]; j++){
                    try {
                        gridButtons[j][i].setText(arrayValues.get(a));
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    a++;
                }
            }
        }



        Button createButton = findViewById(R.id.createsudoku);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareArrays(gridButtons);

                Intent intent = new Intent(SudokuCreator.this, Puzzle.class);

                intent.putExtra("name", title);
                intent.putExtra("type", puzzleType);
                intent.putExtra("gridValues", arrayValues);
                intent.putExtra("gridLocked", arrayLocked);
                intent.putExtra("dims", dims);
                startActivity(intent);
                finish();
            }
        });


    }

    private class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }

    private void prepareArrays(Button[][] b) {
        int a = 0;
        for(int i = 0; i < b.length; i++){
            for(int j = 0; j < b[i].length; j++){

                CharSequence v1 = b[i][j].getText();
                if(v1.toString() != "" || v1 != null) {
                    arrayValues.set(a, String.valueOf(v1));
                }

            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}


