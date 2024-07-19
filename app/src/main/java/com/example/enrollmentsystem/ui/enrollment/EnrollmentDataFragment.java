package com.example.enrollmentsystem.ui.enrollment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.enrollmentsystem.R;
import com.example.enrollmentsystem.model.Student;

public class EnrollmentDataFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView studentIdTextView;
    private TextView studentNameTextView;
    private Spinner departmentSpinner;
    private Spinner levelSpinner;
    private Spinner courseSpinner;
    private Spinner curriculumSpinner;
    private OnStudentSelectedListener listener;

    public interface OnStudentSelectedListener {
        void onStudentSelected(Student student);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnStudentSelectedListener) {
            listener = (OnStudentSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnStudentSelectedListener");
        }
    }

    public EnrollmentDataFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EnrollmentDataFragment newInstance(String param1, String param2) {
        EnrollmentDataFragment fragment = new EnrollmentDataFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enrollment_data, container, false);
        studentIdTextView = view.findViewById(R.id.studentIDTextView);
        studentNameTextView = view.findViewById(R.id.studentNameTextView);
        departmentSpinner = view.findViewById(R.id.departmentSpinner);
        levelSpinner = view.findViewById(R.id.levelSpinner);
        courseSpinner = view.findViewById(R.id.courseSpinner);
        curriculumSpinner = view.findViewById(R.id.curriculumSpinner);
        return view;
    }

    public void updateViewsWithStudentData(Student student) {
        studentIdTextView.setText(student.getStudentID());
        studentNameTextView.setText(student.getStudentName());
        departmentSpinner.setSelection(getIndex(departmentSpinner, student.getStudentDepartment()));
        levelSpinner.setSelection(getIndex(levelSpinner, student.getStudentLevel()));
        courseSpinner.setSelection(getIndex(courseSpinner, student.getStudentCourse()));
        curriculumSpinner.setSelection(getIndex(curriculumSpinner, student.getStudentCurriculum()));
    }

    private int getIndex(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                return i;
            }
        }
        return 0;
    }
}