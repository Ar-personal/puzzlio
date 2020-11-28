package com.example.puzzlio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;

public class Puzzle extends AppCompatActivity implements Serializable {
    //nav drawer
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    //puzzle creation
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
    //messy grid representation of sudoku lines
    private int[][] backGroundMap = {{0, 1, 11, 12, 1, 11, 12, 1, 2}, {3, 4, 6, 7, 4, 6, 7, 4, 5}, {23, 15, 16, 13, 15, 16, 13, 15, 10}, {21, 20, 22, 24, 20, 22, 24, 20, 25}, {3, 4, 6, 7, 4, 6, 7, 4, 5}, {23, 15, 16, 13, 15, 16, 13, 15, 10}, {21, 20, 22, 24, 20, 22, 24, 20, 25}, {3, 4, 6, 7, 4, 6, 7, 4, 5}, {14, 8, 17, 19, 8, 17, 19, 8, 9}};



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle);

        puzzleLayout = (RelativeLayout) findViewById(R.id.puzzle_relative_layout);



        //load information from puzzle creation screens
        title = (String) getIntent().getExtras().get("name");
        puzzleType = getIntent().getExtras().getInt("type");
        arrayValues = (String[][]) getIntent().getExtras().get("gridValues");
        arrayLocked = (Integer[][]) getIntent().getExtras().get("gridLocked");
        load = getIntent().getExtras().getBoolean("load");
        position = getIntent().getExtras().getInt("position");
        dims = getIntent().getExtras().getIntArray("dims");
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);



        gridButtons = new Button[dims[0]][dims[1]];

        Toolbar toolbar = findViewById(R.id.puzzletoolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        Toolbar bottomAppBar = findViewById(R.id.bottomAppBar);

        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.main:
                        finish();
                }
                return false;
            }
        });




        //puzzle drawer
        mDrawer = (DrawerLayout) findViewById(R.id.puzzlelayout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        });


        //generate puzzle based on selected puzzle type
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

    int X, Y = 400;
    private void createSudokuGrid() {
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
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                        X, Y);
                                EditText input = new EditText(Puzzle.this);
                                input.setLayoutParams(new RelativeLayout.LayoutParams(X, Y));
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
                                                arrayValues[j][k] = gridButtons[j][k].getText().toString();
                                                input.clearFocus();
                                                break;
                                        }
                                        return false;
                                    }
                                });
                                puzzleLayout.addView(input, params);
                                input.requestFocus();

                                input.setFocusableInTouchMode(true);


                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                                input.setTransformationMethod(new NumericKeyBoardTransformationMethod());

                                //if user clicks away from a tile, remove the edittext to reset keyboard
                                input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View view, boolean b) {
                                        if(!b){
                                            puzzleLayout.removeView(input);
                                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                        }
                                    }
                                });
                            }
                        });
                    }

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

                    System.out.println(gridButtons[x][y].getText());

                    puzzleLayout.addView(gridButtons[x][y]);

                    X += 120;

                }
                Y += 120;

            }

    }

    //enables numerical keyboard for sudoku grid input
    private class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
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
            //saving and loading, needs to be checked
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
                for (int y = 0; y < arrayValues.length; y++) {
                    for (int x = 0; x < arrayValues[y].length; x++) {
                        arrayValues[x][y] = gridButtons[x][y].getText().toString();
                    }
                }
                //saving and loading, needs to be checked
                if(load) {
                    PuzzleList.replaceItem(Puzzle.this, position);
                    PuzzleList.saveData(this, this);
                }else{
                    PuzzleList.addItem(this);
                    PuzzleList.saveData(this, this);
                    return true;
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.puzzlemenu, menu);
        return true;
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;
        switch(menuItem.getItemId()) {
            case R.id.drawer_item2:
                fragmentClass = PuzzleAttachedMedia.class;
                break;
            default:
                System.out.println("error with drawer item");
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
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
