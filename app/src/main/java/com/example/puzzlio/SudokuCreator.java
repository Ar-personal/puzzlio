package com.example.puzzlio;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
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
import androidx.core.content.ContextCompat;

import java.io.Serializable;
import java.lang.reflect.Array;

public class SudokuCreator extends AppCompatActivity implements Serializable{

    private int[] dims;
    private Button[][] gridButtons;
    private ToggleButton toggleButton;
    private boolean locked;
    private String text, title;
    private int puzzleType;
    private TextView puzzleTitle;
    private Integer[][] arrayLocked, arrayBlack;
    private String[][] arrayValues;
    private boolean scanned;


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
            arrayValues = new String[dims[0]][dims[1]];
        }



        puzzleTitle = findViewById(R.id.puzzleTitle);
        puzzleTitle.setText(title);


        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.sodukulayout);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //set puzzle start to half the screen minus half the puzzle size - change variables
        int X, Y = (displayMetrics.heightPixels / 2) - ((120 * 9) / 2 ) -200;

        //action bar
        getSupportActionBar().setTitle("Editor Mode");
        getSupportActionBar().setLogo(R.drawable.ic_menu_white_30dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#307BC0")));

        //lockbutton
        toggleButton = findViewById(R.id.lockgrid);


        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(toggleButton.isChecked()){
                    locked = true;
                }else{
                    locked = false;
                }
            }
        });


        //puzzle grid
        for(int y = 0; y < gridButtons.length; y++){
                X = 0;
            for(int x = 0; x < gridButtons[y].length; x++){
                final int j = x , k = y;

                gridButtons[x][y] = new Button(this);
                gridButtons[x][y].setX(X);
                gridButtons[x][y].setY(Y);
                gridButtons[x][y].setLayoutParams(new RelativeLayout.LayoutParams(120, 120));

                gridButtons[x][y].setText("");
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





                gridButtons[x][y].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!locked) {
                            gridButtons[j][k].setBackground(ContextCompat.getDrawable(SudokuCreator.this, R.drawable.ic_lock_open_green_30dp));
                            AlertDialog.Builder builder = new AlertDialog.Builder(SudokuCreator.this);
                            builder.setTitle("Edit");
                            EditText input = new EditText(SudokuCreator.this);
                            input.setInputType(InputType.TYPE_CLASS_NUMBER);
                            builder.setView(input);

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    text = input.getText().toString();
                                    gridButtons[j][k].setText(text);
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
                        }else{
                            gridButtons[j][k].setBackground(ContextCompat.getDrawable(SudokuCreator.this, R.drawable.ic_lock_outline_red_30dp));
                            arrayLocked[j][k] = 10;
                            Toast.makeText(SudokuCreator.this, "Unlock to edit", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                System.out.println(gridButtons[x][y].getText());

                relativeLayout.addView(gridButtons[x][y]);

                X += 120;

            }
            Y += 120;

        }

        if(scanned){
            arrayValues = (String[][]) extras.get("valuesScanned");

            for(int i = 0; i < dims[1]; i++){
                for(int j = 0; j < dims[0]; j++){
                    gridButtons[j][i].setText(arrayValues[i][j]);
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

    private void prepareArrays(Button[][] b) {


        for(int i = 0; i < b.length; i++){
            for(int j = 0; j < b[i].length; j++){


                CharSequence v1 = b[i][j].getText();
                if(v1.toString() != "" || v1 != null) {
                    arrayValues[i][j] = String.valueOf(v1);
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


