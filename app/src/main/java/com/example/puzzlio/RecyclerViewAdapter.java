package com.example.puzzlio;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.puzzlio.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Puzzle> mData;
    private Context context;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public RecyclerViewAdapter(Context context, List<Puzzle> data) {
        this.context = context;
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.puzzlelistitem, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Puzzle puzzle = mData.get(position);
        holder.myTextView.setText(puzzle.getName());

        switch (puzzle.getPuzzleType()){
            case 2:
                holder.subtitle.setText("Sudoku");
            default:
                System.out.println("unsupported puzzle type in recyclerviewadapter");
        }


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PuzzleList.deleteItem(position);
            }
        });

        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Puzzle.class);
                intent.putExtra("load", true);
                intent.putExtra("position", position);
                intent.putExtra("type", puzzle.getPuzzleType());
                intent.putExtra("name", puzzle.getName());
                intent.putExtra("gridValues", puzzle.getArrayValues());
                intent.putExtra("gridLocked", puzzle.getArrayLocked());
                intent.putExtra("dims", puzzle.getDims());
                System.out.println(puzzle.getArrayValues());
                context.startActivity(intent);
            }
        });

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView, subtitle;
        ImageView icon;
        ImageView play;
        ImageButton delete;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.name);
            subtitle = itemView.findViewById(R.id.subtitle);
            icon = itemView.findViewById(R.id.icon);
            play = itemView.findViewById(R.id.play);
            delete = itemView.findViewById(R.id.deletepuzzle);


        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Puzzle getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
