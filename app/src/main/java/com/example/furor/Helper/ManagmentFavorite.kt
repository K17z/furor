package com.example.project1762.Helper

import android.content.Context
import android.widget.Toast
import com.example.furor.model.ItemsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ManagmentFavorite(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    // корень узла favorites
    private val dbRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("favorites")

    private val uid: String
        get() = auth.currentUser?.uid.orEmpty()

    /** Добавляет товар в избранное у текущего пользователя */
    fun addFavorite(item: ItemsModel) {
        if (uid.isEmpty()) {
            Toast.makeText(context, "Сначала войдите в аккаунт", Toast.LENGTH_SHORT).show()
            return
        }
        // создаём новую запись под уникальным ключом
        val favRef = dbRef.child(uid).push()
        favRef.setValue(item)
    }

    /** Удаляет товар из избранного по совпадению title */
    fun removeFavorite(item: ItemsModel) {
        if (uid.isEmpty()) return
        val ref = dbRef.child(uid)
        // находим все записи с таким заголовком и удаляем
        ref.orderByChild("title").equalTo(item.title)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { it.ref.removeValue() }
                }
                override fun onCancelled(error: DatabaseError) {
                    // можно логировать ошибку
                }
            })
    }

    /** Проверяет, есть ли товар в избранном у текущего пользователя */
    fun isFavorite(item: ItemsModel, callback: (Boolean) -> Unit) {
        if (uid.isEmpty()) {
            callback(false)
            return
        }
        val ref = dbRef.child(uid)
        ref.orderByChild("title").equalTo(item.title)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.exists())
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }
}
