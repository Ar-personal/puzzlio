package com.example.puzzlio;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.Serializable;

public class Puzzle extends AppCompatActivity implements Serializable {

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
    private static final long serialVersionUID = 42L;

    private PuzzleList puzzleList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle);

        puzzleLayout = (RelativeLayout) findViewById(R.id.puzzlelayout);



        title = (String) getIntent().getExtras().get("name");
        puzzleType = getIntent().getExtras().getInt("type");
        arrayValues = (String[][]) getIntent().getExtras().get("gridValues");
        arrayLocked = (Integer[][]) getIntent().getExtras().get("gridLocked");
        load = getIntent().getExtras().getBoolean("load");
        position = getIntent().getExtras().getInt("position");
        dims = getIntent().getExtras().getIntArray("dims");


//        if(!load) {
//            PuzzleList.addItem(this);
//        }
//        arrayBlack = (Integer[][]) extras.get("gridLocked");


        gridButtons = new Button[dims[0]][dims[1]];

        Toolbar toolbar = findViewById(R.id.puzzletoolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));




        switch (puzzleType){
            case 1:
                System.out.println("create crossword " + puzzleType);
                break;
            case 2:
                createSudokuGrid();
            break;

            case 3:

            break;
        }





    }



    private void createSudokuGrid() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        int X, Y = 400;

        for (int y = 0; y < arrayValues.length; y++) {
            X = 0;
            for (int x = 0; x < arrayValues[y].length; x++) {
                    final int j = x, k = y;

                    gridButtons[x][y] = new Button(this);
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(Puzzle.this);
                                builder.setTitle("Edit");
                                EditText input = new EditText(Puzzle.this);
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

                    puzzleLayout.addView(gridButtons[x][y]);

                    X += 120;

                }
                Y += 120;

            }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                for (int y = 0; y < arrayValues.length; y++) {
                    for (int x = 0; x < arrayValues[y].length; x++) {
                         arrayValues[x][y] = gridButtons[x][y].getText().toString();
                        }
                    }

            if(load) {
                PuzzleList.replaceItem(Puzzle.this, position);
                PuzzleList.saveData(this, this);

                this.finish();
            }else{
                PuzzleList.addItem(this);
                PuzzleList.saveData(this, this);
                this.finish();
                return true;
            }


            case R.id.savepuzzle:
//                PuzzleList.saveData(this, this);
//                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.puzzlemenu, menu);
        return true;
    }



    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return title;
    }

    public int getPuzzleType() {
        return puzzleType;
    }

    public Button[][] getGridButtons() {
        return gridButtons;
    }

    public RelativeLayout getPuzzleLayout() {
        return puzzleLayout;
    }

    public String[][] getArrayValues() {
        return arrayValues;
    }

    public Integer[][] getArrayLocked() {

        return arrayLocked;
    }

    public Integer[][] getArrayBlack() {
        return arrayBlack;
    }

    public int[] getDims() {
        return dims;
    }

    public Integer getPosition() {
        return position;
    }
}
