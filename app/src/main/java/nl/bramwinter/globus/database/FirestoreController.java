package nl.bramwinter.globus.database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import nl.bramwinter.globus.models.TestModel;

public class FirestoreController {

    public void firestoreTest() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TestModel testModel = new TestModel("Emil", "Celik", "emilcelik@gmail.com");

        Map<String, Object> data = new HashMap<>();

        data.put("firstName", testModel.getFirstName());
        data.put("lastName", testModel.getLastName());
        data.put("email", testModel.getEmail());

        db.collection("users").add(data)
                .addOnSuccessListener(documentReference -> Log.d("Celik", "DocumentSnapshot written with ID: " + documentReference))
                .addOnFailureListener(e -> Log.d("Celik", "Error adding document", e));

        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshots : task.getResult()) {
                            Log.d("Celik", documentSnapshots.getId() + " => " + documentSnapshots.getData());
                        }
                    } else {
                        Log.d("Celik", "Error getting documents: ", task.getException());
                    }
                });
    }


}
