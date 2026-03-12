package com.example.healthpriority;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "healthpriority.db";
    private static final int DATABASE_VERSION = 3; // ✅ BUMPED VERSION

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Database", "onCreate called: creating tables");
        db.execSQL("CREATE TABLE users (username TEXT PRIMARY KEY, email TEXT, password TEXT)");
        db.execSQL("CREATE TABLE cart (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, product TEXT, price REAL, otype TEXT)");
        db.execSQL("CREATE TABLE orderplace (username TEXT, fullname TEXT, address TEXT, contactno TEXT, pincode INTEGER, date TEXT, time TEXT, amount REAL, otype TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("Database", "onUpgrade called: dropping and recreating tables");
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS cart");
        db.execSQL("DROP TABLE IF EXISTS orderplace");
        onCreate(db);
    }

    public boolean register(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);
        long result = db.insert("users", null, values);
        db.close();
        return result != -1;
    }

    public boolean login(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{username, password});
        boolean success = cursor.moveToFirst();
        cursor.close();
        db.close();
        return success;
    }

    public void addCart(String username, String product, float price, String otype) {
        if (checkCart(username, product) == 0) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("username", username);
            cv.put("product", product);
            cv.put("price", price);
            cv.put("otype", otype);
            long result = db.insert("cart", null, cv);
            db.close();
            Log.d("Database", result == -1 ? "Failed to add product to cart." : "Product added to cart.");
        }
    }

    public int checkCart(String username, String product) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM cart WHERE username = ? AND product = ?", new String[]{username, product});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists ? 1 : 0;
    }

    public void removeCart(String username, String otype) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cart", "username=? AND otype=?", new String[]{username, otype});
        db.close();
    }

    public ArrayList<String> getCartData(String username, String otype) {
        ArrayList<String> arr = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM cart WHERE username=? AND otype=?", new String[]{username, otype});
        while (c.moveToNext()) {
            String product = c.getString(2);  // Product name
            String price = c.getString(3);    // Price
            arr.add(product + "$" + price);   // Add to array with "$" delimiter
        }
        c.close();
        db.close();
        return arr;
    }

    public void addOrder(String username, String fullname, String address, String contact, int pincode, String date, String time, float price, String otype) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("fullname", fullname);
        cv.put("address", address);
        cv.put("contactno", contact);
        cv.put("pincode", pincode);
        cv.put("date", date);
        cv.put("time", time);
        cv.put("amount", price);
        cv.put("otype", otype);
        db.insert("orderplace", null, cv);
        db.close();
    }

    public ArrayList<String> getOrderData(String username) {
        ArrayList<String> arr = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT * FROM orderplace WHERE username=?", new String[]{username});
            if (c != null && c.moveToFirst()) {
                do {
                    StringBuilder order = new StringBuilder();
                    for (int i = 0; i < c.getColumnCount(); i++) {
                        order.append(c.getString(i));
                        if (i < c.getColumnCount() - 1) {
                            order.append("$");
                        }
                    }
                    arr.add(order.toString());
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error getting order data: " + e.getMessage());
        } finally {
            if (c != null) {
                c.close();
            }
            db.close();
        }
        return arr;
    }

    public int checkAppointmentExists(String username, String fullname, String address, String contact, String date, String time) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        int exists = 0;
        try {
            c = db.rawQuery("SELECT * FROM orderplace WHERE username=? AND fullname=? AND address=? AND contactno=? AND date=? AND time=?",
                    new String[]{username, fullname, address, contact, date, time});
            if (c != null && c.moveToFirst()) {
                exists = 1;
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error checking appointment: " + e.getMessage());
        } finally {
            if (c != null) {
                c.close();
            }
            db.close();
        }
        return exists;
    }
}
