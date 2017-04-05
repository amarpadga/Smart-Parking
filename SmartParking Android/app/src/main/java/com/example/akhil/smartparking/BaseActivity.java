package com.example.akhil.smartparking;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity{
    private SharedPreferences mPreferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        if(mPreferences.contains("uid")){
            menu.getItem(3).setVisible(false);
            menu.getItem(2).setVisible(true);
            menu.getItem(4).setVisible(true);
        }else{
            System.out.println("uid doesnt exist");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        switch (item.getItemId()) {

            case R.id.menu1:
                Toast.makeText(this, "Clicked: Parking View", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
                break;

            case R.id.menu2:
                Toast.makeText(this, "Clicked: My Reservations", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, MyReservations.class);
                this.startActivity(intent2);
                break;

            case R.id.menu3:
                Toast.makeText(this, "Clicked: Login/Register", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(this, LoginActivity.class);
                this.startActivity(intent3);
                break;

            case R.id.menu4:
                Toast.makeText(this, "Clicked: Logout", Toast.LENGTH_SHORT).show();
                SharedPreferences preferences =getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                invalidateOptionsMenu();
                Intent intent5 = new Intent(this, MainActivity.class);
                this.startActivity(intent5);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
    return true;
    }
}