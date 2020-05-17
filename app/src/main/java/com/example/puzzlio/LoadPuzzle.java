package com.example.puzzlio;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoadPuzzle extends Puzzle{


    private int image;
    private String title;
    private String description;
    private String dateCreated;
    private String author;
    private int puzzleType;
    private Button[][] gridButtons;
    private RelativeLayout puzzleLayout;
    private String[][] arrayValues;
    private Integer[][] arrayLocked, arrayBlack;
    private boolean load;
    private Integer position;
    private int[] dims;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        puzzleLayout = (RelativeLayout) findViewById(R.id.puzzlelayout);


        context = getApplicationContext();


        title = (String) getIntent().getExtras().get("name");
        puzzleType = getIntent().getExtras().getInt("type");
        arrayValues = (String[][]) getIntent().getExtras().get("gridValues");
        arrayLocked = (Integer[][]) getIntent().getExtras().get("gridLocked");
        load = getIntent().getExtras().getBoolean("load");
        position = getIntent().getExtras().getInt("position");
        dims = getIntent().getExtras().getIntArray("dims");

        gridButtons = new Button[dims[0]][dims[1]];


        switch (puzzleType){
            case 1:
                System.out.println("create crossword " + puzzleType);
                break;
            case 2:
                createSudokuGrid();
                PuzzleList.addItem(this);
                PuzzleList.saveData(this, this);
                finish();
                break;

            case 3:

                break;
        }


    }

//    public LoadPuzzle(Context context, String name, String[][] vals, Integer[][] locked, Integer[][] black, int[] dims, int type){
//        this.title = name;
//        this.arrayValues = vals;
//        this.arrayLocked = locked;
//        this.arrayBlack = black;
//        this.dims = dims;
//        this.puzzleType = type;
//        this.context = context;
//
//
//

//        switch (puzzleType){
//            case 1:
//                System.out.println("create crossword " + puzzleType);
//                break;
//            case 2:
//                createSudokuGrid();
//                break;
//
//            case 3:
//
//                break;
//        }

//    }


    private void createSudokuGrid() {
        int X, Y = 400;

        for (int y = 0; y < arrayValues.length; y++) {
            X = 0;
            for (int x = 0; x < arrayValues[y].length; x++) {
                final int j = x, k = y;

                gridButtons[x][y] = new Button(context);
                gridButtons[x][y].setX(X);
                gridButtons[x][y].setY(Y);
                gridButtons[x][y].setLayoutParams(new RelativeLayout.LayoutParams(120, 120));

                gridButtons[x][y].setText(arrayValues[x][y]);
                gridButtons[x][y].setTextColor(Color.BLACK);
                gridButtons[x][y].setGravity(Gravity.CENTER);
                gridButtons[x][y].setTextSize(28);
                gridButtons[x][y].setPadding(0, 0, 0, 0);
                gridButtons[x][y].setBackgroundResource(R.drawable.gridborder);
                gridButtons[x][y].setInputType(InputType.TYPE_CLASS_NUMBER);

                //set max text length to 1
                int maxLength = 1;
                InputFilter[] fa = new InputFilter[1];
                fa[0] = new InputFilter.LengthFilter(maxLength);
                gridButtons[x][y].setFilters(fa);

                boolean locked;

                if (arrayLocked[x][y] != null && arrayLocked[x][y] == 10) {
                    locked = true;
                } else {
                    locked = false;
                }


                if (!locked) {
                    gridButtons[x][y].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Edit");
                            EditText input = new EditText(context);
                            input.setInputType(InputType.TYPE_CLASS_NUMBER);
                            builder.setView(input);

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    String text = input.getText().toString();
                                    gridButtons[j][k].setText(text);
                                    arrayValues[j][k] = gridButtons[j][k].getText().toString();
                                    System.out.println(text);

                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });

                            builder.show();
                        }
                    });
                }


                System.out.println(gridButtons[x][y].getText());


                X += 120;

            }
            Y += 120;

        }

    }

    @Override
    public int getImage() {
        return image;
    }

    @Override
    public String getName() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getDateCreated() {
        return dateCreated;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public int getPuzzleType() {
        return puzzleType;
    }

    @Override
    public Button[][] getGridButtons() {
        return gridButtons;
    }

    @Override
    public RelativeLayout getPuzzleLayout() {
        return puzzleLayout;
    }

    @Override
    public String[][] getArrayValues() {
        return arrayValues;
    }

    @Override
    public Integer[][] getArrayLocked() {
        return arrayLocked;
    }

    @Override
    public Integer[][] getArrayBlack() {
        return arrayBlack;
    }

    public boolean isLoad() {
        return load;
    }

    public Integer getPosition() {
        return position;
    }

    @Override
    public int[] getDims() {
        return dims;
    }


}
