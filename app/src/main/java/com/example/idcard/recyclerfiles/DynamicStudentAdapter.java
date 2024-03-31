package com.example.idcard.recyclerfiles;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idcard.R;

import java.util.List;
import java.util.Map;

public class DynamicStudentAdapter extends RecyclerView.Adapter<DynamicStudentAdapter.DynamicStudentViewHolder> {
    private List<DynamicStudent> studentList;

    public DynamicStudentAdapter(List<DynamicStudent> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public DynamicStudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_student_list, parent, false);
        return new DynamicStudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DynamicStudentViewHolder holder, int position) {
        DynamicStudent student = studentList.get(position);
        holder.bind(student);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public class DynamicStudentViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout dynamicLinearLayout;
        private CardView cardView;

        public DynamicStudentViewHolder(@NonNull View itemView) {
            super(itemView);
            dynamicLinearLayout = itemView.findViewById(R.id.dynamicLinearLayout);
            cardView = itemView.findViewById(R.id.cardView);

            /// Handle item click event
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Toggle selection status
                        DynamicStudent student = studentList.get(position);
                        student.setSelected(!student.isSelected());
                        notifyItemChanged(position); // Notify adapter of data change
                    }
                }
            });
        }

        public void bind(DynamicStudent student) {
            // Clear existing views
            dynamicLinearLayout.removeAllViews();

            if (student.isSelected()) {
                cardView.setBackgroundResource(R.drawable.border_selected);
            } else {
                cardView.setBackgroundResource(R.drawable.border_unselected);
            }

            // Loop through all fields and add TextViews dynamically
            for (Map.Entry<String, String> entry : student.getFields().entrySet()) {

                String title = entry.getKey();
                String data = entry.getValue();

                // Create LinearLayout
                LinearLayout linearLayout = new LinearLayout(itemView.getContext());
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                linearLayout.setPadding(20,10,10,10);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                // Create TextView for Title
                TextView distributorLabel = new TextView(itemView.getContext());
                LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                distributorLabel.setLayoutParams(labelParams);
                distributorLabel.setText(title);
                distributorLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                distributorLabel.setTypeface(null, Typeface.BOLD);
                linearLayout.addView(distributorLabel);

                // Create TextView for detail
                TextView distributorName = new TextView(itemView.getContext());
                LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                nameParams.setMargins(10, 0, 0, 0); // Add left margin
                distributorName.setLayoutParams(nameParams);
                distributorName.setText(data);
                linearLayout.addView(distributorName);

        /*        TextView textView = new TextView(itemView.getContext());
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                textView.setText(entry.getKey() + ": " + entry.getValue());*/

                dynamicLinearLayout.addView(linearLayout);
            }
        }
    }
}

