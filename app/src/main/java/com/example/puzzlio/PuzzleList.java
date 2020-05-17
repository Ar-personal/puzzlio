package com.example.puzzlio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.puzzlio.R;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class PuzzleList extends Fragment implements Serializable {

        public static RecyclerViewAdapter recyclerViewAdapter;
        public static ArrayList<Puzzle> puzzleList;
        public static  SharedPreferences sharedPreferences;
        public static ArrayList<Integer> puzzleIds;

        @Override
        public View onCreateView(
                LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState
        ) {
            final View view = inflater.inflate(R.layout.fragment_home, container, false);
            final RecyclerView recyclerView = view.findViewById(R.id.puzzles);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);


            sharedPreferences = getActivity().getSharedPreferences("shared preferences", MODE_PRIVATE);

            puzzleList = new ArrayList<>();



            ArrayList<ImageView> imgs = new ArrayList<>();


            recyclerViewAdapter = new RecyclerViewAdapter(getContext(), puzzleList);
            recyclerView.setAdapter(recyclerViewAdapter);

            loadData();

            // Inflate the layout for this fragment
            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
        }

    public static void addItem(Puzzle puzzle) {
            System.out.println("adding puzzle");
            puzzleList.add(puzzle);

            recyclerViewAdapter.notifyItemInserted(puzzleList.size() - 1);

        }

        public static void replaceItem(Puzzle puzzle, int pos){
            System.out.println("updating puzzle");
            puzzleList.set(pos, puzzle);
            recyclerViewAdapter.notifyItemChanged(pos);
        }

        public static void deleteItem(int pos){
            //remove puzzle from shared prefs
            int code = puzzleList.get(pos).hashCode();
            int s = sharedPreferences.getInt("length", 0);


            SharedPreferences.Editor editor = sharedPreferences.edit();

            //change length of puzzle list in shared prefs
            if(s > 0) {
                editor.putInt("length", s - 1);
            }

            editor.remove(pos + "_hash");
            editor.remove(code + "_name");
            editor.remove(code + "_type");
            editor.remove(code + "_dimx");
            editor.remove(code  +"_dimy");

            for(int i = 0; i < 9; i++){
                for(int j = 0; j < 9; j++){
                    editor.remove(code + "_values" + i + j);
                    editor.remove(code + "_locked" + i + j);
                    editor.remove(code + "_black" + i + j);
                }
            }

            puzzleList.remove(pos);
            recyclerViewAdapter.notifyItemRemoved(pos);
            recyclerViewAdapter.notifyItemRangeChanged(pos, puzzleList.size());


        }

        public static void saveData(Context context, Puzzle puzzle){
            SharedPreferences.Editor editor = sharedPreferences.edit();

           int pos = puzzleList.indexOf(puzzle);
           int code = puzzleList.get(pos).hashCode();

            editor.putInt(pos +  "_hash", code); //add hashcode
            editor.putInt("length", puzzleList.size());

            for(int i = 0; i < 9; i++){
                for(int j = 0; j < 9; j++){
                    editor.putString(code + "_values" + i + j, puzzle.getArrayValues()[i][j]);
                }
            }

            for(int i = 0; i < 9; i++){
                for(int j = 0; j < 9; j++){
                    if(puzzle.getArrayLocked()[i][j] != null) {
                        editor.putInt(code + "_locked" + i + j, puzzle.getArrayLocked()[i][j]);
                    }else{
                        editor.putInt(code + "_locked" + i + j, -1);
                    }
                }
            }




                editor.putString(code + "_name", puzzle.getName());
                editor.putInt(code + "_type", puzzle.getPuzzleType());
                editor.putInt(code + "_dimx", puzzle.getDims()[0]);
                editor.putInt(code  +"_dimy", puzzle.getDims()[1]);
                editor.apply();



        }

        public void loadData(){
//                sharedPreferences.edit().clear().commit();

            if(puzzleList == null){
                puzzleList = new ArrayList<>();
            }

                int s = sharedPreferences.getInt("length", 0);

                for(int o = 0; o < s; o++) { //loop through length of puzzle list in shared preferences grabbing the unique hashcodes and related puzzle info
                    int code = sharedPreferences.getInt(o + "_hash", -1);

                    if(code != -1){
                        int x = sharedPreferences.getInt(code + "_dimx", 0);
                        int y = sharedPreferences.getInt(code + "_dimy", 0);

                        String name = sharedPreferences.getString( code + "_name", null);
                        int type = sharedPreferences.getInt(code + "_type", 1);
                        String locked = sharedPreferences.getString(code + "_gridLocked", null);

                        String[][] arrayValues = new String[x][y];
                        Integer[][] arrayLocked = new Integer[x][y];

                        for (int i = 0; i < y; i++) {
                            for (int j = 0; j < x; j++) {
                                arrayValues[i][j] = sharedPreferences.getString(code + "_values" + i + j, null);
                                arrayLocked[i][j] = sharedPreferences.getInt(code + "_locked" + i + j, -1);
                            }
                        }

                        Intent intent = new Intent(getContext(), LoadPuzzle.class);
                        intent.putExtra("name", name);
                        intent.putExtra("type", type);
                        intent.putExtra("gridValues", arrayValues);
                        intent.putExtra("gridLocked", arrayLocked);
                        intent.putExtra("dims", new int[]{x, y});
                        startActivity(intent);
                    }else{
                        System.out.println("encountered null puzzle when loading");
                    }


//                    addItem(new LoadPuzzle(getContext(), name, arrayValues, arrayLocked, null, new int[]{x, y}, type));
            }
        }
    }

