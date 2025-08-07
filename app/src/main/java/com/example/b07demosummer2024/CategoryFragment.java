package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

/**
 * {CategoryFragment} is the main screen where users choose which
 * data category they wish to manage.  It presents buttons for:
 * <ul>
 *   <li>Documents</li>
 *   <li>Emergency Contacts</li>
 *   <li>Safe Locations</li>
 *   <li>Medications</li>
 * </ul>
 * Tapping a button navigates to the corresponding activity for that category.
 */

public class CategoryFragment extends Fragment {

    /**
     * Called when the activity is first created.  Sets up edge-to-edge drawing,
     * applies window insets padding to avoid overlap with system bars, and
     * wires up the four category buttons to launch their respective activities.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down, this Bundle contains
     *                           the data it most recently supplied.  Otherwise it is null.
     */
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(requireActivity().getWindow(), false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_category, container, false);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // "Documents to pack" button
        Button documentsButton = root.findViewById(R.id.documentsButton);
        documentsButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), DocumentActivity.class);
            startActivity(intent);
        });

        // "Emergency contact" button
        Button emergencyContactsButton = root.findViewById(R.id.contactsButton);
        emergencyContactsButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EmergencyContactsActivity.class);
            startActivity(intent);
        });

        // "Safe locations" button
        Button safeLocationsButton = root.findViewById(R.id.locationsButton);
        safeLocationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SafeLocationsActivity.class);
            startActivity(intent);
        });

        // "Medications" button
        Button medicationsButton = root.findViewById(R.id.medicationsButton);
        medicationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MedicationActivity.class);
            startActivity(intent);
        });

        return root;
    }
}
